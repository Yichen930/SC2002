import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Main entry point for the Internship Placement Management System.
 * 
 * <p>This system manages the complete lifecycle of internship opportunities,
 * student applications, and user management for three distinct roles:
 * Students, Company Representatives, and Career Center Staff.</p>
 * 
 * <p>Key Features:</p>
 * <ul>
 *   <li>Role-based authentication and authorization</li>
 *   <li>Internship opportunity management with approval workflow</li>
 *   <li>Student application processing with two-step approval</li>
 *   <li>Comprehensive reporting and analytics</li>
 *   <li>File-based data persistence</li>
 * </ul>
 * 
 * @version 1.0
 */
public class Main {
    /**
     * Main method to start the CLI application.
     * 
     * <p>Initializes the Command Line Interface and starts the main
     * event loop for user interactions. The application will load
     * data from text files and present the login menu.</p>
     * 
     * @param args command line arguments (not used in this implementation)
     */
    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.run();
    }

    /**
     * Command Line Interface class that handles all user interactions.
     * 
     * <p>This class implements the View layer of the MVC pattern,
     * providing a text-based interface for all system operations.
     * It delegates business logic to appropriate controllers while
     * managing user sessions and maintaining UI state.</p>
     * 
     * <p>Responsibilities:</p>
     * <ul>
     *   <li>Display menus based on user role (Student, Company Rep, Staff)</li>
     *   <li>Handle user input and validation</li>
     *   <li>Delegate operations to controllers</li>
     *   <li>Maintain session state (current user, filter preferences)</li>
     *   <li>Load and initialize system data from files</li>
     * </ul>
     * 
     * <p>Session Management:</p>
     * The CLI maintains filter persistence across menu switches,
     * allowing users to save and reuse their filter preferences
     * within a single session.
     */
    static class CLI {
        private Scanner scanner;
        private AuthServiceInterface authService;
        private ApplicationServiceInterface applicationService;
        private InternshipServiceInterface internshipService;
        private RegistrationServiceInterface registrationService;
        private DataAccessInterface dataAccess;
        private User currentUser;

        // Filter persistence for user session
        private InternshipLevel lastFilterLevel = null;
        private boolean hasLevelFilter = false;

        /**
         * Constructs the CLI and initializes all system components.
         * 
         * <p>Initialization sequence:</p>
         * <ol>
         *   <li>Creates Scanner for user input</li>
         *   <li>Initializes all controllers (Auth, Application, Internship, Registration)</li>
         *   <li>Creates DataManager for file I/O operations</li>
         *   <li>Loads user data from users.txt</li>
         *   <li>Loads internship data from internships.txt</li>
         *   <li>Loads application data from applications.txt</li>
         * </ol>
         * 
         * <p>If data files are missing or corrupted, the system will start
         * with empty data and display a warning message to the user.</p>
         */
        public CLI() {
            this.scanner = new Scanner(System.in);
            // Dependency Injection: Inject concrete implementations
            this.authService = new AuthController();
            this.applicationService = new ApplicationController();
            this.internshipService = new InternshipController();
            this.registrationService = new RegistrationController();
            this.dataAccess = new DataManager();
            
            // Load data from files at startup
            loadDataFromFiles();
        }

        /**
         * Starts the main application event loop.
         * 
         * <p>This method runs indefinitely until the user explicitly exits.
         * It displays different menus based on the current authentication state:</p>
         * <ul>
         *   <li>Login Menu: if no user is authenticated</li>
         *   <li>Role-specific Main Menu: if user is logged in</li>
         * </ul>
         * 
         * <p>The menu system is role-aware and automatically adapts to show
         * options relevant to the current user's role (Student, Company
         * Representative, or Career Center Staff).</p>
         */
        public void run() {
            System.out.println("=============================================");
            System.out.println("Welcome to the Internship Placement Management System!");
            System.out.println("=============================================");
            
            while (true) {
                if (currentUser == null) {
                    showLoginMenu();
                } else {
                    showMainMenu();
                }
            }
        }

        private void showLoginMenu() {
            System.out.println("\n--- Login Menu ---");
            System.out.println("1. Login");
            System.out.println("2. Register Company Representative");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleRegisterCompanyRep();
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        private void handleLogin() {
            System.out.print("Enter User ID (e.g., U2345123F, john@techcorp.com, admin@ntu.edu.sg): ");
                String userId = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            if (userId.isEmpty() || password.isEmpty()) {
                System.out.println("User ID and password cannot be empty.");
                return;
            }

            try {
                currentUser = authService.authenticate(userId, password);
                if (currentUser != null) {
                    ColorUtil.printSuccess("Login successful! Welcome, " + currentUser.getName() + "!");
                    SystemLogger.log("LOGIN", currentUser.getId(), currentUser.getName() + " (" + getRoleName() + ") logged in");
                } else {
                    ColorUtil.printError("Login failed. Invalid credentials.");
                }
            } catch (AuthenticationException e) {
                ColorUtil.printError("Authentication failed: " + e.getMessage());
            }
        }

        private void handleRegisterCompanyRep() {
            System.out.print("Enter your name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter company email (this will be your User ID): ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter company name: ");
            String companyName = scanner.nextLine().trim();
            System.out.print("Enter department: ");
            String department = scanner.nextLine().trim();
            System.out.print("Enter position: ");
            String position = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            if (name.isEmpty() || email.isEmpty() || companyName.isEmpty() || password.isEmpty()) {
                System.out.println("Name, email, company name, and password are required.");
                return;
            }

            boolean success = registrationService.registerCompanyRepresentative(name, email, companyName, department, position, password);
            if (success) {
                System.out.println("Company representative registered successfully!");
                System.out.println("Your User ID is: " + email);
                System.out.println("Note: Your account needs to be approved by Career Center Staff before you can create internships.");
            } else {
                System.out.println("Registration failed. Email may already be registered.");
            }
        }

        private void showMainMenu() {
            if (currentUser == null) {
                System.out.println("Error: No user logged in.");
                return;
            }

            System.out.println("\n--- Main Menu ---");
            System.out.println("Logged in as: " + currentUser.getName() + " (" + getRoleName() + ")");
            
            if (currentUser instanceof Student) {
                showStudentMenu();
            } else if (currentUser instanceof CompanyRepresentative) {
                showCompanyRepMenu();
            } else if (currentUser instanceof CareerCenterStaff) {
                showStaffMenu();
            }
            
            System.out.println("\n0. Logout");
            System.out.print("Choose an option: ");

            int choice = getIntInput();
            
            if (choice == 0) {
                handleLogout();
                return;
            }

            if (currentUser instanceof Student) {
                handleStudentChoice(choice);
            } else if (currentUser instanceof CompanyRepresentative) {
                handleCompanyRepChoice(choice);
            } else if (currentUser instanceof CareerCenterStaff) {
                handleStaffChoice(choice);
            }
        }

        private void showStudentMenu() {
            System.out.println("1. Browse Internships (with filters)");
            System.out.println("2. View My Applications");
            System.out.println("3. Apply for Internship");
            System.out.println("4. Accept Placement Confirmation");
            System.out.println("5. Withdraw Application (Pre-Confirmation)");
            System.out.println("6. Request Post-Confirmation Withdrawal");
            System.out.println("7. Search Internships");
            System.out.println("8. View My Statistics");
            System.out.println("9. Change Password");
        }

        private void handleStudentChoice(int choice) {
            Student student = (Student) currentUser;
            
            switch (choice) {
                case 1:
                    browseInternshipsWithFilters(student);
                    break;
                case 2:
                    viewMyApplications(student);
                    break;
                case 3:
                    applyForInternship(student);
                    break;
                case 4:
                    acceptPlacementConfirmation(student);
                    break;
                case 5:
                    withdrawApplication(student);
                    break;
                case 6:
                    requestPostConfirmationWithdrawal(student);
                    break;
                case 7:
                    searchInternships();
                    break;
                case 8:
                    viewStudentStatistics(student);
                    break;
                case 9:
                    handleChangePassword();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }

        private void showCompanyRepMenu() {
            CompanyRepresentative rep = (CompanyRepresentative) currentUser;
            System.out.println("1. Create Internship Opportunity");
            System.out.println("2. View My Created Internships");
            System.out.println("3. View Applications for My Internships");
            System.out.println("4. Review Student Applications");
            System.out.println("5. Edit Internship");
            System.out.println("6. Delete Internship");
            System.out.println("7. Toggle Internship Visibility");
            System.out.println("8. View My Statistics");
            System.out.println("9. Change Password");
            
            if (!rep.getIsApproved()) {
                System.out.println("\nNote: Your account is pending approval.");
            }
            
            long activeCount = rep.countActiveInternships();
            System.out.println("Active internships: " + activeCount + "/5");
        }

        private void handleCompanyRepChoice(int choice) {
            CompanyRepresentative rep = (CompanyRepresentative) currentUser;
            
            switch (choice) {
                case 1:
                    if (!rep.getIsApproved()) {
                        System.out.println("You cannot create internships until your account is approved.");
                        break;
                    }
                    createInternship(rep);
                    break;
                case 2:
                    viewCreatedInternships(rep);
                    break;
                case 3:
                    viewApplicationsForMyInternships(rep);
                    break;
                case 4:
                    reviewStudentApplications(rep);
                    break;
                case 5:
                    editInternship(rep);
                    break;
                case 6:
                    deleteInternship(rep);
                    break;
                case 7:
                    toggleInternshipVisibility(rep);
                    break;
                case 8:
                    viewCompanyRepStatistics(rep);
                    break;
                case 9:
                    handleChangePassword();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }

        private void showStaffMenu() {
            System.out.println("1. Approve/Reject Internship Opportunities");
            System.out.println("2. Approve/Reject Company Representatives");
            System.out.println("3. Review Withdrawal Requests");
            System.out.println("4. Generate Reports");
            System.out.println("5. View All Internship Opportunities");
            System.out.println("6. Search Internships");
            System.out.println("7. View System Statistics");
            System.out.println("8. Change Password");
        }

        private void handleStaffChoice(int choice) {
            CareerCenterStaff staff = (CareerCenterStaff) currentUser;
            
            switch (choice) {
                case 1:
                    manageInternshipApprovals(staff);
                    break;
                case 2:
                    manageCompanyRepApprovals(staff);
                    break;
                case 3:
                    reviewWithdrawalRequests(staff);
                    break;
                case 4:
                    generateReports();
                    break;
                case 5:
                    viewAllInternships();
                    break;
                case 6:
                    searchInternships();
                    break;
                case 7:
                    viewSystemStatistics();
                    break;
                case 8:
                    handleChangePassword();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }

        private void browseInternshipsWithFilters(Student student) {
            System.out.println("\n--- Browse Internships ---");
            System.out.println("Showing internships filtered by your profile (Year " + student.getYear() + ", " + student.getMajor() + ")");
            
            // Display saved filter if exists
            if (hasLevelFilter && lastFilterLevel != null) {
                System.out.println("(Last filter: " + lastFilterLevel + " level)");
            }
            
            System.out.println("Filter by:");
            System.out.println("1. All eligible internships (filtered by major, level, dates)");
            System.out.println("2. Additional filter by level");
            System.out.println("3. Use last filter" + (hasLevelFilter ? " (" + lastFilterLevel + ")" : " (none saved)"));
            System.out.print("Choose filter option: ");
            
            int filterChoice = getIntInput();
            List<InternshipOpportunity> opportunities = internshipService.getFilteredOpportunities(student);
            
            if (filterChoice == 2) {
                System.out.print("Enter level (BASIC/INTERMEDIATE/ADVANCED): ");
                String levelStr = scanner.nextLine().trim().toUpperCase();
                try {
                    InternshipLevel level = InternshipLevel.valueOf(levelStr);
                    opportunities = internshipService.filterByLevel(opportunities, level);
                    // Save filter for next time
                    lastFilterLevel = level;
                    hasLevelFilter = true;
                    System.out.println("Filter saved for this session.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid level. Showing all eligible internships.");
                    hasLevelFilter = false;
                }
            } else if (filterChoice == 3 && hasLevelFilter && lastFilterLevel != null) {
                opportunities = internshipService.filterByLevel(opportunities, lastFilterLevel);
                System.out.println("Applied last filter: " + lastFilterLevel);
            } else {
                hasLevelFilter = false;
            }
            
            // Sort alphabetically by title (default sorting per assignment)
            opportunities.sort((o1, o2) -> o1.getTitle().compareToIgnoreCase(o2.getTitle()));
            
            displayInternshipList(opportunities);
        }

        private void displayInternshipList(List<InternshipOpportunity> opportunities) {
            if (opportunities.isEmpty()) {
                System.out.println("No internships found.");
                return;
            }
            
            System.out.println("\n--- Internships ---");
                int index = 1;
                for (InternshipOpportunity opp : opportunities) {
                    System.out.println(index + ". " + opp.getTitle() + " - " + opp.getCompanyName());
                    System.out.println("   Level: " + opp.getLevel() + ", Status: " + opp.getStatus());
                
                // Display preferred major if set
                if (opp.getPreferredMajor() != null && !opp.getPreferredMajor().isEmpty()) {
                    System.out.println("   Preferred Major(s): " + String.join(", ", opp.getPreferredMajor()));
                }
                
                // Display slots and remaining
                System.out.println("   Slots: " + opp.getFilledSlots() + "/" + opp.getTotalSlots() + 
                                 " (Remaining: " + opp.remainingSlots() + ")");
                
                // Display application period
                if (opp.getOpenDate() != null && opp.getCloseDate() != null) {
                    System.out.println("   Application Period: " + opp.getOpenDate() + " to " + opp.getCloseDate());
                }
                
                index++;
            }
        }

        private void viewMyApplications(Student student) {
            List<Application> applications = student.getApplications();
            System.out.println("\n--- My Applications ---");
            if (applications.isEmpty()) {
                System.out.println("You have no applications.");
            } else {
                int index = 1;
                for (Application app : applications) {
                    System.out.println(index + ". " + app.getOpportunity().getTitle() + 
                                     " - Status: " + getApplicationStatusDisplay(app.getStatus()));
                    index++;
                }
            }
        }

        private void applyForInternship(Student student) {
            List<InternshipOpportunity> opportunities = internshipService.getFilteredOpportunities(student);
            if (opportunities.isEmpty()) {
                System.out.println("No internships available to apply for.");
                System.out.println("Internships are filtered by your year, major, and eligibility.");
                return;
            }

            // Sort alphabetically by title (default sorting per assignment)
            opportunities.sort((o1, o2) -> o1.getTitle().compareToIgnoreCase(o2.getTitle()));

            System.out.println("\n--- Available Internships (Filtered for You) ---");
            for (int i = 0; i < opportunities.size(); i++) {
                InternshipOpportunity opp = opportunities.get(i);
                System.out.println((i + 1) + ". " + opp.getTitle() + " - " + opp.getCompanyName());
                System.out.println("   Level: " + opp.getLevel() + ", Remaining slots: " + opp.remainingSlots());
                if (opp.getOpenDate() != null && opp.getCloseDate() != null) {
                    System.out.println("   Application Period: " + opp.getOpenDate() + " to " + opp.getCloseDate());
                }
            }

            System.out.print("Select internship number to apply: ");
            int choice = getIntInput();
            
            if (choice > 0 && choice <= opportunities.size()) {
                InternshipOpportunity selected = opportunities.get(choice - 1);
                try {
                    Application app = new Application(student, selected);
                    student.addApplication(app);
                    applicationService.addApplication(app);
                    System.out.println("Application submitted successfully!");
                    System.out.println(ColorUtil.colored("Status: PENDING - Awaiting company review", ColorUtil.YELLOW));
                } catch (ApplicationException e) {
                    System.out.println(ColorUtil.colored("Application failed: " + e.getMessage(), ColorUtil.RED));
                }
                } else {
                System.out.println("Invalid selection.");
            }
        }

        private void acceptPlacementConfirmation(Student student) {
            List<Application> acceptedApps = student.getSuccessfulApplications();
            if (acceptedApps.isEmpty()) {
                System.out.println("You have no approved applications awaiting your confirmation.");
                System.out.println("Applications must be approved by the company representative first.");
                return;
            }

            System.out.println("\n--- Applications Approved by Company (Awaiting Your Confirmation) ---");
            for (int i = 0; i < acceptedApps.size(); i++) {
                Application app = acceptedApps.get(i);
                System.out.println((i + 1) + ". " + app.getOpportunity().getTitle() + 
                                 " - " + app.getOpportunity().getCompanyName());
                System.out.println("   Status: Approved by company - Awaiting your acceptance");
            }

            System.out.print("Select placement to confirm (number): ");
            int choice = getIntInput();

            if (choice > 0 && choice <= acceptedApps.size()) {
                Application selected = acceptedApps.get(choice - 1);
                try {
                    applicationService.accept(student, selected);
                    System.out.println("\n✓ Placement confirmed successfully!");
                    System.out.println("Your internship placement is now finalized.");
                    System.out.println("All other applications have been automatically withdrawn.");
                } catch (ApplicationException e) {
                    System.out.println("Failed to confirm placement: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid selection.");
            }
        }

        private void withdrawApplication(Student student) {
            List<Application> activeApps = new ArrayList<>();
            for (Application app : student.getApplications()) {
                if (app.getStatus() == ApplicationStatus.PENDING) {
                    activeApps.add(app);
                }
            }

            if (activeApps.isEmpty()) {
                System.out.println("You have no pending applications to withdraw.");
                return;
            }

            System.out.println("\n--- Your Pending Applications ---");
            for (int i = 0; i < activeApps.size(); i++) {
                Application app = activeApps.get(i);
                System.out.println((i + 1) + ". " + app.getOpportunity().getTitle() + 
                                 " - " + app.getOpportunity().getCompanyName());
            }

            System.out.print("Select application to withdraw (number): ");
            int choice = getIntInput();

            if (choice > 0 && choice <= activeApps.size()) {
                Application selected = activeApps.get(choice - 1);
                selected.setStatus(ApplicationStatus.WITHDRAWN);
                System.out.println("Application withdrawn successfully.");
            } else {
                System.out.println("Invalid selection.");
            }
        }

        private void requestPostConfirmationWithdrawal(Student student) {
            // Find accepted/confirmed applications
            List<Application> confirmedApps = new ArrayList<>();
            for (Application app : student.getApplications()) {
                if (app.getStatus() == ApplicationStatus.ACCEPTED) {
                    confirmedApps.add(app);
                }
            }

            if (confirmedApps.isEmpty()) {
                System.out.println("You have no confirmed applications to request withdrawal from.");
                return;
            }

            System.out.println("\n--- Your Confirmed Applications ---");
            for (int i = 0; i < confirmedApps.size(); i++) {
                Application app = confirmedApps.get(i);
                System.out.println((i + 1) + ". " + app.getOpportunity().getTitle() + 
                                 " - " + app.getOpportunity().getCompanyName());
                if (app.getWithdrawal() != null) {
                    System.out.println("   Withdrawal Status: " + app.getWithdrawal().getStatus());
                }
            }

            System.out.print("Select application to request withdrawal (number): ");
            int choice = getIntInput();

            if (choice > 0 && choice <= confirmedApps.size()) {
                Application selected = confirmedApps.get(choice - 1);
                
                // Check if already has a pending withdrawal request
                if (selected.getWithdrawal() != null) {
                    System.out.println("A withdrawal request already exists for this application.");
                    System.out.println("Status: " + selected.getWithdrawal().getStatus());
                    return;
                }

                System.out.print("Enter reason for withdrawal: ");
                String reason = scanner.nextLine().trim();
                
                if (reason.isEmpty()) {
                    System.out.println("Reason cannot be empty.");
                    return;
                }

                applicationService.requestForWithdrawal(selected);
                selected.getWithdrawal().setWithdrawalReason(reason);
                System.out.println(ColorUtil.colored("Withdrawal request submitted successfully.", ColorUtil.GREEN));
                System.out.println(ColorUtil.colored("Staff will review your request.", ColorUtil.GREEN));
            } else {
                System.out.println("Invalid selection.");
            }
        }

        private void createInternship(CompanyRepresentative rep) {
            // Check 5 active internships limit
            if (!rep.canCreateMoreInternships()) {
                System.out.println("You have reached the maximum of 5 active internships.");
                System.out.println("Please delete or wait for some to be filled before creating new ones.");
                return;
            }
            
            System.out.println("\n--- Create Internship Opportunity ---");
            System.out.print("Enter internship title: ");
            String title = scanner.nextLine().trim();
            
            InternshipOpportunity opp = new InternshipOpportunity(title, rep.getCompanyName(), rep);
            
            System.out.print("Enter description: ");
            String description = scanner.nextLine().trim();
            opp.setDescription(description);
            
            System.out.print("Enter level (BASIC/INTERMEDIATE/ADVANCED): ");
            String levelStr = scanner.nextLine().trim().toUpperCase();
            try {
                opp.setLevel(InternshipLevel.valueOf(levelStr));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid level. Setting to BASIC.");
                opp.setLevel(InternshipLevel.BASIC);
            }
            
            System.out.print("Enter total slots (max 10): ");
            int slots = getIntInput();
            if (slots < 1 || slots > 10) {
                System.out.println("Invalid slots. Must be between 1 and 10. Setting to 5.");
                slots = 5;
            }
            opp.setTotalSlots(slots);
            
            System.out.print("Enter open date (YYYY-MM-DD): ");
            String openDateStr = scanner.nextLine().trim();
            try {
                opp.setOpenDate(java.time.LocalDate.parse(openDateStr));
            } catch (Exception e) {
                System.out.println("Invalid date format. Setting to today.");
                opp.setOpenDate(java.time.LocalDate.now());
            }
            
            System.out.print("Enter close date (YYYY-MM-DD): ");
            String closeDateStr = scanner.nextLine().trim();
            try {
                opp.setCloseDate(java.time.LocalDate.parse(closeDateStr));
            } catch (Exception e) {
                System.out.println("Invalid date format. Setting to 1 year from today.");
                opp.setCloseDate(java.time.LocalDate.now().plusYears(1));
            }            
            // Set preferred major(s)
            System.out.print("Set preferred major(s)? (Y/N): ");
            String setPref = scanner.nextLine().trim().toUpperCase();
            if (setPref.equals("Y")) {
                System.out.print("Enter preferred major(s) (comma-separated, e.g., Computer Science,Engineering): ");
                String majorsStr = scanner.nextLine().trim();
                if (!majorsStr.isEmpty()) {
                    java.util.List<String> majors = new java.util.ArrayList<>();
                    for (String major : majorsStr.split(",")) {
                        majors.add(major.trim());
                    }
                    opp.setPreferredMajor(majors);
                    System.out.println("Preferred major(s) set: " + String.join(", ", majors));
                }
            } else {
                System.out.println("No preferred major set - all students can see this internship.");
            }
            
            // Add the opportunity directly (staff approval will happen later)
            internshipService.addOpportunity(opp);
            rep.createInternship(opp);
            System.out.println(ColorUtil.colored("Internship opportunity created and submitted for approval!", ColorUtil.GREEN));
        }

        private void viewCreatedInternships(CompanyRepresentative rep) {
            List<InternshipOpportunity> internships = rep.getCreatedInternships();
            System.out.println("\n--- My Created Internships ---");
            if (internships.isEmpty()) {
                System.out.println(ColorUtil.colored("You have not created any internships.", ColorUtil.YELLOW));
            } else {
                int index = 1;
                for (InternshipOpportunity opp : internships) {
                    System.out.println(index + ". " + opp.getTitle());
                    System.out.println("   Status: " + opp.getStatus() + ", Visible: " + opp.isVisible());
                    System.out.println("   Slots: " + opp.getFilledSlots() + "/" + opp.getTotalSlots());
                    index++;
                }
            }
        }

        private void editInternship(CompanyRepresentative rep) {
            List<InternshipOpportunity> internships = rep.getCreatedInternships();
            List<InternshipOpportunity> editable = new ArrayList<>();
            
            for (InternshipOpportunity opp : internships) {
                if (opp.getStatus() == InternshipStatus.PENDING) {
                    editable.add(opp);
                }
            }
            
            if (editable.isEmpty()) {
                System.out.println("No pending internships to edit.");
                return;
            }
            
            System.out.println("\n--- Editable Internships (Pending Only) ---");
            for (int i = 0; i < editable.size(); i++) {
                System.out.println((i + 1) + ". " + editable.get(i).getTitle());
            }
            
            System.out.print("Select internship to edit (number): ");
            int choice = getIntInput();
            
            if (choice > 0 && choice <= editable.size()) {
                InternshipOpportunity selected = editable.get(choice - 1);
                
                System.out.print("New title (press enter to keep current): ");
                String title = scanner.nextLine().trim();
                if (!title.isEmpty()) {
                    selected.setTitle(title);
                }
                
                System.out.print("New description (press enter to keep current): ");
                String desc = scanner.nextLine().trim();
                if (!desc.isEmpty()) {
                    selected.setDescription(desc);
                }
                
                System.out.print("New level (BASIC/INTERMEDIATE/ADVANCED, press enter to keep current): ");
                String levelStr = scanner.nextLine().trim().toUpperCase();
                if (!levelStr.isEmpty()) {
                    try {
                        selected.setLevel(InternshipLevel.valueOf(levelStr));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid level. Keeping current.");
                    }
                }
                
                System.out.print("New total slots (0 to keep current, max 10): ");
                int slots = getIntInput();
                if (slots > 0) {
                    if (slots > 10) {
                        System.out.println("Maximum 10 slots allowed. Keeping current.");
                    } else {
                        selected.setTotalSlots(slots);
                    }
                }
                
                System.out.println(ColorUtil.colored("Internship updated successfully!", ColorUtil.GREEN));
            } else {
                System.out.println("Invalid selection.");
            }
        }

        private void deleteInternship(CompanyRepresentative rep) {
            List<InternshipOpportunity> internships = rep.getCreatedInternships();
            List<InternshipOpportunity> deletable = new ArrayList<>();
            
            for (InternshipOpportunity opp : internships) {
                if (opp.getStatus() == InternshipStatus.PENDING) {
                    deletable.add(opp);
                }
            }
            
            if (deletable.isEmpty()) {
                System.out.println("No pending internships to delete.");
                return;
            }
            
            System.out.println("\n--- Deletable Internships (Pending Only) ---");
            for (int i = 0; i < deletable.size(); i++) {
                System.out.println((i + 1) + ". " + deletable.get(i).getTitle());
            }
            
            System.out.print("Select internship to delete (number): ");
            int choice = getIntInput();
            
            if (choice > 0 && choice <= deletable.size()) {
                InternshipOpportunity selected = deletable.get(choice - 1);
                System.out.print("Are you sure you want to delete '" + selected.getTitle() + "'? (yes/no): ");
                String confirm = scanner.nextLine().trim().toLowerCase();
                
                if (confirm.equals("yes")) {
                    boolean deleted = internshipService.deleteOpportunity(rep, selected);
                    if (deleted) {
                        System.out.println("Internship deleted successfully!");
                    } else {
                        System.out.println("Failed to delete internship.");
                    }
                } else {
                    System.out.println("Deletion cancelled.");
                }
            } else {
                System.out.println("Invalid selection.");
            }
        }

        private void viewApplicationsForMyInternships(CompanyRepresentative rep) {
            if (rep == null) {
                System.out.println("Error: Representative not found.");
                return;
            }

            List<InternshipOpportunity> myInternships = rep.getCreatedInternships();
            if (myInternships.isEmpty()) {
                System.out.println(ColorUtil.colored("You have not created any internships yet.", ColorUtil.YELLOW));
                return;
            }

            List<Application> allApplications = applicationService.getAllApplications();
            boolean hasApplications = false;

            System.out.println("\n=== Applications for My Internships ===");
            
            for (InternshipOpportunity opp : myInternships) {
                List<Application> oppApplications = new ArrayList<>();
                
                for (Application app : allApplications) {
                    if (app != null && app.getOpportunity() != null && 
                        app.getOpportunity().equals(opp)) {
                        oppApplications.add(app);
                    }
                }
                
                if (!oppApplications.isEmpty()) {
                    hasApplications = true;
                    System.out.println("\n" + opp.getTitle() + " (" + opp.getStatus() + ")");
                    System.out.println("  Total Applications: " + oppApplications.size());
                    
                    for (int i = 0; i < oppApplications.size(); i++) {
                        Application app = oppApplications.get(i);
                        Student student = app.getStudent();
                        System.out.println("  " + (i + 1) + ". " + student.getName() + 
                                         " (Year " + student.getYear() + ", " + student.getMajor() + ")");
                        System.out.println("     Status: " + getApplicationStatusDisplay(app.getStatus()));
                    }
                }
            }
            
            if (!hasApplications) {
                System.out.println("No applications received yet for your internships.");
            }
            System.out.println("=====================================");
        }

        private void reviewStudentApplications(CompanyRepresentative rep) {
            if (rep == null) {
                System.out.println("Error: Representative not found.");
                return;
            }

            List<InternshipOpportunity> myInternships = rep.getCreatedInternships();
            List<Application> allApplications = applicationService.getAllApplications();
            List<Application> pendingApplications = new ArrayList<>();
            
            // Find pending applications for this rep's internships
            for (Application app : allApplications) {
                if (app != null && app.getStatus() == ApplicationStatus.PENDING &&
                    app.getOpportunity() != null) {
                    
                    // Check if this application is for one of rep's internships
                    for (InternshipOpportunity opp : myInternships) {
                        if (opp.equals(app.getOpportunity())) {
                            pendingApplications.add(app);
                            break;
                        }
                    }
                }
            }
            
            if (pendingApplications.isEmpty()) {
                System.out.println("No pending applications to review.");
                return;
            }
            
            System.out.println("\n--- Pending Student Applications ---");
            for (int i = 0; i < pendingApplications.size(); i++) {
                Application app = pendingApplications.get(i);
                Student student = app.getStudent();
                System.out.println((i + 1) + ". " + student.getName() + 
                                 " (Year " + student.getYear() + ", " + student.getMajor() + ")");
                System.out.println("   Applied for: " + app.getOpportunity().getTitle());
                System.out.println("   Student ID: " + student.getId());
            }
            
            System.out.print("Select application to review (number): ");
            int choice = getIntInput();
            
            if (choice > 0 && choice <= pendingApplications.size()) {
                Application selected = pendingApplications.get(choice - 1);
                System.out.print("Decision (APPROVE/REJECT): ");
                String decisionStr = scanner.nextLine().trim().toUpperCase();
                
                if (decisionStr.equals("APPROVE")) {
                    applicationService.review(selected.getOpportunity(), selected, ApplicationStatus.ACCEPTED);
                    System.out.println(ColorUtil.colored("Application approved!", ColorUtil.GREEN));
                    System.out.println("Student's application status is now 'Successful'.");
                    System.out.println("Student can now accept the placement confirmation.");
                } else if (decisionStr.equals("REJECT")) {
                    applicationService.review(selected.getOpportunity(), selected, ApplicationStatus.REJECTED);
                    System.out.println(ColorUtil.colored("Application rejected.", ColorUtil.YELLOW));
                } else {
                    System.out.println("Invalid decision. Please enter 'APPROVE' or 'REJECT'.");
                }
            } else {
                System.out.println("Invalid selection. Please enter a number between 1 and " + pendingApplications.size());
            }
        }

        private void toggleInternshipVisibility(CompanyRepresentative rep) {
            List<InternshipOpportunity> internships = rep.getCreatedInternships();
            List<InternshipOpportunity> toggleable = new ArrayList<>();
            
            for (InternshipOpportunity opp : internships) {
                if (opp.getStatus() == InternshipStatus.APPROVED) {
                    toggleable.add(opp);
                }
            }
            
            if (toggleable.isEmpty()) {
                System.out.println(ColorUtil.colored("No approved internships to toggle visibility.", ColorUtil.YELLOW));
                return;
            }
            
            System.out.println("\n--- Approved Internships ---");
            for (int i = 0; i < toggleable.size(); i++) {
                InternshipOpportunity opp = toggleable.get(i);
                System.out.println((i + 1) + ". " + opp.getTitle() + " - Visible: " + opp.isVisible());
            }
            
            System.out.print("Select internship to toggle visibility (number): ");
            int choice = getIntInput();
            
            if (choice > 0 && choice <= toggleable.size()) {
                InternshipOpportunity selected = toggleable.get(choice - 1);
                
                // If making visible, check the 5 active limit
                if (!selected.isVisible() && !rep.canCreateMoreInternships()) {
                    System.out.println("Cannot make visible: You have reached the maximum of 5 active internships.");
                    return;
                }
                
                try {
                    internshipService.toggleVisibility(selected);
                    System.out.println("Visibility toggled! Now: " + (selected.isVisible() ? "Visible" : "Hidden"));
                } catch (IllegalStateException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid selection.");
            }
        }

        private void manageInternshipApprovals(CareerCenterStaff staff) {
            if (staff == null) {
                System.out.println("Error: Staff member not found.");
                return;
            }

            List<InternshipOpportunity> allOpps = internshipService.getAllOpportunities();
            List<InternshipOpportunity> pending = new java.util.ArrayList<>();
            
            for (InternshipOpportunity opp : allOpps) {
                if (opp != null && opp.getStatus() == InternshipStatus.PENDING) {
                    pending.add(opp);
                }
            }
            
            if (pending.isEmpty()) {
                System.out.println("No pending internship opportunities.");
                return;
            }
            
            // Sort alphabetically by title
            pending.sort((o1, o2) -> o1.getTitle().compareToIgnoreCase(o2.getTitle()));
            
            System.out.println("\n--- Pending Internship Opportunities ---");
            for (int i = 0; i < pending.size(); i++) {
                InternshipOpportunity opp = pending.get(i);
                System.out.println((i + 1) + ". " + opp.getTitle() + " - " + opp.getCompanyName());
            }
            
            System.out.print("Select internship to review (number): ");
            int choice = getIntInput();
            
            if (choice > 0 && choice <= pending.size()) {
                InternshipOpportunity selected = pending.get(choice - 1);
                System.out.print("Approve (A) or Reject (R): ");
                String decision = scanner.nextLine().trim().toUpperCase();
                
                if (decision.equals("A")) {
                    internshipService.approve(staff, selected);
                    System.out.println(ColorUtil.colored("Internship approved!", ColorUtil.GREEN));
                } else if (decision.equals("R")) {
                    internshipService.reject(staff, selected);
                    System.out.println("Internship rejected.");
                } else {
                    System.out.println("Invalid decision. Please enter 'A' or 'R'.");
                }
            } else {
                System.out.println("Invalid selection. Please enter a number between 1 and " + pending.size());
            }
        }

        private void manageCompanyRepApprovals(CareerCenterStaff staff) {
            if (staff == null) {
                System.out.println("Error: Staff member not found.");
                return;
            }

            List<CompanyRepresentative> reps = registrationService.getRepresentatives();
            List<CompanyRepresentative> pending = new java.util.ArrayList<>();
            
            for (CompanyRepresentative rep : reps) {
                if (rep != null && !rep.getIsApproved()) {
                    pending.add(rep);
                }
            }
            
            if (pending.isEmpty()) {
                System.out.println("No pending company representative approvals.");
                return;
            }
            
            System.out.println("\n--- Pending Company Representatives ---");
            for (int i = 0; i < pending.size(); i++) {
                CompanyRepresentative rep = pending.get(i);
                System.out.println((i + 1) + ". " + rep.getName() + " - " + rep.getCompanyName());
                System.out.println("   Department: " + rep.getDepartment() + ", Position: " + rep.getPosition());
                System.out.println("   User ID: " + rep.getId());
            }
            
            System.out.print("Select representative to review (number): ");
            int choice = getIntInput();
            
            if (choice > 0 && choice <= pending.size()) {
                CompanyRepresentative selected = pending.get(choice - 1);
                System.out.print("Approve (A) or Reject (R): ");
                String decision = scanner.nextLine().trim().toUpperCase();
                
                if (decision.equals("A")) {
                    registrationService.approveRepresentative(staff, selected);
                    System.out.println("Company representative approved!");
                } else if (decision.equals("R")) {
                    registrationService.rejectRepresentative(staff, selected);
                    System.out.println("Company representative rejected.");
                } else {
                    System.out.println("Invalid decision. Please enter 'A' or 'R'.");
                }
            } else {
                System.out.println("Invalid selection. Please enter a number between 1 and " + pending.size());
            }
        }

        private void reviewWithdrawalRequests(CareerCenterStaff staff) {
            List<Application> allApplications = applicationService.getAllApplications();
            List<Application> pendingWithdrawals = new ArrayList<>();
            
            for (Application app : allApplications) {
                if (app.getWithdrawal() != null && 
                    app.getWithdrawal().getStatus() == WithdrawalStatus.PENDING) {
                    pendingWithdrawals.add(app);
                }
            }
            
            if (pendingWithdrawals.isEmpty()) {
                System.out.println("No pending withdrawal requests.");
                return;
            }
            
            System.out.println("\n--- Pending Withdrawal Requests ---");
            for (int i = 0; i < pendingWithdrawals.size(); i++) {
                Application app = pendingWithdrawals.get(i);
                WithdrawalRequest wr = app.getWithdrawal();
                System.out.println((i + 1) + ". Student: " + app.getStudent().getName());
                System.out.println("   Internship: " + app.getOpportunity().getTitle());
                System.out.println("   Company: " + app.getOpportunity().getCompanyName());
                System.out.println("   Reason: " + wr.getWithdrawalReason());
                System.out.println("   Request Date: " + wr.getRequestDate());
            }
            
            System.out.print("Select withdrawal request to review (number): ");
            int choice = getIntInput();
            
            if (choice > 0 && choice <= pendingWithdrawals.size()) {
                Application selected = pendingWithdrawals.get(choice - 1);
                System.out.print("Decision (APPROVE/REJECT): ");
                String decisionStr = scanner.nextLine().trim().toUpperCase();
                
                WithdrawalStatus decision;
                if (decisionStr.equals("APPROVE")) {
                    decision = WithdrawalStatus.APPROVED;
                } else if (decisionStr.equals("REJECT")) {
                    decision = WithdrawalStatus.REJECTED;
                } else {
                    System.out.println("Invalid decision.");
                    return;
                }
                
                applicationService.decideWithdrawal(staff, selected, decision);
                
                if (decision == WithdrawalStatus.APPROVED) {
                    System.out.println(ColorUtil.colored("Withdrawal approved. Application marked as WITHDRAWN.", ColorUtil.GREEN));
                    System.out.println(ColorUtil.colored("Slot has been freed on the internship.", ColorUtil.GREEN));
                } else {
                    System.out.println("Withdrawal rejected. Application status unchanged.");
                }
            } else {
                System.out.println("Invalid selection.");
            }
        }

        private void generateReports() {
            System.out.println("\n--- Generate Reports ---");
            System.out.println("1. Internship Status Counts");
            System.out.println("2. Student Placements by Major");
            System.out.println("3. Applications per Internship");
            System.out.println("4. Company Representative Approval Status");
            System.out.print("Choose report: ");
            
            int choice = getIntInput();
            
            switch (choice) {
                case 1:
                    reportInternshipStatusCounts();
                    break;
                case 2:
                    reportPlacementsByMajor();
                    break;
                case 3:
                    reportApplicationsPerInternship();
                    break;
                case 4:
                    reportCompanyRepApprovalStatus();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }

        private void reportInternshipStatusCounts() {
            List<InternshipOpportunity> allOpps = internshipService.getAllOpportunities();
            
            int pending = 0;
            int approved = 0;
            int rejected = 0;
            int filled = 0;
            
            for (InternshipOpportunity opp : allOpps) {
                if (opp == null) continue;
                switch (opp.getStatus()) {
                    case PENDING:
                        pending++;
                        break;
                    case APPROVED:
                        approved++;
                        break;
                    case REJECTED:
                        rejected++;
                        break;
                    case FILLED:
                        filled++;
                        break;
                }
            }
            
            System.out.println("\n=== Internship Status Report ===");
            System.out.println("Total Internships: " + allOpps.size());
            System.out.println("  PENDING:  " + pending);
            System.out.println("  APPROVED: " + approved);
            System.out.println("  REJECTED: " + rejected);
            System.out.println("  FILLED:   " + filled);
            System.out.println("================================");
        }

        private void reportPlacementsByMajor() {
            List<Application> allApps = applicationService.getAllApplications();
            java.util.Map<String, Integer> placementsByMajor = new java.util.HashMap<>();
            
            for (Application app : allApps) {
                if (app != null && app.getStatus() == ApplicationStatus.ACCEPTED) {
                    Student student = app.getStudent();
                    if (student != null) {
                        String major = student.getMajor();
                        placementsByMajor.put(major, placementsByMajor.getOrDefault(major, 0) + 1);
                    }
                }
            }
            
            System.out.println("\n=== Student Placements by Major ===");
            if (placementsByMajor.isEmpty()) {
                System.out.println("No placements yet.");
            } else {
                for (java.util.Map.Entry<String, Integer> entry : placementsByMajor.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue() + " student(s)");
                }
            }
            System.out.println("===================================");
        }

        private void reportApplicationsPerInternship() {
            List<InternshipOpportunity> allOpps = internshipService.getAllOpportunities();
            List<Application> allApps = applicationService.getAllApplications();
            
            System.out.println("\n=== Applications per Internship ===");
            
            for (InternshipOpportunity opp : allOpps) {
                if (opp == null) continue;
                
                int appCount = 0;
                for (Application app : allApps) {
                    if (app != null && app.getOpportunity() != null && 
                        app.getOpportunity().equals(opp)) {
                        appCount++;
                    }
                }
                
                double fillRate = 0.0;
                if (opp.getTotalSlots() > 0) {
                    fillRate = (double) opp.getFilledSlots() / opp.getTotalSlots() * 100;
                }
                
                System.out.println("\n" + opp.getTitle() + " (" + opp.getCompanyName() + ")");
                System.out.println("  Applications: " + appCount);
                System.out.println("  Fill Rate: " + String.format("%.1f", fillRate) + "% " +
                                 "(" + opp.getFilledSlots() + "/" + opp.getTotalSlots() + ")");
                System.out.println("  Status: " + opp.getStatus());
            }
            
            System.out.println("\n===================================");
        }

        private void reportCompanyRepApprovalStatus() {
            List<CompanyRepresentative> allReps = registrationService.getRepresentatives();
            
            int approved = 0;
            int pending = 0;
            
            System.out.println("\n=== Company Representative Status ===");
            
            for (CompanyRepresentative rep : allReps) {
                if (rep == null) continue;
                
                String status = rep.getIsApproved() ? "APPROVED" : "PENDING";
                System.out.println(rep.getName() + " (" + rep.getCompanyName() + "): " + status);
                System.out.println("  Department: " + rep.getDepartment() + ", Position: " + rep.getPosition());
                System.out.println("  User ID: " + rep.getId());
                
                if (rep.getIsApproved()) {
                    approved++;
                } else {
                    pending++;
                }
            }
            
            System.out.println("\nSummary:");
            System.out.println("  Total: " + allReps.size());
            System.out.println("  Approved: " + approved);
            System.out.println("  Pending: " + pending);
            System.out.println("====================================");
        }

        private void viewAllInternships() {
            Set<InternshipOpportunity> all = internshipService.showAllInternshipOpportunities();
            System.out.println("\n--- All Internship Opportunities ---");
            if (all.isEmpty()) {
                System.out.println("No internships in the system.");
            } else {
                // Sort alphabetically and display
                List<InternshipOpportunity> sorted = new ArrayList<>(all);
                sorted.sort((o1, o2) -> o1.getTitle().compareToIgnoreCase(o2.getTitle()));
                displayInternshipList(sorted);
            }
        }

        private void handleChangePassword() {
            if (currentUser == null) {
                System.out.println("You must be logged in to change password.");
                return;
            }

            System.out.print("Enter current password: ");
            String oldPassword = scanner.nextLine().trim();
            System.out.print("Enter new password: ");
            String newPassword = scanner.nextLine().trim();

            if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                System.out.println("Passwords cannot be empty.");
                return;
            }

            if (newPassword.length() < 6) {
                System.out.println("New password must be at least 6 characters.");
                return;
            }
            
            // Use user ID for password change to avoid ambiguity from duplicate names
            boolean success = registrationService.changePassword(currentUser.getId(), oldPassword, newPassword);
            if (success) {
                System.out.println("Password changed successfully! You will be logged out and must re-login with the new password.");
                // Force logout so user must re-authenticate with new password
                handleLogout();
            } else {
                System.out.println("Failed to change password. Check your current password.");
            }
        }

        private void handleLogout() {
            if (currentUser != null) {
            SystemLogger.log("LOGOUT", currentUser.getId(), currentUser.getName() + " logged out");
            authService.logout(currentUser);
            currentUser = null;
            ColorUtil.printSuccess("Logged out successfully.");
            } else {
                System.out.println("No user is currently logged in.");
            }
        }

        // ========== New Features: Search, Statistics, Logging ==========
        
        /**
         * Search internships by company name or title.
         */
        private void searchInternships() {
            System.out.println("\n=== Search Internships ===");
            System.out.println("1. Search by Company Name");
            System.out.println("2. Search by Internship Title");
            System.out.println("3. Filter by Level");
            System.out.print("Choose search type: ");
            
            String choice = scanner.nextLine().trim();
            List<InternshipOpportunity> allInternships = internshipService.getOpportunities();
            List<InternshipOpportunity> results = new ArrayList<>();
            String searchDesc = "";
            
            switch (choice) {
                case "1":
                    System.out.print("Enter company name: ");
                    String company = scanner.nextLine().trim();
                    if (!company.isEmpty()) {
                        results = SearchUtil.searchByCompany(allInternships, company);
                        searchDesc = "company '" + company + "'";
                    }
                    break;
                case "2":
                    System.out.print("Enter internship title: ");
                    String title = scanner.nextLine().trim();
                    if (!title.isEmpty()) {
                        results = SearchUtil.searchByTitle(allInternships, title);
                        searchDesc = "title '" + title + "'";
                    }
                    break;
                case "3":
                    System.out.println("Select level:");
                    System.out.println("1. BASIC");
                    System.out.println("2. INTERMEDIATE");
                    System.out.println("3. ADVANCED");
                    System.out.print("Enter choice: ");
                    String levelChoice = scanner.nextLine().trim();
                    InternshipLevel level = null;
                    switch (levelChoice) {
                        case "1": level = InternshipLevel.BASIC; break;
                        case "2": level = InternshipLevel.INTERMEDIATE; break;
                        case "3": level = InternshipLevel.ADVANCED; break;
                    }
                    if (level != null) {
                        results = SearchUtil.searchByLevel(allInternships, level);
                        searchDesc = "level " + level;
                    }
                    break;
                default:
                    ColorUtil.printError("Invalid search type.");
                    return;
            }
            
            if (!searchDesc.isEmpty()) {
                SearchUtil.displaySearchResults(results, searchDesc);
                SystemLogger.logSystem("SEARCH", "Search performed: " + searchDesc + ", results: " + results.size());
            }
        }
        
        /**
         * View statistics for student applications.
         */
        private void viewStudentStatistics(Student student) {
            StatisticsUtil.printStudentStatistics(student);
            SystemLogger.log("VIEW_STATS", student.getId(), "Viewed personal statistics");
        }
        
        /**
         * View statistics for company representative's internships.
         */
        private void viewCompanyRepStatistics(CompanyRepresentative rep) {
            List<Application> allApplications = applicationService.getAllApplications();
            List<InternshipOpportunity> allInternships = internshipService.getOpportunities();
            StatisticsUtil.printCompanyRepStatistics(rep, allInternships, allApplications);
            SystemLogger.log("VIEW_STATS", rep.getId(), "Viewed company statistics");
        }
        
        /**
         * View system-wide statistics for staff.
         */
        private void viewSystemStatistics() {
            List<User> allUsers = authService.getUsers();
            List<InternshipOpportunity> allInternships = internshipService.getOpportunities();
            List<Application> allApplications = applicationService.getAllApplications();
            StatisticsUtil.printSystemStatistics(allUsers, allInternships, allApplications);
            SystemLogger.log("VIEW_STATS", currentUser.getId(), "Viewed system statistics");
        }
        
        // ========== End of New Features ==========


        private String getRoleName() {
            if (currentUser == null) return "None";
            if (currentUser instanceof Student) return "Student";
            if (currentUser instanceof CompanyRepresentative) return "Company Representative";
            if (currentUser instanceof CareerCenterStaff) return "Career Center Staff";
            return "Unknown";
        }

        /**
         * Converts internal ApplicationStatus enum to user-friendly display text.
         * 
         * <p>This method provides a mapping between internal status values
         * and the terminology specified in the assignment requirements.
         * It ensures consistent and user-friendly status messages throughout
         * the application.</p>
         * 
         * <p>Status Mappings:</p>
         * <ul>
         *   <li>PENDING → "Pending" (awaiting company review)</li>
         *   <li>ACCEPTED → "Successful" (approved by company, awaiting student confirmation)</li>
         *   <li>REJECTED → "Unsuccessful" (rejected by company)</li>
         *   <li>WITHDRAWN → "Withdrawn" (withdrawn by student)</li>
         * </ul>
         * 
         * @param status the ApplicationStatus enum value to convert
         * @return user-friendly status string suitable for display, or "Unknown" if status is null
         */
        private String getApplicationStatusDisplay(ApplicationStatus status) {
            if (status == null) return "Unknown";
            switch (status) {
                case PENDING:
                    return "Pending";
                case ACCEPTED:
                    return "Successful";
                case REJECTED:
                    return "Unsuccessful";
                case WITHDRAWN:
                    return "Withdrawn";
                default:
                    return status.toString();
            }
        }

        private int getIntInput() {
            try {
                int input = Integer.parseInt(scanner.nextLine().trim());
                return input;
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        /**
         * Loads all system data from pipe-delimited text files.
         * 
         * <p>This method orchestrates the complete data loading sequence,
         * reading from three main data files:</p>
         * <ol>
         *   <li>users.txt - User accounts (Students, Company Reps, Staff)</li>
         *   <li>internships.txt - Internship opportunities with all details</li>
         *   <li>applications.txt - Student applications linked to internships</li>
         * </ol>
         * 
         * <p>The loading process maintains referential integrity by:</p>
         * <ul>
         *   <li>Loading users first to establish base entities</li>
         *   <li>Loading internships second, linking to Company Representatives</li>
         *   <li>Loading applications last, linking to both Students and Internships</li>
         * </ul>
         * 
         * <p>After successful loading, displays a summary including:</p>
         * <ul>
         *   <li>Count of loaded entities (users, internships, applications)</li>
         *   <li>Sample login credentials for testing</li>
         * </ul>
         * 
         * <p>Error Handling:</p>
         * If files are missing or corrupted, catches DataAccessException
         * and displays a warning, allowing the system to start with empty data.
         * 
         * @see DataManager#loadUsers(String)
         * @see DataManager#loadInternships(String, List)
         * @see DataManager#loadApplications(String, List, List)
         */
        private void loadDataFromFiles() {
            try {
                // Load users first
                List<User> users = dataAccess.loadUsers("data/users.txt");
                authService.setUsers(users);
                registrationService.setUsers(users);
                
                // Load internships
                List<InternshipOpportunity> internships = dataAccess.loadInternships("data/internships.txt", users);
                for (InternshipOpportunity opp : internships) {
                    internshipService.addOpportunity(opp);
                }
                
                // Load applications
                List<Application> applications = dataAccess.loadApplications("data/applications.txt", users, internships);
                for (Application app : applications) {
                    applicationService.addApplication(app);
                }
                
                System.out.println("Data loaded successfully!");
                System.out.println("- Loaded " + users.size() + " users");
                System.out.println("- Loaded " + internships.size() + " internships");
                System.out.println("- Loaded " + applications.size() + " applications");
                System.out.println("\nSample Login Credentials:");
                System.out.println("- Student: U2345123F (Alice, password: password)");
                System.out.println("- Company Rep: john@techcorp.com (John Smith, password: password)");
                System.out.println("- Staff: admin@ntu.edu.sg (Admin, password: password)");
                
            } catch (DataAccessException e) {
                System.out.println("Warning: Could not load data from files: " + e.getMessage());
                System.out.println("Starting with empty data. You can register new users.");
            }
        }
    }
}

