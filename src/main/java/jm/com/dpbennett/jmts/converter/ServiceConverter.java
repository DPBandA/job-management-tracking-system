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
import jm.com.dpbennett.business.entity.Service;

/**
 *
 * @author desbenn
 */
public class ServiceConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("JMTSPU");
        EntityManager em = entityManagerFactory.createEntityManager();

        Service service = Service.findServiceByName(em, value);

        if (service == null) {
            service =  new Service(value);
        }

        return service;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((Service) value).getName();
    }

}
