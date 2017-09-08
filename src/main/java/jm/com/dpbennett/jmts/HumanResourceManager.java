/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import jm.com.dpbennett.business.entity.BusinessOffice;
import jm.com.dpbennett.business.entity.Classification;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.Department;
import jm.com.dpbennett.business.entity.DocumentReport;
import jm.com.dpbennett.business.entity.DocumentSequenceNumber;
import jm.com.dpbennett.business.entity.DocumentTracking;
import jm.com.dpbennett.business.entity.DocumentType;
import jm.com.dpbennett.business.entity.Employee;
import jm.com.dpbennett.business.entity.Internet;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.utils.BusinessEntityUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.component.tabview.Tab;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author Desmond Bennett
 */
@ManagedBean
@SessionScoped
public class HumanResourceManager implements Serializable {

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
    private List<DocumentTracking> documentSearchResultList;
    private DocumentTracking selectedDocument;
    private DocumentTracking currentDocument;
    private DocumentReport documentReport;
    @Column(length = 1024)
    private String status;
    private String priorityLevel;
    private Boolean dirty;
    private Main main;

    /**
     * Creates a new instance of JobManager
     */
    public HumanResourceManager() {
        activeTabIndex = 0;
        activeNavigationTabIndex = 0;
        activeTabForm = "";
        searchType = "General";
        dateSearchField = "dateReceived";
        dateSearchPeriod = "thisMonth";
        searchTextVisible = true;
        changeSearchDatePeriod();
    }

    public Main getMain() {
        if (main == null) {
            main = App.findBean("main");
        }
        return main;
    }

    public void login(ActionEvent actionEvent) {
    }

    public JobManagerUser getUser() {
        return getMain().getUser();
    }

    public void setDirty(Boolean dirty) {
        this.dirty = dirty;
    }

    public Boolean isDirty() {
        return dirty;
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

    private EntityManagerFactory getEMF() {

        if (EMF == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            JMTSApp app = (JMTSApp) context.getApplication().evaluateExpressionGet(context, "#{JMTSApp}", JMTSApp.class);
            EMF = app.getEMF();
        }

        return EMF;
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
            activeTabIndex = 0;
            searchText = previousSearchText;
            searchTextVisible = true;
        } else if (tabTitle.equals("Reporting")) {
            activeTabIndex = 1;
            previousSearchText = searchText;
            // do search with the default search text used in doDocumentTrackingSearch()
            searchText = "";
            searchTextVisible = false;
        }
        doDocumentTrackingSearch();
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
        doDocumentTrackingSearch();
    }

    public final void changeSearchDatePeriod() {

        if (dateSearchPeriod.equals("custom")) {
            setStartSearchDateDisabled(false);
            setEndSearchDateDisabled(false);
        } else if (dateSearchPeriod.equals("thisYear")) {
            startDate = getStartOfCurrentYear();
            endDate = getEndOfCurrentYear();
            setStartSearchDateDisabled(true);
            setEndSearchDateDisabled(true);
        } else if (dateSearchPeriod.equals("thisMonth")) {
            startDate = getStartOfCurrentMonth();
            endDate = getEndOfCurrentMonth();
            setStartSearchDateDisabled(true);
            setEndSearchDateDisabled(true);
        }
        if (EMF != null) {
            doDocumentTrackingSearch();
        }

    }

    public void handleStartSearchDateSelect(SelectEvent event) {
        startDate = (Date) event.getObject();
        doDocumentTrackingSearch();
    }

    public void handleEndSearchDateSelect(SelectEvent event) {
        endDate = (Date) event.getObject();
        doDocumentTrackingSearch();
    }

    public void handleKeepAlive() {
        EntityManager em = getEntityManager();

        System.out.println("Handling keep alive session: doing polling for HRDB..." + new Date());
        em.getTransaction().begin();
        main.setUser(JobManagerUser.findJobManagerUserByUsername(em, main.getUser().getUsername()));
        main.getUser().setPollTime(new Timestamp(new Date().getTime()));

        for (JobManagerUser u : JobManagerUser.findAllJobManagerUsers(em)) {
            em.refresh(u);
        }
        BusinessEntityUtils.saveBusinessEntity(em, main.getUser());

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
        DocumentTracking document = em.find(DocumentTracking.class, selectedDocument.getId());
        em.remove(document);
        em.flush();
        em.getTransaction().commit();

        // do search to update search list.
        doDocumentTrackingSearch();
    }

