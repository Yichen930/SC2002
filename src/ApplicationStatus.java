/**
 * Enumeration representing the possible states of a student application.
 * 
 * <p>Application Lifecycle:</p>
 * <ul>
 *   <li><b>PENDING</b> - Application submitted, awaiting company review</li>
 *   <li><b>ACCEPTED</b> - Approved by company, awaiting student confirmation</li>
 *   <li><b>REJECTED</b> - Rejected by company representative</li>
 *   <li><b>WITHDRAWN</b> - Withdrawn by student (pre or post confirmation)</li>
 * </ul>
 * 
 * @version 1.0
 */
public enum ApplicationStatus {
    /** Application submitted, awaiting company review */
    PENDING,
    /** Approved by company, awaiting student confirmation */
    ACCEPTED,
    /** Rejected by company representative */
    REJECTED,
    /** Withdrawn by student */
    WITHDRAWN
}
