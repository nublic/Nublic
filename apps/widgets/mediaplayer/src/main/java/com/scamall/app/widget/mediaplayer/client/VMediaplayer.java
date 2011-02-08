package com.scamall.app.widget.mediaplayer.client;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.HTML;
import com.scamall.app.widget.mediaplayer.PlayerState;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

/**
 * Client side widget which communicates with the server. Messages from the
 * server are shown as HTML and mouse clicks are sent to the server.
 */
public class VMediaplayer extends HTML implements Paintable {

	/* PLAYER EXTERNAL ATTRIBUTES */
	/* ========================== */

	/** Set the CSS class name to allow styling. */
	/** "projekktor" is needed to be themeable */
	public static final String CLASSNAME = "v-mediaplayer projekktor";

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

	public VMediaplayer() {
		super();
		// setStyleName(CLASSNAME);

		this.state = PlayerState.IDLE;
		this.current_clip_url = null;
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
		playerId = paintableId + "_mediaplayer";
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
		// float width = uidl.getFloatAttribute("player_width");
		// float height = uidl.getFloatAttribute("player_height");

		StringBuffer html = new StringBuffer();
		html.append("<video ");
		html.append(" id=\"");
		html.append(this.playerId);
		html.append("\"");
		html.append(" height=\"");
		// html.append(height);
		html.append(uidl.getStringAttribute("height"));
		html.append("\"");
		html.append(" width=\"");
		// html.append(width);
		html.append(uidl.getStringAttribute("width"));
		html.append("\"");
		html.append(" class=\"");
		html.append(this.getStyleName());
		html.append(" ");
		html.append(CLASSNAME);
		html.append("\"");
		html.append(" controls >");
		html.append("</video>");

		return html.toString();
	}

	/**
	 * Updates the player to be set in the state that is told in the UIDL.
	 * 
	 * @param uidl
	 */
	synchronized void refreshFromUIDL(UIDL uidl) {
		// long volume = uidl.getLongAttribute("volume");

		//PlayerState new_state = PlayerState.valueOf(uidl
		//		.getStringAttribute("state"));
		String new_current_clip_url = uidl
				.getStringAttribute("current_clip_url");

		this.current_clip_url  = new_current_clip_url;
		this.setFile(new_current_clip_url);
		this.play();
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
	 * this.@com.scamall.app.widget.mediaplayer.client.VMediaplayer::player;
	 */

	native void publishPlayer(String basePath, UIDL first_uidl)
	/*-{
		playerId = this.@com.scamall.app.widget.mediaplayer.client.VMediaplayer::playerId;
		
		while($wnd.projekktor == undefined) ;
		
		outside_world = this;
		var player = $wnd.projekktor("#" + playerId,
			{
				"volume": 0.8
			});
		
		this.@com.scamall.app.widget.mediaplayer.client.VMediaplayer::player = player;
		this.@com.scamall.app.widget.mediaplayer.client.VMediaplayer::loaded = true;
		this.@com.scamall.app.widget.mediaplayer.client.VMediaplayer::refreshFromUIDL(Lcom/vaadin/terminal/gwt/client/UIDL;)(first_uidl);
	}-*/;
	
	native void setFile(String file)
	/*-{
		player = this.@com.scamall.app.widget.mediaplayer.client.VMediaplayer::player;
		player.setFile(file, false);
	}-*/;

	native void play()
	/*-{
		player = this.@com.scamall.app.widget.mediaplayer.client.VMediaplayer::player;
		player.setPlay();
	}-*/;
}