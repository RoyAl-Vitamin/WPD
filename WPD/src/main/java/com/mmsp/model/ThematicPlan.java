package com.mmsp.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
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

	@Column(name = "THEMATIC_PLAN_NUMBER")
	private Integer number; // номер темы

	@Column(name = "THEMATIC_PLAN_L")
	private Integer L; // Л

	@Column(name = "THEMATIC_PLAN_PZ")
	private Integer PZ; // ПЗ

	@Column(name = "THEMATIC_PLAN_LR")
	private Integer LR; // ЛР

	@Column(name = "THEMATIC_PLAN_KSR")
	private Integer KSR; // КСР

	@Column(name = "THEMATIC_PLAN_SRS")
	private Integer SRS; // СРС

	//@OneToOne(mappedBy = "thematicPlan")
	//private WPDVersion wpdVersion; // связь с дисциплиной, к которой это тематический план принаделжит

	@ManyToOne
	@JoinColumn(name="THEMATIC_PLAN_VERSION")
	private WPDVersion wpdVersion;

	@Column(name = "THEMATIC_PLAN_BELONGING_TO_THE_SEMESTER")
	private Integer belongingToTheSemester; // Принадлежность к семестру

	@Column(name = "THEMATIC_PLAN_BELONGING_TO_THE_MODULE")
	private Integer belongingToTheModule; // Принадлежность к модулю

	@Column(name = "THEMATIC_PLAN_BELONGING_TO_THE_SECTION")
	private Integer belongingToTheSection; // Принадлежность к разделу

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

	public WPDVersion getWPDVerion() {
		return wpdVersion;
	}

	public Integer getBelongingToTheModule() {
		return belongingToTheModule;
	}

	public Integer getBelongingToTheSection() {
		return belongingToTheSection;
	}

	public Integer getL() {
		return L;
	}

	public void setL(Integer l) {
		L = l;
	}

	public Integer getPZ() {
		return PZ;
	}

	public void setPZ(Integer pZ) {
		PZ = pZ;
	}

	public Integer getLR() {
		return LR;
	}

	public void setLR(Integer lR) {
		LR = lR;
	}

	public Integer getKSR() {
		return KSR;
	}

	public void setKSR(Integer kSR) {
		KSR = kSR;
	}

	public Integer getSRS() {
		return SRS;
	}

	public void setSRS(Integer sRS) {
		SRS = sRS;
	}

	public Integer getBelongingToTheSemester() {
		return belongingToTheSemester;
	}

	public void setBelongingToTheSemester(Integer belongingToTheSemester) {
		this.belongingToTheSemester = belongingToTheSemester;
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

	public void setWPDVerion(WPDVersion wpdVersion) {
		this.wpdVersion = wpdVersion;
	}

	public void setBelongingToTheModule(Integer value) {
		this.belongingToTheModule = value;
	}

	public void setBelongingToTheSection(Integer value) {
		this.belongingToTheSection = value;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	@Override
	public String toString() {
		return "\nThematicPlan: " + this.getClass().getName() + "@" + this.hashCode() +
				"\nID == " + (this.getId() != null ? this.getId().toString() : "null") +
				"\nWPDVersion == " + this.getWPDVerion().getClass().getName() + "@" + this.getWPDVerion().hashCode() +
				"\ntitle == " + this.getTitle() +
				"\ndescription == " + this.getDescription() +
				"\nbelonging To The Semester == " + this.getBelongingToTheSemester() +
				"\nbelonging To The Module == " + this.getBelongingToTheModule() +
				"\nbelonging To The Section == " + this.getBelongingToTheSection() +
				"\nL == " + this.getL() +
				"\nPZ == " + this.getPZ() +
				"\nLR == " + this.getLR() +
				"\nKSR == " + this.getKSR() +
				"\nSRS == " + this.getSRS() + "\n";
		}
}
