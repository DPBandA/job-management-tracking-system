/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts;

import jm.com.dpbennett.business.entity.management.ClientManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.enterprise.context.SessionScoped;
import javax.faces.bean.ManagedProperty;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.AccPacCustomer;
import jm.com.dpbennett.business.entity.AccPacDocument;
import jm.com.dpbennett.business.entity.Address;
import jm.com.dpbennett.business.entity.Alert;
import jm.com.dpbennett.business.entity.BusinessEntity;
import jm.com.dpbennett.business.entity.BusinessEntityManager;
import jm.com.dpbennett.business.entity.BusinessOffice;
import jm.com.dpbennett.business.entity.CashPayment;
import jm.com.dpbennett.business.entity.Classification;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.Contact;
import jm.com.dpbennett.business.entity.CostCode;
import jm.com.dpbennett.business.entity.CostComponent;
import jm.com.dpbennett.business.entity.Country;
import jm.com.dpbennett.business.entity.DatePeriod;
import jm.com.dpbennett.business.entity.Department;
import jm.com.dpbennett.business.entity.DepartmentReport;
import jm.com.dpbennett.business.entity.DepartmentUnit;
import jm.com.dpbennett.business.entity.DocumentStandard;
import jm.com.dpbennett.business.entity.DocumentType;
import jm.com.dpbennett.business.entity.Employee;
import jm.com.dpbennett.business.entity.Internet;
import jm.com.dpbennett.business.entity.Job;
import jm.com.dpbennett.business.entity.JobCategory;
import jm.com.dpbennett.business.entity.JobCosting;
import jm.com.dpbennett.business.entity.JobCostingAndPayment;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.JobReportItem;
import jm.com.dpbennett.business.entity.JobSample;
import jm.com.dpbennett.business.entity.JobSequenceNumber;
import jm.com.dpbennett.business.entity.JobStatusAndTracking;
import jm.com.dpbennett.business.entity.JobSubCategory;
import jm.com.dpbennett.business.entity.Laboratory;
import jm.com.dpbennett.business.entity.Manufacturer;
import jm.com.dpbennett.business.entity.Preference;
import jm.com.dpbennett.business.entity.Report;
import jm.com.dpbennett.business.entity.ReportTableColumn;
import jm.com.dpbennett.business.entity.Sector;
import jm.com.dpbennett.business.entity.ServiceContract;
import jm.com.dpbennett.business.entity.ServiceRequest;
import jm.com.dpbennett.business.entity.SystemOption;
import jm.com.dpbennett.business.entity.UnitCost;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.business.entity.utils.DatePeriodJobReport;
import jm.com.dpbennett.business.entity.utils.DatePeriodJobReportColumnData;
import jm.com.dpbennett.business.entity.utils.MethodResult;
import jm.com.dpbennett.business.entity.utils.SearchParameters;
import jm.com.dpbennett.jmts.utils.DialogActionHandler;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.component.tabview.Tab;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Desmond
 */
@Named
@SessionScoped
public class JobManager implements Serializable, BusinessEntityManager, DialogActionHandler {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    @PersistenceUnit(unitName = "AccPacPU")
    private EntityManagerFactory EMF2;
    private Job currentJob;
    private Long selectedJobId;
    private Long currentJobId;
    private Job selectedJob;
    private JobSample selectedJobSample;
    private JobSample backupSelectedJobSample;
    private CashPayment selectedCashPayment;
    private Boolean addJobSample;
    private Boolean addCostComponent;
    private Boolean addCashPayment;
    private Boolean dynamicTabView;
    private String columnsToExclude;
    private Report jobReport;
    private StreamedContent jobReportFile;
    private StreamedContent jobCostingFile;
    // Rendering
    private Boolean renderSearchComponent;
    private Boolean renderJobDetailTab;
    @ManagedProperty(value = "Jobs")
    private String tabTitle;
    private Integer longProcessProgress;
    private AccPacCustomer accPacCustomer;
    private List<AccPacDocument> filteredAccPacCustomerDocuments;
    private Boolean useAccPacCustomerList;
    private CostComponent selectedCostComponent;
    private Long receivedById;
    private Long classificationId;
    private Long sectorId;
    private Long categoryId;
    private Long subCategoryId;
    // end ids used for object linking
    private String inputTextStyle;
    private String outcome;
    private String defaultOutcome;
    private Integer jobSampleDialogTabViewActiveIndex;
    private Boolean showJobEntry;
    // job report vars
    private Department reportingDepartment;
    private String reportSearchText;
    // current period
    private List<Job> currentPeriodJobReportSearchResultList;
    // previous period
    private List<Job> previousPeriodJobReportSearchResultList;
    private DatePeriod previousDatePeriod;
    private List<Job> jobSearchResultList;
    private DatePeriodJobReport jobSubCategoryReport;
    private DatePeriodJobReport sectorReport;
    private DatePeriodJobReport jobQuantitiesAndServicesReport;
    private String userPrivilegeDialogHeader;
    private String userPrivilegeDialogMessage;
    private Boolean isNewContact;
    private Contact currentContact;
    private Integer loginAttempts;
    private String selectedJobCostingTemplate;
    private Long databaseModuleId;
    private Boolean enableDatabaseModuleSelection;
    private String databaseModule;
    private SearchParameters currentSearchParameters;
    private Boolean isJobToBeCopied;
    private Main main;
    private final ClientManager clientManager;
    private final SearchManager searchManager;
    private SearchParameters reportSearchParameters;
    private Employee reportEmployee;
    private Department unitCostDepartment;
    private UnitCost currentUnitCost;
    private String searchText;
    private List<UnitCost> unitCosts;
    private String dialogActionHandlerId;
    private List<Job> jobsWithCostings;
    private Job currentJobWithCosting;
    private Department jobCostDepartment;
    private String jobsTabTitle;
    private Job[] selectedJobs;
    private Boolean sendJobCostingCompletedEmail;
    private Boolean sendJobCostingApprovedEmail;
    // Monthly report date periods
    private DatePeriod monthlyReportDatePeriod;
    private DatePeriod monthlyReportDataDatePeriod;
    private DatePeriod monthlyReportYearDatePeriod;
    // Show accpac prepayments
    private Boolean showPrepayments;

    /**
     * Creates a new instance of JobManagerBean
     */
    public JobManager() {
        this.isJobToBeCopied = false;
        this.databaseModule = "";
        this.databaseModuleId = 1L;
        this.loginAttempts = 0;
        this.isNewContact = false;
        this.showJobEntry = false;
        this.longProcessProgress = 0;
        this.columnsToExclude = "";
        this.enableDatabaseModuleSelection = false;

        // init fields
        // accpac fields init
        accPacCustomer = new AccPacCustomer(null);
        //accPacCustomer.setId(null);
        //accPacCustomerDocuments = new ArrayList<>();
        filteredAccPacCustomerDocuments = new ArrayList<>();
        useAccPacCustomerList = false;
        jobReport = new Report();
        //dirty = false;
        addJobSample = false;
        addCashPayment = false;
        addCostComponent = false;
        // reporting vars init
        ArrayList searchTypes = new ArrayList();
        ArrayList searchDateFields = new ArrayList();
        // Add search types
        searchTypes.add(new SelectItem("General", "General"));
        // Add search fields
        searchDateFields.add(new SelectItem("dateSubmitted", "Date submitted"));
        searchDateFields.add(new SelectItem("dateOfCompletion", "Date completed"));
        searchDateFields.add(new SelectItem("dateAndTimeEntered", "Date entered"));
        previousDatePeriod = new DatePeriod("Last month", "month", null, null, false, false, true);
        reportSearchParameters
                = new SearchParameters(
                        "Report Data Search",
                        null,
                        false,
                        searchTypes,
                        true,
                        "dateSubmitted",
                        true,
                        searchDateFields,
                        "General",
                        new DatePeriod("This month", "month", null, null, false, false, true),
                        "");
        dynamicTabView = true;
        renderSearchComponent = true;
        inputTextStyle = "font-weight: bold;width: 85%";
        defaultOutcome = "jmtlogin";
        outcome = defaultOutcome;
        selectedJobSample = new JobSample();
        jobSampleDialogTabViewActiveIndex = 0;
        clientManager = Application.findBean("clientManager");
        searchManager = Application.findBean("searchManager");
        sendJobCostingCompletedEmail = false;
        sendJobCostingApprovedEmail = false;
        addCostComponent = false;
        // Monthly report date periods
        monthlyReportDatePeriod = new DatePeriod("Last month", "month", null, null, false, false, true);
        monthlyReportDataDatePeriod = new DatePeriod(
                "Custom",
                "any",
                BusinessEntityUtils.createDate(2012, 3, 1), // tk make option
                new Date(), false, false, true);
        monthlyReportYearDatePeriod = new DatePeriod(
                "This financial year to date",
                "year",
                null,
                null, false, false, true);
    }

    public void onMainViewTabClose(TabCloseEvent event) {
        closeJobDetailTab();
    }

    public void closeJobDetailTab() {
        // Redo search to reload stored jobs including?
        RequestContext context = RequestContext.getCurrentInstance();

        getCurrentJob().setIsJobToBeSubcontracted(false);
        setIsJobToBeCopied(false);

        if (isDirty()) {
            context.update("jobSaveConfirmDialogForm");
            context.execute("jobSaveConfirm.show();");

            return;
        }

        resetCurrentJob();

        // Remove Job Detail tab
        setRenderJobDetailTab(false);
    }

    public void onMainViewTabChange(TabChangeEvent event) {
        RequestContext context = RequestContext.getCurrentInstance();

        Tab activeTab = event.getTab();

        if (activeTab != null) {
            setTabTitle(activeTab.getTitle());

            SearchManager sm = Application.findBean("searchManager");
            switch (getTabTitle()) {
                case "Service Requests":
                    sm.setCurrentSearchParameterKey("Service Request Search");
                    break;
                case "System Administration":
                    sm.setCurrentSearchParameterKey("Admin Search");
                    break;
                default:
                    sm.setCurrentSearchParameterKey("Job Search");
                    break;
            }
        }

        context.update("searchForm");
    }

    public Boolean getRenderJobDetailTab() {
        return renderJobDetailTab;
    }

    public void setRenderJobDetailTab(Boolean renderJobDetailTab) {
        this.renderJobDetailTab = renderJobDetailTab;
    }

    public List<AccPacDocument> getFilteredAccPacCustomerDocuments() {
        return filteredAccPacCustomerDocuments;
    }

    public void setFilteredAccPacCustomerDocuments(List<AccPacDocument> filteredAccPacCustomerDocuments) {
        this.filteredAccPacCustomerDocuments = filteredAccPacCustomerDocuments;
    }

    public Boolean getShowPrepayments() {
        if (showPrepayments == null) {
            showPrepayments = false;
        }
        return showPrepayments;
    }

    public void setShowPrepayments(Boolean showPrepayments) {
        this.showPrepayments = showPrepayments;
    }

    public DatePeriod getMonthlyReportDatePeriod() {
        return monthlyReportDatePeriod;
    }

    public void setMonthlyReportDatePeriod(DatePeriod monthlyReportDatePeriod) {
        this.monthlyReportDatePeriod = monthlyReportDatePeriod;
    }

    public DatePeriod getMonthlyReportDataDatePeriod() {
        return monthlyReportDataDatePeriod;
    }

    public void setMonthlyReportDataDatePeriod(DatePeriod monthlyReportDataDatePeriod) {
        this.monthlyReportDataDatePeriod = monthlyReportDataDatePeriod;
    }

    public DatePeriod getMonthlyReportYearDatePeriod() {
        return monthlyReportYearDatePeriod;
    }

    public void setMonthlyReportYearDatePeriod(DatePeriod monthlyReportYearDatePeriod) {
        this.monthlyReportYearDatePeriod = monthlyReportYearDatePeriod;
    }

    /**
     * Get selected job which is usually displayed in a table.
     *
     * @return
     */
    public Job[] getSelectedJobs() {
        return selectedJobs;
    }

    public void setSelectedJobs(Job[] selectedJobs) {
        this.selectedJobs = selectedJobs;
    }

    public void editPreferences() {
    }

    public String getJobsTabTitle() {
        return jobsTabTitle;
    }

    public void setJobsTabTitle(String jobsTabTitle) {
        this.jobsTabTitle = jobsTabTitle;
    }

    public Main getMain() {
        if (main == null) {
            main = Application.findBean("main");
        }
        return main;
    }

    public Department getJobCostDepartment() {
        if (jobCostDepartment == null) {
            jobCostDepartment = new Department("");
        }
        return jobCostDepartment;
    }

    public void setJobCostDepartment(Department jobCostDepartment) {
        this.jobCostDepartment = jobCostDepartment;
    }

    public Job getCurrentJobWithCosting() {
        if (currentJobWithCosting == null) {
            currentJobWithCosting = new Job();
        }
        return currentJobWithCosting;
    }

    public void setCurrentJobWithCosting(Job currentJobWithCosting) {
        this.currentJobWithCosting = currentJobWithCosting;
    }

    public List<Job> getJobsWithCostings() {
        if (jobsWithCostings == null) {
            jobsWithCostings = new ArrayList<>();
        }
        return jobsWithCostings;
    }

