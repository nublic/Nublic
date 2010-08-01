/**
 * 
 */
package com.scamall.app.example;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;

/**
 * Vaadin main component for the example app. It just greets the user.
 * 
 * @author Alejandro Serrano
 */
public class ExampleMainComponent extends CustomComponent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1599157320589545163L;

	VerticalLayout layout;
	TextField nameText;
	Button greetButton;

	public ExampleMainComponent() {
		layout = new VerticalLayout();

		nameText = new TextField("Write your name here:");
		layout.addComponent(nameText);

		greetButton = new Button("Greet!", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				layout.getWindow().showNotification(
						"Hello, " + nameText.getValue() + "!",
						Window.Notification.TYPE_WARNING_MESSAGE);
				;
			}
		});

		this.setCompositionRoot(layout);
	}
}
