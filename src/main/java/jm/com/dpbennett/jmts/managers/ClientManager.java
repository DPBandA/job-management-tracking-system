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
import jm.com.dpbennett.business.entity.AccPacCustomer;
import jm.com.dpbennett.business.entity.Address;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.Contact;
import jm.com.dpbennett.business.entity.Discount;
import jm.com.dpbennett.business.entity.Internet;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.Tax;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.wal.utils.MainTabView;
import jm.com.dpbennett.wal.validator.AddressValidator;
import jm.com.dpbennett.wal.validator.ContactValidator;
import jm.com.dpbennett.wal.utils.PrimeFacesUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Desmond Bennett
 */
public class ClientManager implements Serializable {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    @PersistenceUnit(unitName = "AccPacPU")
    private EntityManagerFactory EMF2;
    private Boolean isClientNameAndIdEditable;
    private Boolean isActiveClientsOnly;
    private Client selectedClient;
    private Contact selectedContact;
    private Address selectedAddress;
    private String searchText;
    private List<Client> foundClients;
    private MainTabView mainTabView;
    private JobManagerUser user;
    private Boolean edit;

    /**
     * Creates a new instance of ClientManager
     */
    public ClientManager() {
        init();
    }
       
    public List<Client> completeActiveClient(String query) {
        try {
            return Client.findActiveClientsByAnyPartOfName(getEntityManager1(), query);

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }
    
    public List<Discount> completeDiscount(String query) {
        EntityManager em;

        try {
            em = getEntityManager1();

            List<Discount> discounts = Discount.findDiscountsByNameAndDescription(em, query);

            return discounts;

        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }
    
    public List<Tax> completeTax(String query) {
        EntityManager em;

        try {
            em = getEntityManager1();

            List<Tax> taxes = Tax.findTaxesByNameAndDescription(em, query);

            return taxes;

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

    public Boolean getEdit() {
        return edit;
    }

    public void setEdit(Boolean edit) {
        this.edit = edit;
    }

    public void setUser(JobManagerUser user) {
        this.user = user;
    }

    public MainTabView getMainTabView() {
        return mainTabView;
    }

    public void setMainTabView(MainTabView mainTabView) {
        this.mainTabView = mainTabView;
    }

    public void openClientsTab() {
        setIsClientNameAndIdEditable(getUser().getPrivilege().getCanAddClient());

        getMainTabView().openTab("Clients");
    }

    private void init() {
        isClientNameAndIdEditable = false; // tk put as transient in Client
        foundClients = new ArrayList<>();
        selectedClient = null;
        selectedContact = null;
        selectedAddress = null;
        searchText = "";
    }

    public void reset() {
        init();
    }

    public Client getSelectedClient() {
        if (selectedClient == null) {
            return new Client("");
        }
        return selectedClient;
    }

    public void setSelectedClient(Client selectedClient) {
        this.selectedClient = selectedClient;
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

    public void onClientCellEdit(CellEditEvent event) {
        BusinessEntityUtils.saveBusinessEntityInTransaction(getEntityManager1(),
                getFoundClients().get(event.getRowIndex()));
    }

    public EntityManagerFactory getEMF1() {
        return EMF1;
    }

    public void setEMF1(EntityManagerFactory EMF1) {
        this.EMF1 = EMF1;
    }

    public Boolean getIsNewContact() {
        return getSelectedContact().getId() == null && !getEdit();
    }

    public Boolean getIsNewAddress() {
        return getSelectedAddress().getId() == null && !getEdit();
    }

    public Boolean getIsActiveClientsOnly() {
        if (isActiveClientsOnly == null) {
            isActiveClientsOnly = true;
        }
        return isActiveClientsOnly;
    }

    public void setIsActiveClientsOnly(Boolean isActiveClientsOnly) {
        this.isActiveClientsOnly = isActiveClientsOnly;
    }

    public int getNumClientFound() {
        return getFoundClients().size();
    }

    public void doClientSearch() {
        if (searchText.trim().length() > 1) {
            if (getIsActiveClientsOnly()) {
                foundClients = Client.findActiveClientsByFirstPartOfName(getEntityManager1(), searchText);
            } else {
                foundClients = Client.findClientsByFirstPartOfName(getEntityManager1(), searchText);
            }
        } else {
            foundClients = new ArrayList<>();
        }
    }

    public List<Client> getFoundClients() {
        return foundClients;
    }

    public void setFoundClients(List<Client> foundClients) {
        this.foundClients = foundClients;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Boolean getIsNewClient() {
        return getSelectedClient().getId() == null;
    }

    public List<Address> completeClientAddress(String query) {
        List<Address> addresses = new ArrayList<>();

        try {

            for (Address address : getSelectedClient().getAddresses()) {
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

    public List<Contact> completeClientContact(String query) {
        List<Contact> contacts = new ArrayList<>();

        try {

            for (Contact contact : getSelectedClient().getContacts()) {
                if (contact.toString().toUpperCase().contains(query.toUpperCase())) {
                    contacts.add(contact);
                }
            }

            return contacts;
        } catch (Exception e) {

            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public List<Address> getAddressesModel() {
        return getSelectedClient().getAddresses();
    }

    public List<Contact> getContactsModel() {
        return getSelectedClient().getContacts();
    }

    public Address getCurrentAddress() {
        return getSelectedClient().getDefaultAddress();
    }

    public Boolean getIsClientNameAndIdEditable() {
        return isClientNameAndIdEditable;
    }

    public void setIsClientNameAndIdEditable(Boolean isClientNameAndIdEditable) {
        this.isClientNameAndIdEditable = isClientNameAndIdEditable;
    }

    public JobManagerUser getUser() {
        return user;
    }

    public void editClient() {
    }

    public void editSelectedClient() {

        PrimeFacesUtils.openDialog(null, "clientDialog", true, true, true, 450, 700);
    }

    public void updateClient() {
        setIsDirty(true);
    }
    
    public void updateFinancialAccount() {
        
        setIsDirty(true);
    }
    
    public void updateFinancialAccountId() {
        
        selectedClient.setAccountingId(selectedClient.getFinancialAccount().getIdCust());
        // Set credit limit 
        selectedClient.setCreditLimit((selectedClient.
                getFinancialAccount().
                getCreditLimit().doubleValue()));
        
        // Set financial account name??
        
        setIsDirty(true);
    }

    public void updateClientName(AjaxBehaviorEvent event) {
        selectedClient.setName(selectedClient.getName().trim());

        setIsDirty(true);
    }

    public void updateContact() {
        setIsDirty(true);
    }

    public void updateAddress() {
        setIsDirty(true);
    }

    public void createNewClient() {
        createNewClient(true);

        setIsClientNameAndIdEditable(getUser().getPrivilege().getCanAddClient());

        getMainTabView().openTab("Clients");

        PrimeFacesUtils.openDialog(null, "clientDialog", true, true, true, 450, 700);
    }

    public void createNewClient(Boolean active) {
        selectedClient = new Client("", active);
        selectedClient.setDiscount(Discount.findDefault(getEntityManager1(), "0.0"));
        selectedClient.setDefaultTax(Tax.findDefault(getEntityManager1(), "0.0"));
    }

    public Boolean getIsDirty() {
        return getSelectedClient().getIsDirty();
    }

    public void setIsDirty(Boolean isDirty) {
        getSelectedClient().setIsDirty(isDirty);
    }

    public Client getClientById(EntityManager em, Long Id) {

        try {
            return em.find(Client.class, Id);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public void cancelClientEdit(ActionEvent actionEvent) {

        setIsDirty(false);

        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public EntityManager getEntityManager1() {

        return EMF1.createEntityManager();

    }
    
    public EntityManager getEntityManager2() {

        return EMF2.createEntityManager();

    }

    public void okClient() {
        Boolean hasValidAddress = false;
        Boolean hasValidContact = false;

        try {

            // Validate 
            // Check for a valid address
            for (Address address : selectedClient.getAddresses()) {
                hasValidAddress = hasValidAddress || AddressValidator.validate(address);
            }
            if (!hasValidAddress) {
                PrimeFacesUtils.addMessage("Address Required",
                        "A valid address was not entered for this client",
                        FacesMessage.SEVERITY_ERROR);

                return;
            }

            // Check for a valid contact
            for (Contact contact : selectedClient.getContacts()) {
                hasValidContact = hasValidContact || ContactValidator.validate(contact);
            }
            if (!hasValidContact) {
                PrimeFacesUtils.addMessage("Contact Required",
                        "A valid contact was not entered for this client",
                        FacesMessage.SEVERITY_ERROR);

                return;
            }

            // Update tracking
            if (getIsNewClient()) {
                getSelectedClient().setDateFirstReceived(new Date());
                getSelectedClient().setDateEntered(new Date());
                getSelectedClient().setDateEdited(new Date());
                if (getUser() != null) {
                    selectedClient.setEnteredBy(getUser().getEmployee());
                    selectedClient.setEditedBy(getUser().getEmployee());
                }
            }

            // Do save
            if (getIsDirty()) {
                getSelectedClient().setDateEdited(new Date());
                if (getUser() != null) {
                    selectedClient.setEditedBy(getUser().getEmployee());
                }
                selectedClient.save(getEntityManager1());
                setIsDirty(false);
            }

            PrimeFaces.current().dialog().closeDynamic(null);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public Boolean getIsClientValid() {
        return BusinessEntityUtils.validateText(getSelectedClient().getName());
    }

    public void removeContact() {
        getSelectedClient().getContacts().remove(selectedContact);
        setIsDirty(true);
        selectedContact = null;
    }

    public void removeAddress() {
        getSelectedClient().getAddresses().remove(selectedAddress);
        setIsDirty(true);
        selectedAddress = null;
    }

    public Contact getCurrentContact() {
        return getSelectedClient().getDefaultContact();
    }

    public void okContact() {

        selectedContact = selectedContact.prepare();

        if (getIsNewContact()) {
            getSelectedClient().getContacts().add(selectedContact);
        }

        PrimeFaces.current().executeScript("PF('contactFormDialog').hide();");

    }

    public void okAddress() {

        selectedAddress = selectedAddress.prepare();

        if (getIsNewAddress()) {
            getSelectedClient().getAddresses().add(selectedAddress);
        }

        PrimeFaces.current().executeScript("PF('addressFormDialog').hide();");

    }

    public void createNewContact() {
        selectedContact = null;

        for (Contact contact : getSelectedClient().getContacts()) {
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

    public void editCurrentContact() {
        selectedContact = getCurrentContact();
        setEdit(true);
    }

    public void createNewAddress() {
        selectedAddress = null;

        // Find an existing invalid or blank address and use it as the neww address
        for (Address address : getSelectedClient().getAddresses()) {
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

    public void editCurrentAddress() {
        selectedAddress = getCurrentAddress();
        setEdit(true);
    }

    public List<Client> completeClient(String query) {
        EntityManager em = getEntityManager1();

        try {
            List<Client> clients = Client.findActiveClientsByAnyPartOfName(em, query);

            return clients;

        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

}
