/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.process.coverage;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.RectIterFactory;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.SimpleInternationalString;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageToVectorProcess_1 extends AbstractProcess {

    private static final GeometryFactory GF = new GeometryFactory();
    private static final LinearRing[] EMPTY_RING_ARRAY = new LinearRing[0];
    private NumberRange lastNumberRange = null;
    private Geometry[] result = null;
    private int y = -1;
    private int startX = -1;
    private int endX = -1;

    CoverageToVectorProcess_1(ProcessDescriptor descriptor) {
        super(descriptor);
    }

    public Geometry[] toPolygon(GridCoverage2D coverage, final NumberRange[] ranges, int band)
            throws IOException, TransformException {
        coverage = coverage.view(ViewType.GEOPHYSICS);

        final Map<NumberRange, List<Geometry>> polygons = new HashMap<NumberRange, List<Geometry>>();
        for (final NumberRange range : ranges) {
            polygons.put(range, new ArrayList<Geometry>());
        }

        final RectIter iter = RectIterFactory.create(coverage.getRenderedImage(), null);
        final MathTransform2D gridToCRS = coverage.getGridGeometry().getGridToCRS2D();
        final Point gridPosition = new Point(0, 0);

        int bandNum = -1;
        iter.startBands();
        if (!iter.finishedBands()) {

            //iteration over bands
            do {
                bandNum++;
                //System.out.println("bande " + bandNum);

                if (bandNum == band) {
                    //iteration over lines
                    iter.startLines();
                    if (!iter.finishedLines()) {
                        do {

                            //System.out.println("ligne " + gridPosition.y);
                            //iteration over pixels
                            iter.startPixels();
                            if (!iter.finishedPixels()) {
                                do {
                                    //----------------------------------------------
                                    final double value = iter.getSampleDouble();
                                    append(polygons, gridPosition, value);
                                    //----------------------------------------------

                                    gridPosition.x += 1;
                                } while (!iter.nextPixelDone());
                            }

                            //insert last geometry
                            final Polygon pixel = toPolygon(startX, endX, y);
                            polygons.get(lastNumberRange).add(pixel);

                            lastNumberRange = null;
                            startX = -1;
                            endX = -1;
                            y = -1;

                            gridPosition.x = 0;
                            gridPosition.y += 1;
                        } while (!iter.nextLineDone());
                    }
                }

                gridPosition.x = 0;
                gridPosition.y = 0;
            } while (!iter.nextBandDone());
        }

        //System.out.println("packing");
        final Geometry[] polygones = new Geometry[ranges.length];
        for (int i=0; i<ranges.length; i++) {
            final NumberRange range = ranges[i];
            final GeometryCollection gc = GF.createGeometryCollection(polygons.get(range).toArray(new Geometry[0]));
            Geometry union = gc.buffer(0); //union();
            union = JTS.transform(union, gridToCRS);
            union.setUserData(range);
            polygones[i] = union;
        }

        return polygones;
    }

    private void append(Map<NumberRange, List<Geometry>> polygons, Point point, Number value) {
        for (final NumberRange range : polygons.keySet()) {

            if (range.contains(value)) {
                if (lastNumberRange == range) {
                    //last pixel was in the same range
                    endX = point.x + 1;
                    return;
                } else if (lastNumberRange != null) {
                    //last pixel was in a different range, save it's geometry
                    final Polygon pixel = toPolygon(startX, endX, y);
                    polygons.get(lastNumberRange).add(pixel);
                }

                //start a pixel serie
                lastNumberRange = range;
                startX = point.x;
                endX = point.x + 1;
                y = point.y;

                return;
            }
        }
    }

    @Override
    public ParameterValueGroup getOutput() {
        final ParameterValueGroup group = super.getOutput();
        group.parameter(CoverageToVectorDescriptor.GEOMETRIES.getName().getCode()).setValue(result);
        return group;
    }

    @Override
    public void run() {
        if (inputParameters == null) {
            getMonitor().failed(new ProcessEvent(this, -1,
                    new SimpleInternationalString("Input parameters not set."),
                    new NullPointerException("Input parameters not set.")));
        }

        final GridCoverage2D coverage = (GridCoverage2D) inputParameters.parameter(CoverageToVectorDescriptor.COVERAGE.getName().getCode()).getValue();
        final NumberRange[] ranges = (NumberRange[]) inputParameters.parameter(CoverageToVectorDescriptor.RANGES.getName().getCode()).getValue();
        try {
            result = toPolygon(coverage, ranges, 0);
        } catch (IOException ex) {
            getMonitor().failed(new ProcessEvent(this, -1, null, ex));
        } catch (TransformException ex) {
            getMonitor().failed(new ProcessEvent(this, -1, null, ex));
        }

        getMonitor().ended(new ProcessEvent(this));

    }

    private static Polygon toPolygon(int startx, int endx, int y) {
        final Coordinate coord = new Coordinate(startx, y);
        final LinearRing ring = GF.createLinearRing(
                new Coordinate[]{
                    coord,
                    new Coordinate(endx, y),
                    new Coordinate(endx, y + 1),
                    new Coordinate(startx, y + 1),
                    coord
                });
        return GF.createPolygon(ring, EMPTY_RING_ARRAY);
    }

}
