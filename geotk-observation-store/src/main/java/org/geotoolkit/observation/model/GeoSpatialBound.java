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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.temporal.object.DefaultInstant;
import org.geotoolkit.temporal.object.DefaultPeriod;
import org.geotoolkit.temporal.object.InstantWrapper;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import static org.opengis.referencing.IdentifiedObject.NAME_KEY;
import org.opengis.temporal.TemporalGeometricPrimitive;
import org.opengis.temporal.TemporalObject;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@JsonAutoDetect(
    fieldVisibility = Visibility.DEFAULT,
    setterVisibility = Visibility.NONE,
    getterVisibility = Visibility.NONE,
    isGetterVisibility = Visibility.NONE,
    creatorVisibility = Visibility.NONE
)
public class GeoSpatialBound {

    public Date dateStart;
    public Date dateEnd;

    public Double minx;
    public Double maxx;

    public Double miny;
    public Double maxy;

    private final List<Geometry> geometries = new ArrayList<>();
    private final Map<Date, Geometry> historicalLocation = new HashMap<>();

    private static final GeometryFactory GF = new GeometryFactory();

    public void appendLocation(final TemporalObject time, final SamplingFeature feature) {
        Date d = addTime(time);
        Geometry geom = null;
        if (feature != null) {
            geom = feature.getGeometry();
            addGeometry(geom);
            extractBoundary(geom);
        }
        if (d != null && geom != null) {
            historicalLocation.put(d, geom);
        }
    }

    public void addLocation(final long millis, Geometry geometry) {
        final Date d = new Date(millis);
        addLocation(d, geometry);
    }

    public void addLocation(final Date d, Geometry geometry) {
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
        if (time instanceof InstantWrapper i) {
            if (i.getDate() != null) {
                return addDate(i.getDate());
            }
        } else if (time instanceof DefaultPeriod p) {
            addTime(p.ending);
            return addTime(p.beginning);
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

    private void extractBoundary(final Geometry geom) {
        if (geom instanceof Point p) {
            if (p.getCoordinate()!= null) {
                addXCoordinate(p.getCoordinate().getOrdinate(0));
                addYCoordinate(p.getCoordinate().getOrdinate(1));
            }
        } else if (geom != null){
            final Envelope env = geom.getEnvelopeInternal();
            if (env != null) {
                addXCoordinate(env.getMinX());
                addXCoordinate(env.getMaxX());
                addYCoordinate(env.getMinY());
                addYCoordinate(env.getMaxY());
            }
        }
    }

    public void addGeometry(final Geometry geometry) {
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

        for (Geometry geom : other.geometries) {
            if (!this.geometries.contains(geom)) {
                this.geometries.add(geom);
            }
        }
        this.historicalLocation.putAll(other.historicalLocation);
    }

    public TemporalGeometricPrimitive getTimeObject() {
        if (dateStart != null && dateEnd != null) {
            String id = UUID.randomUUID().toString();
            if (dateStart.getTime() == dateEnd.getTime()) {
                return new DefaultInstant(Collections.singletonMap(NAME_KEY, id + "time"), dateStart.toInstant());
            } else {
                return new DefaultPeriod(Collections.singletonMap(NAME_KEY, id + "-time"),
                                         new DefaultInstant(Collections.singletonMap(NAME_KEY, id + "-st-time"), dateStart.toInstant()),
                                         new DefaultInstant(Collections.singletonMap(NAME_KEY, id + "-en-time"), dateEnd.toInstant()));
            }
        }
        return null;
    }

    public Optional<Envelope> getSpatialBounds() {
        if (!hasFullSpatialCoordinates()) {
            return Optional.empty();
        }
        return Optional.of(new Envelope(minx, maxx, miny, maxy));
    }

    public Optional<org.opengis.geometry.Envelope> getEnvelope() {
        if (!hasFullSpatialCoordinates()) {
            return Optional.empty();
        }
        GeneralEnvelope env = new GeneralEnvelope(CommonCRS.defaultGeographic());
        env.setRange(0, minx, maxx);
        env.setRange(1, miny, maxy);
        return Optional.of(env);
    }

    public Geometry getPolyGonBounds() {
        if (!hasFullSpatialCoordinates()) {
            return null;
        }
        final List<Coordinate> positions = new ArrayList<>();
        positions.add(new Coordinate(minx, miny));
        positions.add(new Coordinate(maxx, miny));
        positions.add(new Coordinate(maxx, maxy));
        positions.add(new Coordinate(minx, maxy));
        positions.add(new Coordinate(minx, miny));

        return GF.createPolygon(positions.toArray(Coordinate[]::new));
    }

    public Geometry getLastGeometry() {
        if (historicalLocation.isEmpty()) {
            return null;
        } else {
            List<Date> keys = new ArrayList<>(historicalLocation.keySet());
            Collections.sort(keys);
            return historicalLocation.get(keys.get(keys.size() - 1));
        }
    }

    public Geometry getFullGeometry() {
        if (geometries.isEmpty()) {
            return null;
        } else if (geometries.size() == 1) {
            return geometries.get(0);
        } else {
            return GF.createGeometryCollection(geometries.toArray(Geometry[]::new));
        }
    }

    public Map<Date, Geometry> getHistoricalLocations() {
        return historicalLocation;
    }
}
