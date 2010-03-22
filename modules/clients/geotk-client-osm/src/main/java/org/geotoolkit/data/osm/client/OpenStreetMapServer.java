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

package org.geotoolkit.data.osm.client;

import java.net.URL;
import java.util.logging.Logger;

import org.geotoolkit.client.Server;
import org.geotoolkit.util.logging.Logging;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class OpenStreetMapServer implements Server{

    private static final Logger LOGGER = Logging.getLogger(OpenStreetMapServer.class);

    private final URL serverURL;

    public OpenStreetMapServer(URL url){
        this.serverURL = url;
    }

}
