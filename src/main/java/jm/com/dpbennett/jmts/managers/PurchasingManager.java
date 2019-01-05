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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.BusinessEntity;
import jm.com.dpbennett.business.entity.CostComponent;
import jm.com.dpbennett.business.entity.DatePeriod;
import jm.com.dpbennett.business.entity.Department;
import jm.com.dpbennett.business.entity.Email;
import jm.com.dpbennett.business.entity.Employee;
import jm.com.dpbennett.business.entity.EmployeePosition;
import jm.com.dpbennett.business.entity.Job;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.PurchaseRequisition;
import jm.com.dpbennett.business.entity.Supplier;
import jm.com.dpbennett.business.entity.SystemOption;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.business.entity.utils.ReturnMessage;
import jm.com.dpbennett.wal.utils.BeanUtils;
import jm.com.dpbennett.wal.utils.FinancialUtils;
import jm.com.dpbennett.wal.utils.MainTabView;
import jm.com.dpbennett.wal.utils.PrimeFacesUtils;
import jm.com.dpbennett.wal.utils.Utils;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Desmond Bennett
 */
public class PurchasingManager implements Serializable {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    @PersistenceUnit(unitName = "AccPacPU")
    private EntityManagerFactory EMF2;
    private Integer longProcessProgress;
    private CostComponent selectedCostComponent;
    private PurchaseRequisition selectedPurchaseRequisition;
    private Employee selectedApprover;
    private Boolean edit;
    private String purchaseReqSearchText;
    private List<PurchaseRequisition> foundPurchaseReqs;
    private MainTabView mainTabView;
    private JobManagerUser user;
    private FinanceManager financeManager;
    private String searchType;
    private DatePeriod dateSearchPeriod;
    private Long searchDepartmentId;

    /**
     * Creates a new instance of JobManagerBean
     */
    public PurchasingManager() {
        init();
    }

    public void updateDateSearchField() {
        //doSearch();
    }

    public DatePeriod getDateSearchPeriod() {
        return dateSearchPeriod;
    }

    public void setDateSearchPeriod(DatePeriod dateSearchPeriod) {
        this.dateSearchPeriod = dateSearchPeriod;
    }

    public String getPRApprovalDate(List<EmployeePosition> positions) {
        for (EmployeePosition position : positions) {
            switch (position.getTitle()) {
                case "Team Leader":
                    return BusinessEntityUtils.getDateInMediumDateFormat(getSelectedPurchaseRequisition().
                            getTeamLeaderApprovalDate());
                case "Divisional Manager":
                    return BusinessEntityUtils.getDateInMediumDateFormat(getSelectedPurchaseRequisition().
                            getDivisionalManagerApprovalDate());
                case "Divisional Director":
                    return BusinessEntityUtils.getDateInMediumDateFormat(getSelectedPurchaseRequisition().
                            getDivisionalDirectorApprovalDate());
                case "Finance Manager":
                    return BusinessEntityUtils.getDateInMediumDateFormat(getSelectedPurchaseRequisition().
                            getFinanceManagerApprovalDate());
                case "Executive Director":
                    return BusinessEntityUtils.getDateInMediumDateFormat(getSelectedPurchaseRequisition().
                            getExecutiveDirectorApprovalDate());
                default:
                    return BusinessEntityUtils.getDateInMediumDateFormat(getSelectedPurchaseRequisition().
                            getApprovalDate());
            }
        }

        return "";
    }

    public Employee getSelectedApprover() {
        return selectedApprover;
    }

    public void setSelectedApprover(Employee selectedApprover) {
        this.selectedApprover = selectedApprover;
    }

