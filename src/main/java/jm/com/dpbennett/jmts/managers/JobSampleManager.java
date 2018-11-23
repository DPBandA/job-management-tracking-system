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
import javax.faces.application.FacesMessage;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
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
import jm.com.dpbennett.wal.utils.BeanUtils;
import jm.com.dpbennett.wal.utils.PrimeFacesUtils;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Desmond Bennett
 */
public class JobSampleManager implements Serializable, BusinessEntityManagement {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    private JobSample selectedJobSample;
    private JobSample selectedJobSampleBackup;
    private Integer jobSampleDialogTabViewActiveIndex;
    private JobManager jobManager;

    /**
     * Creates a new instance of JobManagerBean
     */
    public JobSampleManager() {
        init();
    }

    private void init() {
        selectedJobSample = new JobSample();
        jobSampleDialogTabViewActiveIndex = 0;
        selectedJobSampleBackup = null;
    }

    /*
     * NB: Methods to be put in database and not hard coded.
     */
    public List getMethodsOfDisposal() {
        ArrayList methods = new ArrayList();

        // tk to be replaced by the user's organization
        // TK validate reason for doing this.
        String sysOption
                = (String) SystemOption.getOptionValueObject(getEntityManager1(),
                        "organizationName");

        methods.add(new SelectItem("1", "Collected by the client within 30 days"));
        if (sysOption != null) {
            methods.add(new SelectItem("2", "Disposed of by " + sysOption));
        } else {
            methods.add(new SelectItem("2", "Disposed of by us"));
        }
        methods.add(new SelectItem("3", "To be determined"));

        return methods;
    }

    public JobManager getJobManager() {
        if (jobManager == null) {
            jobManager = BeanUtils.findBean("jobManager");
        }
        return jobManager;
    }

    public void reset() {
        init();
    }

