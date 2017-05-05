/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.geometry.jts;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import org.apache.sis.util.ArgumentChecks;
import static java.lang.StrictMath.*;
import org.apache.sis.util.NullArgumentException;

/**
 * Contain method to translate any {@code LineString}.
 *
 * @author Remi Marechal (Geomatys).
 */
public class LineStringTranslator {

    /**
     * Accepted tolerance.
     * @see #intersectionRightLine(com.vividsolutions.jts.geom.Coordinate, com.vividsolutions.jts.geom.Coordinate, com.vividsolutions.jts.geom.Coordinate, com.vividsolutions.jts.geom.Coordinate)
     */
    private static final double TOLERANCE = 1E-9;

    /**
     * {@link IllegalArgumentException} use to stipulate that impossible to translate current {@link LineString}.
     * @see #intersectionRightLine(com.vividsolutions.jts.geom.Coordinate, com.vividsolutions.jts.geom.Coordinate, com.vividsolutions.jts.geom.Coordinate, com.vividsolutions.jts.geom.Coordinate)
     */
    private static final IllegalArgumentException NO_INTERSECTION_EXCEPTION = new IllegalArgumentException("no intersection between lines.");

    public static MultiLineString translateLineString(final MultiLineString line, final double offset) {
        ArgumentChecks.ensureNonNull("MultiLineString", line);
        if (offset == 0) return line;
        final GeometryFactory geomFact = line.getFactory();
        final LineString[] ls = new LineString[line.getNumGeometries()];
        for(int i=0;i<ls.length;i++){
            ls[i] = translateLineString((LineString)line.getGeometryN(i), offset);
        }
        final MultiLineString mls = geomFact.createMultiLineString(ls);
        mls.setSRID(line.getSRID());
        mls.setUserData(line.getUserData());
        return mls;
    }

    /**
     * <p>Translate {@code LineString} in function of offset value.<br/>
     * If offset is positive, translation is on the right of the {@link LineString}
     * and else on the left relative to the sens of the {@link LineString}.<br/>
     * Moreover the distance between the two generates {@link LineString}s is equal
     * to {@link StrictMath#abs(double) } of the offset.<br/>
     * In other word is the standard normal length of each {@link LineString} segments.</p>
     *
     * <p>For example : (generate {@link LineString} own letter with ' character).</p>
     * <p>
     * With a positive offset : </p><br/>
     * <pre>
     *                    B___________ C
     *                    |  _______  \
     *                    | |B'   C'\  \
     *                    | |        \  \
     *                    | |        D'  D
     *                    A A'
     * </pre>
     * <br/><p>
     * And with a negative offset : </p><br/>
     * <pre>
     *                 B'________________C'
     *                 |  B___________ C \
     *                 |  |           \   \
     *                 |  |            \   \
     *                 |  |             \   \
     *                 |  |              D   D'
     *                 A' A
     * </pre>
     *
     * @param line {@link LineString} which will be translate.
     * @param offset define length and sens of translation (+ for right and - for left).
     * @return the translated {@link LineString}.
     * @throws NullArgumentException if line is {@code null}.
     */
    public static LineString translateLineString(final LineString line, final double offset) {
        ArgumentChecks.ensureNonNull("linestring", line);
        if (offset == 0) return line;
        final GeometryFactory geomFact = line.getFactory();
        final Coordinate[] coords = line.getCoordinates();
        final int coordsLength = coords.length;
        final Coordinate[] dstCoords = new Coordinate[coordsLength];
        Coordinate dstPtn0 = new Coordinate();
        Coordinate dstPtn1 = new Coordinate();

        translateVector(coords[0], coords[1], dstPtn0, dstPtn1, offset);
        dstCoords[0] = dstPtn0;
        if (coordsLength == 2) {
            dstCoords[1] = dstPtn1;
            return geomFact.createLineString(dstCoords);
        }

        final int segmentNumber = coordsLength - 1;
        for (int c = 1; c <  segmentNumber; c++) {
            Coordinate dstc0 = new Coordinate();
            Coordinate dstc1 = new Coordinate();
            translateVector(coords[c], coords[c+1], dstc0, dstc1, offset);

            //-- intersection between current translated segment and precedently generated segment.
             final Coordinate i = intersectionRightLine(dstPtn0, dstPtn1, dstc0, dstc1);
            //-- store generated intersection point
            dstCoords[c] = i;

            dstPtn0 = dstc0;
            dstPtn1 = dstc1;
            if (c == segmentNumber - 1) {
                if (coords[0].equals2D(coords[coordsLength - 1])) {
                    //-- in case of closed linestring
                    final Coordinate iend = intersectionRightLine(dstPtn0, dstPtn1, dstCoords[0], dstCoords[1]);
                    dstCoords[0]   = iend;
                    dstCoords[++c] = new Coordinate(iend);
                } else {
                    dstCoords[++c] = dstc1;
                }
            }
        }

        final LineString ls = geomFact.createLineString(dstCoords);
        ls.setSRID(line.getSRID());
        ls.setUserData(line.getUserData());
        return ls;
    }

