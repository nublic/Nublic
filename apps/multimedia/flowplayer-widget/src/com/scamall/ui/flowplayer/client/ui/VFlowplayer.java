package com.scamall.ui.flowplayer.client.ui;

import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Client side widget which communicates with the server. Messages from the
 * server are shown as HTML and mouse clicks are sent to the server.
 */
public class VFlowplayer extends HTML implements Paintable {

	/** Set the CSS class name to allow styling. */
	public static final String CLASSNAME = "v-flowplayer";

	/** The client side widget identifier */
	protected String paintableId;

	/** Id of the embedded object in HTML */
	private String playerId;

	/** Reference to the server connection object. */
	protected ApplicationConnection client;

	private boolean loaded = false;

	public VFlowplayer() {
		super();
		setStyleName(CLASSNAME);
	}

	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

		/* @section Generated code needed for handling the widget */
		// This call should be made first.
		// It handles sizes, captions, tooltips, etc. automatically.
		if (client.updateComponent(this, uidl, true)) {
			// If client.updateComponent returns true there has been no changes
			// and we
			// do not need to update anything.
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
			setHTML("<a id=\"" + playerId + "\" href=\"http://vod01.netdna.com/vod/demo.flowplayer/flowplayer-700.flv\" style=\"display:block;width:425px;height:300px;\"></a>");
			String pathToSwf = GWT.getModuleBaseURL() + "flowplayer-3.2.2.swf";
			publishPlayer(this, playerId, pathToSwf);
			loaded = true;
		}

		/*
		 * int clicks = uidl.getIntAttribute("clicks"); String message =
		 * uidl.getStringAttribute("message");
		 */
	}

	native void publishPlayer(VFlowplayer player, String playerId, String pathToSwf)
	/*-{
	    $wnd["flowplayer_" + playerId] = player;
		$wnd.flowplayer(playerId, pathToSwf);
	}-*/;
}
