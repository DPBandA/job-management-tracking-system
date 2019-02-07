## Tasks
- Modify uninvoiced and deposit reports to use "isearnining" field instead of 
  classification name.
- Edit and deploy all reports to production server and send email re formal
  requests for other reports as follows: 
  * Note in JobStatusAndTracking class javadoc that costingDate and dateCostingCompleted
    are currently treated as the job costing preparation date.
  * Impl finance report showing jobs that are supposed to have deposits but don't
    "Jobs Requiring Deposits" as the report. Make use of client's credit limit.
- Deal with the multiple row entries all reports especially those used by 
  Customer Service. See if "DISTINCT" solves the problem.
- Impl finding report by name and description.
- Add test and calibration values to monthly report Excel spreadsheet.
- Check with Burton and Allen of the "Monthly Report" in the system be taylored
  for the Mechanical monthly report.
- Ask Allen about doing a report based on the monthly report for the Eng. division.
- Try to get the most recent jasper reports library ( >= 6.6.0) and use it with maven.
  The may lead to better exporting of reports to Excel as is done in JR Studio.
- Add amount paid to "Uninvoiced" and "Deposit" reports using an inner aggregate
  subquery.

