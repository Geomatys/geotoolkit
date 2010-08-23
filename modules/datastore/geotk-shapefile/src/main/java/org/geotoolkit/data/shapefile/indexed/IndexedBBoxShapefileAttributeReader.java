/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.shapefile.indexed;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;

import java.io.IOException;
import java.util.List;

import org.geotoolkit.data.dbf.IndexedDbaseFileReader;
import org.geotoolkit.data.shapefile.indexed.IndexDataReader.ShpData;
import org.geotoolkit.data.shapefile.shp.ShapefileReader;
import org.geotoolkit.index.CloseableCollection;
import org.geotoolkit.index.quadtree.LazyTyleSearchIterator;
import org.geotoolkit.index.quadtree.SearchIterator;

import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;

/**
 * Attribut reader that will check the geometry bbox and resolution.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class IndexedBBoxShapefileAttributeReader extends IndexedShapefileAttributeReader{

    private static final PreparedGeometryFactory PREPARED_FACTORY = new PreparedGeometryFactory();
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
    
    private final Object[] buffer = new Object[metaData.length];
    private final PreparedGeometry boundingGeometry;
    //features must be within this bbox
    private final double bboxMinX;
    private final double bboxMinY;
    private final double bboxMaxX;
    private final double bboxMaxY;
    //feature bbox must be bigger than this
    private final double[] minRes;
    private boolean hasNext = false;
    private int geomAttIndex = 0;

    public IndexedBBoxShapefileAttributeReader(List<? extends PropertyDescriptor> properties,
            ShapefileReader shpReader, IndexedDbaseFileReader dbfR, CloseableCollection<ShpData> goodRec,
            SearchIterator<ShpData> ite, Envelope bbox, double[] minRes) {
        super(properties, shpReader, dbfR, goodRec, ite);
        this.bboxMinX = bbox.getMinX();
        this.bboxMinY = bbox.getMinY();
        this.bboxMaxX = bbox.getMaxX();
        this.bboxMaxY = bbox.getMaxY();
        this.minRes = minRes;
        this.boundingGeometry = toGeometry(bbox);

        for (int i = 0, n = properties.size(); i < n; i++) {
            if (properties.get(i) instanceof GeometryDescriptor) {
                geomAttIndex = i;
                break;
            }
        }
    }

    @Override
    public boolean hasNext() throws IOException {
        findNext();
        return hasNext;
    }

    @Override
    public void next() throws IOException {
        hasNext = false;
    }

    @Override
    public void read(Object[] buffer) throws IOException {
        System.arraycopy(this.buffer, 0, buffer, 0, this.buffer.length);
    }

    private void findNext() throws IOException {
        while (!hasNext && super.hasNext()) {

            //only move to the next shape, avoid moving in the dbf until
            //we are sure we need it
            moveToNextShape();

            if (((LazyTyleSearchIterator.Buffered) goodRecs).isSafe()) {

                //check minSize
                if (minRes != null && !(minRes[0] <= (record.maxX - record.minX) || minRes[1] <= (record.maxY - record.minY))) {
                    //System.out.println("doesn't match");
                    continue;
                }

                moveToNextDbf();
                super.read(buffer);
                hasNext = true;
                return;
            }

            //check if we are in the bbox
            if (!(bboxMinX > record.maxX
                    || bboxMaxX < record.minX
                    || bboxMinY > record.maxY
                    || bboxMaxY < record.minY)) {

                //check minSize
                if (minRes != null && !(minRes[0] <= (record.maxX - record.minX) || minRes[1] <= (record.maxY - record.minY))) {
                    //System.out.println("doesn't match");
                    continue;
                }

                moveToNextDbf();
                super.read(buffer);
//                hasNext = true;
                final Geometry candidate = (Geometry) buffer[geomAttIndex];
                hasNext = boundingGeometry.intersects(candidate);
            }
        }
    }

    /**
     * Utility method to transform an envelope in geometry.
     * @param env
     * @return Geometry
     */
    private static PreparedGeometry toGeometry(Envelope env) {
        final Coordinate[] coords = new Coordinate[5];
        coords[0] = new Coordinate(env.getMinX(), env.getMinY());
        coords[1] = new Coordinate(env.getMinX(), env.getMaxY());
        coords[2] = new Coordinate(env.getMaxX(), env.getMaxY());
        coords[3] = new Coordinate(env.getMaxX(), env.getMinY());
        coords[4] = new Coordinate(env.getMinX(), env.getMinY());
        final LinearRing ring = GEOMETRY_FACTORY.createLinearRing(coords);
        Geometry geom = GEOMETRY_FACTORY.createPolygon(ring, new LinearRing[0]);
        return PREPARED_FACTORY.create(geom);
    }
    
}
