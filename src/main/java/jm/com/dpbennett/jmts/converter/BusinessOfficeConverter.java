/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import jm.com.dpbennett.business.entity.BusinessOffice;

/**
 *
 * @author desbenn
 */
public class BusinessOfficeConverter extends ConverterAdapter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
       
        BusinessOffice office = BusinessOffice.findBusinessOfficeByName(getEntityManager(), value);

        if (office == null) {
            office = new BusinessOffice(value);
        }

        return office;
    }

}
