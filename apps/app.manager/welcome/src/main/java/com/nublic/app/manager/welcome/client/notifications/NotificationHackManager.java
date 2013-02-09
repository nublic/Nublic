package com.nublic.app.manager.welcome.client.notifications;

import java.util.ArrayList;

import com.google.gwt.safehtml.shared.SafeHtml;

// Class created to mock the behaviour of a notification system
// Shouldn't exist in final version
public class NotificationHackManager {
	public static ArrayList<Notification> getNotificationsOf(String id) {
		ArrayList<Notification> retList = new ArrayList<Notification>();

		if (id.compareTo("browser") == 0) {
		} else if (id.compareTo("photos") == 0) {
			retList.add(new Notification(NotificationType.ATTENTION, new SafeHtml() {
				private static final long serialVersionUID = 4654149898045281890L;

				@Override
				public String asString() {
					return "Se eliminó el álbum <span style=\"color: red\">erasmus</span>";
				}
			}));
			retList.add(new Notification(new SafeHtml() {
				private static final long serialVersionUID = 4654149898045281890L;

				@Override
				public String asString() {
					return "Añadidas 3 fotos al álbum <a href=\"\">vacaciones</a>";
				}
			}));
		} else if (id.compareTo("music") == 0) {
			retList.add(new Notification(new SafeHtml() {
				private static final long serialVersionUID = -482079151180273389L;

				@Override
				public String asString() {
					return "Hay 33 nuevas canciones de <a href=\"\">The Beatles</a>, <a href=\"\">Muse</a> y <a href=\"\">Queen</a> disponibles en el reproductor de música";
				}
			}));
		} else if (id.compareTo("house") == 0) {
			retList.add(new Notification(NotificationType.POWER, new SafeHtml() {
				private static final long serialVersionUID = 4654149898045281890L;

				@Override
				public String asString() {
					return "Programado  <a href=\"\">apagado de luces</a> en <a href=\"\">salón</a> a las <strong>23:30</strong>";
				}
			}));
		} else {
			retList.add(new Notification());
		}
		return retList;
	}

	public static ArrayList<Notification> getSystemNotifications() {
		ArrayList<Notification> retList = new ArrayList<Notification>();

		retList.add(new Notification(NotificationType.USER, new SafeHtml() {
			private static final long serialVersionUID = 4654149898045281890L;
			@Override
			public String asString() {
				return "Creado nuevo usuario: <strong>pablo</strong>";
			}
		}));
		retList.add(new Notification(NotificationType.PASSWORD, new SafeHtml() {
			private static final long serialVersionUID = 4654149898045281890L;
			@Override
			public String asString() {
				return "Cambiada contraseña de <strong>david</strong>";
			}
		}));
		return retList;
	}
}
