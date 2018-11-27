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
public class PurchasingManager implements Serializable/*, BusinessEntityManagement*/ {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    @PersistenceUnit(unitName = "AccPacPU")
    private EntityManagerFactory EMF2;
    private Integer longProcessProgress;
    private CostComponent selectedCostComponent;
    private AccountingCode selectedAccountingCode;
    private Supplier selectedSupplier;
    private PurchaseRequisition selectedPurchaseRequisition;
    private Contact selectedSupplierContact;
    private Address selectedSupplierAddress;
    private Boolean edit;
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
    public PurchasingManager() {
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
        getSelectedSupplier().setIsDirty(true);
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

    public void updatePurchaseReqNumber() {

        // tk impl auto number generation
        if (selectedPurchaseRequisition.getAutoGenerateNumber()) {
            //selectedPurchaseRequisition.setNumber();
        }

        selectedPurchaseRequisition.setIsDirty(true);

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
        System.out.println("Saving PR to be impl...");

    }

    public Contact getSelectedSupplierContact() {
        return selectedSupplierContact;
    }

    public void setSelectedSupplierContact(Contact selectedSupplierContact) {
        this.selectedSupplierContact = selectedSupplierContact;

        setEdit(true);
    }

    public Address getSelectedSupplierAddress() {
        return selectedSupplierAddress;
    }

    public void setSelectedSupplierAddress(Address selectedSupplierAddress) {
        this.selectedSupplierAddress = selectedSupplierAddress;

        setEdit(true);
    }

    public void removeSupplierAddress() {
        getSelectedSupplier().getAddresses().remove(selectedSupplierAddress);
        getSelectedSupplier().setIsDirty(true);
        selectedSupplierAddress = null;
    }

    public void removeSupplierContact() {
        getSelectedSupplier().getContacts().remove(selectedSupplierContact);
        getSelectedSupplier().setIsDirty(true);
        selectedSupplierContact = null;
    }

    public Boolean getIsNewSupplierAddress() {
        return getSelectedSupplierAddress().getId() == null && !getEdit();
    }

    public void okSupplierAddress() {

        selectedSupplierAddress = selectedSupplierAddress.prepare();

        if (getIsNewSupplierAddress()) {
            getSelectedSupplier().getAddresses().add(selectedSupplierAddress);
        }

        PrimeFaces.current().executeScript("PF('addressFormDialog').hide();");

    }

    public void updateSupplierAddress() {
        getSelectedSupplierAddress().setIsDirty(true);
        getSelectedSupplier().setIsDirty(true);
    }

    public List<Address> getSupplierAddressesModel() {
        return getSelectedSupplier().getAddresses();
    }

    public List<Contact> getSupplierContactsModel() {
        return getSelectedSupplier().getContacts();
    }

    public void createNewSupplierAddress() {
        selectedSupplierAddress = null;

        // Find an existing invalid or blank address and use it as the neww address
        for (Address address : getSelectedSupplier().getAddresses()) {
            if (address.getAddressLine1().trim().isEmpty()) {
                selectedSupplierAddress = address;
                break;
            }
        }

        // No existing blank or invalid address found so creating new one.
        if (selectedSupplierAddress == null) {
            selectedSupplierAddress = new Address("", "Billing");
        }

        setEdit(false);

        getSelectedSupplier().setIsDirty(false);
    }

    public Boolean getIsNewSupplierContact() {
        return getSelectedSupplierContact().getId() == null && !getEdit();
    }

    public void okContact() {

        selectedSupplierContact = selectedSupplierContact.prepare();

        if (getIsNewSupplierContact()) {
            getSelectedSupplier().getContacts().add(selectedSupplierContact);
        }

        PrimeFaces.current().executeScript("PF('contactFormDialog').hide();");

    }

    public void updateSupplierContact() {
        getSelectedSupplierContact().setIsDirty(true);
        getSelectedSupplier().setIsDirty(true);
    }

    public void createNewSupplierContact() {
        selectedSupplierContact = null;

        for (Contact contact : getSelectedSupplier().getContacts()) {
            if (contact.getFirstName().trim().isEmpty()) {
                selectedSupplierContact = contact;
                break;
            }
        }

        if (selectedSupplierContact == null) {
            selectedSupplierContact = new Contact("", "", "Main");
            selectedSupplierContact.setInternet(new Internet());
        }

        setEdit(false);

        getSelectedSupplier().setIsDirty(false);
    }

    public void updateSupplierName(AjaxBehaviorEvent event) {
        selectedSupplier.setName(selectedSupplier.getName().trim());

        getSelectedSupplier().setIsDirty(true);
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

    public Address getSupplierCurrentAddress() {
        return getSelectedSupplier().getDefaultAddress();
    }

    public Contact getSupplierCurrentContact() {
        return getSelectedSupplier().getDefaultContact();
    }

    public void editSupplierCurrentAddress() {
        selectedSupplierAddress = getSupplierCurrentAddress();
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

        getSelectedSupplier().setIsDirty(false);

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
            if (getSelectedSupplier().getIsDirty()) {
                getSelectedSupplier().setDateEdited(new Date());
                if (getUser() != null) {
                    selectedSupplier.setEditedBy(getUser().getEmployee());
                }
                selectedSupplier.save(getEntityManager1());
                getSelectedSupplier().setIsDirty(false);
            }

            PrimeFaces.current().dialog().closeDynamic(null);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void cancelDialogEdit(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void saveSelectedAccountingCode() {

        selectedAccountingCode.save(getEntityManager1());

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

        updateSelectedCostComponent();

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

    public void deleteSelectedCostComponent() {
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
        //}
        //    ++index;
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
