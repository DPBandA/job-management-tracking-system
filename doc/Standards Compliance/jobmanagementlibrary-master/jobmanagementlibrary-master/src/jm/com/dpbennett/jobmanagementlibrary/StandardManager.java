/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jobmanagementlibrary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import jm.com.dpbennett.entity.BusinessOffice;
import jm.com.dpbennett.entity.Classification;
import jm.com.dpbennett.entity.Client;
import jm.com.dpbennett.entity.Department;
import jm.com.dpbennett.entity.DocumentReport;
import jm.com.dpbennett.entity.DocumentStandard;
import jm.com.dpbennett.entity.DocumentType;
import jm.com.dpbennett.entity.Employee;
import jm.com.dpbennett.entity.JobManagerUser;
import jm.com.dpbennett.entity.LegalDocument;
import jm.com.dpbennett.utils.BusinessEntityUtils;
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
//import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author Desmond Bennett
 */
//@ManagedBean
//@SessionScoped
public class StandardManager implements Serializable {

    private EntityManagerFactory EMF;
    private JobManagement jm;
    private JobManagerUser user;
    private Boolean userLogggedIn = false;
    private String username = "";
    private String password = "";
    private Boolean showLogin = true;
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
    private List<DocumentStandard> documentSearchResultList;
    private DocumentStandard selectedDocument;
    private DocumentStandard currentDocument;
    private DocumentReport documentReport;
    private Boolean dirty;
    private String dialogMessage;
    private String dialogMessageHeader;
    private String dialogMessageSeverity;

    /**
     * Creates a new instance of StandardManager
     */
    public StandardManager() {
        jm = new JobManagement();
        activeTabIndex = 0;
        activeNavigationTabIndex = 0;
        activeTabForm = "";
        searchType = "General";
        dateSearchField = "dateEntered";
        dateSearchPeriod = "thisYear";
        searchTextVisible = true;
        dirty = false;
        changeSearchDatePeriod();
    }

    public void setDirty(Boolean dirty) {
        this.dirty = dirty;
    }

    private void promptToSaveIfRequired() {
        RequestContext context = RequestContext.getCurrentInstance();

        if (dirty) {
            setDirty(false);
            setDialogMessage("The form data was modified. Do you wish to save the modified data?");
            setDialogMessageHeader("Form Data Modified");
            setDialogMessageSeverity("info");
            context.update("saveConfirmationDialogForm");
            context.execute("saveConfirmationDialog.show();");
        }
    }

    public String getDialogMessage() {
        return dialogMessage;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }

    public String getDialogMessageHeader() {
        return dialogMessageHeader;
    }

    public void setDialogMessageHeader(String dialogMessageHeader) {
        this.dialogMessageHeader = dialogMessageHeader;
    }

    public String getDialogMessageSeverity() {
        return dialogMessageSeverity;
    }

    public void setDialogMessageSeverity(String dialogMessageSeverity) {
        this.dialogMessageSeverity = dialogMessageSeverity;
    }

    private EntityManagerFactory getEMF() {

        if (EMF == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            App app = (App) context.getApplication().evaluateExpressionGet(context, "#{App}", App.class);
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
        doDocumentSearch();
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
        doDocumentSearch();
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
            doDocumentSearch();
        }

    }

    public void handleStartSearchDateSelect(SelectEvent event) {
        startDate = (Date)event.getObject();
        doDocumentSearch();
    }

    public void handleEndSearchDateSelect(SelectEvent event) {
        endDate =(Date)event.getObject();
        doDocumentSearch();
    }

    public void handleKeepAlive() {
        EntityManager em = getEntityManager();

        System.out.println("Handling keep alive session: doing polling for DocumentStandard..." + new Date());
        if (user.getId() != null) {
            em.getTransaction().begin();
            user.setPollTime(new Date());
            BusinessEntityUtils.saveBusinessEntity(em, user);
            em.getTransaction().commit();
        }
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
        DocumentStandard document = em.find(DocumentStandard.class, selectedDocument.getId());
        em.remove(document);
        em.flush();
        em.getTransaction().commit();

        // do search to update search list.
        doDocumentSearch();
    }

    public void cancelDocumentEdit() {
        activeDocTabIndex = 0;
    }

    public void closeDocumentEdit() {
        activeDocTabIndex = 0;
        promptToSaveIfRequired();
    }

    public void editDocument(ActionEvent actionEvent) {
        if (currentDocument != null) {
            if (currentDocument.getId() != null) {
                selectedDocumentId = currentDocument.getId();
                currentDocument = DocumentStandard.findDocumentStandardById(getEntityManager(), currentDocument.getId());
            }
        }
    }

    public void editDocumentType(ActionEvent actionEvent) {
    }

    public void cancelDocumentTypeEdit(ActionEvent actionEvent) {
    }

