# Internship Placement Management System

A Java command-line application for managing internship opportunities, student applications, and placements. Built with Object-Oriented Design principles for NTU SC2002.

## Features

### Students
- Browse and filter internships by level and date
- Apply for up to 3 internships simultaneously
- Accept placement confirmations
- Request withdrawals

### Company Representatives
- Create and manage up to 5 active internships
- Review and approve student applications
- Toggle internship visibility
- Set slots (max 10), levels, and preferred majors

### Career Center Staff
- Approve internship postings and company registrations
- Manage withdrawal requests
- Generate reports on placements and applications

## Architecture

**MVC Pattern with Service Layer**
- **Model**: User, Student, Application, InternshipOpportunity entities
- **View**: CLI for user interaction
- **Controller**: Service implementations (Auth, Application, Internship)

**SOLID Principles**
- Interface-based design for loose coupling
- Dependency Inversion: CLI depends on service interfaces
- Single Responsibility: Each controller handles one domain
- Encapsulation with private fields and public methods

## Quick Start

```bash
# Compile
javac *.java

# Run
java Main
```

## Sample Credentials

**Students:**
- `U2345123F` / `password` (Alice - Computer Science, Year 2)
- `U3456234G` / `password` (Bob - Engineering, Year 3)

**Company Rep:**
- `john@techcorp.com` / `password` (Tech Corp - Approved)

**Staff:**
- `admin@ntu.edu.sg` / `password` (Career Services)

## Business Rules

- Students: Max 3 active applications; Year 1-2 limited to BASIC level
- Company Reps: Max 5 active internships; must be approved by staff
- Internships: Max 10 slots; auto-filled when capacity reached
- Withdrawals: Post-confirmation requires staff approval

## Application Flow

1. Company Rep creates internship (PENDING)
2. Staff approves internship (APPROVED)
3. Student applies (Application: PENDING)
4. Rep approves application (Application: ACCEPTED)
5. Student accepts placement (slot reserved, other apps withdrawn)

## Key Design Decisions

- **Interface-based architecture** for testability and loose coupling
- **Two-step approval** ensures both parties commit
- **Read-only data access** per assignment requirements (no persistence)
- **Exception hierarchy** for granular error handling

## Project Structure

```
Entity Classes: User, Student, CompanyRepresentative, Application, InternshipOpportunity
Service Interfaces: AuthServiceInterface, ApplicationServiceInterface, InternshipServiceInterface
Controllers: AuthController, ApplicationController, InternshipController, DataManager
Enums: ApplicationStatus, InternshipStatus, InternshipLevel, WithdrawalStatus
Exceptions: ApplicationException, AuthenticationException, AuthorizationException
```

## License

Developed for academic purposes as part of NTU SC2002 coursework.
Authors: Kannan Priyadharshan, 
