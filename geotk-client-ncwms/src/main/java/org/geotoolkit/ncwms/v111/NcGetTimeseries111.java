/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.ncwms.v111;


import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.ncwms.AbstractNcGetTimeseries;

/**
 * Implementation for the GetTimeseries request version 1.1.1.
 *
 * @author Fabien BERNARD (Geomatys)
 * @module
 */
public class NcGetTimeseries111 extends AbstractNcGetTimeseries {

    /**
     * Defines the server url and its version.
     *
     * @param serverURL The url of the webservice.
     */
    public NcGetTimeseries111(final String serverURL, final ClientSecurity security) {
        super(serverURL, "1.1.1", security);
    }

    @Override
    protected String getCRSParameterName() {
        return "SRS";
    }

    @Override
    protected void prepareParameters() {
        super.prepareParameters();
        if (columnIndex == null) {
            throw new IllegalArgumentException("X is not defined");
        }
        if (rawIndex == null) {
            throw new IllegalArgumentException("Y is not defined");
        }
        requestParameters.put("X", String.valueOf(columnIndex));
        requestParameters.put("Y", String.valueOf(rawIndex));
    }
}
