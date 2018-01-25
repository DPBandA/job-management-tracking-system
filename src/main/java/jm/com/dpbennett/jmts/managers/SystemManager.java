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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
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
import jm.com.dpbennett.jmts.utils.PrimeFacesUtils;
import jm.com.dpbennett.jmts.Application;
import org.primefaces.component.tabview.Tab;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author Desmond Bennett
 */
@ManagedBean
@SessionScoped
public class SystemManager implements Serializable {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF;
    private JobManagerUser user;
    private Employee selectedEmployee;
    private Employee foundEmployee;
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
    private Boolean isLoggedInUsersOnly;
    private Boolean isActiveEmployeesOnly;
    private Boolean isActiveDepartmentsOnly;
    private Boolean isActiveClassificationsOnly;
    private Boolean isActiveJobCategoriesOnly;
    private Boolean isActiveJobSubcategoriesOnly;
    private Boolean isActiveSectorsOnly;
    private Boolean isActiveLdapsOnly;
    private Date startDate;
    private Date endDate;
    private Long selectedDocumentId;
    private JobManagerUser selectedUser;
    private JobManagerUser foundUser;
    // Search text
    private String searchText;
    private String userSearchText;
    private String employeeSearchText;
    private String departmentSearchText;
    private String generalSearchText;
    private String systemOptionSearchText;
    private String classificationSearchText;
    private String jobCategorySearchText;
    private String jobSubcategorySearchText;
    private String sectorSearchText;
    private String ldapSearchText;
    // Found object lists
    private List<JobManagerUser> foundUsers;
    private List<Employee> foundEmployees;
    private List<Department> foundDepartments;
    private List<SystemOption> foundSystemOptions;
    private List<SystemOption> foundFinancialSystemOptions;
    private List<LdapContext> foundLdapContexts;
    private List<Classification> foundClassifications;
    private List<JobCategory> foundJobCategories;
    private List<Sector> foundSectors;
    private List<DocumentStandard> foundDocumentStandards;
    private List<JobSubCategory> foundJobSubcategories;
    private Department selectedDepartment;
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

