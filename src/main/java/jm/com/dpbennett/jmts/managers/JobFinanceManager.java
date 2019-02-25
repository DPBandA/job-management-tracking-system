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

import jm.com.dpbennett.wal.managers.SystemManager;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.AccPacCustomer;
import jm.com.dpbennett.business.entity.AccPacDocument;
import jm.com.dpbennett.business.entity.AccountingCode;
import jm.com.dpbennett.business.entity.Alert;
import jm.com.dpbennett.business.entity.CashPayment;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.CostCode;
import jm.com.dpbennett.business.entity.CostComponent;
import jm.com.dpbennett.business.entity.Department;
import jm.com.dpbennett.business.entity.DepartmentUnit;
import jm.com.dpbennett.business.entity.Employee;
import jm.com.dpbennett.business.entity.Job;
import jm.com.dpbennett.business.entity.JobCosting;
import jm.com.dpbennett.business.entity.JobCostingAndPayment;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.Laboratory;
import jm.com.dpbennett.business.entity.Preference;
import jm.com.dpbennett.business.entity.SystemOption;
import jm.com.dpbennett.business.entity.Tax;
import jm.com.dpbennett.business.entity.UnitCost;
import jm.com.dpbennett.business.entity.management.MessageManagement;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import jm.com.dpbennett.business.entity.management.BusinessEntityManagement;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.wal.utils.BeanUtils;
import jm.com.dpbennett.wal.utils.DialogActionHandler;
import jm.com.dpbennett.wal.utils.FinancialUtils;
import jm.com.dpbennett.wal.utils.Utils;
import jm.com.dpbennett.wal.utils.MainTabView;
import jm.com.dpbennett.wal.utils.PrimeFacesUtils;
import jm.com.dpbennett.wal.utils.ReportUtils;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.PrimeFaces;
import org.primefaces.component.selectonemenu.SelectOneMenu;

/**
 *
 * @author Desmond Bennett
 */
