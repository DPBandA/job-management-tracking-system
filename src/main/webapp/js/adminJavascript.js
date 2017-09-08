/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//<![CDATA[
function stopRKey(evt) {
    var evt = (evt) ? evt : ((event) ? event : null);
    var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
    if ((evt.keyCode == 13) && (node.type=="text")) {
        return false;
    }
}
document.onkeypress = stopRKey;

function handleLoginRequest(xhr,status,args) {
    if(args.validationFailed || !args.userLogggedIn) {
    //jQuery('#loginDialog').effect("shake", { times:3 }, 100);
    } else {
        loginDialog.hide();
        window.location.reload();
    }
}

function handleCreateJobRequest(xhr,status,args) {

    if(args.validationFailed) {
        alert("Validation failed!\nTry again or contact the System Administrator.");
    }
    else if(!args.jobCreated) {
        alert("An error occurred while creating a new job!\nTry again or contact the System Administrator.");
    }
    else {

}
}
function reloadCurrentPage() {
    connectionErrorAlertDialog.hide();
    window.location.reload();
}

function handleSaveSelectedUser(xhr,status,args) {
    if(args.validationFailed) {
        alert("Please provide valid values for all fields.");
        return;
    }
    if (args.userSaved === undefined) {
        alert("An undefined error occured while saving the user.\nEnsure that all required fields are filled out and try again.");
        return;
    }
    else if (!args.userSaved) {
        alert("Please ensure that all required fields are filled out with valid values.");
        return;
    }
}

function handleSaveSelectedEmployee(xhr,status,args) {
    if(args.validationFailed) {
        alert("Please provide valid values for all required fields.");
        return;
    }
    if (args.employeeSaved === undefined) {
        alert("An undefined error occured while saving the employee.\nEnsure that all required fields are filled out and try again.");
        return;
    }
    else if (!args.employeeSaved) {
        alert("Please ensure that all required fields are filled out with valid values.");
        return;
    }
    

}

function handleSaveSelectedDepartment(xhr,status,args) {
    if(args.validationFailed) {
        alert("Please provide valid values for all required fields.");
        return;
    }
    if (args.departmentSaved === undefined) {
        alert("An undefined error occured while saving the department.\nEnsure that all required fields are filled out and try again.");
        return;
    }
    else if (!args.departmentSaved) {
        alert("Please ensure that all required fields are filled out with valid values.");
        return;
    }
   
}

// The following code keeps the session alive
//var timeout = 15000; /* 240000 = 4 minutes */
//function keepAlive() {
//    keepAliveRequest();
//
//    setTimeout("keepAlive()", timeout);
//}
//
//setTimeout("keepAlive()", timeout);


