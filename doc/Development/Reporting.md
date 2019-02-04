## Tasks
- Edit and deploy all reports to production server and send email re formal
  requests for other reports as follows: 
  * Uninvoiced jobs: Division, Source of Entry (customer serv etc), Client Type (Credit / Misc), Deposit not 
    stored as part of job record so require a more complex query to extract.
  * Impl finance report showing jobs that are supposed to have deposits but don't
    "Jobs Requiring Deposits" as the report. 
- Deal with the multiple row entries all reports especially those used by 
  Customer Service. See if "DISTINCT" solves the problem.
- Impl finding report by name and description.
- Add test and calibration values to monthly report Excel spreadsheet.
- Check with Burton and Allen of the "Monthly Report" in the system be taylored
  for the Mechanical monthly report.
- Ask Allen about doing a report based on the monthly report for the Eng. division.
- Try to get the most recent jasper reports library ( >= 6.6.0) and use it with maven.
  The may lead to better exporting of reports to Excel as is done in JR Studio.
- Update templates table when new reports are created.
