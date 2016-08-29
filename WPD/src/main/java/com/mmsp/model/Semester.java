package com.mmsp.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Класс данных одного семестра
 * @author rav
 */
public class Semester implements Comparable<Semester> {

	private int NUMBER_OF_SEMESTER; // номер модуля

	private int QUANTITY_OF_MODULE; // количество модулей
	
	private int QUANTITY_OF_SECTION; // количество разделов

	private int QUANTITY_OF_WEEK; // количество недель

	private List<Record> rowT71; // сами записи в таблице 7.1, как { имя предмета и распределение часов или плюсов по неделям}

	private Set<Module> treeModule = new TreeSet<Module>(new Comparator<Module>() { // Дерево модулей для отображения в ttvRoot
		public int compare(Module o1, Module o2) {
			return o1.getNumber() - o2.getNumber();
		}
	});

	public Semester() {
		rowT71 = new ArrayList<Record>();
		/*NUMBER_OF_SEMESTER = 0;
		QUANTITY_OF_MODULE = 0;
		QUANTITY_OF_SECTION = 0;
		QUANTITY_OF_WEEK = 0;*/
	}

	public int getNUMBER_OF_SEMESTER() {
		return NUMBER_OF_SEMESTER;
	}

	public void setNUMBER_OF_SEMESTER(int nUMBER_OF_SEMESTER) {
		NUMBER_OF_SEMESTER = nUMBER_OF_SEMESTER;
	}

	public int getQUANTITY_OF_MODULE() {
		return QUANTITY_OF_MODULE;
	}

	public void setQUANTITY_OF_MODULE(int qUANTITY_OF_MODULE) {
		QUANTITY_OF_MODULE = qUANTITY_OF_MODULE;
	}

	public int getQUANTITY_OF_SECTION() {
		return QUANTITY_OF_SECTION;
	}

	public void setQUANTITY_OF_SECTION(int qUANTITY_OF_SECTION) {
		QUANTITY_OF_SECTION = qUANTITY_OF_SECTION;
	}

	public int getQUANTITY_OF_WEEK() {
		return QUANTITY_OF_WEEK;
	}

	public void setQUANTITY_OF_WEEK(int qUANTITY_OF_WEEK) {
		QUANTITY_OF_WEEK = qUANTITY_OF_WEEK;
	}

	public List<Record> getRowT71() {
		return rowT71;
	}

	public void setRowT71(List<Record> rowT71) {
		this.rowT71 = rowT71;
	}

	public Set<Module> getTreeModule() {
		return treeModule;
	}

	public void setTreeModule(Set<Module> treeModule) {
		this.treeModule = treeModule;
	}

	/**
	 * Возвращает модуль по его номеру или null, если модуль не найден
	 * @param number номер модуля
	 * @return @Module or null
	 */
	public Module getModule(int number) {
		for (Module mod : treeModule)
			if (mod.getNumber() == number) return mod;
		return null;
	}

	public Record getRecord(int pos) {
		for (Record rec : rowT71) {
			if (rec.getPos() == pos) return rec;
		}
		return null;
	}

	@Override
	public int compareTo(Semester o) {
		return this.NUMBER_OF_SEMESTER - o.NUMBER_OF_SEMESTER;
	}
}