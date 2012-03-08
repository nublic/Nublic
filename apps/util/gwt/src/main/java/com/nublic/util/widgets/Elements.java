package com.nublic.util.widgets;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Utility class for DOM element manipulations, UIObject effects, etc.
 * 
 */
public class Elements {

	public interface FadeCallback {
		public void onFadeComplete();

		public void onFadeStart();
	}

	public static class EmptyFadeCallback implements FadeCallback {
        public void onFadeComplete() {}
        public void onFadeStart() {}
	}
	/**
	 * Causes an element to "fade in" via opacity. Make sure that element to
	 * fade is not visible before calling this method. When the callback is
	 * invoked with onFadeStart, the element can be set to visible as it is
	 * entirely transparent. When onFadeComplete is called, the element will be
	 * entirely opaque.
	 * 
	 * @param target
	 *            the UIObject to be faded in
	 * @param milliseconds
	 *            how long the fade effect should take to process
	 * @param maxOpacity
	 * 			  final opacity
	 * @param callback
	 *            the callback to be invoked on fade progress
	 */
	public static void fadeIn(final UIObject target, final int milliseconds, final int maxOpacity,
			final FadeCallback callback) {
	    fadeIn(target.getElement(), milliseconds, maxOpacity, callback);
	}
	public static void fadeIn(final Element e, final int milliseconds, final int maxOpacity,
            final FadeCallback callback) {

	    final int interval = milliseconds / 50;
		setOpacity(e, 0);
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				callback.onFadeStart();

				final Timer t = new Timer() {
					int pct = 0;

					@Override
					public void run() {
						pct += 2;
						pct = Math.min(pct, maxOpacity);
						setOpacity(e, pct);
						if (pct == maxOpacity) {
							this.cancel();
							callback.onFadeComplete();
						}
					}
				};
				t.scheduleRepeating(interval);
			}
		});	}

	/**
	 * Causes an element to "fade out" via opacity. When onFadeComplete is
	 * called, the element will be entirely transparent.
	 * 
	 * @param target
	 *            the UIObject to be faded out
	 * @param milliseconds
	 *            how long the fade effect should take to process
	 * @param minOpacity
	 * 			  final opacity
	 * @param callback
	 *            the callback to be invoked on fade progress
	 */
	public static void fadeOut(final UIObject target, final int milliseconds, final int minOpacity, 
			final FadeCallback callback) {
	    fadeOut(target.getElement(), milliseconds, minOpacity, callback);
	}
	public static void fadeOut(final Element e, final int milliseconds, final int minOpacity, 
	       final FadeCallback callback) {

		final int interval = milliseconds / 50;
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				callback.onFadeStart();

				final Timer t = new Timer() {
					int pct = 100;

					@Override
					public void run() {
						pct -= 2;
						pct = Math.max(pct, minOpacity);
						setOpacity(e, pct);
						if (pct == minOpacity) {
							this.cancel();
							callback.onFadeComplete();
						}
					}
				};
				t.scheduleRepeating(interval);

			}
		});
	}

	/**
     * Sets a UIObject's opacity
     * 
     * @param u
     * @param percent
     */
    public static void setOpacity(final UIObject u, final int percent) {
        setOpacity(u.getElement(), percent);
    }
    
	/**
	 * Sets a DOM element's opacity
	 * 
	 * @param e
	 * @param percent
	 */
	public static void setOpacity(final Element e, final int percent) {
		final Style s = e.getStyle();
		final double d = ((double) percent / (double) 100);
		s.setProperty("opacity", String.valueOf(d));
		s.setProperty("MozOpacity", String.valueOf(d));
		s.setProperty("KhtmlOpacity", String.valueOf(d));
		s.setProperty("filter", "alpha(opacity=" + percent + ")");
	}

	/**
	 * Enables/disables text selection for the specified element.
	 * 
	 * @param e
	 * @param selectable
	 */
	public static native void setTextSelectable(Element e, boolean selectable) /*-{
		if (selectable) {
			e.ondrag = null;
			e.onselectstart = null;
			e.style.MozUserSelect="text";
		} else {
			e.ondrag = function () { return false; };
			e.onselectstart = function () { return false; };
			e.style.MozUserSelect="none";
		}
	}-*/;
}