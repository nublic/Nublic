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
import com.nublic.util.lattice.Ordering;
import com.nublic.util.lattice.PartialComparator;
import com.nublic.util.messages.Message;

public class ChangesMessage extends Message {
	BrowserModel model;
	String path;
	double creationDate;
	double timeToAsk;
	
	public ChangesMessage(BrowserModel model, String path, double timeToAsk) {
		this.model = model;
		this.path = path;
		this.timeToAsk = timeToAsk;
		creationDate = new Date().getTime();
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
		return URL.encode(GWT.getHostPageBaseURL() + "server/changes/" + String.valueOf((long)timeToAsk) + "/" + path);
	}

	@Override
	public void onSuccess(Response response) {
		ChangesContent changesContent = null;
		
		if (response.getStatusCode() == Response.SC_OK) {
			// When the call is successful
			String text = response.getText();
			changesContent = JsonUtils.safeEval(text);
			List<FileNode> addedFiles = convertAddedFiles(changesContent.getNewFiles());
			List<FileNode> deletedFiles = convertDeletesFiles(changesContent.getDeletedFiles());
			
			// TODO: Don't add files, but look for modifies ones also
			model.addFiles(addedFiles);
			model.removeFiles(deletedFiles);
			model.fireFilesUpdateHandlers(false, false);
			model.scheduleNewTimer();
		} else {
			// We're not showing any feedback for polling errors
		}

	}

	// From JsArray to List<FileNode>
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

	// From JsArray to List<FileNode>
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

	public double getCreationDate() {
		return creationDate;
	}

}
