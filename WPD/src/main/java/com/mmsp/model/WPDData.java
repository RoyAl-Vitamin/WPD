package com.mmsp.model;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Данные РПД
 * @author Алексей
 */

@Entity
@Table(name = "WPD_DATA")
public class WPDData implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "WPD_DATA_ID")
    private Long id;
    
    @Column(name = "WPD_DATA_FIRST_NAME")
    private String firstName; // Имя
    
    @Column(name = "WPD_DATA_LAST_NAME")
    private String lastName; // Фамилия
    
    @Column(name = "WPD_DATA_MIDDLE_NAME")
    private String middleName; // Отчество

    @OneToMany(mappedBy = "wpdData")
    private Set<WPDVersion> versions; // множество версий

    //private String tableName = "WPD_DATA";
    
    public WPDData() {
    }
    
    public Long getId() {
        return id;
    }

    public Set<WPDVersion> getTeacher() {
        return versions;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTeacher(Set<WPDVersion> versions) {
        this.versions = versions;
    }

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public Set<WPDVersion> getVersions() {
		return versions;
	}

	public void setVersions(Set<WPDVersion> versions) {
		this.versions = versions;
	}
	
	/*@Override
	public String toString() {
		return tableName;
	}*/
}
