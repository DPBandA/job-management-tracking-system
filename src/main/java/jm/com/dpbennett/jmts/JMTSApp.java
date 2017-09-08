/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
//import jm.com.dpbennett.jobmanagementlibrary.JobManagement;

/**
 *
 * @author Desmond
 */
@Named(value = "JMTSApp")
@ApplicationScoped
public class JMTSApp implements Serializable {    

    @PersistenceUnit(unitName = "BSJDBPU")
    private EntityManagerFactory EMF;
    // privilege constants
    public static int CAN_ENTER_JOB = 0;
    public static int CAN_DELETE_JOB = 1;
    public static int CAN_EDIT_JOB = 2;
    public static int CAN_EDIT_OWN_JOB = 3;
    public static int CAN_EDIT_DEPARTMENTAL_JOB = 4;    
    //private JobManagement jm;
    private List years;

    /** Creates a new instance of JMTSAppImpl */
    public JMTSApp() {
         //jm = new JobManagement();
    }

    public EntityManagerFactory getEMF() {
        return EMF;
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
        dateFields.add(new SelectItem("dateOfCompletion", "Date delivered"));
        dateFields.add(new SelectItem("dateReceived", "Date received"));
        dateFields.add(new SelectItem("expectedDateOfCompletion", "Agreed delivery date"));

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
     * @return
     */
    public List getSearchTypes() {
        ArrayList searchTypes = new ArrayList();

        searchTypes.add(new SelectItem("General", "General"));
//        searchTypes.add(new SelectItem("My documents", "My documents"));
//        searchTypes.add(new SelectItem("My department's documents", "My department's documents"));

        return searchTypes;
    }

    public List getDocumentForms() {
        ArrayList forms = new ArrayList();

        forms.add(new SelectItem("E", "Electronic"));
        forms.add(new SelectItem("H", "Hard copy"));
        forms.add(new SelectItem("V", "Verbal"));

        return forms;
    }

//    public List<Employee> getEmployees() {
//        return jm.getAllEmployees(getEntityManager());
//    }
}
