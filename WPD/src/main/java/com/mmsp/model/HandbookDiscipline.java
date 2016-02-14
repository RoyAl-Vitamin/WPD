package com.mmsp.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Класс справочника дисциплины
 * @author Алексей
 */

@Entity
@Table(name = "HANDBOOK_DISCIPLINE")
public class HandbookDiscipline implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "HANDBOOK_DISCIPLINE_ID")
    private Long id;
    
	@Column(name = "HANDBOOK_DISCIPLINE_CODE")
	private Integer code;
	
    @Column(name = "HANDBOOK_DISCIPLINE_VALUE")
    private String value;

    public HandbookDiscipline() {
    	value = "";
    	code = 0;
    }

    public Long getId() {
        return id;
    }

    public Integer getCode() {
		return code;
	}

	public String getValue() {
        return value;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
	public void setCode(Integer code) {
		this.code = code;
	}

    public void setValue(String value) {
        this.value = value;
    }
}
