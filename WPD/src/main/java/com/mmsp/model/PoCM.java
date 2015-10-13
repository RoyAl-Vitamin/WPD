package com.mmsp.model;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * План контрольных мероприятий
 * @author Алексей
 */
@Entity
@Table(name = "POCM")
public class PoCM implements Serializable { // Plan of Control Measures
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "POCM_ID")
    private Long id;

    @Column(name = "POCM_LABORIOUSNESS")
    private Integer laboriousness;// трудоемкость
    
    /*@ElementCollection
    @Column(name = "POCM_TYPE")
    private Set<String> type; // тип контрольного мероприятия
    /*
        Экзамен
        Зачёт
        Курсовой проект
        Курсовая работа
    */
    
    @Column(name = "POCM_TYPE")
    private Integer type; // тип контрольного мероприятия
    /*
        Будут формироваться как степень 2-ки
    */
    
    /*
    вот это не описано, наверно, стоит задать это как множество?
        привязка к учебным неделям и семестрам;
    */
    
    public PoCM() {
    }

    public Long getId() {
        return id;
    }

    public Integer getLaboriousness() {
        return laboriousness;
    }

    public Integer getType() {
        return type;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public void setLaboriousness(Integer laboriousness) {
        this.laboriousness = laboriousness;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
