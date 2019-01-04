/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.processing.coverage.reducetodomain;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.parameter.Parameters;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.coverage.straighten.StraightenDescriptor;
import org.geotoolkit.coverage.Coverage;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.RangeMeaning;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Fix possible coverage crossing the antimeridan.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class ReduceToDomainProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public ReduceToDomainProcess(final ParameterValueGroup input) {
        super(ReduceToDomainDescriptor.INSTANCE,input);
    }

    /**
     *
     * @param coverage coverage to process
     */
    public ReduceToDomainProcess(Coverage coverage){
        super(ReduceToDomainDescriptor.INSTANCE, asParameters(coverage));
    }

    private static ParameterValueGroup asParameters(Coverage coverage){
        final Parameters params = Parameters.castOrWrap(ReduceToDomainDescriptor.INPUT_DESC.createValue());
        params.getOrCreate(ReduceToDomainDescriptor.COVERAGE_IN).setValue(coverage);
        return params;
    }

    /**
     * Execute process now.
     *
     * @return reduced coverage
     * @throws ProcessException
     */
    public Coverage executeNow() throws ProcessException {
        execute();
        return (Coverage) outputParameters.getValue(ReduceToDomainDescriptor.COVERAGE_OUT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute() throws ProcessException {
        GridCoverage2D candidate = (GridCoverage2D) inputParameters.getValue(StraightenDescriptor.COVERAGE_IN);
        final CoordinateReferenceSystem crs = candidate.getCoordinateReferenceSystem2D();
        final CoordinateReferenceSystem crs2d;
        try {
            crs2d = CRSUtilities.getCRS2D(crs);
        } catch (TransformException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }

        final boolean hack = true;
        final boolean[] wrapAround = new boolean[2];
        final CoordinateSystem cs = crs2d.getCoordinateSystem();
        for(int i=0;i<cs.getDimension();i++){
            wrapAround[i] = crs instanceof GeographicCRS || (cs.getAxis(i).getRangeMeaning() == RangeMeaning.WRAPAROUND);
        }
        if (!(crs instanceof ProjectedCRS)) {
            if(!wrapAround[0] && !wrapAround[1]){
                //no wrap around axis, can't fix anything.
                outputParameters.getOrCreate(StraightenDescriptor.COVERAGE_OUT).setValue(candidate);
                return;
            }
        }


        //straighten coverage---------------------------------------------------
        final Parameters subParams = Parameters.castOrWrap(StraightenDescriptor.INPUT_DESC.createValue());
        subParams.getOrCreate(StraightenDescriptor.COVERAGE_IN).setValue(candidate);
        final org.geotoolkit.process.Process subprocess = StraightenDescriptor.INSTANCE.createProcess(subParams);
        final Parameters result;
        try{
            result = Parameters.castOrWrap(subprocess.call());
        }catch(ProcessException ex){
            throw new ProcessException(ex.getMessage(), this, ex);
        }
        candidate = (GridCoverage2D) result.getValue(StraightenDescriptor.COVERAGE_OUT);



        //resample coverage, we want it to be 'straight', no rotation or different axe scale.
        final GridGeometry2D gridgeom = candidate.getGridGeometry();
        final GridExtent gridenv = gridgeom.getExtent2D();
        final MathTransform gridToCRS = gridgeom.getGridToCRS2D(PixelOrientation.UPPER_LEFT);

        try{
            final double[] coords = new double[2 * 2];
            coords[0] = gridenv.getLow(0);      coords[1] = gridenv.getLow(1);
            coords[2] = gridenv.getHigh(0);     coords[3] = gridenv.getHigh(1);
            gridToCRS.transform(coords, 0, coords, 0, 2);
            double minX = coords[0];
            double maxX = coords[2];
            double minY = coords[3];
            double maxY = coords[1];
            double spanX = maxX-minX;
            double spanY = maxY-minY;
            double scaleX = spanX / gridenv.getSize(0);
            double scaleY = spanY / gridenv.getSize(1);
            double scale = Math.min(scaleX, scaleY);

            final double axiXMinValue;
            final double axiXMaxValue;
            final double axiYMinValue;
            final double axiYMaxValue;
            if(hack && crs2d instanceof GeographicCRS){
                axiXMinValue = -180;
                axiXMaxValue = +180;
                axiYMinValue = -90;
                axiYMaxValue = +90;
            } else if (crs2d instanceof ProjectedCRS) {
                /*
                 * Hack : we did not verified if base CRS is long/lat
                 * this hack work only for Mercator and Plate Carré
                 */
                final MathTransform mt = ((ProjectedCRS)crs2d).getConversionFromBase().getMathTransform();

                DirectPosition2D pt1 = new DirectPosition2D(-180, 0);
                pt1 = (DirectPosition2D) mt.transform(pt1, pt1);
                axiXMinValue = pt1.getX();

                DirectPosition2D pt2 = new DirectPosition2D(180, 0);
                pt2 = (DirectPosition2D) mt.transform(pt2, pt2);
                axiXMaxValue = pt2.getX();

                axiYMinValue = Double.NEGATIVE_INFINITY;
                axiYMaxValue = Double.POSITIVE_INFINITY;
            } else {
                axiXMinValue = cs.getAxis(0).getMinimumValue();
                axiXMaxValue = cs.getAxis(0).getMinimumValue();
                axiYMinValue = cs.getAxis(1).getMaximumValue();
                axiYMaxValue = cs.getAxis(1).getMaximumValue();
            }

            final boolean xWrap = (minX < axiXMinValue || maxX > axiXMaxValue);
            final boolean yWrap = (minY < axiYMinValue || maxY > axiYMaxValue);
            if( !xWrap && !yWrap ){
                //nothing to fix
                outputParameters.getOrCreate(StraightenDescriptor.COVERAGE_OUT).setValue(candidate);
                return;
            }

            //calculate the fixed result image
            final AffineTransform2D baseTrs = (AffineTransform2D)
                    candidate.getGridGeometry().getGridToCRS2D(PixelOrientation.UPPER_LEFT);
            final RenderedImage img = candidate.getRenderedImage();
            final ColorModel cm = img.getColorModel();
            final Raster baseRaster = img.getData();
            final BufferedImage resimg;
            final AffineTransform2D gtc;
            final WritableRaster raster;
            if(xWrap && yWrap){
                //wrap both axes
                raster = baseRaster.createCompatibleWritableRaster((int)(spanX/scale), (int)(spanY/scale));
                resimg = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
                gtc = new AffineTransform2D(
                        scale, 0, 0,
                        -scale,
                        axiXMinValue,
                        axiYMaxValue);

            }else if(xWrap){
                //wrap x axes
                raster = baseRaster.createCompatibleWritableRaster((int)(spanX/scale), img.getHeight());
                resimg = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
                gtc = new AffineTransform2D(
                        scale, 0, 0,
                        -scale,
                        axiXMinValue,
                        baseTrs.getTranslateY());

            }else{
                //wrap y axes
                raster = baseRaster.createCompatibleWritableRaster(img.getWidth(), (int)(spanY/scale));
                resimg = new BufferedImage(cm, raster, cm.isAlphaPremultiplied(), null);
                gtc = new AffineTransform2D(
                        scale, 0, 0,
                        -scale,
                        baseTrs.getTranslateX(),
                        axiYMaxValue);
            }
            final AffineTransform2D baseInv = baseTrs.inverse();
            final AffineTransform2D resInv = gtc.inverse();

            //draw base image
            final Point2D pt = new Point2D.Double();
            AffineTransform tmp = new AffineTransform(resInv);
            tmp.concatenate(baseTrs);
            tmp.transform(pt, pt);
            raster.setRect((int)pt.getX(), (int)pt.getY(), baseRaster);

            final double crsSpanX = axiXMaxValue - axiXMinValue;
            final double crsSpanY = axiYMaxValue - axiYMinValue;

            if(xWrap){
                pt.setLocation(0, 0);
                tmp = new AffineTransform(resInv);
                tmp.translate(-crsSpanX, 0);
                tmp.concatenate(baseTrs);
                tmp.transform(pt, pt);
                raster.setRect((int)pt.getX(), (int)pt.getY(), baseRaster);

                pt.setLocation(0, 0);
                tmp = new AffineTransform(resInv);
                tmp.translate(+crsSpanX, 0);
                tmp.concatenate(baseTrs);
                tmp.transform(pt, pt);
                raster.setRect((int)pt.getX(), (int)pt.getY(), baseRaster);

            }
            if(yWrap){
                pt.setLocation(0, 0);
                tmp = new AffineTransform(resInv);
                tmp.translate(0,-crsSpanY);
                tmp.concatenate(baseTrs);
                tmp.transform(pt, pt);
                raster.setRect((int)pt.getX(), (int)pt.getY(), baseRaster);

                pt.setLocation(0, 0);
                tmp = new AffineTransform(resInv);
                tmp.translate(0,+crsSpanY);
                tmp.concatenate(baseTrs);
                tmp.transform(pt, pt);
                raster.setRect((int)pt.getX(), (int)pt.getY(), baseRaster);
            }
            if(xWrap && yWrap){
                pt.setLocation(0, 0);
                tmp = new AffineTransform(resInv);
                tmp.translate(-crsSpanX,-crsSpanY);
                tmp.concatenate(baseTrs);
                tmp.transform(pt, pt);
                raster.setRect((int)pt.getX(), (int)pt.getY(), baseRaster);

                pt.setLocation(0, 0);
                tmp = new AffineTransform(resInv);
                tmp.translate(-crsSpanX,+crsSpanY);
                tmp.concatenate(baseTrs);
                tmp.transform(pt, pt);
                raster.setRect((int)pt.getX(), (int)pt.getY(), baseRaster);

                pt.setLocation(0, 0);
                tmp = new AffineTransform(resInv);
                tmp.translate(+crsSpanX,-crsSpanY);
                tmp.concatenate(baseTrs);
                tmp.transform(pt, pt);
                raster.setRect((int)pt.getX(), (int)pt.getY(), baseRaster);

                pt.setLocation(0, 0);
                tmp = new AffineTransform(resInv);
                tmp.translate(+crsSpanX,+crsSpanY);
                tmp.concatenate(baseTrs);
                tmp.transform(pt, pt);
                raster.setRect((int)pt.getX(), (int)pt.getY(), baseRaster);
            }


            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            final GridGeometry2D gg = new GridGeometry2D(null, PixelOrientation.UPPER_LEFT, gtc, crs);
            gcb.setGridCoverage(candidate);
            gcb.setGridGeometry(gg);
            gcb.setRenderedImage(resimg);
            final GridCoverage2D outgc = gcb.getGridCoverage2D();
            outputParameters.getOrCreate(StraightenDescriptor.COVERAGE_OUT).setValue(outgc);
        }catch(TransformException ex){
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

}
