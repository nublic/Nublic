package com.nublic.app.browser.web.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.TreeViewModel;

public class BrowserTreeViewModel implements TreeViewModel {
	BrowserModel model;
	
	ListDataProvider<FolderNode> rootDataProvider;
	//final AbstractSelectionModel<String> selectionModel = new NoSelectionModel<String>();

	public BrowserTreeViewModel() {
		model = null;
		rootDataProvider = new ListDataProvider<FolderNode>();
	}
	
		
	public void setModel(BrowserModel model) {
		// To provide the data in the tree we will use the model which is updated asynchronously
		this.model = model;
		
		model.getFolderTree().setDataProvider(rootDataProvider);
	}

	/**
	 * The cell used to render Nodes.
	 */
	private static class NodeCell extends AbstractCell<FolderNode> {
		@Override
		public void render(Context context, FolderNode value, SafeHtmlBuilder sb) {
			if (value != null) {
				sb.appendEscaped(value.getContent().getName());
			}
		}
		
//		@Override
//        public void onBrowserEvent(Context context, Element parent, Node value, NativeEvent event, ValueUpdater<Node> valueUpdater) {
//			if (value != null) {
//				super.onBrowserEvent(context, parent, value, event, valueUpdater);
//				if (event.getButton() == NativeEvent.BUTTON_LEFT) {
//					
//				}
//
//			}
//		}
		
	}
	
	@Override
	public <T> NodeInfo<?> getNodeInfo(T value) {
		if (value == null) {
			// LEVEL 0.
			// We passed null as the root value. Return the folders in the root.
			
			// Create a cell to display a folder.
			final Cell<FolderNode> cell = new NodeCell();
//			DefaultNodeInfo<Node> nodeInfo = new DefaultNodeInfo<Node>(rootDataProvider, cell);
//			nodeInfo.getSelectionModel().addSelectionChangeHandler(new Handler() {
//				@Override
//				public void onSelectionChange(SelectionChangeEvent event) {
//					event.getSource();
//					
//				}
//			});
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
//		return false;
	}



}
