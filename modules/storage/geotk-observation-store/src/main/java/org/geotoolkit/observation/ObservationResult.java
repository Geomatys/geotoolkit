/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.observation;

import java.sql.Timestamp;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ObservationResult {

    public String resultID;
    public Timestamp beginTime;
    public Timestamp endTime;

    public ObservationResult(final String resultID, final Timestamp beginTime, final Timestamp endTime) {
        this.beginTime = beginTime;
        this.endTime   = endTime;
        this.resultID  = resultID;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[ObservationResult]\n");
        if (resultID != null) {
            sb.append("resultID:").append(resultID).append("\n");
        }
        if (beginTime != null) {
            sb.append("beginTime:").append(beginTime).append("\n");
        }
        if (endTime != null) {
            sb.append("endTime:").append(endTime).append("\n");
        }
        return sb.toString();
    }
}
