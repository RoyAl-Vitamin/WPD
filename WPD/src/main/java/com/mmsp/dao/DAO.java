package com.mmsp.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mmsp.util.HibernateUtil;

// https://docs.jboss.org/hibernate/orm/3.3/reference/en/html/queryhql.html

@SuppressWarnings("unchecked")
public interface DAO<T> {
	
	default public Long add(T obj) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();  
		//getting session object from session factory  
		Session session = sessionFactory.openSession();  
		//getting transaction object from session object  
		session.beginTransaction();  
		Long iValue = (Long) session.save(obj);
		System.out.println("Inserted Successfully");
		session.getTransaction().commit();
		session.flush();
		session.close();
		return iValue;
	}

	default public void remove(T obj) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();  
		Session session = sessionFactory.openSession();  
		session.beginTransaction();
		// Subject subject = (Subject)session.load(Subject.class, SUBJECT_ID);
		session.delete(obj);
		System.out.println("Deleted Successfully");
		session.getTransaction().commit();
		session.flush();
		session.close();
	}
	
	default public void update(T obj) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();  
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.saveOrUpdate(obj);
		System.out.println("Save or Update Successfully");
		session.getTransaction().commit();
		session.flush();
		session.close();
	}

	default public List<T> getAll(Class<T> obj) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		//return sessionFactory.getCurrentSession().createQuery("from UserEntity").list();
		Query query = session.createQuery("from " + obj.getName());
		List<T> objects = query.list();
		System.out.println("Found ALL Successfully");
		for(T obj_out : objects)
		{
			System.out.println(obj_out.toString());
		}
		session.getTransaction().commit();
		session.flush();
		session.close();
		return objects;
	}

	default public T getById(Class<T> obj, Long id) {
		T value = null;
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		value = (T) session.get(obj, id);
		if (value != null)
			System.out.println("Found by ID Successfully");
		else
			System.err.println("NOT FOUND by ID");
		session.getTransaction().commit();
		session.flush();
		session.close();
		return value;
	}

	default public List<T> run(String value) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();    
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		Query query = session.createQuery(value);
		List<T> objects = query.list();
		for(T obj_out : objects)
		{
			System.out.println(obj_out.toString());
		}
		session.getTransaction().commit();
		session.flush();
		session.close();
		return objects;
	}
}
