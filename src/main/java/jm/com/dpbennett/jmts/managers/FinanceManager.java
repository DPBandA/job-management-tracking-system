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

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.AccPacCustomer;
import jm.com.dpbennett.business.entity.AccPacDocument;
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
import jm.com.dpbennett.business.entity.UnitCost;
import jm.com.dpbennett.business.entity.management.MessageManagement;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.jmts.utils.DialogActionHandler;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import jm.com.dpbennett.business.entity.management.BusinessEntityManagement;
import jm.com.dpbennett.business.entity.utils.PrimeFacesUtils;
import jm.com.dpbennett.jmts.Application;

/**
 *
 * @author Desmond Bennett
 */
@Named
@SessionScoped
public class FinanceManager implements Serializable, BusinessEntityManagement,
        DialogActionHandler, MessageManagement {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    @PersistenceUnit(unitName = "AccPacPU")
    private EntityManagerFactory EMF2;
    private Job currentJob;
    private CashPayment selectedCashPayment;
    private Boolean addCostComponent;
    private Boolean addCashPayment;
    private StreamedContent jobCostingFile;
    @ManagedProperty(value = "Jobs")
    private Integer longProcessProgress;
    private AccPacCustomer accPacCustomer;
    private List<AccPacDocument> filteredAccPacCustomerDocuments;
    private Boolean useAccPacCustomerList;
    private CostComponent selectedCostComponent;
    private String selectedJobCostingTemplate;
    private Department unitCostDepartment;
    private UnitCost currentUnitCost;
    private String searchText;
    private List<UnitCost> unitCosts;
    private String dialogActionHandlerId;
    private List<Job> jobsWithCostings;
    private Job currentJobWithCosting;
    private Department jobCostDepartment;
    private Boolean showPrepayments;
    private JobManagerUser user;
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

    /**
     * Creates a new instance of JobManagerBean
     */
    public FinanceManager() {
        init();
    }

    private void init() {
        this.longProcessProgress = 0;
        accPacCustomer = new AccPacCustomer(null);
        filteredAccPacCustomerDocuments = new ArrayList<>();
        useAccPacCustomerList = false;
        addCashPayment = false;
        addCostComponent = false;
        addCostComponent = false;
        currentJob = null;
        selectedCashPayment = null;
        selectedCostComponent = null;
        unitCostDepartment = null;
        user = null;
        jobCostDepartment = null;
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

    public void setUser(JobManagerUser user) {
        this.user = user;
    }

    public JobManagerUser getUser() {
        if (user == null) {
            return new JobManagerUser();
        }
        return user;
    }

    /**
     * Get user as currently stored in the database
     *
     * @param em
     * @return
     */
    public JobManagerUser getUser(EntityManager em) {
        if (user == null) {
            return new JobManagerUser();
        } else {
            try {
                if (user.getId() != null) {
                    JobManagerUser foundUser = em.find(JobManagerUser.class, user.getId());
                    if (foundUser != null) {
                        em.refresh(foundUser);
                        user = foundUser;
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
                return new JobManagerUser();
            }
        }

        return user;
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
        RequestContext context = RequestContext.getCurrentInstance();

        setDialogActionHandler(dialogActionHandler);

        setDialogRenderOkButton(true);
        setDialogRenderYesButton(false);
        setDialogRenderNoButton(false);

        setDialogMessage(dialogMessage);
        setDialogMessageHeader(dialogMessageHeader);
        setDialogMessageSeverity(dialoMessageSeverity);

        context.update("commonMessageDialogForm");
        context.execute("commonMessageDialog.show();");
    }

    public void displayCommonConfirmationDialog(DialogActionHandler dialogActionHandler,
            String dialogMessage,
            String dialogMessageHeader,
            String dialoMessageSeverity) {
        RequestContext context = RequestContext.getCurrentInstance();

        setDialogActionHandler(dialogActionHandler);

        setDialogRenderOkButton(false);
        setDialogRenderYesButton(true);
        setDialogRenderNoButton(true);
        setDialogRenderCancelButton(true);

        setDialogMessage(dialogMessage);
        setDialogMessageHeader(dialogMessageHeader);
        setDialogMessageSeverity(dialoMessageSeverity);

        context.update("commonMessageDialogForm");
        context.execute("commonMessageDialog.show();");
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

    public void addMessage(String summary, String detail, Severity severity) {
        FacesMessage message = new FacesMessage(severity, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
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

    public Boolean getCanEditJobCosting() {
        // Can edit if user belongs to the department to which the job was assigned        
        return (getCurrentJob().getDepartment().getId().longValue() == getUser().getEmployee().getDepartment().getId().longValue())
                || (getCurrentJob().getSubContractedDepartment().getId().longValue() == getUser().getEmployee().getDepartment().getId().longValue());
    }

    public String getSelectedJobCostingTemplate() {
        return selectedJobCostingTemplate;
    }

    public void setSelectedJobCostingTemplate(String selectedJobCostingTemplate) {
        this.selectedJobCostingTemplate = selectedJobCostingTemplate;
    }

    private Boolean isJobAssignedToUserDepartment() {

        if (getUser() != null) {
            if (currentJob.getDepartment().getId().longValue() == getUser().getEmployee().getDepartment().getId().longValue()) {
                return true;
            } else {
                return currentJob.getSubContractedDepartment().getId().longValue() == getUser().getEmployee().getDepartment().getId().longValue();
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

        int days = Integer.parseInt(SystemOption.findSystemOptionByName(em, "maxDaysPassInvoiceDate").getOptionValue());

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

    public StreamedContent getJobCostingAnalysisFile(EntityManager em) {

        HashMap parameters = new HashMap();

        try {
            parameters.put("jobId", getCurrentJob().getId());

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

            //prepareAndSaveCurrentJob(em);
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
        if (getUser().getEmployee().isMemberOf(getDepartmentBySystemOptionDeptId("invoicingDepartmentId"))) {
            if (!getCurrentJob().getJobCostingAndPayment().getCostingApproved()
                    && !getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
                getCurrentJob().getJobCostingAndPayment().setInvoiced(false);
                displayCommonMessageDialog(null, "This job costing cannot be marked as being invoiced because it is not prepared/approved", "Not prepared/Approved", "alert");
            } else {
                setJobCostingAndPaymentDirty(true);
            }
        } else {
            displayCommonMessageDialog(null, "You do not have permission to change the invoiced status of this job costing.", "Permission Denied", "alert");
            getCurrentJob().getJobCostingAndPayment().setInvoiced(!getCurrentJob().getJobCostingAndPayment().getInvoiced());
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
        return isJobAssignedToUserDepartment();
    }

    public CashPayment getSelectedCashPayment() {
        return selectedCashPayment;
    }

    public void setSelectedCashPayment(CashPayment selectedCashPayment) {
        this.selectedCashPayment = selectedCashPayment;
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

    public void updateJobCostingAndPayment() {
        setJobCostingAndPaymentDirty(true);
    }

    // tk rename this method
    public void updateAllJobCostings() {
        // Update all costs that depend on tax
        //editJobCosting();
        //updateJobCostings(); // tk needed here?
        updateJobCostingEstimate();

        setJobCostingAndPaymentDirty(true);
    }

    public Boolean getJobHasSubcontracts() {
        return currentJob.hasSubcontracts(getEntityManager1());
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

    public void update() {
        setDirty(true);
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
        EntityManager em = getEntityManager1();

        // Check for completed subcontracts if applicable
        if (!getCurrentJob().getIsSubContracted() && getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
            if (!Job.findIncompleteSubcontracts(em, currentJob).isEmpty()) {
                getCurrentJob().getJobCostingAndPayment().setCostingCompleted(false);
                displayCommonMessageDialog(null,
                        "This job costing cannot be marked prepared before all subcontracted jobs are completed", "Incomplete Subcontracts", "info");
            }
        } else if (getCurrentJob().getJobCostingAndPayment().getCostingApproved()) {
            getCurrentJob().getJobCostingAndPayment().setCostingCompleted(!getCurrentJob().getJobCostingAndPayment().getCostingCompleted());
            getCurrentJob().getJobCostingAndPayment().setCostingCompleted(false);
            displayCommonMessageDialog(null,
                    "The job costing preparation status cannot be changed because it was already approved.",
                    "Job Costing Already Approved", "info");
        } else if (!validateCurrentJobCosting()
                && getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
            getCurrentJob().getJobStatusAndTracking().setDateCostingCompleted(null);
            getCurrentJob().getJobCostingAndPayment().setCostingCompleted(false);
            getCurrentJob().getJobCostingAndPayment().setCostingApproved(false);
            //sendJobCostingCompletedEmail = false;
            displayCommonMessageDialog(null, "Please enter all required (*) fields before checking this job costing as being prepared.", "Required (*) Fields Missing", "info");
        } else if (getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
            getCurrentJob().getJobStatusAndTracking().setDateCostingCompleted(new Date());
            //sendJobCostingCompletedEmail = true;
            setJobCostingAndPaymentDirty(true);
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

        if (Department.findDepartmentAssignedToJob(foundJob, em).getHead().getId().longValue() == getUser().getEmployee().getId().longValue()) {
            return true;
        } else {
            return (Department.findDepartmentAssignedToJob(foundJob, em).getActingHead().getId().longValue() == getUser().getEmployee().getId().longValue())
                    && Department.findDepartmentAssignedToJob(foundJob, em).getActingHeadActive();
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
                //sendJobCostingApprovedEmail = false;
                displayCommonMessageDialog(null, "This job costing cannot be approved before it is prepared", "Cannot Approve", "info");
            } else {
                Date date = new Date();
                setJobCostingDate(date);
                getCurrentJob().getJobStatusAndTracking().setDateCostingApproved(date);
                //sendJobCostingApprovedEmail = true;
                setJobCostingAndPaymentDirty(true);
            }
        } else {
            setJobCostingDate(null);
            getCurrentJob().getJobCostingAndPayment().setCostingApproved(!getCurrentJob().getJobCostingAndPayment().getCostingApproved());
            displayCommonMessageDialog(null, "You do not have the permission to approve job costings.", "No Permission", "alert");
        }
    }

    public void updatePreferences() {
        setDirty(true);
    }

    // tk to be updated
    public void updateCostCode() {
        switch (selectedCostComponent.getCode()) {
            case "FIXED":
                selectedCostComponent.setIsFixedCost(true);
                selectedCostComponent.setIsHeading(false);
                break;
            case "HEADING":
                selectedCostComponent.setIsFixedCost(false);
                selectedCostComponent.setIsHeading(true);
                break;
            case "VARIABLE":
                selectedCostComponent.setIsFixedCost(false);
                selectedCostComponent.setIsHeading(false);
                break;
            case "SUBCONTRACT":
                selectedCostComponent.setIsFixedCost(true);
                selectedCostComponent.setIsHeading(false);
                break;
            default:
                selectedCostComponent.setIsFixedCost(false);
                selectedCostComponent.setIsHeading(false);
                break;
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

                displayCommonMessageDialog(null,
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

                displayCommonMessageDialog(null,
                        "The job costing needs to be prepared before this job can marked as completed.",
                        "Job Work Progress Cannot Be As Marked Completed", "info");

                return false;

            }
        } else {
            displayCommonMessageDialog(null,
                    "This job cannot be marked as completed because it is not yet saved.",
                    "Job Work Progress Cannot be Changed", "info");
            return false;
        }

        return true;
    }

    // tk to be modified
    public void closeJobCostingDialog() {
        // Redo search to reloasd stored jobs including
        // prompt to save modified job before attempting to create new job
        if (isJobCostingAndPaymentDirty()) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.update("jobCostingSaveConfirmDialogForm");
            context.execute("jobCostingSaveConfirm.show();");
        } else {
            RequestContext.getCurrentInstance().closeDialog(null);
        }
    }

    public void closeUnitCostDialog() {
        RequestContext context = RequestContext.getCurrentInstance();

        // prompt to save modified job before attempting to create new job
        if (isDirty()) {
            // ask to save         
            displayCommonConfirmationDialog(initDialogActionHandlerId("unitCostDirty"), "This unit cost was modified. Do you wish to save it?", "Unit Cost Not Saved", "info");
        } else {
            context.execute("unitCostDialog.hide();");
        }

    }

    public void cancelJobCostingEdit(ActionEvent actionEvent) {
        setDirty(false);
//        doJobSearch(searchManager.getCurrentSearchParameters());
//        setRenderJobDetailTab(false);        
        RequestContext.getCurrentInstance().closeDialog(null);
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

    // tk validation not done so change name?
    public void validateJobCostingAndSaveJob(ActionEvent actionEvent) {

        RequestContext context = RequestContext.getCurrentInstance();
        EntityManager em = getEntityManager1();

        try {

            // tk this may or should be done in the job costing dialog.
            currentJob.getJobCostingAndPayment().calculateAmountDue();

            if (getUser().getEmployee() != null) {
                currentJob.getJobCostingAndPayment().setFinalCostDoneBy(getUser().getEmployee().getName());
            }

            //prepareAndSaveCurrentJob(em);
            // Refresh to make sure job costings ids are not null to
            // avoid resaving newly created costing components
            // tk This is done so that newly added cost components are reloaded
            // with non-null ids. May have to implement JobCostingAndPayment.save()
            // that save cost compoents with null ids as is done for samples 
            // to avoid doing this and prevent cost component duplicates from being created.
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

    public void saveUnitCost() {
        EntityManager em = getEntityManager1();
        RequestContext context = RequestContext.getCurrentInstance();

        try {

            // Validate and save objects
            // Department
            Department department = Department.findDepartmentByName(em, getCurrentUnitCost().getDepartment().getName());
            if (department == null) {
                setInvalidFormFieldMessage("This unit cost cannot be saved because a valid department was not entered.");

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
                setInvalidFormFieldMessage("This unit cost cannot be saved because a valid service was not entered.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            }

            // Cost
            if (getCurrentUnitCost().getCost() <= 0.0) {
                setInvalidFormFieldMessage("This unit cost cannot be saved because a valid cost was not entered.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            }

            // Effective date
            if (getCurrentUnitCost().getEffectiveDate() == null) {
                setInvalidFormFieldMessage("This unit cost cannot be saved because a valid effective date was not entered.");
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

    public void editCashPayment(ActionEvent event) {
        System.out.println("Edit cash payment to be impl.");
    }

    public void editCostComponent(ActionEvent event) {
    }

    public void createNewCashPayment(ActionEvent event) {
        addCashPayment = true;
        selectedCashPayment = new CashPayment();
    }

    public void createNewCostComponent(ActionEvent event) {

        addCostComponent = true;
        selectedCostComponent = new CostComponent();
    }

    public void cancelCashPaymentEdit() {

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
        setJobCostingAndPaymentDirty(true);
    }

    public Boolean getCanApplyGCT() {
        return JobCostingAndPayment.getCanApplyGCT(getCurrentJob());
    }

    public void openJobCostingDialog() {

        PrimeFacesUtils.openDialog(null, "jobCostingDialog", true, true, true, 600, 800);
    }

    public void okCashPayment() {
        // tk
        // update jobCostingAndPayment.receiptNumber...append receipt number.
        // exec. jobManager.updateTotalDeposit(), jobManager.updateAmountDue() is exec by jobManager.updateTotalDeposit()?
        System.out.println("Ok cashpayment");

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

        setJobCostingAndPaymentDirty(true);
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

    public Job getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(Job currentJob) {
        this.currentJob = currentJob;
    }

    @Override
    public void setDirty(Boolean dirty) {
        getCurrentJob().getJobCostingAndPayment().setIsDirty(dirty);
    }

    @Override
    public Boolean isDirty() {
        return getCurrentJob().getJobCostingAndPayment().getIsDirty();
    }

    public void setJobCostingAndPaymentDirty(Boolean dirty) {
        getCurrentJob().getJobCostingAndPayment().setIsDirty(dirty);
    }

    public Boolean isJobCostingAndPaymentDirty() {
        return getCurrentJob().getJobCostingAndPayment().getIsDirty();
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

        accPacCustomer = AccPacCustomer.findByName(em, accPacCustomer.getCustomerName().trim());

        if (accPacCustomer != null) {
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

//    public void createSampleBasedJobCostings(JobCostingAndPayment jobCostingAndPayment) {
//        if (currentJob.getJobCostingAndPayment().getAllSortedCostComponents().isEmpty()) {
//            // Add all existing samples as cost oomponents            
//            for (JobSample jobSample : currentJob.getJobSamples()) {
//                jobCostingAndPayment.getAllSortedCostComponents().add(new CostComponent(jobSample.getDescription()));
//            }
//        } else if (currentJob.getJobSamples().size() > currentJob.getJobCostingAndPayment().getAllSortedCostComponents().size()) {
//        }
//    }
//    public void createDefaultJobCostings(JobCostingAndPayment jobCostingAndPayment) {
//
//        if (currentJob.getJobCostingAndPayment().getCostComponents().isEmpty()) {
//            jobCostingAndPayment.getCostComponents().add(new CostComponent("List of Assessments", Boolean.TRUE));
//            jobCostingAndPayment.getCostComponents().add(new CostComponent(""));
//        }
//
//    }
    public Date getCurrentDate() {
        return new Date();
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
        return BusinessEntityUtils.deleteEntity(em, cashPayment);
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

    // tk delete if not needed
    public void doUnitCostSearch() {
        RequestContext context = RequestContext.getCurrentInstance();

        unitCosts = UnitCost.findUnitCosts(getEntityManager1(), getUnitCostDepartment().getName(), getSearchText());
        context.update("unitCostsTableForm");
    }

    // tk delete if not needed
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

    /**
     * This is to be implemented further
     *
     * @return
     */
    public Boolean getDisableSubContracting() {
        try {
            if (getCurrentJob().getIsSubContracted() || getCurrentJob().getIsToBeCopied()) {
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

//    public Department getDepartmentAssignedToJob(Job job, EntityManager em) {
//
//        Department dept;
//
//        if (job.getSubContractedDepartment().getName().equals("--")
//                || job.getSubContractedDepartment().getName().equals("")) {
//            // This is not a subcontracted job see return to parent department            
//            dept = Department.findDepartmentByName(em, job.getDepartment().getName());
//            if (dept != null) {
//                em.refresh(dept);
//            }
//
//            return dept;
//        } else {
//            dept = Department.findDepartmentByName(em, job.getSubContractedDepartment().getName());
//            em.refresh(dept);
//
//            return dept;
//        }
//    }
    public Boolean isCurrentJobNew() {
        return (getCurrentJob().getId() == null);
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

    public List<SelectItem> getGCTPercentages() { // tk put in a costing entity
        ArrayList percentages = new ArrayList();

        percentages.addAll(Application.getStringListAsSelectItems(getEntityManager1(), "GCTPercentageList"));

        return percentages;
    }

}
