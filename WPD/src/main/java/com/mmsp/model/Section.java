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
 * Класс данных одной секции является частью Module
 * @author rav
 */

@Entity
@Table(name = "SECTION")
public class Section implements Comparable<Section> {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "SECTION_ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name="SECTION_LINK")
	private Module module; // Связь с модулем

	@Column(name = "SECTION_NUMBER")
	private int number; // Номер раздела

	@Column(name = "SECTION_NAME")
	private String name; // описание раздела

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "section", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
	private Set<ThematicPlan> treeTheme = new TreeSet<ThematicPlan>((ThematicPlan a, ThematicPlan b) -> a.compareTo(b));

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

	public Set<ThematicPlan> getTreeTheme() {
		return treeTheme;
	}

	public void setTreeTheme(Set<ThematicPlan> setTheme) {
		this.treeTheme = setTheme;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}

	/**
	 * Возвращает тему по её номеру или null, если тема не найдена
	 * @param number номер темы
	 * @return @ThematicPlan or null
	 */
	public ThematicPlan getTheme(int number) {
		for (ThematicPlan theme : treeTheme)
			if (theme.getNumber().equals(Integer.valueOf(number))) return theme;
		return null;
	}

	/**
	 * @return возвращает Л вложенных тематических планов
	 */
	public int getL() {
		int temp = 0;
		for (ThematicPlan theme : this.getTreeTheme()) {
			if (theme.getL() != null)
				temp += theme.getL();
		}
		return temp;
	}

	/**
	 * @return возвращает ПЗ вложенных тематических планов
	 */
	public int getPZ() {
		int temp = 0;
		for (ThematicPlan theme : this.getTreeTheme()) {
			if (theme.getPZ() != null)
				temp += theme.getPZ();
		}
		return temp;
	}

	/**
	 * @return возвращает ЛР вложенных тематических планов
	 */
	public int getLR() {
		int temp = 0;
		for (ThematicPlan theme : this.getTreeTheme()) {
			if (theme.getLR() != null)
				temp += theme.getLR();
		}
		return temp;
	}

	/**
	 * @return возвращает КСР вложенных тематических планов
	 */
	public int getKSR() {
		int temp = 0;
		for (ThematicPlan theme : this.getTreeTheme()) {
			if (theme.getKSR() != null)
				temp += theme.getKSR();
		}
		return temp;
	}

	/**
	 * @return возвращает СРС вложенных тематических планов
	 */
	public int getSRS() {
		int temp = 0;
		for (ThematicPlan theme : this.getTreeTheme()) {
			if (theme.getSRS() != null)
				temp += theme.getSRS();
		}
		return temp;
	}

	@Override
	public int compareTo(Section o) {
		return this.getNumber() - o.getNumber();
	}
}
