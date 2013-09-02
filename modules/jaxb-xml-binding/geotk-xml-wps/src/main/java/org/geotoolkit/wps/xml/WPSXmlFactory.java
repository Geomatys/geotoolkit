/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.wps.xml;

import org.geotoolkit.ows.xml.AbstractOperationsMetadata;
import org.geotoolkit.ows.xml.AbstractServiceIdentification;
import org.geotoolkit.ows.xml.AbstractServiceProvider;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class WPSXmlFactory {

    public static WPSCapabilities buildWPSCapabilities(final String version,  final AbstractServiceIdentification si, final AbstractServiceProvider sp,
            final AbstractOperationsMetadata om, final String updateSequence, ProcessOfferings po, final Languages lg) {

         if ("1.0.0".equals(version)) {
            return new  org.geotoolkit.wps.xml.v100.WPSCapabilitiesType(
                       (org.geotoolkit.ows.xml.v110.ServiceIdentification)si,
                       (org.geotoolkit.ows.xml.v110.ServiceProvider)      sp,
                       (org.geotoolkit.ows.xml.v110.OperationsMetadata)   om,
                       version,
                       updateSequence,
                       (org.geotoolkit.wps.xml.v100.ProcessOfferings)     po,
                       (org.geotoolkit.wps.xml.v100.Languages)            lg,
                       null);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }
}
