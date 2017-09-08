/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import jm.com.dpbennett.business.entity.ShippingContainer;
import jm.com.dpbennett.jmts.App;
import jm.com.dpbennett.jmts.Main;


/**
 *
 * @author dbennett
 */
public class ShippingContainerConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Main main = App.findBean("main");
        ShippingContainer container;


        if (value != null) {
            container = ShippingContainer.findShippingContainerByNumber(main.getEntityManager1(), value);
            if (container != null) {
                return container;
            } else {
                container = new ShippingContainer("");

                return container;
            }
        } else {
            container = new ShippingContainer("");

            return container;
        }

    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((ShippingContainer) value).getNumber();
    }
}
