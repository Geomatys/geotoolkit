/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2011, Geomatys
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
package org.geotoolkit.process;

import java.util.List;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.identification.Identification;
import org.opengis.util.NoSuchIdentifierException;


/**
 * A collection of {@link ProcessDescriptor}s identified by name.
 *
 * @author Johann Sorel (Geomatys)
 * @version 3.19
 *
 * @since 3.19
 * @module
 */
public interface ProcessingRegistry {
    /**
     * General information about this registry. For every descriptors managed by this registry,
     * the {@linkplain Identifier#getAuthority() identifier authority} shall be equals to this
     * {@linkplain Identification#getCitation() identification citation}.
     *
     * @return The identification of this registry.
     */
    Identification getIdentification();

    /**
     * Return all process descriptors available in this registry.
     *
     * @return All process descriptors
     */
    List<ProcessDescriptor> getDescriptors();

    /**
     * Convenience method returning a view over the names of all process descriptors.
     * The returned names are valid only within this factory.
     *
     * @return The names of all process descriptors.
     */
    List<String> getNames();

    /**
     * Return the process descriptor for this given name.
     *
     * @param  name The name of the desired process descriptor.
     * @return The process descriptor (never null).
     * @throws NoSuchIdentifierException if no descriptor exist for the given name.
     */
    ProcessDescriptor getDescriptor(String name) throws NoSuchIdentifierException;
}
