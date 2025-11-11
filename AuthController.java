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

        for (User user : users) {
            if (user.getName() != null && user.getName().equals(username)) {
                if (user.verifyPassword(password)) {
                    return user;
                }
            }
        }

        throw new AuthenticationException("Invalid username or password");
    }

    public void logout(User user) {
        if (user == null) {
            return;
        }
    }

    public void setUsers(List<User> users) {
        if (users != null) {
            this.users = new ArrayList<>(users);
        }
    }
}

