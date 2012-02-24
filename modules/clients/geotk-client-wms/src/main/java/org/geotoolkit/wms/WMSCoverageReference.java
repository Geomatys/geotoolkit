/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.wms;

import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.opengis.feature.type.Name;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSCoverageReference implements CoverageReference{

    private final WebMapServer server;
    private final Name name;
    
    public WMSCoverageReference(WebMapServer server, Name name){
        this.name = name;
        this.server = server;
    }
    
    @Override
    public GridCoverageReader createReader() {
        //TODO
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
