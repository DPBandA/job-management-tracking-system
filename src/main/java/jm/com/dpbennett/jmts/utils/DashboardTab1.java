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

import javax.persistence.EntityManager;
import jm.com.dpbennett.business.entity.JobManagerUser;

public class DashboardTab1 {

    private String id;
    private String name;
    private Boolean renderJobsTab;
    private Boolean renderFinancialAdminTab;
    private Boolean renderAdminTab;
    private Boolean renderJobDetailTab;
    private Boolean renderClientsTab;
    private JobManagerUser user;

    public DashboardTab1(
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
        user.setJobManagementAndTrackingUnit(renderJobsTab);
        user.save(em);       
    }

    public Boolean getRenderFinancialAdminTab() {
        return renderFinancialAdminTab;
    }

    public void setRenderFinancialAdminTab(EntityManager em, Boolean render) {
        this.renderFinancialAdminTab = render;
        user.setFinancialAdminUnit(renderFinancialAdminTab);
        user.save(em);
    }

    public Boolean getRenderAdminTab() {
        return renderAdminTab;
    }

    public void setRenderAdminTab(EntityManager em, Boolean render) {
        this.renderAdminTab = render;
        user.setAdminUnit(renderAdminTab);
        user.save(em);
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