    public List<UnitCost> getUnitCosts() {
        if (unitCosts == null) {
            unitCosts = new ArrayList<>();
        }

        return unitCosts;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public UnitCost getCurrentUnitCost() {
        if (currentUnitCost == null) {
            currentUnitCost = new UnitCost();
        }
        return currentUnitCost;
    }

    public void setCurrentUnitCost(UnitCost currentUnitCost) {
        this.currentUnitCost = currentUnitCost;
    }

    public Department getUnitCostDepartment() {
        if (unitCostDepartment == null) {
            unitCostDepartment = new Department("");
        }
        return unitCostDepartment;
    }

    public void setUnitCostDepartment(Department unitCostDepartment) {
        this.unitCostDepartment = unitCostDepartment;
    }

    public Employee getReportEmployee() {
        if (reportEmployee == null) {
            reportEmployee = new Employee();
        }
        return reportEmployee;
    }

    public void setReportEmployee(Employee reportEmployee) {
        this.reportEmployee = reportEmployee;
    }

    public SearchParameters getReportSearchParameters() {
        return reportSearchParameters;
    }

    public void setReportSearchParameters(SearchParameters reportSearchParameters) {
        this.reportSearchParameters = reportSearchParameters;
    }

    public String getDatabaseModule() {
        return databaseModule;
    }

    public Long getDatabaseModuleId() {
        return databaseModuleId;
    }

    public void setDatabaseModuleId(Long databaseModuleId) {
        this.databaseModuleId = databaseModuleId;
    }

    public Boolean getCanEditJobCosting() {
        // Can edit if user belongs to the department to which the job was assigned
        if ((getCurrentJob().getDepartment().getId().longValue() == getUser().getEmployee().getDepartment().getId().longValue())
                || (getCurrentJob().getSubContractedDepartment().getId().longValue() == getUser().getEmployee().getDepartment().getId().longValue())) {
            return true;
        }

        return false;
    }

    public Integer getLoginAttempts() {
        return loginAttempts;
    }

    public String getSelectedJobCostingTemplate() {
        return selectedJobCostingTemplate;
    }

    public void setSelectedJobCostingTemplate(String selectedJobCostingTemplate) {
        this.selectedJobCostingTemplate = selectedJobCostingTemplate;
    }

    public void setLoginAttempts(Integer loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public void okCurrentContact(ActionEvent actionEvent) {
        if (isNewContact) {
            currentJob.getClient().getContacts().add(currentContact);
            isNewContact = false;
        }
    }

    /**
     * The components to be updated from the Contact dialog
     *
     * @return
     */
    public String getContactDialogToUpdate() {
        return ":contactDialogForm";
    }

    public void removeContact(ActionEvent event) {
        currentJob.getClient().getContacts().remove(currentContact);
        currentContact = null;
    }

    public Contact getCurrentContact() {
        if (currentContact == null) {
            return new Contact();
        }
        return currentContact;
    }

    public void setCurrentContact(Contact currentContact) {
        this.currentContact = currentContact;
    }

    public void createNewContact() {
        isNewContact = true;
        currentContact = new Contact();
        currentContact.setInternet(new Internet());
        setDirty(false);
    }

    public void displayUserPrivilegeDialog(RequestContext context,
            String header,
            String message) {

        setUserPrivilegeDialogHeader(header);
        setUserPrivilegeDialogMessage(message);
        context.update("userPrivilegeDialogForm");
        context.execute("userPrivilegeDialog.show();");
    }

    public String getUserPrivilegeDialogHeader() {
        return userPrivilegeDialogHeader;
    }

    public void setUserPrivilegeDialogHeader(String userPrivilegeDialogHeader) {
        this.userPrivilegeDialogHeader = userPrivilegeDialogHeader;
    }

    public String getUserPrivilegeDialogMessage() {
        return userPrivilegeDialogMessage;
    }

    public void setUserPrivilegeDialogMessage(String userPrivilegeDialogMessage) {
        this.userPrivilegeDialogMessage = userPrivilegeDialogMessage;
    }

    public DatePeriod getPreviousDatePeriod() {
        return previousDatePeriod;
    }

    public void setPreviousDatePeriod(DatePeriod previousDatePeriod) {
        this.previousDatePeriod = previousDatePeriod;
    }

    public List<Job> getPreviousPeriodJobReportSearchResultList() {
        return previousPeriodJobReportSearchResultList;
    }

    public void setPreviousPeriodJobReportSearchResultList(List<Job> previousPeriodJobReportSearchResultList) {
        this.previousPeriodJobReportSearchResultList = previousPeriodJobReportSearchResultList;
    }

    public Department getReportingDepartment() {
        if (reportingDepartment == null) {
            reportingDepartment = new Department("");
        }
        return reportingDepartment;
    }

    public void setReportingDepartment(Department reportingDepartment) {
        this.reportingDepartment = reportingDepartment;
    }

    public Boolean getShowJobEntry() {
        return showJobEntry;
    }

    public void setShowJobEntry(Boolean showJobEntry) {
        this.showJobEntry = showJobEntry;
    }

    public String getReportSearchText() {
        return reportSearchText;
    }

    public void setReportSearchText(String reportSearchText) {
        this.reportSearchText = reportSearchText;
    }

    private Boolean isCurrentJobJobAssignedToUser() {
        if (getUser() != null) {
            if (currentJob.getAssignedTo().getId().longValue() == getUser().getEmployee().getId().longValue()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private Boolean isJobAssignedToUserDepartment() {

        if (getUser() != null) {
            if (currentJob.getDepartment().getId().longValue() == getUser().getEmployee().getDepartment().getId().longValue()) {
                return true;
            } else if (currentJob.getSubContractedDepartment().getId().longValue() == getUser().getEmployee().getDepartment().getId().longValue()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public Boolean getCanEnterJob() {
        if (getUser() != null) {
            return getUser().getPrivilege().getCanEnterJob();
        } else {
            return false;
        }
    }

    /**
     * Can edit job only if the job is assigned to your department or if you
     * have job entry privilege
     *
     * @return
     */
    public Boolean getCanEditDepartmentalJob() {
        if (getCanEnterJob()) {
            return true;
        }

        if (getUser() != null) {
            return getUser().getPrivilege().getCanEditDepartmentJob() && isJobAssignedToUserDepartment();
        } else {
            return false;
        }
    }

    public Boolean getCanEditOwnJob() {
        if (getCanEnterJob()) {
            return true;
        }

        if (getUser() != null) {
            return getUser().getPrivilege().getCanEditOwnJob();
        } else {
            return false;
        }
    }

    public Long getSampledById() {
        return selectedJobSample.getSampledBy().getId();
    }

    public Long getReceivedById() {
        return selectedJobSample.getReceivedBy().getId();
    }

    public void setReceivedById(Long receivedById) {
        this.receivedById = receivedById;
    }

    public Integer getJobSampleDialogTabViewActiveIndex() {
        return jobSampleDialogTabViewActiveIndex;
    }

    public void setJobSampleDialogTabViewActiveIndex(Integer jobSampleDialogTabViewActiveIndex) {
        this.jobSampleDialogTabViewActiveIndex = jobSampleDialogTabViewActiveIndex;
    }

    public Long getSelectedAssigneeId() {
        return currentJob.getAssignedTo().getId();
    }

    public Long getSelectedSubcontractedDepartmentId() {
        return currentJob.getSubContractedDepartment().getId();
    }

    public Long getClassificationId() {
        return currentJob.getClassification().getId();
    }

    public void setClassificationId(Long classificationId) {
        this.classificationId = classificationId;
    }

    public Long getCategoryId() {
        return currentJob.getJobCategory().getId();
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getSubCategoryId() {
        return currentJob.getJobSubCategory().getId();
    }

    public void setSubCategoryId(Long subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public String getJmt() {
        // outcome to use when login
        defaultOutcome = "jmtlogin";

        return "jmt";
    }

    public String getDefaultOutcome() {
        return defaultOutcome;
    }

    public void setDefaultOutcome(String defaultOutcome) {
        this.defaultOutcome = defaultOutcome;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public Long getSelectedBusinessOfficeId() {
        return currentJob.getBusinessOffice().getId();
    }

    public void onCostComponentSelect(SelectEvent event) {
        selectedCostComponent = (CostComponent) event.getObject();
    }

    public CostComponent getSelectedCostComponent() {
        return selectedCostComponent;
    }

    public void setSelectedCostComponent(CostComponent selectedCostComponent) {
        this.selectedCostComponent = selectedCostComponent;
    }

    public Boolean getUseAccPacCustomerList() {
        return useAccPacCustomerList;
    }

    public void setUseAccPacCustomerList(Boolean useAccPacCustomerList) {
        this.useAccPacCustomerList = useAccPacCustomerList;
    }

    public Integer getNumberOfDocumentsPassDocDate(Integer days) {
        Integer count = 0;

        for (AccPacDocument doc : filteredAccPacCustomerDocuments) {
            if (doc.getDaysOverDocumentDate() >= days) {
                ++count;
            }
        }

        return count;
    }

    public String getAccountStatus() {
        if (getTotalInvoicesAmountOverMaxInvDays().doubleValue() > 0.0
                && getTotalInvoicesAmount().doubleValue() > 0.0) {
            return "hold";
        } else {
            return "active";
        }
    }

    public BigDecimal getTotalInvoicesAmountOverMaxInvDays() {
        BigDecimal total = new BigDecimal(0.0);

        for (AccPacDocument doc : filteredAccPacCustomerDocuments) {
            if (doc.getDaysOverdue() > getMaxDaysPassInvoiceDate()) {
                total = total.add(doc.getCustCurrencyAmountDue());
            }
        }

        return total;
    }

    public BigDecimal getTotalInvoicesAmount() {
        BigDecimal total = new BigDecimal(0.0);

        for (AccPacDocument doc : filteredAccPacCustomerDocuments) {
            total = total.add(doc.getCustCurrencyAmountDue());
        }

        return total;
    }

    public Integer getMaxDaysPassInvoiceDate() {

        EntityManager em = getEntityManager1();

        int days = Integer.parseInt(SystemOption.findSystemOptionByName(em, "maxDaysPassInvoiceDate").getOptionValue());

        return days;
    }

    public List getWorkProgressList() {
        ArrayList list = new ArrayList();
        EntityManager em = getEntityManager1();

        String listAsString = SystemOption.findSystemOptionByName(em, "workProgressList").getOptionValue();
        String progressName[] = listAsString.split(";");

        for (String name : progressName) {
            list.add(new SelectItem(name, name));
        }

        return list;

    }

    /**
     * Get status based on the total amount on documents pass the max allowed
     * days pass the invoice date
     *
     * @return
     */
    public String getAccPacCustomerAccountStatus() {

        if (getAccountStatus().equals("hold")) {
            return "HOLD";
        } else {
            return "ACTIVE";
        }
    }

    public Integer getNumDocumentsPassMaxInvDate() {
        return getNumberOfDocumentsPassDocDate(getMaxDaysPassInvoiceDate());
    }

    public Integer getLongProcessProgress() {
        if (longProcessProgress == null) {
            longProcessProgress = 0;
        } else {
            if (longProcessProgress < 10) {
                // this is to ensure that this method does not make the progress
                // complete as this is done elsewhere.
                longProcessProgress = longProcessProgress + 1;
            }
        }

        return longProcessProgress;
    }

    public void onLongProcessComplete() {
        longProcessProgress = 0;
    }

    public void setLongProcessProgress(Integer longProcessProgress) {
        this.longProcessProgress = longProcessProgress;
    }

    /**
     * For future implementation if necessary
     *
     * @param query
     * @return
     */
    public List<String> completeSearchText(String query) {
        List<String> suggestions = new ArrayList<>();

        return suggestions;
    }

    public StreamedContent getJobCostingAnalysisFile(EntityManager em) {

        HashMap parameters = new HashMap();

        try {
            parameters.put("jobId", getCurrentJob().getId().longValue());

            Client client = Application.getActiveClientByNameIfAvailable(em, getCurrentJob().getClient());

            parameters.put("contactPersonName", BusinessEntityUtils.getContactFullName(client.getMainContact()));
            parameters.put("customerAddress", client.getBillingAddress().toString());
            parameters.put("contactNumbers", client.getStringListOfContactPhoneNumbers());
            parameters.put("jobDescription", getCurrentJob().getJobDescription());

            parameters.put("totalCost", getCurrentJob().getJobCostingAndPayment().getTotalJobCostingsAmount());
            parameters.put("depositReceiptNumbers", getCurrentJob().getJobCostingAndPayment().getReceiptNumber());
            parameters.put("discount", getCurrentJob().getJobCostingAndPayment().getDiscount());
            parameters.put("discountType", getCurrentJob().getJobCostingAndPayment().getDiscountType());
            parameters.put("deposit", getCurrentJob().getJobCostingAndPayment().getDeposit());
            parameters.put("amountDue", getCurrentJob().getJobCostingAndPayment().getAmountDue());
            parameters.put("totalTax", getCurrentJob().getJobCostingAndPayment().getTotalTax());
            parameters.put("totalTaxLabel", getCurrentJob().getJobCostingAndPayment().getTotalTaxLabel());
            parameters.put("grandTotalCostLabel", getCurrentJob().getJobCostingAndPayment().getTotalCostWithTaxLabel().toUpperCase().trim());
            parameters.put("grandTotalCost", getCurrentJob().getJobCostingAndPayment().getTotalCost());

            Connection con = BusinessEntityUtils.establishConnection(
                    SystemOption.findSystemOptionByName(em, "defaultDatabaseDriver").getOptionValue(),
                    SystemOption.findSystemOptionByName(em, "defaultDatabaseURL").getOptionValue(),
                    SystemOption.findSystemOptionByName(em, "defaultDatabaseUsername").getOptionValue(),
                    SystemOption.findSystemOptionByName(em, "defaultDatabasePassword").getOptionValue());

            if (con != null) {
                try {
                    StreamedContent streamContent;
                    // generate report
                    JasperPrint print = JasperFillManager.fillReport(SystemOption.findSystemOptionByName(em, "jobCosting").getOptionValue(), parameters, con);

                    byte[] fileBytes = JasperExportManager.exportReportToPdf(print);

                    streamContent = new DefaultStreamedContent(new ByteArrayInputStream(fileBytes), "application/pdf", "Job Costing - " + currentJob.getJobNumber() + ".pdf");

                    setLongProcessProgress(100);

                    return streamContent;
                } catch (JRException ex) {
                    System.out.println(ex);
                    return null;
                }
            }

            return null;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

    }

    public StreamedContent getJobCostingFile() {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            if (validateCurrentJob(em, false)) {
                saveCurrentJob(em);
            }

            jobCostingFile = getJobCostingAnalysisFile(em);

            setLongProcessProgress(100);

        } catch (Exception e) {
            System.out.println(e);
            setLongProcessProgress(100);
        }

        return jobCostingFile;
    }

    public Job createNewJob(EntityManager em,
            JobManagerUser user,
            Boolean autoGenerateJobNumber) {
        Job job = new Job();
        job.setClient(new Client("", false));
        job.setReportNumber("");
        job.setJobDescription("");

        // Departments initialization
        job.setSubContractedDepartment(getDefaultDepartment(em, "--"));

        job.setBusinessOffice(getDefaultBusinessOffice(em, "Head Office"));
        // reporting fields
        job.setClassification(Classification.findClassificationByName(em, "--"));
        job.setSector(Sector.findSectorByName(em, "--"));
        job.setJobCategory(JobCategory.findJobCategoryByName(em, "--"));
        job.setJobSubCategory(JobSubCategory.findJobSubCategoryByName(em, "--"));
        // service contract
        job.setServiceContract(createServiceContract());
        // set default values
        job.setAutoGenerateJobNumber(autoGenerateJobNumber);
        job.setIsEarningJob(Boolean.TRUE);
        job.setYearReceived(Calendar.getInstance().get(Calendar.YEAR));
        // job status and tracking
        job.setJobStatusAndTracking(new JobStatusAndTracking());
        job.getJobStatusAndTracking().setDateAndTimeEntered(new Date());
        job.getJobStatusAndTracking().setDateSubmitted(new Date());
        job.getJobStatusAndTracking().setWorkProgress("Not started");
        // job costing and payment
        job.setJobCostingAndPayment(createJobCostingAndPayment());
        // this is done here because job number is dependent on business office, department/subcontracted department
        job.setNumberOfSamples(0L);
        if (job.getAutoGenerateJobNumber()) {
            job.setJobNumber(getJobNumber(job, em));
        }

        return job;
    }

    public StreamedContent getServiceContractStreamContent() {
        EntityManager em = null;

        try {

            em = getEntityManager1();

            if (validateCurrentJob(em, false)) {
                saveCurrentJob(em);
            }

            String filePath = SystemOption.findSystemOptionByName(em, "serviceContract").getOptionValue();
            FileInputStream stream = createServiceContractExcelFileInputStream(em, getUser(), currentJob.getId(), filePath);

            DefaultStreamedContent dsc = new DefaultStreamedContent(stream, "application/xls", "servicecontract.xls");

            return dsc;

        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    public StreamedContent getServiceContractFile() {
        StreamedContent serviceContractStreamContent = null;

        try {
            serviceContractStreamContent = getServiceContractStreamContent();

            setLongProcessProgress(100);
        } catch (Exception e) {
            System.out.println(e);
            setLongProcessProgress(100);
        }

        return serviceContractStreamContent;
    }

    public StreamedContent getJobReportFile() {

        EntityManager em = null;

        try {
            em = getEntityManager1();

            jobReport = em.find(Report.class, jobReport.getId());

            if (jobReport.getReportFileMimeType().equals("application/jasper")) {
                if (jobReport.getName().equals("Jobs entered by employee")) {
                    jobReportFile = getJobEnteredByReportPDFFile();
                }
                if (jobReport.getName().equals("Jobs entered by department")) {
                    jobReportFile = getJobEnteredByDepartmentReportPDFFile();
                }
                if (jobReport.getName().equals("Jobs assigned to department")) {
                    jobReportFile = getJobAssignedToDepartmentReportXLSFile();
                }
            } else if (jobReport.getReportFileMimeType().equals("application/xlsx")) {
                if (jobReport.getName().equals("Jobs completed by department")) {
                    jobReportFile = getCompletedByDepartmentReport(em);
                }

                if (jobReport.getName().equals("Analytical Services Report")) {
                    jobReportFile = getAnalyticalServicesReport(em);
                }
            } else if (jobReport.getReportFileMimeType().equals("application/xls")) {
                if (jobReport.getName().equals("Monthly report")) {
                    jobReportFile = getMonthlyReport3(em);
                } else {
                    jobReportFile = getExcelReportStreamContent();
                }
            }

            setLongProcessProgress(100);

        } catch (Exception e) {
            System.out.println(e);
            setLongProcessProgress(100);
        }

        return jobReportFile;
    }

    public StreamedContent getMonthlyReport(EntityManager em) {

        try {
            DatePeriod datePeriods[] = BusinessEntityUtils.getMonthlyReportDatePeriods(reportSearchParameters.getDatePeriod()); // org

            List<JobSubCategory> subCategories = JobSubCategory.findAllJobSubCategoriesGroupedByEarningsByDepartment(em, reportingDepartment);
            List<Sector> sectors = Sector.findAllSectorsByDeparment(em, reportingDepartment);
            List<JobReportItem> jobReportItems = JobReportItem.findAllJobReportItemsByDeparment(em, reportingDepartment);

            // reports
            jobSubCategoryReport = new DatePeriodJobReport(reportingDepartment, subCategories, null, null, datePeriods);
            sectorReport = new DatePeriodJobReport(reportingDepartment, null, sectors, null, datePeriods);
            jobQuantitiesAndServicesReport = new DatePeriodJobReport(reportingDepartment, null, null, jobReportItems, datePeriods);

            // populate SubCategoryReport/Sector/job report
            for (int i = 0; i < datePeriods.length; i++) {
                // job subcat report
                List<DatePeriodJobReportColumnData> data = jobSubCategogyGroupReportByDatePeriod(em,
                        "dateOfCompletion",
                        jobSubCategoryReport.getReportingDepartment().getName(),
                        datePeriods[i].getStartDate(),
                        datePeriods[i].getEndDate());
                jobSubCategoryReport.updateSubCategoriesReportColumnData(datePeriods[i].getName(), data);
            }

            for (int i = 0; i < datePeriods.length; i++) {
                // sector report
                List<DatePeriodJobReportColumnData> data = sectorReportByDatePeriod(em,
                        "dateOfCompletion",
                        sectorReport.getReportingDepartment().getName(),
                        datePeriods[i].getStartDate(),
                        datePeriods[i].getEndDate());
                sectorReport.updateSectorsReportColumnData(datePeriods[i].getName(), data);
            }

            for (int i = 0; i < datePeriods.length; i++) {
                // job report
                List<DatePeriodJobReportColumnData> data = jobReportByDatePeriod(em,
                        jobQuantitiesAndServicesReport.getReportingDepartment().getName(),
                        datePeriods[i].getStartDate(),
                        datePeriods[i].getEndDate());
                jobQuantitiesAndServicesReport.setReportColumnData(datePeriods[i].getName(), data);
            }

            // generate report
            FileInputStream stream = createExcelJobReportFileInputStream(
                    this.getClass().getResource("MonthlyReport.xls"),
                    getUser(), reportingDepartment, jobSubCategoryReport, sectorReport, jobQuantitiesAndServicesReport);

            return new DefaultStreamedContent(stream, jobReport.getReportFileMimeType(), jobReport.getReportFile());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public StreamedContent getMonthlyReport2(EntityManager em) {

        updateReportingDepartment();

        try {
            // Get byte stream for report file
            ByteArrayInputStream stream = createExcelMonthlyReportFileInputStream(
                    new File(jobReport.getReportFileTemplate()),
                    reportingDepartment.getId());

            return new DefaultStreamedContent(stream, jobReport.getReportFileMimeType(), jobReport.getReportFile());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public StreamedContent getMonthlyReport3(EntityManager em) {

        updateReportingDepartment();

        try {
            // Get byte stream for report file
            ByteArrayInputStream stream = createExcelMonthlyReportFileInputStream2(
                    new File(jobReport.getReportFileTemplate()),
                    reportingDepartment.getId());

            return new DefaultStreamedContent(stream, jobReport.getReportFileMimeType(), jobReport.getReportFile());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public StreamedContent getCompletedByDepartmentReport(EntityManager em) {

        updateReportingDepartment();

        try {
            // Get byte stream for report file
            ByteArrayInputStream stream = jobsCompletedByDepartmentFileInputStream(
                    new File(jobReport.getReportFileTemplate()),
                    reportingDepartment.getId());

            return new DefaultStreamedContent(stream, jobReport.getReportFileMimeType(), jobReport.getReportFile());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public StreamedContent getAnalyticalServicesReport(EntityManager em) {

        updateReportingDepartment();

        try {
            // Get byte stream for report file
            ByteArrayInputStream stream = analyticalServicesReportFileInputStream(
                    new File(jobReport.getReportFileTemplate()),
                    reportingDepartment.getId());

            return new DefaultStreamedContent(stream, jobReport.getReportFileMimeType(), jobReport.getReportFile());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public StreamedContent getExcelReportStreamContent() throws URISyntaxException {

        try {

            URL url = this.getClass().getResource(jobReport.getReportFileTemplate());
            File file = new File(url.toURI());
            FileInputStream inp = new FileInputStream(file);
            short startingRow;

            // create workbook from input file
            POIFSFileSystem fileSystem = new POIFSFileSystem((FileInputStream) inp);
            HSSFWorkbook wb = new HSSFWorkbook(fileSystem);

            // ensure that crucial sheets are updated automatically
            HSSFSheet reportSheet = wb.getSheet("Report");
            reportSheet.setActive(true);

            // create temp file for output
            FileOutputStream out = new FileOutputStream(jobReport.getReportFile() + getUser().getId());

            HSSFSheet jobSheet = wb.getSheet("Statistics");
            if (jobSheet == null) {
                jobSheet = wb.createSheet("Statistics");
            }

            wb.setSheetOrder("Statistics", 0);
            wb.setActiveSheet(0);

            // set starting for inserting headers and data
            startingRow = (short) (jobSheet.getLastRowNum());

            // Create header font
            HSSFFont headerFont = wb.createFont();
            headerFont.setFontHeightInPoints((short) 14);

            headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            if (!jobReport.getReportColumns().isEmpty()) {
                HSSFRow headerRow = jobSheet.createRow(startingRow++);
                // Setup header style
                HSSFCellStyle headerCellStyle = wb.createCellStyle();
                headerCellStyle.setFont(headerFont);
                headerCellStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
                headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                headerCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                for (int i = 0; i < jobReport.getReportColumns().size(); i++) {
                    headerRow.createCell(i);
                    // Set header style
                    headerRow.getCell(i).setCellStyle(headerCellStyle);
                    // Set as first sheet
                }
                // merge header cells
                jobSheet.addMergedRegion(new CellRangeAddress(
                        jobSheet.getLastRowNum(), //first row
                        jobSheet.getLastRowNum(), //last row
                        0, //first column
                        jobReport.getReportColumns().size() - 1 //last column
                ));
                // Set header title
                headerRow.getCell(0).setCellValue(new HSSFRichTextString(jobReport.getName()));

                // Setup column headers
                // Create column header font
                HSSFFont columnHeaderFont = wb.createFont();
                columnHeaderFont.setFontHeightInPoints((short) 12);
                //headerFont.setFontName("Courier New");
                columnHeaderFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                HSSFRow columnHeaderRow = jobSheet.createRow(startingRow++);
                // Setup header style
                HSSFCellStyle headerColumnCellStyle = wb.createCellStyle();
                headerColumnCellStyle.setFont(columnHeaderFont);
                headerColumnCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                for (int i = 0; i < jobReport.getReportColumns().size(); i++) {
                    columnHeaderRow.createCell(i).setCellValue(new HSSFRichTextString(jobReport.getReportColumns().get(i).getName().trim()));
                    columnHeaderRow.getCell(i).setCellStyle(headerColumnCellStyle);
                }

                // Insert jobs data
                // Data font
                HSSFFont dataFont = wb.createFont();
                dataFont.setFontHeightInPoints((short) 12);
                short row = startingRow;
                HSSFCellStyle dataCellStyle = wb.createCellStyle();
                dataCellStyle.setFont(dataFont);
                headerColumnCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                for (Job job : currentPeriodJobReportSearchResultList) {
                    HSSFRow dataRow = jobSheet.createRow(row);
                    for (int i = 0; i < jobReport.getReportColumns().size(); i++) {

                        try {
                            ReportTableColumn col = jobReport.getReportColumns().get(i);
                            BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(i), job, col.getEntityClassMethodName());
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }

                        jobSheet.autoSizeColumn((short) i);
                    }
                    ++row;
                }
            }

            // write and save file for later use
            wb.write(out);
            out.close();

            FileInputStream stream = new FileInputStream(jobReport.getReportFile() + getUser().getId());
            return new DefaultStreamedContent(stream, jobReport.getReportFileMimeType(), jobReport.getReportFile());

        } catch (IOException ex) {
            System.out.println(ex);
        }

        return null;
    }

    public String getTabTitle() {
        return tabTitle;
    }

    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
    }

    public String getJobSearchResultsPanelVisibility() {
        if (renderSearchComponent) {
            return "visible";
        } else {
            return "hidden";
        }
    }

    public Boolean getRenderSearchComponent() {
        return renderSearchComponent;
    }

    public void setRenderSearchComponent(Boolean renderSearchComponent) {
        this.renderSearchComponent = renderSearchComponent;
    }

    public Report getJobReport() {
        return jobReport;
    }

    public void setJobReport(Report jobReport) {
        this.jobReport = jobReport;
    }

    public void initJobReport() {
        List<Report> jobReports = getJobReports();
        if (jobReports != null) {
            if (!jobReports.isEmpty()) {
                jobReport = jobReports.get(0);
                updateJobReport();
            }
        } else {
            jobReport = new Report("");
            updateJobReport();
        }
    }

    public Boolean getDisableReportingDepartment() {
        if (jobReport.getName().equals("Jobs entered by department")
                || jobReport.getName().equals("Jobs for my department")) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean getRenderReportingDepartment() {
        if (jobReport.getName().equals("Monthly report")
                || jobReport.getName().equals("Jobs for my department")
                || jobReport.getName().equals("Jobs assigned to department")
                || jobReport.getName().equals("Jobs entered by department")
                || jobReport.getName().equals("Jobs completed by department")
                || jobReport.getName().equals("Analytical Services Report")) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean getRenderJobsReportTableTabView() {
        if (jobReport.getName().equals("Jobs for my department")
                || jobReport.getName().equals("Jobs in period")) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean getIsMonthlyReport() {
        if (jobReport.getName().equals("Monthly report")) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean getRenderEmployee() {
        if (jobReport.getName().equals("Jobs entered by employee")) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean getRenderPreviousPeriod() {
        if (jobReport.getName().equals("Monthly report")) {
            return true;
        } else {
            return false;
        }
    }

    public void refreshJobReport() {
        updateJobReport();
    }

    public void updatePreferedJobTableView() {
        setDirty(true);
    }

    public void updateServiceContract() {
        EntityManager em = getEntityManager1();
    }

    public Boolean getCurrentJobIsValid() {
        if (getCurrentJob().getId() != null) {
            return true;
        }

        return false;
    }

    public void invoiceSelectedJobCostings() {
        if (selectedJobs.length > 0) {
            EntityManager em = getEntityManager1();

            for (Job job : selectedJobs) {
                if (job.getJobCostingAndPayment().getCostingApproved()) {
                    em.getTransaction().begin();
                    job.getJobCostingAndPayment().setInvoiced(true);
                    BusinessEntityUtils.saveBusinessEntity(em, job);
                    em.getTransaction().commit();
                } else {
                    em.getTransaction().begin();
                    job.getJobCostingAndPayment().setInvoiced(false);
                    BusinessEntityUtils.saveBusinessEntity(em, job);
                    em.getTransaction().commit();
                    getMain().displayCommonMessageDialog(null, "Job costing could not be marked as being invoiced because it was not approved", "Not Approved", "alert");
                }
            }
        } else {
            getMain().displayCommonMessageDialog(null, "No job costing was selected", "No Selection", "info");
        }
    }

    public Boolean getCanExportJobCosting() {
        if (getCurrentJob().getJobCostingAndPayment().getCostingApproved()
                && getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
            return false;
        }
        return true;
    }

    public void invoiceJobCosting() {
        if (getUser().getEmployee().isMemberOf(getDepartmentBySystemOptionDeptId("invoicingDepartmentId"))) {
            if (!getCurrentJob().getJobCostingAndPayment().getCostingApproved()
                    && !getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
                getCurrentJob().getJobCostingAndPayment().setInvoiced(false);
                getMain().displayCommonMessageDialog(null, "This job costing cannot be marked as being invoiced because it is not prepared/approved", "Not prepared/Approved", "alert");
            } else {
                setDirty(true);
            }
        } else {
            getMain().displayCommonMessageDialog(null, "You do not have permission to change the invoiced status of this job costing.", "Permission Denied", "alert");
            getCurrentJob().getJobCostingAndPayment().setInvoiced(!getCurrentJob().getJobCostingAndPayment().getInvoiced());
        }
    }

    public void approveSelectedJobCostings() {
        if (selectedJobs.length > 0) {
            EntityManager em = getEntityManager1();

            for (int i = 0; i < selectedJobs.length; i++) {
                Job job = selectedJobs[i];
                if (job.getJobCostingAndPayment().getCostingCompleted()) {
                    em.getTransaction().begin();
                    job.getJobCostingAndPayment().setCostingApproved(true);
                    BusinessEntityUtils.saveBusinessEntity(em, job);
                    em.getTransaction().commit();
                } else {
                    em.getTransaction().begin();
                    job.getJobCostingAndPayment().setCostingApproved(false);
                    BusinessEntityUtils.saveBusinessEntity(em, job);
                    em.getTransaction().commit();
                    getMain().displayCommonMessageDialog(null, "Job costing could not be marked as being approved because it was not prepared", "Not Prepared", "alert");
                }
            }
        } else {
            getMain().displayCommonMessageDialog(null, "No job costing was selected", "No Selection", "info");
        }

    }

    public void updateJobReport() {

        EntityManager em = getEntityManager1();

        if (jobReport.getId() != null) {
            jobReport = getLatestJobReport(em);

            // set search text if require
            if (jobReport.getName().equals("Jobs for my department")) {
                reportSearchText = getUser().getEmployee().getDepartment().getName();
                reportingDepartment = getUser().getEmployee().getDepartment();
                currentPeriodJobReportSearchResultList = doJobReportSearch(reportSearchParameters.getDatePeriod().getStartDate(), reportSearchParameters.getDatePeriod().getEndDate());
            } else if (jobReport.getName().equals("Jobs for my department")) {
                reportSearchText = getUser().getEmployee().getDepartment().getName();
                reportingDepartment = getUser().getEmployee().getDepartment();
                currentPeriodJobReportSearchResultList = doJobReportSearch(reportSearchParameters.getDatePeriod().getStartDate(), reportSearchParameters.getDatePeriod().getEndDate());
            } else if (jobReport.getName().equals("Jobs entered by department")) {
                if (reportingDepartment == null) {
                    reportingDepartment = getUser().getEmployee().getDepartment();
                    reportSearchText = reportingDepartment.getName();
                } else {
                    reportSearchText = reportingDepartment.getName();
                }
                reportSearchParameters.setDateField("dateAndTimeEntered");
            } else if (jobReport.getName().equals("Monthly report")) {
                if (reportingDepartment == null) {
                    reportingDepartment = getUser().getEmployee().getDepartment();
                    reportSearchText = reportingDepartment.getName();
                } else {
                    reportSearchText = reportingDepartment.getName();
                }
                currentPeriodJobReportSearchResultList = new ArrayList<>();
                previousPeriodJobReportSearchResultList = new ArrayList<>();
            } else if (jobReport.getName().equals("Jobs entered by employee")) {
                reportSearchParameters.setDateField("dateAndTimeEntered");
            } else if (jobReport.getName().equals("Jobs entered by department")) {
                reportSearchParameters.setDateField("dateAndTimeEntered");
            } else if (jobReport.getName().equals("Jobs completed by department")
                    || jobReport.getName().equals("Analytical Services Report")) {
                reportSearchParameters.setDateField("dateOfCompletion");
                if (getReportingDepartment().getName().equals("")) {
                    reportingDepartment = getUser().getEmployee().getDepartment();
                }
            } else {
                currentPeriodJobReportSearchResultList = new ArrayList<>();
            }
        }

    }

    public List<Report> getJobReports() {
        EntityManager em = getEntityManager1();

        List<Report> reports = Report.findAllReports(em);

        return reports;
    }

    public List<Preference> getJobTableViewPreferences() {
        EntityManager em = getEntityManager1();

        List<Preference> prefs = Preference.findAllPreferencesByName(em, "jobTableView");

        return prefs;
    }

    public String getColumnsToExclude() {
        return columnsToExclude;
    }

    public void setColumnsToExclude(String columnsToExclude) {
        this.columnsToExclude = columnsToExclude;
    }

    public void postProcessXLS(Object document) {
        Long jobId = null;
        DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");

        // NB: Save the current job id for later restoration
        // of the current job
        if (currentJob != null) {
            jobId = currentJob.getId();
        }

        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);
        // Create a new font and alter it.
        HSSFFont headerFont = wb.createFont();
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        HSSFFont dataFont = wb.createFont();

        sheet.shiftRows(0, currentPeriodJobReportSearchResultList.size(), 1);
        // set columns widths
        for (short i = 0; i < 8; i++) {
            if (i != (short) 3) { // services column
                sheet.autoSizeColumn(i);
            } else {
                sheet.setColumnWidth(i, 15000);
            }
        }
        // set cell borders
        if (currentPeriodJobReportSearchResultList.size() > 0) {
            for (int k = 0; k < sheet.getPhysicalNumberOfRows(); k++) {
                HSSFRow firstInfoRow = sheet.getRow(k);
                for (int i = 0; i < firstInfoRow.getPhysicalNumberOfCells(); i++) {
                    HSSFCell cell = firstInfoRow.getCell(i);
                    // Style the cell with borders all around.
                    if (cell != null) {
                        // Create a new font and alter it.
                        HSSFCellStyle style = wb.createCellStyle();

                        if (k == 1) { // data header
                            dataFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                        } else {
                            dataFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
                        }
                        style.setFont(dataFont);
                        style.setWrapText(true);
                        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
                        style.setBorderTop(HSSFCellStyle.BORDER_THIN);

                        // Change cell type/style from string to date
                        if ((i == 6) || (i == 7)) {
                            if (k != 1) {
                                try {
                                    String dateStr = cell.getRichStringCellValue().getString();

                                    if (!dateStr.equals("")) {
                                        Date date = (Date) formatter.parse(dateStr);
                                        cell.setCellValue(date);
                                        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                                        style.setDataFormat(wb.createDataFormat().getFormat("MMM dd, yyyy"));
                                    }
                                    cell.setCellStyle(style);
                                } catch (ParseException ex) {
                                    System.out.println(ex);
                                }
                            } else {
                                cell.setCellStyle(style);
                            }
                        } else {
                            cell.setCellStyle(style);
                        }
                    }
                }

                // insert sheet header row
                HSSFRow header = sheet.getRow(0);

                header.createCell(0).setCellValue(new HSSFRichTextString(jobReport.getName()));
                // merge header cells
                sheet.addMergedRegion(new CellRangeAddress(
                        0, //first row
                        0, //last row
                        0, //first column
                        7 //last column
                ));
                // style the header
                HSSFCellStyle cellStyle = wb.createCellStyle();
                cellStyle.setFont(headerFont);
                cellStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
                cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
                    HSSFCell cell = header.getCell(i);
                    cell.setCellStyle(cellStyle);
                }
            }
        }

        // NB: Data exporter sets the current job to null
        // so this ensures that it is not null before returning
        // to the client.
        EntityManager em = getEntityManager1();
        if (jobId != null) {
            currentJob = em.find(Job.class, jobId);
        } else {
            currentJob = createNewJob(em, getUser(), true);
        }
    }

    public Boolean getDynamicTabView() {
        return dynamicTabView;
    }

    public void setDynamicTabView(Boolean dynamicTabView) {
        this.dynamicTabView = dynamicTabView;
    }

    public void handleKeepAlive() {
        EntityManager em = getEntityManager1();

        System.out.println("Handling keep alive session: doing polling for SysAdmin..." + new Date());
        em.getTransaction().begin();
        main.setUser(JobManagerUser.findJobManagerUserByUsername(em, main.getUser().getUsername()));
        main.getUser().setPollTime(new Timestamp(new Date().getTime()));

        for (JobManagerUser u : JobManagerUser.findAllJobManagerUsers(em)) {
            em.refresh(u);
        }
        BusinessEntityUtils.saveBusinessEntity(em, main.getUser());

        em.getTransaction().commit();
    }

    /**
     * Determine if the current user can mark the current job costing as being
     * completed. This is done by determining if the job was assigned to the
     * user.
     *
     * @param job
     * @return
     */
    public Boolean canUserCompleteJobCosting(Job job) {
        return isJobAssignedToUserDepartment();
    }

    public void saveUser() {
        EntityManager em = getEntityManager1();

        if (getUser().getId() != null) {
            em.getTransaction().begin();
            BusinessEntityUtils.saveBusinessEntity(em, getUser());
            em.getTransaction().commit();
        } else {
            System.out.println("Not saving");
        }

    }

    /**
     * Conditionally disable department entry. Currently not used.
     *
     * @return
     */
    public Boolean getDisableDepartmentEntry() {

        // allow department entry only if business office is null
        if (currentJob != null) {
            if (currentJob.getBusinessOffice() != null) {
                if (currentJob.getBusinessOffice().getCode().trim().equals("")) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void setJobCompletionDate(Date date) {
        currentJob.getJobStatusAndTracking().setDateOfCompletion(date);
    }

    public Date getJobCompletionDate() {
        if (currentJob != null) {
            return currentJob.getJobStatusAndTracking().getDateOfCompletion();
        } else {
            return null;
        }
    }

    // NB: This and other code that get date is no longer necessary. Clean up!
    public Date getExpectedDateOfCompletion() {
        if (currentJob != null) {
            return currentJob.getJobStatusAndTracking().getExpectedDateOfCompletion();
        } else {
            return null;
        }
    }

    public void setExpectedDateOfCompletion(Date date) {
        currentJob.getJobStatusAndTracking().setExpectedDateOfCompletion(date);
    }

    public Date getDateSamplesCollected() {
        if (currentJob != null) {
            if (currentJob.getJobStatusAndTracking().getDateSamplesCollected() != null) {
                return currentJob.getJobStatusAndTracking().getDateSamplesCollected();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setDateSamplesCollected(Date date) {
        currentJob.getJobStatusAndTracking().setDateSamplesCollected(date);
    }

    public Date getDateDocumentCollected() {
        if (currentJob != null) {
            if (currentJob.getJobStatusAndTracking().getDateDocumentCollected() != null) {
                return currentJob.getJobStatusAndTracking().getDateDocumentCollected();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setDateDocumentCollected(Date date) {
        currentJob.getJobStatusAndTracking().setDateDocumentCollected(date);
    }

    // service requested - calibration
    public Boolean getServiceRequestedCalibration() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getServiceRequestedCalibration();
        } else {
            return false;
        }
    }

    public void setServiceRequestedCalibration(Boolean b) {
        currentJob.getServiceContract().setServiceRequestedCalibration(b);
    }

    // service requested - label evaluation
    public Boolean getServiceRequestedLabelEvaluation() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getServiceRequestedLabelEvaluation();
        } else {
            return false;
        }
    }

    public void setServiceRequestedLabelEvaluation(Boolean b) {
        currentJob.getServiceContract().setServiceRequestedLabelEvaluation(b);
    }
    // service requested - inspection

    public Boolean getServiceRequestedInspection() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getServiceRequestedInspection();
        } else {
            return false;
        }
    }

    public void setServiceRequestedInspection(Boolean b) {
        currentJob.getServiceContract().setServiceRequestedInspection(b);
    }

    // service requested - consultancy
    public Boolean getServiceRequestedConsultancy() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getServiceRequestedConsultancy();
        } else {
            return false;
        }
    }

    public void setServiceRequestedConsultancy(Boolean b) {
        currentJob.getServiceContract().setServiceRequestedConsultancy(b);
    }

    // service requested - training
    public Boolean getServiceRequestedTraining() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getServiceRequestedTraining();
        } else {
            return false;
        }
    }

    public void setServiceRequestedTraining(Boolean b) {
        currentJob.getServiceContract().setServiceRequestedTraining(b);
    }

    // service requested - other
    public Boolean getServiceRequestedOther() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getServiceRequestedOther();
        } else {
            return false;
        }
    }

    public void setServiceRequestedOther(Boolean b) {
        currentJob.getServiceContract().setServiceRequestedOther(b);
        if (!b) {
            currentJob.getServiceContract().setServiceRequestedOtherText(null);
        }
    }

    //Intended Market
    //intended martket - local
    public Boolean getIntendedMarketLocal() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getIntendedMarketLocal();
        } else {
            return false;
        }
    }

    public void setIntendedMarketLocal(Boolean b) {
        currentJob.getServiceContract().setIntendedMarketLocal(b);
    }

    // intended martket - caricom
    public Boolean getIntendedMarketCaricom() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getIntendedMarketCaricom();
        } else {
            return false;
        }
    }

    public void setIntendedMarketCaricom(Boolean b) {
        currentJob.getServiceContract().setIntendedMarketCaricom(b);
    }

    // intended martket - UK
    public Boolean getIntendedMarketUK() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getIntendedMarketUK();
        } else {
            return false;
        }
    }

    public void setIntendedMarketUK(Boolean b) {
        currentJob.getServiceContract().setIntendedMarketUK(b);
    }

    // intended martket - USA
    public Boolean getIntendedMarketUSA() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getIntendedMarketUSA();
        } else {
            return false;
        }
    }

    public void setIntendedMarketUSA(Boolean b) {
        currentJob.getServiceContract().setIntendedMarketUSA(b);
    }
    // intended martket - Canada

    public Boolean getIntendedMarketCanada() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getIntendedMarketCanada();
        } else {
            return false;
        }
    }

    public void setIntendedMarketCanada(Boolean b) {
        currentJob.getServiceContract().setIntendedMarketCanada(b);
    }

    // intended martket - Canada
    public Boolean getIntendedMarketOther() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getIntendedMarketOther();
        } else {
            return false;
        }
    }

    public void setIntendedMarketOther(Boolean b) {
        currentJob.getServiceContract().setIntendedMarketOther(b);
        if (!b) {
            currentJob.getServiceContract().setIntendedMarketOtherText(null);
        }
    }

    /**
     *
     * @return
     */
    public Boolean getCompleted() {
        if (currentJob != null) {
            return currentJob.getJobStatusAndTracking().getCompleted();
        } else {
            return false;
        }
    }

    public void setCompleted(Boolean b) {
        currentJob.getJobStatusAndTracking().setCompleted(b);
    }

    public Boolean getJobSaved() {
        return getCurrentJob().getId() != null;
    }

    public Boolean getSamplesCollected() {
        if (currentJob != null) {
            return currentJob.getJobStatusAndTracking().getSamplesCollected();
        } else {
            return false;
        }
    }

    public void setSamplesCollected(Boolean b) {
        currentJob.getJobStatusAndTracking().setSamplesCollected(b);
    }

    // documents collected by
    public Boolean getDocumentCollected() {
        if (currentJob != null) {
            return currentJob.getJobStatusAndTracking().getDocumentCollected();
        } else {
            return false;
        }
    }

    public void setDocumentCollected(Boolean b) {
        currentJob.getJobStatusAndTracking().setDocumentCollected(b);
    }

    public CashPayment getSelectedCashPayment() {
        return selectedCashPayment;
    }

    public void setSelectedCashPayment(CashPayment selectedCashPayment) {
        this.selectedCashPayment = selectedCashPayment;
    }

    public EntityManager getEntityManager1() {
        return getEMF1().createEntityManager();
    }

    public EntityManager getEntityManager2() {
        return getEMF2().createEntityManager();
    }

    public void updateJobCategory() {
        setDirty(true);
    }

    public void updateJobSubCategory() {
        setDirty(true);
    }

    public void updateCashPayment(AjaxBehaviorEvent event) {

        System.out.println("Fields for: " + CashPayment.class.getSimpleName());
        Field[] fields = CashPayment.class.getDeclaredFields();
        for (Field field : fields) {
            System.out.println("Name: " + field.getName() + "\n");
        }
    }

    public void updateJob() {
        setDirty(true);
    }

    public void updateAllJobCostings() {
        // Update all costs that depend on tax
        updateJobCosting();
        updateJobCostingEstimate();

        setDirty(true);
    }

    public void updateJobClassification() {
        EntityManager em = getEntityManager1();

        // Get the clasification saved for use in setting taxes
        Classification classification = Classification.findClassificationByName(em,
                currentJob.getClassification().getName());
        currentJob.setClassification(classification);

        JobCostingAndPayment.setJobCostingTaxes(em, currentJob);
        // Update all costs that depend on tax
        if (currentJob.getId() != null) {
            updateAllJobCostings();
        }
    }

    public void updateJobCostingEstimate() {
        EntityManager em = getEntityManager1();

        // Update estmated cost and min. deposit  
        if (currentJob.getJobCostingAndPayment().getEstimatedCost() != null) {
            Double estimatedCostWithTaxes = BusinessEntityUtils.roundTo2DecimalPlaces(currentJob.getJobCostingAndPayment().getEstimatedCost()
                    + currentJob.getJobCostingAndPayment().getEstimatedCost()
                    * currentJob.getJobCostingAndPayment().getPercentageGCT() / 100.0);
            currentJob.getJobCostingAndPayment().setEstimatedCostIncludingTaxes(estimatedCostWithTaxes);
            setDirty(true);
        }

        if (currentJob.getJobCostingAndPayment().getMinDeposit() != null) {
            Double minDepositWithTaxes = BusinessEntityUtils.roundTo2DecimalPlaces(currentJob.getJobCostingAndPayment().getMinDeposit()
                    + currentJob.getJobCostingAndPayment().getMinDeposit()
                    * currentJob.getJobCostingAndPayment().getPercentageGCT() / 100.0);
            currentJob.getJobCostingAndPayment().setMinDepositIncludingTaxes(minDepositWithTaxes);
            setDirty(true);
        }

    }

    public void updateTotalDeposit() {
        EntityManager em = getEntityManager1();

        Employee employee = Employee.findEmployeeById(em, getUser().getEmployee().getId());
        if (employee != null) {
            getCurrentJob().getJobCostingAndPayment().setLastPaymentEnteredBy(employee);
        }
        updateAmountDue();
        setDirty(true);
    }

    public void updateTestsAndCalibration() {
        currentJob.setNoOfTestsOrCalibrations(currentJob.getNoOfTests() + currentJob.getNoOfCalibrations());

        setDirty(true);
    }

    public void update() {
        setDirty(true);
    }

    public void updateJobCostingValidity() {
        if (!validateCurrentJobCosting() && getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
            getCurrentJob().getJobCostingAndPayment().setCostingCompleted(false);
            getCurrentJob().getJobCostingAndPayment().setCostingApproved(false);
            getMain().displayCommonMessageDialog(null, "Removing the content of a required field has invalidated this job costing", "Invalid Job Costing", "info");
        } else {
            setDirty(true);
        }
    }

    public void completeJobCosting() {
        EntityManager em = getEntityManager1();

        // Check for completed subcontracts if applicable
        if (!getCurrentJob().isSubContracted() && getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
            if (!Job.findIncompleteSubcontracts(em, currentJob).isEmpty()) {
                getCurrentJob().getJobCostingAndPayment().setCostingCompleted(false);
                getMain().displayCommonMessageDialog(null,
                        "This job costing cannot be marked prepared before all subcontracted jobs are completed", "Incomplete Subcontracts", "info");
            }
        } else if (getCurrentJob().getJobCostingAndPayment().getCostingApproved()) {
            getCurrentJob().getJobCostingAndPayment().setCostingCompleted(!getCurrentJob().getJobCostingAndPayment().getCostingCompleted());
            getCurrentJob().getJobCostingAndPayment().setCostingCompleted(false);
            getMain().displayCommonMessageDialog(null,
                    "The job costing preparation status cannot be changed because it was already approved.",
                    "Job Costing Already Approved", "info");
        } else if (!validateCurrentJobCosting()
                && getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
            getCurrentJob().getJobStatusAndTracking().setDateCostingCompleted(null);
            getCurrentJob().getJobCostingAndPayment().setCostingCompleted(false);
            getCurrentJob().getJobCostingAndPayment().setCostingApproved(false);
            sendJobCostingCompletedEmail = false;
            getMain().displayCommonMessageDialog(null, "Please enter all required (*) fields before checking this job costing as being prepared.", "Required (*) Fields Missing", "info");
        } else if (getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
            getCurrentJob().getJobStatusAndTracking().setDateCostingCompleted(new Date());
            sendJobCostingCompletedEmail = true;
            setDirty(true);
        }
    }

    /**
     * Determine if the current user is the department's supervisor. This is
     * done by determining if the user is the head/active acting head of the
     * department to which the job was assigned.
     *
     * @param job
     * @return
     */
    public Boolean isUserDepartmentSupervisor(Job job) {
        EntityManager em = getEntityManager1();

        Job foundJob = Job.findJobById(em, job.getId());

        if (getDepartmentAssignedToJob(foundJob, em).getHead().getId().longValue() == getUser().getEmployee().getId().longValue()) {
            return true;
        } else if ((getDepartmentAssignedToJob(foundJob, em).getActingHead().getId().longValue() == getUser().getEmployee().getId().longValue())
                && getDepartmentAssignedToJob(foundJob, em).getActingHeadActive()) {
            return true;
        } else {
            return false;
        }
    }

    public void approveJobCosting() {

        if (isUserDepartmentSupervisor(getCurrentJob())
                || (isJobAssignedToUserDepartment()
                && getUser().getPrivilege().getCanApproveJobCosting())) {
            if (!getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
                getCurrentJob().getJobStatusAndTracking().setDateCostingApproved(null);
                setJobCostingDate(null);
                getCurrentJob().getJobCostingAndPayment().setCostingApproved(false);
                sendJobCostingApprovedEmail = false;
                getMain().displayCommonMessageDialog(null, "This job costing cannot be approved before it is prepared", "Cannot Approve", "info");
            } else {
                Date date = new Date();
                setJobCostingDate(date);
                getCurrentJob().getJobStatusAndTracking().setDateCostingApproved(date);
                sendJobCostingApprovedEmail = true;
                setDirty(true);
            }
        } else {
            setJobCostingDate(null);
            getCurrentJob().getJobCostingAndPayment().setCostingApproved(!getCurrentJob().getJobCostingAndPayment().getCostingApproved());
            getMain().displayCommonMessageDialog(null, "You do not have the permission to approve job costings.", "No Permission", "alert");
        }
    }

    public void updatePreferences() {
        setDirty(true);
    }

    public void updateDocumentsCollectedDate() {
        setDateDocumentCollected(null);
        setDirty(true);
    }

    public void updateJobCompleted() {
        if (getCompleted()) {
            currentJob.getJobStatusAndTracking().setWorkProgress("Completed");
            setJobCompletionDate(new Date());
        } else {
            currentJob.getJobStatusAndTracking().setWorkProgress("Not started");
            setJobCompletionDate(null);
        }
        setDirty(true);
    }

    public void updateSamplesCollectedDate() {
        setDirty(true);
        setDateSamplesCollected(null);
    }

    public void updateCostCode() {
        EntityManager em = getEntityManager1();
        CostCode costCode = CostCode.findCostCodeByCode(em, selectedCostComponent.getCode());

        if (costCode.getRate() != null) {
            selectedCostComponent.setRate(costCode.getRate());
        }

        setDirty(true);
        
    }

    public Boolean getAllowCostEdit() {
        if (selectedCostComponent != null) {
            if (selectedCostComponent.getCode() == null) {
                return true;
            } else if (selectedCostComponent.getCode().equals("--")) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public String getInputTextStyle() {
        if (getAllowCostEdit()) {
            inputTextStyle = "width: 85%";
        } else {
            // May be made a system option
            inputTextStyle = "width: 85%;background-color: lightgrey;background-image: none;color: blue;";
        }

        return inputTextStyle;
    }

    public void setInputTextStyle(String inputTextStyle) {
        this.inputTextStyle = inputTextStyle;
    }

    public void updateJobReportNumber() {
        setDirty(true);
    }

    public void updateAutoGenerateJobNumber() {
        if (currentJob.getAutoGenerateJobNumber()) {
            currentJob.setJobNumber(getCurrentJobNumber());
        }
        setDirty(true);
    }

    public void updateIsCostComponentHeading() {

    }

    public void updateIsCostComponentFixedCost() {

        if (getSelectedCostComponent().getIsFixedCost()) {

        }
    }

    public void updateNewClient() {
        setDirty(true);
    }

    public void updateDepartmentReport() {
    }

    public void updateJobNumber() {
        setDirty(true);
    }

    public Boolean getEnableDatabaseModuleSelection() {
        return enableDatabaseModuleSelection;
    }

    public void updateSamplesCollected() {
        setDirty(true);
    }

    public Boolean checkWorkProgressReadinessToBeChanged() {
        EntityManager em = getEntityManager1();

        // Find the currently stored job and check it's work status
        if (getCurrentJob().getId() != null) {
            Job job = Job.findJobById(em, getCurrentJob().getId());
            if (job.getJobStatusAndTracking().getWorkProgress().equals("Completed")
                    && !getUser().getPrivilege().getCanBeJMTSAdministrator()
                    && !isUserDepartmentSupervisor(getCurrentJob())) {

                // Reset current job to its saved work progress
                getCurrentJob().getJobStatusAndTracking().
                        setWorkProgress(job.getJobStatusAndTracking().getWorkProgress());

                getMain().displayCommonMessageDialog(null,
                        "This job is marked as completed and cannot be changed. You may contact the department's supervisor.",
                        "Job Work Progress Cannot Be Changed", "info");

                return false;
            } else if (job.getJobStatusAndTracking().getWorkProgress().equals("Completed")
                    && (getUser().getPrivilege().getCanBeJMTSAdministrator()
                    || isUserDepartmentSupervisor(getCurrentJob()))) {

                return true;
            } else if (!job.getJobStatusAndTracking().getWorkProgress().equals("Completed")
                    && getCurrentJob().getJobStatusAndTracking().getWorkProgress().equals("Completed")
                    && !getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {

                // Reset current job to its saved work progress
                getCurrentJob().getJobStatusAndTracking().
                        setWorkProgress(job.getJobStatusAndTracking().getWorkProgress());

                getMain().displayCommonMessageDialog(null,
                        "The job costing needs to be prepared before this job can marked as completed.",
                        "Job Work Progress Cannot Be As Marked Completed", "info");

                return false;

            }
        } else {
            getMain().displayCommonMessageDialog(null,
                    "This job cannot be marked as completed because it is not yet saved.",
                    "Job Work Progress Cannot be Changed", "info");
            return false;
        }
        // Get stored job and check its work progress

        return true;
    }

    public void updateWorkProgress() {
        RequestContext context = RequestContext.getCurrentInstance();

        if (checkWorkProgressReadinessToBeChanged()) {
            if (!currentJob.getJobStatusAndTracking().getWorkProgress().equals("Completed")) {
                currentJob.getJobStatusAndTracking().setCompleted(false);
                currentJob.getJobStatusAndTracking().setSamplesCollected(false);
                currentJob.getJobStatusAndTracking().setDocumentCollected(false);
                // overall job completion
                currentJob.getJobStatusAndTracking().setDateOfCompletion(null);
                // sample collection
                currentJob.getJobStatusAndTracking().setSamplesCollectedBy(null);
                currentJob.getJobStatusAndTracking().setDateSamplesCollected(null);
                // document collection
                currentJob.getJobStatusAndTracking().setDocumentCollectedBy(null);
                currentJob.getJobStatusAndTracking().setDateDocumentCollected(null);

                // Update start date
                if (currentJob.getJobStatusAndTracking().getWorkProgress().equals("Ongoing")
                        && currentJob.getJobStatusAndTracking().getStartDate() == null) {
                    currentJob.getJobStatusAndTracking().setStartDate(new Date());
                } else if (currentJob.getJobStatusAndTracking().getWorkProgress().equals("Not started")) {
                    currentJob.getJobStatusAndTracking().setStartDate(null);
                }

                context.addCallbackParam("jobCompleted", false);
            } else {
                currentJob.getJobStatusAndTracking().setCompleted(true);
                currentJob.getJobStatusAndTracking().setDateOfCompletion(new Date());
                context.addCallbackParam("jobCompleted", true);
            }

            setDirty(true);
        }

    }

    public void resetCurrentJob() {
        EntityManager em = getEntityManager1();

        createJob(em, false);

    }

    // tk put in Job class?
    public void createJob(EntityManager em, Boolean isSubcontract) {

        RequestContext context = RequestContext.getCurrentInstance();
        Boolean jobCreated;

        try {
            if (isSubcontract) {

                if (currentJob.getId() == null) {
                    context.addCallbackParam("jobNotSaved", true);
                    return;
                }

                // Create copy of job and use current sequence number and year.
                Long currentJobSequenceNumber = currentJob.getJobSequenceNumber();
                Integer yearReceived = currentJob.getYearReceived();

                currentJob = copyJob(em, currentJob, getUser(), true, true);
                currentJob.setIsJobToBeSubcontracted(isSubcontract);
                currentJob.setYearReceived(yearReceived);
                currentJob.setJobSequenceNumber(currentJobSequenceNumber);

                // Get and use this organization's name as the client
                SystemOption sysOption
                        = SystemOption.findSystemOptionByName(em, "organizationName");
                if (sysOption != null) {
                    currentJob.setClient(Client.findActiveDefaultClient(em, sysOption.getOptionValue(), true));
                } else {
                    currentJob.setClient(Client.findActiveDefaultClient(em, "--", true));
                }

                // Set default billing address
                currentJob.setBillingAddress(currentJob.getClient().getBillingAddress());

                // Set default contact
                currentJob.setContact(currentJob.getClient().getMainContact());

            } else {
                currentJob = createNewJob(em, getUser(), true);
            }
            if (currentJob == null) {
                jobCreated = false;
            } else {
                jobCreated = true;
                if (isSubcontract) {
                    setDirty(true);
                } else {
                    setDirty(false);
                }
            }

            accPacCustomer = new AccPacCustomer("");
            if (context != null) {
                context.addCallbackParam("jobCreated", jobCreated);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void createNewJob() {
        RequestContext context = RequestContext.getCurrentInstance();
        EntityManager em = getEntityManager1();

        if (checkJobEntryPrivilege(em, context)) {
            createJob(em, false);
            setRenderJobDetailTab(true);
            context.update("mainTabViewForm");
            context.execute("mainTabViewVar.select(1);");
        }

    }

    /**
     *
     * @param serviceRequest
     */
    public void createNewJob(ServiceRequest serviceRequest) {
        // handle user privilege and return if the user does not have
        // the privilege to do what they wish
        EntityManager em = getEntityManager1();
        createJob(em, false);

        // fill in fields from service request
        currentJob.setBusinessOffice(serviceRequest.getBusinessOffice());
        currentJob.setJobSequenceNumber(serviceRequest.getServiceRequestSequenceNumber());
        currentJob.getClient().doCopy(serviceRequest.getClient());
        currentJob.setDepartment(serviceRequest.getDepartment());
        currentJob.setAssignedTo(serviceRequest.getAssignedTo());
        if (currentJob.getAutoGenerateJobNumber()) {
            currentJob.setJobNumber(getJobNumber(currentJob, em));
        }
        // set job dirty to ensure it is saved if attempt is made to close it
        //  before saving
        setDirty(true);       
    }

    public void subContractJob(ActionEvent actionEvent) {
        EntityManager em = getEntityManager1();
        createJob(em, true);        
    }

    public void createNewJobWithoutSavingCurrent(ActionEvent action) {
        EntityManager em = getEntityManager1();
        setDirty(false);
        createJob(em, false);        
    }

    public void loadOtherJobWithoutSavingCurrent(ActionEvent action) {
        setDirty(false);
        loadJob();
    }

    public void cancelClientEdit(ActionEvent actionEvent) {
        if (currentJob.getClient().getId() == null) {
            currentJob.getClient().setName("");
        }
    }

    public Boolean getIsJobToBeCopied() {
        return isJobToBeCopied;
    }

    public void setIsJobToBeCopied(Boolean isJobToBeCopied) {
        this.isJobToBeCopied = isJobToBeCopied;
    }

    public String getSearchResultsTableHeader() {
        return Application.getSearchResultsTableHeader(currentSearchParameters, getJobSearchResultList());
    }

    public void closeJobCostingDialog() {
        // Redo search to reloasd stored jobs including
        // prompt to save modified job before attempting to create new job
        if (isDirty()) {
            RequestContext context = RequestContext.getCurrentInstance();

            // ask to save
            context.update("jobCostingSaveConfirmDialogForm");
            context.execute("jobCostingSaveConfirm.show();");
        }

    }

    public void closeUnitCostDialog() {
        RequestContext context = RequestContext.getCurrentInstance();

        // prompt to save modified job before attempting to create new job
        if (isDirty()) {
            // ask to save         
            getMain().displayCommonConfirmationDialog(initDialogActionHandlerId("unitCostDirty"), "This unit cost was modified. Do you wish to save it?", "Unit Cost Not Saved", "info");
        } else {
            context.execute("unitCostDialog.hide();");
        }

    }

    public void cancelJobEdit(ActionEvent actionEvent) {
        setDirty(false);
        doJobSearch(searchManager.getCurrentSearchParameters());
        setRenderJobDetailTab(false);
    }

    public void closePreferencesDialog2(CloseEvent closeEvent) {
        // Redo search to reloasd stored jobs including
        // the currently edited job.
        closePreferencesDialog1(null);
    }

    public void closePreferencesDialog1(ActionEvent actionEvent) {
        RequestContext context = RequestContext.getCurrentInstance();

        if (isDirty()) {
            // save prefs and update view
            savePreferences();
        }

        context.update("mainTabViewForm");
        context.update("unitDialogForms");
        context.execute("preferencesDialog.hide();");

        setDirty(false);
    }

    public void savePreferences() {
        // user object holds preferences for now so we save it
        saveUser();
    }

    public void cancelJobCostingAndPayment(ActionEvent actionEvent) {
        EntityManager em = getEntityManager1();

        // refetch costing data from database
        if (currentJob != null) {
            if (currentJob.getId() != null) {
                Job job = Job.findJobById(em, currentJob.getId());
                currentJob.setJobCostingAndPayment(job.getJobCostingAndPayment());
            }
        }

        setDirty(false);
    }

    public void validateJobCostingAndSaveJob(ActionEvent actionEvent) {

        RequestContext context = RequestContext.getCurrentInstance();
        EntityManager em = getEntityManager1();

        try {

            currentJob.getJobCostingAndPayment().calculateAmountDue();

            if (getUser().getEmployee() != null) {
                currentJob.getJobCostingAndPayment().setFinalCostDoneBy(getUser().getEmployee().getName());
            }
            // all seems to be well so try to validate and save job here
            if (validateCurrentJob(em, true)) {
                saveCurrentJob(em);
            }
            // Refresh to make sure job costings ids are not null to
            // avoid resaving newly created costing components
            currentJob.setJobCostingAndPayment(em.find(JobCostingAndPayment.class, currentJob.getJobCostingAndPayment().getId()));

        } catch (Exception e) {
            System.out.println(e);
        }

        context.addCallbackParam("jobCostingAndPaymentSaved", true);
    }

    public Boolean validateCurrentJobCosting() {

        EntityManager em = getEntityManager1();

        try {
            // check for valid job
            if (getCurrentJob().getId() == null) {
                return false;
            }

            // check for job report # and description
            if ((currentJob.getReportNumber() == null) || (currentJob.getReportNumber().trim().equals(""))) {
                return false;
            }

            if (currentJob.getJobDescription().trim().equals("")) {
                return false;
            }

        } catch (Exception e) {
           System.out.println(e);
        }

        return true;

    }

    public void saveAndCloseCurrentJob() {

        setRenderJobDetailTab(false);
        saveCurrentJob();

    }

    public void saveCurrentJob() {
        EntityManager em = getEntityManager1();

        if (!validateCurrentJob(em, true)) {
            System.out.println("Job not valid and NOT be save!");
        } else if (isCurrentJobNew() && getUser().getEmployee().getDepartment().getPrivilege().getCanEditJob()) {
            System.out.println("You can enter/edit any new job...saving");
            saveCurrentJob(em);
        } else if (isCurrentJobNew() && getUser().getEmployee().getDepartment().getPrivilege().getCanEnterJob()) {
            System.out.println("You can enter any new job...saving");
            saveCurrentJob(em);
        } else if (isCurrentJobNew() && getUser().getPrivilege().getCanEnterJob()) {
            System.out.println("You can enter any new job...saving");
            saveCurrentJob(em);
        } else if (isCurrentJobNew()
                && getUser().getPrivilege().getCanEnterDepartmentJob()
                && getUser().getEmployee().isMemberOf(getDepartmentAssignedToJob(currentJob, em))) {
            System.out.println("You can enter new jobs for your department...saving");
            saveCurrentJob(em);
        } else if (isCurrentJobNew()
                && getUser().getPrivilege().getCanEnterOwnJob()
                && isCurrentJobJobAssignedToUser()) {
            System.out.println("You can enter new jobs for yourself...saving");
            saveCurrentJob(em);
        } else if (isDirty() && !isCurrentJobNew() && getUser().getPrivilege().getCanEditJob()) {
            System.out.println("You can edit any job...saving");
            saveCurrentJob(em);
        } else if (isDirty() && !isCurrentJobNew()
                && getUser().getPrivilege().getCanEditDepartmentJob()
                && (getUser().getEmployee().isMemberOf(getDepartmentAssignedToJob(currentJob, em))
                || getUser().getEmployee().isMemberOf(currentJob.getDepartment()))) {

            System.out.println("You can edit jobs for your department...saving");
            saveCurrentJob(em);
        } else if (isDirty() && !isCurrentJobNew()
                && getUser().getPrivilege().getCanEditOwnJob()
                && isCurrentJobJobAssignedToUser()) {
            System.out.println("You can edit own jobs...saving");
            saveCurrentJob(em);
        } else if (isJobToBeCopied) {
            System.out.println("Saving cause copy is being created");
            saveCurrentJob(em);
        } else if (currentJob.getIsJobToBeSubcontracted()) {
            System.out.println("Saving cause subcontract is being created");
            saveCurrentJob(em);
        } else if (!isDirty()) {
            System.out.println("Job not dirty so it will not be saved.");
        } else {
            getMain().displayCommonMessageDialog(null, "You may not have sufficient privilege to enter/save this job. "
                    + "Also, please note that you may only enter jobs that are assigned to you or your department. "
                    + "Please contact the IT/MIS Department for further assistance.", "Insufficient Privilege", "alert");
        }
    }

    public void saveUnitCost() {
        EntityManager em = getEntityManager1();
        RequestContext context = RequestContext.getCurrentInstance();

        try {

            // Validate and save objects
            // Department
            Department department = Department.findDepartmentByName(em, getCurrentUnitCost().getDepartment().getName());
            if (department == null) {
                getMain().setInvalidFormFieldMessage("This unit cost cannot be saved because a valid department was not entered.");

                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            } else {
                getCurrentUnitCost().setDepartment(department);
            }

            // Department unit
            DepartmentUnit departmentUnit = DepartmentUnit.findDepartmentUnitByName(em, getCurrentUnitCost().getDepartmentUnit().getName());
            if (departmentUnit == null) {
                getCurrentUnitCost().setDepartmentUnit(DepartmentUnit.getDefaultDepartmentUnit(em, "--"));
            } else {
                getCurrentUnitCost().setDepartmentUnit(departmentUnit);
            }

            // Laboratory unit
            Laboratory laboratory = Laboratory.findLaboratoryByName(em, getCurrentUnitCost().getLaboratory().getName());
            if (laboratory == null) {
                getCurrentUnitCost().setLaboratory(Laboratory.getDefaultLaboratory(em, "--"));
            } else {
                getCurrentUnitCost().setLaboratory(laboratory);
            }

            // Service
            if (getCurrentUnitCost().getService().trim().equals("")) {
                getMain().setInvalidFormFieldMessage("This unit cost cannot be saved because a valid service was not entered.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            }

            // Cost
            if (getCurrentUnitCost().getCost() <= 0.0) {
                getMain().setInvalidFormFieldMessage("This unit cost cannot be saved because a valid cost was not entered.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            }

            // Effective date
            if (getCurrentUnitCost().getEffectiveDate() == null) {
                getMain().setInvalidFormFieldMessage("This unit cost cannot be saved because a valid effective date was not entered.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            }

            // save job to database and check for errors
            em.getTransaction().begin();

            Long id = BusinessEntityUtils.saveBusinessEntity(em, currentUnitCost);
            if (id == null) {
                context.execute("undefinedErrorDialog.show();");
                sendErrorEmail("An error occured while saving this unit cost",
                        "Unit cost save error occured");
                return;
            }

            em.getTransaction().commit();
            setDirty(false);

        } catch (Exception e) {
            context.execute("undefinedErrorDialog.show();");
            System.out.println(e);
            // send error message to developer's email
            sendErrorEmail("An exception occurred while saving a unit cost!",
                    "\nJMTS User: " + getUser().getUsername()
                    + "\nDate/time: " + new Date()
                    + "\nException detail: " + e);
        }       
    }

    public void saveCurrentAndLoadOtherJob(ActionEvent actionEvent) {
        EntityManager em = getEntityManager1();

        if (validateCurrentJob(em, true)) {
            saveCurrentJob(em);
        }

        loadJob();

    }

    public Boolean checkJobEntryPrivilege(EntityManager em, RequestContext context) {
        // prompt to save modified job before attempting to create new job
        if (getUser().getPrivilege().getCanEnterJob()
                || getUser().getPrivilege().getCanEnterDepartmentJob()
                || getUser().getDepartment().getPrivilege().getCanEnterJob()
                || getUser().getPrivilege().getCanEnterOwnJob()) {
            return true;
        } else {
            displayUserPrivilegeDialog(context,
                    "Job Entry Privilege",
                    "You do not have job entry privilege.");

            return false;
        }
    }

    public Boolean getIsClientNameValid() {
        return BusinessEntityUtils.validateName(currentJob.getClient().getName());
    }

    public Boolean validateCurrentJob(EntityManager em, Boolean displayErrorMessage) {
        RequestContext context = RequestContext.getCurrentInstance();

        MethodResult result = getCurrentJob().validate(em);
        if (!result.isSuccess()) {
            if (displayErrorMessage) {
                getMain().setInvalidFormFieldMessage(result.getMessage());
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
            }

            setDirty(false);

            return false;
        }

        return true;
    }

    public Boolean saveCurrentJob(EntityManager em) {

        Date now = new Date();
        RequestContext context = RequestContext.getCurrentInstance();
        JobSequenceNumber nextJobSequenceNumber = null;
        boolean jobEmailAlertsActivated = Boolean.parseBoolean(SystemOption.
                findSystemOptionByName(em,
                        "jobEmailAlertsActivated").getOptionValue());

        try {

            // Do not save changed job if it's already marked as completed in the database
            if (getCurrentJob().getId() != null) {
                Job job = Job.findJobById(em, getCurrentJob().getId());
                if (job.getJobStatusAndTracking().getWorkProgress().equals("Completed")
                        && !getUser().getEmployee().isMemberOf(getDepartmentBySystemOptionDeptId("invoicingDepartmentId"))
                        && !getUser().getPrivilege().getCanBeJMTSAdministrator()
                        && !isUserDepartmentSupervisor(getCurrentJob())) {
                    setDirty(false);
                    doJobSearch(searchManager.getCurrentSearchParameters());
                    getMain().displayCommonMessageDialog(null,
                            "This job is marked as completed so changes cannot be saved. You may contact your department's supervisor or a system administrator.",
                            "Job Cannot Be Saved", "info");

                    return false;
                }
            }

            em.getTransaction().begin();

            // Make sure department is not same as subcontracted department
            if (currentJob.getDepartment().getName().equals(currentJob.getSubContractedDepartment().getName())) {
                currentJob.setSubContractedDepartment(getDefaultDepartment(em, "--"));
            }

            // set date enetered
            if (currentJob.getJobStatusAndTracking().getDateAndTimeEntered() == null) {
                currentJob.getJobStatusAndTracking().setDateAndTimeEntered(now);
            }
            // Get employee for later use
            Employee employee = Employee.findEmployeeById(em, getUser().getEmployee().getId());
            // This means this this is a new job so set user and person who entered the job
            if (employee != null) {
                if (currentJob.getJobStatusAndTracking().getEnteredBy().getId() == null) {
                    currentJob.getJobStatusAndTracking().setEnteredBy(employee);
                    currentJob.getJobStatusAndTracking().setEditedBy(employee);
                }
            }

            // Update re the person who last edited/entered the job etc.
            if (isDirty()) {
                currentJob.getJobStatusAndTracking().setDateStatusEdited(now);
                currentJob.getJobStatusAndTracking().setEditedBy(employee);
            }

            // update work progress
            if (currentJob.getJobStatusAndTracking().getWorkProgress() == null) {
                currentJob.getJobStatusAndTracking().setWorkProgress("Not started");
            }

            // save job and check for save error
            // modify job number with sequence number if required
            if (currentJob.getAutoGenerateJobNumber()) {
                //if (currentJob.getJobSequenceNumber() == null) {
                if ((currentJob.getId() == null) && (currentJob.getJobSequenceNumber() == null)) {
                    nextJobSequenceNumber = JobSequenceNumber.findNextJobSequenceNumber(em, currentJob.getYearReceived());
                    currentJob.setJobSequenceNumber(nextJobSequenceNumber.getSequentialNumber());
                    currentJob.setJobNumber(getJobNumber(currentJob, em));
                } else {
                    currentJob.setJobNumber(getJobNumber(currentJob, em));
                }
            }

            // Update the sampledby field of the job samples
            if (currentJob.getJobSamples().size() > 0) {
                for (JobSample jobSample : currentJob.getJobSamples()) {

                    if (jobSample.getSampledBy().getId() != null) {
                        Employee e = Employee.findEmployeeByName(em, jobSample.getSampledBy().getName());
                        if (e != null) {
                            jobSample.setSampledBy(e);
                        }
                    }

                    /// Save new samples in list first 
                    if (jobSample.getId() == null) {
                        BusinessEntityUtils.saveBusinessEntity(em, jobSample);
                    }
                }
            } else {
                createNewJobSample(null);
                selectedJobSample.setDescription("--");
                BusinessEntityUtils.saveBusinessEntity(em, selectedJobSample);
            }

            Long id = BusinessEntityUtils.saveBusinessEntity(em, currentJob);
            if (id == null) {
                // set seq. number to null to ensure that the next sequence #
                // is retrieved with the next job save attempt.                
                currentJob.setJobNumber(getJobNumber(currentJob, em));
                context.execute("undefinedErrorDialog.show();");
                sendErrorEmail("An error occured while saving job (Null ID)" + currentJob.getJobNumber(),
                        "Job save error occured");

                return false;

            } else if (id == 0L) {
                // set seq. number to null to ensure that the next sequence #
                // is retrieved with the next job save attempt.                
                currentJob.setJobNumber(getJobNumber(currentJob, em));
                context.execute("undefinedErrorDialog.show();");
                sendErrorEmail("An error occured while saving job (0L ID)" + currentJob.getJobNumber(),
                        "Job save error occured");

                return false;
            } else {
                currentJobId = id;
                // save job sequence number only if job save was successful
                if (nextJobSequenceNumber != null) {
                    BusinessEntityUtils.saveBusinessEntity(em, nextJobSequenceNumber);
                }

                // Send job email alerts if the option is activated
                try {
                    // Send email alerts if any was flagged to be sent                
                    if (jobEmailAlertsActivated) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("Generating and sending emails...");
                                generateEmailAlerts();
                            }
                        }).start();

                        //generateEmailAlerts();
                    } else {
                        System.out.println("Email alerts will not be generated!");
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }

            }

            em.getTransaction().commit();
            setIsJobToBeCopied(false);
            currentJob.setIsJobToBeSubcontracted(false);
            setDirty(false);
        } catch (Exception e) {
            currentJob.setJobNumber(getJobNumber(currentJob, em));
            context.execute("undefinedErrorDialog.show();");
            System.out.println(e);
            // send error message to developer's email
            sendErrorEmail("An exception occurred while saving a job!",
                    "Job number: " + currentJob.getJobNumber()
                    + "\nJMTS User: " + getUser().getUsername()
                    + "\nDate/time: " + new Date()
                    + "\nException detail: " + e);
        }

        return true;

    }

    public String getCompletedJobCostingEmailMessage(Job job) {
        String message = "";
        DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");

        message = message + "Dear Colleague,<br><br>";
        message = message + "The costing for a job with the following details was completed via the <a href='http://boshrmapp:8080/jmts'>Job Management & Tracking System (JMTS)</a>:<br><br>";
        message = message + "<span style='font-weight:bold'>Job number: </span>" + job.getJobNumber() + "<br>";
        message = message + "<span style='font-weight:bold'>Client: </span>" + job.getClient().getName() + "<br>";
        if (!job.getSubContractedDepartment().getName().equals("--")) {
            message = message + "<span style='font-weight:bold'>Department: </span>" + job.getSubContractedDepartment().getName() + "<br>";
        } else {
            message = message + "<span style='font-weight:bold'>Department: </span>" + job.getDepartment().getName() + "<br>";
        }
        message = message + "<span style='font-weight:bold'>Date submitted: </span>" + formatter.format(job.getJobStatusAndTracking().getDateSubmitted()) + "<br>";
        message = message + "<span style='font-weight:bold'>Current assignee: </span>" + BusinessEntityUtils.getPersonFullName(job.getAssignedTo(), Boolean.FALSE) + "<br>";
        message = message + "<span style='font-weight:bold'>Task/Sample descriptions: </span>" + job.getJobSampleDescriptions() + "<br><br>";
        message = message + "As the department's supervisor/head, you are required to review and approve this costing. An email will be automatically sent to the Finance department after approval.<br><br>";
        message = message + "If this job was incorrectly assigned to your department, the department supervisor should contact the person who entered/assigned the job.<br><br>";
        message = message + "This email was automatically generated and sent by the <a href='http://boshrmapp:8080/jmts'>JMTS</a>. Please DO NOT reply.<br><br>";
        message = message + "Signed<br>";
        message = message + "Job Manager";

        return message;
    }

    public String getApprovedJobCostingEmailMessage(Job job) {
        String message = "";
        DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        EntityManager em = getEntityManager1();

        message = message + "Dear Colleague,<br><br>";
        message = message + "The costing for a job with the following details was approved via the <a href='http://boshrmapp:8080/jmts'>Job Management & Tracking System (JMTS)</a>:<br><br>";
        message = message + "<span style='font-weight:bold'>Job number: </span>" + job.getJobNumber() + "<br>";
        message = message + "<span style='font-weight:bold'>Client: </span>" + job.getClient().getName() + "<br>";
        if (!job.getSubContractedDepartment().getName().equals("--")) {
            message = message + "<span style='font-weight:bold'>Department: </span>" + job.getSubContractedDepartment().getName() + "<br>";
        } else {
            message = message + "<span style='font-weight:bold'>Department: </span>" + job.getDepartment().getName() + "<br>";
        }
        message = message + "<span style='font-weight:bold'>Date submitted: </span>" + formatter.format(job.getJobStatusAndTracking().getDateSubmitted()) + "<br>";
        message = message + "<span style='font-weight:bold'>Department/Unit Head: </span>" + BusinessEntityUtils.getPersonFullName(getDepartmentAssignedToJob(job, em).getHead(), false) + "<br>";
        message = message + "<span style='font-weight:bold'>Task/Sample descriptions: </span>" + job.getJobSampleDescriptions() + "<br><br>";
        message = message + "If this email was sent to you in error, please contact the department referred to above.<br><br>";
        message = message + "This email was automatically generated and sent by the <a href='http://boshrmapp:8080/jmts'>JMTS</a>. Please DO NOT reply.<br><br>";
        message = message + "Signed<br>";
        message = message + "Job Manager";

        return message;
    }

    public String getNewJobEmailMessage(Job job) {
        String message = "";
        DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");

        message = message + "Dear Colleague,<br><br>";
        message = message + "A job with the following details was assigned to your department via the <a href='http://boshrmapp:8080/jmts'>Job Management & Tracking System (JMTS)</a>:<br><br>";
        message = message + "<span style='font-weight:bold'>Job number: </span>" + job.getJobNumber() + "<br>";
        message = message + "<span style='font-weight:bold'>Client: </span>" + job.getClient().getName() + "<br>";
        if (!job.getSubContractedDepartment().getName().equals("--")) {
            message = message + "<span style='font-weight:bold'>Department: </span>" + job.getSubContractedDepartment().getName() + "<br>";
        } else {
            message = message + "<span style='font-weight:bold'>Department: </span>" + job.getDepartment().getName() + "<br>";
        }
        message = message + "<span style='font-weight:bold'>Date submitted: </span>" + formatter.format(job.getJobStatusAndTracking().getDateSubmitted()) + "<br>";
        message = message + "<span style='font-weight:bold'>Current assignee: </span>" + BusinessEntityUtils.getPersonFullName(job.getAssignedTo(), Boolean.FALSE) + "<br>";
        message = message + "<span style='font-weight:bold'>Entered by: </span>" + BusinessEntityUtils.getPersonFullName(job.getJobStatusAndTracking().getEnteredBy(), Boolean.FALSE) + "<br>";
        message = message + "<span style='font-weight:bold'>Task/Sample descriptions: </span>" + job.getJobSampleDescriptions() + "<br><br>";
        message = message + "If you are the department's supervisor, you should immediately ensure that the job was correctly assigned to your staff member who will see to its completion.<br><br>";
        message = message + "If this job was incorrectly assigned to your department, the department supervisor should contact the person who entered/assigned the job.<br><br>";
        message = message + "This email was automatically generated and sent by the <a href='http://boshrmapp:8080/jmts'>JMTS</a>. Please DO NOT reply.<br><br>";
        message = message + "Signed<br>";
        message = message + "Job Manager";

        return message;
    }

    public String getUpdatedJobEmailMessage(Job job) {
        String message = "";
        DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");

        message = message + "Dear Colleague,<br><br>";
        message = message + "A job with the following details was updated by someone outside of your department via the <a href='http://boshrmapp:8080/jmts'>Job Management & Tracking System (JMTS)</a>:<br><br>";
        message = message + "<span style='font-weight:bold'>Job number: </span>" + job.getJobNumber() + "<br>";
        message = message + "<span style='font-weight:bold'>Client: </span>" + job.getClient().getName() + "<br>";
        if (!job.getSubContractedDepartment().getName().equals("--")) {
            message = message + "<span style='font-weight:bold'>Department: </span>" + job.getSubContractedDepartment().getName() + "<br>";
        } else {
            message = message + "<span style='font-weight:bold'>Department: </span>" + job.getDepartment().getName() + "<br>";
        }
        message = message + "<span style='font-weight:bold'>Date submitted: </span>" + formatter.format(job.getJobStatusAndTracking().getDateSubmitted()) + "<br>";
        message = message + "<span style='font-weight:bold'>Current assignee: </span>" + BusinessEntityUtils.getPersonFullName(job.getAssignedTo(), Boolean.FALSE) + "<br>";
        message = message + "<span style='font-weight:bold'>Updated by: </span>" + BusinessEntityUtils.getPersonFullName(job.getJobStatusAndTracking().getEditedBy(), Boolean.FALSE) + "<br>";
        message = message + "<span style='font-weight:bold'>Task/Sample descriptions: </span>" + job.getJobSampleDescriptions() + "<br><br>";
        message = message + "You are being informed of this update so that you may take the requisite action.<br><br>";
        message = message + "This email was automatically generated and sent by the <a href='http://boshrmapp:8080/jmts'>JMTS</a>. Please DO NOT reply.<br><br>";
        message = message + "Signed<br>";
        message = message + "Job Manager";

        return message;
    }

    /**
     * Update/create alert for the current job if the job is not completed.
     *
     * @param em
     */
    public void updateAlert(EntityManager em) throws Exception {
        if (getCurrentJob().getJobStatusAndTracking().getCompleted() == null) {
            em.getTransaction().begin();

            Alert alert = Alert.findFirstAlertByOwnerId(em, currentJob.getId());
            if (alert == null) { // This seems to be a new job
                alert = new Alert(currentJob.getId(), new Date(), "Job entered");
                em.persist(alert);
            } else {
                em.refresh(alert);
                alert.setActive(true);
                alert.setDueTime(new Date());
                alert.setStatus("Job saved");
            }

            em.getTransaction().commit();
        } else if (!getCurrentJob().getJobStatusAndTracking().getCompleted()) {
            em.getTransaction().begin();

            Alert alert = Alert.findFirstAlertByOwnerId(em, currentJob.getId());
            if (alert == null) { // This seems to be a new job
                alert = new Alert(currentJob.getId(), new Date(), "Job saved");
                em.persist(alert);
            } else {
                em.refresh(alert);
                alert.setActive(true);
                alert.setDueTime(new Date());
                alert.setStatus("Job saved");
            }

            em.getTransaction().commit();
        }

    }

    public void sendErrorEmail(String subject, String message) {
        try {
            // send error message to developer's email            
            BusinessEntityUtils.postMail(null, null, subject, message);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    /**
     * To be applied when sample if saved
     */
    public void updateJobSampleReference() {
        // update reference while ensuring number of samples is not less than 1
        // or greater than 700 (for now but to be made system option)
        if (selectedJobSample.getSampleQuantity() != null) {
            if (selectedJobSample.getSampleQuantity() == 1) {
                selectedJobSample.setReference(BusinessEntityUtils.getAlphaCode(selectedJobSample.getReferenceIndex()));
            } else {
                selectedJobSample.setReference(BusinessEntityUtils.getAlphaCode(selectedJobSample.getReferenceIndex()) + "-"
                        + BusinessEntityUtils.getAlphaCode(selectedJobSample.getReferenceIndex()
                                + selectedJobSample.getSampleQuantity() - 1));
            }
        }
        setDirty(true);
    }

    public void updateProductQuantity() {
        setDirty(true);
    }

    public void deleteJobSample() {
        EntityManager em = getEntityManager1();

        // update number of samples
        if ((currentJob.getNumberOfSamples() - selectedJobSample.getSampleQuantity()) > 0) {
            currentJob.setNumberOfSamples(currentJob.getNumberOfSamples() - selectedJobSample.getSampleQuantity());
        } else {
            currentJob.setNumberOfSamples(0L);
        }

        List<JobSample> samples = currentJob.getJobSamples();
        int index = 0;
        for (JobSample sample : samples) {
            if (sample.getReference().equals(selectedJobSample.getReference())) {
                // removed sample record
                samples.remove(index);
                break;
            }
            ++index;
        }

        updateSampleReferences();

        currentJob.setJobNumber(getCurrentJobNumber());
        selectedJobSample = new JobSample();

        setDirty(Boolean.TRUE);
    }

    public void updateDepartments(Job job, EntityManager em) {

        if (!job.getDepartment().getName().equals("")) {
            Department department = Department.findDepartmentByName(em, job.getDepartment().getName());
            if (department != null) {
                job.setDepartment(department);
            }
        }
        // Update subcontracted department
        if (!job.getSubContractedDepartment().getName().equals("")) {
            Department subContractedDepartment = Department.findDepartmentByName(em, job.getSubContractedDepartment().getName());
            job.setSubContractedDepartment(subContractedDepartment);
        }

    }

    public void deleteCostComponent() {
        deleteCostComponentByName(selectedCostComponent.getName());
    }

    // Remove this and other code out of JobManager? Put in JobCostingAndPayment or Job?
    public void deleteCostComponentByName(String componentName) {

        List<CostComponent> components = currentJob.getJobCostingAndPayment().getAllSortedCostComponents();
        int index = 0;
        for (CostComponent costComponent : components) {
            if (costComponent.getName().equals(componentName)) {
                components.remove(index);
                setDirty(Boolean.TRUE);

                break;
            }
            ++index;
        }

        updateFinalCost();
        updateAmountDue();
    }

    public void editJobSample(ActionEvent event) {
        jobSampleDialogTabViewActiveIndex = 0;

        addJobSample = false;
    }

    public void editCashPayment(ActionEvent event) {
        System.out.println("Edit cash payment to be impl.");
    }

    public void copyJobSample() {

        addJobSample = true;
        selectedJobSample = new JobSample(selectedJobSample);
        selectedJobSample.setReferenceIndex(getCurrentNumberOfJobSamples());
        // Init sample    
        if (selectedJobSample.getSampleQuantity() == 1L) {
            selectedJobSample.setReference(BusinessEntityUtils.getAlphaCode(getCurrentNumberOfJobSamples()));
        } else {
            selectedJobSample.setReference(BusinessEntityUtils.getAlphaCode(getCurrentNumberOfJobSamples()) + "-"
                    + BusinessEntityUtils.getAlphaCode(getCurrentNumberOfJobSamples()
                            + selectedJobSample.getSampleQuantity() - 1));
        }

        jobSampleDialogTabViewActiveIndex = 0;
    }

    public void editCostComponent(ActionEvent event) {
    }

    public void createNewCashPayment(ActionEvent event) {
        addCashPayment = true;
        selectedCashPayment = new CashPayment();
    }

    public void createNewJobSample(ActionEvent event) {

        if (getCurrentJob().hasOnlyDefaultJobSample()) {
            addJobSample = false;
            selectedJobSample = getCurrentJob().getJobSamples().get(0);
            selectedJobSample.setDescription("");
        } else {
            addJobSample = true;
            selectedJobSample = new JobSample();
            // Init sample
            selectedJobSample.setJobId(currentJob.getId());
            selectedJobSample.setSampleQuantity(1L);
            selectedJobSample.setQuantity(1L);

            selectedJobSample.setReferenceIndex(getCurrentNumberOfJobSamples());

            if (selectedJobSample.getSampleQuantity() == 1L) {
                selectedJobSample.setReference(BusinessEntityUtils.getAlphaCode(selectedJobSample.getReferenceIndex()));
            } else {
                selectedJobSample.setReference(BusinessEntityUtils.getAlphaCode(selectedJobSample.getReferenceIndex()) + "-"
                        + BusinessEntityUtils.getAlphaCode(selectedJobSample.getReferenceIndex()
                                + selectedJobSample.getSampleQuantity() - 1));
            }
        }

        selectedJobSample.setDateSampled(new Date());
        jobSampleDialogTabViewActiveIndex = 0;
    }

    public void createNewCostComponent(ActionEvent event) {
        // init sample
        addCostComponent = true;
        selectedCostComponent = new CostComponent();
    }

    public void cancelCashPaymentEdit() {

    }

    public void cancelJobSampleEdit() {
        addJobSample = false;
        //find and revert the edit of job sample
        for (JobSample jobSample : currentJob.getJobSamples()) {
            if (jobSample.getReference().equals(selectedJobSample.getReference())) {
                jobSample.setId(backupSelectedJobSample.getId());
                jobSample.setJobId(backupSelectedJobSample.getJobId());
                jobSample.setReference(backupSelectedJobSample.getReference());
                jobSample.setReferenceIndex(backupSelectedJobSample.getReferenceIndex());
                jobSample.setSampleQuantity(backupSelectedJobSample.getSampleQuantity());
                jobSample.setQuantity(backupSelectedJobSample.getQuantity());
                jobSample.setUnitOfMeasure(backupSelectedJobSample.getUnitOfMeasure());
                jobSample.setDescription(backupSelectedJobSample.getDescription());
                jobSample.setDateReceived(backupSelectedJobSample.getDateReceived());
                jobSample.setMethodOfDisposal(backupSelectedJobSample.getMethodOfDisposal());
                backupSelectedJobSample = null;
                setDirty(false);
                return;
            }
        }
        // at this point the edited sample was not found in the current list of samples
        selectedJobSample = new JobSample();
        backupSelectedJobSample = null;
        setDirty(false);
    }

    private Long getCurrentNumberOfJobSamples() {
        if (currentJob.getNumberOfSamples() == null) {
            currentJob.setNumberOfSamples(0L);
            return currentJob.getNumberOfSamples();
        } else {
            return currentJob.getNumberOfSamples();
        }
    }

    /**
     * Checks maximum allowed samples and groups. Currently not used
     */
    public void checkNumberOfJobSamplesAndGroups() {
        EntityManager em = getEntityManager1();

        // setup context for client response
        RequestContext context = RequestContext.getCurrentInstance();
        // check for max sample
        int maxSamples = Integer.parseInt(SystemOption.findSystemOptionByName(em, "maximumJobSamples").getOptionValue());
        if (getCurrentNumberOfJobSamples() == maxSamples) {
            context.addCallbackParam("maxJobSamplesReached", true);
        }
        // check for max sample groups
        int maxGropus = Integer.parseInt(SystemOption.findSystemOptionByName(em, "maximumJobSampleGroups").getOptionValue());
        if (currentJob.getJobSamples().size() == maxGropus) {
            context.addCallbackParam("maxJobSampleGroupsReached", true);
        }
    }

    private void setNumberOfSamples() {
        currentJob.setNumberOfSamples(0L);
        for (int i = 0; i < currentJob.getJobSamples().size(); i++) { // find total
            if (currentJob.getJobSamples().get(i).getSampleQuantity() == null) {
                currentJob.getJobSamples().get(i).setSampleQuantity(1L);
            }
            currentJob.setNumberOfSamples(currentJob.getNumberOfSamples()
                    + currentJob.getJobSamples().get(i).getSampleQuantity());
        }
    }

    private void updateSampleReferences() {
        Long refIndex = 0L;

        ArrayList<JobSample> samplesCopy = new ArrayList<>(currentJob.getJobSamples());
        currentJob.getJobSamples().clear();

        for (JobSample jobSample : samplesCopy) {

            jobSample.setReferenceIndex(refIndex);
            if (jobSample.getSampleQuantity() == 1) {
                jobSample.setReference(BusinessEntityUtils.getAlphaCode(refIndex));
            } else {
                jobSample.setReference(BusinessEntityUtils.getAlphaCode(refIndex) + "-"
                        + BusinessEntityUtils.getAlphaCode(refIndex + jobSample.getSampleQuantity() - 1));
            }

            currentJob.getJobSamples().add(jobSample);

            refIndex = refIndex + jobSample.getSampleQuantity();

        }
    }

    public void updateCostingComponent() {
        if (addCostComponent) {
            addCostComponent = false;
            currentJob.getJobCostingAndPayment().getCostComponents().add(selectedCostComponent);
        }
        updateFinalCost();
        updateAmountDue();
    }

    public void updateFinalCost() {
        currentJob.getJobCostingAndPayment().setFinalCost(currentJob.getJobCostingAndPayment().getTotalJobCostingsAmount());
        setDirty(true);
    }

    public void updateAmountDue() {
        currentJob.getJobCostingAndPayment().setAmountDue(currentJob.getJobCostingAndPayment().calculateAmountDue());
        setDirty(true);
    }

    public Boolean getCanApplyGCT() {
        return JobCostingAndPayment.getCanApplyGCT(getCurrentJob());
    }

    public void editJob() {
        RequestContext context = RequestContext.getCurrentInstance();

        setRenderJobDetailTab(true);
        context.update("mainTabViewForm");
        context.execute("mainTabViewVar.select(1);");
    }

    public void editJobCosting() {
        // Nothing to do yet
        System.out.println("Editing job costing...");
    }

    public void updateJobCosting() {

        EntityManager em = getEntityManager1();

        //updateDepartments(currentJob, em);
        Department dept = getDepartmentAssignedToJob(currentJob, em);
        if (dept != null) {
            if (getDepartmentAssignedToJob(currentJob, em).getJobCostingType().equals("Sample-based")) {
                createSampleBasedJobCostings(currentJob.getJobCostingAndPayment());
            } else {
                createDefaultJobCostings(currentJob.getJobCostingAndPayment());
            }

            // Add sub-contract oostings if any
            List<Job> jobs = Job.findJobsByYearReceivedAndJobSequenceNumber(em,
                    currentJob.getYearReceived(),
                    currentJob.getJobSequenceNumber());
            if (jobs != null) {
                for (Job job : jobs) {
                    if (job.isSubContracted() && !currentJob.isSubContracted()
                            && (job.getJobStatusAndTracking().getWorkProgress().equals("Completed"))) {
                        String ccName = "Subcontract to " + job.getSubContractedDepartment().getName() + " (" + job.getJobNumber() + ")";
                        // Check that this cost component does not already exist.
                        // The assumption is that only one component will be found if any
                        if (!CostComponent.findCostComponentsByName(ccName,
                                currentJob.getJobCostingAndPayment().getCostComponents()).isEmpty()) {
                            deleteCostComponentByName(ccName);
                        }
                        CostComponent cc
                                = new CostComponent(
                                        ccName,
                                        job.getJobCostingAndPayment().getFinalCost(),
                                        true, false);

                        currentJob.getJobCostingAndPayment().getCostComponents().add(cc);
                        setDirty(true);
                    }
                }
            }
        }
        // NB: Ensure that amount due is recalc. in case something affects
        // taxes was changed
        currentJob.getJobCostingAndPayment().calculateAmountDue();
    }

    public void okCashPayment() {
        // tk
        // update jobCostingAndPayment.receiptNumber...append receipt number.
        // exec. jobManager.updateTotalDeposit(), jobManager.updateAmountDue() is exec by jobManager.updateTotalDeposit()?
        System.out.println("Ok cashpayment");

    }

    public void okJobSample() {
        RequestContext context = RequestContext.getCurrentInstance();
        EntityManager em = getEntityManager1();

        // get sampled by
        selectedJobSample.setSampledBy(Employee.findEmployeeByName(em, selectedJobSample.getSampledBy().getName()));

        // validate form entries
        if (selectedJobSample.getDateSampled() == null) {
            context.execute("jobSampleDialog.show();");
            context.execute("jobSampleRequiredFieldMessageDialog.show();");
            return;
        } else if (selectedJobSample.getSampleQuantity() == null) {
            context.execute("jobSampleDialog.show();");
            context.execute("jobSampleRequiredFieldMessageDialog.show();");
            return;
        } else if (selectedJobSample.getName().trim().isEmpty()) {
            context.execute("jobSampleDialog.show();");
            context.execute("jobSampleRequiredFieldMessageDialog.show();");
            return;
        } else if (selectedJobSample.getQuantity() == null) {
            context.execute("jobSampleDialog.show();");
            context.execute("jobSampleRequiredFieldMessageDialog.show();");
            return;
        } else {
            context.execute("jobSampleDialog.hide();");
        }

        updateJobSampleReference();
        updateProductQuantity();
        if (addJobSample) {
            currentJob.getJobSamples().add(selectedJobSample);
            addJobSample = false;
        }
        setNumberOfSamples();

        updateSampleReferences();

        // Update department
        if (!currentJob.getDepartment().getName().equals("")) {
            Department department = Department.findDepartmentByName(em, currentJob.getDepartment().getName());
            if (department != null) {
                currentJob.setDepartment(department);
            }
        }
        // Update subcontracted department
        if (!currentJob.getSubContractedDepartment().getName().equals("")) {
            Department subContractedDepartment = Department.findDepartmentByName(em, currentJob.getSubContractedDepartment().getName());
            currentJob.setSubContractedDepartment(subContractedDepartment);
        }

        currentJob.setJobNumber(getCurrentJobNumber());
        jobSampleDialogTabViewActiveIndex = 0;

    }

    public JobSample getSelectedJobSample() {
        return selectedJobSample;
    }

    public void setSelectedJobSample(JobSample selectedJobSample) {
        this.selectedJobSample = selectedJobSample;
        if (selectedJobSample != null) {
            backupSelectedJobSample = new JobSample();
            backupSelectedJobSample.setId(selectedJobSample.getId());
            backupSelectedJobSample.setJobId(selectedJobSample.getJobId());
            backupSelectedJobSample.setReference(selectedJobSample.getReference());
            backupSelectedJobSample.setReferenceIndex(selectedJobSample.getReferenceIndex());
            backupSelectedJobSample.setSampleQuantity(selectedJobSample.getSampleQuantity());
            backupSelectedJobSample.setQuantity(selectedJobSample.getQuantity());
            backupSelectedJobSample.setUnitOfMeasure(selectedJobSample.getUnitOfMeasure());
            backupSelectedJobSample.setDescription(selectedJobSample.getDescription());
            backupSelectedJobSample.setDateReceived(selectedJobSample.getDateReceived());
            backupSelectedJobSample.setMethodOfDisposal(selectedJobSample.getMethodOfDisposal());
        }
    }

    public String getJobAssignee() {
        if (currentJob.getAssignedTo() != null) {
            return currentJob.getAssignedTo().getLastName() + ", " + currentJob.getAssignedTo().getFirstName();
        } else {
            return "";
        }
    }

    public void loadJob() {

        EntityManager em = getEntityManager1();
        RequestContext context = RequestContext.getCurrentInstance();

        // prompt to save modified job before attempting to create new job
        if (isDirty()) {
            context.addCallbackParam("jobDirty", true);
            return;
        }

        if ((selectedJobId != 0L) && (selectedJobId != null)) {
            currentJob = Job.findJobById(em, selectedJobId);
            // create job costings if it does not exist
            currentJobId = currentJob.getId();
            // set accpac custmomer name for later use
            accPacCustomer = new AccPacCustomer();
            accPacCustomer.setCustomerName(currentJob.getClient().getName());
            // update status if we are using accpac customer list and database
            if (useAccPacCustomerList) {
                updateCreditStatusSearch();
            }
        } else {
            createJob(em, false);
        }

        setDirty(false);

    }

    public String getCurrentJobNumber() {
        return getJobNumber(currentJob, getEntityManager1());
    }

    public Date getJobSubmissionDate() {
        if (currentJob != null) {
            if (currentJob.getJobStatusAndTracking().getDateSubmitted() != null) {
                return currentJob.getJobStatusAndTracking().getDateSubmitted();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setJobSubmissionDate(Date date) {
        currentJob.getJobStatusAndTracking().setDateSubmitted(date);
    }

    public Date getJobCostingDate() {
        return currentJob.getJobStatusAndTracking().getCostingDate();
    }

    public void setJobCostingDate(Date date) {
        currentJob.getJobStatusAndTracking().setCostingDate(date);
    }

    public void handleJobCostingDateSelect(SelectEvent event) {
        Date selectedDate = (Date) event.getObject();

        setJobCostingDate(selectedDate);

        setDirty(true);
    }

    public Date getJobSampleReceivalDate() {
        if (selectedJobSample != null) {
            if (selectedJobSample.getDateReceived() != null) {
                return selectedJobSample.getDateReceived();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setJobSampleReceivalDate(Date date) {
        selectedJobSample.setDateReceived(date);
    }

    public Long getSelectedJobId() {

        return selectedJobId;
    }

    public void setSelectedJobId(Long selectedJobId) {
        this.selectedJobId = selectedJobId;
    }

    public String getDateString(Calendar c, String delim, String format, String sep) {
        // date delimiter
        if (delim == null) {
            delim = "'";
        }
        // date format, YMD, MDY etc
        if (format == null) {
            format = "YMD";
        }
        // separator of date fields
        if (sep == null) {
            sep = "-";
        }

        if (c != null) {
            String str;
            String year = getFourDigitString(c.get(Calendar.YEAR));
            int month = c.get(Calendar.MONTH) + 1;
            int day = c.get(Calendar.DAY_OF_MONTH);
            if (format.equals("YMD")) {
                str = delim + year + sep + month + sep + day + delim;
            } else if (format.equals("MDY")) {
                str = delim + month + sep + day + sep + year + delim;
            } else if (format.equals("DMY")) {
                str = delim + day + sep + month + sep + year + delim;
            } else {
                str = delim + year + sep + month + sep + day + delim;
            }
            return str;
        }

        return null;
    }

    public String getDateString(Date d, String delim, String format, String sep) {
        if (d != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            return BusinessEntityUtils.getDateString(c, delim, format, sep);
        } else {
            return "";
        }
    }

    public void handleDateSubmittedSelect() {

        EntityManager em = getEntityManager1();

        currentJob.setJobNumber(getJobNumber(currentJob, getEntityManager1()));

        JobCostingAndPayment.setJobCostingTaxes(em, currentJob);
        if (currentJob.getId() != null) {
            updateAllJobCostings();
        }
    }

    public void handleCurrentPeriodStartDateSelect(SelectEvent event) {
        reportSearchParameters.getDatePeriod().setStartDate((Date) event.getObject());
        updateJobReport();
    }

    public void handleCurrentPeriodEndDateSelect(SelectEvent event) {
        reportSearchParameters.getDatePeriod().setEndDate((Date) event.getObject());
        updateJobReport();
    }

    public void handlePreviousPeriodStartDateSelect(SelectEvent event) {
        previousDatePeriod.setStartDate((Date) event.getObject());
        updateJobReport();
    }

    public void handlePreviousPeriodEndDateSelect(SelectEvent event) {
        previousDatePeriod.setEndDate((Date) event.getObject());
        updateJobReport();
    }

    public void updateDateJobCompleted(SelectEvent event) {
        Date selectedDate = (Date) event.getObject();

        currentJob.getJobStatusAndTracking().setDateOfCompletion(selectedDate);

        setDirty(Boolean.TRUE);
    }

    public void updateDateExpectedCompletionDate(SelectEvent event) {
        Date selectedDate = (Date) event.getObject();

        currentJob.getJobStatusAndTracking().setExpectedDateOfCompletion(selectedDate);

        setDirty(Boolean.TRUE);
    }

    public void updateDateSamplesCollected(SelectEvent event) {
        Date selectedDate = (Date) event.getObject();

        currentJob.getJobStatusAndTracking().setDateSamplesCollected(selectedDate);

        setDirty(Boolean.TRUE);
    }

    public void updateDateDocsCollected(SelectEvent event) {
        Date selectedDate = (Date) event.getObject();

        currentJob.getJobStatusAndTracking().setDateDocumentCollected(selectedDate);

        setDirty(Boolean.TRUE);
    }

    public List<SelectItem> getDatePeriods() {
        ArrayList<SelectItem> datePeriods = new ArrayList<>();

        // add items
        if (jobReport.getName().equals("Monthly report")) {
            for (String name : DatePeriod.getDatePeriodNames()) {
                // get only month date periods                
                datePeriods.add(new SelectItem(name, name));

            }
        } else {
            for (String name : DatePeriod.getDatePeriodNames()) {
                datePeriods.add(new SelectItem(name, name));
            }
        }

        return datePeriods;
    }

    public List<JobCostingAndPayment> completeJobCostingAndPaymentName(String query) {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            List<JobCostingAndPayment> results = JobCostingAndPayment.findAllJobCostingAndPaymentsByDepartmentAndName(em,
                    getUser().getEmployee().getDepartment().getName(), query);

            return results;

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List<BusinessOffice> completeBusinessOffice(String query) {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            List<BusinessOffice> offices = BusinessOffice.findActiveBusinessOfficesByName(em, query);
           
            return offices;
        } catch (Exception e) {
            
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public List<Address> completeClientAddress(String query) {
        List<Address> addresses = new ArrayList<>();

        try {

            for (Address address : getCurrentJob().getClient().getAddresses()) {
                if (address.toString().toUpperCase().contains(query.toUpperCase())) {
                    addresses.add(address);
                }
            }

            return addresses;
        } catch (Exception e) {

            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public ArrayList<String> completeCountry(String query) {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            ArrayList<Country> countries = new ArrayList<>(Country.findCountriesByName(em, query));
            ArrayList<String> countriesList = (ArrayList<String>) (ArrayList<?>) countries;

            return countriesList;
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public List<Classification> completeClassification(String query) {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            List<Classification> classifications = Classification.findActiveClassificationsByName(em, query);
           
            return classifications;
        } catch (Exception e) {
            
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public List<Department> completeDepartment(String query) {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            List<Department> departments = Department.findActiveDepartmentsByName(em, query);
           
            return departments;

        } catch (Exception e) {
           System.out.println(e + ": completeDepartment");

            return new ArrayList<>();
        }
    }

    public List<String> completePreferenceValue(String query) {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            List<String> preferenceValues = Preference.findAllPreferenceValues(em, query);

            return preferenceValues;

        } catch (Exception e) {
           System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List<DepartmentUnit> completeDepartmentUnit(String query) {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            List<DepartmentUnit> departmentUnits = DepartmentUnit.findDepartmentUnitsByName(em, query);

            return departmentUnits;

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List<Laboratory> completeLaboratory(String query) {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            List<Laboratory> laboratories = Laboratory.findLaboratoriesByName(em, query);

            return laboratories;

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List<Employee> completeEmployee(String query) {
        EntityManager em = null;

        try {

            em = getEntityManager1();
            List<Employee> employees = Employee.findActiveEmployeesByName(em, query);
            
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

    public List<String> completeAccPacClientName(String query) {
        EntityManager em2 = null;

        try {

            em2 = getEntityManager2();
            List<AccPacCustomer> clients = AccPacCustomer.findAccPacCustomersByName(em2, query);
            List<String> suggestions = new ArrayList<>();

            for (AccPacCustomer client : clients) {
                suggestions.add(client.getCustomerName());
            }

            return suggestions;
        } catch (Exception e) {
            
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List<AccPacCustomer> completeAccPacClient(String query) {
        EntityManager em2 = null;

        try {
            em2 = getEntityManager2();

            return AccPacCustomer.findAccPacCustomersByName(em2, query);
        } catch (Exception e) {
            
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    /**
     * This method is used for test purposes. The content of which is to be
     * commented out when not in use. This is code is typically run after doing
     * a job search.
     *
     * @param em
     */
    public void runTestCode(EntityManager em) {
    }

    public void doJobSearch(SearchParameters currentSearchParameters) {
        this.currentSearchParameters = currentSearchParameters;
        EntityManager em = null;

        if (getUser().getId() != null) {
            em = getEntityManager1();
            jobSearchResultList = Job.findJobsByDateSearchField(em,
                    getUser(),
                    currentSearchParameters.getDateField(),
                    currentSearchParameters.getJobType(),
                    currentSearchParameters.getSearchType(),
                    currentSearchParameters.getSearchText(),
                    currentSearchParameters.getDatePeriod().getStartDate(),
                    currentSearchParameters.getDatePeriod().getEndDate(), false);
            if (jobSearchResultList.isEmpty()) { // Do search with sampple search enabled
                jobSearchResultList = Job.findJobsByDateSearchField(em,
                        getUser(),
                        currentSearchParameters.getDateField(),
                        currentSearchParameters.getJobType(),
                        currentSearchParameters.getSearchType(),
                        currentSearchParameters.getSearchText(),
                        currentSearchParameters.getDatePeriod().getStartDate(),
                        currentSearchParameters.getDatePeriod().getEndDate(), true);
            }

        } else {
            jobSearchResultList = new ArrayList<>();
        }

        // Run test code after completing search.
        // This is used to do bactch updates or just test out new code.
        // NB: The code in this method should be commented out or deleted 
        // when no longer needed.
        runTestCode(em);

        // Reset select selected job id to allow new selection
        selectedJobId = 0L;
    }

    /**
     * Search for jobs using the current job report search parameters.
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<Job> doJobReportSearch(Date startDate, Date endDate) {
        EntityManager em = getEntityManager1();

        List<Job> jobReportSearchResultList = Job.findJobsByDateSearchField(em,
                getUser(),
                reportSearchParameters.getDateField(),
                reportSearchParameters.getJobType(),
                jobReport.getName(),
                reportSearchText,
                startDate, endDate, false);

        if (jobReportSearchResultList == null) {
            jobReportSearchResultList = new ArrayList<>();
        }

        return jobReportSearchResultList;

    }

    /**
     *
     * @return
     */
    public List<Classification> getActiveClassifications() {
        EntityManager em = getEntityManager1();

        List<Classification> classifications = Classification.findAllActiveClassifications(em);

        return classifications;
    }

    public List<Sector> getActiveSectors() {
        EntityManager em = getEntityManager1();

        List<Sector> sectors = Sector.findAllActiveSectors(em);

        return sectors;
    }
    
    /**
     * NB: query parameter currently not used to filter sectors.
     * @param query
     * @return
     */
    public List<Sector> completeActiveSectors(String query) {
        EntityManager em = getEntityManager1();

        List<Sector> sectors = Sector.findAllActiveSectors(em);

        return sectors;
    }

    public List<Address> getClientAddresses() {
        EntityManager em = getEntityManager1();

        List<Address> addresses = getCurrentJob().getClient().getAddresses();

        return addresses;
    }
    
    /**
     * NB: query not used to filter
     * @param query
     * @return
     */
    public List<JobCategory> completeActiveJobCategories(String query) {
        EntityManager em = getEntityManager1();

        List<JobCategory> categories = JobCategory.findAllActiveJobCategories(em);

        return categories;
    }

    public List<JobCategory> getActiveJobCategories() {
        EntityManager em = getEntityManager1();

        List<JobCategory> categories = JobCategory.findAllActiveJobCategories(em);

        return categories;
    }

    public List<JobSubCategory> getActiveJobSubCategories() {
        EntityManager em = getEntityManager1();

        List<JobSubCategory> subCategories = JobSubCategory.findAllActiveJobSubCategories(em);

        return subCategories;
    }
    
    public List<JobSubCategory> completeActiveJobSubCategories(String query) {
        EntityManager em = getEntityManager1();

        List<JobSubCategory> subCategories = JobSubCategory.findAllActiveJobSubCategories(em);

        return subCategories;
    }

    public List<CostCode> getAllCostCodes() {
        EntityManager em = getEntityManager1();

        List<CostCode> codes = CostCode.findAllCostCodes(em);

        return codes;
    }

    private EntityManagerFactory getEMF1() {
        return EMF1;
    }

    private EntityManagerFactory getEMF2() {
        return EMF2;
    }

    public List<Job> getJobSearchResultList() {
        if (jobSearchResultList == null) {
            jobSearchResultList = new ArrayList<>();
        }
        return jobSearchResultList;
    }

    public List<Job> getCurrentPeriodJobReportSearchResultList() {
        if (currentPeriodJobReportSearchResultList == null) {
            currentPeriodJobReportSearchResultList = new ArrayList<>();
        }
        return currentPeriodJobReportSearchResultList;
    }

    public int getNumberOfJobsFound() {
        if (jobSearchResultList != null) {
            return jobSearchResultList.size();
        }

        return 0;
    }

    public int getNumberOfCurrentPeriodJobsFound() {
        if (currentPeriodJobReportSearchResultList != null) {
            return currentPeriodJobReportSearchResultList.size();
        }

        return 0;
    }

    public int getNumberOfPreviousPeriodJobsFound() {
        if (previousPeriodJobReportSearchResultList != null) {
            return previousPeriodJobReportSearchResultList.size();
        }

        return 0;
    }

    public Boolean getRenderJobSearchResultsList() {
        if (jobSearchResultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean getCanExportCurrentPeriodJobReport() {
        if (jobReport.getName().equals("Monthly report")) {
            return true;
        }

        if (jobReport.getName().equals("Jobs entered by employee")) {
            if (Employee.findEmployeeByName(getEntityManager1(), getReportEmployee().getName()) != null) {
                return true;
            } else {
                return false;
            }
        }

        if (jobReport.getName().equals("Jobs entered by department")
                || jobReport.getName().equals("Jobs assigned to department")
                || jobReport.getName().equals("Jobs completed by department")
                || jobReport.getName().equals("Analytical Services Report")) {
            if (Department.findDepartmentByName(getEntityManager1(), getReportingDepartment().getName()) != null) {
                return true;
            } else {
                return false;
            }
        }

        // fix this. should check for current period 
        if (currentPeriodJobReportSearchResultList != null) {
            if (!currentPeriodJobReportSearchResultList.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    public void updateReportDateField() {
        updateJobReport();
    }

    public void changeCurrentDateSearchPeriod() {
        reportSearchParameters.getDatePeriod().initDatePeriod();
        updateJobReport();
    }

    public void changeMonthlyReportDatePeriods() {
        getMonthlyReportDatePeriod().initDatePeriod();
        getMonthlyReportDataDatePeriod().initDatePeriod();
        getMonthlyReportYearDatePeriod().initDatePeriod();
        updateJobReport();
    }

    public void changePreviousDateSearchPeriod() {
        previousDatePeriod.initDatePeriod();
        updateJobReport();
    }

    public Job getCurrentJob() {
        if (currentJob == null) {
            resetCurrentJob();
        }
        return currentJob;
    }

    public void setCurrentJob(Job currentJob) {
        this.currentJob = currentJob;
    }

    @Override
    public void setDirty(Boolean dirty) {
        //this.dirty = dirty;
        getCurrentJob().setDirty(dirty);
    }

    @Override
    public Boolean isDirty() {
        //return dirty;
        return getCurrentJob().getDirty();
    }

    public void handlePoll() {
        System.out.println("Handling poll: " + new Date());
    }

    public String getSystemInfo() {
        String clientName;
        String dirtyStatus;

        EntityManager em = null;

        try {
            if (getUser() != null) {
                em = getEntityManager1();

                if (currentJob == null) {
                    currentJob = createNewJob(em, getUser(), true);
                    currentJobId = currentJob.getId();
                }
                // get client name
                if (currentJob.getClient() == null) {
                    clientName = "";
                } else {
                    clientName = " (" + currentJob.getClient().getName() + ")";
                }
                // get dirty status of job
                if (isDirty()) {
                    dirtyStatus = " - MODIFIED";
                } else {
                    dirtyStatus = "";
                }

                // build and return system info string
                return "Current job: " + currentJob.getJobNumber() + clientName + dirtyStatus;
            } else {
                return "";
            }
        } catch (Exception e) {
           
            System.out.println(e);
        }

        return "";
    }

    public void updateBusinessOffice(SelectEvent event) {

        try {
            currentJob.setJobNumber(getCurrentJobNumber());
            setDirty(true);

        } catch (Exception e) {

            System.out.println(e);
        }
    }

    public void updateClassification() {
        EntityManager em = null;

        try {
            em = getEntityManager1();
            Classification classification = Classification.findClassificationById(em, classificationId);
            if (classification != null) {
                currentJob.setClassification(classification);
                setDirty(true);
            }
           
        } catch (Exception e) {
           
            System.out.println(e);
        }
    }

    public void updateSector() {
        setDirty(true);
    }

    public void updateBillingAddress() {
        setDirty(true);
    }

    public void updateDepartment() {
        EntityManager em = null;

        try {
            em = getEntityManager1();
            if (!currentJob.getDepartment().getName().equals("")) {
                Department department = Department.findDepartmentByName(em, currentJob.getDepartment().getName());
                if (department != null) {
                    currentJob.setDepartment(department);
                    currentJob.setJobNumber(getCurrentJobNumber());
                    JobCostingAndPayment.setJobCostingTaxes(em, currentJob);
                    if (currentJob.getId() != null) {
                        updateAllJobCostings();
                    }
                }
            }

        } catch (Exception e) {
           System.out.println(e);
        }
    }

    public void updateCurrentUnitCostDepartment() {
        EntityManager em = null;

        try {
            em = getEntityManager1();
            if (currentUnitCost.getDepartment().getName() != null) {
                Department department = Department.findDepartmentByName(em, currentUnitCost.getDepartment().getName());
                if (department != null) {
                    currentUnitCost.setDepartment(department);
                    setDirty(true);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void updateCurrentUnitCostDepartmentUnit() {
        EntityManager em = null;

        try {
            em = getEntityManager1();
            if (currentUnitCost.getDepartmentUnit().getName() != null) {
                DepartmentUnit departmentUnit = DepartmentUnit.findDepartmentUnitByName(em, currentUnitCost.getDepartmentUnit().getName());
                if (departmentUnit != null) {
                    currentUnitCost.setDepartmentUnit(departmentUnit);
                    setDirty(true);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void updateCurrentUnitCostDepartmentLab() {
        EntityManager em = null;

        try {
            em = getEntityManager1();
            if (currentUnitCost.getLaboratory().getName() != null) {
                Laboratory laboratory = Laboratory.findLaboratoryByName(em, currentUnitCost.getLaboratory().getName());

                if (laboratory != null) {
                    currentUnitCost.setLaboratory(laboratory);
                    setDirty(true);
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void updateUnitCostDepartment() {
        EntityManager em = null;

        try {
            em = getEntityManager1();
            if (unitCostDepartment.getName() != null) {
                Department department = Department.findDepartmentByName(em, unitCostDepartment.getName());
                if (department != null) {
                    unitCostDepartment = department;
                    doUnitCostSearch();
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void updateJobCostDepartment() {
        EntityManager em = null;

        try {
            em = getEntityManager1();
            if (jobCostDepartment.getName() != null) {
                Department department = Department.findDepartmentByName(em, jobCostDepartment.getName());
                if (department != null) {
                    jobCostDepartment = department;
                    doUnitCostSearch();
                }
            }

        } catch (Exception e) {
             System.out.println(e);
        }
    }

    public void updateReportingDepartment() {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            //if (getReportingDepartment().getName() != null) {
            Department department = Department.findDepartmentByName(em, getReportingDepartment().getName());
            if (department != null) {
                reportingDepartment = department;
                updateJobReport();
            }

        } catch (Exception e) {
             System.out.println(e);
        }
    }

    public void updateSubContractedDepartment() {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            if (!currentJob.getSubContractedDepartment().getName().equals("")) {
                Department subContractedDepartment = Department.findDepartmentByName(em, currentJob.getSubContractedDepartment().getName());
                if (subContractedDepartment != null) {
                    currentJob.setSubContractedDepartment(subContractedDepartment);
                    currentJob.setJobNumber(getCurrentJobNumber());
                    JobCostingAndPayment.setJobCostingTaxes(em, currentJob);

                    if (currentJob.getId() != null) {
                        updateAllJobCostings();
                    }
                }
            }

        } catch (Exception e) {            
            currentJob.setJobNumber(getCurrentJobNumber());
            System.out.println(e + ": updateSubContractedDepartment");
        }
    }

    public void updateClient() {
        EntityManager em = null;

        try {
            em = getEntityManager1();
            if (currentJob != null) {
                if (currentJob.getClient() != null) {
                    Client client = Client.findClientByName(em, currentJob.getClient().getName(), true);
                    if (client != null) {
                        currentJob.setClient(client);
                    }
                }
                accPacCustomer.setCustomerName(currentJob.getClient().getName());
                if (useAccPacCustomerList) {
                    updateCreditStatus(null);
                }
            }

        } catch (Exception e) {
             System.out.println(e);
        }
    }

    public void updateAccPacClient() {

        setShowPrepayments(false);
        
        filteredAccPacCustomerDocuments = new ArrayList<>();

        try {
            if (currentJob != null) {
                if (!currentJob.getClient().getName().equals("")) {
                    accPacCustomer.setCustomerName(currentJob.getClient().getName());
                } else {
                    accPacCustomer.setCustomerName("?");
                }
                updateCreditStatus(null);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    /**
     * Do update for the client field on the General tab on the Job Details form
     */
    public void updateJobEntryTabClient() {
        // Create copy of existing client

        accPacCustomer.setCustomerName(currentJob.getClient().getName());
        if (useAccPacCustomerList) {
            updateCreditStatus(null);
        }
        
        currentJob.setBillingAddress(currentJob.getClient().getBillingAddress());
        currentJob.setContact(currentJob.getClient().getMainContact());

        setDirty(true);
    }

    public void updateAccPacCustomer(SelectEvent event) {
        if (accPacCustomer != null) {
            try {
                EntityManager em = getEntityManager2();

                accPacCustomer = AccPacCustomer.findAccPacCustomerByName(em, accPacCustomer.getCustomerName());
                if (accPacCustomer == null) {
                    accPacCustomer = new AccPacCustomer();
                    accPacCustomer.setCustomerName("");
                }

                // set the found client name to the present job client
                if (accPacCustomer.getCustomerName() != null) {
                    updateCreditStatus(null);
                }
            } catch (Exception e) {
                System.out.println(e);
                accPacCustomer = new AccPacCustomer();
                accPacCustomer.setCustomerName("");
            }
        }
    }

    public void updateClientInformation() {
    }

    public Integer getNumberOfFilteredAccPacCustomerDocuments() {
        if (filteredAccPacCustomerDocuments != null) {
            return filteredAccPacCustomerDocuments.size();
        }

        return 0;
    }

    public Boolean getFilteredDocumentAvailable() {
        if (filteredAccPacCustomerDocuments != null) {
            if (filteredAccPacCustomerDocuments.isEmpty()) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public void updateCreditStatusSearch() {
        accPacCustomer.setCustomerName(currentJob.getClient().getName());
        updateCreditStatus(null);
    }

    public void updateCreditStatus(SelectEvent event) {
        EntityManager em = getEntityManager2();

        accPacCustomer = AccPacCustomer.findAccPacCustomerByName(em, accPacCustomer.getCustomerName().trim());

        if (accPacCustomer != null) {
            //accPacCustomerDocuments = AccPacDocument.findAccPacInvoicesDueByCustomerId(em, accPacCustomer.getId(), true);
            if (getShowPrepayments()) {
                filteredAccPacCustomerDocuments = AccPacDocument.findAccPacInvoicesDueByCustomerId(em, accPacCustomer.getId(), true);
            } else {
                filteredAccPacCustomerDocuments = AccPacDocument.findAccPacInvoicesDueByCustomerId(em, accPacCustomer.getId(), false);
            }
        } else {
            createNewAccPacCustomer();
        }
    }

    public void updateCreditStatus() {
        updateCreditStatus(null);
    }

    public void createNewAccPacCustomer() {
        if (accPacCustomer != null) {
            accPacCustomer = new AccPacCustomer(accPacCustomer.getCustomerName());
        } else {
            accPacCustomer = new AccPacCustomer(null);
        }
        accPacCustomer.setId(null);
        filteredAccPacCustomerDocuments = new ArrayList<>();
    }

    public String getAccPacCustomerID() {
        if (accPacCustomer.getId() == null) {
            return "";
        } else {
            return accPacCustomer.getId();
        }
    }

    public String getAccPacCustomerName() {
        if (accPacCustomer.getCustomerName() == null) {
            return "{Not found}";
        } else {
            return accPacCustomer.getCustomerName();
        }
    }

    public String getCustomerType() {
        if (accPacCustomer.getIDACCTSET().equals("TRADE")) {
            return "CREDIT";
        } else {
            return "REGULAR";
        }
    }

    public AccPacCustomer getAccPacCustomer() {
        return accPacCustomer;
    }

    public void setAccPacCustomer(AccPacCustomer accPacCustomer) {
        this.accPacCustomer = accPacCustomer;
    }

    public void updateClient(SelectEvent event) {
        updateClient();
    }

    public void updateAssignee() {
        EntityManager em = getEntityManager1();

        currentJob.setAssignedTo(Employee.findEmployeeByName(em, currentJob.getAssignedTo().getName()));

        currentJob.getJobStatusAndTracking().setAlertDate(null);
        setDirty(true);
    }

    public void updateReportEmployee() {
        EntityManager em = getEntityManager1();

        try {
            reportEmployee = Employee.findEmployeeByName(em, getReportEmployee().getName());

        } catch (Exception e) {
             System.out.println(e);
        }

    }

    public void updateCostingComponents() {
        if (selectedJobCostingTemplate != null) {
            EntityManager em = getEntityManager1();
            JobCostingAndPayment jcp
                    = JobCostingAndPayment.findJobCostingAndPaymentByDepartmentAndName(em,
                            getUser().getEmployee().getDepartment().getName(),
                            selectedJobCostingTemplate);
            if (jcp != null) {
                currentJob.getJobCostingAndPayment().getCostComponents().clear();
                currentJob.getJobCostingAndPayment().setCostComponents(copyCostComponents(jcp.getCostComponents()));
                currentJob.getJobCostingAndPayment().calculateAmountDue();
                setDirty(true);
            } else {
                // Nothing yet
            }

            selectedJobCostingTemplate = null;

        }
    }

    public void removeCurrentJobCostingComponents(EntityManager em) {

        if (!getCurrentJob().getJobCostingAndPayment().getCostComponents().isEmpty()) {
            em.getTransaction().begin();
            for (Iterator<CostComponent> iter = getCurrentJob().getJobCostingAndPayment().getCostComponents().iterator(); iter.hasNext();) {
                CostComponent costComponent = iter.next();

                if (costComponent.getId() != null) {
                    costComponent = em.find(CostComponent.class, costComponent.getId());
                    em.remove(costComponent);
                }
            }

            getCurrentJob().getJobCostingAndPayment().getCostComponents().clear();
            BusinessEntityUtils.saveBusinessEntity(em, getCurrentJob().getJobCostingAndPayment());
            em.getTransaction().commit();
        }

    }

    public void updateSampledBy() {
        EntityManager em = getEntityManager1();
        selectedJobSample.setSampledBy(Employee.findEmployeeByName(em, selectedJobSample.getSampledBy().getName()));       
    }

    public void updateReceivedBy() {

        EntityManager em = null;
        try {
            em = getEntityManager1();

            if (receivedById != null) {
                Employee receivedBy = Employee.findEmployeeById(em, receivedById);
                if (receivedBy != null) {
                    selectedJobSample.setReceivedBy(receivedBy);
                    setDirty(true);
                } else {
                    selectedJobSample.setReceivedBy(getDefaultEmployee(em, "--", "--"));
                }
            }

        } catch (Exception e) {
             System.out.println(e);
        }
    }

    public Job getSelectedJob() {
        if (selectedJob == null) {
            selectedJob = new Job();
            selectedJob.setJobNumber("");
        }
        return selectedJob;
    }

    public void setSelectedJob(Job selectedJob) {
        this.selectedJob = selectedJob;
    }

    public String getLastJobNumber() {
        return "?";
    }

    public void saveManufacturer(EntityManager em, Manufacturer manufacturer) {
        if (manufacturer.getName() != null) {

            Manufacturer currentManufacturer = Manufacturer.findManufacturerByName(em, manufacturer.getName());

            if (currentManufacturer == null) { // new?
                currentManufacturer = new Manufacturer();
                currentManufacturer.setName(manufacturer.getName());

            } else {
                manufacturer.setId(currentManufacturer.getId());
            }

            BusinessEntityUtils.saveBusinessEntity(em, manufacturer);
        }
    }

    public JobManagerUser getUser() {
        return getMain().getUser();
    }

    public void createNewJobClient() {
        clientManager.createNewClient();
        clientManager.setUser(getUser());
        clientManager.setClientOwner(currentJob);
        clientManager.setBillingAddress(clientManager.getClient().getBillingAddress());
        clientManager.setSave(true);
        clientManager.setClientNameAndIdEditable(getUser().getPrivilege().getCanAddClient());
        clientManager.setExternalEntityManagerFactory(EMF1);        
        getMain().openDialog(null, "clientForm", true, true, true, 420, 600);
    }

    // Edit the client via the ClientManagerKeep
    public void editJobClient() {
        clientManager.setUser(getUser());
        clientManager.setClientOwner(currentJob);
        clientManager.setClient(currentJob.getClient());
        clientManager.setBillingAddress(currentJob.getBillingAddress());
        clientManager.setSave(true);
        clientManager.setClientNameAndIdEditable(getUser().getPrivilege().getCanAddClient());
        clientManager.setExternalEntityManagerFactory(EMF1);
        getMain().openDialog(null, "clientForm", true, true, true, 420, 600);
    }

    public ServiceRequest createNewServiceRequest(EntityManager em,
            JobManagerUser user,
            Boolean autoGenerateServiceRequestNumber) {

        ServiceRequest sr = new ServiceRequest();
        sr.setClient(new Client("", false));
        sr.setServiceRequestNumber("");
        sr.setJobDescription("");

        sr.setBusinessOffice(getDefaultBusinessOffice(em, "Head Office"));

        sr.setClassification(Classification.findClassificationByName(em, "--"));
        sr.setSector(Sector.findSectorByName(em, "--"));
        sr.setJobCategory(JobCategory.findJobCategoryByName(em, "--"));
        sr.setJobSubCategory(JobSubCategory.findJobSubCategoryByName(em, "--"));

        sr.setServiceContract(createServiceContract());
        sr.setAutoGenerateServiceRequestNumber(autoGenerateServiceRequestNumber);

        // job status and tracking
        sr.setDateSubmitted(new Date());

        return sr;
    }

    public JobManagerUser createNewUser(EntityManager em) {
        JobManagerUser user = new JobManagerUser();

        user.setEmployee(getDefaultEmployee(em, "--", "--"));

        return user;
    }

    public DocumentStandard createNewDocumentStandard(EntityManager em,
            JobManagerUser user) {

        DocumentStandard documentStandard = new DocumentStandard();
        documentStandard.setNumber("");
        documentStandard.setType(DocumentType.findDocumentTypeByName(em, "Standard"));
        documentStandard.setClassification(Classification.findClassificationByName(em, "--"));
        documentStandard.setEnforcement("Mandatory");

        documentStandard.setAutoGenerateNumber(Boolean.FALSE);

        return documentStandard;
    }

    /*
     * Takes a list of job costings enties an set their ids and component ids
     * to null which will result in new job costings being created when
     * the job costins are commited to the database
     */
    public List<JobCosting> copyJobCostings(List<JobCosting> srcCostings) {
        ArrayList<JobCosting> newJobCostings = new ArrayList<>();

        for (JobCosting jobCosting : srcCostings) {
            JobCosting newJobCosting = new JobCosting(jobCosting);
            for (CostComponent costComponent : jobCosting.getCostComponents()) {
                CostComponent newCostComponent = new CostComponent(costComponent);
                newJobCosting.getCostComponents().add(newCostComponent);
            }
            newJobCostings.add(newJobCosting);
        }

        return newJobCostings;
    }

    public List<CostComponent> copyCostComponents(List<CostComponent> srcCostComponents) {
        ArrayList<CostComponent> newCostComponents = new ArrayList<>();

        for (CostComponent costComponent : srcCostComponents) {
            CostComponent newCostComponent = new CostComponent(costComponent);
            newCostComponents.add(newCostComponent);
        }

        return newCostComponents;
    }

    public void createSampleBasedJobCostings(JobCostingAndPayment jobCostingAndPayment) {
        if (currentJob.getJobCostingAndPayment().getAllSortedCostComponents().isEmpty()) {
            // Add all existing samples as cost oomponents            
            for (JobSample jobSample : currentJob.getJobSamples()) {
                jobCostingAndPayment.getAllSortedCostComponents().add(new CostComponent(jobSample.getDescription()));
            }
        } else if (currentJob.getJobSamples().size() > currentJob.getJobCostingAndPayment().getAllSortedCostComponents().size()) {
        }
    }

    public void createDefaultJobCostings(JobCostingAndPayment jobCostingAndPayment) {

        if (currentJob.getJobCostingAndPayment().getCostComponents().isEmpty()) {
            jobCostingAndPayment.getCostComponents().add(new CostComponent("List of Assessments", Boolean.TRUE));
            jobCostingAndPayment.getCostComponents().add(new CostComponent(""));
        }

    }

    public JobCostingAndPayment createJobCostingAndPayment() {
        JobCostingAndPayment jobCostingAndPayment = new JobCostingAndPayment();

        jobCostingAndPayment.setPurchaseOrderNumber("");
        jobCostingAndPayment.setReceiptNumber("");

        return jobCostingAndPayment;
    }

    public Job copyJob(EntityManager em,
            Job currentJob,
            JobManagerUser user,
            Boolean autoGenerateJobNumber,
            Boolean copySamples) {
        Job job = new Job();

         
        job.setClient(new Client("", false));
        setIsJobToBeCopied(true);

        job.setReportNumber("");
        job.setJobDescription("");

        job.setBusinessOffice(currentJob.getBusinessOffice());
        job.setDepartment(currentJob.getDepartment());
        job.setSubContractedDepartment(getDefaultDepartment(em, "--"));        
        job.setClassification(Classification.findClassificationByName(em, "--"));
        job.setSector(Sector.findSectorByName(em, "--"));
        job.setJobCategory(JobCategory.findJobCategoryByName(em, "--"));
        job.setJobSubCategory(JobSubCategory.findJobSubCategoryByName(em, "--"));
        // service contract
        job.setServiceContract(createServiceContract());
        // set default values
        job.setAutoGenerateJobNumber(autoGenerateJobNumber);
        job.setIsEarningJob(Boolean.TRUE);
        job.setYearReceived(Calendar.getInstance().get(Calendar.YEAR));
        // job status and tracking
        job.setJobStatusAndTracking(new JobStatusAndTracking());
        job.getJobStatusAndTracking().setDateAndTimeEntered(new Date());
        job.getJobStatusAndTracking().setDateSubmitted(new Date());
        job.getJobStatusAndTracking().setAlertDate(null);
        job.getJobStatusAndTracking().setDateJobEmailWasSent(null);
        // job costing and payment 
        job.setJobCostingAndPayment(createJobCostingAndPayment());
        // this is done here because job number is dependent on business office, department/subcontracted department

        // copy samples
        if (copySamples) {
            List<JobSample> samples = currentJob.getJobSamples();
            job.setNumberOfSamples(currentJob.getNumberOfSamples());
            for (Iterator<JobSample> it = samples.iterator(); it.hasNext();) {
                JobSample jobSample = it.next();
                job.getJobSamples().add(new JobSample(jobSample));
            }
        }

        // sequence number
        job.setJobSequenceNumber(currentJob.getJobSequenceNumber());

        // job number
        if (job.getAutoGenerateJobNumber()) {
            job.setJobNumber(getJobNumber(job, em));
        }

        return job;
    }

    public EntityManagerFactory setupDatabaseConnection(String PU) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU);
            if (emf.isOpen()) {
                return emf;
            } else {
                return null;
            }
        } catch (Exception ex) {
            System.out.println(PU + " Connection failed: " + ex);
            return null;
        }
    }

    public HashMap<String, String> getConnectionProperties(
            String url,
            String driver,
            String username,
            String password) {

        // setup new database connection properties
        HashMap<String, String> prop = new HashMap<>();
        prop.put("javax.persistence.jdbc.user", username);
        prop.put("javax.persistence.jdbc.password", password);
        prop.put("javax.persistence.jdbc.url", url);
        prop.put("javax.persistence.jdbc.driver", driver);

        return prop;
    }

    public Boolean validateJobNumber(String jobNumber, Boolean auto) {
        Integer departmentCode = 0;
        Integer year = 0;
        Long sequenceNumber = 0L;

        String parts[] = jobNumber.split("/");
        if (parts != null) {
            // check for correct number of parts
            if ((parts.length >= 3)
                    && (parts.length <= 5)) {
                // are subgroup code, year and sequence number valid integers/long?
                try {
                    if (auto && parts[0].equals("?")) {
                        // This means the complete job number has not yet
                        // been generate. Ignore for now.
                    } else {
                        departmentCode = Integer.parseInt(parts[0]);
                    }
                    year = Integer.parseInt(parts[1]);
                    if (auto && parts[2].equals("?")) {
                        // This means the complete job number has not yet
                        // been generate. Ignore for now.
                    } else {
                        sequenceNumber = Long.parseLong(parts[2]);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                    return false;
                }
                // subgroup code, year and deparment code have valid ranges?
                if (auto && parts[0].equals("?")) {
                    // This means the complete job number has not yet
                    // been generate. Ignore for now.
                } else if (departmentCode < 0) {
                    return false;
                }
                if (year < 1970) {
                    return false;
                }
                if (auto && parts[2].equals("?")) {
                    // This means the complete job number has not yet
                    // been generate. Ignore for now.
                } else if (sequenceNumber < 1L) {
                    return false;
                }
                // validate 4th part that can be an integer for a department
                // code or a sample reference(s)
                if (parts.length > 3) {
                    try {
                        departmentCode = Integer.parseInt(parts[3]);
                        if (departmentCode < 0) {
                            return false;
                        }
                    } catch (Exception e) {
                        // this means 4th part is not a department code
                        // and that's ok for now.
                        System.out.println("Job number validation error: This means 4th part is not a department code.: " + e);
                    }
                }
                // all is well here
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Delete a generic entity
     *
     * @param entity
     * @return
     */
    private Boolean deleteEntity(EntityManager em, Object entity) {

        try {
            if (entity != null) {
                em.remove(entity);
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }

        return true;
    }

    public Date getCurrentDate() {
        return new Date();
    }

    /**
     * This may no longer be needed
     *
     * @param em
     * @param client
     * @return
     */
    public Long saveClient(EntityManager em, Client client) {
        return BusinessEntityUtils.saveBusinessEntity(em, client);
    }

    public Long saveBillingAddress(EntityManager em, Address address) {
        return BusinessEntityUtils.saveBusinessEntity(em, address);
    }

    /**
     * getFourDigitNumberString Pad number with leading zeros if the number is
     * less than four digits
     *
     * @param number long
     * @return String
     */
    public String getFourDigitString(long number) {
        String fourDigitString = "";

        if ((number >= 0L) && (number <= 9L)) {
            fourDigitString = "000" + number;
        }
        if ((number >= 10L) && (number <= 99L)) {
            fourDigitString = "00" + number;
        }
        if ((number >= 100L) && (number <= 999L)) {
            fourDigitString = "0" + number;
        }
        if (number >= 1000L) {
            fourDigitString = "" + number;
        }
        return fourDigitString;
    }

    public String getJobNumber(
            String departmentCode,
            Integer year,
            Long jobSequenceNumber,
            String subContractedDepartmentCode,
            ArrayList<JobSample> jobSamples) {

        String jobNumber = "";
        String jobSequenceNumberStr = "?";
        Integer numberOfJobSamples = null;

        if (jobSamples != null) {
            numberOfJobSamples = jobSamples.size();
        }

        // include the department code of the deparment id if valid
        if (departmentCode == null) {
            departmentCode = "?";
        }
        // include the sequence number if it is valid
        if (jobSequenceNumber != 0) {          
            jobSequenceNumberStr = getFourDigitString(jobSequenceNumber);
        }

        // set base job number
        jobNumber = "" + departmentCode + "/" + year + "/" + jobSequenceNumberStr;
        // append subcontracted code if valid
        if (subContractedDepartmentCode != null) {
            jobNumber = jobNumber + "/" + subContractedDepartmentCode;
        }
        // append samples code if any
        if ((numberOfJobSamples != null) && (numberOfJobSamples > 0)) {
            if ((subContractedDepartmentCode == null) && (numberOfJobSamples > 1)) {
                jobNumber = jobNumber + "/"
                        + BusinessEntityUtils.getAlphaCode(0) + "-"
                        + BusinessEntityUtils.getAlphaCode(numberOfJobSamples - 1);
            } else if ((subContractedDepartmentCode == null) && (numberOfJobSamples <= 1)) {
                // job number remains as is for now
            } else if (subContractedDepartmentCode != null) { // subcontract?
                int index = 0;
                // update subcontract samples
                for (JobSample sample : jobSamples) {
                    if (index == 0) {
                        jobNumber = jobNumber + "/" + sample.getReference();
                    } else {
                        jobNumber = jobNumber + "," + sample.getReference();
                    }
                    index++;
                }
            }
        }

        return jobNumber;
    }

    public String getContactPerson(
            Client client,
            Integer index) {
        String firstname = "";
        String lastname = "";

        try {
            if (client.getContacts().get(index).getFirstName() != null) {
                firstname = client.getContacts().get(index).getFirstName();
            }
            if (client.getContacts().get(index).getLastName() != null) {
                lastname = client.getContacts().get(index).getLastName();
            }
            return firstname + " " + lastname;
        } catch (Exception e) {
            return null;
        }
    }

    public String getReportsLocation() {
        return "."; // to be extracted from database
    }

    public Long saveDepartmentReport(EntityManager em, DepartmentReport departmentReport) {

        try {
            if (departmentReport.getId() != null) {
                em.merge(departmentReport);
            } else {
                em.persist(departmentReport);
            }
        } catch (Exception e) {
            return null;
        }

        return departmentReport.getId();
    }

    public ServiceContract createServiceContract() {
        ServiceContract serviceContract = new ServiceContract();
        // init service contract
        serviceContract.setIntendedMarketLocal(true);
        serviceContract.setAutoAddSampleInformation(true);
        serviceContract.setAdditionalServiceUrgent(false);
        serviceContract.setAdditionalServiceFaxResults(false);
        serviceContract.setAdditionalServiceTelephonePresumptiveResults(false);
        serviceContract.setAdditionalServiceSendMoreContractForms(false);
        serviceContract.setAdditionalServiceOther(false);
        serviceContract.setAdditionalServiceOtherText("");
        serviceContract.setIntendedMarketLocal(false);
        serviceContract.setIntendedMarketCaricom(false);
        serviceContract.setIntendedMarketUK(false);
        serviceContract.setIntendedMarketUSA(false);
        serviceContract.setIntendedMarketCanada(false);
        serviceContract.setIntendedMarketOther(false);
        serviceContract.setIntendedMarketOtherText("");
        serviceContract.setServiceRequestedTesting(false);
        serviceContract.setServiceRequestedCalibration(false);
        serviceContract.setServiceRequestedLabelEvaluation(false);
        serviceContract.setServiceRequestedInspection(false);
        serviceContract.setServiceRequestedConsultancy(false);
        serviceContract.setServiceRequestedTraining(false);
        serviceContract.setServiceRequestedOther(false);
        serviceContract.setServiceRequestedOtherText("");
        serviceContract.setSpecialInstructions("");

        return serviceContract;
    }

    public String getMonthShortFormat(Date date) {
        String month = "";
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        switch (c.get(Calendar.MONTH)) {
            case 0:
                month = "Jan";
                break;
            case 1:
                month = "Feb";
                break;
            case 2:
                month = "Mar";
                break;
            case 3:
                month = "Apr";
                break;
            case 4:
                month = "May";
                break;
            case 5:
                month = "Jun";
                break;
            case 6:
                month = "Jul";
                break;
            case 7:
                month = "Aug";
                break;
            case 8:
                month = "Sep";
                break;
            case 9:
                month = "Oct";
                break;
            case 10:
                month = "Nov";
                break;
            case 11:
                month = "Dec";
                break;
            default:
                month = "";
                break;
        }
        return month;
    }

    public String getYearShortFormat(Date date, int digits) {
        String yearString = "";
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int year = c.get(Calendar.YEAR);
        yearString = yearString + year;

        // get last x digits of year
        yearString = yearString.substring(yearString.length() - digits, yearString.length());

        return yearString;
    }

    public String getJobNumber(Job job, EntityManager em) {
        Calendar c = Calendar.getInstance();
        String departmentOrCompanyCode;
        String year = "?";
        String sequenceNumber;
        String subContractedDepartmenyOrCompanyCode;

        if (job.getAutoGenerateJobNumber() != false) {

            departmentOrCompanyCode = job.getDepartment().getSubGroupCode().equals("") ? "?" : job.getDepartment().getSubGroupCode();
            subContractedDepartmenyOrCompanyCode = job.getSubContractedDepartment().getSubGroupCode().equals("") ? "?" : job.getSubContractedDepartment().getSubGroupCode();

            // Use the date entered to get the year if it is valid
            // and only if this is not a subcontracted job
            if ((job.getJobStatusAndTracking().getDateAndTimeEntered() != null)
                    && (subContractedDepartmenyOrCompanyCode.equals("?"))) {
                c.setTime(job.getJobStatusAndTracking().getDateAndTimeEntered());
                year = "" + c.get(Calendar.YEAR);
            } else if (job.getYearReceived() != null) {
                year = job.getYearReceived().toString();
            }
            // include the sequence number if it is valid
            if (job.getJobSequenceNumber() != null) {
                //sequenceNumber = job.getJobSequenceNumber().toString();
                sequenceNumber = getFourDigitString(job.getJobSequenceNumber());
            } else {
                sequenceNumber = "?";
            }
            // set base job number
            job.setJobNumber(departmentOrCompanyCode + "/" + year + "/" + sequenceNumber);
            // append subcontracted code if valid
            if (!subContractedDepartmenyOrCompanyCode.equals("?")) {
                job.setJobNumber(job.getJobNumber() + "/" + subContractedDepartmenyOrCompanyCode);
            }

            SystemOption sysOption = SystemOption.findSystemOptionByName(em,
                    "includeSampleReference");

            Boolean includeRef = true;
            if (sysOption != null) {
                includeRef = Boolean.parseBoolean(sysOption.getOptionValue());
            }
            // Append sample codes if any
            if (includeRef) {
                if ((job.getNumberOfSamples() != null) && (job.getNumberOfSamples() > 1)) {
                    job.setJobNumber(job.getJobNumber() + "/"
                            + BusinessEntityUtils.getAlphaCode(0) + "-"
                            + BusinessEntityUtils.getAlphaCode(job.getNumberOfSamples() - 1));
                }
            }
        }

        return job.getJobNumber();
    }

    public Long saveJobSample(EntityManager em, JobSample jobSample) {
        return BusinessEntityUtils.saveBusinessEntity(em, jobSample);
    }

    public Boolean deleteJob(EntityManager em, Long Id) {

        Job job = em.find(Job.class, Id);
        return deleteEntity(em, job);
    }

    public Long insertJobCategory(EntityManager em, String category,
            String subCategory) {
        JobCategory jobCategory = new JobCategory();
        JobSubCategory jobSubCategory = new JobSubCategory();

        jobCategory.setCategory(category);
        if (subCategory != null) {
            jobSubCategory.setCategoryId(jobCategory.getId());
            jobSubCategory.setSubCategory(subCategory);
        }

        // save category and/or subcategory
        em.persist(jobCategory);
        if (jobCategory.getId() != null) {
            if (subCategory != null) {
                em.persist(jobSubCategory);
                if (jobSubCategory.getId() != null) {
                    return jobCategory.getId();
                }
            }
            return jobCategory.getId();
        }

        return 0L;
    }

    public Long insertJobSubCategory(EntityManager em, Long categoryId,
            String subCategory) {
        JobSubCategory jobSubCategory = new JobSubCategory();
        jobSubCategory.setIsEarning(Boolean.TRUE);

        jobSubCategory.setCategoryId(categoryId);
        jobSubCategory.setSubCategory(subCategory);

        // save subcategory
        em.persist(jobSubCategory);
        if (jobSubCategory.getId() != null) {
            return jobSubCategory.getId();
        } else {
            return 0L;
        }
    }

    public List<DatePeriodJobReportColumnData> jobSubCategogyGroupReportByDatePeriod(
            EntityManager em,
            String dateSearchField,
            String searchText,
            Date startDate,
            Date endDate) {

        List<DatePeriodJobReportColumnData> data = null;
        
        String searchQuery
                = "SELECT NEW jm.com.dpbennett.utils.DatePeriodJobReportColumnData"
                + "("
                + "job.jobSubCategory,"
                + "SUM(jobCostingAndPayment.finalCost),"
                + "SUM(job.noOfTestsOrCalibrations)"
                + ")"
                + " FROM Job job"
                + " JOIN job.jobStatusAndTracking jobStatusAndTracking"
                + " JOIN job.jobSubCategory jobSubCategory"
                + " JOIN job.department department"
                + " JOIN job.subContractedDepartment subContractedDepartment"
                + " JOIN job.jobCostingAndPayment jobCostingAndPayment"
                + " WHERE ((jobStatusAndTracking." + dateSearchField + " >= " + BusinessEntityUtils.getDateString(startDate, "'", "YMD", "-")
                + " AND jobStatusAndTracking." + dateSearchField + " <= " + BusinessEntityUtils.getDateString(endDate, "'", "YMD", "-") + "))"
                + " AND ( UPPER(department.name) = '" + searchText.toUpperCase() + "'"
                + " OR UPPER(subContractedDepartment.name) = '" + searchText.toUpperCase() + "')"
                + " AND UPPER(jobStatusAndTracking.workProgress) <> 'CANCELLED'"
                + " AND UPPER(jobStatusAndTracking.workProgress) <> 'WITHDRAWN BY CLIENT'"
                + " GROUP BY job.jobSubCategory"
                + " ORDER BY job.jobSubCategory.subCategory ASC";
        // now do search
        try {
            data = em.createQuery(searchQuery, DatePeriodJobReportColumnData.class).getResultList();
            if (data == null) {
                data = new ArrayList<>();
            }
            // sort by earnings and non-earnings
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

        return data;
    }

    public List<DatePeriodJobReportColumnData> sectorReportByDatePeriod(
            EntityManager em,
            String dateSearchField,
            String searchText, // filled in with department's name
            Date startDate,
            Date endDate) {

        List<DatePeriodJobReportColumnData> data;
        String searchQuery;

        searchQuery
                = "SELECT NEW jm.com.dpbennett.utils.DatePeriodJobReportColumnData"
                + "("
                + "job.sector,"
                + "SUM(job.noOfTestsOrCalibrations)"
                + ")"
                + " FROM Job job"
                + " JOIN job.jobStatusAndTracking jobStatusAndTracking"
                + " JOIN job.sector sector"
                + " JOIN job.department department"
                + " JOIN job.subContractedDepartment subContractedDepartment"
                + " WHERE ((jobStatusAndTracking." + dateSearchField + " >= " + BusinessEntityUtils.getDateString(startDate, "'", "YMD", "-")
                + " AND jobStatusAndTracking." + dateSearchField + " <= " + BusinessEntityUtils.getDateString(endDate, "'", "YMD", "-") + "))"
                + " AND ( UPPER(department.name) = '" + searchText.toUpperCase() + "'"
                + " OR UPPER(subContractedDepartment.name) = '" + searchText.toUpperCase() + "')"
                + " AND UPPER(jobStatusAndTracking.workProgress) <> 'CANCELLED'"
                + " AND UPPER(jobStatusAndTracking.workProgress) <> 'WITHDRAWN BY CLIENT'"
                + " GROUP BY job.sector"
                + " ORDER BY job.sector.name ASC";
        // now do search

        try {
            data = em.createQuery(searchQuery, DatePeriodJobReportColumnData.class).getResultList();
            if (data
                    == null) {
                data = new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

        return data;
    }

    public List<DatePeriodJobReportColumnData> jobReportByDatePeriod(
            EntityManager em,
            String searchText, // filled in with department's name
            Date startDate,
            Date endDate) {

        List<DatePeriodJobReportColumnData> data;
      
        String searchQuery
                = 
                "SELECT NEW jm.com.dpbennett.utils.DatePeriodJobReportColumnData"
                + "("
                + "job"
                + ")"
                + " FROM Job job"
                + " JOIN job.jobStatusAndTracking jobStatusAndTracking"
                + " JOIN job.department department"
                + " JOIN job.subContractedDepartment subContractedDepartment"
                + " WHERE ((jobStatusAndTracking.dateOfCompletion >= " + BusinessEntityUtils.getDateString(startDate, "'", "YMD", "-")
                + " AND jobStatusAndTracking.dateOfCompletion <= " + BusinessEntityUtils.getDateString(endDate, "'", "YMD", "-") + ")"
                + " OR"
                + " (jobStatusAndTracking.dateSubmitted >= " + BusinessEntityUtils.getDateString(startDate, "'", "YMD", "-")
                + " AND jobStatusAndTracking.dateSubmitted <= " + BusinessEntityUtils.getDateString(endDate, "'", "YMD", "-") + ")"
                + " OR"
                + " (jobStatusAndTracking.expectedDateOfCompletion >= " + BusinessEntityUtils.getDateString(startDate, "'", "YMD", "-")
                + " AND jobStatusAndTracking.expectedDateOfCompletion <= " + BusinessEntityUtils.getDateString(endDate, "'", "YMD", "-") + "))"
                + " AND ( UPPER(department.name) = '" + searchText.toUpperCase() + "'"
                + " OR UPPER(subContractedDepartment.name) = '" + searchText.toUpperCase() + "')"
                + " AND UPPER(jobStatusAndTracking.workProgress) <> 'CANCELLED'"
                + " AND UPPER(jobStatusAndTracking.workProgress) <> 'WITHDRAWN BY CLIENT'"
                + " ORDER BY job.sector.name ASC";
        // now do search

        try {
            data = em.createQuery(searchQuery, DatePeriodJobReportColumnData.class).getResultList();
            if (data
                    == null) {
                data = new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

        return data;
    }

    public Long getJobCountByQuery(EntityManager em, String query) {
        try {
            return (Long) em.createQuery(query).getSingleResult();
        } catch (Exception e) {
            System.out.println(e);
            return 0L;
        }
    }

    public Long saveServiceContract(EntityManager em, ServiceContract serviceContract) {
        return BusinessEntityUtils.saveBusinessEntity(em, serviceContract);
    }

    public Long saveCashPayment(EntityManager em, CashPayment cashPayment) {
        return BusinessEntityUtils.saveBusinessEntity(em, cashPayment);
    }

    public List<CashPayment> getCashPaymentsByJobId(EntityManager em, Long jobId) {
        try {
            List<CashPayment> cashPayments
                    = em.createQuery("SELECT c FROM CashPayment c "
                            + "WHERE c.jobId "
                            + "= '" + jobId + "'", CashPayment.class).getResultList();
            return cashPayments;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public Boolean deleteCashPayment(EntityManager em, Long Id) {
        CashPayment cashPayment;

        cashPayment = em.find(CashPayment.class, Id);
        return deleteEntity(em, cashPayment);
    }

    public Long saveDepartment(EntityManager em, Department department) {
        return BusinessEntityUtils.saveBusinessEntity(em, department);
    }

    public Long saveEmployee(EntityManager em, Employee employee) {
        return BusinessEntityUtils.saveBusinessEntity(em, employee);
    }

    public BusinessOffice getDefaultBusinessOffice(EntityManager em, String name) {
        BusinessOffice office = BusinessOffice.findBusinessOfficeByName(em, name);

        if (office == null) {
            em.getTransaction().begin();
            office = new BusinessOffice();
            office.setName(name);
            BusinessEntityUtils.saveBusinessEntity(em, office);
            em.getTransaction().commit();
        }

        return office;
    }

    // TK: Remove if one in Application is used
    public Employee getDefaultEmployee(EntityManager em, String firstName, String lastName) {
        Employee employee = Employee.findEmployeeByName(em, firstName, lastName);

        // create employee if it does not exist
        if (employee == null) {
            employee = new Employee();
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setBusinessOffice(getDefaultBusinessOffice(em, "--"));
            employee.setDepartment(getDefaultDepartment(em, "--"));
            // save
            em.getTransaction().begin();
            BusinessEntityUtils.saveBusinessEntity(em, employee);
            em.getTransaction().commit();
        }

        return employee;
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

    public void postJobManagerMailToUser(
            Session mailSession,
            JobManagerUser user,
            String subject,
            String message) throws Exception {

        boolean debug = false;
        Message msg;
        EntityManager em = getEntityManager1();

        if (mailSession == null) {
            //Set the host smtp address
            Properties props = new Properties();
            String mailServer = SystemOption.findSystemOptionByName(getEntityManager1(), "mail.smtp.host").getOptionValue();
            props.put("mail.smtp.host", mailServer);

            // create some properties and get the default Session
            Session session = Session.getDefaultInstance(props, null);
            session.setDebug(debug);
            msg = new MimeMessage(session);
        } else {
            msg = new MimeMessage(mailSession);
        }

        // set the from and to address
        String email = SystemOption.findSystemOptionByName(em, "jobManagerEmailAddress").getOptionValue();
        String name = SystemOption.findSystemOptionByName(em, "jobManagerEmailName").getOptionValue();
        InternetAddress addressFrom = new InternetAddress(email, name); // option job manager email addres
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[1];
        if (user != null) {
            addressTo[0] = new InternetAddress(user.getUsername(), user.getEmployee().getFirstName() + " " + user.getEmployee().getLastName());
        } else {
            String email1 = SystemOption.findSystemOptionByName(em, "administratorEmailAddress").getOptionValue();
            String name1 = SystemOption.findSystemOptionByName(em, "administratorEmailName").getOptionValue();
            addressTo[0] = new InternetAddress(email1, name1);
        }

        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(message, "text/plain");
        Transport.send(msg);
    }

    public void postJobManagerMail(
            Session mailSession,
            String addressedTo,
            String fullNameOfAddressedTo,
            String subject,
            String message) throws Exception {

        boolean debug = false;
        Message msg;
        EntityManager em = getEntityManager1();

        try {
            if (mailSession == null) {
                //Set the host smtp address
                Properties props = new Properties();
                String mailServer = SystemOption.findSystemOptionByName(em, "mail.smtp.host").getOptionValue();
                String trust = SystemOption.findSystemOptionByName(em, "mail.smtp.ssl.trust").getOptionValue();
                props.put("mail.smtp.host", mailServer);
                props.setProperty("mail.smtp.ssl.trust", trust);

                // create some properties and get the default Session
                Session session = Session.getDefaultInstance(props, null); // def null
                session.setDebug(debug);
                msg = new MimeMessage(session);
            } else {
                msg = new MimeMessage(mailSession);
            }

            // set the from and to address
            String email = SystemOption.findSystemOptionByName(em, "jobManagerEmailAddress").getOptionValue();
            String name = SystemOption.findSystemOptionByName(em, "jobManagerEmailName").getOptionValue();
            InternetAddress addressFrom = new InternetAddress(email, name);
            msg.setFrom(addressFrom);

            InternetAddress[] addressTo = new InternetAddress[1];

            addressTo[0] = new InternetAddress(addressedTo, fullNameOfAddressedTo);

            msg.setRecipients(Message.RecipientType.TO, addressTo);

            // Setting the Subject and Content Type
            msg.setSubject(subject);
            msg.setContent(message, "text/html; charset=utf-8");  // org  "text/plain"

            Transport.send(msg);
        } catch (UnsupportedEncodingException | MessagingException e) {
            System.out.println(e);
        }
    }

    public FileInputStream createExcelJobReportFileInputStream(
            URL FileUrl,
            JobManagerUser user,
            Department reportingDepartment,
            DatePeriodJobReport jobSubCategoryReport,
            DatePeriodJobReport sectorReport,
            DatePeriodJobReport jobQuantitiesAndServicesReport) throws URISyntaxException {

        try {
            File file = new File(FileUrl.toURI());
            FileInputStream inp = new FileInputStream(file);
            int row = 0;

            // create workbook from input file
            POIFSFileSystem fileSystem = new POIFSFileSystem((FileInputStream) inp);
            HSSFWorkbook wb = new HSSFWorkbook(fileSystem);
            HSSFCellStyle dataCellStyle = wb.createCellStyle();
            HSSFCellStyle headerCellStyle = wb.createCellStyle();
            headerCellStyle.setFont(BusinessEntityUtils.createBoldFont(wb, (short) 14, HSSFColor.BLUE.index));
            HSSFCellStyle columnHeaderCellStyle = wb.createCellStyle();
            columnHeaderCellStyle.setFont(BusinessEntityUtils.createBoldFont(wb, (short) 12, HSSFColor.BLUE.index));

            // create temp file for output
            FileOutputStream out = new FileOutputStream("MonthlyReport" + user.getId() + ".xls");

            HSSFSheet jobSheet = wb.getSheet("Statistics");
            if (jobSheet == null) {
                jobSheet = wb.createSheet("Statistics");
            }

            BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                    "Job Statistics",
                    "java.lang.String", headerCellStyle);

            BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                    reportingDepartment.getName(),
                    "java.lang.String", headerCellStyle);

            BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                    BusinessEntityUtils.getDateInMediumDateFormat(new Date()),
                    "java.lang.String", headerCellStyle);

            BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                    jobSubCategoryReport.getDatePeriod(0).getPeriodString(),
                    "java.lang.String", headerCellStyle);

            row++;
            // subcategory report
            if (jobSubCategoryReport != null) {
                BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                        "EARNINGS",
                        "java.lang.String", headerCellStyle);

                row++;
                for (int i = 0; i < jobSubCategoryReport.getDatePeriods().length; i++) {
                    List<DatePeriodJobReportColumnData> reportColumnData = jobSubCategoryReport.getReportColumnData(jobSubCategoryReport.getDatePeriod(i).getName());
                    // insert table headings
                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                            jobSubCategoryReport.getDatePeriod(i).toString(), "java.lang.String", columnHeaderCellStyle);
                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 0,
                            "Job subcategory", "java.lang.String", columnHeaderCellStyle);
                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 1,
                            "Earnings", "java.lang.String", columnHeaderCellStyle);
                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 2,
                            "Calibrations/Tests", "java.lang.String", columnHeaderCellStyle);

                    // SUMMARY - earnings
                    for (DatePeriodJobReportColumnData data : reportColumnData) {
                        // subcategory
                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 0,
                                data.getJobSubCategory().getName(), "java.lang.String", dataCellStyle);
                        // cost
                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 1,
                                data.getJobCostingAndPayment().getFinalCost(), "java.lang.Double", dataCellStyle);
                        // test/cals
                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 2,
                                data.getNoOfTestsOrCalibrations(), "java.lang.Integer", dataCellStyle);
                    }
                    ++row;
                }
            }

            if (sectorReport != null) {
                BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                        "SECTORS SERVED",
                        "java.lang.String", headerCellStyle);

                row++;
                for (int i = 0; i < sectorReport.getDatePeriods().length; i++) {
                    List<DatePeriodJobReportColumnData> reportColumnData = sectorReport.getReportColumnData(sectorReport.getDatePeriod(i).getName());
                    // sector table headings
                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                            sectorReport.getDatePeriod(i).toString(), "java.lang.String", columnHeaderCellStyle);
                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 0,
                            "Sector", "java.lang.String", columnHeaderCellStyle);

                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 2,
                            "Calibrations/Tests", "java.lang.String", columnHeaderCellStyle);

                    // SECTOR - tests/cals
                    for (DatePeriodJobReportColumnData data : reportColumnData) {
                        // sector
                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 0,
                                data.getSector().getName(), "java.lang.String", dataCellStyle);
                        // test/cals
                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 2,
                                data.getNoOfTestsOrCalibrations(), "java.lang.Integer", dataCellStyle);
                    }
                    ++row;
                }
            }

            if (jobQuantitiesAndServicesReport != null) {
                BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                        "JOB QUANTITIES AND SERVICES",
                        "java.lang.String", headerCellStyle);

                row++;
                for (int i = 0; i < jobQuantitiesAndServicesReport.getDatePeriods().length; i++) {
                    List<DatePeriodJobReportColumnData> reportColumnData = jobQuantitiesAndServicesReport.getReportColumnData(jobQuantitiesAndServicesReport.getDatePeriod(i).getName());

                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                            sectorReport.getDatePeriod(i).toString(), "java.lang.String", headerCellStyle);
                    // JobQuantities And Services table headings
                    List<JobReportItem> jobReportItems = jobQuantitiesAndServicesReport.getJobReportItems();
                    for (JobReportItem jobReportItem : jobReportItems) {
                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 0,
                                jobReportItem.getName(),
                                "java.lang.String", dataCellStyle);

                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 1,
                                (Double) jobQuantitiesAndServicesReport.getReportItemValue(jobReportItem, jobQuantitiesAndServicesReport.getDatePeriod(i), reportColumnData),
                                "java.lang.Double", dataCellStyle);
                    }
                    row++;
                }
            }

            // write and save file for later use
            wb.write(out);
            out.close();

            return new FileInputStream("MonthlyReport" + user.getId() + ".xls");

        } catch (IOException ex) {
            System.out.println(ex);
        }

        return null;
    }

    public ByteArrayInputStream createExcelMonthlyReportFileInputStream(
            File reportFile,           
            Long departmentId) {

        try {
            FileInputStream inp = new FileInputStream(reportFile);
            int row = 2;
            
            XSSFWorkbook wb = new XSSFWorkbook(inp);

            XSSFCellStyle stringCellStyle = wb.createCellStyle();
            XSSFCellStyle longCellStyle = wb.createCellStyle();
            XSSFCellStyle integerCellStyle = wb.createCellStyle();
            XSSFCellStyle doubleCellStyle = wb.createCellStyle();
            XSSFCellStyle dateCellStyle = wb.createCellStyle();

            // Output stream for modified Excel file
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Get sheets
            XSSFSheet graphsAndCharts = wb.getSheet("Graphs & Charts");
            XSSFSheet valuations = wb.getSheet("Valuations");
            XSSFSheet talliesAndRatesA = wb.getSheet("Tallies & Rates (A)");
            XSSFSheet talliesAndRatesB = wb.getSheet("Tallies & Rates (B)");
            XSSFSheet rawData = wb.getSheet("Raw Data");

            // Get report data
            List<Object[]> reportData = Job.getJobReportRecords(
                    getEntityManager1(),
                    BusinessEntityUtils.getDateString(monthlyReportDataDatePeriod.getStartDate(), "'", "YMD", "-"),
                    BusinessEntityUtils.getDateString(monthlyReportDataDatePeriod.getEndDate(), "'", "YMD", "-"),
                    departmentId);

            // Fill in report data            
            System.out.println("rowdata: " + reportData.size());
            for (Object[] rowData : reportData) {
                System.out.println("Inserting row: " + row);
                // Job numbers
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 0,
                        (String) rowData[6],
                        "java.lang.String", stringCellStyle);
                // Client
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 1,
                        (String) rowData[8],
                        "java.lang.String", stringCellStyle);
                // Business office
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 2,
                        (String) rowData[11],
                        "java.lang.String", stringCellStyle);
                // Work progress
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 3,
                        (String) rowData[12],
                        "java.lang.String", stringCellStyle);
                // Classification
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 7,
                        (String) rowData[13],
                        "java.lang.String", stringCellStyle);
                // Category
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 8,
                        (String) rowData[14],
                        "java.lang.String", stringCellStyle);
                // Section (Subcategory
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 9,
                        (String) rowData[15],
                        "java.lang.String", stringCellStyle);
                // Section (Subcategory
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 10,
                        (String) rowData[16],
                        "java.lang.String", stringCellStyle);
                // Assigned department
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 14,
                        (String) rowData[9],
                        "java.lang.String", stringCellStyle);
                // Assigned department
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 15,
                        (String) rowData[10],
                        "java.lang.String", stringCellStyle);
                // No. samples
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 16,
                        (Long) rowData[5],
                        "java.lang.Long", longCellStyle);
                // No. test
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 18,
                        (Integer) rowData[4],
                        "java.lang.Integer", integerCellStyle);
                // Total deposit
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 31,
                        (Double) rowData[26],
                        "java.lang.Double", doubleCellStyle);
                // Amount due
                if ((rowData[27] != null) && (rowData[26] != null)) {
                    BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 32,
                            (Double) rowData[27] - (Double) rowData[26],
                            "java.lang.Double", doubleCellStyle);
                }
                // Estimated cost
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 33,
                        (Double) rowData[28],
                        "java.lang.Double", doubleCellStyle);
                // Final cost
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 34,
                        (Double) rowData[27],
                        "java.lang.Double", doubleCellStyle);
                //  Job entry date
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 36,
                        (Date) rowData[20],
                        "java.util.Date", dateCellStyle);
                //  Submission date 
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 37,
                        (Date) rowData[29],
                        "java.util.Date", dateCellStyle);
                //  Expected date completion
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 38,
                        (Date) rowData[17],
                        "java.util.Date", dateCellStyle);
                //  Completion date
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 39,
                        (Date) rowData[19],
                        "java.util.Date", dateCellStyle);
                //  Assignee
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 41,
                        (String) rowData[21] + " " + (String) rowData[22],
                        "java.lang.String", stringCellStyle);
                //  Entered by firstname
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 42,
                        (String) rowData[24],
                        "java.lang.String", stringCellStyle);
                //  Entered by lastname
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 43,
                        (String) rowData[25],
                        "java.lang.String", stringCellStyle);
                //  List of samples
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 44,
                        (String) rowData[0],
                        "java.lang.String", stringCellStyle);
                //  List of sample brands
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 45,
                        (String) rowData[1],
                        "java.lang.String", stringCellStyle);
                //  List of sample brands
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 46,
                        (String) rowData[2],
                        "java.lang.String", stringCellStyle);
                //  List of sample brands
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 47,
                        (String) rowData[30],
                        "java.lang.String", stringCellStyle);
                row++;

            }

            // Insert data at top of sheet
            //  Department name
            Department department = Department.findDepartmentById(getEntityManager1(), departmentId);
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 1,
                    department.getName(),
                    "java.lang.String", stringCellStyle);
            //  Data starts at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 4,
                    monthlyReportDataDatePeriod.getStartDate(),
                    "java.util.Date", dateCellStyle);
            //  Data ends at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 6,
                    monthlyReportDataDatePeriod.getEndDate(),
                    "java.util.Date", dateCellStyle);
            //  Month starts at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 8,
                    monthlyReportDatePeriod.getStartDate(),
                    "java.util.Date", dateCellStyle);
            //  Month ends at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 10,
                    monthlyReportDatePeriod.getEndDate(),
                    "java.util.Date", dateCellStyle);
            // Year type
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 12,
                    monthlyReportYearDatePeriod.getName(),
                    "java.lang.String", stringCellStyle);
            //  Year starts at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 15,
                    monthlyReportYearDatePeriod.getStartDate(),
                    "java.util.Date", dateCellStyle);
            //  Year ends at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 17,
                    monthlyReportYearDatePeriod.getEndDate(),
                    "java.util.Date", dateCellStyle);

            // Update calculations in relevant sheets
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

            for (Row r : talliesAndRatesA) {
                for (Cell c : r) {
                    if (c.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        evaluator.evaluateFormulaCell(c);
                    }
                }
            }

            for (Row r : valuations) {
                for (Cell c : r) {
                    if (c.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        evaluator.evaluateFormulaCell(c);
                    }
                }
            }

            // Write modified Excel file and return it
            wb.write(out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public ByteArrayInputStream jobsCompletedByDepartmentFileInputStream(
            File reportFile,
            Long departmentId) {

        try {
            FileInputStream inp = new FileInputStream(reportFile);
            int row = 1;
            int col = 0;
            int cell = 0;

            XSSFWorkbook wb = new XSSFWorkbook(inp);

            XSSFCellStyle stringCellStyle = wb.createCellStyle();
            XSSFCellStyle longCellStyle = wb.createCellStyle();
            XSSFCellStyle integerCellStyle = wb.createCellStyle();
            XSSFCellStyle doubleCellStyle = wb.createCellStyle();
            XSSFCellStyle dateCellStyle = wb.createCellStyle();

            // Output stream for modified Excel file
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // Get sheets          
            XSSFSheet rawData = wb.getSheet("Raw Data");

            // Get report data            
            List<Object[]> reportData = Job.getCompletedJobRecords(
                    getEntityManager1(),
                    BusinessEntityUtils.getDateString(reportSearchParameters.getDatePeriod().getStartDate(), "'", "YMD", "-"),
                    BusinessEntityUtils.getDateString(reportSearchParameters.getDatePeriod().getEndDate(), "'", "YMD", "-"),
                    departmentId);

            // Fill in report data            
            for (Object[] rowData : reportData) {
                col = 0;
                //  Assignee
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[19],
                        "java.lang.String", stringCellStyle);
                // No. samples
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Long) rowData[25],
                        "java.lang.Long", longCellStyle);
                // No. tests/calibrations
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Integer) rowData[26],
                        "java.lang.Integer", integerCellStyle);
                // No. tests
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Integer) rowData[27],
                        "java.lang.Integer", integerCellStyle);
                // No. calibrations
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Integer) rowData[28],
                        "java.lang.Integer", integerCellStyle);
                // Total cost
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Double) rowData[21],
                        "java.lang.Double", doubleCellStyle);
                //  Completion date
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Date) rowData[15],
                        "java.util.Date", dateCellStyle);
                //  Expected completion date
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Date) rowData[29],
                        "java.util.Date", dateCellStyle);
                // Job numbers
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[30],
                        "java.lang.String", stringCellStyle);

                row++;

            }

            wb.write(out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public ByteArrayInputStream analyticalServicesReportFileInputStream(
            File reportFile,
            Long departmentId) {

        try {
            FileInputStream inp = new FileInputStream(reportFile);
            int row = 1;
            int col = 0;
            int cell = 0;

            XSSFWorkbook wb = new XSSFWorkbook(inp);
            XSSFCellStyle stringCellStyle = wb.createCellStyle();
            XSSFCellStyle longCellStyle = wb.createCellStyle();
            XSSFCellStyle integerCellStyle = wb.createCellStyle();
            XSSFCellStyle doubleCellStyle = wb.createCellStyle();
            XSSFCellStyle dateCellStyle = wb.createCellStyle();

            // Output stream for modified Excel file
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // Get sheets          
            XSSFSheet rawData = wb.getSheet("Raw Data");
            XSSFSheet jobReportSheet = wb.getSheet("Jobs Report");
            XSSFSheet employeeReportSheet = wb.getSheet("Employee Report");
            XSSFSheet sectorReportSheet = wb.getSheet("Sector Report");

            // Get report data
            List<Object[]> reportData = Job.getCompletedJobRecords(
                    getEntityManager1(),
                    BusinessEntityUtils.getDateString(reportSearchParameters.getDatePeriod().getStartDate(), "'", "YMD", "-"),
                    BusinessEntityUtils.getDateString(reportSearchParameters.getDatePeriod().getEndDate(), "'", "YMD", "-"),
                    departmentId);

            // Fill in report data            
            for (Object[] rowData : reportData) {
                col = 0;
                //  Employee/Assignee
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[7],
                        "java.lang.String", stringCellStyle);
                // No. samples
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Long) rowData[9],
                        "java.lang.Long", longCellStyle);
                // No. tests/calibrations
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Integer) rowData[10],
                        "java.lang.Integer", integerCellStyle);
                // No. tests
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Integer) rowData[11],
                        "java.lang.Integer", integerCellStyle);
                // No. calibrations
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Integer) rowData[12],
                        "java.lang.Integer", integerCellStyle);
                // Total cost
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Double) rowData[8],
                        "java.lang.Double", doubleCellStyle);
                //  Completion date
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Date) rowData[6],
                        "java.util.Date", dateCellStyle);
                //  Expected completion date
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Date) rowData[13],
                        "java.util.Date", dateCellStyle);
                // Job numbers
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[14],
                        "java.lang.String", stringCellStyle);
                // Sample description
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[0],
                        "java.lang.String", stringCellStyle);
                // Client/Source
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[15],
                        "java.lang.String", stringCellStyle);
                //  Date submitted
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Date) rowData[16],
                        "java.util.Date", dateCellStyle);
                // Sector
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[17],
                        "java.lang.String", stringCellStyle);
                // Turnaround time status
                if ((rowData[6] != null) && (rowData[13] != null)) {
                    BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                            ((Date) rowData[6]).getTime() > ((Date) rowData[13]).getTime()
                            ? "late" : "on-time",
                            "java.lang.String", stringCellStyle);
                } else {
                    BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                            "",
                            "java.lang.String", stringCellStyle);
                }
                // Classification
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[18],
                        "java.lang.String", stringCellStyle);
                // Category
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[19],
                        "java.lang.String", stringCellStyle);
                // subcategory
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[20],
                        "java.lang.String", stringCellStyle);

                row++;

            }

            // Set department name and report period
            // Dept. name
            BusinessEntityUtils.setExcelCellValue(wb, jobReportSheet, 0, 0,
                    getReportingDepartment().getName(),
                    "java.lang.String", null);
            BusinessEntityUtils.setExcelCellValue(wb, employeeReportSheet, 0, 0,
                    getReportingDepartment().getName(),
                    "java.lang.String", null);
            BusinessEntityUtils.setExcelCellValue(wb, sectorReportSheet, 0, 0,
                    getReportingDepartment().getName(),
                    "java.lang.String", null);
            // Period
            BusinessEntityUtils.setExcelCellValue(wb, jobReportSheet, 2, 0,
                    BusinessEntityUtils.getDateInMediumDateFormat(reportSearchParameters.getDatePeriod().getStartDate())
                    + " - "
                    + BusinessEntityUtils.getDateInMediumDateFormat(reportSearchParameters.getDatePeriod().getEndDate()),
                    "java.lang.String", null);
            BusinessEntityUtils.setExcelCellValue(wb, employeeReportSheet, 2, 0,
                    BusinessEntityUtils.getDateInMediumDateFormat(reportSearchParameters.getDatePeriod().getStartDate())
                    + " - "
                    + BusinessEntityUtils.getDateInMediumDateFormat(reportSearchParameters.getDatePeriod().getEndDate()),
                    "java.lang.String", null);
            BusinessEntityUtils.setExcelCellValue(wb, sectorReportSheet, 2, 0,
                    BusinessEntityUtils.getDateInMediumDateFormat(reportSearchParameters.getDatePeriod().getStartDate())
                    + " - "
                    + BusinessEntityUtils.getDateInMediumDateFormat(reportSearchParameters.getDatePeriod().getEndDate()),
                    "java.lang.String", null);

            // Write modified Excel file and return it
            wb.write(out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public ByteArrayInputStream createExcelMonthlyReportFileInputStream2(
            File reportFile,
            Long departmentId) {

        try {

            // create workbook from input file
            POIFSFileSystem fileSystem = new POIFSFileSystem(new FileInputStream(reportFile));
            HSSFWorkbook wb = new HSSFWorkbook(fileSystem);

            // ensure that crucial sheets are updated automatically
            int row = 2;

            HSSFCellStyle stringCellStyle = wb.createCellStyle();
            HSSFCellStyle longCellStyle = wb.createCellStyle();
            HSSFCellStyle integerCellStyle = wb.createCellStyle();
            HSSFCellStyle doubleCellStyle = wb.createCellStyle();
            HSSFCellStyle dateCellStyle = wb.createCellStyle();

            // Output stream for modified Excel file
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Get sheets
            HSSFSheet graphsAndCharts = wb.getSheet("Graphs & Charts");
            graphsAndCharts.setForceFormulaRecalculation(true);

            HSSFSheet valuations = wb.getSheet("Valuations");
            valuations.setForceFormulaRecalculation(true);

            HSSFSheet talliesAndRatesA = wb.getSheet("Tallies & Rates (A)");
            talliesAndRatesA.setForceFormulaRecalculation(true);

            HSSFSheet talliesAndRatesB = wb.getSheet("Tallies & Rates (B)");
            talliesAndRatesB.setForceFormulaRecalculation(true);

            HSSFSheet rawData = wb.getSheet("Raw Data");

            // Get report data
            List<Object[]> reportData = Job.getJobReportRecords(
                    getEntityManager1(),
                    BusinessEntityUtils.getDateString(monthlyReportDataDatePeriod.getStartDate(), "'", "YMD", "-"),
                    BusinessEntityUtils.getDateString(monthlyReportDataDatePeriod.getEndDate(), "'", "YMD", "-"),
                    departmentId);

            // Fill in report data            
            System.out.println("rowdata: " + reportData.size());
            for (Object[] rowData : reportData) {
                System.out.println("Inserting row: " + row);
                // Job numbers
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 0,
                        (String) rowData[6],
                        "java.lang.String", stringCellStyle);
                // Client
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 1,
                        (String) rowData[8],
                        "java.lang.String", stringCellStyle);
                // Business office
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 2,
                        (String) rowData[11],
                        "java.lang.String", stringCellStyle);
                // Work progress
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 3,
                        (String) rowData[12],
                        "java.lang.String", stringCellStyle);
                // Classification
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 7,
                        (String) rowData[13],
                        "java.lang.String", stringCellStyle);
                // Category
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 8,
                        (String) rowData[14],
                        "java.lang.String", stringCellStyle);
                // Section (Subcategory
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 9,
                        (String) rowData[15],
                        "java.lang.String", stringCellStyle);
                // Section (Subcategory
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 10,
                        (String) rowData[16],
                        "java.lang.String", stringCellStyle);
                // Assigned department
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 14,
                        (String) rowData[9],
                        "java.lang.String", stringCellStyle);
                // Assigned department
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 15,
                        (String) rowData[10],
                        "java.lang.String", stringCellStyle);
                // No. samples
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 16,
                        (Long) rowData[5],
                        "java.lang.Long", longCellStyle);
                // No. test
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 18,
                        (Integer) rowData[4],
                        "java.lang.Integer", integerCellStyle);
                // Total deposit
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 31,
                        (Double) rowData[26],
                        "java.lang.Double", doubleCellStyle);
                // Amount due
                if ((rowData[27] != null) && (rowData[26] != null)) {
                    BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 32,
                            (Double) rowData[27] - (Double) rowData[26],
                            "java.lang.Double", doubleCellStyle);
                }
                // Estimated cost
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 33,
                        (Double) rowData[28],
                        "java.lang.Double", doubleCellStyle);
                // Final cost
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 34,
                        (Double) rowData[27],
                        "java.lang.Double", doubleCellStyle);
                //  Job entry date
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 36,
                        (Date) rowData[20],
                        "java.util.Date", dateCellStyle);
                //  Submission date 
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 37,
                        (Date) rowData[29],
                        "java.util.Date", dateCellStyle);
                //  Expected date completion
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 38,
                        (Date) rowData[17],
                        "java.util.Date", dateCellStyle);
                //  Completion date
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 39,
                        (Date) rowData[19],
                        "java.util.Date", dateCellStyle);
                //  Assignee
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 41,
                        (String) rowData[21] + " " + (String) rowData[22],
                        "java.lang.String", stringCellStyle);
                //  Entered by firstname
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 42,
                        (String) rowData[24],
                        "java.lang.String", stringCellStyle);
                //  Entered by lastname
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 43,
                        (String) rowData[25],
                        "java.lang.String", stringCellStyle);
                //  List of samples
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 44,
                        (String) rowData[0],
                        "java.lang.String", stringCellStyle);
                //  List of sample brands
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 45,
                        (String) rowData[1],
                        "java.lang.String", stringCellStyle);
                //  List of sample brands
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 46,
                        (String) rowData[2],
                        "java.lang.String", stringCellStyle);
                //  List of sample brands
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 47,
                        (String) rowData[30],
                        "java.lang.String", stringCellStyle);
                row++;

            }

            // Insert data at top of sheet
            //  Department name
            Department department = Department.findDepartmentById(getEntityManager1(), departmentId);
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 1,
                    department.getName(),
                    "java.lang.String", stringCellStyle);
            //  Data starts at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 4,
                    monthlyReportDataDatePeriod.getStartDate(),
                    "java.util.Date", dateCellStyle);
            //  Data ends at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 6,
                    monthlyReportDataDatePeriod.getEndDate(),
                    "java.util.Date", dateCellStyle);
            //  Month starts at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 8,
                    monthlyReportDatePeriod.getStartDate(),
                    "java.util.Date", dateCellStyle);
            //  Month ends at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 10,
                    monthlyReportDatePeriod.getEndDate(),
                    "java.util.Date", dateCellStyle);
            // Year type
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 12,
                    monthlyReportYearDatePeriod.getName(),
                    "java.lang.String", stringCellStyle);
            //  Year starts at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 15,
                    monthlyReportYearDatePeriod.getStartDate(),
                    "java.util.Date", dateCellStyle);
            //  Year ends at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 17,
                    monthlyReportYearDatePeriod.getEndDate(),
                    "java.util.Date", dateCellStyle);

            wb.write(out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    HSSFCellStyle getDefaultCellStyle(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("Arial");
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        return cellStyle;
    }

    Font getWingdingsFont(HSSFWorkbook wb) {
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 14);
        font.setFontName("Wingdings");

        return font;
    }

    Font getFont(HSSFWorkbook wb, String fontName, short fontsize) {
        Font font = wb.createFont();
        font.setFontHeightInPoints(fontsize);
        font.setFontName(fontName);

        return font;
    }

    public FileInputStream createServiceContractExcelFileInputStream_bkup(
            EntityManager em,
            JobManagerUser user,
            Long jobId,
            String filePath) {
        try {

            Job job = Job.findJobById(em, jobId);

            Client ativeClient = Application.getActiveClientByNameIfAvailable(em, job.getClient());

            File file = new File(filePath);

            FileInputStream inp = new FileInputStream(file);

            // create workbook from input file
            POIFSFileSystem fileSystem = new POIFSFileSystem((FileInputStream) inp);

            HSSFWorkbook wb = new HSSFWorkbook(fileSystem);
            HSSFCellStyle dataCellStyle = getDefaultCellStyle(wb);
            // Default font
            Font defaultFont = getFont(wb, "Arial", (short) 16);
            Font departmentUseOnlyFont = getFont(wb, "Arial", (short) 12);

            // create temp file for output
            FileOutputStream out = new FileOutputStream("ServiceContract-" + user.getId() + ".xls");

            // get service contract sheet
            HSSFSheet serviceContractSheet = wb.getSheet("ServiceContract");
            serviceContractSheet.setActive(true);
            serviceContractSheet.setForceFormulaRecalculation(true);

            // fill in job data
            // company name and address
            dataCellStyle.setBorderLeft((short) 2);
            dataCellStyle.setFont(defaultFont);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 4, 0,
                    ativeClient.getName(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 5, 0,
                    ativeClient.getBillingAddress().getAddressLine1(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 6, 0,
                    ativeClient.getBillingAddress().getAddressLine2(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 7, 0,
                    ativeClient.getBillingAddress().getStateOrProvince(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 8, 0,
                    ativeClient.getBillingAddress().getCity(),
                    "java.lang.String", dataCellStyle);

            // contracting business office
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 4, 8,
                    job.getBusinessOffice().getName(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 5, 8,
                    job.getBusinessOffice().getAddress().getAddressLine1(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 6, 8,
                    job.getBusinessOffice().getAddress().getAddressLine2(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 7, 8,
                    job.getBusinessOffice().getAddress().getStateOrProvince(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 8, 8,
                    job.getBusinessOffice().getAddress().getCity(),
                    "java.lang.String", dataCellStyle);

            // Department use only
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(departmentUseOnlyFont);
            dataCellStyle.setAlignment(CellStyle.VERTICAL_BOTTOM);
            dataCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 4, 16,
                    job.getDepartment().getName(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 5, 16,
                    job.getJobNumber(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 6, 16,
                    job.getJobStatusAndTracking().getEnteredBy().getFirstName() + " " + job.getJobStatusAndTracking().getEnteredBy().getLastName(),
                    "java.lang.String", dataCellStyle);
            // Date and time
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 7, 16,
                    BusinessEntityUtils.getDateInMediumDateAndTimeFormat(new Date()),
                    "java.lang.String", dataCellStyle);

            // contact person
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 2);
            dataCellStyle.setFont(defaultFont);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 11, 0,
                    ativeClient.getMainContact(),
                    "java.lang.String", dataCellStyle);

            // phone number
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 13, 0,
                    ativeClient.getMainContact().getMainPhoneNumber().getLocalNumber(),
                    "java.lang.String", dataCellStyle);

            // fax number
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 13, 4,
                    ativeClient.getMainContact().getMainFaxNumber().getLocalNumber(),
                    "java.lang.String", dataCellStyle);

            // email address
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 13, 7,
                    ativeClient.getMainContact().getInternet().getEmail1(),
                    "java.lang.String", dataCellStyle);

            // p.o number
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            dataCellStyle.setBorderBottom((short) 2);
            dataCellStyle.setBorderLeft((short) 2);
            dataCellStyle.setFont(defaultFont);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 0,
                    job.getJobCostingAndPayment().getPurchaseOrderNumber(),
                    "java.lang.String", dataCellStyle);

            // date submitted
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 3,
                    BusinessEntityUtils.getDateInMediumDateFormat(job.getJobStatusAndTracking().getDateSubmitted()),
                    "java.lang.String", dataCellStyle);

            // estimated turn around time
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 6,
                    job.getEstimatedTurnAroundTimeInDays() + " day(s)",
                    "java.lang.String", dataCellStyle);

            // estimated cost
            dataCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 8,
                    job.getJobCostingAndPayment().getEstimatedCost(),
                    "Currency", dataCellStyle);

            // total deposit
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 11,
                    job.getJobCostingAndPayment().getDeposit(),
                    "Currency", dataCellStyle);

            // receipt no(s)        
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 14,
                    job.getJobCostingAndPayment().getReceiptNumber(),
                    "java.lang.String", dataCellStyle);

            // date deposit paid
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            dataCellStyle.setBorderBottom((short) 2);
            dataCellStyle.setFont(defaultFont);

            if (job.getJobStatusAndTracking().getDepositDate() != null) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 21, 17,
                        BusinessEntityUtils.getDateInMediumDateFormat(job.getJobStatusAndTracking().getDepositDate()),
                        "java.lang.String", dataCellStyle);
            }

            // service requested
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            if (job.getServiceContract().getServiceRequestedCalibration()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 23, 2,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (job.getServiceContract().getServiceRequestedLabelEvaluation()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 23, 4,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (job.getServiceContract().getServiceRequestedInspection()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 0,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (job.getServiceContract().getServiceRequestedConsultancy()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 2,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (job.getServiceContract().getServiceRequestedTraining()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 4,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (job.getServiceContract().getServiceRequestedOther()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 6,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setBorderLeft((short) 2);
            if (job.getServiceContract().getServiceRequestedTesting()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 23, 0,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderRight((short) 2);
            if ((job.getServiceContract().getServiceRequestedOtherText() != null)
                    && (!job.getServiceContract().getServiceRequestedOtherText().isEmpty())) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 7,
                        "Other (" + job.getServiceContract().getServiceRequestedOtherText() + ")",
                        "java.lang.String", dataCellStyle);
            }

            // details/samples
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 2);
            dataCellStyle.setBorderLeft((short) 2);
            dataCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
            dataCellStyle.setWrapText(true);
            dataCellStyle.setFont(defaultFont);
            String samplesDetail = "";

            for (JobSample jobSample : job.getJobSamples()) {
                samplesDetail = samplesDetail + jobSample.getSampleDetail() + "\n";
            }
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 26, 0,
                    samplesDetail,
                    "java.lang.String", dataCellStyle);

            // Special instructions           
            // NB: Instruction and joined with special instructions for now
            // until new service contract is implemented.
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 25, 8,
                    job.getInstructions() + "\n" + job.getServiceContract().getSpecialInstructions(),
                    "java.lang.String", dataCellStyle);

            // intended market
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            if (job.getServiceContract().getIntendedMarketLocal()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 47, 16, // org 49, 16
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (job.getServiceContract().getIntendedMarketCaricom()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 47, 18,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (job.getServiceContract().getIntendedMarketUK()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 47, 20,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (job.getServiceContract().getIntendedMarketCanada()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 49, 18,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (job.getServiceContract().getIntendedMarketOther()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 49, 20,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            dataCellStyle.setBorderLeft((short) 2);
            if (job.getServiceContract().getIntendedMarketUSA()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 49, 16,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            // Sample disposal
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setBorderLeft((short) 2);
            // Collected by the client within 30 days
            if (checkForSampleDisposalMethod(currentJob.getJobSamples(), 1)) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 47, 0,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            // Disposed of by the company
            if (checkForSampleDisposalMethod(job.getJobSamples(), 2)) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 48, 0,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            // addservice requested
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setBorderLeft((short) 2);
            if (job.getServiceContract().getAdditionalServiceUrgent()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 14, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            if (job.getServiceContract().getAdditionalServiceFaxResults()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 15, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            if (job.getServiceContract().getAdditionalServiceTelephonePresumptiveResults()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 16, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            if (job.getServiceContract().getAdditionalServiceSendMoreContractForms()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 17, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            if (job.getServiceContract().getAdditionalServiceOther()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 18, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            // other additinal service
            dataCellStyle = getDefaultCellStyle(wb);
            Font font = getFont(wb, "Arial", (short) 14);
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            dataCellStyle.setFont(font);
            dataCellStyle.setBorderBottom((short) 1);
            if (job.getServiceContract().getAdditionalServiceOtherText() != null) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 18, 13,
                        job.getServiceContract().getAdditionalServiceOtherText(),
                        "java.lang.String", dataCellStyle);
            }

            // write and save file for later use
            wb.write(out);
            out.close();

            return new FileInputStream("ServiceContract-" + user.getId() + ".xls");
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    public FileInputStream createServiceContractExcelFileInputStream(
            EntityManager em,
            JobManagerUser user,
            Long jobId,
            String filePath) {
        try {

            Job job = Job.findJobById(em, jobId);
            job.getJobCostingAndPayment().calculateAmountDue();

            Client ativeClient = Application.getActiveClientByNameIfAvailable(em, job.getClient());

            File file = new File(filePath);

            FileInputStream inp = new FileInputStream(file);

            // Create workbook from input file
            POIFSFileSystem fileSystem = new POIFSFileSystem((FileInputStream) inp);

            HSSFWorkbook wb = new HSSFWorkbook(fileSystem);

            // Fonts
            Font defaultFont = getFont(wb, "Arial", (short) 10);
            Font samplesFont = getFont(wb, "Arial", (short) 9);
            Font samplesRefFont = getFont(wb, "Arial", (short) 9);
            samplesRefFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            Font jobNumberFont = getFont(wb, "Arial Black", (short) 14);

            // Cell style
            HSSFCellStyle dataCellStyle;

            // create temp file for output
            FileOutputStream out = new FileOutputStream("ServiceContract-" + user.getId() + ".xls");

            // get service contract sheet
            HSSFSheet serviceContractSheet = wb.getSheet("ServiceContract");
            serviceContractSheet.setActive(true);
            serviceContractSheet.setForceFormulaRecalculation(true);

            // Fill in job data
            // Job number
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 2);
            dataCellStyle.setFont(jobNumberFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A6", // A = 0 , 6 = 5
                    currentJob.getJobNumber(),
                    "java.lang.String", dataCellStyle);

            // Contracting business office       
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "K6",
                    currentJob.getBusinessOffice().getName(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "K7",
                    "",
                    "java.lang.String", dataCellStyle);

            // Job entry agent:
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F9",
                    currentJob.getJobStatusAndTracking().getEnteredBy().getName(),
                    "java.lang.String", dataCellStyle);

            // Date agent prepared contract:   
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F10",
                    currentJob.getJobStatusAndTracking().getDateAndTimeEntered(),
                    "java.util.Date", dataCellStyle);

            // Department in charge of job (Parent department):   
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F11",
                    currentJob.getDepartment().getName(),
                    "java.lang.String", dataCellStyle);

            // Estimated turn around time:
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F12",
                    currentJob.getEstimatedTurnAroundTimeInDays(),
                    "java.lang.String", dataCellStyle);

            // Estimated Sub Total (J$):
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F13",
                    currentJob.getJobCostingAndPayment().getEstimatedCost(),
                    "Currency", dataCellStyle);

            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F14",
                    currentJob.getJobCostingAndPayment().getEstimatedCost()
                    * currentJob.getJobCostingAndPayment().getPercentageGCT() / 100,
                    "Currency", dataCellStyle);

            // Estimated Total Cost (J$):
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F15",
                    currentJob.getJobCostingAndPayment().getEstimatedCostIncludingTaxes(),
                    "Currency", dataCellStyle);

            // Minimum First Deposit (J$):
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F16",
                    currentJob.getJobCostingAndPayment().getMinDepositIncludingTaxes(),
                    "Currency", dataCellStyle);

            // RECEIPT #
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            dataCellStyle.setWrapText(true);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "R9",
                    currentJob.getJobCostingAndPayment().getReceiptNumber(),
                    "java.lang.String", dataCellStyle);

            // TOTAL PAID (J$)
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            dataCellStyle.setWrapText(true);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "V9",
                    currentJob.getJobCostingAndPayment().getDeposit(),
                    "Currency", dataCellStyle);

            // PAYMENT BREAKDOWN
            // Calculate GCT and Job Cost
            Double jobCost = (100.0 * currentJob.getJobCostingAndPayment().getDeposit())
                    / (currentJob.getJobCostingAndPayment().getPercentageGCT() + 100.0);
            Double jobGCT = currentJob.getJobCostingAndPayment().getDeposit() - jobCost;
            // GCT
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "AC9",
                    jobGCT,
                    "Currency", dataCellStyle);
            // Job
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "AC10",
                    jobCost,
                    "Currency", dataCellStyle);

            // DATE PAID (date of last payment)
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "AH9",
                    currentJob.getJobStatusAndTracking().getDepositDate(),
                    "java.util.Date", dataCellStyle);

            // BALANCE (amount due) 
            dataCellStyle = getDefaultCellStyle(wb);          
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            if (currentJob.getJobCostingAndPayment().getFinalCost() > 0.0) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AL9",
                        "exactly",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AL10",
                        currentJob.getJobCostingAndPayment().getAmountDue(),
                        "Currency", dataCellStyle);
            } else {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AL9",
                        "approximately",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AL10",
                        currentJob.getJobCostingAndPayment().getEstimatedCostIncludingTaxes()
                        - currentJob.getJobCostingAndPayment().getDeposit(),
                        "Currency", dataCellStyle);
            }

            // PAYMENT TERMS/INFORMATION
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setBorderRight((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
            if (!currentJob.getJobCostingAndPayment().getPaymentTerms().trim().equals("")) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "R12",
                        currentJob.getJobCostingAndPayment().getPaymentTerms(),
                        "java.lang.String", dataCellStyle);
            } else {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "R12",
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
            }

            // THE INFORMATION IN SECTION 3
            // AGENT/CASHIER
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setBorderRight((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "AL12",
                    currentJob.getJobCostingAndPayment().getLastPaymentEnteredBy().getName(),
                    "java.lang.String", dataCellStyle);

            // CLIENT NAME & BILLING ADDRESS
            // Name
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A20",
                    ativeClient.getName(),
                    "java.lang.String", dataCellStyle);
            // Address          
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A22",
                    ativeClient.getBillingAddress().getAddressLine1(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A23",
                    ativeClient.getBillingAddress().getAddressLine2(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A24",
                    ativeClient.getBillingAddress().getStateOrProvince(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A25",
                    ativeClient.getBillingAddress().getCity(),
                    "java.lang.String", dataCellStyle);

            // Contact person
            // Name
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "M20",
                    ativeClient.getMainContact(),
                    "java.lang.String", dataCellStyle);

            // Email
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "M22",
                    ativeClient.getMainContact().getInternet().getEmail1(),
                    "java.lang.String", dataCellStyle);

            // Phone
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "Z20",
                    ativeClient.getMainContact().getMainPhoneNumber(),
                    "java.lang.String", dataCellStyle);

            // Fax
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "Z22",
                    ativeClient.getMainContact().getMainFaxNumber(),
                    "java.lang.String", dataCellStyle);

            // TYPE OF SERVICE(S) NEEDED
            // Gather services
            String services = "";
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
            dataCellStyle.setWrapText(true);
            if (job.getServiceContract().getServiceRequestedTesting()) {
                services = services + "Testing";
            }
            if (job.getServiceContract().getServiceRequestedCalibration()) {
                services = services + "; Calibration";
            }
            if (job.getServiceContract().getServiceRequestedLabelEvaluation()) {
                services = services + "; Label Evaluation";
            }
            if (job.getServiceContract().getServiceRequestedInspection()) {
                services = services + "; Inspection";
            }
            if (job.getServiceContract().getServiceRequestedConsultancy()) {
                services = services + "; Consultancy";
            }
            if (job.getServiceContract().getServiceRequestedTraining()) {
                services = services + "; Training";
            }
            if (job.getServiceContract().getServiceRequestedOther()) {
                if ((job.getServiceContract().getServiceRequestedOtherText() != null)
                        && (!job.getServiceContract().getServiceRequestedOtherText().isEmpty())) {
                    services = services + "; " + job.getServiceContract().getServiceRequestedOtherText();
                }
            }
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "AD21",
                    services,
                    "java.lang.String", dataCellStyle);

            // Fax/Email report?:
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderRight((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
            if (job.getServiceContract().getAdditionalServiceFaxResults()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN21",
                        "Yes",
                        "java.lang.String", dataCellStyle);
            } else {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN21",
                        "No",
                        "java.lang.String", dataCellStyle);
            }

            // Expedite?
            if (job.getServiceContract().getAdditionalServiceUrgent()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN23",
                        "Yes",
                        "java.lang.String", dataCellStyle);
            } else {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN23",
                        "No",
                        "java.lang.String", dataCellStyle);
            }

            // Purchase Order:        
            if (currentJob.getJobCostingAndPayment().getPurchaseOrderNumber().equals("")) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN25",
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
            } else {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN25",
                        currentJob.getJobCostingAndPayment().getPurchaseOrderNumber(),
                        "java.lang.String", dataCellStyle);
            }

            // CLIENT INSTRUCTION/DETAILS FOR JOB
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "M24",
                    currentJob.getInstructions(),
                    "java.lang.String", dataCellStyle);

            // DESCRIPTION OF SUBMITTED SAMPLE(S)
            int samplesStartngRow = 32;
            if (job.getJobSamples().size() > 0) {
                for (JobSample jobSample : job.getJobSamples()) {
                    dataCellStyle = getDefaultCellStyle(wb);
                    dataCellStyle.setBorderLeft((short) 1);
                    dataCellStyle.setFont(samplesRefFont);
                    dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                    dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                    dataCellStyle.setWrapText(true);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "A" + samplesStartngRow,
                            jobSample.getReference(),
                            "java.lang.String", dataCellStyle);
                    dataCellStyle = getDefaultCellStyle(wb);
                    dataCellStyle.setBorderLeft((short) 1);
                    dataCellStyle.setFont(samplesFont);
                    dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                    dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                    dataCellStyle.setWrapText(true);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "B" + samplesStartngRow,
                            jobSample.getName(),
                            "java.lang.String", dataCellStyle);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "H" + samplesStartngRow,
                            jobSample.getProductBrand(),
                            "java.lang.String", dataCellStyle);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "O" + samplesStartngRow,
                            jobSample.getProductModel(),
                            "java.lang.String", dataCellStyle);

                    String productSerialAndCode = jobSample.getProductSerialNumber();
                    if (!jobSample.getProductCode().equals("")) {
                        productSerialAndCode = productSerialAndCode + "/" + jobSample.getProductCode();
                    }
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "W" + samplesStartngRow,
                            productSerialAndCode,
                            "java.lang.String", dataCellStyle);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "AG" + samplesStartngRow,
                            jobSample.getSampleQuantity(),
                            "java.lang.String", dataCellStyle);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "AI" + samplesStartngRow,
                            jobSample.getQuantity(),
                            "java.lang.String", dataCellStyle);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "AI" + samplesStartngRow,
                            jobSample.getQuantity() + " (" + jobSample.getUnitOfMeasure() + ")",
                            "java.lang.String", dataCellStyle);
                    // Disposal
                    if (jobSample.getMethodOfDisposal() == 2) {
                        BusinessEntityUtils.setExcelCellValue(
                                wb, serviceContractSheet, "AO" + samplesStartngRow,
                                "Yes",
                                "java.lang.String", dataCellStyle);
                    } else {
                        BusinessEntityUtils.setExcelCellValue(
                                wb, serviceContractSheet, "AO" + samplesStartngRow,
                                "No",
                                "java.lang.String", dataCellStyle);
                    }

                    samplesStartngRow++;
                }
            } else {
                dataCellStyle = getDefaultCellStyle(wb);
                dataCellStyle.setBorderLeft((short) 1);
                dataCellStyle.setFont(samplesRefFont);
                dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                dataCellStyle.setWrapText(true);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "A" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
                dataCellStyle = getDefaultCellStyle(wb);
                dataCellStyle.setBorderLeft((short) 1);
                dataCellStyle.setFont(samplesFont);
                dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                dataCellStyle.setWrapText(true);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "B" + samplesStartngRow,
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "H" + samplesStartngRow,
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "O" + samplesStartngRow,
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "W" + samplesStartngRow,
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AG" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AI" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AI" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
                // Disposal                   
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AO" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
            }

            // ADDITIONAL DETAILS FOR SAMPLE(S) 
            String details = "";
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            if (job.getJobSamples().isEmpty()) {
                details = "Not applicable";
            } else {
                for (JobSample jobSample : job.getJobSamples()) {
                    if (!jobSample.getDescription().isEmpty()) {
                        details = details + " (" + jobSample.getReference() + ") "
                                + jobSample.getDescription();
                    }
                }
            }
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A27",
                    details,
                    "java.lang.String", dataCellStyle);

            // write and save file for later use
            wb.write(out);
            out.close();

            return new FileInputStream("ServiceContract-" + user.getId() + ".xls");
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    public Boolean checkForSampleDisposalMethod(List<JobSample> samples, Integer method) {
        for (JobSample jobSample : samples) {
            if (jobSample.getMethodOfDisposal().compareTo(method) == 0) {
                return true;
            }
        }
        return false;
    }

    public void associateDepartmentWithJobSubCategory(
            EntityManager em,
            Department department,
            JobSubCategory jobSubCategory) {

        em.getTransaction().begin();
        jobSubCategory.getDepartments().add(department);
        BusinessEntityUtils.saveBusinessEntity(em, jobSubCategory);
        em.getTransaction().commit();
    }

    public void associateDepartmentWithSector(
            EntityManager em,
            Department department,
            Sector sector) {

        em.getTransaction().begin();
        sector.getDepartments().add(department);
        BusinessEntityUtils.saveBusinessEntity(em, sector);
        em.getTransaction().commit();
    }

    public void associateDepartmentWithJobReportItem(
            EntityManager em,
            Department department,
            JobReportItem jobReportItem) {

        em.getTransaction().begin();
        jobReportItem.getDepartments().add(department);
        BusinessEntityUtils.saveBusinessEntity(em, jobReportItem);
        em.getTransaction().commit();
    }

    public void updateAllJobs(EntityManager em) {
        long jobsUpdated = 0;

        try {
            List<Job> jobs = Job.findAllJobs(em);
            em.getTransaction().begin();
            for (Job job : jobs) {
                if (job.getJobStatusAndTracking() != null) {
                    if (job.getJobStatusAndTracking().getAlertDate() == null) {
                        System.out.println("Updating alert date for: " + job);
                        job.getJobStatusAndTracking().setAlertDate(new Date());
                        BusinessEntityUtils.saveBusinessEntity(em, job);
                        ++jobsUpdated;
                    }
                }
            }
            em.getTransaction().commit();
            System.out.println("Jobs updated: " + jobsUpdated);

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    /**
     * Get report using a JasperReport compiled report
     *
     * @param databaseDriverClass
     * @param databaseURL
     * @return
     */
    public FileInputStream getJasperReportFileInputStream(
            String databaseDriverClass,
            String databaseURL,
            String username,
            String password,
            String jasperReportFileName,
            String exportedReportType,
            HashMap parameters) {

        try {
            // Declare init default reporter
            JRExporter exporter = new JRPdfExporter();

            // Load the required JDBC driver and create the connection
            Class.forName(databaseDriverClass);
            Connection con = DriverManager.getConnection(databaseURL,
                    username,
                    password);

            // Fill report and export it
            JasperPrint print = JasperFillManager.fillReport(jasperReportFileName, parameters, con);

            // Get reporter
            if (exportedReportType.equalsIgnoreCase("HTML")) {
                exporter = new JRHtmlExporter();
            }

            // Configure the exporter (set output file name and print object)
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "file.pdf");
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
            exporter.exportReport();
            // prepare exported file for transmission to the client
            return new FileInputStream("file.pdf");

        } catch (ClassNotFoundException | SQLException | JRException | FileNotFoundException e) {
            System.out.println(e);
        }

        return null;
    }

    public void copyAllEntitiesToDatabase(String destPU, List<BusinessEntity> entities) {
        Integer numEntities = 0;

        try {

            EntityManager em = BusinessEntityUtils.getEntityManager(destPU);

            for (BusinessEntity entity : entities) {
                ++numEntities;
                System.out.println("Copying: " + entity + " : " + numEntities);
                em.getTransaction().begin();
                BusinessEntityUtils.saveBusinessEntity(em, entity);
                em.getTransaction().commit();
            }
            System.out.println("Saving/updating " + numEntities + " entities...");

            System.out.println("Done!");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Report getLatestJobReport(EntityManager em) {
        Report report = Report.findReportById(em, getJobReport().getId());
        em.refresh(report);

        return report;
    }

    public StreamedContent getJobEnteredByReportPDFFile() {

        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();

        try {

            reportEmployee = Employee.findEmployeeByName(em, getReportEmployee().getName());

            if (getReportEmployee().getId() != null) {

                jobReport = getLatestJobReport(em);
                String reportFileURL = jobReport.getReportFile();

                Connection con = BusinessEntityUtils.establishConnection(
                        jobReport.getDatabaseDriverClass(),
                        jobReport.getDatabaseURL(),
                        jobReport.getDatabaseUsername(),
                        jobReport.getDatabasePassword());
                if (con != null) {
                    StreamedContent streamContent;

                    parameters.put("startOFPeriod", reportSearchParameters.getDatePeriod().getStartDate());
                    parameters.put("endOFPeriod", reportSearchParameters.getDatePeriod().getEndDate());
                    parameters.put("inspectorID", getReportEmployee().getId());

                    // generate report
                    JasperPrint print = JasperFillManager.fillReport(reportFileURL, parameters, con);

                    byte[] fileBytes = JasperExportManager.exportReportToPdf(print);

                    streamContent = new DefaultStreamedContent(new ByteArrayInputStream(fileBytes), "application/pdf", "employee_job_entry.pdf");
                    setLongProcessProgress(100);

                    return streamContent;
                } else {
                    return null;
                }
            } else {
                getMain().displayCommonMessageDialog(null, "The name of employee is required for this report", "Employee Required", "info");
                return null;
            }

        } catch (JRException e) {
            System.out.println(e);
            setLongProcessProgress(100);

            return null;
        }

    }

    public StreamedContent getJobEnteredByDepartmentReportPDFFile() {

        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();

        try {

            reportingDepartment = Department.findDepartmentByName(em, getReportingDepartment().getName());

            if (getReportingDepartment().getId() != null) {

                jobReport = getLatestJobReport(em);
                String reportFileURL = jobReport.getReportFile();

                Connection con = BusinessEntityUtils.establishConnection(
                        jobReport.getDatabaseDriverClass(),
                        jobReport.getDatabaseURL(),
                        jobReport.getDatabaseUsername(),
                        jobReport.getDatabasePassword());
                if (con != null) {
                    StreamedContent streamContent;

                    parameters.put("startOFPeriod", reportSearchParameters.getDatePeriod().getStartDate());
                    parameters.put("endOFPeriod", reportSearchParameters.getDatePeriod().getEndDate());
                    parameters.put("departmentID", getReportingDepartment().getId());

                    // generate report
                    JasperPrint print = JasperFillManager.fillReport(reportFileURL, parameters, con);

                    byte[] fileBytes = JasperExportManager.exportReportToPdf(print);

                    streamContent = new DefaultStreamedContent(new ByteArrayInputStream(fileBytes), "application/pdf", "department_jobs_entered.pdf");
                    setLongProcessProgress(100);

                    return streamContent;
                } else {
                    return null;
                }
            } else {
                getMain().displayCommonMessageDialog(null, "The name of a department is required for this report", "Department Required", "info");
                return null;
            }

        } catch (JRException e) {
            System.out.println(e);
            setLongProcessProgress(100);

            return null;
        }

    }

    public StreamedContent getJobAssignedToDepartmentReportXLSFile() {

        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();

        try {

            reportingDepartment = Department.findDepartmentByName(em, getReportingDepartment().getName()); //getEmployeeByName(em, getReportEmployee().getName());

            // Use user's department if none found
            if (getReportingDepartment().getId() == null) {
                reportingDepartment = getUser().getEmployee().getDepartment();
            }

            jobReport = getLatestJobReport(em);
            String reportFileURL = jobReport.getReportFile();

            Connection con = BusinessEntityUtils.establishConnection(
                    jobReport.getDatabaseDriverClass(),
                    jobReport.getDatabaseURL(),
                    jobReport.getDatabaseUsername(),
                    jobReport.getDatabasePassword());
            if (con != null) {
                StreamedContent streamContent;

                parameters.put("startOFPeriod", reportSearchParameters.getDatePeriod().getStartDate());
                parameters.put("endOFPeriod", reportSearchParameters.getDatePeriod().getEndDate());
                parameters.put("departmentID", getReportingDepartment().getId());
                parameters.put("departmentName", getReportingDepartment().getName());
                // generate report
                JasperPrint print = JasperFillManager.fillReport(reportFileURL, parameters, con);

                JRXlsExporter exporterXLS = new JRXlsExporter();
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, print);
                exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, outStream);
                exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
                exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                exporterXLS.exportReport();

                streamContent = new DefaultStreamedContent(new ByteArrayInputStream(outStream.toByteArray()), "application/xls", "department_jobs_assigned.xls");
                setLongProcessProgress(100);

                return streamContent;
            } else {
                return null;
            }

        } catch (JRException e) {
            System.out.println(e);
            setLongProcessProgress(100);

            return null;
        }

    }

    public void openJobPricingsDialog() {
        Map<String, Object> options = new HashMap<>();
        options.put("modal", true);
        options.put("draggable", true);
        options.put("resizable", true);
        options.put("contentHeight", 600);

        searchText = "";

        RequestContext.getCurrentInstance().openDialog("jobPricings", options, null);
    }

    public void openJobCostingsDialog() {
        Map<String, Object> options = new HashMap<>();
        options.put("modal", true);
        options.put("draggable", true);
        options.put("resizable", true);
        options.put("contentWidth", 800);
        options.put("contentHeight", 600);

        RequestContext.getCurrentInstance().openDialog("jobCostings", options, null);
    }

    public void doUnitCostSearch() {
        RequestContext context = RequestContext.getCurrentInstance();

        unitCosts = UnitCost.findUnitCosts(getEntityManager1(), getUnitCostDepartment().getName(), getSearchText());
        context.update("unitCostsTableForm");
    }

    public void doJobCostSearch() {
        RequestContext context = RequestContext.getCurrentInstance();

        jobsWithCostings = Job.findJobsWithJobCosting(getEntityManager1(), getUnitCostDepartment().getName(), getSearchText());
        context.update("jobCostsTableForm");
    }

    public void createNewUnitCost() {
        RequestContext context = RequestContext.getCurrentInstance();

        currentUnitCost = new UnitCost();

        context.update("unitCostForm");
        context.execute("unitCostDialog.show();");
    }

    public void editUnitCost() {
    }

    @Override
    public void handleDialogOkButtonClick() {
    }

    @Override
    public void handleDialogYesButtonClick() {

        if (dialogActionHandlerId.equals("unitCostDirty")) {
            RequestContext context = RequestContext.getCurrentInstance();
            saveUnitCost();
            context.execute("unitCostDialog.hide();");
        }

    }

    @Override
    public void handleDialogNoButtonClick() {

        if (dialogActionHandlerId.equals("unitCostDirty")) {
            RequestContext context = RequestContext.getCurrentInstance();
            setDirty(false);
            context.execute("unitCostDialog.hide();");
        }
    }

    @Override
    public DialogActionHandler initDialogActionHandlerId(String id) {
        dialogActionHandlerId = id;
        return this;
    }

    @Override
    public String getDialogActionHandlerId() {
        return dialogActionHandlerId;
    }

    @Override
    public void handleDialogCancelButtonClick() {
    }

    public void onJobCostingsTableCellEdit(CellEditEvent event) {
        System.out.println("Job number of costing: " + getJobsWithCostings().get(event.getRowIndex()).getJobNumber());
    }

    public JobDataModel getJobsModel() {
        return new JobDataModel(jobSearchResultList);
    }

    public Boolean getDisableDepartment() {

        if (getUser().getPrivilege().getCanBeJMTSAdministrator()) {
            return false;
        }
        return getCurrentJob().getId() != null;
    }

    public Boolean getRenderSubContractingDepartment() {
        return getCurrentJob().getIsJobToBeSubcontracted() || getCurrentJob().isSubContracted();
    }

    /**
     * This is to be implemented further
     *
     * @return
     */
    public Boolean getDisableSubContracting() {
        try {
            if (getCurrentJob().isSubContracted() || isJobToBeCopied) {
                return false;
            } else if (getCurrentJob().getId() != null) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            System.out.println(e + ": getDisableSubContracting");
        }

        return false;
    }

    public List<String> getDepartmentSupervisorsEmailAddresses(Department department) {
        List<String> emails = new ArrayList<>();

        emails.add(getEmployeeDefaultEmailAdress(department.getHead()));
        // Get the email of the acting head of he/she is currently acting
        if (department.getActingHeadActive()) {
            emails.add(getEmployeeDefaultEmailAdress(department.getActingHead()));
        }

        return emails;
    }

    public String getEmployeeDefaultEmailAdress(Employee employee) {
        String address = "";

        // Get email1 which is treated as the employee's company email address
        if (!employee.getInternet().getEmail1().trim().equals("")) {
            address = employee.getInternet().getEmail1();
        } else {
            // Get and set default email using company domain
            EntityManager em = getEntityManager1();

            String listAsString = SystemOption.findSystemOptionByName(em, "domainNames").getOptionValue();
            String domainNames[] = listAsString.split(";");

            JobManagerUser user = JobManagerUser.findJobManagerUserByEmployeeId(em, employee.getId());

            // Build email address
            if (user != null) {
                address = user.getUsername();
                if (domainNames.length > 0) {
                    address = address + "@" + domainNames[0];
                }
            }

        }

        return address;
    }

    public Department getDepartmentAssignedToJob(Job job, EntityManager em) {

        Department dept;

        if (job.getSubContractedDepartment().getName().equals("--")
                || job.getSubContractedDepartment().getName().equals("")) {
            // This is not a subcontracted job see return to parent department            
            dept = Department.findDepartmentByName(em, job.getDepartment().getName());
            if (dept != null) {
                em.refresh(dept);
            }

            return dept;
        } else {
            dept = Department.findDepartmentByName(em, job.getSubContractedDepartment().getName());
            em.refresh(dept);

            return dept;
        }
    }

    public Boolean isCurrentJobNew() {
        return (getCurrentJob().getId() == null);
    }

    public void generateEmailAlerts() {

        EntityManager em = getEntityManager1();
        // Post job mail to the department if this is a new job and wasn't entered by 
        // by a person assignment 
        try {
            // Do not sent job email if user is a member of the department to which the job was assigned
            if (isCurrentJobNew() && !getUser().getEmployee().isMemberOf(getDepartmentAssignedToJob(currentJob, em))) {
                List<String> emails = getDepartmentSupervisorsEmailAddresses(getDepartmentAssignedToJob(currentJob, em));
                emails.add(getEmployeeDefaultEmailAdress(currentJob.getAssignedTo()));
                for (String email : emails) {
                    if (!email.equals("")) {
                        postJobManagerMail(null, email, "", "New Job Assignment", getNewJobEmailMessage(currentJob));
                    } else {
                        sendErrorEmail("The department's head email address is not valid!",
                                "Job number: " + currentJob.getJobNumber()
                                + "\nJMTS User: " + getUser().getUsername()
                                + "\nDate/time: " + new Date());
                    }
                }

            } else if (isDirty() && !getUser().getEmployee().isMemberOf(getDepartmentAssignedToJob(currentJob, em))) {
                List<String> emails = getDepartmentSupervisorsEmailAddresses(getDepartmentAssignedToJob(currentJob, em));
                emails.add(getEmployeeDefaultEmailAdress(currentJob.getAssignedTo()));
                for (String email : emails) {
                    if (!email.equals("")) {
                        postJobManagerMail(null, email, "", "Job Update Alert", getUpdatedJobEmailMessage(currentJob));
                    } else {
                        sendErrorEmail("The department's head email address is not valid!",
                                "Job number: " + currentJob.getJobNumber()
                                + "\nJMTS User: " + getUser().getUsername()
                                + "\nDate/time: " + new Date());
                    }
                }

            }

            // Emails related to job costing            
            if (sendJobCostingCompletedEmail) {
                sendJobCostingCompletedEmail = false;
                try {
                    List<String> emails = getDepartmentSupervisorsEmailAddresses(getDepartmentAssignedToJob(currentJob, em));
                    for (String email : emails) {
                        if (!email.equals("")) {
                            postJobManagerMail(null, email,
                                    ""/*BusinessEntityUtils.getPersonFullName(getDepartmentAssignedToJob(currentJob).getHead(), false)*/,
                                    "Job Costing Completed", getCompletedJobCostingEmailMessage(currentJob));
                        } else {
                            sendErrorEmail("The department's head email address is not valid!",
                                    "Job number: " + currentJob.getJobNumber()
                                    + "\nJMTS User: " + getUser().getUsername()
                                    + "\nDate/time: " + new Date());
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Error sending Costing Completed Email");
                    System.out.println(e);
                }
            }

            if (sendJobCostingApprovedEmail) {
                sendJobCostingApprovedEmail = false;
                try {
                    // Send email(s) to approved job costing email recipient(s) 
                    String listAsString = SystemOption.findSystemOptionByName(em, "approvedJobCostingEmailRecipients").getOptionValue();
                    String emails[] = listAsString.split(";");
                    for (String email : emails) {
                        if (!email.equals("")) {
                            postJobManagerMail(null, email,
                                    ""/*BusinessEntityUtils.getPersonFullName(getDepartmentAssignedToJob(currentJob, em).getHead(), false)*/,
                                    "Job Costing Approved", getApprovedJobCostingEmailMessage(currentJob));
                        } else {
                            sendErrorEmail("The approved job costing email recipient is not valid!",
                                    "Job number: " + currentJob.getJobNumber()
                                    + "\nJMTS User: " + getUser().getUsername()
                                    + "\nDate/time: " + new Date());
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Error sending Costing Approved Email");
                    System.out.println(e);
                }
            }

        } catch (Exception e) {
            System.out.println("Error generating email alert");
            System.out.println(e);
        }

    }

    public void openClientDatabaseDialog() {
        getMain().openDialog(null, "clientDatabaseDialog", true, true, true, 800, 600);
    }

    public Department getDepartmentBySystemOptionDeptId(String option) {
        EntityManager em = getEntityManager1();

        Long id = Long.parseLong(SystemOption.findSystemOptionByName(em, option).getOptionValue());

        Department department = Department.findDepartmentById(em, id);
        em.refresh(department);

        if (department != null) {
            return department;
        } else {
            return new Department("");
        }
    }

    public Boolean getIsMemberOfAccountsDept() {
        if (getUser().getEmployee().isMemberOf(getDepartmentBySystemOptionDeptId("accountsDepartmentId"))) {
            return true;
        }
        return false;
    }

    public Boolean getIsMemberOfCustomerServiceDept() {
        if (getUser().getEmployee().isMemberOf(getDepartmentBySystemOptionDeptId("customerServiceDeptId"))) {
            return true;
        }
        return false;
    }

    public Boolean getIsAuthorizedToModifyCostings() {
        // For now
        return true;
    }

    public List<SelectItem> getGCTPercentages() {
        ArrayList percentages = new ArrayList();

        percentages.addAll(Application.getStringListAsSelectItems(getEntityManager1(), "GCTPercentageList"));

        return percentages;
    }

}
