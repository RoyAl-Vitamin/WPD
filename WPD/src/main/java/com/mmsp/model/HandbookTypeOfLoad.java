package com.mmsp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Класс справочника вида нагрузки
 * @author Алексей
 */

@Entity
@Table(name = "HANDBOOK_TYPE_OF_LOAD")
public class HandbookTypeOfLoad {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "HANDBOOK_TYPE_OF_LOAD_ID")
    private Long id;
    
	@Column(name = "HANDBOOK_TYPE_OF_LOAD_CODE")
	private Integer code;
	
    @Column(name = "HANDBOOK_TYPE_OF_LOAD_VALUE")
    private String value;

	public HandbookTypeOfLoad() {
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
