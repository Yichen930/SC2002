# Internship Placement Management System

A Java command-line application for managing internship opportunities, student applications, and placements. Built with Object-Oriented Design principles for NTU SC2002.

## Features

### Students
- Browse and filter internships by level and date
- Apply for up to 3 internships simultaneously
- Accept placement confirmations
- Request withdrawals
- **Search internships** by company, title, or level
- **View statistics** on application performance

### Company Representatives
- Create and manage up to 5 active internships
- Review and approve student applications
- Toggle internship visibility
- Set slots (max 10), levels, and preferred majors
- **View statistics** on internship applications

### Career Center Staff
- Approve internship postings and company registrations
- Manage withdrawal requests
- Generate reports on placements and applications
- **Search internships** across all companies
- **View system-wide statistics**

### Additional Features
- **File-based persistence** with write-back support (auto-save on user registration and password changes)
- **Colored CLI output** for better user experience (green for success, red for errors, etc.)
- **Comprehensive logging** to `logs/system.log` for audit trails
- **Statistics dashboard** for all user roles
- **Advanced search** with flexible filtering options

## Architecture

**MVC Pattern with Service Layer**
- **Model**: User, Student, Application, InternshipOpportunity entities
- **View**: CLI for user interaction
- **Controller**: Service implementations (Auth, Application, Internship)

**SOLID Principles**
- Single Responsibility Principle: Each part is responsible for one functionality
- Interface-based design: For loose coupling
- Dependency Inversion: CLI depends on service interfaces
- Single Responsibility: Each controller handles one domain
- Encapsulation with private fields and public methods

## Quick Start

```bash
# First time setup: Create bin directory
mkdir -p bin

# Compile (generates .class files in bin/)
javac src/*.java -d bin

# Run
java -cp bin Main
```

**Note:** The `-d bin` flag separates compiled `.class` files from source code for better organization.

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

**Phase 1: Internship Setup**
1. Company Rep creates internship → `InternshipStatus.PENDING`
2. Staff reviews and approves → `InternshipStatus.APPROVED` (visible to students)
3. Company Rep toggles visibility (optional)

**Phase 2: Student Application**

4. Student browses internships (auto-filtered by major, year, dates, slots)

5. Student submits application → `ApplicationStatus.PENDING`

**Phase 3: Company Review**

6. Company Rep reviews application:
   - **Approve** → `ApplicationStatus.ACCEPTED` (awaiting student confirmation)
   - **Reject** → `ApplicationStatus.REJECTED`

**Phase 4: Student Confirmation**

7. Student accepts placement:
   - Internship slot reserved (`filledSlots++`)
   - All other active applications auto-withdrawn → `ApplicationStatus.WITHDRAWN`
   - If capacity reached → `InternshipStatus.FILLED`

**Phase 5: Withdrawals**

8. **Pre-Confirmation Withdrawal**: 
   - Student withdraws directly → `ApplicationStatus.WITHDRAWN`
9. **Post-Confirmation Withdrawal**: 
   - Student creates `WithdrawalRequest` → `WithdrawalStatus.PENDING`
   - Staff approves → Slot freed, `ApplicationStatus.WITHDRAWN`, `InternshipStatus.FILLED` → `APPROVED`
   - Staff rejects → Placement maintained

## State Diagram

```
INTERNSHIP:
PENDING → [Staff Approves] → APPROVED → [Capacity Full] → FILLED
                                    ↑                          ↓
                                    └───── [Slot Freed] ───────┘

APPLICATION:
                        ┌──→ REJECTED (Company denies)
                        │
PENDING → [Company] ────┤
                        │
                        └──→ ACCEPTED → [Student confirms] → Slot Reserved
                                                              ↓
                                                         Other apps → WITHDRAWN

PRE-CONFIRMATION: Student withdraws → WITHDRAWN (no approval needed)
POST-CONFIRMATION: WithdrawalRequest.PENDING → [Staff] → APPROVED/REJECTED
```

## Key Design Decisions

- **Interface-based architecture** for testability and loose coupling
- **Two-step approval** ensures both parties commit
- **File-based persistence** with selective write-back (user registration)
- **Exception hierarchy** for granular error handling
- **Enum-based state management** for type safety and simplicity

## Project Structure

```
SC2002/
├── src/                                    # Source code
│   ├── Main.java                           # CLI entry point
│   │
│   ├── Entity Classes:
│   │   ├── User.java                       # Abstract base user class
│   │   ├── Student.java
│   │   ├── CompanyRepresentative.java
│   │   ├── CareerCenterStaff.java
│   │   ├── Application.java
│   │   ├── InternshipOpportunity.java
│   │   └── WithdrawalRequest.java
│   │
│   ├── Service Interfaces:
│   │   ├── AuthServiceInterface.java
│   │   ├── ApplicationServiceInterface.java
│   │   ├── InternshipServiceInterface.java
│   │   ├── RegistrationServiceInterface.java
│   │   └── DataAccessInterface.java
│   │
│   ├── Controllers:
│   │   ├── AuthController.java
│   │   ├── ApplicationController.java
│   │   ├── InternshipController.java
│   │   ├── RegistrationController.java
│   │   └── DataManager.java
│   │
│   ├── Utilities:
│   │   ├── ColorUtil.java                  # ANSI color output
│   │   ├── SystemLogger.java               # Audit logging
│   │   ├── StatisticsUtil.java             # Statistics generation
│   │   └── SearchUtil.java                 # Advanced search
│   │
│   ├── Enums:
│   │   ├── ApplicationStatus.java
│   │   ├── InternshipStatus.java
│   │   ├── InternshipLevel.java
│   │   └── WithdrawalStatus.java
│   │
│   └── Exceptions:
│       ├── ApplicationException.java
│       ├── AuthenticationException.java
│       ├── AuthorizationException.java
│       └── DataAccessException.java
│
├── data/                                   # Persistent data files
│   ├── users.txt                           # User accounts
│   ├── internships.txt                     # Internship listings
│   └── applications.txt                    # Student applications
│
├── bin/                                    # Compiled .class files (auto-generated)
│
├── javadoc/                                # JavaDoc API documentation (auto-generated)
│   ├── index.html                          # Main documentation page
│   ├── allclasses-index.html
│   ├── package-summary.html
│   └── ... (HTML docs for all classes)
│
├── UML/                                    # UML diagrams
│
└── README.md                               # This file
```

## Documentation

- **API Documentation**: Open `javadoc/index.html` in a browser to view JavaDoc
- **System Logs**: Check logs directory for detailed operation history

## License

Developed for academic purposes as part of NTU SC2002 coursework.

**Authors:** Kannan Priyadharshan, Gong Yichen, Arthur Lee Jian You, Tan Xin Yu, Harsh Kamdar
