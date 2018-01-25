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
import jm.com.dpbennett.business.entity.Job;
import org.primefaces.model.SelectableDataModel;

/**
 *
 * @author dbennett
 */
public class JobDataModel extends ListDataModel<Job> implements SelectableDataModel<Job> {
    
    private List<Job> list;

    public JobDataModel() {
    }

    public JobDataModel(List<Job> list) {
        super(list);
        this. list = list;
    }

    @Override
    public Object getRowKey(Job job) {
        return job.getId();
    }

    @Override
    public Job getRowData(String rowKey) {
        for (Job job : list) {
            if (job.getId().toString().equals(rowKey)) {
                return job;
            }
        }

        return null;
    }
}
