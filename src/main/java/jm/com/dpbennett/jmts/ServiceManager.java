/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts;


import jm.com.dpbennett.business.entity.management.ClientManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
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
import jm.com.dpbennett.business.entity.Classification;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.Department;
import jm.com.dpbennett.business.entity.Employee;
import jm.com.dpbennett.business.entity.JobCategory;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.JobSequenceNumber;
import jm.com.dpbennett.business.entity.JobSubCategory;
import jm.com.dpbennett.business.entity.Sector;
import jm.com.dpbennett.business.entity.Service;
import jm.com.dpbennett.business.entity.ServiceRequest;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.business.entity.utils.SearchParameters;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author dbennett
 */
@Named(value = "serviceManager")
@SessionScoped
public class ServiceManager implements Serializable, BusinessEntityManager {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF;
    private List<ServiceRequest> serviceRequestSearchResultsList;
    private SearchParameters currentSearchParameters;
    private ServiceRequest currentServiceRequest;
    private Boolean dirty;
    private Main main;
    private ClientManager clientManager;

    /**
     * Creates a new instance of ServiceManager
     */
    public ServiceManager() {
        dirty = false;
        main = Application.findBean("main");
        clientManager = Application.findBean("clientManager");
    }

    public void updateAutoGenerateServiceRequestNumber() {

        if (currentServiceRequest.getAutoGenerateServiceRequestNumber()) {
            currentServiceRequest.getServiceRequestNumber();
        } else {
            currentServiceRequest.setServiceRequestSequenceNumber(null);
        }

        setDirty(true);
    }

    public void updateServiceRequestNumber() {
        setDirty(true);
    }

    public void updateServiceRequest() {
        setDirty(true);
    }

    public ServiceRequest getCurrentServiceRequest() {
        if (currentServiceRequest == null) {
            currentServiceRequest = Application.createNewServiceRequest(getEntityManager(), getUser(), Boolean.TRUE);
        }
        return currentServiceRequest;
    }

    public void setCurrentServiceRequest(ServiceRequest currentServiceRequest) {
        this.currentServiceRequest = currentServiceRequest;
    }

    @Override
    public void setDirty(Boolean dirty) {
        this.dirty = dirty;
    }

    public void saveCurrentServiceRequest() {
        EntityManager em = getEntityManager();
        saveCurrentServiceRequest(em);
        closeEntityManager(em);
    }

    public void setInvalidFormFieldMessage(String messag) {
        //JobManager manager = Application.findBean("jobManager");
        main.setInvalidFormFieldMessage(messag);
    }

