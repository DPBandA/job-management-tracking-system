var UTILS = {
    getDaysInMonth: function(iMonth, iYear) {
        return 32 - new Date(iYear, iMonth, 32).getDate();
    },
    getCurrentYear: function() {
        var today = new Date();
        return today.getFullYear();
    },
    getCurrentDayOfMonth: function() {
        var today = new Date();
        return today.getDate();
    },
    getFirstDateOfCurrentMonth: function() {
        var date = new Date();
        return new Date(date.getFullYear(), date.getMonth(), 1);
    },
    getLastDateOfCurrentMonth: function() {
        var date = new Date();
        return new Date(date.getFullYear(), date.getMonth() + 1, 0);
    },
    displayMessageDialog: function(title, message) {
        $('#messageDialogId').dialog("option", "title", title);
        $('#messageDialogMessage').text(message);
        $('#messageDialogId').dialog('open');        
    }
};
