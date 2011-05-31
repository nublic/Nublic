package com.scamall.app.image;

/**
 * 
 * @author David Navarro Estruch
 * 
 */
public interface WindowState {

	/**
	 * Generate the appropriate URL for the given State
	 * 
	 * It should save the most important features of the State but is not
	 * required to save minor details of the state. For example, is appropriate
	 * to represent the query of a search but not the selected items of a
	 * multiple selection widget.
	 * 
	 * @return The URL
	 */
	public String getURL();


}
