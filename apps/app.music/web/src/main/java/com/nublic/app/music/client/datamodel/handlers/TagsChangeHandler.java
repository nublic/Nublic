package com.nublic.app.music.client.datamodel.handlers;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.nublic.app.music.client.datamodel.Tag;

public interface TagsChangeHandler extends EventHandler {
	public void onTagsChange();
	
//	public class TagsChangeEvent {
//		TagsChangeEventType type;
//		List<Tag> involvedSet;
//		
//		public TagsChangeEvent(TagsChangeEventType type, List<Tag> involvedSet) {
//			this.type = type;
//			this.involvedSet = involvedSet;
//		}
//
//		public TagsChangeEventType getType() { return type; }
//		public List<Tag> getInvolvedSet() { return involvedSet; }
//	}
//	
//	public enum TagsChangeEventType {
//		TAGS_ADDED,
//		TAGS_REMOVED,
//	}
}