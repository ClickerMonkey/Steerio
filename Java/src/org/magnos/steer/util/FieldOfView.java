
package org.magnos.steer.util;

/**
 * A flag used to determine whether an object is in the field of view of a subject. A flag is necessary when the object is overlapping with the field
 * of view's outer (left & right) boundaries.
 * 
 * @author pdiffenderfer
 *
 */
public enum FieldOfView
{
    /**
     * The object is in the field of view of the subject even if it's circle is partially inside the field of view.
     */
    PARTIAL,
    /**
     * The object is in the field of view of the subject if at least half of it's circle is inside the field of view.
     */
    HALF,
    /**
     * The object is in the field of view of the subject if all of it's circle is inside the field of view.
     */
    FULL,
    /**
     * The object is always in the field of view of the subject.
     */
    IGNORE
}
