/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import jm.com.dpbennett.business.entity.Sector;

/**
 *
 * @author desbenn
 */
@FacesConverter("sectorConverter")
public class SectorConverter extends ConverterAdapter {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
       
        Sector sector = Sector.findSectorByName(getEntityManager(), value);

        if (sector == null) {
            sector = new Sector(value);
        }

        return sector;
    }
}
