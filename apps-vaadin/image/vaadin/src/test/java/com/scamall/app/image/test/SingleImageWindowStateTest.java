/**
 * 
 */
package com.scamall.app.image.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.scamall.app.image.Image;
import com.scamall.app.image.SingleImageWindowState;
import com.vaadin.data.util.BeanItemContainer;

/**
 * @author david
 * 
 */
public class SingleImageWindowStateTest {

	private SingleImageWindowState state;
	private String pathToTest;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		state = new SingleImageWindowState();
		pathToTest = System.getProperty("user.dir")+
							"/src/test/resources/test_nublic";
		state.setListId(pathToTest);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.scamall.app.image.SingleImageWindowState#getURL()}.
	 * 
	 * @throws IOException
	 */
	@Test
	public final void testGetURL() throws IOException {
		assertEquals("Navigator URL Improperly formed", state.getURL(), 
					"view" + pathToTest + "/0");
	}
	

	/**
	 * Test method for
	 * {@link com.scamall.app.image.SingleImageWindowState#getListImages}.
	 */
	@Test
	public final void testGetListImagesProperlyFormed() {
		BeanItemContainer<Image> listImages = state.getListImages();
		assertNotNull("ListImages should be correctly initialized with the test elements", listImages);
		assertFalse("ListImages should not be empty", listImages.size() == 0);
		assertEquals("ListImages should have exactly this number of images" + listImages.size(),
					(long)listImages.size(), (long)5);
	}
	
}
