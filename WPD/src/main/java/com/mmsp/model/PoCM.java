package com.mmsp.model;

import java.io.Serializable;
import javax.persistence.Column;
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

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "POCM_ID")
    private Long id;

    /*
    @Column(name = "POCM_LABORIOUSNESS")
    private Integer laboriousness; // трудоемкость
    */
    
    @Column(name = "POCM_NUMBER_OF_SEMESTER")
    private Integer number; // номер семестра

    //Будут формироваться как степень 2-ки
    @Column(name = "POCM_TYPE")
    private Integer type; // тип контрольного мероприятия
    
    public PoCM() {
    }

    public Long getId() {
        return id;
    }

    /*public Integer getLaboriousness() {
        return laboriousness;
    }*/

    public Integer getType() {
        return type;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    /*public void setLaboriousness(Integer laboriousness) {
        this.laboriousness = laboriousness;
    }*/

    public void setType(Integer type) {
        this.type = type;
    }
}
