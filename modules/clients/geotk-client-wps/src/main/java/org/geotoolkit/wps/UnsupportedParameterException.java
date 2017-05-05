/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.wps;

/**
 * WPS excpetion raised when a process parameter could not be mapped to a known
 * data type.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class UnsupportedParameterException extends Exception {

    public UnsupportedParameterException(String processName, String parameterName) {
        this(processName,parameterName,null,null);
    }

    public UnsupportedParameterException(String processName, String parameterName, String details) {
        this(processName,parameterName,details,null);
    }

    public UnsupportedParameterException(String processName, String parameterName, String details, Throwable cause) {
        super("Parameter "+parameterName+" not supported in process "+processName+ ((details!=null)?"\n"+details:""), cause);
    }

}
