/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts.managers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.BusinessOffice;
import jm.com.dpbennett.business.entity.Classification;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.Department;
import jm.com.dpbennett.business.entity.DocumentReport;
import jm.com.dpbennett.business.entity.DocumentSequenceNumber;
import jm.com.dpbennett.business.entity.DocumentType;
import jm.com.dpbennett.business.entity.Employee;
import jm.com.dpbennett.business.entity.Internet;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.LegalDocument;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.jmts.Application;
import jm.com.dpbennett.jmts.utils.MainTabView;
import jm.com.dpbennett.jmts.utils.PrimeFacesUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.PrimeFaces;
import org.primefaces.component.tabview.Tab;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author Desmond Bennett
 */
@Named
@SessionScoped
public class LegalDocumentManager implements Serializable {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF;
    private int activeTabIndex;
    private int activeDocTabIndex;
    private int activeNavigationTabIndex;
    private String activeTabForm;
    private Tab activeTab;
    private String dateSearchField;
    private String dateSearchPeriod;
    private String searchType;
    private Boolean startSearchDateDisabled;
    private Boolean endSearchDateDisabled;
    private Date startDate;
    private Date endDate;
    private String searchText;
    private String previousSearchText;
    private Boolean searchTextVisible;
    private Long selectedDocumentId;
    private List<LegalDocument> documentSearchResultList;
    private LegalDocument selectedDocument;
    private LegalDocument currentDocument;
    private DocumentReport documentReport;
    @Column(length = 1024)
    private String status;
    private String priorityLevel;
    private JobManager jobManager;

    /**
     * Creates a new instance of JobManager
     */
    public LegalDocumentManager() {
        activeTabIndex = 0;
        activeNavigationTabIndex = 0;
        activeTabForm = "";
        searchType = "General";
        dateSearchField = "dateReceived";
        dateSearchPeriod = "thisMonth";
        searchTextVisible = true;
        changeSearchDatePeriod();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }

    public int getActiveDocTabIndex() {
        return activeDocTabIndex;
    }

    public void setActiveDocTabIndex(int activeDocTabIndex) {
        this.activeDocTabIndex = activeDocTabIndex;
    }

    public Boolean getSearchTextVisible() {
        return searchTextVisible;
    }

    public void setSearchTextVisible(Boolean searchTextVisible) {
        this.searchTextVisible = searchTextVisible;
    }

    public DocumentReport getDocumentReport() {
        if (documentReport == null) {
            documentReport = new DocumentReport();
        }
        return documentReport;
    }

    public void setDocumentReport(DocumentReport documentReport) {
        this.documentReport = documentReport;
    }

    public int getActiveNavigationTabIndex() {
        return activeNavigationTabIndex;
    }

    public void setActiveNavigationTabIndex(int activeNavigationTabIndex) {
        this.activeNavigationTabIndex = activeNavigationTabIndex;
    }

    public void loadDocument() {
    }

    public void onMainTabChange(TabChangeEvent event) {
        String tabTitle = event.getTab().getTitle();
        if (tabTitle.equals("Documents database")) {
//            activeNavigationTabIndex = 0;
            activeTabIndex = 0;
            searchText = previousSearchText;
            searchTextVisible = true;
        } else if (tabTitle.equals("Reporting")) {
//            activeNavigationTabIndex = 1;
            activeTabIndex = 1;
            previousSearchText = searchText;
            // do search with the default search text used in doLegalDocumentSearch()
            searchText = "";
            searchTextVisible = false;
        }
        doLegalDocumentSearch();
    }

    public void onDocTabChange(TabChangeEvent event) {
        String tabTitle = event.getTab().getTitle();
        if (tabTitle.equals("General")) {
            activeDocTabIndex = 0;
        } else if (tabTitle.equals("Tracking")) {
            activeDocTabIndex = 1;
        }
    }

    public void onNavigationTabChange(TabChangeEvent event) {
//        String tabTitle = event.getTab().getTitle();
//        if (tabTitle.equals("Document search criteria")) {
//            activeTabIndex = 0;
//            activeNavigationTabIndex = 0;
//            searchText = previousSearchText;
//            doLegalDocumentSearch();
//        } else if (tabTitle.equals("Reporting criteria")) {
//            activeTabIndex = 1;
//            activeNavigationTabIndex = 1;
//            previousSearchText = searchText;
//            searchText = "ed";
//            doLegalDocumentSearch();
//        }
        doLegalDocumentSearch();
    }

    
    // tk make use of DatePeriod class for this as is done for job search
    public final void changeSearchDatePeriod() {
                  
//        if (dateSearchPeriod.equals("Custom")) {
//            setStartSearchDateDisabled(false);
//            setEndSearchDateDisabled(false);
//        } else if (dateSearchPeriod.equals("This year")) {
//            startDate = getStartOfCurrentYear();
//            endDate = getEndOfCurrentYear();
//            setStartSearchDateDisabled(true);
//            setEndSearchDateDisabled(true);
//        } else if (dateSearchPeriod.equals("Month")) {
//            startDate = getStartOfCurrentMonth();
//            endDate = getEndOfCurrentMonth();
//            setStartSearchDateDisabled(true);
//            setEndSearchDateDisabled(true);
//        }
//        if (EMF != null) {
//            doLegalDocumentSearch();
//        }

    }

