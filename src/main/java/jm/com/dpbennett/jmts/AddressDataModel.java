/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts;

import java.util.List;
import javax.faces.model.ListDataModel;
import jm.com.dpbennett.business.entity.Address;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author D P Bennett & Associates
 */
public class AddressDataModel extends ListDataModel<Address> implements SelectableDataModel<Address> {
    
    private List<Address> list;

    public AddressDataModel() {
    }

    public AddressDataModel(List<Address> list) {
        super(list);
        this. list = list;
    }

    @Override
    public Object getRowKey(Address address) {
        return address.getId();
    }

    @Override
    public Address getRowData(String rowKey) {
        for (Address address : list) {
            if (address.getId().toString().equals(rowKey)) {
                return address;
            }
        }

        return null;
    }
}
