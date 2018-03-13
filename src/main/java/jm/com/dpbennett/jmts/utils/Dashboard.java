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
import org.primefaces.PrimeFaces;

/**
 *
 * @author Desmond Bennett
 */
public class Dashboard implements Serializable {

    private JobManagerUser user;
    private List<DashboardTab> tabs;
    private Boolean render;
    private Integer tabIndex;

    public Dashboard(JobManagerUser user) {
        this.user = user;
        tabs = new ArrayList<>();
        tabIndex = 0;
        render = false;
    }

    public void removeAllTabs() {
        tabs.clear();
    }

    // tk change to addTab since rendering is not actually being done here
    public void addTab(
            EntityManager em,
            String tabId,
            Boolean render) {

        DashboardTab tab = findTab(tabId);

        if (tab != null && !render) {
            // DashboardTab is being removed           
            tabs.remove(tab);
        } else if (tab != null && render) {
            // DashboardTab already rendered
        } else if (tab == null && !render) {
            // DashboardTab is not be rendered            
        } else if (tab == null && render) {
            // DashboardTab is to be rendered 
            tabs.add(new DashboardTab(tabId, tabId));
        }

        // Update tabview and select the appropriate tab
        update("dashboardForm:dashboardAccordion");

        select(render);
    }

    public int getTabIndex(String tabId) {
        DashboardTab tab = findTab(tabId);
        if (tab != null) {
            return tabIndex;
        }

        return -1;
    }

    public JobManagerUser getUser() {
        return user;
    }

    public void setUser(JobManagerUser user) {
        this.user = user;
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

    public void select(int tabIndex) {

        this.tabIndex = tabIndex < 0 ? 0 : tabIndex;

        PrimeFaces.current().executeScript("PF('dashboardAccordionVar').select(" + this.tabIndex + ");");

    }

    public void select(String componentVar, int tabIndex) {

        this.tabIndex = tabIndex < 0 ? 0 : tabIndex;

        PrimeFaces.current().executeScript("PF('" + componentVar + "')" + ".select(" + this.tabIndex + ");");

    }

    public void select(Boolean wasTabAdded) {

        if (wasTabAdded) {
            PrimeFaces.current().executeScript("PF('dashboardAccordionVar').select(" + tabIndex + ");");
        } else {
            PrimeFaces.current().executeScript("PF('dashboardAccordionVar').select(" + ((tabIndex - 1) < 0 ? 0 : (tabIndex - 1)) + ");");
        }
    }

    public void select(String componentVar, Boolean wasTabAdded) {

        if (wasTabAdded) {
            PrimeFaces.current().executeScript("PF('" + componentVar + "')" + ".select(" + tabIndex + ");");
        } else {
            PrimeFaces.current().executeScript("PF('" + componentVar + "')" + ".select(" + ((tabIndex - 1) < 0 ? 0 : (tabIndex - 1)) + ");");
        }
    }

    public void update(String tabId, String componentId, String componentVar) {

        DashboardTab tab = findTab(tabId);

        if (tab != null) {
            PrimeFaces.current().ajax().update(componentId);
            select(componentVar, true);
        }
    }

    public void update(String componentId) {
        PrimeFaces.current().ajax().update(componentId);
    }

    public Integer getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(Integer tabIndex) {
        this.tabIndex = tabIndex;
    }

    private void init() {

        if (getUser().getModules().getJobManagementAndTrackingModule()) {
            tabs.add(new DashboardTab("Job Management", "Job Management"));
        }
        if (getUser().getModules().getAdminModule()) {
            tabs.add(new DashboardTab("System Administration", "System Administration"));
        }
        if (getUser().getModules().getFinancialAdminModule()) {
            tabs.add(new DashboardTab("Financial Administration", "Financial Administration"));
        }
        if (getUser().getModules().getComplianceModule()) {
            tabs.add(new DashboardTab("Standard Compliance", "Standard Compliance"));
        }
        if (getUser().getModules().getFoodsModule()) {
            tabs.add(new DashboardTab("Food Inspectorate", "Food Inspectorate"));
        }
        if (getUser().getModules().getLegalMetrologyModule()) {
            tabs.add(new DashboardTab("Legal Metrology", "Legal Metrology"));
        }
        if (getUser().getModules().getStandardsModule()) {
            tabs.add(new DashboardTab("Standards", "Standards"));
        }
        if (getUser().getModules().getCertificationModule()) {
            tabs.add(new DashboardTab("Certification", "Certification"));
        }
        if (getUser().getModules().getServiceRequestModule()) {
            tabs.add(new DashboardTab("Service Request", "Service Request"));
        }
        if (getUser().getModules().getLegalOfficeModule()) {
            tabs.add(new DashboardTab("Legal Office", "Legal Office"));
        }
        if (getUser().getModules().getCrmModule()) {
            tabs.add(new DashboardTab("Customer Relationship Management",
                    "Customer Relationship Management"));
        }
    }

    public void reset(JobManagerUser user) {
        this.user = user;

        // Remove all tabs re-init
        removeAllTabs();
        init();

        setRender(true);
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