    public void handleStartSearchDateSelect() {
        doLegalDocumentSearch();
    }

    public void handleEndSearchDateSelect() {
        doLegalDocumentSearch();
    }

    public void handleKeepAlive() {
        EntityManager em = getEntityManager();

        em.getTransaction().begin();
        getUser().setPollTime(new Date());
//        jm.saveBusinessEntity(em, user);
        BusinessEntityUtils.saveBusinessEntity(em, getUser());
        em.getTransaction().commit();
    }

    private Date getStartOfCurrentYear() { // tk put in lib
        Calendar c, current;

        // current time and date
        current = Calendar.getInstance();
        c = Calendar.getInstance();
        // set start date
        c.set(current.get(Calendar.YEAR), Calendar.JANUARY, 1, 0, 0, 0);

        return c.getTime();
    }

    private Date getStartOfCurrentMonth() { // tk put in lib
        Calendar c, current;

        // current time and date
        current = Calendar.getInstance();
        c = Calendar.getInstance();
        // set start date
        c.set(current.get(Calendar.YEAR), current.get(Calendar.MONTH), 1, 0, 0, 0);

        return c.getTime();
    }

    private int getDaysInMonth(int month) {
        Calendar current = Calendar.getInstance();

        switch (month) {
            case Calendar.JANUARY:
                return 31;
            case Calendar.FEBRUARY:
                int currentYear = current.get(Calendar.YEAR);
                if ((currentYear % 4) == 0) { // leap year?
                    return 29;
                } else {
                    return 28;
                }
            case Calendar.MARCH:
                return 31;
            case Calendar.APRIL:
                return 30;
            case Calendar.MAY:
                return 31;
            case Calendar.JUNE:
                return 30;
            case Calendar.JULY:
                return 31;
            case Calendar.AUGUST:
                return 31;
            case Calendar.SEPTEMBER:
                return 30;
            case Calendar.OCTOBER:
                return 31;
            case Calendar.NOVEMBER:
                return 30;
            case Calendar.DECEMBER:
                return 31;
            default:
                return -1;
        }
    }

    private Date getEndOfCurrentMonth() { // tk put in lib
        Calendar c, current;

        // current time and date
        current = Calendar.getInstance();
        c = Calendar.getInstance();
        // set end date
        c.set(current.get(Calendar.YEAR), current.get(Calendar.MONTH), getDaysInMonth(current.get(Calendar.MONTH)), 23, 59, 59);

        return c.getTime();
    }

    private Date getEndOfCurrentYear() { // tk put in lib
        Calendar c, current;

        // current time and date
        current = Calendar.getInstance();
        c = Calendar.getInstance();
        // set end date
        c.set(current.get(Calendar.YEAR), Calendar.DECEMBER, 31, 23, 59, 59);

        return c.getTime();
    }

    public int getNumberOfDocumentsFound() {
        if (documentSearchResultList != null) {
            return documentSearchResultList.size();
        } else {
            return 0;
        }
    }

    public void deleteDocument() {
        EntityManager em = getEntityManager();

        em.getTransaction().begin();
        LegalDocument document = em.find(LegalDocument.class, selectedDocument.getId());
        em.remove(document);
        em.flush();
        em.getTransaction().commit();

        // do search to update search list.
        doLegalDocumentSearch();
    }

    public void cancelDocumentEdit(ActionEvent actionEvent) {
        activeDocTabIndex = 0;
    }

    public void editDocument(ActionEvent actionEvent) {
        if (currentDocument != null) {
            if (currentDocument.getId() != null) {
                selectedDocumentId = currentDocument.getId();
                currentDocument = LegalDocument.findLegalDocumentById(getEntityManager(), currentDocument.getId());
            }
        }
    }

    public void editDocumentType(ActionEvent actionEvent) {
        //tk
//        if (currentDocument != null) {
//            if (currentDocument.getId() != null) {
//                currentDocument = jm.getLegalDocumentById(getEntityManager(), currentDocument.getId());
//                // get most recent type from database
//                if (currentDocument.getType() != null) {
//                    currentDocument.setType(jm.getDocumentTypeById(getEntityManager(), currentDocument.getType().getId()));
//                }
//            }
//        }
    }

