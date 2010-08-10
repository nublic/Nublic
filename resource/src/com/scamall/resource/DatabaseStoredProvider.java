package com.scamall.resource;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

public abstract class DatabaseStoredProvider extends Provider {

	HibernateDao<Key> keyDao;
	ValueDao valueDao;
	
	/** Initialize the required configurations for working with the database
	 * 
	 */
	public DatabaseStoredProvider() {
		Configuration configuration = new AnnotationConfiguration().configure();

		SessionFactory factory = configuration.buildSessionFactory();

		//this.keyDao = new HibernateDao<Key>(factory, Key.class);
		this.valueDao = new ValueDao(factory);
	}
	
	@Override
	public String value(String app, String key, String subkey) {
		Value value = valueDao.findByAppKeySubkey(app, key, subkey);
		return value.getValue();
	}

	void databaseRemoveValues(String key, String[] args) {
		// TODO
	}

}
