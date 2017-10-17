/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts.utils;

import javax.persistence.EntityManager;
import jm.com.dpbennett.business.entity.JobManagerUser;

public class MainTab {

    private String id;
    private String name;
    private Boolean renderJobsTab;
    private Boolean renderFinancialAdminTab;
    private Boolean renderAdminTab;
    private Boolean renderJobDetailTab;
    private Boolean renderClientsTab;
    private JobManagerUser user;

    public MainTab(
            String id,
            String name,
            Boolean renderJobsTab,
            Boolean renderJobDetailTab,
            Boolean renderFinancialAdminTab,
            Boolean renderAdminTab,
            Boolean renderClientsTab,
            JobManagerUser user) {

        this.id = id;
        this.name = name;
        this.user = user;
        this.renderJobDetailTab = renderJobDetailTab;
        this.renderJobsTab = renderJobsTab;
        this.renderFinancialAdminTab = renderFinancialAdminTab;
        this.renderAdminTab = renderAdminTab;
        this.renderClientsTab = renderClientsTab;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getRenderClientsTab() {
        return renderClientsTab;
    }

    public void setRenderClientsTab(Boolean renderClientsTab) {
        this.renderClientsTab = renderClientsTab;
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

    public void setRenderJobsTab(EntityManager em, Boolean render) {
        this.renderJobsTab = render;
        //user.setJobManagementAndTrackingUnit(renderJobsTab);
        //user.save(em);       
    }

    public Boolean getRenderFinancialAdminTab() {
        return renderFinancialAdminTab;
    }

    public void setRenderFinancialAdminTab(EntityManager em, Boolean render) {
        this.renderFinancialAdminTab = render;
        //user.setFinancialAdminUnit(renderFinancialAdminTab);
        //user.save(em);
    }

    public Boolean getRenderAdminTab() {
        return renderAdminTab;
    }

    public void setRenderAdminTab(EntityManager em, Boolean render) {
        this.renderAdminTab = render;
        //user.setAdminUnit(renderAdminTab);
        //user.save(em);
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
