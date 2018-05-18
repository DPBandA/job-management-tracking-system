/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//<![CDATA[


function handleSaveClientRequest(xhr,status,args) {

}

function handleSaveEntityRequest(xhr,status,args) {
    longProcessDialogVar.hide();
}

function handleLoginRequest(xhr,status,args) {
    if(args.validationFailed || !args.userLogggedIn) {
        //jQuery('#dialog').effect("shake", { times:3 }, 100);
        loginDialog.show();
    } else {
        loginDialog.hide();
    }
}

// The following code keeps the session alive
var timeout = 240000; /* 240000 = 4 minutes */
function keepAlive() {
    keepAliveRequest();

    setTimeout("keepAlive()", timeout);
}

setTimeout("keepAlive()", timeout);

function checkForConnectionLife(xhr,status,args) {
    if (args.isConnectionLive == undefined) {        
        connectionErrorDialog.show();
        longProcessDialogVar.hide();
    }   
    else {
        longProcessDialogVar.hide();
    }
}

function handleCreateNewMarketSurvey(xhr,status,args) {
    if (args.isConnectionLive == undefined) {        
        connectionErrorDialog.show();
        longProcessDialogVar.hide();
    }   
    else {
        marketProductSurveyDialog.show();
        longProcessDialogVar.hide();
    }
}

function handleCreateNewComplianceSurvey(xhr,status,args) {
    if (args.isConnectionLive == undefined) {        
        connectionErrorDialog.show();
        longProcessDialogVar.hide();
    }   
    else {
        ComplianceSurveyDialog.show();
        longProcessDialogVar.hide();
    }
}

function handleCreateNewPortOfEntryDetention(xhr,status,args) {
    if (args.isConnectionLive == undefined) {        
        connectionErrorDialog.show();
        longProcessDialogVar.hide();
    }   
    else {
        portOfEntryDetentionDialog.show();
        longProcessDialogVar.hide();
    }
}

function handleCreateNewSampleRequest(xhr,status,args) {
    if (args.isConnectionLive == undefined) {        
        connectionErrorDialog.show();
        longProcessDialogVar.hide();
    }   
    else {
        sampleRequestDialog.show();
        longProcessDialogVar.hide();
    }
}

function showStatus() {
    longProcessDialogVar.show();
}

function hideStatus() {
    longProcessDialogVar.hide();
}

//]]>

