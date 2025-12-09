🛡️ SecureCoda: Activity & Exposure Monitor
SecureCoda is a standalone security monitoring system for Coda.io workspaces. It connects via the Coda REST API to detect security risks such as unused documents, public exposure, and sensitive data leaks, offering a centralized dashboard for monitoring and one-click remediation.

🚀 Features
1. Security Scanning
Unused Document Detection: Flags documents that haven't been modified in 90 days (configurable).

Public Exposure Detection: Identifies documents that are published to the web.

Sensitive Data Scanning: Scans table rows for patterns like Emails and Passwords using Regex.

2. Remediation
One-Click Fix: Execute corrective actions directly from the dashboard.

Unused Docs → Deletes the document.

Public Docs → Deletes the document (can be extended to unpublish).

Sensitive Data → Deletes the specific row containing the leak.

3. Dashboard
Real-time UI: A clean, responsive HTML/JS dashboard served by Spring Boot.

Live Stats: View total alerts and severity breakdown (High/Medium/Low).

Audit History: Tracks resolved alerts in the database.

🏗️ Architecture
The application follows SOLID principles and a Layered Architecture to ensure separation of concerns and maintainability.

Tech Stack
Language: Java 17+

Framework: Spring Boot 3.x

Database: H2 (In-Memory for Dev) / PostgreSQL (Production ready)

API Client: Spring Cloud OpenFeign

Frontend: HTML5, Bootstrap 5, Vanilla JavaScript

Design Patterns Used
Strategy Pattern (DetectionRule):

The scanning logic is decoupled from the main service.

New rules (e.g., "SQL Injection Check") can be added by implementing the DetectionRule interface without modifying the ScannerService.

DTO Pattern:

Data Transfer Objects (AlertSummaryDto) decouple the internal database entities from the API responses.

Repository Pattern:

Abstracts the data access layer using Spring Data JPA.

⚙️ Setup & Configuration
Prerequisites
Java 17 or higher

Maven 3.6+

A Coda Account & API Token

1. Clone the Repository
Bash

git clone https://github.com/rathoddarshan/SecureCoda.git
cd SecureCoda
2. Configure Application
Open src/main/resources/application.properties and update your settings:

Properties

# Required: Your Coda API Token
coda.api.token=Bearer YOUR_REAL_CODA_TOKEN_HERE

# Optional: Thresholds
coda.scan.unused-days=90

# Database (H2 is default)
spring.datasource.url=jdbc:h2:mem:securecoda
3. Build & Run
Bash

mvn clean install
java -jar target/secure-coda-0.0.1-SNAPSHOT.jar
The application will start on http://localhost:8080.

🐳 Docker Support
To run the application in a container:

1. Build the Docker Image

Bash

docker build -t secure-coda .
2. Run the Container

Bash

docker run -p 8080:8080 -e CODA_API_TOKEN="Bearer YOUR_TOKEN" secure-coda
(Note: Ensure your code reads the token from System Environment if passing it via -e)

📖 Usage Guide
Open Dashboard: Navigate to http://localhost:8080.

Trigger Scan: Click the blue "Run Security Scan Now" button.

Background: The backend fetches docs, scans tables, and applies detection rules.

View Alerts: Refresh the list to see any detected vulnerabilities.

Remediate: Click the "Fix" button next to an alert to auto-resolve the issue on Coda.

🔌 Extensibility
The system is designed to be easily extended:

Adding a New Detection Rule:

Create a new class in service/detection/ (e.g., SqlInjectionRule.java).

Implement the DetectionRule interface.

Add your logic in the evaluate(CodaDoc doc) method.

Annotate with @Component. Spring will automatically pick it up and include it in the next scan!
