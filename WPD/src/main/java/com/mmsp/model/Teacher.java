package com.mmsp.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * Класс преподавателя
 * @author Алексей
 */

@Entity
@Table(name = "TEACHER")
public class Teacher implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "TEACHER_ID")
    private Long id;
    
    @Column(name = "TEACHER_FIRST_NAME", length = 32)
    private String firstName; // имя
    
    @Column(name = "TEACHER_LAST_NAME", length = 32)
    private String lastName; // фамилия

    /*
     * возможно стоит сделать просто Set<String> subject; , а не связь ManyToMany
     * сделал так, потому что не нашёл как маппить Set
     * 
     * UPD: нашёл - @ElementCollection
     */
    
    @ManyToMany // Здесь будет ошибка при сборке
    @JoinTable(name = "TEACHER_SUBJECT", 
            joinColumns = {@JoinColumn(name = "TEACHER_ID")}, 
            inverseJoinColumns = {@JoinColumn(name = "SUBJECT_ID")})
    private Set<Subject> subjects = new HashSet<Subject>(); // монжество предметов, которые преподаватель преподаёт

    public Teacher() {
    }

    public Long getId() {
        return id;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    
    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setId(Long id) {
        this.id = id;
    }
            
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }
}
