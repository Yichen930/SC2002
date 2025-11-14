import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

/**
 * Implementation of internship management services.
 * 
 * <p>This class implements the InternshipServiceInterface, providing
 * internship opportunity management with loose coupling.</p>
 * 
 * @version 1.0
 */
public class InternshipController implements InternshipServiceInterface {
    private List<InternshipOpportunity> opportunities;

    public InternshipController() {
        this.opportunities = new ArrayList<>();
    }

    public boolean create(CareerCenterStaff staff, InternshipOpportunity opp) {
        if (staff == null || opp == null) {
            return false;
        }

        if (opp.getTitle() == null || opp.getCompanyName() == null) {
            return false;
        }

        if (!opportunities.contains(opp)) {
            opportunities.add(opp);
            return true;
        }

        return false;
    }

    public void approve(CareerCenterStaff staff, InternshipOpportunity opp) {
        if (staff == null || opp == null) {
            return;
        }

        if (opp.getStatus() == InternshipStatus.PENDING) {
            opp.setStatus(InternshipStatus.APPROVED);
        }
    }

    public void reject(CareerCenterStaff staff, InternshipOpportunity opp) {
        if (staff == null || opp == null) {
            return;
        }

        if (opp.getStatus() == InternshipStatus.PENDING) {
            opp.setStatus(InternshipStatus.REJECTED);
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
        if (opp == null) return;

        // Basic validation: title and company required
        if (opp.getTitle() == null || opp.getTitle().trim().isEmpty() || opp.getCompanyName() == null || opp.getCompanyName().trim().isEmpty()) {
            return;
        }

        // Slots validation
        if (opp.getTotalSlots() <= 0 || opp.getTotalSlots() > 10) {
            return;
        }

        // Rep approval and per-rep active count enforcement
        CompanyRepresentative rep = opp.getRepInCharge();
        if (rep == null) return;
        if (!rep.getIsApproved()) return; // only approved reps may add opportunities in backend
        if (rep.countActiveInternships() >= 5) return; // enforce 5 active internships limit

        if (!opportunities.contains(opp)) {
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

    public List<InternshipOpportunity> getFilteredOpportunities(Student student) {
        if (student == null) {
            return new ArrayList<>();
        }

        List<InternshipOpportunity> filtered = new ArrayList<>();
        LocalDate today = LocalDate.now();
        
        for (InternshipOpportunity opp : opportunities) {
            // Skip null entries
            if (opp == null) continue;
            
            // Check if visible
            if (!opp.isVisible()) continue;
            
            // Status must be approved
            if (opp.getStatus() != InternshipStatus.APPROVED) continue;
            
            // Date must be within range
            if (opp.getOpenDate() == null || opp.getCloseDate() == null) continue;
            if (today.isBefore(opp.getOpenDate()) || today.isAfter(opp.getCloseDate())) continue;
            
            // Must not be filled
            if (opp.isFilled()) continue;
            
            // Level eligibility (Year 1-2 can only see BASIC)
            if (!student.canApplyForLevel(opp.getLevel())) continue;
            
            // Preferred major filter (if set, only students with matching major can see it)
            List<String> preferred = opp.getPreferredMajor();
            if (preferred != null && !preferred.isEmpty() && !preferred.contains(student.getMajor())) {
                continue;
            }

            filtered.add(opp);
        }
        
        return filtered;
    }
    
    public List<InternshipOpportunity> getOpportunities() {
        return new ArrayList<>(opportunities);
    }
}

