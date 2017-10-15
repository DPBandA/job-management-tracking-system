/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts.utils;

import jm.com.dpbennett.business.entity.JobManagerUser;

public class MainTabViewTab {

    private String id;
    private String name;
    private Boolean renderJobsTab;
    private Boolean renderFinancialAdminTab;
    private Boolean renderAdminTab;
    private Boolean renderJobDetailTab;
    private JobManagerUser user;

    public MainTabViewTab(
            String id,
            String name,
            Boolean renderJobsTab,
            Boolean renderJobDetailTab,
            Boolean renderFinancialAdminTab,
            Boolean renderAdminTab,
            JobManagerUser user) {

        this.id = id;
        this.name = name;
        this.user = user;
        this.renderJobDetailTab = renderJobDetailTab;
        this.renderJobsTab = renderJobsTab;
        this.renderFinancialAdminTab = renderFinancialAdminTab;
        this.renderAdminTab = renderAdminTab;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRenderJobsTab() {
        return renderJobsTab;
    }

    public void setRenderJobsTab(Boolean renderJobsTab) {
        this.renderJobsTab = renderJobsTab;
    }

    public Boolean getRenderFinancialAdminTab() {
        return renderFinancialAdminTab;
    }

    public void setRenderFinancialAdminTab(Boolean renderFinancialAdminTab) {
        this.renderFinancialAdminTab = renderFinancialAdminTab;
    }

    public Boolean getRenderAdminTab() {
        return renderAdminTab;
    }

    public void setRenderAdminTab(Boolean renderAdminTab) {
        this.renderAdminTab = renderAdminTab;
    }

    public Boolean getRenderJobDetailTab() {
        return renderJobDetailTab;
    }

    public void setRenderJobDetailTab(Boolean renderJobDetailTab) {
        this.renderJobDetailTab = renderJobDetailTab;
    }

    

    public JobManagerUser getUser() {
        return user;
    }

    public void setUser(JobManagerUser user) {
        this.user = user;
    }

}
