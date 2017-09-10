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
import jm.com.dpbennett.business.entity.Department;
import jm.com.dpbennett.jmts.Application;
import jm.com.dpbennett.jmts.Main;

/**
 *
 * @author dbennett
 */
@FacesValidator("jm.com.dpbennett.bsjdb.validator.SubcontractedDepartmentValidator")
public class SubcontractedDepartmentValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        Main main = Application.findBean("main");

        if ( value != null) {
            Department subContractedDepartment = Department.findDepartmentByName(main.getEntityManager1(), ((Department) value).getName());
            if (subContractedDepartment == null) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Department not valid!", null));
            } 
        } else {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Department not valid!", null));
        }
    }
}
