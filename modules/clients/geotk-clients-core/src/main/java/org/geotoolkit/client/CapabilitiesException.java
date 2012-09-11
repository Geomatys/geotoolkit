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
package org.geotoolkit.client;

/**
 * Exception throwed when the getCapabilities of the server is null or could not
 * be parsed.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CapabilitiesException extends Exception{
    
    public CapabilitiesException(String message) {
        super(message);
    }
    
    public CapabilitiesException(String message, Throwable t) {
        super(message,t);
    }
    
}
