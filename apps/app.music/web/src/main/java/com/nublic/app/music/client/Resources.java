package com.nublic.app.music.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources used by the entire application.
 */
public interface Resources extends ClientBundle {
	public static final Resources INSTANCE =  GWT.create(Resources.class);
	
	@Source("images/play_mini.png")
	ImageResource playMini();
	
	@Source("images/pause_mini.png")
	ImageResource pauseMini();
	
	@Source("images/edit.png")
	ImageResource edit();
	
	@Source("images/add_at_end.png")
	ImageResource addAtEnd();
	
	@Source("images/save.png")
	ImageResource save();
	
	@Source("images/plus.png")
	ImageResource plus();
	
	@Source("images/delete.png")
	ImageResource delete();

	@Source("images/artist.png")
	ImageResource artist();

	@Source("images/album.png")
	ImageResource album();

	@Source("images/player_next.png")
	ImageResource playerNext();

	@Source("images/player_pause.png")
	ImageResource playerPause();

	@Source("images/player_play.png")
	ImageResource playerPlay();

	@Source("images/player_prev.png")
	ImageResource playerPrev();
	
	@Source("images/volume.png")
	ImageResource volume();
	
	@Source("images/volume_low.png")
	ImageResource volumeLow();
	
	@Source("images/mute.png")
	ImageResource mute();
	
	@Source("images/repeat.png")
	ImageResource repeat();
	
	@Source("images/shuffle.png")
	ImageResource shuffle();
	
	@Source("images/down_arrow.png")
	ImageResource downArrow();
	
	@Source("images/up_arrow.png")
	ImageResource upArrow();
	
	@Source("images/left_arrow.png")
	ImageResource leftArrow();
	
	@Source("images/drag1.png")
	ImageResource drag1();
	
	@Source("images/drag2.png")
	ImageResource drag2();
	
	@Source("images/drag3.png")
	ImageResource drag3();
	
	@Source("images/drag4.png")
	ImageResource drag4();
	
	@Source("images/drag5.png")
	ImageResource drag5();
	
	@Source("images/drag6.png")
	ImageResource drag6();
}