/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  Desmond Bennett <info@dpbennett.com.jm at http//dpbennett.com.jm>
 * Created: Feb 5, 2019
 */

/*
Job costing id: 1126640, CP: 1435293, 1435295
*/

SELECT         
     (SELECT SUM(cashpayment.PAYMENT) FROM cashpayment
      INNER JOIN `jobcostingandpayment_cashpayment` jobcostingandpayment_cashpayment ON 
            cashpayment.ID = jobcostingandpayment_cashpayment.cashPayments_ID 
      INNER JOIN `jobcostingandpayment` jobcostingandpayment ON 
            jobcostingandpayment.ID = jobcostingandpayment_cashpayment.JobCostingAndPayment_ID 
      WHERE jobcostingandpayment.ID = job.JOBCOSTINGANDPAYMENT_ID  
     ) AS jobcostingandpayment_TOTALPAYMENT,        
     client.`NAME` AS client_NAME,
     job.`JOBSTATUSANDTRACKING_ID` AS job_JOBSTATUSANDTRACKING_ID,
     job.`JOBSUBCATEGORY_ID` AS job_JOBSUBCATEGORY_ID,
     jobstatusandtracking.`DATESUBMITTED` AS jobstatusandtracking_DATESUBMITTED,
     jobstatusandtracking.`COSTINGDATE` AS jobstatusandtracking_COSTINGDATE,
     jobstatusandtracking.`DATECOSTINGAPPROVED` AS jobstatusandtracking_DATECOSTINGAPPROVED,
     jobsubcategory.`SubCategory` AS jobsubcategory_SubCategory,
     jobsubcategory.`IsEarning` AS jobsubcategory_IsEarning,
     classification.`NAME` AS classification_NAME,
     jobcostingandpayment.`FINALCOST` AS jobcostingandpayment_FINALCOST,
     jobcostingandpayment.`TOTALCOST` AS jobcostingandpayment_TOTALCOST,     
     department.`ID` AS department_ID,
     department.`NAME` AS department_NAME,
     CASE WHEN department_A.`NAME` = '--' THEN 'N/A' ELSE  department_A.`NAME` END AS department_A_NAME,
     CASE WHEN jobcostingandpayment.`INVOICED` = 0 THEN 'No' ELSE  'Yes' END AS costing_INVOICED,
     department_A.`JOBCOSTINGTYPE` AS department_A_JOBCOSTINGTYPE,
     job.`YEARRECEIVED` AS job_YEARRECEIVED,
     job.`JOBNUMBER` AS job_JOBNUMBER,
     CASE WHEN department.`SUBGROUPCODE` = 0 THEN 42 ELSE department.`SUBGROUPCODE` END AS dept_CODE,
     CASE WHEN department_A.`SUBGROUPCODE` = 0 THEN 0 ELSE department_A.`SUBGROUPCODE` END AS dept_A_CODE
     
FROM
     `client` client INNER JOIN `job` job ON client.`ID` = job.`CLIENT_ID`
     INNER JOIN `jobstatusandtracking` jobstatusandtracking ON job.`JOBSTATUSANDTRACKING_ID` = jobstatusandtracking.`ID`
     INNER JOIN `jobsubcategory` jobsubcategory ON job.`JOBSUBCATEGORY_ID` = jobsubcategory.`ID`
     INNER JOIN `classification` classification ON job.`CLASSIFICATION_ID` = classification.`ID`
     INNER JOIN `jobcostingandpayment` jobcostingandpayment ON job.`JOBCOSTINGANDPAYMENT_ID` = jobcostingandpayment.`ID`
     LEFT OUTER JOIN `department` department ON job.`DEPARTMENT_ID` = department.`ID`
     INNER JOIN `department` department_A ON job.`SUBCONTRACTEDDEPARTMENT_ID` = department_A.`ID`
     
WHERE
 jobstatusandtracking.`DATESUBMITTED` >= '2018-01-01'
 AND jobstatusandtracking.`DATESUBMITTED` <= '2018-12-31'
 AND (classification.`NAME` NOT LIKE '%Non-Earning - Government Client Partnership%')
 AND (classification.`NAME` NOT LIKE '%Non-Earning - Internal Client Request%')
 AND (classification.`NAME` NOT LIKE '%Non-Earning - Obligatory Duties%')
 AND (classification.`NAME` NOT LIKE '%Non-Earning - Service Offered%')
 AND (jobstatusandtracking.`WORKPROGRESS` NOT LIKE '%Cancelled%')
 AND (jobstatusandtracking.`WORKPROGRESS` NOT LIKE '%Withdrawn by client%')
 AND ( jobcostingandpayment.`INVOICED` = 0)
ORDER BY
     jobstatusandtracking.`DATESUBMITTED` DESC

