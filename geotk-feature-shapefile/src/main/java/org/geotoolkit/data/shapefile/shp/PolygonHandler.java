/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data.shapefile.shp;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.geometry.jts.JTS;
import org.locationtech.jts.algorithm.CGAlgorithms;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;


/**
 * Wrapper for a Shapefile polygon.
 *
 * TODO: remove all Buffer cast after migration to JDK9.
 *
 * @author aaime
 * @author Ian Schneider
 * @author Johann Sorel (Geomatys)
 * @version $Id$
 * @module
 */
public class PolygonHandler extends AbstractShapeHandler {

    protected final List<LinearRing> shells = new ArrayList<>();
    protected final List<LinearRing> holes = new ArrayList<>();

    public PolygonHandler(final boolean read3D) {
        super(ShapeType.POLYGON,read3D);
    }

    public PolygonHandler(final ShapeType type, final boolean read3D) throws DataStoreException {
        super(type,read3D);
        if ((type != ShapeType.POLYGON) && (type != ShapeType.POLYGONM)
                && (type != ShapeType.POLYGONZ)) {
            throw new DataStoreException(
                    "PolygonHandler constructor - expected type to be 5, 15, or 25.");
        }
    }

    // returns true if testPoint is a point in the pointList list.
    protected boolean pointInList(final Coordinate testPoint, final Coordinate[] pointList) {
        Coordinate p;

        for (int t = pointList.length - 1; t >= 0; t--) {
            p = pointList[t];

            if ((testPoint.x == p.x)
              && (testPoint.y == p.y)
              && ((testPoint.z == p.z) || (!(testPoint.z == p.z)))
                    // nan
                    // test;
                    // x!=x
                    // iff
                    // x is
                    // nan
            ) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ShapeType getShapeType() {
        return shapeType;
    }

    @Override
    public int getLength(final Object geometry) {
        final MultiPolygon multi;

        if (geometry instanceof MultiPolygon) {
            multi = (MultiPolygon) geometry;
        } else {
            multi = GEOMETRY_FACTORY
                    .createMultiPolygon(new Polygon[] { (Polygon) geometry });
        }

        int nrings = 0;
        for (int t = 0; t < multi.getNumGeometries(); t++) {
            final Polygon p = (Polygon) multi.getGeometryN(t);
            nrings = nrings + 1 + p.getNumInteriorRing();
        }

        final int npoints = multi.getNumPoints();
        final int length;

        if (shapeType == ShapeType.POLYGONZ) {
            length = 44 + (4 * nrings) + (16 * npoints) + (8 * npoints) + 16
                    + (8 * npoints) + 16;
        } else if (shapeType == ShapeType.POLYGONM) {
            length = 44 + (4 * nrings) + (16 * npoints) + (8 * npoints) + 16;
        } else if (shapeType == ShapeType.POLYGON) {
            length = 44 + (4 * nrings) + (16 * npoints);
        } else {
            throw new IllegalStateException(
                    "Expected ShapeType of Polygon, got " + shapeType);
        }
        return length;
    }

    @Override
    public Geometry estimated(final double minX, final double maxX, final double minY, final double maxY) {
        final double[] array = new double[]{
            minX,minY,
            minX,maxY,
            maxX,maxY,
            maxX,minY,
            minX,minY
        };
        final LinearRing shell = GEOMETRY_FACTORY.createLinearRing(new ShapeCoordinateSequence2D(array, 5));
        return GEOMETRY_FACTORY.createMultiPolygon(new Polygon[] {GEOMETRY_FACTORY.createPolygon(shell, null)});
    }

    @Override
    public Geometry read(final ByteBuffer buffer, final ShapeType type) {
        if (type == ShapeType.NULL) {
            return createNull();
        }

        //clear from previous read
        shells.clear();
        holes.clear();

        // skip the bounds
        ((Buffer) buffer).position(((Buffer) buffer).position() + 32);

        final int numParts = buffer.getInt();
        final int numPoints = buffer.getInt();
        final int[] partOffsets = new int[numParts];

        for (int i = 0; i < numParts; i++) {
            partOffsets[i] = buffer.getInt();
        }

        final DoubleBuffer dbuffer = buffer.asDoubleBuffer();
        final int dimensions = (read3D && shapeType == ShapeType.POLYGONZ)? 3:2;

        //read everything in one round : +2 for minZ/maxZ
        final double[] coords = new double[numPoints*dimensions + ((dimensions==2)?0:2)];
        final int xySize = numPoints*2;
        dbuffer.get(coords);

        int coordIndex = 0;
        for (int part = 0; part < numParts; part++) {

            final int finish;
            if (part == (numParts - 1)) {
                finish = numPoints;
            } else {
                finish = partOffsets[part + 1];
            }
            final int length = finish - partOffsets[part];

            // REVISIT: polyons with only 1 to 3 points are not polygons -
            // geometryFactory will bomb so we skip if we find one.
            if(length > 0 && length < 4){
                coordIndex += length;
                continue;
            }

            final Coordinate[] points = new Coordinate[length];
            for (int i = 0; i < length; i++) {
                if(dimensions==2){
                    points[i] = new Coordinate(coords[coordIndex*2],coords[coordIndex*2+1]);
                }else{
                    points[i] = new Coordinate(coords[coordIndex*2],coords[coordIndex*2+1],coords[xySize+coordIndex+2]);
                }
                coordIndex++;
            }

            JTS.ensureClosed(points);

            final LinearRing ring = GEOMETRY_FACTORY.createLinearRing(points);
            if (CGAlgorithms.isCCW(points)) {
                // counter-clockwise
                holes.add(ring);
            } else {
                // clockwise
                shells.add(ring);
            }
        }

        // quick optimization: if there's only one shell no need to check
        // for holes inclusion
        if (shells.size() == 1) {
            return createMulti(shells.get(0), holes);
        }
        // if for some reason, there is only one hole, we just reverse it and
        // carry on.
        else if (holes.size() == 1 && shells.isEmpty()) {
            //LOGGER.warning("only one hole in this polygon record");
            return createMulti(JTS.reverseRing(holes.get(0)));
        } else {

            // build an association between shells and holes
            final List<List<LinearRing>> holesForShells = assignHolesToShells(shells, holes);
            return buildGeometries(shells, holes, holesForShells);
        }
    }


//    @Override
//    public Object read(ByteBuffer buffer, ShapeType type) {
//        if (type == ShapeType.NULL) {
//            return createNull();
//        }
//        // skip the bounds
//        buffer.position(buffer.position() + 4 * 8);
//
//        final int numParts = buffer.getInt();
//        final int numPoints = buffer.getInt();
//        final int[] partOffsets = new int[numParts];
//
//        for (int i = 0; i < numParts; i++) {
//            partOffsets[i] = buffer.getInt();
//        }
//
//        final List<LinearRing> shells = new ArrayList<LinearRing>();
//        final List<LinearRing> holes = new ArrayList<LinearRing>();
//
//        final Coordinate[] coords = new Coordinate[numPoints];
//        for (int t = 0; t < numPoints; t++) {
//            coords[t] = new Coordinate(buffer.getDouble(), buffer.getDouble());
//        }
//
//        if (shapeType == ShapeType.POLYGONZ) {
//            // skip zmin and zmax
//            buffer.position(buffer.position() + 2 * 8);
//
//            for (int t = 0; t < numPoints; t++) {
//                coords[t].z = buffer.getDouble();
//            }
//        }
//
//        int offset = 0;
//        int start;
//        int finish;
//        int length;
//
//        for (int part = 0; part < numParts; part++) {
//            start = partOffsets[part];
//
//            if (part == (numParts - 1)) {
//                finish = numPoints;
//            } else {
//                finish = partOffsets[part + 1];
//            }
//
//            length = finish - start;
//
//            // Use the progressive CCW algorithm.
//            // basically the area algorithm for polygons
//            // which also tells us vertex order based upon the
//            // sign of the area.
//            Coordinate[] points = new Coordinate[length];
//            // double area = 0;
//            // int sx = offset;
//            for (int i = 0; i < length; i++) {
//                points[i] = coords[offset++];
//                // int j = sx + (i + 1) % length;
//                // area += points[i].x * coords[j].y;
//                // area -= points[i].y * coords[j].x;
//            }
//            // area = -area / 2;
//            // REVISIT: polyons with only 1 or 2 points are not polygons -
//            // geometryFactory will bomb so we skip if we find one.
//            if (points.length == 0 || points.length > 3) {
//                LinearRing ring = GEOMETRY_FACTORY.createLinearRing(points);
//
//                if (CGAlgorithms.isCCW(points)) {
//                    // counter-clockwise
//                    holes.add(ring);
//                } else {
//                    // clockwise
//                    shells.add(ring);
//                }
//            }
//        }
//
//        // quick optimization: if there's only one shell no need to check
//        // for holes inclusion
//        if (shells.size() == 1) {
//            return createMulti(shells.get(0), holes);
//        }
//        // if for some reason, there is only one hole, we just reverse it and
//        // carry on.
//        else if (holes.size() == 1 && shells.isEmpty()) {
//            //LOGGER.warning("only one hole in this polygon record");
//            return createMulti(JTSUtilities.reverseRing(holes.get(0)));
//        } else {
//
//            // build an association between shells and holes
//            final List<List<LinearRing>> holesForShells = assignHolesToShells(shells, holes);
//            return buildGeometries(shells, holes, holesForShells);
//        }
//    }

    /**
     * @param shells
     * @param holes
     * @param holesForShells
     */
    protected Geometry buildGeometries(final List<LinearRing> shells, final List<LinearRing> holes,
            final List<List<LinearRing>> holesForShells) {
        final Polygon[] polygons;

        // if we have shells, lets use them
        final int shellSize = shells.size();
        final int holeSize = holes.size();
        if (shellSize > 0) {
            polygons = new Polygon[shellSize];
        } else {
            // oh, this is a bad record with only holes
            polygons = new Polygon[holeSize];
        }

        // this will do nothing for the "only holes case"
        for (int i=0; i<shellSize; i++) {
            final List<LinearRing> lst = holesForShells.get(i);
            polygons[i] = GEOMETRY_FACTORY.createPolygon(
                    shells.get(i),
                    lst.toArray(new LinearRing[lst.size()]));
        }

        // this will take care of the "only holes case"
        // we just reverse each hole
        if (shellSize == 0) {
            for (int i=0; i<holeSize; i++) {
                final LinearRing hole = holes.get(i);
                polygons[i] = GEOMETRY_FACTORY.createPolygon(JTS.reverseRing(hole),
                        new LinearRing[0]);
            }
        }

        return GEOMETRY_FACTORY.createMultiPolygon(polygons);
    }

    /**
     * <b>Package private for testing</b>
     *
     * @param shells
     * @param holes
     */
    protected List<List<LinearRing>> assignHolesToShells(final List<LinearRing> shells, final List<LinearRing> holes) {

        final int shellSize = shells.size();
        final int holeSize = holes.size();

        final List<List<LinearRing>> holesForShells = new ArrayList(shellSize);
        for (int i=0; i<shellSize ; i++) {
            holesForShells.add(new ArrayList<LinearRing>());
        }

        // find homes
        for (final LinearRing testRing : holes) {
            final Envelope testEnv = testRing.getEnvelopeInternal();
            final Coordinate testPt = testRing.getCoordinateN(0);

            LinearRing minShell = null;
            Envelope minEnv = null;

            for (final LinearRing tryRing : shells) {

                Envelope tryEnv = tryRing.getEnvelopeInternal();
                if (minShell != null) {
                    minEnv = minShell.getEnvelopeInternal();
                }

                boolean isContained = false;
                final Coordinate[] coordList = tryRing.getCoordinates();

                if (tryEnv.contains(testEnv)
                        && (CGAlgorithms.isPointInRing(testPt, coordList) || (pointInList(
                                testPt, coordList)))) {
                    isContained = true;
                }

                // check if this new containing ring is smaller than the current
                // minimum ring
                if (isContained) {
                    if ((minShell == null) || minEnv.contains(tryEnv)) {
                        minShell = tryRing;
                    }
                }
            }

            if (minShell == null) {
                //LOGGER.warning("polygon found with a hole thats not inside a shell");
                // now reverse this bad "hole" and turn it into a shell
                shells.add(JTS.reverseRing(testRing));
                holesForShells.add(new ArrayList());
            } else {
                ((ArrayList) holesForShells.get(shells.indexOf(minShell)))
                        .add(testRing);
            }
        }

        return holesForShells;
    }

    protected MultiPolygon createMulti(final LinearRing single) {
        return createMulti(single, java.util.Collections.EMPTY_LIST);
    }

    protected MultiPolygon createMulti(final LinearRing single, final List<LinearRing> holes) {
        return GEOMETRY_FACTORY
                .createMultiPolygon(new Polygon[] { GEOMETRY_FACTORY
                        .createPolygon(single, holes.toArray(new LinearRing[holes.size()])) });
    }

    protected MultiPolygon createNull() {
        return GEOMETRY_FACTORY.createMultiPolygon(null);
    }

    @Override
    public void write(final ByteBuffer buffer, final Object geometry) {
        final MultiPolygon multi;

        if (geometry instanceof MultiPolygon) {
          multi = (MultiPolygon) geometry;
        } else {
          multi = GEOMETRY_FACTORY.createMultiPolygon(new Polygon[] { (Polygon) geometry });
        }

        final Envelope box = multi.getEnvelopeInternal();
        buffer.putDouble(box.getMinX());
        buffer.putDouble(box.getMinY());
        buffer.putDouble(box.getMaxX());
        buffer.putDouble(box.getMaxY());

        //need to find the total number of rings and points
        final int nrings;
        final CoordinateSequence []coordinates;

        final List<CoordinateSequence> allCoords = new ArrayList<CoordinateSequence>();
        for (int t=0,n=multi.getNumGeometries(); t<n; t++) {
            final Polygon p = (Polygon) multi.getGeometryN(t);
          allCoords.add(p.getExteriorRing().getCoordinateSequence());
          for(int ringN = 0; ringN < p.getNumInteriorRing(); ringN++){
              allCoords.add(p.getInteriorRingN(ringN).getCoordinateSequence());
          }
        }
        coordinates = (CoordinateSequence[])allCoords.toArray(new CoordinateSequence[allCoords.size()]);
        nrings = coordinates.length;

        final int npoints = multi.getNumPoints();
        buffer.putInt(nrings);
        buffer.putInt(npoints);

        int count = 0;
        for (int t = 0; t < nrings; t++) {
          buffer.putInt(count);
          count = count + coordinates[t].size();
        }

        final double[] zExtreame = {Double.NaN, Double.NaN};

        //write out points here!.. and gather up min and max z values
        for (int ringN = 0; ringN < nrings; ringN++) {
            final CoordinateSequence coords = coordinates[ringN];

            JTS.zMinMax(coords, zExtreame);

            final int seqSize = coords.size();
            for(int coordN = 0; coordN < seqSize; coordN++){
                buffer.putDouble(coords.getOrdinate(coordN, 0));
                buffer.putDouble(coords.getOrdinate(coordN, 1));
            }
        }

        if (shapeType == ShapeType.POLYGONZ) {
          //z
          if (Double.isNaN(zExtreame[0])) {
            buffer.putDouble(0.0);
            buffer.putDouble(0.0);
          } else {
            buffer.putDouble(zExtreame[0]);
            buffer.putDouble(zExtreame[1]);
          }

          for (int ringN = 0; ringN < nrings; ringN++) {
              final CoordinateSequence coords = coordinates[ringN];

              final int seqSize = coords.size();
              for (int coordN = 0; coordN < seqSize; coordN++) {
                  final double z = coords.getOrdinate(coordN, 2);
                  if (Double.isNaN(z)) {
                      buffer.putDouble(0.0);
                  } else {
                      buffer.putDouble(z);
                  }
              }
          }
        }

        if (shapeType == ShapeType.POLYGONM || shapeType == ShapeType.POLYGONZ) {
          //m
          buffer.putDouble(-10E40);
          buffer.putDouble(-10E40);

          for (int t = 0; t < npoints; t++) {
            buffer.putDouble(-10E40);
          }
        }
    }

}
