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
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.AccountingCode;
import jm.com.dpbennett.business.entity.Address;
import jm.com.dpbennett.business.entity.Contact;
import jm.com.dpbennett.business.entity.Internet;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.Supplier;
import org.primefaces.event.CellEditEvent;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.wal.utils.FinancialUtils;
import jm.com.dpbennett.wal.utils.MainTabView;
import jm.com.dpbennett.wal.utils.PrimeFacesUtils;
import jm.com.dpbennett.wal.validator.AddressValidator;
import jm.com.dpbennett.wal.validator.ContactValidator;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Desmond Bennett
 */
public class FinanceManager implements Serializable {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    @PersistenceUnit(unitName = "AccPacPU")
    private EntityManagerFactory EMF2;
    private Integer longProcessProgress;
    private AccountingCode selectedAccountingCode;
    private Supplier selectedSupplier;
    private Contact selectedSupplierContact;
    private Address selectedSupplierAddress;
    private Boolean edit;
    private String supplierSearchText;
    private String accountingCodeSearchText;
    private Boolean isActiveSuppliersOnly;
    private List<AccountingCode> foundAccountingCodes;
    private List<Supplier> foundSuppliers;
    private MainTabView mainTabView;
    private JobManagerUser user;

    /**
     * Creates a new instance of JobManagerBean
     */
    public FinanceManager() {
        init();
    } 
        
    public void openSuppliersTab() {
        mainTabView.openTab("Suppliers");
    }
    
    public void saveSelectedAccountingCode() {

        selectedAccountingCode.save(getEntityManager1());

        PrimeFaces.current().dialog().closeDynamic(null);

    }
    
    public List getAccountingCodeTypes() {
        ArrayList valueTypes = new ArrayList();

        valueTypes.add(new SelectItem("Distribution Code", "Distribution Code"));
        valueTypes.add(new SelectItem("Revenue Account", "Revenue Account"));
        valueTypes.add(new SelectItem("General", "General"));

        return valueTypes;
    }
    
    public void editAccountingCode() {
        PrimeFacesUtils.openDialog(null, "accountingCodeDialog", true, true, true, 0, 500);
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

    public String getAccountingCodeSearchText() {
        return accountingCodeSearchText;
    }

    public void setAccountingCodeSearchText(String accountingCodeSearchText) {
        this.accountingCodeSearchText = accountingCodeSearchText;
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

    public void createNewAccountingCode() {

        selectedAccountingCode = new AccountingCode();

        PrimeFacesUtils.openDialog(null, "accountingCodeDialog", true, true, true, 0, 500);
    }

    public AccountingCode getSelectedAccountingCode() {
        return selectedAccountingCode;
    }

    public void setSelectedAccountingCode(AccountingCode selectedAccountingCode) {
        this.selectedAccountingCode = selectedAccountingCode;
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

    public void closeDialog() {
        PrimeFacesUtils.closeDialog(null);
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

    public int getNumOfSuppliersFound() {
        return getFoundSuppliers().size();
    }

    public void editSelectedSupplier() {

        getSelectedSupplier().setIsNameAndIdEditable(getUser().getPrivilege().getCanAddSupplier());

        PrimeFacesUtils.openDialog(null, "supplierDialog", true, true, true, 450, 700);
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

    public String getSupplierSearchText() {
        return supplierSearchText;
    }

    public void setSupplierSearchText(String supplierSearchText) {
        this.supplierSearchText = supplierSearchText;
    }

//    public Boolean getIsSupplierNameAndIdEditable() {
//        return isSupplierNameAndIdEditable;
//    }
//
//    public void setIsSupplierNameAndIdEditable(Boolean isSupplierNameAndIdEditable) {
//        this.isSupplierNameAndIdEditable = isSupplierNameAndIdEditable;
//    }
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
    }

    public Boolean getIsNewSupplier() {
        return getSelectedSupplier().getId() == null;
    }

    public void cancelSupplierEdit(ActionEvent actionEvent) {

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
        accountingCodeSearchText = "";
        supplierSearchText = "";
        foundSuppliers = new ArrayList<>();
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

}
