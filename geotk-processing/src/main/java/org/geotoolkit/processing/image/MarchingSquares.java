/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.processing.image;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.internal.processing.isoline.Isolines;
import org.apache.sis.util.Static;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.operation.linemerge.LineMerger;

/**
 *
 * Based on algorithm : https://en.wikipedia.org/wiki/Marching_squares
 *
 * @author Johann Sorel (Geomatys)
 * @deprecated Superseded with Apache SIS {@link Isolines} processing.
 */
@Deprecated(forRemoval = true, since = "22.04.22")
public final class MarchingSquares extends Static {

    private static final GeometryFactory GF = org.geotoolkit.geometry.jts.JTS.getFactory();

    /**
     *
     * @param ite image pixel iterator
     * @param threshold searched value
     * @param band band index in image
     * @param mergeLines use LineMerger to reduce the number of created lines.
     * @return MultiLineString, can be null if no segment found
     */
    public static MultiLineString build(PixelIterator ite, double threshold, int band, boolean mergeLines) {
        final Rectangle domain = ite.getDomain();

        final List<LineString> geometries = new ArrayList<>();
        for (int x=domain.x,nx=x+domain.width-1; x<nx; x++) {
            for (int y=domain.y,ny=y+domain.height-1; y<ny; y++) {
                ite.moveTo(x, y);
                double ll = ite.getSampleDouble(band);
                if (Double.isNaN(ll)) continue;
                ite.moveTo(x+1, y);
                double lr = ite.getSampleDouble(band);
                if (Double.isNaN(lr)) continue;
                ite.moveTo(x, y+1);
                double tl = ite.getSampleDouble(band);
                if (Double.isNaN(tl)) continue;
                ite.moveTo(x+1, y+1);
                double tr = ite.getSampleDouble(band);
                if (Double.isNaN(tr)) continue;

                int idx = 0;
                if (ll > threshold) idx |= 1;
                if (lr > threshold) idx |= 2;
                if (tr > threshold) idx |= 4;
                if (tl > threshold) idx |= 8;

                //solve saddle points
                if (idx == 5 && isCenterBelowThreshold(threshold, ll, lr, tl, tr)) {
                    idx = 10;
                } else if (idx == 10 && isCenterBelowThreshold(threshold, ll, lr, tl, tr)) {
                    idx = 5;
                }

                Object geom = buildGeometry(idx, threshold, ll, lr, tl, tr, x, y);
                if (geom instanceof LineString) {
                    geometries.add((LineString) geom);
                } else if(geom instanceof LineString[]) {
                    LineString[] array = (LineString[]) geom;
                    geometries.add(array[0]);
                    geometries.add(array[1]);
                }
            }
        }

        if (geometries.isEmpty()) {
            return null;
        }

        if (mergeLines) {
            final LineMerger lineMerger = new LineMerger();
            lineMerger.add(geometries);
            geometries.clear();
            geometries.addAll(lineMerger.getMergedLineStrings());
        }

        return GF.createMultiLineString(geometries.toArray(new LineString[geometries.size()]));
    }

