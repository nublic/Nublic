package com.nublic.app.browser.web.client;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionModel.AbstractSelectionModel;
import com.google.gwt.view.client.TreeViewModel;

public class BrowserUi extends Composite {

	private static BrowserUiUiBinder uiBinder = GWT.create(BrowserUiUiBinder.class);
	
	@UiField(provided=true) CellTree folderTree = new CellTree(
		new TreeViewModel() {
			final AbstractDataProvider<String> dataProvider = new ListDataProvider<String>();
			final AbstractSelectionModel<String> selectionModel = new NoSelectionModel<String>();
			@Override
			public <T> NodeInfo<?> getNodeInfo(T value) {
				return new DefaultNodeInfo<String>(dataProvider, new TextCell(), selectionModel, null);
			}
			@Override
			public boolean isLeaf(Object value) {
				return true;
			}
		}, null);
	@UiField FlowPanel contentPlace;

	interface BrowserUiUiBinder extends UiBinder<Widget, BrowserUi> {
	}

	public BrowserUi() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
