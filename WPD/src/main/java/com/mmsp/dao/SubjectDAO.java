package com.mmsp.dao;

import java.util.List;
import com.mmsp.model.Subject;

/**
 * @author Alex
 * Creating, Retriving, Updating and Deleting
 */

public interface SubjectDAO {
	
	void addSubject(Subject subject);
	
	List getAllSubject();
	
	void removeSubject(Subject subject);
	
	void updateSubject(Subject subject);
	
	/*
	 * public List<Book> findAll() {
	        List<Book> books = (List<Book>) getCurrentSession().createQuery("from Book").list();
	        return books;
	    }
	    public void deleteAll() {
	        List<Book> entityList = findAll();
	        for (Book entity : entityList) {
	            delete(entity);
	        }
	    }
	*/
}
