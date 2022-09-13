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
package org.geotoolkit.observation.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.gml.GMLUtilities;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.AbstractRing;
import org.geotoolkit.gml.xml.Envelope;
import org.geotoolkit.gml.xml.LineString;
import org.geotoolkit.gml.xml.Point;
import org.geotoolkit.gml.xml.Polygon;
import org.geotoolkit.gml.xml.v321.AbstractGeometryType;
import org.geotoolkit.sampling.xml.SamplingFeature;
import org.geotoolkit.sos.xml.SOSXmlFactory;
import org.opengis.geometry.Geometry;
import org.opengis.observation.AnyFeature;
import org.opengis.temporal.Instant;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.temporal.TemporalObject;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class GeoSpatialBound {

    public Date dateStart;
    public Date dateEnd;

    public Double minx;
    public Double maxx;

    public Double miny;
    public Double maxy;

    private final List<AbstractGeometry> geometries = new ArrayList<>();
    private final Map<Date, AbstractGeometry> historicalLocation = new HashMap<>();

    public void appendLocation(final TemporalObject time, final AnyFeature feature) {
        Date d = addTime(time);
        AbstractGeometry ageom = null;
        if (feature instanceof SamplingFeature sf) {
            final Geometry geom = sf.getGeometry();
            if (geom instanceof AbstractGeometry) {
                ageom = (AbstractGeometry)geom;
            } else if (geom != null) {
                ageom = GMLUtilities.getGMLFromISO(geom);
            }
            addGeometry(ageom);
            extractBoundary(ageom);
        }
        if (d != null && ageom != null) {
            historicalLocation.put(d, ageom);
        }
    }

    public void addLocation(final long millis, AbstractGeometry geometry) {
        final Date d = new Date(millis);
        addLocation(d, geometry);
    }

    public void addLocation(final Date d, AbstractGeometry geometry) {
        addDate(d);
        addGeometry(geometry);
        historicalLocation.put(d, geometry);
    }

    /**
     * Add one or two dates (Instant or Period), extracted from the temporal object.
     * Return then the first date.
     *
     * @param time a temporal object
     * @return
     */
    public Date addTime(final TemporalObject time) {
        if (time instanceof Instant i) {
            if (i.getDate() != null) {
                return addDate(i.getDate());
            }
        } else if (time instanceof Period p) {
            addTime(p.getEnding());
            return addTime(p.getBeginning());
        }
        return null;
    }

    public Date addDate(final long millis) {
        final Date d = new Date(millis);
        return addDate(d);
    }

    public Date addDate(final Date date) {
        if (date == null) return null;
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
        return date;
    }

    public void addXYCoordinate(final Double x, final Double y) {
        addXCoordinate(x);
        addYCoordinate(y);
    }

    private void addXCoordinate(final Double x) {
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

    private void addYCoordinate(final Double y) {
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

    private void extractBoundary(final AbstractGeometry geom) {
        if (geom instanceof Point p) {
            if (p.getPos() != null) {
                addXCoordinate(p.getPos().getOrdinate(0));
                addYCoordinate(p.getPos().getOrdinate(1));
            }
        } else if (geom instanceof LineString ls) {
            final Envelope env = ls.getBounds();
            if (env != null) {
                addXCoordinate(env.getMinimum(0));
                addXCoordinate(env.getMaximum(0));
                addYCoordinate(env.getMinimum(1));
                addYCoordinate(env.getMaximum(1));
            }
        } else if (geom instanceof Polygon p) {
            AbstractRing ext = p.getExterior().getAbstractRing();
            // TODO
        }
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
        this.historicalLocation.putAll(other.historicalLocation);
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
            return new org.geotoolkit.gml.xml.v311.EnvelopeType(lower, upper, null);
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

    public AbstractGeometry getLastGeometry(final String version) {
        if (historicalLocation.isEmpty()) {
            return null;
        } else {
            List<Date> keys = new ArrayList<>(historicalLocation.keySet());
            Collections.sort(keys);
            return historicalLocation.get(keys.get(keys.size() - 1));
        }
    }

    public AbstractGeometry getFullGeometry(final String version) {
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

    public Map<Date, AbstractGeometry> getHistoricalLocations() {
        return historicalLocation;
    }
}
