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
package org.geotoolkit.csw.v202;

import org.geotoolkit.csw.AbstractDescribeRecord;
import org.geotoolkit.security.ClientSecurity;


/**
 * Implementation for the DescribeRecord request version 2.0.2.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class DescribeRecord202 extends AbstractDescribeRecord {
    /**
     * Defines the server url and its version.
     *
     * @param serverURL The url of the webservice.
     */
    public DescribeRecord202(final String serverURL, final ClientSecurity security){
        super(serverURL, "2.0.2", security);
    }
}
