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

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.geometry.jts.JTS;
import static org.locationtech.jts.algorithm.Orientation.COUNTERCLOCKWISE;
import static org.locationtech.jts.algorithm.Orientation.index;
import org.locationtech.jts.algorithm.RayCrossingCounter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Location;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;


/**
 * Wrapper for a Shapefile polygon.
 *
 * @author aaime
 * @author Ian Schneider
 * @author Johann Sorel (Geomatys)
 * @version $Id$
 * @module
 */
public class PolygonHandler extends AbstractShapeHandler {

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
    protected boolean pointInList(final Coordinate testPoint, final CoordinateSequence pointList) {
        Coordinate p;

        for (int t = pointList.size() - 1; t >= 0; t--) {
            p = pointList.getCoordinate(t);

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

    protected CoordinateSequence decimateRing(CoordinateSequence ring) {
        return ring;
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

        // skip the bounds
        buffer.position(buffer.position() + 32);

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
        /**
         * This shortcut is important for performance: If there's only a single ring, there's no need to try to
         * differentiate holes from shells. This means that we can avoid checking rings orientations,
         * create intermediate lists, etc.
         */
        if (numParts == 1) {
            final CoordinateSequence points = dimensions == 2
                    ? new ShapeCoordinateSequence2D(coords, coordIndex * 2, numPoints)
                    : new ShapeCoordinateSequence3D(coords, coordIndex * 2, numPoints, xySize + 2);
            return GEOMETRY_FACTORY.createMultiPolygon(new Polygon[]{ GEOMETRY_FACTORY.createPolygon(decimateRing(points)) });
        }

        final List<LinearRing> shells = new ArrayList<>();
        final List<LinearRing> holes = new ArrayList<>();
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

            final CoordinateSequence points = dimensions == 2
                    ? new ShapeCoordinateSequence2D(coords, coordIndex * 2, length)
                    : new ShapeCoordinateSequence3D(coords, coordIndex * 2, length, xySize + 2 + coordIndex);
            coordIndex += length;

            // Is this really required ?
            ensureClosed(points);

            final LinearRing ring = GEOMETRY_FACTORY.createLinearRing(points);

            if (isCCW(points)) {
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

    private static void ensureClosed(CoordinateSequence seq) {
        final int last = seq.size() - 1;
        double startX = seq.getX(0);
        double endX = seq.getX(last);
        double startY = seq.getY(0);
        double endY = seq.getY(last);
        if (startX != endX) seq.setOrdinate(last, 0, startX);
        if (startY != endY) seq.setOrdinate(last, 1, startY);
    }

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

                if (tryEnv.contains(testEnv)) {
                    CoordinateSequence cs = tryRing.getCoordinateSequence();
                    if ( (RayCrossingCounter.locatePointInRing(testPt, cs) != Location.EXTERIOR)
                      || (pointInList(testPt, cs))) {
                        isContained = true;
                    }
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


    /**
     * Copied from JTS 1.19.0: same code, but optimized for packed coordinate sequence.
     * We try to replace calls to "getCoordinate" in loops, to avoid unnecessary Coordinate object creation.
     * @param ring
     * @return
     */
    static boolean isCCW(CoordinateSequence ring)
    {
        // # of points without closing endpoint
        int nPts = ring.size() - 1;
        // return default value if ring is flat
        if (nPts < 3) return false;

        /**
         * Find first highest point after a lower point, if one exists
         * (e.g. a rising segment)
         * If one does not exist, hiIndex will remain 0
         * and the ring must be flat.
         * Note this relies on the convention that
         * rings have the same start and end point.
         */
        Coordinate upHiPt = ring.getCoordinate(0);
        double prevY = upHiPt.y;
        Coordinate upLowPt = new Coordinate(Coordinate.NULL_ORDINATE, Coordinate.NULL_ORDINATE);
        int iUpHi = 0;
        for (int i = 1; i <= nPts; i++) {
            double py = ring.getOrdinate(i, Coordinate.Y);
            /**
             * If segment is upwards and endpoint is higher, record it
             */
            if (py > prevY && py >= upHiPt.y) {
                upHiPt.x = ring.getOrdinate(i, Coordinate.X);
                upHiPt.y = py;
                iUpHi = i;
                upLowPt.x = ring.getOrdinate(i-1, Coordinate.X);
                upLowPt.y = prevY;
            }
            prevY = py;
        }
        /**
         * Check if ring is flat and return default value if so
         */
        if (iUpHi == 0) return false;

        /**
         * Find the next lower point after the high point
         * (e.g. a falling segment).
         * This must exist since ring is not flat.
         */
        int iDownLow = iUpHi;
        do {
            iDownLow = (iDownLow + 1) % nPts;
        } while (iDownLow != iUpHi && ring.getOrdinate(iDownLow, Coordinate.Y) == upHiPt.y );

        Coordinate downLowPt = ring.getCoordinate(iDownLow);
        int iDownHi = iDownLow > 0 ? iDownLow - 1 : nPts - 1;
        Coordinate downHiPt = ring.getCoordinate(iDownHi);

        /**
         * Two cases can occur:
         * 1) the hiPt and the downPrevPt are the same.
         *    This is the general position case of a "pointed cap".
         *    The ring orientation is determined by the orientation of the cap
         * 2) The hiPt and the downPrevPt are different.
         *    In this case the top of the cap is flat.
         *    The ring orientation is given by the direction of the flat segment
         */
        if (upHiPt.equals2D(downHiPt)) {
            /**
             * Check for the case where the cap has configuration A-B-A.
             * This can happen if the ring does not contain 3 distinct points
             * (including the case where the input array has fewer than 4 elements), or
             * it contains coincident line segments.
             */
            if (upLowPt.equals2D(upHiPt) || downLowPt.equals2D(upHiPt) || upLowPt.equals2D(downLowPt))
                return false;

            /**
             * It can happen that the top segments are coincident.
             * This is an invalid ring, which cannot be computed correctly.
             * In this case the orientation is 0, and the result is false.
             */
            int index = index(upLowPt, upHiPt, downLowPt);
            return index == COUNTERCLOCKWISE;
        }
        else {
            /**
             * Flat cap - direction of flat top determines orientation
             */
            double delX = downHiPt.x - upHiPt.x;
            return delX < 0;
        }
    }
}
