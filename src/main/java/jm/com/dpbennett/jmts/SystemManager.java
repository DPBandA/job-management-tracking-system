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

package jm.com.dpbennett.jmts;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
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
    private Boolean userLogggedIn = false;
    private String username = "";
    private String password = "";
    private Boolean showLogin = true;
    private int activeTabIndex;
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
    private JobManagerUser selectedUser;
    private JobManagerUser foundUser;
    private List<Employee> foundEmployees;
    private String employeeSearchText;
    private String userSearchText;
    private JobManager jobManager;
    private List<JobManagerUser> foundUsers;
    private String departmentSearchText;
    private List<Department> foundDepartments;
    private Department selectedDepartment;
    private List<SystemOption> foundSystemOptions;
    private List<SystemOption> foundFinancialSystemOptions;
    private String generalSearchText;
    private String systemOptionSearchText;
    private SystemOption selectedSystemOption;
    private List<LdapContext> foundLdapContexts;
    private List<Classification> foundClassifications;
    private List<JobCategory> foundJobCategories;
    private List<JobSubCategory> foundJobSubCategories;
    private List<Sector> foundSectors;
    private Boolean privilegeValue;
    private List<DocumentStandard> foundDocumentStandards;

    // petrolStationDatabaseForm:petrolPumpTable
    /**
     * Creates a new instance of SystemManager
     */
    public SystemManager() {
        // jm = new JobManagement();
        activeTabIndex = 0;
        activeNavigationTabIndex = 0;
        activeTabForm = "";
        searchType = "General";
        dateSearchField = "dateReceived";
        dateSearchPeriod = "thisMonth";
        searchTextVisible = true;
        foundEmployees = new ArrayList<>();
        foundUsers = new ArrayList<>();
        foundDepartments = new ArrayList<>();
        foundLdapContexts = new ArrayList<>();
        employeeSearchText = "";
        userSearchText = "";
        generalSearchText = "";
        systemOptionSearchText = "";

        jobManager = Application.findBean("jobManager");
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
            foundSectors = Sector.findAllSectors(getEntityManager());
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

    public List<JobSubCategory> getFoundJobSubCategories() {
        if (foundJobSubCategories == null) {
            foundJobSubCategories = JobSubCategory.findAllJobSubCategories(getEntityManager());
        }
        return foundJobSubCategories;
    }

    public void setFoundJobSubCategories(List<JobSubCategory> foundJobSubCategories) {
        this.foundJobSubCategories = foundJobSubCategories;
    }

    public List<JobCategory> getFoundJobCategories() {
        if (foundJobCategories == null) {
            foundJobCategories = JobCategory.findAllJobCategories(getEntityManager());
        }
        return foundJobCategories;
    }

    public void setFoundJobCategories(List<JobCategory> foundJobCategories) {
        this.foundJobCategories = foundJobCategories;
    }

    public List<Classification> getFoundClassifications() {
        if (foundClassifications == null) {
            foundClassifications = Classification.findAllClassifications(getEntityManager());
        }
        return foundClassifications;
    }

    public void setFoundClassifications(List<Classification> foundClassifications) {
        this.foundClassifications = foundClassifications;
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
        int index = event.getRowIndex();
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        try {
            if (newValue != null && !newValue.equals(oldValue)) {
                if (!newValue.toString().trim().equals("")) {
                    EntityManager em = getEntityManager();

                    em.getTransaction().begin();
                    LdapContext context = getFoundLdapContexts().get(index);
                    BusinessEntityUtils.saveBusinessEntity(em, context);
                    em.getTransaction().commit();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void onClassificationCellEdit(CellEditEvent event) {
        Application.saveBusinessEntity(getEntityManager(), getFoundClassifications().get(event.getRowIndex()));
    }

    public void onJobCategoryCellEdit(CellEditEvent event) {
        Application.saveBusinessEntity(getEntityManager(), getFoundJobCategories().get(event.getRowIndex()));
    }

    public void onJobSubCategoryCellEdit(CellEditEvent event) {
        Application.saveBusinessEntity(getEntityManager(), getFoundJobSubCategories().get(event.getRowIndex()));
    }

    public void onSectorCellEdit(CellEditEvent event) {
        Application.saveBusinessEntity(getEntityManager(), getFoundSectors().get(event.getRowIndex()));
    }

    public void onDocumentStandardCellEdit(CellEditEvent event) {
        Application.saveBusinessEntity(getEntityManager(), getFoundDocumentStandards().get(event.getRowIndex()));
    }

    public List<LdapContext> getFoundLdapContexts() {
        if (foundLdapContexts.isEmpty()) {
            foundLdapContexts = LdapContext.findAllLdapContexts(getEntityManager());
        }
        return foundLdapContexts;
    }

    public void setFoundLdapContexts(List<LdapContext> foundLdapContexts) {
        this.foundLdapContexts = foundLdapContexts;
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
        if (foundDepartments.isEmpty()) {
            foundDepartments = Department.findAllDepartments(getEntityManager());
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
        if (foundUsers.isEmpty()) {
            foundUsers = JobManagerUser.findAllJobManagerUsers(getEntityManager());
        }
        return foundUsers;
    }

    public void setFoundUsers(List<JobManagerUser> foundUsers) {
        this.foundUsers = foundUsers;
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

        if (getDepartmentSearchText().trim().length() > 0) {
            foundDepartments = Department.findDepartmentsByName(getEntityManager(), getDepartmentSearchText());

            if (foundDepartments == null) {
                foundDepartments = new ArrayList<>();
            }

        } else {
            foundDepartments = new ArrayList<>();
        }

        context.addCallbackParam("isConnectionLive", true);
    }

    public void doSystemOptionSearch() {
        RequestContext context = RequestContext.getCurrentInstance();

        foundSystemOptions = SystemOption.findSystemOptions(getEntityManager(), getSystemOptionSearchText());

        if (foundSystemOptions == null) {
            foundSystemOptions = new ArrayList<>();
        }

        context.addCallbackParam("isConnectionLive", true);
    }

    public void doFinancialSystemOptionSearch() {
        RequestContext context = RequestContext.getCurrentInstance();

        foundFinancialSystemOptions = SystemOption.findFinancialSystemOptions(getEntityManager(), getSystemOptionSearchText());

        if (foundFinancialSystemOptions == null) {
            foundFinancialSystemOptions = new ArrayList<>();
        }

        context.addCallbackParam("isConnectionLive", true);
    }

    public void doEmployeeSearch() {

        RequestContext context = RequestContext.getCurrentInstance();

        if (getEmployeeSearchText().trim().length() > 0) {
            foundEmployees = Employee.findEmployeesByName(getEntityManager(), getEmployeeSearchText());

            if (foundEmployees == null) {
                foundEmployees = new ArrayList<>();
            }

        } else {
            foundEmployees = new ArrayList<>();
        }

        context.addCallbackParam("isConnectionLive", true);
    }

    public void doUserSearch() {

        RequestContext context = RequestContext.getCurrentInstance();

        if (getUserSearchText().trim().length() > 0) {
            foundUsers = JobManagerUser.findJobManagerUserByName(getEntityManager(), getUserSearchText());

            if (foundUsers == null) {
                foundUsers = new ArrayList<>();
            }

        } else {
            foundUsers = new ArrayList<>();
        }

        context.addCallbackParam("isConnectionLive", true);
    }

    public JobManager getJobManager() {
        return jobManager;
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
        getJobManager().openDialog(null, "systemOptionDialog", true, true, true, 400, 600);
    }

    public void editDepartment() {
        getJobManager().openDialog(null, "departmentDialog", true, true, true, 420, 600);
    }

    public void editEmployee() {
        getJobManager().openDialog(null, "employeeDialog", true, true, true, 420, 600);
    }

    public void editUser() {
        getJobManager().openDialog(getSelectedUser(), "userDialog", true, true, true, 420, 600);
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

//        // Refresh to get the saved user tk
//        if (selectedUser.getId() != null) {
//            System.out.println("getting sved user: " + selectedUser.getUsername()); //tk
//            selectedUser = JobManagerUser.findJobManagerUserById(getEntityManager(), selectedUser.getId());
//        }

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

    public void onMainTabChange(TabChangeEvent event) {
        String tabTitle = event.getTab().getTitle();
        switch (tabTitle) {
            case "Documents database":
                //            activeNavigationTabIndex = 0;
                activeTabIndex = 0;
                searchText = previousSearchText;
                //            doLegalDocumentSearch();
                break;
            case "Reporting":
                //            activeNavigationTabIndex = 1;
                activeTabIndex = 1;
                previousSearchText = searchText;
                //            doLegalDocumentSearch();
                searchText = null;
                //            doLegalDocumentSearch();
                break;
        }
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

    public void saveSelectedUser(ActionEvent actionEvent) {
        EntityManager em = getEntityManager();
        RequestContext context = RequestContext.getCurrentInstance();

        boolean saved = JobManagerUser.save(em, selectedUser);

        context.addCallbackParam("userSaved", saved);


        context.closeDialog(null);       

    }

    public void saveSelectedDepartment() {
        EntityManager em = getEntityManager();
        RequestContext context = RequestContext.getCurrentInstance();

        context.addCallbackParam("departmentSaved", Department.save(em, selectedDepartment));

        context.closeDialog(null);
    }

    public void saveSelectedSystemOption() {
        EntityManager em = getEntityManager();

        em.getTransaction().begin();
        BusinessEntityUtils.saveBusinessEntity(em, selectedSystemOption);
        em.getTransaction().commit();

        RequestContext.getCurrentInstance().closeDialog(null);

    }

    public void saveSelectedEmployee(ActionEvent actionEvent) {
        EntityManager em = getEntityManager();

        RequestContext context = RequestContext.getCurrentInstance();

        // validate names
        // firstname and lastname
        if (!BusinessEntityUtils.validateName(selectedEmployee.getFirstName())
                || !BusinessEntityUtils.validateName(selectedEmployee.getLastName())) {
            context.addCallbackParam("employeeSaved", false);
            return;
        } else {
            selectedEmployee.setName(selectedEmployee.toString());
        }

        // Get needed objects
        // business office
        BusinessOffice businessOffice = BusinessOffice.findBusinessOfficeByName(em, selectedEmployee.getBusinessOffice().getName());
        if (businessOffice != null) {
            selectedEmployee.setBusinessOffice(businessOffice);
        } else {
            selectedEmployee.setBusinessOffice(BusinessOffice.findDefaultBusinessOffice(em, "--"));
        }

        // department
        Department department = Department.findDepartmentByName(em, selectedEmployee.getDepartment().getName());
        if (department != null) {
            selectedEmployee.setDepartment(department);
        } else {
            selectedEmployee.setDepartment(Department.findDefaultDepartment(em, "--"));
        }

        em.getTransaction().begin();
        BusinessEntityUtils.saveBusinessEntity(em, selectedEmployee);
        em.getTransaction().commit();

        RequestContext.getCurrentInstance().closeDialog(null);

        context.addCallbackParam("employeeSaved", true);
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

    public void updateCanEditJob() {

        if (selectedUser.getPrivilege().getCanEditJob().booleanValue()) {
            selectedUser.getPrivilege().setCanEditDepartmentJob(true);
            selectedUser.getPrivilege().setCanEditOwnJob(true);
        } else {
            selectedUser.getPrivilege().setCanEditDepartmentJob(false);
            selectedUser.getPrivilege().setCanEditOwnJob(false);
        }

    }

    public void updateCanEnterJob() {

        if (selectedUser.getPrivilege().getCanEnterJob().booleanValue()) {
            selectedUser.getPrivilege().setCanEnterDepartmentJob(true);
            selectedUser.getPrivilege().setCanEnterOwnJob(true);
        } else {
            selectedUser.getPrivilege().setCanEnterDepartmentJob(false);
            selectedUser.getPrivilege().setCanEnterOwnJob(false);
        }
    }

    public void updateCanEditDepartmentalJob() {
        System.out.println("Can edit dept job: " + selectedUser.getPrivilege().getCanEditDepartmentJob());
    }

    public void updateCanEnterDepartmentJob() {
        System.out.println("Can enter dept job: " + selectedUser.getPrivilege().getCanEnterDepartmentJob());
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

        getJobManager().openDialog(selectedUser, "userDialog", true, true, true, 420, 600);
    }

    public void createNewDepartment() {
        toBeImpl();
    }

    public void createNewClassification() {
        foundClassifications.add(0, new Classification());
    }

    public void createNewJobCategory() {
        foundJobCategories.add(0, new JobCategory());
    }

    public void createNewJobSubCategory() {
        foundJobSubCategories.add(0, new JobSubCategory());
    }

    public void createNewSector() {
        foundSectors.add(0, new Sector());
    }

    public void createNewDocumentStandard() {
        foundDocumentStandards.add(0, new DocumentStandard());
    }

    public void createNewEmployee() {
        EntityManager em = getEntityManager();

        selectedEmployee = new Employee();
        selectedEmployee.setBusinessOffice(BusinessOffice.findDefaultBusinessOffice(em, "Head Office"));

        getJobManager().openDialog(null, "employeeDialog", true, true, true, 420, 600);
    }

    public void createNewSystemOption() {

        selectedSystemOption = new SystemOption();

        getJobManager().openDialog(null, "systemOptionDialog", true, true, true, 400, 600);
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
        if (foundEmployees.isEmpty()) {
            foundEmployees = Employee.findAllEmployees(getEntityManager());
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
            case "Gas Pump Test":
                setActiveTabIndex(1);
                activeTabForm = "GasPumpTestTabForm";
                break;
            case "Test Schedule":
                setActiveTabIndex(2);
                activeTabForm = "petrolPumpTestScheduleForm";
                break;
            case "Job Management":
                setActiveTabIndex(3);
                activeTabForm = "JobManagementForm";
                break;
        }
    }

    public String getSystemInfo() {
        return ""; // tk info to provide to be decided.
    }

    public void setUser(JobManagerUser user) {
        this.user = user;
    }

    public JobManagerUser getUser() {
        return user;
    }

    public void login(ActionEvent actionEvent) {
        EntityManager em = getEntityManager();

        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Invalid credentials");

        if ((getUsername() != null) && (getPassword() != null)) {
            if (getJobManager().validateAndAssociateUser(em, getUsername(), getPassword())) {
                // Get the job manager user if one exists and
                // facilitate the display of the employee dialog for entry if it
                // does not exist.
                // Associate this user with an employee record if this
                // was not already done.
                setUser(JobManagerUser.findJobManagerUserByUsername(em, getUsername()));
                if (getUser() != null) {
                    em.refresh(user);
                    // Check if user is an administrator
                    if (user.getPrivilege().getCanBeJMTSAdministrator()) {

                        setUserLogggedIn(true);
                        setShowLogin(false);
                        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome",
                                getUser().getUserFirstname() + " " + getUser().getUserLastname());
                        context.addCallbackParam("userExists", true);
                        handleKeepAlive();
                        username = "";
                        password = "";
                    } else {
                        username = "";
                        password = "";
                        getJobManager().displayCommonMessageDialog(null,
                                "Sorry but you are not a system administrator",
                                "Not Administrator",
                                "alert");
                    }

                } else {
                    setUserLogggedIn(false);
                    setShowLogin(true);
                    password = "";
                    context.addCallbackParam("userExists", false);
                }
            } else {
                password = "";
                setUserLogggedIn(false);
                setShowLogin(true);
            }
        } else {
            password = "";
            setUserLogggedIn(false);
            setShowLogin(true);
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("userLogggedIn", getUserLogggedIn());
    }

    public void logout() {
        user = null;
        userLogggedIn = false;
        showLogin = true;
        password = "";
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

    // tk copied from job management
    public final boolean setupDatabaseConnection(String PU) {
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

    public EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    public Date getCurrentDate() {
        return new Date();
    }

    public void handleKeepAlive() {
        EntityManager em = getEntityManager();

        System.out.println("Handling keep alive session: doing polling for SysAdmin..." + new Date());
        if (user != null) {
            em.getTransaction().begin();
            user = JobManagerUser.findJobManagerUserByUsername(em, user.getUsername());
            user.setPollTime(new Timestamp(new Date().getTime()));

            BusinessEntityUtils.saveBusinessEntity(em, user);

            em.getTransaction().commit();
        }
    }

//    public Boolean getCanAuthDetentionRequest() {
//        return JobManagerUser.isPrivileged(getSelectedUser(), "AUTH_DETENTION_REQUEST");
//    }
//
//    public void setCanAuthDetentionRequest(Boolean auth) {
//        JobManagerUser.setPrivilege(getEntityManager(), getSelectedUser(), "AUTH_DETENTION_REQUEST", auth);
//    }
//
//    public Boolean getCanAuthDetentionNotice() {
//        return JobManagerUser.isPrivileged(getSelectedUser(), "AUTH_DETENTION_NOTICE");
//    }
//
//    public void setCanAuthDetentionNotice(Boolean auth) {
//        JobManagerUser.setPrivilege(getEntityManager(), getSelectedUser(), "AUTH_DETENTION_NOTICE", auth);
//    }
//
//    public Boolean getCanApproveReleaseRequest() {
//        return JobManagerUser.isPrivileged(getSelectedUser(), "APPRV_RELEASE_REQUEST");
//    }
//
//    public void setCanApproveReleaseRequest(Boolean apprv) {
//        JobManagerUser.setPrivilege(getEntityManager(), getSelectedUser(), "APPRV_RELEASE_REQUEST", apprv);
//    }
    public void toBeImpl() {
        getJobManager().displayCommonMessageDialog(null,
                "This feature is not yet implemented",
                "Not Yet Implemented",
                "alert");
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
