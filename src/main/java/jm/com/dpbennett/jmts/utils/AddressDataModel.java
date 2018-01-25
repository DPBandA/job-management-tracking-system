/*
Business Entity Library (BEL) - A foundational library for JSF web applications 
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
package jm.com.dpbennett.jmts.utils;

import java.util.List;
import javax.faces.model.ListDataModel;
import jm.com.dpbennett.business.entity.Address;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author D P Bennett &amp; Associates
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