    private void init() {
        activeTabIndex = 0;
        activeNavigationTabIndex = 0;
        activeTabForm = "";
        searchType = "General";
        dateSearchField = "dateReceived";
        dateSearchPeriod = "thisMonth";
        searchTextVisible = true;
        foundEmployees = null;
        foundUsers = null;
        foundDepartments = null;
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
        employeeSearchText = "";
        userSearchText = "";
        departmentSearchText = "";
        generalSearchText = "";
        systemOptionSearchText = "";
        jobCategorySearchText = "";
        jobSubcategorySearchText = "";
        classificationSearchText = "";
        sectorSearchText = "";
        ldapSearchText = "";
        // Active flags
        isActiveJobCategoriesOnly = true;
        isActiveJobSubcategoriesOnly = true;
        isActiveSectorsOnly = true;
        isActiveLdapsOnly = true;
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

    public Boolean getIsActiveDepartmentsOnly() {
        if (isActiveDepartmentsOnly == null) {
            isActiveDepartmentsOnly = true;
        }
        return isActiveDepartmentsOnly;
    }

    public void setIsActiveDepartmentsOnly(Boolean isActiveDepartmentsOnly) {
        this.isActiveDepartmentsOnly = isActiveDepartmentsOnly;
    }

    public Boolean getIsLoggedInUsersOnly() {
        if (isLoggedInUsersOnly == null) {
            isLoggedInUsersOnly = false;
        }
        return isLoggedInUsersOnly;
    }

    public void setIsLoggedInUsersOnly(Boolean isLoggedInUsersOnly) {
        this.isLoggedInUsersOnly = isLoggedInUsersOnly;
    }

    public Boolean getIsActiveEmployeesOnly() {
        if (isActiveEmployeesOnly == null) {
            isActiveEmployeesOnly = true;
        }
        return isActiveEmployeesOnly;
    }

    public void setIsActiveEmployeesOnly(Boolean isActiveEmployeesOnly) {
        this.isActiveEmployeesOnly = isActiveEmployeesOnly;
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
        Application.saveBusinessEntity(getEntityManager(), getFoundClassifications().get(event.getRowIndex()));
    }

    public void onJobCategoryCellEdit(CellEditEvent event) {
        Application.saveBusinessEntity(getEntityManager(), getFoundJobCategories().get(event.getRowIndex()));
    }

    public void onJobSubCategoryCellEdit(CellEditEvent event) {
        Application.saveBusinessEntity(getEntityManager(), getFoundJobSubcategories().get(event.getRowIndex()));
    }

    public void onSectorCellEdit(CellEditEvent event) {
        Application.saveBusinessEntity(getEntityManager(), getFoundSectors().get(event.getRowIndex()));
    }

    public void onDocumentStandardCellEdit(CellEditEvent event) {
        Application.saveBusinessEntity(getEntityManager(), getFoundDocumentStandards().get(event.getRowIndex()));
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

    public Department getSelectedDepartment() {
        if (selectedDepartment == null) {
            selectedDepartment = new Department();
        }
        return selectedDepartment;
    }

    public void setSelectedDepartment(Department selectedDepartment) {
        this.selectedDepartment = selectedDepartment;
    }

    public List<Department> getFoundDepartments() {
        if (foundDepartments == null) {
            foundDepartments = Department.findAllActiveDepartments(getEntityManager());
        }
        return foundDepartments;
    }

    public void setFoundDepartments(List<Department> foundDepartments) {
        this.foundDepartments = foundDepartments;
    }

    public String getDepartmentSearchText() {
        return departmentSearchText;
    }

    public void setDepartmentSearchText(String departmentSearchText) {
        this.departmentSearchText = departmentSearchText;
    }

    public List<JobManagerUser> getFoundUsers() {
        if (foundUsers == null) {
            foundUsers = JobManagerUser.findAllJobManagerUsers(getEntityManager());
        }
        return foundUsers;
    }

    public String getUserSearchText() {
        return userSearchText;
    }

    public void setUserSearchText(String userSearchText) {
        this.userSearchText = userSearchText;
    }

    public String getEmployeeSearchText() {
        return employeeSearchText;
    }

    public void setEmployeeSearchText(String employeeSearchText) {
        this.employeeSearchText = employeeSearchText;
    }

    public void doDepartmentSearch() {

        RequestContext context = RequestContext.getCurrentInstance();

        if (getIsActiveDepartmentsOnly()) {
            foundDepartments = Department.findActiveDepartmentsByName(getEntityManager(), getDepartmentSearchText());
        } else {
            foundDepartments = Department.findDepartmentsByName(getEntityManager(), getDepartmentSearchText());
        }

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

    public void doEmployeeSearch() {

        if (getIsActiveEmployeesOnly()) {
            foundEmployees = Employee.findActiveEmployeesByName(getEntityManager(), getEmployeeSearchText());
        } else {
            foundEmployees = Employee.findEmployeesByName(getEntityManager(), getEmployeeSearchText());
        }

    }

    public void doUserSearch() {

        foundUsers = JobManagerUser.findJobManagerUserByName(getEntityManager(), getUserSearchText());

        if (foundUsers == null) {
            foundUsers = new ArrayList<>();
        }
//        else {
//            if (isLoggedInUsersOnly) {
//                List<JobManagerUser> loggedInUsers = new ArrayList<>();
//                for (JobManagerUser jmuser : foundUsers) {
//                    if (jmuser.isLoggedIn()) {
//                        loggedInUsers.add(jmuser);
//                    }
//                }
//                foundUsers.clear();
//                foundUsers.addAll(loggedInUsers);
//            }
//        }

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

    public String getFoundEmployee() {
        if (foundEmployee == null) {
            foundEmployee = new Employee();
            foundEmployee.setFirstName("");
            foundEmployee.setLastName("");
        }

        return foundEmployee.toString();
    }

    public void setFoundEmployee(String name) {
        if (name != null) {
            String names[] = name.split(",");
            if (names.length == 2) {
                foundEmployee.setFirstName(names[1].trim());
                foundEmployee.setLastName(names[0].trim());
            }
        }
    }

    public void editSystemOption() {
        PrimeFacesUtils.openDialog(null, "systemOptionDialog", true, true, true, 330, 500);
    }

    public void editClassification() {
        PrimeFacesUtils.openDialog(null, "classificationDialog", true, true, true, 300, 600);
    }

    public void editLdapContext() {
        PrimeFacesUtils.openDialog(null, "ldapDialog", true, true, true, 240, 450);
    }

    public void createNewLdapContext() {
        selectedLdapContext = new LdapContext();

        PrimeFacesUtils.openDialog(null, "ldapDialog", true, true, true, 240, 450);
    }

    public void editDepartment() {
        PrimeFacesUtils.openDialog(null, "departmentDialog", true, true, true, 460, 600);
    }

    public void editEmployee() {
        PrimeFacesUtils.openDialog(null, "employeeDialog", true, true, true, 350, 600);
    }

    public void editUser() {
        PrimeFacesUtils.openDialog(getSelectedUser(), "userDialog", true, true, true, 430, 650);
    }

    public Employee getSelectedEmployee() {
        if (selectedEmployee == null) {
            selectedEmployee = Employee.findDefaultEmployee(getEntityManager(), "--", "--", true);
        }

        return selectedEmployee;
    }

    public void setSelectedEmployee(Employee selectedEmployee) {
        this.selectedEmployee = selectedEmployee;
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
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void cancelEmployeeEdit(ActionEvent actionEvent) {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void cancelDepartmentEdit(ActionEvent actionEvent) {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void cancelSystemOptionEdit(ActionEvent actionEvent) {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void cancelClassificationEdit(ActionEvent actionEvent) {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void cancelLdapContextEdit(ActionEvent actionEvent) {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void cancelSectorEdit(ActionEvent actionEvent) {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void cancelJobCategoryEdit(ActionEvent actionEvent) {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void cancelJobSubcategoryEdit(ActionEvent actionEvent) {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void saveSelectedUser(ActionEvent actionEvent) {

        selectedUser.save(getEntityManager());

        RequestContext.getCurrentInstance().closeDialog(null);

    }

    public void saveSelectedDepartment() {

        selectedDepartment.save(getEntityManager());

        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void saveSelectedSystemOption() {

        selectedSystemOption.save(getEntityManager());

        RequestContext.getCurrentInstance().closeDialog(null);

    }

    public void saveSelectedClassification() {

        selectedClassification.save(getEntityManager());

        RequestContext.getCurrentInstance().closeDialog(null);

    }

    public void saveSelectedLdapContext() {

        selectedLdapContext.save(getEntityManager());

        RequestContext.getCurrentInstance().closeDialog(null);

    }

    public void saveSelectedSector() {

        selectedSector.save(getEntityManager());

        RequestContext.getCurrentInstance().closeDialog(null);

    }

    public void saveSelectedJobCategory() {

        selectedJobCategory.save(getEntityManager());

        RequestContext.getCurrentInstance().closeDialog(null);

    }

    public void saveSelectedJobSubcategory() {

        selectedJobSubcategory.save(getEntityManager());

        RequestContext.getCurrentInstance().closeDialog(null);

    }

    public void saveSelectedEmployee(ActionEvent actionEvent) {

        selectedEmployee.save(getEntityManager());

        RequestContext.getCurrentInstance().closeDialog(null);

    }

    public void updateSelectedUserDepartment() {

        if (selectedUser.getDepartment().getId() != null) {
            selectedUser.setDepartment(Department.findDepartmentById(getEntityManager(), selectedUser.getDepartment().getId()));
        }
    }

    public void updateSelectedEmployeeDepartment() {
        if (selectedEmployee.getDepartment() != null) {
            if (selectedEmployee.getDepartment().getId() != null) {
                selectedEmployee.setDepartment(Department.findDepartmentById(getEntityManager(), selectedEmployee.getDepartment().getId()));
            } else {
                selectedEmployee.setDepartment(Department.findDefaultDepartment(getEntityManager(), "--"));
            }
        }
    }

    public void updateSelectedEmployeeBusinessOffice() {
        if (selectedEmployee.getBusinessOffice().getId() != null) {
            selectedEmployee.setBusinessOffice(BusinessOffice.findBusinessOfficeById(getEntityManager(), selectedEmployee.getBusinessOffice().getId()));
        } else {  // try to get defaut
            selectedEmployee.setBusinessOffice(BusinessOffice.findDefaultBusinessOffice(getEntityManager(), "--"));
        }
    }

    public void updateSelectedDepartmentHead() {
        if (selectedDepartment.getHead().getId() != null) {
            selectedDepartment.setHead(Employee.findEmployeeById(getEntityManager(), selectedDepartment.getHead().getId()));
        } else {
            selectedDepartment.setHead(Employee.findDefaultEmployee(getEntityManager(), "--", "--", true));
        }
    }

    public void updateSelectedUserEmployee() {
        if (selectedUser.getEmployee() != null) {
            if (selectedUser.getEmployee().getId() != null) {
                selectedUser.setEmployee(Employee.findEmployeeById(getEntityManager(), selectedUser.getEmployee().getId()));
                selectedUser.setUserFirstname(selectedUser.getEmployee().getFirstName());
                selectedUser.setUserLastname(selectedUser.getEmployee().getLastName());
            } else {
                Employee employee = Employee.findDefaultEmployee(getEntityManager(), "--", "--", true);
                if (selectedUser.getEmployee() != null) {
                    selectedUser.setEmployee(employee);
                    selectedUser.setUserFirstname(selectedUser.getEmployee().getFirstName());
                    selectedUser.setUserLastname(selectedUser.getEmployee().getLastName());
                    selectedUser.setDepartment(Department.findDefaultDepartment(getEntityManager(), "--"));
                }
            }
        } else {
            Employee employee = Employee.findDefaultEmployee(getEntityManager(), "--", "--", true);
            if (selectedUser.getEmployee() != null) {
                selectedUser.setEmployee(employee);
                selectedUser.setUserFirstname(selectedUser.getEmployee().getFirstName());
                selectedUser.setUserLastname(selectedUser.getEmployee().getLastName());
            }
        }
    }

    public void updateSelectedUser() {

        EntityManager em = getEntityManager();

        if (selectedUser.getId() != null) {
            selectedUser = JobManagerUser.findJobManagerUserById(em, selectedUser.getId());
        }
    }

    public void updateUserCanEditJob() {

        if (selectedUser.getPrivilege().getCanEditJob()) {
            selectedUser.getPrivilege().setCanEditDepartmentJob(true);
            selectedUser.getPrivilege().setCanEditOwnJob(true);
        } else {
            selectedUser.getPrivilege().setCanEditDepartmentJob(false);
            selectedUser.getPrivilege().setCanEditOwnJob(false);
        }

    }

    public void updateUserCanEnterJob() {

        if (selectedUser.getPrivilege().getCanEnterJob()) {
            selectedUser.getPrivilege().setCanEnterDepartmentJob(true);
            selectedUser.getPrivilege().setCanEnterOwnJob(true);
        } else {
            selectedUser.getPrivilege().setCanEnterDepartmentJob(false);
            selectedUser.getPrivilege().setCanEnterOwnJob(false);
        }
    }

    public void updateCanEditDepartmentalJob() {

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
        return BusinessEntityUtils.getPersonalTitles();
    }

    public List getSexes() {
        return BusinessEntityUtils.getSexes();
    }

    public List<String> completeEmployee(String query) {

        try {
            List<Employee> employees = Employee.findEmployeesByName(getEntityManager(), query);
            List<String> suggestions = new ArrayList<>();
            if (employees != null) {
                if (!employees.isEmpty()) {
                    for (Employee employee : employees) {
                        suggestions.add(employee.toString());
                    }
                }
            }

            return suggestions;
        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

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

    public void updateFoundEmployee(SelectEvent event) {

        Employee employee = Employee.findEmployeeByName(getEntityManager(),
                foundEmployee.getFirstName().trim(),
                foundEmployee.getLastName().trim());
        if (employee != null) {
            foundEmployee = employee;
            selectedEmployee = employee;
        }
    }

    public void createNewUser() {
        EntityManager em = getEntityManager();

        selectedUser = new JobManagerUser();
        selectedUser.setEmployee(Employee.findDefaultEmployee(em, "--", "--", true));

        PrimeFacesUtils.openDialog(selectedUser, "userDialog", true, true, true, 430, 650);
    }

    public void createNewDepartment() {

        selectedDepartment = new Department();
        selectedDepartment.setActive(true);

        PrimeFacesUtils.openDialog(null, "departmentDialog", true, true, true, 460, 600);
    }

    public void createNewClassification() {
        selectedClassification = new Classification();

        PrimeFacesUtils.openDialog(null, "classificationDialog", true, true, true, 300, 600);
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

    public void editSector() {
        PrimeFacesUtils.openDialog(null, "sectorDialog", true, true, true, 275, 600);
    }

    public void createNewDocumentStandard() {
        foundDocumentStandards.add(0, new DocumentStandard());
    }

    public void createNewEmployee() {
        EntityManager em = getEntityManager();

        selectedEmployee = new Employee();
        selectedEmployee.setBusinessOffice(BusinessOffice.findDefaultBusinessOffice(em, "Head Office"));

        PrimeFacesUtils.openDialog(null, "employeeDialog", true, true, true, 350, 600);
    }

    public void createNewSystemOption() {

        selectedSystemOption = new SystemOption();

        PrimeFacesUtils.openDialog(null, "systemOptionDialog", true, true, true, 330, 500);
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

    public List<BusinessOffice> getBusinessOffices() {
        return BusinessOffice.findAllBusinessOffices(getEntityManager());
    }

    public List<Department> getDepartments() {
        return Department.findAllDepartments(getEntityManager());
    }

    public List<JobManagerUser> getUsers() {
        List<JobManagerUser> users = JobManagerUser.findAllJobManagerUsers(getEntityManager());

        return users;
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

    public List<Employee> getFoundEmployees() {
        if (foundEmployees == null) {
            foundEmployees = Employee.findAllActiveEmployees(getEntityManager());
        }

        return foundEmployees;
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
        //System.out.println("Yes: " + privilegeName);       
        System.out.println(event.getComponent().getId());
        System.out.println("val " + !((Boolean) getPrivilegeValue()).booleanValue());
    }

    public void handleUserDialogReturn() {
        System.out.println("user dialog return");
    }
}
