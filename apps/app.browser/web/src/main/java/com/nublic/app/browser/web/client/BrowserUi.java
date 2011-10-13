package com.nublic.app.browser.web.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class BrowserUi extends Composite implements OpenHandler<TreeNode>, FileListUpdateHandler {
	
	BrowserModel model = null;
	BrowserTreeViewModel treeView = new BrowserTreeViewModel();

	private static BrowserUiUiBinder uiBinder = GWT.create(BrowserUiUiBinder.class);
	
	@UiField(provided=true) CellTree folderTree = new CellTree(treeView, null);
	@UiField FlowPanel centralPanel;
	@UiField Button buttonFolderRequest;
	@UiField Button buttonFilesRequest;

	interface BrowserUiUiBinder extends UiBinder<Widget, BrowserUi> {
	}

	public BrowserUi(BrowserModel model) {
		// inits
		initWidget(uiBinder.createAndBindUi(this));
		this.model = model;
		treeView.setModel(model);
		
		// request to update folder tree with the root directory
		model.updateFolders(model.getFolderTree(), Constants.DEFAULT_DEPTH);
		
		// to handle openings of tree nodes
		folderTree.addOpenHandler(this);
		
		// to handle updates on files list
		model.addUpdateHandler(this);
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
	public void onOpen(OpenEvent<TreeNode> event) {
		Object openedValue = event.getTarget().getValue();
		if (openedValue instanceof FolderNode) {
			model.updateFolders((FolderNode) openedValue, Constants.DEFAULT_DEPTH);
		}
	}

	// Handler fired when a new update of the file list is available
	@Override
	public void onUpdate(BrowserModel m) {
		List <FileNode> fileList = m.getFileList();
		
		centralPanel.clear();
		for (FileNode n : fileList) {
			centralPanel.add(new FileWidget(n));
		}
	}
}
