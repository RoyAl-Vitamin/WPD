package com.mmsp.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * Класс в котором содержится название предмета
 * @author Алексей
 */

@Entity
@Table(name = "SUBJECT")
public class Subject implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SUBJECT_ID")
    private Long id;
    
    @Column(name = "SUBJECT_FIRST_NAME")
    private String firstName; // Имя
    
    @Column(name = "SUBJECT_LAST_NAME")
    private String lastName; // Фамилия
    
    @Column(name = "SUBJECT_MIDDLE_NAME")
    private String middleName; // Отчество
        
    @Column(name = "SUBJECT_NAME", length = 128)
    private String name; // название предмета
    
    @OneToMany(mappedBy = "subject")
    private Set<WPDVersion> versions; // множество версий

    public Subject() {
    }
    
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public Set<WPDVersion> getTeacher() {
        return versions;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setTeacher(Set<WPDVersion> versions) {
        this.versions = versions;
    }
}
