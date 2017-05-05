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
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.Envelope;
import org.geotoolkit.gml.xml.v321.AbstractGeometryType;
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

    private final List<AbstractGeometry> geometries = new ArrayList<>();

    public void addDate(final long millis) {
        final Date d = new Date(millis);
        addDate(d);
    }

    public void addDate(final Date date) {
        if (date == null) return;
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

    public void addXYCoordinate(final Double x, final Double y) {
        addXCoordinate(x);
        addYCoordinate(y);
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

   /**
    * TODO int from a CRS
    */
    public void initBoundary() {
        minx = -180.0;
        maxx = 180.0;
        miny = -90.0;
        maxy = 90.0;
    }


    public void addGeometry(final AbstractGeometry geometry) {
        if (!geometries.contains(geometry)) {
            geometries.add(geometry);
        }
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

        for (AbstractGeometry geom : other.geometries) {
            if (!this.geometries.contains(geom)) {
                this.geometries.add(geom);
            }
        }
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

    public Envelope getSpatialBounds(final String version) {
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

    public AbstractGeometry getPolyGonBounds(final String version) {
        if (!hasFullSpatialCoordinates()) {
            return null;
        }
        final List<Double> positions = new ArrayList<>();
        positions.add(miny);
        positions.add(minx);

        positions.add(miny);
        positions.add(maxx);

        positions.add(maxy);
        positions.add(maxx);

        positions.add(maxy);
        positions.add(minx);

        positions.add(miny);
        positions.add(minx);

        if ("1.0.0".equals(version)) {
            final org.geotoolkit.gml.xml.v311.DirectPositionListType posList = new org.geotoolkit.gml.xml.v311.DirectPositionListType(positions);
            final org.geotoolkit.gml.xml.v311.AbstractRingType exterior = new org.geotoolkit.gml.xml.v311.LinearRingType("EPSG:4326", posList);
            return new org.geotoolkit.gml.xml.v311.PolygonType(exterior, null);
        } else if ("2.0.0".equals(version)) {
            final org.geotoolkit.gml.xml.v321.DirectPositionListType posList = new org.geotoolkit.gml.xml.v321.DirectPositionListType(positions);
            final org.geotoolkit.gml.xml.v321.AbstractRingType exterior = new org.geotoolkit.gml.xml.v321.LinearRingType("EPSG:4326", posList);
            return new org.geotoolkit.gml.xml.v321.PolygonType(exterior, null);
        } else {
            throw new IllegalArgumentException("unexpected version:" + version);
        }
    }

    public AbstractGeometry getGeometry(final String version) {
        if (geometries.isEmpty()) {
            return null;
        } else if (geometries.size() == 1) {
            return geometries.get(0);
        } else {
            final List<org.geotoolkit.gml.xml.v321.GeometryPropertyType> members = new ArrayList<>();
            for (AbstractGeometry geom : geometries) {
                members.add(new org.geotoolkit.gml.xml.v321.GeometryPropertyType((AbstractGeometryType) geom));
            }
            final org.geotoolkit.gml.xml.v321.MultiGeometryType geom = new org.geotoolkit.gml.xml.v321.MultiGeometryType(members);
            return geom;
        }
    }
}
