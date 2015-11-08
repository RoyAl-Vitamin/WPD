/**
 * 
 */
package com.mmsp.dao;

import com.mmsp.model.Subject;
import java.util.Collection;
import java.sql.SQLException;

/**
 * @author Алексей
 *
 */
public interface SubjectDAO {
	
	public void addSubject(Subject subject) throws SQLException;
	public void updateSubject(Long subject_id, Subject subject) throws SQLException;
	public Subject getSubjectById(Long subject_id) throws SQLException;
	public Collection getAllSubject() throws SQLException;
	public void deleteSubject(Subject subject) throws SQLException;
}
