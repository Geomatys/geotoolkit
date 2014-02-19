/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;


/**
 * Default interface for requests on a server.
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public interface Request {

    /**
     * Returns a map that represents the request header parameters.
     */
    Map<String,String> getHeaderMap();

    /**
     * Returns the URL of a request on a web service, in REST mode.
     *
     * @throws MalformedURLException if the request does not comply with the {@link URL}
     *                               specification.
     */
    URL getURL() throws MalformedURLException;

    /**
     * Returns the stream of the SOAP response.
     *
     * @throws IOException if an exception occurs while getting the output stream.
     */
    InputStream getResponseStream() throws IOException;
}
