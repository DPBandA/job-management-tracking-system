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
function loggedOnUser() {
    var WinNetwork = new ActiveXObject("WScript.Network");
    alert(WinNetwork.userName);
}
function reloadCurrentPage() {
    connectionErrorAlertDialog.hide();
    window.location.reload();
}
function enableDateSearchFields(source) {
    source.form[source.form.id + ":startSearchDate"].disabled = true;
}

function handleLoginRequest(xhr, status, args) {
    if (args.validationFailed || !args.userLoggedIn) {
        loginDialog.show();
    } else {
        loginDialog.hide();
    }
}

function handleSaveClientRequest(xhr, status, args) {
    if (args.validationFailed) {
        requiredValuesAlertDialog.show();
        return;
    }
    if (args.clientExist) {
        clientExistsDialog.show();
        return;
    }
    if (args.invalidClient) {
        invalidClientDialog.show();
        return;
    }
    if (args.clientSaved == undefined) {
        undefinedErrorDialog.show();
        return;
    } else if (!args.clientSaved) {
        undefinedErrorDialog.show();
    }

    clientDialog.hide();
}

function handleSaveAndCloseJobDialog(xhr, status, args) {
    if (args.validationFailed) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("Please provide valid values for all job fields.");
        return;
    }
    if (args.invalidBusinessOffice) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("Please select a business office.");
        return;
    }
    if (args.existingJobNumber) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("Please enter a unique job number.");
        return;
    }
    if (args.invalidJobNumber) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("Please enter a valid job number.");
        return;
    }
    if (args.invalidClient) {
        jobSaveConfirm.hide();
        jobDialog.show();
        invalidClientDialog.show();
        return;
    }
    if (args.invalidDepartment) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("Please select a department.");
        return;
    }
    if (args.duplicateDepartments) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("Duplicate departments detected.");
        return;
    }
    if (args.invalidAssignee) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("Please select an assignee.");
        return;
    }
    if (args.invalidJobCategory) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("Please select a category.");
        return;
    }
    if (args.invalidJobSubCategory) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("Please select a subcategory.");
        return;
    }
    if (args.jobSaved == undefined) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("An undefined error occured while saving the job.\nEnsure that all required fields are filled out and try again.");
        return;
    } else if (!args.jobSaved) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("The current job could not be saved.\nEnsure that all required fields are filled out and try again.");
        return;
    }

    jobDialog.hide();
    jobSaveConfirm.hide();
}

function handleSaveJobRequest(xhr, status, args) {
    if (args.validationFailed) {
        alert("Please provide valid values for all job fields.");
        return;
    }
    if (args.invalidBusinessOffice) {
        alert("Please select a business office.");
        return;
    }
    if (args.existingJobNumber) {
        alert("Please enter a unique job number.");
        return;
    }
    if (args.invalidJobNumber) {
        alert("Please enter a valid job number.");
        return;
    }
    if (args.invalidClient) {
        //alert("Please select or enter a valid client name.");
        invalidClientDialog.show();
        return;
    }
    if (args.invalidDepartment) {
        alert("Please select a department.");
        return;
    }
    if (args.duplicateDepartments) {
        alert("Duplicate departments detected.");
        return;
    }
    if (args.invalidAssignee) {
        alert("Please select an assignee.");
        return;
    }
    if (args.invalidJobCategory) {
        alert("Please select a category.");
        return;
    }
    if (args.invalidJobSubCategory) {
        alert("Please select a subcategory.");
        return;
    }
    if (args.jobSaved == undefined) {
        alert("An undefined error occured while saving the job.\nEnsure that all required fields are filled out and try again.");
        return;
    } else if (!args.jobSaved) {
        alert("The current job could not be saved.\nEnsure that all required fields are filled out and try again.");
        return;
    }

    jobSaveConfirm.hide();
}

function handleSaveJobCostingAndPaymentRequest(xhr, status, args) {
    if (args.validationFailed) {
        alert("Please provide valid values for all fields.");
        return;
    }
    if (args.invalidJob) {
        alert("Please enter and save this job before creating a job costing.");
        return;
    }
    if (args.invalidJobDescription) {
        alert("Please enter a description for the job.");
        return;
    }
    if (args.invalidReportNumber) {
        alert("Please enter a valid report number.");
        return;
    }
    // last check
    if (args.jobCostingAndPaymentSaved === undefined) {
        alert("An undefined error occured while saving the job costing.\nEnsure that all required fields are filled out and try again.");
        return;
    } else if (!args.jobCostingAndPaymentSaved) {
        alert("The job costing could not be saved.\nEnsure that all required fields are filled out and try again.");
        return;
    }

}

