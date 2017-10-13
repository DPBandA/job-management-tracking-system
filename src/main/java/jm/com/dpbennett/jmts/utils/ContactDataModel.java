/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts.utils;

import java.util.List;
import javax.faces.model.ListDataModel;
import jm.com.dpbennett.business.entity.Contact;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author dbennett
 */
public class ContactDataModel extends ListDataModel<Contact> implements SelectableDataModel<Contact> {
    
    private List<Contact> list;

    public ContactDataModel() {
    }

    public ContactDataModel(List<Contact> list) {
        super(list);
        this. list = list;
    }

    @Override
    public Object getRowKey(Contact contact) {
        return contact.getId();
    }

    @Override
    public Contact getRowData(String rowKey) {
        for (Contact contact : list) {
            if (contact.getId().toString().equals(rowKey)) {
                return contact;
            }
        }

        return null;
    }
}
