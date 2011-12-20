package com.nublic.app.browser.web.client.devices;

import java.util.ArrayList;
import java.util.List;

import com.nublic.app.browser.web.client.Constants;
import com.nublic.app.browser.web.client.model.BrowserModel;
import com.nublic.app.browser.web.client.model.FolderNode;

public class DevicesManager {
	List<Device> devicesList = new ArrayList<Device>();

//	public void updateDevices() {
//		DeviceMessage m = new DeviceMessage(this);
//		SequenceHelper.sendJustOne(m, RequestBuilder.GET);
//	}

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
	// TODO: maybe the devices are not updated yet when the query arrives
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

//	public void createRootTree(BrowserModel model, FolderNode n, JsArray<FolderContent> folderList) {
//		if (folderList.length() != 0) {
//			n.clear();
//			for (int i = 0; i < folderList.length(); i++) {
//				FolderContent f = folderList.get(i);
//				if (f.getName().equals(Constants.NUBLIC_ONLY)) {
//					FolderNode child = new FolderNode(n, Constants.NUBLIC_ONLY, f.getWritable());
//					n.addChild(child);
//					// Recursive call to update child
//					model.updateTreeNoSync(child, f.getSubfolders());
//				} else {
//					// Go through every id in each kind of device
//					JsArray<FolderContent> subfolders = f.getSubfolders();
//					for (int j = 0; j < subfolders.length(); j++) {
//						FolderContent g = subfolders.get(j);
//						Device dev = getDevice(DeviceKind.parse(f.getName()),
//											   Integer.valueOf(g.getName()));
//						FolderNode child = new FolderNode(n, dev.getName(), g.getWritable());
//						n.addChild(child);
//						// Recursive call to update child
//						model.updateTreeNoSync(child, f.getSubfolders());
//					}
//				}
//			}
//		}
	public void createRootTree(BrowserModel model) {
		FolderNode root = model.getFolderTree();
		
		FolderNode nublicOnly = new FolderNode(root, Constants.NUBLIC_ONLY, true);
		root.addChild(nublicOnly);

		for (Device d : devicesList) {
			FolderNode deviceFolder = new FolderNode(root,
													 d.getName(),
													 d.getKind().getPathName() + "/" + d.getId(),
													 false);
			root.addChild(deviceFolder);
		}
	}
}