public class JobFinanceManager implements Serializable, BusinessEntityManagement,
        DialogActionHandler, MessageManagement {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    @PersistenceUnit(unitName = "AccPacPU")
    private EntityManagerFactory EMF2;
    private CashPayment selectedCashPayment;
    private StreamedContent jobCostingFile;
    private Integer longProcessProgress;
    private AccPacCustomer accPacCustomer;
    private List<AccPacDocument> filteredAccPacCustomerDocuments;
    private Boolean useAccPacCustomerList;
    private CostComponent selectedCostComponent;
    private JobCostingAndPayment selectedJobCostingAndPayment;
    private String selectedJobCostingTemplate;        
    private Department unitCostDepartment;
    private UnitCost currentUnitCost;
    private List<UnitCost> unitCosts;
    private String dialogActionHandlerId;
    private List<Job> jobsWithCostings;
    private Job currentJobWithCosting;
    private Department jobCostDepartment;
    private Boolean showPrepayments;
    private String invalidFormFieldMessage;
    private String dialogMessage;
    private String dialogMessageHeader;
    private String dialogMessageSeverity;
    private Boolean dialogRenderOkButton;
    private Boolean dialogRenderYesButton;
    private Boolean dialogRenderNoButton;
    private Boolean dialogRenderCancelButton;
    private DialogActionHandler dialogActionHandler;
    private Boolean enableOnlyPaymentEditing;
    private JobManager jobManager;
    private Boolean edit;
    private String fileDownloadErrorMessage;    
    private MainTabView mainTabView;
    private JobManagerUser user;

    /**
     * Creates a new instance of JobManagerBean
     */
    public JobFinanceManager() {
        init();
    }
    
    public List<Tax> completeTax(String query) {
        EntityManager em;

        try {
            em = getEntityManager1();

            List<Tax> taxes = Tax.findTaxesByNameAndDescription(em, query);

            return taxes;

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
      
    /**
     * Returns the discount type that can be applied to a payment/amount
     *
     * @return
     */
    public List getDiscountTypes() {

        return FinancialUtils.getDiscountTypes();
    }
    
    public List getCostTypeList() {
        return FinancialUtils.getCostTypeList();
    }

    public List getPaymentTypes() {
        return FinancialUtils.getPaymentTypes();
    }

    public List getPaymentPurposes() {
        return FinancialUtils.getPaymentPurposes();
    }
  
    public void closeDialog() {
        PrimeFacesUtils.closeDialog(null);
    }
    
    public JobCostingAndPayment getSelectedJobCostingAndPayment() {
        return selectedJobCostingAndPayment;
    }

    public void setSelectedJobCostingAndPayment(JobCostingAndPayment selectedJobCostingAndPayment) {
        this.selectedJobCostingAndPayment = selectedJobCostingAndPayment;
    }

    public void cancelDialogEdit(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public MainTabView getMainTabView() {
        
        return mainTabView;
    }

    public void setMainTabView(MainTabView mainTabView) {
        this.mainTabView = mainTabView;
    }

    public StreamedContent getAccpacInvoicesFile() {

        try {
            ByteArrayInputStream stream;

            stream = getAccpacInvoicesFileInputStream(
                    new File(getClass().getClassLoader().
                            getResource("/reports/"
                                    + (String) SystemOption.getOptionValueObject(getEntityManager1(),
                                            "AccpacInvoicesFileTemplateName")).getFile()));

            setLongProcessProgress(100);

            return new DefaultStreamedContent(stream,
                    "application/xlsx",
                    "Invoices-" + BusinessEntityUtils.getDateInMediumDateAndTimeFormat(new Date())
                    + "-" + fileDownloadErrorMessage + ".xlsx");

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }
    
    public ByteArrayInputStream getAccpacInvoicesFileInputStream(
            File file) {

        try {
            FileInputStream inp = new FileInputStream(file);
            int invoiceRow = 1;
            int invoiceDetailsRow = 1;
            int invoiceCol;
            int invoiceDetailsCol;

            fileDownloadErrorMessage = "";

            XSSFWorkbook wb = new XSSFWorkbook(inp);
            XSSFCellStyle stringCellStyle = wb.createCellStyle();
            XSSFCellStyle integerCellStyle = wb.createCellStyle();
            XSSFCellStyle doubleCellStyle = wb.createCellStyle();
            XSSFCellStyle dateCellStyle = wb.createCellStyle();
            CreationHelper createHelper = wb.getCreationHelper();
            dateCellStyle.setDataFormat(
                    createHelper.createDataFormat().getFormat("m/d/yyyy"));

            // Output stream for modified Excel file
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // Get sheets          
            XSSFSheet invoices = wb.getSheet("Invoices");
            XSSFSheet invoiceDetails = wb.getSheet("Invoice_Details");

            // Get report data
            List<Job> reportData = getJobManager().getJobSearchResultList();
            for (Job job : reportData) {
                invoiceCol = 0;
                invoiceDetailsCol = 0;

                // Fill out the Invoices 
                // CNTBTCH (batch number)
                ReportUtils.setExcelCellValue(wb, invoices, invoiceRow, invoiceCol++,
                        0,
                        "java.lang.Integer", integerCellStyle);
                // CNTITEM (Item number)
                ReportUtils.setExcelCellValue(wb, invoices, invoiceRow, invoiceCol++,
                        invoiceRow,
                        "java.lang.Integer", integerCellStyle);
                // IDCUST (Customer Id)
                if (job.getClient().getAccountingId().isEmpty()) {
                    fileDownloadErrorMessage = "Error - missing customer id(s)";
                }
                ReportUtils.setExcelCellValue(wb, invoices, invoiceRow, invoiceCol++,
                        job.getClient().getAccountingId(),
                        "java.lang.String", stringCellStyle);
                // IDINVC (Invoice No./Id)
                ReportUtils.setExcelCellValue(wb, invoices, invoiceRow, invoiceCol++,
                        "IN" + job.getYearReceived() + "" + job.getJobSequenceNumber(),
                        "java.lang.String", stringCellStyle);
                // TEXTTRX
                ReportUtils.setExcelCellValue(wb, invoices, invoiceRow, invoiceCol++,
                        1,
                        "java.lang.Integer", integerCellStyle);
                // IDTRX
                ReportUtils.setExcelCellValue(wb, invoices, invoiceRow, invoiceCol++,
                        12,
                        "java.lang.Integer", integerCellStyle);
                // INVCDESC
                ReportUtils.setExcelCellValue(wb, invoices, invoiceRow, invoiceCol++,
                        job.getInstructions(),
                        "java.lang.String", stringCellStyle);
                // DATEINVC
                ReportUtils.setExcelCellValue(wb, invoices, invoiceRow, invoiceCol++,
                        new Date(),
                        "java.util.Date", dateCellStyle);
                // INVCTYPE
                ReportUtils.setExcelCellValue(wb, invoices, invoiceRow, invoiceCol++,
                        2,
                        "java.lang.Integer", integerCellStyle);

                // Fill out invoice details
                // CNTBTCH (batch number)
                ReportUtils.setExcelCellValue(wb, invoiceDetails, invoiceDetailsRow,
                        invoiceDetailsCol++,
                        0,
                        "java.lang.Integer", integerCellStyle);
                // CNTITEM (Item number)
                ReportUtils.setExcelCellValue(wb, invoiceDetails, invoiceDetailsRow,
                        invoiceDetailsCol++,
                        invoiceRow,
                        "java.lang.Integer", integerCellStyle);
                // CNTLINE
                ReportUtils.setExcelCellValue(wb, invoiceDetails, invoiceDetailsRow,
                        invoiceDetailsCol++,
                        invoiceDetailsRow, // Cell value
                        "java.lang.Integer", integerCellStyle);

                invoiceDetailsRow++;
                invoiceRow++;

            }

            // Write modified Excel file and return it
            wb.write(out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException ex) {
            System.out.println(ex);
        }

        return null;
    }
 
    public Boolean getEdit() {
        return edit;
    }

    public void setEdit(Boolean edit) {
        this.edit = edit;
    }

    public JobManager getJobManager() {
        if (jobManager == null) {
            jobManager = BeanUtils.findBean("jobManager");
        }
        return jobManager;
    }

    private void init() {
        longProcessProgress = 0;
        accPacCustomer = new AccPacCustomer(null);
        useAccPacCustomerList = false;
        selectedCashPayment = null;
        selectedCostComponent = null;
        unitCostDepartment = null;
        jobCostDepartment = null;
        filteredAccPacCustomerDocuments = new ArrayList<>();
    }

    public void reset() {
        init();
    }

    public Boolean getEnableOnlyPaymentEditing() {
        if (enableOnlyPaymentEditing == null) {
            enableOnlyPaymentEditing = false;
        }
        return enableOnlyPaymentEditing;
    }

    public void setEnableOnlyPaymentEditing(Boolean enableOnlyPaymentEditing) {
        this.enableOnlyPaymentEditing = enableOnlyPaymentEditing;
    }

    public Boolean getDialogRenderCancelButton() {
        return dialogRenderCancelButton;
    }

    public void setDialogRenderCancelButton(Boolean dialogRenderCancelButton) {
        this.dialogRenderCancelButton = dialogRenderCancelButton;
    }

    public void setDialogActionHandler(DialogActionHandler dialogActionHandler) {
        this.dialogActionHandler = dialogActionHandler;
    }

    public Boolean getDialogRenderOkButton() {
        return dialogRenderOkButton;
    }

    public void setDialogRenderOkButton(Boolean dialogRenderOkButton) {
        this.dialogRenderOkButton = dialogRenderOkButton;
    }

    public Boolean getDialogRenderYesButton() {
        return dialogRenderYesButton;
    }

    public void setDialogRenderYesButton(Boolean dialogRenderYesButton) {
        this.dialogRenderYesButton = dialogRenderYesButton;
    }

    public Boolean getDialogRenderNoButton() {
        return dialogRenderNoButton;
    }

    public void setDialogRenderNoButton(Boolean dialogRenderNoButton) {
        this.dialogRenderNoButton = dialogRenderNoButton;
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

    public EntityManager getEntityManager1() {
        return EMF1.createEntityManager();
    }

    public JobManagerUser getUser() {
        return user;
    }

    public void setUser(JobManagerUser user) {
        this.user = user;
    }
    
    @Override
    public String getInvalidFormFieldMessage() {
        return invalidFormFieldMessage;
    }

    @Override
    public void setInvalidFormFieldMessage(String invalidFormFieldMessage) {
        this.invalidFormFieldMessage = invalidFormFieldMessage;
    }

    public void displayCommonMessageDialog(DialogActionHandler dialogActionHandler, String dialogMessage,
            String dialogMessageHeader,
            String dialoMessageSeverity) {

        setDialogActionHandler(dialogActionHandler);

        setDialogRenderOkButton(true);
        setDialogRenderYesButton(false);
        setDialogRenderNoButton(false);

        setDialogMessage(dialogMessage);
        setDialogMessageHeader(dialogMessageHeader);
        setDialogMessageSeverity(dialoMessageSeverity);

        PrimeFaces.current().ajax().update("commonMessageDialogForm");
        PrimeFaces.current().executeScript("PF('commonMessageDialog').show();");
    }

    public void displayCommonConfirmationDialog(DialogActionHandler dialogActionHandler,
            String dialogMessage,
            String dialogMessageHeader,
            String dialoMessageSeverity) {

        setDialogActionHandler(dialogActionHandler);

        setDialogRenderOkButton(false);
        setDialogRenderYesButton(true);
        setDialogRenderNoButton(true);
        setDialogRenderCancelButton(true);

        setDialogMessage(dialogMessage);
        setDialogMessageHeader(dialogMessageHeader);
        setDialogMessageSeverity(dialoMessageSeverity);

        PrimeFaces.current().ajax().update("commonMessageDialogForm");
        PrimeFaces.current().executeScript("PF('commonMessageDialog').show();");
    }

    public void handleDialogOkButtonPressed() {
        if (dialogActionHandler != null) {
            dialogActionHandler.handleDialogOkButtonClick();
        }
    }

    public void handleDialogYesButtonPressed() {
        if (dialogActionHandler != null) {
            dialogActionHandler.handleDialogYesButtonClick();
        }
    }

    public void handleDialogNoButtonPressed() {
        if (dialogActionHandler != null) {
            dialogActionHandler.handleDialogNoButtonClick();
        }
    }

    public void handleDialogCancelButtonPressed() {
        if (dialogActionHandler != null) {
            dialogActionHandler.handleDialogCancelButtonClick();
        }
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

    public Boolean getCanEditJobCosting() {
        // Can edit if user belongs to the department to which the job was assigned
        return getUser().getPrivilege().getCanBeFinancialAdministrator()
                || getCurrentJob().getJobCostingAndPayment().getCashPayments().isEmpty();
    }

    public String getSelectedJobCostingTemplate() {
        return selectedJobCostingTemplate;
    }

    public void setSelectedJobCostingTemplate(String selectedJobCostingTemplate) {
        this.selectedJobCostingTemplate = selectedJobCostingTemplate;
    }

    private Boolean isJobAssignedToUserDepartment(Job job) {

        if (getUser() != null) {
            if (job.getDepartment().getId().longValue() == getUser().getEmployee().getDepartment().getId().longValue()) {
                return true;
            } else {
                return job.getSubContractedDepartment().getId().longValue() == getUser().getEmployee().getDepartment().getId().longValue();
            }
        } else {
            return false;
        }
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

        int days = (Integer) SystemOption.getOptionValueObject(em, "maxDaysPassInvoiceDate");

        return days;
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

    // tk get forms 
    public StreamedContent getJobCostingAnalysisFile(EntityManager em) {

        HashMap parameters = new HashMap();

        try {
            parameters.put("jobId", getCurrentJob().getId());

            Client client = getCurrentJob().getClient();

            parameters.put("contactPersonName", BusinessEntityUtils.getContactFullName(getCurrentJob().getContact()));
            parameters.put("customerAddress", getCurrentJob().getBillingAddress().toString());
            parameters.put("contactNumbers", client.getStringListOfContactPhoneNumbers());
            parameters.put("jobDescription", getCurrentJob().getJobDescription());

            parameters.put("totalCost", getCurrentJob().getJobCostingAndPayment().getTotalJobCostingsAmount());
            parameters.put("depositReceiptNumbers", getCurrentJob().getJobCostingAndPayment().getReceiptNumber());
            parameters.put("discount", getCurrentJob().getJobCostingAndPayment().getDiscount());
            parameters.put("discountType", getCurrentJob().getJobCostingAndPayment().getDiscountType());
            parameters.put("deposit", getCurrentJob().getJobCostingAndPayment().getTotalPayment());
            parameters.put("amountDue", getCurrentJob().getJobCostingAndPayment().getAmountDue());
            parameters.put("totalTax", getCurrentJob().getJobCostingAndPayment().getTotalTax());
            parameters.put("totalTaxLabel", getCurrentJob().getJobCostingAndPayment().getTotalTaxLabel());
            parameters.put("grandTotalCostLabel", getCurrentJob().getJobCostingAndPayment().getTotalCostWithTaxLabel().toUpperCase().trim());
            parameters.put("grandTotalCost", getCurrentJob().getJobCostingAndPayment().getTotalCost());

            Connection con = BusinessEntityUtils.establishConnection(
                    (String) SystemOption.getOptionValueObject(em, "defaultDatabaseDriver"),
                    (String) SystemOption.getOptionValueObject(em, "defaultDatabaseURL"),
                    (String) SystemOption.getOptionValueObject(em, "defaultDatabaseUsername"),
                    (String) SystemOption.getOptionValueObject(em, "defaultDatabasePassword"));

            if (con != null) {
                try {
                    StreamedContent streamContent;
                    // Compile report
                     JasperReport jasperReport = 
                             JasperCompileManager.
                                     compileReport((String) SystemOption.getOptionValueObject(em, "jobCosting"));
                    
                    // Generate report
                    //JasperPrint print = JasperFillManager.fillReport((String) SystemOption.getOptionValueObject(em, "jobCosting"), parameters, con);
                    JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, con);

                    byte[] fileBytes = JasperExportManager.exportReportToPdf(print);

                    streamContent = new DefaultStreamedContent(new ByteArrayInputStream(fileBytes), "application/pdf", "Job Costing - " + getCurrentJob().getJobNumber() + ".pdf");

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
        EntityManager em;

        try {
            em = getEntityManager1();

            if (getCurrentJob().getIsDirty()) {
                getCurrentJob().getJobCostingAndPayment().save(em);
                getCurrentJob().setIsDirty(false);
            }

            jobCostingFile = getJobCostingAnalysisFile(em);

            setLongProcessProgress(100);

        } catch (Exception e) {
            System.out.println(e);
            setLongProcessProgress(100);
        }

        return jobCostingFile;
    }

    public Boolean getCanExportJobCosting() {
        return !(getCurrentJob().getJobCostingAndPayment().getCostingApproved()
                && getCurrentJob().getJobCostingAndPayment().getCostingCompleted());
    }

    public void invoiceJobCosting() {
        if (canInvoiceJobCosting(getCurrentJob())) {
            setJobCostingAndPaymentDirty(true);
        } else {
            getCurrentJob().getJobCostingAndPayment().setInvoiced(!getCurrentJob().
                    getJobCostingAndPayment().getInvoiced());
        }
    }

    public Boolean canInvoiceJobCosting(Job job) {

        if (job.getJobCostingAndPayment().getCostingApproved()
                && job.getJobCostingAndPayment().getCostingCompleted()
                && getUser().getEmployee().isMemberOf(getDepartmentBySystemOptionDeptId("invoicingDepartmentId"))) {

            return true;

        } else {

            PrimeFacesUtils.addMessage("Permission Denied",
                    "You do not have permission to change invoice status or "
                    + "the job costing has not been prepared and approved for "
                    + job.getJobNumber(),
                    FacesMessage.SEVERITY_ERROR);

            return false;
        }

    }

    public List<Preference> getJobTableViewPreferences() {
        EntityManager em = getEntityManager1();

        List<Preference> prefs = Preference.findAllPreferencesByName(em, "jobTableView");

        return prefs;
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
        return isJobAssignedToUserDepartment(job);
    }

    public CashPayment getSelectedCashPayment() {
        return selectedCashPayment;
    }

    public void setSelectedCashPayment(CashPayment selectedCashPayment) {

        this.selectedCashPayment = selectedCashPayment;

        // If this is a new cash payment ensure that all related costs are updated
        // and the job cost and payment saved.
        if (getSelectedCashPayment().getId() == null) {
            updateFinalCost();
            updateAmountDue();

            if (!getCurrentJob().prepareAndSave(getEntityManager1(), getUser()).isSuccess()) {
                PrimeFacesUtils.addMessage("Payment and Job NOT Saved!", "Payment and the job and the payment were NOT saved!", FacesMessage.SEVERITY_ERROR);
            }
        }
    }

    public EntityManager getEntityManager2() {
        return getEMF2().createEntityManager();
    }

    public void updateJobCostingAndPayment() {
        setJobCostingAndPaymentDirty(true);
    }

    public void updateCashPayment() {
        getSelectedCashPayment().setIsDirty(true);
    }

    public void updateCostComponent() {
        getSelectedCostComponent().setIsDirty(true);
    }

    public void updateSubcontract(AjaxBehaviorEvent event) {
        if (!((SelectOneMenu) event.getComponent()).getValue().toString().equals("null")) {
            Long subcontractId = new Long(((SelectOneMenu) event.getComponent()).getValue().toString());
            Job subcontract = Job.findJobById(getEntityManager1(), subcontractId);

            selectedCostComponent.setCost(subcontract.getJobCostingAndPayment().getFinalCost());
            selectedCostComponent.setName("Subcontract (" + subcontract.getJobNumber() + ")");

            updateCostComponent();
        }
    }

    public void updateJobDescription() {
        setJobCostingAndPaymentDirty(true);
    }

    public void updateAllTaxes(AjaxBehaviorEvent event) {
        EntityManager em = getEntityManager1();

        if (getCurrentJob().getJobCostingAndPayment().getId() != null) {
            JobCostingAndPayment jcp
                    = JobCostingAndPayment.findJobCostingAndPaymentById(em,
                            getCurrentJob().getJobCostingAndPayment().getId());
            em.refresh(jcp);

            if (!(jcp.getCashPayments().isEmpty()
                    || getUser().getPrivilege().getCanBeFinancialAdministrator())) {

                // Reset cash payments
                getCurrentJob().getJobCostingAndPayment().
                        setCashPayments(jcp.getCashPayments());

                PrimeFacesUtils.addMessage("Permission Denied",
                        "A payment was made on this job so update of this field is not allowed",
                        FacesMessage.SEVERITY_ERROR);

                setJobCostingAndPaymentDirty(false);
            } else {
                updateJobCostingEstimate();
                updateTotalCost();

                setJobCostingAndPaymentDirty(true);
            }

        } else {
            updateJobCostingEstimate();
            updateTotalCost();

            setJobCostingAndPaymentDirty(true);
        }

    }

    public String getSubcontractsMessage() {
        if (getCurrentJob().getId() != null) {
            if (!getCurrentJob().findSubcontracts(getEntityManager1()).isEmpty()) {
                return ("{ " + getCurrentJob().getSubcontracts(getEntityManager1()).size() + " subcontract(s) exist(s) that can be added as cost item(s) }");
            } else if (!getCurrentJob().findPossibleSubcontracts(getEntityManager1()).isEmpty()) {
                return ("{ " + getCurrentJob().getPossibleSubcontracts(getEntityManager1()).size() + " possible subcontract(s) exist(s) that can be added as cost item(s) }");
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public void updateMinimumDepositRequired() {
        EntityManager em = getEntityManager1();

        if (getCurrentJob().getJobCostingAndPayment().getId() != null) {
            JobCostingAndPayment jcp
                    = JobCostingAndPayment.findJobCostingAndPaymentById(em,
                            getCurrentJob().getJobCostingAndPayment().getId());
            em.refresh(jcp);

            if (!(jcp.getCashPayments().isEmpty()
                    || getUser().getPrivilege().getCanBeFinancialAdministrator())) {

                // Reset min deposit required
                getCurrentJob().getJobCostingAndPayment().
                        setMinDeposit(jcp.getMinDeposit());

                // Reset cash payments
                getCurrentJob().getJobCostingAndPayment().
                        setCashPayments(jcp.getCashPayments());

                PrimeFacesUtils.addMessage("Permission Denied",
                        "A payment was made on this job so update of this field is not allowed",
                        FacesMessage.SEVERITY_ERROR);

                setJobCostingAndPaymentDirty(false);

            } else {
                updateJobCostingEstimate();

                setJobCostingAndPaymentDirty(true);
            }

        } else {
            updateJobCostingEstimate();

            setJobCostingAndPaymentDirty(true);
        }

    }

    public void updatePurchaseOrderNumber() {
        EntityManager em = getEntityManager1();

        if (getCurrentJob().getJobCostingAndPayment().getId() != null) {
            JobCostingAndPayment jcp
                    = JobCostingAndPayment.findJobCostingAndPaymentById(em,
                            getCurrentJob().getJobCostingAndPayment().getId());
            em.refresh(jcp);

            if (!(jcp.getCashPayments().isEmpty()
                    || getUser().getPrivilege().getCanBeFinancialAdministrator())) {
                // Reset PO#
                getCurrentJob().getJobCostingAndPayment().
                        setPurchaseOrderNumber(jcp.getPurchaseOrderNumber());

                // Reset cash payments
                getCurrentJob().getJobCostingAndPayment().
                        setCashPayments(jcp.getCashPayments());

                PrimeFacesUtils.addMessage("Permission Denied",
                        "A payment was made on this job so update of this field is not allowed",
                        FacesMessage.SEVERITY_ERROR);

                setJobCostingAndPaymentDirty(false);
            } else {
                setJobCostingAndPaymentDirty(true);
            }

        } else {
            setJobCostingAndPaymentDirty(true);
        }

    }

    public void updateEstimatedCostIncludingTaxes() {
        updateJobCostingEstimate();
    }

    public void updateMinimumDepositIncludingTaxes() {
        updateJobCostingEstimate();
    }

    public void updateJobCostingEstimate() {

        EntityManager em = getEntityManager1();

        if (getCurrentJob().getJobCostingAndPayment().getId() != null) {
            JobCostingAndPayment jcp
                    = JobCostingAndPayment.findJobCostingAndPaymentById(em,
                            getCurrentJob().getJobCostingAndPayment().getId());
            em.refresh(jcp);

            if (!(jcp.getCashPayments().isEmpty()
                    || getUser().getPrivilege().getCanBeFinancialAdministrator())) {
                // Reset cost estimate
                getCurrentJob().getJobCostingAndPayment().
                        setEstimatedCost(jcp.getEstimatedCost());

                // Reset cash payments
                getCurrentJob().getJobCostingAndPayment().
                        setCashPayments(jcp.getCashPayments());

                PrimeFacesUtils.addMessage("Permission Denied",
                        "A payment was made on this job so update of this field is not allowed",
                        FacesMessage.SEVERITY_ERROR);

                setJobCostingAndPaymentDirty(false);
            } else {
                // Update estmated cost and min. deposit  
                // tk may not need to do this here but in the respective get methods
                if (getCurrentJob().getJobCostingAndPayment().getEstimatedCost() != null) {
                    Double estimatedCostWithTaxes = getCurrentJob().getJobCostingAndPayment().getEstimatedCost()
                            + getCurrentJob().getJobCostingAndPayment().getEstimatedCost()
                            * getCurrentJob().getJobCostingAndPayment().getTax().getValue();
                    getCurrentJob().getJobCostingAndPayment().setEstimatedCostIncludingTaxes(estimatedCostWithTaxes);
                    setJobCostingAndPaymentDirty(true);
                }

                // tk may not need to do this here but in the respective get methods
                if (getCurrentJob().getJobCostingAndPayment().getMinDeposit() != null) {
                    Double minDepositWithTaxes = getCurrentJob().getJobCostingAndPayment().getMinDeposit()
                            + getCurrentJob().getJobCostingAndPayment().getMinDeposit()
                            * getCurrentJob().getJobCostingAndPayment().getTax().getValue();
                    getCurrentJob().getJobCostingAndPayment().setMinDepositIncludingTaxes(minDepositWithTaxes);
                    setJobCostingAndPaymentDirty(true);
                }
            }
        } else {
            setJobCostingAndPaymentDirty(true);
        }

    }

    public void updateTotalDeposit() {
        EntityManager em = getEntityManager1();

        Employee employee = Employee.findEmployeeById(em, getUser().getEmployee().getId());
        if (employee != null) {
            getCurrentJob().getJobCostingAndPayment().setLastPaymentEnteredBy(employee);
        }
        updateAmountDue();
        setIsDirty(true);
    }

    public void update() {
        setIsDirty(true);
    }

    public void updateJobCostingValidity() {
        if (!validateCurrentJobCosting() && getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
            getCurrentJob().getJobCostingAndPayment().setCostingCompleted(false);
            getCurrentJob().getJobCostingAndPayment().setCostingApproved(false);
            displayCommonMessageDialog(null, "Removing the content of a required field has invalidated this job costing", "Invalid Job Costing", "info");
        } else {
            setJobCostingAndPaymentDirty(true);
        }
    }

    public void completeJobCosting() {

        if (getCurrentJob().getJobCostingAndPayment().getCostingApproved()) {
            getCurrentJob().getJobCostingAndPayment().setCostingCompleted(!getCurrentJob().getJobCostingAndPayment().getCostingCompleted());
            PrimeFacesUtils.addMessage("Job Costing Already Approved",
                    "The job costing preparation status cannot be changed because it was already approved",
                    FacesMessage.SEVERITY_ERROR);
        } else if (getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
            getCurrentJob().getJobStatusAndTracking().setDateCostingCompleted(new Date());
            getCurrentJob().getJobStatusAndTracking().setCostingDate(new Date());
        } else if (!getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
            getCurrentJob().getJobStatusAndTracking().setDateCostingCompleted(null);
            getCurrentJob().getJobStatusAndTracking().setCostingDate(null);
        }

        setJobCostingAndPaymentDirty(true);
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

        if (Department.findDepartmentAssignedToJob(foundJob, em).getHead().getId().longValue() == getUser().getEmployee().getId().longValue()) {
            return true;
        } else {
            return (Department.findDepartmentAssignedToJob(foundJob, em).getActingHead().getId().longValue() == getUser().getEmployee().getId().longValue())
                    && Department.findDepartmentAssignedToJob(foundJob, em).getActingHeadActive();
        }
    }

    public void approveJobCosting() {

        if (canChangeJobCostingApprovalStatus(getCurrentJob())) {
            if (getCurrentJob().getJobCostingAndPayment().getCostingApproved()) {
                getCurrentJob().getJobStatusAndTracking().setDateCostingApproved(new Date());
            } else {
                getCurrentJob().getJobStatusAndTracking().setDateCostingApproved(null);
            }
            setJobCostingAndPaymentDirty(true);
        } else {
            getCurrentJob().getJobCostingAndPayment().
                    setCostingApproved(!getCurrentJob().getJobCostingAndPayment().getCostingApproved());
        }
    }

    public Boolean canChangeJobCostingApprovalStatus(Job job) {

        if (!job.getJobCostingAndPayment().getCostingCompleted()
                || job.getJobCostingAndPayment().getInvoiced()) {

            PrimeFacesUtils.addMessage("Cannot Change Approval Status",
                    "The job costing approval status for " + job.getJobNumber()
                    + " cannot be changed before the job costing is prepared or if it was already invoiced",
                    FacesMessage.SEVERITY_ERROR);

            return false;

        } else if (isUserDepartmentSupervisor(job)
                || (isJobAssignedToUserDepartment(job)
                && getUser().getPrivilege().getCanApproveJobCosting())) {

            return true;

        } else {

            PrimeFacesUtils.addMessage("No Permission",
                    "You do not have the permission to change the job costing approval status for " + job.getJobNumber(),
                    FacesMessage.SEVERITY_ERROR);

            return false;

        }
    }

    public void updatePreferences() {
        setIsDirty(true);
    }

    public void updateCostType() {
        switch (selectedCostComponent.getType()) {
            case "FIXED":
                selectedCostComponent.setIsFixedCost(true);
                selectedCostComponent.setIsHeading(false);
                selectedCostComponent.setHours(0.0);
                selectedCostComponent.setHoursOrQuantity(0.0);
                selectedCostComponent.setRate(0.0);
                break;
            case "HEADING":
                selectedCostComponent.setIsFixedCost(false);
                selectedCostComponent.setIsHeading(true);
                selectedCostComponent.setHours(0.0);
                selectedCostComponent.setHoursOrQuantity(0.0);
                selectedCostComponent.setRate(0.0);
                selectedCostComponent.setCost(0.0);
                break;
            case "VARIABLE":
                selectedCostComponent.setIsFixedCost(false);
                selectedCostComponent.setIsHeading(false);
                break;
            case "SUBCONTRACT":
                selectedCostComponent.setIsFixedCost(true);
                selectedCostComponent.setIsHeading(false);
                selectedCostComponent.setHours(0.0);
                selectedCostComponent.setHoursOrQuantity(0.0);
                selectedCostComponent.setRate(0.0);
                break;
            default:
                selectedCostComponent.setIsFixedCost(false);
                selectedCostComponent.setIsHeading(false);
                break;
        }

        updateCostComponent();

    }

    public Boolean getAllowCostEdit() {
        if (selectedCostComponent != null) {
            if (null == selectedCostComponent.getType()) {
                return true;
            } else switch (selectedCostComponent.getType()) {
                case "--":
                    return true;
                default:
                    return false;
            }
        } else {
            return true;
        }
    }

    public void updateIsCostComponentHeading() {

    }

    public void updateIsCostComponentFixedCost() {

        if (getSelectedCostComponent().getIsFixedCost()) {

        }
    }

    public void updateNewClient() {
        setIsDirty(true);
    }

    public void updateDepartmentReport() {
    }

    public void updateJobNumber() {
        setIsDirty(true);
    }

    public void updateSamplesCollected() {
        setIsDirty(true);
    }

    public void closelJobCostingDialog() {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void closeUnitCostDialog() {

        // prompt to save modified job before attempting to create new job
        if (getIsDirty()) {
            // ask to save         
            displayCommonConfirmationDialog(initDialogActionHandlerId("unitCostDirty"), "This unit cost was modified. Do you wish to save it?", "Unit Cost Not Saved", "info");
        } else {
            PrimeFaces.current().executeScript("PF('unitCostDialog').hide();");
        }

    }

    public void cancelJobCostingEdit(ActionEvent actionEvent) {
        setIsDirty(false);
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void cancelJobCostingAndPayment(ActionEvent actionEvent) {
        EntityManager em = getEntityManager1();

        // refetch costing data from database
        if (getCurrentJob() != null) {
            if (getCurrentJob().getId() != null) {
                Job job = Job.findJobById(em, getCurrentJob().getId());
                getCurrentJob().setJobCostingAndPayment(job.getJobCostingAndPayment());
            }
        }

        setIsDirty(false);
    }

    public void jobCostingDialogReturn() {

        if (getCurrentJob().getId() != null) {
            if (isJobCostingAndPaymentDirty()) {
                if (getCurrentJob().prepareAndSave(getEntityManager1(), getUser()).isSuccess()) {
                    getCurrentJob().getJobStatusAndTracking().setEditStatus("");
                    PrimeFacesUtils.addMessage("Job Costing and Job Saved", "This job and the costing were saved", FacesMessage.SEVERITY_INFO);
                }
            }
        }
    }

    public void okJobCosting(ActionEvent actionEvent) {

        try {

            if (getUser().getEmployee() != null) {
                getCurrentJob().getJobCostingAndPayment().setFinalCostDoneBy(getUser().getEmployee().getName());
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public Boolean validateCurrentJobCosting() {

        try {
            // check for valid job
            if (getCurrentJob().getId() == null) {
                return false;
            }
            // check for job report # and description
            if ((getCurrentJob().getReportNumber() == null) || (getCurrentJob().getReportNumber().trim().equals(""))) {
                return false;
            }
            if (getCurrentJob().getJobDescription().trim().equals("")) {
                return false;
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return true;

    }

    public void saveUnitCost() {
        EntityManager em = getEntityManager1();

        try {

            // Validate and save objects
            // Department
            Department department = Department.findDepartmentByName(em, getCurrentUnitCost().getDepartment().getName());
            if (department == null) {
                setInvalidFormFieldMessage("This unit cost cannot be saved because a valid department was not entered.");

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
                setInvalidFormFieldMessage("This unit cost cannot be saved because a valid service was not entered.");

                return;
            }

            // Cost
            if (getCurrentUnitCost().getCost() <= 0.0) {
                setInvalidFormFieldMessage("This unit cost cannot be saved because a valid cost was not entered.");

                return;
            }

            // Effective date
            if (getCurrentUnitCost().getEffectiveDate() == null) {
                setInvalidFormFieldMessage("This unit cost cannot be saved because a valid effective date was not entered.");

                return;
            }

            // save job to database and check for errors
            em.getTransaction().begin();

            Long id = BusinessEntityUtils.saveBusinessEntity(em, currentUnitCost);
            if (id == null) {
                
                sendErrorEmail("An error occurred while saving this unit cost",
                        "Unit cost save error occurred");
                return;
            }

            em.getTransaction().commit();
            setIsDirty(false);

        } catch (Exception e) {
            
            System.out.println(e);
            // send error message to developer's email
            sendErrorEmail("An exception occurred while saving a unit cost!",
                    "\nJMTS User: " + getUser().getUsername()
                    + "\nDate/time: " + new Date()
                    + "\nException detail: " + e);
        }
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
        message = message + "<span style='font-weight:bold'>Department/Unit Head: </span>" + BusinessEntityUtils.getPersonFullName(Department.findDepartmentAssignedToJob(job, em).getHead(), false) + "<br>";
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
     * @throws java.lang.Exception
     */
    public void updateAlert(EntityManager em) throws Exception {
        if (getCurrentJob().getJobStatusAndTracking().getCompleted() == null) {
            em.getTransaction().begin();

            Alert alert = Alert.findFirstAlertByOwnerId(em, getCurrentJob().getId());
            if (alert == null) { // This seems to be a new job
                alert = new Alert(getCurrentJob().getId(), new Date(), "Job entered");
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

            Alert alert = Alert.findFirstAlertByOwnerId(em, getCurrentJob().getId());
            if (alert == null) { // This seems to be a new job
                alert = new Alert(getCurrentJob().getId(), new Date(), "Job saved");
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
            // Send error message to developer's email            
            Utils.postMail(null, null, null, subject, message, 
                    "text/plain", getEntityManager1());
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void deleteCostComponent() {
        deleteCostComponentByName(selectedCostComponent.getName());
    }

    // Remove this and other code out of JobManager? Put in JobCostingAndPayment or Job?
    public void deleteCostComponentByName(String componentName) {

        List<CostComponent> components = getCurrentJob().getJobCostingAndPayment().getAllSortedCostComponents();
        int index = 0;
        for (CostComponent costComponent : components) {
            if (costComponent.getName().equals(componentName)) {
                components.remove(index);
                setJobCostingAndPaymentDirty(true);

                break;
            }
            ++index;
        }

        updateFinalCost();
        updateAmountDue();
    }

    public void editCostComponent(ActionEvent event) {
        setEdit(true);
    }

    public void createNewCashPayment(ActionEvent event) {

        if (getCurrentJob().getId() != null) {
            selectedCashPayment = new CashPayment();

            // If there were other payments it is assumed that this is a final payment.
            // Otherwsie, it is assumed to be a deposit.
            if (getCurrentJob().getJobCostingAndPayment().getCashPayments().size() > 0) {
                selectedCashPayment.setPaymentPurpose("Final");
            } else {
                selectedCashPayment.setPaymentPurpose("Deposit");
            }

            PrimeFacesUtils.openDialog(null, "/job/finance/cashPaymentDialog", true, true, true, 350, 500);
        } else {
            PrimeFacesUtils.addMessage("Job NOT Saved",
                    "Job must be saved before a new payment can be added",
                    FacesMessage.SEVERITY_WARN);
        }

    }

    public void editCashPayment(ActionEvent event) {

        PrimeFacesUtils.openDialog(null, "/job/finance/cashPaymentDialog", true, true, true, 350, 500);

    }

    public void createNewCostComponent(ActionEvent event) {
        selectedCostComponent = new CostComponent();
        setEdit(false);
    }

    public void cancelCashPaymentEdit() {
        selectedCashPayment.setIsDirty(false);

        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void cancelCostComponentEdit() {
        selectedCostComponent.setIsDirty(false);
    }

    public void cashPaymentDialogReturn() {
        if (getCurrentJob().getId() != null) {
            if (isJobCostingAndPaymentDirty()) {
                if (getCurrentJob().prepareAndSave(getEntityManager1(), getUser()).isSuccess()) {
                    PrimeFacesUtils.addMessage("Payment and Job Saved", "Payment and the job and the payment were saved", FacesMessage.SEVERITY_INFO);
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public Boolean getIsJobCompleted() {
        if (getCurrentJob() != null) {
            return getCurrentJob().getJobStatusAndTracking().getCompleted();
        } else {
            return false;
        }
    }

    public void okCostingComponent() {
        if (selectedCostComponent.getId() == null && !getEdit()) {
            getCurrentJob().getJobCostingAndPayment().getCostComponents().add(selectedCostComponent);
        }
        setEdit(false);
        updateFinalCost();
        updateAmountDue();

        PrimeFaces.current().executeScript("PF('costingComponentDialog').hide();");
    }

    public void updateFinalCost() {
        getCurrentJob().getJobCostingAndPayment().setFinalCost(getCurrentJob().getJobCostingAndPayment().getTotalJobCostingsAmount());
        setJobCostingAndPaymentDirty(true);
    }

    public void updateTotalCost() {
        updateAmountDue();
    }

    public void updateAmountDue() {
        setJobCostingAndPaymentDirty(true);
    }

    public Boolean getCanApplyTax() {

        return JobCostingAndPayment.getCanApplyTax(getCurrentJob())
                && getCanEditJobCosting();
    }

    public void openJobCostingDialog() {
        if (getCurrentJob().getId() != null && !getCurrentJob().getIsDirty()) {
            // Reload cash payments if possible to avoid overwriting them 
            // when saving
            EntityManager em = getEntityManager1();
            JobCostingAndPayment jcp
                    = JobCostingAndPayment.findJobCostingAndPaymentById(em,
                            getCurrentJob().getJobCostingAndPayment().getId());

            em.refresh(jcp);

            getCurrentJob().getJobCostingAndPayment().setCashPayments(jcp.getCashPayments());

            PrimeFacesUtils.openDialog(null, "/job/finance/jobCostingDialog", true, true, true, 600, 850);
        } else {
            PrimeFacesUtils.addMessage("Job NOT Saved",
                    "Job must be saved before the job costing can be viewed or edited",
                    FacesMessage.SEVERITY_WARN);
            PrimeFaces.current().executeScript("PF('longProcessDialogVar').hide();");
        }
    }

    public void editJobCosting() {

        PrimeFacesUtils.openDialog(null, "jobCostingDialog", true, true, true, 600, 850);
    }

    public void okCashPayment() {

        if (getSelectedCashPayment().getId() == null) {
            getCurrentJob().getJobCostingAndPayment().getCashPayments().add(selectedCashPayment);
        }

        updateFinalCost();
        updateAmountDue();

        PrimeFaces.current().dialog().closeDynamic(null);

    }

    public void updateJobCostingDate() {
        setJobCostingAndPaymentDirty(true);
    }

    public List<JobCostingAndPayment> completeJobCostingAndPaymentName(String query) {
        EntityManager em;

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

    public List<String> completeAccPacClientName(String query) {
        EntityManager em2;

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
        EntityManager em2;

        try {
            em2 = getEntityManager2();

            return AccPacCustomer.findAccPacCustomersByName(em2, query);
        } catch (Exception e) {

            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public List<CostCode> getAllCostCodes() {
        EntityManager em = getEntityManager1();

        List<CostCode> codes = CostCode.findAllCostCodes(em);

        return codes;
    }

    public List<Job> getAllSubcontracts() {
        List<Job> subcontracts = new ArrayList<>();

        if (getCurrentJob().getId() != null) {
            if (!getCurrentJob().findSubcontracts(getEntityManager1()).isEmpty()) {
                subcontracts.addAll(getCurrentJob().findSubcontracts(getEntityManager1()));
            } else {
                subcontracts.addAll(getCurrentJob().findPossibleSubcontracts(getEntityManager1()));
            }

            subcontracts.add(0, new Job("-- select a subcontract --"));
        } else {
            subcontracts.add(0, new Job("-- none exists --"));
        }

        return subcontracts;
    }

    private EntityManagerFactory getEMF2() {
        return EMF2;
    }

    public Job getCurrentJob() {
        return getJobManager().getCurrentJob();
    }

    @Override
    public void setIsDirty(Boolean dirty) {
        getCurrentJob().setIsDirty(dirty);
    }

    @Override
    public Boolean getIsDirty() {
        return getCurrentJob().getIsDirty();
    }

    public void setJobCostingAndPaymentDirty(Boolean dirty) {
        getCurrentJob().getJobCostingAndPayment().setIsDirty(dirty);

        if (dirty) {
            getCurrentJob().getJobStatusAndTracking().setEditStatus("(edited)");
        } else {
            getCurrentJob().getJobStatusAndTracking().setEditStatus("");
        }
    }

    public Boolean isJobCostingAndPaymentDirty() {
        return getCurrentJob().getJobCostingAndPayment().getIsDirty();
    }

    public void updateCurrentUnitCostDepartment() {
        EntityManager em;

        try {
            em = getEntityManager1();
            if (currentUnitCost.getDepartment().getName() != null) {
                Department department = Department.findDepartmentByName(em, currentUnitCost.getDepartment().getName());
                if (department != null) {
                    currentUnitCost.setDepartment(department);
                    setIsDirty(true);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void updateCurrentUnitCostDepartmentUnit() {
        EntityManager em;

        try {
            em = getEntityManager1();
            if (currentUnitCost.getDepartmentUnit().getName() != null) {
                DepartmentUnit departmentUnit = DepartmentUnit.findDepartmentUnitByName(em, currentUnitCost.getDepartmentUnit().getName());
                if (departmentUnit != null) {
                    currentUnitCost.setDepartmentUnit(departmentUnit);
                    setIsDirty(true);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void updateCurrentUnitCostDepartmentLab() {
        EntityManager em;

        try {
            em = getEntityManager1();
            if (currentUnitCost.getLaboratory().getName() != null) {
                Laboratory laboratory = Laboratory.findLaboratoryByName(em, currentUnitCost.getLaboratory().getName());

                if (laboratory != null) {
                    currentUnitCost.setLaboratory(laboratory);
                    setIsDirty(true);
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void updateUnitCostDepartment() {
        EntityManager em;

        try {
            em = getEntityManager1();
            if (unitCostDepartment.getName() != null) {
                Department department = Department.findDepartmentByName(em, unitCostDepartment.getName());
                if (department != null) {
                    unitCostDepartment = department;
                    //doUnitCostSearch();
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void updateJobCostDepartment() {
        EntityManager em;

        try {
            em = getEntityManager1();
            if (jobCostDepartment.getName() != null) {
                Department department = Department.findDepartmentByName(em, jobCostDepartment.getName());
                if (department != null) {
                    jobCostDepartment = department;
                    //doUnitCostSearch();
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
            if (getCurrentJob() != null) {
                if (!getCurrentJob().getClient().getName().equals("")) {
                    accPacCustomer.setCustomerName(getCurrentJob().getClient().getName());
                } else {
                    accPacCustomer.setCustomerName("?");
                }
                updateCreditStatus(null);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void updateAccPacCustomer(SelectEvent event) {
        if (accPacCustomer != null) {
            try {
                EntityManager em = getEntityManager2();

                accPacCustomer = AccPacCustomer.findByName(em, accPacCustomer.getCustomerName());
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

    public Integer getNumberOfFilteredAccPacCustomerDocuments() {
        if (filteredAccPacCustomerDocuments != null) {
            return filteredAccPacCustomerDocuments.size();
        }

        return 0;
    }

    public Boolean getFilteredDocumentAvailable() {
        if (filteredAccPacCustomerDocuments != null) {
            return !filteredAccPacCustomerDocuments.isEmpty();
        } else {
            return false;
        }
    }

    public void updateCreditStatusSearch() {
        accPacCustomer.setCustomerName(getCurrentJob().getClient().getName());
        updateCreditStatus(null);
    }

    public void updateCreditStatus(SelectEvent event) {
        EntityManager em = getEntityManager2();

        accPacCustomer = AccPacCustomer.findByName(em, accPacCustomer.getCustomerName().trim());

        if (accPacCustomer != null) {
            if (getShowPrepayments()) {
                filteredAccPacCustomerDocuments = AccPacDocument.findAccPacInvoicesDueByCustomerId(em, accPacCustomer.getIdCust(), true);
            } else {
                filteredAccPacCustomerDocuments = AccPacDocument.findAccPacInvoicesDueByCustomerId(em, accPacCustomer.getIdCust(), false);
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
        accPacCustomer.setIdCust(null);
        filteredAccPacCustomerDocuments = new ArrayList<>();
    }

    public String getAccPacCustomerID() {
        if (accPacCustomer.getIdCust() == null) {
            return "";
        } else {
            return accPacCustomer.getIdCust();
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
        if (accPacCustomer.getIDACCTSET().equals("TRADE")
                && accPacCustomer.getCreditLimit().doubleValue() > 0.0) {
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

    public void updateCostingComponents() {
        if (selectedJobCostingTemplate != null) {
            EntityManager em = getEntityManager1();
            JobCostingAndPayment jcp
                    = JobCostingAndPayment.findJobCostingAndPaymentByDepartmentAndName(em,
                            getUser().getEmployee().getDepartment().getName(),
                            selectedJobCostingTemplate);
            if (jcp != null) {
                getCurrentJob().getJobCostingAndPayment().getCostComponents().clear();
                getCurrentJob().getJobCostingAndPayment().setCostComponents(copyCostComponents(jcp.getCostComponents()));

                setJobCostingAndPaymentDirty(true);
            } else {
                // Nothing yet
            }

            selectedJobCostingTemplate = null;

        }
    }

    public void removeCurrentJobCostingComponents(EntityManager em) {

        if (!getCurrentJob().getJobCostingAndPayment().getCostComponents().isEmpty()) {
            em.getTransaction().begin();
            for (CostComponent costComponent : getCurrentJob().getJobCostingAndPayment().getCostComponents()) {
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

//    public Date getCurrentDate() {
//        return new Date();
//    }

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

    public void deleteCashPayment() {

        List<CashPayment> payments = getCurrentJob().getJobCostingAndPayment().getCashPayments();

        for (CashPayment payment : payments) {
            if (payment.equals(selectedCashPayment)) {
                payments.remove(selectedCashPayment);
                break;
            }
        }

        updateFinalCost();
        updateAmountDue();

        // Do job save if possible...
        if (getCurrentJob().getId() != null
                && getCurrentJob().prepareAndSave(getEntityManager1(), getUser()).isSuccess()) {
            PrimeFacesUtils.addMessage("Job Saved",
                    "The payment was deleted and the job was saved", FacesMessage.SEVERITY_INFO);
        } else {
            setJobCostingAndPaymentDirty(true);
            PrimeFacesUtils.addMessage("Job NOT Saved",
                    "The payment was deleted but the job was not saved", FacesMessage.SEVERITY_WARN);
        }

        PrimeFaces.current().dialog().closeDynamic(null);

    }

    public void checkForSubcontracts(ActionEvent event) {
        //PrimeFacesUtils.openDialog(null, "/job/finance/cashPaymentDeleteConfirmDialog", true, true, true, 110, 375);
    }

    public void openCashPaymentDeleteConfirmDialog(ActionEvent event) {

        PrimeFacesUtils.openDialog(null, "/job/finance/cashPaymentDeleteConfirmDialog", true, true, true, 110, 375);
    }

    public void closeJCashPaymentDeleteConfirmDialog() {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void openJobPricingsDialog() {

        PrimeFacesUtils.openDialog(null, "jobPricings", true, true, true, 600, 800);
    }

    public void openJobCostingsDialog() {

        PrimeFacesUtils.openDialog(null, "jobCostings", true, true, true, 600, 800);
    }

   
    public void doJobCostSearch() {
        System.out.println("To be implemented");
    }

    public void createNewUnitCost() {

        currentUnitCost = new UnitCost();

        PrimeFaces.current().ajax().update("unitCostForm");
        PrimeFaces.current().executeScript("PF('unitCostDialog').show();");
    }

    public void editUnitCost() {
    }

    @Override
    public void handleDialogOkButtonClick() {
    }

    @Override
    public void handleDialogYesButtonClick() {

        if (dialogActionHandlerId.equals("unitCostDirty")) {
            saveUnitCost();
            PrimeFaces.current().executeScript("PF('unitCostDialog').hide();");
        }

    }

    @Override
    public void handleDialogNoButtonClick() {

        if (dialogActionHandlerId.equals("unitCostDirty")) {
            setIsDirty(false);
            PrimeFaces.current().executeScript("PF('unitCostDialog').hide();");
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

    /**
     * This is to be implemented further
     *
     * @return
     */
    public Boolean getDisableSubContracting() {
        try {
            if (getCurrentJob().getIsSubContract() || getCurrentJob().getIsToBeCopied()) {
                return false;
            } else return getCurrentJob().getId() == null;
        } catch (Exception e) {
            System.out.println(e);
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

            String listAsString = (String) SystemOption.getOptionValueObject(em, "domainNames");
            String domainNames[] = listAsString.split(";");

            JobManagerUser user1 = JobManagerUser.findActiveJobManagerUserByEmployeeId(em, employee.getId());

            // Build email address
            if (user1 != null) {
                address = user1.getUsername();
                if (domainNames.length > 0) {
                    address = address + "@" + domainNames[0];
                }
            }

        }

        return address;
    }

    public Boolean isCurrentJobNew() {
        return (getCurrentJob().getId() == null);
    }

    public Department getDepartmentBySystemOptionDeptId(String option) {
        EntityManager em = getEntityManager1();

        Long id = (Long) SystemOption.getOptionValueObject(em, option);

        Department department = Department.findDepartmentById(em, id);
        em.refresh(department);

        if (department != null) {
            return department;
        } else {
            return new Department("");
        }
    }

    public Boolean getIsMemberOfAccountsDept() {
        return getUser().getEmployee().isMemberOf(getDepartmentBySystemOptionDeptId("accountsDepartmentId"));
    }

    public Boolean getIsMemberOfCustomerServiceDept() {
        return getUser().getEmployee().isMemberOf(getDepartmentBySystemOptionDeptId("customerServiceDeptId"));
    }

    /**
     * Return discount types. NB: Discount types to be obtained from System
     * Options in the future
     *
     * @param query
     * @return
     */
    public List<String> completeDiscountType(String query) {
        String discountTypes[] = {"Currency", "Percentage"};
        List<String> matchedDiscountTypes = new ArrayList<>();

        for (String discountType : discountTypes) {
            if (discountType.contains(query)) {
                matchedDiscountTypes.add(discountType);
            }
        }

        return matchedDiscountTypes;

    }

}
