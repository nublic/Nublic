package com.nublic.app.browser.web.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.History;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.TreeViewModel;

public class BrowserTreeViewModel implements TreeViewModel {
	BrowserModel model;
	ListDataProvider<FolderNode> rootDataProvider;

	public BrowserTreeViewModel() {
		model = null;
		rootDataProvider = new ListDataProvider<FolderNode>();
	}

	public void setModel(BrowserModel model) {
		// To provide the data in the tree we will use the model which is updated asynchronously
		this.model = model;
		
		model.getFolderTree().setDataProvider(rootDataProvider);
	}

	// The cell used to render Nodes.
	private static class NodeCell extends AbstractCell<FolderNode> {
		NodeCell(){
			super("click", "keydown");
		}
		
		@Override
		public void render(Context context, FolderNode value, SafeHtmlBuilder sb) {
			if (value != null) {
				sb.appendEscaped(value.getContent().getName());
			}
		}
		
		// To handle clicks on the tree cells
		@Override
        public void onBrowserEvent(Context context, Element parent, FolderNode value, NativeEvent event, ValueUpdater<FolderNode> valueUpdater) {
			// Let AbstractCell handle the keydown event.
			super.onBrowserEvent(context, parent, value, event, valueUpdater);

			// Handle the click event.
			if ("click".equals(event.getType()) && value != null) {
				cellAction(value);
			}
		}
		
		// To handle the Enter key press
		@Override
	    protected void onEnterKeyDown(Context context, Element parent, FolderNode value, NativeEvent event,
	        ValueUpdater<FolderNode> valueUpdater) {
			if (value != null) {
				cellAction(value);
			}
	    }
		
		public static void cellAction(FolderNode node) {
			// "Redirect" to the correspondent browser page
			History.newItem(Constants.BROWSER_VIEW
					+ "?" + Constants.BROWSER_PATH_PARAMETER
					+ "=" + node.getPath(), true);
		}
		
	}
	
	@Override
	public <T> NodeInfo<?> getNodeInfo(T value) {
		if (value == null) {
			// LEVEL 0.
			// We passed null as the root value. Return the folders in the root.
			// Create a cell to display a folder.
			final Cell<FolderNode> cell = new NodeCell();

			return new DefaultNodeInfo<FolderNode>(rootDataProvider, cell);
		} else if (value instanceof FolderNode) {
			// LEVEL 1+.
			// We want the children of the given folder. Return the children folders.
			FolderNode n = (FolderNode) value;
			Cell<FolderNode> cell = new NodeCell();
			
			return new DefaultNodeInfo<FolderNode>(n.getDataProvider(), cell);
		}
		return null;
	}

	@Override
	public boolean isLeaf(Object value) {
		if (value instanceof FolderNode) {
			return ((FolderNode) value).getChildren().isEmpty();
		} else {
			return false;
		}
	}



}
