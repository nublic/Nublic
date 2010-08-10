package com.scamall.resource;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

public class ValueDao extends HibernateDao<Value> {
	
	/**
	 * 
	 * @param factory
	 */
	public ValueDao(SessionFactory factory) {
		super(factory, Value.class);
	}
	
	/**
	 * 
	 * @param app
	 * @param key
	 * @param subkey
	 * @return
	 */
	public Value findByAppKeySubkey(String app, String key, String subkey) {
		KeyDao keyDao = new KeyDao(factory);
		Key keyFound = keyDao.findByKeyApp(app, key);
		long keyId = keyFound.getId();		
		
		Session session = this.factory.openSession();
		
		Criteria criteria = session.createCriteria(Key.class);
		
		criteria.add(Restrictions.eq("keyId", keyId));
		criteria.add(Restrictions.eq("subkey", subkey));

		try {
			return (Value) criteria.uniqueResult();
		} finally {
			session.close();
		}
	}
}
