/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.BusinessEntityManager;
import jm.com.dpbennett.business.entity.BusinessOffice;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.Employee;
import jm.com.dpbennett.business.entity.FactoryInspection;
import jm.com.dpbennett.business.entity.FactoryInspectionComponent;
import jm.com.dpbennett.business.entity.FoodFactory;
import jm.com.dpbennett.business.utils.BusinessEntityUtils;
import jm.com.dpbennett.business.utils.SearchParameters;

import org.primefaces.context.RequestContext;

/**
 *
 * @author dbennett
 */
@Named(value = "foodsManager")
@SessionScoped
public class FoodsManager implements Serializable, BusinessEntityManager {

    @PersistenceUnit(unitName = "BSJDBPU")
    private EntityManagerFactory EMF1;
    private EntityManager entityManager1;
    private Main main;
    private Boolean dirty;
    private Boolean isNewFoodFactory = false;
    private FoodFactory currentFoodFactory;
    private List<FoodFactory> factories;

    /**
     * Creates a new instance of ComplianceManager
     */
    public FoodsManager() {
        factories = new ArrayList<>();

        main = App.findBean("main");
    }

    private EntityManagerFactory getEMF1() {
        return EMF1;
    }

    public EntityManager getEntityManager1() {
        return getEMF1().createEntityManager();
    }

    public Main getMain() {
        return main;
    }

    public void saveFoodFactory() {
        EntityManager em = getEntityManager1();

        if (validateCurrentFoodFactory(em, true)) {
            saveFoodFactory(em);
        }

        em.close();
    }

    public void update() {
        setDirty(true);
    }

    public List<String> completeFoodFactoryName(String query) {
        List<Client> clients = Client.findActiveClientsByFirstPartOfName(getEntityManager1(), query);
        List<String> suggestions = new ArrayList<>();

        if (clients != null) {
            if (!clients.isEmpty()) {
                for (Client client : clients) {
                    suggestions.add(client.getName());
                }
            }
        }

        return suggestions;
    }

