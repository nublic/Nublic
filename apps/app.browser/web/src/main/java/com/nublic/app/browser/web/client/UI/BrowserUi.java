package com.nublic.app.browser.web.client.UI;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.bramosystems.oss.player.core.client.AbstractMediaPlayer;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
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
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.model.BrowserModel;
import com.nublic.app.browser.web.client.model.FileNode;
import com.nublic.app.browser.web.client.model.FolderNode;
import com.nublic.app.browser.web.client.model.ModelUpdateHandler;
import com.nublic.util.error.ErrorPopup;
import com.nublic.util.gwt.Callback;
import com.nublic.util.gwt.LazyLoader;
import com.nublic.util.messages.Message;
import com.nublic.util.messages.SequenceHelper;

import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorTheme;

public class BrowserUi extends Composite implements ModelUpdateHandler, OpenHandler<TreeItem>, SelectionHandler<TreeItem>, CloseHandler<PopupPanel>, ShowsPlayer, CheckedChangeHandler {
	private static BrowserUiUiBinder uiBinder = GWT.create(BrowserUiUiBinder.class);
	interface BrowserUiUiBinder extends UiBinder<Widget, BrowserUi> { }
	
	BrowserModel model = null;
	TreeAdapter treeAdapter = null;
	LazyLoader loader = new LazyLoader();
	String lastFilter = "";
	boolean descOrderCurrently = true;
	Set<Widget> selectedFiles = new HashSet<Widget>(); // See newSelectedFiles to better understand the typing
//	Object selectedStateLock = new Object();
	
