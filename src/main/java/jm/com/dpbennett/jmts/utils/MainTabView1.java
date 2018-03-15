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
public class MainTabView1 implements Serializable {

    private Boolean render;
    private Integer tabIndex;
    private List<MainTab> tabs;
    private JobManagerUser user;
    private MainTab jobsTab;
    private MainTab financialAdminTab;
    private MainTab adminTab;
    private MainTab jobDetailTab;
    private MainTab clientsTab;
    private MainTab reportsTab;
    private MainTab cashierTab;
    private MainTab jobCostingsTab;

    public MainTabView1(JobManagerUser user) {
        this.user = user;
        tabs = new ArrayList<>();
        tabIndex = 0;
        render = false;
    }
    
    public void setTabName(String tabId, String name) {
        MainTab tab = findTab(tabId);
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
        MainTab tab = findTab(tabId);

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

    public MainTab findTab(String tabId) {
        tabIndex = 0;

        for (MainTab tab : tabs) {
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
        MainTab tab = findTab(tabId);
        if (tab != null) {
            return tabIndex;
        }

        return -1;
    }

    public void addTab(
            EntityManager em,
            String tabId,
            Boolean render) {

        MainTab tab = findTab(tabId);

        if (tab != null && !render) {
            // MainTab is being removed
            switch (tabId) {
                case "jobsViewTab":
                    tab.setRenderJobsTab(em, render);
                    break;
//                case "jobDetailTab":
//                    tab.setRenderJobDetailTab(render);
//                    break;
                case "financialAdminTab":
                    tab.setRenderFinancialAdminTab(em, render);
                    break;
                case "adminTab":
                    tab.setRenderAdminTab(em, render);
                    break;
                case "clientsTab":
                    tab.setRenderClientsTab(render);
                    break;
                case "reportsTab":
                    tab.setRenderReportsTab(render);
                    break;
//                case "cashierTab":
//                    tab.setRenderCashierTab(render);
//                    break;
//                case "jobCostingsTab":
//                    tab.setRenderJobCostingsTab(render);
//                    break;
                default:
                    break;
            }
            tabs.remove(tab);
        } else if (tab != null && render) {
            // MainTab already rendered
        } else if (tab == null && !render) {
            // MainTab is not be rendered            
        } else if (tab == null && render) {
            // MainTab is to be rendered    
            switch (tabId) {
                case "jobsViewTab":
                    jobsTab.setRenderJobsTab(em, render);
                    tabs.add(jobsTab);
                    break;
                case "financialAdminTab":
                    financialAdminTab.setRenderFinancialAdminTab(em, render);
                    tabs.add(financialAdminTab);
                    break;
                case "adminTab":
                    adminTab.setRenderAdminTab(em, render);
                    tabs.add(adminTab);
                    break;
                case "clientsTab":
                    clientsTab.setRenderClientsTab(render);
                    tabs.add(clientsTab);
                    break;
                case "reportsTab":
                    reportsTab.setRenderReportsTab(render);
                    tabs.add(reportsTab);
                    break;
//                case "cashierTab":
//                    cashierTab.setRenderCashierTab(render);
//                    tabs.add(cashierTab);
//                    break;
//                case "jobCostingsTab":
//                    jobCostingsTab.setRenderJobCostingsTab(render);
//                    tabs.add(jobCostingsTab);
//                    break;
                default:
                    break;
            }

            //select(render);
        }

    }

    public void removeAllTabs() {
        tabs.clear();
    }

    private void init() {
        // Jobs tab
        jobsTab = new MainTab(
                "jobsTab",
                "Jobs",
                getUser().getIsJobsPreferredJobTableView(),
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                getUser());
        // Financial admin tab
        financialAdminTab = new MainTab(
                "financialAdminTab",
                "Financial Administration",
                false,
                false,
                getUser().getModules().getFinancialAdminModule(),
                false,
                false,
                false,
                false,
                false,
                getUser());
        // Admin tab
        adminTab = new MainTab(
                "adminTab",
                "System Administration",
                false,
                false,
                false,
                getUser().getModules().getAdminModule(),
                false,
                false,
                false,
                false,
                getUser());
        // Job detail tab
        jobDetailTab = new MainTab(
                "jobsViewTab",
                "Jobs View",
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                getUser());
        // Clients tab
        clientsTab = new MainTab(
                "clientsTab",
                "Clients",
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                getUser());
        // Reports tab
        reportsTab = new MainTab(
                "reportsTab",
                "Reports",
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                getUser());
        // Cashier tab
        cashierTab = new MainTab(
                "cashierTab",
                "Cashier View",
                false,
                false,
                false,
                false,
                false,
                false,
                getUser().getIsCashierPreferredJobTableView(),
                false,
                getUser());
        // Job Costings tab
        jobCostingsTab = new MainTab(
                "jobCostingsTab",
                "Job Costings",
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                getUser().getIsJobCostingsPreferredJobTableView(),
                getUser());
    }

    public void reset(JobManagerUser user) {
        this.user = user;
        // Remove all
        removeAllTabs();
        // Construct tabs
        init();
        // Add tabs
        if (getUser().getIsJobsPreferredJobTableView()) {
            tabs.add(jobsTab);
        }
        if (getUser().getIsCashierPreferredJobTableView()) {
            tabs.add(cashierTab);
        }
        if (getUser().getIsJobCostingsPreferredJobTableView()) {
            tabs.add(jobCostingsTab);
        }
        if (getUser().getModules().getFinancialAdminModule()) {
            tabs.add(financialAdminTab);
        }
        if (getUser().getModules().getAdminModule()) {
            tabs.add(adminTab);
        }
        
        setRender(true);
    }

    public List<MainTab> getTabs() {
        //return tabs;
        // tk
//        if (tabs.isEmpty()) {
//            MainTab blank = new MainTab(
//                    "",
//                    "",
//                    false,
//                    false,
//                    false,
//                    false,
//                    false,
//                    false,
//                    false,
//                    false,
//                    null);
//            tabs.add(blank);
//        }

        return tabs;
    }

    public void setTabs(ArrayList<MainTab> tabs) {
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
