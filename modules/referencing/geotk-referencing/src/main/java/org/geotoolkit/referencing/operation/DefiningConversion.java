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

import java.util.Collections;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.OperationMethod;
import org.apache.sis.referencing.operation.DefaultConversion;
import org.apache.sis.referencing.operation.DefaultOperationMethod;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * A conversion used for the definition of a {@linkplain org.opengis.referencing.crs.GeneralDerivedCRS
 * derived CRS} (including projections). This conversion has no source and target CRS, and no math
 * transform. Those elements are created by the derived CRS itself.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @author Matthias Basler
 * @since 2.1
 * @module
 *
 * @deprecated DefaultConversion moved to Apache SIS. DefiningConversion is no longer needed.
 */
@Deprecated
public class DefiningConversion extends DefaultConversion {
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
        super(Collections.singletonMap(NAME_KEY, name), getOperationMethod(parameters), null, parameters);
    }

    /**
     * Work around for RFE #4093999 in Sun's bug database
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static OperationMethod getOperationMethod(final ParameterValueGroup parameters) {
        ensureNonNull("parameters", parameters);
        final ParameterDescriptorGroup descriptor = parameters.getDescriptor();
        return new DefaultOperationMethod(
                org.geotoolkit.referencing.IdentifiedObjects.getProperties(descriptor, null), 2, 2, descriptor);
    }
}
