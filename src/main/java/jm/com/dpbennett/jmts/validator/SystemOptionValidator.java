/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import jm.com.dpbennett.business.utils.BusinessEntityUtils;

/**
 *
 * @author desbenn
 */
@FacesValidator("jm.com.dpbennett.bsjdb.validator.SystemOptionValidator")
public class SystemOptionValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (!BusinessEntityUtils.validateName(value.toString().trim())) {
            //FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter all required (*) fields.", null);

            throw new ValidatorException(getMessage(component.getId()));
        }
    }
    
    private FacesMessage getMessage(String componentId) {
        switch(componentId) {
            case "systemOptionName":
                return new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter an option name.", null);
            case "systemOptionValue":
                return new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter an option value.", null);    
            case "systemOptionValueType":
                return new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter an option value type.", null);   
            case "systemOptionCategory":
                return new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter an option category.", null);  
            case "systemOptionComment":
                return new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter an option comment.", null);      
            default:
                return new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please enter all required (*) fields.", null);
        }
    }
}
