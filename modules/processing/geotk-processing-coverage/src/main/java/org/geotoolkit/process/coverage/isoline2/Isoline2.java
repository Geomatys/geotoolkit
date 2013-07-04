/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.process.coverage.isoline2;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.apache.sis.storage.DataStoreException;

import org.geotoolkit.coverage.*;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.memory.MemoryFeatureStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import static org.geotoolkit.parameter.Parameters.*;
import static org.geotoolkit.process.coverage.isoline2.IsolineDescriptor2.*;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.metadata.spatial.PixelOrientation;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class Isoline2 extends AbstractProcess {

    private static final Logger LOGGER = Logging.getLogger(Isoline2.class);
    private static final GeometryFactory GF = new GeometryFactory();
    
    //iteration informations
    private CoordinateReferenceSystem crs;
    private MathTransform gridtoCRS;
    private FeatureType type;
    private FeatureCollection col;
    private double[] intervals;
    
    //previous triangles informations
    private Boundary[][] line0TopNeighbor; // [level][X] for previous line
    private Boundary[][] line1TopNeighbor; // [level][X] for current line
    private Boundary[] leftNeighbor; // [Level] for current line
    
    //current pixel box
    private final Coordinate UL = new Coordinate();
    private final Coordinate UR = new Coordinate();
    private final Coordinate BL = new Coordinate();
    private final Coordinate BR = new Coordinate();
                        
    
    public Isoline2(ProcessDescriptor desc, ParameterValueGroup input) {
        super(desc, input);
    }

    @Override
    protected void execute() throws ProcessException {
        final CoverageReference coverageRef = value(COVERAGE_REF, inputParameters);
        FeatureStore featureStore = value(FEATURE_STORE, inputParameters);
        final String featureTypeName = value(FEATURE_NAME, inputParameters);
        intervals = value(INTERVALS, inputParameters);
        
        if(featureStore==null){
            featureStore = new MemoryFeatureStore();
        }
        
        try{
            final GridCoverageReader reader = coverageRef.createReader();
            final GridCoverage2D coverage = (GridCoverage2D) reader.read(coverageRef.getImageIndex(), null);
            final RenderedImage image = coverage.getRenderedImage();
            final PixelIterator ite = PixelIteratorFactory.createDefaultIterator(image);
            crs = coverage.getCoordinateReferenceSystem();
            gridtoCRS = coverage.getGridGeometry().getGridToCRS(PixelOrientation.UPPER_LEFT);
            type = getOrCreateIsoType(featureStore, featureTypeName, coverage.getCoordinateReferenceSystem());
            col = featureStore.createSession(false).getFeatureCollection(QueryBuilder.all(type.getName()));
            
            //line buffer
            final int height = image.getHeight();
            final int width = image.getWidth();
            leftNeighbor = new Boundary[intervals.length];
            line0TopNeighbor = new Boundary[intervals.length][width];
            line1TopNeighbor = new Boundary[intervals.length][width];
            double[] line0 = new double[image.getWidth()];
            double[] line1 = new double[image.getWidth()];
            
            for(int y=0;y<height;y++){
                for(int x=0;x<width;x++){
                    ite.next();
                    line1[x] = ite.getSampleDouble();
                    
                    //calculate lines
                    if(y>0 && x>0){
                        //set the 4 corner values
                        UL.x = x-1;  UL.y = y-1;  UL.z = line0[x-1];
                        UR.x = x  ;  UR.y = y-1;  UR.z = line0[x  ];
                        BL.x = x-1;  BL.y = y  ;  BL.z = line1[x-1];
                        BR.x = x  ;  BR.y = y  ;  BR.z = line1[x  ];
                        
                        for(int k=0;k<intervals.length;k++){
                            final double level = intervals[k];
                            final Boundary nb = buildTriangles(k,level,line0TopNeighbor[k][x], leftNeighbor[k]);
                            //the created boundary is the left boundary of next pixel
                            //and the top boundary of underneath pixel
                            leftNeighbor[k] = nb;
                            line1TopNeighbor[k][x] = nb;
                        }
                    }
                }
                
                if(y>0){
                    //filter the constructions which are not used
                    final Set<Construction> oldinconstructions = new HashSet<Construction>();
                    final Set<Construction> newinconstructions = new HashSet<Construction>();
                    for(int x=1;x<width;x++){
                        for(int k=0;k<intervals.length;k++){
                            if(line0TopNeighbor[k][x] != null){
                                line0TopNeighbor[k][x].getConstructions(oldinconstructions);
                            }
                            line1TopNeighbor[k][x].getConstructions(newinconstructions);
                        }
                    }
                    oldinconstructions.removeAll(newinconstructions);

                    //push in the feature collection geometries which are not used anymore
                    for(final Construction str : oldinconstructions){
                        pushGeometry(str.toGeometry(), str.getLevel());
                    }
                }
                
                //swap lines
                final double[] templine = line0;
                line0 = line1;
                line1 = templine;
                final Boundary[][] tempbound = line0TopNeighbor;
                line0TopNeighbor = line1TopNeighbor;
                line1TopNeighbor = tempbound;
                leftNeighbor[0] = null;
                
            }
            
            //loop on the last line to push the remaining geometries
            final Set<Construction> oldinconstructions = new HashSet<Construction>();
            for(int x=1;x<width;x++){
                for(int k=0;k<intervals.length;k++){
                    line0TopNeighbor[k][x].getConstructions(oldinconstructions);
                }
            }
            for(final Construction str : oldinconstructions){
                pushGeometry(str.toGeometry(), str.getLevel());
            }
            
            
        }catch(Exception ex){
            throw new ProcessException(ex.getMessage(), this, ex);
        }
        
        System.out.println(">>>>>>>>>>>>>>>>> "+col.size());
        
        outputParameters.parameter("outFeatureCollection").setValue(col);
    }
        
    private void update(Construction cst, int k){
        for(int x=0;x<line0TopNeighbor[0].length;x++){
            cst.update(line0TopNeighbor[k][x]);
            cst.update(line1TopNeighbor[k][x]);
        }
    }
    
    
    private Boundary buildTriangles(final int k, final double level, final Boundary top, final Boundary left) 
            throws MismatchedDimensionException, TransformException{
        
        //the new triangle
        final Boundary newBoundary = new Boundary();
        
        final boolean ulCorner = (UL.z == level);
        final boolean urCorner = (UR.z == level);
        final boolean blCorner = (BL.z == level);
        final boolean brCorner = (BR.z == level);
        final Coordinate crossUp = interpolate(level, UL, UR);
        final Coordinate crossLf = interpolate(level, UL, BL);
        final Coordinate crossHp = interpolate(level, UR, BL);
        final Coordinate crossBt = interpolate(level, BL, BR);
        final Coordinate crossRi = interpolate(level, UR, BR);
        
        // FIRST TRIANGLE //////////////////////////////////////////////////////
        Construction.Edge SBottom = null;
        Construction.Edge SMiddle = null;
        Construction.Edge STop = null;
        
        if(top!=null && left!=null){ //-----------------------------------------
            //pixel is somewhere in the image 
            
            if(top.HLeft!=null){
//                if(urCorner){
//                    STop = top.HLeft;
//                    STop.add(UR);
//                }else 
                if(crossHp!=null){
                    SMiddle = top.HLeft;
                    SMiddle.add(crossHp);
                }
//                else if(left.VBottom!=null){
//                    SBottom = top.HLeft;
//                    SBottom.add(BL);
//                }
            }else if(top.HRight!=null){
                if(left.VMiddle!=null){
                    top.HRight.add(crossLf);
                    top.HRight.getConstruction().merge(left.VMiddle.getConstruction());
                    update(top.HRight.getConstruction(),k);
                }else{
                    STop = top.HRight;
                }
            }else if(top.HMiddle!=null){
                if(crossHp!=null){
                    if(SMiddle!=null) throw new RuntimeException("Logic error, SMiddle should not be set");
                    SMiddle = top.HMiddle;
                    SMiddle.add(crossHp);
                }else if(left.VMiddle!=null){
                    top.HMiddle.add(crossLf);
                    top.HMiddle.getConstruction().merge(left.VMiddle.getConstruction());
                    update(top.HMiddle.getConstruction(),k);
                }else if(left.VBottom!=null){
//                    if(SBottom!=null) throw new RuntimeException("Logic error, SBottom should not be set");
//                    top.HMiddle.add(BL);
//                    top.HMiddle.getConstruction().merge(left.VBottom.getConstruction());
//                    update(top.HMiddle.getConstruction(),k);
//                    SBottom = top.HMiddle;
                }
            }
            
            if(left.VTop!=null){
                if(crossHp!=null && SMiddle==null){
                    if(SMiddle!=null) throw new RuntimeException("Logic error, SMiddle should not be set");
                    SMiddle = left.VTop;
                    SMiddle.add(crossHp);
                }else if(top.HRight==null && urCorner){
                    if(STop!=null) throw new RuntimeException("Logic error, STop should not be set");
                    STop = left.VTop;
                    STop.add(UR);
                }
            }else if(left.VBottom!=null){
                SBottom = left.VBottom;
                
            }else if(left.VMiddle!=null){
                if(crossHp!=null){
                    if(SMiddle!=null) throw new RuntimeException("Logic error, SMiddle should not be set");
                    SMiddle = left.VMiddle;
                    SMiddle.add(crossHp);
                }
                //don't test already done above
                //else if(crossUp!=null){
                //    left.VMiddle.add(crossUp);
                //}
            } 
            
            
        }else if(top!=null){ //-------------------------------------------------
            //pixel is on the left image border
//            if(top.HLeft!=null){
//                if(urCorner){
//                    STop = top.HLeft;
//                    STop.add(UR);
//                }else if(crossHp!=null){
//                    SMiddle = top.HLeft;
//                    SMiddle.add(crossHp);
//                }else if(blCorner){
//                    SBottom = top.HLeft;
//                    SBottom.add(BL);
//                }
//            }else if(top.HRight!=null){
//                if(crossLf!=null){
//                    top.HRight.add(crossLf);
//                }else{
//                    STop = top.HRight;
//                }
//            }else 
                if(top.HMiddle!=null){
                if(crossHp!=null){
                    if(SMiddle!=null) throw new RuntimeException("Logic error, SMiddle should not be set");
                    SMiddle = top.HMiddle;
                    SMiddle.add(crossHp);
                }else if(crossLf!=null){
                    top.HMiddle.add(crossLf);
                }else if(blCorner){
                    SBottom = top.HMiddle;
                    SBottom.add(BL);
                }
            }
            
            if(crossLf!=null && crossHp!=null){
                if(SMiddle!=null) throw new RuntimeException("Logic error, SMiddle should not be set");
                final Construction cst = new Construction(level);
                SMiddle = cst.getEdge1();
                SMiddle.add(crossLf);
                SMiddle.add(crossHp);
            }            
            
        }else if(left!=null){ //------------------------------------------------
            //pixel is on the top image border
//            if(left.VTop != null){
//                if(urCorner){
//                    STop = left.VTop;
//                    STop.add(UR);
//                }else if(crossHp!=null){
//                    SMiddle = left.VTop;
//                    SMiddle.add(crossHp);
//                }else if(blCorner){
//                    SBottom = left.VTop;
//                    SBottom.add(BL);
//                }
//            }else if(left.VBottom != null){
//                if(crossUp!=null){
//                    left.VBottom.add(crossUp);
//                }else{
//                    SBottom = left.VBottom;
//                }
//            }else 
                if(left.VMiddle!=null){
                if(crossHp!=null){
                    if(SMiddle!=null) throw new RuntimeException("Logic error, SMiddle should not be set");
                    SMiddle = left.VMiddle;
                    SMiddle.add(crossHp);
                }else if(crossUp!=null){
                    left.VMiddle.add(crossUp);
                }
            }
            
            if(crossUp!=null && crossHp!=null){
                if(SMiddle!=null) throw new RuntimeException("Logic error, SMiddle should not be set");
                final Construction cst = new Construction(level);
                SMiddle = cst.getEdge1();
                SMiddle.add(crossUp);
                SMiddle.add(crossHp);
            }
            
        }else{ //---------------------------------------------------------------
            //pixel is on the top left image corner
            
            //on border cases
            if(ulCorner && urCorner && blCorner){
                //close triangle
                final Construction cst = new Construction(level);
                STop = cst.getEdge1();
                STop.add(BL);
                STop.add(UL);
                STop.add(UR);
                SBottom = cst.getEdge2();
                
            }else if(ulCorner && urCorner){
                //adjacent border
                final Construction cst = new Construction(level);
                STop = cst.getEdge1();
                STop.add(UL);
                STop.add(UR);
                
            }else if(ulCorner && blCorner){
                //opposite border
                final Construction cst = new Construction(level);
                SBottom = cst.getEdge1();
                SBottom.add(UL);
                SBottom.add(BL);
                
            }else if(urCorner && blCorner){
                //hypothenus border
                final Construction cst = new Construction(level);
                STop = cst.getEdge1();
                STop.add(BL);
                STop.add(UR);
                SBottom = cst.getEdge2();
            }

            //split on a height
            if(ulCorner && crossHp!=null){
                if(SMiddle!=null) throw new RuntimeException("Logic error, SMiddle should not be set");
                final Construction cst = new Construction(level);
                SMiddle = cst.getEdge1();
                SMiddle.add(UL);
                SMiddle.add(crossHp);
            }else if(urCorner && crossLf!=null){
                if(STop!=null) throw new RuntimeException("Logic error, STop should not be set");
                final Construction cst = new Construction(level);
                STop = cst.getEdge1();
                STop.add(crossLf);
                STop.add(UR);
            }else if(blCorner && crossUp!=null){
                if(SBottom!=null) throw new RuntimeException("Logic error, SBottom should not be set");
                final Construction cst = new Construction(level);
                SBottom = cst.getEdge1();
                SBottom.add(crossUp);
                SBottom.add(BL);
            }
            
            //split on 2 edges
            else if(crossUp!=null && crossHp!=null){
                if(SMiddle!=null) throw new RuntimeException("Logic error, SMiddle should not be set");
                final Construction cst = new Construction(level);
                SMiddle = cst.getEdge1();
                SMiddle.add(crossUp);
                SMiddle.add(crossHp);
            }else if(crossLf!=null && crossHp!=null){
                if(SMiddle!=null) throw new RuntimeException("Logic error, SMiddle should not be set");
                final Construction cst = new Construction(level);
                SMiddle = cst.getEdge1();
                SMiddle.add(crossLf);
                SMiddle.add(crossHp);
            }else if(crossUp!=null && crossLf!=null){
                //only case where we can push the geometry directly
                final Geometry geom = GF.createLineString(new Coordinate[]{crossUp, crossLf});
                pushGeometry(geom, level);
            }
        }
        
        
        //algotihm check, can not have all S set
        if(SMiddle!=null && (STop!=null || SBottom!=null)){
            throw new RuntimeException("Logic error, Muplite S set");
        }
        
        
        //SECOND TRIANGLE //////////////////////////////////////////////////////
        
        if(SMiddle != null){
            //continue existing lines
            if(crossBt != null){
                newBoundary.HMiddle = SMiddle;
                newBoundary.HMiddle.add(crossBt);
            }else if(crossRi != null){
                newBoundary.VMiddle = SMiddle;
                newBoundary.VMiddle.add(crossRi);
            }else if(brCorner){
                newBoundary.VBottom = SMiddle;
                newBoundary.VBottom.add(BR);
                //duplicate line here, we might have a fork
                Construction cst = new Construction(level);
                newBoundary.HRight = cst.getEdge1();
                newBoundary.HRight.add(BR);
            }
            
        }
        
        if(STop != null){
            if(crossBt!=null){
                //propage top limit
                newBoundary.VTop = STop;
                                
                //duplicate line here, we might have a fork
                Construction cst = new Construction(level);
                newBoundary.HMiddle = cst.getEdge1();
                newBoundary.HMiddle.add(UR);
                newBoundary.HMiddle.add(crossBt);
                
            }else{
                //propage top limit
                newBoundary.VTop = STop;
            }
        }
        if(SBottom != null){
            if(crossRi!=null){
                newBoundary.VMiddle = SBottom;
                newBoundary.VMiddle.add(crossRi);
                
                //propage bottom limit
                //duplicate line here, we might have a fork
                Construction cst = new Construction(level);
                newBoundary.HLeft = cst.getEdge1();
                newBoundary.HLeft.add(BL);
            }else{
                //propage bottom limit
                newBoundary.HLeft = SBottom;
            }
        }
        
        if(newBoundary.VMiddle==null && newBoundary.HMiddle==null){
            if(crossBt != null && crossRi != null){
                //create new lines 
                final Construction cst = new Construction(level);
                newBoundary.VMiddle = cst.getEdge1();
                newBoundary.VMiddle.add(crossBt);
                newBoundary.VMiddle.add(crossRi);
                newBoundary.HMiddle = cst.getEdge2();
            }
        }
        
        //check for singular segments (edges)
        if(newBoundary.HRight == null && brCorner && newBoundary.HLeft == null){
            //create a single point
            final Construction cst = new Construction(level);
            newBoundary.HRight = cst.getEdge1();
            newBoundary.HRight.add(BR);
            newBoundary.VBottom = cst.getEdge2();
        }
        
        
        //algotihm check, can not have all S set
        if(newBoundary.HMiddle!=null && (newBoundary.HLeft!=null || newBoundary.HRight!=null)){
            throw new RuntimeException("Logic error, Muplite H set");
        }
        if(newBoundary.VMiddle!=null && (newBoundary.VTop!=null || newBoundary.VBottom!=null)){
            throw new RuntimeException("Logic error, Muplite V set top="+newBoundary.VTop+" bottom="+newBoundary.VBottom);
        }
        
        return newBoundary;
    }
    
    private void pushGeometry(Geometry geom, double level) throws MismatchedDimensionException, TransformException{
        if(geom==null) return;
        final Feature f = FeatureUtilities.defaultFeature(type, "0");
        geom = JTS.transform(geom, gridtoCRS);
        JTS.setCRS(geom, crs);
        f.getProperty("geometry").setValue(geom);
        f.getProperty("level").setValue(level);
        
        col.add(f);
    }
        
    private static Coordinate interpolate(double candidate, Coordinate start, Coordinate end){
        if(start.z<candidate && candidate<end.z){
            double ratio = (candidate-start.z) / (end.z-start.z);
            return new Coordinate(
                    start.x + (end.x-start.x)*ratio, 
                    start.y + (end.y-start.y)*ratio,
                    candidate);
        }else if(start.z>candidate && candidate>end.z){
            double ratio = (candidate-end.z) / (start.z-end.z);
            return new Coordinate(
                    end.x + (start.x-end.x)*ratio, 
                    end.y + (start.y-end.y)*ratio,
                    candidate);
        }
        return null;
    }
    
    private static FeatureType getOrCreateIsoType(FeatureStore featureStore, String featureTypeName, CoordinateReferenceSystem crs) throws DataStoreException {
        //FeatureType with scale
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(featureTypeName != null ? featureTypeName : "isolines");
        ftb.add("geometry", LineString.class, crs);
        ftb.add("level", Double.class);
        ftb.setDefaultGeometry("geometry");
        FeatureType type = ftb.buildFeatureType();

        //create FeatureType in FeatureStore if not exist
        boolean createSchema = false;
        try {
            if (featureStore.getFeatureType(type.getName()) == null) {
                createSchema = true;
            }
        } catch (DataStoreException ex) {
            createSchema = true;
        }

        if (createSchema) {
            featureStore.createFeatureType(type.getName(), type);
        }

        type = featureStore.getFeatureType(type.getName());

        return type;
    }
}