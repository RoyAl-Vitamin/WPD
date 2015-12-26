package com.mmsp.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mmsp.dao.SubjectDAO;
import com.mmsp.model.Subject;
import com.mmsp.util.HibernateUtil;

/**
 * @author Алексей
 *
 */
public class SubjectDAOImpl implements SubjectDAO {

	/**
	 * 
	 */
	public SubjectDAOImpl() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.mmsp.repository.SubjectRepository#addSubject(com.mmsp.model.Subject)
	 */
	public void addSubject(Subject subject) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();  
		//getting session object from session factory  
		Session session = sessionFactory.openSession();  
		//getting transaction object from session object  
		session.beginTransaction();  
		session.save(subject);
		System.out.println("Inserted Successfully");  
		session.getTransaction().commit();
		session.flush();
		session.close();  
	}

	/* (non-Javadoc)
	 * @see com.mmsp.repository.SubjectRepository#removeSubject(com.mmsp.model.Subject)
	 */
	public void removeSubject(Subject subject) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();  
		//getting session object from session factory  
		Session session = sessionFactory.openSession();  
		//getting transaction object from session object  
		session.beginTransaction();  
		// Subject subject = (Subject)session.load(Subject.class, SUBJECT_ID); 
		session.delete(subject);  
		System.out.println("Deleted Successfully");  
		session.getTransaction().commit();  
	}

	/* (non-Javadoc)
	 * @see com.mmsp.repository.SubjectRepository#updateSubject(com.mmsp.model.Subject)
	 */
	public void updateSubject(Subject subject) {
		// TODO Auto-generated method stub

	}

	public List<Subject> getAllSubject() {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory();  
		//getting session object from session factory  
		Session session = sessionFactory.openSession();  
		//getting transaction object from session object  
		session.beginTransaction();
		//return sessionFactory.getCurrentSession().createQuery("from UserEntity").list();
		Query query = session.createQuery("from Subject");  
		List<Subject> subjects = query.list();  
		for(Subject Subject : subjects)  
		{  
			System.out.println("Subject Name: " + Subject.getName() + ", Teacher Second name: " + Subject.getLastName() + ", Teacher First name: " + Subject.getFirstName() + ", Teacher Middle name: " + Subject.getMiddleName());  
		}
		System.out.println("List Subject is empty? " + subjects.isEmpty());
		session.getTransaction().commit();  
		//sessionFactory.close();  
		return subjects;
	}

}
