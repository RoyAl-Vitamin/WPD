package com.mmsp.model;

import java.io.Serializable;
import java.util.HashSet;
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

	@Column(name = "WPD_DATA_FIRST_NAME", length = 256)
	private String firstName; // Имя

	@Column(name = "WPD_DATA_LAST_NAME", length = 256)
	private String lastName; // Фамилия

	@Column(name = "WPD_DATA_MIDDLE_NAME", length = 256)
	private String middleName; // Отчество

	@OneToMany(mappedBy = "wpdData") //, cascade = CascadeType.REMOVE)
	private Set<WPDVersion> versions = new HashSet<WPDVersion>(); // множество версий

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

	@Override
	public String toString() {
		return "\nWPDData: " + this.getClass().getName() + "@" + this.hashCode() +
				"\nID == " + this.id +
				"\nfirstName == " + this.firstName +
				"\nmiddleName == " + this.middleName +
				"\nlastName == " + this.lastName +
				"\nversion ==" + this.versions.toString();
	}
}
