import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of application management services.
 * 
 * <p>This class implements the ApplicationServiceInterface, providing
 * application lifecycle management with loose coupling.</p>
 * 
 * @version 1.0
 */
public class ApplicationController implements ApplicationServiceInterface {
    private List<Application> applications;

    public ApplicationController() {
        this.applications = new ArrayList<>();
    }

    public boolean accept(Student student, Application app) throws ApplicationException {
        if (student == null || app == null) {
            return false;
        }
        if (!app.getStudent().equals(student)) {
            return false;
        }
        if (app.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new ApplicationException("Application must be in ACCEPTED status to be confirmed");
        }

        // Reserve slot on the internship
        InternshipOpportunity opp = app.getOpportunity();
        boolean slotReserved = opp.reserveSlot();
        if (!slotReserved) {
            throw new ApplicationException("No slots available for this internship");
        }

        // Auto-withdraw all other active applications from the same student
        for (Application otherApp : student.getApplications()) {
            if (otherApp != app && otherApp.isActive()) {
                otherApp.setStatus(ApplicationStatus.WITHDRAWN);
            }
        }
        
        // Persist changes to file
        try {
            writeApplicationsToFile("data/applications.txt");
            SystemLogger.logSystem("APPLICATION_ACCEPTED", "Student " + student.getName() + " accepted placement for " + opp.getTitle());
        } catch (Exception e) {
            SystemLogger.logSystem("ERROR", "Failed to save applications after acceptance: " + e.getMessage());
        }

        return true;
    }

    public boolean reject(Student student, Application app) {
        if (student == null || app == null) {
            return false;
        }
        if (!app.getStudent().equals(student)) {
            return false;
        }
        if (app.getStatus() == ApplicationStatus.PENDING) {
            app.setStatus(ApplicationStatus.REJECTED);
            return true;
        }
        return false;
    }

    public void review(InternshipOpportunity internshipOpportunity, Application app, ApplicationStatus decision) {
        if (internshipOpportunity == null || app == null || decision == null) {
            return;
        }
        if (!app.getOpportunity().equals(internshipOpportunity)) {
            return;
        }
        if (app.getStatus() == ApplicationStatus.PENDING) {
            app.setStatus(decision);
            
            // Persist changes to file
            try {
                writeApplicationsToFile("data/applications.txt");
                SystemLogger.logSystem("APPLICATION_REVIEWED", "Application for " + app.getStudent().getName() + " reviewed: " + decision);
            } catch (Exception e) {
                SystemLogger.logSystem("ERROR", "Failed to save applications after review: " + e.getMessage());
            }
        }
    }

    public void requestForWithdrawal(Application app) {
        if (app == null) {
            return;
        }

        if (app.getStatus() != null && app.getStatus() != ApplicationStatus.WITHDRAWN) {
            WithdrawalRequest withdrawal = new WithdrawalRequest(app.getStudent(), "Requested by student");
            app.setWithdrawal(withdrawal);
        }
    }

    public void decideWithdrawal(CareerCenterStaff staff, Application app, WithdrawalStatus decision) {
        if (staff == null || app == null || decision == null) {
            return;
        }

        WithdrawalRequest withdrawal = app.getWithdrawal();
        if (withdrawal != null) {
            withdrawal.decide(staff, decision);
            if (decision == WithdrawalStatus.APPROVED) {
                app.setStatus(ApplicationStatus.WITHDRAWN);
                // Free the slot on the internship
                app.getOpportunity().freeSlot();
            }
        }
    }

    public void addApplication(Application app) {
        if (app != null && !applications.contains(app)) {
            applications.add(app);
            
            // Persist changes to file
            try {
                writeApplicationsToFile("data/applications.txt");
                SystemLogger.logSystem("APPLICATION_ADDED", "Application added for " + app.getStudent().getName() + " to " + app.getOpportunity().getTitle());
            } catch (Exception e) {
                // Log the error
                SystemLogger.logSystem("ERROR", "Failed to save applications: " + e.getMessage());
                // If persistence fails, revert the change
                applications.remove(app);
            }
        }
    }

    public List<Application> getAllApplications() {
        return new ArrayList<>(applications);
    }
    
    private void writeApplicationsToFile(String filepath) throws java.io.IOException {
        if (filepath == null) throw new java.io.IOException("Invalid filepath");

        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(filepath))) {
            // Write header/comments
            pw.println("# Application Data File");
            pw.println("# Format: STUDENT_ID|INTERNSHIP_TITLE|STATUS|CREATED_DATE|UPDATED_DATE");
            pw.println();

            for (Application app : applications) {
                if (app == null) continue;
                
                pw.printf("%s|%s|%s|%s|%s\n",
                    app.getStudent().getId(),
                    app.getOpportunity().getTitle(),
                    app.getStatus(),
                    app.getCreatedAt(),
                    app.getUpdatedAt()
                );
            }
        }
    }
}