    public void cancelDocumentEdit(ActionEvent actionEvent) {
        setDirty(false);
        activeDocTabIndex = 0;
    }

    public void editDocument(ActionEvent actionEvent) {
        if (currentDocument != null) {
            if (currentDocument.getId() != null) {
                selectedDocumentId = currentDocument.getId();
                currentDocument = DocumentTracking.findDocumentTrackingById(getEntityManager(), currentDocument.getId());
            }
        }
    }

    public void editDocumentType(ActionEvent actionEvent) {
    }

    public void cancelDocumentTypeEdit(ActionEvent actionEvent) {
    }

    public void saveDocumentType(ActionEvent actionEvent) {
        EntityManager em = getEntityManager();

        DocumentType type = DocumentType.findDocumentTypeByNameAndCode(em, currentDocument.getType().getName(),
                currentDocument.getType().getCode());
        // if document already exist do not save
        if (type != null) {
            // update document number
            if (currentDocument.getAutoGenerateNumber()) {
                currentDocument.setNumber(DocumentTracking.getDocumentTrackingNumber(currentDocument, "ED"));
            }

        } // if type with this name exists update the code and save
        else if ((DocumentType.findDocumentTypeByName(em, currentDocument.getType().getName()) != null)
                || (DocumentType.findDocumentTypeByCode(em, currentDocument.getType().getCode()) != null)) {
            em.getTransaction().begin();
            BusinessEntityUtils.saveBusinessEntity(em, currentDocument.getType());
            em.getTransaction().commit();
            // update document number
            if (currentDocument.getAutoGenerateNumber()) {
                currentDocument.setNumber(DocumentTracking.getDocumentTrackingNumber(currentDocument, "ED"));
            }


        } else {
            em.getTransaction().begin();
            currentDocument.getType().setId(null); // forces saving of new type.
            BusinessEntityUtils.saveBusinessEntity(em, currentDocument.getType());
            em.getTransaction().commit();

            // update document number
            if (currentDocument.getAutoGenerateNumber()) {
                currentDocument.setNumber(DocumentTracking.getDocumentTrackingNumber(currentDocument, "ED"));
            }
        }
    }

