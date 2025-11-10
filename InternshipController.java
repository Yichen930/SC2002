import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

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

    public List<InternshipOpportunity> getOpenOpportunities(LocalDate today) {
        List<InternshipOpportunity> open = new ArrayList<>();
        for (InternshipOpportunity opp : opportunities) {
            if (opp != null && opp.isOpenForApplication(today)) {
                open.add(opp);
            }
        }
        return open;
    }

    public List<InternshipOpportunity> filterByLevel(List<InternshipOpportunity> opps, InternshipLevel level) {
        if (level == null) return opps;
        List<InternshipOpportunity> filtered = new ArrayList<>();
        for (InternshipOpportunity opp : opps) {
            if (opp != null && opp.getLevel() == level) {
                filtered.add(opp);
            }
        }
        return filtered;
    }

    public List<InternshipOpportunity> filterByDateRange(List<InternshipOpportunity> opps, LocalDate today) {
        if (today == null) return opps;
        List<InternshipOpportunity> filtered = new ArrayList<>();
        for (InternshipOpportunity opp : opps) {
            if (opp != null && opp.getOpenDate() != null && opp.getCloseDate() != null) {
                if (!today.isBefore(opp.getOpenDate()) && !today.isAfter(opp.getCloseDate())) {
                    filtered.add(opp);
                }
            }
        }
        return filtered;
    }

    public List<InternshipOpportunity> filterVisible(List<InternshipOpportunity> opps) {
        List<InternshipOpportunity> filtered = new ArrayList<>();
        for (InternshipOpportunity opp : opps) {
            if (opp != null && opp.isVisible() && opp.getStatus() == InternshipStatus.APPROVED) {
                filtered.add(opp);
            }
        }
        return filtered;
    }

    public boolean deleteOpportunity(CompanyRepresentative rep, InternshipOpportunity opp) {
        if (rep == null || opp == null) {
            return false;
        }
        
        // Can only delete if not yet approved
        if (opp.getStatus() != InternshipStatus.PENDING) {
            return false;
        }
        
        // Verify ownership
        if (!opp.getRepInCharge().equals(rep)) {
            return false;
        }
        
        opportunities.remove(opp);
        rep.removeInternship(opp);
        return true;
    }

    public void removeOpportunity(InternshipOpportunity opp) {
        opportunities.remove(opp);
    }
}

