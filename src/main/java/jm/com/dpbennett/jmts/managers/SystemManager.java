/*
Job Management & Tracking System (JMTS) 
Copyright (C) 2017  D P Bennett & Associates Limited

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

Email: info@dpbennett.com.jm
 */
package jm.com.dpbennett.jmts.managers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.BusinessOffice;
import jm.com.dpbennett.business.entity.Classification;
import jm.com.dpbennett.business.entity.Department;
import jm.com.dpbennett.business.entity.DocumentReport;
import jm.com.dpbennett.business.entity.DocumentStandard;
import jm.com.dpbennett.business.entity.DocumentType;
import jm.com.dpbennett.business.entity.Employee;
import jm.com.dpbennett.business.entity.JobCategory;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.JobSubCategory;
import jm.com.dpbennett.business.entity.LdapContext;
import jm.com.dpbennett.business.entity.Sector;
import jm.com.dpbennett.business.entity.SystemOption;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.wal.utils.BeanUtils;
import jm.com.dpbennett.wal.utils.Utils;
import jm.com.dpbennett.wal.utils.MainTabView;
import jm.com.dpbennett.wal.utils.PrimeFacesUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author Desmond Bennett
 */
public class SystemManager implements Serializable {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF;
    private JobManagerUser user;
    private int activeTabIndex;
    private int activeNavigationTabIndex;
    private String activeTabForm;
    private Tab activeTab;
    private String dateSearchField;
    private String dateSearchPeriod;
    private String searchType;
    private Boolean startSearchDateDisabled;
    private Boolean endSearchDateDisabled;
    private Boolean privilegeValue;
    private Boolean searchTextVisible;
    private Boolean isActiveUsersOnly;
    private Boolean isActiveClassificationsOnly;
    private Boolean isActiveJobCategoriesOnly;
    private Boolean isActiveJobSubcategoriesOnly;
    private Boolean isActiveSectorsOnly;
    private Boolean isActiveLdapsOnly;
    private Boolean isActiveDocumentTypesOnly;
    private Date startDate;
    private Date endDate;
    private JobManagerUser selectedUser;
    private JobManagerUser foundUser;
    // Search text
    private String searchText;
    private String userSearchText;
    private String generalSearchText;
    private String systemOptionSearchText;
    private String classificationSearchText;
    private String jobCategorySearchText;
    private String jobSubcategorySearchText;
    private String sectorSearchText;
    private String ldapSearchText;
    private String documentTypeSearchText;
    private String openedJobsSearchText;
    // Found object lists
    private List<JobManagerUser> foundUsers;
    private List<SystemOption> foundSystemOptions;
    private List<SystemOption> foundFinancialSystemOptions;
    private List<LdapContext> foundLdapContexts;
    private List<Classification> foundClassifications;
    private List<JobCategory> foundJobCategories;
    private List<Sector> foundSectors;
    private List<DocumentStandard> foundDocumentStandards;
    private List<JobSubCategory> foundJobSubcategories;
    private List<DocumentType> foundDocumentTypes;
    // Selected objects
    private DocumentType selectedDocumentType;
    private SystemOption selectedSystemOption;
    private Classification selectedClassification;
    private JobCategory selectedJobCategory;
    private JobSubCategory selectedJobSubcategory;
    private Sector selectedSector;
    private LdapContext selectedLdapContext;
    
    /**
     * Creates a new instance of SystemManager
     */
    public SystemManager() {
        init();
    }

    public String getOpenedJobsSearchText() {
        return openedJobsSearchText;
    }

    public void setOpenedJobsSearchText(String openedJobsSearchText) {
        this.openedJobsSearchText = openedJobsSearchText;
    }

    public List getValueTypes() {
        ArrayList valueTypes = new ArrayList();

        valueTypes.add(new SelectItem("String", "String"));
        valueTypes.add(new SelectItem("Boolean", "Boolean"));
        valueTypes.add(new SelectItem("Integer", "Integer"));
        valueTypes.add(new SelectItem("Long", "Long"));
        valueTypes.add(new SelectItem("List<String>", "List<String>"));

        return valueTypes;
    }

    public List getSystemOptionCategories() {
        ArrayList categories = new ArrayList();

        categories.add(new SelectItem("System", "System"));
        categories.add(new SelectItem("Authentication", "Authentication"));
        categories.add(new SelectItem("Database", "Database"));
        categories.add(new SelectItem("Compliance", "Compliance"));
        categories.add(new SelectItem("Document", "Document"));
        categories.add(new SelectItem("Finance", "Finance"));
        categories.add(new SelectItem("General", "General"));
        categories.add(new SelectItem("GUI", "GUI"));
        categories.add(new SelectItem("Job", "Job"));
        categories.add(new SelectItem("Legal", "Legal"));
        categories.add(new SelectItem("Metrology", "Metrology"));
        categories.add(new SelectItem("Notification", "Notification"));

        return categories;
    }

