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
package org.geotoolkit.sos.netcdf;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.geotoolkit.gml.xml.Envelope;
import org.geotoolkit.sos.xml.SOSXmlFactory;
import org.opengis.temporal.TemporalGeometricPrimitive;

/**
 *
 * @author guilhem
 */
public class GeoSpatialBound {
    
    public Date dateStart;
    public Date dateEnd;

    public Double minx;
    public Double maxx;

    public Double miny;
    public Double maxy;

    private final List<Double> positions = new ArrayList<>();

    public void addDate(final Date date) {
        if (dateStart == null) {
            dateStart = date;
        }
        if (dateEnd == null) {
            dateEnd = date;
        }
        if (dateStart.getTime() > date.getTime()) {
            dateStart = date;
        }
        if (dateEnd.getTime() < date.getTime()) {
            dateEnd = date;
        }
    }

    public void addXCoordinate(final Double x) {
        if (x == null) return;
        if (minx == null) {
            minx = x;
        }
        if (maxx == null) {
            maxx = x;
        }
        if (minx > x) {
            minx = x;
        }
        if (maxx < x) {
            maxx = x;
        }
    }

    public void addYCoordinate(final Double y) {
        if (y == null) return;
        if (miny == null) {
            miny = y;
        }
        if (maxy == null) {
            maxy = y;
        }
        if (miny > y) {
            miny = y;
        }
        if (maxy < y) {
            maxy = y;
        }
    }

    public List<Double> getPositions() {
        return positions;
    }

    public boolean hasFullSpatialCoordinates() {
        return maxx != null && minx != null &&
               maxy != null && miny != null;
    }

    public void merge(final GeoSpatialBound other) {
        addDate(other.dateStart);
        addDate(other.dateEnd);
        addXCoordinate(other.minx);
        addXCoordinate(other.maxx);
        addYCoordinate(other.miny);
        addYCoordinate(other.maxy);
    }

    public TemporalGeometricPrimitive getTimeObject(final String version) {
        if (dateStart != null && dateEnd != null) {
            if (dateStart.getTime() == dateEnd.getTime()) {
                return SOSXmlFactory.buildTimeInstant(version, new Timestamp(dateStart.getTime()));
            } else {
                return SOSXmlFactory.buildTimePeriod(version, new Timestamp(dateStart.getTime()), new Timestamp(dateEnd.getTime()));
            }
        }
        return null;
    }

    public Envelope getSpatialObject(final String version) {
        if (!hasFullSpatialCoordinates()) {
            return null;
        }
        if ("1.0.0".equals(version)) {
            final org.geotoolkit.gml.xml.v311.DirectPositionType lower = new org.geotoolkit.gml.xml.v311.DirectPositionType(minx, miny);
            final org.geotoolkit.gml.xml.v311.DirectPositionType upper = new org.geotoolkit.gml.xml.v311.DirectPositionType(maxx, maxy);
            return new org.geotoolkit.gml.xml.v311.EnvelopeType(null, lower, upper, null);
        } else if ("2.0.0".equals(version)) {
            final org.geotoolkit.gml.xml.v321.DirectPositionType lower = new org.geotoolkit.gml.xml.v321.DirectPositionType(minx, miny);
            final org.geotoolkit.gml.xml.v321.DirectPositionType upper = new org.geotoolkit.gml.xml.v321.DirectPositionType(maxx, maxy);
            return new org.geotoolkit.gml.xml.v321.EnvelopeType(lower, upper, null);
        } else {
            throw new IllegalArgumentException("unexpected version:" + version);
        }
    }
}
