package com.mmsp.model;

import java.io.Serializable;
import java.sql.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
//import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * Версия РПД
 * @author Алексей
 */

@Entity
@Table(name = "WPD_VERSION")
public class WPDVersion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "WPD_VERSION_ID")
    private Long id;
    
    @Column(name = "WPD_VERSION_DATA")
    private Date date; // дата
    
    @Column(name = "WPD_VERSION_NUMBER")
    private Long number; // номер (id HandbookDiscipline)
    
    @Column(name = "WPD_VERSION_TEMPLATE_NAME")
    private String templateName; // название шаблона или путь
    
    @Column(name = "WPD_VERSION_STUDY_LOAD")
    private Integer studyLoad; // учебная нагрузка по дисциплине по семестрам

    @ManyToOne
    @JoinColumn(name="WPD_VERSION_SUBJECT")
    private WPDData subject;
    
    @OneToOne(mappedBy = "subject")
    private ThematicPlan thematicPlan; // связь с тематическим планом

    /*@OneToOne(mappedBy = "")
    private PoCM planOfConMes; // связь с планом контр мероприятий
    */

    public WPDVersion() {
    }

    public Long getId() {
        return id;
    }
    
    public Date getDate() {
        return date;
    }

    public Long getNumber() {
        return number;
    }

    public String getTemplateName() {
        return templateName;
    }

    public Integer getStudyLoad() {
        return studyLoad;
    }

    public WPDData getSubject() {
		return subject;
	}
    
    public ThematicPlan getThematicPlan() {
		return thematicPlan;
	}

	public void setSubject(WPDData subject) {
		this.subject = subject;
	}

	public void setThematicPlan(ThematicPlan thematicPlan) {
		this.thematicPlan = thematicPlan;
	}

	public void setId(Long id) {
        this.id = id;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setStudyLoad(Integer studyLoad) {
        this.studyLoad = studyLoad;
    }
}
