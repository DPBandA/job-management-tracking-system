/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jobmanagementlibrary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.entity.AccPacCustomer;
import jm.com.dpbennett.entity.AccPacDocument;
import jm.com.dpbennett.entity.Address;
import jm.com.dpbennett.entity.BusinessEntity;
import jm.com.dpbennett.entity.BusinessEntityManager;
import jm.com.dpbennett.entity.BusinessOffice;
import jm.com.dpbennett.entity.CashPayment;
import jm.com.dpbennett.entity.Classification;
import jm.com.dpbennett.entity.Client;
import jm.com.dpbennett.entity.Contact;
import jm.com.dpbennett.entity.CostCode;
import jm.com.dpbennett.entity.CostComponent;
import jm.com.dpbennett.entity.DatePeriod;
import jm.com.dpbennett.entity.Department;
import jm.com.dpbennett.entity.DepartmentReport;
import jm.com.dpbennett.entity.Distributor;
import jm.com.dpbennett.entity.DocumentSequenceNumber;
import jm.com.dpbennett.entity.DocumentStandard;
import jm.com.dpbennett.entity.DocumentType;
import jm.com.dpbennett.entity.Employee;
import jm.com.dpbennett.entity.FactoryInspection;
import jm.com.dpbennett.entity.FactoryInspectionComponent;
import jm.com.dpbennett.entity.Internet;
import jm.com.dpbennett.entity.Job;
import jm.com.dpbennett.entity.JobCategory;
import jm.com.dpbennett.entity.JobCosting;
import jm.com.dpbennett.entity.JobCostingAndPayment;
import jm.com.dpbennett.entity.JobManagerUser;
import jm.com.dpbennett.entity.JobReportItem;
import jm.com.dpbennett.entity.JobSample;
import jm.com.dpbennett.entity.JobSequenceNumber;
import jm.com.dpbennett.entity.JobStatusAndTracking;
import jm.com.dpbennett.entity.JobSubCategory;
import jm.com.dpbennett.entity.LegalDocument;
import jm.com.dpbennett.entity.Manufacturer;
import jm.com.dpbennett.entity.PetrolCompany;
import jm.com.dpbennett.entity.PetrolPump;
import jm.com.dpbennett.entity.PetrolPumpNozzle;
import jm.com.dpbennett.entity.PetrolStation;
import jm.com.dpbennett.entity.Report;
import jm.com.dpbennett.entity.ReportTableColumn;
import jm.com.dpbennett.entity.Seal;
import jm.com.dpbennett.entity.Sector;
import jm.com.dpbennett.entity.ServiceContract;
import jm.com.dpbennett.entity.ServiceRequest;
import jm.com.dpbennett.entity.Sticker;
import jm.com.dpbennett.entity.SystemOption;
import jm.com.dpbennett.entity.TestMeasure;
import jm.com.dpbennett.utils.BusinessEntityUtils;
import jm.com.dpbennett.utils.DatePeriodJobReport;
import jm.com.dpbennett.utils.DatePeriodJobReportColumnData;
import jm.com.dpbennett.utils.SearchParameters;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Desmond
 */
public class JobManager implements Serializable, BusinessEntityManager {

    @PersistenceUnit(unitName = "JMTSWebAppPU")
    private EntityManagerFactory EMF1;
    @PersistenceUnit(unitName = "AccPacPU")
    private EntityManagerFactory EMF2;
//    private JobManagement jm;
    private Boolean dirty;
    private Job currentJob;
    private Long selectedJobId;
    private Long currentJobId;
    private Job selectedJob;
    private JobSample selectedJobSample;
    private JobSample backupSelectedJobSample;
    private CashPayment selectedCashPayment;
    private Boolean addJobSample;
    private Boolean dynamicTabView;
    private String columnsToExclude = "";
    private Report jobReport;
    private StreamedContent jobReportFile;
    private StreamedContent jobCostingFile;
    private Boolean renderSearchComponent;
    private String tabTitle = "Job Entry";
    private Integer longProcessProgress = 0;
    private AccPacCustomer accPacCustomer;
    private List<AccPacDocument> accPacCustomerDocuments;
    private Boolean useAccPacCustomerList; // tk to be made option
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
    private Boolean showJobEntry = false;
    // job report vars
    private Department reportingDepartment;
    private String reportSearchText;
    // current period
    private List<Job> currentPeriodJobReportSearchResultList;
    private DatePeriod currentDatePeriod;
    // previous period
    private List<Job> previousPeriodJobReportSearchResultList;
    private DatePeriod previousDatePeriod;
    private List<Job> jobSearchResultList;
    private DatePeriodJobReport jobSubCategoryReport;
    private DatePeriodJobReport sectorReport;
    private DatePeriodJobReport jobQuantitiesAndServicesReport;
    private String invalidFormFieldMessage;
    private String userPrivilegeDialogHeader;
    private String userPrivilegeDialogMessage;
    private Boolean isNewContact = false;
    private Contact currentContact;
    private Integer loginAttempts = 0;
    private String selectedJobCostingTemplate;
    private Long databaseModuleId = 1L;
    private Boolean enableDatabaseModuleSelection = false;
    private String databaseModule = "";
    private SearchParameters currentSearchParameters;
    private Boolean isJobToBeCopied = false;
    private UserManagement userManagement;
    private SearchManagement searchManagement;
    private ClientManagement clientManagement;
    private MessageManagement messageManagement;

    /**
     * Creates a new instance of JobManagerBean
     */
    public JobManager() {
        // init fields
//        jm = new JobManagement();
        // accpac fields init
        accPacCustomer = new AccPacCustomer(null);
        accPacCustomer.setId(null);
        accPacCustomerDocuments = new ArrayList<AccPacDocument>();
        useAccPacCustomerList = false; //true; //tk to be made option
        jobReport = new Report();
        dirty = false;
        addJobSample = false;
        // reporting vars init
        // current period
        currentDatePeriod = new DatePeriod("This month", "month", null, null, false, false, true);
        previousDatePeriod = new DatePeriod("Last month", "month", null, null, false, false, true);
        // for searches based on selected tab
//        searhParameters.put("Job Search", new SearchParameters("Job Search", "dateSubmitted", "General", new DatePeriod("This month", "month", null, null, false, false), ""));
//        searhParameters.put("Service Request Search", new SearchParameters("Service Request Search", "dateSubmitted", "General", new DatePeriod("This month", "month", null, null, false, false), ""));
        dynamicTabView = true;
        renderSearchComponent = true;
        inputTextStyle = "font-weight: bold;width: 85%";
        defaultOutcome = "jmtlogin";
        outcome = defaultOutcome;
        selectedJobSample = new JobSample();
        jobSampleDialogTabViewActiveIndex = 0;
        invalidFormFieldMessage = "";
    }

    public MessageManagement getMessageManagement() {
        return messageManagement;
    }

    public void setMessageManagement(MessageManagement messageManagement) {
        this.messageManagement = messageManagement;
    }

    public SearchManagement getSearchManagement() {
        return searchManagement;
    }

    public void setSearchManagement(SearchManagement searchManagement) {
        this.searchManagement = searchManagement;
    }

    public ClientManagement getClientManagement() {
        return clientManagement;
    }

    public void setClientManagement(ClientManagement clientManagement) {
        this.clientManagement = clientManagement;
    }

    public UserManagement getUserManagement() {
        return userManagement;
    }

