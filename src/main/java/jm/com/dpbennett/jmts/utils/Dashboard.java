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
    private List<Tab> tabs;
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

        Tab tab = findTab(tabId);

        if (tab != null && !render) {
            // Tab is being removed and update the dashboard          
            tabs.remove(tab);
            // Update dashboard and select the appropriate tab
            update("dashboardForm:dashboardAccordion");
            select(render);
        } else if (tab != null && render) {
            // Tab already added so select and update the dashboard
            select(render);
        } else if (tab == null && !render) {
            // Tab is not be added or removed           
        } else if (tab == null && render) {
            // Tab is to be rendered 
            tabs.add(new Tab(tabId, tabId));
            // Update tabview and select the appropriate tab
            update("dashboardForm:dashboardAccordion");
            select(render);
        }
    }

    public int getTabIndex(String tabId) {
        Tab tab = findTab(tabId);
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

        Tab tab = findTab(tabId);

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
            tabs.add(new Tab("Job Management", "Job Management"));
        }
        if (getUser().getModules().getAdminModule()) {
            tabs.add(new Tab("System Administration", "System Administration"));
        }
        if (getUser().getModules().getFinancialAdminModule()) {
            tabs.add(new Tab("Financial Administration", "Financial Administration"));
        }
        if (getUser().getModules().getComplianceModule()) {
            tabs.add(new Tab("Standard Compliance", "Standard Compliance"));
        }
        if (getUser().getModules().getFoodsModule()) {
            tabs.add(new Tab("Foods Inspectorate", "Foods Inspectorate"));
        }
        if (getUser().getModules().getLegalMetrologyModule()) {
            tabs.add(new Tab("Legal Metrology", "Legal Metrology"));
        }
        if (getUser().getModules().getStandardsModule()) {
            tabs.add(new Tab("Standards", "Standards"));
        }
        if (getUser().getModules().getCertificationModule()) {
            tabs.add(new Tab("Certification", "Certification"));
        }
        if (getUser().getModules().getServiceRequestModule()) {
            tabs.add(new Tab("Service Request", "Service Request"));
        }
        if (getUser().getModules().getLegalOfficeModule()) {
            tabs.add(new Tab("Legal Documents", "Legal Documents"));
        }
        if (getUser().getModules().getCrmModule()) {
            tabs.add(new Tab("Customer Relationship Management",
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

    public List<Tab> getTabs() {

        return tabs;
    }

    public void setTabs(ArrayList<Tab> tabs) {
        this.tabs = tabs;
    }

}
