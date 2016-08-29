package com.mmsp.model;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class Module {

	private int number; // номер модуля

	private String name; // Название модуля

	private Set<Section> treeSection = new TreeSet<Section>(new Comparator<Section>() { // Множество разделов
		public int compare(Section o1, Section o2) {
			return o1.getNumber() - o2.getNumber();
		}
	});

	/**
	 * true, если это объекты одного класса и их номера совпадают
	 */
	/*@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Module)) return false;
		if (((Module) obj).number == this.number) return true;
		return false;
	}*/

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
}