/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

function checkWhichBrowser() {
//    var txt = "";
//
//    txt = navigator.appCodeName + "\n";
//    txt+= navigator.appName;
//    txt+= navigator.appVersion + "\n";
//    txt+= navigator.cookieEnabled + "\n";
//    txt+= navigator.platform + "\n";
//    txt+= navigator.userAgent + "\n";

    if (navigator.userAgent.toUpperCase().lastIndexOf("MSIE") !== -1) {
        //        window.open("http://localhost:8080/jmtsbv2/jmts/incompatiblebrowser.xhtml", "_self");
        incompatibleBrowserDialog.show();
    }

}

function handleSaveEntityRequest(xhr,status,args) {    
    
   if (args.entitySaved === undefined) {
        alert("An undefined error occured while saving.\nEnsure that all required fields are filled out and try again.");
        return;
    }
    if (!args.entitySaved) {
        alert("Please ensure that all required fields are filled out with valid values.");
        return;
    }    
   
}
