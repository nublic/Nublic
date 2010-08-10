/**
 * 
 */
package com.scamall.resource;


import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

/**
 * @author david
 *
 */
public class KeyDao extends HibernateDao<Key> {

	public KeyDao(SessionFactory factory) {
		super(factory, Key.class);
	}
	
	public Key findByKeyApp(String app, String key) {
		Session session = factory.openSession();
		Criteria criteria = session.createCriteria(Key.class);
		
		criteria.add(Restrictions.eq("appId", app));
		criteria.add(Restrictions.eq("keyId", key));
		
		try {
			return (Key) criteria.uniqueResult();
		} finally {
			session.close();
		}
	}
}
