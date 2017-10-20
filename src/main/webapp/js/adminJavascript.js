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



