package com.mmsp.repository;

import java.util.List;
import com.mmsp.model.Subject;

/**
 * @author Alex
 * Creating, Retriving, Updating and Deleting
 */

public interface SubjectRepository {
	
	void addSubject(Subject subject);
	
	List getAllSubject();
	
	void removeSubject(Subject subject);
	
	void updateSubject(Subject subject);
	
	List query(SubjectSpecification specification);
	
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
