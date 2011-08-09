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
package org.geotoolkit.process.coverage.coveragetovector;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.Point;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.media.jai.iterator.RectIter;
import javax.media.jai.iterator.RectIterFactory;

import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.ViewType;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.util.NumberRange;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;

/**
 * Process to extract Polygon from a coverage.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class CoverageToVectorProcess extends AbstractProcess {

    private static final GeometryFactory GF = new GeometryFactory();
    private static final int LAST_LINE = 0;
    private static final int CURRENT_LINE = 1;

    //last line cache boundary
    private final Map<NumberRange, List<Polygon>> polygons = new HashMap<NumberRange, List<Polygon>>();

    //buffer[0] holds last line buffer
    //buffer[1] holds current line buffer
    private Boundary[][] buffers;

    //current pixel block
    private final Block block = new Block();

    CoverageToVectorProcess(final ParameterValueGroup input) {
        super(CoverageToVectorDescriptor.INSTANCE,input);
    }

    public Geometry[] toPolygon(GridCoverage2D coverage, final NumberRange[] ranges, final int band)
            throws IOException, TransformException {
        coverage = coverage.view(ViewType.GEOPHYSICS);

        //add a range for Nan values.
        NumberRange NaNRange = new NaNRange();
        polygons.put(NaNRange, new ArrayList<Polygon>());

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
                //System.err.println("bande " + bandNum);

                if (bandNum == band) {
                    //iteration over lines
                    iter.startLines();
                    if (!iter.finishedLines()) {
                        do {

                            //System.err.println("ligne " + gridPosition.y);
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
//                            final Set<Boundary> boundaries = new HashSet<Boundary>();
//                            //System.err.println("--------------------------------------------------------------------------------------------");
//                            for(int i=0; i< buffers[LAST_LINE].length; i++){
//                                //System.err.println("> " + i + " " + block.y +" " + buffers[LAST_LINE][i].toString());
//                                boundaries.add(buffers[LAST_LINE][i]);
//                            }

                            block.reset();

                            gridPosition.x = 0;
                            gridPosition.y += 1;
                        } while (!iter.nextLineDone());
                    }

                    //we have finish the requested band, close all geometries
                    for(int i=0;i<buffers[LAST_LINE].length;i++){
                        Polygon poly = buffers[LAST_LINE][i].link(
                                new Coordinate(i, gridPosition.y),
                                new Coordinate(i+1, gridPosition.y)
                                );
                        if(poly != null){
                            polygons.get(buffers[LAST_LINE][i].range).add(poly);
                        }
                    }

                }

                gridPosition.x = 0;
                gridPosition.y = 0;
            } while (!iter.nextBandDone());
        }

        final List<Geometry> polygones = new ArrayList<Geometry>();
        for (int i=0; i<ranges.length; i++) {
            final NumberRange range = ranges[i];
            for(Polygon poly : polygons.get(range)){
                polygones.add(JTS.transform(poly, gridToCRS));
            }
            //we dont merge them in a single polygon to avoid to complexe geometries
        }

        return polygones.toArray(new Polygon[polygones.size()]);
    }

    private void append(final Point point, Number value) {
        //System.err.println("POINT["+point+"] value = " + value);

        //special case for NaN or null
        //todo


        for (final NumberRange range : polygons.keySet()) {

            if (range.contains(value)) {
                if (block.range == range) {
                    //last pixel was in the same range
                    block.endX = point.x;
                    return;
                } else if (block.range != null) {
                    //last pixel was in a different range, save it's geometry
                    constructBlock();
                }

                //start a pixel serie
                block.range = range;
                block.startX = point.x;
                block.endX = point.x;
                block.y = point.y;

                return;
            }
        }

        throw new IllegalArgumentException("Value not in any range :" + value);
    }

    private void constructBlock(){

        //System.err.println("BLOCK ["+block.startX+","+block.endX+"]");

        if(block.y == 0){
            //first line, the buffer is empty, must fill it
            final Boundary boundary = new Boundary(block.range);
            boundary.start(block.startX, block.endX+1, block.y);

            for(int i=block.startX; i<=block.endX; i++){
                buffers[CURRENT_LINE][i] = boundary;
            }
        }else{
            Boundary currentBoundary = null;

            //first pass to close unfriendly blocks ----------------------------
            for(int i=block.startX; i<=block.endX;){
                final Boundary candidate = buffers[LAST_LINE][i];
                final int[] candidateExtent = findExtent(i);
                
                //do not treat same blockes here
                if(candidate.range != block.range){
                    //System.err.println("A different block extent : "+ candidateExtent[0] + " " + candidateExtent[1]);
                    //System.err.println("before :" + candidate.toString());

                    if(candidateExtent[0] >= block.startX && candidateExtent[1] <= block.endX){
                        //block overlaps completly candidate
                        final Polygon poly = candidate.link(
                                new Coordinate(candidateExtent[0], block.y),
                                new Coordinate(candidateExtent[1]+1, block.y)
                                );
                        if(poly != null) polygons.get(candidate.range).add(poly);
                    }else{
                        final Polygon poly = candidate.link(
                                new Coordinate( (block.startX<candidateExtent[0]) ? candidateExtent[0]: block.startX, block.y),
                                new Coordinate( (block.endX>candidateExtent[1]) ? candidateExtent[1]+1: block.endX+1, block.y)
                                );
                        if(poly != null) polygons.get(candidate.range).add(poly);
                    }

                    //System.err.println("after :" + candidate.toString());
                }
                
                i = candidateExtent[1]+1;
            }

            //second pass to fuse with friendly blocks -------------------------

            //we first merge the last line boundary if needed
            int firstAnchor = Integer.MAX_VALUE;
            int lastAnchor = Integer.MIN_VALUE;

            for(int i=block.startX; i<=block.endX; ){
                final Boundary candidate = buffers[LAST_LINE][i];
                final int[] candidateExtent = findExtent(i);

                //do not treat different blocks here
                if(candidate.range == block.range){
                    //System.err.println("A firnet block extent : "+ candidateExtent[0] + " " + candidateExtent[1]);
//                    //System.err.println("before :" + candidate.toString());
                
                    if(currentBoundary == null){
                        //set the current boundary, will expend this one
                        currentBoundary = candidate;
                    }else if(currentBoundary != null){
                        if(currentBoundary != candidate){
                            //those two blocks doesnt belong to the same boundaries, we must merge them
                            currentBoundary.merge(candidate);
                        }
                        currentBoundary.link(
                            new Coordinate(lastAnchor, block.y),
                            new Coordinate(candidateExtent[0], block.y)
                            );

                        replaceInLastLigne(candidate, currentBoundary);
                        //System.out.println("Merging : " + currentBoundary.toString());
                    }

                    if(candidateExtent[0] < firstAnchor){
                        firstAnchor = candidateExtent[0];
                    }
                    lastAnchor = candidateExtent[1]+1;
                }

                i = candidateExtent[1]+1;
            }

            if(currentBoundary != null){
                //System.err.println("before :" + currentBoundary.toString());
            }

            if(currentBoundary == null){
                //no previous friendly boundary to link with
                //make a new one
                currentBoundary = new Boundary(block.range);
                currentBoundary.start(block.startX, block.endX+1, block.y);
            }else{
                if(firstAnchor < block.startX){
                    //the previous block has created a floating sequence to this end
                    firstAnchor = block.startX;
                }

                //add the coordinates
                //System.err.println("> first anchor : " +firstAnchor + " lastAnchor : " +lastAnchor);
                if(firstAnchor == block.startX){
                    currentBoundary.add(
                        new Coordinate(firstAnchor, block.y),
                        new Coordinate(block.startX, block.y+1)
                        );
                }else{
                    currentBoundary.add(
                        new Coordinate(firstAnchor, block.y),
                        new Coordinate(block.startX, block.y)
                        );
                    currentBoundary.add(
                        new Coordinate(block.startX, block.y),
                        new Coordinate(block.startX, block.y+1)
                        );
                }

                if(block.endX+1 >= lastAnchor){
                    if(lastAnchor == block.endX+1){
                        currentBoundary.add(
                            new Coordinate(lastAnchor, block.y),
                            new Coordinate(block.endX+1, block.y+1)
                            );
                    }else{
                        //System.err.println("0 add :" + currentBoundary.toString());
                        currentBoundary.add(
                            new Coordinate(lastAnchor, block.y),
                            new Coordinate(block.endX+1, block.y)
                            );
                        //System.err.println("1 add:" + currentBoundary.toString());
                        currentBoundary.add(
                            new Coordinate(block.endX+1, block.y),
                            new Coordinate(block.endX+1, block.y+1)
                            );
                        //System.err.println("after add:" + currentBoundary.toString());
                    }
                }else{
                    currentBoundary.addFloating(
                            new Coordinate(block.endX+1, block.y),
                            new Coordinate(block.endX+1, block.y+1)
                            );
                }

                //System.err.println(currentBoundary.toString());
                
            }

            //System.err.println("after :" + currentBoundary.toString());
            
            //fill in the current line -----------------------------------------
            
            for(int i=block.startX; i<=block.endX; i++){
                if(currentBoundary.isEmpty()){
                    throw new IllegalArgumentException("An empty boundary inserted ? not possible.");
                }

                buffers[CURRENT_LINE][i] = currentBoundary;
            }

        }

    }

    private void replaceInLastLigne(final Boundary old, final Boundary newone){
        for(int i=0,n=buffers[LAST_LINE].length; i<n; i++){
            if(buffers[LAST_LINE][i] == old){
                buffers[LAST_LINE][i] = newone;
            }

            if(buffers[CURRENT_LINE][i] == old){
                buffers[CURRENT_LINE][i] = newone;
            }
        }
    }


    private int[] findExtent(final int index){
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
    public ParameterValueGroup call() {
        if (inputParameters == null) {
            fireFailEvent(new ProcessEvent(this,
                    "Input parameters not set.",0,
                    new NullPointerException("Input parameters not set.")));
        }

        final GridCoverage2D coverage = (GridCoverage2D) inputParameters.parameter(CoverageToVectorDescriptor.COVERAGE.getName().getCode()).getValue();
        final NumberRange[] ranges = (NumberRange[]) inputParameters.parameter(CoverageToVectorDescriptor.RANGES.getName().getCode()).getValue();
        Integer band = (Integer) inputParameters.parameter(CoverageToVectorDescriptor.BAND.getName().getCode()).getValue();
        if(band == null){
            band = 0;
        }

        Geometry[] result = null;
        try {
            result = toPolygon(coverage, ranges, 0);
        } catch (IOException ex) {
            fireFailEvent(new ProcessEvent(this, null,0, ex));
        } catch (TransformException ex) {
            fireFailEvent(new ProcessEvent(this, null,0, ex));
        }
        
        //avoid memory use
        buffers = null;
        polygons.clear();
        
        outputParameters.parameter(CoverageToVectorDescriptor.GEOMETRIES.getName().getCode()).setValue(result);

        fireEndEvent(new ProcessEvent(this));

        return outputParameters;
    }

    private static class NaNRange extends NumberRange{

        public NaNRange(){
            super(Number.class,0,0);
        }

        @Override
        public boolean contains(final Number number) throws IllegalArgumentException {
            return Double.isNaN(number.doubleValue());
        }

    }

}
