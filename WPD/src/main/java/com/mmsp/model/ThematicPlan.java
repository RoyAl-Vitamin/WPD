package com.mmsp.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * Тематический план дисциплины
 * @author Алексей
 */

@Entity
@Table(name = "THEMATIC_PLAN")
public class ThematicPlan implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "THEMATIC_PLAN_ID")
    private Long id;
    
    @Column(name = "THEMATIC_PLAN_TITLE", length = 32)
    private String title; // название темы
    
    @Column(name = "THEMATIC_PLAN_DESCRIPTION")
    private String description; // описание темы
    
    @Column(name = "THEMATIC_PLAN_LABORIOUSNESS")
    private Integer laboriousness; // трудоемкость темы по всем видам нагрузки; Вроде как измеряется в ЗЕ (зачётных единицах)
    
    @OneToOne
    @PrimaryKeyJoinColumn
    private Subject subject; // связь с дисциплиной, к которой это тематический план принаделжит
    
    @Column(name = "THEMATIC_PLAN_NUMBER")
    private Integer number; // Принадлежность к модулю
    
    public ThematicPlan() {
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getLaboriousness() {
        return laboriousness;
    }

    public Subject getSubject() {
        return subject;
    }

    public Integer getNumber() {
        return number;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLaboriousness(Integer laboriousness) {
        this.laboriousness = laboriousness;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
