package com.nublic.app.browser.web.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionModel.AbstractSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

public class BrowserTreeViewModel implements TreeViewModel {
	//final AbstractDataProvider<String> dataProvider = new ListDataProvider<String>();
	final AbstractSelectionModel<String> selectionModel = new NoSelectionModel<String>();
	BrowserModel model = null;


//	public BrowserTreeViewModel(BrowserModel model) {
//		// To provide the data in the tree we will use the model which is updated asynchronously
//		this.model = model;
//	}
	public BrowserTreeViewModel() {
	}
	
	public void setModel(BrowserModel model) {
		// To provide the data in the tree we will use the model which is updated asynchronously
		this.model = model;
	}
	
	@Override
	public <T> NodeInfo<?> getNodeInfo(T value) {
		if (value == null) {
			// LEVEL 0.
			// We passed null as the root value. Return the folders in the root.

			// Create a data provider that contains the list of folders.
			ListDataProvider<Node> dataProvider = null;
			if (model != null) {
				dataProvider = new ListDataProvider<Node>(model.getFolderTree().getChildren());
			} else {
				dataProvider = new ListDataProvider<Node>();
			}

			// Create a cell to display a folder.
			Cell<Node> cell = new AbstractCell<Node>() {
				@Override
				public void render(Context context, Node value, SafeHtmlBuilder sb) {
					if (value != null) {
						sb.appendEscaped(value.getContent().getName());
					}
				}
			};

			return new DefaultNodeInfo<Node>(dataProvider, cell);

		} else if (value instanceof Node) {
			// LEVEL 1.
			// We want the children of the given folder. Return the children folders.
			ListDataProvider<Node> dataProvider =
					new ListDataProvider<Node>(((Node) value).getChildren());
			
			Cell<Node> cell = new AbstractCell<Node>() {
				@Override
				public void render(Context context, Node value, SafeHtmlBuilder sb) {
					if (value != null) {
						sb.appendEscaped(value.getContent().getName());
					}
				}
			};
			return new DefaultNodeInfo<Node>(dataProvider, cell);
		}
		return null;
		
		//return new DefaultNodeInfo<String>(dataProvider, new TextCell(), selectionModel, null);
	}

	@Override
	public boolean isLeaf(Object value) {
		if (value instanceof Node) {
			if (((Node) value).getChildren() == null) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
