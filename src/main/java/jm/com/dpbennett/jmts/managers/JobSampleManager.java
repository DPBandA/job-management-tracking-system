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
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.Department;
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
    private Integer jobSampleDialogTabViewActiveIndex;
    private JobManagerUser user;

    /**
     * Creates a new instance of JobManagerBean
     */
    public JobSampleManager() {
        init();
    }

    private void init() {
        selectedJobSample = new JobSample();
        jobSampleDialogTabViewActiveIndex = 0;
        currentJob = null;
        selectedJobSampleBackup = null;
        user = null;
    }

    public void reset() {
        init();
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
        selectedJobSampleBackup = new JobSample(this.selectedJobSample);
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

    public void jobSampleDialogReturn() {
        if (getCurrentJob().getId() != null) {
            if (!isCurrentJobDirty() && getSelectedJobSample().getIsDirty()) {
                if (getCurrentJob().prepareAndSave(getEntityManager1(), getUser()).isSuccess()) {
                    getSelectedJobSample().setIsDirty(false);
                    PrimeFacesUtils.addMessage("Sample(s) and Job Saved", "This job and the edited/added sample(s) were saved", FacesMessage.SEVERITY_INFO);
                }
            } else if (isCurrentJobDirty() && getSelectedJobSample().getIsDirty()) {
                PrimeFacesUtils.addMessage("Sample(s) Added/Edited", "Save this job if you wish to keep the changes", FacesMessage.SEVERITY_WARN);
            } else if (isCurrentJobDirty() && !getSelectedJobSample().getIsDirty()) {
                PrimeFacesUtils.addMessage("Job to be Saved", "Sample(s) not edited but this job was previously edited but not saved", FacesMessage.SEVERITY_WARN);
            } else if (!isCurrentJobDirty() && !getSelectedJobSample().getIsDirty()) {
                // Nothing to do yet
            }
        }
    }

    /**
     * To be applied when sample if saved
     *
     * @param event
     */
    public void updateSampleQuantity(AjaxBehaviorEvent event) {
        if (hasFieldValueChange(event.getComponent().getId())) {
            getSelectedJobSample().setIsDirty(true);
            updateSampleReference();
        }
    }

    private void updateSampleReference() {
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
    }

    public void closeJobSampleDeleteConfirmDialog() {
        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public Integer getJobSampleDialogTabViewActiveIndex() {
        return jobSampleDialogTabViewActiveIndex;
    }

    public void setJobSampleDialogTabViewActiveIndex(Integer jobSampleDialogTabViewActiveIndex) {
        this.jobSampleDialogTabViewActiveIndex = jobSampleDialogTabViewActiveIndex;
    }

    public void updateSample(AjaxBehaviorEvent event) {
        if (event.getComponent() != null) {
            if (hasFieldValueChange(event.getComponent().getId())) {
                getSelectedJobSample().setIsDirty(true);
            }
        }
    }

    public void okJobSample() {
        EntityManager em = getEntityManager1();
        updateSampleReference();
        if (selectedJobSample.getIsToBeAdded()) {
            currentJob.getJobSamples().add(selectedJobSample);
        }

        selectedJobSample.setIsToBeAdded(false);

        setNumberOfSamples();

        updateSampleReferences();

        // Update department
        if (!currentJob.getDepartment().getName().equals("")) {
            Department department = Department.findDepartmentByName(em, currentJob.getDepartment().getName());
            if (department != null) {
                currentJob.setDepartment(department);
            }
        }
        // Update subcontracted department
        if (!currentJob.getSubContractedDepartment().getName().equals("")) {
            Department subContractedDepartment = Department.findDepartmentByName(em, currentJob.getSubContractedDepartment().getName());
            currentJob.setSubContractedDepartment(subContractedDepartment);
        }

        if (currentJob.getAutoGenerateJobNumber()) {
            currentJob.setJobNumber(getCurrentJobNumber());
        }
        jobSampleDialogTabViewActiveIndex = 0;

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

    public void setEditSelectedJobSample(JobSample selectedJobSample) {
        this.selectedJobSample = selectedJobSample;
        selectedJobSampleBackup = new JobSample(this.selectedJobSample);
        this.selectedJobSample.setIsToBeAdded(false);
        this.selectedJobSample.setIsDirty(false);
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

    public void setCopySelectedJobSample(JobSample selectedJobSample) {
        this.selectedJobSample = selectedJobSample;
        if (selectedJobSample != null) {
            selectedJobSampleBackup = new JobSample(this.selectedJobSample);
            doCopyJobSample();
            this.selectedJobSample.setIsToBeAdded(true);
            this.selectedJobSample.setIsDirty(false);
        }
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
        for (int i = 0; i < currentJob.getJobSamples().size(); i++) {
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

    private EntityManager getEntityManager1() {
        return EMF1.createEntityManager();
    }

    private Boolean hasFieldValueChange(String componentId) {
        switch (componentId) {
            case "sampleReference":
                if (!selectedJobSample.getReference().equals(selectedJobSampleBackup.getReference())) {
                    return true;
                }
                break;
            case "sampledBy":
                if (!selectedJobSample.getSampledBy().equals(selectedJobSampleBackup.getSampledBy())) {
                    return true;
                }
                break;
            case "dateSampled":
                if (!selectedJobSample.getDateSampled().equals(selectedJobSampleBackup.getDateSampled())) {
                    return true;
                }
                break;
            case "sampleQuantity":
                if (!selectedJobSample.getSampleQuantity().equals(selectedJobSampleBackup.getSampleQuantity())) {
                    return true;
                }
                break;
            case "productCommonName":
                if (!selectedJobSample.getName().equals(selectedJobSampleBackup.getName())) {
                    return true;
                }
                break;
            case "sampleCountryOfOrigin":
                if (!selectedJobSample.getCountryOfOrigin().equals(selectedJobSampleBackup.getCountryOfOrigin())) {
                    return true;
                }
                break;
            case "productBrand":
                if (!selectedJobSample.getProductBrand().equals(selectedJobSampleBackup.getProductBrand())) {
                    return true;
                }
                break;
            case "productModel":
                if (!selectedJobSample.getProductModel().equals(selectedJobSampleBackup.getProductModel())) {
                    return true;
                }
                break;
            case "productSerialNumber":
                if (!selectedJobSample.getProductSerialNumber().equals(selectedJobSampleBackup.getProductSerialNumber())) {
                    return true;
                }
                break;
            case "productCode":
                if (!selectedJobSample.getProductCode().equals(selectedJobSampleBackup.getProductCode())) {
                    return true;
                }
                break;
            case "sampleDescription":
                if (!selectedJobSample.getDescription().equals(selectedJobSampleBackup.getDescription())) {
                    return true;
                }
                break;
            case "productQuantity":
                if (!selectedJobSample.getQuantity().equals(selectedJobSampleBackup.getQuantity())) {
                    return true;
                }
                break;
            case "productUnitOfMeasure":
                if (!selectedJobSample.getUnitOfMeasure().equals(selectedJobSampleBackup.getUnitOfMeasure())) {
                    return true;
                }
                break;
            case "methodOfDisposal":
                if (!selectedJobSample.getMethodOfDisposal().equals(selectedJobSampleBackup.getMethodOfDisposal())) {
                    return true;
                }
                break;
            case "dateSampleReceived":
                if (!selectedJobSample.getDateReceived().equals(selectedJobSampleBackup.getDateReceived())) {
                    return true;
                }
                break;
            default:
                break;
        }

        return false;
    }
}
