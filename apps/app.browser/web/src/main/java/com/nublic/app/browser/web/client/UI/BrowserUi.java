package com.nublic.app.browser.web.client.UI;

import java.util.List;
import java.util.Stack;

import com.bramosystems.oss.player.core.client.AbstractMediaPlayer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.model.BrowserModel;
import com.nublic.app.browser.web.client.model.FileNode;
import com.nublic.app.browser.web.client.model.FolderNode;
import com.nublic.app.browser.web.client.model.ModelUpdateHandler;
import com.nublic.app.browser.web.client.model.ParamsHashMap;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.gwt.Callback;
import com.nublic.util.gwt.LazyLoader;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorTheme;

public class BrowserUi extends Composite implements ModelUpdateHandler, OpenHandler<TreeItem>, SelectionHandler<TreeItem>, CloseHandler<PopupPanel>, ShowsPlayer {
	private static BrowserUiUiBinder uiBinder = GWT.create(BrowserUiUiBinder.class);
	interface BrowserUiUiBinder extends UiBinder<Widget, BrowserUi> { }
	
	BrowserModel model = null;
	TreeAdapter treeAdapter = null;
	LazyLoader loader = new LazyLoader();
	
	@UiField FlowPanel centralPanel;
//	@UiField HorizontalPanel orderPanel;
	@UiField Tree treeView;
	@UiField PushButton upButton;
	@UiField PushButton downButton;
	@UiField ListBox orderList;
	FixedPopup popUpBox;

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

		// Init the current order mode
		upButton.setEnabled(false);
		orderList.addItem("by name");
		orderList.addItem("by type");
		orderList.addItem("by upload date");
		
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
	
	// TODO: set title of browser
	public void showImage(ParamsHashMap paramsHashMap) {
		String path = paramsHashMap.get(Constants.PATH_PARAMETER);
		if (path != null) {
			final Image newImage = new Image(GWT.getHostPageBaseURL() + "server/view/" + path + "." + Constants.IMAGE_TYPE);
			// Load handler
			newImage.addLoadHandler(new LoadHandler() {
				@Override
				public void onLoad(LoadEvent event) {
					popUpBox.setOriginalSize(newImage.getWidth(), newImage.getHeight());
				}
			});
			// Error handler (for example when the image hasn't been found)
			newImage.addErrorHandler(new ErrorHandler() {	
				@Override
				public void onError(ErrorEvent event) {
					popUpBox.hide();
					ErrorPopup.showError("Image file not found");
				}
			});

			popUpBox.setContentWidget(newImage);
			popUpBox.show();
		} else {
			ErrorPopup.showError("Image file not found");
		}
	}

	public void showPDF(ParamsHashMap hmap) {
		String path = hmap.get(Constants.PATH_PARAMETER);
		if (path != null) {
			Frame frame = new Frame(GWT.getHostPageBaseURL() + "server/view/" + path + "." + Constants.DOCUMENT_TYPE);
		
			popUpBox.setContentWidget(frame);
			popUpBox.show();
		} else {
			ErrorPopup.showError("Document file not found");
		}
	}
	
	public void showText(ParamsHashMap hmap) {
		final String path = hmap.get(Constants.PATH_PARAMETER);
		if (path != null) {
			final AceEditor editor = new AceEditor();
			// Get text
			Message m = new Message() {
				@Override
				public void onSuccess(Response response) {
					editor.setText(response.getText());
				}
				@Override
				public void onError() {	}
				@Override
				public String getURL() {
					return GWT.getHostPageBaseURL() + "server/view/" + path + "." + Constants.TEXT_TYPE;
				}
			};
			SequenceHelper.sendJustOne(m, RequestBuilder.GET);
			// Show the widget
			popUpBox.setContentWidget(editor);
			popUpBox.show();
			editor.startEditor();
			editor.setTheme(AceEditorTheme.ECLIPSE);
			editor.setReadOnly(true);
			editor.setShowPrintMargin(false);
			editor.setUseWrapMode(true);
			
			final AceEditorMode mode = AceEditorMode.fromPath(path);
			if (mode != null) {
				loader.loadJS(GWT.getHostPageBaseURL() + "browserapp/ace/mode-" + mode.getName() + ".js", new Callback<Event>() {
					@Override
					public void execute(Event t) {
						editor.setMode(mode);
					}
				});
			}
			
		} else {
			ErrorPopup.showError("Document file not found");
		}
	}

	@Override
	public void showPlayer(AbstractMediaPlayer player) {
		popUpBox.setContentWidget(player);
		popUpBox.show();
	}
}
