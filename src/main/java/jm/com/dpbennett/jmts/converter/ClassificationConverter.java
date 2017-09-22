/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import jm.com.dpbennett.business.entity.Classification;

/**
 *
 * @author desbenn
 */
@FacesConverter("classificationConverter")
public class ClassificationConverter extends ConverterAdapter{
        
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
                
        Classification classification = 
                Classification.findClassificationByName(getEntityManager(), submittedValue);
        
        if (classification == null) {
            classification = new Classification();
            classification.setName(submittedValue);
        } 
        
        return classification;
    }
    
}
