import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;

public class DataManager {
    private List<User> userStorage;
    private List<Object> dataStorage;

    public DataManager() {
        this.userStorage = new ArrayList<>();
        this.dataStorage = new ArrayList<>();
    }

    public List<User> loadUsers(String filepath) throws DataAccessException {
        if (filepath == null) {
            throw new DataAccessException("Filepath cannot be null");
        }
        
        List<User> users = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                
                String[] parts = line.split("\\|");
                if (parts.length < 4) {
                    continue; // Skip invalid lines
                }
                
                String type = parts[0].trim();
                String id = parts[1].trim();
                String name = parts[2].trim();
                String password = parts[3].trim();
                
                User user = null;
                
                if (type.equals("STUDENT") && parts.length >= 6) {
                    String major = parts[4].trim();
                    int year = Integer.parseInt(parts[5].trim());
                    user = new Student(id, name, major, year);
                } else if (type.equals("COMPANY_REP") && parts.length >= 8) {
                    String companyName = parts[4].trim();
                    String department = parts[5].trim();
                    String position = parts[6].trim();
                    boolean isApproved = Boolean.parseBoolean(parts[7].trim());
                    CompanyRepresentative rep = new CompanyRepresentative(id, name, companyName);
                    rep.setDepartment(department);
                    rep.setPosition(position);
                    rep.setApproved(isApproved);
                    user = rep;
                } else if (type.equals("STAFF") && parts.length >= 5) {
                    String department = parts[4].trim();
                    user = new CareerCenterStaff(id, name, department);
                }
                
                if (user != null) {
                    user.setPassword(password);
                    users.add(user);
                }
            }
        } catch (IOException e) {
            throw new DataAccessException("Error reading file: " + filepath + " - " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new DataAccessException("Error parsing numeric data: " + e.getMessage());
        }
        
        this.userStorage = new ArrayList<>(users);
        return users;
    }

    public void saveUsers(String filepath, List<User> users) throws DataAccessException {
        if (filepath == null) {
            throw new DataAccessException("Filepath cannot be null");
        }
        if (users == null) {
            throw new DataAccessException("Users list cannot be null");
        }
        this.userStorage = new ArrayList<>(users);
    }

    public List<Object> loadData(String filepath) throws DataAccessException {
        if (filepath == null) {
            throw new DataAccessException("Filepath cannot be null");
        }
        return new ArrayList<>(dataStorage);
    }

    public void saveData(String filepath, List<Object> data) throws DataAccessException {
        if (filepath == null) {
            throw new DataAccessException("Filepath cannot be null");
        }
        if (data == null) {
            throw new DataAccessException("Data list cannot be null");
        }
        this.dataStorage = new ArrayList<>(data);
    }

    public void addUser(User user) {
        if (user != null && !userStorage.contains(user)) {
            userStorage.add(user);
        }
    }

    public void addData(Object data) {
        if (data != null && !dataStorage.contains(data)) {
            dataStorage.add(data);
        }
    }

    public void clearUsers() {
        userStorage.clear();
    }

    public void clearData() {
        dataStorage.clear();
    }
    
    public List<InternshipOpportunity> loadInternships(String filepath, List<User> users) throws DataAccessException {
        if (filepath == null) {
            throw new DataAccessException("Filepath cannot be null");
        }
        
        List<InternshipOpportunity> internships = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split("\\|");
                if (parts.length < 13) {
                    continue;
                }
                
                // String id = parts[0].trim(); // Not used currently
                String title = parts[1].trim();
                String companyName = parts[2].trim();
                String repId = parts[3].trim();
                String description = parts[4].trim();
                String levelStr = parts[5].trim();
                String preferredMajor = parts[6].trim();
                int totalSlots = Integer.parseInt(parts[7].trim());
                int filledSlots = Integer.parseInt(parts[8].trim());
                String statusStr = parts[9].trim();
                boolean visible = Boolean.parseBoolean(parts[10].trim());
                String openDateStr = parts[11].trim();
                String closeDateStr = parts[12].trim();
                
                // Find the rep
                CompanyRepresentative rep = null;
                for (User user : users) {
                    if (user instanceof CompanyRepresentative && user.getId().equals(repId)) {
                        rep = (CompanyRepresentative) user;
                        break;
                    }
                }
                
                if (rep == null) {
                    continue; // Skip if rep not found
                }
                
                InternshipOpportunity opp = new InternshipOpportunity(title, companyName, rep);
                opp.setDescription(description);
                opp.setLevel(InternshipLevel.valueOf(levelStr));
                
                // Set preferred major (can be comma-separated for multiple majors)
                if (preferredMajor != null && !preferredMajor.isEmpty() && !preferredMajor.equalsIgnoreCase("N/A")) {
                    List<String> majors = new ArrayList<>();
                    for (String major : preferredMajor.split(",")) {
                        majors.add(major.trim());
                    }
                    opp.setPreferredMajor(majors);
                }
                
                opp.setTotalSlots(totalSlots);
                opp.setFilledSlots(filledSlots);
                opp.setStatus(InternshipStatus.valueOf(statusStr));
                opp.setVisible(visible);
                opp.setOpenDate(LocalDate.parse(openDateStr));
                opp.setCloseDate(LocalDate.parse(closeDateStr));
                
                internships.add(opp);
                rep.createInternship(opp);
            }
        } catch (IOException e) {
            throw new DataAccessException("Error reading file: " + filepath + " - " + e.getMessage());
        } catch (Exception e) {
            throw new DataAccessException("Error parsing internship data: " + e.getMessage());
        }
        
        return internships;
    }
    
    public List<Application> loadApplications(String filepath, List<User> users, List<InternshipOpportunity> internships) 
            throws DataAccessException {
        if (filepath == null) {
            throw new DataAccessException("Filepath cannot be null");
        }
        
        List<Application> applications = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split("\\|");
                if (parts.length < 5) {
                    continue;
                }
                
                String studentId = parts[0].trim();
                String internshipTitle = parts[1].trim();
                String statusStr = parts[2].trim();
                // String createdDateStr = parts[3].trim(); // Not used currently
                // String updatedDateStr = parts[4].trim(); // Not used currently
                
                // Find student
                Student student = null;
                for (User user : users) {
                    if (user instanceof Student && user.getId().equals(studentId)) {
                        student = (Student) user;
                        break;
                    }
                }
                
                if (student == null) {
                    continue;
                }
                
                // Find internship
                InternshipOpportunity internship = null;
                for (InternshipOpportunity opp : internships) {
                    if (opp.getTitle().equals(internshipTitle)) {
                        internship = opp;
                        break;
                    }
                }
                
                if (internship == null) {
                    continue;
                }
                
                Application app = new Application(student, internship);
                app.setStatus(ApplicationStatus.valueOf(statusStr));
                
                applications.add(app);
                try {
                    student.addApplication(app);
                } catch (ApplicationException e) {
                    // Ignore validation errors when loading
                }
            }
        } catch (IOException e) {
            throw new DataAccessException("Error reading file: " + filepath + " - " + e.getMessage());
        } catch (Exception e) {
            throw new DataAccessException("Error parsing application data: " + e.getMessage());
        }
        
        return applications;
    }
}

