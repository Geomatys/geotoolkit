/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.wcs.xml;

import org.geotoolkit.ows.xml.AbstractCapabilitiesCore;
import org.geotoolkit.ows.xml.AbstractOperationsMetadata;
import org.geotoolkit.ows.xml.AbstractServiceIdentification;
import org.geotoolkit.ows.xml.AbstractServiceProvider;

/**
 *
 * @author Cédric Briançon
 * @module
 */
public interface GetCapabilitiesResponse extends AbstractCapabilitiesCore {

    AbstractOperationsMetadata getOperationsMetadata();

    AbstractServiceIdentification getServiceIdentification();

    AbstractServiceProvider getServiceProvider();

    Content getContents();

    String getUpdateSequence();

    String getVersion();

    void setUpdateSequence(final String updateSequence);

}
