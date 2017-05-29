package com.mmsp.util;

import com.mmsp.logic.newtab.FXMLCtrlNewTab;

import javafx.scene.control.Tab;

/**
 * Содержит в себе вкладку, её контроллер и id wpdVersion
 * @author rav
 *
 */
public class Ctrl {

	private FXMLCtrlNewTab ctrl; // FXMLCtrl
	private Long id; // ID wpdVersion
	private Tab t; // tab
	
	public Ctrl(FXMLCtrlNewTab ctrl, Long id, Tab t) {
		this.setCtrl(ctrl);
		this.id = id;
		this.t = t;
	}
	
	public FXMLCtrlNewTab getFXMLCtrlNewTab() {
		return getCtrl();
	}

	public Long getId() {
		return id;
	}

	public void setFXMLCtrlNewTab(FXMLCtrlNewTab ctrl) {
		this.setCtrl(ctrl);
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Tab getTab() {
		return t;
	}

	public void setTab(Tab t) {
		this.t = t;
	}

	public FXMLCtrlNewTab getCtrl() {
		return ctrl;
	}

	public void setCtrl(FXMLCtrlNewTab ctrl) {
		this.ctrl = ctrl;
	}
}
