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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.Classification;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.DatePeriod;
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
public class LegalDocumentManager implements Serializable {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF;
    private int activeTabIndex;
    private int activeDocTabIndex;
    private int activeNavigationTabIndex;
    private String activeTabForm;
    private Tab activeTab;
    private String dateSearchField;
    private DatePeriod datePeriod;
    private String searchType;
    private String searchText;
    private String previousSearchText;
    private Long selectedDocumentId;
    private List<LegalDocument> documentSearchResultList;
    private LegalDocument selectedDocument;
    private LegalDocument currentDocument;
    private DocumentType currentDocumentType;
    private DocumentReport documentReport;
    @Column(length = 1024)
    private String status;
    private String priorityLevel;
    // Managers
    private JobManager jobManager;
    private ClientManager clientManager;

    /**
     * Creates a new instance of JobManager
     */
    public LegalDocumentManager() {
        activeTabIndex = 0;
        activeNavigationTabIndex = 0;
        activeTabForm = "";
        searchType = "General";
        dateSearchField = "dateReceived";
        // tk to be set back to month
        datePeriod = new DatePeriod("This year", "year", null, null, false, false, false);
        //datePeriod = new DatePeriod("This month", "month", null, null, false, false, false);
        changeSearchDatePeriod();

    }

    public DocumentType getCurrentDocumentType() {
        return currentDocumentType;
    }

    public void setCurrentDocumentType(DocumentType currentDocumentType) {
        this.currentDocumentType = currentDocumentType;
    }

    public Boolean getIsClientNameValid() {
        return BusinessEntityUtils.validateName(getCurrentDocument().getExternalClient().getName());
    }

    public void editExternalClient() {

        getClientManager().setSelectedClient(getCurrentDocument().getExternalClient());
        getClientManager().setIsClientNameAndIdEditable(getUser().getPrivilege().getCanAddClient());

        PrimeFacesUtils.openDialog(null, "/client/clientDialog", true, true, true, 450, 700);
    }

    public void externalClientDialogReturn() {
        if (getClientManager().getSelectedClient().getId() != null) {
            getCurrentDocument().setExternalClient(getClientManager().getSelectedClient());
        }
    }

    public void createNewExternalClient() {
        getClientManager().createNewClient(true);
        getClientManager().setIsClientNameAndIdEditable(getUser().getPrivilege().getCanAddClient());

        PrimeFacesUtils.openDialog(null, "/client/clientDialog", true, true, true, 450, 700);
    }

    public DatePeriod getDatePeriod() {
        return datePeriod;
    }

    public void setDatePeriod(DatePeriod datePeriod) {
        this.datePeriod = datePeriod;
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

//    public Boolean getSearchTextVisible() {
//        return searchTextVisible;
//    }
//
//    public void setSearchTextVisible(Boolean searchTextVisible) {
//        this.searchTextVisible = searchTextVisible;
//    }
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
            //searchTextVisible = true;
        } else if (tabTitle.equals("Reporting")) {
//            activeNavigationTabIndex = 1;
            activeTabIndex = 1;
            previousSearchText = searchText;
            // do search with the default search text used in doLegalDocumentSearch()
            searchText = "";
            //searchTextVisible = false;
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
        getDatePeriod().initDatePeriod();
    }

    public void handleStartSearchDateSelect(SelectEvent event) {
        getDatePeriod().setStartDate((Date) event.getObject());
        doLegalDocumentSearch();
    }

    public void handleEndSearchDateSelect(SelectEvent event) {
        getDatePeriod().setEndDate((Date) event.getObject());
        doLegalDocumentSearch();
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
        currentDocumentType = getCurrentDocument().getType();
    }

    public void createNewDocumentType(ActionEvent actionEvent) {
        currentDocumentType = new DocumentType();
    }

