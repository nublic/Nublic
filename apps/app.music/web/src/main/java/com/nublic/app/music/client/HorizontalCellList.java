package com.nublic.app.music.client;

import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.view.client.SelectionModel;

public class HorizontalCellList<T> extends CellList<T> {

	private Cell<T> cell;
	
	public HorizontalCellList(Cell<T> cell) {
		super(cell);
		this.cell = cell;
	}

	@Override
	protected void renderRowValues(SafeHtmlBuilder sb, List<T> values, int start,
			SelectionModel<? super T> selectionModel) {
		int length = values.size();
		int end= start + length;
		for (int i = start; i < end ; i++){
			T value = values.get(i-start);
			SafeHtmlBuilder cellBuilder = new SafeHtmlBuilder();
			Context context = new Context(i,0,getValueKey(value));
			cell.render(context, value, cellBuilder);
			sb.append(cellBuilder.toSafeHtml());
		}
	}
}
