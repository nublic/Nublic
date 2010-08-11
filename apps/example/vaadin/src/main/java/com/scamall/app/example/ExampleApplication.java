/*
 * Copyright 2009 IT Mill Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.scamall.app.example;

import com.scamall.app.widget.flowplayer.Flowplayer;
import com.vaadin.Application;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class ExampleApplication extends Application {
	Window window;
	VerticalLayout layout;
	TextField nameText;
	Button greetButton;

	@Override
	public void init() {
		window = new Window("My Vaadin Application");
		setMainWindow(window);

		layout = new VerticalLayout();

		nameText = new TextField("Write your name here:");
		layout.addComponent(nameText);

		greetButton = new Button("Greet!", new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				layout.getWindow().showNotification(
						"Hello, " + nameText.getValue() + "!",
						Window.Notification.TYPE_WARNING_MESSAGE);
				;
			}
		});
		layout.addComponent(greetButton);

		Flowplayer player = new Flowplayer();
		layout.addComponent(player);

		window.addComponent(layout);
	}

}
