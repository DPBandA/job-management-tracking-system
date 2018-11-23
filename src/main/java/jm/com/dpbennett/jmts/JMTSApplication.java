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
import jm.com.dpbennett.business.entity.AccPacCustomer;
import jm.com.dpbennett.business.entity.Country;
import jm.com.dpbennett.business.entity.DepartmentUnit;
import jm.com.dpbennett.business.entity.Job;
import jm.com.dpbennett.business.entity.JobCategory;
import jm.com.dpbennett.business.entity.JobSubCategory;
import jm.com.dpbennett.business.entity.Laboratory;
import jm.com.dpbennett.business.entity.Preference;
import jm.com.dpbennett.business.entity.Sector;

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

    ////////////////////////////////////////////////////////////////////////////
   
    

    public List<String> completePreferenceValue(String query) {
        EntityManager em;

        try {
            em = getEntityManager1();

            List<String> preferenceValues = Preference.findAllPreferenceValues(em, query);

            return preferenceValues;

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List<String> getJobTableViews() {
        EntityManager em;

        try {
            em = getEntityManager1();

            List<String> preferenceValues = Preference.findAllPreferenceValues(em, "");

            return preferenceValues;

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List<DepartmentUnit> completeDepartmentUnit(String query) {
        EntityManager em;

        try {
            em = getEntityManager1();

            List<DepartmentUnit> departmentUnits = DepartmentUnit.findDepartmentUnitsByName(em, query);

            return departmentUnits;

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List<Laboratory> completeLaboratory(String query) {
        EntityManager em;

        try {
            em = getEntityManager1();

            List<Laboratory> laboratories = Laboratory.findLaboratoriesByName(em, query);

            return laboratories;

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    /**
     * NB: query parameter currently not used to filter sectors.
     *
     * @param query
     * @return
     */
    public List<Sector> completeActiveSectors(String query) {
        EntityManager em = getEntityManager1();

        List<Sector> sectors = Sector.findAllActiveSectors(em);

        return sectors;
    }

    /**
     * NB: query not used to filter
     *
     * @param query
     * @return
     */
    public List<JobCategory> completeActiveJobCategories(String query) {
        EntityManager em = getEntityManager1();

        List<JobCategory> categories = JobCategory.findAllActiveJobCategories(em);

        return categories;
    }

    public List<JobSubCategory> completeActiveJobSubCategories(String query) {
        EntityManager em = getEntityManager1();

        List<JobSubCategory> subCategories = JobSubCategory.findAllActiveJobSubCategories(em);

        return subCategories;
    }
}
