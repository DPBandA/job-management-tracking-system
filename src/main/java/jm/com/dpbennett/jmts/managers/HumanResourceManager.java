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
import jm.com.dpbennett.business.entity.Business;
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
import jm.com.dpbennett.jmts.utils.MainTabView;
import org.primefaces.PrimeFaces;
import org.primefaces.component.tabview.Tab;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DualListModel;

/**
 *
 * @author Desmond Bennett
 */
@ManagedBean
@SessionScoped
public class HumanResourceManager implements Serializable {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF;
    private JobManager jobManager;
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
    private Boolean isActiveUsersOnly;
    private Boolean isActiveEmployeesOnly;
    private Boolean isActiveDepartmentsOnly;
    //private Boolean isActiveClassificationsOnly;
    //private Boolean isActiveJobCategoriesOnly;
    //private Boolean isActiveJobSubcategoriesOnly;
    //private Boolean isActiveSectorsOnly;
    //private Boolean isActiveLdapsOnly;
    private Boolean isActiveBusinessesOnly;
    //private Boolean isActiveDocumentTypesOnly;
    private Date startDate;
    private Date endDate;
    //private JobManagerUser selectedUser;
    //private JobManagerUser foundUser;
    // Search text
    private String searchText;
    //private String userSearchText;
    private String employeeSearchText;
    private String departmentSearchText;
    //private String generalSearchText;
    //private String systemOptionSearchText;
    //private String classificationSearchText;
    //private String jobCategorySearchText;
    //private String jobSubcategorySearchText;
    //private String sectorSearchText;
    //private String ldapSearchText;
    private String businessSearchText;
    //private String documentTypeSearchText;
    //private String openedJobsSearchText;
    // Found object lists
    //private List<JobManagerUser> foundUsers;
    private List<Employee> foundEmployees;
    private List<Department> foundDepartments;
    //private List<SystemOption> foundSystemOptions;
    //private List<SystemOption> foundFinancialSystemOptions;
    //private List<LdapContext> foundLdapContexts;
    //private List<Classification> foundClassifications;
    //private List<JobCategory> foundJobCategories;
    //private List<Sector> foundSectors;
    //private List<DocumentStandard> foundDocumentStandards;
    //private List<JobSubCategory> foundJobSubcategories;
    private List<Business> foundBusinesses;
    //private List<DocumentType> foundDocumentTypes;
    private DualListModel<Department> departmentDualList;
    // Selected objects
    //private DocumentType selectedDocumentType;
    private Department selectedDepartment;
    //private SystemOption selectedSystemOption;
    //private Classification selectedClassification;
    //private JobCategory selectedJobCategory;
    //private JobSubCategory selectedJobSubcategory;
    //private Sector selectedSector;
    //private LdapContext selectedLdapContext;
    private Business selectedBusiness;
    
    /**
     * Creates a new instance of SystemManager
     */
    public HumanResourceManager() {
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
        foundDepartments = null;
        // Search texts
        searchText = "";
        employeeSearchText = "";
        departmentSearchText = "";
        // Active flags
    }
    
    public JobManager getJobManager() {
        if (jobManager == null) {
            jobManager = Application.findBean("jobManager");
        }
        return jobManager;
    }

    public MainTabView getMainTabView() {
        JobManager jm = Application.findBean("jobManager");

        return jm.getMainTabView();
    }
    
    

    public DualListModel<Department> getDepartmentDualList() {

        List<Department> source = Department.findAllActiveDepartments(getEntityManager());
        List<Department> target = selectedBusiness.getDepartments();

        source.removeAll(target);

        departmentDualList = new DualListModel<>(source, target);

        return departmentDualList;
    }

    public void setDepartmentDualList(DualListModel<Department> departmentDualList) {
        this.departmentDualList = departmentDualList;
    }

    public String getBusinessSearchText() {
        if (businessSearchText == null) {
            businessSearchText = "";
        }
        return businessSearchText;
    }

    public void setBusinessSearchText(String businessSearchText) {
        this.businessSearchText = businessSearchText;
    }

    public Business getSelectedBusiness() {
        return selectedBusiness;
    }

    public void setSelectedBusiness(Business selectedBusiness) {
        this.selectedBusiness = selectedBusiness;
    }

    public void reset() {
        init();
    }

    public Boolean getIsActiveDepartmentsOnly() {
        if (isActiveDepartmentsOnly == null) {
            isActiveDepartmentsOnly = true;
        }
        return isActiveDepartmentsOnly;
    }

    public Boolean getIsActiveBusinessesOnly() {
        return isActiveBusinessesOnly;
    }

