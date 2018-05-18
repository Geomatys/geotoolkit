/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.wps.xml;

/**
 *
 * @author guilhem
 */
public interface StatusInfo extends WPSResponse {
    
    /** The job has finished with no errors. */
    public static final String STATUS_SUCCEEDED = "Succeeded";
    /** The job has finished with errors. */
    public static final String STATUS_FAILED = "Failed";
    /** The job is queued for execution. */
    public static final String STATUS_ACCEPTED = "Accepted";
    /** The job is running. */
    public static final String STATUS_RUNNING = "Running";
    /** The job has been dismissed. */
    public static final String STATUS_DISSMISED = "Dismissed";
    /** The job has been paused. */
    public static final String STATUS_PAUSED = "Paused";

    Integer getPercentCompleted();
    
    String getMessage();
    
    String getStatus();
}
