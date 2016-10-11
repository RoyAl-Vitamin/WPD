package com.mmsp.model;

import java.util.Set;
import java.util.TreeSet;

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
import javax.persistence.Table;

/**
 * Класс данных одного модуля является частью Semester
 * @author rav
 */

@Entity
@Table(name = "MODULE")
public class Module implements Comparable<Module> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "MODULE_ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name="MODULE_LINK")
	private Semester semester;

	@Column(name = "MODULE_NUMBER")
	private int number; // номер модуля

	@Column(name = "MODULE_NAME", length = 256)
	private String name; // Название модуля

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "module", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private Set<Section> treeSection = new TreeSet<Section>((Section a, Section b) -> a.compareTo(b));

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Section> getTreeSection() {
		return treeSection;
	}

	public void setTreeSection(Set<Section> setSection) {
		this.treeSection = setSection;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Semester getSemester() {
		return semester;
	}

	public void setSemester(Semester semester) {
		this.semester = semester;
	}

	/**
	 * Возвращает секцию по её номеру или null, если секция не найдена
	 * @param number номер секции
	 * @return @Section or null
	 */
	public Section getSection(int number) {
		for (Section sec : treeSection)
			if (sec.getNumber() == number) return sec;
		return null;
	}

	public int getL() {
		int temp = 0;
		for (Section sec : this.getTreeSection()) {
			temp += sec.getL();
		}
		return temp;
	}

	@Override
	public int compareTo(Module o) {
		return this.getNumber() - o.getNumber();
	}
}
