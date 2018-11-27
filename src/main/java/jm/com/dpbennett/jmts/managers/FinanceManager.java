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
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.AccountingCode;
import jm.com.dpbennett.business.entity.Address;
import jm.com.dpbennett.business.entity.Contact;
import jm.com.dpbennett.business.entity.CostComponent;
import jm.com.dpbennett.business.entity.Internet;
import jm.com.dpbennett.business.entity.Job;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.PurchaseRequisition;
import jm.com.dpbennett.business.entity.Supplier;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import jm.com.dpbennett.business.entity.management.BusinessEntityManagement;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.business.entity.utils.ReturnMessage;
import jm.com.dpbennett.wal.utils.FinancialUtils;
import jm.com.dpbennett.wal.utils.MainTabView;
import jm.com.dpbennett.wal.utils.PrimeFacesUtils;
import jm.com.dpbennett.wal.validator.AddressValidator;
import jm.com.dpbennett.wal.validator.ContactValidator;
import org.primefaces.PrimeFaces;
import org.primefaces.component.selectonemenu.SelectOneMenu;

/**
 *
 * @author Desmond Bennett
 */
public class FinanceManager implements Serializable, BusinessEntityManagement {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    @PersistenceUnit(unitName = "AccPacPU")
    private EntityManagerFactory EMF2;
    private Integer longProcessProgress;
    private CostComponent selectedCostComponent;
    private AccountingCode selectedAccountingCode;
    private Supplier selectedSupplier;
    private PurchaseRequisition selectedPurchaseRequisition;
    private Contact selectedContact;
    private Address selectedAddress;
    private String accountingCodeSearchText;
    private Boolean edit;
    private List<AccountingCode> foundAccountingCodes;
    private Boolean isSupplierNameAndIdEditable;
    private String supplierSearchText;
    private String purchaseReqSearchText;
    private Boolean isActiveSuppliersOnly;
    private List<Supplier> foundSuppliers;
    private List<PurchaseRequisition> foundPurchaseReqs;
    private MainTabView mainTabView;
    private JobManagerUser user;

    /**
     * Creates a new instance of JobManagerBean
     */
    public FinanceManager() {
        init();
    }
    
