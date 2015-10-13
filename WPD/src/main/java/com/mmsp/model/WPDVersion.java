package com.mmsp.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Данные РПД
 * @author Алексей
 */

@Entity
@Table(name = "WPD_VERSION")
public class WPDVersion implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "WPD_VERSION_ID")
    private Long id;
    
    @Column(name = "WPD_VERSION_DATA")
    private Date date; // дата
    
    @Column(name = "WPD_VERSION_NUMBER")
    private Integer number; // номер
    
    @Column(name = "WPD_VERSION_TEMPLATE_NAME")
    private String templateName; // название шаблона
    
    @Column(name = "WPD_VERSION_STUDY_LOAD")
    private Integer studyLoad; // учебная нагрузка по дисциплине по семестрам

    public WPDVersion() {
    }

    public Long getId() {
        return id;
    }
    
    public Date getDate() {
        return date;
    }

    public Integer getNumber() {
        return number;
    }

    public String getTemplateName() {
        return templateName;
    }

    public Integer getStudyLoad() {
        return studyLoad;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setStudyLoad(Integer studyLoad) {
        this.studyLoad = studyLoad;
    }
}
