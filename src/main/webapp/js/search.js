var SEARCH = {    
    getSearchParameters: function() {
        return JSON.stringify({
            "searchType": $('#searchTypeId').puidropdown('getSelectedValue'),
            "dateField": $('#dateFieldId').puidropdown('getSelectedValue'),
            "searchText": $('#searchTextId').val(),
            "startDate": $('#startDatepickerId').datepicker('getDate'),
            "endDate": $('#endDatepickerId').datepicker('getDate')}); 
    },
    initSearchPanel: function(searchType, dateField, datePeriod) {
        $('#searchTypeId').puidropdown('selectValue', searchType);
        $('#dateFieldId').puidropdown('selectValue', dateField);
        $('#datePeriodId').puidropdown('selectValue', datePeriod);
        this.initDatePeriod(datePeriod);
    },
    initDatePeriod: function(selectedDatePeriod) {
        if (selectedDatePeriod === "This month") {
            $("#startDatepickerId").datepicker("setDate", UTILS.getFirstDateOfCurrentMonth());
            $("#endDatepickerId").datepicker("setDate", UTILS.getLastDateOfCurrentMonth());
            $("#startDatepickerId").datepicker("option", "disabled", true);
            $("#endDatepickerId").datepicker("option", "disabled", true);
        }
        else if (selectedDatePeriod === 'Last month') {
            $("#startDatepickerId").datepicker("option", "disabled", true);
            $("#endDatepickerId").datepicker("option", "disabled", true);
        }
        else if (selectedDatePeriod === 'Custom') {
            $("#startDatepickerId").datepicker("option", "disabled", false);
            $("#endDatepickerId").datepicker("option", "disabled", false);
        }

    }
};

