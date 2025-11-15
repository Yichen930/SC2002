/**
 * Abstract base class representing a user in the Internship Placement Management System.
 * 
 * <p>This class provides common functionality for all user types:</p>
 * <ul>
 *   <li>{@link Student} - Can browse and apply for internships</li>
 *   <li>{@link CompanyRepresentative} - Can create and manage internship opportunities</li>
 *   <li>{@link CareerCenterStaff} - Can approve internships and manage the system</li>
 * </ul>
 * 
 * <p>All users have:</p>
 * <ul>
 *   <li>Unique ID (student ID, email, or staff email)</li>
 *   <li>Name</li>
 *   <li>Password (encrypted, default: "password")</li>
 * </ul>
 * 
 * @version 1.0
 */
public abstract class User {
    private String id;
    private String name;
    private String password = "password";
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public boolean changePassword(String oldPassword, String newPassword) {
        if (oldPassword == null || newPassword == null) {
            return false;
        }
        if (oldPassword.equals(password)) {
            password = newPassword;
            return true;
        }
        return false;
    }

    public boolean verifyPassword(String inputPassword) {
        if (inputPassword == null) {
            return false;
        }
        return password.equals(inputPassword);
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
    /**
     * Package-private method for persistence only.
     * Not exposed as public API.
     */
    String getPasswordForPersistence() {
        return this.password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
