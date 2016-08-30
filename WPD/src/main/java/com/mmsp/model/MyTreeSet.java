package com.mmsp.model;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * свой TreeSet с компаратором и методами
 * @author rav
 */
public class MyTreeSet extends TreeSet<Semester> implements Comparator<Semester> {

	private static final long serialVersionUID = -8219796252512673673L;

	/**
	 * Возвращает семестр по его номеру или null, если семестр не найден
	 * @param number номер семестра
	 * @return @Semester or null
	 */
	public Semester getSemester(int number) {
		for (Semester sem : this) {
			if (sem.getNUMBER_OF_SEMESTER() == number) return sem;
		}
		return null;
	}

	/*
	 * Пусть сразу с компаратором будет
	 */
	@Override
	public int compare(Semester o1, Semester o2) {
		return o1.getNUMBER_OF_SEMESTER() - o2.getNUMBER_OF_SEMESTER();
	}

	@Override
	public String toString() {
		String s = "";
		// спустимся по дереву
		System.err.println();
		for (Semester sem : this) {
			System.err.println(" + ");
			System.err.println(" ++> " + "Семестр № " + sem.getNUMBER_OF_SEMESTER() + " количество недель в нём == " + sem.getQUANTITY_OF_WEEK());
			for (Module mod : sem.getTreeModule()) {
				System.err.println(" + ");
				System.err.println(" +++> " + "Модуль № " + mod.getNumber() + " и его название " + mod.getName());
				for (Section sec : mod.getTreeSection()) {
					System.err.println(" + ");
					System.err.println(" ++++> " + "Секция № " + sec.getNumber() + " и её название " + sec.getName());
					for (ThematicPlan theme : sec.getTreeTheme()) {
						System.err.println(" + ");
						System.err.println(" +++++> " + " Тема № " + theme.getNumber() + " и её название " + theme.getTitle());
					}
				}
			}
		}
		System.err.println();
		return s;
	}
}
