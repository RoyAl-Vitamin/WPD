/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mmsp.logic;

import org.hibernate.SessionFactory;

/**
 * Логика приложения
 * @author Алексей
 */
public class Logic {

    private static SessionFactory sessionFactory = null;
    
    /**
     * Подгрузка конфигурационного файла
     * Создание сессии
     */
    public void initialization() {
        sessionFactory =  com.mmsp.util.HibernateUtil.getSessionFactory();
    }
    
    
}