    public void setIsActiveBusinessesOnly(Boolean isActiveBusinessesOnly) {
        this.isActiveBusinessesOnly = isActiveBusinessesOnly;
    }

    public void setIsActiveDepartmentsOnly(Boolean isActiveDepartmentsOnly) {
        this.isActiveDepartmentsOnly = isActiveDepartmentsOnly;
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

    public Boolean getIsActiveEmployeesOnly() {
        if (isActiveEmployeesOnly == null) {
            isActiveEmployeesOnly = true;
        }
        return isActiveEmployeesOnly;
    }

    public void setIsActiveEmployeesOnly(Boolean isActiveEmployeesOnly) {
        this.isActiveEmployeesOnly = isActiveEmployeesOnly;
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

    public List<Business> getFoundBusinesses() {
        if (foundBusinesses == null) {
            foundBusinesses = Business.findAllBusinesses(getEntityManager());
        }

        return foundBusinesses;
    }

    public void okDepartmentPickList() {

        getSelectedBusiness().setDepartments(departmentDualList.getTarget());

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

    public String getEmployeeSearchText() {
        return employeeSearchText;
    }

    public void setEmployeeSearchText(String employeeSearchText) {
        this.employeeSearchText = employeeSearchText;
    }

    public void doDepartmentSearch() {

        if (getIsActiveDepartmentsOnly()) {
            foundDepartments = Department.findActiveDepartmentsByName(getEntityManager(), getDepartmentSearchText());
        } else {
            foundDepartments = Department.findDepartmentsByName(getEntityManager(), getDepartmentSearchText());
        }

    }

    public void doBusinessSearch() {
        foundBusinesses = Business.findBusinessesByName(getEntityManager(), getBusinessSearchText());

    }

    public void doEmployeeSearch() {

        if (getIsActiveEmployeesOnly()) {
            foundEmployees = Employee.findActiveEmployees(getEntityManager(), getEmployeeSearchText());
        } else {
            foundEmployees = Employee.findEmployees(getEntityManager(), getEmployeeSearchText());
        }

    }

    public void openSystemBrowser() {
        getMainTabView().addTab(getEntityManager(), "System Administration", true);
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

    public void editDepartment() {
        PrimeFacesUtils.openDialog(null, "departmentDialog", true, true, true, 460, 700);
    }

    public void editBusiness() {
        PrimeFacesUtils.openDialog(null, "businessDialog", true, true, true, 600, 700);
    }

    public void editEmployee() {
        PrimeFacesUtils.openDialog(null, "employeeDialog", true, true, true, 300, 600);
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

    // tk replace cancel* with cancelDialog
    public void cancelEmployeeEdit(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void cancelDepartmentEdit(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void cancelBusinessEdit(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void saveSelectedDepartment() {

        selectedDepartment.save(getEntityManager());

        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void saveSelectedBusiness() {

        selectedBusiness.save(getEntityManager());

        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void saveSelectedEmployee(ActionEvent actionEvent) {

        selectedEmployee.save(getEntityManager());

        PrimeFaces.current().dialog().closeDynamic(null);

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

    public void updateSelectedDepartmentHead() {
        if (selectedDepartment.getHead().getId() != null) {
            selectedDepartment.setHead(Employee.findEmployeeById(getEntityManager(), selectedDepartment.getHead().getId()));
        } else {
            selectedDepartment.setHead(Employee.findDefaultEmployee(getEntityManager(), "--", "--", true));
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
    
    public void updateFoundEmployee(SelectEvent event) {

        Employee employee = Employee.findEmployeeByName(getEntityManager(),
                foundEmployee.getFirstName().trim(),
                foundEmployee.getLastName().trim());
        if (employee != null) {
            foundEmployee = employee;
            selectedEmployee = employee;
        }
    }

    public void createNewDepartment() {

        selectedDepartment = new Department();

        PrimeFacesUtils.openDialog(null, "departmentDialog", true, true, true, 460, 700);
    }

    public void createNewBusiness() {

        selectedBusiness = new Business();

        PrimeFacesUtils.openDialog(null, "businessDialog", true, true, true, 600, 700);
    }

    public void createNewEmployee() {

        selectedEmployee = new Employee();

        PrimeFacesUtils.openDialog(null, "employeeDialog", true, true, true, 300, 600);
    }

    public void fetchDepartment(ActionEvent action) {
    }

    public void fetchEmployee(ActionEvent action) {
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

    public JobManagerUser getUser() {
        return getJobManager().getUser();
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
