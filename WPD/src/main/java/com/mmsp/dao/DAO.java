package com.mmsp.dao;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mmsp.util.HibernateUtil;

// https://docs.jboss.org/hibernate/orm/3.3/reference/en/html/queryhql.html

@SuppressWarnings("unchecked")
public interface DAO<T> {

    static final Logger log = LogManager.getLogger(DAO.class);

    default public Long add(T obj) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        // getting session object from session factory
        Session session = sessionFactory.openSession();
        // getting transaction object from session object
        session.beginTransaction();
        Long iValue = (Long) session.save(obj);
        log.debug("Inserted Successfully");
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
        log.debug("Deleted Successfully");
        session.getTransaction().commit();
        session.flush();
        session.close();
    }

    default public void update(T obj) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.saveOrUpdate(obj);
        log.debug("Save or Update Successfully");
        session.getTransaction().commit();
        session.flush();
        session.close();
    }

    default public List<T> getAll(Class<T> clazz) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        // return sessionFactory.getCurrentSession().createQuery("from
        // UserEntity").list();
        Query query = session.createQuery("from " + clazz.getName());
        List<T> objects = query.list();
        log.debug("Found ALL Successfully");
        for (T obj_out : objects) {
            System.out.println(obj_out.toString());
        }
        session.getTransaction().commit();
        session.flush();
        session.close();
        return objects;
    }

    default public T getById(Class<T> clazz, Long id) {
        T value = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        value = (T) session.get(clazz, id);
        if (value != null) {
            log.debug("Found by ID Successfully");
        } else {
            log.debug("NOT FOUND by ID");
        }
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
        for (T obj_out : objects) {
            log.debug(obj_out.toString());
        }
        session.getTransaction().commit();
        session.flush();
        session.close();
        return objects;
    }
}
