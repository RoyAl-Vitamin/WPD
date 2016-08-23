package com.mmsp.logic;

/**
 * класс одной записи в таблице 7.1
 * @author rav
 */
class Record {

	private String courseTitle; // название дисциплины

	private String[] arrWeek; // распределение часов/плюсов по неделям

	private int pos; // позиция строки в таблице (нужна, что бы не промахнуться с удалением)

	Record(int size, int currPos) {
		arrWeek = new String[size];
		pos = currPos;
	}

	public String getCourseTitle() {
		return courseTitle;
	}

	public void setCourseTitle(String courseTitle) {
		this.courseTitle = courseTitle;
	}

	public String[] getArrWeek() {
		return arrWeek;
	}

	public void setArrWeek(String[] arrWeek) {
		this.arrWeek = arrWeek;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	/**
	 * пересоздание массива недель, при указании размера меньшего, чем было, лишние элементы будут утеряны
	 * @param size размер нового массива
	 */
	public void setSizeArrWeek(int size) {
		if (arrWeek != null) {
			arrWeek = new String[size];
			return;
		}

		String[] temp = new String[size];

		if (arrWeek != null) { 
			int minSize = size < arrWeek.length ? size : arrWeek.length;
			for (int i = 0; i < minSize; i++)
				temp[i] = arrWeek[i];
			for (int i = minSize; i < temp.length; i++)
				temp[i] = "";
		}
		arrWeek = temp;
	}
}
