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
        }
    }

    public List<Application> getAllApplications() {
        return new ArrayList<>(applications);
    }
}

