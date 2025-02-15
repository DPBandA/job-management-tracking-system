# Job Management & Tracking System (JMTS)

[![Current Release](https://img.shields.io/badge/release-latest-green.svg)](https://github.com/DPBandA/job-management-tracking-system/releases/latest)
[![GitHub Issues](https://img.shields.io/github/issues/dpbanda/job-management-tracking-system.svg)](https://github.com/dpbanda/job-management-tracking-system/issues)
[![Wiki](https://img.shields.io/badge/documentation-wiki-green.svg)](https://github.com/DPBandA/job-management-tracking-system/wiki)

## Overview
The **Job Management & Tracking System (JMTS)** is a Java EE web application designed 
to manage and track job activities efficiently. It allows users to create, update, 
and monitor job progress while integrating with various business modules.

## Features
- Job creation, assignment, and tracking
- User role management (Admin, Manager, Employee)
- Real-time job status updates
- Reports and analytics
- Secure authentication and authorization

## Technologies Used
- **Java EE (Jakarta EE)** – Backend development
- **JSF (JavaServer Faces)** – User interface
- **MySQL** – Database management
- **Maven** – Dependency and build management
- **JPA (Eclipse)** – ORM for database interaction

## Setup & Installation
### Prerequisites:
- JDK 11 or later
- Maven installed (`mvn -version`)
- MySQL database running
- Payara/Tomcat application server

### Steps:
1. Clone the repository:
   ```sh
   git clone https://github.com/DPBandA/job-management-tracking-system.git
   cd job-management-tracking-system
2. Build the project
   `mvn clean install`
3. Deploy the WAR file to your application server.

## Usage
1. Open the application in a web browser (e.g. http://localhost:8080/jmts)
2. Login using provided credentials.
3. Manage jobs, track progress, and generate reports.
