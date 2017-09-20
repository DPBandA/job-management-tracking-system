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
import jm.com.dpbennett.business.entity.Classification;

/**
 *
 * @author desbenn
 */
public class ClassificationConverter implements Converter{
        
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("JMTSPU");
        EntityManager em = entityManagerFactory.createEntityManager();
        
        Classification classification = 
                Classification.findClassificationByName(em, submittedValue);
        
        if (classification == null) {
            classification = new Classification();
            classification.setName(submittedValue);
        } 
        
        return classification;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {

        if (value == null || value.equals("")) {
            return "";
        } else {
            if (((Classification) value).getName() != null) {
                return ((Classification) value).getName().replaceAll("&#38;", "&");
            } else {
                return "";
            }
        }
    }
    
}
