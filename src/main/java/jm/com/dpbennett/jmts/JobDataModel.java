/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts;

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
