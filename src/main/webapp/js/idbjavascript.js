/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


window.indexedDB = window.indexedDB || window.webkitIndexedDB || window.mozIndexedDB;

var dbRequest = indexedDB.open("myDatabase");
var database;
dbRequest.onsuccess = function(event) {
    // The database object appears as event.result
    database = event.result;
    alert('yes');
};

dbRequest.onerror = function(event) {
    // Manage error
     alert('no');
};