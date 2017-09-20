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
import jm.com.dpbennett.business.entity.Client;

/**
 *
 * @author desbenn
 */
public class ActiveClientConverter implements Converter {
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("JMTSPU");
        EntityManager em = entityManagerFactory.createEntityManager();
        
        Client client = Client.findActiveClientByName(em, submittedValue, Boolean.TRUE);

        if (client == null) {
            client = new Client();
            client.setName(submittedValue);
        } 
        
        return client;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        if (value == null || value.equals("")) {
            return "";
        } else {
            if (((Client) value).getName() != null) {
                return ((Client) value).getName().replaceAll("&#38;", "&");
            } else {
                return "";
            }
        }
    }
}
