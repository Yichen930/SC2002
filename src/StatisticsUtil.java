import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Utility class for generating statistics and reports.
 * 
 * <p>Provides data analysis capabilities for students, company representatives,
 * and staff to view summary information about applications and internships.</p>
 * 
 * @version 1.0
 */
public class StatisticsUtil {
    
    /**
     * Generates statistics for a student's applications.
     * 
     * @param student the student
     */
    public static void printStudentStatistics(Student student) {
        List<Application> applications = student.getApplications();
        
        int total = applications.size();
        int pending = 0;
        int accepted = 0;
        int rejected = 0;
        int withdrawn = 0;
        
        for (Application app : applications) {
            switch (app.getStatus()) {
                case PENDING:
                    pending++;
                    break;
                case ACCEPTED:
                    accepted++;
                    break;
                case REJECTED:
                    rejected++;
                    break;
                case WITHDRAWN:
                    withdrawn++;
                    break;
            }
        }
        
        ColorUtil.printHeader("\n========== MY APPLICATION STATISTICS ==========");
        System.out.println("Total Applications: " + total);
        System.out.println("Pending: " + ColorUtil.colored(String.valueOf(pending), ColorUtil.YELLOW));
        System.out.println("Accepted: " + ColorUtil.colored(String.valueOf(accepted), ColorUtil.GREEN));
        System.out.println("Rejected: " + ColorUtil.colored(String.valueOf(rejected), ColorUtil.RED));
        System.out.println("Withdrawn: " + withdrawn);
        
        if (total > 0) {
            double successRate = (accepted * 100.0) / total;
            System.out.printf("Success Rate: %.1f%%\n", successRate);
        }
        ColorUtil.printHeader("==============================================\n");
    }
    
    /**
     * Generates statistics for a company representative's internships.
     * 
     * @param rep the company representative
     * @param internships list of all internships
     * @param applications list of all applications
     */
    public static void printCompanyRepStatistics(CompanyRepresentative rep, 
                                                  List<InternshipOpportunity> internships,
                                                  List<Application> applications) {
        List<InternshipOpportunity> myInternships = rep.getCreatedInternships();
        
        int total = myInternships.size();
        int approved = 0;
        int pending = 0;
        int filled = 0;
        int totalApplications = 0;
        
        for (InternshipOpportunity opp : myInternships) {
            switch (opp.getStatus()) {
                case APPROVED:
                    approved++;
                    break;
                case PENDING:
                    pending++;
                    break;
                case FILLED:
                    filled++;
                    break;
            }
            
            // Count applications for this internship
            for (Application app : applications) {
                if (app.getOpportunity().equals(opp)) {
                    totalApplications++;
                }
            }
        }
        
        ColorUtil.printHeader("\n========== MY INTERNSHIP STATISTICS ==========");
        System.out.println("Total Internships Created: " + total);
        System.out.println("Approved: " + ColorUtil.colored(String.valueOf(approved), ColorUtil.GREEN));
        System.out.println("Pending Approval: " + ColorUtil.colored(String.valueOf(pending), ColorUtil.YELLOW));
        System.out.println("Filled: " + filled);
        System.out.println("Total Applications Received: " + totalApplications);
        
        if (total > 0) {
            double avgApplications = totalApplications / (double) total;
            System.out.printf("Average Applications per Internship: %.1f\n", avgApplications);
        }
        ColorUtil.printHeader("==============================================\n");
    }
    
    /**
     * Generates system-wide statistics for staff.
     * 
     * @param users list of all users
     * @param internships list of all internships
     * @param applications list of all applications
     */
    public static void printSystemStatistics(List<User> users, 
                                             List<InternshipOpportunity> internships,
                                             List<Application> applications) {
        int students = 0;
        int reps = 0;
        int staff = 0;
        int pendingReps = 0;
        
        for (User user : users) {
            if (user instanceof Student) {
                students++;
            } else if (user instanceof CompanyRepresentative) {
                reps++;
                if (!((CompanyRepresentative) user).getIsApproved()) {
                    pendingReps++;
                }
            } else if (user instanceof CareerCenterStaff) {
                staff++;
            }
        }
        
        int activeInternships = 0;
        int pendingInternships = 0;
        int filledInternships = 0;
        
        for (InternshipOpportunity opp : internships) {
            if (opp.getStatus() == InternshipStatus.APPROVED) {
                activeInternships++;
            } else if (opp.getStatus() == InternshipStatus.PENDING) {
                pendingInternships++;
            } else if (opp.getStatus() == InternshipStatus.FILLED) {
                filledInternships++;
            }
        }
        
        int totalApplications = applications.size();
        int pendingApplications = 0;
        int acceptedApplications = 0;
        
        for (Application app : applications) {
            if (app.getStatus() == ApplicationStatus.PENDING) {
                pendingApplications++;
            } else if (app.getStatus() == ApplicationStatus.ACCEPTED) {
                acceptedApplications++;
            }
        }
        
        ColorUtil.printHeader("\n========== SYSTEM OVERVIEW ==========");
        System.out.println("USERS:");
        System.out.println("  Total Students: " + students);
        System.out.println("  Total Company Reps: " + reps);
        System.out.println("  Pending Rep Approvals: " + ColorUtil.colored(String.valueOf(pendingReps), ColorUtil.YELLOW));
        System.out.println("  Total Staff: " + staff);
        
        System.out.println("\nINTERNSHIPS:");
        System.out.println("  Active: " + ColorUtil.colored(String.valueOf(activeInternships), ColorUtil.GREEN));
        System.out.println("  Pending Approval: " + ColorUtil.colored(String.valueOf(pendingInternships), ColorUtil.YELLOW));
        System.out.println("  Filled: " + filledInternships);
        
        System.out.println("\nAPPLICATIONS:");
        System.out.println("  Total: " + totalApplications);
        System.out.println("  Pending Review: " + ColorUtil.colored(String.valueOf(pendingApplications), ColorUtil.YELLOW));
        System.out.println("  Accepted: " + ColorUtil.colored(String.valueOf(acceptedApplications), ColorUtil.GREEN));
        
        if (totalApplications > 0) {
            double acceptanceRate = (acceptedApplications * 100.0) / totalApplications;
            System.out.printf("  Overall Acceptance Rate: %.1f%%\n", acceptanceRate);
        }
        ColorUtil.printHeader("=====================================\n");
    }
}
