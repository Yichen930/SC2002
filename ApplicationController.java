import java.util.ArrayList;
import java.util.List;

public class ApplicationController {
    private List<Application> applications;

    public ApplicationController() {
        this.applications = new ArrayList<>();
    }

    public boolean accept(Student student, Application app) {
        if (student == null || app == null) {
            return false;
        }
        if (!app.getStudent().equals(student)) {
            return false;
        }
        if (app.getStatus() == ApplicationStatus.PENDING) {
            app.setStatus(ApplicationStatus.ACCEPTED);
            return true;
        }
        return false;
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

