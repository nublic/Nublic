package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class BrowserUi extends Composite {
	
	BrowserModel model = null;
	BrowserTreeViewModel treeView = new BrowserTreeViewModel();

	private static BrowserUiUiBinder uiBinder = GWT.create(BrowserUiUiBinder.class);
	
	@UiField(provided=true) CellTree folderTree = new CellTree(treeView, null);;
	@UiField FlowPanel contentPlace;
	@UiField Button buttonFolderRequest;

	interface BrowserUiUiBinder extends UiBinder<Widget, BrowserUi> {
	}

	public BrowserUi(BrowserModel model) {
		initWidget(uiBinder.createAndBindUi(this));
		this.model = model;
		this.model.updateFolders(model.getFolderTree(), Constants.DEFAULT_DEPTH);
		treeView.setModel(model);
		//treeView.updateTree();
		//folderTree.getRootTreeNode().setChildOpen(0, true);
	}

	@UiHandler("buttonFolderRequest")
	void onButtonFolderRequestClick(ClickEvent event) {
		model.updateFolders(model.getFolderTree(), 4);
		//treeView.updateTree();
	}
}
