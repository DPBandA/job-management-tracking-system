/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import jm.com.dpbennett.business.entity.Employee;

/**
 *
 * @author desbenn
 */
public class EmployeeConverter implements Converter {
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("JMTSPU");
        EntityManager em = entityManagerFactory.createEntityManager();
        
        Employee employee = Employee.findEmployeeByName(em, value);

        if (value == null) {
            employee = new Employee("--", "--");
        }

        return employee;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((Employee) value).getName();
    }
}
