/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.coverage.io;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;

import org.geotoolkit.coverage.CoverageFactoryFinder;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.operation.transform.ProjectiveTransform;

import org.geotoolkit.util.logging.Logging;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;

/**
 * Default Coverage Reader using ImageReader.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultCoverageReader implements CoverageReader{

    private final ImageReader reader;
    private final CoordinateReferenceSystem crs;
    private final Rectangle boundsGRID;
    private final Rectangle2D boundsCRS;
    private final MathTransform gridToCRS;
    private final MathTransform CRStoGrid;
    
    public DefaultCoverageReader(ImageReader reader, MathTransform gridtoCRS, CoordinateReferenceSystem crs) 
            throws NoninvertibleTransformException{
        if(reader == null || gridtoCRS == null || crs == null){
            throw new NullPointerException("Reader,CRS and GridToCRS transform can not be null");
        }
        this.reader = reader;
        this.gridToCRS = gridtoCRS;
        this.CRStoGrid = gridtoCRS.inverse();
        this.crs = crs;
        
        //calculate bounds
        Rectangle rect = new Rectangle(1,1);
        try {
            rect = new Rectangle(reader.getWidth(0), reader.getHeight(0));
        } catch (IOException ex) {
            Logging.getLogger(CoverageReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.boundsGRID = rect;
        
        Rectangle2D rectCRS = new Rectangle2D.Double();
        try {
            rectCRS = CRS.transform((MathTransform2D)gridToCRS, rect, rectCRS);
        } catch (TransformException ex) {
            Logging.getLogger(CoverageReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.boundsCRS = rectCRS;
        
    }
    /**
     * {@inheritDoc }
     */
    public GridCoverage2D read(final CoverageReadParam param) 
            throws FactoryException, TransformException, IOException{
        
        if(param == null){
            //no parameters, return the complete image
            final ImageReadParam readParam = reader.getDefaultReadParam();
            final RenderedImage image = reader.readAsRenderedImage(0, readParam);
            return CoverageFactoryFinder.getGridCoverageFactory(null)
                .create("Le coverage", image, crs, gridToCRS, null, null, null);
        }
        
        
        Envelope requestEnvelope = param.getEnveloppe();
        final double[] requestResolution = param.getResolution();
        
        if(!requestEnvelope.getCoordinateReferenceSystem().equals(crs)){
            //reproject requested enveloppe to dataCRS
            MathTransform objToData = CRS.findMathTransform(
                    requestEnvelope.getCoordinateReferenceSystem(), crs);
            requestEnvelope = CRS.transform(objToData, requestEnvelope);
        }
        
        final Rectangle sourceRegion = new Rectangle();
        CRS.transform((MathTransform2D) CRStoGrid, toRectangle(requestEnvelope), sourceRegion);
        if (sourceRegion.x < 0) sourceRegion.x = 0;
        if (sourceRegion.y < 0) sourceRegion.y = 0;
        if (sourceRegion.width > boundsGRID.width) sourceRegion.width = boundsGRID.width;
        if( sourceRegion.height > boundsGRID.height) sourceRegion.height = boundsGRID.height;
        Point2D resolution = new Point2D.Double(requestResolution[0], requestResolution[1]);
        resolution = ((AffineTransform) CRStoGrid).deltaTransform(resolution, resolution);
        final int subsamplingX = Math.max(1, (int) Math.abs(resolution.getX()));
        final int subsamplingY = Math.max(1, (int) Math.abs(resolution.getY()));
        
        //check if we are out of the region
        if(    sourceRegion.x > boundsGRID.width 
            || sourceRegion.y > boundsGRID.height){
            return null;
        }
        
        final ImageReadParam readParam = reader.getDefaultReadParam();
        readParam.setSourceRegion(sourceRegion);
        readParam.setSourceSubsampling(subsamplingX, subsamplingY, 0, 0);
        final RenderedImage image = reader.readAsRenderedImage(0, readParam);
        final AffineTransform subGridToCRS = new AffineTransform((AffineTransform) gridToCRS);
        subGridToCRS.translate(sourceRegion.x, sourceRegion.y);
        subGridToCRS.scale(subsamplingX, subsamplingY);
        final MathTransform mt = ProjectiveTransform.create(subGridToCRS);
        final GridCoverage2D coverage = CoverageFactoryFinder.getGridCoverageFactory(null)
                .create("Le coverage", image, crs, mt, null, null, null);
        return coverage;
    }
    
    
    
    /**
     * {@inheritDoc }
     */
    public Envelope getCoverageBounds(){
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, boundsCRS.getMinX(), boundsCRS.getMaxX());
        env.setRange(1, boundsCRS.getMinY(), boundsCRS.getMaxY());
        return env;
    }
    
    private Rectangle2D toRectangle(final Envelope env){
        final double x = env.getMinimum(0);
        final double y = env.getMinimum(1);
        final double w = env.getMaximum(0) - x;
        final double h = env.getMaximum(1) - y;
        return new Rectangle2D.Double(x, y, w, h);
    }
    
}