    public void setUserManagement(UserManagement userManagement) {
        this.userManagement = userManagement;
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
        if ((getCurrentJob().getDepartment().getId() == getUser().getEmployee().getDepartment().getId())
                || (getCurrentJob().getSubContractedDepartment().getId() == getUser().getEmployee().getDepartment().getId())) {
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

    public String getInvalidFormFieldMessage() {
        return invalidFormFieldMessage;
    }

    public void setInvalidFormFieldMessage(String invalidFormFieldMessage) {
        this.invalidFormFieldMessage = invalidFormFieldMessage;
    }

    public DatePeriod getCurrentDatePeriod() {
        return currentDatePeriod;
    }

    public void setCurrentDatePeriod(DatePeriod currentDatePeriod) {
        this.currentDatePeriod = currentDatePeriod;
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
        return reportingDepartment;
    }

    public void setReportingDepartment(Department reportingDepartment) {
        this.reportingDepartment = reportingDepartment;
    }

    public void editJob(ActionEvent actionEvent) {
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

    private Boolean isJobAssignedToUser() {
        if (getUser() != null) {
            if (currentJob.getAssignedTo().getId() == getUser().getEmployee().getId()) {
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
            if (currentJob.getDepartment().getId() == getUser().getEmployee().getDepartment().getId()) {
                return true;
            } else if (currentJob.getSubContractedDepartment().getId() == getUser().getEmployee().getDepartment().getId()) {
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
            return getUser().getCanEnterJob();
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
            return getUser().getCanEditDepartmentalJob() && isJobAssignedToUserDepartment();
        } else {
            return false;
        }
    }

    public Boolean getCanEditOwnJob() {
        if (getCanEnterJob()) {
            return true;
        }

        if (getUser() != null) {
            return getUser().getCanEditOwnJob(); //&& isJobAssignedToUser();
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

    public Long getSectorId() {
        return currentJob.getSector().getId();
    }

    public void setSectorId(Long sectorId) {
        this.sectorId = sectorId;
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

//    public JobManagement getJMLibrary() {
//        return jm;
//    }

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

        for (AccPacDocument doc : accPacCustomerDocuments) {
            if (doc.getDaysOverDocumentDate() >= days) {
                ++count;
            }
        }

        return count;
    }

    public BigDecimal getTotalInvoicesAmountOverMaxInvDays() {
        BigDecimal total = new BigDecimal(0.0);

        for (AccPacDocument doc : accPacCustomerDocuments) {
            if (doc.getDaysOverDocumentDate() > getMaxDaysPassInvoiceDate()) {
                total = total.add(doc.getCustCurrencyAmountDue());
            }
        }

        return total;
    }

    public BigDecimal getTotalInvoicesAmount() {
        BigDecimal total = new BigDecimal(0.0);

        for (AccPacDocument doc : accPacCustomerDocuments) {
            total = total.add(doc.getCustCurrencyAmountDue());
        }

        return total;
    }

    public Integer getMaxDaysPassInvoiceDate() {

        EntityManager em = getEntityManager1();

        int days = Integer.parseInt(SystemOption.findSystemOptionByName(em, "maxDaysPassInvoiceDate").getOptionValue());

        closeEntityManager(em);

        return days;
    }

    /**
     * Get status based on the total amount on documents pass the max allowed
     * days pass the invoice date
     *
     * @return
     */
    public String getAccPacCustomerAccountStatus() {

        if (getTotalInvoicesAmountOverMaxInvDays().doubleValue() > 0.0) {
            return "HOLD";
        } else {
            return "ACTIVE";
        }
    }

    public Integer getNumDocumentsPassMaxInvDate() {
        return getNumberOfDocumentsPassDocDate(getMaxDaysPassInvoiceDate());
    }

    public List<AccPacDocument> getAccPacCustomerDocuments() {
        return accPacCustomerDocuments;
    }

    public void setAccPacCustomerDocuments(List<AccPacDocument> accPacCustomerDocuments) {
        this.accPacCustomerDocuments = accPacCustomerDocuments;
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
        List<String> suggestions = new ArrayList<String>();

        return suggestions;
    }

    public StreamedContent getJobCostingFile() {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            jobCostingFile = getJobCostingStreamContent(em);

            setLongProcessProgress(100);

            closeEntityManager(em);

        } catch (Exception e) {
            closeEntityManager(em);
            System.out.println(e);
            setLongProcessProgress(100);
        }

        return jobCostingFile;
    }

    public StreamedContent getJobCostingStreamContent(EntityManager em) throws URISyntaxException {
        String costingTableColumns[] = {"Components", "Hour/Qty", "Rate", "Fee($)"};

        try {

            URL url = this.getClass().getResource("jobCosting.xls");
            File file = new File(url.toURI());
            FileInputStream inp = new FileInputStream(file);

            // create workbook from input file
            POIFSFileSystem fileSystem = new POIFSFileSystem((FileInputStream) inp);
            HSSFWorkbook wb = new HSSFWorkbook(fileSystem);

            // create temp file for output
            FileOutputStream out = new FileOutputStream("jobCosting.xls" + getUser().getId());

            // Ensure that the printable costing sheet formulae are
            // updated automatically
            HSSFSheet costingSheet = wb.getSheet("Costing");
            costingSheet.setActive(true);
            costingSheet.setForceFormulaRecalculation(true);

            // Get/Create sheet and insert costing data
            HSSFSheet costSheet = wb.getSheet("Costing Data");
            if (costSheet == null) {
                costSheet = wb.createSheet("Costing Data");
            }

            wb.setSheetOrder("Costing Data", 1);
            wb.setActiveSheet(0);
            // insert header
            BusinessEntityUtils.insertExcelHeader(wb, costSheet, "Costing Data", (short) 0, (short) 0, costingTableColumns.length, (short) 14);
            // Setup column headers
            // Create column header font
            BusinessEntityUtils.insertColumnHeaders(wb, costSheet, (short) 1, costingTableColumns, (short) 12);

            // Data font
            HSSFFont dataFont = wb.createFont();
            dataFont.setFontHeightInPoints((short) 12);
            // insert data
            short row = 2;
            HSSFCellStyle dataCellStyle = wb.createCellStyle();
            dataCellStyle.setFont(dataFont);
            HSSFRow dataRow = costSheet.createRow(row);
            // Insert costing data           
            refetchCurrentJob(em);
            for (JobCosting jobCosting : currentJob.getJobCostingAndPayment().getJobCostings()) {
                for (int i = 0; i < jobCosting.getCostComponents().size(); i++) {
                    dataRow = costSheet.createRow(row);
                    try {
                        CostComponent component = jobCosting.getCostComponents().get(i);
                        BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(0), component.getName(), "java.lang.String", null);
                        BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(1), component.getHoursOrQuantity(), "java.lang.Double", null);
                        BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(2), component.getRate(), "java.lang.Double", null);
                        BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(3), component.getCost(), "java.lang.Double", null);
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                    ++row;
                }
                // export sub total for the costing
                dataRow = costSheet.createRow(row);
                try {
                    BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(2), "SUB TOTAL ($):", "java.lang.String", null);
                    BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(3), jobCosting.getTotalCost(), "java.lang.Double", null);
                } catch (Exception ex) {
                    System.out.println(ex);
                }
                ++row;
            }

            // export total for the costing
            dataRow = costSheet.createRow(row);
            try {
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(2), "TOTAL ($):", "java.lang.String", null);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(3), currentJob.getJobCostingAndPayment().getTotalJobCostingsAmount(), "java.lang.Double", null);
            } catch (Exception ex) {
                System.out.println(ex);
            }
            ++row;

            // insert header/footer
            BusinessEntityUtils.insertExcelHeader(wb, costSheet, "General Data", row, (short) 0, 4, (short) 14);
            ++row;

            // insert header/footer data
            try {
                // dept
                dataRow = costSheet.createRow(row);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(0), "Department:", "java.lang.String", null);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(1), currentJob.getDepartment().getName(), "java.lang.String", null);
                ++row;
                // job #
                dataRow = costSheet.createRow(row);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(0), "Job number:", "java.lang.String", null);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(1), currentJob.getJobNumber(), "java.lang.String", null);
                ++row;
                // client
                dataRow = costSheet.createRow(row);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(0), "Client:", "java.lang.String", null);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(1), currentJob.getClient().getName(), "java.lang.String", null);
                ++row;
                // client contact person
                dataRow = costSheet.createRow(row);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(0), "Contact person:", "java.lang.String", null);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(1),
                        currentJob.getClient().getMainContact().getName(), "java.lang.String", null);
                ++row;
                // billing address
                dataRow = costSheet.createRow(row);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(0), "Client address:", "java.lang.String", null);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(1),
                        BusinessEntityUtils.getBasicAddress(currentJob.getClient().getBillingAddress()),
                        "java.lang.String", null);
                ++row;
                // report number
                dataRow = costSheet.createRow(row);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(0), "Report number:", "java.lang.String", null);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(1), currentJob.getReportNumber(), "java.lang.String", null);
                ++row;
                // costing date
                dataRow = costSheet.createRow(row);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(0), "Costing date:", "java.lang.String", null);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(1), currentJob.getJobCostingAndPayment().getCostingDate(), "java.util.Date", null);
                ++row;
                // po number
                dataRow = costSheet.createRow(row);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(0), "Purchase order number:", "java.lang.String", null);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(1), currentJob.getJobCostingAndPayment().getPurchaseOrderNumber(), "java.lang.String", null);
                ++row;
                // job decription
                dataRow = costSheet.createRow(row);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(0), "Description:", "java.lang.String", null);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(1), currentJob.getJobDescription(), "java.lang.String", null);
                ++row;
                // assignee(s)
                dataRow = costSheet.createRow(row);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(0), "Assignee:", "java.lang.String", null);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(1), currentJob.getAssignedTo().getFirstName() + " " + currentJob.getAssignedTo().getLastName(), "java.lang.String", null);
                ++row;
                // receipt #s
                dataRow = costSheet.createRow(row);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(0), "Receipt numbers:", "java.lang.String", null);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(1), currentJob.getJobCostingAndPayment().getReceiptNumber(), "java.lang.String", null);
                ++row;
                // deposits
                dataRow = costSheet.createRow(row);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(0), "Total deposits:", "java.lang.String", null);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(1), currentJob.getJobCostingAndPayment().getDeposit(), "java.lang.Double", null);
                ++row;
                // discounts
                dataRow = costSheet.createRow(row);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(0), "Discount:", "java.lang.String", null);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(1), currentJob.getJobCostingAndPayment().getDiscount(), "java.lang.Double", null);
                ++row;
                // final cost/amount due
                dataRow = costSheet.createRow(row);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(0), "Amount due:", "java.lang.String", null);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(1), currentJob.getJobCostingAndPayment().getFinalCost(), "java.lang.Double", null);
                ++row;
                // comments
                dataRow = costSheet.createRow(row);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(0), "Comment:", "java.lang.String", null);
                BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(1), currentJob.getComment(), "java.lang.String", null);
                ++row;

            } catch (Exception ex) {
                System.out.println(ex);
            }

            // write and save file for later use
            wb.write(out);
            out.close();

            FileInputStream stream = new FileInputStream("jobCosting.xls" + getUser().getId());
            jobCostingFile = new DefaultStreamedContent(stream, "application/xls", "jobCosting.xls");

        } catch (IOException ex) {
            System.out.println(ex);
        }


        return jobCostingFile;
    }

    private void refetchCurrentJob(EntityManager em) {
        if (currentJobId != null) {
            currentJob = em.find(Job.class, currentJobId);
        } else {
            currentJob = createNewJob(em, getUser(), true);
        }
    }
    
    public Job createNewJob(EntityManager em,
            JobManagerUser user,
            Boolean autoGenerateJobNumber) {
        Job job = new Job();
        job.setClient(new Client("", false));
        job.setReportNumber("");
        job.setJobDescription("");

        job.setBusinessOffice(getDefaultBusinessOffice(em, "Head Office"));
//        job.setDepartment(getDefaultDepartment(em, "--"));
//        job.setAssignedTo(getDefaultEmployee(em, "--", "--"));
//        job.setSubContractedDepartment(getDefaultDepartment(em, "--")); //tk remove comment
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
        job.getJobStatusAndTracking().setDateSubmitted(new Date());
        job.getJobStatusAndTracking().setWorkProgress("--");
        // job costing and payment
        // tk init costing...to be done with a template later
        job.setJobCostingAndPayment(createJobCostingAndPayment());
        // this is done here because job number is dependent on business office, department/subcontracted department
        job.setNumberOfSamples(0L);
        if (job.getAutoGenerateJobNumber()) {
            job.setJobNumber(getJobNumber(job));
        }

        return job;
    }

    public StreamedContent getServiceContractStreamContent() {
        EntityManager em = null;

        try {

            em = getEntityManager1();

            URL url = this.getClass().getResource("ServiceContractTemplate.xls");

            FileInputStream stream = createServiceContractExcelFileInputStream(em, getUser(), currentJob.getId(), url);

            DefaultStreamedContent dsc = new DefaultStreamedContent(stream, "application/xls", "servicecontract.xls");

            closeEntityManager(em);

            return dsc;

        } catch (Exception e) {
            closeEntityManager(em);
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

            // sync before returning
            jobReport = em.find(Report.class, jobReport.getId());

            // get report file stream based on report template
            // use the report template extension
            String[] fileNameParts = jobReport.getReportFileTemplate().split("\\.");
            if (fileNameParts.length > 1) {
                String ext = fileNameParts[fileNameParts.length - 1];

                if (ext.equals("jasper")) {
                } else {
                    if (jobReport.getName().equals("Monthly report")) {
                        jobReportFile = getMonthlyReport(em);
                    } else {
                        jobReportFile = getExcelReportStreamContent();
                    }
                }
            }

            setLongProcessProgress(100);
            closeEntityManager(em);
        } catch (Exception e) {
            closeEntityManager(em);
            System.out.println(e);
            setLongProcessProgress(100);
        }

        return jobReportFile;
    }

    public StreamedContent getMonthlyReport(EntityManager em) {

        try {
            DatePeriod datePeriods[] = BusinessEntityUtils.getMonthlyReportDatePeriods(currentDatePeriod);

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
            //reportSheet.setForceFormulaRecalculation(true);

            // create temp file for output
            FileOutputStream out = new FileOutputStream(jobReport.getReportFile() + getUser().getId());

            // if (!jobReport.getName().equals("Monthly report")) {
            // Get/Create sheet and insert job data
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
            //headerFont.setFontName("Courier New");
            headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            if (!jobReport.getReportColumns().isEmpty()) {
                HSSFRow headerRow = jobSheet.createRow(startingRow++); //org. 0
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
        if (!jobReport.getName().equals("Monthly report")) {
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
        //currentDatePeriod = new DatePeriod("This month", "month", null, null, false, false);
        updateJobReport();
    }

    public void updateServiceContract() {
    }

    public Boolean getCurrentJobIsValid() {
        if (getCurrentJob().getId() != null) {
            return true;
        }

        return false;
    }

    public void updateJobReport() {

        EntityManager em = getEntityManager1();

        if (jobReport.getId() != null) {
            jobReport = Report.findReportById(em, jobReport.getId());

            // set search text if require
            if (jobReport.getName().equals("Jobs for my department")) {
                reportSearchText = getUser().getEmployee().getDepartment().getName();
                reportingDepartment = getUser().getEmployee().getDepartment();
                currentPeriodJobReportSearchResultList = doJobReportSearch(currentDatePeriod.getStartDate(), currentDatePeriod.getEndDate());
            } else if (jobReport.getName().equals("Jobs in period")) {
                reportingDepartment = null;
                reportSearchText = "";
                currentPeriodJobReportSearchResultList = doJobReportSearch(currentDatePeriod.getStartDate(), currentDatePeriod.getEndDate());
            } else if (jobReport.getName().equals("Monthly report")) {
                if (reportingDepartment == null) {
                    reportingDepartment = getUser().getEmployee().getDepartment();
                    reportSearchText = reportingDepartment.getName();
                } else {
                    reportSearchText = reportingDepartment.getName();
                }
                currentPeriodJobReportSearchResultList = doJobReportSearch(currentDatePeriod.getStartDate(), currentDatePeriod.getEndDate());
                previousPeriodJobReportSearchResultList = doJobReportSearch(previousDatePeriod.getStartDate(), previousDatePeriod.getEndDate());
            } else {
                currentPeriodJobReportSearchResultList = new ArrayList<Job>();
            }
        }

        closeEntityManager(em);
    }

    public List<Report> getJobReports() {
        EntityManager em = getEntityManager1();

        List<Report> reports = Report.findAllReports(em);

        closeEntityManager(em);

        return reports;
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
        closeEntityManager(em);
    }

    public Boolean getDynamicTabView() {
        return dynamicTabView;
    }

    public void setDynamicTabView(Boolean dynamicTabView) {
        this.dynamicTabView = dynamicTabView;
    }

    public void handleKeepAlive() {
        getUser().setPollTime(new Date());
        System.out.println("Handling keep alive session: doing polling for JMTS..." + getUser().getPollTime());
        saveUser();
    }

    public void saveUser() {
        EntityManager em = getEntityManager1();

        if (getUser().getId() != null) {
            em.getTransaction().begin();
            BusinessEntityUtils.saveBusinessEntity(em, getUser());
            em.getTransaction().commit();
        }

        closeEntityManager(em);
    }

    public Boolean getDisableDepartmentEntry() {
        // allow department entry only if business office is null
        if (currentJob != null) {
            if (currentJob.getBusinessOffice() != null) {
                if (currentJob.getBusinessOffice().getCode() == null) {
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

    // Job Status and Tracking
    // job completed by
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
    // samples collected by

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

    /*
     * Get the department to which the current job is assigned
     */
    public String getAssignedDepartment() {
        if ((currentJob.getDepartment().getSubGroupCode() != null)
                && (currentJob.getSubContractedDepartment().getSubGroupCode() == null)) {
            return currentJob.getDepartment().getName();
        } else if (currentJob.getSubContractedDepartment().getSubGroupCode() != null) {
            return currentJob.getSubContractedDepartment().getName();
        } else {
            return "{no assigned department}";
        }
    }

    public CashPayment getSelectedCashPayment() {
        return selectedCashPayment;
    }

    public void setSelectedCashPayment(CashPayment selectedCashPayment) {
        this.selectedCashPayment = selectedCashPayment;
    }

    public void closeEntityManager(EntityManager em) {
        if (em != null) {
            if (em.isOpen()) {
                em.close();
            }
        }
    }

    public EntityManager getEntityManager1() {
        return getEMF1().createEntityManager();
    }

    public EntityManager getEntityManager2() {
        return getEMF2().createEntityManager();
    }

    public void updateJobCategory() {
        EntityManager em = null;

        try {
            em = getEntityManager1();
            JobCategory jobCategory = JobCategory.findJobCategoryById(em, categoryId);
            if (jobCategory != null) {
                currentJob.setJobCategory(jobCategory);
                setDirty(true);
            }

            closeEntityManager(em);

        } catch (Exception e) {
            closeEntityManager(em);
            System.out.println(e);
        }
    }

    public void updateJobSubCategory() {
        EntityManager em = null;

        try {
            em = getEntityManager1();
            JobSubCategory jobSubCategory = JobSubCategory.findJobSubCategoryById(em, subCategoryId);
            if (jobSubCategory != null) {
                currentJob.setJobSubCategory(jobSubCategory);
                setDirty(true);
            }

            closeEntityManager(em);
        } catch (Exception e) {
            closeEntityManager(em);
            System.out.println(e);
        }
    }

    public void updateJob() {
        setDirty(true);
    }

    public void updatePreferences() {
        // Enable job management unit if certain units have been enabled. 
//        if (getUser().getServiceRequestUnit()
//                || getUser().getComplianceUnit()
//                || getUser().getLegalMetrologyUnit()
//                || getUser().getFoodsUnit()) {
//            getUser().setJobManagementAndTrackingUnit(true);
//        }
        setDirty(true);
    }

    public void updateDocumentsCollectedDate() {
        setDateDocumentCollected(null);
        setDirty(true);
    }

    public void updateJobCompletionDate() {
        setJobCompletionDate(null);
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
        closeEntityManager(em);
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
        } else {
            currentJob.setJobSequenceNumber(null);
        }
        setDirty(true);
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
        if (currentJob.getJobStatusAndTracking().getWorkProgress().equals("Not started")) {
            currentJob.getJobStatusAndTracking().setWorkProgress("Started");
        }
        setDirty(true);
    }

    public void updateWorkProgress() {
        RequestContext context = RequestContext.getCurrentInstance();

        if (currentJob.getJobStatusAndTracking().getWorkProgress().equals("Not started")) {
            // overall completion status
            if (currentJob.getJobStatusAndTracking().getCompleted()
                    || currentJob.getJobStatusAndTracking().getSamplesCollected()
                    || currentJob.getJobStatusAndTracking().getDocumentCollected()) {
                // reset check boxes to false
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
                context.addCallbackParam("jobNotStarted", true);
            } else if (currentJob.getJobStatusAndTracking().getWorkProgress().equals("Withdrawn by client")) {
                context.addCallbackParam("jobNotStarted", true);
            } else {
                context.addCallbackParam("jobNotStarted", false);
            }
        }

        setDirty(true);
    }

    public void resetCurrentJob() {
        EntityManager em = getEntityManager1();

        createJob(em, false);

        currentJob.setBusinessOffice(new BusinessOffice("-- updating --"));
        currentJob.setJobNumber("-- updating --");
        currentJob.setClient(new Client("-- updating --"));
        currentJob.setDepartment(new Department("-- updating --"));
        currentJob.setSubContractedDepartment(new Department("-- updating --"));
        currentJob.setAssignedTo(new Employee());

        closeEntityManager(em);
    }

    public void createJob(EntityManager em, Boolean isSubcontract) {

        RequestContext context = RequestContext.getCurrentInstance();
        Boolean jobCreated;

        try {
            if (isSubcontract) {
                if (currentJob.getId() == null) {
                    context.addCallbackParam("jobNotSaved", true);
                    return;
                }
                currentJob = copyJob(em, currentJob, getUser(), true, true);
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
        // handle user privilege and return if the user does not have
        // the privilege to do what they wish
        RequestContext context = RequestContext.getCurrentInstance();
        EntityManager em = getEntityManager1();

        if (!checkPrivilege(em, context)) {
            // to be implemented
        } else {
            createJob(em, false);
            context.update("jobDialogForm");
            context.execute("jobDialog.show();");
        }

        closeEntityManager(em);
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
        currentJob.setClient(serviceRequest.getClient());
        currentJob.setDepartment(serviceRequest.getDepartment());
        currentJob.setAssignedTo(serviceRequest.getAssignedTo());
        if (currentJob.getAutoGenerateJobNumber()) {
            currentJob.setJobNumber(getJobNumber(currentJob));
        }
        // set job dirty to ensure it is saved if attempt is made to close it
        //  before saving
        setDirty(true);
        closeEntityManager(em);
    }

    public void copyJob(ActionEvent actionEvent) {
        EntityManager em = getEntityManager1();
        createJob(em, true);
        closeEntityManager(em);
    }

    public void createNewJobWithoutSavingCurrent(ActionEvent action) {
        EntityManager em = getEntityManager1();
        setDirty(false);
        createJob(em, false);
        closeEntityManager(em);
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
        return JMTSApp.getSearchResultsTableHeader(currentSearchParameters, getJobSearchResultList());
    }

    public void closeJobDialog1() {
        // Redo search to reloasd stored jobs including

        setIsJobToBeCopied(false);

        // prompt to save modified job before attempting to create new job
        if (getDirty()) {

            RequestContext context = RequestContext.getCurrentInstance();

            setDirty(false);

            context.addCallbackParam("jobDirty", true); // tk remove
            // ask to save
            context.update("jobSaveConfirmDialogForm");
            context.execute("jobDialog.hide();jobSaveConfirm.show();");

            return;
            //doJobSearch(sm.getCurrentSearchParameters());
        }

        // the currently edited job.        

        resetCurrentJob();
    }

    public void cancelJobEdit(ActionEvent actionEvent) {
//        SearchManager sm = JMTSApp.findBean("searchManager");

        setDirty(false);
        doJobSearch(searchManagement.getCurrentSearchParameters());
    }

    public void closeJobDialog2() {
        // Redo search to reloasd stored jobs including
        // the currently edited job.
        closeJobDialog1();
    }

    public void closePreferencesDialog2(CloseEvent closeEvent) {
        // Redo search to reloasd stored jobs including
        // the currently edited job.
        closePreferencesDialog1(null);
    }

    public void closePreferencesDialog1(ActionEvent actionEvent) {
        RequestContext context = RequestContext.getCurrentInstance();

        if (getDirty()) {
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

        closeEntityManager(em);
    }

    public void validateJobCostingAndSaveJob(ActionEvent actionEvent) {

        RequestContext context = RequestContext.getCurrentInstance();
        EntityManager em = getEntityManager1();

        try {
            // check for valid job
            if (currentJob != null) {
                if (currentJob.getId() == null) {
                    context.addCallbackParam("invalidJob", true);
                    return;
                }
            } else {
                context.addCallbackParam("invalidJob", true);
                return;
            }
            // check for job report # and description
            if ((currentJob.getReportNumber() == null) || (currentJob.getReportNumber().trim().equals(""))) {
                context.addCallbackParam("invalidReportNumber", true);
                return;
            }
            if ((currentJob.getJobDescription() == null) || (currentJob.getJobDescription().trim().equals(""))) {
                context.addCallbackParam("invalidJobDescription", true);
                return;
            }

            // save the job the which will result in automatics saving of the
            // job costing through the cascading mechanism
            //saveCurrentJob(em);
            currentJob.getJobCostingAndPayment().setFinalCost(currentJob.getJobCostingAndPayment().calculateFinalCost());
            if (getUser().getEmployee() != null) {
                currentJob.getJobCostingAndPayment().setFinalCostDoneBy(getUser().getEmployee().getName());
            }

            // all seems to be well so save job here
            saveCurrentJob(em);

            // refresh to make sure job costings ids are not null and
            // avoid resaving them
            currentJob.setJobCostingAndPayment(em.find(JobCostingAndPayment.class,
                    currentJob.getJobCostingAndPayment().getId()));

            closeEntityManager(em);
        } catch (Exception e) {
            closeEntityManager(em);
            System.out.println(e);
        }

        context.addCallbackParam("jobCostingAndPaymentSaved", true);
    }

    public void saveCurrentJob() {
        EntityManager em = getEntityManager1();
        saveCurrentJob(em);
        closeEntityManager(em);
    }

    public void saveCurrentAndCreateNewJob(ActionEvent actionEvent) {
        EntityManager em1 = getEntityManager1();
        EntityManager em2 = getEntityManager1();


        em1.getTransaction().begin();
        saveCurrentJob(em1);
        em1.getTransaction().commit();

        setDirty(false);
        createJob(em2, false);

        closeEntityManager(em1);
        closeEntityManager(em2);
    }

    public void saveCurrentAndLoadOtherJob(ActionEvent actionEvent) {
        EntityManager em = getEntityManager1();

        saveCurrentJob(em);

        setDirty(false);

        loadJob();

        closeEntityManager(em);
    }

    public Boolean checkPrivilege(EntityManager em, RequestContext context) {
        // prompt to save modified job before attempting to create new job
//        if (getUser() != null) {
//            user = jm.getJobManagerUserById(em, user.getId());
//            em.refresh(user);
//        } else {
//            return true;
//        }

        if (!getUser().getCanEnterJob() && currentJob.getId() == null) {
            displayUserPrivilegeDialog(context,
                    "Job Entry Privilege",
                    "You do not have job entry privilege.");
            return false;
        }

        return true;
    }

    public void saveCurrentJob(EntityManager em) {
        RequestContext context = RequestContext.getCurrentInstance();
        JobSequenceNumber nextJobSequenceNumber = null;

        try {

            if (!BusinessEntityUtils.validateName(currentJob.getBusinessOffice().getName())) {
                getMessageManagement().setInvalidFormFieldMessage("This job cannot be saved because a valid business office was not entered.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            }

            BusinessOffice businessOffice = BusinessOffice.findBusinessOfficeByName(em, currentJob.getBusinessOffice().getName());
            if (businessOffice != null) {
                currentJob.setBusinessOffice(businessOffice);
            } else {
                //invalidFormFieldMessage = "This job cannot be saved because a valid business office was not entered.";
                getMessageManagement().setInvalidFormFieldMessage("This job cannot be saved because a valid business office was not entered.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            }

            // job number - check if job nunmber is already associated and a job
            // and validate the job number if required ie if not auto generated 
            if (!currentJob.getAutoGenerateJobNumber()) {
                Job existingJob = Job.findJobByJobNumber(em, currentJob.getJobNumber());
                if (existingJob != null) {
                    System.out.println("existing job: " + existingJob.getJobNumber());
                    if (existingJob.getId() != currentJob.getId()) {
                        invalidFormFieldMessage = "This job cannot be saved because the job number is not unique.";
                        context.update("invalidFieldDialogForm");
                        context.execute("invalidFieldDialog.show();");
                        return;
                    }
                }
            }

            // get  job number if auto is on
            if (currentJob.getAutoGenerateJobNumber()) {
                if (!validateJobNumber(currentJob.getJobNumber(), currentJob.getAutoGenerateJobNumber())) {
                    getMessageManagement().setInvalidFormFieldMessage("This job cannot be saved because a valid job number was not entered.");
                    context.update("invalidFieldDialogForm");
                    context.execute("invalidFieldDialog.show();");
                    return;
                }
            }

            // Validate and save client
            if (!BusinessEntityUtils.validateName(currentJob.getClient().getName())) {
                getMessageManagement().setInvalidFormFieldMessage("This job cannot be saved. Please enter a valid client name.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            }

            // department
            Department department = Department.findDepartmentByName(em, currentJob.getDepartment().getName());
            if (department == null) {
                getMessageManagement().setInvalidFormFieldMessage("This job cannot be saved because a valid department was not entered.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            } else {
                currentJob.setDepartment(department);
            }
            // subcontracted department
            if (currentJob.getSubContractedDepartment().getName() != null) {
                Department subContractedDepartment = Department.findDepartmentByName(em, currentJob.getSubContractedDepartment().getName());
                if (subContractedDepartment == null) {
                    currentJob.setSubContractedDepartment(getDefaultDepartment(em, "--"));
                } else {
                    currentJob.setSubContractedDepartment(subContractedDepartment);
                }
            }

            // make sure department is not same as subcontracted department
            if ((currentJob.getSubContractedDepartment().getName() != null) && (currentJob.getDepartment().getName() != null)) {
                if (currentJob.getDepartment().getName().equals(currentJob.getSubContractedDepartment().getName())) {
                    currentJob.setSubContractedDepartment(getDefaultDepartment(em, "--"));
                }
            }

            // assignee
            Employee assignee = getEmployeeByName(em, currentJob.getAssignedTo().getName());
            if (assignee != null) {
                currentJob.setAssignedTo(assignee);
            } else {
                getMessageManagement().setInvalidFormFieldMessage("This job cannot be saved because a valid assignee/department representative was not entered.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            }

            // classification objects
            Classification classification = Classification.findClassificationById(em, currentJob.getClassification().getId());
            if (classification == null) {
                getMessageManagement().setInvalidFormFieldMessage("Please select a classification.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            } else {
                currentJob.setClassification(classification);
            }

            Sector sector = Sector.findSectorById(em, currentJob.getSector().getId());
            if (sector == null) {
                getMessageManagement().setInvalidFormFieldMessage("Please select a sector.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            } else {
                currentJob.setSector(sector);
            }

            JobCategory category = JobCategory.findJobCategoryById(em, currentJob.getJobCategory().getId());
            if (category == null) {
                getMessageManagement().setInvalidFormFieldMessage("Please select a job category.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            } else {
                currentJob.setJobCategory(category);
            }

            JobSubCategory subCategory = JobSubCategory.findJobSubCategoryById(em, currentJob.getJobSubCategory().getId());
            if (subCategory == null) {
                getMessageManagement().setInvalidFormFieldMessage("Please select a job subcategory.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            } else {
                currentJob.setJobSubCategory(subCategory);
            }

            // set date enetered
            if (currentJob.getJobStatusAndTracking().getDateAndTimeEntered() == null) {
                currentJob.getJobStatusAndTracking().setDateAndTimeEntered(new Date());
            }
            // Get employee for later use
            Employee employee = Employee.findEmployeeById(em, getUser().getEmployee().getId());

            // This means this this is a new job so set user and person who entered the job
            if (currentJob.getJobStatusAndTracking().getEnteredBy().getId() == null) {
                currentJob.getJobStatusAndTracking().setEnteredBy(employee);
            }
            // Update re the person who last edited/entered the job etc.
            if (dirty) {
                currentJob.getJobStatusAndTracking().setDateStatusEdited(new Date());
                currentJob.getJobStatusAndTracking().setEditedBy(employee);
            }

            // update work progress
            if (currentJob.getJobStatusAndTracking().getWorkProgress() == null) {
                currentJob.getJobStatusAndTracking().setWorkProgress("--");
            }

            // save job and check for save error
            // modify job number with sequence number if required
            if (currentJob.getAutoGenerateJobNumber()) {
                if (currentJob.getJobSequenceNumber() == null) {
                    //currentJob.setJobSequenceNumber(jm.getNextJobSequentialNumber(em, currentJob.getYearReceived()));
                    nextJobSequenceNumber = JobSequenceNumber.findNextJobSequenceNumber(em, currentJob.getYearReceived());
                    currentJob.setJobSequenceNumber(nextJobSequenceNumber.getSequentialNumber());
                    currentJob.setJobNumber(getJobNumber(currentJob));
                } else {
                    currentJob.setJobNumber(getJobNumber(currentJob));
                }
            }

            // save job to database and check for errors
            em.getTransaction().begin();

            if (currentJob.getJobSamples().size() > 0) {
                for (JobSample jobSample : currentJob.getJobSamples()) {

                    if (jobSample.getSampledBy().getId() != null) {
                        Employee e = getEmployeeByName(em, jobSample.getSampledBy().getName());
                        if (e != null) {
                            jobSample.setSampledBy(e);
                        }
                    }

                    /// save new objects in list first 
                    if (jobSample.getId() == null) {
                        BusinessEntityUtils.saveBusinessEntity(em, jobSample);
                    }
                }
            } else { // tk ensure there is at least one sample present
                createNewJobSample(null);
                selectedJobSample.setDescription("--");
                updateJobSample();
            }

            // ensure new samples are created
            if (isJobToBeCopied) {
                List<JobSample> currentSamples = currentJob.getJobSamples();
                for (JobSample jobSample : currentSamples) {
                    jobSample.setId(null);
                }
            }

            Long id = BusinessEntityUtils.saveBusinessEntity(em, currentJob);
            if (id == null) {
                // set seq. number to null to ensure that the next sequence #
                // is retrieved with the next job save attempt.
                currentJob.setJobSequenceNumber(null);
                currentJob.setJobNumber(getJobNumber(currentJob));
                context.execute("undefinedErrorDialog.show();");
                sendErrorEmail("An error occured while saving job (Null ID)" + currentJob.getJobNumber(),
                        "Job save error occured");

                return;

            } else if (id == 0L) {
                // set seq. number to null to ensure that the next sequence #
                // is retrieved with the next job save attempt.
                currentJob.setJobSequenceNumber(null);
                currentJob.setJobNumber(getJobNumber(currentJob));
                context.execute("undefinedErrorDialog.show();");
                sendErrorEmail("An error occured while saving job (0L ID)" + currentJob.getJobNumber(),
                        "Job save error occured");

                return;
            } else {
                currentJobId = id;
                // save job sequence number only if job save was successful
                if (nextJobSequenceNumber != null) {
                    BusinessEntityUtils.saveBusinessEntity(em, nextJobSequenceNumber);
                }
            }
            em.getTransaction().commit();
            setIsJobToBeCopied(false);
            setDirty(false);

        } catch (Exception e) {
            // set seq. number to null to ensure that the next sequence #
            // is retrieved with the next job save attempt.
            currentJob.setJobSequenceNumber(null);
            currentJob.setJobNumber(getJobNumber(currentJob));
            context.execute("undefinedErrorDialog.show();");
            System.out.println(e);
            // send error message to developer's email
            sendErrorEmail("An exception occurred while saving a job!",
                    "Job number: " + currentJob.getJobNumber()
                    + "\nJMTS User: " + getUser().getUsername()
                    + "\nDate/time: " + new Date()
                    + "\nException detail: " + e);
        } finally {
            //closeEntityManager(em); //tk
        }
    }

    // tk one in JMTSApp to be impl and used
    public void sendErrorEmail(String subject, String message) {
        try {
            // send error message to developer's email
            postJobManagerMail(null, null, subject, message);
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

    public void editJobSample(ActionEvent event) {
        jobSampleDialogTabViewActiveIndex = 0;

        addJobSample = false;
    }

    public void editCostComponent(ActionEvent event) {
    }

    public void createNewJobSample(ActionEvent event) {
        // init sample
        addJobSample = true;
        selectedJobSample = new JobSample();
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

        selectedJobSample.setDateSampled(new Date());
        jobSampleDialogTabViewActiveIndex = 0;
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

    public void checkNumberOfJobSamplesAndGroups() {
        // setup context for client response
        RequestContext context = RequestContext.getCurrentInstance();
        // check for max sample
        if (getCurrentNumberOfJobSamples() == 700) { // tk make this an option somewhere
            context.addCallbackParam("maxJobSamplesReached", true);
        }
        // check for max sample groups
        if (currentJob.getJobSamples().size() == 6) {
            context.addCallbackParam("maxJobSampleGroupsReached", true);
        }
    }

    private void setNumberOfSamples() {
        currentJob.setNumberOfSamples(0L); // reset
        for (int i = 0; i < currentJob.getJobSamples().size(); i++) { // find total
            if (currentJob.getJobSamples().get(i).getSampleQuantity() == null) {
                currentJob.getJobSamples().get(i).setSampleQuantity(1L);
            }
            currentJob.setNumberOfSamples(currentJob.getNumberOfSamples()
                    + currentJob.getJobSamples().get(i).getSampleQuantity());
        }
    }

    private void updateSampleReferences() {
        int prevSampleIndex = -1;
        Long prevSampleQuantity;
        Long prevSampleReferenceIndex;
        Long totalNumSamples = 0L;
        for (int i = 0; i < currentJob.getJobSamples().size(); i++) {
            if (prevSampleIndex == -1) { // this is the first sample
                currentJob.getJobSamples().get(i).setReferenceIndex(0L);
                if (currentJob.getJobSamples().get(i).getSampleQuantity() == 1) {
                    currentJob.getJobSamples().get(i).
                            setReference(BusinessEntityUtils.getAlphaCode(0));
                } else {
                    currentJob.getJobSamples().get(i).
                            setReference(BusinessEntityUtils.getAlphaCode(i) + "-"
                            + BusinessEntityUtils.getAlphaCode(currentJob.getJobSamples().get(i).getSampleQuantity() - 1));
                }
                totalNumSamples = totalNumSamples + currentJob.getJobSamples().get(i).getSampleQuantity();
            } else {
                prevSampleQuantity = currentJob.getJobSamples().get(prevSampleIndex).getSampleQuantity();
                prevSampleReferenceIndex = currentJob.getJobSamples().get(prevSampleIndex).getReferenceIndex();
                currentJob.getJobSamples().get(i).setReferenceIndex(totalNumSamples);
                if (currentJob.getJobSamples().get(i).getSampleQuantity() == 1) {
                    currentJob.getJobSamples().get(i).setReference(BusinessEntityUtils.getAlphaCode(prevSampleReferenceIndex + prevSampleQuantity));
                } else {
                    currentJob.getJobSamples().get(i).
                            setReference(BusinessEntityUtils.getAlphaCode(currentJob.getJobSamples().get(i).getReferenceIndex()) + "-"
                            + BusinessEntityUtils.getAlphaCode(currentJob.getJobSamples().get(i).getReferenceIndex()
                            + currentJob.getJobSamples().get(i).getSampleQuantity() - 1));
                }
                totalNumSamples = totalNumSamples + currentJob.getJobSamples().get(i).getSampleQuantity();
            }
            prevSampleIndex = i;
        }

    }

    public void updateCostingComponent() {
//        currentJob.getJobCostingAndPayment().setFinalCost(currentJob.getJobCostingAndPayment().calculateFinalCost());
        updateFinalCost();
    }

    public void updateFinalCost() {
        currentJob.getJobCostingAndPayment().setFinalCost(currentJob.getJobCostingAndPayment().calculateFinalCost());
        setDirty(true);
    }

    public void updateJobCosting() {
        if (currentJob.getJobCostingAndPayment().getAllCostComponents().isEmpty()) {
            createJobCostings(currentJob.getJobCostingAndPayment());
        }
    }

    public void updateJobSample() {
        RequestContext context = RequestContext.getCurrentInstance();
        EntityManager em = getEntityManager1();

        // get sampled by
        selectedJobSample.setSampledBy(getEmployeeByName(em, selectedJobSample.getSampledBy().getName()));

        // validate form entries
        if (selectedJobSample.getDateSampled() == null) {
            context.execute("jobSampleDialog.show();");
            context.execute("jobSampleRequiredFieldMessageDialog.show();");
            return;
        } else if (selectedJobSample.getSampleQuantity() == null) {
            context.execute("jobSampleDialog.show();");
            context.execute("jobSampleRequiredFieldMessageDialog.show();");
            return;
        } else if ((selectedJobSample.getDescription() == null)
                || (selectedJobSample.getDescription().trim().isEmpty())) {
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
        currentJob.setJobNumber(getCurrentJobNumber());
        jobSampleDialogTabViewActiveIndex = 0;

        //context.addCallbackParam("sampleUpdated", true);
        setDirty(true);

        closeEntityManager(em);
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
        if (getDirty()) {
            context.addCallbackParam("jobDirty", true);
            return;
        }

        if ((selectedJobId != 0L) && (selectedJobId != null)) {
            currentJob = Job.findJobById(em, selectedJobId);
            // create job costings if it does not exist
            if (currentJob.getJobCostingAndPayment().getJobCostings() == null) {
                currentJob.getJobCostingAndPayment().setJobCostings(createJobCostingAndPayment().getJobCostings());
            }
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

        closeEntityManager(em);
    }

    public String getCurrentJobNumber() {
        return getJobNumber(currentJob);
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
        if (currentJob != null) {
            if (currentJob.getJobCostingAndPayment().getCostingDate() == null) {
                return new Date();
            } else {
                return currentJob.getJobCostingAndPayment().getCostingDate();
            }
        } else {
            return new Date();
        }
    }

    public void setJobCostingDate(Date date) {
        currentJob.getJobCostingAndPayment().setCostingDate(date);
    }

    public void handleJobCostingDateSelect(SelectEvent event) {
        Date selectedDate = (Date)event.getObject();

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

    public void handleDateSubmittedSelect(SelectEvent event) {
        Date selectedDate = (Date)event.getObject();
        Calendar c = Calendar.getInstance();

        currentJob.getJobStatusAndTracking().setDateSubmitted(selectedDate);
        c.setTime(currentJob.getJobStatusAndTracking().getDateSubmitted());
        currentJob.setYearReceived(c.get(Calendar.YEAR));
        currentJob.setJobNumber(getJobNumber(currentJob));

        setDirty(true);
    }

    public void handleCurrentPeriodStartDateSelect(SelectEvent event) {
        currentDatePeriod.setStartDate((Date)event.getObject());
        updateJobReport();
    }

    public void handleCurrentPeriodEndDateSelect(SelectEvent event) {
        currentDatePeriod.setEndDate((Date)event.getObject());
        updateJobReport();
    }

    public void handlePreviousPeriodStartDateSelect(SelectEvent event) {
        previousDatePeriod.setStartDate((Date)event.getObject());
        updateJobReport();
    }

    public void handlePreviousPeriodEndDateSelect(SelectEvent event) {
        previousDatePeriod.setEndDate((Date)event.getObject());
        updateJobReport();
    }

    public void updateDateJobCompleted(SelectEvent event) {
        Date selectedDate =(Date)event.getObject();

        currentJob.getJobStatusAndTracking().setDateOfCompletion(selectedDate);

        setDirty(Boolean.TRUE);
    }

    public void updateDateExpectedCompletionDate(SelectEvent event) {
        Date selectedDate = (Date)event.getObject();

        currentJob.getJobStatusAndTracking().setExpectedDateOfCompletion(selectedDate);

        setDirty(Boolean.TRUE);
    }

    public void updateDateSamplesCollected(SelectEvent event) {
        Date selectedDate = (Date)event.getObject();

        currentJob.getJobStatusAndTracking().setDateSamplesCollected(selectedDate);

        setDirty(Boolean.TRUE);
    }

    public void updateDateDocsCollected(SelectEvent event) {
        Date selectedDate = (Date)event.getObject();

        currentJob.getJobStatusAndTracking().setDateDocumentCollected(selectedDate);

        setDirty(Boolean.TRUE);
    }

    public List<SelectItem> getDatePeriods() {
        ArrayList<SelectItem> datePeriods = new ArrayList<SelectItem>();



        // add items
        if (jobReport.getName().equals("Monthly report")) {
            for (String name : DatePeriod.getDatePeriodNames()) {
                // get only month date periods
                //  if (name.toUpperCase().contains("MONTH")) {
                datePeriods.add(new SelectItem(name, name));
                //  }
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

            closeEntityManager(em);

            return results;

        } catch (Exception e) {
            closeEntityManager(em);
            System.out.println(e);

            return new ArrayList<JobCostingAndPayment>();
        }
    }

    // tk to be put in JMTSApp
    public List<BusinessOffice> completeBusinessOffice(String query) {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            List<BusinessOffice> offices = BusinessOffice.findBusinessOfficesByName(em, query);
            closeEntityManager(em);

            return offices;
        } catch (Exception e) {
            closeEntityManager(em);

            System.out.println(e);
            return new ArrayList<BusinessOffice>();
        }
    }

    public List<Department> completeDepartment(String query) { // tj to be removed and put in JMTSApp
        EntityManager em = null;

        try {
            em = getEntityManager1();

            List<Department> departments = Department.findDepartmentsByName(em, query);

            closeEntityManager(em);

            return departments;

        } catch (Exception e) {
            closeEntityManager(em);
            System.out.println(e);

            return new ArrayList<Department>();
        }
    }

    // tk use one in JMTSApp?
    public List<Employee> completeEmployee(String query) {
        EntityManager em = null;

        try {

            em = getEntityManager1();
            List<Employee> employees = Employee.findActiveEmployeesByName(em, query);
            closeEntityManager(em);

            if (employees != null) {
                return employees;
            } else {
                return new ArrayList<Employee>();
            }
        } catch (Exception e) {
            closeEntityManager(em);
            System.out.println(e);
            return new ArrayList<Employee>();
        }
    }

    public List<String> completeSealNumber(String query) { // tk put in job management lib
        EntityManager em = null;

        try {
            em = getEntityManager1();
            List<Seal> seals = Seal.findSealsByNumber(em, query);
            List<String> suggestions = new ArrayList<String>();

            // find matching clients
            for (Seal seal : seals) {
                suggestions.add(seal.getNumber());
            }

            closeEntityManager(em);

            return suggestions;
        } catch (Exception e) {
            closeEntityManager(em);
            System.out.println(e);

            return new ArrayList<String>();
        }
    }

    public List<String> completePetrolCompanyName(String query) { // tk put in job management lib
        EntityManager em = null;

        try {
            em = getEntityManager1();
            List<PetrolCompany> companies = PetrolCompany.findPetrolCompaniesByName(em, query);
            List<String> suggestions = new ArrayList<String>();

            // find matching clients
            for (PetrolCompany company : companies) {
                suggestions.add(company.getName());
            }

            closeEntityManager(em);

            return suggestions;
        } catch (Exception e) {
            closeEntityManager(em);

            System.out.println(e);

            return new ArrayList<String>();
        }
    }

    public List<String> completeAccPacClientName(String query) {
        EntityManager em2 = null;

        try {

            em2 = getEntityManager2();
            List<AccPacCustomer> clients = AccPacCustomer.findAccPacCustomersByName(em2, query);
            List<String> suggestions = new ArrayList<String>();

            for (AccPacCustomer client : clients) {
                suggestions.add(client.getCustomerName());
            }

            closeEntityManager(em2);

            return suggestions;
        } catch (Exception e) {
            closeEntityManager(em2);

            System.out.println(e);

            return new ArrayList<String>();
        }
    }

    public List<AccPacCustomer> completeAccPacClient(String query) {
        EntityManager em2 = null;

        try {
            em2 = getEntityManager2();

//            closeEntityManager(em2);

            return AccPacCustomer.findAccPacCustomersByName(em2, query);
        } catch (Exception e) {
            closeEntityManager(em2);

            System.out.println(e);
            return new ArrayList<AccPacCustomer>();
        }
    }

    public void doJobSearch(SearchParameters currentSearchParameters) {
        this.currentSearchParameters = currentSearchParameters;
        EntityManager em = null;

        if (getUser().getId() != null) {
            em = getEntityManager1();
            jobSearchResultList = Job.findJobsByDateSearchField(em,
                    getUser(),
                    currentSearchParameters.getDateField(),
                    currentSearchParameters.getSearchType(),
                    currentSearchParameters.getSearchText(),
                    currentSearchParameters.getDatePeriod().getStartDate(),
                    currentSearchParameters.getDatePeriod().getEndDate(), false); // def. false

            if (jobSearchResultList == null) {
                jobSearchResultList = new ArrayList<Job>();
            }

        } else {
            jobSearchResultList = new ArrayList<Job>();
        }

        closeEntityManager(em);

        // Reset select selected job id to allow new selection
        selectedJobId = 0L;
    }

    /**
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<Job> doJobReportSearch(Date startDate, Date endDate) {
        EntityManager em = getEntityManager1();

        List<Job> jobReportSearchResultList = Job.findJobsByDateSearchField(em,
                getUser(),
                "dateSubmitted", jobReport.getName(), reportSearchText,
                startDate, endDate, false);

        if (jobReportSearchResultList == null) {
            jobReportSearchResultList = new ArrayList<Job>();
        }

        closeEntityManager(em);

        return jobReportSearchResultList;

    }

    /**
     *
     * @return
     */
    public List<Classification> getClassifications() {
        EntityManager em = getEntityManager1();

        List<Classification> classifications = Classification.findAllClassifications(em);

        closeEntityManager(em);

        return classifications;
    }

    public List<Sector> getSectors() {
        EntityManager em = getEntityManager1();

        List<Sector> sectors = Sector.findAllSectors(em);

        closeEntityManager(em);

        return sectors;
    }

    public List<JobCategory> getJobCategories() {
        EntityManager em = getEntityManager1();

        List<JobCategory> categories = JobCategory.findAllJobCategories(em);

        closeEntityManager(em);

        return categories;
    }

    public List<JobSubCategory> getJobSubCategories() {
        EntityManager em = getEntityManager1();

        List<JobSubCategory> subCategories = JobSubCategory.findAllJobSubCategories(em);

        closeEntityManager(em);

        return subCategories;
    }

    public List<CostCode> getAllCostCodes() {
        EntityManager em = getEntityManager1();

        List<CostCode> codes = CostCode.findAllCostCodes(em);

        closeEntityManager(em);

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
            jobSearchResultList = new ArrayList<Job>();
        }
        return jobSearchResultList;
    }

//    public int getSizeOfSearchResults() {
//        return getJobSearchResultList().size();
//    }
    public List<Job> getCurrentPeriodJobReportSearchResultList() {
        if (currentPeriodJobReportSearchResultList == null) {
            currentPeriodJobReportSearchResultList = new ArrayList<Job>();
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
        // tk restrict reporting until all reports have been designed
        if (jobReport.getName().equals("Monthly report") && (reportingDepartment != null)) {
            if (!reportingDepartment.getName().equals("Electrical/Electronic Engineering")) {
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

    public void changeCurrentDateSearchPeriod() {
        currentDatePeriod.initDatePeriod();
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

    public Boolean getDirty() {
        return dirty;
    }

    @Override
    public void setDirty(Boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public Boolean isDirty() {
        return dirty;
    }

//    public void setUser(JobManagerUser user) {
//        this.user = user;
//    }
//
//    public JobManagerUser getUser() {
//        if (user == null) {
//            return new JobManagerUser();
//        }
//        return user;
//    }
    public Boolean checkUserPrivilege(int priv) {

        if (getUser() != null) {
            switch (priv) {
                case 0: // CAN_ENTER_JOB
                    return getUser().getCanEnterJob();
                case 1: // CAN_DELETE_JOB
                    return getUser().getCanDeleteJob();
                case 2: // CAN_EDIT_JOB
                    return getUser().getCanEditJob();
            }
        }

        return false;
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
                if (getDirty()) {
                    dirtyStatus = " - MODIFIED";
                } else {
                    dirtyStatus = "";
                }

                closeEntityManager(em);
                // build and return system info string
                return "Current job: " + currentJob.getJobNumber() + clientName + dirtyStatus;
            } else {
                return "";
            }
        } catch (Exception e) {
            closeEntityManager(em);

            System.out.println(e);
        }

        return "";
    }

    public void updateBusinessOffice() {

        EntityManager em = null;

        try {
            em = getEntityManager1();
            BusinessOffice businessOffice = BusinessOffice.findBusinessOfficeByName(em, currentJob.getBusinessOffice().getName());
            if (businessOffice != null) {
                currentJob.setBusinessOffice(businessOffice);
                if ((businessOffice.getCode() != null)) { // not null or 0 means regional office
                    Department department = getDefaultDepartment(em, "--");
                    currentJob.setDepartment(department);
                } else {
                    currentJob.setDepartment(new Department());
                }
            }
            currentJob.setJobNumber(getCurrentJobNumber());
            setDirty(true);

            closeEntityManager(em);
        } catch (Exception e) {
            closeEntityManager(em);
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
            closeEntityManager(em);
        } catch (Exception e) {
            closeEntityManager(em);
            System.out.println(e);
        }
    }

    public void updateSector() {
        EntityManager em = null;

        try {
            em = getEntityManager1();
            Sector sector = Sector.findSectorById(em, sectorId);
            if (sector != null) {
                currentJob.setSector(sector);
                setDirty(true);
            }
            closeEntityManager(em);
        } catch (Exception e) {
            closeEntityManager(em);
            System.out.println(e);
        }
    }

    public void updateDepartment() {
        EntityManager em = null;

        try {
            em = getEntityManager1();
            if (currentJob.getDepartment().getName() != null) {
                Department department = Department.findDepartmentByName(em, currentJob.getDepartment().getName());
                if (department != null) {
                    currentJob.setDepartment(department);
                    currentJob.setJobNumber(getCurrentJobNumber());
                    setDirty(true);
                }
            }

            closeEntityManager(em);
        } catch (Exception e) {
            closeEntityManager(em);
            System.out.println(e);
        }
    }

    public void updateReportingDepartment() {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            if (getReportingDepartment().getName() != null) {
                Department department = Department.findDepartmentByName(em, getReportingDepartment().getName());
                if (department != null) {
                    reportingDepartment = department;
//                    reportSearchText = reportingDepartment.getName();
                    updateJobReport();
                }
            }

            closeEntityManager(em);
        } catch (Exception e) {
            closeEntityManager(em);
            System.out.println(e);
        }
    }

    public void updateSubContractedDepartment() {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            if (currentJob.getSubContractedDepartment().getName() != null) {
                Department subContractedDepartment = Department.findDepartmentByName(em, currentJob.getSubContractedDepartment().getName());
                currentJob.setSubContractedDepartment(subContractedDepartment);
                currentJob.setJobNumber(getCurrentJobNumber());
                setDirty(true);
            }

            closeEntityManager(em);
        } catch (Exception e) {
            closeEntityManager(em);
            currentJob.setSubContractedDepartment(null);
            currentJob.setJobNumber(getCurrentJobNumber());
            System.out.println(e);
        }
    }

    public void updateClient() {
        EntityManager em = null;

        try {
            em = getEntityManager1();
            if (currentJob != null) {
                if (currentJob.getClient() != null) {
                    Client client = Client.findClientByName(em, currentJob.getClient().getName());
                    if (client != null) {
                        currentJob.setClient(client);
                    }
                }
                accPacCustomer.setCustomerName(currentJob.getClient().getName());
                if (useAccPacCustomerList) {
                    updateCreditStatus(null);
                }
            }

            closeEntityManager(em);
        } catch (Exception e) {
            closeEntityManager(em);
            System.out.println(e);
        }
    }

    public void updateAccPacClient() {

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

    public void createNewClient() {
        clientManagement.setEntityManagerFactory(EMF1);
        clientManagement.createNewClient(null, new Client("", true));
        clientManagement.setSave(true);
    }

    public void updateJobEntryTabClient() {

        // Create copy of existing client
        EntityManager em = getEntityManager1();
        Client client = Client.findClientByName(em, currentJob.getClient().getName());
        if (client != null) {
            currentJob.getClient().doCopy(client);
            currentJob.getClient().setActive(false);
        }

        accPacCustomer.setCustomerName(currentJob.getClient().getName());
        if (useAccPacCustomerList) {
            updateCreditStatus(null);
        }

        setDirty(true);
        closeEntityManager(em);
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

    public Integer getNumberOfAccPacCustomerDocuments() {
        if (accPacCustomerDocuments != null) {
            return accPacCustomerDocuments.size();
        }

        return 0;
    }

    public Boolean getDocumentAvailable() {
        if (accPacCustomerDocuments != null) {
            if (accPacCustomerDocuments.isEmpty()) {
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
            accPacCustomerDocuments = AccPacDocument.findAccPacInvoicesDueByCustomerId(em, accPacCustomer.getId());
            for (AccPacDocument doc : accPacCustomerDocuments) {
                em.refresh(doc);
            }
        } else {
            createNewAccPacCustomer();
        }
    }

    public void createNewAccPacCustomer() {
        if (accPacCustomer != null) {
            accPacCustomer = new AccPacCustomer(accPacCustomer.getCustomerName());
        } else {
            accPacCustomer = new AccPacCustomer(null);
        }
        accPacCustomer.setId(null);
        accPacCustomerDocuments = new ArrayList<AccPacDocument>();
    }

    public String getAccPacCustomerID() {
        if (accPacCustomer.getId() == null) {
            return "{Not found}";
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
        currentJob.setAssignedTo(getEmployeeByName(em, currentJob.getAssignedTo().getName()));
        currentJob.getJobStatusAndTracking().setAlertDate(null);
        setDirty(true);

        closeEntityManager(em);
    }

    public void updateJobCostingAndPayment() {
        if (selectedJobCostingTemplate != null) {
            EntityManager em = getEntityManager1();
            JobCostingAndPayment jcp =
                    JobCostingAndPayment.findJobCostingAndPaymentByDepartmentAndName(em,
                    getUser().getEmployee().getDepartment().getName(),
                    selectedJobCostingTemplate);
            if (jcp != null) {
                currentJob.getJobCostingAndPayment().setJobCostings(copyJobCostings(jcp.getJobCostings()));
                currentJob.getJobCostingAndPayment().setFinalCost(currentJob.getJobCostingAndPayment().calculateFinalCost());
                setDirty(true);
            } else {
                System.out.println("Job costing not found");
            }

            selectedJobCostingTemplate = null;

            closeEntityManager(em);
        }
    }

    // tk remove this and use the one in JMTSApp?
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

    public void updateSampledBy() {
        EntityManager em = getEntityManager1();
        selectedJobSample.setSampledBy(getEmployeeByName(em, selectedJobSample.getSampledBy().getName()));
        closeEntityManager(em);
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

            closeEntityManager(em);
        } catch (Exception e) {
            closeEntityManager(em);
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
                // create new
                currentManufacturer = new Manufacturer();
                currentManufacturer.setName(manufacturer.getName());

            } else {
                manufacturer.setId(currentManufacturer.getId());
            }

            // do save
            BusinessEntityUtils.saveBusinessEntity(em, manufacturer);
        }
    }

    public JobManagerUser getUser() {
        return userManagement.getUser();
    }

    // Edit the client via the ClientManager
    public void editJobClient() {

        EntityManager em = getEntityManager1();
        // Try to retrieve the client from the database if it exists
        Client client = Client.findClientByName(em, currentJob.getClient().getName());
        if (client != null) {
            currentJob.setClient(client);
            clientManagement.setClient(client);
        } else {
            clientManagement.setClient(currentJob.getClient());
        }
        closeEntityManager(em);

        // make sure that this is not an active client managed for the BSJ
        currentJob.getClient().setActive(false);

        clientManagement.setEntityManagerFactory(EMF1);
        clientManagement.setSave(false);
        clientManagement.setBusinessEntityManager(this);
        clientManagement.setComponentsToUpdate(":jobDialogForm:jobFormTabView:client");

    }

    public void handleReportFileUpload(FileUploadEvent event) {
//        FileOutputStream fout;
//        UploadedFile upLoadedFile = event.getFile();
//        String baseURL = "\\\\bosprintsrv\\c$\\uploads\\images"; // ".\\uploads\\images\\" +  // tk to be made option
//        String upLoadedFileName = getUser().getId() + "_"
//                + new Date().getTime() + "_"
//                + upLoadedFile.getFileName();
//
//        String imageURL = baseURL + "\\" + upLoadedFileName;
//
//        try {
//            fout = new FileOutputStream(imageURL);
//            fout.write(upLoadedFile.getContents());
//            fout.close();
//
//            getCurrentProductInspection().setImageURL(upLoadedFileName);
//            setDirty(true);
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//        >>>>>>> branch 'master' of https://github.com/desbenn/jmtswebapp.git
    }
    
    public ServiceRequest createNewServiceRequest(EntityManager em,
            JobManagerUser user,
            Boolean autoGenerateServiceRequestNumber) {

        ServiceRequest sr = new ServiceRequest();
        sr.setClient(new Client(""));
        sr.setServiceRequestNumber("");
        sr.setJobDescription("");

        sr.setBusinessOffice(getDefaultBusinessOffice(em, "Head Office"));
//        sr.setDepartment(getDefaultDepartment(em, "--"));
//        sr.setAssignedTo(getDefaultEmployee(em, "--", "--"));

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

    /**
     * Validate a user and associate the user with an employee if possible.
     *
     * @param em
     * @param username
     * @param password
     * @return
     */
    public Boolean validateAndAssociateUser(EntityManager em, String username, String password) {
        Boolean userValidated = false;
        InitialLdapContext ctx = null;
        Employee employee;

        // tk for testing purposes
        if (username.equals("admin") && password.equals("password")) { // password to be encrypted
            return true;
        }

        try {
            List<jm.com.dpbennett.entity.LdapContext> ctxs = jm.com.dpbennett.entity.LdapContext.getAllLdapContexts(em);

            for (jm.com.dpbennett.entity.LdapContext ldapContext : ctxs) {
                em.refresh(ldapContext);
                ctx = ldapContext.getInitialLDAPContext(em, username, password);
                if (checkForLDAPUser(em, username, ctx)) {
                    // user does not exist in LDAP                    
                    userValidated = true;
                    break;
                }
            }

            // get employee that corresponds to this username
            if (userValidated) {
                employee = Employee.findEmployeeByUsername(em, username, ctx);
            } else {
                return false;
            }

            // get the user if one exists
            JobManagerUser jmtsUser = JobManagerUser.findJobManagerUserByUsername(em, username);

            if ((jmtsUser == null) && (employee != null)) {
                // create and associate the user with the employee
                jmtsUser = createNewUser(em);
                jmtsUser.setUsername(username);
                jmtsUser.setEmployee(employee);
                jmtsUser.setUserFirstname(employee.getFirstName());
                jmtsUser.setUserLastname(employee.getLastName());
                em.getTransaction().begin();
                BusinessEntityUtils.saveBusinessEntity(em, jmtsUser);
                em.getTransaction().commit();

                System.out.println("User validated and associated with employee.");

                return true;
            } else if ((jmtsUser != null) && (employee != null)) {
                System.out.println("User validated.");
                return true;
            } else if ((jmtsUser != null) && (employee == null)) {
                if (jmtsUser.getEmployee() != null) {
                    System.out.println("User validated.");
                    return true;
                } else {
                    System.out.println("User NOT validated!");
                    return false;
                }

            } else {
                System.out.println("User NOT validated!");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Problem connecting to directory: " + e);
        }


        return false;
    }

    /**
     * Get LDAP attributes
     *
     * @param username
     * @param ctx
     * @return
     */
    public Boolean checkForLDAPUser(EntityManager em, String username, LdapContext ctx) {

        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String[] attrIDs = {"displayName"};

            constraints.setReturningAttributes(attrIDs);

            NamingEnumeration answer = ctx.search("DC=bos,DC=local", "SAMAccountName=" // tk make DC=? option
                    + username, constraints);
            if (!answer.hasMore()) { // assuming only one match
                System.out.println("LDAP user not found!");
                return Boolean.FALSE;
            }
        } catch (Exception ex) {
            System.out.println(ex);
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    /**
     * Get LDAP attributes
     *
     * @param username
     * @param ctx
     * @return
     */
    public String getUserBasicAttributes(String username, LdapContext ctx) {
        String userdetails = null;
        try {

            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String[] attrIDs = {
                "distinguishedName",
                "sn",
                "givenname",
                "mail",
                "telephonenumber",
                "displayName",
                "uid"};
            constraints.setReturningAttributes(attrIDs);
            //First input parameter is search bas, it can be "CN=Users,DC=YourDomain,DC=com"
            //Second Attribute can be uid=username
            NamingEnumeration answer = ctx.search("DC=bos,DC=local", "SAMAccountName="
                    + username, constraints);
            if (answer.hasMore()) {
                Attributes attrs = ((SearchResult) answer.next()).getAttributes();
                System.out.println("distinguishedName " + attrs.get("distinguishedName"));
                System.out.println("givenname " + attrs.get("givenname"));
                System.out.println("sn " + attrs.get("sn"));
                System.out.println("mail " + attrs.get("mail"));
                System.out.println("telephonenumber " + attrs.get("telephonenumber"));
                System.out.println("displayName " + attrs.get("displayName"));
                System.out.println("uid " + attrs.get("uid"));
            } else {
                throw new Exception("Invalid User");
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }
        return userdetails;
    }

    public Boolean validateUser(String username, String password) {
        Hashtable env = new Hashtable();
        String principal = username + "@bos.local"; // tk option
        String ldapURL = "ldap://admain:389"; // tk option
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, principal);
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put(Context.PROVIDER_URL, ldapURL);

        try {
            //Create the initial directory context
            LdapContext ctx = new InitialLdapContext(env, null);
            return true;
        } catch (NamingException e) {
            System.err.println("Problem connecting to directory: " + e);
        }

        return false;
    }

    public JobManagerUser createNewUser(EntityManager em) {
        JobManagerUser user = new JobManagerUser();

        user.setUsername("");
        // init employee
        user.setEmployee(getDefaultEmployee(em, "--", "--"));

        // name
        user.setUserFirstname("");
        user.setUserLastname("");
        // default privileges
        user.setCanEditOwnJob(Boolean.TRUE);
        user.setCanEditDepartmentalJob(Boolean.TRUE);
        user.setCanEnterJob(Boolean.TRUE);

        return user;
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
        legalDocument.setNumber(getLegalDocumentNumber(legalDocument, "ED"));

        return legalDocument;
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

    public void createFactoryInspectionComponents(FactoryInspection inspection) {
        // tk impl getting this from a file
        // ESTABLISHMENT ENVIRONS category
        FactoryInspectionComponent component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Location", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Waste Disposal", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Building Exterior", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Drainage", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Yard Storage", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Tank", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Other Buildings", false);
        inspection.getInspectionComponents().add(component);
        // BUILDING INTERIOR
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Design & Contruction", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Pipes/Conduits/Beams", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Other Overhead Structures", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Lighting/Lighting Intensity", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Ventilation", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Drainage Systems", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Maintenance of walls", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Maintenance of floors", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Maintenance of ceilings", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Gutters/drains", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Screens/Blowers/Insecticutor", false);
        inspection.getInspectionComponents().add(component);
        // RECEIVING
        component = new FactoryInspectionComponent("RECEIVING", "Raw Material Infestation", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("RECEIVING", "Raw Material Infestation", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("RECEIVING", "Raw Material Infestation", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("RECEIVING", "Raw Material Infestation", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("RECEIVING", "Raw Material Infestation", false);
        inspection.getInspectionComponents().add(component);
    }

    /*
     * Takes a list of job costings enties an set their ids and component ids
     * to null which will result in new job costings being created when
     * the job costins are commited to the database
     */
    public List<JobCosting> copyJobCostings(List<JobCosting> srcCostings) {
        ArrayList<JobCosting> newJobCostings = new ArrayList<JobCosting>();

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

    // tk to be created from template file or database records
    public void createJobCostings(JobCostingAndPayment jobCostingAndPayment) {

        JobCosting jobCosting = new JobCosting("1.0 TEST/CALIBRATION");
        jobCosting.getCostComponents().add(new CostComponent(jobCosting.getName(), Boolean.TRUE));
        jobCosting.getCostComponents().add(new CostComponent("1.1 Preparation"));
        jobCosting.getCostComponents().add(new CostComponent("1.2 Work"));
        jobCostingAndPayment.getJobCostings().add(jobCosting);

        jobCosting = new JobCosting("2.0 LABEL EVALUATION/INSPECTION");
        jobCosting.getCostComponents().add(new CostComponent(jobCosting.getName(), Boolean.TRUE));
        jobCosting.getCostComponents().add(new CostComponent("2.1"));
        jobCosting.getCostComponents().add(new CostComponent("2.2"));
        jobCostingAndPayment.getJobCostings().add(jobCosting);

        jobCosting = new JobCosting("3.0 CONSULTANCY/TRAINING");
        jobCosting.getCostComponents().add(new CostComponent(jobCosting.getName(), Boolean.TRUE));
        jobCosting.getCostComponents().add(new CostComponent("3.1"));
        jobCosting.getCostComponents().add(new CostComponent("3.2"));
        jobCostingAndPayment.getJobCostings().add(jobCosting);

        jobCosting = new JobCosting("4.0 REPORT WRITING");
        jobCosting.getCostComponents().add(new CostComponent(jobCosting.getName(), Boolean.TRUE));
        jobCosting.getCostComponents().add(new CostComponent("4.1 Analysis of data"));
        jobCosting.getCostComponents().add(new CostComponent("4.2 Documentation of report"));
        jobCosting.getCostComponents().add(new CostComponent("4.3 Supervisor's review/approval"));
        jobCostingAndPayment.getJobCostings().add(jobCosting);

        jobCosting = new JobCosting("5.0 ENERGY/MATERIAL COST");
        jobCosting.getCostComponents().add(new CostComponent(jobCosting.getName(), Boolean.TRUE));
        jobCosting.getCostComponents().add(new CostComponent("5.1"));
        jobCosting.getCostComponents().add(new CostComponent("5.2"));
        jobCostingAndPayment.getJobCostings().add(jobCosting);

        jobCosting = new JobCosting("6.0 TRAVELLING");
        jobCosting.getCostComponents().add(new CostComponent(jobCosting.getName(), Boolean.TRUE));
        jobCosting.getCostComponents().add(new CostComponent("6.1 Driver"));
        jobCosting.getCostComponents().add(new CostComponent("6.2 Passenger"));
        jobCostingAndPayment.getJobCostings().add(jobCosting);

        jobCosting = new JobCosting("7.0 OTHER CHARGES");
        jobCosting.getCostComponents().add(new CostComponent(jobCosting.getName(), Boolean.TRUE));
        jobCosting.getCostComponents().add(new CostComponent("7.1"));
        jobCosting.getCostComponents().add(new CostComponent("7.2"));
        jobCostingAndPayment.getJobCostings().add(jobCosting);

    }

    public JobCostingAndPayment createJobCostingAndPayment() {
        JobCostingAndPayment jobCostingAndPayment = new JobCostingAndPayment();

        jobCostingAndPayment.setCostingDate(new Date());
        jobCostingAndPayment.setPurchaseOrderNumber("");
        jobCostingAndPayment.setReceiptNumber("");

        // create costings from template
        createJobCostings(jobCostingAndPayment);

        return jobCostingAndPayment;
    }

    

    public Job copyJob(EntityManager em,
            Job currentJob,
            JobManagerUser user,
            Boolean autoGenerateJobNumber,
            Boolean copySamples) {
        Job job = new Job();

        // client
        job.setClient(currentJob.getClient());

        job.setReportNumber("");
        job.setJobDescription("");

        // business office
        job.setBusinessOffice(currentJob.getBusinessOffice());
        job.setDepartment(currentJob.getDepartment());

        job.setSubContractedDepartment(getDefaultDepartment(em, "--"));
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
        job.getJobStatusAndTracking().setDateSubmitted(new Date());
        job.getJobStatusAndTracking().setAlertDate(null);
        job.getJobStatusAndTracking().setDateJobEmailWasSent(null);
        // job costing and payment
        // tk init costing...to be done with a template later
        job.setJobCostingAndPayment(createJobCostingAndPayment());
        // this is done here because job number is dependent on business office, department/subcontracted department

        // copy samples
        if (copySamples) {
//            job.setJobSamples(currentJob.getJobSamples());
            List<JobSample> samples = currentJob.getJobSamples();
            job.setNumberOfSamples(currentJob.getNumberOfSamples());
            for (Iterator<JobSample> it = samples.iterator(); it.hasNext();) {
                JobSample jobSample = it.next();
//                JobSample newJobSample = new JobSample(jobSample);
//                newJobSample.setId(new Long(job.getJobSamples().size() + 1));
                job.getJobSamples().add(new JobSample(jobSample));
//                job.getJobSamples().add(jobSample);
            }
        }


        // sequence number
        job.setJobSequenceNumber(currentJob.getJobSequenceNumber());

        // job number
        if (job.getAutoGenerateJobNumber()) {
            job.setJobNumber(getJobNumber(job));
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

//    public Long saveBusinessEntity(EntityManager em, BusinessEntity businessEntity) {
//
//        if (businessEntity.getId() != null) {
//            em.merge(businessEntity);
//        } else {
//            em.persist(businessEntity);
//        }
//
//        return businessEntity.getId();
//    }
//    public Boolean testDatabaseConnection() {
//        return EMF.isOpen();
//    }

    public HashMap<String, String> getConnectionProperties(
            String url,
            String driver,
            String username,
            String password) {

        // setup new database connection properties
        HashMap<String, String> prop = new HashMap<String, String>();
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
                        System.out.println("Job number validation error: This means 4th part is not a department code.");
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

    // tk may lomger needed or used. Check out
    public Long saveClient(EntityManager em, Client client) {
        if (client.getBillingAddress() != null) {
            BusinessEntityUtils.saveBusinessEntity(em, client.getBillingAddress());
        }
        return BusinessEntityUtils.saveBusinessEntity(em, client);
    }

    public Seal getDefaultSeal(EntityManager em,
            String number) {
        Seal seal = Seal.findSealByNumber(em, number);

        if (seal == null) {
            seal = new Seal();

            em.getTransaction().begin();
            seal.setNumber(number);
            BusinessEntityUtils.saveBusinessEntity(em, seal);
            em.getTransaction().commit();
        }

        return seal;
    }

    public Sticker getDefaultSticker(EntityManager em,
            String number) {
        Sticker sticker = Sticker.findStickerByNumber(em, number);

        if (sticker == null) {
            sticker = new Sticker();

            //em.getTransaction().begin();
            sticker.setNumber(number);
            BusinessEntityUtils.saveBusinessEntity(em, sticker);
            //em.getTransaction().commit();
        }

        return sticker;
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
            //sequenceNumber = job.getJobSequenceNumber().toString();
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
            legalDocument.setNumber(getLegalDocumentNumber(legalDocument, "ED"));
        }
        BusinessEntityUtils.saveBusinessEntity(em, legalDocument);

        return legalDocument;
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

    // tk move to doc manager
    public String getLegalDocumentNumber(LegalDocument legalDocument, String prefix) {
        String number = prefix;

        // append department code
        if (legalDocument.getResponsibleDepartment().getSubGroupCode() != null) {
            number = number + legalDocument.getResponsibleDepartment().getSubGroupCode();
        } else {
            number = number + "?";
        }
        // append doc type
        if (legalDocument.getType() != null) {
            number = number + "_" + legalDocument.getType().getCode();
        }
        // append doc form
        if (legalDocument.getDocumentForm() != null) {
            number = number + "/" + legalDocument.getDocumentForm();
        }
        // append doc seq
        if (legalDocument.getSequenceNumber() != null) {
            NumberFormat formatter = DecimalFormat.getIntegerInstance();
            formatter.setMinimumIntegerDigits(2);
            number = number + "_" + formatter.format(legalDocument.getSequenceNumber());
        } else {
            number = number + "_?";
        }
        // append month in the form (MMM) and year in the form (YY).
        if (legalDocument.getDateReceived() != null) {
            number = number + "/" + getMonthShortFormat(legalDocument.getDateReceived())
                    + getYearShortFormat(legalDocument.getDateReceived(), 2);
        }

        return number;
    }

    public String getJobNumber(Job job) {
        Calendar c = Calendar.getInstance();
        String departmentOrCompanyCode = "?";
        String year = "?";
        String sequenceNumber = "?";
        String subContractedDepartmenyOrCompanyCode = null;

        if ((job.getAutoGenerateJobNumber() != null) && (job.getAutoGenerateJobNumber() != false)) {
            // include the department code based on parent or subcontract
            if ((job.getSubContractedDepartment() == null) && // is regional office job
                    (job.getBusinessOffice().getCode() != null)) {

                departmentOrCompanyCode = job.getBusinessOffice().getCode();
                subContractedDepartmenyOrCompanyCode = null;
            } else if ((job.getSubContractedDepartment() == null)
                    && (job.getDepartment() != null)) { // not a subcontract
                // get the department code based on its id
                if (job.getDepartment().getSubGroupCode() != null) {
                    departmentOrCompanyCode = job.getDepartment().getSubGroupCode();
                }
                subContractedDepartmenyOrCompanyCode = null;
            } else if ((job.getSubContractedDepartment() != null) && // is subcontract
                    ((job.getDepartment() != null)
                    || (job.getBusinessOffice().getCode() != null))) {
                if (job.getBusinessOffice().getCode() != null) {
                    departmentOrCompanyCode = job.getBusinessOffice().getCode();
                } else if (job.getDepartment().getSubGroupCode() != null) {
                    departmentOrCompanyCode = job.getDepartment().getSubGroupCode();
                }
                subContractedDepartmenyOrCompanyCode = job.getSubContractedDepartment().getSubGroupCode();
            }
            // use the date submitted to get the year if it is valid
            // and only if this is not a subcontracted job
            if ((job.getJobStatusAndTracking().getDateSubmitted() != null) && (job.getSubContractedDepartment() == null)) {
                c.setTime(job.getJobStatusAndTracking().getDateSubmitted());
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
            if (subContractedDepartmenyOrCompanyCode != null) {
                job.setJobNumber(job.getJobNumber() + "/" + subContractedDepartmenyOrCompanyCode);
            }
            // append samples code if any
            if ((job.getNumberOfSamples() != null) && (job.getNumberOfSamples() > 0)) {
                if ((subContractedDepartmenyOrCompanyCode == null) && (job.getNumberOfSamples() > 1)) {
                    job.setJobNumber(job.getJobNumber() + "/"
                            + BusinessEntityUtils.getAlphaCode(0) + "-"
                            + BusinessEntityUtils.getAlphaCode(job.getNumberOfSamples() - 1));
                } else if ((subContractedDepartmenyOrCompanyCode == null) && (job.getNumberOfSamples() <= 1)) {
                    // job number remains as is for now
                } else if (subContractedDepartmenyOrCompanyCode != null) { // subcontract?
                    int index = 0;
                    for (JobSample sample : job.getJobSamples()) {
                        if (index == 0) {
                            job.setJobNumber(job.getJobNumber() + "/" + sample.getReference());
                        } else {
                            job.setJobNumber(job.getJobNumber() + "," + sample.getReference());
                        }
                        index++;
                    }
                }
            }
        }

        return job.getJobNumber();
    }

    public Long saveJobSample(EntityManager em, JobSample jobSample) {
        return BusinessEntityUtils.saveBusinessEntity(em, jobSample);
    }

    public Boolean deleteJob(EntityManager em, Long Id) {

        Job job = null;

        job = em.find(Job.class, Id);
        return deleteEntity(em, job);
    }

    public Boolean deleteJobSample(EntityManager em, Long Id) {

        JobSample jobSample = null;

        jobSample = em.find(JobSample.class, Id);
        return deleteEntity(em, jobSample);
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
            String searchText, // filled in with department's name
            Date startDate,
            Date endDate) {

        List<DatePeriodJobReportColumnData> data = null;
        List<DatePeriodJobReportColumnData> dataSortedByEarningType = new ArrayList<DatePeriodJobReportColumnData>();

        String searchQuery = null;

        searchQuery =
                "SELECT NEW jm.com.dpbennett.utils.DatePeriodJobReportColumnData"
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
                data = new ArrayList<DatePeriodJobReportColumnData>();
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

        List<DatePeriodJobReportColumnData> data = null;
        String searchQuery = null;

        searchQuery =
                "SELECT NEW jm.com.dpbennett.utils.DatePeriodJobReportColumnData"
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
            if (data == null) {
                data = new ArrayList<DatePeriodJobReportColumnData>();
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

        List<DatePeriodJobReportColumnData> data = null;
        String searchQuery = null;
        searchQuery =
                //                "SELECT NEW jm.com.dpbennett.utils.DatePeriodJobReportColumnData"
                //                + "("
                //                + "job"
                //                + ")"
                //                + " FROM Job job"
                //                + " JOIN job.jobStatusAndTracking jobStatusAndTracking"
                //                + " JOIN job.department department"
                //                + " JOIN job.subContractedDepartment subContractedDepartment"
                //                + " WHERE "
                //                + " (jobStatusAndTracking.dateSubmitted >= " + BusinessEntityUtils.getDateString(startDate, "'", "YMD", "-")
                //                + " AND jobStatusAndTracking.dateSubmitted <= " + BusinessEntityUtils.getDateString(endDate, "'", "YMD", "-") + ")"
                //                + " AND ( UPPER(department.name) = '" + searchText.toUpperCase() + "'"
                //                + " OR UPPER(subContractedDepartment.name) = '" + searchText.toUpperCase() + "')"
                //                + " AND jobStatusAndTracking.workProgress <> 'Cancelled'"
                //                + " AND jobStatusAndTracking.workProgress <> 'Withdrawn by client'"
                //                + " ORDER BY job.sector.name ASC";

                searchQuery =
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
            if (data == null) {
                data = new ArrayList<DatePeriodJobReportColumnData>();
            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

        return data;
    }

//    public List<Job> getJobsByPositionAndPageSize(
//            EntityManager em,
//            String query,
//            String searchText,
//            int startPos,
//            int pageSize) {
//
//        List<Job> filteredJobs;
//        List<Job> foundJobs = new ArrayList<Job>();
//        try {
//            filteredJobs = em.createQuery(query, Job.class).setFirstResult(startPos).setMaxResults(pageSize).getResultList();
//            if ((filteredJobs != null) && (!filteredJobs.isEmpty())) {
//                for (Job job : filteredJobs) {
//                    if (getJobState(job).toUpperCase().
//                            contains(searchText.trim().toUpperCase())) {
//                        foundJobs.add(job);
//                    }
//                }
//                return foundJobs;
//            }
//        } catch (Exception e) {
//            return null;
//        }
//
//        return null;
//    }
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
            List<CashPayment> cashPayments =
                    em.createQuery("SELECT c FROM CashPayment c "
                    + "WHERE c.jobId "
                    + "= '" + jobId + "'", CashPayment.class).getResultList();
            return cashPayments;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public Boolean deleteCashPayment(EntityManager em, Long Id) {
        CashPayment cashPayment = null;

        cashPayment = em.find(CashPayment.class, Id);
        return deleteEntity(em, cashPayment);

    }

    public Long saveDepartment(EntityManager em, Department department) {
        return BusinessEntityUtils.saveBusinessEntity(em, department);
    }

    public Long saveEmployee(EntityManager em, Employee employee) {
        return BusinessEntityUtils.saveBusinessEntity(em, employee);
    }

//    public EntityManager getEntityManager() {
//        return EMF.createEntityManager();
//    }

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

    public Manufacturer getDefaultManufacturer(EntityManager em,
            String name) {
        Manufacturer manufacturer = Manufacturer.findManufacturerByName(em, name);

        if (manufacturer == null) {
            manufacturer = new Manufacturer();
            em.getTransaction().begin();
            manufacturer.setName(name);
            BusinessEntityUtils.saveBusinessEntity(em, manufacturer);
            em.getTransaction().commit();
        }

        return manufacturer;
    }

    /**
     * Get the default distributor by name and creates one if it does not exist.
     *
     * @param em
     * @param name
     * @return
     */
    public Distributor getDefaultDistributor(EntityManager em,
            String name) {
        Distributor distributor = Distributor.findDistributorByName(em, name);

        if (distributor == null) {
            distributor = new Distributor();
            em.getTransaction().begin();
            distributor.setName(name);
            BusinessEntityUtils.saveBusinessEntity(em, distributor);
            em.getTransaction().commit();
        }

        return distributor;
    }

    public PetrolCompany getDefaultPetrolCompany(EntityManager em,
            String name) {
        PetrolCompany petrolCompany = PetrolCompany.findPetrolCompanyByName(em, name);

        if (petrolCompany == null) {
            petrolCompany = new PetrolCompany();

            em.getTransaction().begin();
            petrolCompany.setName(name);
            BusinessEntityUtils.saveBusinessEntity(em, petrolCompany);
            em.getTransaction().commit();
        }

        return petrolCompany;
    }

    public Contact getDefaultContact(EntityManager em, String firstName, String lastName) {

        Contact contact = Contact.findContactByName(em, firstName, lastName);

        // create employee if it does not exist
        if (contact == null) {
            contact = new Contact();
            contact.setFirstName(firstName);
            contact.setLastName(lastName);
            // save
            em.getTransaction().begin();
            BusinessEntityUtils.saveBusinessEntity(em, contact);
            em.getTransaction().commit();
        }

        return contact;
    }

    public void postJobManagerMail(
            Session mailSession,
            JobManagerUser user,
            String subject,
            String message) throws Exception {

        boolean debug = false;
        Message msg;

        if (mailSession == null) {
            //Set the host smtp address
            Properties props = new Properties();
            props.put("mail.smtp.host", "BOSMAIL2.BOS.local");// "172.16.0.3" // tk host to be made an option stored in database

            // create some properties and get the default Session
            Session session = Session.getDefaultInstance(props, null); // def null
            session.setDebug(debug);
            msg = new MimeMessage(session);
        } else {
            msg = new MimeMessage(mailSession);
        }

        // set the from and to address
        InternetAddress addressFrom = new InternetAddress("jobmanager", "Job Manager");
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[1];
        if (user != null) {
            addressTo[0] = new InternetAddress(user.getUsername(), user.getEmployee().getFirstName() + " " + user.getEmployee().getLastName());
        } else {
            // tk send message to developer. username and full name to be obtained from database in future.
            addressTo[0] = new InternetAddress("dbennett", "Desmond Bennett");
        }

        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(message, "text/plain");
        Transport.send(msg);
    }

    public Manufacturer getValidManufacturer(EntityManager em, String name) {

        if ((name == null) || (name.equals(""))) {
            return null;
        } else if (!BusinessEntityUtils.validateName(name)) {
            return null;
        } else {
            Manufacturer currentManufacturer = Manufacturer.findManufacturerByName(em, name);
            if (currentManufacturer == null) {
                // create new
                currentManufacturer = new Manufacturer();
                // replace double quotes with two single quotes to avoid query issues
                currentManufacturer.setName(name.replaceAll("\"", "''"));

                return currentManufacturer;
            } else { // create and return new manufacturer
                return currentManufacturer;
            }
        }
    }

    public void buildGasStationDatabase(
            EntityManager em,
            int sheetNumber,
            String company,
            String station,
            ArrayList<TestMeasure> measures) { // when set to null the each row on sheet is used as company branch. Otherwise each row is company.
        // cell indices
        final int NO = 0;
        final int PARISH = 1;
        final int STATION_NAME = 2;
        final int ADDRESS = 3;
        final int NUMBER_OF_PUMPS = 4;
        final int REJECTED_OR_NOT_WORKING = 5;
        final int CERTIFIED_ON = 6;
        final int EXPIRY_DATE = 7;
        final int STATUS = 8;
        final int RATE = 9;
        final int FINAL_AMOUNT = 10;
        final int NOZZLES_PER_PUMP = 2;
        int rowCount = 0;
        int cellCount = 0;
        PetrolCompany petrolCompany = new PetrolCompany();
        //
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream("GAS STATION DATABASE 2009.xls"));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            em.getTransaction().begin();
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                if (i == sheetNumber) {
                    HSSFSheet sheet = wb.getSheetAt(i);
                    petrolCompany.setName(company);
                    for (rowCount = 0; rowCount < sheet.getPhysicalNumberOfRows(); rowCount++) {
//                        for (HSSFRow row : sheet.g) {
                        if (rowCount > 0) { // make sure this is not the header
                            HSSFRow row = sheet.getRow(rowCount);
                            cellCount = 0;
                            // create new station
                            PetrolStation petrolStation = new PetrolStation();
                            if (row != null) {
                                for (cellCount = 0; cellCount < row.getPhysicalNumberOfCells(); cellCount++) {
                                    // populate petrol company and stations with data
                                    HSSFCell cell = row.getCell(cellCount);
                                    if (cell != null) {
                                        switch (cellCount) {
                                            case NO:
                                                break;
                                            case PARISH:
                                                if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                                                    petrolStation.getBillingAddress().setStateOrProvince(cell.getStringCellValue());
                                                }
                                                break;
                                            case STATION_NAME: // add station only if name if valid
                                                if ((cell.getCellType() == HSSFCell.CELL_TYPE_STRING)
                                                        && (!cell.getRichStringCellValue().getString().trim().equals(""))) {
                                                    System.out.println("Importing: " + cell.getRichStringCellValue().getString());
                                                    petrolCompany.getPetrolStations().add(petrolStation);
                                                    petrolStation.setName(cell.getRichStringCellValue().getString());
                                                }
                                                break;
                                            case ADDRESS:
                                                if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                                                    petrolStation.getBillingAddress().setAddressLine1(cell.getStringCellValue());
                                                }
                                                break;
                                            case NUMBER_OF_PUMPS:
                                                if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                                                    int numberOfNozzles = (int) cell.getNumericCellValue();
                                                    // create petrol pumps and nozzles. use max 2 nozzles per pump.
                                                    int numberOfPumps = (int) (numberOfNozzles / NOZZLES_PER_PUMP);
                                                    int oddNozzles = numberOfNozzles % NOZZLES_PER_PUMP;

                                                    if (numberOfPumps > 0) {
                                                        for (int j = 0; j < numberOfPumps; j++) {
                                                            PetrolPump petrolPump = new PetrolPump();
                                                            petrolStation.getPetrolPumps().add(petrolPump);
                                                            // create NOZZLES_PER_PUMP nozzles
                                                            for (int k = 0; k < NOZZLES_PER_PUMP; k++) {
                                                                PetrolPumpNozzle nozzle = new PetrolPumpNozzle(measures,
                                                                        getDefaultSeal(em, "--"),
                                                                        getDefaultSticker(em, "--"));
                                                                petrolPump.getNozzles().add(nozzle);
                                                            }
                                                        }
                                                        // create a pump with odd no. nozzles if any
                                                        if (oddNozzles > 0) {
                                                            PetrolPump petrolPump = new PetrolPump();
                                                            petrolStation.getPetrolPumps().add(petrolPump);
                                                            // create NOZZLES_PER_PUMP nozzles
                                                            petrolPump.setNozzles(new ArrayList<PetrolPumpNozzle>());
                                                            for (int k = 0; k < oddNozzles; k++) {
                                                                PetrolPumpNozzle nozzle = new PetrolPumpNozzle(measures,
                                                                        getDefaultSeal(em, "--"),
                                                                        getDefaultSticker(em, "--"));
                                                                petrolPump.getNozzles().add(nozzle);
                                                            }
                                                        }
                                                    } else if (oddNozzles > 0) {
                                                        PetrolPump petrolPump = new PetrolPump();
                                                        petrolStation.getPetrolPumps().add(petrolPump);
                                                        // create NOZZLES_PER_PUMP nozzles
                                                        petrolPump.setNozzles(new ArrayList<PetrolPumpNozzle>());
                                                        for (int k = 0; k < oddNozzles; k++) {
                                                            PetrolPumpNozzle nozzle = new PetrolPumpNozzle(measures,
                                                                    getDefaultSeal(em, "--"),
                                                                    getDefaultSticker(em, "--"));
                                                            petrolPump.getNozzles().add(nozzle);
                                                        }
                                                    } else {
                                                        System.out.println("Station has no pump!!");
                                                    }
                                                    System.out.println("No. of 2 nozzle pumps: " + numberOfPumps + ", Odd nozzles: " + oddNozzles);
                                                }
                                                break;
                                            case REJECTED_OR_NOT_WORKING:
                                                if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                                                    int nozzlesRejected = (int) cell.getNumericCellValue();
                                                    if (nozzlesRejected > 0) {
                                                        int pumpsToUpdate = (int) (nozzlesRejected / NOZZLES_PER_PUMP);
                                                        int oddNozzlesToUpdate = nozzlesRejected % NOZZLES_PER_PUMP;
                                                        // fill out all pump nozzles here
                                                        if (pumpsToUpdate > 0) {
                                                            for (int j = 0; j < pumpsToUpdate; j++) {
                                                                PetrolPump pertrolPump = petrolStation.getPetrolPumps().get(j);
                                                                for (PetrolPumpNozzle nozzle : pertrolPump.getNozzles()) {
                                                                    nozzle.setComments("Rejected/Not working");
                                                                }
                                                            }
                                                            // fill out the odd number pump nozzles
                                                            if (oddNozzlesToUpdate > 0) {
                                                                System.out.println("Bad nozzles: " + oddNozzlesToUpdate);
                                                                PetrolPump pertrolPump = petrolStation.getPetrolPumps().get(pumpsToUpdate);
                                                                for (int j = 0; j < oddNozzlesToUpdate; j++) {
                                                                    pertrolPump.getNozzles().get(j).setComments("Rejected/Not working");
                                                                }
                                                            }
                                                        } else if (oddNozzlesToUpdate > 0) {
                                                            System.out.println("Bad nozzles: " + oddNozzlesToUpdate);
                                                            PetrolPump pertrolPump = petrolStation.getPetrolPumps().get(0);
                                                            if (!pertrolPump.getNozzles().isEmpty()) {
                                                                for (int j = 0; j < oddNozzlesToUpdate; j++) {
                                                                    pertrolPump.getNozzles().get(j).setComments("Rejected/Not working");
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                break;
                                            case CERTIFIED_ON:
                                                // set the date in all pumps for now
                                                if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                                                    Date certifiedOn = cell.getDateCellValue();
                                                    if (certifiedOn != null) {
                                                        int numberOfPumps = petrolStation.getPetrolPumps().size();
                                                        for (int j = 0; j < numberOfPumps; j++) {
                                                            PetrolPump petrolPump = petrolStation.getPetrolPumps().get(j);
                                                            petrolPump.getCertification().setDateIssued(certifiedOn);
                                                        }
                                                    }
                                                }
                                                break;
                                            case EXPIRY_DATE:
                                                // set the date in all pumps for now
                                                if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                                                    Date expiryDate = cell.getDateCellValue();
                                                    if (expiryDate != null) {
                                                        int numberOfPumps = petrolStation.getPetrolPumps().size();
                                                        for (int j = 0; j < numberOfPumps; j++) {
                                                            PetrolPump petrolPump = petrolStation.getPetrolPumps().get(j);
                                                            petrolPump.getCertification().setExpiryDate(expiryDate);
                                                        }
                                                    }
                                                }
                                                break;
                                            case STATUS:
                                                break;
                                            case RATE:
                                                break;
                                            case FINAL_AMOUNT:
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // save the petrol company
                    System.out.println("Saving Petrol company: " + company);
                    BusinessEntityUtils.saveBusinessEntity(em, petrolCompany);
                    em.getTransaction().commit();
                    // stat
                    System.out.println("Number of stations imported: " + petrolCompany.getPetrolStations().size());

                    return;
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void insertStickers(EntityManager em, Date dateAssigned, Integer startNumber, Integer endNumber) {
        em.getTransaction().begin();
        for (int i = startNumber; i < endNumber + 1; i++) {
            System.out.println("Inserting sticker number: " + i);
            Sticker sticker = new Sticker(Integer.toString(i), dateAssigned);
            BusinessEntityUtils.saveBusinessEntity(em, sticker);
        }
        em.getTransaction().commit();
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

            HSSFSheet costingSheet = wb.getSheet("Report");
            costingSheet.setActive(true);
            costingSheet.setForceFormulaRecalculation(true);

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
                    //System.out.println(jobReportItems);
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
        font.setFontHeightInPoints((short) 12);
        font.setFontName("Wingdings");

        return font;
    }

    Font getFont(HSSFWorkbook wb, String fontName) {
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(fontName);

        return font;
    }

    public FileInputStream createServiceContractExcelFileInputStream(
            EntityManager em,
            JobManagerUser user,
            Long jobId,
            URL url) {
        try {

            Job currentJob = Job.findJobById(em, jobId);
            File file = new File(url.toURI());

            FileInputStream inp = new FileInputStream(file);

            // create workbook from input file
            POIFSFileSystem fileSystem = new POIFSFileSystem((FileInputStream) inp);

            HSSFWorkbook wb = new HSSFWorkbook(fileSystem);
            HSSFCellStyle dataCellStyle = getDefaultCellStyle(wb);

            // create temp file for output
            FileOutputStream out = new FileOutputStream("ServiceContract-" + user.getId() + ".xls");

            // get service contract sheet
            HSSFSheet serviceContractSheet = wb.getSheet("ServiceContract");
            serviceContractSheet.setActive(true);
            serviceContractSheet.setForceFormulaRecalculation(true);

            // fill in job data
            // company name and address
            dataCellStyle.setBorderLeft((short) 2);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 4, 0,
                    currentJob.getClient().getName(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 5, 0,
                    currentJob.getClient().getBillingAddress().getAddressLine1(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 6, 0,
                    currentJob.getClient().getBillingAddress().getAddressLine2(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 7, 0,
                    currentJob.getClient().getBillingAddress().getStateOrProvince(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 8, 0,
                    currentJob.getClient().getBillingAddress().getCity(),
                    "java.lang.String", dataCellStyle);
            // contracting business office
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 4, 8,
                    currentJob.getBusinessOffice().getName(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 5, 8,
                    currentJob.getBusinessOffice().getAddress().getAddressLine1(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 6, 8,
                    currentJob.getBusinessOffice().getAddress().getAddressLine2(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 7, 8,
                    currentJob.getBusinessOffice().getAddress().getStateOrProvince(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 8, 8,
                    currentJob.getBusinessOffice().getAddress().getCity(),
                    "java.lang.String", dataCellStyle);

            // department use only
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            dataCellStyle.setBorderBottom((short) 1);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 4, 16,
                    currentJob.getDepartment().getName(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 5, 16,
                    currentJob.getJobNumber(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 6, 16,
                    currentJob.getJobStatusAndTracking().getEnteredBy().getFirstName() + " " + currentJob.getJobStatusAndTracking().getEnteredBy().getLastName(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 7, 16,
                    BusinessEntityUtils.getDateInMediumDateFormat(currentJob.getJobStatusAndTracking().getDateSubmitted()),
                    "java.lang.String", dataCellStyle);

            // contact person
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 2);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 11, 0,
                    currentJob.getClient().getMainContact(),
                    "java.lang.String", dataCellStyle);

            // phone number
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 13, 0,
                    currentJob.getClient().getMainContact().getMainPhoneNumber().getLocalNumber(),
                    "java.lang.String", dataCellStyle);

            // fax number
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 13, 4,
                    currentJob.getClient().getMainContact().getMainFaxNumber().getLocalNumber(),
                    "java.lang.String", dataCellStyle);

            // fax number
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 13, 7,
                    currentJob.getClient().getMainContact().getInternet().getEmail1(),
                    "java.lang.String", dataCellStyle);

            // p.o number
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            dataCellStyle.setBorderBottom((short) 2);
            dataCellStyle.setBorderLeft((short) 2);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 0,
                    currentJob.getJobCostingAndPayment().getPurchaseOrderNumber(),
                    "java.lang.String", dataCellStyle);

            // date submitted
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 3,
                    BusinessEntityUtils.getDateInMediumDateFormat(currentJob.getJobStatusAndTracking().getDateSubmitted()),
                    "java.lang.String", dataCellStyle);

            // estimated turn around time
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 6,
                    currentJob.getEstimatedTurnAroundTimeInDays(),
                    "java.lang.String", dataCellStyle);

            // estimated cost
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 8,
                    "J$" + currentJob.getJobCostingAndPayment().getEstimatedCost(),
                    "java.lang.String", dataCellStyle);

            // deposit
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 11,
                    "J$" + currentJob.getJobCostingAndPayment().getDeposit(),
                    "java.lang.String", dataCellStyle);

            // receipt no(s)
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 14,
                    currentJob.getJobCostingAndPayment().getReceiptNumber(),
                    "java.lang.String", dataCellStyle);

            // date deposit paid
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            dataCellStyle.setBorderBottom((short) 2);

            if (currentJob.getJobCostingAndPayment().getDepositDate() != null) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 21, 17,
                        BusinessEntityUtils.getDateInMediumDateFormat(currentJob.getJobCostingAndPayment().getDepositDate()),
                        "java.lang.String", dataCellStyle);
            }

            // service requested
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            if (currentJob.getServiceContract().getServiceRequestedCalibration()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 23, 2,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getServiceRequestedLabelEvaluation()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 23, 4,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getServiceRequestedInspection()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 0,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getServiceRequestedConsultancy()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 2,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getServiceRequestedTraining()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 4,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getServiceRequestedOther()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 6,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setBorderLeft((short) 2);
            if (currentJob.getServiceContract().getServiceRequestedTesting()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 23, 0,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getServiceRequestedInspection()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 0,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setBorderRight((short) 2);
            if ((currentJob.getServiceContract().getServiceRequestedOtherText() != null)
                    && (!currentJob.getServiceContract().getServiceRequestedOtherText().isEmpty())) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 7,
                        "Other (" + currentJob.getServiceContract().getServiceRequestedOtherText() + ")",
                        "java.lang.String", dataCellStyle);
            }

            // details/sampleples
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 2);
            dataCellStyle.setBorderLeft((short) 2);
            dataCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
            dataCellStyle.setWrapText(true);
            String samplesDetail = "";
            int count = 0;
            for (JobSample jobSample : currentJob.getJobSamples()) {
                if (count == 0) {
                    samplesDetail = samplesDetail + "(" + jobSample.getReference() + ") " + jobSample.getDescription();
                } else {
                    samplesDetail = samplesDetail + ", (" + jobSample.getReference() + ") " + jobSample.getDescription();
                }
                ++count;
            }
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 26, 0,
                    samplesDetail,
                    "java.lang.String", dataCellStyle);


            // special instructions           
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 25, 8,
                    currentJob.getServiceContract().getSpecialInstructions(),
                    "java.lang.String", dataCellStyle);

            // intended market
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            if (currentJob.getServiceContract().getIntendedMarketLocal()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 49, 16,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getIntendedMarketCaricom()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 49, 18,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getIntendedMarketUK()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 49, 20,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getIntendedMarketCanada()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 51, 18,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getIntendedMarketOther()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 51, 20,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            dataCellStyle.setBorderLeft((short) 2);
            if (currentJob.getServiceContract().getIntendedMarketUSA()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 51, 16,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            // sample disposal
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setBorderLeft((short) 2);
            // Collected by the client within 30 days
            if (checkForSampleDisposalMethod(currentJob.getJobSamples(), 1)) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 49, 0,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            // Disposed of by the bsj
            if (checkForSampleDisposalMethod(currentJob.getJobSamples(), 2)) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 50, 0,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            // addservice requested
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setBorderLeft((short) 2);
            if (currentJob.getServiceContract().getAdditionalServiceUrgent()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 14, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            if (currentJob.getServiceContract().getAdditionalServiceFaxResults()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 15, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            if (currentJob.getServiceContract().getAdditionalServiceTelephonePresumptiveResults()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 16, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            if (currentJob.getServiceContract().getAdditionalServiceSendMoreContractForms()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 17, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            if (currentJob.getServiceContract().getAdditionalServiceOther()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 18, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            dataCellStyle = getDefaultCellStyle(wb);
            Font font = getFont(wb, "Arial");
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            dataCellStyle.setFont(font);
            if (currentJob.getServiceContract().getAdditionalServiceOtherText() != null) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 18, 12,
                        "Other: " + currentJob.getServiceContract().getAdditionalServiceOtherText(),
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

    

    public void generateTestMonthlyReport(String datePeriodName) {

        // Setup reporting month and period
        DatePeriod reportingPeriod = new DatePeriod(datePeriodName, "month", null, null, false, false, true);
        DatePeriod datePeriods[] = BusinessEntityUtils.getMonthlyReportDatePeriods(reportingPeriod);

        //JobManagement jm = new JobManagement();

        try {

            DatePeriodJobReport jobSubCategoryReport;
            DatePeriodJobReport sectorReport;
            DatePeriodJobReport jobQuantitiesAndServicesReport;

            EntityManager em = BusinessEntityUtils.getEntityManager("EntitiesPU");
            JobManagerUser user = JobManagerUser.findJobManagerUserByUsername(em, "dbennett");
            Department reportingDepartment = Department.findDepartmentById(em, 20L); // eletrical dept id = 20
            List<JobSubCategory> subCategories = JobSubCategory.findAllJobSubCategoriesGroupedByEarningsByDepartment(em, reportingDepartment);
            List<Sector> sectors = Sector.findAllSectorsByDeparment(em, reportingDepartment);
            List<JobReportItem> jobReportItems = JobReportItem.findAllJobReportItemsByDeparment(em, reportingDepartment);

            //             reports
            jobSubCategoryReport = new DatePeriodJobReport(reportingDepartment, subCategories, null, null, datePeriods);
            sectorReport = new DatePeriodJobReport(reportingDepartment, null, sectors, null, datePeriods);
            jobQuantitiesAndServicesReport = new DatePeriodJobReport(reportingDepartment, null, null, jobReportItems, datePeriods);

            // populate SubCategoryReport/Sector/report items
            if (em != null) {
                for (int i = 0; i < datePeriods.length; i++) {
                    //                     job subcat report
                    List<DatePeriodJobReportColumnData> data = jobSubCategogyGroupReportByDatePeriod(em,
                            "dateOfCompletion", // dateOfCompletion
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

                //   generate report
                createExcelJobReportFileInputStream(
                        getClass().getResource("MonthlyReport.xls"),
                        user, reportingDepartment, jobSubCategoryReport, sectorReport, jobQuantitiesAndServicesReport);
                System.out.println("Report generated!");
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void updateAllJobs(EntityManager em) {
        long jobsUpdated = 0;

        try {
            System.out.println("Getting all jobs...");
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
     * Get report based on JasperReport compiled report
     *
     * @return
     */
    public FileInputStream getJasperReportFileInputStream(EntityManager em) {

        FileInputStream stream = null;
        HashMap parameters = new HashMap();

        try {

            // load the required JDBC driver and create the connection
            // tk

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://boshrmapp/jmts",
                    "root",
                    "bsj0001");

            // set parameters and retrieve report
            parameters.put("jobId", 352L); // $P{jobId}
            JasperPrint print = JasperFillManager.fillReport("C:\\Projects\\JobManagementAndTracking\\JMTSWebAppBetaV2\\reports\\Foods Dept Lab Requisition.jasper", parameters, con);

            JRExporter exporter = new JRPdfExporter();
            // Configure the exporter (set output file name and print object)
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "Foods Dept Lab Requisition.pdf");
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

            // Export the PDF file
            exporter.exportReport();
//
//
//            // prepare exported file for transmission to the client
//            stream = new FileInputStream("form.pdf");
            //jobReportFile = new DefaultStreamedContent(stream, mimeType, fileToExportTo);

        } catch (Exception e) {
            System.out.println(e);
            //longProcessProgress = 100;
        }

        return stream;
    }

    /**
     * Get report using a JasperReport compiled report
     *
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


        } catch (Exception e) {
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

}
