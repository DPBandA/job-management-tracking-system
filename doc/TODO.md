# Urgent
- Add STRATEGICPRIORITY to legaldocument table in all databases and StrategicPriority1-3 system option list.

# Impl HR module with HumanResourceManager clas
- Reload entities before opening dialogs. Start with employee.
  * Load from database using setSelected* in <f:setPropertyActionListener>
    and redo search in the "dialogReturn" method.
- In departments tab in division dialog, list the departments that are in subgroups
  in addition to those that fall directly under the division.
- Deploy and subgroup and division records.
- Add code to generate "accounting codes" in Job Costing tab.
- When error occurs while saving job occurs and email is sent, a job number is 
still generated. This happened for job 6153 which was later saved to 6154. 
A valid job number should not be generated when this occurs.

# General
- Set address type to billing for new addresses.
- Newly entered document type appears in "Document #" field but not in the
"Document type" field. Fix!
- Hide "For job earnings:" in classification dialog except for "Job" category. 	
- Do update Legal Office report with set and actual turnaround times.
- When doc type is first edited it is not reflected in the doc type form field.
  Replicate this issue and fix! Solve by impl "dialogReturn" for the Document Type
  dialog and set the document type.
- Check out Server Performance Tuner in GF4/5 to see if this help the speed of 
  searches etc.
- Dr. Ramdonquestion: Can JMTS have more than one person able to untick complete?
  * Allow other persons apart from department "Head" and "Acting Head" and system admin.
- Activate acting head for micro b.
- Reminder: include info re "Legal Office" module in JMTS User manual.
- Put Contact Management module in Future.md
- Impl automatic including child job cost based on various criteria such as 
  percentage, cost component type etc.
