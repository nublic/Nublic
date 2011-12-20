package com.nublic.app.browser.web.client.devices;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.http.client.RequestBuilder;
import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.model.BrowserModel;
import com.nublic.app.browser.web.client.model.FolderMessage;
import com.nublic.app.browser.web.client.model.FolderNode;

public class DevicesManager {
	List<Device> devicesList = new ArrayList<Device>();

	public Device getDevice(DeviceKind kind, int id) {
		for (Device d : devicesList) {
			if (d.getKind() == kind && d.getId() == id) {
				return d;
			}
		}
		return null;
	}
	
	// To be mainly called by DeviceMessage
	public void addDevice(Device d) {
		devicesList.add(d);
	}

	public void removeDevice(Device d) {
		devicesList.remove(d);
	}

	public void clearDevices() {
		devicesList.clear();
	}

	// To transform real paths to mock ones and viceversa
//	public String getRealPath(String mockPath) {
//		// Converts paths of the form "dispositive_name/..." to "dispositive_kind/id/.."
//		if (mockPath.equals("")) {
//			return "";
//		}
//
//		String splitPath[] = mockPath.split("/", 2);
//		if (splitPath[0].equals(Constants.NUBLIC_ONLY)) {
//			return mockPath;
//		} else {
//			StringBuilder realPath = new StringBuilder();
//			for (Device d : devicesList) {
//				if (d.getName().equals(splitPath[0])) {
//					realPath.append(d.getKind().getPathName());
//					realPath.append("/");
//					realPath.append(d.getId());
//					realPath.append("/");
//					realPath.append(splitPath[1]);
//					return realPath.toString();
//				}
//			}
//			// This should never happen
//			return null;
//		}
//	}
	
	public String getMockPath(String realPath) {
		// Convert paths of the form "dispositive_kind/id/.." to "dispositive_name/..."
		String splitPath[] = realPath.split("/", 3);
		
		if (splitPath[0].equals(Constants.NUBLIC_ONLY) || splitPath.length < 2) {
			return realPath;
		} else {
			for (Device d : devicesList) {
				if (d.getKind() == DeviceKind.parse(splitPath[0])
						&& d.getId() == Integer.valueOf(splitPath[1])) {
					String rest = splitPath.length == 2 ? "" : splitPath[2]; 
					return d.getName() + rest;
				}
			}
			// TODO: This should never happen
//			return null;
			return realPath;
		}
	}

	public void createRootTree(BrowserModel model) {
//		FolderNode root = model.getFolderTree();
		
		createNodeOnRoot(Constants.NUBLIC_ONLY, Constants.NUBLIC_ONLY, true, model);
		
//		FolderNode nublicOnly = new FolderNode(root, Constants.NUBLIC_ONLY, true);
//		root.addChild(nublicOnly);
//		
//		// Get the folders of the new Node created by devices
//		FolderMessage message = new FolderMessage(nublicOnly, Constants.DEFAULT_DEPTH, model);
//		model.getFoldersMessageHelper().send(message, RequestBuilder.GET);

		for (Device d : devicesList) {
			createNodeOnRoot(d.getName(), d.getKind().getPathName() + "/" + d.getId(), false, model);
//			FolderNode deviceFolder = new FolderNode(root,
//													 d.getName(),
//													 d.getKind().getPathName() + "/" + d.getId(),
//													 false);
//			root.addChild(deviceFolder);
//			
//			// Get the folders of the new Node created by devices
//			message = new FolderMessage(deviceFolder, Constants.DEFAULT_DEPTH, model);
//			model.getFoldersMessageHelper().send(message, RequestBuilder.GET);
		}
	}

	private void createNodeOnRoot(String name, String path, boolean writable, BrowserModel model) {
		FolderNode root = model.getFolderTree();
		
		FolderNode newNode = new FolderNode(root, name, path, writable);
		root.addChild(newNode);
		
		// Get the folders of the new Node created by devices
		FolderMessage message = new FolderMessage(newNode, Constants.DEFAULT_DEPTH, model);
		model.getFoldersMessageHelper().send(message, RequestBuilder.GET);
	}
}
