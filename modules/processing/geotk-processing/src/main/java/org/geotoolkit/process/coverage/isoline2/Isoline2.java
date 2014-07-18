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

import java.awt.Point;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.sis.storage.DataStoreException;

import org.geotoolkit.coverage.*;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureStore;
import org.geotoolkit.data.memory.MemoryFeatureStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.type.BasicFeatureTypes;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.image.iterator.PixelIterator;
import org.geotoolkit.image.iterator.PixelIteratorFactory;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.metadata.iso.spatial.PixelTranslation;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import static org.geotoolkit.parameter.Parameters.*;
import static org.geotoolkit.process.coverage.isoline2.IsolineDescriptor2.*;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @module pending
 */
public class Isoline2 extends AbstractProcess {

    private static final Logger LOGGER = Logging.getLogger(Isoline2.class);
    private static final GeometryFactory GF = new GeometryFactory();
    private static final boolean DEBUG = false;

    //iteration informations
    private CoordinateReferenceSystem crs;
    private FeatureType type;
    private FeatureCollection col;
    private double[] intervals;

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
            final int imgIndex = coverageRef.getImageIndex();
            final GridCoverageReader reader  = coverageRef.acquireReader();
            final GeneralGridGeometry gridgeom = reader.getGridGeometry(imgIndex);
            crs = gridgeom.getCoordinateReferenceSystem();
            type = getOrCreateIsoType(featureStore, featureTypeName, crs);
            col = featureStore.createSession(false).getFeatureCollection(QueryBuilder.all(type.getName()));

