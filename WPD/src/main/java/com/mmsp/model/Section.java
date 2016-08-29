package com.mmsp.model;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class Section {

	private int number; // Номер раздела

	private String name; // описание раздела

	private Set<ThematicPlan> treeTheme = new TreeSet<ThematicPlan>(new Comparator<ThematicPlan>() { // Множество тем
		public int compare(ThematicPlan o1, ThematicPlan o2) {
			return o1.getNumber() - o2.getNumber();
		}
	});

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

	public Set<ThematicPlan> getSetTheme() {
		return treeTheme;
	}

	public void setSetTheme(Set<ThematicPlan> setTheme) {
		this.treeTheme = setTheme;
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
}