    public Boolean checkPRWorkProgressReadinessToBeChanged() {
        EntityManager em = getEntityManager1();

        // Find the currently stored PR and check it's work status
        if (getSelectedPurchaseRequisition().getId() != null) {

            PurchaseRequisition savedPurchaseRequisition
                    = PurchaseRequisition.findById(em, getSelectedPurchaseRequisition().getId());

            if (savedPurchaseRequisition != null) {
                if (!getUser().getEmployee().isProcurementOfficer()
                        && !getSelectedPurchaseRequisition().getWorkProgress().equals("Completed")
                        && savedPurchaseRequisition.getWorkProgress().equals("Completed")) {
                    PrimeFacesUtils.addMessage("Procurement Officer Required",
                            "You are not a procurement officer so you cannot change the completion status of this purchase requisition.",
                            FacesMessage.SEVERITY_WARN);

                    return false;
                }
            }

            // Procurement officer is required to approve PRs
            if (!getUser().getEmployee().isProcurementOfficer()
                    && getSelectedPurchaseRequisition().getWorkProgress().equals("Completed")) {

                PrimeFacesUtils.addMessage("Procurement Officer Required",
                        "You are not a procurement officer so you cannot mark this purchase requisition as completed.",
                        FacesMessage.SEVERITY_WARN);

                return false;
            }

            // Do not allow flagging PR as completed unless it is approved             
            if (!getSelectedPurchaseRequisition().isApproved(2) // tk required num. to be made system option
                    && getSelectedPurchaseRequisition().getWorkProgress().equals("Completed")) {

                PrimeFacesUtils.addMessage("Purchase Requisition Not Approved",
                        "This purchase requisition is NOT approved so it cannot be marked as completed.",
                        FacesMessage.SEVERITY_WARN);

                return false;
            }

        } else {

            PrimeFacesUtils.addMessage("Purchase Requisition Work Progress Cannot be Changed",
                    "This purchase requisition's work progress cannot be changed until it is saved.",
                    FacesMessage.SEVERITY_WARN);
            return false;
        }

        return true;
    }

    public void updateWorkProgress() {

        if (checkPRWorkProgressReadinessToBeChanged()) {
            if (!getSelectedPurchaseRequisition().getWorkProgress().equals("Completed")) {

                selectedPurchaseRequisition.setPurchasingDepartment(
                        Department.findDefaultDepartment(getEntityManager1(),
                                "--"));
                selectedPurchaseRequisition.setProcurementOfficer(
                        Employee.findDefaultEmployee(getEntityManager1(),
                                "--", "--", false));
                getSelectedPurchaseRequisition().setDateOfCompletion(null);

                getSelectedPurchaseRequisition().setPurchaseOrderDate(null);

            } else if (getSelectedPurchaseRequisition().getWorkProgress().equals("Completed")) {

                getSelectedPurchaseRequisition().setDateOfCompletion(new Date());

                getSelectedPurchaseRequisition().setPurchaseOrderDate(new Date());

                // Set the procurement officer and their department
                getSelectedPurchaseRequisition().
                        setProcurementOfficer(getUser().getEmployee());

                getSelectedPurchaseRequisition().
                        setPurchasingDepartment(getUser().getEmployee().getDepartment());

                updatePurchaseReq(null);

                getSelectedPurchaseRequisition().addAction(BusinessEntity.Action.COMPLETE);
            }

        } else {
            if (getSelectedPurchaseRequisition().getId() != null) {
                // Reset work progress to the currently saved state
                PurchaseRequisition foundPR = PurchaseRequisition.findById(getEntityManager1(),
                        getSelectedPurchaseRequisition().getId());
                if (foundPR != null) {
                    getSelectedPurchaseRequisition().setWorkProgress(foundPR.getWorkProgress());
                } else {
                    getSelectedPurchaseRequisition().setWorkProgress("Ongoing");
                }
            } else {
                getSelectedPurchaseRequisition().setWorkProgress("Ongoing");
            }
        }

    }

    public void deleteCostComponent() {
        deleteCostComponentByName(selectedCostComponent.getName());
    }

    public void deleteSelectedPRApprover() {
        deleteApproverByName(selectedApprover.getName());
    }

    public void deleteApproverByName(String approverName) {

        List<Employee> employees = getSelectedPurchaseRequisition().getApprovers();
        int index = 0;
        for (Employee employee : employees) {
            if (employee.getName().equals(approverName)) {
                employees.remove(index);
                removePRApprovalDate(employee.getPositions());
                getSelectedPurchaseRequisition().setIsDirty(true);
                getSelectedPurchaseRequisition().addAction(BusinessEntity.Action.EDIT);

                break;
            }
            ++index;
        }
    }

