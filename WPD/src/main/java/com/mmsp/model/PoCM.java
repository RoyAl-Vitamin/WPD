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
    
    @OneToOne(mappedBy = "planOfConMes")
    private WPDVersion wpdVersion; // связь с дисциплиной, к которой это тематический план принаделжит
    
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
    
    public Integer getNumber() {
		return number;
	}

	public WPDVersion getWpdVersion() {
		return wpdVersion;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public void setWpdVersion(WPDVersion wpdVersion) {
		this.wpdVersion = wpdVersion;
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
