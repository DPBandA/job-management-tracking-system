package jm.com.dpbennett.jmts.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.JobManagerUser;
import org.primefaces.context.RequestContext;

/**
 *
 * @author desbenn
 */
public class MainTabView implements Serializable {

    private List<MainTabViewTab> tabs;
    private JobManagerUser user;
    private MainTabViewTab jobsTab;
    private MainTabViewTab financialAdminTab;
    private MainTabViewTab adminTab;
    private MainTabViewTab jobDetailTab;

    public MainTabView(JobManagerUser user) {
        this.user = user;
        tabs = new ArrayList<>();
    }

    public MainTabViewTab findTab(String tabId) {
        for (MainTabViewTab tab : tabs) {
            if (tab.getId().equals(tabId)) {
                return tab;
            }
        }

        return null;
    }

    public void renderTab(EntityManager em, String tabId, Boolean render) {
        RequestContext context = RequestContext.getCurrentInstance();
        MainTabViewTab tab = findTab(tabId);

        if (tab != null && !render) {
            // Tab is being removed
            switch (tabId) {
                case "jobsTab":
                    tab.setRenderJobsTab(em, render);
                    break;
                case "jobDetailTab":

                    break;
                case "financialAdminTab":
                    tab.setRenderFinancialAdminTab(em, render);
                    break;
                case "adminTab":
                    tab.setRenderAdminTab(em, render);
                    break;
                default:
                    break;
            }
            tabs.remove(tab);
        } else if (tab != null && render) {
            // Tab already rendered
        } else if (tab == null && !render) {
            // Tab is not be rendered            
        } else if (tab == null && render) {
            // Tab is to be rendered    
            switch (tabId) {
                case "jobsTab":
                    jobsTab.setRenderJobsTab(em, render);
                    tabs.add(jobsTab);
                    break;
                case "jobDetailTab":

                    break;
                case "financialAdminTab":
                    financialAdminTab.setRenderFinancialAdminTab(em, render);
                    tabs.add(financialAdminTab);
                    break;
                case "adminTab":
                    adminTab.setRenderAdminTab(em, render);
                    tabs.add(adminTab);
                    break;
                default:
                    break;
            }
        }

        context.update("mainTabViewForm:mainTabView");
    }

    public void removeAllTabs() {
        tabs.clear();
    }

    private void init() {
        // Create tabs
        jobsTab = new MainTabViewTab(
                "jobsTab",
                "Jobs",
                getUser().getJobManagementAndTrackingUnit(),
                false,
                false,
                false, getUser());
        financialAdminTab = new MainTabViewTab(
                "financialAdminTab",
                "Financial Administration",
                false,
                false,
                user.getFinancialAdminUnit(),
                false,
                getUser());
        adminTab = new MainTabViewTab(
                "adminTab",
                "System Administration",
                false,
                false,
                false,
                user.getAdminUnit(),
                user);
        jobDetailTab = new MainTabViewTab(
                "jobDetailTab",
                "Job Detail",
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

    public List<MainTabViewTab> getTabs() {
        return tabs;
    }

    public void setTabs(ArrayList<MainTabViewTab> tabs) {
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
}
