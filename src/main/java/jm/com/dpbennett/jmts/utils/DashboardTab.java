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

public class DashboardTab {
    
    public enum TabId {JOB_MANAGEMENT, SYSTEM_ADMIN, FINANCIAL_ADMIN};

    private TabId id;
    private String name;
    private String content;
    private int test = 0;

    public DashboardTab(
            TabId id,
            String name,
            String content) {

        this.id = id;
        this.name = name;
        this.content = content;
    }

    public int getTest() {
        return test;
    }

    public void setTest(int test) {
        this.test = test;
    }
    
    public int getIsJobManagementTab() {
        System.out.println("is job manaegement"); //tk
        
        return id == TabId.JOB_MANAGEMENT ?  1 : 0;
    }
   
    public TabId getId() {
        if (id == null) {
            id = TabId.JOB_MANAGEMENT;
        }

        return id;
    }

    public void setId(TabId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
