/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.wcs.v100;

import org.geotoolkit.security.ClientSecurity;
import org.geotoolkit.wcs.AbstractDescribeCoverage;
import org.geotoolkit.wcs.WebCoverageClient;


/**
 * Implementation for the DescribeCoverage request version 1.0.0.
 *
 * @author Cédric Briançon (Geomatys)
 * @module
 */
public class DescribeCoverage100 extends AbstractDescribeCoverage {
    /**
     * Defines the server url and its version.
     *
     * @param serverURL The url of the webservice.
     */
    public DescribeCoverage100(final String serverURL, final ClientSecurity security){
        super(serverURL, "1.0.0", security);
    }

}
