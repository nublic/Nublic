package com.nublic.app.browser.web.client.devices;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
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
	
	public List<Device> getDevicesList() {
		return devicesList;
	}

	public String getMockPath(String realPath) {
		// Convert paths of the form "device_kind/id/.." to "device_name/..."
		String splitPath[] = realPath.split("/", 3);
		if (realPath.equals("")) {
			return "";
		} else if (splitPath[0].equals(Constants.NUBLIC_ONLY) || splitPath.length < 2) {
			StringBuilder mockPath = new StringBuilder(Constants.I18N.nublicFiles());
			mockPath.append("/");
			if (splitPath.length == 2) {
				mockPath.append(splitPath[1]);
			} else if (splitPath.length > 2) {
				mockPath.append(splitPath[1]);
				mockPath.append("/");
				mockPath.append(splitPath[2]);
			}
			return mockPath.toString();
		} else {
			for (Device d : devicesList) {
				if (d.getKind() == DeviceKind.parseFromPath(splitPath[0])
						&& d.getId() == Integer.valueOf(splitPath[1])) {
					String rest = splitPath.length == 2 ? "" : splitPath[2];
					return d.getName() + rest;
				}
			}
			// This should never happen
			return null;
		}
	}
	
	public List<String> splitPath(String path) {
		String[] tokenArray = path.split("/");
		if (tokenArray.length < 2 || tokenArray[0].equals(Constants.NUBLIC_ONLY)) {
			return Lists.newArrayList(tokenArray);
		} else {
			List<String> tokenList = Lists.newArrayList(tokenArray);
			StringBuilder newBegining = new StringBuilder(tokenList.remove(0));
			newBegining.append("/");
			newBegining.append(tokenList.remove(0));
			tokenList.add(0, newBegining.toString());
			return tokenList;
		}
	}

	public void createRootTree(BrowserModel model) {		
//		createNodeOnRoot(Constants.NUBLIC_ONLY, Constants.NUBLIC_ONLY, true, model);
		createNodeOnRoot(Constants.I18N.nublicFiles(), Constants.NUBLIC_ONLY, true, model);

		for (Device d : devicesList) {
			createNodeOnRoot(d.getName(), d.getKind().getPathName() + "/" + d.getId(), false, model);
		}
	}

	private void createNodeOnRoot(String name, String path, boolean writable, BrowserModel model) {
		FolderNode whatIsAlreadyCreated = model.search(path);
		if (whatIsAlreadyCreated == null) {
			FolderNode root = model.getFolderTree();
		
			whatIsAlreadyCreated = new FolderNode(root, name, path, writable);
			root.addChild(whatIsAlreadyCreated);
		}
		// Get the folders of the new Node created by devices
		FolderMessage message = new FolderMessage(whatIsAlreadyCreated, Constants.DEFAULT_DEPTH, model);
		model.getFoldersMessageHelper().send(message, RequestBuilder.GET);
			
	}
}
