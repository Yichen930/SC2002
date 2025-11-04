import java.util.ArrayList;
import java.util.List;

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
        return new ArrayList<>(userStorage);
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
}

