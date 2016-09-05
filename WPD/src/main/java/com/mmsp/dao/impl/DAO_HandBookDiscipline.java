package com.mmsp.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mmsp.dao.DAO;
import com.mmsp.model.HandbookDiscipline;
import com.mmsp.util.HibernateUtil;

@SuppressWarnings("unchecked")
public class DAO_HandBookDiscipline implements DAO<HandbookDiscipline>{

	@Override
	public Long add(HandbookDiscipline hbdValue) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		Long iValue = (Long) session.save(hbdValue);
		System.out.println("Inserted HANDBOOK Successfully with ID = " + iValue.toString());
		session.getTransaction().commit();
		session.flush();
		session.close();
		return iValue;
	}

	public void update(HandbookDiscipline hbdValue) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();  
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		session.saveOrUpdate(hbdValue);
		System.out.println("Save or Update Successfully HandbookDiscipline");
		session.getTransaction().commit();
		session.flush();
		session.close();
	}
	
	public List<HandbookDiscipline> getByValue(String value) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		//return sessionFactory.getCurrentSession().createQuery("from UserEntity").list();
		Query query = session.createQuery("from HandbookDiscipline WHERE value = \'" + value + "\'");
		List<HandbookDiscipline> objects = query.list();
		System.out.println("Found ALL HandBook Discipline Successfully with size = " + objects.size());
		session.getTransaction().commit();
		session.flush();
		session.close();
		return objects;
	}
	
	public Long getIdByValueAndCode(String sValue, String sCode) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		Query query = session.createQuery("SELECT id FROM HandbookDiscipline WHERE value = \'" + sValue + "\' AND code = \'" + sCode + "\'");
		List<Long> objects = query.list();
		System.out.println("Found handbook discipline ID Successfully");
		session.getTransaction().commit();
		session.flush();
		session.close();
		if (objects.size() != 0)
			return objects.get(0);
		else
			return null;
	}
	
	public HandbookDiscipline getByValueAndCode(String sValue, String sCode) {
		// FIXME Узнать: Мб дисциплины с одинаковым value(названием) и code?
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		Query query = session.createQuery("FROM HandbookDiscipline WHERE value = \'" + sValue + "\' AND code = \'" + sCode + "\'");
		List<HandbookDiscipline> objects = query.list();
		System.out.println("Found by value and code Successfully. Size = " + objects.size());
		session.getTransaction().commit();
		session.flush();
		session.close();

		HandbookDiscipline object = null;
		switch (objects.size()) {
		case 0:
			System.err.println("Объект не найден \nValue = " + sValue + "\nCode = " + sCode);
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
		return object;
	}

}
