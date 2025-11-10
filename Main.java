import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.run();
    }

    static class CLI {
        private Scanner scanner;
        private AuthController authController;
        private ApplicationController applicationController;
        private InternshipController internshipController;
        private RegistrationController registrationController;
        private DataManager dataManager;
        private User currentUser;

        public CLI() {
            this.scanner = new Scanner(System.in);
            this.authController = new AuthController();
            this.applicationController = new ApplicationController();
            this.internshipController = new InternshipController();
            this.registrationController = new RegistrationController();
            this.dataManager = new DataManager();
            
            // Initialize with sample data for testing
            initializeSampleData();
        }

        public void run() {
            System.out.println("=== Internship Management System ===");
            
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
            System.out.print("Enter username (name): ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            try {
                currentUser = authController.authenticate(username, password);
                if (currentUser != null) {
                    System.out.println("Login successful! Welcome, " + currentUser.getName() + "!");
                }
            } catch (AuthenticationException e) {
                System.out.println("Authentication failed: " + e.getMessage());
            }
        }

        private void handleRegisterCompanyRep() {
            System.out.print("Enter company name: ");
            String companyName = scanner.nextLine().trim();
            System.out.print("Enter email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();

            boolean success = registrationController.registerCompanyRepresentative(companyName, email, password);
            if (success) {
                System.out.println("Company representative registered successfully!");
                System.out.println("Note: Your account needs to be approved by Career Center Staff.");
            } else {
                System.out.println("Registration failed. Please check your input.");
            }
        }

        private void showMainMenu() {
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
            System.out.println("4. Accept Offer");
            System.out.println("5. Withdraw Application (Pre-Confirmation)");
            System.out.println("6. Request Post-Confirmation Withdrawal");
            System.out.println("7. Change Password");
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
                    acceptOffer(student);
                    break;
                case 5:
                    withdrawApplication(student);
                    break;
                case 6:
                    requestPostConfirmationWithdrawal(student);
                    break;
                case 7:
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
            System.out.println("3. Edit Internship");
            System.out.println("4. Delete Internship");
            System.out.println("5. Toggle Internship Visibility");
            System.out.println("6. Change Password");
            
            if (!rep.getIsApproved()) {
                System.out.println("\n Note: Your account is pending approval.");
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
                    editInternship(rep);
                    break;
                case 4:
                    deleteInternship(rep);
                    break;
                case 5:
                    toggleInternshipVisibility(rep);
                    break;
                case 6:
                    handleChangePassword();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }

        private void showStaffMenu() {
            System.out.println("1. Approve/Reject Internship Opportunities");
            System.out.println("2. Approve/Reject Company Representatives");
            System.out.println("3. Review Applications");
            System.out.println("4. Review Withdrawal Requests");
            System.out.println("5. View All Internship Opportunities");
            System.out.println("6. Change Password");
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
                    reviewApplications(staff);
                    break;
                case 4:
                    reviewWithdrawalRequests(staff);
                    break;
                case 5:
                    viewAllInternships();
                    break;
                case 6:
                    handleChangePassword();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }

        private void browseInternshipsWithFilters(Student student) {
            System.out.println("\n--- Browse Internships ---");
            System.out.println("Filter by:");
            System.out.println("1. All visible internships");
            System.out.println("2. Filter by level");
            System.out.println("3. Only open for application (within date range)");
            System.out.print("Choose filter option: ");
            
            int filterChoice = getIntInput();
            List<InternshipOpportunity> opportunities = new ArrayList<>(internshipController.getVisibleOpportunities());
            
            switch (filterChoice) {
                case 2:
                    System.out.print("Enter level (BASIC/INTERMEDIATE/ADVANCED): ");
                    String levelStr = scanner.nextLine().trim().toUpperCase();
                    try {
                        InternshipLevel level = InternshipLevel.valueOf(levelStr);
                        opportunities = internshipController.filterByLevel(opportunities, level);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid level. Showing all.");
                    }
                    break;
                case 3:
                    opportunities = internshipController.getOpenOpportunities(java.time.LocalDate.now());
                    break;
                default:
                    // Show all visible (case 1 or any other input)
                    break;
            }
            
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
                System.out.println("   Slots: " + opp.getFilledSlots() + "/" + opp.getTotalSlots() + 
                                 " (Remaining: " + opp.remainingSlots() + ")");
                if (opp.getOpenDate() != null && opp.getCloseDate() != null) {
                    System.out.println("   Application Period: " + opp.getOpenDate() + " to " + opp.getCloseDate());
                }
                index++;
            }
        }

        private void viewAvailableInternships() {
            Set<InternshipOpportunity> opportunities = internshipController.getVisibleOpportunities();
            System.out.println("\n--- Available Internships ---");
            if (opportunities.isEmpty()) {
                System.out.println("No internships available at the moment.");
            } else {
                displayInternshipList(new ArrayList<>(opportunities));
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
                                     " - Status: " + app.getStatus());
                    index++;
                }
            }
        }

        private void applyForInternship(Student student) {
            Set<InternshipOpportunity> opportunities = internshipController.getVisibleOpportunities();
            if (opportunities.isEmpty()) {
                System.out.println("No internships available to apply for.");
                return;
            }

            System.out.println("\n--- Available Internships ---");
            InternshipOpportunity[] oppArray = opportunities.toArray(new InternshipOpportunity[0]);
            for (int i = 0; i < oppArray.length; i++) {
                System.out.println((i + 1) + ". " + oppArray[i].getTitle() + " - " + oppArray[i].getCompanyName());
            }

            System.out.print("Select internship number to apply: ");
            int choice = getIntInput();
            
            if (choice > 0 && choice <= oppArray.length) {
                InternshipOpportunity selected = oppArray[choice - 1];
                try {
                    Application app = new Application(student, selected);
                    student.addApplication(app);
                    applicationController.addApplication(app);
                    System.out.println("Application submitted successfully!");
                } catch (ApplicationException e) {
                    System.out.println("Application failed: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid selection.");
            }
        }

        private void acceptOffer(Student student) {
            List<Application> acceptedApps = student.getSuccessfulApplications();
            if (acceptedApps.isEmpty()) {
                System.out.println("You have no offers to accept.");
                return;
            }

            System.out.println("\n--- Your Offers ---");
            for (int i = 0; i < acceptedApps.size(); i++) {
                Application app = acceptedApps.get(i);
                System.out.println((i + 1) + ". " + app.getOpportunity().getTitle() + 
                                 " - " + app.getOpportunity().getCompanyName());
            }

            System.out.print("Select offer to accept (number): ");
            int choice = getIntInput();

            if (choice > 0 && choice <= acceptedApps.size()) {
                Application selected = acceptedApps.get(choice - 1);
                try {
                    applicationController.accept(student, selected);
                    System.out.println("Offer accepted! All other applications have been withdrawn.");
                } catch (ApplicationException e) {
                    System.out.println("Failed to accept offer: " + e.getMessage());
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

                applicationController.requestForWithdrawal(selected);
                selected.getWithdrawal().setWithdrawalReason(reason);
                System.out.println("Withdrawal request submitted successfully.");
                System.out.println("Staff will review your request.");
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
            
            System.out.print("Enter total slots: ");
            int slots = getIntInput();
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
            
            // Add the opportunity directly (staff approval will happen later)
            internshipController.addOpportunity(opp);
            rep.createInternship(opp);
            System.out.println("Internship opportunity created and submitted for approval!");
        }

        private void viewCreatedInternships(CompanyRepresentative rep) {
            List<InternshipOpportunity> internships = rep.getCreatedInternships();
            System.out.println("\n--- My Created Internships ---");
            if (internships.isEmpty()) {
                System.out.println("You have not created any internships.");
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
                
                System.out.print("New total slots (0 to keep current): ");
                int slots = getIntInput();
                if (slots > 0) {
                    selected.setTotalSlots(slots);
                }
                
                System.out.println("Internship updated successfully!");
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
                    boolean deleted = internshipController.deleteOpportunity(rep, selected);
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

        private void toggleInternshipVisibility(CompanyRepresentative rep) {
            List<InternshipOpportunity> internships = rep.getCreatedInternships();
            List<InternshipOpportunity> toggleable = new ArrayList<>();
            
            for (InternshipOpportunity opp : internships) {
                if (opp.getStatus() == InternshipStatus.APPROVED) {
                    toggleable.add(opp);
                }
            }
            
            if (toggleable.isEmpty()) {
                System.out.println("No approved internships to toggle visibility.");
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
                    internshipController.toggleVisibility(selected);
                    System.out.println("Visibility toggled! Now: " + (selected.isVisible() ? "Visible" : "Hidden"));
                } catch (IllegalStateException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                System.out.println("Invalid selection.");
            }
        }

        private void manageInternshipApprovals(CareerCenterStaff staff) {
            List<InternshipOpportunity> allOpps = internshipController.getAllOpportunities();
            List<InternshipOpportunity> pending = new java.util.ArrayList<>();
            
            for (InternshipOpportunity opp : allOpps) {
                if (opp.getStatus() == InternshipStatus.PENDING) {
                    pending.add(opp);
                }
            }
            
            if (pending.isEmpty()) {
                System.out.println("No pending internship opportunities.");
                return;
            }
            
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
                    internshipController.approve(staff, selected);
                    System.out.println("Internship approved!");
                } else if (decision.equals("R")) {
                    internshipController.reject(staff, selected);
                    System.out.println("Internship rejected.");
                } else {
                    System.out.println("Invalid decision.");
                }
            }
        }

        private void manageCompanyRepApprovals(CareerCenterStaff staff) {
            List<CompanyRepresentative> reps = registrationController.getRepresentatives();
            List<CompanyRepresentative> pending = new java.util.ArrayList<>();
            
            for (CompanyRepresentative rep : reps) {
                if (!rep.getIsApproved()) {
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
            }
            
            System.out.print("Select representative to review (number): ");
            int choice = getIntInput();
            
            if (choice > 0 && choice <= pending.size()) {
                CompanyRepresentative selected = pending.get(choice - 1);
                System.out.print("Approve (A) or Reject (R): ");
                String decision = scanner.nextLine().trim().toUpperCase();
                
                if (decision.equals("A")) {
                    registrationController.approveRepresentative(staff, selected);
                    System.out.println("Company representative approved!");
                } else if (decision.equals("R")) {
                    registrationController.rejectRepresentative(staff, selected);
                    System.out.println("Company representative rejected.");
                } else {
                    System.out.println("Invalid decision.");
                }
            }
        }

        private void reviewApplications(CareerCenterStaff staff) {
            List<Application> applications = applicationController.getAllApplications();
            List<Application> pending = new java.util.ArrayList<>();
            
            for (Application app : applications) {
                if (app.getStatus() == ApplicationStatus.PENDING) {
                    pending.add(app);
                }
            }
            
            if (pending.isEmpty()) {
                System.out.println("No pending applications to review.");
                return;
            }
            
            System.out.println("\n--- Pending Applications ---");
            for (int i = 0; i < pending.size(); i++) {
                Application app = pending.get(i);
                System.out.println((i + 1) + ". " + app.getStudent().getName() + 
                                 " applied for " + app.getOpportunity().getTitle());
            }
            
            System.out.print("Select application to review (number): ");
            int choice = getIntInput();
            
            if (choice > 0 && choice <= pending.size()) {
                Application selected = pending.get(choice - 1);
                System.out.print("Decision (ACCEPTED/REJECTED): ");
                String decisionStr = scanner.nextLine().trim().toUpperCase();
                
                try {
                    ApplicationStatus decision = ApplicationStatus.valueOf(decisionStr);
                    applicationController.review(selected.getOpportunity(), selected, decision);
                    System.out.println("Application reviewed: " + decision);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid decision.");
                }
            }
        }

        private void reviewWithdrawalRequests(CareerCenterStaff staff) {
            List<Application> allApplications = applicationController.getAllApplications();
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
                
                applicationController.decideWithdrawal(staff, selected, decision);
                
                if (decision == WithdrawalStatus.APPROVED) {
                    System.out.println("Withdrawal approved. Application marked as WITHDRAWN.");
                    System.out.println("Slot has been freed on the internship.");
                } else {
                    System.out.println("Withdrawal rejected. Application status unchanged.");
                }
            } else {
                System.out.println("Invalid selection.");
            }
        }

        private void viewAllInternships() {
            Set<InternshipOpportunity> all = internshipController.showAllInternshipOpportunities();
            System.out.println("\n--- All Internship Opportunities ---");
            if (all.isEmpty()) {
                System.out.println("No internships in the system.");
            }
        }

        private void handleChangePassword() {
            System.out.print("Enter current password: ");
            String oldPassword = scanner.nextLine().trim();
            System.out.print("Enter new password: ");
            String newPassword = scanner.nextLine().trim();
            
            boolean success = registrationController.changePassword(currentUser.getName(), oldPassword, newPassword);
            if (success) {
                System.out.println("Password changed successfully!");
            } else {
                System.out.println("Failed to change password. Check your current password.");
            }
        }

        private void handleLogout() {
            authController.logout(currentUser);
            currentUser = null;
            System.out.println("Logged out successfully.");
        }

        private String getRoleName() {
            if (currentUser instanceof Student) return "Student";
            if (currentUser instanceof CompanyRepresentative) return "Company Representative";
            if (currentUser instanceof CareerCenterStaff) return "Career Center Staff";
            return "Unknown";
        }

        private int getIntInput() {
            try {
                int input = Integer.parseInt(scanner.nextLine().trim());
                return input;
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        private void initializeSampleData() {
            // Create sample users for testing
            Student student1 = new Student("STU001", "Alice", "Computer Science", 2);
            Student student2 = new Student("STU002", "Bob", "Engineering", 3);
            
            CompanyRepresentative rep1 = new CompanyRepresentative("REP001", "John", "Tech Corp");
            rep1.setApproved(true);
            
            CareerCenterStaff staff1 = new CareerCenterStaff("STAFF001", "Admin", "Career Services");
            
            // Add to data manager
            List<User> users = new java.util.ArrayList<>();
            users.add(student1);
            users.add(student2);
            users.add(rep1);
            users.add(staff1);
            
            try {
                dataManager.saveUsers("users.dat", users);
                authController.setUsers(dataManager.loadUsers("users.dat"));
                registrationController.setUsers(dataManager.loadUsers("users.dat"));
            } catch (DataAccessException e) {
                System.out.println("Warning: Could not initialize sample data: " + e.getMessage());
            }
            
            System.out.println("Sample data initialized:");
            System.out.println("- Students: Alice (password: password), Bob (password: password)");
            System.out.println("- Company Rep: John from Tech Corp (password: password, approved)");
            System.out.println("- Staff: Admin (password: password)");
        }
    }
}

