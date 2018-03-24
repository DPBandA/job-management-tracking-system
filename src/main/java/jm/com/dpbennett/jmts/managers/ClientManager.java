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
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.Address;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.Contact;
import jm.com.dpbennett.business.entity.Internet;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.jmts.utils.PrimeFacesUtils;
import jm.com.dpbennett.business.entity.validator.AddressValidator;
import jm.com.dpbennett.business.entity.validator.ContactValidator;
import jm.com.dpbennett.jmts.Application;
import jm.com.dpbennett.jmts.utils.MainTabView;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;

/**
 *
 * @author dbennett
 */
@Named
@SessionScoped
public class ClientManager implements Serializable {

    @PersistenceUnit(unitName = "JMTSPU") // tk to be put in resource bundle
    private EntityManagerFactory entityManagerFactory;
    private Boolean isClientNameAndIdEditable;
    private Boolean isActiveClientsOnly;
    private Client selectedClient;
    private Contact selectedContact;
    private Address selectedAddress;
    private String searchText;
    private List<Client> foundClients;
    private MainTabView mainTabView;
    private JobManager jobManager;

    /**
     * Creates a new instance of ClientForm
     */
    public ClientManager() {
        init();
    }

    public JobManager getJobManager() {
        if (jobManager == null) {
            jobManager = Application.findBean("jobManager");
        }
        return jobManager;
    }

    private void init() {
        isClientNameAndIdEditable = false; // tk put as transient in Client
        foundClients = new ArrayList<>();
        selectedClient = null;
        selectedContact = null;
        selectedAddress = null;
        searchText = null;
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
    }

    public Address getSelectedAddress() {
        return selectedAddress;
    }

    public void setSelectedAddress(Address selectedAddress) {
        this.selectedAddress = selectedAddress;
    }

    // tk to be removed
//    public void clientDialogReturn() {
//        if (getCurrentJob().getIsDirty() && getCurrentJob().getId() != null) {
//            if (getCurrentJob().prepareAndSave(getEntityManager(), getUser()).isSuccess()) {
//                PrimeFacesUtils.addMessage("Client and Job Saved", "This job and the edited/added client were saved", FacesMessage.SEVERITY_INFO);
//            }
//        }
//    }
    public void onClientCellEdit(CellEditEvent event) {
        Application.saveBusinessEntity(getEntityManager(), getFoundClients().get(event.getRowIndex()));
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public Boolean getIsNewContact() {
        return getSelectedContact().getId() == null;
    }

    public Boolean getIsNewAddress() {
        return getSelectedAddress().getId() == null;
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
                foundClients = Client.findActiveClientsByFirstPartOfName(getEntityManager(), searchText);
            } else {
                foundClients = Client.findClientsByFirstPartOfName(getEntityManager(), searchText);
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
        return getJobManager().getUser();
    }

    public void editClient() {
    }

    public void editSelectedClient() {

        PrimeFacesUtils.openDialog(null, "clientDialog", true, true, true, 450, 700);
    }

    public void updateClient() {
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

    public void createNewClient(Boolean active) {
        selectedClient = new Client("", active);
    }

    public void setMainTabView(MainTabView mainTabView) {
        this.mainTabView = mainTabView;
    }

    public void closeClientsTab() {
        mainTabView.addTab(getEntityManager(), "Clients", false);
    }

    public void createNewClient() {
        createNewClient(true);

        setIsClientNameAndIdEditable(getUser().getPrivilege().getCanAddClient());

        PrimeFacesUtils.openDialog(null, "clientDialog", true, true, true, 450, 700);
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

    public EntityManager getEntityManager() {

        return entityManagerFactory.createEntityManager();

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
                selectedClient.save(getEntityManager());
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

        setIsDirty(false);
    }

    public void editCurrentContact() {
        selectedContact = getCurrentContact();
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

        setIsDirty(false);
    }

    public void editCurrentAddress() {
        selectedAddress = getCurrentAddress();
    }

    public List<Client> completeClient(String query) {
        EntityManager em = getEntityManager();

        try {
            List<Client> clients = Client.findActiveClientsByAnyPartOfName(em, query);

            return clients;

        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

}
