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

package org.geotoolkit.wmts.xml;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.ows.xml.AbstractOperationsMetadata;
import org.geotoolkit.ows.xml.AbstractServiceIdentification;
import org.geotoolkit.ows.xml.AbstractServiceProvider;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class WMTSXmlFactory {

    public static WMTSResponse buildCapabilities(final String version,  final AbstractServiceIdentification si, final AbstractServiceProvider sp,
            final AbstractOperationsMetadata om, final String updateSequence, final Contents cont, final List<Themes> themes) {

        if ("1.0.0".equals(version)) {
            final List<org.geotoolkit.wmts.xml.v100.Themes> them100 = new ArrayList<>();
            if (themes != null) {
                for (Themes th : themes) {
                    if (th instanceof org.geotoolkit.wmts.xml.v100.Themes) {
                        them100.add((org.geotoolkit.wmts.xml.v100.Themes)th);
                    } else {
                        throw new IllegalArgumentException("bad version of object themes");
                    }
                }
            }
            return new  org.geotoolkit.wmts.xml.v100.Capabilities(
                       (org.geotoolkit.ows.xml.v110.ServiceIdentification)si,
                       (org.geotoolkit.ows.xml.v110.ServiceProvider)      sp,
                       (org.geotoolkit.ows.xml.v110.OperationsMetadata)   om,
                       version,updateSequence,
                       (org.geotoolkit.wmts.xml.v100.ContentsType) cont,
                      them100);
        } else {
            throw new IllegalArgumentException("unexpected version number:" + version);
        }
    }
}
