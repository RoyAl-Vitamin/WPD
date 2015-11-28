package com.mmsp.repository;

import java.util.List;
import com.mmsp.model.Subject;

/**
 * @author Алексей
 *
 */

public interface SubjectRepository {
	
	void addSubject(Subject subject);
	
	void removeSubject(Subject subject);
	
	void updateSubject(Subject subject);
	
	List query(SubjectSpecification specification);
	
}