    public void saveDocumentType(ActionEvent actionEvent) {
        System.out.println("Not implemented for document standard now");
//        EntityManager em = getEntityManager();
//
//        DocumentType type = jm.findDocumentTypeByNameAndCode(em, currentDocument.getType().getName(),
//                currentDocument.getType().getCode());
//        // if document already exist do not save
//        if (type != null) {
//            // update document number
//            if (currentDocument.getAutoGenerateNumber()) {
//                currentDocument.setNumber(jm.getLegalDocumentNumber(currentDocument, "ED"));
//            }
//            return;
//        } // if type with this name exists update the code and save
//        else if ((jm.getDocumentTypeByName(em, currentDocument.getType().getName()) != null)
//                || (jm.getDocumentTypeByCode(em, currentDocument.getType().getCode()) != null)) {
//            em.getTransaction().begin();
//            jm.saveBusinessEntity(em, currentDocument.getType());
//            em.getTransaction().commit();
//            // update document number
//            if (currentDocument.getAutoGenerateNumber()) {
//                currentDocument.setNumber(jm.getLegalDocumentNumber(currentDocument, "ED"));
//            }
//
//            return;
//        } else {
//            em.getTransaction().begin();
//            currentDocument.getType().setId(null); // forces saving of new type.
//            jm.saveBusinessEntity(em, currentDocument.getType());
//            em.getTransaction().commit();
//
//            // update document number
//            if (currentDocument.getAutoGenerateNumber()) {
//                currentDocument.setNumber(jm.getLegalDocumentNumber(currentDocument, "ED"));
//            }
//        }
    }

