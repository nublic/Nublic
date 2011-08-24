/**
 * 
 */
package com.scamall.app.widget.mediaplayer;

import java.util.Collection;

import com.vaadin.data.util.BeanItemContainer;

/**
 * 
 * 
 * @author Alejandro Serrano
 */
public class ClipContainer extends BeanItemContainer<Clip> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4031362629708033634L;
	
	public ClipContainer() {
		super(Clip.class);
	}

	public ClipContainer(Collection<Clip> collection) {
		super(Clip.class, collection);
	}
}
