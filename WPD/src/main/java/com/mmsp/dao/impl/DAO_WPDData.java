package com.mmsp.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mmsp.dao.DAO;
import com.mmsp.model.WPDData;
import com.mmsp.util.HibernateUtil;

public class DAO_WPDData implements DAO<WPDData>{

	public DAO_WPDData() {
	}
	
	public List<WPDData> getAll() {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		//return sessionFactory.getCurrentSession().createQuery("from UserEntity").list();
		Query query = session.createQuery("from com.mmsp.model.WPDData");
		List<WPDData> objects = (List<WPDData>) query.list();
		System.out.println("Found ALL Successfully");
		for(WPDData obj_out : objects)
		{
			System.out.println(obj_out.toString());
		}
		session.getTransaction().commit();
		return objects;
	}
}