    public Boolean isSamplesDirty() {
        Boolean dirty = false;

        for (JobSample jobSample : getCurrentJob().getJobSamples()) {
            dirty = dirty || jobSample.getIsDirty();
        }

        return dirty;
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
            selectedJobSample.setJobId(getCurrentJob().getId());
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
            PrimeFacesUtils.openDialog(null, "/job/sample/jobSampleDialog", true, true, true, 600, 700);
        }
    }

    public JobManagerUser getUser() {
        return getJobManager().getUser();
    }

    @Override
    public void setIsDirty(Boolean dirty) {
        selectedJobSample.setIsDirty(dirty);
    }

    @Override
    public Boolean getIsDirty() {
        return selectedJobSample.getIsDirty();
    }

    public Boolean isCurrentJobDirty() {
        return getCurrentJob().getIsDirty();
    }

    public void setCurrentJobDirty() {
        getCurrentJob().setIsDirty(true);
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
        PrimeFaces.current().dialog().closeDynamic(null);
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
            getCurrentJob().getJobSamples().add(selectedJobSample);
        }

        selectedJobSample.setIsToBeAdded(false);

        setNumberOfSamples();

        updateSampleReferences();

        // Update department
        if (!getCurrentJob().getDepartment().getName().equals("")) {
            Department department = Department.findDepartmentByName(em, getCurrentJob().getDepartment().getName());
            if (department != null) {
                getCurrentJob().setDepartment(department);
            }
        }
        // Update subcontracted department
        if (!getCurrentJob().getSubContractedDepartment().getName().equals("")) {
            Department subContractedDepartment = Department.findDepartmentByName(em, getCurrentJob().getSubContractedDepartment().getName());
            getCurrentJob().setSubContractedDepartment(subContractedDepartment);
        }

        if (getCurrentJob().getAutoGenerateJobNumber()) {
            getCurrentJob().setJobNumber(getCurrentJobNumber());
        }
        jobSampleDialogTabViewActiveIndex = 0;

        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void deleteJobSample() {

        // update number of samples
        if ((getCurrentJob().getNumberOfSamples() - selectedJobSample.getSampleQuantity()) > 0) {
            getCurrentJob().setNumberOfSamples(getCurrentJob().getNumberOfSamples() - selectedJobSample.getSampleQuantity());
        } else {
            getCurrentJob().setNumberOfSamples(0L);
        }

        List<JobSample> samples = getCurrentJob().getJobSamples();
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

        if (getCurrentJob().getAutoGenerateJobNumber()) {
            getCurrentJob().setJobNumber(getCurrentJobNumber());
        }

        // Do job save if possible...
        if (getCurrentJob().getId() != null
                && getCurrentJob().prepareAndSave(getEntityManager1(), getUser()).isSuccess()) {
            PrimeFacesUtils.addMessage("Job Saved",
                    "Sample(s) deleted and the job was saved", FacesMessage.SEVERITY_INFO);
        } else {
            setCurrentJobDirty();
            PrimeFacesUtils.addMessage("Job NOT Saved",
                    "Sample(s) deleted but the job was not saved", FacesMessage.SEVERITY_WARN);
        }

        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void editJobSample(ActionEvent event) {
        jobSampleDialogTabViewActiveIndex = 0;
        PrimeFacesUtils.openDialog(null, "/job/sample/jobSampleDialog", true, true, true, 600, 700);
    }

    public void setEditSelectedJobSample(JobSample selectedJobSample) {

        // Get the saved sample for edit if it exists
        if (selectedJobSample.getId() != null) {
            EntityManager em = getEntityManager1();
            this.selectedJobSample = JobSample.findJobSampleById(em, selectedJobSample.getId());
            em.refresh(this.selectedJobSample);
            getCurrentJob().getJobSamples().remove(selectedJobSample);
            getCurrentJob().getJobSamples().add(this.selectedJobSample);
        } else {
            this.selectedJobSample = selectedJobSample;
        }

        selectedJobSampleBackup = new JobSample(this.selectedJobSample);
        this.selectedJobSample.setIsToBeAdded(false);
        this.selectedJobSample.setIsDirty(false);
    }

    public void openJobSampleDeleteConfirmDialog(ActionEvent event) {

        PrimeFacesUtils.openDialog(null, "/job/sample/jobSampleDeleteConfirmDialog", true, true, true, 110, 375);
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
        PrimeFacesUtils.openDialog(null, "/job/sample/jobSampleDialog", true, true, true, 600, 700);
    }

    public void setCopySelectedJobSample(JobSample selectedJobSample) {
        this.selectedJobSample = selectedJobSample;
        if (selectedJobSample != null) {
            selectedJobSampleBackup = new JobSample(this.selectedJobSample);
            doCopyJobSample();
            this.selectedJobSample.setIsToBeAdded(true);
            this.selectedJobSample.setIsDirty(true);
        }
    }

    public void cancelJobSampleDialogEdits() {
        // Restore backed up job sample        
        selectedJobSample.copy(selectedJobSampleBackup);

        PrimeFaces.current().dialog().closeDynamic(null);
    }

    private Long getCurrentNumberOfJobSamples() {
        if (getCurrentJob().getNumberOfSamples() == null) {
            getCurrentJob().setNumberOfSamples(0L);
            return getCurrentJob().getNumberOfSamples();
        } else {
            return getCurrentJob().getNumberOfSamples();
        }
    }

    /**
     * Checks maximum allowed samples and groups. Currently not used
     */
    public void checkNumberOfJobSamplesAndGroups() {
        EntityManager em = getEntityManager1();

        // check for max sample
        int maxSamples = (Integer) SystemOption.getOptionValueObject(em, "maximumJobSamples");
        if (getCurrentNumberOfJobSamples() == maxSamples) {
            PrimeFaces.current().ajax().addCallbackParam("maxJobSamplesReached", true);
        }
        // check for max sample groups
        int maxGropus = (Integer) SystemOption.getOptionValueObject(em, "maximumJobSampleGroups");
        if (getCurrentJob().getJobSamples().size() == maxGropus) {
            PrimeFaces.current().ajax().addCallbackParam("maxJobSampleGroupsReached", true);
        }
    }

    private void setNumberOfSamples() {
        getCurrentJob().setNumberOfSamples(0L);
        for (int i = 0; i < getCurrentJob().getJobSamples().size(); i++) {
            if (getCurrentJob().getJobSamples().get(i).getSampleQuantity() == null) {
                getCurrentJob().getJobSamples().get(i).setSampleQuantity(1L);
            }
            getCurrentJob().setNumberOfSamples(getCurrentJob().getNumberOfSamples()
                    + getCurrentJob().getJobSamples().get(i).getSampleQuantity());
        }
    }

    private void updateSampleReferences() {
        Long refIndex = 0L;

        ArrayList<JobSample> samplesCopy = new ArrayList<>(getCurrentJob().getJobSamples());
        getCurrentJob().getJobSamples().clear();

        for (JobSample jobSample : samplesCopy) {

            jobSample.setReferenceIndex(refIndex);
            if (jobSample.getSampleQuantity() == 1) {
                jobSample.setReference(BusinessEntityUtils.getAlphaCode(refIndex));
            } else {
                jobSample.setReference(BusinessEntityUtils.getAlphaCode(refIndex) + "-"
                        + BusinessEntityUtils.getAlphaCode(refIndex + jobSample.getSampleQuantity() - 1));
            }

            getCurrentJob().getJobSamples().add(jobSample);

            refIndex = refIndex + jobSample.getSampleQuantity();

        }
    }

    public Job getCurrentJob() {
        return getJobManager().getCurrentJob();
    }

    public JobSample getSelectedJobSample() {
        return selectedJobSample;
    }

    public void setSelectedJobSample(JobSample selectedJobSample) {
        this.selectedJobSample = selectedJobSample;
    }

    public String getCurrentJobNumber() {
        return Job.getJobNumber(getCurrentJob(), getEntityManager1());
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