            if (coverageRef instanceof PyramidalCoverageReference) {
                final PyramidalCoverageReference pm = (PyramidalCoverageReference) coverageRef;
                computeIsolineFromPM(pm);

            } else {
                final MathTransform gridtoCRS = gridgeom.getGridToCRS(PixelInCell.CELL_CENTER);

                final Object obj = reader.getInput();
                final RenderedImage image;
                if(obj instanceof RenderedImage){
                    image = (RenderedImage)obj;
                }else if(obj instanceof ImageReader){
                    image = ((ImageReader)obj).read(imgIndex);
                } else if (obj instanceof ImageInputStream) {
                    final ImageReader imgReader = XImageIO.getReader(obj, false, false);
                    image = imgReader.read(imgIndex);
                } else{
                    final GridCoverage2D coverage = (GridCoverage2D) reader.read(coverageRef.getImageIndex(), null);
                    image = coverage.getRenderedImage();
                }

                final PixelIterator ite = PixelIteratorFactory.createDefaultIterator(image);
                final int width = image.getWidth();
                final int height = image.getHeight();

                final BlockRunnable runnable = new BlockRunnable(gridtoCRS, ite, width, height, 0);
                runnable.run();
            }
            coverageRef.recycle(reader);

        }catch(Exception ex){
            throw new ProcessException(ex.getMessage(), this, ex);
        }

        outputParameters.parameter("outFeatureCollection").setValue(col);
    }

    private void computeIsolineFromPM(PyramidalCoverageReference pm) throws DataStoreException, ProcessException, InterruptedException{

        final PyramidSet set = pm.getPyramidSet();
        final Collection<Pyramid> pyramids = set.getPyramids();

        final ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        for (Pyramid pyramid : pyramids) {

            //analyse mosaics in order to count all not missing tiles.
            int tiles = 0;
            for (final GridMosaic mosaic : pyramid.getMosaics()) {
                for (int y=0; y<mosaic.getGridSize().height; y++) {
                    for (int x=0; x<mosaic.getGridSize().width; x++) {
                        if ( !mosaic.isMissing(x,y)) tiles++;
                    }
                }
            }

            for (final GridMosaic mosaic : pyramid.getMosaics()) {
                final GridMosaicRenderedImage gridImage = new GridMosaicRenderedImage(mosaic);

                for (int y=0; y<gridImage.getNumYTiles(); y++) {
                    for (int x=0; x<gridImage.getNumXTiles(); x++) {
                        if (!mosaic.isMissing(x,y)) {
                            try{
                                final TileReference ref = mosaic.getTile(x, y, null);
                                MathTransform gridtoCRS = AbstractGridMosaic.getTileGridToCRS(mosaic, new Point(x, y));
                                gridtoCRS = PixelTranslation.translate(gridtoCRS, PixelInCell.CELL_CORNER, PixelInCell.CELL_CENTER);
                                final Object obj = ref.getInput();
                                final RenderedImage image;
                                if(obj instanceof RenderedImage){
                                    image = (RenderedImage)obj;
                                }else{
                                    //final RenderedImage image = new LargeRenderedImage(imgReader, imgIndex);
                                    //final PixelIterator ite = PixelIteratorFactory.createDefaultIterator(image);
                                    final ImageReader imgReader = ref.getImageReader();
                                    if(imgReader==null){
                                        LOGGER.log(Level.WARNING, "Can't compute isoline, ImageReader can't be found.");
                                        return;
                                    }
                                    image = imgReader.read(0);
                                }
                                final PixelIterator ite = PixelIteratorFactory.createDefaultIterator(image);
                                final int width = image.getWidth();
                                final int height = image.getHeight();
                                final BlockRunnable runnable = new BlockRunnable(gridtoCRS, ite, width, height, 0);
                                exec.submit(runnable);
                            }catch(IOException ex){
                                throw new ProcessException(ex.getMessage(), this, ex);
                            }
                        }
                    }
                }
            }

        }

        exec.shutdown();
        exec.awaitTermination(1, TimeUnit.DAYS);
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

        FeatureType type = buildIsolineFeatureType(featureTypeName);

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

    /**
     * Build isoline FeatureType
     * @param featureTypeName
     * @return
     * @throws DataStoreException
     */
    public static FeatureType buildIsolineFeatureType(String featureTypeName) throws DataStoreException {
        //FeatureType with scale
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(featureTypeName != null ? featureTypeName : "isolines");
        try {
            ftb.add(BasicFeatureTypes.GEOMETRY_ATTRIBUTE_NAME, LineString.class, CRS.decode("EPSG:4326", true));
        } catch (FactoryException ex) {
            throw new DataStoreException(ex);
        }
        ftb.add("scale", Double.class);
        ftb.add("value", Double.class);
        ftb.setDefaultGeometry(BasicFeatureTypes.GEOMETRY_ATTRIBUTE_NAME);
        return ftb.buildFeatureType();
    }

    private class BlockRunnable implements Runnable {

        private final MathTransform gridtoCRS;
        private final PixelIterator ite;
        private final int width;
        private final int height;
        private final double scale;

        //previous triangles informations
        private Boundary[][] line0TopNeighbor; // [level][X] for previous line
        private Boundary[][] line1TopNeighbor; // [level][X] for current line
        private Boundary[] leftNeighbor; // [Level] for current line

        //current pixel box
        private final Coordinate UL = new Coordinate();
        private final Coordinate UR = new Coordinate();
        private final Coordinate BL = new Coordinate();
        private final Coordinate BR = new Coordinate();

        public BlockRunnable(MathTransform gridtoCRS, PixelIterator ite, int width, int height, double scale) {
            this.ite = ite;
            this.width = width;
            this.height = height;
            this.scale = scale;
            this.gridtoCRS = gridtoCRS;
        }

        @Override
        public void run() {
            try {
                computeIsoligne();
            } catch (ProcessException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

        private void computeIsoligne() throws ProcessException{
            try{
                //line buffer
                leftNeighbor = new Boundary[intervals.length];
                line0TopNeighbor = new Boundary[intervals.length][width];
                line1TopNeighbor = new Boundary[intervals.length][width];
                double[] line0 = new double[width];
                double[] line1 = new double[width];

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
                    for(int i=0;i<leftNeighbor.length;i++){
                        leftNeighbor[i] = null;
                    }
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
                throw new ProcessException(ex.getMessage(), Isoline2.this, ex);
            }
        }

        private void update(final Construction cst, final int k){
            for(Boundary b : line0TopNeighbor[k]) cst.update(b);
            for(Boundary b : line1TopNeighbor[k]) cst.update(b);
            cst.update(leftNeighbor[k]);
        }

        private Boundary buildTriangles(final int k, final double level, final Boundary top, final Boundary left)
                throws MismatchedDimensionException, TransformException, ProcessException{

            final boolean ulCorner = (UL.z == level);
            final boolean urCorner = (UR.z == level);
            final boolean blCorner = (BL.z == level);
            final Coordinate crossUp = interpolate(level, UL, UR);
            final Coordinate crossLf = interpolate(level, UL, BL);
            final Coordinate crossHp = interpolate(level, UR, BL);

            // FIRST TRIANGLE //////////////////////////////////////////////////////
            // the 3 points on the first triangle hypothenuse
            // +---+ STop
            // |  /
            // | + SMiddle
            // |/
            // + SBottom
            final Construction.Edge STop;
            final Construction.Edge SMiddle;
            final Construction.Edge SBottom;

            if(top!=null && left!=null){ //-----------------------------------------
                //pixel is somewhere in the image

                if(top.HMiddle!=null){
                    if(crossHp!=null){
                        SMiddle = top.HMiddle;
                        SMiddle.add(crossHp);
                        STop = null;
                        SBottom = null;
                    }else if(left.VMiddle!=null){
                        top.HMiddle.add(crossLf);
                        top.HMiddle.getConstruction().merge(left.VMiddle);
                        update(top.HMiddle.getConstruction(),k);
                        STop = null;
                        SMiddle = null;
                        SBottom = null;
                    }else if(left.VBottom!=null){
                        top.HMiddle.add(BL);
                        //do not merge, used by triangle on the left side
                        //create a fork
                        final Construction cst = new Construction(level);
                        SBottom = cst.getEdge1();
                        SBottom.add(BL);
                        STop = null;
                        SMiddle = null;
                    }else{
                        STop = null;
                        SMiddle = null;
                        SBottom = null;
                    }
                    if(DEBUG) checkIntermediate(level,SBottom, SMiddle, STop);
                }else if(left.VMiddle!=null){
                    if(crossHp!=null){
                        SMiddle = left.VMiddle;
                        SMiddle.add(crossHp);
                        STop = null;
                        SBottom = null;
                    }else if(top.HRight!=null){
                        left.VMiddle.add(UR);
                        left.VMiddle.getConstruction().merge(top.HRight);
                        update(left.VMiddle.getConstruction(), k);
                        //create a fork
                        final Construction cst = new Construction(level);
                        STop = cst.getEdge1();
                        STop.add(UR);
                        SMiddle = null;
                        SBottom = null;
                    }else{
                        STop = null;
                        SMiddle = null;
                        SBottom = null;
                    }
                    if(DEBUG) checkIntermediate(level,SBottom, SMiddle, STop);
                }else if(left.VTop!=null && left.VBottom!=null && top.HRight!=null){
                    left.VBottom.add(UL);
                    STop = left.VTop;
                    STop.add(UR);
                    SMiddle = null;
                    SBottom = null;
                    if(DEBUG) checkIntermediate(level,SBottom, SMiddle, STop);
                }else if(left.VTop!=null && left.VBottom!=null){

                    left.VTop.add(BL);
                    left.VTop.getConstruction().merge(left.VBottom);
                    update(left.VTop.getConstruction(),k);
                    //create a fork
                    final Construction cst = new Construction(level);
                    SBottom = cst.getEdge1();
                    SBottom.add(BL);
                    STop = null;
                    SMiddle = null;
                    if(DEBUG) checkIntermediate(level,SBottom, SMiddle, STop);
                }else if(left.VTop!=null && top.HRight!=null){

                    left.VTop.add(UR);
                    left.VTop.getConstruction().merge(top.HRight);
                    update(left.VTop.getConstruction(),k);
                    //create a fork
                    final Construction cst = new Construction(level);
                    STop = cst.getEdge1();
                    STop.add(UR);
                    SBottom = null;
                    SMiddle = null;
                    if(DEBUG) checkIntermediate(level,SBottom, SMiddle, STop);
                }else if(left.VBottom!=null && top.HRight!=null){

                    //close the segment
                    left.VBottom.add(UR);

                    STop = top.HRight;
                    //create a fork at the bottom
                    final Construction cst = new Construction(level);
                    SBottom = cst.getEdge1();
                    SBottom.add(BL);
                    SMiddle = null;
                    if(DEBUG) checkIntermediate(level,SBottom, SMiddle, STop);
                }else if(left.VTop!=null){
                    if(crossHp!=null){
                        SMiddle = left.VTop;
                        SMiddle.add(crossHp);
                        STop = null;
                        SBottom = null;
                    }else{
                        STop = null;
                        SMiddle = null;
                        SBottom = null;
                    }
                    if(DEBUG) checkIntermediate(level,SBottom, SMiddle, STop);
                }else if(left.VBottom!=null){
                    STop = null;
                    SMiddle = null;
                    SBottom = left.VBottom;
                    if(DEBUG) checkIntermediate(level,SBottom, SMiddle, STop);
                }else if(top.HRight!=null){
                    STop = top.HRight;
                    SMiddle = null;
                    SBottom = null;
                    if(DEBUG) checkIntermediate(level,SBottom, SMiddle, STop);
                }else{
                    STop = null;
                    SMiddle = null;
                    SBottom = null;
                    if(DEBUG) checkIntermediate(level,SBottom, SMiddle, STop);
                }

                if(DEBUG) checkIntermediate(level,SBottom, SMiddle, STop);

            }else if(top!=null){ //-------------------------------------------------
                //pixel is on the left image border
                if(top.HMiddle!=null){
                    if(crossHp!=null){
                        SMiddle = top.HMiddle;
                        SMiddle.add(crossHp);
                        STop = null;
                        SBottom = null;
                    }else if(crossLf!=null){
                        top.HMiddle.add(crossLf);
                        STop = null;
                        SMiddle = null;
                        SBottom = null;
                    }else if(blCorner){
                        SBottom = top.HMiddle;
                        SBottom.add(BL);
                        STop = null;
                        SMiddle = null;
                    }else{
                        STop = null;
                        SMiddle = null;
                        SBottom = null;
                    }
                }else if(top.HLeft!=null){
                    if(crossHp!=null){
                        SMiddle = top.HLeft;
                        SMiddle.add(crossHp);
                        STop = null;
                        SBottom = null;
                    }else if(blCorner && top.HRight!=null){
                        SBottom = top.HLeft;
                        SBottom.add(BL);

                        final Construction cst = new Construction(level);
                        STop = cst.getEdge1();
                        STop.add(UL);
                        STop.add(UR);
                        SMiddle = null;
                    }else if(blCorner){
                        SBottom = top.HLeft;
                        SBottom.add(BL);
                        STop = null;
                        SMiddle = null;
                    }else if(top.HRight!=null){
                        STop = top.HLeft;
                        STop.add(UR);
                        SMiddle = null;
                        SBottom = null;
                    }else {
                        STop = null;
                        SMiddle = null;
                        SBottom = null;
                    }
                }else if(top.HRight!=null){
                    if(crossLf!=null){
                        top.HRight.add(crossLf);
                        STop = null;
                    }else{
                        STop = top.HRight;
                    }
                    SMiddle = null;
                    SBottom = null;
                }else if(crossLf!=null && crossHp!=null){
                    final Construction cst = new Construction(level);
                    SMiddle = cst.getEdge1();
                    SMiddle.add(crossLf);
                    SMiddle.add(crossHp);
                    STop = null;
                    SBottom = null;
                }else{
                    STop = null;
                    SMiddle = null;
                    SBottom = null;
                }

                if(DEBUG) checkIntermediate(level,SBottom, SMiddle, STop);

            }else if(left!=null){ //------------------------------------------------
                //pixel is on the top image border
                if(left.VMiddle!=null){
                    if(crossHp!=null){
                        SMiddle = left.VMiddle;
                        SMiddle.add(crossHp);
                        STop = null;
                        SBottom = null;
                    }else if(crossUp!=null){
                        left.VMiddle.add(crossUp);
                        STop = null;
                        SMiddle = null;
                        SBottom = null;
                    }else if(urCorner){
                        STop = left.VMiddle;
                        STop.add(UR);
                        SMiddle = null;
                        SBottom = null;
                    }else{
                        STop = null;
                        SMiddle = null;
                        SBottom = null;
                    }
                }else if(left.VTop != null && left.VBottom != null){
                    if(urCorner){
                        STop = left.VTop;
                        STop.add(UR);
                        SMiddle = null;
                        SBottom = left.VBottom;

                    }else{
                        left.VTop.getConstruction().merge(left.VBottom);
                        update(left.VTop.getConstruction(), k);
                        //fork it
                        final Construction cst = new Construction(level);
                        SBottom = cst.getEdge1();
                        SBottom.add(BL);
                        STop = null;
                        SMiddle = null;
                    }

                }else if(left.VTop != null){
                    if(urCorner){
                        STop = left.VTop;
                        STop.add(UR);
                        SMiddle = null;
                        SBottom = null;
                    }else if(crossHp!=null){
                        SMiddle = left.VTop;
                        SMiddle.add(crossHp);
                        STop = null;
                        SBottom = null;
                    }else{
                        STop = null;
                        SMiddle = null;
                        SBottom = null;
                    }
                }else if(left.VBottom != null){
                    if(crossUp!=null){
                        left.VBottom.add(crossUp);
                        //fork it
                        final Construction cst = new Construction(level);
                        SBottom = cst.getEdge1();
                        SBottom.add(BL);
                        STop = null;
                        SMiddle = null;
                    }else{
                        STop = null;
                        SMiddle = null;
                        SBottom = left.VBottom;
                    }
                }else if(crossUp!=null && crossHp!=null){
                    final Construction cst = new Construction(level);
                    SMiddle = cst.getEdge1();
                    SMiddle.add(crossUp);
                    SMiddle.add(crossHp);
                    STop = null;
                    SBottom = null;
                }else{
                    STop = null;
                    SMiddle = null;
                    SBottom = null;
                }

                if(DEBUG) checkIntermediate(level, SBottom, SMiddle, STop);

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
                    SMiddle = null;

                }else if(ulCorner && urCorner){
                    //adjacent border
                    final Construction cst = new Construction(level);
                    STop = cst.getEdge1();
                    STop.add(UL);
                    STop.add(UR);
                    SMiddle = null;
                    SBottom = null;

                }else if(ulCorner && blCorner){
                    //opposite border
                    final Construction cst = new Construction(level);
                    SBottom = cst.getEdge1();
                    SBottom.add(UL);
                    SBottom.add(BL);
                    STop = null;
                    SMiddle = null;

                }else if(urCorner && blCorner){
                    //hypothenus border
                    final Construction cst = new Construction(level);
                    STop = cst.getEdge1();
                    STop.add(BL);
                    STop.add(UR);
                    SBottom = cst.getEdge2();
                    SMiddle = null;
                }

                //split on a height
                else if(ulCorner && crossHp!=null){
                    final Construction cst = new Construction(level);
                    SMiddle = cst.getEdge1();
                    SMiddle.add(UL);
                    SMiddle.add(crossHp);
                    STop = null;
                    SBottom = null;

                }else if(urCorner && crossLf!=null){
                    final Construction cst = new Construction(level);
                    STop = cst.getEdge1();
                    STop.add(crossLf);
                    STop.add(UR);
                    SMiddle = null;
                    SBottom = null;

                }else if(blCorner && crossUp!=null){
                    final Construction cst = new Construction(level);
                    SBottom = cst.getEdge1();
                    SBottom.add(crossUp);
                    SBottom.add(BL);
                    STop = null;
                    SMiddle = null;
                }

                //split on 2 edges
                else if(crossUp!=null && crossHp!=null){
                    final Construction cst = new Construction(level);
                    SMiddle = cst.getEdge1();
                    SMiddle.add(crossUp);
                    SMiddle.add(crossHp);
                    STop = null;
                    SBottom = null;

                }else if(crossLf!=null && crossHp!=null){
                    final Construction cst = new Construction(level);
                    SMiddle = cst.getEdge1();
                    SMiddle.add(crossLf);
                    SMiddle.add(crossHp);
                    STop = null;
                    SBottom = null;

                }else if(crossUp!=null && crossLf!=null){
                    //only case where we can push the geometry directly
                    final Geometry geom = GF.createLineString(new Coordinate[]{crossUp, crossLf});
                    pushGeometry(geom, level);
                    STop = null;
                    SMiddle = null;
                    SBottom = null;
                }else{
                    STop = null;
                    SMiddle = null;
                    SBottom = null;
                }

                if(DEBUG) checkIntermediate(level,SBottom, SMiddle, STop);
            }

            return createSecondTriangle(level, SBottom, SMiddle, STop);
        }

        private void checkIntermediate(final double level,
                                              final Construction.Edge SBottom,
                                              final Construction.Edge SMiddle,
                                              final Construction.Edge STop) throws ProcessException{
            final Coordinate crossHp = interpolate(level, UR, BL);

            //algorithm check
            if(SBottom!=null && !SBottom.getLast().equals2D(BL)){
                throw new ProcessException("Unvalid point at BL",Isoline2.this,null);
            }
            if(SMiddle!=null && !SMiddle.getLast().equals2D(crossHp)){
                throw new ProcessException("Unvalid point at HP",Isoline2.this,null);
            }
            if(STop!=null && !STop.getLast().equals2D(UR)){
                throw new ProcessException("Unvalid point at UR",Isoline2.this,null);
            }
        }

        private Boundary createSecondTriangle(final double level,
                                              final Construction.Edge SBottom,
                                              final Construction.Edge SMiddle,
                                              final Construction.Edge STop) throws ProcessException{

            final boolean urCorner = (UR.z == level);
            final boolean blCorner = (BL.z == level);
            final boolean brCorner = (BR.z == level);
            final Coordinate crossBt = interpolate(level, BL, BR);
            final Coordinate crossRi = interpolate(level, UR, BR);

            //the new triangle
            final Boundary newBoundary = new Boundary();

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

            }else if(STop!=null && SBottom!=null){
                //propagate the two edges
                newBoundary.VTop = STop;
                newBoundary.HLeft = SBottom;

            }else if(STop != null){
                if(crossBt!=null){
                    //propage top limit
                    newBoundary.VTop = STop;

                    //duplicate line here, we might have a fork
                    Construction cst = new Construction(level);
                    newBoundary.HMiddle = cst.getEdge1();
                    newBoundary.HMiddle.add(UR);
                    newBoundary.HMiddle.add(crossBt);

                }else if(brCorner){
                    //propage bottom limit
                    newBoundary.HRight = STop;
                    newBoundary.HRight.add(BR);

                    //duplicate line here, we might have a fork
                    //if they are linked, the first triangle of next line will link them
                    Construction cst1 = new Construction(level);
                    newBoundary.VTop = cst1.getEdge1();
                    newBoundary.VTop.add(UR);

                    Construction cst2 = new Construction(level);
                    newBoundary.VBottom = cst2.getEdge1();
                    newBoundary.VBottom.add(BR);

                }else{
                    //propage top limit
                    newBoundary.VTop = STop;
                }
            }else if(SBottom != null){
                if(crossRi!=null){
                    newBoundary.VMiddle = SBottom;
                    newBoundary.VMiddle.add(crossRi);

                    //propage bottom limit
                    //duplicate line here, we might have a fork
                    Construction cst = new Construction(level);
                    newBoundary.HLeft = cst.getEdge1();
                    newBoundary.HLeft.add(BL);
                }else if(brCorner){
                    //propage bottom limit
                    newBoundary.HLeft = SBottom;

                    //duplicate line here, we might have a fork
                    //if they are linked, the first triangle of next line will link them
                    Construction cst1 = new Construction(level);
                    newBoundary.VBottom = cst1.getEdge1();
                    newBoundary.VBottom.add(BR);

                    final Construction cst2 = new Construction(level);
                    newBoundary.HRight = cst2.getEdge1();
                    newBoundary.HRight.add(BR);
                }else{
                    //propage bottom limit
                    newBoundary.HLeft = SBottom;
                }
            } else if (crossBt != null && Double.isNaN(UR.z)) {
                // special case when upper value is NaN
                Construction cst = new Construction(level);
                newBoundary.HMiddle = cst.getEdge1();
                newBoundary.HMiddle.add(crossBt);

            } else if (crossRi != null && Double.isNaN(BL.z)) {
                // special case when left value is NaN
                Construction cst = new Construction(level);
                newBoundary.VMiddle = cst.getEdge1();
                newBoundary.VMiddle.add(crossRi);
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
            if(newBoundary.HRight == null && brCorner){
                //create a single point
                final Construction cst1 = new Construction(level);
                newBoundary.VBottom = cst1.getEdge1();
                newBoundary.VBottom.add(BR);
                final Construction cst2 = new Construction(level);
                newBoundary.HRight = cst2.getEdge1();
                newBoundary.HRight.add(BR);
            }


            if(DEBUG) {
                //algorithm check, can not have all H or V set
                if(newBoundary.HMiddle!=null && (newBoundary.HLeft!=null || newBoundary.HRight!=null)){
                    throw new ProcessException("Logic error, Muplite H set",Isoline2.this,null);
                }
                if(newBoundary.VMiddle!=null && (newBoundary.VTop!=null || newBoundary.VBottom!=null)){
                    throw new ProcessException("Logic error, Muplite V set top="+newBoundary.VTop+" bottom="+newBoundary.VBottom,Isoline2.this,null);
                }
                //algorithm check, can not have all H or V set
                if(newBoundary.HLeft!=null   && !blCorner)     throw new ProcessException("Invalid point creation HL",Isoline2.this,null);
                if(newBoundary.HMiddle!=null && crossBt==null) throw new ProcessException("Invalid point creation HM",Isoline2.this,null);
                if(newBoundary.HRight!=null  && !brCorner)     throw new ProcessException("Invalid point creation HR",Isoline2.this,null);
                if(newBoundary.VTop!=null    && !urCorner)     throw new ProcessException("Invalid point creation VT",Isoline2.this,null);
                if(newBoundary.VMiddle!=null && crossRi==null) throw new ProcessException("Invalid point creation VM",Isoline2.this,null);
                if(newBoundary.VBottom!=null && !brCorner)     throw new ProcessException("Invalid point creation VB",Isoline2.this,null);
                newBoundary.checkIncoherence();

                if(newBoundary.HLeft!=null   && !newBoundary.HLeft  .getLast().equals2D(BL))      throw new ProcessException("Invalid point creation HL",Isoline2.this,null);
                if(newBoundary.HMiddle!=null && !newBoundary.HMiddle.getLast().equals2D(crossBt)) throw new ProcessException("Invalid point creation HM",Isoline2.this,null);
                if(newBoundary.HRight!=null  && !newBoundary.HRight .getLast().equals2D(BR))      throw new ProcessException("Invalid point creation HR",Isoline2.this,null);
                if(newBoundary.VTop!=null    && !newBoundary.VTop   .getLast().equals2D(UR))      throw new ProcessException("Invalid point creation VT",Isoline2.this,null);
                if(newBoundary.VMiddle!=null && !newBoundary.VMiddle.getLast().equals2D(crossRi)) throw new ProcessException("Invalid point creation VM",Isoline2.this,null);
                if(newBoundary.VBottom!=null && !newBoundary.VBottom.getLast().equals2D(BR))      throw new ProcessException("Invalid point creation VB",Isoline2.this,null);
            }

            return newBoundary;
        }

        private void pushGeometry(Geometry geom, double level) throws MismatchedDimensionException, TransformException{
            if(geom==null) return;
            final Feature f = FeatureUtilities.defaultFeature(type, "0");
            geom = JTS.transform(geom, gridtoCRS);
            JTS.setCRS(geom, crs);
            f.getProperty(BasicFeatureTypes.GEOMETRY_ATTRIBUTE_NAME).setValue(geom);
            f.getProperty("scale").setValue(scale);
            f.getProperty("value").setValue(level);
            col.add(f);
        }

    }


}