    public void okCostingComponent() {
        if (selectedCostComponent.getId() == null && !getEdit()) {
            getSelectedPurchaseRequisition().getCostComponents().add(selectedCostComponent);
        }
        setEdit(false);

        if (getSelectedCostComponent().getIsDirty()) {
            updatePurchaseReq(null);
        }

        PrimeFaces.current().executeScript("PF('purchreqCostingCompDialog').hide();");
    }

    public void openPurchaseReqsTab() {
        mainTabView.openTab("Purchase Requisitions");
    }

    /**
     * Get FinanceManager SessionScoped bean.
     *
     * @return
     */
    public FinanceManager getFinanceManager() {
        if (financeManager == null) {
            financeManager = BeanUtils.findBean("financeManager");
        }

        return financeManager;
    }

    public List getCostTypeList() {
        return FinancialUtils.getCostTypeList();
    }

    public Boolean getIsSupplierNameValid() {
        return BusinessEntityUtils.validateName(selectedPurchaseRequisition.getSupplier().getName());
    }

    public StreamedContent getPurchaseReqFile() {
        StreamedContent streamContent = null;

        try {

            // tk impl get PR form
            //streamContent = getContractManager().getServiceContractStreamContent();
            setLongProcessProgress(100);

        } catch (Exception e) {
            System.out.println(e);
            setLongProcessProgress(0);
        }

        return streamContent;
    }

    public StreamedContent getPurchaseOrderFile() {
        StreamedContent streamContent = null;

        try {

            // tk impl get PO form
            //streamContent = getContractManager().getServiceContractStreamContent();
            setLongProcessProgress(100);

        } catch (Exception e) {
            System.out.println(e);
            setLongProcessProgress(0);
        }

        return streamContent;
    }

    public void updatePurchaseReq(AjaxBehaviorEvent event) {
        getSelectedPurchaseRequisition().setIsDirty(true);
        getSelectedPurchaseRequisition().setEditStatus("(edited)");

        getSelectedPurchaseRequisition().addAction(BusinessEntity.Action.EDIT);

    }

    public void updateAutoGeneratePRNumber() {

        if (getSelectedPurchaseRequisition().getAutoGenerateNumber()) {
            getSelectedPurchaseRequisition().generateNumber();
        }

        updatePurchaseReq(null);
    }

    public void closeDialog() {

    }

    public void closePurchaseReqDialog() {
        PrimeFacesUtils.closeDialog(null);
    }

    public Boolean getIsSelectedPurchaseReqIsValid() {
        return getSelectedPurchaseRequisition().getId() != null
                && !getSelectedPurchaseRequisition().getIsDirty();
    }

    public PurchaseRequisition getSelectedPurchaseRequisition() {
        if (selectedPurchaseRequisition == null) {
            selectedPurchaseRequisition = new PurchaseRequisition();
        }
        return selectedPurchaseRequisition;
    }

    public void setSelectedPurchaseRequisition(PurchaseRequisition selectedPurchaseRequisition) {
        this.selectedPurchaseRequisition = selectedPurchaseRequisition;
    }

    public void saveSelectedPurchaseRequisition() {

        if (getSelectedPurchaseRequisition().getIsDirty()) {
            ReturnMessage returnMessage;

            returnMessage = getSelectedPurchaseRequisition().prepareAndSave(getEntityManager1(), getUser());

            if (returnMessage.isSuccess()) {
                PrimeFacesUtils.addMessage("Saved!", "Purchase requisition was saved", FacesMessage.SEVERITY_INFO);
                getSelectedPurchaseRequisition().setEditStatus("");

            } else {
                PrimeFacesUtils.addMessage(returnMessage.getHeader(),
                        returnMessage.getMessage(),
                        FacesMessage.SEVERITY_ERROR);

                Utils.sendErrorEmail("An error occurred while saving a purchase requisition! - "
                        + returnMessage.getHeader(),
                        "Purchase requisition number: " + getSelectedPurchaseRequisition().getNumber()
                        + "\nJMTS User: " + getUser().getUsername()
                        + "\nDate/time: " + new Date()
                        + "\nDetail: " + returnMessage.getDetail(),
                        getEntityManager1());
            }
        } else {
            PrimeFacesUtils.addMessage("Already Saved",
                    "This purchase requisition was not saved because it was not modified or it was recently saved.",
                    FacesMessage.SEVERITY_INFO);
        }

    }

