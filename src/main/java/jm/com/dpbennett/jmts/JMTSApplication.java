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
package jm.com.dpbennett.jmts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.Job;
import jm.com.dpbennett.business.entity.JobSubCategory;

/**
 *
 * @author Desmond Bennett
 */
@Named(value = "App")
@ApplicationScoped
@Singleton
public class JMTSApplication implements Serializable {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    @PersistenceUnit(unitName = "AccPacPU")
    private EntityManagerFactory EMF2;
    private final Map<String, String> themes = new TreeMap<>();
    private final List<Job> openedJobs;

    /**
     * Creates a new instance of Application
     */
    public JMTSApplication() {
        openedJobs = new ArrayList<>();

        // tk Put in psrent Application class. Init primefaces themes
        themes.put("Aristo", "aristo");
        themes.put("Black-Tie", "black-tie");
        themes.put("Redmond", "redmond");
        themes.put("Dark Hive", "dark-hive");
    }

    public synchronized List<Job> getOpenedJobs() {
        return openedJobs;
    }

    public synchronized void addOpenedJob(Job job) {
        getOpenedJobs().add(job);
    }

    public synchronized void removeOpenedJob(Job job) {
        getOpenedJobs().remove(job);
    }

    public synchronized Job findOpenedJob(Long jobId) {
        for (Job job : openedJobs) {
            if (Objects.equals(job.getId(), jobId)) {
                return job;
            }
        }
        
        return null;
    }

    public Map<String, String> getThemes() {
        return themes;
    }

    public EntityManager getEntityManager1() {
        return EMF1.createEntityManager();
    }

    public EntityManager getEntityManager2() {
        return EMF2.createEntityManager();
    }

}
