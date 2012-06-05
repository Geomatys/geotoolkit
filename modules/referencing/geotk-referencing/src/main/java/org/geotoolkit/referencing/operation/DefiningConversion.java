/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.operation;

import java.util.Map;
import java.util.Collections;
import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationMethod;

import org.geotoolkit.io.wkt.Formatter;
import org.geotoolkit.referencing.IdentifiedObjects;

import static org.geotoolkit.util.ArgumentChecks.ensureNonNull;


/**
 * A conversion used for the definition of a {@linkplain org.opengis.referencing.crs.GeneralDerivedCRS
 * derived CRS} (including projections). This conversion has no source and target CRS, and no math
 * transform. Those elements are created by the derived CRS itself.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Matthias Basler
 * @version 3.20
 *
 * @see org.opengis.referencing.operation.CoordinateOperationFactory#createDefiningConversion
 *
 * @since 2.1
 * @module
 */
@Immutable
public class DefiningConversion extends DefaultConversion {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 7399026512478064721L;

    /**
     * Convenience constructor for creating a defining conversion with a default operation method.
     * The operation method is assumed two-dimensional.
     *
     * @param name       The conversion name.
     * @param parameters The parameter values.
     *
     * @since 2.2
     */
    public DefiningConversion(final String name, final ParameterValueGroup parameters) {
        this(Collections.singletonMap(NAME_KEY, name), getOperationMethod(parameters), parameters);
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static OperationMethod getOperationMethod(final ParameterValueGroup parameters) {
        ensureNonNull("parameters", parameters);
        final ParameterDescriptorGroup descriptor = parameters.getDescriptor();
        return new DefaultOperationMethod(IdentifiedObjects.getProperties(descriptor, null), 2, 2, descriptor);
    }

    /**
     * Constructs a conversion from a set of parameters.
     * The properties given in argument follow the same rules than for the
     * {@linkplain AbstractCoordinateOperation#AbstractCoordinateOperation(Map,
     * CoordinateReferenceSystem, CoordinateReferenceSystem, MathTransform)
     * base-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param method     The operation method.
     * @param parameters The parameter values.
     */
    public DefiningConversion(final Map<String,?>       properties,
                              final OperationMethod     method,
                              final ParameterValueGroup parameters)
    {
        super(properties, null, null, null, method);
        ensureNonNull("parameters", parameters);
        this.parameters = parameters.clone();
    }

    /**
     * Constructs a conversion from a math transform.
     * The properties given in argument follow the same rules than for the
     * {@linkplain AbstractCoordinateOperation#AbstractCoordinateOperation(Map,
     * CoordinateReferenceSystem, CoordinateReferenceSystem, MathTransform)
     * base-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param method     The operation method.
     * @param transform  Transform from positions in the {@linkplain #getSourceCRS source CRS}
     *                   to positions in the {@linkplain #getTargetCRS target CRS}.
     *
     * @since 2.5
     */
    public DefiningConversion(final Map<String,?>   properties,
                              final OperationMethod method,
                              final MathTransform   transform)
    {
        super(properties, null, null, transform, method);
    }

    /**
     * Invoked by the super-class constructor for checking argument validity. This special
     * kind of conversion accepts non-null {@code transform} even if {@code sourceCRS} and
     * {@code targetCRS} are non-null.
     */
    @Override
    void validate() throws IllegalArgumentException {
        if (transform == null) {
            super.validate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatWKT(final Formatter formatter) {
        final String name = super.formatWKT(formatter);
        formatter.append(parameters);
        return name;
    }
}
