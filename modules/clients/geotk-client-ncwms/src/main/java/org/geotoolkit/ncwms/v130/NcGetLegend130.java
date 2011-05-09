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
package org.geotoolkit.ncwms.v130;

import org.geotoolkit.ncwms.AbstractNcGetLegend;


/**
 * Implementation for the GetLegendGraphic request version 1.3.0.
 *
 * @author Olivier Terral (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class NcGetLegend130 extends AbstractNcGetLegend {
    /**
     * Defines the server url and its version.
     *
     * @param serverURL The url of the webservice.
     */
    public NcGetLegend130(final String serverURL){
        super(serverURL,"1.3.0");
    }


}
