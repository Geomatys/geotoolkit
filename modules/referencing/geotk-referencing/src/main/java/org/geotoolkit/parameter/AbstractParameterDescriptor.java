/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.parameter;

import java.util.Map;
import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.GeneralParameterDescriptor;

import org.geotoolkit.resources.Errors;
import org.apache.sis.io.wkt.Formatter;
import org.apache.sis.util.ComparisonMode;
import org.apache.sis.referencing.AbstractIdentifiedObject;


/**
 * The root class of {@link org.opengis.parameter.ParameterDescriptor} and
 * {@link org.opengis.parameter.ParameterDescriptorGroup} implementations.
 * Subclasses provide abstract definition of a parameter or group of parameters
 * used by an operation method.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.18
 *
 * @see AbstractParameter
 *
 * @since 2.0
 * @module
 */
@Immutable
public abstract class AbstractParameterDescriptor extends AbstractIdentifiedObject
           implements GeneralParameterDescriptor
{
    /**
     * Serial number for inter-operability with different versions.
     */
//  private static final long serialVersionUID = -2630644278783845276L;

    /**
     * The minimum number of times that values for this parameter group or
     * parameter are required.
     */
    private final int minimumOccurs;

    /**
     * Constructs a descriptor with the same values than the specified one. This copy constructor
     * may be used in order to wraps an arbitrary implementation into a Geotk one.
     *
     * @param descriptor The descriptor to copy.
     *
     * @since 2.2
     */
    protected AbstractParameterDescriptor(final GeneralParameterDescriptor descriptor) {
        super(descriptor);
        minimumOccurs = descriptor.getMinimumOccurs();
    }

    /**
     * Constructs a parameter from a set of properties. The properties map is given unchanged to the
     * {@linkplain AbstractIdentifiedObject#AbstractIdentifiedObject(Map) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param minimumOccurs The {@linkplain #getMinimumOccurs minimum number of times}
     *        that values for this parameter group or parameter are required.
     * @param maximumOccurs The {@linkplain #getMaximumOccurs maximum number of times} that values
     *        for this parameter group or parameter are required. This value is used in order to
     *        check the range. For {@link ParameterValue}, it should always be 1.
     */
    protected AbstractParameterDescriptor(final Map<String,?> properties,
                                          final int minimumOccurs,
                                          final int maximumOccurs)
    {
        super(properties);
        this.minimumOccurs = minimumOccurs;
        if (minimumOccurs < 0  ||  maximumOccurs < minimumOccurs) {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.ILLEGAL_RANGE_2,
                        minimumOccurs, maximumOccurs));
        }
    }

    /**
     * Creates a new instance of {@linkplain AbstractParameter parameter value or group} initialized
     * with the {@linkplain DefaultParameterDescriptor#getDefaultValue default value(s)}.
     * The {@linkplain AbstractParameter#getDescriptor parameter value descriptor} for the
     * created parameter value(s) will be {@code this} object.
     * <p>
     * Implementation example:
     *
     * {@preformat java
     *     return new Parameter(this);
     * }
     */
    @Override
    public abstract GeneralParameterValue createValue();

    /**
     * The minimum number of times that values for this parameter group or
     * parameter are required. The default value is one. A value of 0 means
     * an optional parameter.
     *
     * @see #getMaximumOccurs
     */
    @Override
    public int getMinimumOccurs() {
        return minimumOccurs;
    }

    /**
     * The maximum number of times that values for this parameter group or parameter
     * can be included. For a {@linkplain DefaultParameterDescriptor single parameter},
     * the value is always 1. For a {@linkplain DefaultParameterDescriptorGroup parameter group},
     * it may vary.
     *
     * @see #getMinimumOccurs
     */
    @Override
    public abstract int getMaximumOccurs();

    /**
     * Compares the specified object with this parameter for equality.
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode {@link ComparisonMode#STRICT STRICT} for performing a strict comparison, or
     *         {@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA} for comparing only properties
     *         relevant to transformations.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (super.equals(object, mode)) {
            switch (mode) {
                case STRICT: {
                    final AbstractParameterDescriptor that = (AbstractParameterDescriptor) object;
                    return this.minimumOccurs == that.minimumOccurs;
                }
                default: {
                    final GeneralParameterDescriptor that = (GeneralParameterDescriptor) object;
                    return getMinimumOccurs() == that.getMinimumOccurs() &&
                           getMaximumOccurs() == that.getMaximumOccurs();
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected long computeHashCode() {
        return super.computeHashCode() + minimumOccurs;
    }

    /**
     * Formats the inner part of a
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
     * Known Text</cite> (WKT)</A> element. Note that WKT is not yet defined for parameter descriptor.
     * Current implementation print only the name.
     *
     * @param  formatter The formatter to use.
     * @return The WKT element name, which is "PARAMETER"
     */
    @Override
    protected String formatTo(final Formatter formatter) {
        formatter.setInvalidWKT(this);
        return "PARAMETER";
    }
}
