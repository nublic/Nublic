package com.scamall.ui.flowplayer;

import java.util.Map;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;

/**
 * Server side component for the VFlowplayer widget.
 */
@com.vaadin.ui.ClientWidget(com.scamall.ui.flowplayer.client.ui.VFlowplayer.class)
public class Flowplayer extends AbstractComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 154637716022382786L;

	private Playlist playlist;
	private PlayerState state;
	private long current_clip_id;
	private long volume;
	private boolean repeat;
	private boolean random;

	/**
	 * @return the state
	 */
	public PlayerState getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(PlayerState state) {
		this.state = state;
	}

	/**
	 * @return the volume
	 */
	public long getVolume() {
		return volume;
	}

	/**
	 * @param volume
	 *            the volume to set
	 */
	public void setVolume(long volume) {
		this.volume = volume;
	}

	/**
	 * @return the repeat
	 */
	public boolean isRepeat() {
		return repeat;
	}

	/**
	 * @param repeat
	 *            the repeat to set
	 */
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	/**
	 * @return the random
	 */
	public boolean isRandom() {
		return random;
	}

	/**
	 * @param random
	 *            the random to set
	 */
	public void setRandom(boolean random) {
		this.random = random;
	}

	/**
	 * @return the playlist
	 */
	public Playlist getPlaylist() {
		return playlist;
	}

	/**
	 * @return the current clip
	 */
	public Clip getCurrentClip() {
		if (current_clip_id == -1)
			return null;
		
		return playlist.get(current_clip_id);
	}

	/**
	 * @param clip
	 *            the clip to set as current
	 * @remark The clip must be added to the playlist
	 */
	public void setCurrentClip(Clip clip) {
		this.current_clip_id = clip.getId();
	}

	@Override
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);

		/*
		 * // Paint any component specific content by setting attributes //
		 * These attributes can be read in updateFromUIDL in the widget.
		 * target.addAttribute("clicks", clicks); target.addAttribute("message",
		 * message);
		 * 
		 * // We could also set variables in which values can be returned // but
		 * declaring variables here is not required
		 */
	}

	/**
	 * Receive and handle events and other variable changes from the client.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void changeVariables(Object source, Map<String, Object> variables) {
		super.changeVariables(source, variables);

		// Variables set by the widget are returned in the "variables" map.

		/*
		 * if (variables.containsKey("click")) {
		 * 
		 * // When the user has clicked the component we increase the // click
		 * count, update the message and request a repaint so // the changes are
		 * sent back to the client. clicks++; message += "<br/>" +
		 * variables.get("click");
		 * 
		 * requestRepaint(); }
		 */
	}

}