    public void cancelDocumentTypeEdit(ActionEvent actionEvent) {
    }

//    public Client getExternalClient() {
//        if (currentDocument.getExternalClient() == null) {
//            currentDocument.setExternalClient(jm.getDefaultClient(getEntityManager(), "--"));
//
//            return currentDocument.getExternalClient();
//        } else {
//            return currentDocument.getExternalClient();
//        }
//    }
//
//    public void setExternalClient(Client client) {
//        currentDocument.setExternalClient(client);
//    }
    public void saveDocumentType(ActionEvent actionEvent) {
        EntityManager em = getEntityManager();

        DocumentType type = DocumentType.findDocumentTypeByNameAndCode(em, currentDocument.getType().getName(),
                currentDocument.getType().getCode());
        // if document already exist do not save
        if (type != null) {
            // update document number
            if (currentDocument.getAutoGenerateNumber()) {
                currentDocument.setNumber(LegalDocument.getLegalDocumentNumber(currentDocument, "ED"));
            }
            return;
        } // if type with this name exists update the code and save
        else if ((DocumentType.findDocumentTypeByName(em, currentDocument.getType().getName()) != null)
                || (DocumentType.findDocumentTypeByCode(em, currentDocument.getType().getCode()) != null)) {
            em.getTransaction().begin();
            BusinessEntityUtils.saveBusinessEntity(em, currentDocument.getType());
            em.getTransaction().commit();
            // update document number
            if (currentDocument.getAutoGenerateNumber()) {
                currentDocument.setNumber(LegalDocument.getLegalDocumentNumber(currentDocument, "ED"));
            }

            return;
        } else {
            em.getTransaction().begin();
            currentDocument.getType().setId(null); // forces saving of new type.
            BusinessEntityUtils.saveBusinessEntity(em, currentDocument.getType());
            em.getTransaction().commit();

            // update document number
            if (currentDocument.getAutoGenerateNumber()) {
                currentDocument.setNumber(LegalDocument.getLegalDocumentNumber(currentDocument, "ED"));
            }
        }
    }

    public void saveCurrentLegalDocument(ActionEvent actionEvent) {
        EntityManager em = getEntityManager();
        RequestContext context = RequestContext.getCurrentInstance();

        try {

            // validate required values.
            if (currentDocument.getNumber() == null) {
                context.addCallbackParam("valueRequired", true);
                return;
            } else if (currentDocument.getNumber().trim().equals("")) {
                context.addCallbackParam("valueRequired", true);
                return;
            } else if (getEmployeeByName(currentDocument.getSubmittedBy(), currentDocument.getSubmittedBy().getName()) == null) {
                context.addCallbackParam("valueRequired", true);
                return;
            } else if (getEmployeeByName(currentDocument.getResponsibleOfficer(), currentDocument.getResponsibleOfficer().getName()) == null) {
                context.addCallbackParam("valueRequired", true);
                return;
            } else if (currentDocument.getDateReceived() == null) {
                context.addCallbackParam("valueRequired", true);
                return;
            } else {
                context.addCallbackParam("valueRequired", false);
            }

            // get needed objects
            // get objects
            currentDocument.setType(DocumentType.findDocumentTypeById(em, currentDocument.getType().getId()));
            //currentDocument.setResponsibleOfficer(jm.getEmployeeById(em, currentDocument.getResponsibleOfficer().getId()));
            Employee responsibleOfficer = Employee.findEmployeeByName(em,
                    currentDocument.getResponsibleOfficer().getFirstName(),
                    currentDocument.getResponsibleOfficer().getLastName());
            if (responsibleOfficer != null) {
                currentDocument.setResponsibleOfficer(responsibleOfficer);
            }
            currentDocument.setResponsibleDepartment(Department.findDepartmentById(em, currentDocument.getResponsibleDepartment().getId()));
            currentDocument.setRequestingDepartment(Department.findDepartmentById(em, currentDocument.getRequestingDepartment().getId()));
            //currentDocument.setSubmittedBy(getEmployeeByName(currentDocument.getSubmittedBy(), currentDocument.getSubmittedBy().getName()));
            Employee submittedBy = Employee.findEmployeeByName(em,
                    currentDocument.getSubmittedBy().getFirstName(),
                    currentDocument.getSubmittedBy().getLastName());
            if (submittedBy != null) {
                currentDocument.setSubmittedBy(submittedBy);
            }
            currentDocument.setClassification(Classification.findClassificationById(em, currentDocument.getClassification().getId()));

            // save client and doc
            em.getTransaction().begin();
            // Get client from database if it exists
            updateClient();
            if (!currentDocument.getExternalClient().getName().trim().equals("")) {
                BusinessEntityUtils.saveBusinessEntity(em, currentDocument.getExternalClient());
            } else {
                currentDocument.setExternalClient(null);
            }
            saveLegalDocument(em, currentDocument);
            em.getTransaction().commit();
            // redo search
            doLegalDocumentSearch();
            // reset to first first doc tab
            activeDocTabIndex = 0;
            // add callback param
            context.addCallbackParam("legalDocumentSaved", true);
        } catch (Exception e) {
            System.out.println(e);
            context.addCallbackParam("legalDocumentSaved", false);
        }
    }

