/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.text.ecql;

import java.util.LinkedList;
import java.util.List;

import org.geotoolkit.filter.text.commons.BuildResultStack;
import org.geotoolkit.filter.text.commons.Result;
import org.geotoolkit.filter.text.cql2.CQLException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;


/**
 * Builds a Polygon using the lines (shell and Holes) made in the parsing process.
 *
 * @author Mauricio Pazos (Axios Engineering)
 * @since 2.6
 */
class PolygonBuilder extends GeometryBuilder {
    /**
     * @param statement
     * @param resultStack
     */
    public PolygonBuilder(final String statement, final BuildResultStack resultStack) {
        super(statement, resultStack);
    }

    /**
     * Builds the a polygon using the linestring geometries (Sell and holes).
     * @param linestringNode LineString identifier defined in the grammar file
     */
    @Override
    public Geometry build(final int linestringNode) throws CQLException {
        final Result result = getResultStack().peek();
        try{
            // Retrieve the liner ring for shell and holes
            final List<Geometry> geometryList= popGeometry(linestringNode);

            assert geometryList.size() >= 1;

            // retrieves the shell
            final LineString line = (LineString) geometryList.get(0);
            final LinearRing shell = getGeometryFactory().createLinearRing(line.getCoordinates());

            // if it has holes, creates a ring for each linestring
            LinearRing[] holes = new LinearRing[0];
            if (geometryList.size() > 1) {

                final List<LinearRing> holeList = new LinkedList<LinearRing>();
                for (int i = 1; i < geometryList.size(); i++) {

                    final LineString holeLines = (LineString) geometryList.get(i);
                    final LinearRing ring = getGeometryFactory().createLinearRing(holeLines.getCoordinates());
                    holeList.add(ring);
                }
                final int holesSize = holeList.size();
                holes = holeList.toArray(new LinearRing[holesSize]);
            }
            // creates the polygon
            return getGeometryFactory().createPolygon(shell, holes);
        } catch (Exception e) {
            throw new CQLException(e.getMessage(), result.getToken(), getStatemet());
        }
    }
}