function handleCreateJobRequest(xhr, status, args) {
    jobDialog.show();
}

function handleCloseJobDetailTab(xhr, status, args) {

    if (args.isDirty) {
        jobDetailTabCloseConfirmation.show();
    } else {
        mainTabViewVar.remove(mainTabViewVar.getActiveIndex());
        mainTabViewVar.select(0);
    }
}

function handleCreateJobCopy(xhr, status, args) {

    if (args.jobDirty) {
        jobModifiedDialog.show();
        return;
    } else {
        jobModifiedDialog.hide();
    }
    if (args.jobNotSaved) {
        jobNotSavedDialog.show();
        return;
    } else {
        jobNotSavedDialog.hide();
    }

    if (args.validationFailed) {
        alert("Validation failed!\nTry again or contact the System Administrator.");
        return;
    }
    if (!args.jobCreated) {
        alert("An error occurred while copying job!\nTry again or contact the System Administrator.");
        return;
    } else {
        // Show growl instead
        return;
    }
}

function handleLoadJobRequest(xhr, status, args) {
    jobDialog.show();
}

function handleWorkProgressUpdate(xhr, status, args) {
    if (args.jobNotStarted) {
        alert("This action cannot be completed because the job has not yet started.");
    }

}

function gotoURL(url) {
    popup = window.open(url, "popup",
            "toolbar=no,menubar=yes,scrollbars=yes,resizable=yes");
    popup.focus();
}

// The following code keeps the session alive
var timeout = 240000; /* 240000 = 4 minutes */
function keepAlive() {
    keepAliveRequest();

    setTimeout("keepAlive()", timeout);
}

setTimeout("keepAlive()", timeout);

// used to reset forms and do loging when page is reloaded
function doLogin() {
    doLoginRequest();
}

function refreshScreen() {

}

function handleSavePetrolCompany(xhr, status, args) {
    if (args.validationFailed) {
        requiredValuesAlertDialog.show();
    } else if (!args.petrolCompanySaved) {
        unknownErrorDialog.show();
    } else {
        petrolCompanyDialog.hide();
    }
}

function handleSavePetrolStation(xhr, status, args) {
    if (args.validationFailed) {
        alertDialog.show();
    } else {
        petrolStationDialog.hide();
    }
}

function handleSaveScaleCompany(xhr, status, args) {
    if (args.validationFailed) {
        scaleCompanyDialog.show();
    } else {
        scaleCompanyDialog.hide();
    }
}

function handleSavePetrolPumpNozzle(xhr, status, args) {
    if (args.validationFailed) {
        alertDialog.show();
    } else if (args.invalidIssuedDate) {
        issueDateAlertDialog.show(); //tk show seal detail dialog.
    } else {
        petrolPumpNozzleDialog.hide();
    }
}

function handleSavePetrolPump(xhr, status, args) {
    if (args.validationFailed) {
        petrolPumpDialog.show();
    } else {
        petrolPumpDialog.hide();
    }
}

function handleUpdatePetrolCompany(xhr, status, args) {
    if (args.petrolCompanyExists) {
        petrolCompanyDialog.show();
    } else {
        petrolCompanyNonexistentConfirm.show();
    }
}

function handleUpdatePetrolStation(xhr, status, args) {
    if (args.petrolStationExists) {
        petrolStationDialog.show();
    } else {
        petrolCompanyNonexistentConfirm.show();
    }
}

function handleCreatePetrolPump(xhr, status, args) {
    if (args.petrolStationExists) {
        petrolPumpDialog.show();
    } else {
        createNewPetrolPumpAlert.show();
    }
}

function handleSaveTestData(xhr, status, args) {
    if (args.testDataSaved) {
        petrolPumpNozzleTestDialog.hide();
    } else {
        unknownErrorDialog.show();
    }
}

function handleSaveTestFormData(xhr, status, args) {
    if (args.testDataSaved) {
        petrolPumpNozzleTestDialog.hide();
    } else {
        unknownErrorDialog.show();
    }
}

function handleSaveScale(xhr, status, args) {
    if (args.scaleSaved) {
        scaleDialog.hide();
    } else {
        unknownErrorDialog.show();
    }
}

function handleCreateJob(xhr, status, args) {
    if (mainTabViewVar.getLength() >= 1) {
        mainTabViewVar.select(1);
    } else {
        mainTabViewVar.select(0);
    }
    jobFormTabVar.select(0);
}