    private void init() {
        activeTabIndex = 0;
        activeNavigationTabIndex = 0;
        activeTabForm = "";
        searchType = "General";
        dateSearchField = "dateReceived";
        dateSearchPeriod = "thisMonth";
        searchTextVisible = true;
        foundUsers = null;
        foundLdapContexts = null;
        foundSystemOptions = null;
        foundFinancialSystemOptions = null;
        foundLdapContexts = null;
        foundClassifications = null;
        foundJobCategories = null;
        foundJobSubcategories = null;
        foundDocumentStandards = null;
        // Search texts
        searchText = "";
        userSearchText = "";
        generalSearchText = "";
        systemOptionSearchText = "";
        jobCategorySearchText = "";
        jobSubcategorySearchText = "";
        classificationSearchText = "";
        sectorSearchText = "";
        ldapSearchText = "";
        documentTypeSearchText = "";
        openedJobsSearchText = "";
        // Active flags
        isActiveJobCategoriesOnly = true;
        isActiveJobSubcategoriesOnly = true;
        isActiveSectorsOnly = true;
        isActiveLdapsOnly = true;
        isActiveDocumentTypesOnly = true;
    }

    public Boolean getIsActiveDocumentTypesOnly() {
        return isActiveDocumentTypesOnly;
    }

    public void setIsActiveDocumentTypesOnly(Boolean isActiveDocumentTypesOnly) {
        this.isActiveDocumentTypesOnly = isActiveDocumentTypesOnly;
    }

    public String getDocumentTypeSearchText() {
        return documentTypeSearchText;
    }

    public void setDocumentTypeSearchText(String documentTypeSearchText) {
        this.documentTypeSearchText = documentTypeSearchText;
    }

    public DocumentType getSelectedDocumentType() {
        return selectedDocumentType;
    }

    public void setSelectedDocumentType(DocumentType selectedDocumentType) {
        this.selectedDocumentType = selectedDocumentType;
    }

    public MainTabView getMainTabView() {
        JobManager jm = BeanUtils.findBean("jobManager");

        return jm.getMainTabView();
    }

    public void updatePrivileges(AjaxBehaviorEvent event) {
        switch (event.getComponent().getId()) {
            // Job Privileges
            case "canEnterJob":
                selectedUser.getPrivilege().
                        setCanEnterDepartmentJob(selectedUser.getPrivilege().getCanEnterJob());
                selectedUser.getPrivilege().
                        setCanEnterOwnJob(selectedUser.getPrivilege().getCanEnterJob());
                break;
            case "canEditJob":
                selectedUser.getPrivilege().setCanEditDepartmentJob(selectedUser.
                        getPrivilege().getCanEditJob());
                selectedUser.getPrivilege().setCanEditOwnJob(selectedUser.getPrivilege().getCanEditJob());
                break;
            case "canEnterDepartmentJob":
            case "canEnterOwnJob":
            case "canEditDepartmentalJob":
            case "canEditOwnJob":
            case "canApproveJobCosting":
            // Organizational Privileges    
            case "canAddClient":
            case "canAddSupplier":
            case "canDeleteClient":
            case "canAddEmployee":
            case "canDeleteEmployee":
            case "canAddDepartment":
            case "canDeleteDepartment":
            case "canBeSuperUser":
                break;
            default:
                break;
        }

        selectedUser.getPrivilege().setIsDirty(true);
    }