    public List<Supplier> completeActiveSupplier(String query) {
        try {
            return Supplier.findActiveSuppliersByAnyPartOfName(getEntityManager1(), query);

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }
    
    public List getCostCodeList() {
        return FinancialUtils.getCostCodeList();
    }

    
    public void updateSupplier() {

        setIsDirty(true);
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
        setIsDirty(true);
    }

    public void updateNumber() {

        // tk impl auto number generation
        if (selectedPurchaseRequisition.getAutoGenerateNumber()) {
            //selectedPurchaseRequisition.setNumber();
        }
        setIsDirty(true);

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
        EntityManager em = getEntityManager1();
        ReturnMessage returnMessage;

        // tk
        System.out.println("Saving PR...");

    }

    public Contact getSelectedContact() {
        return selectedContact;
    }

    public void setSelectedContact(Contact selectedContact) {
        this.selectedContact = selectedContact;

        setEdit(true);
    }

    public Address getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(Address selectedAddress) {
        this.selectedAddress = selectedAddress;

        setEdit(true);
    }

    public void removeAddress() {
        getSelectedSupplier().getAddresses().remove(selectedAddress);
        setIsDirty(true);
        selectedAddress = null;
    }

    public void removeContact() {
        getSelectedSupplier().getContacts().remove(selectedContact);
        setIsDirty(true);
        selectedContact = null;
    }

    public Boolean getIsNewAddress() {
        return getSelectedAddress().getId() == null && !getEdit();
    }

    public void okAddress() {

        selectedAddress = selectedAddress.prepare();

        if (getIsNewAddress()) {
            getSelectedSupplier().getAddresses().add(selectedAddress);
        }

        PrimeFaces.current().executeScript("PF('addressFormDialog').hide();");

    }

    public void updateAddress() {
        setIsDirty(true);
    }

    public List<Address> getAddressesModel() {
        return getSelectedSupplier().getAddresses();
    }

    public List<Contact> getContactsModel() {
        return getSelectedSupplier().getContacts();
    }

    public void createNewAddress() {
        selectedAddress = null;

        // Find an existing invalid or blank address and use it as the neww address
        for (Address address : getSelectedSupplier().getAddresses()) {
            if (address.getAddressLine1().trim().isEmpty()) {
                selectedAddress = address;
                break;
            }
        }

        // No existing blank or invalid address found so creating new one.
        if (selectedAddress == null) {
            selectedAddress = new Address("", "Billing");
        }

        setEdit(false);

        setIsDirty(false);
    }

    public Boolean getIsNewContact() {
        return getSelectedContact().getId() == null && !getEdit();
    }

    public void okContact() {

        selectedContact = selectedContact.prepare();

        if (getIsNewContact()) {
            getSelectedSupplier().getContacts().add(selectedContact);
        }

        PrimeFaces.current().executeScript("PF('contactFormDialog').hide();");

    }

    public void updateContact() {
        setIsDirty(true);
    }

    public void createNewContact() {
        selectedContact = null;

        for (Contact contact : getSelectedSupplier().getContacts()) {
            if (contact.getFirstName().trim().isEmpty()) {
                selectedContact = contact;
                break;
            }
        }

        if (selectedContact == null) {
            selectedContact = new Contact("", "", "Main");
            selectedContact.setInternet(new Internet());
        }

        setEdit(false);

        setIsDirty(false);
    }

    public void updateSupplierName(AjaxBehaviorEvent event) {
        selectedSupplier.setName(selectedSupplier.getName().trim());

        setIsDirty(true);
    }

    public void onSupplierCellEdit(CellEditEvent event) {
        BusinessEntityUtils.saveBusinessEntityInTransaction(getEntityManager1(),
                getFoundSuppliers().get(event.getRowIndex()));
    }

    public void onPurchaseReqCellEdit(CellEditEvent event) {
        BusinessEntityUtils.saveBusinessEntityInTransaction(getEntityManager1(),
                getFoundPurchaseReqs().get(event.getRowIndex()));
    }

    public int getNumOfSuppliersFound() {
        return getFoundSuppliers().size();
    }

    public int getNumOfPurchaseReqsFound() {
        return getFoundPurchaseReqs().size();
    }

    public void editPurhaseReqSuppier() {
        setSelectedSupplier(getSelectedPurchaseRequisition().getSupplier());
        setIsSupplierNameAndIdEditable(getUser().getPrivilege().getCanAddSupplier());

        editSelectedSupplier();
    }

    public void supplierDialogReturn() {
        if (getSelectedSupplier().getId() != null) {
            getSelectedPurchaseRequisition().setSupplier(getSelectedSupplier());

            setIsDirty(true);
        }
    }

    public void createNewPurhaseReqSupplier() {
        createNewSupplier();
        setIsSupplierNameAndIdEditable(getUser().getPrivilege().getCanAddSupplier());

        editSelectedSupplier();
    }

    public void editSelectedSupplier() {

        PrimeFacesUtils.openDialog(null, "supplierDialog", true, true, true, 450, 700);
    }

    public void editSelectedPurchaseReq() {

        PrimeFacesUtils.openDialog(null, "purchreqDialog", true, true, true, 700, 700);
    }

    public Boolean getIsActiveSuppliersOnly() {
        if (isActiveSuppliersOnly == null) {
            isActiveSuppliersOnly = true;
        }
        return isActiveSuppliersOnly;
    }

    public List<Supplier> getFoundSuppliers() {
        return foundSuppliers;
    }

    public void setFoundSuppliers(List<Supplier> foundSuppliers) {
        this.foundSuppliers = foundSuppliers;
    }

    public List<PurchaseRequisition> getFoundPurchaseReqs() {
        return foundPurchaseReqs;
    }

    public void setFoundPurchaseReqs(List<PurchaseRequisition> foundPurchaseReqs) {
        this.foundPurchaseReqs = foundPurchaseReqs;
    }

    public void setIsActiveSuppliersOnly(Boolean isActiveSuppliersOnly) {
        this.isActiveSuppliersOnly = isActiveSuppliersOnly;
    }

    public void doSupplierSearch() {
        if (supplierSearchText.trim().length() > 1) {
            if (getIsActiveSuppliersOnly()) {
                foundSuppliers = Supplier.findActiveSuppliersByFirstPartOfName(getEntityManager1(), supplierSearchText);
            } else {
                foundSuppliers = Supplier.findSuppliersByFirstPartOfName(getEntityManager1(), supplierSearchText);
            }
        } else {
            foundSuppliers = new ArrayList<>();
        }
    }

    public void doPurchaseReqSearch() {
        System.out.println("PR search to be done using search parameters from dashboard.");
    }

    public String getSupplierSearchText() {
        return supplierSearchText;
    }

    public void setSupplierSearchText(String supplierSearchText) {
        this.supplierSearchText = supplierSearchText;
    }

    public String getPurchaseReqSearchText() {
        return purchaseReqSearchText;
    }

    public void setPurchaseReqSearchText(String purchaseReqSearchText) {
        this.purchaseReqSearchText = purchaseReqSearchText;
    }

    public Boolean getIsSupplierNameAndIdEditable() {
        return isSupplierNameAndIdEditable;
    }

    public void setIsSupplierNameAndIdEditable(Boolean isSupplierNameAndIdEditable) {
        this.isSupplierNameAndIdEditable = isSupplierNameAndIdEditable;
    }

    public Supplier getSelectedSupplier() {
        if (selectedSupplier == null) {
            return new Supplier("");
        }
        return selectedSupplier;
    }

    public void setSelectedSupplier(Supplier selectedSupplier) {
        this.selectedSupplier = selectedSupplier;
    }

    public Address getCurrentAddress() {
        return getSelectedSupplier().getDefaultAddress();
    }

    public Contact getCurrentContact() {
        return getSelectedSupplier().getDefaultContact();
    }

    public void editCurrentAddress() {
        selectedAddress = getCurrentAddress();
        setEdit(true);
    }

    public void createNewSupplier() {

        selectedSupplier = new Supplier("", true);

        setIsSupplierNameAndIdEditable(getUser().getPrivilege().getCanAddSupplier());

        PrimeFacesUtils.openDialog(null, "supplierDialog", true, true, true, 450, 700);
    }

    public void createNewPurchaseReq() {
        selectedPurchaseRequisition = new PurchaseRequisition();
        selectedPurchaseRequisition.setSupplier(new Supplier("", true));
        selectedPurchaseRequisition.
                setOriginatingDepartment(getUser().getEmployee().getDepartment());
        selectedPurchaseRequisition.setOriginator(getUser().getEmployee());
        selectedPurchaseRequisition.setRequisitionDate(new Date());

        editSelectedPurchaseReq();
    }

    public Boolean getIsNewSupplier() {
        return getSelectedSupplier().getId() == null;
    }

    public void cancelEdit(ActionEvent actionEvent) {

        setIsDirty(false);

        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void okSupplier() {
        Boolean hasValidAddress = false;
        Boolean hasValidContact = false;

        try {

            // Validate 
            // Check for a valid address
            for (Address address : selectedSupplier.getAddresses()) {
                hasValidAddress = hasValidAddress || AddressValidator.validate(address);
            }
            if (!hasValidAddress) {
                PrimeFacesUtils.addMessage("Address Required",
                        "A valid address was not entered for this supplier",
                        FacesMessage.SEVERITY_ERROR);

                return;
            }

            // Check for a valid contact
            for (Contact contact : selectedSupplier.getContacts()) {
                hasValidContact = hasValidContact || ContactValidator.validate(contact);
            }
            if (!hasValidContact) {
                PrimeFacesUtils.addMessage("Contact Required",
                        "A valid contact was not entered for this supplier",
                        FacesMessage.SEVERITY_ERROR);

                return;
            }

            // Update tracking
            if (getIsNewSupplier()) {
                getSelectedSupplier().setDateEntered(new Date());
                getSelectedSupplier().setDateEdited(new Date());
                if (getUser() != null) {
                    selectedSupplier.setEnteredBy(getUser().getEmployee());
                    selectedSupplier.setEditedBy(getUser().getEmployee());
                }
            }

            // Do save
            if (getIsDirty()) {
                getSelectedSupplier().setDateEdited(new Date());
                if (getUser() != null) {
                    selectedSupplier.setEditedBy(getUser().getEmployee());
                }
                selectedSupplier.save(getEntityManager1());
                setIsDirty(false);
            }

            PrimeFaces.current().dialog().closeDynamic(null);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void onAccountingCodeCellEdit(CellEditEvent event) {
        int index = event.getRowIndex();
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();

        try {
            if (newValue != null && !newValue.equals(oldValue)) {
                if (!newValue.toString().trim().equals("")) {
                    AccountingCode code = getFoundAccountingCodes().get(index);
                    code.save(getEntityManager1());
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public List<AccountingCode> getFoundAccountingCodes() {
        if (foundAccountingCodes == null) {
            foundAccountingCodes = AccountingCode.findAllAccountingCodes(getEntityManager1());
        }

        return foundAccountingCodes;
    }

    public void setFoundAccountingCodes(List<AccountingCode> foundAccountingCodes) {
        this.foundAccountingCodes = foundAccountingCodes;
    }

    public void doAccountingCodeSearch() {

        foundAccountingCodes = AccountingCode.findAccountingCodesByNameAndDescription(getEntityManager1(),
                getAccountingCodeSearchText());

        if (foundAccountingCodes == null) {
            foundAccountingCodes = new ArrayList<>();
        }
    }

    public List getAccountingCodeTypes() {
        ArrayList valueTypes = new ArrayList();

        valueTypes.add(new SelectItem("Distribution Code", "Distribution Code"));
        valueTypes.add(new SelectItem("General", "General"));

        return valueTypes;
    }

    public void cancelDialogEdit(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void saveSelectedAccountingCode() {

        selectedAccountingCode.save(getEntityManager1());

        PrimeFaces.current().dialog().closeDynamic(null);

    }    
   
    public void createNewAccountingCode() {

        selectedAccountingCode = new AccountingCode();

        PrimeFacesUtils.openDialog(null, "accountingCodeDialog", true, true, true, 0, 500);
    }

    public void editAccountingCode() {
        PrimeFacesUtils.openDialog(null, "accountingCodeDialog", true, true, true, 0, 500);
    }

    public MainTabView getMainTabView() {
        
        return mainTabView;
    }

    public void setMainTabView(MainTabView mainTabView) {
        this.mainTabView = mainTabView;
    }
    

    public AccountingCode getSelectedAccountingCode() {
        return selectedAccountingCode;
    }

    public void setSelectedAccountingCode(AccountingCode selectedAccountingCode) {
        this.selectedAccountingCode = selectedAccountingCode;
    }

    public List<AccountingCode> completeAccountingCode(String query) {
        EntityManager em;

        try {
            em = getEntityManager1();

            List<AccountingCode> accountingCodes
                    = AccountingCode.findAccountingCodesByNameAndDescription(em, query);

            return accountingCodes;

        } catch (Exception e) {
            return new ArrayList<>();
        }
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
        accountingCodeSearchText = "";
        isSupplierNameAndIdEditable = false; // tk put as transient in Client
        supplierSearchText = "";
        foundSuppliers = new ArrayList<>();
        foundPurchaseReqs = new ArrayList<>();
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
  
    public String getAccountingCodeSearchText() {
        return accountingCodeSearchText;
    }

    public void setAccountingCodeSearchText(String accountingCodeSearchText) {
        this.accountingCodeSearchText = accountingCodeSearchText;
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

    public void update() {
        setIsDirty(true);
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
        setIsDirty(true);
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

   

    public void deleteCostComponent() {
        deleteCostComponentByName(selectedCostComponent.getName());
    }

    // Remove this and other code out of JobManager? Put in JobCostingAndPayment or Job?
    public void deleteCostComponentByName(String componentName) {

        //List<CostComponent> components = getCurrentJob().getJobCostingAndPayment().getAllSortedCostComponents();
        int index = 0;
        //for (CostComponent costComponent : components) {
          //  if (costComponent.getName().equals(componentName)) {
                //components.remove(index);
                //setJobCostingAndPaymentDirty(true);

         //       break;
            }
        //    ++index;
        //}

    @Override
    public void setIsDirty(Boolean isDirty) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Boolean getIsDirty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

    public void editCostComponent(ActionEvent event) {
        setEdit(true);
    }

    public void createNewCostComponent(ActionEvent event) {
        selectedCostComponent = new CostComponent();
        setEdit(false);
    }

    public void cancelCostComponentEdit() {
        selectedCostComponent.setIsDirty(false);
    }

    public void okCostingComponent() {
        if (selectedCostComponent.getId() == null && !getEdit()) {
            //getCurrentJob().getJobCostingAndPayment().getCostComponents().add(selectedCostComponent);
        }
        setEdit(false);

        PrimeFaces.current().executeScript("PF('costingComponentDialog').hide();");
    }

    private EntityManagerFactory getEMF2() {
        return EMF2;
    }


    public void updateAccountingCode(SelectEvent event) {
        selectedAccountingCode = (AccountingCode) event.getObject();
        // tk
        System.out.println("selected jcp: " + selectedAccountingCode.getName());
    }

    public List<CostComponent> copyCostComponents(List<CostComponent> srcCostComponents) {
        ArrayList<CostComponent> newCostComponents = new ArrayList<>();

        for (CostComponent costComponent : srcCostComponents) {
            CostComponent newCostComponent = new CostComponent(costComponent);
            newCostComponents.add(newCostComponent);
        }

        return newCostComponents;
    }

}
