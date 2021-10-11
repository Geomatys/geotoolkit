/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.data.wfs.v200;

import org.geotoolkit.data.wfs.AbstractGetFeature;
import org.geotoolkit.ogc.xml.FilterVersion;
import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wfs.xml.WFSVersion;

/**
 * Get feature request for WFS 2.0.0
 *
 * @author Samuel Andrés (Geomatys)
 */
public class GetFeature200 extends AbstractGetFeature {

    public GetFeature200(final String serverURL, final ClientSecurity security) {
        super(serverURL, WFSVersion.v200, security);
    }

    @Override
    public FilterVersion getFilterVersion() {
        return FilterVersion.V200;
    }

    @Override
    public String getTypeNameParameterKey() {
        return "TYPENAMES";
    }
}
