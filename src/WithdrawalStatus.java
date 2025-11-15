/**
 * Enumeration representing the status of a post-confirmation withdrawal request.
 * 
 * <p>Post-confirmation withdrawals require Career Center Staff approval:</p>
 * <ul>
 *   <li><b>PENDING</b> - Request submitted, awaiting staff review</li>
 *   <li><b>APPROVED</b> - Approved by staff, application withdrawn and slot freed</li>
 *   <li><b>REJECTED</b> - Rejected by staff, placement maintained</li>
 * </ul>
 * 
 * <p>Note: Pre-confirmation withdrawals do not require approval and bypass this status.</p>
 * 
 * @version 1.0
 */
public enum WithdrawalStatus {
    /** Request submitted, awaiting staff review */
    PENDING,
    /** Approved by staff, slot freed */
    APPROVED,
    /** Rejected by staff */
    REJECTED
}

