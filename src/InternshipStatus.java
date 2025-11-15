/**
 * Enumeration representing the possible states of an internship opportunity.
 * 
 * <p>Internship Lifecycle:</p>
 * <ul>
 *   <li><b>PENDING</b> - Created by company rep, awaiting staff approval</li>
 *   <li><b>APPROVED</b> - Approved by staff, visible to students</li>
 *   <li><b>REJECTED</b> - Rejected by staff, not visible to students</li>
 *   <li><b>FILLED</b> - All slots taken, no longer accepting applications</li>
 * </ul>
 * 
 * @version 1.0
 */
public enum InternshipStatus{
  /** Created by company rep, awaiting staff approval */
  PENDING,
  /** Approved by staff, visible to students */
  APPROVED,
  /** Rejected by staff */
  REJECTED,
  /** All slots filled */
  FILLED
}
