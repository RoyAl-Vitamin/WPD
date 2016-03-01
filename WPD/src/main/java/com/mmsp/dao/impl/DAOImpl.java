package com.mmsp.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.mmsp.util.HibernateUtil;

/**
 * @author Алексей
 *
 */
public class DAOImpl {

	public DAOImpl() {
	}

	/* (non-Javadoc)
	 * @see com.mmsp.repository.SubjectRepository#addSubject(com.mmsp.model.Subject)
	 */
	public <T> void add(T obj) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();  
		//getting session object from session factory  
		Session session = sessionFactory.openSession();  
		//getting transaction object from session object  
		session.beginTransaction();  
		session.save(obj);
		System.out.println("Inserted Successfully");  
		session.getTransaction().commit();
		session.flush();
		session.close();  
	}

	/* (non-Javadoc)
	 * @see com.mmsp.repository.SubjectRepository#removeSubject(com.mmsp.model.Subject)
	 */
	public <T> void remove(T obj) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();  
		//getting session object from session factory  
		Session session = sessionFactory.openSession();  
		//getting transaction object from session object  
		session.beginTransaction();  
		// Subject subject = (Subject)session.load(Subject.class, SUBJECT_ID); 
		session.delete(obj);  
		System.out.println("Deleted Successfully");  
		session.getTransaction().commit();  
	}

	/* (non-Javadoc)
	 * @see com.mmsp.repository.SubjectRepository#updateSubject(com.mmsp.model.Subject)
	 */
	public <T> void update(T obj) {
		throw new NullPointerException("Method not Resolved");
	}

	public <T> List<T> getAll(T obj) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();  
		//getting session object from session factory  
		Session session = sessionFactory.openSession();  
		//getting transaction object from session object  
		session.beginTransaction();
		//return sessionFactory.getCurrentSession().createQuery("from UserEntity").list();
		Query query = session.createQuery("from " + obj.getClass().getName());
		List<T> objects = query.list();  
		for(T obj_out : objects)  
		{  
			System.out.println(obj_out.toString());  
		}
		//System.out.println("List Subject is empty? " + objects.isEmpty());
		session.getTransaction().commit();  
		//sessionFactory.close();  
		return objects;
	}

}
