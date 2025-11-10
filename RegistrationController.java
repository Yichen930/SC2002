import java.util.ArrayList;
import java.util.List;

public class RegistrationController {
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
        return true;
    }

    public boolean approveRepresentative(CareerCenterStaff staff, CompanyRepresentative rep) {
        if (staff == null || rep == null) {
            return false;
        }
        rep.setApproved(true);
        return true;
    }

    public boolean rejectRepresentative(CareerCenterStaff staff, CompanyRepresentative rep) {
        if (staff == null || rep == null) {
            return false;
        }
        rep.setApproved(false);
        return true;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        if (username == null || oldPassword == null || newPassword == null) {
            return false;
        }

        for (User user : users) {
            if (user.getName() != null && user.getName().equals(username)) {
                return user.changePassword(oldPassword, newPassword);
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
}

