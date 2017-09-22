/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import jm.com.dpbennett.business.entity.Country;

/**
 *
 * @author desbenn
 */
@FacesConverter("countryConverter")
public class CountryConverter extends ConverterAdapter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        
        Country country = Country.findCountryByName(getEntityManager(), value);

        if (country == null) {
            country =  new Country(value);
        }

        return country;
    }

}
