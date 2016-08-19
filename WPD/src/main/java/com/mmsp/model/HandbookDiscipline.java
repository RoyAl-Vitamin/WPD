package com.mmsp.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Класс справочника дисциплины
 * @author Алексей
 */

// http://docs.jboss.org/hibernate/stable/annotations/reference/en/html_single/#entity-hibspec-cascade

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
    
    @OneToMany(mappedBy = "hbD", fetch = FetchType.EAGER, cascade = CascadeType.ALL) //, cascade = CascadeType.REMOVE)
    private Set<WPDVersion> versions = new HashSet<WPDVersion>(); // множество версий

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

    public Set<WPDVersion> getVersions() {
		return versions;
	}

	public void setVersions(Set<WPDVersion> versions) {
		this.versions = versions;
	}
	
	public void addVersions(WPDVersion wpdVers) {
		this.versions.add(wpdVers);
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

    /**
     * remove WPDVersion
     * @param wpdVers - версия, которую удаляют из HandBookDiscipline
     */
	public void remVersion(WPDVersion wpdVers) {
		if (!versions.remove(wpdVers)) System.err.println("Не была удалена WPDVerion, т.к. данный элемент не содержался в коллекции");
	}

	@Override
	public String toString() {
		String s = "HandbookDiscipline: " + this.getClass().getName() + "@" + this.hashCode() +
				"\nID == " + this.getId().toString() +
				"\ncode == " + this.getCode() +
				"\nvalue == " + this.getValue();
		for (WPDVersion t : this.getVersions()) {
			s += "\nWPDVersion == " + t.getClass().getName() + "@" + this.getVersions().hashCode();
		}
		return s;
	}
}
