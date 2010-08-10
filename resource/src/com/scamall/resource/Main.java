/**
 * 
 */
package com.scamall.resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

/**
 * @author david
 *
 */
public class Main {

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String order;
		String appName;
		String resourceName;
		
		if (args.length < 2) {
			return; // Incorrect call format @TODO Return a value?
		}
		
		order = args[0];
		appName = args[1];
		
		// Prepare Hibernate
		Configuration configuration = new AnnotationConfiguration().configure();
		SessionFactory factory = configuration.buildSessionFactory();
		HibernateDao<App> dao = new HibernateDao<App>(factory, App.class);
		
		App appLoaded = dao.findById(3);
		String name = appLoaded.getAppId();
		int id = appLoaded.getId();
		
	}
}
