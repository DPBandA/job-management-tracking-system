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
package jm.com.dpbennett.jmts.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import jm.com.dpbennett.business.entity.JobManagerUser;
import org.primefaces.context.RequestContext;

/**
 *
 * @author desbenn
 */
public class Dashboard implements Serializable {

    private List<DashboardTab> tabs;
    private Boolean render;
       public Dashboard() {
        tabs = new ArrayList<>();
        tabs.add(new DashboardTab(DashboardTab.TabId.JOB_MANAGEMENT, "Job Management", ""));
        tabs.add(new DashboardTab(DashboardTab.TabId.SYSTEM_ADMIN, "System Admin", ""));
        tabs.add(new DashboardTab(DashboardTab.TabId.FINANCIAL_ADMIN, "Financial Management", ""));

        render = true;
    }

    private void init() {

    }

    public void reset(JobManagerUser user) {

    }
    
    public Boolean getRender() {
        return render;
    }

    public void setRender(Boolean render) {
        this.render = render;
    }

    public List<DashboardTab> getTabs() {

        return tabs;
    }

    public void setTabs(ArrayList<DashboardTab> tabs) {
        this.tabs = tabs;
    }

}
