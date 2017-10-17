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

    private Integer tabIndex;
    private List<MainTab> tabs;
    private JobManagerUser user;
    private MainTab jobsTab;
    private MainTab financialAdminTab;
    private MainTab adminTab;
    private MainTab jobDetailTab;
    private MainTab clientsTab;

    public MainTabView(JobManagerUser user) {
        this.user = user;
        tabs = new ArrayList<>();
        tabIndex = 0;
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

        context.execute("mainTabViewVar.select(" + this.tabIndex + ");");

    }

    public void select(String componentVar, int tabIndex) {
        RequestContext context = RequestContext.getCurrentInstance();

        this.tabIndex = tabIndex < 0 ? 0 : tabIndex;

        context.execute(componentVar + ".select(" + this.tabIndex + ");");

    }

    public void select(Boolean wasTabAdded) {
        RequestContext context = RequestContext.getCurrentInstance();

        if (wasTabAdded) {
            context.execute("mainTabViewVar.select(" + tabIndex + ");");
        } else {
            context.execute("mainTabViewVar.select(" + ((tabIndex - 1) < 0 ? 0 : (tabIndex - 1)) + ");");
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

    public int getTabIndex(String tabId) {
        MainTab tab = findTab(tabId);
        if (tab != null) {
            return tabIndex;
        }

        return -1;
    }

    public void renderTab(
            EntityManager em,
            String tabId,
            Boolean render) {

        MainTab tab = findTab(tabId);

        if (tab != null && !render) {
            // DashboardTab is being removed
            switch (tabId) {
                case "jobsTab":
                    tab.setRenderJobsTab(em, render);
                    break;
                case "jobDetailTab":
                    tab.setRenderJobDetailTab(render);
                    break;
                case "financialAdminTab":
                    tab.setRenderFinancialAdminTab(em, render);
                    break;
                case "adminTab":
                    tab.setRenderAdminTab(em, render);
                    break;
                case "clientsTab":
                    tab.setRenderClientsTab(render);
                    break;
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
                case "jobDetailTab":
                    jobDetailTab.setRenderJobDetailTab(render);
                    tabs.add(jobDetailTab);
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
                default:
                    break;
            }
        }

        // Update tabview and select the appropriate tab       
        update("mainTabViewForm:mainTabView");

        select(render);
    }

    public void removeAllTabs() {
        tabs.clear();
    }

    private void init() {
        // Jobs tab
        jobsTab = new MainTab(
                "jobsTab",
                "Jobs",
                getUser().getJobManagementAndTrackingUnit(),
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
                getUser().getFinancialAdminUnit(),
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
                getUser().getAdminUnit(),
                false,
                getUser());
        // Job detail tab
        jobDetailTab = new MainTab(
                "jobDetailTab",
                "Job Detail",
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
                getUser());
    }

    public void reset(JobManagerUser user) {        
        this.user = user;
        // Construct tabs
        init();
        // Add tabs
        if (getUser().getJobManagementAndTrackingUnit()) {
            tabs.add(jobsTab);
        }
        if (getUser().getFinancialAdminUnit()) {
            tabs.add(financialAdminTab);
        }
        if (getUser().getAdminUnit()) {
            tabs.add(adminTab);
        }
    }

    public List<MainTab> getTabs() {
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