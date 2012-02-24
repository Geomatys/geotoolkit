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
package org.geotoolkit.referencing.operation.provider;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Transformation;

import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.transform.NTv2Transform;
import org.geotoolkit.referencing.operation.MathTransformProvider;


/**
 * The provider for "<cite>National Transformation</cite>" version 2 (ESPG:9615).
 * The math transform implementations instantiated by this provider may be any of
 * the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.transform.NTv2Transform}</li>
 * </ul>
 *
 * {@section Grid data}
 *
 * This transform requires data that are not bundled by default with Geotk. Run the
 * <a href="http://www.geotoolkit.org/modules/utility/geotk-setup">geotk-setup</a> module
 * for downloading and installing the grid data.
 *
 * @author Simon Reynard (Geomatys)
 * @version 3.12
 *
 * @since 3.12
 * @module
 */
public class NTv2 extends MathTransformProvider {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -4707304160205218546L;

    /**
     * The operation parameter descriptor for the <cite>Latitude and longitude difference file</cite>
     * parameter value. The file extension is typically {@code ".gsb"}. There is no default value.
     */
    public static final ParameterDescriptor<String> DIFFERENCE_FILE = new DefaultParameterDescriptor<>(
            "Latitude and longitude difference file", String.class, null, null);

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.EPSG, "NTv2"),
            new IdentifierCode (Citations.EPSG,  9615)
        }, new ParameterDescriptor<?>[] {
            DIFFERENCE_FILE
        });

    /**
     * Constructs a provider.
     */
    public NTv2() {
        super(2, 2, PARAMETERS);
    }

    /**
     * Returns the operation type.
     */
    @Override
    public Class<Transformation> getOperationType() {
        return Transformation.class;
    }

    /**
     * Creates a math transform from the specified group of parameter values.
     *
     * @throws FactoryException If the grid files can not be loaded.
     */
    @Override
    protected MathTransform createMathTransform(final ParameterValueGroup values) throws FactoryException {
        return new NTv2Transform(Parameters.stringValue(DIFFERENCE_FILE, values));
    }
}
