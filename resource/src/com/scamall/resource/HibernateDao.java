/**
 * 
 */
package com.scamall.resource;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * @author david
 *
 */
public class HibernateDao<T> {

	protected Class<T> persistentClass;

	protected SessionFactory factory;

	public HibernateDao(SessionFactory factory, Class<T> persistentClass) {
		this.factory = factory;
		this.persistentClass = persistentClass;
	}

	public T findById(Integer id) {
		Session session = this.factory.openSession();
		try {
			T object = (T) session.get(persistentClass, id);
			return object;
		} finally {
			session.close();
		}
	}
	
	public void saveOrUpdate(Object object) {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.saveOrUpdate(object);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			throw e;
		} finally {
			session.close();
		}
	}
	
	public void delete(Object object) {
		Session session = factory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.delete(object);
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			throw e;
		} finally {
			session.close();
		}
	}
	
	public List<T> findAll() {
		Session session = factory.openSession();
		try {
			Criteria criteria = session.createCriteria(persistentClass);
			List<T> objects = (List<T>) criteria.list();
			return objects;
		} finally {
			session.close();
		}
	}
}
