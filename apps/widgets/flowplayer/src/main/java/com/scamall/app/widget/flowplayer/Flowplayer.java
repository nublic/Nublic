package com.scamall.app.widget.flowplayer;

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;

import com.scamall.app.widget.flowplayer.client.VFlowplayer;
import com.vaadin.data.Container;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Item;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.ClientWidget;

/**
 * Server side component for the VFlowplayer widget.
 */
@ClientWidget(VFlowplayer.class)
public class Flowplayer extends AbstractComponent implements
		Container.ItemSetChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 154637716022382786L;

	/* PRIVATE FIELDS */
	/* ============== */

	private Container.Indexed playlist;
	private int current_clip_idx;

	private boolean repeat;
	private boolean random;
	private int[] random_order;

	private PlayerState state;
	private int volume;
	private boolean muted;

	/**
	 * For styling the control bar
	 */
	private String controls_css_class;
	private double controls_height;

	public Flowplayer() {
		this(new ClipContainer());
	}

	public Flowplayer(Container.Indexed playlist) {
		// State initialization
		this.state = PlayerState.PLAYING;
		this.volume = 20;
		this.controls_css_class = "hulu";
		this.controls_height = 40;
		// Playlist initialization
		this.playlist = playlist;
		this.current_clip_idx = -1;
		// Set events for container changes
		if (this.playlist instanceof Container.ItemSetChangeNotifier) {
			Container.ItemSetChangeNotifier notifier = (Container.ItemSetChangeNotifier)this.playlist;
			notifier.addListener(this);
		}
		this.regenerateRandomOrder();
	}

	/* PLAYLIST MANAGEMENT */
	/* =================== */

	/**
	 * Builds a new random order for the playlist.
	 */
	public void regenerateRandomOrder() {
		int size = this.playlist.size();
		if (size == 0) {
			random_order = new int[0];
		} else {
			RandomData data = new RandomDataImpl();
			random_order = data.nextPermutation(size, size);
		}
	}

	/**
	 * Allows a container set with no events to be used with this widget.
	 */
	public void containetItemSetChanged() {
		this.regenerateRandomOrder();
	}

	/**
	 * Handler for the event of container change.
	 */
	public void containerItemSetChange(ItemSetChangeEvent event) {
		this.regenerateRandomOrder();
	}

	/* NEXT AND PREVIOUS */
	/* ================= */

	private Object getFirstClipId() {
		if (this.playlist.size() == 0)
			return null;

		if (this.random) {
			return this.playlist.getIdByIndex(this.random_order[0]);
		} else {
			return this.playlist.firstItemId();
		}
	}

	private Object getLastClipId() {
		if (this.playlist.size() == 0)
			return null;

		if (this.random) {
			return this.playlist
					.getIdByIndex(this.random_order[this.random_order.length - 1]);
		} else {
			return this.playlist.lastItemId();
		}
	}

	public Object getNextClipId(Object id) {
		if (!random) {
			Object next_id = this.playlist.nextItemId(id);
			// We came to the end
			if (next_id == null) {
				return this.repeat ? this.getFirstClipId() : null;
			} else {
				return next_id;
			}
		} else {
			int index = this.playlist.indexOfId(id);
			int random_pos = ArrayUtils.indexOf(this.random_order, index);
			// We came to the end
			if (random_pos == (this.playlist.size() - 1)) {
				return this.repeat ? this.getFirstClipId() : null;
			}
			// Normal case
			return this.playlist
					.getIdByIndex(this.random_order[random_pos + 1]);
		}
	}

	public Object getPreviousClipId(Object id) {
		if (!random) {
			Object prev_id = this.playlist.prevItemId(id);
			// We came to the start
			if (prev_id == null) {
				return this.repeat ? this.getLastClipId() : null;
			} else {
				return prev_id;
			}
		} else {
			int index = this.playlist.indexOfId(id);
			int random_pos = ArrayUtils.indexOf(this.random_order, index);
			// We came to the start
			if (random_pos == 0) {
				return this.repeat ? this.getLastClipId() : null;
			}
			// Normal case
			return this.playlist
					.getIdByIndex(this.random_order[random_pos - 1]);
		}
	}

	/* VAADIN CLIENT-SERVER COMMUNICATION */
	/* ================================== */

	@Override
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);

		// Set dimensions
		target.addAttribute("player_width", this.getWidth());
		target.addAttribute("player_height", this.getHeight());
		target.addAttribute("controls_css_class", this.controls_css_class);
		target.addAttribute("controls_height", this.controls_height);

		// Set global player attributes
		target.addAttribute("state", this.state.toString());
		target.addAttribute("volume", this.volume);
		target.addAttribute("muted", this.muted);

		// Set clip attributes
		Object clip_id = this.getCurrentClipId();
		Item clip = this.getCurrentClip();
		target.addAttribute("current_clip_id", clip_id.toString());
		target.addAttribute("current_clip_url",
				(String) clip.getItemProperty("url").getValue());
		target.addAttribute("current_clip_scaling", ((ClipScaling) clip
				.getItemProperty("scaling").getValue()).toString());
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
			this.setCurrentClip(this.getNextClipId(this.getCurrentClipId()));
			requestRepaint();
		}
		if (variables.containsKey("previous")) {
			this.setCurrentClip(this.getPreviousClipId(this.getCurrentClipId()));
			requestRepaint();
		}
	}

	/* GETTERS AND SETTERS */
	/* =================== */

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
		requestRepaint();
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
		requestRepaint();
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
		requestRepaint();
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
		requestRepaint();
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
		requestRepaint();
	}

	/**
	 * @return the playlist
	 */
	public Container.Indexed getPlaylist() {
		return playlist;
	}

	/**
	 * @return the current clip id
	 */
	public Object getCurrentClipId() {
		return (current_clip_idx < 0) ? null : playlist
				.getIdByIndex(current_clip_idx);
	}

	/**
	 * @return the current clip
	 */
	public Item getCurrentClip() {
		Object id = getCurrentClipId();
		return (id == null) ? null : playlist.getItem(id);
	}

	/**
	 * @param id
	 *            the clip id to set as current
	 * @remark The clip must be in the container
	 */
	public void setCurrentClip(Object id) {
		this.current_clip_idx = this.playlist.indexOfId(id);
		requestRepaint();
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

}