---
layout: wiki
author: Desmond Bennett
title: Wiki
subject: Installation
---

## Installation

This outlines the installation procedure for the Job Management & Tracking System (JMTS). Currently, only instructions for the Windows operating system are included.

## Getting started

The following are required for the installation of the JMTS:
- The MySQL Community Server v5.5.x that can be obtained <a href="https://dev.mysql.com/downloads/mysql/" target="_blank">here</a>.
- The Glassfish v3.1.2.2 application server that can be downloaded from <a href="https://www.oracle.com/java/technologies/ogs-v3122-downloads.html" target="_blank">here</a>.
- The latest release of the JMTS that can be obtained <a href="https://github.com/DPBandA/job-management-tracking-system/releases/latest" target="_blank">here</a> as a WAR archive file.

Following are instructions for installing the MySQL Community Server, the Glassfish application server and finally the JMTS.

## MySQL Community Server Installation

After downloading a suitable installer or ZIP archive, the MySQL server should be installed with the most optimal configuration settings as given <a href="https://dev.mysql.com/doc/mysql-installation-excerpt/5.5/en/windows-installation.html" target="_blank">here</a>. The server should be installed as a Windows service where the default service name will be MySQL55.

The database schemas required by the JMTS must be imported or created before the Glassfish application server can be configured. These database schemas can be provided by a database administrator or a software developer of the JMTS.

To remove ONLY_FULL_GROUP_BY issues configure MySQL in the my.cnf files as follows:
[mysqld] sql_mode = STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION

## Glassfish Server Installation

The Glassfish application server should be installed from a ZIP archive in accordance with the instructions given <a href="https://docs.oracle.com/cd/E26576_01/doc.312/e24935/installing.htm#GSING00006" target="_blank">here</a>. After installation, the administration console can be accessed at http://localhost:4848 as shown in **Figure 1**. The user name admin and no password should be entered by default.

<img class="mb-2 img-fluid" src="/doc/image/glassfish%20login.png" alt="DPB&A">

**Figure 1 - Glassfish Server Administration Console login**

In order to activate remote and secure access to the administration console, the administrator password must be set as shown in **Figure 2** below:

<img class="mb-2 img-fluid" src="/doc/image/admin%20password.png" alt="Administrator password">

**Figure 2 - Setting the administrator password**

To install the server as a Windows service please see [here](http://www.luv2code.com/2013/11/13/install-glassfish-4-as-a-windows-service/).

### Database Resources

The Java Database Connectivity (JDBC) driver files for the MS SQL and MySQL database servers are required before database resources can be configured. The MS Server database driver can be downloaded [here](https://github.com/DPBandA/job-management-tracking-system/blob/master/doc/database/jtds-1.2.5.jar?raw=true). The MySQL server database driver file can be downloaded [here](https://github.com/DPBandA/job-management-tracking-system/blob/master/doc/database/mysql-connector-java-5.1.45-bin.jar?raw=true). These files should be copied to "<Glassfish installation folder>\glassfish3\glassfish\lib". The Glassfish server should then be restarted.

Java Database Connectivity (JDBC) connection pools must be created as shown in **Figure 3** below although the actual names may differ. However, JDBC resources with the JNDI names of 'jdbc/__JMTSPool' and 'jdbc/__ACCPACDBPool' for MySQL server and Microsoft SQL Server respectively must be created. A JMTS database administrator or software developer can assist with this.

<img class="mb-2 img-fluid" src="/doc/image/connection%20resources%20and%20pools.png" alt="JDBC connection pools and resources">

**Figure 3 - JDBC connection pools and resources**

For MS SQL Server connection use: Pool Name: DBPool, Datasource Classname: net.sourceforge.jtds.jdbc.Driver, Resource Type: java.sql.Driver.

### Performance Tuning

The performance of the Glassfish server can be tuned by following the procedure given <a href="https://docs.oracle.com/cd/E18930_01/html/821-2431/index.html" target="_blank">here</a>. However, basic performance tuning can be initiated by clicking the 'Performance Tuner' link as shown in **Figure 4** below:

<img class="mb-2 img-fluid" src="/doc/image/performance%20tuner.png" alt="Performance tuning">

**Figure 4 - Performance Tuning**

## JMTS Installation
Finally, the JMTS can be deployed by uploading the WAR archive using the "Deploy..." button as shown in **Figure 5** below:

<img class="mb-2 img-fluid" src="/doc/image/jmts%20installation.png" alt="JMTS installation">

Detailed instructions on how to deploy a sample application can be found <a href="https://docs.oracle.com/cd/E18930_01/html/821-2432/geyvr.html" target="_blank">here</a>. The JMTS WAR archive should be located where it was downloaded and the configuration settings in the deployment screen changed as necessary.

## Conclusion

This concludes the instructions for installing the JMTS on a Windows computer. Instructions for installing it on other operating systems will be provided in the future.
