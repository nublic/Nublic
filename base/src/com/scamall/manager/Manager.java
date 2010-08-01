/**
 * 
 */
package com.scamall.manager;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.scamall.base.App;

/**
 * Implements the logic neccessary for loading apps. Using this class needs
 * special permissions for the app.
 * 
 * @author Alejandro Serrano
 */
public class Manager implements ManagerRefresh {

	/**
	 * The path where app information will be found.
	 */
	static final String APP_DESCRIPTORS_PATH = ".";

	/**
	 * Extension of app description, that is, the file that tells where the app
	 * lives, which class to load...
	 */
	static final String APP_DESCRIPTOR_EXTENSION = "xml";

	/**
	 * Extension of app policy, a Java policy-like file that tells the
	 * permissions that this app will be granted.
	 */
	static final String APP_POLICY_EXTENSION = "policy";

	/**
	 * Initial part of the complete policy file.
	 */
	static final String POLICY_FILE_START_PATH = ".";

	/**
	 * Final part of the complete policy file.
	 */
	static final String POLICY_FILE_END_PATH = ".";

	/**
	 * Place to save the complete policy file.
	 */
	static final String POLICY_FILE_PATH = ".";

	/**
	 * Saves the last date in which an app was loaded.
	 */
	private HashMap<String, Long> dates;

	/**
	 * Saves the relation between IDs and each app class.
	 */
	private HashMap<String, Class<? extends App>> classes;

	/**
	 * Saves each app singleton, with its ID.
	 */
	private HashMap<String, App> singletons;

	public Manager() {
		// Check manager permission
		// TODO: Now it is disabled to help debugging
		// ManagerPermission permission = new ManagerPermission("all");
		// AccessController.checkPermission(permission);

		// Initialize relations
		dates = new HashMap<String, Long>();
		classes = new HashMap<String, Class<? extends App>>();
		singletons = new HashMap<String, App>();

		// Initial loading
		this.refresh();
	}

	/**
	 * Gets the list of all apps loaded in the system.
	 * 
	 * @return The list of app IDs.
	 */
	public Set<String> getAppIds() {
		return Collections.unmodifiableSet(this.singletons.keySet());
	}

	/**
	 * Returns an app singleton given its ID.
	 * 
	 * @param id
	 *            The app ID.
	 * @return The app singleton.
	 * @throws IllegalAccessException
	 *             See java.lang.Class#newInstance()
	 * @throws InstantiationException
	 *             See java.lang.Class#newInstance()
	 * @throws IllegalArgumentException
	 *             When no app has the specified ID.
	 */
	public App getAppById(String id) throws InstantiationException,
			IllegalAccessException {
		if (!this.singletons.containsKey(id))
			throw new IllegalArgumentException("No app with that ID");

		App app = this.singletons.get(id);
		if (app == null) {
			// Create a new instance if none was created before
			Class<? extends App> klass = this.classes.get(id);
			app = klass.newInstance();
			this.singletons.put(id, app);
		}
		return app;
	}

	/**
	 * Return an app singleton given its type.
	 * 
	 * @param <A>
	 *            The app type.
	 * @param theClass
	 *            The app type class.
	 * @return The app singleton.
	 * @throws IllegalAccessException
	 *             See java.lang.Class#newInstance()
	 * @throws InstantiationException
	 *             See java.lang.Class#newInstance()
	 * @throws IllegalArgumentException
	 *             When no app has the specified type.
	 */
	@SuppressWarnings("unchecked")
	public <A extends App> A getApp(Class<A> theClass)
			throws InstantiationException, IllegalAccessException {
		for (String id : this.classes.keySet()) {
			Class<? extends App> klass = this.classes.get(id);
			if (theClass.equals(klass)) {
				return (A) this.getAppById(id);
			}
		}

		throw new IllegalArgumentException("No app with that type");
	}

	/**
	 * @see com.scamall.manager.ManagerRefresh#refresh()
	 */
	@Override
	public synchronized void refresh() {
		ArrayList<String> added = new ArrayList<String>();
		ArrayList<String> updated = new ArrayList<String>();
		ArrayList<String> removed = new ArrayList<String>();

		File app_descriptor_dir = new File(APP_DESCRIPTORS_PATH);
		FilenameFilter filter = new WildcardFileFilter("*."
				+ APP_DESCRIPTOR_EXTENSION);

		// Find new and updated files
		for (File descr : app_descriptor_dir.listFiles(filter)) {
			String id = FilenameUtils.getBaseName(descr.getName());

			// Check whether the file is new or updated
			if (dates.containsKey(id)) {
				long prev_date = dates.get(id);
				if (prev_date != descr.lastModified())
					updated.add(id);
			} else {
				added.add(id);
			}
		}

		// Get the files that were removed
		for (String id : dates.keySet()) {
			if (!added.contains(id) && !updated.contains(id))
				removed.add(id);
		}

		// Make changes to lists
		for (String id : removed) {
			this.removeApp(id);
		}
		for (String id : updated) {
			this.removeApp(id);
			try {
				this.addApp(id);
			} catch (Exception e) {
				// Do nothing
			}
		}
		for (String id : added) {
			try {
				this.addApp(id);
			} catch (Exception e) {
				// Do nothing
			}
		}

		createPolicyFile();
	}

	private void addApp(String id) throws ManagerException {
		String filename = FilenameUtils.concat(APP_DESCRIPTORS_PATH, id + "."
				+ APP_DESCRIPTOR_EXTENSION);
		File config_file = new File(filename);

		try {

			XMLConfiguration config = new XMLConfiguration(config_file);
			File jarfile = new File(config.getString("jarfile"));
			String klass_name = config.getString("class");

			@SuppressWarnings("deprecation")
			URLClassLoader loader = new URLClassLoader(
					new URL[] { jarfile.toURL() });
			@SuppressWarnings("unchecked")
			Class<? extends App> klass = (Class<? extends App>) loader
					.loadClass(klass_name);

			dates.put(id, config_file.lastModified());
			classes.put(id, klass);
			singletons.put(id, null);
		} catch (Exception e) {
			throw new ManagerException(e);
		}
	}

	private void removeApp(String id) {
		dates.remove(id);
		classes.remove(id);
		singletons.remove(id);
	}

	private void createPolicyFile() {

	}

	/**
	 * Example application loading apps and showing which ones has found.
	 * 
	 * @param args
	 *            Commmand line arguments
	 */
	public static void main(String[] args) {
		Manager m = new Manager();
		System.out.println("Loaded apps:");
		for (String id : m.getAppIds()) {
			System.out.print("* ");
			System.out.print(id);
			System.out.print(".. ");
			try {
				m.getAppById(id);
				System.out.println("instantiated");
			} catch (Exception e) {
				System.out.println("error");
			}
		}
	}
}
