package com.nublic.app.browser.web.client;

import java.util.List;
import java.util.Stack;

import com.bramosystems.oss.player.core.client.AbstractMediaPlayer;
import com.bramosystems.oss.player.core.client.LoadException;
import com.bramosystems.oss.player.core.client.PlayerUtil;
import com.bramosystems.oss.player.core.client.Plugin;
import com.bramosystems.oss.player.core.client.PluginNotFoundException;
import com.bramosystems.oss.player.core.client.PluginVersionException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class BrowserUi extends Composite implements ModelUpdateHandler, OpenHandler<TreeItem>, SelectionHandler<TreeItem>, CloseHandler<PopupPanel> {
	
	BrowserModel model = null;
	TreeAdapter treeAdapter = null;

	private static BrowserUiUiBinder uiBinder = GWT.create(BrowserUiUiBinder.class);
	@UiField FlowPanel centralPanel;
	@UiField Tree treeView;
	FixedPopup popUpBox;
	

	interface BrowserUiUiBinder extends UiBinder<Widget, BrowserUi> { }

	public BrowserUi(BrowserModel model) {
		// Inits
		initWidget(uiBinder.createAndBindUi(this));
		this.model = model;

		// Request to update folder tree with the root directory
		model.updateFolders(model.getFolderTree(), Constants.DEFAULT_DEPTH);
		
		// To handle openings of tree nodes
		treeView.addOpenHandler(this);
		
		// To handle selections on an item of the tree
		treeView.addSelectionHandler(this);
		
		// To handle updates on files list
		model.addUpdateHandler(this);
		treeAdapter = new TreeAdapter(treeView, model);
		
		// Set the properties of our popUpDialog. Should start empty, hidden, ...
		popUpBox = new FixedPopup(true, true); // auto-hide, modal

		popUpBox.hide();
		popUpBox.setGlassEnabled(true);
		popUpBox.addCloseHandler(this);
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
				+ "?" + Constants.PATH_PARAMETER
				+ "=" + ((FolderNode) item.getUserObject()).getPath(), true);
	}

	// Handler fired when a new update of the file list is available
	@Override
	public void onFilesUpdate(BrowserModel m, String path) {
		// We cancel any popup which could be hidding the central panel
		popUpBox.hide();
		
		List <FileNode> fileList = m.getFileList();

		// Update the information shown in the central panel
		centralPanel.clear();
		for (FileNode n : fileList) {
			centralPanel.add(new FileWidget(n, path));
		}

		FolderNode node = model.createBranch(path);
		// If the given node has no children we try to update its info
		if (node.getChildren().isEmpty()) {
			model.updateFolders(node, Constants.DEFAULT_DEPTH);
		}
		
		TreeItem nodeView = treeAdapter.search(node);

		// nodeView is null when node is the root (there is no view for the main root)
		if (nodeView != null) {
			// Open the tree and show all the parents of the selected node open
			TreeItem parent = nodeView.getParentItem();
			Stack<TreeItem> pathStack = new Stack<TreeItem>();
			while (parent != null) {
				pathStack.push(parent);
				parent = parent.getParentItem();
			}
			while (!pathStack.isEmpty()) {
				TreeItem iterator = pathStack.pop();
				iterator.setState(true, false);
			}
			
			// Set the node as selected
			treeView.setSelectedItem(nodeView);
		}
	}
	
	// Handler of the pop-up close event
	@Override
	public void onClose(CloseEvent<PopupPanel> event) {
		if (event.isAutoClosed()) {
			History.back();
		}
	}

	@Override
	public void onFoldersUpdate(BrowserModel m, FolderNode node) {
		treeAdapter.updateView(node);
	}
	
	public void showImage(ParamsHashMap paramsHashMap) {
		String path = paramsHashMap.get(Constants.PATH_PARAMETER);
		if (path != null) {
			final Image newImage = new Image(GWT.getHostPageBaseURL() + "server/view/" + path + "." + Constants.IMAGE_TYPE);
			newImage.addLoadHandler(new LoadHandler() {
				@Override
				public void onLoad(LoadEvent event) {
					// these 4 things give the same result
//					int width = newImage.getWidth();
//					int width2 = newImage.getElement().getOffsetWidth();
//					int width3 = ImageElement.as(newImage.getElement()).getWidth();
//					int width4 = newImage.getOffsetWidth();
					popUpBox.setOriginalSize(newImage.getWidth(), newImage.getHeight());
				}
			});

			popUpBox.setContentWidget(newImage);
			popUpBox.show();
		} else {
			// TODO: error, image not found
		}

	}


	public void showPDF(ParamsHashMap hmap) {
		String path = hmap.get(Constants.PATH_PARAMETER);
		if (path != null) {
			Frame frame = new Frame(GWT.getHostPageBaseURL() + "server/view/" + path + "." + Constants.DOCUMENT_TYPE);
			popUpBox.setContentWidget(frame);
			popUpBox.show();
		} else {
			// TODO: error, image not found
		}

	}

	public void showPlayer(ParamsHashMap hmap) {
		AbstractMediaPlayer player = null;
		try {
			String path = hmap.get(Constants.PATH_PARAMETER);
			if (path != null) {
			     // get any player that can playback media
			     player = PlayerUtil.getPlayer(Plugin.Auto, 
			    		    GWT.getHostPageBaseURL() + "server/view/" + path + "." + Constants.MUSIC_TYPE,
			                false, "50px", "100%");
	//		     panel.setWidget(player); // add player to panel.
			     popUpBox.setContentWidget(player);
			     popUpBox.show();
			} else {
				// TODO: error, music not found
			}
		} catch(LoadException e) {
		     // catch loading exception and alert user
			// TODO: error
//		     Window.alert("An error occured while loading");
		} catch (PluginVersionException e) {
		     // catch PluginVersionException, thrown if required plugin version is not found
			popUpBox.setContentWidget(PlayerUtil.getMissingPluginNotice(e.getPlugin()));
			popUpBox.show();
		} catch(PluginNotFoundException e) {
		     // catch PluginNotFoundException, thrown if no plugin is not found
			popUpBox.setContentWidget(PlayerUtil.getMissingPluginNotice(e.getPlugin()));
			popUpBox.show();
		}
	}
}
