/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.openeo.process;

import org.geotoolkit.openeo.dto.process.DataTypeSchema;
import org.geotoolkit.process.ProcessDescriptor;

/**
 * Implemented by a {@link ProcessDescriptor} that wants to be exposed as an openEO process,
 * under an openEO process id that may differ from its Examind identifier.
 * <p>
 * {@link OpenEOUtils} discovers implementors generically through {@code ProcessFinder}, so
 * exposing a new Examind process through openEO only requires implementing this interface on
 * the descriptor itself, no edit to {@link OpenEOUtils} is needed.
 *
 * @author Quentin Bialota (Geomatys)
 */
public interface OpenEOExposedProcess {

    /**
     * @return the openEO process id under which this descriptor should be exposed (e.g. "load_collection").
     */
    String getOpenEOProcessId();

    /**
     * Optional openEO schema override for one of this descriptor's own parameters.
     *
     * @param descriptorName name of the parameter being described
     * @param type openEO type of the descriptor (as a string)
     * @param clazz Java class of the descriptor
     * @param isArray whether the descriptor is an array
     * @param mandatory whether the descriptor is mandatory
     * @return a custom schema for this parameter, or {@code null} to fall back to the generic schema logic
     */
    default DataTypeSchema[] getOpenEOSchemaOverride(String descriptorName, String type, Class<?> clazz,
            boolean isArray, boolean mandatory) {
        return null;
    }
}
