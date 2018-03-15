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
public class MainTabView implements Serializable {

    private Boolean render;
    private Integer tabIndex;
    private List<Tab> tabs;
    private JobManagerUser user;
    
    public MainTabView(JobManagerUser user) {
        this.user = user;
        tabs = new ArrayList<>();
        tabIndex = 0;
        render = false;
    }

    public void setTabName(String tabId, String name) {
        Tab tab = findTab(tabId);
        if (tab != null) {
            tab.setName(name);
        }
    }

    public Boolean getRender() {
        return render;
    }

    public void setRender(Boolean render) {
        this.render = render;
    }

    public void update(String componentId) {
        RequestContext context = RequestContext.getCurrentInstance();

        context.update(componentId);
    }

    public void update(String tabId, String componentId, String componentVar) {
        RequestContext context = RequestContext.getCurrentInstance();
        Tab tab = findTab(tabId);

        if (tab != null) {
            context.update(componentId);
            select(componentVar, true);
        }
    }

    public void select(int tabIndex) {
        RequestContext context = RequestContext.getCurrentInstance();

        this.tabIndex = tabIndex < 0 ? 0 : tabIndex;

        context.execute("PF('mainTabViewVar').select(" + this.tabIndex + ");");

    }

    public void select(String tabId) {
        select(getTabIndex(tabId));
    }

    public void select(String componentVar, int tabIndex) {
        RequestContext context = RequestContext.getCurrentInstance();

        this.tabIndex = tabIndex < 0 ? 0 : tabIndex;

        context.execute("PF('" + componentVar + "')" + ".select(" + this.tabIndex + ");");

    }

    public void select(Boolean wasTabAdded) {
        RequestContext context = RequestContext.getCurrentInstance();

        if (wasTabAdded) {
            context.execute("PF('mainTabViewVar').select(" + tabIndex + ");");
        } else {
            context.execute("PF('mainTabViewVar').select(" + ((tabIndex - 1) < 0 ? 0 : (tabIndex - 1)) + ");");
        }
    }

    public void select(String componentVar, Boolean wasTabAdded) {
        RequestContext context = RequestContext.getCurrentInstance();

        if (wasTabAdded) {
            context.execute("PF('" + componentVar + "')" + ".select(" + tabIndex + ");");
        } else {
            context.execute("PF('" + componentVar + "')" + ".select(" + ((tabIndex - 1) < 0 ? 0 : (tabIndex - 1)) + ");");
        }
    }

    public Tab findTab(String tabId) {
        tabIndex = 0;

        for (Tab tab : tabs) {
            if (tab.getId().equals(tabId)) {
                return tab;
            }
            ++tabIndex;
        }

        return null;
    }

    public Boolean isTabRendered(String tabId) {
        return !(findTab(tabId) == null);
    }

    public int getTabIndex(String tabId) {
        Tab tab = findTab(tabId);
        if (tab != null) {
            return tabIndex;
        }

        return -1;
    }

    public void addTab(
            EntityManager em,
            String tabId,
            Boolean render) {

        Tab tab = findTab(tabId);

        if (tab != null && !render) {
            // Tab is being removed           
            tabs.remove(tab);
        } else if (tab != null && render) {
            // Tab already added
        } else if (tab == null && !render) {
            // Tab is not be added            
        } else if (tab == null && render) {
            // Tab is to be added 
            tabs.add(new Tab(tabId, tabId));
        }

        // Update accordion and select the appropriate tab
        update("mainTabViewForm:mainTabView");

        select(render);

    }

    public void removeAllTabs() {
        tabs.clear();
    }

    private void init() {

        if (getUser().getModules().getJobManagementAndTrackingModule()) {
            tabs.add(new Tab("Job Browser", "Job Browser"));
        }
        if (getUser().getModules().getAdminModule()) {
            tabs.add(new Tab("System Administration", "System Administration"));
        }
        if (getUser().getModules().getFinancialAdminModule()) {
            tabs.add(new Tab("Financial Administration", "Financial Administration"));
        }
//        if (getUser().getModules().getComplianceModule()) {
//            tabs.add(new Tab("Clients", "Clients"));
//        }
//        if (getUser().getModules().getFoodsModule()) {
//            tabs.add(new Tab("Reports", "Reports"));
//        }
    }

    public void reset(JobManagerUser user) {
        this.user = user;

        // Remove all tabs re-init
        removeAllTabs();
        init();

        setRender(true);
    }

    public List<Tab> getTabs() {

        return tabs;
    }

    public void setTabs(ArrayList<Tab> tabs) {
        this.tabs = tabs;
    }

    public JobManagerUser getUser() {
        if (user == null) {
            return new JobManagerUser();
        }
        return user;
    }

    public void setUser(JobManagerUser user) {
        this.user = user;
    }

    public Integer getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(Integer tabIndex) {
        this.tabIndex = tabIndex;
    }
}
