package com.mmsp.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mmsp.dao.DAO;
import com.mmsp.model.WPDVersion;
import com.mmsp.util.HibernateUtil;
import com.mmsp.wpd.WPD;

@SuppressWarnings("unchecked")
public class DAO_WPDVersion implements DAO<WPDVersion> {

	// provide on request
	@Override
	public List<WPDVersion> run(String value) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();    
		Session session = sessionFactory.openSession();  
		session.beginTransaction();
		Query query = session.createQuery(value);
		List<WPDVersion> objects = query.list();  
		for(WPDVersion obj_out : objects)  
		{  
			System.out.println(obj_out.toString());  
		}
		session.getTransaction().commit();    
		return objects;
	}
	
	public Long getIdByNumber(Long lValue) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		//return sessionFactory.getCurrentSession().createQuery("from UserEntity").list();
		//System.err.println("SELECT id FROM WPDVersion WHERE number = \'" + iValue.toString() + "\'");
		Query query = session.createQuery("SELECT id FROM WPDVersion WHERE number = " + lValue.toString());
		List<Long> objects = query.list();
		System.out.println("Found ID Successfully");
		session.getTransaction().commit();
		if (objects.size() != 0)
			return objects.get(0);
		else
			return null;
	}
	
	// Список всех версий, которые ссылаются на дисциплину с ID = lValue
	public List<WPDVersion> getAllByNumber(Long lValue) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		//return sessionFactory.getCurrentSession().createQuery("from UserEntity").list();
		Query query = session.createQuery("from WPDVersion WHERE number = " + lValue.toString());
		List<WPDVersion> objects = query.list();
		session.getTransaction().commit();
		return objects;
	}

	public Long getIdByName(String value) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		//System.err.println("SELECT id FROM WPDVersion WHERE number = \'" + iValue.toString() + "\'");
		Query query = session.createQuery("SELECT id FROM WPDVersion WHERE name = \'" + value + "\'");
		List<Long> objects = query.list();
		System.out.println("Found ID Successfully");
		session.getTransaction().commit();
		if (objects.size() != 0)
			return objects.get(0);
		else
			return null;
	}
}
