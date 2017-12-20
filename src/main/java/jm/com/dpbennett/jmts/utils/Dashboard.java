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

    private Boolean render;
    private Integer tabIndex;
    private List<DashboardTab> tabs;
    private JobManagerUser user;
    private DashboardTab jobsTab;
    private DashboardTab financialAdminTab;
    private DashboardTab adminTab;

    public Dashboard(JobManagerUser user) {
        this.user = user;
        tabs = new ArrayList<>();
        tabIndex = 0;
        render = false;
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
        DashboardTab tab = findTab(tabId);

        if (tab != null) {
            context.update(componentId);
            select(componentVar, true);
        }
    }

    public void select(int tabIndex) {
        RequestContext context = RequestContext.getCurrentInstance();

        this.tabIndex = tabIndex < 0 ? 0 : tabIndex;

        context.execute("dashboardAccordionVar.select(" + this.tabIndex + ");");

    }

    public void select(String componentVar, int tabIndex) {
        RequestContext context = RequestContext.getCurrentInstance();

        this.tabIndex = tabIndex < 0 ? 0 : tabIndex;

        context.execute(componentVar + ".select(" + this.tabIndex + ");");

    }

    public void select(Boolean wasTabAdded) {
        RequestContext context = RequestContext.getCurrentInstance();

        if (wasTabAdded) {
            context.execute("dashboardAccordionVar.select(" + tabIndex + ");");
        } else {
            context.execute("dashboardAccordionVar.select(" + ((tabIndex - 1) < 0 ? 0 : (tabIndex - 1)) + ");");
        }
    }

    public void select(String componentVar, Boolean wasTabAdded) {
        RequestContext context = RequestContext.getCurrentInstance();

        if (wasTabAdded) {
            context.execute(componentVar + ".select(" + tabIndex + ");");
        } else {
            context.execute(componentVar + ".select(" + ((tabIndex - 1) < 0 ? 0 : (tabIndex - 1)) + ");");
        }
    }

    public DashboardTab findTab(String tabId) {
        tabIndex = 0;

        for (DashboardTab tab : tabs) {
            if (tab.getId().equals(tabId)) {
                return tab;
            }
            ++tabIndex;
        }

        return null;
    }

    public int getTabIndex(String tabId) {
        DashboardTab tab = findTab(tabId);
        if (tab != null) {
            return tabIndex;
        }

        return -1;
    }

    public void renderTab(
            EntityManager em,
            String tabId,
            Boolean render) {

        DashboardTab tab = findTab(tabId);

        if (tab != null && !render) {
            // DashboardTab is being removed
            switch (tabId) {
                case "jobsTab":
                    tab.setRenderJobsTab(em, render);
                    break;
//                case "financialAdminTab":
//                    tab.setRenderFinancialAdminTab(em, render);
//                    break;
//                case "adminTab":
//                    tab.setRenderAdminTab(em, render);
//                    break;
                default:
                    break;
            }
            tabs.remove(tab);
        } else if (tab != null && render) {
            // DashboardTab already rendered
        } else if (tab == null && !render) {
            // DashboardTab is not be rendered            
        } else if (tab == null && render) {
            // DashboardTab is to be rendered    
            switch (tabId) {
                case "jobsTab":
                    jobsTab.setRenderJobsTab(em, render);
                    tabs.add(jobsTab);
                    break;
//                case "financialAdminTab":
//                    financialAdminTab.setRenderFinancialAdminTab(em, render);
//                    tabs.add(financialAdminTab);
//                    break;
//                case "adminTab":
//                    adminTab.setRenderAdminTab(em, render);
//                    tabs.add(adminTab);
//                    break;
                default:
                    break;
            }
        }

        // Update tabview and select the appropriate tab
        update("dashboardForm:dashboardAccordion");

        select(render);
    }

    public void removeAllTabs() {
        tabs.clear();
    }

    private void init() {
        // Jobs tab
        jobsTab = new DashboardTab(
                "jobsTab",
                "Job Management",
                getUser().getJobManagementAndTrackingUnit(),
                false,
                false,
                false,
                false,
                getUser());
//        // Financial admin tab
//        financialAdminTab = new DashboardTab(
//                "financialAdminTab",
//                "Financial Administration",
//                false,
//                false,
//                getUser().getFinancialAdminUnit(),
//                false,
//                false,
//                getUser());
//        // Admin tab
//        adminTab = new DashboardTab(
//                "adminTab",
//                "System Administration",
//                false,
//                false,
//                false,
//                getUser().getAdminUnit(),
//                false,
//                getUser());        
    }

    public void reset(JobManagerUser user) {
        this.user = user;
        // Remove all
        removeAllTabs();
        // Construct tabs
        init();
        // Add tabs
        if (getUser().getJobManagementAndTrackingUnit()) {
            tabs.add(jobsTab);
        }

        setRender(true);
    }

    public List<DashboardTab> getTabs() {
        return tabs;
    }

    public void setTabs(ArrayList<DashboardTab> tabs) {
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
