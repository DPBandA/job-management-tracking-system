
## Updates based on training, testing and feedback
- The delete payment dialog text does not show in dark hive. Fix!
- Reload entities from database before loading in dialogs.
- Allow creating subcontract from a subcontract.
- Indicate somewhere in the job dialog when a subcontract is being created.
- Fix sammple reference sequencing when a sample is deleted. Test with job 5740.
- Widen credit status dialog and cost component dialog for better display 
  in dark hive theme.
- Display prompt when there are completed subcontracted jobs when the Job Costing dialog
  is opened:
  * Impl "Existing Subcontracts" dialog...copy the "Delete Payment" dialog.
- Fix sample sequencing when a sample is deleted. Note that sample does not 
  change initially when the # of samples is reduced.
  * Do sample sorted by id and not reference taking into account null ids as is
    done for the DatePeriod class. Do not use the string version of the id as is
    done for date period. Compare the value instead. Look for all classes that
    use the string version and change them.
  * The wrong sample seem to get edited after one is deleted. Fix!
- Cordinate the editing of a client and what happens when the dialog returns
  to the job dialog.
- Alerts should be sent to person who entered job, job assignee, representative(s)
  department head where appropriate eg send alert to parent job assignee and the
  person who entered the job when child job is approved.
- Ensure that a job is not marked completed until the child job costing is included?
- Remember to  create labs and units within departments.

## General
- Add Division and Business to the Job class and initialize them when a job is created or
  subcontracted.
- Create Module entity class for storing and tracking module information such as name and
  activation status
- Set all search results list in all managers to null when logout to prevent the next logged
  user from accessing that list.
- Misc config search results not sorted. Fix!
- Set address type to billing for new addresses.
- Check out Server Performance Tuner in GF4/5 to see if this help the speed of 
  searches etc.
- Dr. Ramdonquestion: Can JMTS have more than one person able to untick complete?
  * Allow other persons apart from department "Head" and "Acting Head" and system admin.
- Reminder: include info re "Legal Office" module in JMTS User manual.
- Put Contact Management module in Future.md
- Impl automatic including child job cost based on various criteria such as 
  percentage, cost component type etc. -- Include this as proposal for future 
  work.
- Allow tracking information to added to the job even after it is marked completed
  * Impl tacking feature for this.
- Impl using only one EMF which is used by all session beans when an entity 
  manager is needed.
- Now that department is removed from JMUser, check that no code is broken.
- Send error email for only job save related exceptions. Not messages pertaining
  to privileges. Send emails to desbenn@gmail.com
- Remove major modules such as helpdesk, hrm, crm and stds dev and create as
separate projects using maven overlays.
- Add option to auto generate sample reference as is done with job number.
- Consider using oauth for authentication over the web.
- Check why admain2 is not used when admain is not available.
- Impl login with admin user and encrypted password.
- See if all passwords can be encrypted in system options.
- Receipt # not showing on some job costings. Impl the "Sing User Job Access" for
  to solve this which may be as result of one user overwriting the edits of 
  another.
- Impl UpdateAction as is done in PR class to determine alerts to send.
- In Authentication impl notification when login attempts reaches a certain amount
  eg. 2.
- Get rid of "shadow" when  mouse over menu item in edit client menu and others.
- Get rid of excess "--" employee.
- Display growl when user is not authenticated.
- Use dialogReturn through the system update the respective tables when dialogs
  are closed.
- Backup all affected projects and and change jmuser to user or userprofile.
  Eventually rename the jmuser table to user or userprofile.
- Set all dialog heights to 0 so the actual height is automatically set
  when the dialog is open.
- Remove TRN and add iden. type and iden. to client dialog. If iden type is TRN
  set the TRN field in the Client class.
- Add list of id type as autocomplete to supplier and client dialogs.
- Make Parish/State/Province autocomplete in address dialog.
- Impl searching or any part of client or supplier as is done with job entry.
- Use finance related methods in FinancialApplication and not JMTSApplication.
- Clean up unused or unneeded out of JMTSApplication. Some of this code is already
  in managers/WAL or can be put in them.
- Create Application class and put in WAL. Other application classes such as
  JMTSApplication can be put in it.
- Do Dashboard and MainTab tab init in JobManager and not in the constructors.
- Put the sync methods in the Application class from which all application scope
   classes would inherit. openedJobs would become openedObjects etc.
- Go through each manager and remove code that is not needed in that manager.
- Add a separate tab in user account dialog for sys admin.
- Impl giving privileges to specific tabs in sys admin such as hrm.
- Use uneditable greyed input text to display uneditable date field instead of 
  calendar component sin
- Checkout what causes this error to occure: javax.persistence.RollbackException: Exception [EclipseLink-4002] (Eclipse Persistence Services - 2.3.2.v20111125-r10461): org.eclipse.persistence.exceptions.DatabaseException
Internal Exception: com.mysql.jdbc.MysqlDataTruncation: Data truncation: Data too long for column 'COMMENT' at row 1
Error Code: 1406
Call: UPDATE job SET COMMENT = ?, JOBNUMBER = ?, JOBSEQUENCENUMBER = ? WHERE (ID = ?)
        bind => [4 parameters bound]
Query: UpdateObjectQuery(1514057)
- Impl the submission of issues and feedback within the JMTS. Github issue tracking system could be used in the backend.
- Add default report category to JobManagerUser.
- Make all tabs closeable and maintain a "openedTabs" list all tabs in the "user profile" and reopen them when the user logs in.
- When a new job is created and the dialog box is closed do a search with the sequence number 
  and "this month" as search parameters.
- Redesign the module access and privilege feature to allow finer level access to a module/submodule and features.
