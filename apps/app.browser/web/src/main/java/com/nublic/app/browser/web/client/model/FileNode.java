package com.nublic.app.browser.web.client.model;

import java.util.Comparator;

import com.nublic.app.browser.web.client.Constants;
import com.nublic.util.comparators.CompoundComparator;
import com.nublic.util.comparators.InverseComparator;

public class FileNode {
	String name;
	String mime;
	String view;
	double size;
	double lastUpdate;
	boolean writable;
	String importantLink = null;

	// Static Comparators
	@SuppressWarnings("unchecked")
	public static final Comparator<FileNode> NAME_COMPARATOR =
			CompoundComparator.<FileNode>create(new SimpleFolderComparator(),
												new SimpleNameComparator());
	public static final Comparator<FileNode> INVERSE_NAME_COMPARATOR =
			new CompoundComparator<FileNode>(new SimpleFolderComparator(),
											 new InverseComparator<FileNode>(new SimpleNameComparator()));
	
	@SuppressWarnings("unchecked")
	public static final Comparator<FileNode> TYPE_COMPARATOR =
			CompoundComparator.<FileNode>create(new SimpleFolderComparator(),
												new SimpleViewComparator(),
												new SimpleTypeComparator(),
												new SimpleNameComparator());
	@SuppressWarnings("unchecked")
	public static final Comparator<FileNode> INVERSE_TYPE_COMPARATOR =
			CompoundComparator.<FileNode>create(new SimpleFolderComparator(),
					new InverseComparator<FileNode>(new SimpleViewComparator()),
					new InverseComparator<FileNode>(new SimpleTypeComparator()),
					new InverseComparator<FileNode>(new SimpleNameComparator()));
	
	@SuppressWarnings("unchecked")
	public static final Comparator<FileNode> DATE_COMPARATOR =
			CompoundComparator.<FileNode>create(new SimpleFolderComparator(),
												new SimpleDateComparator(),
												new SimpleNameComparator());
	@SuppressWarnings("unchecked")
	public static final Comparator<FileNode> INVERSE_DATE_COMPARATOR =
			CompoundComparator.<FileNode>create(new SimpleFolderComparator(),
												new InverseComparator<FileNode>(new SimpleDateComparator()),
												new SimpleNameComparator());
	
	@SuppressWarnings("unchecked")
	public static final Comparator<FileNode> SIZE_COMPARATOR =
			CompoundComparator.<FileNode>create(new SimpleFolderComparator(),
												new SimpleSizeComparator());
	@SuppressWarnings("unchecked")
	public static final Comparator<FileNode> INVERSE_SIZE_COMPARATOR =
			CompoundComparator.<FileNode>create(new SimpleFolderComparator(),
												new InverseComparator<FileNode>(new SimpleSizeComparator()));

	// Constructors
	public FileNode() {
		name = null;
		mime = null;
		view = null;
		size = 0;
		lastUpdate = 0;
	}

	public FileNode(String name, String mime, String view, double size, double lastUpdate, boolean writable) {
		this.name = name;
		this.mime = mime;
		this.view = view;
		this.size = size;
		this.lastUpdate = lastUpdate;
		this.writable = writable;
	}
	
	// Comparators
	private static class SimpleNameComparator implements Comparator<FileNode> {
		@Override
		public int compare(FileNode o1, FileNode o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());
		}
	}
	
	private static class SimpleFolderComparator implements Comparator<FileNode> {
		@Override
		public int compare(FileNode o1, FileNode o2) {
			if (o1.getMime().equals(o2.getMime())) {
				return 0;
			} else if (o1.getMime().equals(Constants.FOLDER_MIME)) {
				return -1;
			} else if (o2.getMime().equals(Constants.FOLDER_MIME)){
				return 1;
			} else {
				return 0;
			}
		}
	}
	
	private static class SimpleViewComparator implements Comparator<FileNode> {
		@Override
		public int compare(FileNode o1, FileNode o2) {
			String view1 = o1.getView() == null ? "" : o1.getView();
			String view2 = o2.getView() == null ? "" : o2.getView();
			return view1.compareToIgnoreCase(view2);
		}
	}
	
	private static class SimpleTypeComparator implements Comparator<FileNode> {
		@Override
		public int compare(FileNode o1, FileNode o2) {
			return o1.getMime().compareToIgnoreCase(o2.getMime());
		}
	}

	private static class SimpleDateComparator implements Comparator<FileNode> {
		@Override
		public int compare(FileNode o1, FileNode o2) {
			return (int) (o1.getLastUpdate() - o2.getLastUpdate());
		}
	}
	
	private static class SimpleSizeComparator implements Comparator<FileNode> {
		@Override
		public int compare(FileNode o1, FileNode o2) {
			return (int) (o1.getSize() - o2.getSize());
		}
	}


	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getMime() {
		return mime;
	}
	
	public void setMime(String mime) {
		this.mime = mime;
	}
	
	public String getView() {
		return view;
	}
	
	public void setView(String view) {
		this.view = view;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public double getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(double lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public boolean isWritable() {
		return writable;
	}

	public String getImportantLink() {
		return importantLink;
	}

	public void setImportantLink(String importantLink) {
		this.importantLink = importantLink;
	}
}
