import java.util.Objects;

public class CareerCenterStaff extends User {
    private String staffDepartment;

    public CareerCenterStaff(String id, String name, String staffDepartment) {
        setId(id);
        setName(name);
        this.staffDepartment = staffDepartment;
    }

    public String getDepartment() {
        return staffDepartment;
    }

    public void setDepartment(String department) {
        this.staffDepartment = department;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CareerCenterStaff that = (CareerCenterStaff) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "CareerCenterStaff{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", department='" + staffDepartment + '\'' +
                '}';
    }
}
