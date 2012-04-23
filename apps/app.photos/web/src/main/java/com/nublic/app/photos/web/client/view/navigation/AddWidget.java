package com.nublic.app.photos.web.client.view.navigation;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AddWidget extends Composite {
	private static AddWidgetUiBinder uiBinder = GWT.create(AddWidgetUiBinder.class);
	interface AddWidgetUiBinder extends UiBinder<Widget, AddWidget> { }

	@UiField Anchor addAnchor;
	@UiField TextBox addTextBox;
	String defaultText;
	List<PutTagHandler> tagHandlerList = new ArrayList<PutTagHandler>();
	
	public AddWidget() {
		this("New Collection");
	}
	
	public AddWidget(String defaultText) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.defaultText = defaultText;
		addTextBox.setVisible(false);
	}
	
	public void setDefaultText(String defaultText) {
		this.defaultText = defaultText;
	}
	
	public void showAnchor() {
		addAnchor.setVisible(true);
		addTextBox.setVisible(false);
	}
	
	public void showTextBox() {
		addAnchor.setVisible(false);
		addTextBox.setVisible(true);
		addTextBox.setFocus(true);
		addTextBox.setText(defaultText);
		addTextBox.selectAll();
	}
	
	@UiHandler("addAnchor")
	void onAddAnchorClick(ClickEvent event) {
		showTextBox();
	}
	
	@UiHandler("addTextBox")
	void onAddTextBoxBlur(BlurEvent event) {
		showAnchor();
	}

	@UiHandler("addTextBox")
	void onAddTextBoxKeyDown(KeyDownEvent event) {
		switch (event.getNativeKeyCode()) {
		case KeyCodes.KEY_ENTER:
			callHandlers(addTextBox.getText());
			break;
		case KeyCodes.KEY_ESCAPE:
			showAnchor();
			break;
		}
	}
	
	// Handlers to call when action is done
	public void addPutTagHandler(PutTagHandler putTagHandler) {
		tagHandlerList.add(putTagHandler);
	}
	
	private void callHandlers(String newTagName) {
		for (PutTagHandler pth : tagHandlerList) {
			pth.onPutTag(newTagName);
		}
		showAnchor();
	}
	
}
