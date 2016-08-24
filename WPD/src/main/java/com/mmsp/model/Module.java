package com.mmsp.model;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class Module {

	private int number; // номер модуля

	private String name; // Название модуля

	private Set<Section> setSection = new TreeSet<Section>(new Comparator<Section>() { // Множество разделов
		public int compare(Section o1, Section o2) {
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

	public Set<Section> getSetSection() {
		return setSection;
	}

	public void setSetSection(Set<Section> setSection) {
		this.setSection = setSection;
	}
}