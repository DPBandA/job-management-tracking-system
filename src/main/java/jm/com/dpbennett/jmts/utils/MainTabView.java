package jm.com.dpbennett.jmts.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jm.com.dpbennett.business.entity.JobManagerUser;

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
        init();
    }

    private void init() {
        // Create tabs
        jobsTab = new MainTabViewTab(
                "jobsTab",
                "Jobs",
                user.getJobManagementAndTrackingUnit(),
                false,
                false,
                false, user);
        financialAdminTab = new MainTabViewTab(
                "financialAdminTab",
                "Financial Administration",
                false,
                false,
                user.getFinancialAdminUnit(),
                false,
                user);
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
                user);

        // Add tabs
        if (user.getJobManagementAndTrackingUnit()) {
            tabs.add(jobsTab);
        }
        if (user.getFinancialAdminUnit()) {
            tabs.add(financialAdminTab);
        }
        if (user.getAdminUnit()) {
            tabs.add(adminTab);
        }

    }

    public List<MainTabViewTab> getTabs() {
        return tabs;
    }

//    public void setTabs(ArrayList<MainTabViewTab> tabs) {
//        this.tabs = tabs;
//    }

    public JobManagerUser getUser() {
        return user;
    }

    public void setUser(JobManagerUser user) {
        this.user = user;
    }
}
