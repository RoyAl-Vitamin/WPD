package com.mmsp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
//import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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

	@Column(name = "WPD_VERSION_DATE")
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date date; // дата

	@Column(name = "WPD_VERSION_NUMBER")
	private Long number; // номер (id HandbookDiscipline)

	@Column(name = "WPD_VERSION_TEMPLATE_NAME")
	private String templateName; // путь до шаблона

	@Column(name = "WPD_VERSION_NAME")
	private String name; // имя версии

	@Column(name = "WPD_VERSION_STUDY_LOAD")
	private Integer studyLoad; // учебная нагрузка по дисциплине по семестрам

	@ManyToOne//(cascade = CascadeType.REFRESH)
	@JoinColumn(name="WPD_VERSION_DATA")
	private WPDData wpdData;

	//@OneToOne//(cascade = CascadeType.REMOVE)
	//@PrimaryKeyJoinColumn
	//private ThematicPlan thematicPlan; // связь с тематическим планом
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "wpdVersion") //, cascade = CascadeType.REMOVE)
	private Set<ThematicPlan> thematicPlans = new HashSet<>(); // множество версий

	@OneToOne//(cascade = CascadeType.REMOVE)
	@PrimaryKeyJoinColumn
	private PoCM planOfConMes; // связь с планом контр мероприятий

	@ManyToOne//(cascade = CascadeType.REFRESH)
	@JoinColumn(name="HANDBOOK_DISCIPLINE_ID")
	private HandbookDiscipline hbD;

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

	public WPDData getWPDData() {
		return wpdData;
	}

	public PoCM getPoCM() {
		return planOfConMes;
	}

	public String getName() {
		return name;
	}

	public HandbookDiscipline getHbD() {
		return hbD;
	}

	public WPDData getWpdData() {
		return wpdData;
	}

	public void setWpdData(WPDData wpdData) {
		this.wpdData = wpdData;
	}

	public Set<ThematicPlan> getThematicPlans() {
		return thematicPlans;
	}

	public void setThematicPlans(Set<ThematicPlan> thematicPlans) {
		this.thematicPlans = thematicPlans;
	}

	public PoCM getPlanOfConMes() {
		return planOfConMes;
	}

	public void setPlanOfConMes(PoCM planOfConMes) {
		this.planOfConMes = planOfConMes;
	}

	public void setHbD(HandbookDiscipline hbD) {
		this.hbD = hbD;
	}

	public void setWPDData(WPDData wpdData) {
		this.wpdData = wpdData;
	}

	public void setPoCM(PoCM pocm) {
		this.planOfConMes = pocm;
	}

	public void setName(String name) {
		this.name = name;
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

	@Override
	public String toString() {
		String s = "WPDVers: " + this.getClass().getName() + "@" + this.hashCode() +
		"\nID == " + this.getId().toString() +
		"\nname == " + this.getName() +
		"\ntemplate name == " + this.getTemplateName() +
		"\ndate == " + this.getDate().toString() +
		"\nHANDBOOKDISCIPLINE == " + this.getHbD().toString() +
		"\nPoCM == " + this.getPoCM().getClass().getName() + "@" + this.getPoCM().hashCode();
		for (ThematicPlan t : this.getThematicPlans()) {
			s += "\nThematicPlan == " + t.getClass().getName() + "@" + this.getThematicPlans().hashCode();
		}
		return s;
	}
}
