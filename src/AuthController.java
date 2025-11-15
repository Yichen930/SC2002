import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of authentication services.
 * 
 * <p>This class implements the AuthServiceInterface, providing
 * concrete authentication logic while allowing for loose coupling
 * with client code.</p>
 * 
 * @version 1.0
 */
public class AuthController implements AuthServiceInterface {
    private List<User> users;

    public AuthController() {
        this.users = new ArrayList<>();
    }

    public User authenticate(String username, String password) throws AuthenticationException {
        if (username == null || password == null) {
            throw new AuthenticationException("Username and password cannot be null");
        }

        // First try to find user by ID
        User found = null;
        for (User user : users) {
            if (user != null && user.getId() != null && user.getId().equals(username)) {
                found = user;
                break;
            }
        }

        if (found == null) {
            throw new AuthenticationException("Invalid ID");
        }

        // If the account is a company representative, ensure it's approved before allowing login
        if (found instanceof CompanyRepresentative) {
            CompanyRepresentative rep = (CompanyRepresentative) found;
            if (!rep.getIsApproved()) {
                throw new AuthenticationException("Account not approved");
            }
        }

        // ID found and account authorized (if applicable), now verify password
        if (!found.verifyPassword(password)) {
            throw new AuthenticationException("Incorrect password");
        }

        return found;
    }

    public void logout(User user) {
        if (user == null) {
            return;
        }
    }

    public void setUsers(List<User> users) {
        if (users != null) {
            this.users = users; // Share the same list reference
        }
    }
    
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }
}

