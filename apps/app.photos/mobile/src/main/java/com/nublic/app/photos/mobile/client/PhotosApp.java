package com.nublic.app.photos.mobile.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.gwtmobile.phonegap.client.Device;
import com.gwtmobile.phonegap.client.Event;
import com.gwtmobile.phonegap.client.Event.Callback;
import com.gwtmobile.ui.client.page.Page;
import com.gwtmobile.ui.client.page.PageHistory;
import com.gwtmobile.ui.client.utils.Utils;
import com.gwtmobile.ui.client.widgets.Button;
import com.gwtmobile.ui.client.widgets.HeaderPanel;
import com.nublic.app.photos.mobile.client.ui.MainUi;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PhotosApp implements EntryPoint {

	public static MainUi mainUi;

	@Override
	public void onModuleLoad() {

		if (Utils.isAndroid() || Utils.isIOS()) {

			if (Utils.isAndroid()) {
				Event.onBackButton(new Event.Callback() {
					@Override
					public void onEventFired() {
						Utils.Console("Pulsado boton atras");
						onBackKeyDown();
					}
				});
			}

			Event.onDeviceReady(new Callback() {
				@Override
				public void onEventFired() {
					Utils.Console("Estamos iniciando..");
					new Timer() {
						@Override
						public void run() {
							if (mainUi == null) {
								Utils.Console("Loading main ui...");
								mainUi = new MainUi();
								Page.load(mainUi);
							} else {
								this.cancel();
							}
						}
					}.scheduleRepeating(50);
				}
			});
		} else {
			mainUi = new MainUi();
			Page.load(mainUi);
		}
	}

	public void onBackKeyDown() {
		if (PageHistory.Instance.from() == null) {
			Device.exitApp();
		} else {
			if (!PageHistory.Instance.current().getClass().toString().endsWith(".EventUi")) {
				// emulate click on the header back button for page transition to show.
				emulateClickOnBackButton();
			}
		}
	}

	protected void emulateClickOnBackButton() {
		HTMLPanel current = (HTMLPanel) PageHistory.Instance.current().getWidget();
		HeaderPanel header = (HeaderPanel) current.getWidget(0);
		Button left = header.getLeftButton();
		NativeEvent event = Document.get().createClickEvent(1, 1, 1, 1, 1, false, false, false, false);
		left.getElement().dispatchEvent(event);
	}

}
