package com.mmsp.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mmsp.dao.DAO;
import com.mmsp.model.WPDVersion;
import com.mmsp.util.HibernateUtil;

public class DAO_WPDVersion implements DAO<WPDVersion> {

	// provide on request
	@Override
	public List<WPDVersion> get(String value) {
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
}
