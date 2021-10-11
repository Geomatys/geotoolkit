/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.ows.xml;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface AbstractCapabilitiesBase extends AbstractCapabilitiesCore {

    /**
     * Gets the value of the serviceIdentification property.
     *
     * @return
     */
    AbstractServiceIdentification getServiceIdentification();

    /**
     * Gets the value of the serviceProvider property.
     *
     * @return
     */
    AbstractServiceProvider getServiceProvider();

    /**
     * Gets the value of the operationsMetadata property.
     * @return
     */
    AbstractOperationsMetadata getOperationsMetadata();

    /**
     * Gets the value of the version property.
     *
     * @return
     */
    String getVersion();

    /**
     * Gets the value of the updateSequence property.
     *
     * @return
     */
    String getUpdateSequence();
}