    public void updateModuleAccess(AjaxBehaviorEvent event) {
        switch (event.getComponent().getId()) {
            case "canAccessComplianceUnit":
                getSelectedUser().getModules().setComplianceModule(getSelectedUser().
                        getPrivilege().getCanAccessComplianceUnit());
                break;
            case "canAccessCertificationUnit":
                getSelectedUser().getModules().setCertificationModule(getSelectedUser().
                        getPrivilege().getCanAccessCertificationUnit());
                break;
            case "canAccessFoodsUnit":
                getSelectedUser().getModules().setFoodsModule(getSelectedUser().
                        getPrivilege().getCanAccessFoodsUnit());
                break;
            case "canAccessJobManagementUnit":
                getSelectedUser().getModules().setJobManagementAndTrackingModule(getSelectedUser().
                        getPrivilege().getCanAccessJobManagementUnit());
                break;
            case "canAccessLegalMetrologyUnit":
                getSelectedUser().getModules().setLegalMetrologyModule(getSelectedUser().
                        getPrivilege().getCanAccessLegalMetrologyUnit());
                break;
            case "canAccessLegalOfficeUnit":
                getSelectedUser().getModules().setLegalOfficeModule(getSelectedUser().
                        getPrivilege().getCanAccessLegalOfficeUnit());
                break;
            case "canAccessServiceRequestUnit":
                getSelectedUser().getModules().setServiceRequestModule(getSelectedUser().
                        getPrivilege().getCanAccessServiceRequestUnit());
                break;
            case "canAccessStandardsUnit":
                getSelectedUser().getModules().setStandardsModule(getSelectedUser().
                        getPrivilege().getCanAccessStandardsUnit());
                break;
            case "canAccessCRMUnit":
                getSelectedUser().getModules().setCrmModule(getSelectedUser().
                        getPrivilege().getCanAccessCRMUnit());
                break;
            case "canBeFinancialAdministrator":
                getSelectedUser().getModules().setFinancialAdminModule(getSelectedUser().
                        getPrivilege().getCanBeFinancialAdministrator());
                break;
            case "canBeJMTSAdministrator":
                getSelectedUser().getModules().setAdminModule(getSelectedUser().
                        getPrivilege().getCanBeJMTSAdministrator());
                break;
            default:
                break;

        }

        getSelectedUser().getPrivilege().setIsDirty(true);
        getSelectedUser().getModules().setIsDirty(true);
    }

    public Boolean getIsActiveLdapsOnly() {
        return isActiveLdapsOnly;
    }

    public void setIsActiveLdapsOnly(Boolean isActiveLdapsOnly) {
        this.isActiveLdapsOnly = isActiveLdapsOnly;
    }

    public Boolean getIsActiveSectorsOnly() {
        return isActiveSectorsOnly;
    }

    public void setIsActiveSectorsOnly(Boolean isActiveSectorsOnly) {
        this.isActiveSectorsOnly = isActiveSectorsOnly;
    }

    public Boolean getIsActiveJobSubcategoriesOnly() {
        return isActiveJobSubcategoriesOnly;
    }

    public void setIsActiveJobSubcategoriesOnly(Boolean isActiveJobSubcategoriesOnly) {
        this.isActiveJobSubcategoriesOnly = isActiveJobSubcategoriesOnly;
    }

    public Boolean getIsActiveJobCategoriesOnly() {
        return isActiveJobCategoriesOnly;
    }

    public void setIsActiveJobCategoriesOnly(Boolean isActiveJobCategoriesOnly) {
        this.isActiveJobCategoriesOnly = isActiveJobCategoriesOnly;
    }

    public LdapContext getSelectedLdapContext() {
        return selectedLdapContext;
    }

    public void setSelectedLdapContext(LdapContext selectedLdapContext) {
        this.selectedLdapContext = selectedLdapContext;
    }

    public String getLdapSearchText() {
        return ldapSearchText;
    }

    public void setLdapSearchText(String ldapSearchText) {
        this.ldapSearchText = ldapSearchText;
    }

    public String getSectorSearchText() {
        return sectorSearchText;
    }

    public void setSectorSearchText(String sectorSearchText) {
        this.sectorSearchText = sectorSearchText;
    }

    public Sector getSelectedSector() {
        return selectedSector;
    }

    public void setSelectedSector(Sector selectedSector) {
        this.selectedSector = selectedSector;
    }

    public List<JobSubCategory> getFoundJobSubcategories() {
        if (foundJobSubcategories == null) {
            foundJobSubcategories = JobSubCategory.findAllActiveJobSubCategories(getEntityManager());
        }
        return foundJobSubcategories;
    }

    public JobSubCategory getSelectedJobSubcategory() {
        return selectedJobSubcategory;
    }

    public void setSelectedJobSubcategory(JobSubCategory selectedJobSubcategory) {
        this.selectedJobSubcategory = selectedJobSubcategory;
    }

    public String getJobSubcategorySearchText() {
        return jobSubcategorySearchText;
    }

    public void setJobSubcategorySearchText(String jobSubcategorySearchText) {
        this.jobSubcategorySearchText = jobSubcategorySearchText;
    }

    public String getJobCategorySearchText() {
        return jobCategorySearchText;
    }

    public void setJobCategorySearchText(String jobCategorySearchText) {
        this.jobCategorySearchText = jobCategorySearchText;
    }

