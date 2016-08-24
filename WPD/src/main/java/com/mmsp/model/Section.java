package com.mmsp.model;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class Section {

	private int number; // Номер раздела

	private String name; // описание раздела

	private Set<ThematicPlan> setTheme = new TreeSet<ThematicPlan>(new Comparator<ThematicPlan>() { // Множество тем
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
		return setTheme;
	}

	public void setSetTheme(Set<ThematicPlan> setTheme) {
		this.setTheme = setTheme;
	}
}