    private void emailProcurementOfficers(String action) {
        EntityManager em = getEntityManager1();

        List<Employee> procurementOfficers = Employee.
                findActiveEmployeesByPosition(em,
                        "Procurement Officer");

        for (Employee procurementOfficer : procurementOfficers) {
            JobManagerUser procurementOfficerUser
                    = JobManagerUser.findActiveJobManagerUserByEmployeeId(
                            em, procurementOfficer.getId());

            if (!getUser().equals(procurementOfficerUser)) {
                sendPurchaseReqEmail(em, procurementOfficerUser,
                        "a procurement officer", action);
            }

        }
    }
    
    private void emailPurchaseReqApprovers(String action) {
        EntityManager em = getEntityManager1();


        for (Employee approver : getSelectedPurchaseRequisition().getApprovers()) {
            JobManagerUser approverUser
                    = JobManagerUser.findActiveJobManagerUserByEmployeeId(
                            em, approver.getId());

            if (!getUser().equals(approverUser)) {
                sendPurchaseReqEmail(em, approverUser,
                        "an approver", action);
            }

        }
    }

    private void emailDepartmentRepresentatives(String action) {
        EntityManager em = getEntityManager1();

        JobManagerUser originatorUser = JobManagerUser.
                findActiveJobManagerUserByEmployeeId(em, 
                        getSelectedPurchaseRequisition().getOriginator().getId());
        Employee head = getSelectedPurchaseRequisition().getOriginatingDepartment().getHead();
        Employee actingHead = getSelectedPurchaseRequisition().getOriginatingDepartment().getActingHead();
        JobManagerUser headUser = JobManagerUser.findActiveJobManagerUserByEmployeeId(em, head.getId());
        JobManagerUser actingHeadUser = JobManagerUser.findActiveJobManagerUserByEmployeeId(em, actingHead.getId());

        // Send to originator
        if (!getUser().equals(originatorUser)) {
            sendPurchaseReqEmail(em, originatorUser, "the orginator", action);
        }

        // Send to department head
        if (!getUser().equals(headUser)) {
            sendPurchaseReqEmail(em, headUser, "a department head", action);
        }

        // Send to acting head if active.
        if (!getUser().equals(actingHeadUser)) {
            if (getSelectedPurchaseRequisition().getOriginatingDepartment().getActingHeadActive()) {
                sendPurchaseReqEmail(em, actingHeadUser, "an acting department head", action);
            }
        }
    }

    private void sendPurchaseReqEmail(
            EntityManager em,
            JobManagerUser user,
            String role,
            String action) {

        Email email = Email.findActiveEmailByName(em, "pr-email-template");
        String prNum = getSelectedPurchaseRequisition().getNumber();
        String department = getSelectedPurchaseRequisition().
                getOriginatingDepartment().getName();
        String JMTSURL = (String) SystemOption.getOptionValueObject(em, "appURL");
        String originator = getSelectedPurchaseRequisition().getOriginator().getFirstName()
                + " " + getSelectedPurchaseRequisition().getOriginator().getLastName();
        String requisitionDate = BusinessEntityUtils.
                getDateInMediumDateFormat(getSelectedPurchaseRequisition().getRequisitionDate());
        String description = getSelectedPurchaseRequisition().getDescription();

        Utils.postMail(null,
                user,
                email.getSubject().
                        replace("{action}", action).
                        replace("{purchaseRequisitionNumber}", prNum),
                email.getContent("/correspondences/").
                        replace("{title}",
                                user.getEmployee().getTitle()).
                        replace("{surname}",
                                user.getEmployee().getLastName()).
                        replace("{JMTSURL}", JMTSURL).
                        replace("{purchaseRequisitionNumber}", prNum).
                        replace("{originator}", originator).
                        replace("{department}", department).
                        replace("{requisitionDate}", requisitionDate).
                        replace("{role}", role).
                        replace("{action}", action).
                        replace("{description}", description),
                email.getContentType(),
                em);
    }

