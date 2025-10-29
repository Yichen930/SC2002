import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Student extends User {
    private int year;
    private String major;

    private final List<Application> applications = new ArrayList<>();

    private static final int MAX_ACTIVE_APPLICATIONS = 3;

    public Student(String id, String name, String major, int year) {
        setId(id);
        setName(name);
        this.major = major;
        this.year = Math.max(1, year);
    }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = Math.max(1, year); }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public List<Application> getApplications() {
        return Collections.unmodifiableList(applications);
    }

    public void addApplication(Application app) {
        if (app == null) return;

        if (getApplicationByOpportunity(app.getOpportunity()) != null) return;

        if (hasAcceptedOffer()) return;
        if (!canApplyMore()) return;

        applications.add(app);
    }

    public boolean canApplyForLevel(InternshipLevel level) {
        if (level == null) return false;
        if (year <= 2) return level == InternshipLevel.BASIC;
        return true;
    }

    public boolean canApplyForInternship(InternshipOpportunity opp) {
        if (opp == null) return false;
        if (!canApplyForLevel(opp.getLevel())) return false;
        if (!canApplyMore()) return false;
        return true;
    }

    public int getAppliedCount() { return applications.size(); }

    public List<InternshipOpportunity> getAppliedInternships() {
        List<InternshipOpportunity> out = new ArrayList<>();
        for (Application app : applications) {
            if (app != null && app.getOpportunity() != null) {
                out.add(app.getOpportunity());
            }
        }
        return out;
    }

    public Application getApplicationByOpportunity(InternshipOpportunity opp) {
        if (opp == null) return null;
        for (Application app : applications) {
            if (app == null || app.getOpportunity() == null) continue;
            if (app.getOpportunity() == opp) return app;
        }
        return null;
    }

    public boolean canViewInternship(InternshipOpportunity opp) {
        if (opp == null) return false;
        if (getApplicationByOpportunity(opp) != null) return true;
        return opp.isVisible();
    }

    public List<Application> getSuccessfulApplications() {
        List<Application> out = new ArrayList<>();
        for (Application app : applications) {
            if (app != null && app.isSuccessful()) out.add(app);
        }
        return out;
    }

    public boolean hasAcceptedOffer() {
        for (Application app : applications) {
            if (app != null && (app.isSuccessful() || app.isConfirmed())) return true;
        }
        return false;
    }

    public Application getConfirmedApplication() {
        for (Application app : applications) {
            if (app != null && (app.isSuccessful() || app.isConfirmed())) return app;
        }
        return null;
    }

    public boolean hasWithdrawalRequests() {
        for (Application app : applications) {
            if (app != null && app.getWithdrawal() != null) return true;
        }
        return false;
    }

    public boolean canApplyMore() {
        if (hasAcceptedOffer()) return false;
        int active = 0;
        for (Application app : applications) {
            if (app == null) continue;
            if (app.isPending()) {
                active++;
                if (active >= MAX_ACTIVE_APPLICATIONS) return false;
            }
        }
        return true;
    }
}