	@UiField FlowPanel centralPanel;
//	@UiField HorizontalPanel orderPanel;
	@UiField Tree treeView;
	@UiField PushButton upButton;
	@UiField PushButton downButton;
	@UiField ListBox orderList;
	@UiField TextBox filterBox;
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
		downButton.setEnabled(false);
		orderList.addItem("by name");
		orderList.addItem("by type");
		orderList.addItem("by upload date");
		orderList.addItem("by size");
		orderList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				updateCentralPanel();
			}
		});
		
		// Set the properties of our popUpDialog. Should start empty, hidden, ...
		popUpBox = new FixedPopup(true, true); // auto-hide, modal

		popUpBox.hide();
		popUpBox.setGlassEnabled(true);
		popUpBox.addCloseHandler(this);
	}
	
	// To allow BrowserUi to work as a stateProvider
	public Set<Widget> getSelectedFiles() {
		return selectedFiles;
	}
	
	public String getPath() {
		return model.getShowingPath();
	}
	
	public List<FileNode> getShowingFiles() {
		return model.getFileList();
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
	public void onFilesUpdate(BrowserModel m) {
		selectedFiles.clear();
		updateCentralPanel();

		// We proceed to update the navigation tree
		FolderNode node = model.createBranch(model.getShowingPath());
		// If the given node has no children we try to update its info
		// TODO: I think this should be done at the same time as the request to the files, not in the response
		if (node.getChildren().isEmpty()) {
			model.updateFolders(node, Constants.DEFAULT_DEPTH);
		}

		// Get the item in the tree corresponding to the node in the model
		TreeItem nodeView = treeAdapter.search(node);

		// nodeView is null when node is the root (there is no view in the tree for the main root)
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
	
	// Converts strings to lowercases without diacritical marks
	String toComparable(String s) {
		return s.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
	}
	
	private void updateCentralPanel() {
		// We cancel any popup which could be hiding the central panel
		popUpBox.hide();

		List <FileNode> fileList = model.getFileList();
		String path = model.getShowingPath();
		
		// Filter the list
		if (!filterBox.getText().equals("")) {
			Collection<FileNode> fileColection = Collections2.filter(fileList, new Predicate<FileNode>(){
				@Override
				public boolean apply(FileNode elem) {
					return (toComparable(elem.getName())).contains(toComparable(filterBox.getText()));
//					return elem.getName().toLowerCase().contains(filterBox.getText().toLowerCase());
				}
			});
			fileList = Lists.newArrayList(fileColection);
		}

		// Order attending to orderList
		switch (orderList.getSelectedIndex()) {
			case 0:
				if (descOrderCurrently) {
					Collections.sort(fileList, FileNode.NAME_COMPARATOR);
				} else {
					Collections.sort(fileList, FileNode.INVERSE_NAME_COMPARATOR);
				}
				break;
			case 1:
				if (descOrderCurrently) {
					Collections.sort(fileList, FileNode.TYPE_COMPARATOR);
				} else {
					Collections.sort(fileList, FileNode.INVERSE_TYPE_COMPARATOR);
				}
				break;
			case 2:
				if (descOrderCurrently) {
					Collections.sort(fileList, FileNode.DATE_COMPARATOR);
				} else {
					Collections.sort(fileList, FileNode.INVERSE_DATE_COMPARATOR);
				}
				break;
			case 3:
				if (descOrderCurrently) {
					Collections.sort(fileList, FileNode.SIZE_COMPARATOR);
				} else {
					Collections.sort(fileList, FileNode.INVERSE_SIZE_COMPARATOR);
				}
				break;
				
		}
		
		// Update the information shown in the central panel
		centralPanel.clear();
		for (FileNode n : fileList) {
			FileWidget newFileWidget = new FileWidget(n, path);
			// To handle changes in selections of files
			newFileWidget.addCheckedChangeHandler(this);
			// Add it to central panel
			centralPanel.add(newFileWidget);
		}
		
		// Select the new widgets marked as selected by the user before
		// newSelectedFiles will always be Set<FileWidget> but it comes from the intersection of Set<Widget> and we must maintain the type
		Set<Widget> newSelectedFiles = Sets.intersection(Sets.newHashSet(centralPanel), selectedFiles);
		for (Widget fw : newSelectedFiles) {
			((FileWidget)fw).setChecked(true);
		}

		selectedFiles = Sets.newHashSet(newSelectedFiles);
	}
	
	// Checks the checkboxes of all the file widgets showing
	public void selectAllFiles() {
		selectedFiles = Sets.newHashSet(centralPanel);
		for (Widget w : centralPanel) {
			((FileWidget)w).setChecked(true);
		}
	}
	
	// Unchecks the checkboxes of all the file widgets showing
	public void unselectAllFiles() {
		selectedFiles.clear();
		for (Widget w : centralPanel) {
			((FileWidget)w).setChecked(false);
		}
	}

	// Handler of the pop-up close event
	@Override
	public void onClose(CloseEvent<PopupPanel> event) {
		if (event.isAutoClosed()) {
			History.back();
		}
	}
	
	// Handlers for ordering buttons
	@UiHandler("upButton")
	void onUpButtonClick(ClickEvent event) {
		changeOrder(false);
	}
	
	@UiHandler("downButton")
	void onDownButtonClick(ClickEvent event) {
		changeOrder(true);
	}
	
	private void changeOrder(boolean desc) {
		descOrderCurrently = desc;
		if (desc) {
			upButton.setEnabled(true);
			downButton.setEnabled(false);
		} else {
			upButton.setEnabled(false);
			downButton.setEnabled(true);
		}
		updateCentralPanel();
	}
	
	// Handler for filter text change
	@UiHandler("filterBox")
	void onFilterBoxKeyUp(KeyUpEvent event) {
		if (!filterBox.getText().equals(lastFilter)) {
			lastFilter = filterBox.getText();
			updateCentralPanel();
		}
	}

	// Handler for model change event
	@Override
	public void onFoldersUpdate(BrowserModel m, FolderNode node) {
		treeAdapter.updateView(node);
	}
	
	// Handler for dealing with selections by the user on files
	@Override
	public void onChekedChange(FileWidget w) {
		if (w.isChecked()) {
			selectedFiles.add(w);
		} else {
			selectedFiles.remove(w);
		}
	}

	public void showImage(String path) {
		final Image newImage = new Image(GWT.getHostPageBaseURL()
				+ "server/view/" + path + "." + Constants.IMAGE_TYPE);
		// Load handler
		newImage.addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				popUpBox.setOriginalSize(newImage.getWidth(),
						newImage.getHeight());
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
	}

	public void showPDF(String path) {
		Frame frame = new Frame(GWT.getHostPageBaseURL() + "server/view/" + path + "." + Constants.DOCUMENT_TYPE);
		popUpBox.setContentWidget(frame);
		popUpBox.show();
	}
	
	public void showText(final String path) {
		final AceEditor editor = new AceEditor();
		// Get text
		Message m = new Message() {
			@Override
			public void onSuccess(Response response) {
				editor.setText(response.getText());
			}
			@Override
			public void onError() {
				ErrorPopup.showError("Error reading text file");
			}
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
	}

	@Override
	public void showPlayer(AbstractMediaPlayer player) {
		popUpBox.setContentWidget(player);
		popUpBox.show();
	}
		
	public void setWindowTitle(String title) {
		Window.setTitle(Constants.WINDOW_PRETITLE + title);
	}

}
