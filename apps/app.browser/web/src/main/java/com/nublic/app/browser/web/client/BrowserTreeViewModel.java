package com.nublic.app.browser.web.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.TreeViewModel;

public class BrowserTreeViewModel implements TreeViewModel {
	BrowserModel model;
	
	ListDataProvider<Node> rootDataProvider;
	//final AbstractSelectionModel<String> selectionModel = new NoSelectionModel<String>();

	public BrowserTreeViewModel() {
		model = null;
		rootDataProvider = new ListDataProvider<Node>();
	}
	
		
	public void setModel(BrowserModel model) {
		// To provide the data in the tree we will use the model which is updated asynchronously
		this.model = model;
		
		model.getFolderTree().setDataProvider(rootDataProvider);
	}

	/**
	 * The cell used to render Nodes.
	 */
	private static class NodeCell extends AbstractCell<Node> {
		@Override
		public void render(Context context, Node value, SafeHtmlBuilder sb) {
			if (value != null) {
				sb.appendEscaped(value.getContent().getName());
			}
		}
	}
	
	@Override
	public <T> NodeInfo<?> getNodeInfo(T value) {
		if (value == null) {
			// LEVEL 0.
			// We passed null as the root value. Return the folders in the root.
			
			// Create a cell to display a folder.
			final Cell<Node> cell = new NodeCell();
			return new DefaultNodeInfo<Node>(rootDataProvider, cell);

		} else if (value instanceof Node) {
			// LEVEL 1.
			// We want the children of the given folder. Return the children folders.
			Node n = (Node) value;
			Cell<Node> cell = new NodeCell();
			
			return new DefaultNodeInfo<Node>(n.getDataProvider(), cell);
		}
		return null;
	}

	@Override
	public boolean isLeaf(Object value) {
		if (value instanceof Node) {
			return ((Node) value).getChildren().isEmpty();
		} else {
			return false;
		}
//		return false;
	}



}
