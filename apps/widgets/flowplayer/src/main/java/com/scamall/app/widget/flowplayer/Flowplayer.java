package com.scamall.app.widget.flowplayer;

import java.util.Map;

import com.scamall.app.widget.flowplayer.client.VFlowplayer;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ClientWidget;

/**
 * Server side component for the VFlowplayer widget.
 */
@ClientWidget(VFlowplayer.class)
public class Flowplayer extends AbstractComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 154637716022382786L;

	private Playlist playlist;
	private PlayerState state;
	private int current_clip_id;
	private int volume;
	private boolean muted;

	/**
	 * For styling the control bar
	 */
	private String controls_css_class;
	private double controls_height;

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
	public int getVolume() {
		return volume;
	}

	/**
	 * @param volume
	 *            the volume to set
	 */
	public void setVolume(int volume) {
		this.volume = volume;
	}

	/**
	 * @return the muted
	 */
	public boolean isMuted() {
		return muted;
	}

	/**
	 * @param muted
	 *            the muted to set
	 */
	public void setMuted(boolean muted) {
		this.muted = muted;
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

		return playlist.getClip(current_clip_id);
	}

	/**
	 * @param clip
	 *            the clip to set as current
	 * @remark The clip must be added to the playlist
	 */
	public void setCurrentClip(Clip clip) {
		this.current_clip_id = clip.getId();
	}

	/**
	 * @return the controls CSS class
	 */
	public String getControlsCSSClass() {
		return controls_css_class;
	}

	/**
	 * @param controls_css_class
	 *            the controls CSS class to set
	 */
	public void setControlsCSSClass(String controls_css_class) {
		this.controls_css_class = controls_css_class;
	}

	/**
	 * @return the controls height
	 */
	public double getControlsHeight() {
		return controls_height;
	}

	/**
	 * @param controls_height
	 *            the controls height to set
	 */
	public void setControlsHeight(double controls_height) {
		this.controls_height = controls_height;
	}

	public Flowplayer() {
		this.playlist = new Playlist(this);
		this.state = PlayerState.PLAYING;
		this.current_clip_id = -1;
		this.volume = 20;
		this.controls_css_class = "hulu";
		this.controls_height = 40;
	}

	@Override
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);

		// Set dimensions
		target.addAttribute("player_width", this.getWidth());
		target.addAttribute("player_height", this.getHeight());
		target.addAttribute("controls_css_class", this.controls_css_class);
		target.addAttribute("controls_height", this.controls_height);

		// Set global player attributes
		target.addAttribute("state", this.state.serializeToUidl());
		target.addAttribute("current_clip_id", this.current_clip_id);
		target.addAttribute("volume", this.volume);
		target.addAttribute("muted", this.muted);

		// Set clip attributes
		Clip clip = this.getCurrentClip();
		if (clip == null) {
			for (String attribute : Clip.getUidlSerializableAttributes()) {
				target.addAttribute("current_clip_" + attribute, "");
			}
		} else {
			for (String attribute : Clip.getUidlSerializableAttributes()) {
				target.addAttribute("current_clip_" + attribute,
						clip.getUidlSerializedAttribute(attribute));
			}
		}
	}

	/**
	 * Receive and handle events and other variable changes from the client.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public void changeVariables(Object source, Map<String, Object> variables) {
		super.changeVariables(source, variables);
		
		if (variables.containsKey("finished")) {
			
		}
		if (variables.containsKey("next")) {
			int clip_id = (Integer)variables.get("next");
			Clip the_clip = this.playlist.getClip(clip_id);
			this.setCurrentClip(this.playlist.getNextClip(the_clip));
			requestRepaint();
		}
		if (variables.containsKey("previous")) {
			int clip_id = (Integer)variables.get("previous");
			Clip the_clip = this.playlist.getClip(clip_id);
			this.setCurrentClip(this.playlist.getPreviousClip(the_clip));
			requestRepaint();
		}
	}

}