    public JobCategory getSelectedJobCategory() {
        return selectedJobCategory;
    }

    public void setSelectedJobCategory(JobCategory selectedJobCategory) {
        this.selectedJobCategory = selectedJobCategory;
    }

    public Classification getSelectedClassification() {
        return selectedClassification;
    }

    public void setSelectedClassification(Classification selectedClassification) {
        this.selectedClassification = selectedClassification;
    }

    public void reset() {
        init();
    }

    public Boolean getIsActiveClassificationsOnly() {
        if (isActiveClassificationsOnly == null) {
            isActiveClassificationsOnly = true;
        }
        return isActiveClassificationsOnly;
    }

    public void setIsActiveClassificationsOnly(Boolean isActiveClassificationsOnly) {
        this.isActiveClassificationsOnly = isActiveClassificationsOnly;
    }

    public String getClassificationSearchText() {
        return classificationSearchText;
    }

    public void setClassificationSearchText(String classificationSearchText) {
        this.classificationSearchText = classificationSearchText;
    }

    public Boolean getIsActiveUsersOnly() {
        if (isActiveUsersOnly == null) {
            isActiveUsersOnly = true;
        }
        return isActiveUsersOnly;
    }

    public void setIsActiveUsersOnly(Boolean isActiveUsersOnly) {
        this.isActiveUsersOnly = isActiveUsersOnly;
    }

    public Boolean getPrivilegeValue() {
        if (privilegeValue == null) {
            privilegeValue = false;
        }
        return privilegeValue;
    }

    public void setPrivilegeValue(Boolean privilegeValue) {
        this.privilegeValue = privilegeValue;
    }

    public void onPrivilegeValueChanged(ValueChangeEvent event) {
        System.out.println("Test" + event.getSource());

    }

    public List<DocumentType> getFoundDocumentTypes() {
        if (foundDocumentTypes == null) {
            foundDocumentTypes = DocumentType.findAllDocumentTypes(getEntityManager());
        }

        return foundDocumentTypes;
    }

    public void setFoundDocumentTypes(List<DocumentType> foundDocumentTypes) {
        this.foundDocumentTypes = foundDocumentTypes;
    }

    public List<Sector> getFoundSectors() {
        if (foundSectors == null) {
            foundSectors = Sector.findAllActiveSectors(getEntityManager());
        }
        return foundSectors;
    }

    public void setFoundSectors(List<Sector> foundSectors) {
        this.foundSectors = foundSectors;
    }

    public List<DocumentStandard> getFoundDocumentStandards() {
        if (foundDocumentStandards == null) {
            foundDocumentStandards = DocumentStandard.findAllDocumentStandards(getEntityManager());
        }
        return foundDocumentStandards;
    }

    public void setFoundDocumentStandards(List<DocumentStandard> foundDocumentStandards) {
        this.foundDocumentStandards = foundDocumentStandards;
    }

    public List<JobCategory> getFoundJobCategories() {
        if (foundJobCategories == null) {
            foundJobCategories = JobCategory.findAllActiveJobCategories(getEntityManager());
        }
        return foundJobCategories;
    }

    public void setFoundJobCategories(List<JobCategory> foundJobCategories) {
        this.foundJobCategories = foundJobCategories;
    }

    public List<Classification> getFoundClassifications() {
        if (foundClassifications == null) {
            foundClassifications = Classification.findAllActiveClassifications(getEntityManager());
        }
        return foundClassifications;
    }

    public SystemOption getSelectedSystemOption() {
        if (selectedSystemOption == null) {
            selectedSystemOption = new SystemOption();
        }
        return selectedSystemOption;
    }

    public void setSelectedSystemOption(SystemOption selectedSystemOption) {
        this.selectedSystemOption = selectedSystemOption;
    }

