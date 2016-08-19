package com.mmsp.logic;

import java.util.List;

/**
 * Класс данных одного семестра
 * @author rav
 */
class Semester {

	private int NUMBER_OF_SEMESTER; // номер модуля

	private int QUANTITY_OF_MODULE; // количество модулей
	
	private int QUANTITY_OF_SECTION; // количество разделов

	private int QUANTITY_OF_WEEK; // количество недель

	private List<Record> rowT71; // сами записи в таблице 7.1, как { имя предмета и распределение часов или плюсов по неделям}

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

}