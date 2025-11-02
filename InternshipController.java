import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class InternshipController {
    private List<InternshipOpportunity> opportunities;

    public InternshipController() {
        this.opportunities = new ArrayList<>();
    }

    public boolean create(CareerCenterStaff staff, InternshipOpportunity app) {
        if (staff == null || app == null) {
            return false;
        }

        if (app.getTitle() == null || app.getCompanyName() == null) {
            return false;
        }

        if (!opportunities.contains(app)) {
            opportunities.add(app);
            return true;
        }

        return false;
    }

    public void approve(CareerCenterStaff staff, InternshipOpportunity app) {
        if (staff == null || app == null) {
            return;
        }

        if (app.getStatus() == InternshipStatus.PENDING) {
            app.setStatus(InternshipStatus.APPROVED);
        }
    }

    public void reject(CareerCenterStaff staff, InternshipOpportunity app) {
        if (staff == null || app == null) {
            return;
        }

        if (app.getStatus() == InternshipStatus.PENDING) {
            app.setStatus(InternshipStatus.REJECTED);
        }
    }

    public void toggleVisibility(InternshipOpportunity opp) {
        if (opp == null) {
            return;
        }
        opp.setVisible(!opp.isVisible());
    }

    public void showInternshipOpportunity(InternshipOpportunity opp) {
        if (opp == null) {
            return;
        }
        System.out.println(opp.toString());
    }

    public Set<InternshipOpportunity> getVisibleOpportunities() {
        Set<InternshipOpportunity> visible = new HashSet<>();
        for (InternshipOpportunity opp : opportunities) {
            if (opp != null && opp.isVisible() && opp.getStatus() == InternshipStatus.APPROVED) {
                visible.add(opp);
            }
        }
        return visible;
    }

    public Set<InternshipOpportunity> showAllInternshipOpportunities() {
        Set<InternshipOpportunity> all = new HashSet<>();
        for (InternshipOpportunity opp : opportunities) {
            if (opp != null) {
                all.add(opp);
                showInternshipOpportunity(opp);
            }
        }
        return all;
    }

    public void addOpportunity(InternshipOpportunity opp) {
        if (opp != null && !opportunities.contains(opp)) {
            opportunities.add(opp);
        }
    }

    public List<InternshipOpportunity> getAllOpportunities() {
        return new ArrayList<>(opportunities);
    }
}

