/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts;

import java.io.Serializable;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import jm.com.dpbennett.business.entity.Employee;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import org.primefaces.context.RequestContext;

/**
 *
 * @author dbennett
 */
@Named(value = "employeeManager")
@SessionScoped
public class EmployeeManager implements Serializable {

    private EntityManagerFactory entityManagerFactory;
    private Employee employee;
    private Boolean dirty;
    private String componentsToUpdate;

    /**
     * Creates a new instance of ClientForm
     */
    public EmployeeManager() {
        //private EntityManager entityManager;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public void updateEmployee() {
        setDirty(true);
    }

    public void createNewEmployee() {
        setEmployee(new Employee());
    }

    public String getComponentsToUpdate() {
        return componentsToUpdate;
    }

    public void setComponentsToUpdate(String componentsToUpdate) {
        this.componentsToUpdate = componentsToUpdate;
    }

    public Boolean getDirty() {
        return dirty;
    }

    public void setDirty(Boolean dirty) {
        this.dirty = dirty;
    }

    public Employee getEmployee() {
        if (employee == null) {
            employee = new Employee();
        }

        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployeeById(EntityManager em, Long Id) {

        try {
            return em.find(Employee.class, Id);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    /**
     * Gets first employee with the given firstname and lasname
     *
     * @param em
     * @param firstName
     * @param lastName
     * @return
     */
    public Employee getEmployeeByName(EntityManager em, String firstName, String lastName) {

        if (firstName != null && lastName != null) {
            String newFirstName = firstName.replaceAll("'", "''").trim().toUpperCase();
            String newLastName = lastName.replaceAll("'", "''").trim().toUpperCase();
            try {
                List<Employee> employees = em.createQuery("SELECT e FROM Employee e "
                        + "WHERE UPPER(e.firstName) "
                        + "= '" + newFirstName + "' AND UPPER(e.lastName) = '" + newLastName + "'",
                        Employee.class).getResultList();
                if (employees.size() > 0) {
                    return employees.get(0);
                }
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    public void cancelEmployeeEdit(ActionEvent actionEvent) {
        setEmployee(null);
    }

    public void saveEmployee(ActionEvent actionEvent) {
        EntityManager em = getEntityManager();
        RequestContext context = RequestContext.getCurrentInstance();
        Boolean isNew = false;

        try {

            if ((getEmployee().getName() == null) || (getEmployee().getName().trim().equals(""))) {
                return;
            } else if (!BusinessEntityUtils.validateName(getEmployee().getName())) {
                return;
            }
            // replace double quotes with two single quotes to avoid query issues
            getEmployee().setFirstName(getEmployee().getFirstName().replaceAll("\"", "''"));
            getEmployee().setLastName(getEmployee().getLastName().replaceAll("\"", "''"));

            // save client and check for save error
            // indicate if this is a new client being created
            if (getEmployee().getId() == null) {
                // check if the Employee already exist
                Employee currentEmployee = getEmployeeByName(em, getEmployee().getFirstName(), getEmployee().getLastName());
                if (currentEmployee != null) {
                    context.addCallbackParam("employeeExists", true);
                    return;
                }
                getEmployee().setActive(true);
                isNew = true;
            }

            em.getTransaction().begin();

            BusinessEntityUtils.saveBusinessEntity(em, getEmployee());
            em.getTransaction().commit();

            // a new client was assigned to this job so the job is now dirty 
            if (isNew) {
                setDirty(true);
            }

            em.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
