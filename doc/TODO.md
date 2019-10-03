### JMTS Migration
- Request a processor with the best specs for the jmts vm.
- Use the database on 172.16.0.39 from payara 4 on this machine and see if performance improves.
- Use payara 4 on 172.16.0.39 see if there is improvement in performance.
- Upgrade to Payara 5 on boshrmapp.

### JMTS Vision Document
- Update and send document.

### General
- Take financial management lib out of jmts lib since it is included in cm lib.
- Implement month report feature update in accordance with proforma, report template
  and GA's email:
  * Update Job class to contain the new fields according to the new monthly report format:
    ~ Add the fields to the Job class for the number of services offered (eg noOfTrainings).
      Export the first 2 rows of the report template as PDF for reference.
  * Update the Job dialog to allow entry into the new fields.
  * See GA's email and Github issues.
- Put back the PM search and other features after adding the purchase-management-lib dependency.
- Implement enabling a module for a user from the user menu.
- Check why Finance staff cannot export job costing. This may be applicable to only
  subcontracted jobs. **Done**
- Fix java.lang.NullPointerException
	at jm.com.dpbennett.wal.managers.SystemManager.updateDashboardTabs(SystemManager.java:353)
that is thrown in user menu when a module is selected.
- Remove PM, FM etc menu items and put a master menu from which modules can be selected as is 
done in the preferences dialog. See if there is a checkbox menu item in PF
- Create a lib project for each web application. Eg system-management-lib, 
purchase-management-lib etc.
- Take out the relevant files and put them in their own modules. See the JMTS copy 
  for the files.
- Add authenticate to c lewis account after testing.

### UI Design
- Put "Help" and "About" menu items in the "User" menu.
- Some of the module labels have common letters for a word. Fix.

### Database Updates and Configurations
- job table: SERVICELOCATION (VARCHAR(255)), NOOFINSPECTIONS, NOOFTRAININGS,
  NOOFLABELASSESSMENTS, NOOFCERTIFICATIONS, NOOFCONSULTATIONS, NOOFOTHERASSESSMENTS
- System option: serviceLocationList (List<String>) (In-house;On-site;In-house & On-site)

### Release Notes
The following are included in this release:
- Fixed the display of the text in the document delete confirmation dialog box in the Legal Office (LO) module.
- Fixed the display of the text in the sample delete confirmation dialog box.
- Service location autocomplete combobox added to the Services tab of the Job Form.

