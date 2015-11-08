package com.mmsp.dao.impl;

import java.sql.SQLException;
import java.util.Collection;
import org.hibernate.Session;
import com.mmsp.logic.Logic;
import com.mmsp.dao.SubjectDAO;
import com.mmsp.model.Subject;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class SubjectDAOImpl implements SubjectDAO {

	@Override
	public void addSubject(Subject subject) throws SQLException {
		Session session = null;
		try {
			session = Logic.getSessionFactory().openSession();
			session.beginTransaction();
			session.save(subject);
			session.getTransaction().commit();
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Ошибка");
			alert.setHeaderText("К сожалению, ...");
			alert.setContentText("...возникла ошибка при сохранени");
			alert.showAndWait();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	@Override
	public void updateSubject(Long subject_id, Subject subject) throws SQLException {
		// TODO Auto-generated method stub		
	}

	@Override
	public Subject getSubjectById(Long subject_id) throws SQLException {
		Session session = null;
		Subject subject = null;
		try {
			session = Logic.getSessionFactory().openSession();
			subject = (Subject) session.load(Subject.class, subject_id);
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Ошибка");
			alert.setHeaderText("К сожалению, ...");
			alert.setContentText("...возникла ошибка при выборке");
			alert.showAndWait();
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	    return subject;
	}

	@Override
	public Collection getAllSubject() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteSubject(Subject subject) throws SQLException {
		// TODO Auto-generated method stub		
	}

}
