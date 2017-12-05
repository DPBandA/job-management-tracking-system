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
package jm.com.dpbennett.jmts.managers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.Job;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.JobSample;
import jm.com.dpbennett.business.entity.SystemOption;
import jm.com.dpbennett.business.entity.management.BusinessEntityManagement;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.business.entity.utils.PrimeFacesUtils;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Desmond Bennett
 */
@Named
@SessionScoped
public class JobSampleManager implements Serializable, BusinessEntityManagement {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;   
    private Job currentJob;   
    private JobSample selectedJobSample;
    private JobSample selectedJobSampleBackup;    
    private Integer longProcessProgress;   
    private Integer jobSampleDialogTabViewActiveIndex;   
    private JobManagerUser user;    

    /**
     * Creates a new instance of JobManagerBean
     */
    public JobSampleManager() {       
        longProcessProgress = 0;               
        selectedJobSample = new JobSample();
        jobSampleDialogTabViewActiveIndex = 0;       
    }
    
     public void createNewJobSample(ActionEvent event) {

        if (getCurrentJob().hasOnlyDefaultJobSample()) {
            selectedJobSample = getCurrentJob().getJobSamples().get(0);
            selectedJobSample.setDescription("");
            selectedJobSample.setIsToBeAdded(false);
        } else {
            selectedJobSample = new JobSample();
            selectedJobSample.setIsToBeAdded(true);
            // Init sample
            selectedJobSample.setJobId(currentJob.getId());
            selectedJobSample.setSampleQuantity(1L);
            selectedJobSample.setQuantity(1L);

            selectedJobSample.setReferenceIndex(getCurrentNumberOfJobSamples());

            if (selectedJobSample.getSampleQuantity() == 1L) {
                selectedJobSample.setReference(BusinessEntityUtils.getAlphaCode(selectedJobSample.getReferenceIndex()));
            } else {
                selectedJobSample.setReference(BusinessEntityUtils.getAlphaCode(selectedJobSample.getReferenceIndex()) + "-"
                        + BusinessEntityUtils.getAlphaCode(selectedJobSample.getReferenceIndex()
                                + selectedJobSample.getSampleQuantity() - 1));
            }
        }

        selectedJobSample.setDateSampled(new Date());
        jobSampleDialogTabViewActiveIndex = 0;

        if (event != null) {
            PrimeFacesUtils.openDialog(null, "jobSampleDialog", true, true, true, 600, 700);
        }
    }

    public void setUser(JobManagerUser user) {
        this.user = user;
    }

    public JobManagerUser getUser() {
        if (user == null) {
            return new JobManagerUser();
        }
        return user;
    }

    @Override
    public void setDirty(Boolean dirty) {
        selectedJobSample.setIsDirty(dirty);
    }

    @Override
    public Boolean isDirty() {
        return selectedJobSample.getIsDirty();
    }
    
    public Boolean isCurrentJobDirty() {
        return getCurrentJob().getIsDirty();
    }
    
    
//    public void jobSampleDialogReturn() {
//        if (!isDirty() && getSelectedJobSample().getIsDirty()) {
//            if (prepareAndSaveCurrentJob(getEntityManager1())) {
//                getSelectedJobSample().setIsDirty(false);
//                addMessage("Sample(s) and Job Saved", "This job and the edited/added sample(s) were saved", FacesMessage.SEVERITY_INFO);
//            } else {
//                addMessage("Sample(s) and Job NOT Saved", "Sample(s) NOT saved. Please ensure that all required fields were filled out", FacesMessage.SEVERITY_WARN);
//            }
//        } else if (isDirty() && getSelectedJobSample().getIsDirty()) {
//            addMessage("Sample(s) Added/Edited", "Save this job if you wish to keep the changes", FacesMessage.SEVERITY_WARN);
//        } else if (isDirty() && !getSelectedJobSample().getIsDirty()) {
//            addMessage("Job to be Saved", "Sample(s) not edited but this job was previously edited but not saved", FacesMessage.SEVERITY_WARN);
//        } else if (!isDirty() && !getSelectedJobSample().getIsDirty()) {
//            // Nothing to do yet
//        }
//    }

    // tk remove when autcomplete is done
    public Long getSampledById() {
        return selectedJobSample.getSampledBy().getId();
    }

    // tk remove when autcomplete is done
    public Long getReceivedById() {
        return selectedJobSample.getReceivedBy().getId();
    }

    /**
     * To be applied when sample if saved
     */
    public void updateJobSampleReference() {
        // update reference while ensuring number of samples is not less than 1
        // or greater than 700 (for now but to be made system option)
        if (selectedJobSample.getSampleQuantity() != null) {
            if (selectedJobSample.getSampleQuantity() == 1) {
                selectedJobSample.setReference(BusinessEntityUtils.getAlphaCode(selectedJobSample.getReferenceIndex()));
            } else {
                selectedJobSample.setReference(BusinessEntityUtils.getAlphaCode(selectedJobSample.getReferenceIndex()) + "-"
                        + BusinessEntityUtils.getAlphaCode(selectedJobSample.getReferenceIndex()
                                + selectedJobSample.getSampleQuantity() - 1));
            }
        }

        //setDirty(true);
        getSelectedJobSample().setIsDirty(true);
    }

    public void updateProductQuantity() {
        //setDirty(true);
        getSelectedJobSample().setIsDirty(true);
    }

