package com.scamall.app.widget.flowplayer.client;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HTML;
import com.scamall.app.widget.flowplayer.ClipScaling;
import com.scamall.app.widget.flowplayer.PlayerState;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

/**
 * Client side widget which communicates with the server. Messages from the
 * server are shown as HTML and mouse clicks are sent to the server.
 */
public class VFlowplayer extends HTML implements Paintable {

	/* PLAYER EXTERNAL ATTRIBUTES */
	/* ========================== */

	/** Set the CSS class name to allow styling. */
	public static final String CLASSNAME = "v-flowplayer";

	/** The client side widget identifier */
	protected String paintableId;

	/** Id of the embedded object in HTML */
	private String playerId;

	/** Reference to the server connection object. */
	protected ApplicationConnection client;

	private boolean loading = false;
	private boolean loaded = false;

	/* PLAYER MANAGEMENT ATTRIBUTES */
	/* ============================ */

	private JavaScriptObject player;
	private PlayerState state;

	private String current_clip_url;
	private ClipScaling current_clip_scaling;

	public VFlowplayer() {
		super();
		setStyleName(CLASSNAME);

		this.state = PlayerState.STOPPED;
		this.current_clip_url = null;
		this.current_clip_scaling = null;
	}

	public synchronized void updateFromUIDL(UIDL uidl,
			ApplicationConnection client) {

		/* @section Generated code needed for handling the widget */
		// This call should be made first.
		// It handles sizes, captions, tooltips, etc. automatically.
		if (client.updateComponent(this, uidl, true)) {
			// If client.updateComponent returns true there has been
			// no changes and we do not need to update anything.
			return;
		}
		// Save reference to server connection object to be able to send
		// user interaction later
		this.client = client;
		// Save the client side identifier (paintable id) for the widget
		paintableId = uidl.getId();
		// and generate the corresponding player id
		playerId = paintableId + "_flowplayer";
		/* @endsection */

		if (!loaded) {
			if (!loading) {
				// Do not start loading more than once
				loading = true;
				// Init player
				setHTML(generateHTML(uidl));
				publishPlayer(GWT.getModuleBaseURL(), uidl);
			}
		} else {
			this.refreshFromUIDL(uidl);
		}
	}

	private String generateHTML(UIDL uidl) {
		// Get needed information
		float width = uidl.getFloatAttribute("player_width");
		float height = uidl.getFloatAttribute("player_height");
		float controls_height = uidl.getFloatAttribute("controls_height");
		String klass = uidl.getStringAttribute("controls_css_class");

		StringBuffer html = new StringBuffer();
		html.append("<div id=\"");
		html.append(this.playerId);
		html.append("\" style=\"display:block;width:");
		html.append(width);
		html.append("px;height:");
		html.append(height - controls_height);
		html.append("px;\"></div>");

		html.append("<div id=\"");
		html.append(this.playerId);
		html.append("_controls\" class=\"");
		html.append(klass);
		html.append("\">");
		html.append("<a class=\"play\">play</a>");
		html.append("<a class=\"previous\">previous</a>");
		html.append("<a class=\"next\">next</a>");
		html.append("<div class=\"track\">");
		html.append("<div class=\"buffer\"></div>");
		html.append("<div class=\"progress\"></div>");
		html.append("<div class=\"playhead\"></div>");
		html.append("</div>");
		html.append("<div class=\"time\"></div>");
		html.append("<a class=\"mute\">mute</a>");
		html.append("</div>");

		return html.toString();
	}

	/**
	 * Updates the player to be set in the state that is told in the UIDL.
	 * 
	 * @param uidl
	 */
	synchronized void refreshFromUIDL(UIDL uidl) {
		long volume = uidl.getLongAttribute("volume");
		boolean muted = uidl.getBooleanAttribute("muted");
		this.setNativeVolume(volume, muted);

		PlayerState new_state = PlayerState.valueOf(uidl
				.getStringAttribute("state"));
		String new_current_clip_url = uidl
				.getStringAttribute("current_clip_url");
		ClipScaling new_current_clip_scaling = ClipScaling.valueOf(uidl
				.getStringAttribute("current_clip_scaling"));

		if ((!new_current_clip_url.equals(this.current_clip_url))
				|| (!new_current_clip_scaling.equals(this.current_clip_scaling))) {
			if (new_current_clip_url == null || new_current_clip_url == "") {
				this.current_clip_url = null;
				this.current_clip_scaling = null;
				this.setNativeNoPlaylist();
			} else {
				this.current_clip_url = new_current_clip_url;
				this.current_clip_scaling = new_current_clip_scaling;
				this.setNativeCurrentClip(new_current_clip_url,
						new_current_clip_scaling.getFlowplayerName());

				if (new_state == PlayerState.PLAYING) {
					this.play();
				}
			}
		} else {

		}
	}