    public void saveCurrentServiceRequest(EntityManager em) {
        RequestContext context = RequestContext.getCurrentInstance();
        JobSequenceNumber nextJobSequenceNumber = null;

        try {

            BusinessOffice businessOffice = BusinessOffice.findBusinessOfficeByName(em, currentServiceRequest.getBusinessOffice().getName());
            if (businessOffice != null) {
                currentServiceRequest.setBusinessOffice(businessOffice);
            } else {
                setInvalidFormFieldMessage("Please enter or select a valid business office.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            }
            // request number - check if nunmber is already associated with a request            
            ServiceRequest existingRequest = ServiceRequest.findServiceRequestByServiceRequestNumber(em, currentServiceRequest.getServiceRequestNumber());
            if (existingRequest != null) {
                if (existingRequest.getId() != currentServiceRequest.getId()) {
                    setInvalidFormFieldMessage("This service request cannot be saved because the service request number is not unique.");
                    context.update("invalidFieldDialogForm");
                    context.execute("invalidFieldDialog.show();");
                    return;
                }
            }

            // consider validating number here

            // Validate client
            // Validate and save client
            if (!BusinessEntityUtils.validateName(currentServiceRequest.getClient().getName())) {
                main.setInvalidFormFieldMessage("This service request cannot be saved. Please enter a valid client name.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            }

            // department
            Department department = Department.findDepartmentByName(em, currentServiceRequest.getDepartment().getName());
            if (department == null) {
                setInvalidFormFieldMessage("Please select a department.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            } else {
                currentServiceRequest.setDepartment(department);
            }

            // assignee
            Employee assignee = getEmployeeByName(em, currentServiceRequest.getAssignedTo().getName());
            if (assignee != null) {
                currentServiceRequest.setAssignedTo(assignee);
            } else {
                setInvalidFormFieldMessage("Please select an assignee/department representative.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            }

            // classification objects
            Classification classification = Classification.findClassificationById(em, currentServiceRequest.getClassification().getId());
            if (classification == null) {
                setInvalidFormFieldMessage("Please select a classification.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            } else {
                currentServiceRequest.setClassification(classification);
            }

            Sector sector = Sector.findSectorById(em, currentServiceRequest.getSector().getId());
            if (sector == null) {
                setInvalidFormFieldMessage("Please select a sector.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            } else {
                currentServiceRequest.setSector(sector);
            }

            JobCategory category = JobCategory.findJobCategoryById(em, currentServiceRequest.getJobCategory().getId());
            if (category == null) {
                setInvalidFormFieldMessage("Please select a job category.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            } else {
                currentServiceRequest.setJobCategory(category);
            }

            JobSubCategory subCategory = JobSubCategory.findJobSubCategoryById(em, currentServiceRequest.getJobSubCategory().getId());
            if (subCategory == null) {
                setInvalidFormFieldMessage("Please select a job subcategory.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            } else {
                currentServiceRequest.setJobSubCategory(subCategory);
            }

            // set date enetered
            if (currentServiceRequest.getDateAndTimeEntered() == null) {
                currentServiceRequest.setDateAndTimeEntered(new Date());
            }

            // Get employee for later use
            Employee employee = Employee.findEmployeeById(em, getUser().getEmployee().getId());

            // This means this this is a new request so set user and person who entered the request
            if (currentServiceRequest.getEnteredBy().getId() == null) {
                currentServiceRequest.setEnteredBy(employee);
            }

            // Update re the person who last edited/entered the job etc.
            currentServiceRequest.setDateStatusEdited(new Date());
            currentServiceRequest.setEditedBy(employee);

            // save and check for error
            // modify service number with sequence number if required
            if (currentServiceRequest.getAutoGenerateServiceRequestNumber()) {
                if (currentServiceRequest.getServiceRequestSequenceNumber() == null) {
                    //currentJob.setJobSequenceNumber(jm.getNextJobSequentialNumber(em, currentJob.getYearReceived()));
                    nextJobSequenceNumber = JobSequenceNumber.findNextJobSequenceNumber(em, currentServiceRequest.getYearReceived());
                    currentServiceRequest.setServiceRequestSequenceNumber(nextJobSequenceNumber.getSequentialNumber());
                    // get/set servie request number
                    currentServiceRequest.setServiceRequestNumber(currentServiceRequest.getServiceRequestNumber());
                } else {
                    // get/set servie request number
                    currentServiceRequest.setServiceRequestNumber(currentServiceRequest.getServiceRequestNumber());
                }
            }

            if (currentServiceRequest.getService() != null) {
                //if (currentServiceRequest.getService().getName() != null) {
                Service service = Service.findServiceByName(em, currentServiceRequest.getService().getName());
                if (service != null) {
                    currentServiceRequest.setService(service);
                } else {
                    setInvalidFormFieldMessage("Please enter or select a service");
                    context.update("invalidFieldDialogForm");
                    context.execute("invalidFieldDialog.show();");
                    return;
                }
            }

            // save job to database and check for errors
            em.getTransaction().begin();
            Long id = BusinessEntityUtils.saveBusinessEntity(em, currentServiceRequest);
            if (id == null) {
                // set seq. number to null to ensure that the next sequence #
                // is retrieved with the next job save attempt.
                currentServiceRequest.setServiceRequestSequenceNumber(null);
                context.execute("undefinedErrorDialog.show();");
                sendErrorEmail("An error occured while saving job (Null ID)" + currentServiceRequest.getServiceRequestNumber(),
                        "Job save error occured");

            } else if (id == 0L) {
                // set seq. number to null to ensure that the next sequence #
                // is retrieved with the next job save attempt.
                currentServiceRequest.setServiceRequestSequenceNumber(null);
                context.execute("undefinedErrorDialog.show();");
                sendErrorEmail("An error occured while saving job (0L ID)" + currentServiceRequest.getServiceRequestNumber(),
                        "Job save error occured");
            } else {
                // save job sequence number only if job save was successful
                if (nextJobSequenceNumber != null) {
                    BusinessEntityUtils.saveBusinessEntity(em, nextJobSequenceNumber);
                }
            }
            em.getTransaction().commit();
            setDirty(false);

        } catch (Exception e) {
            // set seq. number to null to ensure that the next sequence #
            // is retrieved with the next job save attempt.
            currentServiceRequest.setServiceRequestSequenceNumber(null);
            context.execute("undefinedErrorDialog.show();");
            System.out.println(e);
            // send error message to developer's email
            sendErrorEmail("An exception occurred while saving the service request!",
                    "Service Request number: " + currentServiceRequest.getServiceRequestNumber()
                    + "\nJMTS User: " + getUser().getUsername()
                    + "\nDate/time: " + new Date()
                    + "\nException detail: " + e);
        }
    }

    /**
     * Send error email using the Job Manager
     *
     * @param subject
     * @param message
     */
    public void sendErrorEmail(String subject, String message) {
        try {
            // send error message to developer's email
            JobManager manager = Application.findBean("jobManager");
            manager.sendErrorEmail(subject, message);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void updateDateExpectedCompletionDate(SelectEvent event) {
        Date selectedDate = (Date)event.getObject();

        currentServiceRequest.setExpectedDateOfCompletion(selectedDate);

        setDirty(Boolean.TRUE);
    }

    public void updateDateCompleted(SelectEvent event) {
        Date selectedDate = (Date)event.getObject();

        currentServiceRequest.setDateOfCompletion(selectedDate);

        setDirty(Boolean.TRUE);
    }

    public void doServiceRequestSearch(SearchParameters currentSearchParameters) {

        this.currentSearchParameters = currentSearchParameters;

        serviceRequestSearchResultsList = ServiceRequest.findServiceRequestsByDateSearchField(getEntityManager(),
                getUser(),
                currentSearchParameters.getDateField(),
                currentSearchParameters.getSearchType(),
                currentSearchParameters.getSearchText(),
                currentSearchParameters.getDatePeriod().getStartDate(),
                currentSearchParameters.getDatePeriod().getEndDate(), false);

        if (serviceRequestSearchResultsList == null) {
            serviceRequestSearchResultsList = new ArrayList<>();
        }
    }

    public String getSearchResultsTableHeader() {
        return Application.getSearchResultsTableHeader(currentSearchParameters, getServiceRequestSearchResultsList());
    }

    public List<ServiceRequest> getServiceRequestSearchResultsList() {
        if (serviceRequestSearchResultsList == null) {
            serviceRequestSearchResultsList = new ArrayList<>();
        }
        return serviceRequestSearchResultsList;
    }

    public JobManagerUser getUser() {
        return main.getUser();
    }

    public EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    public void createNewServiceRequest() {
        // handle user privilege and return if the user does not have
        // the privilege to do what they wish
        RequestContext context = RequestContext.getCurrentInstance();
        EntityManager em = getEntityManager();

        currentServiceRequest = Application.createNewServiceRequest(em, getUser(), Boolean.TRUE);
        context.execute("longProcessDialogVar.hide();serviceRequestDialog.show();");
        context.update("unitDialogForms:serviceRequest");

    }

    public void updateBusinessOffice() {
    }

    public void updateClient() {

        EntityManager em = getEntityManager();
        Client client = Client.findClientByName(em, currentServiceRequest.getClient().getName(), true);
        if (client != null) {
            currentServiceRequest.getClient().doCopy(client);
            currentServiceRequest.getClient().setActive(false);
        }

        setDirty(true);
        closeEntityManager(em);
    }

    public void closeEntityManager(EntityManager em) {
        if (em != null) {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public void updateJob() {
        setDirty(true);
    }

    public void updateDepartment() {
        EntityManager em = getEntityManager();

        try {
            if (currentServiceRequest.getDepartment().getName() != null) {
                Department department = Department.findDepartmentByName(em, currentServiceRequest.getDepartment().getName());
                if (department != null) {
                    currentServiceRequest.setDepartment(department);
                    setDirty(true);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // tk remove this and use the one in Application
    private Employee getEmployeeByName(EntityManager em, String name) {
        String names[] = name.split(",");
        if (names.length == 2) {
            return Employee.findEmployeeByName(em,
                    names[1].trim(),
                    names[0].trim());
        } else {
            return null;
        }
    }

    public void updateAssignee() {
        currentServiceRequest.setAssignedTo(getEmployeeByName(getEntityManager(), currentServiceRequest.getAssignedTo().getName()));

        setDirty(true);
    }

    public void handleDateSubmittedSelect(SelectEvent event) {
        Date selectedDate = (Date)event.getObject();
        Calendar c = Calendar.getInstance();

        // use the date to modify the request number if required.
        currentServiceRequest.setDateSubmitted(selectedDate);
        c.setTime(currentServiceRequest.getDateSubmitted());

        //currentServiceRequest.setServiceRequestNumber(null);

        setDirty(true);
    }

    // Edit the client via the ClientManager
    public void editClient() {

        EntityManager em = getEntityManager();

        // Try to retrieve the client from the database if it exists
        Client client = Client.findClientByName(em, currentServiceRequest.getClient().getName(), true);
        if (client != null) {
            currentServiceRequest.setClient(client);
            clientManager.setClient(client);
        } else {
            clientManager.setClient(currentServiceRequest.getClient());
        }
        closeEntityManager(em);

        // make sure that this is not an active client managed for the BSJ
        //currentServiceRequest.getClient().setActive(false);
        clientManager.setExternalEntityManagerFactory(EMF);
        clientManager.setSave(false);
        clientManager.setBusinessEntityManager(this);
        clientManager.setComponentsToUpdate(":unitDialogForms:serviceRequestDialogTabView:serviceRequestClient");
    }

    public void createNewClient() {
        currentServiceRequest.setClient(new Client(""));
    }

    public void editServiceRequest() {
    }

    public void createJobFromServiceRequest() {
        JobManager jm1 = Application.findBean("jobManager");
        jm1.createNewJob(currentServiceRequest);
    }

    public void closeServiceRequestDialog1(ActionEvent actionEvent) {
        // Redo search to reloasd stored jobs including
        RequestContext context = RequestContext.getCurrentInstance();

        // prompt to save modified job before attempting to create new job
        if (isDirty()) {
            context.update("unitDialogForms:serviceRequestSaveConfirm");
            context.execute("serviceRequestSaveConfirmDialog.show();");
            return;
        }


        setDirty(false);
        doServiceRequestSearch(currentSearchParameters);
    }

    public void closeServiceRequestDialog2(CloseEvent closeEvent) {
        // Redo search to reloasd stored jobs including
        // the currently edited job.
        closeServiceRequestDialog1(null);
    }

    public void cancelServiceRequestEdit() {
        setDirty(false);
        doServiceRequestSearch(currentSearchParameters);
    }

    @Override
    public Boolean isDirty() {
        return dirty;
    }
}
