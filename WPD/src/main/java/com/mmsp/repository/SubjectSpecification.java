package com.mmsp.repository;

import com.mmsp.model.Subject;
/**
 * @author Alex
 *
 */
public interface SubjectSpecification {
	
	boolean specified(Subject account);
	
}
