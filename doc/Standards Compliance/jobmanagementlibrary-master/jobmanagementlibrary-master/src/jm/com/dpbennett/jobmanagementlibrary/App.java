/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jobmanagementlibrary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.entity.Employee;
import jm.com.dpbennett.utils.BusinessEntityUtils;

/**
 *
 * @author Desmond
 */

public class App implements Serializable {

//    @PersistenceUnit(unitName = "DocumentStandardPU")
    private EntityManagerFactory EMF;    
    private JobManagement jm;
    private List years;

    /**
     * Creates a new instance of JMTSAppImpl
     */
    public App() {
        jm = new JobManagement();
    }
    
    public App(String persistenceUnitName) {
        jm = new JobManagement();
        EMF = BusinessEntityUtils.getEntityManagerFactory(persistenceUnitName);
    }

    public EntityManagerFactory getEMF() {
        return EMF;
    }

    public void setEMF(EntityManagerFactory EMF) {
        this.EMF = EMF;
    }

    private EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    public List getYears() {
        if (years == null) {
            years = new Vector();
            Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
            for (Integer i = currentYear; i > (currentYear - 10); i--) {
                years.add(new SelectItem(i, i.toString()));
            }

        } else {
            return years;
        }

        return years;
    }

    public List getDateFields() {
        ArrayList dateFields = new ArrayList();

        // add items
        dateFields.add(new SelectItem("dateEntered", "Date entered into database"));
        dateFields.add(new SelectItem("dateEdited", "Date edited"));
        dateFields.add(new SelectItem("dateRevised", "Date revised"));
        dateFields.add(new SelectItem("dateConfirmed", "Date confirmed"));
        dateFields.add(new SelectItem("datePublished", "Date published"));
        dateFields.add(new SelectItem("dateRevisionDue", "Date revision due"));

        return dateFields;
    }

    public List getDatePeriods() {
        ArrayList dateFields = new ArrayList();

        // add items
        dateFields.add(new SelectItem("thisMonth", "This month"));
        dateFields.add(new SelectItem("thisYear", "This year"));
        dateFields.add(new SelectItem("custom", "Custom"));
        // more??

        return dateFields;
    }

    public String getLogonUser() {
        String userName = System.getProperty("user.name");

        return userName;
    }

    /**
     * To be obtained from database
     *
     * @return
     */
    public List getSearchTypes() {
        ArrayList searchTypes = new ArrayList();

        searchTypes.add(new SelectItem("General", "General"));

        return searchTypes;
    }

    public List getDocumentForms() {
        ArrayList forms = new ArrayList();

        forms.add(new SelectItem("E", "Electronic"));
        forms.add(new SelectItem("H", "Hard copy"));
        forms.add(new SelectItem("V", "Verbal"));

        return forms;
    }

    public List<Employee> getEmployees() {
        return Employee.findAllEmployees(getEntityManager());
    }
    
    public List getStandardEnforcementTypes() {
        ArrayList searchTypes = new ArrayList();

        searchTypes.add(new SelectItem("Mandatory", "Mandatory"));
        searchTypes.add(new SelectItem("Voluntary", "Voluntary"));

        return searchTypes;
    }
}