    public LegalDocument saveLegalDocument(EntityManager em, LegalDocument legalDocument) {

        // check if the current sequence number exist and assign a new sequence number if needed
        if (DocumentSequenceNumber.findDocumentSequenceNumber(em,
                legalDocument.getSequenceNumber(),
                legalDocument.getYearReceived(),
                legalDocument.getMonthReceived(),
                legalDocument.getType().getId()) == null) {
            legalDocument.setSequenceNumber(DocumentSequenceNumber.findNextDocumentSequenceNumber(em,
                    legalDocument.getYearReceived(),
                    legalDocument.getMonthReceived(),
                    legalDocument.getType().getId()));
        }
        if (legalDocument.getAutoGenerateNumber()) {
            legalDocument.setNumber(LegalDocument.getLegalDocumentNumber(legalDocument, "ED"));
        }
        BusinessEntityUtils.saveBusinessEntity(em, legalDocument);

        return legalDocument;
    }

    public List<Client> completeClient(String query) { // tk use version in Application class
        try {

            return Client.findActiveClientsByAnyPartOfName(getEntityManager(), query);
        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List<Employee> completeEmployee(String query) {

        try {
            List<Employee> employees = Employee.findEmployeesByName(getEntityManager(), query);
//            List<String> suggestions = new ArrayList<String>();
            if (employees != null) {
                return employees;
            } else {
                return new ArrayList<Employee>();
            }
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<Employee>();
        }
    }

    public List<String> completeType(String query) {

        try {
            List<DocumentType> types = DocumentType.findDocumentTypesByName(getEntityManager(), query);
            List<String> suggestions = new ArrayList<String>();
            if (types != null) {
                if (!types.isEmpty()) {
                    for (DocumentType type : types) {
                        suggestions.add(type.getName());
                    }
                }
            }

            return suggestions;
        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<String>();
        }
    }

    public List<String> completeCode(String query) {

        try {
            List<DocumentType> types = DocumentType.findDocumentTypesByCode(getEntityManager(), query);
            List<String> suggestions = new ArrayList<String>();
            if (types != null) {
                if (!types.isEmpty()) {
                    for (DocumentType type : types) {
                        suggestions.add(type.getCode());
                    }
                }
            }

            return suggestions;
        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<String>();
        }
    }

//    public String getSubmittedBy() {
//        //return currentDocument.getSubmittedBy().getLastName() + ", " + currentDocument.getSubmittedBy().getFirstName();
//        return currentDocument.getSubmittedBy().getName();
//    }
//
//    public void setSubmittedBy(String name) {
//        if (name != null) {
//            String names[] = name.split(",");
//            if (names.length == 2) {
//                currentDocument.getSubmittedBy().setFirstName(names[1].trim());
//                currentDocument.getSubmittedBy().setLastName(names[0].trim());
//            }
//        }
//    }
//    public String getOfficerResponsible() {
//        return currentDocument.getResponsibleOfficer().getLastName() + ", "
//                + currentDocument.getResponsibleOfficer().getFirstName();
//    }
//
//    public void setOfficerResponsible(String name) {
//        if (name != null) {
//            String names[] = name.split(",");
//            if (names.length == 2) {
//                currentDocument.getResponsibleOfficer().setFirstName(names[1].trim());
//                currentDocument.getResponsibleOfficer().setLastName(names[0].trim());
//            }
//        }
//    }
    private Employee getEmployeeByName(Employee employee, String name) {
        String names[] = name.split(",");
        if (names.length == 2) {
            employee.setFirstName(names[1].trim());
            employee.setLastName(names[0].trim());
            return Employee.findEmployeeByName(getEntityManager(),
                    names[1].trim(),
                    names[0].trim());
        } else {
            return null;
        }

    }

    public void updateSubmittebBy(SelectEvent event) {
        currentDocument.setSubmittedBy(getEmployeeByName(currentDocument.getSubmittedBy(), currentDocument.getSubmittedBy().getName()));
    }

    public void updateOfficerResponsible(SelectEvent event) {
        currentDocument.setResponsibleOfficer(getEmployeeByName(currentDocument.getResponsibleOfficer(), currentDocument.getResponsibleOfficer().getName()));
    }

    public void updateType(SelectEvent event) {
        System.out.println(currentDocument.getType().getName());
    }

    public void updateCode(SelectEvent event) {
    }

    public void updateClient(SelectEvent event) {
        EntityManager em = getEntityManager();

        if (currentDocument.getExternalClient().getName() != null) {
            Client client = Client.findClientByName(em, currentDocument.getExternalClient().getName(), true);
            if (client != null) {
                currentDocument.setExternalClient(client);
            } else {
                if (!currentDocument.getExternalClient().getName().trim().equals("")) {
                    currentDocument.setExternalClient(new Client(currentDocument.getExternalClient().getName().trim()));
                } else {
                    currentDocument.setExternalClient(null);
                }
            }
        }

    }

    public void editClient() {
        if (currentDocument.getExternalClient().getName() == null) {
            currentDocument.setExternalClient(new Client(""));
            currentDocument.getExternalClient().setInternet(new Internet());
        }
    }

    public void updateClient() {
        updateClient(null);
    }

    public void saveClient(ActionEvent actionEvent) {
        EntityManager em = getEntityManager();
        RequestContext context = RequestContext.getCurrentInstance();

        try {
            context.addCallbackParam("clientSaved", true);

            if ((currentDocument.getExternalClient().getName() == null)
                    || (currentDocument.getExternalClient().getName().trim().equals(""))) {
                return;
            } else if (!BusinessEntityUtils.validateName(currentDocument.getExternalClient().getName())) {
                return;
            }
            // replace double quotes with two single quotes to avoid query issues
            currentDocument.getExternalClient().setName(currentDocument.getExternalClient().getName().replaceAll("\"", "''"));
            // replace & with &#38; query issues
            // save client and check for save error           
            // indicate if this is a new client being created
            if (currentDocument.getExternalClient().getId() == null) {
                // check if the client already exist
                String clientName = currentDocument.getExternalClient().getName();
                Client currentClient = Client.findClientByName(em, clientName, true);
                if (currentClient != null) {
                    context.addCallbackParam("clientExist", true);
                    return;
                }
                currentDocument.getExternalClient().setDateFirstReceived(new Date());
            }
            em.getTransaction().begin();
            Long id = BusinessEntityUtils.saveBusinessEntity(em, currentDocument.getExternalClient());
            em.getTransaction().commit();

            if (id == null) {
                context.addCallbackParam("clientSaved", false);
            } else if ((id == null) || (id == 0L)) {
                context.addCallbackParam("clientSaved", false);
            } else {
                context.addCallbackParam("clientSaved", true);
                // make sure this client is assigned to this job
                currentDocument.setExternalClient(Client.getClientById(em, id));
            }

        } catch (Exception e) {
            context.addCallbackParam("clientSaved", false);
            System.out.println(e);
        }
    }

    public void cancelClientEdit(ActionEvent actionEvent) {
        // redo search to reload for data with existing database records
        doLegalDocumentSearch();
    }

    public void updateDocument() {
        if (currentDocument.getAutoGenerateNumber()) {
            currentDocument.setNumber(LegalDocument.getLegalDocumentNumber(currentDocument, "ED"));
        }
    }

    public void updateDeliveryDate() {
        //currentDocument.setDateOfCompletion(e.getDate());
    }

    public void updateExpectedDeliveryDate() {
        //currentDocument.setExpectedDateOfCompletion(e.getDate());
    }

    public void updateDateReceived() {
        //currentDocument.setDateReceived(e.getDate());
        Calendar c = Calendar.getInstance();

        // set new month received
        c.setTime(currentDocument.getDateReceived());
        currentDocument.setMonthReceived(c.get(Calendar.MONTH));
        currentDocument.setYearReceived(c.get(Calendar.YEAR));
        if (currentDocument.getAutoGenerateNumber()) {
            currentDocument.setNumber(LegalDocument.getLegalDocumentNumber(currentDocument, "ED"));
        }
    }

    public void updateAutoGenerateNumber() {
    }

    public void updateDepartmentResponsible() {
        if (currentDocument.getResponsibleDepartment().getId() != null) {
            currentDocument.setResponsibleDepartment(Department.findDepartmentById(getEntityManager(),
                    currentDocument.getResponsibleDepartment().getId()));
            if (currentDocument.getAutoGenerateNumber()) {
                currentDocument.setNumber(LegalDocument.getLegalDocumentNumber(currentDocument, "ED"));
            }
        }
    }

    public void updateResquestingDepartment() {
        if (currentDocument.getRequestingDepartment().getId() != null) {
            currentDocument.setRequestingDepartment(Department.findDepartmentById(getEntityManager(),
                    currentDocument.getRequestingDepartment().getId()));
        }
    }

    /**
     * Updates the document type and then update the document number which is
     * partly based on the document type.
     */
    public void updateDocumentType() {
        if (currentDocument.getType().getId() != null) {
            currentDocument.setType(DocumentType.findDocumentTypeById(getEntityManager(),
                    currentDocument.getType().getId()));
            if (currentDocument.getAutoGenerateNumber()) {
                currentDocument.setNumber(LegalDocument.getLegalDocumentNumber(currentDocument, "ED"));
            }
        }
    }

    public void updateDocumentReport() {
        if (documentReport.getId() != null) {
            documentReport = DocumentReport.findDocumentReportById(getEntityManager(), documentReport.getId());
            doLegalDocumentSearch();
        }
    }

    public void updateDocumentForm() {
        if (currentDocument.getAutoGenerateNumber()) {
            currentDocument.setNumber(LegalDocument.getLegalDocumentNumber(currentDocument, "ED"));
        }
    }

    // tk seriously change this code and use current method for creating new object
    // Could use new department creation as example.
    public void createNewLegalDocument(ActionEvent action) {
        selectedDocumentId = currentDocument.getId();
        currentDocument = createNewLegalDocument(getEntityManager(), getUser());

        activeDocTabIndex = 0;
    }
    
    public void documentDialogReturn() {
        System.out.println("Doc dialog return...");
    }
    
    public void openDocumentBrowser() {
        // Add the Job Browser tab is 
        getMainTabView().addTab(getEntityManager(), "Document Browser", true);
        getMainTabView().select("Document Browser");
    }
    
    public MainTabView getMainTabView() {
        JobManager jm = Application.findBean("jobManager");

        return jm.getMainTabView();
    }

    public JobManagerUser getUser() {
        return getJobManager().getUser();
    }

    public LegalDocument createNewLegalDocument(EntityManager em,
            JobManagerUser user) {

        LegalDocument legalDocument = new LegalDocument();
        legalDocument.setAutoGenerateNumber(Boolean.TRUE);
        // department, employee & business office
        if (user != null) {
            if (user.getEmployee() != null) {
                legalDocument.setResponsibleOfficer(Employee.findEmployeeById(em, user.getEmployee().getId()));
                legalDocument.setResponsibleDepartment(Department.findDepartmentById(em, user.getEmployee().getDepartment().getId()));
            }
        }
        // externla client
        //legalDocument.setExternalClient(getDefaultClient(em, "--"));
        // default requesting department
        legalDocument.setRequestingDepartment(getDefaultDepartment(em, "--"));
        // submitted by
        //legalDocument.setSubmittedBy(getEmployeeByName(em, "--", "--"));
        // doc type
        legalDocument.setType(DocumentType.findDocumentTypeByName(em, "--"));
        // doc classification
        legalDocument.setClassification(Classification.findClassificationByName(em, "--"));
        // doc for
        legalDocument.setDocumentForm("H"); //
        // get number
        legalDocument.setNumber(LegalDocument.getLegalDocumentNumber(legalDocument, "ED"));

        return legalDocument;
    }

    public Department getDefaultDepartment(EntityManager em,
            String name) {
        Department department = Department.findDepartmentByName(em, name);

        if (department == null) {
            department = new Department();

            em.getTransaction().begin();
            department.setName(name);
            BusinessEntityUtils.saveBusinessEntity(em, department);
            em.getTransaction().commit();
        }

        return department;
    }

    public void fetchDepartment(ActionEvent action) {
    }

    public void fetchEmployee(ActionEvent action) {
    }

    public void exportDocumentReportTable() {
    }

    public LegalDocument getCurrentDocument() {
        if (currentDocument == null) {
            currentDocument = createNewLegalDocument(getEntityManager(), getUser());
        }

        return currentDocument;
    }

    public void setCurrentDocument(LegalDocument currentDocument) {
        this.currentDocument = currentDocument;
    }

    public LegalDocument getSelectedDocument() {
        return selectedDocument;
    }

    public void setSelectedDocument(LegalDocument selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public List<LegalDocument> getDocumentSearchResultList() {
        return documentSearchResultList;
    }

    public List<LegalDocument> getDocumentSearchByTypeResultList() {
        EntityManager em = getEntityManager();

        if (selectedDocument != null) {
            return LegalDocument.findLegalDocumentsByDateSearchField(em,
                    dateSearchField, "By type", selectedDocument.getType().getName(), // tk get from main search
                    startDate, endDate);
        } else {
            return new ArrayList<LegalDocument>();
        }
    }

    public void setDocumentSearchResultList(List<LegalDocument> documentSearchResultList) {
        this.documentSearchResultList = documentSearchResultList;
    }

    public Tab getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(Tab activeTab) {
        this.activeTab = activeTab;
    }

    public String getDateSearchField() {
        return dateSearchField;
    }

    public void setDateSearchField(String dateSearchField) {
        this.dateSearchField = dateSearchField;
    }

    public String getDateSearchPeriod() {
        return dateSearchPeriod;
    }

    public void setDateSearchPeriod(String dateSearchPeriod) {
        this.dateSearchPeriod = dateSearchPeriod;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getEndSearchDateDisabled() {
        return endSearchDateDisabled;
    }

    public void setEndSearchDateDisabled(Boolean endSearchDateDisabled) {
        this.endSearchDateDisabled = endSearchDateDisabled;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Long getSelectedDocumentId() {
        return selectedDocumentId;
    }

    public void setSelectedDocumentId(Long selectedDocumentId) {
        this.selectedDocumentId = selectedDocumentId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Boolean getStartSearchDateDisabled() {
        return startSearchDateDisabled;
    }

    public void setStartSearchDateDisabled(Boolean startSearchDateDisabled) {
        this.startSearchDateDisabled = startSearchDateDisabled;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public void updateSearchText() {
        if (searchType.equals("General")) {
            doLegalDocumentSearch();
        } else if (searchType.equals("My jobs")) {
            if (getUser() != null) {
                searchText = getUser().getEmployee().getLastName() + ", " + getUser().getEmployee().getFirstName();
                doLegalDocumentSearch();
            }
        } else if (searchType.equals("My department's jobs")) {
            if (getUser() != null) {
                searchText = getUser().getDepartment().getName();
                doLegalDocumentSearch();
            }
        }
    }

    public void doLegalDocumentSearch() {

        EntityManager em = getEntityManager();

        if (activeTabIndex == 0) {
            if (searchText != null) {
                documentSearchResultList = LegalDocument.findLegalDocumentsByDateSearchField(em,
                        dateSearchField, searchType, searchText.trim(),
                        startDate, endDate);
            } else { // get all documents based on common test ie "" for now
                documentSearchResultList = LegalDocument.findLegalDocumentsByDateSearchField(em,
                        dateSearchField, searchType, "",
                        startDate, endDate);
            }
        } else if (activeTabIndex == 1) { // report based search
            if (documentReport == null) { // get first listed report
                List<DocumentReport> reports = DocumentReport.findAllDocumentReports(em);
                if (reports != null) {
                    if (!reports.isEmpty()) {
                        documentReport = reports.get(0);
                    }
                }
            } else if (documentReport.getId() != null) {
                documentReport = DocumentReport.findDocumentReportById(em, documentReport.getId());
            } else { // get first report
                List<DocumentReport> reports = DocumentReport.findAllDocumentReports(em);
                if (reports != null) {
                    if (!reports.isEmpty()) {
                        documentReport = reports.get(0);
                    }
                }
            }

            // do actual report generation here
            if (documentReport.getShowNumberOfDocuments()) {// this means a report that involves grouping
                documentSearchResultList = LegalDocument.findGroupedLegalDocumentsByDateSearchField(em,
                        dateSearchField, searchType,
                        startDate, endDate);
            } else if (!documentReport.getShowNumberOfDocuments()) { // report that includes all documents within the specified dates
                documentSearchResultList = LegalDocument.findLegalDocumentsByDateSearchField(em,
                        dateSearchField, searchType, "",
                        startDate, endDate);
            }
        }
    }

    public List<BusinessOffice> getBusinessOffices() {
        return BusinessOffice.findAllBusinessOffices(getEntityManager());
    }

    public List<Department> getDepartments() {
        return Department.findAllDepartments(getEntityManager());
    }

    public List<DocumentType> getDocumentTypes() {
        return DocumentType.findAllDocumentTypes(getEntityManager());
    }

    public List<Classification> getClassifications() {
        return Classification.findAllClassifications(getEntityManager());
    }

    public List<DocumentReport> getDocumentReports() {
        return DocumentReport.findAllDocumentReports(getEntityManager());
    }

    public List<Employee> getEmployees() {
        List<Employee> employees = Employee.findAllEmployees(getEntityManager());
        List<Employee> some = new ArrayList<Employee>();

        //return jm.getAllEmployees(getEntityManager());
        for (int i = 0; i < employees.size() - 150; i++) {
            some.add(employees.get(i));
        }

        return some;
    }

    public int getActiveTabIndex() {
        return activeTabIndex;
    }

    public String getActiveTabForm() {
        return activeTabForm;
    }

    public void setActiveTabForm(String activeTabForm) {
        this.activeTabForm = activeTabForm;
    }

    public void setActiveTabIndex(int activeTabIndex) {
        this.activeTabIndex = activeTabIndex;
    }

    public String getSystemInfo() {
        return ""; // tk info to provide to be decided.
    }

    public List<String> completeSearchText(String query) {
        List<String> suggestions = new ArrayList<>();
//
//        if (searchType.equals("My department's jobs")) {
//            List<Department> department = jm.getAllDepartments(getEntityManager());
//            for (Department d : department) {
//                if (d.getName().trim().toUpperCase().startsWith(query.trim().toUpperCase())) {
//                    suggestions.add(d.getName());
//                }
//            }
//        }
//
        return suggestions;
    }

    // 
    private String getUserBasicAttributes(String username, LdapContext ctx) {
        String userdetails = null; // tk
        try {

            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String[] attrIDs = {"distinguishedName",
                "sn",
                "givenname",
                "mail",
                "telephonenumber"};
            constraints.setReturningAttributes(attrIDs);
            //First input parameter is search bas, it can be "CN=Users,DC=YourDomain,DC=com"
            //Second Attribute can be uid=username
            NamingEnumeration answer = ctx.search("DC=bos,DC=local", "sAMAccountName="
                    + username, constraints);
            if (answer.hasMore()) {
                Attributes attrs = ((SearchResult) answer.next()).getAttributes();
                System.out.println("distinguishedName " + attrs.get("distinguishedName"));
                System.out.println("givenname " + attrs.get("givenname"));
                System.out.println("sn " + attrs.get("sn"));
                System.out.println("mail " + attrs.get("mail"));
                System.out.println("telephonenumber " + attrs.get("telephonenumber"));
            } else {
                throw new Exception("Invalid User");
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }
        return userdetails;
    }

    public JobManager getJobManager() {
        if (jobManager == null) {
            jobManager = Application.findBean("jobManager");
        }
        return jobManager;
    }

    private boolean setupDatabaseConnection(String PU) {
        if (EMF == null) {
            try {
                EMF = Persistence.createEntityManagerFactory(PU);
                if (EMF.isOpen()) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception ex) {
                return false;
            }
        } else {
            return true;
        }
    }

    private EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    public Date getCurrentDate() {
        return new Date();
    }

    public void formatDocumentTableXLS(Object document, String headerTitle) {
        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);
        // get columns row
        int numCols = sheet.getRow(0).getPhysicalNumberOfCells();
//        // create heading row
        sheet.shiftRows(0, sheet.getLastRowNum(), 1);
//        sheet.createRow(0);

        HSSFRow header = sheet.getRow(0);
        HSSFFont headerFont = wb.createFont();
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        HSSFCellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        for (int i = 0; i < numCols; i++) {
            header.createCell(i);
            HSSFCell cell = header.getCell(i);
            cell.setCellStyle(headerCellStyle);
        }
        header.getCell(0).setCellValue(headerTitle);
        // merge header cells
        sheet.addMergedRegion(new CellRangeAddress(
                0, //first row
                (short) 0, //last row
                0, //first column
                (short) (numCols - 1) //last column
        ));

        // Column setup
        // get columns row
        HSSFRow cols = sheet.getRow(1);
//        HSSFRow cols = sheet.getRow(0);

        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        // set columns widths
        for (int i = 0; i < cols.getPhysicalNumberOfCells(); i++) {

            //if (i != 2) { // if not particulars column
            sheet.autoSizeColumn(i);
            //} else {
            if (sheet.getColumnWidth(i) > 15000) {
                sheet.setColumnWidth(i, 15000);
            }

            //}
        }
        // set columns cell style
        for (int i = 0; i < cols.getPhysicalNumberOfCells(); i++) {
            HSSFCell cell = cols.getCell(i);
            cell.setCellStyle(cellStyle);
        }
    }

    public void postProcessDocumentTableXLS(Object document) {
        formatDocumentTableXLS(document, "Document by group");
    }

    public void postProcessXLS(Object document) {
        formatDocumentTableXLS(document, documentReport.getName());
    }

    // tk put in db
    public List getPriorityLevels() {
        ArrayList levels = new ArrayList();

        levels.add(new SelectItem("--", "--"));
        levels.add(new SelectItem("High", "High"));
        levels.add(new SelectItem("Medium", "Medium"));
        levels.add(new SelectItem("Low", "Low"));
        levels.add(new SelectItem("Emergency", "Emergency"));

        return levels;
    }

    // tk put in db
    public List getStatuses() {
        ArrayList statuses = new ArrayList();

        statuses.add(new SelectItem("--", "--"));
        statuses.add(new SelectItem("Clarification required", "Clarification required"));
        statuses.add(new SelectItem("Completed", "Completed"));
        statuses.add(new SelectItem("On target", "On target"));
        statuses.add(new SelectItem("Transferred to Ministry", "Transferred to Ministry"));

        return statuses;
    }
}