    public void closeJobSampleDeleteConfirmDialog() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void deleteJobSample() {

        // update number of samples
        if ((currentJob.getNumberOfSamples() - selectedJobSample.getSampleQuantity()) > 0) {
            currentJob.setNumberOfSamples(currentJob.getNumberOfSamples() - selectedJobSample.getSampleQuantity());
        } else {
            currentJob.setNumberOfSamples(0L);
        }

        List<JobSample> samples = currentJob.getJobSamples();
        int index = 0;
        for (JobSample sample : samples) {
            if (sample.getReference().equals(selectedJobSample.getReference())) {
                // removed sample record
                samples.remove(index);
                break;
            }
            ++index;
        }

        updateSampleReferences();

        if (currentJob.getAutoGenerateJobNumber()) {
            currentJob.setJobNumber(getCurrentJobNumber());
        }
        selectedJobSample = new JobSample();

        setDirty(true);

        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public void editJobSample(ActionEvent event) {
        jobSampleDialogTabViewActiveIndex = 0;
        PrimeFacesUtils.openDialog(null, "jobSampleDialog", true, true, true, 600, 700);
    }

    public void openJobSampleDeleteConfirmDialog(ActionEvent event) {

        PrimeFacesUtils.openDialog(null, "jobSampleDeleteConfirmDialog", true, true, true, 90, 375);
    }

    public void doCopyJobSample() {

        selectedJobSample = new JobSample(selectedJobSample);
        selectedJobSample.setReferenceIndex(getCurrentNumberOfJobSamples());
        // Init sample    
        if (selectedJobSample.getSampleQuantity() == 1L) {
            selectedJobSample.setReference(BusinessEntityUtils.getAlphaCode(getCurrentNumberOfJobSamples()));
        } else {
            selectedJobSample.setReference(BusinessEntityUtils.getAlphaCode(getCurrentNumberOfJobSamples()) + "-"
                    + BusinessEntityUtils.getAlphaCode(getCurrentNumberOfJobSamples()
                            + selectedJobSample.getSampleQuantity() - 1));
        }

        jobSampleDialogTabViewActiveIndex = 0;

    }

    public void copyJobSample() {
        PrimeFacesUtils.openDialog(null, "jobSampleDialog", true, true, true, 600, 700);
    }

    public void cancelJobSampleDialogEdits() {
        // Restore backed up job sample        
        selectedJobSample.copy(selectedJobSampleBackup);

        RequestContext.getCurrentInstance().closeDialog(null);
    }

    private Long getCurrentNumberOfJobSamples() {
        if (currentJob.getNumberOfSamples() == null) {
            currentJob.setNumberOfSamples(0L);
            return currentJob.getNumberOfSamples();
        } else {
            return currentJob.getNumberOfSamples();
        }
    }

    /**
     * Checks maximum allowed samples and groups. Currently not used
     */
    public void checkNumberOfJobSamplesAndGroups() {
        EntityManager em = getEntityManager1();

        // setup context for client response
        RequestContext context = RequestContext.getCurrentInstance();
        // check for max sample
        int maxSamples = Integer.parseInt(SystemOption.findSystemOptionByName(em, "maximumJobSamples").getOptionValue());
        if (getCurrentNumberOfJobSamples() == maxSamples) {
            context.addCallbackParam("maxJobSamplesReached", true);
        }
        // check for max sample groups
        int maxGropus = Integer.parseInt(SystemOption.findSystemOptionByName(em, "maximumJobSampleGroups").getOptionValue());
        if (currentJob.getJobSamples().size() == maxGropus) {
            context.addCallbackParam("maxJobSampleGroupsReached", true);
        }
    }

    private void setNumberOfSamples() {
        currentJob.setNumberOfSamples(0L);
        for (int i = 0; i < currentJob.getJobSamples().size(); i++) { // find total
            if (currentJob.getJobSamples().get(i).getSampleQuantity() == null) {
                currentJob.getJobSamples().get(i).setSampleQuantity(1L);
            }
            currentJob.setNumberOfSamples(currentJob.getNumberOfSamples()
                    + currentJob.getJobSamples().get(i).getSampleQuantity());
        }
    }

    private void updateSampleReferences() {
        Long refIndex = 0L;

        ArrayList<JobSample> samplesCopy = new ArrayList<>(currentJob.getJobSamples());
        currentJob.getJobSamples().clear();

        for (JobSample jobSample : samplesCopy) {

            jobSample.setReferenceIndex(refIndex);
            if (jobSample.getSampleQuantity() == 1) {
                jobSample.setReference(BusinessEntityUtils.getAlphaCode(refIndex));
            } else {
                jobSample.setReference(BusinessEntityUtils.getAlphaCode(refIndex) + "-"
                        + BusinessEntityUtils.getAlphaCode(refIndex + jobSample.getSampleQuantity() - 1));
            }

            currentJob.getJobSamples().add(jobSample);

            refIndex = refIndex + jobSample.getSampleQuantity();

        }
    }

    public Job getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(Job currentJob) {
        this.currentJob = currentJob;
    }

    public JobSample getSelectedJobSample() {
        return selectedJobSample;
    }

    public void setSelectedJobSample(JobSample selectedJobSample) {
        this.selectedJobSample = selectedJobSample;
    }

    public String getCurrentJobNumber() {
        return Job.getJobNumber(currentJob, getEntityManager1());
    }

    public final EntityManager getEntityManager1() {
        return EMF1.createEntityManager();
    }
}
