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

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.prep.PreparedGeometry;
import org.locationtech.jts.geom.prep.PreparedGeometryFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.sis.internal.feature.AttributeConvention;

import org.geotoolkit.data.shapefile.lock.AccessManager;
import org.geotoolkit.data.shapefile.indexed.IndexDataReader.ShpData;
import org.geotoolkit.index.CloseableCollection;
import org.geotoolkit.index.quadtree.LazyTyleSearchIterator;
import org.apache.sis.storage.DataStoreException;
import org.opengis.feature.AttributeType;
import org.opengis.feature.PropertyType;


/**
 * Attribut reader that will check the geometry bbox and resolution.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class IndexedBBoxShapefileAttributeReader extends IndexedShapefileAttributeReader<LazyTyleSearchIterator.Buffered<ShpData>>{

    private static final PreparedGeometryFactory PREPARED_FACTORY = new PreparedGeometryFactory();
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    private final Object[] buffer = new Object[metaData.length];
    private final PreparedGeometry boundingGeometry;
    //features must be within this bbox
    private final double bboxMinX;
    private final double bboxMinY;
    private final double bboxMaxX;
    private final double bboxMaxY;

    //feature bbox must be bigger than this, otherwise they are bypassed
    private final boolean minRes;
    private final double minResX;
    private final double minResY;
    //if we must be accurate or not in intersection test
    private final boolean loose;
    private boolean hasNext = false;
    private int geomAttIndex = 0;

    /**
     *
     * @param locker - to aquiere different readers and writers.
     * @param atts - the attributes that we are going to read.
     * @param read3D - for shp reader, read 3d coordinate or not.
     * @param memoryMapper - for shp and dbf reader
     * @param resample - for shp reader, decimate coordinates while reading
     * @param readDBF - true to open a dbf reader
     * @param charset - for dbf reader
     * @param estimateRes - avoid reading geometry if under this resolution,
     *                      while return an approximate geometry
     */
    public IndexedBBoxShapefileAttributeReader(final AccessManager locker,
            final AttributeType[] atts, final boolean read3D, final boolean memoryMapped,
            final double[] resample, final boolean readDBF, final Charset charset,
            final double[] estimateRes, final CloseableCollection<ShpData> goodRec,
            final LazyTyleSearchIterator.Buffered<ShpData> ite, final Envelope bbox,
            final boolean loose, final double[] minRes) throws IOException, DataStoreException {
        super(locker,atts,read3D,memoryMapped,resample,readDBF,charset,estimateRes,goodRec,ite);
        this.bboxMinX = bbox.getMinX();
        this.bboxMinY = bbox.getMinY();
        this.bboxMaxX = bbox.getMaxX();
        this.bboxMaxY = bbox.getMaxY();
        if(minRes != null){
            this.minRes = true;
            this.minResX = minRes[0];
            this.minResY = minRes[1];
        }else{
            this.minRes = false;
            this.minResX = 0;
            this.minResY = 0;
        }
        this.loose = loose;
        this.boundingGeometry = toGeometry(bbox);

        for (int i=0; i<atts.length; i++) {
            if (AttributeConvention.isGeometryAttribute(atts[i])) {
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
    public void read(final Object[] buffer) throws IOException {
        System.arraycopy(this.buffer, 0, buffer, 0, this.buffer.length);
    }

    private void findNext() throws IOException {
        while (!hasNext && super.hasNext()) {

            //only move to the next shape, avoid moving in the dbf until
            //we are sure we need it
            moveToNextShape();

            if (goodRecs!=null && goodRecs.isSafe()) {

                //check minSize
                if (minRes && !(minResX <= (record.maxX - record.minX) || minResY <= (record.maxY - record.minY))) {
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
                if (minRes && !(minResX <= (record.maxX - record.minX) || minResY <= (record.maxY - record.minY))) {
                    continue;
                }

                moveToNextDbf();
                super.read(buffer);

                if(loose){
                    hasNext = true;
                }else{
                    final Geometry candidate = (Geometry) buffer[geomAttIndex];
                    hasNext = boundingGeometry.intersects(candidate);
                }
            }
        }
    }

    /**
     * Utility method to transform an envelope in geometry.
     * @param env
     * @return Geometry
     */
    private static PreparedGeometry toGeometry(final Envelope env) {
        double minx = env.getMinX();
        double miny = env.getMinY();
        double maxx = env.getMaxX();
        double maxy = env.getMaxY();
        if(Double.isNaN(minx)) minx = Double.NEGATIVE_INFINITY;
        if(Double.isNaN(miny)) miny = Double.NEGATIVE_INFINITY;
        if(Double.isNaN(maxx)) maxx = Double.POSITIVE_INFINITY;
        if(Double.isNaN(maxy)) maxy = Double.POSITIVE_INFINITY;

        final Coordinate[] coords = new Coordinate[5];
        coords[0] = new Coordinate(minx, miny);
        coords[1] = new Coordinate(minx, maxy);
        coords[2] = new Coordinate(maxx, maxy);
        coords[3] = new Coordinate(maxx, miny);
        coords[4] = new Coordinate(minx, miny);
        final LinearRing ring = GEOMETRY_FACTORY.createLinearRing(coords);
        Geometry geom = GEOMETRY_FACTORY.createPolygon(ring, new LinearRing[0]);
        return PREPARED_FACTORY.create(geom);
    }

}
