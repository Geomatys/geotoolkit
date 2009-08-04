/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.filter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import org.geotoolkit.geometry.jts.SRIDGenerator;
import org.opengis.geometry.Envelope;

/**
 * Special care for envelopes wich are changed in JTS polygons.
 *
 * @author Johann Sorel (Geomatys)
 */
public class DefaultEnvelopeLiteral extends DefaultLiteral<Geometry> {

    private static final GeometryFactory GF = new GeometryFactory();

    public DefaultEnvelopeLiteral(Envelope value) {
        super(toGeometry(value));
    }

    private static Geometry toGeometry(Envelope env){
        final double minx = env.getMinimum(0);
        final double miny = env.getMinimum(1);
        final double maxx = env.getMaximum(0);
        final double maxy = env.getMaximum(1);
        final Coordinate[] coords = new Coordinate[5];
        coords[0] = new Coordinate(minx, miny);
        coords[1] = new Coordinate(minx, maxy);
        coords[2] = new Coordinate(maxx, maxy);
        coords[3] = new Coordinate(maxx, miny);
        coords[4] = new Coordinate(minx, miny);
        final LinearRing ring = GF.createLinearRing(coords);
        final Polygon poly = GF.createPolygon(ring, new LinearRing[0]);
        final int srid = SRIDGenerator.toSRID(env.getCoordinateReferenceSystem(), SRIDGenerator.COMPACT_V1);
        poly.setSRID(srid);
        return poly;
    }

}