    private synchronized void processPurchaseReqActions() {

        // tk
        System.out.println("Processing: " + getSelectedPurchaseRequisition().getActions());

        for (BusinessEntity.Action action : getSelectedPurchaseRequisition().getActions()) {
            switch (action) {
                case CREATE:
                    emailProcurementOfficers("created");
                    emailDepartmentRepresentatives("created");
                    emailPurchaseReqApprovers("created");
                    break;
                case EDIT:
                    emailProcurementOfficers("edited");
                    emailDepartmentRepresentatives("edited");
                    emailPurchaseReqApprovers("edited");
                    break;
                case APPROVE:
                    emailProcurementOfficers("approved");
                    emailDepartmentRepresentatives("approved");
                    emailPurchaseReqApprovers("approved");
                    break;
                case COMPLETE:
                    emailProcurementOfficers("completed");
                    emailDepartmentRepresentatives("completed");
                    emailPurchaseReqApprovers("completed");
                    break;
                default:
                    break;
            }
        }

        getSelectedPurchaseRequisition().getActions().clear();

    }

    public void onPurchaseReqCellEdit(CellEditEvent event) {
        BusinessEntityUtils.saveBusinessEntityInTransaction(getEntityManager1(),
                getFoundPurchaseReqs().get(event.getRowIndex()));
    }

    public int getNumOfPurchaseReqsFound() {
        return getFoundPurchaseReqs().size();
    }

    public String getPurchaseReqsTableHeader() {
        if (getUser().getPrivilege().getCanBeFinancialAdministrator()) {
            return "Search Results (found: " + getNumOfPurchaseReqsFound() + ")";
        } else {
            return "Search Results (found: " + getNumOfPurchaseReqsFound() + " for "
                    + getUser().getEmployee().getDepartment() + ")";
        }
    }

    public void editPurhaseReqSuppier() {
        getFinanceManager().setSelectedSupplier(getSelectedPurchaseRequisition().getSupplier());

        getFinanceManager().editSelectedSupplier();
    }

    public void purchaseReqSupplierDialogReturn() {
        if (getFinanceManager().getSelectedSupplier().getId() != null) {
            getSelectedPurchaseRequisition().setSupplier(getFinanceManager().getSelectedSupplier());

        }
    }

