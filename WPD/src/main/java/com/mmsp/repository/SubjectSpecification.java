package com.mmsp.repository;

import com.mmsp.model.Subject;
/**
 * @author Алексей
 *
 */
public interface SubjectSpecification {
	
	boolean specified(Subject account);
	
}
