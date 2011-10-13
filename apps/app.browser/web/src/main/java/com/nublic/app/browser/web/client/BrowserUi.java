package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class BrowserUi extends Composite implements OpenHandler<TreeNode> {
	
	BrowserModel model = null;
	BrowserTreeViewModel treeView = new BrowserTreeViewModel();

	private static BrowserUiUiBinder uiBinder = GWT.create(BrowserUiUiBinder.class);
	
	@UiField(provided=true) CellTree folderTree = new CellTree(treeView, null);
	@UiField FlowPanel contentPlace;
	@UiField Button buttonFolderRequest;

	interface BrowserUiUiBinder extends UiBinder<Widget, BrowserUi> {
	}

	public BrowserUi(BrowserModel model) {
		initWidget(uiBinder.createAndBindUi(this));
		this.model = model;
		treeView.setModel(model);
		this.model.updateFolders(model.getFolderTree(), Constants.DEFAULT_DEPTH);
		folderTree.addOpenHandler(this);
	}

	@UiHandler("buttonFolderRequest")
	void onButtonFolderRequestClick(ClickEvent event) {
		model.updateFolders(model.getFolderTree(), 4);
		//treeView.updateTree();
	}

	// Handler of open action for the browser tree
	@Override
	public void onOpen(OpenEvent<TreeNode> event) {
		Object openedValue = event.getTarget().getValue();
		if (openedValue instanceof FolderNode) {
			model.updateFolders((FolderNode) openedValue, Constants.DEFAULT_DEPTH);
		}
	}
}
