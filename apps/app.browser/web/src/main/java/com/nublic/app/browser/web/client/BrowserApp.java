package com.nublic.app.browser.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.nublic.app.browser.web.client.UI.BrowserUi;
import com.nublic.app.browser.web.client.UI.EmptyUI;
import com.nublic.app.browser.web.client.UI.UIUtils;
import com.nublic.app.browser.web.client.model.BrowserModel;
import com.nublic.app.browser.web.client.model.FolderNode;
import com.nublic.app.browser.web.client.model.ParamsHashMap;
import com.nublic.util.error.ErrorPopup;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BrowserApp implements EntryPoint, ValueChangeHandler<String> {
	
	BrowserUi theUi;
	BrowserModel model;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		model = null;
		theUi = null;
	
	    String startingToken = History.getToken();
	    History.newItem(startingToken);
	    History.addValueChangeHandler(this);
	    History.fireCurrentHistoryState();
	}
	
	public void initBrowser() {
		// Create the model and the UI
		model = new BrowserModel();
		theUi = new BrowserUi(model);

		RootLayoutPanel rp = RootLayoutPanel.get();
	    rp.add(theUi);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();
		
		String args = "";
		int question = token.indexOf("?");
		if (question != -1) {
			args = token.substring(question + 1);
			token = token.substring(0, question);
		}
		
		ParamsHashMap hmap = new ParamsHashMap(args);
		if (token.isEmpty() || token.equals(Constants.BROWSER_VIEW)) {
			// Initial or browser page
			showBrowser(hmap);
		} else if (token.equals(Constants.IMAGE_VIEW)) {
			// An image visualization
			showImage(hmap);
		} else if (token.equals(Constants.DOCUMENT_VIEW)) {
			// A document visualization
			showDocument(hmap);
		} else if (token.equals(Constants.TEXT_VIEW)) {
			// Plain text visualization
			showText(hmap);
		} else if (token.equals(Constants.MUSIC_VIEW) || token.equals(Constants.VIDEO_VIEW)) {
			// A music or video visualization	
			showPlayer(hmap, token);
		} else {
			ErrorPopup.showError("Invalid URL");
		}
	}

	private void showBrowser(ParamsHashMap hmap) {
		// Necessary to distinct whether the user wants the browser below or is accessing a raw content
		if (model == null) {
			initBrowser();
		}
		String path = hmap.get(Constants.PATH_PARAMETER) == null ? "" : hmap.get(Constants.PATH_PARAMETER);

//		path = model.getDevicesManager().getMockPath(path);
		if (path.equals("")) {
			theUi.setWindowTitle(Constants.WINDOW_HOME_TITLE);
			// TODO: initial screen
		} else {
			theUi.setWindowTitle(path);
		}
		
		if (path.equals(model.getShowingPath())) {
			// If we're already showing the asked path we'll just uncover the browser view
			theUi.showBrowser();
		} else {
			boolean shouldUpdateFoldersOnSuccess = true;
			// If the branch to the folderNode already exists and has no children we try to update its information now
			// Otherwise it will be created when the answer of files confirms its a valid folder
			FolderNode node = model.search(path);
			if (node != null && node.getChildren().isEmpty()) {
				model.updateFolders(node, Constants.DEFAULT_DEPTH);
				shouldUpdateFoldersOnSuccess = false;
			}
			
			// show the browser screen (empties the file list of the model and fills it with the new one)
			model.updateFiles(path, shouldUpdateFoldersOnSuccess);
		}
	}
	
	private void showImage(ParamsHashMap hmap) {
		String path = hmap.get(Constants.PATH_PARAMETER);
		if (path != null) {
			if (model == null) {
				// Redirect navigation to raw resource in server
				Window.open(GWT.getHostPageBaseURL() + "server/view/" + path + "." + Constants.IMAGE_TYPE, "_self", "");
			} else {
				theUi.setWindowTitle(model.getDevicesManager().getMockPath(path));
				// show the image lightbox
				theUi.showImage(model.getDevicesManager().getMockPath(path));
				
//				theUi.setWindowTitle(path);
//				// show the image lightbox
//				theUi.showImage(path);
			}
		} else {
			ErrorPopup.showError("Image file not found");
		}
	}
	
	private void showDocument(ParamsHashMap hmap) {
		String path = hmap.get(Constants.PATH_PARAMETER);
		if (path != null) {
			if (model == null) {
				// Redirect navigation to raw resource in server
				Window.open(GWT.getHostPageBaseURL() + "server/view/" + path + "." + Constants.DOCUMENT_TYPE, "_self", "");
			} else {
				theUi.setWindowTitle(model.getDevicesManager().getMockPath(path));
				// show the image lightbox
				theUi.showPDF(model.getDevicesManager().getMockPath(path));
				
//				theUi.setWindowTitle(path);
//				// show the PDF lightbox
//				theUi.showPDF(path);
			}
		} else {
			ErrorPopup.showError("No path to the resource found");
		}
	}
	
	private void showText(ParamsHashMap hmap) {
		String path = hmap.get(Constants.PATH_PARAMETER);
		if (path != null) {
			if (model == null) {
				// Redirect navigation to raw resource in server
				Window.open(GWT.getHostPageBaseURL() + "server/view/" + path + "." + Constants.TEXT_TYPE, "_self", "");
			} else {
				theUi.setWindowTitle(model.getDevicesManager().getMockPath(path));
				// show the image lightbox
				theUi.showText(model.getDevicesManager().getMockPath(path));
				
//				theUi.setWindowTitle(path);
//				// show the Ace lightbox
//				theUi.showText(path);
			}
		} else {
			ErrorPopup.showError("No path to the resource found");
		}
	}
	
	private void showPlayer(ParamsHashMap hmap, String type) {
		String path = hmap.get(Constants.PATH_PARAMETER);
		if (path != null) {
			if (model == null) {
				// Create a new "empty" windows with the player
				EmptyUI empty = new EmptyUI();
				RootLayoutPanel rp = RootLayoutPanel.get();
			    rp.add(empty);
			    UIUtils.showPlayer(empty, path, false, type);
			} else {
				theUi.setWindowTitle(model.getDevicesManager().getMockPath(path));
				UIUtils.showPlayer(theUi, path, false, type);
				
//				theUi.setWindowTitle(path);
//				UIUtils.showPlayer(theUi, path, false, type);
			}
		} else {
			ErrorPopup.showError("No path to the resource found");
		}
	}
}
