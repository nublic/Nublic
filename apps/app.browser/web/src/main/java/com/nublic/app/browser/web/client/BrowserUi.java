package com.nublic.app.browser.web.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class BrowserUi extends Composite implements ModelUpdateHandler, OpenHandler<TreeItem>, SelectionHandler<TreeItem> {
	
	BrowserModel model = null;
	TreeAdapter treeAdapter = null;

	private static BrowserUiUiBinder uiBinder = GWT.create(BrowserUiUiBinder.class);
	@UiField FlowPanel centralPanel;
	@UiField Tree treeView;
	@UiField Button buttonFolderRequest;
	@UiField Button buttonFilesRequest;

	interface BrowserUiUiBinder extends UiBinder<Widget, BrowserUi> {
	}

	public BrowserUi(BrowserModel model) {
		// Inits
		initWidget(uiBinder.createAndBindUi(this));
		this.model = model;

		// Request to update folder tree with the root directory
		initFolder("");
		
		// To handle openings of tree nodes
		treeView.addOpenHandler(this);
		
		// To handle selections on an item of the tree
		treeView.addSelectionHandler(this);
		
		// To handle updates on files list
		model.addUpdateHandler(this);
		treeAdapter = new TreeAdapter(treeView, model);
	}
	
	
	public void initFolder(String path) {
		FolderNode node = model.createBranch(path);
		model.updateFolders(node, Constants.DEFAULT_DEPTH);
	}

	@UiHandler("buttonFolderRequest")
	void onButtonFolderRequestClick(ClickEvent event) {
		model.updateFolders(model.getFolderTree(), 4);
	}
	
	@UiHandler("buttonFilesRequest")
	void onButtonFilesRequestClick(ClickEvent event) {
		//centralPanel.add(new FileWidget());
		History.newItem(Constants.BROWSER_VIEW
				+ "?" + Constants.BROWSER_PATH_PARAMETER
				+ "=" + model.getFolderTree().getPath(), true);
	}

	// Handler of the open action for the browser tree
	@Override
	public void onOpen(OpenEvent<TreeItem> event) {
		FolderNode node = (FolderNode) event.getTarget().getUserObject();
		model.updateFolders(node, Constants.DEFAULT_DEPTH);
	}
	
	// Handler of the selection (click) action on the tree
	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		TreeItem item = event.getSelectedItem();
		History.newItem(Constants.BROWSER_VIEW
				+ "?" + Constants.BROWSER_PATH_PARAMETER
				+ "=" + ((FolderNode) item.getUserObject()).getPath(), true);
	}

	// Handler fired when a new update of the file list is available
	@Override
	public void onFilesUpdate(BrowserModel m, String path) {
		List <FileNode> fileList = m.getFileList();
		
		centralPanel.clear();
		for (FileNode n : fileList) {
			centralPanel.add(new FileWidget(n));
		}

		FolderNode node = model.createBranch(path);
		// If the given node has no children we try to update its info
		if (node.getChildren().isEmpty()) {
			model.updateFolders(node, Constants.DEFAULT_DEPTH);
		}
		//treeView.setSelectedItem(item);
	}

	@Override
	public void onFoldersUpdate(BrowserModel m, FolderNode node) {
		treeAdapter.updateView(node);
	}
}
