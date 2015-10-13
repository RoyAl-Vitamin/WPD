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
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SUBJECT_ID")
    private Long id;
        
    @Column(name = "SUBJECT_NAME", length = 64)
    private String name; // название предмета
    
    @ManyToMany(mappedBy = "subjects")
    private Set<Teacher> teacher = new HashSet<Teacher>(); // множество преподавателей, которые обучают этому предмету
    
    /*
     * Если я правильно понимаю, то тематический план дисциплины существует у каждой дисциплины, т.е. OneToOne
     */
    @OneToOne
    @PrimaryKeyJoinColumn
    private ThematicPlan thematicPlan; // связь с тематическим планом

    public Subject() {
    }
    
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }

    public Set<Teacher> getTeacher() {
        return teacher;
    }
    
    public ThematicPlan getThematicPlan() {
        return thematicPlan;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setTeacher(Set<Teacher> teacher) {
        this.teacher = teacher;
    }

    public void setThematicPlan(ThematicPlan thematicPlan) {
        this.thematicPlan = thematicPlan;
    }
}
