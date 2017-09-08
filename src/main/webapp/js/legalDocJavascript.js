/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//<![CDATA[
function stopRKey() {
    var evt = (evt) ? evt : ((event) ? event : null);
    var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);

    if ((evt.keyCode == 13) && (node.type=="text")) {
        return false;
    }
    else {
        return true;
    }
}
document.onkeypress = stopRKey;

function handleLoginRequest(xhr,status,args) {
    if(args.validationFailed || !args.userLogggedIn) {   
    } else {
        loginDialog.hide();
    }
}


function reloadCurrentPage() {
    connectionErrorAlertDialog.hide();
    window.location.reload();
}

function handleSaveLegalDocumentRequest(xhr,status,args) {
    if(args.valueRequired) {
        valuesRequiredDialog.show();
        return;
    }
    if (args.legalDocumentSaved == undefined) {
        unknownErrorDialog.show();
        return;
    }
    else if (!args.legalDocumentSaved) {
        unknownErrorDialog.show();
        return;
    }
    // all is assumed well
    documentDialog.hide();

}

// The following code keeps the session alive
var timeout = 240000; /* 240000 = 4 minutes */
function keepAlive() {
    keepAliveRequest();

    setTimeout("keepAlive()", timeout);
}

setTimeout("keepAlive()", timeout);

//]]>