    /**
     * Save update document number. If this is a new type set the document type 
     * to a "blank" type so he that the new type can be selected from the autocomplete component.
     * @param actionEvent 
     */
    public void saveDocumentType(ActionEvent actionEvent) {

       if (getCurrentDocumentType().getId() == null) {
            getCurrentDocumentType().save(getEntityManager());
            currentDocument.setType(new DocumentType());
        }
        else {
            getCurrentDocumentType().save(getEntityManager());
            currentDocument.setType(getCurrentDocumentType());
        }

        if (currentDocument.getAutoGenerateNumber()) {
            currentDocument.setNumber(LegalDocument.getLegalDocumentNumber(currentDocument, "ED"));
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

    public List<String> completeTypeName(String query) {

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
    }

    public void updateOfficerResponsible(SelectEvent event) {
    }

    public void updateType(SelectEvent event) {
    }

    public void updateCode(SelectEvent event) {
    }

    public void updateClient(SelectEvent event) {
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
            // tk doc # to be created and implemented as an interface
            currentDocument.setNumber(LegalDocument.getLegalDocumentNumber(currentDocument, "ED"));
        }
    }

    public void updateDeliveryDate() {
    }

    public void updateExpectedDeliveryDate() {
    }

    public void updateDateReceived() {
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
    }

    /**
     * Updates the document type and then update the document number which is
     * partly based on the document type.
     */
    public void updateDocumentType() {

        if (currentDocument.getAutoGenerateNumber()) {
            currentDocument.setNumber(LegalDocument.getLegalDocumentNumber(currentDocument, "ED"));
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

    // tk remove use of default employee, department etc and just create 
    // new blank objects
    public LegalDocument createNewLegalDocument(EntityManager em,
            JobManagerUser user) {

        LegalDocument legalDocument = new LegalDocument();
        legalDocument.setAutoGenerateNumber(Boolean.TRUE);
        // department, employee & business office
//        if (getUser() != null) {
//            if (getUser().getEmployee() != null) {
//                legalDocument.setResponsibleOfficer(Employee.findEmployeeById(em, getUser().getEmployee().getId()));
//                legalDocument.setResponsibleDepartment(Department.findDepartmentById(em, getUser().getEmployee().getDepartment().getId()));
//            }
//        }

        // tk
        legalDocument.setResponsibleOfficer(Employee.findDefaultEmployee(getEntityManager(), "--", "--", true));
        legalDocument.setResponsibleDepartment(Department.findDefaultDepartment(em, "--"));

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
                    getDatePeriod().getStartDate(), getDatePeriod().getEndDate());
        } else {
            return new ArrayList<>();
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

//        if (activeTabIndex == 0) {
        if (searchText != null) {
            documentSearchResultList = LegalDocument.findLegalDocumentsByDateSearchField(em,
                    dateSearchField, searchType, searchText.trim(),
                    getDatePeriod().getStartDate(), getDatePeriod().getEndDate());
        } else { // get all documents based on common test ie "" for now
            documentSearchResultList = LegalDocument.findLegalDocumentsByDateSearchField(em,
                    dateSearchField, searchType, "",
                    getDatePeriod().getStartDate(), getDatePeriod().getEndDate());
        }
//        } else if (activeTabIndex == 1) { // report based search
//            if (documentReport == null) { // get first listed report
//                List<DocumentReport> reports = DocumentReport.findAllDocumentReports(em);
//                if (reports != null) {
//                    if (!reports.isEmpty()) {
//                        documentReport = reports.get(0);
//                    }
//                }
//            } else if (documentReport.getId() != null) {
//                documentReport = DocumentReport.findDocumentReportById(em, documentReport.getId());
//            } else { // get first report
//                List<DocumentReport> reports = DocumentReport.findAllDocumentReports(em);
//                if (reports != null) {
//                    if (!reports.isEmpty()) {
//                        documentReport = reports.get(0);
//                    }
//                }
//            }
//
//            // do actual report generation here
//            if (documentReport.getShowNumberOfDocuments()) {// this means a report that involves grouping
//                documentSearchResultList = LegalDocument.findGroupedLegalDocumentsByDateSearchField(em,
//                        dateSearchField, searchType,
//                        getDatePeriod().getStartDate(), getDatePeriod().getEndDate());
//            } else if (!documentReport.getShowNumberOfDocuments()) { // report that includes all documents within the specified dates
//                documentSearchResultList = LegalDocument.findLegalDocumentsByDateSearchField(em,
//                        dateSearchField, searchType, "",
//                        getDatePeriod().getStartDate(), getDatePeriod().getEndDate());
//            }
//        }

        openDocumentBrowser();
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

    public JobManager getJobManager() {
        if (jobManager == null) {
            jobManager = Application.findBean("jobManager");
        }
        return jobManager;
    }

    public ClientManager getClientManager() {
        if (clientManager == null) {
            clientManager = Application.findBean("clientManager");
        }

        return clientManager;
    }

    private EntityManager getEntityManager() {
        return EMF.createEntityManager();
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
