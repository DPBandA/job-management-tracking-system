var JMTS = {
    dateFormat: "M dd, yy",
    currentJob: {},
    user: {},
    receivedDateFormat: "yy-mm-dd",
    dirty: false,
    loggedIn: false,
    setDirty: function(dirty) {
        this.dirty = dirty;
        console.log('dirty: ' + this.dirty); //tk
    },
    isDirty: function() {
        return this.dirty;
    },
    login: function() {
        if ($('#usernameId').val() === '') {
            //UTILS.displayMessageDialog("No Username", "Please enter a valid username");
            $('#loginDialogMessage').text("Please enter a valid username/password:");
            $('#loginDialogMessage').css({color: '#FF0000'});
        }
        else if ($('#passwordId').val() === '') {
            //UTILS.displayMessageDialog("No password", "Please enter a valid password");
            $('#loginDialogMessage').text("Please enter a valid username/password:");
            $('#loginDialogMessage').css({color: '#FF0000'});
        }
        else {
            $('#loginDialogMessage').text("Please provide your login details below:");
            $('#loginDialogMessage').css({color: '#000000'})
            $('#loginDialogId').puidialog('hide');
            // tk only if successful  
            $('#mainTabView').show();
            myLayout.open('north');
            myLayout.open('west');
            $('#usernameId').val('');
            $('#passwordId').val('');
            this.loggedIn = true;
        }
    },
    logout: function() {

        myLayout.close('west');
        myLayout.close('north');
        $('#mainTabView').hide();

        // Could be done elsewhere
        SEARCH.initSearchPanel('General', 'dateSubmitted', 'This month');

        $('#loginDialogId').puidialog('show');
        this.loggedIn = false;
    },
    displayJobDialog: function(jobData) {
        $.ajax({
            url: this.getJobManagerRWSURL() + '/' + jobData.id,
            dataType: "json",
            type: 'GET',
            beforeSend: function() {
                $("#wait").css("display", "block");
            },
            error: function(jqxhr, status, errorMsg) {
                if (errorMsg === 'Not Found') {
                    UTILS.displayMessageDialog("Job Display Error", "A connection error occurred!");
                }
                else if (errorMsg === '') {
                    UTILS.displayMessageDialog("Job Display Error", "A connection error occurred!");
                }
                else {
                    UTILS.displayMessageDialog("Job Display Error", errorMsg);
                }
            }
            ,
            success: function(job)
            {
                try {
                    JMTS.currentJob = job;
                    // Init dialog components 
                    // General tab
                    $('#businessOfficeId').val(job.businessOffice.name);
                    $('#jobNumberId').val(job.jobNumber);
                    $('#autoJobNumberId').puicheckbox(job.autoGenerateJobNumber ? 'check' : 'uncheck');
                    $('#clientId').val(job.client.name);
                    $('#dateSubmittedDatepickerId').datepicker('setDate', $.datepicker.parseDate(JMTS.receivedDateFormat, job.jobStatusAndTracking.dateSubmitted.split('T')[0]));
                    $('#departmentId').val(job.department.name);
                    $('#subContractedDepartmentId').val(job.subContractedDepartment.name);
                    $('#assigneeId').val(job.assignedTo.name);
                    $('#jobSamplesTableId').puidatatable('option', 'datasource', JMTS.getJobSampleData(job.jobSamples));
                    // Job costing tab
                    $('#purchaseOrderNumberId').val(job.jobCostingAndPayment.purchaseOrderNumber);
                    JMTS.setDirty(false);
                    // Select first tab and show dialog 
                    $('#jobDetailTabViewId').puitabview('select', 0);
                    $('#jobDetailDialogId').puidialog('show');
                }
                catch (e) {
                    console.log(e);
                }
            },
            complete: function() {
                $("#wait").css("display", "none");
            }
        });
    },
    getJobData: function(data) {
        var jobs = [];
        if (data !== undefined) {
            if (data.length !== undefined) { // Crude way to determine if this is an array
                for (i = 0; i < data.length; i++) {
                    jobs.push(
                            {
                                id: data[i].id,
                                jobNumber: data[i].jobNumber,
                                client: data[i].client.name,
                                received: data[i].jobStatusAndTracking.dateSubmitted !== undefined ?
                                        $.datepicker.formatDate(this.dateFormat, $.datepicker.parseDate(this.receivedDateFormat, data[i].jobStatusAndTracking.dateSubmitted.split('T')[0])) : "",
                                completed: data[i].jobStatusAndTracking.dateOfCompletion !== undefined ?
                                        $.datepicker.formatDate(this.dateFormat, $.datepicker.parseDate(this.receivedDateFormat, data[i].jobStatusAndTracking.dateOfCompletion.split('T')[0])) : "",
                                progress: data[i].jobStatusAndTracking.workProgress

                            });
                }
            }
            else {
                jobs.push(
                        {
                            id: data.id,
                            jobNumber: data.jobNumber,
                            client: data.client.name,
                            received: data.jobStatusAndTracking.dateSubmitted !== undefined ?
                                    $.datepicker.formatDate(this.dateFormat, $.datepicker.parseDate(this.receivedDateFormat, data.jobStatusAndTracking.dateSubmitted.split('T')[0])) : "",
                            completed: data.jobStatusAndTracking.dateOfCompletion !== undefined ?
                                    $.datepicker.formatDate(this.dateFormat, $.datepicker.parseDate(this.receivedDateFormat, data.jobStatusAndTracking.dateOfCompletion.split('T')[0])) : "",
                            progress: data.jobStatusAndTracking.workProgress

                        });
            }
        }

        this.jobsFound = jobs.length;
        return jobs;
    },
    getJobSampleData: function(data) {
        var jobSamples = [];
        if (data !== undefined) {
            if (data.length !== undefined) { // Crude way to determine if this is an array
                for (i = 0; i < data.length; i++) {
                    jobSamples.push(
                            {
                                reference: data[i].reference,
                                description: data[i].description
                            });
                }
            }
            else {
                jobSamples.push(
                        {
                            reference: data.reference,
                            description: data.description

                        });
            }
        }

        return jobSamples;
    },
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
    initDatePeriod: function(selectedDatePeriod) {

        if (selectedDatePeriod === "This month") {
            var date = new Date();
            var firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
            var lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);
            $("#startDatepickerId").datepicker("setDate", firstDay);
            $("#endDatepickerId").datepicker("setDate", lastDay);
            $("#startDatepickerId").datepicker("option", "disabled", true);
            $("#endDatepickerId").datepicker("option", "disabled", true);
        }
        else if (selectedDatePeriod === 'Last month') {
//                console.log('init dateperiod for ...: ' + selectedDatePeriod);
            $("#startDatepickerId").datepicker("setDate", "Oct 12, 2013");
            $("#endDatepickerId").datepicker("setDate", "Oct 12, 2013");
            $("#startDatepickerId").datepicker("option", "disabled", true);
            $("#endDatepickerId").datepicker("option", "disabled", true);
        }
        else if (selectedDatePeriod === 'Custom') {
//                console.log('init dateperiod for ...: ' + selectedDatePeriod);

            $("#startDatepickerId").datepicker("option", "disabled", false);
            $("#endDatepickerId").datepicker("option", "disabled", false);
            $("#startDatepickerId").datepicker("setDate", "0");
            $("#endDatepickerId").datepicker("setDate", "0");
        }

    },
    getJobManagerRWSURL: function() {
        // Get the host and port part of URL
        var hostAndPort = document.URL.split('/')[2];
        // Build URL
        var url = "http://" + hostAndPort + "/jmts/webresources/job";

        return url;
    },
    doJobSearch: function(event) {

        $.ajax({
            url: this.getJobManagerRWSURL() + '/search',
            dataType: "json",
            contentType: "application/json",
            type: 'POST',
            data: SEARCH.getSearchParameters(),
            beforeSend: function() {
                $("#wait").css("display", "block");
            },
            error: function(jqxhr, status, errorMsg) {
                $('#messageDialogId').dialog("option", "title", "Search Error");
                if (errorMsg === 'Not Found') {
                    $('#messageDialogMessage').text("A connection error occurred!");
                }
                else if (errorMsg === '') {
                    $('#messageDialogMessage').text("A connection error occurred!");
                }
                else {
                    $('#messageDialogMessage').text(errorMsg);
                }

                $('#messageDialogId').dialog('open');
            }
            ,
            success: function(data)
            {
                try {
                    $('#jobsTable').puidatatable('option', 'datasource', JMTS.getJobData(data.job));
                }
                catch (e) {
                    console.log(e);
                    $('#jobsTable').puidatatable('option', 'datasource', []);
                }
            },
            complete: function() {
                $("#wait").css("display", "none");
            }
        });
    },
    getCurrentJobAsJSON: function() {
        console.log(JSON.stringify(JMTS.currentJob)); //tk
        return JSON.stringify(JMTS.currentJob);
    },
    saveJob: function(event) {
        $.ajax({
            url: this.getJobManagerRWSURL(),
            dataType: "json",
            contentType: "application/json",
            type: 'PUT', // tk org PUT
            data: JMTS.getCurrentJobAsJSON(), //'{"jobNumber": "ddddd", "autoGenerateJobNumber": "true", "reportNumber": "rep/2321"}',
            beforeSend: function() {
                $("#wait").css("display", "block");
            },
            error: function(jqxhr, status, errorMsg) {
                $('#messageDialogId').dialog("option", "title", "Job Save Error");
                if (errorMsg === 'Not Found') {
                    $('#messageDialogMessage').text("A connection error occurred!");
                }
                else if (errorMsg === '') {
                    $('#messageDialogMessage').text("A connection error occurred!");
                }
                else {
                    $('#messageDialogMessage').text(errorMsg);
                }

                $('#messageDialogId').dialog('open');
            }
            ,
            success: function(data)
            {
                console.log('Job save success!');
            },
            complete: function() {
                $("#wait").css("display", "none");
            }
        });
    }

};