    /**
     * Translate a single segment from {@link LineString}.
     *
     * @param srcPt0 begin point of the segment which will be translate.
     * @param srcPt1 ending point of the segment which will be translated.
     * @param destPt0 the destination begin point of the translated segment.
     * @param destPt1 the destination ending point of the translated segment.
     * @param offset define length and sens of translation (+ for right and - for left).
     */
    private static void translateVector(final Coordinate srcPt0,  final Coordinate srcPt1,
                                       final Coordinate destPt0, final Coordinate destPt1, final double offset) {

        final double vx = srcPt1.x - srcPt0.x;
        final double vy = srcPt1.y - srcPt0.y;

        //-- orthogonal vector coordinates.
        double ox = vy;
        double oy = - vx;

        final double normO = hypot(ox, oy);
        ox /= normO;
        oy /= normO;

        ox *= offset;
        oy *= offset;

        destPt0.x = srcPt0.x + ox;
        destPt0.y = srcPt0.y + oy;

        destPt1.x = srcPt1.x + ox;
        destPt1.y = srcPt1.y + oy;
    }

    /**
     * Found intersection point between two lines (AB) (CD).
     *
     * @param A begin point of the vector AB.
     * @param B ending point of the vector AB.
     * @param C begin point of the vector CD.
     * @param D ending point of the vector CD.
     * @return intersection point of the two right line define by the two vector AB AC.
     * @throws IllegalArgumentException if the two right line don't intersect.
     */
    private static Coordinate intersectionRightLine(final Coordinate A, final Coordinate B, final Coordinate C, final Coordinate D) {

        /*
         * That u director vector of (AB) line.
         */
         final double ux = B.x - A.x;
         final double uy = B.y - A.y;

        /*
         * That v director vector of (CD) line.
         */
         final double vx = D.x - C.x;
         final double vy = D.y - C.y;


         //-- particularity cases.

        /*
         * pB = pC
         */
        if (abs(B.x - C.x) < TOLERANCE && abs(B.y - C.y) < TOLERANCE) return new Coordinate(B.x, B.y);

        /*
         * pA = pD
         * if lineString sens is respected, normaly should never append.
         */
        if (abs(A.x - D.x) < TOLERANCE && abs(A.y - D.y) < TOLERANCE) return new Coordinate(A.x, A.y);

        /*
         * pA = pB
         * Should never append.
         */
        if (abs(A.x - B.x) < TOLERANCE && abs(A.y - B.y) < TOLERANCE) {
            /*
             * CA vector.
             */
            final double wx = A.x - C.x;
            final double wy = A.y - C.y;
            /*
             * Compute scalar product of vector CA and CD.
             * If equal zero means colinear vector and AB € CD.
             */
            if (abs(wx * vy - wy * vx) < TOLERANCE) return new Coordinate(A.x, A.y);
            throw NO_INTERSECTION_EXCEPTION;
        }

        /*
         * pC = pD
         * Should never append.
         */
        if (abs(C.x - D.x) < TOLERANCE && abs(C.y - D.y) < TOLERANCE) {
            /*
             * AC vector.
             */
            final double wx = C.x - A.x;
            final double wy = C.y - A.y;
            /*
             * Compute scalar product of vector AC and AB.
             * If equal zero means colinear vector and CD € AB.
             */
            if (abs(ux * wy - uy * wx) < TOLERANCE) return new Coordinate(C.x, C.y);
            throw NO_INTERSECTION_EXCEPTION;
        }

        if (abs(vy) < TOLERANCE && abs(ux) < TOLERANCE) {
              /*
               *     B
               *     |
               * C __|___ D
               *     |
               *     A
               */
                return new Coordinate(A.x, C.y);
        }

        if (abs(vx) < TOLERANCE && abs(uy) < TOLERANCE) {
              /*
               *     D
               *     |
               * A __|___ B
               *     |
               *     C
               */
                return new Coordinate(C.x, A.y);
        }

         if (abs(ux) < TOLERANCE && abs(vx) < TOLERANCE) {
            /*
             * B
             * |  D
             * A  |
             *    C
             */
            if (abs(A.x - C.x) > TOLERANCE) throw NO_INTERSECTION_EXCEPTION;
            return new Coordinate(A.x, getIntersectionOnOneDimension(A.y, B.y, C.y, D.y));
        }

         if (abs(uy) < TOLERANCE && abs(vy) < TOLERANCE) {
            /*
             * A_____B
             *    C_____D
             */
            if (abs(A.y - C.y) > TOLERANCE) throw NO_INTERSECTION_EXCEPTION;
            return new Coordinate(getIntersectionOnOneDimension(A.x, B.x, C.x, D.x), A.y);
         }

         if (abs(ux) <= TOLERANCE) {
            /*
             *     B  D
             *     | /
             *     |/
             *    /|
             *   / A
             *  C
             */
             final double yv = (A.x - C.x) * (vy / vx) + C.y;
             return new Coordinate(A.x, yv);
         }

         if (abs(uy) <= TOLERANCE) {
            /*
             *       D
             *      /
             *  A__/____B
             *    /
             *   C
             */
             final double xv = (A.y - C.y) * (vx / vy) + C.x;
             return new Coordinate(xv, A.y);
         }

         if (abs(vx) <= TOLERANCE) {
            /*
             *     D  B
             *     | /
             *     |/
             *    /|
             *   / C
             *  A
             */
             final double yu = (C.x - A.x) * (uy / ux) + A.y;
             return new Coordinate(C.x, yu);
         }

         if (abs(vy) <= TOLERANCE) {
            /*
             *       B
             *      /
             *  C__/____D
             *    /
             *   A
             */
             final double xu = (C.y - A.y) * (ux / uy) + A.x;
             return new Coordinate(xu, C.y);
         }

         final double vx_vy = vx / vy;

         /*
          * (AB) Cartesian equation.
          * {X(t) = t * ux + A.x
          * {Y(t) = t * uy + A.y
          */
         final double t = ((A.y - C.y) * vx_vy + C.x - A.x) / (ux - uy * vx_vy);
         final double x = t * ux + A.x;
         final double y = t * uy + A.y;

         /*
          * That x tq : y = ax + b cartesian line expression.
          */
         final double y2 = (x - C.x) / vx_vy + C.y; // y2 = (x - pC.x) * (vy / vx) + pC.y

         assert abs(y - y2) <= TOLERANCE : "Error computing y from lines should be equal : y(AB) = "+y+" y(CD) = "+y2;
         return new Coordinate(x, y);
    }

    /**
     * Returns intersection point between 2 spans.
     *
     * @param a begin coordinate of the ab span.
     * @param b ending coordinate of the ab span.
     * @param c begin coordinate of the cd span.
     * @param d ending coordinate of the cd span.
     * @return intersection point between 2 spans.
     * @throws IllegalArgumentException if no intersection.
     */
    private static double getIntersectionOnOneDimension(final double a, final double b, final double c, final double d) {
       /*
        * Define intersection on 1 axis if exist
        */
       final double iminx = max(a, c);
       final double imaxx = min(b, d);

       if (imaxx + TOLERANCE < iminx - TOLERANCE) {
         /*
          * A_____B    C____D
          */
           throw NO_INTERSECTION_EXCEPTION;
       }
       return (iminx + imaxx) / 2.0;
    }
}
