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
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.Point;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class CoverageToVectorProcess extends AbstractProcess {

    private static final GeometryFactory GF = new GeometryFactory();
    private static final LinearRing[] EMPTY_RING_ARRAY = new LinearRing[0];
    private static final int LAST_LINE = 0;
    private static final int CURRENT_LINE = 1;

    //last line cache boundary
    private final Map<NumberRange, List<Polygon>> polygons = new HashMap<NumberRange, List<Polygon>>();

    //buffer[0] holds last line buffer
    //buffer[1] holds current line buffer
    private Boundary[][] buffers;

    private Geometry[] result = null;

    //current pixel block
    private NumberRange lastNumberRange = null;
    private int y = -1;
    private int startX = -1;
    private int endX = -1;

    CoverageToVectorProcess(ProcessDescriptor descriptor) {
        super(descriptor);
    }

    public Geometry[] toPolygon(GridCoverage2D coverage, final NumberRange[] ranges, int band)
            throws IOException, TransformException {
        coverage = coverage.view(ViewType.GEOPHYSICS);

        for (final NumberRange range : ranges) {
            polygons.put(range, new ArrayList<Polygon>());
        }

        final RenderedImage image = coverage.getRenderedImage();
        final RectIter iter = RectIterFactory.create(image, null);
        final MathTransform2D gridToCRS = coverage.getGridGeometry().getGridToCRS2D();
        final Point gridPosition = new Point(0, 0);

        buffers = new Boundary[2][image.getWidth()];

        int bandNum = -1;
        iter.startBands();
        if (!iter.finishedBands()) {

            //iteration over bands
            do {
                bandNum++;
                System.err.println("bande " + bandNum);

                if (bandNum == band) {
                    //iteration over lines
                    iter.startLines();
                    if (!iter.finishedLines()) {
                        do {

                            System.err.println("ligne " + gridPosition.y);
                            //iteration over pixels
                            iter.startPixels();
                            if (!iter.finishedPixels()) {
                                do {
                                    //----------------------------------------------
                                    final double value = iter.getSampleDouble();
                                    append(gridPosition, value);
                                    //----------------------------------------------

                                    gridPosition.x += 1;
                                } while (!iter.nextPixelDone());
                            }

                            //insert last geometry
                            constructBlock();

                            //flip buffers, reuse old buffer line.
                            Boundary[] oldLine = buffers[LAST_LINE];
                            buffers[LAST_LINE] = buffers[CURRENT_LINE];
                            buffers[CURRENT_LINE] = oldLine;
                            final Set<Boundary> boundaries = new HashSet<Boundary>();
                            for(int i=0; i< buffers[LAST_LINE].length; i++){
                                System.err.println("> " + i + " " + y +" " + buffers[LAST_LINE][i].toStringFull());
                                boundaries.add(buffers[LAST_LINE][i]);
                            }
                            for(final Boundary b : boundaries){
                                b.nextLine();
                            }

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

        System.err.println("packing");
        final Geometry[] polygones = new Geometry[ranges.length];
        for (int i=0; i<ranges.length; i++) {
            final NumberRange range = ranges[i];
            final MultiPolygon union = GF.createMultiPolygon(polygons.get(range).toArray(new Polygon[0]));
            union.setUserData(range);
            polygones[i] = union;
        }

        return polygones;
    }

    private void append(Point point, Number value) {
        System.err.println("POINT["+point+"] value = " + value);

        for (final NumberRange range : polygons.keySet()) {

            if (range.contains(value)) {
                if (lastNumberRange == range) {
                    //last pixel was in the same range
                    endX = point.x;
                    return;
                } else if (lastNumberRange != null) {
                    //last pixel was in a different range, save it's geometry
                    constructBlock();
                }

                //start a pixel serie
                lastNumberRange = range;
                startX = point.x;
                endX = point.x;
                y = point.y;

                return;
            }
        }

        throw new IllegalArgumentException("Value not in any range :" + value);
    }

    private void constructBlock(){

        System.err.println("BLOCK ["+startX+","+endX+"]");

        if(y == 0){
            //first line, the buffer is empty, must fill it
            final Boundary boundary = new Boundary(lastNumberRange);
            boundary.start(startX, endX+1);

            System.err.println("fill from " + startX +" to " + endX);
            for(int i=startX; i<=endX; i++){
                buffers[CURRENT_LINE][i] = boundary;
            }
        }else{
            Boundary currentBoundary = null;

            //first pass to close unfriendly blocks ----------------------------
            for(int i=startX; i<=endX;){
                final Boundary candidate = buffers[LAST_LINE][i];
                final int[] candidateExtent = findExtent(i);
                
                //do not treat same blockes here
                if(candidate.range != lastNumberRange){
                    System.err.println("A different block extent : "+ candidateExtent[0] + " " + candidateExtent[1]);
                    if(endX < candidateExtent[1]){
                        //the next block will take care of it
                    }else{
                        expendBlock(candidate, i, candidateExtent[1]+1);
                    }

                    System.err.println(candidate);
                    
                }
                
                i = candidateExtent[1]+1;
            }

            //second pass to fuse with friendly blocks -------------------------

            //we first merge the last line boundary if needed
            int firstAnchor = Integer.MAX_VALUE;
            int lastAnchor = Integer.MIN_VALUE;
            for(int i=startX; i<=endX; ){
                final Boundary bnd = buffers[LAST_LINE][i];
                final int[] candidateExtent = findExtent(i);

                //do not treat different blocks here
                if(bnd.range == lastNumberRange){
                
                    if(currentBoundary == null){
                        //set the current boundary, will expend this one
                        currentBoundary = bnd;
                    }else if(currentBoundary != null && currentBoundary != bnd){
                        //those two blocks doesnt belong to the same boundaries, we must merge them
                        currentBoundary.merge(bnd, lastAnchor, i);


                        System.out.println("Merging : " + currentBoundary.toString());
                    }


                    System.err.println(currentBoundary);

                    //same range
                    System.err.println("A friendly block  : " + candidateExtent[0] + " " + candidateExtent[1]);

                    if(candidateExtent[0] < firstAnchor){
                        firstAnchor = candidateExtent[0];
                    }
                    lastAnchor = candidateExtent[1]+1;
                }

                i = candidateExtent[1]+1;
            }

            if(currentBoundary == null){
                //no previous friendly boundary to link with
                //make a new one
                currentBoundary = new Boundary(lastNumberRange,y);
                currentBoundary.start(startX, endX+1);
            }else{
                //add the coordinates
                System.err.println("first anchor : " +firstAnchor + " lastAnchor : " +lastAnchor);
                currentBoundary.add(firstAnchor, startX);
                currentBoundary.add(lastAnchor, endX+1);
            }


            //fill in the current line -----------------------------------------
            

            for(int i=startX; i<=endX; i++){
                buffers[CURRENT_LINE][i] = currentBoundary;
            }



        }

        final Polygon pixel = toPolygon(startX, endX, y);
        polygons.get(lastNumberRange).add(pixel);
    }

    //unfriendly block
    private void expendBlock(Boundary candidate, int start, int end){
        final Polygon poly = candidate.expend(start, end);

        if(poly != null){
            System.err.println("FINISHED BLOCK  range : "+ candidate.range +" " + poly);
            polygons.get(candidate.range).add(poly);
        }
    }

    private int[] findExtent(int index){
        final int[] extent = new int[]{index,index};
        final Boundary bnd = buffers[LAST_LINE][index];

        while(extent[0] > 0 && buffers[LAST_LINE][ extent[0]-1 ] == bnd){
            extent[0]--;
        }

        while(extent[1] < buffers[LAST_LINE].length-1 && buffers[LAST_LINE][ extent[1]+1 ] == bnd){
            extent[1]++;
        }

        return extent;
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
