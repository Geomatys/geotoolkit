/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.internal.referencing;

import org.opengis.referencing.cs.AxisDirection;
import org.opengis.referencing.cs.CoordinateSystem;

import org.geotoolkit.lang.Static;


/**
 * Utilities methods related to {@link AxisDirection}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.13
 * @module
 *
 * @deprecated Moved to {@link org.apache.sis.internal.referencing.AxisDirections}.
 */
@Deprecated
public final class AxisDirections extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private AxisDirections() {
    }

    /**
     * Returns the "absolute" direction of the given direction.
     * This "absolute" operation is similar to the {@code Math.abs(int)}
     * method in that "negative" directions like ({@link #SOUTH}, {@link #WEST},
     * {@link #DOWN}, {@link #PAST}) are changed for their "positive" counterparts
     * ({@link #NORTH}, {@link #EAST}, {@link #UP}, {@link #FUTURE}).
     * More specifically, the following conversion table is applied:
     * <br>&nbsp;
     * <table cellpadding="9"><tr>
     * <td width='50%'><table border="1" bgcolor="F4F8FF">
     *   <tr bgcolor="#B9DCFF">
     *     <th nowrap width='50%'>&nbsp;&nbsp;Direction&nbsp;&nbsp;</th>
     *     <th nowrap width='50%'>&nbsp;&nbsp;Absolute value&nbsp;&nbsp;</th>
     *   </tr>
     *   <tr><td width='50%'>&nbsp;{@link #NORTH}</td> <td width='50%'>&nbsp;{@link #NORTH}</td> </tr>
     *   <tr><td width='50%'>&nbsp;{@link #SOUTH}</td> <td width='50%'>&nbsp;{@link #NORTH}</td> </tr>
     *   <tr><td width='50%'>&nbsp;{@link #EAST}</td>  <td width='50%'>&nbsp;{@link #EAST}</td>  </tr>
     *   <tr><td width='50%'>&nbsp;{@link #WEST}</td>  <td width='50%'>&nbsp;{@link #EAST}</td>  </tr>
     *   <tr><td width='50%'>&nbsp;{@link #UP}</td>    <td width='50%'>&nbsp;{@link #UP}</td>    </tr>
     *   <tr><td width='50%'>&nbsp;{@link #DOWN}</td>  <td width='50%'>&nbsp;{@link #UP}</td>    </tr>
     * </table></td>
     * <td width='50%'><table border="1" bgcolor="F4F8FF">
     *   <tr bgcolor="#B9DCFF">
     *     <th nowrap width='50%'>&nbsp;&nbsp;Direction&nbsp;&nbsp;</th>
     *     <th nowrap width='50%'>&nbsp;&nbsp;Absolute value&nbsp;&nbsp;</th>
     *   </tr>
     *   <tr><td width='50%'>&nbsp;{@link #DISPLAY_RIGHT}</td> <td width='50%'>&nbsp;{@link #DISPLAY_RIGHT}</td> </tr>
     *   <tr><td width='50%'>&nbsp;{@link #DISPLAY_LEFT}</td>  <td width='50%'>&nbsp;{@link #DISPLAY_RIGHT}</td> </tr>
     *   <tr><td width='50%'>&nbsp;{@link #DISPLAY_UP}</td>    <td width='50%'>&nbsp;{@link #DISPLAY_UP}</td>    </tr>
     *   <tr><td width='50%'>&nbsp;{@link #DISPLAY_DOWN}</td>  <td width='50%'>&nbsp;{@link #DISPLAY_UP}</td>    </tr>
     *   <tr><td width='50%'>&nbsp;{@link #FUTURE}</td>        <td width='50%'>&nbsp;{@link #FUTURE}</td>        </tr>
     *   <tr><td width='50%'>&nbsp;{@link #PAST}</td>          <td width='50%'>&nbsp;{@link #FUTURE}</td>        </tr>
     * </table></td></tr>
     *   <tr align="center"><td width='50%'>{@link #OTHER}</td><td width='50%'>{@link #OTHER}</td></tr>
     * </table>
     *
     * @param  dir The direction for which to return the absolute direction.
     * @return The direction from the above table.
     */
    public static AxisDirection absolute(final AxisDirection dir) {
        return org.apache.sis.internal.referencing.AxisDirections.absolute(dir);
    }

    /**
     * Returns the opposite direction of the given direction. The opposite direction of
     * {@linkplain #NORTH North} is {@linkplain #SOUTH South}, and the opposite direction
     * of {@linkplain #SOUTH South} is {@linkplain #NORTH North}. The same applies to
     * {@linkplain #EAST East}-{@linkplain #WEST West}, {@linkplain #UP Up}-{@linkplain #DOWN Down}
     * and {@linkplain #FUTURE Future}-{@linkplain #PAST Past}, <i>etc.</i> If the given axis
     * direction has no opposite, then this method returns {@code null}.
     *
     * @param  dir The direction for which to return the opposite direction.
     * @return The opposite direction, or {@code null} if none or unknown.
     */
    public static AxisDirection opposite(final AxisDirection dir) {
        return org.apache.sis.internal.referencing.AxisDirections.opposite(dir);
    }

    /**
     * Returns {@code true} if the given direction is an "opposite" direction.
     * If this method can not determine if the given direction is an "opposite"
     * one, then it conservatively returns {@code true}.
     *
     * @param  dir The direction to test, or {@code null}.
     * @return {@code true} if the given direction is an "opposite".
     *
     * @since 3.14
     */
    public static boolean isOpposite(final AxisDirection dir) {
        return org.apache.sis.internal.referencing.AxisDirections.isOpposite(dir);
    }

    /**
     * Finds the dimension of an axis having the given direction or its opposite.
     * If more than one axis has the given direction, only the first occurrence is returned.
     * If both the given direction and its opposite exist, then the dimension for the given
     * direction has precedence over the opposite direction.
     *
     * @param  cs The coordinate system to inspect, or {@code null}.
     * @param  direction The direction of the axis to search.
     * @return The dimension of the axis using the given direction or its opposite, or -1 if none.
     *
     * @since 3.20
     */
    public static int indexOf(final CoordinateSystem cs, final AxisDirection direction) {
        return org.apache.sis.internal.referencing.AxisDirections.indexOf(cs, direction);
    }
}
