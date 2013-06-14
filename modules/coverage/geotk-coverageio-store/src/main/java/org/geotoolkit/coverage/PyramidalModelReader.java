/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011-2012, Geomatys
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
package org.geotoolkit.coverage;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageReader;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.finder.CoverageFinder;
import org.geotoolkit.coverage.finder.CoverageFinderFactory;
import org.geotoolkit.coverage.grid.GeneralGridEnvelope;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridEnvelope2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.referencing.cs.DiscreteCoordinateSystemAxis;
import org.geotoolkit.referencing.operation.transform.AffineTransform2D;
import org.geotoolkit.util.Cancellable;
import org.geotoolkit.util.ImageIOUtilities;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;
import org.opengis.util.NameFactory;
import org.opengis.util.NameSpace;

/**
 * GridCoverage reader on top of a Pyramidal object.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class PyramidalModelReader extends GridCoverageReader{

    private CoverageReference ref;
    private final CoverageFinder coverageFinder;

    @Deprecated
    public PyramidalModelReader() {
        this.coverageFinder = CoverageFinderFactory.createDefaultCoverageFinder();
//        this.coverageFinder = CoverageFinderFactory.createStrictlyCoverageFinder();
    }
    
    public PyramidalModelReader(CoverageFinder coverageFinder) {
        this.coverageFinder = coverageFinder;
    }

    @Override
    public CoverageReference getInput() {
        return ref;
    }

    private PyramidalCoverageReference getPyramidalModel(){
        return (PyramidalCoverageReference)ref;
    }

    @Override
    public void setInput(Object input) throws CoverageStoreException {
        if(!(input instanceof CoverageReference) || !(input instanceof PyramidalCoverageReference)){
            throw new CoverageStoreException("Unsupported input type, can only be CoverageReference implementing PyramidalModel.");
        }
        this.ref = (CoverageReference) input;
        super.setInput(input);
    }

    @Override
    public List<? extends GenericName> getCoverageNames() throws CoverageStoreException, CancellationException {
        final NameFactory dnf = FactoryFinder.getNameFactory(null);
        final String nameSpace = getInput().getName().getNamespaceURI() != null ? getInput().getName().getNamespaceURI() : "http://geotoolkit.org" ;
        final NameSpace ns = dnf.createNameSpace(dnf.createGenericName(null, nameSpace), null);
        final GenericName gn = dnf.createLocalName(ns, getInput().getName().getLocalPart());
        return Collections.singletonList(gn);
    }

    @Override
    public GeneralGridGeometry getGridGeometry(int index) throws CoverageStoreException, CancellationException {

        final PyramidSet set;
        try {
            set = getPyramidalModel().getPyramidSet();
        } catch (DataStoreException ex) {
            throw new CoverageStoreException(ex);
        }

        final GeneralGridGeometry gridGeom;
        if(!set.getPyramids().isEmpty()){
            //we use the first pyramid as default
            final Pyramid pyramid = set.getPyramids().iterator().next();

            final List<GridMosaic> mosaics = new ArrayList<GridMosaic>(pyramid.getMosaics());
            Collections.sort(mosaics, CoverageFinder.SCALE_COMPARATOR);

            if(mosaics.isEmpty()){
                //no mosaics
                gridGeom = new GeneralGridGeometry(null, null, set.getEnvelope());
            }else{
                final CoordinateReferenceSystem crs = pyramid.getCoordinateReferenceSystem();
                final CoordinateSystem cs = crs.getCoordinateSystem();
                final int nbdim = cs.getDimension();

                //use the last mosaic informations
                final GridMosaic mosaic = mosaics.get(mosaics.size()-1);
                final Dimension gridSize = mosaic.getGridSize();
                final Dimension tileSize = mosaic.getTileSize();

                //we expect no rotation
                final AffineTransform2D gridToCRS2D = AbstractGridMosaic.getTileGridToCRS(mosaic, new Point(0, 0));
                final List<double[]> axisValues = new ArrayList<double[]>();
                for(int i=2;i<nbdim;i++){
                    final CoordinateSystemAxis axis = cs.getAxis(i);
                    if(axis instanceof DiscreteCoordinateSystemAxis){
                        final DiscreteCoordinateSystemAxis dca = (DiscreteCoordinateSystemAxis) axis;
                        final double[] values = new double[dca.length()];
                        for(int k=0; k<values.length; k++){
                            final Comparable c = dca.getOrdinateAt(k);
                            if(c instanceof Number){
                                values[k] = ((Number)c).doubleValue();
                            }else if(c instanceof Date){
                                values[k] = ((Date)c).getTime();
                            }
                        }
                        axisValues.add(values);
                    }else{
                        axisValues.add(new double[0]);
                    }
                }

                final double[][] discretValues = axisValues.toArray(new double[0][0]);
                final MathTransform gridToCRS = ReferencingUtilities.toTransform(gridToCRS2D, discretValues);

                final int[] low = new int[nbdim];
                final int[] high = new int[nbdim];
                low[0] = 0;
                low[1] = 0;
                high[0] = gridSize.width*tileSize.width;
                high[1] = gridSize.height*tileSize.height;

                for(int i=2;i<nbdim;i++){
                    low[i] = 0;
                    high[i] = discretValues[i-2].length;
                }
                final GeneralGridEnvelope ge = new GeneralGridEnvelope(low,high,false);

                gridGeom = new GeneralGridGeometry(ge, PixelInCell.CELL_CORNER, gridToCRS, crs);
            }

        }else{
            //empty pyramid set
            gridGeom = new GeneralGridGeometry(null, null, set.getEnvelope());
        }

        return gridGeom;
    }

    @Override
    public List<GridSampleDimension> getSampleDimensions(int index) throws CoverageStoreException, CancellationException {
        try {
            return getPyramidalModel().getSampleDimensions(index);
        } catch (DataStoreException ex) {
            throw new CoverageStoreException(ex.getMessage(), ex);
        }
    }

    @Override
    public GridCoverage read(int index, GridCoverageReadParam param) throws CoverageStoreException, CancellationException {
        if(index != 0){
            throw new CoverageStoreException("Invalid Image index.");
        }

        if(param == null){
            param = new GridCoverageReadParam();
        }

        final int[] desBands = param.getDestinationBands();
        final int[] sourceBands = param.getSourceBands();
        if(desBands != null || sourceBands != null){
            throw new CoverageStoreException("Source or destination bands can not be used on pyramide coverages.");
        }


        CoordinateReferenceSystem crs = param.getCoordinateReferenceSystem();
        Envelope paramEnv = param.getEnvelope();
        double[] resolution = param.getResolution();


        //verify envelope and crs
        if(crs == null && paramEnv == null){
            //use the max extent
            paramEnv = getGridGeometry(0).getEnvelope();
            crs = paramEnv.getCoordinateReferenceSystem();
        }else if(crs != null && paramEnv != null){
            //check the envelope crs matches given crs
            if(!CRS.equalsIgnoreMetadata(paramEnv.getCoordinateReferenceSystem(),crs)){
                throw new CoverageStoreException("Invalid parameters : envelope crs do not match given crs.");
            }
        }else if(paramEnv != null){
            //use the envelope crs
            crs = paramEnv.getCoordinateReferenceSystem();
        }else if(crs != null){
            //use the given crs
            paramEnv = getGridGeometry(0).getEnvelope();
            try {
                paramEnv = CRS.transform(paramEnv, crs);
            } catch (TransformException ex) {
                throw new CoverageStoreException("Could not transform coverage envelope to given crs.");
            }
        }

        //estimate resolution if not given
        if(resolution == null){
            //set resolution to infinite, will select the last mosaic level
            resolution = new double[]{Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY};
        }


        final PyramidSet pyramidSet;
        try {
            pyramidSet = getPyramidalModel().getPyramidSet();
        } catch (DataStoreException ex) {
            throw new CoverageStoreException(ex);
        }


        Pyramid pyramid = null;
        try {
//            pyramid = CoverageUtilities.findPyramid(pyramidSet, crs);
            pyramid = coverageFinder.findPyramid(pyramidSet, crs);
        } catch (FactoryException ex) {
            throw new CoverageStoreException(ex);
        }

        if(pyramid == null){
            //no reliable pyramid
            throw new CoverageStoreException("No pyramid defined.");
        }

        final CoordinateReferenceSystem pyramidCRS = pyramid.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem pyramidCRS2D;
        GeneralEnvelope wantedEnv2D;
        GeneralEnvelope wantedEnv;
        try {
            pyramidCRS2D = CRSUtilities.getCRS2D(pyramidCRS);
            wantedEnv2D = new GeneralEnvelope(CRS.transform(paramEnv, pyramidCRS2D));
            wantedEnv = new GeneralEnvelope(ReferencingUtilities.transform(paramEnv, pyramidCRS));
        } catch (TransformException ex) {
            throw new CoverageStoreException(ex.getMessage(),ex);
        }

        //ensure we don't go out of the crs envelope
        final Envelope maxExt = CRS.getEnvelope(pyramidCRS);
        if(maxExt != null){
            wantedEnv2D.intersect(maxExt);
            if(Double.isNaN(wantedEnv2D.getMinimum(0))){ wantedEnv2D.setRange(0, maxExt.getMinimum(0), wantedEnv2D.getMaximum(0));  }
            if(Double.isNaN(wantedEnv2D.getMaximum(0))){ wantedEnv2D.setRange(0, wantedEnv2D.getMinimum(0), maxExt.getMaximum(0));  }
            if(Double.isNaN(wantedEnv2D.getMinimum(1))){ wantedEnv2D.setRange(1, maxExt.getMinimum(1), wantedEnv2D.getMaximum(1));  }
            if(Double.isNaN(wantedEnv2D.getMaximum(1))){ wantedEnv2D.setRange(1, wantedEnv2D.getMinimum(1), maxExt.getMaximum(1));  }
            wantedEnv.setRange(0, wantedEnv2D.getMinimum(0), wantedEnv2D.getMaximum(0));
            wantedEnv.setRange(1, wantedEnv2D.getMinimum(1), wantedEnv2D.getMaximum(1));
        }


        //the wanted image resolution
        final double wantedResolution = resolution[0];
        final double tolerance = 0.1d;

        GridMosaic mosaic = null;
        try {
//            mosaic = CoverageUtilities.findMosaic(pyramid, wantedResolution, tolerance, wantedEnv,100);
            mosaic = coverageFinder.findMosaic(pyramid, wantedResolution, tolerance, wantedEnv,100);
        } catch (FactoryException ex) {
            throw new CoverageStoreException(ex.getMessage(),ex);
        }
        
        if(mosaic == null){
            //no reliable mosaic
            throw new CoverageStoreException("No mosaic defined.");
        }


        //we definitly do not want some NaN values
        if(Double.isNaN(wantedEnv.getMinimum(0))){ wantedEnv.setRange(0, Double.NEGATIVE_INFINITY, wantedEnv.getMaximum(0));  }
        if(Double.isNaN(wantedEnv.getMaximum(0))){ wantedEnv.setRange(0, wantedEnv.getMinimum(0), Double.POSITIVE_INFINITY);  }
        if(Double.isNaN(wantedEnv.getMinimum(1))){ wantedEnv.setRange(1, Double.NEGATIVE_INFINITY, wantedEnv.getMaximum(1));  }
        if(Double.isNaN(wantedEnv.getMaximum(1))){ wantedEnv.setRange(1, wantedEnv.getMinimum(1), Double.POSITIVE_INFINITY);  }



        final DirectPosition ul = mosaic.getUpperLeftCorner();
        final double tileMatrixMinX = ul.getOrdinate(0);
        final double tileMatrixMaxY = ul.getOrdinate(1);
        final Dimension gridSize = mosaic.getGridSize();
        final Dimension tileSize = mosaic.getTileSize();
        final double scale = mosaic.getScale();
        final double tileSpanX = scale * tileSize.width;
        final double tileSpanY = scale * tileSize.height;
        final int gridWidth = gridSize.width;
        final int gridHeight = gridSize.height;

        //find all the tiles we need --------------------------------------

        final double epsilon = 1e-6;
        final double bBoxMinX = wantedEnv.getMinimum(0);
        final double bBoxMaxX = wantedEnv.getMaximum(0);
        final double bBoxMinY = wantedEnv.getMinimum(1);
        final double bBoxMaxY = wantedEnv.getMaximum(1);
        double tileMinCol = Math.floor( (bBoxMinX - tileMatrixMinX) / tileSpanX + epsilon);
        double tileMaxCol = Math.floor( (bBoxMaxX - tileMatrixMinX) / tileSpanX - epsilon)+1;
        double tileMinRow = Math.floor( (tileMatrixMaxY - bBoxMaxY) / tileSpanY + epsilon);
        double tileMaxRow = Math.floor( (tileMatrixMaxY - bBoxMinY) / tileSpanY - epsilon)+1;

        //ensure we dont go out of the grid
        if(tileMinCol < 0) tileMinCol = 0;
        if(tileMaxCol > gridWidth) tileMaxCol = gridWidth;
        if(tileMinRow < 0) tileMinRow = 0;
        if(tileMaxRow > gridHeight) tileMaxRow = gridHeight;


        //tiles to render, coordinate in grid -> image offset
        final Collection<Point> candidates = new ArrayList<Point>();

        for(int tileCol=(int)tileMinCol; tileCol<tileMaxCol; tileCol++){
            for(int tileRow=(int)tileMinRow; tileRow<tileMaxRow; tileRow++){
                if(mosaic.isMissing(tileCol, tileRow)){
                    //tile not available
                    continue;
                }candidates.add(new Point(tileCol, tileRow));
            }
        }

        //aggregation ----------------------------------------------------------
        final Map hints = Collections.EMPTY_MAP;

        //image in which all tiles will be aggregated
        BufferedImage image = null;


        final BlockingQueue<Object> queue;
        try {
            queue = mosaic.getTiles(candidates, hints);
        } catch (DataStoreException ex) {
            throw new CoverageStoreException(ex.getMessage(),ex);
        }

        while(true){
            Object obj = null;
            try {
                obj = queue.poll(100, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                //not important
            }

            if(abortRequested){
                if(queue instanceof Cancellable){
                    ((Cancellable)queue).cancel();
                }
                break;
            }

            if(obj == GridMosaic.END_OF_QUEUE){
                break;
            }

            if(obj instanceof TileReference){
                final TileReference tile = (TileReference)obj;
                final Point position = tile.getPosition();
                final Point offset = new Point(
                        (int)(position.x-tileMinCol)*tileSize.width,
                        (int)(position.y-tileMinRow)*tileSize.height);

                final Object input = tile.getInput();
                RenderedImage tileImage = null;
                if(input instanceof RenderedImage){
                    tileImage = (RenderedImage) input;
                }else{
                    ImageReader reader = null;
                    try {
                        reader = tile.getImageReader();
                        tileImage = reader.read(tile.getImageIndex());
                    } catch (IOException ex) {
                        throw new CoverageStoreException(ex.getMessage(),ex);
                    }finally{
                        ImageIOUtilities.releaseReader(reader);
                    }
                }

                if(image == null){
                    final ColorModel cm = tileImage.getColorModel();
                    final WritableRaster raster = cm.createCompatibleWritableRaster(
                            (int)(tileMaxCol-tileMinCol)*tileSize.width,
                            (int)(tileMaxRow-tileMinRow)*tileSize.height);

                    image = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
                }

                image.getRaster().setDataElements(offset.x, offset.y, tileImage.getData());
                //we consider all images have the same data model
                //g2d.drawRenderedImage(tileImage, new AffineTransform(1, 0, 0, 1, offset.x, offset.y));
            }
        }

        if(image == null){
            image = new BufferedImage(
                (int)(tileMaxCol-tileMinCol)*tileSize.width,
                (int)(tileMaxRow-tileMinRow)*tileSize.height,
                BufferedImage.TYPE_INT_ARGB);
        }


        //build the coverage ---------------------------------------------------
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName(ref.getName().getLocalPart());        
        final List<GridSampleDimension> dimensions = getSampleDimensions(ref.getImageIndex());
        if (dimensions != null) {
            gcb.setSampleDimensions(dimensions.toArray(new GridSampleDimension[dimensions.size()]));
        }
        
        final MathTransform gtc = AbstractGridMosaic.getTileGridToCRS(mosaic, new Point((int)tileMinCol,(int)tileMinRow));
        final GridEnvelope2D ge = new GridEnvelope2D(0, 0, image.getWidth(), image.getHeight());
        final GridGeometry2D gridgeo = new GridGeometry2D(ge, PixelOrientation.UPPER_LEFT, gtc, pyramidCRS2D, null);
        gcb.setGridGeometry(gridgeo);
        gcb.setRenderedImage(image);
        
        return gcb.build();
    }

}
