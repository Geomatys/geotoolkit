/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.cs;

import java.util.List;
import java.util.Arrays;
import java.util.Collections;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;

import org.geotoolkit.util.ComparisonMode;

import static org.geotoolkit.util.Utilities.deepEquals;
import static org.geotoolkit.util.ArgumentChecks.ensureNonNull;


/**
 * A coordinate system made of two or more independent coordinate systems.
 *
 * <TABLE CELLPADDING='6' BORDER='1'>
 * <TR BGCOLOR="#EEEEFF"><TH NOWRAP>Used with CRS type(s)</TH></TR>
 * <TR><TD>
 *   {@link org.geotoolkit.referencing.crs.DefaultCompoundCRS Compound}
 * </TD></TR></TABLE>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @since 2.0
 * @module
 */
@Immutable
public class DefaultCompoundCS extends AbstractCS {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -5726410275278843372L;

    /**
     * The coordinate systems.
     */
    private final CoordinateSystem[] cs;

    /**
     * An immutable view of {@link #cs} as a list. Will be created only when first needed.
     */
    private transient List<CoordinateSystem> asList;

    /**
     * Constructs a compound coordinate system. A name for this CS will
     * be automatically constructed from the name of all specified CS.
     *
     * @param cs The set of coordinate syztem.
     */
    public DefaultCompoundCS(CoordinateSystem... cs) {
        super(getName(cs=clone(cs)), getAxis(cs));
        this.cs = cs;
    }

    /**
     * Returns a clone of the specified array. This method would be bundle right
     * into the constructor if RFE #4093999 was fixed.
     */
    private static CoordinateSystem[] clone(CoordinateSystem[] cs) {
        ensureNonNull("cs", cs);
        cs = cs.clone();
        for (int i=0; i<cs.length; i++) {
            ensureNonNull("cs", i, cs);
        }
        return cs;
    }

    /**
     * Returns the axis of all coordinate systems.
     */
    private static CoordinateSystemAxis[] getAxis(final CoordinateSystem[] cs) {
        int count = 0;
        for (int i=0; i<cs.length; i++) {
            count += cs[i].getDimension();
        }
        final CoordinateSystemAxis[] axis = new CoordinateSystemAxis[count];
        count = 0;
        for (int i=0; i<cs.length; i++) {
            final CoordinateSystem c = cs[i];
            final int dim = c.getDimension();
            for (int j=0; j<dim; j++) {
                axis[count++] = c.getAxis(j);
            }
        }
        assert count == axis.length;
        return axis;
    }

    /**
     * Constructs a name from a merge of the name of all coordinate systems.
     *
     * @param cs The coordinate systems.
     * @param locale The locale for the name.
     */
    private static String getName(final CoordinateSystem[] cs) {
        final StringBuilder buffer = new StringBuilder();
        for (int i=0; i<cs.length; i++) {
            if (buffer.length() != 0) {
                buffer.append(" / ");
            }
            buffer.append(cs[i].getName().getCode());
        }
        return buffer.toString();
    }

    /**
     * Returns all coordinate systems in this compound CS.
     *
     * @return All coordinate systems in this compound CS.
     */
    public synchronized List<CoordinateSystem> getCoordinateSystems() {
        if (asList == null) {
            asList = Collections.unmodifiableList(Arrays.asList(cs));
        }
        return asList;
    }

    /**
     * Compares this coordinate system with the specified object for equality.
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode {@link ComparisonMode#STRICT STRICT} for performing a strict comparison, or
     *         {@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA} for comparing only properties
     *         relevant to transformations.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true; // Slight optimization.
        }
        if (object instanceof DefaultCompoundCS && super.equals(object, mode)) {
            final DefaultCompoundCS that = (DefaultCompoundCS) object;
            return deepEquals(this.cs, that.cs, mode);
        }
        return false;
    }
}
