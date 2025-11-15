import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of registration services.
 * 
 * <p>This class implements the RegistrationServiceInterface, providing
 * user registration and approval logic with loose coupling.</p>
 * 
 * @version 1.0
 */
public class RegistrationController implements RegistrationServiceInterface {
    private List<CompanyRepresentative> representatives;
    private List<User> users;

    public RegistrationController() {
        this.representatives = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    public boolean registerCompanyRepresentative(String name, String email, String companyName, String department, String position, String password) {
        if (name == null || email == null || companyName == null || password == null) {
            return false;
        }

        if (name.trim().isEmpty() || email.trim().isEmpty() || companyName.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }

        // Check if email already exists
        for (User user : users) {
            if (user != null && user.getId() != null && user.getId().equals(email)) {
                return false;
            }
        }

        // Use email as User ID (per assignment requirements)
        CompanyRepresentative newRep = new CompanyRepresentative(email, name, companyName);
        newRep.setDepartment(department != null && !department.trim().isEmpty() ? department : "N/A");
        newRep.setPosition(position != null && !position.trim().isEmpty() ? position : "N/A");
        newRep.setPassword(password);

        representatives.add(newRep);
        users.add(newRep);

        // Persist users to file
        try {
            writeUsersToFile("data/users.txt");
        } catch (Exception e) {
            // If persistence fails, remove newly added user to avoid inconsistent runtime state
            representatives.remove(newRep);
            users.remove(newRep);
            return false;
        }

        return true;
    }

    public boolean approveRepresentative(CareerCenterStaff staff, CompanyRepresentative rep) {
        if (staff == null || rep == null) {
            return false;
        }
        rep.setApproved(true);
        
        // Persist changes to file
        try {
            writeUsersToFile("data/users.txt");
        } catch (Exception e) {
            // If persistence fails, revert the change
            rep.setApproved(false);
            return false;
        }
        
        return true;
    }

    public boolean rejectRepresentative(CareerCenterStaff staff, CompanyRepresentative rep) {
        if (staff == null || rep == null) {
            return false;
        }
        rep.setApproved(false);
        
        // Persist changes to file
        try {
            writeUsersToFile("data/users.txt");
        } catch (Exception e) {
            // If persistence fails, revert the change
            rep.setApproved(true);
            return false;
        }
        
        return true;
    }

    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        if (userId == null || oldPassword == null || newPassword == null) {
            return false;
        }

        // Lookup by user ID (email for company reps, student/staff IDs)
        for (User user : users) {
            if (user.getId() != null && user.getId().equals(userId)) {
                boolean changed = user.changePassword(oldPassword, newPassword);
                if (changed) {
                    try {
                        writeUsersToFile("data/users.txt");
                    } catch (Exception e) {
                        // rollback password change on failure to persist
                        user.changePassword(newPassword, oldPassword);
                        return false;
                    }
                }
                return changed;
            }
        }

        return false;
    }

    public void setUsers(List<User> users) {
        if (users != null) {
            this.users = new ArrayList<>(users);
            for (User user : users) {
                if (user instanceof CompanyRepresentative) {
                    CompanyRepresentative rep = (CompanyRepresentative) user;
                    if (!representatives.contains(rep)) {
                        representatives.add(rep);
                    }
                }
            }
        }
    }

    public List<CompanyRepresentative> getRepresentatives() {
        return new ArrayList<>(representatives);
    }

        private void writeUsersToFile(String filepath) throws java.io.IOException {
        if (filepath == null) throw new java.io.IOException("Invalid filepath");

        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(filepath))) {
            // Write header/comments similar to sample file
            pw.println("# User Data File");
            pw.println("# Format: TYPE|ID|NAME|PASSWORD|ROLE_SPECIFIC_FIELDS");
            pw.println("# STUDENT: TYPE|ID|NAME|PASSWORD|MAJOR|YEAR");
            pw.println("# COMPANY_REP: TYPE|ID|NAME|PASSWORD|COMPANY_NAME|DEPARTMENT|POSITION|IS_APPROVED");
            pw.println("# STAFF: TYPE|ID|NAME|PASSWORD|DEPARTMENT");
            pw.println();

            for (User user : users) {
                if (user instanceof Student) {
                    Student s = (Student) user;
                    pw.printf("STUDENT|%s|%s|%s|%s|%d\n", s.getId(), s.getName(), s.getPasswordForPersistence(), s.getMajor(), s.getYear());
                } else if (user instanceof CompanyRepresentative) {
                    CompanyRepresentative r = (CompanyRepresentative) user;
                    pw.printf("COMPANY_REP|%s|%s|%s|%s|%s|%s|%b\n", r.getId(), r.getName(), r.getPasswordForPersistence(), r.getCompanyName(), r.getDepartment(), r.getPosition(), r.getIsApproved());
                } else if (user instanceof CareerCenterStaff) {
                    CareerCenterStaff st = (CareerCenterStaff) user;
                    pw.printf("STAFF|%s|%s|%s|%s\n", st.getId(), st.getName(), st.getPasswordForPersistence(), st.getDepartment());
                }
            }
        }
    }
}

