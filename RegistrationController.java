import java.util.ArrayList;
import java.util.List;

public class RegistrationController {
    private List<CompanyRepresentative> representatives;
    private List<User> users;

    public RegistrationController() {
        this.representatives = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    public boolean registerCompanyRepresentative(String companyName, String email, String password) {
        if (companyName == null || email == null || password == null) {
            return false;
        }

        if (companyName.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }

        for (CompanyRepresentative rep : representatives) {
            if (rep.getCompanyName().equals(companyName)) {
                return false;
            }
        }

        String id = "REP-" + System.currentTimeMillis();
        CompanyRepresentative newRep = new CompanyRepresentative(id, email, companyName);
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

