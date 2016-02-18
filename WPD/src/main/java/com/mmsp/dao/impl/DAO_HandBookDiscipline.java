package com.mmsp.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mmsp.dao.DAO;
import com.mmsp.model.HandbookDiscipline;
import com.mmsp.util.HibernateUtil;

public class DAO_HandBookDiscipline implements DAO<HandbookDiscipline>{
	
	public List<HandbookDiscipline> getByValue(String value) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		//return sessionFactory.getCurrentSession().createQuery("from UserEntity").list();
		Query query = session.createQuery("from HandbookDiscipline WHERE value = \'" + value + "\'"); // HANDBOOK_DISCIPLINE_VALUE
		List<HandbookDiscipline> objects = query.list();
		System.out.println("Found ALL Successfully with size = " + objects.size());
		// <test
		for(HandbookDiscipline obj_out : objects)
		{
			System.out.println(obj_out.toString());
		}
		// test/>
		session.getTransaction().commit();
		return objects;
	}
	
	public Long getId(String sValue, Integer iCode) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		//return sessionFactory.getCurrentSession().createQuery("from UserEntity").list();
		Query query = session.createQuery("SELECT id FROM HandbookDiscipline WHERE value = \'" + sValue + "\' AND code = " + iCode.intValue());
		List<Long> objects = query.list();
		System.out.println("Found ID Successfully");
		session.getTransaction().commit();
		if (objects.size() != 0)
			return objects.get(0);
		else
			return null;
	}
	
	public HandbookDiscipline get(String sValue, Integer iCode) {
		// FIXME Узнать: Мб дисциплины с одинаковым value(названием) и code?
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		Query query = session.createQuery("FROM HandbookDiscipline WHERE value = \'" + sValue + "\' AND code = " + iCode.intValue());
		List<HandbookDiscipline> objects = query.list();
		System.out.println("Found by value and code Successfully. Size = " + objects.size());
		session.getTransaction().commit();

		HandbookDiscipline object = null;
		switch (objects.size()) {
		case 0:
			System.err.println("Объект не найден \nValue = " + sValue + "\nCode = " + iCode.intValue());
			break;
		case 1:
			object = objects.get(0);
			break;
		default:
			object = objects.get(0);
			System.err.println("Найдены похожие элементы по Value and Code");
			for (int i = 1; i < objects.size(); i++) {	
				remove(objects.get(i)); // удалим все, кроме нулевого
			}
		}
		
		/*if (objects.size() > 1) { // Удалим похожие
			object = objects.get(0);
			System.err.println("Найдены похожие элементы по Value and Id");
			for (int i = 1; i < objects.size(); i++) {	
				remove(objects.get(i)); // удалим все, кроме нулевого
			}
		} else
			if (objects.size() == 1) object = objects.get(0);
		*/
		return object;
	}
}
