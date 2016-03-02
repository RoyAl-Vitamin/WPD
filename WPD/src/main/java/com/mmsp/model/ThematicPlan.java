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
    
    @OneToOne(mappedBy = "thematicPlan")
    private WPDVersion wpdVersion; // связь с дисциплиной, к которой это тематический план принаделжит
    
    @Column(name = "THEMATIC_PLAN_BELONGING_TO_THE_MODULE")
    private Integer belongingToTheModule; // Принадлежность к модулю, его номер
    
    @Column(name = "THEMATIC_PLAN_BELONGING_TO_THE_SECTION")
    private Integer belongingToTheSection; // Принадлежность к разделу, его номер раздела дисциплины
    
    private String tableName = "THEMATIC_PLAN";
    
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

    public WPDVersion getWPDVerion() {
        return wpdVersion;
    }

    public Integer getBelongingToTheModule() {
        return belongingToTheModule;
    }
    
    public Integer getBelongingToTheSection() {
        return belongingToTheSection;
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

    public void setWPDVerion(WPDVersion wpdVersion) {
        this.wpdVersion = wpdVersion;
    }

    public void setBelongingToTheModule(Integer value) {
        this.belongingToTheModule = value;
    }
    
    public void setBelongingToTheSection(Integer value) {
        this.belongingToTheModule = value;
    }
    
    /*@Override
    public String toString() {
    	return tableName + " " + title;
    }*/
}
