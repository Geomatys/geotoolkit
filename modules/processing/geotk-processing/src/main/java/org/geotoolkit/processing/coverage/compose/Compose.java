/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.processing.coverage.compose;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.prep.PreparedGeometry;
import com.vividsolutions.jts.geom.prep.PreparedGeometryFactory;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.List;
import java.util.Map.Entry;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.parameter.Parameters;
import static org.apache.sis.parameter.Parameters.castOrWrap;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.matrix.Matrix3;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import static org.apache.sis.referencing.operation.transform.MathTransforms.concatenate;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.grid.GridEnvelope2D;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.geometry.GeometricUtilities;
import org.geotoolkit.geometry.GeometricUtilities.WrapResolution;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.referencing.ReferencingUtilities;
import static org.geotoolkit.processing.coverage.compose.ComposeDescriptor.*;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Jean-Loup Amiot (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class Compose extends AbstractProcess {

    private static final GeometryFactory GF = new GeometryFactory();
    private static final PreparedGeometryFactory PGF = new PreparedGeometryFactory();

    Compose(ParameterValueGroup input) {
        super(ComposeDescriptor.INSTANCE, input);
    }

    public Compose(List<Entry<GridCoverage2D,Geometry>> layers) {
        this(asParameters(layers));
    }

    private static ParameterValueGroup asParameters(List<Entry<GridCoverage2D,Geometry>> layers){
        final Parameters params = Parameters.castOrWrap(INPUT.createValue());
        for (Entry<GridCoverage2D,Geometry> entry : layers) {
            final Parameters layer = Parameters.castOrWrap(params.addGroup(LAYER_PARAM.getName().getCode()));
            layer.getOrCreate(COVERAGE_PARAM).setValue(entry.getKey());
            layer.getOrCreate(CLIP_PARAM).setValue(entry.getValue());
        }
        return params;
    }

    public GridCoverage2D executeNow() throws ProcessException {
        execute();
        return outputParameters.getValue(ComposeDescriptor.COVERAGE_PARAM);
    }

    @Override
    protected void execute() throws ProcessException {

        final List<ParameterValueGroup> imageParams = inputParameters.groups(LAYER_PARAM.getName().getCode());

        RenderedImage outImageReference = null;

        final int nbCoverage = imageParams.size();
        final GridCoverage2D[] inGridCoverages = new GridCoverage2D[nbCoverage];
        final Geometry[] clipGeometries = new Geometry[nbCoverage];

        //extract coverages and geometries
        for (int i = 0; i < nbCoverage; i++) {
            final Parameters covParam = castOrWrap(imageParams.get(i));
            final GridCoverage2D coverage = covParam.getValue(COVERAGE_PARAM);
            final CoordinateReferenceSystem coverageCrs = coverage.getCoordinateReferenceSystem2D();
            Geometry geometry = covParam.getValue(CLIP_PARAM);
            //ensure geometry is in coverage crs
            try {
                geometry = JTS.transform(geometry, coverageCrs);
            } catch (MismatchedDimensionException | TransformException | FactoryException ex) {
                throw new ProcessException(ex.getMessage(), this, ex);
            }

            inGridCoverages[i] = coverage;
            if (outImageReference == null) {
                outImageReference = coverage.getRenderedImage();
            }

            //intersect geometry with coverage envelope
            final Geometry envelopeGeometry = GeometricUtilities.toJTSGeometry(coverage.getEnvelope(), WrapResolution.NONE);
            JTS.setCRS(geometry, coverage.getCoordinateReferenceSystem2D());
            final Geometry geometryClipped = geometry.intersection(envelopeGeometry);
            JTS.setCRS(geometryClipped,coverageCrs);
            clipGeometries[i] = geometryClipped;
        }

        //compute output grid
        final GridGeometry2D outGridGeom;
        final CoordinateReferenceSystem outCrs;
        final MathTransform2D outGridToCrs;
        final MathTransform2D outCrsToGrid;
        try {
            outGridGeom = getOutputGridGeometry(inGridCoverages);
            outCrs = outGridGeom.getCoordinateReferenceSystem2D();
            outGridToCrs = outGridGeom.getGridToCRS2D();
            outCrsToGrid = outGridToCrs.inverse();
        } catch (TransformException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
        final int outWidth = outGridGeom.getExtent().getSpan(0);
        final int outHeight = outGridGeom.getExtent().getSpan(1);


        //convert and prepare all geometries to output crs
        final PreparedGeometry[] clips = new PreparedGeometry[nbCoverage];

        for (int i = 0; i < nbCoverage; i++) {
            Geometry geometry = clipGeometries[i];
            try {
                //convert to output crs
                geometry = JTS.transform(geometry, outCrs);
                //convert to output grid crs
                geometry = JTS.transform(geometry, outCrsToGrid);
            } catch (MismatchedDimensionException | TransformException | FactoryException e) {
                throw new ProcessException(e.getMessage(),this,e);
            }
            clips[i] = PGF.create(geometry);
        }

        //compute output grid crs to source coverage grid crs.
        final MathTransform[] outGridCSToSourceGridCS = new MathTransform[nbCoverage];
        for (int i = 0; i < nbCoverage; i++) {
            try {
                final CoordinateReferenceSystem sourceCrs = inGridCoverages[i].getCoordinateReferenceSystem2D();
                final MathTransform outCrsToSourceCrs = CRS.findOperation(outCrs, sourceCrs, null).getMathTransform();
                final MathTransform sourceCrsToGrid = inGridCoverages[i].getGridGeometry().getGridToCRS2D().inverse();

                outGridCSToSourceGridCS[i] = concatenate(
                        outGridToCrs,
                        outCrsToSourceCrs,
                        sourceCrsToGrid
                );
            } catch (NoninvertibleTransformException | FactoryException e) {
                throw new ProcessException(e.getMessage(),this,e);
            }
        }

        final BufferedImage outImage = BufferedImages.createImage(outWidth, outHeight, outImageReference);
        final WritableRaster outRaster = outImage.getRaster();

        final double[] pixelBuffer = new double[outImage.getSampleModel().getNumBands()];
        final double[] imgPoint = new double[2];
        final Coordinate coord = new Coordinate(0, 0);
        final Point geoPoint = GF.createPoint(coord);
        for (int x = 0; x < outWidth; x++) {
            for (int y = 0; y < outHeight; y++) {
                coord.x = x;
                coord.y = y;
                //clear geometry caches, we need to do it ourself
                //since we modified the coordinates directly
                geoPoint.geometryChanged();
                for (int i = 0; i < clips.length; i++) {
                    if (clips[i].intersects(geoPoint)) {
                        imgPoint[0] = x;
                        imgPoint[1] = y;
                        try {
                            outGridCSToSourceGridCS[i].transform(imgPoint, 0, imgPoint, 0, 1);
                        } catch (TransformException e) {
                            throw new ProcessException(e.getMessage(),this,e);
                        }
                        final WritableRaster inRaster = ((BufferedImage)inGridCoverages[i].getRenderedImage()).getRaster();
                        inRaster.getPixel((int)imgPoint[0], (int)imgPoint[1], pixelBuffer);
                        outRaster.setPixel(x, y, pixelBuffer);
                        break;
                    }
                }
            }
        }

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName("imageOut");
        gcb.setRenderedImage(outImage);
        gcb.setGridGeometry(outGridGeom);

        final GridCoverage2D gridCoverage2d = gcb.getGridCoverage2D();
        final ParameterValue<?> gridCoverageParamOut = outputParameters.parameter(COVERAGE_PARAM.getName().getCode());
        gridCoverageParamOut.setValue(gridCoverage2d);
    }


    private static GridGeometry2D getOutputGridGeometry(GridCoverage2D[] coverages) throws TransformException {

        //select the first coverage CRS as output crs
        //todo : use a better algorithm to find best crs
        final GeneralEnvelope envelope = new GeneralEnvelope(coverages[0].getGridGeometry().getEnvelope2D());
        final CoordinateReferenceSystem crs = envelope.getCoordinateReferenceSystem();

        //compute most accurate resolution for this crs
        final double[] resolution = {Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY};

        for (GridCoverage2D coverage : coverages) {
            final GridGeometry2D gridGeometry = coverage.getGridGeometry();
            final double[] res = gridGeometry.getResolution();
            final Envelope2D covEnv = gridGeometry.getEnvelope2D();

            final double[] cdtRes = ReferencingUtilities.convertResolution(covEnv, res, crs);
            resolution[0] = Math.min(resolution[0], cdtRes[0]);
            resolution[1] = Math.min(resolution[1], cdtRes[1]);

            //expand envelope
            envelope.add(Envelopes.transform(covEnv, crs));
        }

        final int outWidth = (int)Math.ceil(envelope.getSpan(0) / resolution[0]);
        final int outHeight = (int)Math.ceil(envelope.getSpan(1) / resolution[1]);

        final Matrix3 matrix = new Matrix3(
            resolution[0],    0,              envelope.getMinimum(0),
            0,                -resolution[1], envelope.getMinimum(1) + outHeight*resolution[1],
            0,                0,              1
        );
        final LinearTransform gridToGeo = MathTransforms.linear(matrix);

        final GridEnvelope2D extent = new GridEnvelope2D(0, 0, outWidth, outHeight);
        return new GridGeometry2D(extent, PixelOrientation.UPPER_LEFT, gridToGeo, crs, null);
    }

}
