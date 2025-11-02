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
            System.out.println("1. View Available Internships");
            System.out.println("2. View My Applications");
            System.out.println("3. Apply for Internship");
            System.out.println("4. Change Password");
        }

        private void handleStudentChoice(int choice) {
            Student student = (Student) currentUser;
            
            switch (choice) {
                case 1:
                    viewAvailableInternships();
                    break;
                case 2:
                    viewMyApplications(student);
                    break;
                case 3:
                    applyForInternship(student);
                    break;
                case 4:
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
            System.out.println("3. Change Password");
            
            if (!rep.getIsApproved()) {
                System.out.println("\n⚠️  Note: Your account is pending approval.");
            }
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
            System.out.println("4. View All Internship Opportunities");
            System.out.println("5. Change Password");
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
                    viewAllInternships();
                    break;
                case 5:
                    handleChangePassword();
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }

        private void viewAvailableInternships() {
            Set<InternshipOpportunity> opportunities = internshipController.getVisibleOpportunities();
            System.out.println("\n--- Available Internships ---");
            if (opportunities.isEmpty()) {
                System.out.println("No internships available at the moment.");
            } else {
                int index = 1;
                for (InternshipOpportunity opp : opportunities) {
                    System.out.println(index + ". " + opp.getTitle() + " - " + opp.getCompanyName());
                    System.out.println("   Level: " + opp.getLevel() + ", Status: " + opp.getStatus());
                    index++;
                }
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
                if (student.canApplyForInternship(selected)) {
                    Application app = new Application(student, selected);
                    student.addApplication(app);
                    applicationController.addApplication(app);
                    System.out.println("Application submitted successfully!");
                } else {
                    System.out.println("You cannot apply for this internship. Check your eligibility or application limits.");
                }
            } else {
                System.out.println("Invalid selection.");
            }
        }

        private void createInternship(CompanyRepresentative rep) {
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
                    index++;
                }
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

