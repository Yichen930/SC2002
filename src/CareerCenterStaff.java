import java.util.Objects;

/**
 * Represents a Career Center Staff member in the Internship Placement Management System.
 * 
 * <p>Staff members have administrative privileges to:</p>
 * <ul>
 *   <li>Approve or reject internship opportunities</li>
 *   <li>Approve or reject company representative registrations</li>
 *   <li>Review and approve post-confirmation withdrawal requests</li>
 *   <li>Generate system-wide reports and statistics</li>
 *   <li>Search and view all internships across companies</li>
 * </ul>
 * 
 * <p>Staff are identified by their email (e.g., admin@ntu.edu.sg) and belong to a department
 * (typically "Career Services").</p>
 * 
 * @version 1.0
 */
public class CareerCenterStaff extends User {
    private String staffDepartment;

    /**
     * Constructs a new CareerCenterStaff with the specified details.
     * 
     * @param id the staff email (e.g., admin@ntu.edu.sg)
     * @param name the full name of the staff member
     * @param staffDepartment the department the staff belongs to
     */
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
