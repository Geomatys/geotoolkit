/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.xal.model;

/**
 * <p>A spécific exception class for xAL parsing errors.</p>
 *
 * @author Samuel Andrés
 */
public class XalException extends Exception{

    private final String message;

    /**
     *
     * @param message
     */
    public XalException(String message){
        this.message = message;
    }

    /**
     *
     * @return
     */
    @Override
    public String getMessage(){
        return this.message;
    }



}
