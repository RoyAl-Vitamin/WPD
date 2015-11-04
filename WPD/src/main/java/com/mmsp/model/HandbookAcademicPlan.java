package com.mmsp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Класс справочника учебного (академического) плана
 * @author Алексей
 */

@Entity
@Table(name = "HANDBOOK_ACADEMIC_PLAN")
public class HandbookAcademicPlan {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "HANDBOOK_ACADEMIC_PLAN_ID")
    private Long id;
    
	@Column(name = "HANDBOOK_ACADEMIC_PLAN_CODE")
	private Integer code;
	
    @Column(name = "HANDBOOK_ACADEMIC_PLAN_VALUE")
    private String value;
    
    public HandbookAcademicPlan() {
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
