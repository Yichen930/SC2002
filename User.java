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
     * Returns the current password for persistence.
     * Note: This is provided for assignment persistence only.
     */
    public String getPassword() {
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
