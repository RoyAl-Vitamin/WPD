package com.mmsp.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mmsp.dao.DAO;
import com.mmsp.model.HandbookDiscipline;
import com.mmsp.model.WPDData;
import com.mmsp.util.HibernateUtil;

public class DAO_HandBookDiscipline implements DAO<HandbookDiscipline>{
	
	public List<HandbookDiscipline> getByValue(String value) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		//return sessionFactory.getCurrentSession().createQuery("from UserEntity").list();
		Query query = session.createQuery("from HandbookDiscipline WHERE value = \'" + value + "\'"); // HANDBOOK_DISCIPLINE_VALUE
		List<HandbookDiscipline> objects = query.list();
		System.out.println("Found ALL Successfully");
		// <test
		for(HandbookDiscipline obj_out : objects)
		{
			System.out.println(obj_out.toString());
		}
		// test/>
		session.getTransaction().commit();
		return objects;
	}
}
