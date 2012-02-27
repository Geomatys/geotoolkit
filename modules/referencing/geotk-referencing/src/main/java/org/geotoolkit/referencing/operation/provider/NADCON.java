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
 */
package org.geotoolkit.referencing.operation.provider;

import java.io.IOException;
import net.jcip.annotations.Immutable;

import org.opengis.util.FactoryException;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Transformation;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.internal.referencing.Identifiers;
import org.geotoolkit.internal.io.Installation;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.operation.MathTransformProvider;
import org.geotoolkit.referencing.operation.transform.NadconTransform;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.util.logging.Logging;


/**
 * The provider for "<cite>North American Datum Conversion</cite>" (EPSG:9613). The math
 * transform implementations instantiated by this provider may be any of the following classes:
 * <p>
 * <ul>
 *   <li>{@link org.geotoolkit.referencing.operation.transform.NadconTransform}</li>
 * </ul>
 *
 * {@section Grid data}
 *
 * This transform requires data that are not bundled by default with Geotk. Run the
 * <a href="http://www.geotoolkit.org/modules/utility/geotk-setup">geotk-setup</a> module
 * for downloading and installing the grid data.
 *
 * @author Rueben Schulz (UBC)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
@Immutable
public class NADCON extends MathTransformProvider {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -4707304160205218546L;

    /**
     * The operation parameter descriptor for the <cite>Latitude difference file</cite>
     * parameter value. The default value is {@code "conus.las"}.
     */
    public static final ParameterDescriptor<String> LAT_DIFF_FILE = new DefaultParameterDescriptor<String>(
            "Latitude difference file", String.class, null, "conus.las");

    /**
     * The operation parameter descriptor for the <cite>Longitude difference file</cite>
     * parameter value. The default value is {@code "conus.los"}.
     */
    public static final ParameterDescriptor<String> LONG_DIFF_FILE = new DefaultParameterDescriptor<String>(
            "Longitude difference file", String.class, null, "conus.los");

    /**
     * The parameters group.
     */
    public static final ParameterDescriptorGroup PARAMETERS = Identifiers.createDescriptorGroup(
        new ReferenceIdentifier[] {
            new NamedIdentifier(Citations.EPSG, "NADCON"),
            new IdentifierCode (Citations.EPSG,  9613),
            new NamedIdentifier(Citations.GEOTOOLKIT, Vocabulary.formatInternational(
                                Vocabulary.Keys.NADCON_TRANSFORM))
        }, new ParameterDescriptor<?>[] {
            LAT_DIFF_FILE,
            LONG_DIFF_FILE
        });

    /**
     * Constructs a provider.
     */
    public NADCON() {
        super(2, 2, PARAMETERS);
    }

    /**
     * Returns {@code true} if the NADCON data seem to be present. This method checks for the existence
     * of {@code "conus.las"} and {@code "conus.los"} (continental United States) files, using the same
     * search criterion than the one applied by the {@linkplain NadconTransform#NadconTransform(String,
     * String) transform constructor}.
     * <p>
     * Some optional data can be automatically downloaded and installed by running the
     * <a href="http://www.geotoolkit.org/modules/utility/geotk-setup">geotk-setup</a> module.
     *
     * @return {@code true} if NADCON data seem to be present.
     */
    public static boolean isAvailable() {
        try {
            return Installation.NADCON.exists(NadconTransform.class, "conus.las") &&
                   Installation.NADCON.exists(NadconTransform.class, "conus.los");
        } catch (IOException e) {
            Logging.recoverableException(NADCON.class, "isAvailable", e);
            return false;
        }
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
        final String latitudeGridFile  = Parameters.stringValue(LAT_DIFF_FILE,  values);
        final String longitudeGridFile = Parameters.stringValue(LONG_DIFF_FILE, values);
        return new NadconTransform(longitudeGridFile, latitudeGridFile);
    }
}
