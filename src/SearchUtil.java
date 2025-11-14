import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for searching internships and applications.
 * 
 * <p>Provides search functionality to help users find specific
 * internships or applications based on various criteria.</p>
 * 
 * @version 1.0
 */
public class SearchUtil {
    
    /**
     * Searches internships by company name (case-insensitive partial match).
     * 
     * @param internships list of all internships
     * @param companyName the company name to search for
     * @return list of matching internships
     */
    public static List<InternshipOpportunity> searchByCompany(
            List<InternshipOpportunity> internships, String companyName) {
        List<InternshipOpportunity> results = new ArrayList<>();
        String searchTerm = companyName.toLowerCase();
        
        for (InternshipOpportunity opp : internships) {
            if (opp.getCompanyName().toLowerCase().contains(searchTerm)) {
                results.add(opp);
            }
        }
        
        return results;
    }
    
    /**
     * Searches internships by title (case-insensitive partial match).
     * 
     * @param internships list of all internships
     * @param title the title to search for
     * @return list of matching internships
     */
    public static List<InternshipOpportunity> searchByTitle(
            List<InternshipOpportunity> internships, String title) {
        List<InternshipOpportunity> results = new ArrayList<>();
        String searchTerm = title.toLowerCase();
        
        for (InternshipOpportunity opp : internships) {
            if (opp.getTitle().toLowerCase().contains(searchTerm)) {
                results.add(opp);
            }
        }
        
        return results;
    }
    
    /**
     * Searches internships by level.
     * 
     * @param internships list of all internships
     * @param level the internship level
     * @return list of matching internships
     */
    public static List<InternshipOpportunity> searchByLevel(
            List<InternshipOpportunity> internships, InternshipLevel level) {
        List<InternshipOpportunity> results = new ArrayList<>();
        
        for (InternshipOpportunity opp : internships) {
            if (opp.getLevel() == level) {
                results.add(opp);
            }
        }
        
        return results;
    }
    
    /**
     * Displays search results in a formatted list.
     * 
     * @param results list of internship opportunities
     * @param searchType description of search type (e.g., "company 'Google'")
     */
    public static void displaySearchResults(List<InternshipOpportunity> results, String searchType) {
        if (results.isEmpty()) {
            ColorUtil.printWarning("No internships found matching " + searchType);
            return;
        }
        
        ColorUtil.printSuccess("\nFound " + results.size() + " internship(s) matching " + searchType + ":");
        System.out.println("----------------------------------------");
        
        for (int i = 0; i < results.size(); i++) {
            InternshipOpportunity opp = results.get(i);
            System.out.printf("%d. %s - %s\n", i + 1, opp.getTitle(), opp.getCompanyName());
            System.out.printf("   Level: %s | Slots: %d/%d | Status: %s\n",
                opp.getLevel(),
                opp.getFilledSlots(),
                opp.getTotalSlots(),
                opp.getStatus());
            
            if (opp.getCloseDate() != null) {
                System.out.printf("   Closes: %s\n", opp.getCloseDate());
            }
            System.out.println();
        }
    }
}
