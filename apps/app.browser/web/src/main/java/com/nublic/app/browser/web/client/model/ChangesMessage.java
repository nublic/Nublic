package com.nublic.app.browser.web.client.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.util.lattice.Ordering;
import com.nublic.util.lattice.PartialComparator;
import com.nublic.util.messages.Message;

public class ChangesMessage extends Message {
	BrowserModel model;
	String path;
	double date;
	
	public ChangesMessage(BrowserModel model, String path) {
		this.model = model;
		this.path = path;
		date = new Date().getTime() - Constants.TIME_TO_POLLING;
	}

	public static class Comparator implements PartialComparator<ChangesMessage> {
		@Override
		public Ordering compare(ChangesMessage a, ChangesMessage b) {
			if (a.getSequenceNumber() > b.getSequenceNumber()) {
				return Ordering.GREATER;
			} else if (a.getSequenceNumber() == b.getSequenceNumber()) {
				return Ordering.EQUAL;
			} else {
				return Ordering.LESS;
			}
		}
	}
	
	@Override
	public String getURL() {
		return URL.encode(GWT.getHostPageBaseURL() + "server/changes/" + String.valueOf((long)date) + "/" + path);
	}

	@Override
	public void onSuccess(Response response) {
		ChangesContent changesContent = null;
		
		if (Response.SC_OK == response.getStatusCode()) {
			// When the call is successful
			String text = response.getText();
			changesContent = JsonUtils.safeEval(text);
			List<FileNode> addedFiles = convertAddedFiles(changesContent.getNewFiles());
			List<FileNode> deletedFiles = convertDeletesFiles(changesContent.getDeletedFiles());
			
			model.addFiles(addedFiles);
			model.removeFiles(deletedFiles);
			model.fireFilesUpdateHandlers(false, false);
		} else {
			// We're not showing any feedback for polling errors
		}

	}

	private List<FileNode> convertAddedFiles(JsArray<FileContent> newFiles) {
		List<FileNode> retList = new ArrayList<FileNode>();
		for (int i = 0; i < newFiles.length() ; i++) {
			FileNode n = new FileNode(newFiles.get(i).getName(),	   newFiles.get(i).getMime(),
									  newFiles.get(i).getView(),	   newFiles.get(i).getSize(),
									  newFiles.get(i).getLastUpdate(), newFiles.get(i).getWritable());
			retList.add(n);
		}
		return retList;
	}

	private List<FileNode> convertDeletesFiles(JsArrayString deletedFiles) {
		List<FileNode> retList = new ArrayList<FileNode>();
		for (int i = 0; i < deletedFiles.length() ; i++) {
			FileNode n = new FileNode(deletedFiles.get(i), null, null, 0, 0, false);
			retList.add(n);
		}
		return retList;
	}

	@Override
	public void onError() {
		// We're not showing any feedback for polling errors
	}

}