	void clipStarted() {
		client.updateVariable(paintableId, "start", "", true);
	}

	void clipFinished() {
		client.updateVariable(paintableId, "finished", "", true);
	}

	void stateChanged(String state) {
		client.updateVariable(paintableId, "state", state, true);
	}

	void requestPrevious() {
		client.updateVariable(paintableId, "previous", "", true);
	}

	void requestNext() {
		client.updateVariable(paintableId, "next", "", true);
	}

	void volumeChanged(int volume, boolean muted) {
		HashMap<String, Object> values = new HashMap<String, Object>();
		values.put("volume", volume);
		values.put("muted", muted);
		client.updateVariable(paintableId, "volume", values, true);
	}

	/*
	 * Lines needed for accessing the Flowplayer object > player =
	 * this.@com.scamall.app.widget.flowplayer.client.VFlowplayer::player;
	 */

	native void publishPlayer(String basePath, UIDL first_uidl)
	/*-{
		playerId = this.@com.scamall.app.widget.flowplayer.client.VFlowplayer::playerId;
		
		while($wnd.flowplayer == undefined) ;
		
		outside_world = this;
		var player = $wnd.flowplayer(playerId, basePath + "flowplayer-3.2.5.swf",
			{
				"clip": {
					"autoPlay": false,
					"autoBuffering": true
				},
				"plugins": {
					"audio": {
						"url": basePath + "flowplayer.audio-3.2.1.swf"
					},
					"controls": null
				}
			})
			.controls(playerId + "_controls", {"duration":0})
			.onLoad(function() {
				alert("LOADED!");
				player = $wnd.flowplayer(playerId);
				player.pause();
				
				player.onBegin(function(clip) {
					outside_world.@com.scamall.app.widget.flowplayer.client.VFlowplayer::clipStarted()();
				});
				player.onFinish(function(clip) {
					player.stop();
					outside_world.@com.scamall.app.widget.flowplayer.client.VFlowplayer::clipFinished()();
					outside_world.@com.scamall.app.widget.flowplayer.client.VFlowplayer::requestNext()();
				});
				
				outside_world.@com.scamall.app.widget.flowplayer.client.VFlowplayer::player = player;
				outside_world.@com.scamall.app.widget.flowplayer.client.VFlowplayer::loaded = true;
				outside_world.@com.scamall.app.widget.flowplayer.client.VFlowplayer::refreshFromUIDL(Lcom/vaadin/terminal/gwt/client/UIDL;)(first_uidl);
				
				return true;
			});
		
		// Load the player into memory
		player.load();
	}-*/;

	native void setNativeNoPlaylist()
	/*-{
		player = this.@com.scamall.app.widget.flowplayer.client.VFlowplayer::player;
	    player.setPlaylist([]);
	}-*/;

	native void setNativeCurrentClip(String url, String scaling)
	/*-{
		player = this.@com.scamall.app.widget.flowplayer.client.VFlowplayer::player;
	  	
	    clip = {"url": url, "scaling": scaling};
	    player.setClip(clip);
	    player.pause();
	    // player.startBuffering();
	}-*/;

	native double getNativeVolume()
	/*-{
		player = this.@com.scamall.app.widget.flowplayer.client.VFlowplayer::player;
		return player.getStatus().volume;
	}-*/;

	native boolean getNativeIsMuted()
	/*-{
		player = this.@com.scamall.app.widget.flowplayer.client.VFlowplayer::player;
		return player.getStatus().muted;
	}-*/;

	native void setNativeVolume(double volume, boolean muted)
	/*-{
		player = this.@com.scamall.app.widget.flowplayer.client.VFlowplayer::player;
		status = player.getStatus();
		
		if (status.volume != volume) {
			player.setVolume(volume);
		}
		
		if (status.muted != muted) {
			if (muted) {
				player.mute();
			} else {
				player.unmute();
			}
		}
	}-*/;

	native void play()
	/*-{
		player = this.@com.scamall.app.widget.flowplayer.client.VFlowplayer::player;
		
		var clip_timer = null;
	    clip_timer = setInterval(function() {
	        var c = player.getStatus();
	        if (c === undefined || c === null || c.bufferStart === undefined || c.bufferStart === null)
	        	return;
	        if ((c.bufferEnd - c.bufferStart) > 0.5) {
	            player.play();
	            clearInterval(clip_timer);
	        }
	    }, 500);
	    
	    player.startBuffering();
	}-*/;

	native void pause()
	/*-{
		player = this.@com.scamall.app.widget.flowplayer.client.VFlowplayer::player;
		player.pause();
	}-*/;

	native void resume()
	/*-{
		player = this.@com.scamall.app.widget.flowplayer.client.VFlowplayer::player;
		player.resume();
	}-*/;

	native void stop()
	/*-{
		player = this.@com.scamall.app.widget.flowplayer.client.VFlowplayer::player;
		player.stop();
	}-*/;
}