    public void purchaseReqDialogReturn() {
        if (getSelectedPurchaseRequisition().getIsDirty()) {
            PrimeFacesUtils.addMessage("Purchase requisition NOT saved",
                    "The recently edited purchase requisition was not saved",
                    FacesMessage.SEVERITY_WARN);
            PrimeFaces.current().ajax().update("headerForm:growl3");
            getSelectedPurchaseRequisition().setIsDirty(false);
        } else {
            doPurchaseReqSearch();
            // Process actions performed during the editing of the saved PR.
            if (getSelectedPurchaseRequisition().getId() != null) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            processPurchaseReqActions();
                        } catch (Exception e) {
                            System.out.println("Error processing PR actions: " + e);
                        }
                    }

                }.start();
            }
        }
    }

    public void createNewPurhaseReqSupplier() {
        getFinanceManager().createNewSupplier();

        getFinanceManager().editSelectedSupplier();
    }

    public void editSelectedPurchaseReq() {

        PrimeFacesUtils.openDialog(null, "purchreqDialog", true, true, true, false, 625, 700);
    }

    public List<PurchaseRequisition> getFoundPurchaseReqs() {
        if (foundPurchaseReqs == null) {
            doPurchaseReqSearch();
        }
        return foundPurchaseReqs;
    }

    public void setFoundPurchaseReqs(List<PurchaseRequisition> foundPurchaseReqs) {
        this.foundPurchaseReqs = foundPurchaseReqs;
    }

    public void doPurchaseReqSearch() {

        EntityManager em = getEntityManager1();

        if (!purchaseReqSearchText.isEmpty()) {
            foundPurchaseReqs = PurchaseRequisition.findByDateSearchField(em,
                    dateSearchPeriod.getDateField(), searchType, purchaseReqSearchText.trim(),
                    dateSearchPeriod.getStartDate(), dateSearchPeriod.getEndDate(),
                    searchDepartmentId);
        } else {
            foundPurchaseReqs = PurchaseRequisition.findByDateSearchField(em,
                    dateSearchPeriod.getDateField(), searchType, "",
                    dateSearchPeriod.getStartDate(), dateSearchPeriod.getEndDate(),
                    searchDepartmentId);
        }
    }

    public void doPurchaseReqSearch(DatePeriod dateSearchPeriod,
            String searchType, String searchText, Long searchDepartmentId) {

        this.dateSearchPeriod = dateSearchPeriod;
        this.searchType = searchType;
        this.purchaseReqSearchText = searchText;
        this.searchDepartmentId = searchDepartmentId;

        doPurchaseReqSearch();

    }

    public String getPurchaseReqSearchText() {
        return purchaseReqSearchText;
    }

    public void setPurchaseReqSearchText(String purchaseReqSearchText) {
        this.purchaseReqSearchText = purchaseReqSearchText;
    }

    public void createNewPurchaseReq() {
        selectedPurchaseRequisition = new PurchaseRequisition();
        selectedPurchaseRequisition.setPurchasingDepartment(Department.findDefaultDepartment(getEntityManager1(),
                "--"));
        selectedPurchaseRequisition.setProcurementOfficer(Employee.findDefaultEmployee(getEntityManager1(),
                "--", "--", false));
        selectedPurchaseRequisition.setSupplier(new Supplier("", true));
        selectedPurchaseRequisition.
                setOriginatingDepartment(getUser().getEmployee().getDepartment());
        selectedPurchaseRequisition.setOriginator(getUser().getEmployee());
        selectedPurchaseRequisition.setRequisitionDate(new Date());
        selectedPurchaseRequisition.generateNumber();
        selectedPurchaseRequisition.addAction(BusinessEntity.Action.CREATE);

        doPurchaseReqSearch(dateSearchPeriod,
                searchType,
                purchaseReqSearchText,
                getUser().getEmployee().getDepartment().getId());

        openPurchaseReqsTab();

        editSelectedPurchaseReq();
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

    public Boolean getEdit() {
        return edit;
    }

    public void setEdit(Boolean edit) {
        this.edit = edit;
    }

    private void init() {
        longProcessProgress = 0;
        selectedCostComponent = null;
        searchType = "Purchase requisitions";
        dateSearchPeriod = new DatePeriod("This year", "year",
                "requisitionDate", null, null, null, false, false, false);
        dateSearchPeriod.initDatePeriod();
        purchaseReqSearchText = "";
        foundPurchaseReqs = null;
    }

    public void reset() {
        init();
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

    public void onCostComponentSelect(SelectEvent event) {
        selectedCostComponent = (CostComponent) event.getObject();
    }

    public CostComponent getSelectedCostComponent() {
        return selectedCostComponent;
    }

    public void setSelectedCostComponent(CostComponent selectedCostComponent) {
        this.selectedCostComponent = selectedCostComponent;
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

    public EntityManager getEntityManager2() {
        return EMF2.createEntityManager();
    }

    public void updateSelectedCostComponent() {
        getSelectedCostComponent().setIsDirty(true);
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
            default:
                selectedCostComponent.setIsFixedCost(false);
                selectedCostComponent.setIsHeading(false);
                break;
        }

        updateSelectedCostComponent();

    }

    public Boolean getAllowCostEdit() {
        if (selectedCostComponent != null) {
            if (null == selectedCostComponent.getType()) {
                return true;
            } else {
                switch (selectedCostComponent.getType()) {
                    case "--":
                        return true;
                    default:
                        return false;
                }
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

    public void deleteSelectedCostComponent() {
        deleteCostComponentByName(selectedCostComponent.getName());
    }

    public void deleteCostComponentByName(String componentName) {

        List<CostComponent> components = getSelectedPurchaseRequisition().getAllSortedCostComponents();
        int index = 0;
        for (CostComponent costComponent : components) {
            if (costComponent.getName().equals(componentName)) {
                components.remove(index);
                getSelectedPurchaseRequisition().setIsDirty(true);

                break;
            }
            ++index;
        }
    }

    public void editCostComponent(ActionEvent event) {
        setEdit(true);
    }

    public void createNewCostComponent(ActionEvent event) {
        selectedCostComponent = new CostComponent();
        setEdit(false);
    }

    public void approveSelectedPurchaseRequisition(ActionEvent event) {

        // Check if the approver is already in the list of approvers
        if (BusinessEntityUtils.isBusinessEntityList(
                getSelectedPurchaseRequisition().getApprovers(),
                getUser().getEmployee().getId())) {

            PrimeFacesUtils.addMessage("Already Approved",
                    "You already approved this purchase requisition.",
                    FacesMessage.SEVERITY_INFO);

            return;

        }

        // Do not allow originator to approve
        if (getSelectedPurchaseRequisition().getOriginator().
                equals(getUser().getEmployee())) {

            PrimeFacesUtils.addMessage("Cannot Approve",
                    "The originator cannot approve this purchase requisition.",
                    FacesMessage.SEVERITY_WARN);

            return;

        }

        // Check if total cost is within the approver's limit
        if (isPRCostWithinApprovalLimit(getUser().getEmployee().getPositions())) {
            getSelectedPurchaseRequisition().getApprovers().add(getUser().getEmployee());
            setPRApprovalDate(getUser().getEmployee().getPositions());

            updatePurchaseReq(null);
            getSelectedPurchaseRequisition().addAction(BusinessEntity.Action.APPROVE);

        } else {

            PrimeFacesUtils.addMessage("Cannot Approve",
                    "You cannot approve this purchase requisition because the Total Cost is greater than your approval limit.",
                    FacesMessage.SEVERITY_WARN);

        }
    }

    private Boolean isPRCostWithinApprovalLimit(List<EmployeePosition> positions) {
        for (EmployeePosition position : positions) {
            if (position.getUpperApprovalLevel() >= getSelectedPurchaseRequisition().getTotalCost()) {
                return true;
            }
        }

        return false;
    }

    private void setPRApprovalDate(List<EmployeePosition> positions) {
        for (EmployeePosition position : positions) {
            switch (position.getTitle()) {
                case "Team Leader":
                    getSelectedPurchaseRequisition().setTeamLeaderApprovalDate(new Date());
                    return;
                case "Divisional Manager":
                    getSelectedPurchaseRequisition().setDivisionalManagerApprovalDate(new Date());
                    return;
                case "Divisional Director":
                    getSelectedPurchaseRequisition().setDivisionalDirectorApprovalDate(new Date());
                    return;
                case "Finance Manager":
                    getSelectedPurchaseRequisition().setFinanceManagerApprovalDate(new Date());
                    return;
                case "Executive Director":
                    getSelectedPurchaseRequisition().setExecutiveDirectorApprovalDate(new Date());
                    return;
                default:
                    getSelectedPurchaseRequisition().setApprovalDate(new Date());
                    return;
            }
        }
    }

    private void removePRApprovalDate(List<EmployeePosition> positions) {
        for (EmployeePosition position : positions) {
            switch (position.getTitle()) {
                case "Team Leader":
                    getSelectedPurchaseRequisition().setTeamLeaderApprovalDate(null);
                    break;
                case "Divisional Manager":
                    getSelectedPurchaseRequisition().setDivisionalManagerApprovalDate(null);
                    break;
                case "Divisional Director":
                    getSelectedPurchaseRequisition().setDivisionalDirectorApprovalDate(null);
                    break;
                case "Finance Manager":
                    getSelectedPurchaseRequisition().setFinanceManagerApprovalDate(null);
                    break;
                case "Executive Director":
                    getSelectedPurchaseRequisition().setExecutiveDirectorApprovalDate(null);
                    break;
                default:
                    break;
            }
        }
    }

    public void cancelCostComponentEdit() {
        selectedCostComponent.setIsDirty(false);
    }

    private EntityManagerFactory getEMF2() {
        return EMF2;
    }

    public List<CostComponent> copyCostComponents(List<CostComponent> srcCostComponents) {
        ArrayList<CostComponent> newCostComponents = new ArrayList<>();

        for (CostComponent costComponent : srcCostComponents) {
            CostComponent newCostComponent = new CostComponent(costComponent);
            newCostComponents.add(newCostComponent);
        }

        return newCostComponents;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

}
