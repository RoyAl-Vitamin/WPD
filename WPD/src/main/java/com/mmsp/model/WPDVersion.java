package com.mmsp.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
//import javax.persistence.CascadeType;
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

	private static final long serialVersionUID = 7149382190381953724L;

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

	@Column(name = "WPD_VERSION_NAME", length = 256)
	private String name; // имя версии

	@Column(name = "WPD_VERSION_STUDY_LOAD")
	private Integer studyLoad; // учебная нагрузка по дисциплине по семестрам

	@ManyToOne//(cascade = CascadeType.REFRESH)
	@JoinColumn(name="WPD_VERSION_DATA")
	private WPDData wpdData;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "wpdVersion", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private Set<Semester> semesters = new TreeSet<Semester>((Semester a, Semester b) -> a.compareTo(b)); // множество семестров

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

	public Set<Semester> getTreeSemesters() {
		return semesters;
	}

	public void setTreeSemesters(Set<Semester> thematicPlans) {
		this.semesters = thematicPlans;
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
				"\nHANDBOOKDISCIPLINE == " + this.getHbD().toString();
		if (this.getPoCM() != null)
				s += "\nPoCM == " + this.getPoCM().getClass().getName() + "@" + this.getPoCM().hashCode();
		else s+= "\n PoCM == null";
		// спустимся по дереву
		if (this.getTreeSemesters().size() != 0)
		for (Semester sem : this.getTreeSemesters()) {
			s += "\nSemester == " + sem.getClass().getName() + "@" + this.getTreeSemesters().hashCode();
			s += "\n + ";
			s += "\n ++> " + "Семестр № " + sem.getNUMBER_OF_SEMESTER() + " количество недель в нём == " + sem.getQUANTITY_OF_WEEK();
			for (Module mod : sem.getTreeModule()) {
				s += "\n + ";
				s += "\n +++> " + "Модуль № " + mod.getNumber() + " и его название " + mod.getName();
				for (Section sec : mod.getTreeSection()) {
					s += "\n + ";
					s += "\n ++++> " + "Секция № " + sec.getNumber() + " и её название " + sec.getName();
					for (ThematicPlan theme : sec.getTreeTheme()) {
						s += "\n + ";
						s += "\n +++++> " + " Тема № " + theme.getNumber() + " и её название " + theme.getTitle();
					}
				}
			}
		}
		else s += "\nSemester count == 0\n";
		return s;
	}

	public Set<ThematicPlan> getThematicPlans() {
		Set<ThematicPlan> setTheme = new TreeSet<ThematicPlan>();
		for (Semester sem : this.getTreeSemesters())
			for (Module mod : sem.getTreeModule())
				for (Section sec : mod.getTreeSection())
					setTheme.addAll(sec.getTreeTheme());
		return setTheme;
	}

	/**
	 * Вывод через запятую семестров доступных для данной @WPDVersion
	 */
	public String getStringSemester() {
		String s = "";
		for (Semester sem : semesters) {
			s += "," + sem.getNUMBER_OF_SEMESTER();
		}
		if (s.length() != 0) {
			return s.substring(1);
		}
		return null;
	}

	/**
	 * Возвращает семестр по его номеру или null, если семестр не найден
	 * @param number номер семестра
	 * @return @Semester or null
	 */
	public Semester getSemester(int number) {
		for (Semester sem : semesters) {
			if (sem.getNUMBER_OF_SEMESTER() == number) return sem;
		}
		return null;
	}
}
