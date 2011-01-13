/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.referencing.operation.transform;

import javax.measure.unit.SI;
import javax.measure.unit.NonSI;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.operation.SingleOperation;


/**
 * An object (usually a {@link MathTransform}) which can supply its parameters in a
 * {@link ParameterValueGroup}. Every Geotk implementations of {@code MathTransform}
 * implement this interface, including {@link AffineTransform2D} even if it does not
 * extend {@link AbstractMathTransform}.
 *
 * {@note The name of this interface is not <code>ParameterizedTransform</code> because in
 *        some few cases, it may be implemented by objects that are not math transform. For
 *        example it may be implemented by proxies during WKT formatting.}
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.00
 * @module
 */
public interface Parameterized {
    /**
     * Returns the parameter descriptors for this math transform, or {@code null} if unknown.
     * This method is similar to {@link OperationMethod#getParameters}, except that typical
     * {@link MathTransform} implementations return parameters in standard units (usually
     * {@linkplain SI#METRE metres} or {@linkplain NonSI#DEGREE_ANGLE decimal degrees}).
     *
     * @return The parameter descriptors for this math transform, or {@code null}.
     *
     * @see OperationMethod#getParameters
     */
    ParameterDescriptorGroup getParameterDescriptors();

    /**
     * Returns a copy of the parameter values for this math transform, or {@code null} if unknown.
     * This method is similar to {@link SingleOperation#getParameterValues}, except that typical
     * {@link MathTransform} implementations return parameters in standard units (usually
     * {@linkplain SI#METRE metres} or {@linkplain NonSI#DEGREE_ANGLE decimal degrees}).
     *
     * @return A copy of the parameter values for this math transform, or {@code null}.
     *         Since this method returns a copy of the parameter values, any change to
     *         a value will have no effect on this math transform.
     *
     * @see SingleOperation#getParameterValues
     */
    ParameterValueGroup getParameterValues();
}