    public void closeFoodFactoryDialog(ActionEvent actionEvent) {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public Boolean validateCurrentFoodFactory(EntityManager em, Boolean displayErrorMessage) {
        RequestContext context = RequestContext.getCurrentInstance();

        if (!BusinessEntityUtils.validateName(currentFoodFactory.getName())) {

            if (displayErrorMessage) {
                getMain().setInvalidFormFieldMessage("This factory cannot be saved because a valid factory name was not entered.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
            }
            return false;
        }


        // Assigned inspector
        Employee assignee = Employee.findEmployeeByName(em, currentFoodFactory.getAssignedInspector().getName());
        if (assignee == null) {
            currentFoodFactory.setAssignedInspector(Employee.findDefaultEmployee(em, "--", "--", true));
            if (displayErrorMessage) {
                getMain().setInvalidFormFieldMessage("This factory cannot be saved because an inspector was not assigned.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
            }
            return false;
        }


        return true;
    }

    public void saveFoodFactory(EntityManager em) {
        //EntityManager em = getEntityManager1();
        RequestContext context = RequestContext.getCurrentInstance();

        try {
       
            // replace double quotes with two single quotes to avoid query issues
            currentFoodFactory.setName(currentFoodFactory.getName().trim().replaceAll("\"", "''"));
            // replace & with &#38; query issues
            // save and check for save error
            // indicate if this is a new factory being created
            if (currentFoodFactory.getId() == null) {
                // check if the factory already exists
                FoodFactory factory = FoodFactory.findFoodFactoryByName(em, currentFoodFactory.getName());
                if (factory != null) {
                    getMain().displayCommonMessageDialog(
                            null, 
                            "This factory already exists in the database and will not be re-created.", 
                            "Factory Exists", "info");
                    return;
                }                 
                currentFoodFactory.setDateFirstReceived(new Date());
            } 
            
            // Business office
            currentFoodFactory.setAssignedBusinessOffice(BusinessOffice.findBusinessOfficeByName(em, currentFoodFactory.getAssignedBusinessOffice().getName()));
          
            // Assign already validated employee
            currentFoodFactory.setAssignedInspector(Employee.findEmployeeByName(em, currentFoodFactory.getAssignedInspector().getName()));
         
            // set edited by
            Employee editedBy = Employee.findEmployeeById(em, getMain().getUser().getEmployee().getId());
            currentFoodFactory.setEditedBy(editedBy);

            em.getTransaction().begin();            
            // now save the factory
            Long id = BusinessEntityUtils.saveBusinessEntity(em, currentFoodFactory);
            em.getTransaction().commit();

            if (id == null) {
                context.addCallbackParam("entitySaved", false);
            } else if (id == 0L) {
                context.addCallbackParam("entitySaved", false);
            } else {
                context.addCallbackParam("entitySaved", true);
                isNewFoodFactory = false;
                setDirty(false);
                
            }

        } catch (Exception e) {
            context.addCallbackParam("entitySaved", false);
            System.out.println(e);
        }
    }
    
    public void editFoodFactory() {
        getMain().openDialog(null, "foodFactoryDialog", true, true, true, 400, 600);
    }

    public void doEntitySearch(SearchParameters currentSearchParameters) {

        if (getMain().getUserLoggedIn()) {
            factories = FoodFactory.findFoodFactoriesByDateSearchField(getEntityManager1(),
                    getMain().getUser(),
                    currentSearchParameters.getDateField(),
                    currentSearchParameters.getSearchType(),
                    currentSearchParameters.getSearchText(),
                    null,
                    null);

            if (factories == null) {
                factories = new ArrayList<>();
            }
        } else {
            factories = new ArrayList<>();
        }
//
        System.out.println("factories found: " + factories.size()); //tk
    }

    public String getFactoryDialogHeader() {
        if (isNewFoodFactory) {
            return "Food Factory (NEW)";
        } else {
            return "Food Factory";
        }
    }

    public FoodFactory getCurrentFoodFactory() {
        if (currentFoodFactory == null) {
            return new FoodFactory("");
        }

        return currentFoodFactory;
    }

    public void setCurrentFoodFactory(FoodFactory currentFoodFactory) {
        this.currentFoodFactory = currentFoodFactory;
    }

    public List<FoodFactory> getFactories() {
        Collections.sort(factories);

        return factories;
    }

    public void setFactories(List<FoodFactory> factories) {
        this.factories = factories;
    }

    @Override
    public void setDirty(Boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public Boolean isDirty() {
        return dirty;
    }

    public void createNewFoodFactory() {
        EntityManager em = getEntityManager1();

        isNewFoodFactory = true;
        currentFoodFactory = new FoodFactory("");
        currentFoodFactory.setAssignedBusinessOffice(BusinessOffice.getDefaultBusinessOffice(em, "--"));
        currentFoodFactory.setAssignedInspector(Employee.findDefaultEmployee(em, "--", "--", true));
        currentFoodFactory.setEditedBy(Employee.findDefaultEmployee(em, "--", "--", true));
        setDirty(false);
        
        editFoodFactory();
    }

    public void createFactoryInspectionComponents(FactoryInspection inspection) {
        // ESTABLISHMENT ENVIRONS category
        FactoryInspectionComponent component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Location", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Waste Disposal", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Building Exterior", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Drainage", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Yard Storage", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Tank", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Other Buildings", false);
        inspection.getInspectionComponents().add(component);
        // BUILDING INTERIOR
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Design & Contruction", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Pipes/Conduits/Beams", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Other Overhead Structures", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Lighting/Lighting Intensity", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Ventilation", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Drainage Systems", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Maintenance of walls", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Maintenance of floors", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Maintenance of ceilings", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Gutters/drains", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Screens/Blowers/Insecticutor", false);
        inspection.getInspectionComponents().add(component);
        // RECEIVING
        component = new FactoryInspectionComponent("RECEIVING", "Raw Material Infestation", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("RECEIVING", "Raw Material Infestation", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("RECEIVING", "Raw Material Infestation", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("RECEIVING", "Raw Material Infestation", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("RECEIVING", "Raw Material Infestation", false);
        inspection.getInspectionComponents().add(component);
    }
}
