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
