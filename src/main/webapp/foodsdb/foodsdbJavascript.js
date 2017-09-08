/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//<![CDATA[
// This function and the following line of code handles the
// enter key as appropriate
function handleEnterKey(evt) {

    var evt2 = (evt) ? evt : ((event) ? event : null);
    var node = (evt2.target) ? evt2.target : ((evt2.srcElement) ? evt2.srcElement : null);

    if ((evt2.keyCode == 13) && (node.type=="text")) {
        if (node.id == 'jobSearchForm:searchText_input') {
            document.forms['jobSearchForm']['jobSearchForm:jobSearch'].click();
            return true 
        }
        else {
            return false;
        }
    }
    else {
        return true; 
    }
}

document.onkeypress = handleEnterKey;

function loggedOnUser() {
    var WinNetwork = new ActiveXObject("WScript.Network");
    alert(WinNetwork.userName);
}
function reloadCurrentPage() {
    connectionErrorAlertDialog.hide();
    window.location.reload();
}
function enableDateSearchFields(source) {
    //source.form[source.form.id + ":searchText"].value = "dsdsd";
    source.form[source.form.id + ":startSearchDate"].disabled = true;
}

function handleLoginRequest(xhr,status,args) {
    if(args.validationFailed || !args.userLoggedIn) {
        //jQuery('#dialog').effect("shake", { times:3 }, 100);
        loginDialog.show();
    } else {
        loginDialog.hide();
    //        updateHeaderForm();
    //        updateJobEntryForm();
    //        window.location.reload();
    }
}

function handleSaveClientRequest(xhr,status,args) {
    if(args.validationFailed) {
        requiredValuesAlertDialog.show();
        return;
    }
    if(args.clientExist) {
        clientExistsDialog.show();
        return;
    }
    if(args.invalidClient) {
        invalidClientDialog.show();
        return;
    }
    if (args.clientSaved == undefined) {
        undefinedErrorDialog.show();
        return;
    }
    else if (!args.clientSaved) {
        undefinedErrorDialog.show();
    }

    clientDialog.hide();
    updateSystemInfo();
    updateJobEntryTabClientName();
}

function handleSaveEntityRequest(xhr,status,args) {
    if(args.validationFailed) {
        requiredValuesAlertDialog.show();
        return;
    }
    if(args.invalidName) {
        invalidNameDialog.show();
        return;
    }
    if(args.entityExists) {
        entityExistsDialog.show();
        return;
    }
    if (args.entityExists == undefined) {
        undefinedErrorDialog.show();
        return;
    }
}

function handleSaveAndCloseFactoryDialog(xhr,status,args) {
    if(args.validationFailed) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("Please provide valid values for all job fields.");
        return;
    }
    if(args.invalidBusinessOffice) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("Please select a business office.");
        return;
    }
    if(args.existingJobNumber) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("Please enter a unique job number.");
        return;
    }
    if(args.invalidJobNumber) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("Please enter a valid job number.");
        return;
    }
    if(args.invalidClient) {
        jobSaveConfirm.hide();
        jobDialog.show();
        invalidClientDialog.show();
        return;
    }
    if(args.invalidDepartment) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("Please select a department.");
        return;
    }
    if(args.duplicateDepartments) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("Duplicate departments detected.");
        return;
    }
    if(args.invalidAssignee) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("Please select an assignee.");
        return;
    }
    if(args.invalidJobCategory) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("Please select a category.");
        return;
    }
    if(args.invalidJobSubCategory) {
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
    }
    else if (!args.jobSaved) {
        jobSaveConfirm.hide();
        jobDialog.show();
        alert("The current job could not be saved.\nEnsure that all required fields are filled out and try again.");
        return;
    }

    jobDialog.hide();
    jobSaveConfirm.hide();
}

function handleCloseFactoryDialog(xhr,status,args) {
    
    if(args.foodFactoryDirty) {
        formDataChangeDialog.show();
    }
    else {
        foodFactoryDialog.hide();       
    }
    longProcessDialogVar.hide();
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
//createFreshJob();   
}

//]]>