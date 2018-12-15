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
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.CostComponent;
import jm.com.dpbennett.business.entity.DatePeriod;
import jm.com.dpbennett.business.entity.Department;
import jm.com.dpbennett.business.entity.Employee;
import jm.com.dpbennett.business.entity.EmployeePosition;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.PurchaseRequisition;
import jm.com.dpbennett.business.entity.Supplier;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.business.entity.utils.ReturnMessage;
import jm.com.dpbennett.wal.utils.BeanUtils;
import jm.com.dpbennett.wal.utils.DateUtils;
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
                    BusinessEntityUtils.getDateInMediumDateFormat(getSelectedPurchaseRequisition().
                            getExecutiveDirectorApprovalDate());
                default:
                    break;
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

            // Do not allow flagging PR as completed unless it is approved and the 
            // person is a procurement officer. 
            if (!getSelectedPurchaseRequisition().isApproved()
                    && !getUser().getEmployee().isProcurementOfficer()
                    && getSelectedPurchaseRequisition().getWorkProgress().equals("Completed")) {

                PrimeFacesUtils.addMessage("Work Progress Cannot Be As Marked Completed",
                        "You are not a procurement officer or this purchase requisition needs to be approved before it can marked as completed.",
                        FacesMessage.SEVERITY_WARN);

                return false;
            }

            if (getSelectedPurchaseRequisition().getWorkProgress().equals("Completed")
                    && !getUser().getPrivilege().getCanBeFinancialAdministrator()) {

                // Reset PR to its saved work progress
                getSelectedPurchaseRequisition().
                        setWorkProgress(savedPurchaseRequisition.getWorkProgress());

                PrimeFacesUtils.addMessage("Work Progress Cannot Be Changed",
                        "\"This purchase requisition is marked as completed and cannot be changed. You may contact a financial administrator for assistance.",
                        FacesMessage.SEVERITY_WARN);

                return false;
            } else if (getSelectedPurchaseRequisition().getWorkProgress().equals("Completed")
                    && getUser().getPrivilege().getCanBeFinancialAdministrator()) {
                // System admin can change work status even if it's completed.
                return true;
            } else if (!savedPurchaseRequisition.getWorkProgress().equals("Completed")
                    && getSelectedPurchaseRequisition().getWorkProgress().equals("Completed")
                    && !getSelectedPurchaseRequisition().isApproved()) {

                // Reset PR to its saved work progress
                getSelectedPurchaseRequisition().
                        setWorkProgress(savedPurchaseRequisition.getWorkProgress());

                PrimeFacesUtils.addMessage("Purchase Requisition Work Progress Cannot Be As Marked Completed",
                        "The purchase requisition needs to be approved before it can be marked as completed.",
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

                getSelectedPurchaseRequisition().setDateOfCompletion(null);

            } else if (getSelectedPurchaseRequisition().getWorkProgress().equals("Completed")) {

                getSelectedPurchaseRequisition().setDateOfCompletion(new Date());

                // Set the procurement officer and their department
                getSelectedPurchaseRequisition().
                        setProcurementOfficer(getUser().getEmployee());

                getSelectedPurchaseRequisition().
                        setPurchasingDepartment(getUser().getEmployee().getDepartment());
            }

            getSelectedPurchaseRequisition().setIsDirty(true);
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

    public void okCostingComponent() {
        if (selectedCostComponent.getId() == null && !getEdit()) {
            getSelectedPurchaseRequisition().getCostComponents().add(selectedCostComponent);
        }
        setEdit(false);

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

    public List getCostCodeList() {
        return FinancialUtils.getCostCodeList();
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
    }

    public void updateAutoGeneratePRNumber() {

        if (getSelectedPurchaseRequisition().getAutoGenerateNumber()) {
            getSelectedPurchaseRequisition().generateNumber();
        }
        getSelectedPurchaseRequisition().setIsDirty(true);

    }

    public void closeDialog() {
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

        ReturnMessage returnMessage;

        returnMessage = getSelectedPurchaseRequisition().prepareAndSave(getEntityManager1(), getUser());

        if (returnMessage.isSuccess()) {
            PrimeFacesUtils.addMessage("Saved!", "Purchase requisition was saved", FacesMessage.SEVERITY_INFO);
            getSelectedPurchaseRequisition().setEditStatus("");
        } else {
            PrimeFacesUtils.addMessage("Purchase requisition NOT Saved!",
                    "Purchase requisition was NOT saved. Please contact the System Administrator!",
                    FacesMessage.SEVERITY_ERROR);

            sendErrorEmail("An error occurred while saving a purchase requisition!",
                    "Purchase requisition number: " + getSelectedPurchaseRequisition().getNumber()
                    + "\nJMTS User: " + getUser().getUsername()
                    + "\nDate/time: " + new Date()
                    + "\nDetail: " + returnMessage.getDetail());
        }

    }

    public void sendErrorEmail(String subject, String message) {
        try {
            // Send error message to developer's email            
            Utils.postMail(null, null, subject, message);
        } catch (Exception ex) {
            System.out.println(ex);
        }
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
        }
        else {
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

    public void createNewPurhaseReqSupplier() {
        getFinanceManager().createNewSupplier();

        getFinanceManager().editSelectedSupplier();
    }

    public void editSelectedPurchaseReq() {

        PrimeFacesUtils.openDialog(null, "purchreqDialog", true, true, true, 600, 700);
    }

    public List<PurchaseRequisition> getFoundPurchaseReqs() {
        if (foundPurchaseReqs == null) {
            foundPurchaseReqs = new ArrayList<>();
        }
        return foundPurchaseReqs;
    }

    public void setFoundPurchaseReqs(List<PurchaseRequisition> foundPurchaseReqs) {
        this.foundPurchaseReqs = foundPurchaseReqs;
    }

    public void doPurchaseReqSearch() {

        doPurchaseReqSearch(dateSearchPeriod, searchType, purchaseReqSearchText,
        getUser().getEmployee().getDepartment().getId());
    }

    public void doPurchaseReqSearch(DatePeriod datePeriod,
            String searchType, String searchText, Long departmentId) {
        
        EntityManager em = getEntityManager1();

        if (!searchText.isEmpty()) {
            foundPurchaseReqs = PurchaseRequisition.findByDateSearchField(em,
                    datePeriod.getDateField(), searchType, searchText.trim(),
                    datePeriod.getStartDate(), datePeriod.getEndDate(), departmentId);
        } else {
            foundPurchaseReqs = PurchaseRequisition.findByDateSearchField(em,
                    datePeriod.getDateField(), searchType, "",
                    datePeriod.getStartDate(), datePeriod.getEndDate(), departmentId);
        }

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
        foundPurchaseReqs = new ArrayList<>();
        searchType = "Purchase requisitions";
       dateSearchPeriod = new DatePeriod("This year", "year",
                "requisitionDate", null, null, null, false, false, false);
        dateSearchPeriod.initDatePeriod();
        purchaseReqSearchText = "";
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

    public void updateCostCode() {
        switch (selectedCostComponent.getCode()) {
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
            if (null == selectedCostComponent.getCode()) {
                return true;
            } else {
                switch (selectedCostComponent.getCode()) {
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

        if (!getSelectedPurchaseRequisition().getOriginator().
                equals(getUser().getEmployee())) {

            getSelectedPurchaseRequisition().getApprovers().add(getUser().getEmployee());
            setPRApprovalDate(getUser().getEmployee().getPositions());
        } else {
            PrimeFacesUtils.addMessage("Originator Cannot Approve",
                    "The originator cannot approve this purchase requisition.",
                    FacesMessage.SEVERITY_INFO);
        }
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
                    return;
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