    public void saveCurrentDocumentTracking(ActionEvent actionEvent) {
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

            // Flag as edited   
            if (isDirty()) {
                Employee employee = Employee.findEmployeeById(em, getUser().getEmployee().getId());
                currentDocument.setEditedBy(employee);
            }

            saveDocumentTracking(em, currentDocument);
            em.getTransaction().commit();
            // redo search
            doDocumentTrackingSearch();
            // reset to first first doc tab
            activeDocTabIndex = 0;
            // add callback param
            context.addCallbackParam("legalDocumentSaved", true);
        } catch (Exception e) {
            System.out.println(e);
            context.addCallbackParam("legalDocumentSaved", false);
        }
    }

    public DocumentTracking saveDocumentTracking(EntityManager em, DocumentTracking legalDocument) {

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
            legalDocument.setNumber(DocumentTracking.getDocumentTrackingNumber(legalDocument, "ED"));
        }
        BusinessEntityUtils.saveBusinessEntity(em, legalDocument);

        return legalDocument;
    }

    public List<Client> completeClient(String query) { // tk put in job management lib
        try {
            List<Client> clients = Client.findActiveClientsByFirstPartOfName(getEntityManager(), query);
            List<Client> suggestions = new ArrayList<>();

            // find matching clients
            for (Client client : clients) {

                //client.getName().replaceAll("&", "&#38;"); // tk needed??
                suggestions.add(client);
            }

            return suggestions;
        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List<Employee> completeEmployee(String query) {

        try {
            List<Employee> employees = Employee.findEmployeesByName(getEntityManager(), query);

            if (employees != null) {
                return employees;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public List<String> completeType(String query) {

        try {
            List<DocumentType> types = DocumentType.findDocumentTypesByName(getEntityManager(), query);
            List<String> suggestions = new ArrayList<>();
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

            return new ArrayList<>();
        }
    }

    public List<String> completeCode(String query) {

        try {
            List<DocumentType> types = DocumentType.findDocumentTypesByCode(getEntityManager(), query);
            List<String> suggestions = new ArrayList<>();
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

            return new ArrayList<>();
        }
    }

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
        setDirty(true);
    }

    public void updateOfficerResponsible(SelectEvent event) {
        currentDocument.setResponsibleOfficer(getEmployeeByName(currentDocument.getResponsibleOfficer(), currentDocument.getResponsibleOfficer().getName()));

        setDirty(true);
    }

    public void updateType(SelectEvent event) {
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

            setDirty(true);
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
            } else if (id == 0L) {
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
        doDocumentTrackingSearch();
    }

    public void updateDocument() {
        if (currentDocument.getAutoGenerateNumber()) {
            currentDocument.setNumber(DocumentTracking.getDocumentTrackingNumber(currentDocument, "ED"));
        }
        setDirty(true);
    }

    public void updateDeliveryDate(SelectEvent event) {
        currentDocument.setDateOfCompletion((Date) event.getObject());
        setDirty(true);
    }

    public void updateExpectedDeliveryDate(SelectEvent event) {
        currentDocument.setExpectedDateOfCompletion((Date) event.getObject());
        setDirty(true);
    }

    public void updateDateReceived(SelectEvent event) {
        currentDocument.setDateReceived((Date) event.getObject());
        Calendar c = Calendar.getInstance();

        // set new month received
        c.setTime(currentDocument.getDateReceived());
        currentDocument.setMonthReceived(c.get(Calendar.MONTH));
        currentDocument.setYearReceived(c.get(Calendar.YEAR));
        if (currentDocument.getAutoGenerateNumber()) {
            currentDocument.setNumber(DocumentTracking.getDocumentTrackingNumber(currentDocument, "ED"));
        }

        setDirty(true);
    }

    public void updateAutoGenerateNumber() {
        setDirty(true);
    }

    public void updateDepartmentResponsible() {
        if (currentDocument.getResponsibleDepartment().getId() != null) {
            currentDocument.setResponsibleDepartment(Department.findDepartmentById(getEntityManager(),
                    currentDocument.getResponsibleDepartment().getId()));
            if (currentDocument.getAutoGenerateNumber()) {
                currentDocument.setNumber(DocumentTracking.getDocumentTrackingNumber(currentDocument, "ED"));
            }

            setDirty(true);
        }
    }

    public void updateResquestingDepartment() {
        if (currentDocument.getRequestingDepartment().getId() != null) {
            currentDocument.setRequestingDepartment(Department.findDepartmentById(getEntityManager(),
                    currentDocument.getRequestingDepartment().getId()));
        }

        setDirty(true);
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
                currentDocument.setNumber(DocumentTracking.getDocumentTrackingNumber(currentDocument, "ED"));
            }

            setDirty(true);
        }
    }

    public void updateDocumentReport() {
        if (documentReport.getId() != null) {
            documentReport = DocumentReport.findDocumentReportById(getEntityManager(), documentReport.getId());
            doDocumentTrackingSearch();
        }
    }

    public void updateDocumentForm() {
        if (currentDocument.getAutoGenerateNumber()) {
            currentDocument.setNumber(DocumentTracking.getDocumentTrackingNumber(currentDocument, "ED"));
            setDirty(true);
        }
    }

    public void createNewDocumentTracking(ActionEvent action) {
        selectedDocumentId = currentDocument.getId();
        currentDocument = createNewDocumentTracking(getEntityManager(), getUser());

        activeDocTabIndex = 0;
    }

    public DocumentTracking createNewDocumentTracking(EntityManager em,
            JobManagerUser user) {

        DocumentTracking legalDocument = new DocumentTracking();
        legalDocument.setAutoGenerateNumber(Boolean.TRUE);
        // department, employee & business office
        if (getUser().getId() != null) {
            if (user.getEmployee().getId() != null) {
                legalDocument.setResponsibleOfficer(Employee.findEmployeeById(em, user.getEmployee().getId()));
                legalDocument.setResponsibleDepartment(Department.findDepartmentById(em, user.getEmployee().getDepartment().getId()));
//                legalDocument.setResponsibleDepartment(getDefaultDepartment(em, "--"));
            }
        }
        // externla client        
        // default requesting department
        legalDocument.setRequestingDepartment(getDefaultDepartment(em, "--"));
        // submitted by
        // doc type
        legalDocument.setType(DocumentType.findDocumentTypeByName(em, "--"));
        // doc classification
        legalDocument.setClassification(Classification.findClassificationByName(em, "--"));
        // doc for
        legalDocument.setDocumentForm("H");
        // get number
        legalDocument.setNumber(DocumentTracking.getDocumentTrackingNumber(legalDocument, "ED"));

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

    public DocumentTracking getCurrentDocument() {
        if (currentDocument == null) {
            currentDocument = createNewDocumentTracking(getEntityManager(), getUser());
        }

        return currentDocument;
    }

    public void setCurrentDocument(DocumentTracking currentDocument) {
        this.currentDocument = currentDocument;
    }

    public DocumentTracking getSelectedDocument() {
        return selectedDocument;
    }

    public void setSelectedDocument(DocumentTracking selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public List<DocumentTracking> getDocumentSearchResultList() {
        return documentSearchResultList;
    }

    public List<DocumentTracking> getDocumentSearchByTypeResultList() {
        EntityManager em = getEntityManager();


        if (selectedDocument != null) {
            return DocumentTracking.findDocumentTrackingsByDateSearchField(em,
                    dateSearchField, "By type", selectedDocument.getType().getName(), // tk get from main search
                    startDate, endDate);
        } else {
            return new ArrayList<>();
        }
    }

    public void setDocumentSearchResultList(List<DocumentTracking> documentSearchResultList) {
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
            doDocumentTrackingSearch();
        } else if (searchType.equals("My jobs")) {
            if (getUser() != null) {
                searchText = getUser().getEmployee().getLastName() + ", " + getUser().getEmployee().getFirstName();
                doDocumentTrackingSearch();
            }
        } else if (searchType.equals("My department's jobs")) {
            if (getUser() != null) {
                searchText = getUser().getDepartment().getName();
                doDocumentTrackingSearch();
            }
        }
    }

    public void doDocumentTrackingSearch() {

        EntityManager em = getEntityManager();

        if (activeTabIndex == 0) {
            if (searchText != null) {
                documentSearchResultList = DocumentTracking.findDocumentTrackingsByDateSearchField(em,
                        dateSearchField, searchType, searchText.trim(),
                        startDate, endDate);
            } else { // get all documents based on common test ie "" for now
                documentSearchResultList = DocumentTracking.findDocumentTrackingsByDateSearchField(em,
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
                documentSearchResultList = DocumentTracking.findGroupedDocumentTrackingsByDateSearchField(em,
                        dateSearchField, searchType,
                        startDate, endDate);
            } else if (!documentReport.getShowNumberOfDocuments()) { // report that includes all documents within the specified dates
                documentSearchResultList = DocumentTracking.findDocumentTrackingsByDateSearchField(em,
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
        List<Employee> some = new ArrayList<>();

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

        return suggestions;
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
        return getEMF().createEntityManager();
    }

    public Date getCurrentDate() {
        return new Date();
    }

    public void formatDocumentTableXLS(Object document, String headerTitle) {
        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);

        int numCols = sheet.getRow(0).getPhysicalNumberOfCells();
        sheet.shiftRows(0, sheet.getLastRowNum(), 1);

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

        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        // set columns widths
        for (int i = 0; i < cols.getPhysicalNumberOfCells(); i++) {

            sheet.autoSizeColumn(i);

            if (sheet.getColumnWidth(i) > 15000) {
                sheet.setColumnWidth(i, 15000);
            }

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
    public List<SelectItem> getPriorityLevels() {
        ArrayList levels = new ArrayList();

        levels.add(new SelectItem("--", "--"));
        levels.addAll(App.getStringListAsSelectItems(getEntityManager(), "priorityLevelList"));

        return levels;
    }

    // tk put in db
    public List<SelectItem> getStatuses() {
        ArrayList statuses = new ArrayList();

        statuses.add(new SelectItem("--", "--"));
        
        statuses.addAll(App.getStringListAsSelectItems(getEntityManager(), "documentStatusList"));

        return statuses;
    }
}
