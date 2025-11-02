import java.util.ArrayList;
import java.util.List;

public class AuthController {
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

