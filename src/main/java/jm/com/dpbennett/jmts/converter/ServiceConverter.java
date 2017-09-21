/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import jm.com.dpbennett.business.entity.Service;

/**
 *
 * @author desbenn
 */
public class ServiceConverter extends ConverterAdapter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        
        Service service = Service.findServiceByName(getEntityManager(), value);

        if (service == null) {
            service =  new Service(value);
        }

        return service;
    }

}
