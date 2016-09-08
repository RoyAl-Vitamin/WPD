package com.mmsp.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

/**
 * класс одной записи в таблице 7.1
 * @author rav
 */

@Entity
@Table(name = "RECORD")
public class Record {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "RECORD_ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name="RECORD_LINK")
	private Semester semester;

	@Column(name = "RECORD_COURSE_TITLE")
	private String courseTitle; // название дисциплины

	//@IndexColumn
	@OrderColumn(name = "RECORD_ARRAY_WEEK")
	private String[] arrWeek; // распределение часов/плюсов по неделям

	@Column(name = "RECORD_POS")
	private int pos; // позиция строки в таблице (нужна, что бы не промахнуться с удалением)

	public Record(int size, int currPos) {
		arrWeek = new String[size];
		pos = currPos;
	}

	public Record() {
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
