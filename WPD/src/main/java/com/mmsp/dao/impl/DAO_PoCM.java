package com.mmsp.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mmsp.dao.DAO;
import com.mmsp.model.PoCM;
import com.mmsp.util.HibernateUtil;

/**
 * @author Алексей
 *
 */
@SuppressWarnings("unchecked")
public class DAO_PoCM implements DAO<PoCM> {

	public DAO_PoCM() {
	}

	@Override
	public Long add(PoCM obj) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();    
		Session session = sessionFactory.openSession();  
		session.beginTransaction();  
		Long iValue = (Long) session.save(obj);
		System.out.println("Inserted PoCM Successfully");  
		session.getTransaction().commit();
		session.flush();
		session.close();
		return iValue;
	}

	@Override
	public void remove(PoCM obj) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();    
		Session session = sessionFactory.openSession();
		session.beginTransaction();   
		session.delete(obj);  
		System.out.println("Deleted PoCM Successfully");  
		session.getTransaction().commit();  
	}

	@Override
	public void update(PoCM obj) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();  
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.saveOrUpdate(obj);
		System.out.println("Save or Update PoCM Successfully");
		session.getTransaction().commit();
	}

	@Override
	public List<PoCM> getAll(PoCM obj) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();    
		Session session = sessionFactory.openSession();  
		session.beginTransaction();
		Query query = session.createQuery("from " + obj.getClass().getName());
		List<PoCM> objects = query.list();  
		for(PoCM obj_out : objects)  
		{  
			System.out.println(obj_out.toString());  
		}
		session.getTransaction().commit();    
		return objects;
	}
}
