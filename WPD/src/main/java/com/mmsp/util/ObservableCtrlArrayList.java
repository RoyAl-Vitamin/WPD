package com.mmsp.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;

public class ObservableCtrlArrayList implements ObservableList<Ctrl> {

	private final ObservableList<Ctrl> instance = FXCollections.observableArrayList();

	@Override
	public boolean add(Ctrl e) {
		return instance.add(e);
	}

	@Override
	public void add(int index, Ctrl element) {
		instance.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends Ctrl> c) {
		return instance.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Ctrl> c) {
		return instance.addAll(index, c);
	}

	@Override
	public void clear() {
		instance.clear();
	}

	@Override
	public boolean contains(Object o) {
		return instance.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return instance.containsAll(c);
	}

	@Override
	public Ctrl get(int index) {
		return instance.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return instance.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return instance.isEmpty();
	}

	@Override
	public Iterator<Ctrl> iterator() {
		return instance.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return instance.lastIndexOf(o);
	}

	@Override
	public ListIterator<Ctrl> listIterator() {
		return instance.listIterator();
	}

	@Override
	public ListIterator<Ctrl> listIterator(int index) {
		return instance.listIterator();
	}

	@Override
	public boolean remove(Object o) {
		return instance.remove(o);
	}

	@Override
	public Ctrl remove(int index) {
		return instance.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return instance.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return instance.retainAll(c);
	}

	@Override
	public Ctrl set(int index, Ctrl element) {
		return instance.set(index, element);
	}

	@Override
	public int size() {
		return instance.size();
	}

	@Override
	public List<Ctrl> subList(int fromIndex, int toIndex) {
		return instance.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return instance.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return instance.toArray(a);
	}

	@Override
	public void addListener(InvalidationListener listener) {
		instance.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		instance.removeListener(listener);
	}

	@Override
	public boolean addAll(Ctrl... elements) {
		return instance.addAll(elements);
	}

	@Override
	public void addListener(ListChangeListener<? super Ctrl> listener) {
		instance.addListener(listener);
	}

	@Override
	public void remove(int from, int to) {
		instance.remove(from, to);
	}

	@Override
	public boolean removeAll(Ctrl... elements) {
		return instance.removeAll(elements);
	}

	@Override
	public void removeListener(ListChangeListener<? super Ctrl> listener) {
		instance.removeListener(listener);
	}

	@Override
	public boolean retainAll(Ctrl... elements) {
		return instance.retainAll(elements);
	}

	@Override
	public boolean setAll(Ctrl... elements) {
		return instance.setAll(elements);
	}

	@Override
	public boolean setAll(Collection<? extends Ctrl> col) {
		return instance.setAll(col);
	}

	/**
	 * Находит по ID нужный Ctrl из olCtrl
	 * @param id_Vers ID дисциплины
	 * @return найденный Ctrl
	 */
	public Ctrl getCtrlById(Long id_Vers) {
		for (Ctrl c: instance) {
			if (c.getId().equals(id_Vers)) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * Находит Tab по id wpdVersion
	 * @param id of {@link com.mmsp.model.WPDVersion}
	 * @return {@link Tab}
	 */
	public Tab getTabById(Long id) {
		Tab tab = null;
		for (Ctrl c: instance) {
			if (c.getId().equals(id)) {
				tab = c.getTab();
				break;
			}
		}
		return tab;
	}
	
	/**
	 * Находится ли данная вкладка в списке
	 * @param id of {@link com.mmsp.model.WPDVersion}
	 * @return true если находится, иначе false
	 */
	public boolean tabIsOpen(Long id) {
		for (Ctrl c: instance) {
			if (c.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Выдаёт вкладку по её названию, первую найденную
	 * @param oldTabName
	 * @return {@link com.mmsp.util.Ctrl}
	 */
	public Ctrl getCtrlByTabText(String oldTabName) {
		Ctrl t = null;
		for (Ctrl ctrlValue : instance) {
			if (ctrlValue.getTab().getText().equals(oldTabName)) {
				t = ctrlValue;
				break;
			}
		}
		return t;
	}
}
