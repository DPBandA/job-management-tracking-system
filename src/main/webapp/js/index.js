// Initialization of UI for index.html
var myLayout;

$(document).ready(function() {
    // Initialize UI 
    $('#messages').puigrowl();

    // Login dialog components
    $("#loginDialogId").puidialog({
        modal: true,
        closable: false,
        width: 350
    });
    $('#usernameId').puiinputtext();
    $('#passwordId').puiinputtext();
    $('#loginButtonId').puibutton({
        click: function(event) {
            JMTS.login();
        }
    });
    // end Login dialog components

    $("#messageDialogId").dialog({
        modal: true,
        autoOpen: false,
        dialogClass: "alert",
        buttons: [
            {
                text: "OK",
                click: function() {
                    $(this).dialog("close");
                }
            }
        ]
    });
    myLayout = $('body').layout(
            {
                applyDefaultStyles: true,
                west: {
                    size: "380"
                },
                north: {
                    size: "117"
                }
            });
    $('#searchPanel').puipanel();
    $('#headerPanel').puipanel();
    $('#mainTabView').puitabview({
        change: function(event, index) {

        }
    });
    $('#userMenuTrigger').puibutton({
        icon: 'ui-icon-triangle-1-s',
        iconPos: 'right'
    });
    $('#userMenu').puimenu({
        popup: true,
        trigger: $('#userMenuTrigger')
    });

    // Search widgets
    $('#searchTypeId').puidropdown();
    $('#dateFieldId').puidropdown();
    $('#datePeriodId').puidropdown(
            {
                change: function() {
                    SEARCH.initDatePeriod($('#datePeriodId').puidropdown('getSelectedValue'));
                }
            });


    $('#startDatepickerId').datepicker(
            {
                dateFormat: "M dd, yy"
            });
    $('#endDatepickerId').datepicker(
            {
                dateFormat: "M dd, yy"
            });
    $('#searchTextId').puiinputtext();
    $('#searchButtonId').puibutton(
            {
                icon: 'ui-icon-search',
                click: function(event) {
                    JMTS.doJobSearch(event);
                }
            });

    // Job related UI components        
    $('#saveJobButton1Id').puibutton(
            {
                icon: 'ui-icon-disk',
                click: function(event) {
                    JMTS.saveJob(event);
                }
            }
    );
    $('#closeJobDialogButton1Id').puibutton(
            {
                icon: 'ui-icon-close',
                click: function(event) {
                    // tk check for dirty job first
                    $('#jobDetailDialogId').puidialog('hide');
                }
            }
    );
    $('#copyJobButton1Id').puibutton(
            {
                icon: 'ui-icon-copy'
            }
    );
    $('#jobFormMenuTrigger').puibutton({
        icon: 'ui-icon-triangle-1-s'
    });
    $('#jobFormMenu').puimenu({
        popup: true,
        trigger: $('#jobFormMenuTrigger')
    });
    $('#saveJobButton2Id').puibutton(
            {
                icon: 'ui-icon-disk',
                click: function(event) {
                    JMTS.saveJob(event);
                }
            }
    );
    $('#closeJobDialogButton2Id').puibutton(
            {
                icon: 'ui-icon-close',
                click: function(event) {
                    // tk check for dirty job first
                    $('#jobDetailDialogId').puidialog('hide');
                }
            }
    );
    $('#copyJobButton2Id').puibutton(
            {
                icon: 'ui-icon-copy'
            }
    );
    $("#jobDetailDialogId").puidialog({
        modal: true,
        closable: false,
        width: 800,
        height: 550
    });
    // Init General tab
    $('#businessOfficeId').puiautocomplete(
            {
                completeSource: ["Head Office", "Mandeville"], //tk
                dropdown: true,
                select: function(event, item) {
                    JMTS.setDirty(true);
                }
            }
    );
    $('input').change(
            function(event) {
                JMTS.setDirty(true);
            }
    );

    $('textarea').change(
            function(event) {
                JMTS.setDirty(true);
            }
    );

    $('#jobNumberId').puiinputtext();
    $('#autoJobNumberId').puicheckbox();
    $('#clientId').puiautocomplete({
        completeSource: ["JATCO", "JPSCo"], //tk
        dropdown: true
    });
    $('#dateSubmittedDatepickerId').datepicker({dateFormat: "M dd, yy"});
    $('#departmentId').puiautocomplete({
        completeSource: ["Finance", "Electrical/Electronic"], //tk
        dropdown: true,
        select: function(event, item) {
            JMTS.setDirty(true);
        }
    });
    $('#subContractedDepartmentId').puiautocomplete({
        completeSource: ["Finance", "Electrical/Electronic"], //tk
        dropdown: true,
        select: function(event, item) {
            JMTS.setDirty(true);
        }
    });
    $('#assigneeId').puiautocomplete({
        completeSource: ["Bennett, Desmond", "Allen, Garfield"], //tk
        dropdown: true,
        select: function(event, item) {
            JMTS.setDirty(true);
        }
    });
    $('#jobSamplesPanelId').puipanel();
    $('#addJobSampleButtonId').puibutton(
            {
                icon: 'ui-icon-plus',
                click: function(event) {
                    console.log('add sample');
                }
            }
    );
    $('#addJobSampleButton2Id').puibutton(
            {
                icon: 'ui-icon-plus',
                click: function(event) {
                    console.log('add sample');
                }
            }
    );
    $('#jobSamplesTableId').puidatatable({
        //caption: 'Jobs Found',
        paginator: {
            rows: 5,
            totalRecords: 750
        },
        columns: [
            {field: 'reference', headerText: 'Reference', sortable: true},
            {field: 'description', headerText: 'Description', sortable: true}
        ],
        datasource: [],
        selectionMode: 'single',
        rowSelect: function(event, sampleData) {
            //$('#messages').puigrowl('show', [{severity: 'info', summary: 'Row Selected', detail: (data.id + ' ' + data.client)}]);           
            //JMTS.displayJobDialog(jobData);
        }
    });
    //$('#jobSamplesTableId').css({"width": "100%"}); //tk
    $('#jobDetailTabViewId').puitabview({
        change: function(event, index) {

        }
    });
    $('#costingPanelId').puipanel();
    $('#purchaseOrderNumberId').puiinputtext();
    $('#costEstimateId').puiinputtext();
    $('#minimumDepositRequiredId').puiinputtext();
    $('#paymentTermsId').puiinputtextarea();
    $('#invoiceNumberId').puiinputtext();
    $('#receiptNumberId').puiinputtext();
    $('#totalDepositId').puiinputtext();
    $('#depositDateDatepickerId').datepicker({dateFormat: "M dd, yy"});
    $('#finalCostId').puiinputtext();

    $('#invoicingAndPaymentPanelId').puipanel();
    // Init jobs table
    $('#jobsTable').puidatatable({
        caption: 'Jobs Found',
        paginator: {
            rows: 15,
            totalRecords: 75
        },
        columns: [
            {field: 'jobNumber', headerText: 'Job number', sortable: true},
            {field: 'client', headerText: 'Client', sortable: true},
            {field: 'received', headerText: 'Received', sortable: true},
            {field: 'completed', headerText: 'Completed', sortable: true},
            {field: 'progress', headerText: 'Work progress', sortable: true}
        ],
        datasource: [],
        selectionMode: 'single',
        rowSelect: function(event, jobData) {
            //$('#messages').puigrowl('show', [{severity: 'info', summary: 'Row Selected', detail: (data.id + ' ' + data.client)}]);           
            JMTS.displayJobDialog(jobData);
        }
    });

});