    public void onSystemOptionCellEdit(CellEditEvent event) {
        int index = event.getRowIndex();
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        try {
            if (newValue != null && !newValue.equals(oldValue)) {
                if (!newValue.toString().trim().equals("")) {
                    EntityManager em = getEntityManager();

                    em.getTransaction().begin();
                    SystemOption option = getFoundSystemOptions().get(index);
                    BusinessEntityUtils.saveBusinessEntity(em, option);
                    em.getTransaction().commit();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void onFinancialSystemOptionCellEdit(CellEditEvent event) {
        int index = event.getRowIndex();
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        try {
            if (newValue != null && !newValue.equals(oldValue)) {
                if (!newValue.toString().trim().equals("")) {
                    EntityManager em = getEntityManager();

                    em.getTransaction().begin();
                    SystemOption option = getFoundFinancialSystemOptions().get(index);
                    BusinessEntityUtils.saveBusinessEntity(em, option);
                    em.getTransaction().commit();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void onLDAPCellEdit(CellEditEvent event) {

        getFoundLdapContexts().get(event.getRowIndex()).save(getEntityManager());

    }

    public void onClassificationCellEdit(CellEditEvent event) {
        BusinessEntityUtils.saveBusinessEntityInTransaction(getEntityManager(), getFoundClassifications().get(event.getRowIndex()));
    }

    public void onJobCategoryCellEdit(CellEditEvent event) {
        BusinessEntityUtils.saveBusinessEntityInTransaction(getEntityManager(), getFoundJobCategories().get(event.getRowIndex()));
    }

    public void onJobSubCategoryCellEdit(CellEditEvent event) {
        BusinessEntityUtils.saveBusinessEntityInTransaction(getEntityManager(), getFoundJobSubcategories().get(event.getRowIndex()));
    }

    public void onSectorCellEdit(CellEditEvent event) {
        BusinessEntityUtils.saveBusinessEntityInTransaction(getEntityManager(), getFoundSectors().get(event.getRowIndex()));
    }

    public void onDocumentStandardCellEdit(CellEditEvent event) {
        BusinessEntityUtils.saveBusinessEntityInTransaction(getEntityManager(), getFoundDocumentStandards().get(event.getRowIndex()));
    }

    public List<LdapContext> getFoundLdapContexts() {
        if (foundLdapContexts == null) {
            foundLdapContexts = LdapContext.findAllActiveLdapContexts(getEntityManager());
        }
        return foundLdapContexts;
    }

    public List<SystemOption> getFoundSystemOptions() {
        if (foundSystemOptions == null) {
            foundSystemOptions = SystemOption.findAllSystemOptions(getEntityManager());
        }
        return foundSystemOptions;
    }

    public void setFoundSystemOptions(List<SystemOption> foundSystemOptions) {
        this.foundSystemOptions = foundSystemOptions;
    }

    public List<SystemOption> getFoundFinancialSystemOptions() {
        if (foundFinancialSystemOptions == null) {
            foundFinancialSystemOptions = SystemOption.findAllFinancialSystemOptions(getEntityManager());
        }
        return foundFinancialSystemOptions;
    }

    public void setFoundFinancialSystemOptions(List<SystemOption> foundFinancialSystemOptions) {
        this.foundFinancialSystemOptions = foundFinancialSystemOptions;
    }

    public String getSystemOptionSearchText() {
        return systemOptionSearchText;
    }

    public void setSystemOptionSearchText(String systemOptionSearchText) {
        this.systemOptionSearchText = systemOptionSearchText;
    }

    public String getGeneralSearchText() {
        return generalSearchText;
    }

    public void setGeneralSearchText(String generalSearchText) {
        this.generalSearchText = generalSearchText;
    }

    public List<JobManagerUser> getFoundUsers() {
        if (foundUsers == null) {
            foundUsers = JobManagerUser.findAllActiveJobManagerUsers(getEntityManager());
        }
        return foundUsers;
    }

    public String getUserSearchText() {
        return userSearchText;
    }

    public void setUserSearchText(String userSearchText) {
        this.userSearchText = userSearchText;
    }

    public void doClassificationSearch() {

        if (getIsActiveClassificationsOnly()) {
            foundClassifications = Classification.findActiveClassificationsByName(getEntityManager(), getClassificationSearchText());
        } else {
            foundClassifications = Classification.findClassificationsByName(getEntityManager(), getClassificationSearchText());
        }

    }

    public void doSectorSearch() {

        if (getIsActiveSectorsOnly()) {
            foundSectors = Sector.findActiveSectorsByName(getEntityManager(), getSectorSearchText());
        } else {
            foundSectors = Sector.findSectorsByName(getEntityManager(), getSectorSearchText());
        }

    }

    public void doDocumentTypeSearch() {

        foundDocumentTypes = DocumentType.findDocumentTypesByName(getEntityManager(), getDocumentTypeSearchText());

        selectSystemAdminTab("dataListsTabViewVar", "Document types", 4, 4);
    }

    public void doJobCategorySearch() {

        if (getIsActiveJobCategoriesOnly()) {
            foundJobCategories = JobCategory.findActiveJobCategoriesByName(getEntityManager(), getJobCategorySearchText());
        } else {
            foundJobCategories = JobCategory.findJobCategoriesByName(getEntityManager(), getJobCategorySearchText());
        }

    }

    public void doJobSubcategorySearch() {

        if (getIsActiveJobSubcategoriesOnly()) {
            foundJobSubcategories = JobSubCategory.findActiveJobSubcategoriesByName(getEntityManager(), getJobSubcategorySearchText());
        } else {
            foundJobSubcategories = JobSubCategory.findJobSubcategoriesByName(getEntityManager(), getJobSubcategorySearchText());
        }

    }

    /**
     * Select an system administration tab based on whether or not the tab is
     * already opened.
     *
     * @param innerTabViewVar
     * @param innerTabName
     * @param adminTabIndex
     * @param innerTabIndex
     */
    private void selectSystemAdminTab(String innerTabViewVar, String innerTabName, int adminTabIndex, int innerTabIndex) {
        if (getMainTabView().findTab("System Administration") == null) {
            getMainTabView().addTab(getEntityManager(), "System Administration", true);
            PrimeFaces.current().executeScript("PF('centerTabVar').select(" + adminTabIndex + ");");
            PrimeFacesUtils.addMessage("Select Tab", "Select the " + innerTabName + " tab to begin search", FacesMessage.SEVERITY_INFO);
        } else {
            PrimeFaces.current().executeScript("PF('centerTabVar').select(" + adminTabIndex + ");");
            PrimeFaces.current().executeScript("PF('" + innerTabViewVar + "').select(" + innerTabIndex + ");");
        }
    }

    public void doSystemOptionSearch() {

        foundSystemOptions = SystemOption.findSystemOptions(getEntityManager(), getSystemOptionSearchText());

        if (foundSystemOptions == null) {
            foundSystemOptions = new ArrayList<>();
        }

    }

    public void doLdapContextSearch() {
        if (getIsActiveLdapsOnly()) {
            foundLdapContexts = LdapContext.findActiveLdapContexts(getEntityManager(), getLdapSearchText());
        } else {
            foundLdapContexts = LdapContext.findLdapContexts(getEntityManager(), getLdapSearchText());
        }

    }

    public void doFinancialSystemOptionSearch() {

        foundFinancialSystemOptions = SystemOption.findFinancialSystemOptions(getEntityManager(), getSystemOptionSearchText());

        if (foundFinancialSystemOptions == null) {
            foundFinancialSystemOptions = new ArrayList<>();
        }

    }

    public void doUserSearch() {

        if (getIsActiveUsersOnly()) {
            foundUsers = JobManagerUser.findActiveJobManagerUsersByName(getEntityManager(), getUserSearchText());
        } else {
            foundUsers = JobManagerUser.findJobManagerUsersByName(getEntityManager(), getUserSearchText());
        }

    }

    public void openSystemBrowser() {
        getMainTabView().addTab(getEntityManager(), "System Administration", true);
    }

    public void openFinancialAdministration() {
        getMainTabView().addTab(getEntityManager(), "Financial Administration", true);
    }

    public String getFoundUser() {

        if (foundUser != null) {
            return foundUser.getUsername();
        } else {
            foundUser = new JobManagerUser();
            foundUser.setUsername("");

            return foundUser.getUsername();
        }
    }

    public void setFoundUser(String username) {
        foundUser.setUsername(username);
    }

    public void editSystemOption() {
        PrimeFacesUtils.openDialog(null, "systemOptionDialog", true, true, true, 430, 500);
    }

    public void editClassification() {
        PrimeFacesUtils.openDialog(null, "classificationDialog", true, true, true, 325, 600);
    }

    public void editLdapContext() {
        PrimeFacesUtils.openDialog(null, "ldapDialog", true, true, true, 240, 450);
    }

    public void createNewLdapContext() {
        selectedLdapContext = new LdapContext();

        PrimeFacesUtils.openDialog(null, "ldapDialog", true, true, true, 240, 450);
    }

    public void openDocumentTypeDialog(String url) {
        PrimeFacesUtils.openDialog(null, url, true, true, true, 175, 400);
    }

    public void editDepartment() {
        PrimeFacesUtils.openDialog(null, "departmentDialog", true, true, true, 460, 700);
    }

    public void editBusiness() {
        PrimeFacesUtils.openDialog(null, "businessDialog", true, true, true, 600, 700);
    }

    public void editEmployee() {
        PrimeFacesUtils.openDialog(null, "employeeDialog", true, true, true, 325, 700);
    }

    public void editUser() {
        PrimeFacesUtils.openDialog(getSelectedUser(), "userDialog", true, true, true, 430, 750);
    }

    public JobManagerUser getSelectedUser() {
        // init with current logged on user if null
        if (selectedUser == null) {
            selectedUser = new JobManagerUser();
        }

        return selectedUser;
    }

    public void setSelectedUser(JobManagerUser selectedUser) {
        this.selectedUser = selectedUser;
    }

    public Boolean getSearchTextVisible() {
        return searchTextVisible;
    }

    public void setSearchTextVisible(Boolean searchTextVisible) {
        this.searchTextVisible = searchTextVisible;
    }

    public int getActiveNavigationTabIndex() {
        return activeNavigationTabIndex;
    }

    public void setActiveNavigationTabIndex(int activeNavigationTabIndex) {
        this.activeNavigationTabIndex = activeNavigationTabIndex;
    }

    public void loadDocument() {
    }

    public void cancelUserEdit(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void cancelDocumentTypeEdit(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void cancelBusinessEdit(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void cancelSystemOptionEdit(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void cancelClassificationEdit(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void cancelLdapContextEdit(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void cancelSectorEdit(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void cancelJobCategoryEdit(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void cancelJobSubcategoryEdit(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void saveSelectedUser(ActionEvent actionEvent) {

        selectedUser.save(getEntityManager());

        PrimeFaces.current().dialog().closeDynamic(null);

    }

    public void saveSelectedDocumentType() {

        selectedDocumentType.save(getEntityManager());

        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void saveSelectedSystemOption() {

        selectedSystemOption.save(getEntityManager());

        PrimeFaces.current().dialog().closeDynamic(null);

    }

    public void saveSelectedClassification() {

        selectedClassification.save(getEntityManager());

        PrimeFaces.current().dialog().closeDynamic(null);

    }

    public void saveSelectedLdapContext() {

        selectedLdapContext.save(getEntityManager());

        PrimeFaces.current().dialog().closeDynamic(null);

    }

    public void saveSelectedSector() {

        selectedSector.save(getEntityManager());

        PrimeFaces.current().dialog().closeDynamic(null);

    }

    public void saveSelectedJobCategory() {

        selectedJobCategory.save(getEntityManager());

        PrimeFaces.current().dialog().closeDynamic(null);

    }

    public void saveSelectedJobSubcategory() {

        selectedJobSubcategory.save(getEntityManager());

        PrimeFaces.current().dialog().closeDynamic(null);

    }

//    public void updateSelectedUserDepartment() {
//
//        if (selectedUser.getDepartment().getId() != null) {
//            selectedUser.setDepartment(Department.findDepartmentById(getEntityManager(), selectedUser.getDepartment().getId()));
//        }
//    }

    public void updateSelectedUserEmployee() {
        if (selectedUser.getEmployee() != null) {
            if (selectedUser.getEmployee().getId() != null) {
                selectedUser.setEmployee(Employee.findEmployeeById(getEntityManager(), selectedUser.getEmployee().getId()));
            } else {
                Employee employee = Employee.findDefaultEmployee(getEntityManager(), "--", "--", true);
                if (selectedUser.getEmployee() != null) {
                    selectedUser.setEmployee(employee);
                    //selectedUser.setDepartment(Department.findDefaultDepartment(getEntityManager(), "--"));
                }
            }
        } else {
            Employee employee = Employee.findDefaultEmployee(getEntityManager(), "--", "--", true);
            if (selectedUser.getEmployee() != null) {
                selectedUser.setEmployee(employee);
            }
        }
    }

    public void updateSelectedUser() {

        EntityManager em = getEntityManager();

        if (selectedUser.getId() != null) {
            selectedUser = JobManagerUser.findJobManagerUserById(em, selectedUser.getId());
        }
    }

    public void updateCanEnterDepartmentJob() {

    }

    public void updateFoundUser(SelectEvent event) {

        EntityManager em = getEntityManager();

        JobManagerUser u = JobManagerUser.findJobManagerUserByUsername(em, foundUser.getUsername().trim());
        if (u != null) {
            foundUser = u;
            selectedUser = u;
        }
    }

    public List getPersonalTitles() {
        return Utils.getPersonalTitles();
    }

    public List getSexes() {
        return Utils.getSexes();
    }

//    public List<String> completeEmployee(String query) {
//
//        try {
//            List<Employee> employees = Employee.findEmployeesByName(getEntityManager(), query);
//            List<String> suggestions = new ArrayList<>();
//            if (employees != null) {
//                if (!employees.isEmpty()) {
//                    for (Employee employee : employees) {
//                        suggestions.add(employee.toString());
//                    }
//                }
//            }
//
//            return suggestions;
//        } catch (Exception e) {
//            System.out.println(e);
//
//            return new ArrayList<>();
//        }
//    }

    public List<String> completeUser(String query) {

        try {
            List<JobManagerUser> users = JobManagerUser.findJobManagerUsersByUsername(getEntityManager(), query);
            List<String> suggestions = new ArrayList<>();
            if (users != null) {
                if (!users.isEmpty()) {
                    for (JobManagerUser u : users) {
                        suggestions.add(u.getUsername());
                    }
                }
            }

            return suggestions;
        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public void createNewUser() {
        EntityManager em = getEntityManager();

        selectedUser = new JobManagerUser();
        selectedUser.setEmployee(Employee.findDefaultEmployee(em, "--", "--", true));

        PrimeFacesUtils.openDialog(selectedUser, "userDialog", true, true, true, 430, 750);
    }

    public void createNewClassification() {
        selectedClassification = new Classification();

        PrimeFacesUtils.openDialog(null, "classificationDialog", true, true, true, 325, 600);
    }

    public void createNewJobCategory() {
        selectedJobCategory = new JobCategory();

        PrimeFacesUtils.openDialog(null, "jobCategoryDialog", true, true, true, 300, 500);
    }

    public void editJobCategory() {
        PrimeFacesUtils.openDialog(null, "jobCategoryDialog", true, true, true, 300, 500);
    }

    public void createNewJobSubCategory() {
        selectedJobSubcategory = new JobSubCategory();

        PrimeFacesUtils.openDialog(null, "jobSubcategoryDialog", true, true, true, 300, 500);
    }

    public void editJobSubcategory() {
        PrimeFacesUtils.openDialog(null, "jobSubcategoryDialog", true, true, true, 300, 500);
    }

    public void createNewSector() {
        selectedSector = new Sector();

        PrimeFacesUtils.openDialog(null, "sectorDialog", true, true, true, 275, 500);
    }

    public void createNewDocumentType() {
        selectedDocumentType = new DocumentType();

        getMainTabView().addTab(getEntityManager(), "System Administration", true);
        PrimeFaces.current().executeScript("PF('centerTabVar').select(4);");

        PrimeFacesUtils.openDialog(null, "documentTypeDialog", true, true, true, 275, 400);

    }

    public void editSector() {
        PrimeFacesUtils.openDialog(null, "sectorDialog", true, true, true, 275, 600);
    }

    public void editDocumentType() {
        openDocumentTypeDialog("documentTypeDialog");
    }

    public void createNewDocumentStandard() {
        foundDocumentStandards.add(0, new DocumentStandard());
    }

    public void createNewSystemOption() {

        selectedSystemOption = new SystemOption();

        PrimeFacesUtils.openDialog(null, "systemOptionDialog", true, true, true, 430, 500);
    }

    public void createNewFinancialSystemOption() {

        selectedSystemOption = new SystemOption();
        selectedSystemOption.setCategory("Finance");

        getMainTabView().addTab(getEntityManager(), "Financial Administration", true);

        PrimeFacesUtils.openDialog(null, "systemOptionDialog", true, true, true, 430, 500);
    }

    public void fetchDepartment(ActionEvent action) {
    }

    public void fetchEmployee(ActionEvent action) {
    }

    public void exportDocumentReportTable() {
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
        return Employee.findAllEmployees(getEntityManager());
    }

    public List<JobManagerUser> getLoggedInJobManagerUsers() {
        return new ArrayList<>();
    }

    public List<SystemOption> getAllSystemOptions() {
        foundSystemOptions = SystemOption.findAllSystemOptions(getEntityManager());

        return foundSystemOptions;
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

    public void onActiveTabChange(TabChangeEvent event) {
        activeTab = event.getTab();
        String tabTitle = activeTab.getTitle();
        switch (tabTitle) {
            case "Petrol Station Database":
                setActiveTabIndex(0);
                activeTabForm = "PetrolStationDatabaseForm";
                break;
            case "Job Management":
                setActiveTabIndex(3);
                activeTabForm = "JobManagementForm";
                break;
            default:
                break;
        }
    }

    public String getSystemInfo() {
        return "";
    }

    public void setUser(JobManagerUser user) {
        this.user = user;
    }

    public JobManagerUser getUser() {
        return user;
    }

    public EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    public Date getCurrentDate() {
        return new Date();
    }

    public void updateUserPrivilege(ValueChangeEvent event) {
    }

    public void handleUserDialogReturn() {
    }
}