    public void saveCurrentDocument() {
        EntityManager em = getEntityManager();
        RequestContext context = RequestContext.getCurrentInstance();

        try {

            // validate required values.
//            if (currentDocument.getNumber() == null) {
//                context.addCallbackParam("valueRequired", true);
//                return;
//            } else if (currentDocument.getNumber().trim().equals("")) {
//                context.addCallbackParam("valueRequired", true);
//                return;
//            } else {
//                context.addCallbackParam("valueRequired", false);
//            }

            // get needed objects
            // get objects
//            currentDocument.setType(jm.getDocumentTypeById(em, currentDocument.getType().getId()));
            currentDocument.setType(DocumentType.findDocumentTypeByName(em, currentDocument.getType().getName()));

//            currentDocument.setClassification(jm.getClassificationById(em, currentDocument.getClassification().getId()));
            currentDocument.setClassification(Classification.findClassificationByName(em, currentDocument.getClassification().getName()));

            if (currentDocument.getDateEntered() == null) {
                currentDocument.setDateEntered(new Date());
            }

            if (dirty) {
                currentDocument.setDateEdited(new Date());
                // Get employee for later use
                Employee employee = Employee.findEmployeeById(em, getUser().getEmployee().getId());
                currentDocument.setEditedBy(employee);
            }

            // save client and doc
            em.getTransaction().begin();
            BusinessEntityUtils.saveBusinessEntity(em, currentDocument);
            em.getTransaction().commit();
            // redo search
            doDocumentSearch();
            // reset to first first doc tab
            activeDocTabIndex = 0;



            setDirty(false);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public List<Client> completeClient(String query) { // tk put in job management lib
        try {
            List<Client> clients = Client.findClientsByName(getEntityManager(), query);
            List<Client> suggestions = new ArrayList<Client>();

            // find matching clients
            for (Client client : clients) {

                client.getName();
                suggestions.add(client);
            }

            return suggestions;
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<Client>();
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

    public void updateType(SelectEvent event) {
        System.out.println(currentDocument.getType().getName());
    }

    public void updateCode(SelectEvent event) {
    }

    public void updateDocument() {
        setDirty(true);
    }

    public void updateAutoGenerateNumber() {
    }

    /**
     * Updates the document type and then update the document number which is
     * partly based on the document type.
     */
    public void updateDocumentType() {
        currentDocument.setType(DocumentType.findDocumentTypeByName(getEntityManager(), currentDocument.getType().getName()));
        setDirty(true);
    }

    public void updateDocumentClassification() {
//        if (currentDocument.getType().getName() != null) {
        System.out.println("class.: " + currentDocument.getClassification().getName());
        currentDocument.setClassification(Classification.findClassificationByName(getEntityManager(), currentDocument.getClassification().getName()));
//            if (currentDocument.getAutoGenerateNumber()) {
//                currentDocument.setNumber(jm.getLegalDocumentNumber(currentDocument, "ED"));
//            }
//        }
    }

    public void updateDocumentReport() {
        if (documentReport.getId() != null) {
            documentReport = DocumentReport.findDocumentReportById(getEntityManager(), documentReport.getId());
            System.out.println("report cols to exclude: " + documentReport.getColumnsToExclude());
            doDocumentSearch();
        }
    }

    public void updateDocumentForm() {
//        if (currentDocument.getAutoGenerateNumber()) {
//            currentDocument.setNumber(jm.getLegalDocumentNumber(currentDocument, "ED"));
//        }
    }

    public void createNewDocumentStandard() {
        selectedDocumentId = currentDocument.getId();
        currentDocument = jm.createNewDocumentStandard(getEntityManager(), user);

        activeDocTabIndex = 0;
    }

    public DocumentStandard getCurrentDocument() {
        if (currentDocument == null) {
            currentDocument = jm.createNewDocumentStandard(getEntityManager(), user);
        }

        return currentDocument;
    }

    public void setCurrentDocument(DocumentStandard currentDocument) {
        this.currentDocument = currentDocument;
    }

    public DocumentStandard getSelectedDocument() {
        return selectedDocument;
    }

    public void setSelectedDocument(DocumentStandard selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public List<DocumentStandard> getDocumentSearchResultList() {
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

    public void setDocumentSearchResultList(List<DocumentStandard> documentSearchResultList) {
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
            doDocumentSearch();
        } else if (searchType.equals("My jobs")) {
            if (user != null) {
                searchText = user.getEmployee().getLastName() + ", " + user.getEmployee().getFirstName();
                doDocumentSearch();
            }
        } else if (searchType.equals("My department's jobs")) {
            if (user != null) {
                searchText = user.getDepartment().getName();
                doDocumentSearch();
            }
        }
    }

    public void doDocumentSearch() {

        EntityManager em = getEntityManager();

        if (activeTabIndex == 0) {
            if (searchText != null) {
                documentSearchResultList = DocumentStandard.findDocumentStandardsByDateSearchField(em,
                        dateSearchField, searchType, searchText.trim(),
                        startDate, endDate);
            } else { // get all documents based on common test ie "" for now
                documentSearchResultList = DocumentStandard.findDocumentStandardsByDateSearchField(em,
                        dateSearchField, searchType, "",
                        startDate, endDate);
            }
        }
//        else if (activeTabIndex == 1) { // report based search
//            if (documentReport == null) { // get first listed report
//                List<DocumentReport> reports = jm.getAllDocumentReports(em);
//                if (reports != null) {
//                    if (!reports.isEmpty()) {
//                        documentReport = reports.get(0);
//                    }
//                }
//            } else if (documentReport.getId() != null) {
//                documentReport = jm.getDocumentReportById(em, documentReport.getId());
//            } else { // get first report
//                List<DocumentReport> reports = jm.getAllDocumentReports(em);
//                if (reports != null) {
//                    if (!reports.isEmpty()) {
//                        documentReport = reports.get(0);
//                    }
//                }
//            }

        // do actual report generation here
//            if (documentReport.getShowNumberOfDocuments()) {// this means a report that involves grouping
////                documentSearchResultList = jm.findGroupedLegalDocumentsByDateSearchField(em,
////                        dateSearchField, searchType,
////                        startDate, endDate);
//            } else if (!documentReport.getShowNumberOfDocuments()) { // report that includes all documents within the specified dates
////                documentSearchResultList = jm.findLegalDocumentsByDateSearchField(em,
////                        dateSearchField, searchType, "",
////                        startDate, endDate);
//            }
//        }
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

    public void setUser(JobManagerUser user) {
        this.user = user;
    }

    public List<String> completeSearchText(String query) {
        List<String> suggestions = new ArrayList<String>();

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
            ex.printStackTrace();
        }
        return userdetails;
    }

    public JobManagerUser getUser() {
        return user;
    }

    public void login(ActionEvent actionEvent) {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Invalid credentials");

        if ((getUsername() != null) && (getPassword() != null)) {
            if (jm.validateUser(getUsername(), getPassword())) {
//            if (true) { // tk NB: REMOVE!!!!
                // get the job manager user if one exists and
                // facilitate the display of the employee dialog for entry if it
                // does not exist
                setUser(JobManagerUser.findJobManagerUserByUsername(getEntityManager(), getUsername()));
                if (getUser() != null) {
                    // tk may have to check for valid passwrd here
                    setUserLogggedIn(true);
                    setShowLogin(false);
                    msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome",
                            getUser().getUserFirstname() + " " + getUser().getUserLastname());
                    context.addCallbackParam("userExists", true);
                    doDocumentSearch();
                } else {
                    setUserLogggedIn(false);
                    setShowLogin(true);
                    context.addCallbackParam("userExists", false);
                }

            } else {
                setUserLogggedIn(false);
                setShowLogin(true);
            }
        } else {
            setUserLogggedIn(false);
            setShowLogin(true);
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("userLogggedIn", getUserLogggedIn());
    }

    public void logout() {
        user = null;
        userLogggedIn = false;
        username = "";
        password = "";
        showLogin = true;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getShowLogin() {
        return showLogin;
    }

    public void setShowLogin(Boolean showLogin) {
        this.showLogin = showLogin;
    }

    public Boolean getUserLogggedIn() {
        return userLogggedIn;
    }

    public void setUserLogggedIn(Boolean userLogggedIn) {
        this.userLogggedIn = userLogggedIn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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
}
