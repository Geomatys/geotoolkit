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
        final double[] intervals = value(INTERVALS, inputParameters);
        
        if(featureStore==null){
            featureStore = new MemoryFeatureStore();
        }
        
        try{
            final GridCoverageReader reader = coverageRef.createReader();
            final GridCoverage2D coverage = (GridCoverage2D) reader.read(coverageRef.getImageIndex(), null);
            final RenderedImage image = coverage.getRenderedImage();
            final PixelIterator ite = PixelIteratorFactory.createDefaultIterator(image);
            crs = coverage.getCoordinateReferenceSystem();
            gridtoCRS = coverage.getGridGeometry().getGridToCRS();
            type = getOrCreateIsoType(featureStore, featureTypeName, coverage.getCoordinateReferenceSystem());
            col = featureStore.createSession(false).getFeatureCollection(QueryBuilder.all(type.getName()));
            
            //line buffer
            final int height = image.getHeight();
            final int width = image.getWidth();
            double[] line0 = new double[image.getWidth()];
            double[] line1 = new double[image.getWidth()];
            
            for(int y=0;y<height;y++){
                for(int x=0;x<width;x++){
                    ite.next();
                    line1[x] = ite.getSampleDouble();
                    
                    //calculate lines
                    if(y>0 && x>0){
                        UL.x = x-1;  UL.y = y-1;  UL.z = line0[x-1];
                        UR.x = x  ;  UR.y = y-1;  UR.z = line0[x  ];
                        BL.x = x-1;  BL.y = y  ;  BL.z = line1[x-1];
                        BR.x = x  ;  BR.y = y  ;  BR.z = line1[x  ];
                        
                        for(double level : intervals){
                            buildTriangles(level);
                        }
                    }
                }
                
                //swap lines
                final double[] temp = line0;
                line0 = line1;
                line1 = temp;
            }
            
        }catch(Exception ex){
            throw new ProcessException(ex.getMessage(), this, ex);
        }
        
        outputParameters.parameter("outFeatureCollection").setValue(col);
    }
    
    private void buildTriangles(double level) throws MismatchedDimensionException, TransformException{
        
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
        Geometry geom = null;
        
        //on border cases
        if(ulCorner && urCorner && blCorner){
            //close triangle
            geom = GF.createLineString(new Coordinate[]{UL,UR,BL});
        }else if(ulCorner && urCorner){
            //adjacent border
            geom = GF.createLineString(new Coordinate[]{UL,UR});
        }else if(ulCorner && blCorner){
            //opposite border
            geom = GF.createLineString(new Coordinate[]{UL,BL});
        }else if(urCorner && blCorner){
            //hypothenus border
            geom = GF.createLineString(new Coordinate[]{UR,BL});
        }
        
        //split on a height
        else if(ulCorner && crossHp!=null){
            geom = GF.createLineString(new Coordinate[]{UL,crossHp});
        }else if(urCorner && crossLf!=null){
            geom = GF.createLineString(new Coordinate[]{UR,crossLf});
        }else if(blCorner && crossUp!=null){
            geom = GF.createLineString(new Coordinate[]{BL,crossUp});
        }
        
        //split on 2 edges
        else if(crossUp!=null && crossHp!=null){
            geom = GF.createLineString(new Coordinate[]{crossUp, crossHp});
        }else if(crossLf!=null && crossHp!=null){
            geom = GF.createLineString(new Coordinate[]{crossLf, crossHp});
        }else if(crossUp!=null && crossLf!=null){
            geom = GF.createLineString(new Coordinate[]{crossUp, crossLf});
        }
        
        if(geom!=null){
            pushGeometry(geom, level);
        }
        
        //SECOND TRIANGLE //////////////////////////////////////////////////////
        
        geom = null;
        
        //on border cases
        if(urCorner && blCorner && brCorner){
            //close triangle
            geom = GF.createLineString(new Coordinate[]{UR, BL, BR});
        }else if(brCorner && urCorner){
            //adjacent border
            geom = GF.createLineString(new Coordinate[]{BR,UR});
        }else if(brCorner && blCorner){
            //opposite border
            geom = GF.createLineString(new Coordinate[]{BR,BL});
        }else if(urCorner && blCorner){
            //hypothenus border
            geom = GF.createLineString(new Coordinate[]{UR,BL});
        }
        
        //split on a height
        else if(brCorner && crossHp!=null){
            geom = GF.createLineString(new Coordinate[]{BR,crossHp});
        }else if(urCorner && crossBt!=null){
            geom = GF.createLineString(new Coordinate[]{UR,crossBt});
        }else if(blCorner && crossRi!=null){
            geom = GF.createLineString(new Coordinate[]{BL,crossRi});
        }
        
        //split on 2 edges
        else if(crossRi!=null && crossHp!=null){
            geom = GF.createLineString(new Coordinate[]{crossRi, crossHp});
        }else if(crossBt!=null && crossHp!=null){
            geom = GF.createLineString(new Coordinate[]{crossBt, crossHp});
        }else if(crossRi!=null && crossBt!=null){
            geom = GF.createLineString(new Coordinate[]{crossRi, crossBt});
        }
        
        if(geom!=null){
            pushGeometry(geom, level);
        }
        
    }
    
    private void pushGeometry(Geometry geom, double level) throws MismatchedDimensionException, TransformException{
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
                    start.y + (end.y-start.y)*ratio);
        }else if(start.z>candidate && candidate>end.z){
            double ratio = (candidate-end.z) / (start.z-end.z);
            return new Coordinate(
                    end.x + (start.x-end.x)*ratio, 
                    end.y + (start.y-end.y)*ratio);
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