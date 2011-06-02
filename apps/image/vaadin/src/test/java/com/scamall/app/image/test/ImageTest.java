/**
 * 
 */
package com.scamall.app.image.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.scamall.app.image.Image;

/**
 * @author david
 * 
 */
public class ImageTest {

	private static String pathToCache = System.getProperty("user.dir")
			+ "/src/test/resources/test_nublic/.cache";
	private static String pathToBackup = System.getProperty("user.dir")
			+ "/src/test/resources/test_nublic/.old_cache";

	private static String pathToTestFolder = System.getProperty("user.dir")
			+ "/src/test/resources/test_nublic";

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Save the .cache folder to restore after.
		File cacheFolder = new File(pathToCache);
		cacheFolder.renameTo(new File(pathToBackup));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		// Restore the .cache folder saved before.
		
		// We use FileUtils to delete recursively all the content in the
		// directory. delete method does not allow this functionality
		FileUtils.deleteDirectory(new File(pathToCache));
		
		File cacheFolder = new File(pathToBackup);
		cacheFolder.renameTo(new File(pathToCache));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.scamall.app.image.Image#resizeImage()}.
	 * 
	 */
	@Test
	public final void testResizeImage() throws IOException {
		Image image = new Image(new File(pathToTestFolder
				+ "/schrodinger_gato_vivo.jpg"));
		File normal = image.getResourceNormalSize();
		assertTrue("Check .cache directory exists", new File(pathToTestFolder
				+ "/.cache").exists());
		assertTrue("Check .cache/size directory exists", new File(
				pathToTestFolder + "/.cache/800x500").exists());
		assertEquals("Expected same path", pathToTestFolder
				+ "/.cache/800x500/schrodinger_gato_vivo.jpg", normal.getPath());
		// TODO It has not been tested that the image is the correct size.

		File thumbnail = image.getResourceThumbnail();
		assertTrue("Check .cache directory exists", new File(pathToTestFolder
				+ "/.cache").exists());
		assertTrue("Check .cache/size directory exists", new File(
				pathToTestFolder + "/.cache/800x500").exists());
		assertEquals("Expected same path", pathToTestFolder
				+ "/.cache/800x500/schrodinger_gato_vivo.jpg", thumbnail.getPath());
		// TODO It has not been tested that the image is the correct size.
		
		
	}

}