    private static Object buildGeometry(int idx, double threshold, double ll, double lr, double tl, double tr, int x, int y) {
        switch (idx) {
            /*
             0----0
             |    |
             |    |
             0----0
            */
            case 0 :
            /*
             1----1
             |    |
             |    |
             1----1
            */
            case 15 :
                return null;
            /*
             0----0
             \    |
             |\   |
             1-\--0
            */
            case 1 :
            /*
             1----1
             \    |
             |\   |
             0-\--1
            */
            case 14 :
                return createWtoS(threshold, ll, lr, tl, tr, x, y);
            /*
             0----0
             |    /
             |   /|
             0--/-1
            */
            case 2 :
            /*
             1----1
             |    /
             |   /|
             1--/-0
            */
            case 13 :
                return createEtoS(threshold, ll, lr, tl, tr, x, y);
            /*
             0----0
             |____|
             |    |
             1----1
            */
            case 3 :
            /*
             1----1
             |____|
             |    |
             0----0
            */
            case 12 :
                return createWtoE(threshold, ll, lr, tl, tr, x, y);
            /*
             0--\-1
             |   \|
             |    \
             0----0
            */
            case 4 :
            /*
             1--\-0
             |   \|
             |    \
             1----1
            */
            case 11 :
                return createEtoN(threshold, ll, lr, tl, tr, x, y);
            /*
             1-/--0
             |/   /
             /   /|
             0--/-1
            */
            case 5 :
                return new LineString[]{
                    createEtoS(threshold, ll, lr, tl, tr, x, y),
                    createWtoN(threshold, ll, lr, tl, tr, x, y)
                };
            /*
             0--|-1
             |  | |
             |  | |
             0--|-1
            */
            case 6 :
            /*
             1--|-0
             |  | |
             |  | |
             1--|-0
            */
            case 9 :
                return createStoN(threshold, ll, lr, tl, tr, x, y);
            /*
             0-/--1
             |/   |
             /    |
             1----1
            */
            case 7 :
            /*
             1-/--0
             |/   |
             /    |
             0----0
            */
            case 8 :
                return createWtoN(threshold, ll, lr, tl, tr, x, y);
            /*
             1--\-0
             \   \|
             |\   \
             0-\--1
            */
            case 10 :
                return new LineString[]{
                    createEtoN(threshold, ll, lr, tl, tr, x, y),
                    createWtoS(threshold, ll, lr, tl, tr, x, y)
                };
        }

        throw new IllegalStateException("Unexpected case " + idx);
    }

    private static double interpolate(double start, double end, double threshold) {
        return (threshold - start) / (end - start);
    }

    private static Coordinate createE(double threshold, double lr, double tr, int x, int y) {
        return new Coordinate(x + 1 , y + interpolate(lr, tr, threshold));
    }

    private static Coordinate createW(double threshold, double ll, double tl, int x, int y) {
        return new Coordinate(x , y + interpolate(ll, tl, threshold));
    }

    private static Coordinate createS(double threshold, double ll, double lr, int x, int y) {
        return new Coordinate(x + interpolate(ll, lr, threshold), y);
    }

    private static Coordinate createN(double threshold, double tl, double tr, int x, int y) {
        return new Coordinate(x + interpolate(tl, tr, threshold), y + 1);
    }

    private static LineString createWtoS(double threshold, double ll, double lr, double tl, double tr, int x, int y) {
        return GF.createLineString(new Coordinate[]{
            createW(threshold, ll, tl, x, y),
            createS(threshold, ll, lr, x, y)
        });
    }

    private static LineString createWtoN(double threshold, double ll, double lr, double tl, double tr, int x, int y) {
        return GF.createLineString(new Coordinate[]{
            createW(threshold, ll, tl, x, y),
            createN(threshold, tl, tr, x, y)
        });
    }

    private static LineString createWtoE(double threshold, double ll, double lr, double tl, double tr, int x, int y) {
        return GF.createLineString(new Coordinate[]{
            createW(threshold, ll, tl, x, y),
            createE(threshold, lr, tr, x, y)
        });
    }

    private static LineString createEtoN(double threshold, double ll, double lr, double tl, double tr, int x, int y) {
        return GF.createLineString(new Coordinate[]{
            createE(threshold, lr, tr, x, y),
            createN(threshold, tl, tr, x, y)
        });
    }

    private static LineString createEtoS(double threshold, double ll, double lr, double tl, double tr, int x, int y) {
        return GF.createLineString(new Coordinate[]{
            createE(threshold, lr, tr, x, y),
            createS(threshold, ll, lr, x, y)
        });
    }

    private static LineString createStoN(double threshold, double ll, double lr, double tl, double tr, int x, int y) {
        return GF.createLineString(new Coordinate[]{
            createS(threshold, ll, lr, x, y),
            createN(threshold, tl, tr, x, y)
        });
    }

    private static boolean isCenterBelowThreshold(double threshold, double ll, double lr, double tl, double tr) {
        return ((ll + lr + tl + tr) / 4.0) < threshold;
    }
}
