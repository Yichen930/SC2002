/**
 * Enumeration representing the difficulty level of an internship opportunity.
 * 
 * <p>Level Restrictions:</p>
 * <ul>
 *   <li><b>BASIC</b> - Suitable for all students (Year 1-4)</li>
 *   <li><b>INTERMEDIATE</b> - Requires Year 3+ students</li>
 *   <li><b>ADVANCED</b> - Requires Year 3+ students with relevant experience</li>
 * </ul>
 * 
 * <p>Note: Year 1-2 students can only apply to BASIC level internships.</p>
 * 
 * @version 1.0
 */
public enum InternshipLevel{
  /** Suitable for all students */
  BASIC,
  /** Requires Year 3+ */
  INTERMEDIATE,
  /** Requires Year 3+ with experience */
  ADVANCED
}
