/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2005 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2010, Geomatys
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
package org.geotoolkit.display.exception;

/**
 * Exception that may be thrown by a portraying operation.
 * 
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public final class PortrayalException extends Exception{

    private static final String ERROR = "Portrayal exception : ";
    private static final long serialVersionUID = 3200411272785006830L;
    
    public PortrayalException(final String message){
        super(ERROR+ message);
        if(message == null || message.isEmpty()){
            throw new IllegalArgumentException("Portrayal exception message is null or empty.");
        }
    }

    public PortrayalException(final Throwable throwable){
        super(ERROR + ((throwable.getMessage()==null)? "No message" : throwable.getMessage()), throwable);
    }
    
    public PortrayalException(final String message, final Throwable throwable){
        super(ERROR + ((message != null) ? message : "no message"), throwable);
        if(message == null || message.isEmpty()){
            throw new IllegalArgumentException("Portrayal exception message is null or empty.");
        }
    }
}
