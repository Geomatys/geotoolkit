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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.List;
import java.util.Map.Entry;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.IncompleteGridGeometryException;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.parameter.Parameters;
import static org.apache.sis.parameter.Parameters.castOrWrap;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.matrix.Matrix3;
import org.apache.sis.referencing.operation.transform.LinearTransform;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import static org.apache.sis.referencing.operation.transform.MathTransforms.concatenate;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.geometry.GeometricUtilities;
import org.geotoolkit.geometry.GeometricUtilities.WrapResolution;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.geometry.jts.awt.JTSGeometryJ2D;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.interpolation.GridFactory;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import static org.geotoolkit.processing.coverage.compose.ComposeDescriptor.*;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.metadata.spatial.PixelOrientation;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
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

    Compose(ParameterValueGroup input) {
        super(ComposeDescriptor.INSTANCE, input);
    }

    public Compose(List<Entry<GridCoverage,Geometry[]>> layers, GridGeometry2D gridGeom) {
        this(asParameters(layers,gridGeom));
    }

    private static ParameterValueGroup asParameters(List<Entry<GridCoverage,Geometry[]>> layers, GridGeometry2D gridGeom){
        final Parameters params = Parameters.castOrWrap(INPUT.createValue());

        final Parameters firstLayer = Parameters.castOrWrap(params.groups(LAYER_PARAM.getName().getCode()).get(0));
        firstLayer.getOrCreate(COVERAGE_PARAM).setValue(layers.get(0).getKey());
        firstLayer.getOrCreate(INCLUDE_PARAM).setValue(layers.get(0).getValue()[0]);
        firstLayer.getOrCreate(EXCLUDE_PARAM).setValue(layers.get(0).getValue()[1]);

        for (int i=1,n=layers.size();i<n;i++) {
            final Entry<GridCoverage,Geometry[]> entry = layers.get(i);
            final Parameters layer = Parameters.castOrWrap(params.addGroup(LAYER_PARAM.getName().getCode()));
            layer.getOrCreate(COVERAGE_PARAM).setValue(entry.getKey());
            layer.getOrCreate(INCLUDE_PARAM).setValue(entry.getValue()[0]);
            layer.getOrCreate(EXCLUDE_PARAM).setValue(entry.getValue()[1]);
        }

        if (gridGeom!=null) {
            params.getOrCreate(GRID_PARAM).setValue(gridGeom);
        }

        return params;
    }

    public GridCoverage executeNow() throws ProcessException {
        execute();
        return outputParameters.getValue(ComposeDescriptor.COVERAGE_PARAM);
    }

    @Override
    protected void execute() throws ProcessException {

        final List<ParameterValueGroup> imageParams = inputParameters.groups(LAYER_PARAM.getName().getCode());

        RenderedImage outImageReference = null;
        SampleDimension[] sampleDimensions = null;

        final int nbCoverage = imageParams.size();
        final GridCoverage[] inGridCoverages = new GridCoverage[nbCoverage];
        final PixelIterator[] inCursors = new PixelIterator[nbCoverage];
        final int[][] inSizes = new int[nbCoverage][2];
        final Geometry[] includeGeometries = new Geometry[nbCoverage];
        final Geometry[] excludeGeometries = new Geometry[nbCoverage];

        //extract coverages and geometries
        for (int i = 0; i < nbCoverage; i++) {
            final Parameters covParam = castOrWrap(imageParams.get(i));
            final GridCoverage coverage = covParam.getValue(COVERAGE_PARAM);

            //extract image informations
            inGridCoverages[i] = coverage;
            final RenderedImage covImg = coverage.render(null);
            inCursors[i] = PixelIterator.create(covImg);
            inSizes[i][0] = covImg.getWidth()-1;
            inSizes[i][1] = covImg.getHeight()-1;
            if (outImageReference == null) {
                outImageReference = coverage.render(null);
                sampleDimensions = coverage.getSampleDimensions().toArray(new SampleDimension[0]);
            }

            includeGeometries[i] = covParam.getValue(INCLUDE_PARAM);
            excludeGeometries[i] = covParam.getValue(EXCLUDE_PARAM);
        }

        //compute output grid
        GridGeometry2D outGridGeom = (GridGeometry2D) inputParameters.getValue(GRID_PARAM);
        if(outGridGeom == null) {
            try {
                outGridGeom = getOutputGridGeometry(inGridCoverages);
            } catch (TransformException ex) {
                throw new ProcessException(ex.getMessage(), this, ex);
            }
        }
        final CoordinateReferenceSystem outCrs = outGridGeom.getCoordinateReferenceSystem2D();
        final AffineTransform2D outGridToCrs = (AffineTransform2D) outGridGeom.getGridToCRS(PixelInCell.CELL_CORNER);
        final AffineTransform2D outCrsToGrid;
        try {
            outCrsToGrid = outGridToCrs.inverse();
        } catch (TransformException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
        final int outWidth = Math.toIntExact(outGridGeom.getExtent().getSize(0));
        final int outHeight = Math.toIntExact(outGridGeom.getExtent().getSize(1));

        //convert and convert all geometries to output crs as a bit mask
        final WritableRaster[] clips = new WritableRaster[nbCoverage];

        for (int i = 0; i < nbCoverage; i++) {
            final GridGeometry2D g2d = GridGeometry2D.castOrCopy(inGridCoverages[i].getGridGeometry());
            final BufferedImage image = new BufferedImage(outWidth, outHeight, BufferedImage.TYPE_BYTE_BINARY);
            final CoordinateReferenceSystem coverageCrs = g2d.getCoordinateReferenceSystem2D();
            try {
                MathTransform covCrstoOutCrs = CRS.findOperation(coverageCrs, outCrs, null).getMathTransform();

                //paint in white valid area
                final Graphics2D g = (Graphics2D) image.getGraphics();
                g.setTransform(outCrsToGrid); //convert to output grid crs

                //set a clip to coverage envelope
                Geometry clip = GeometricUtilities.toJTSGeometry(g2d.getEnvelope(), WrapResolution.NONE);
                clip.setUserData(coverageCrs);
                g.setClip(new JTSGeometryJ2D(clip,covCrstoOutCrs));

                //set the valid area
                g.setColor(Color.WHITE);
                if (includeGeometries[i]==null) {
                    g.setTransform(new AffineTransform());
                    g.fillRect(0, 0, outWidth, outHeight);
                    g.setTransform(outCrsToGrid);
                } else {
                    g.fill(new JTSGeometryJ2D(includeGeometries[i],covCrstoOutCrs));
                }

                //remove exclusion geometry
                if (excludeGeometries[i]!=null) {
                    covCrstoOutCrs = CRS.findOperation(JTS.findCoordinateReferenceSystem(excludeGeometries[i]), outCrs, null).getMathTransform();
                    g.setColor(Color.BLACK);
                    g.fill(new JTSGeometryJ2D(excludeGeometries[i],covCrstoOutCrs));
                }
                g.dispose();

            } catch (Exception e) {
                throw new ProcessException(e.getMessage(),this,e);
            }
            clips[i] = image.getRaster();
        }

        //compute output grid crs to source coverage grid crs.
        final MathTransform2D[] outGridCSToSourceGridCS = new MathTransform2D[nbCoverage];
        final Rectangle outRect = new Rectangle(0, 0, outWidth, outHeight);
        final GridFactory gf = new GridFactory(0.3);
        for (int i = 0; i < nbCoverage; i++) {
            try {
                final GridGeometry2D g2d = GridGeometry2D.castOrCopy(inGridCoverages[i].getGridGeometry());
                final CoordinateReferenceSystem sourceCrs = g2d.getCoordinateReferenceSystem2D();
                final MathTransform outCrsToSourceCrs = CRS.findOperation(outCrs, sourceCrs, null).getMathTransform();
                final MathTransform sourceCrsToGrid = g2d.getGridToCRS2D().inverse();

                final MathTransform tmpTr = concatenate(
                        outGridToCrs,
                        outCrsToSourceCrs,
                        sourceCrsToGrid
                );

                if (tmpTr instanceof MathTransform2D) {
                    outGridCSToSourceGridCS[i] = (MathTransform2D) tmpTr;
                } else {
                    throw new ProcessException("Cannot deduce 2D transform from given data.", this);
                }

                try {
                    //try to optimize it
                    Object obj = gf.create(outGridCSToSourceGridCS[i], outRect);
                    if (obj instanceof AffineTransform) {
                        outGridCSToSourceGridCS[i] = new AffineTransform2D((AffineTransform)obj);
                    }
                } catch (Exception e) {}

            } catch (NoninvertibleTransformException | FactoryException e) {
                throw new ProcessException(e.getMessage(),this,e);
            }
        }

        final BufferedImage outImage = BufferedImages.createImage(outWidth, outHeight, outImageReference);
        final WritableRaster outRaster = outImage.getRaster();

        final double[] pixelBuffer = new double[outImage.getSampleModel().getNumBands()];
        final WritablePixelIterator outIterator = new PixelIterator.Builder().createWritable(outRaster);
        final java.awt.Point clipPos = new java.awt.Point();
        while (outIterator.next()) {
            final java.awt.Point outPos = outIterator.getPosition();
            for (int i = 0; i < clips.length; i++) {
                if (clips[i].getSample(outPos.x, outPos.y, 0) == 1) {
                    try {
                        outGridCSToSourceGridCS[i].transform(outPos, clipPos);
                    } catch (TransformException e) {
                        throw new ProcessException(e.getMessage(), this, e);
                    }
                    if (clipPos.x < 0 || clipPos.y < 0 || clipPos.x > inSizes[i][0] || clipPos.y > inSizes[i][1])
                        continue;
                    inCursors[i].moveTo(clipPos.x, clipPos.y);
                    inCursors[i].getPixel(pixelBuffer);
                    outIterator.setPixel(pixelBuffer);
                    break;
                }
            }
        }

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setValues(outImage);
        gcb.setDomain(outGridGeom);
        if (sampleDimensions!=null) gcb.setRanges(sampleDimensions);

        final GridCoverage gridCoverage2d = gcb.build();
        final ParameterValue<?> gridCoverageParamOut = outputParameters.parameter(COVERAGE_PARAM.getName().getCode());
        gridCoverageParamOut.setValue(gridCoverage2d);
    }


    private static GridGeometry2D getOutputGridGeometry(GridCoverage[] coverages) throws TransformException {

        //select the first coverage CRS as output crs
        //todo : use a better algorithm to find best crs
        final GeneralEnvelope envelope = new GeneralEnvelope(GridGeometry2D.castOrCopy(coverages[0].getGridGeometry()).getEnvelope2D());
        final CoordinateReferenceSystem crs = envelope.getCoordinateReferenceSystem();

        //compute most accurate resolution for this crs
        final double[] resolution = {Double.POSITIVE_INFINITY,Double.POSITIVE_INFINITY};

        for (GridCoverage coverage : coverages) {
            final GridGeometry gridGeometry = coverage.getGridGeometry();
            double[] res = null;
            try {
                res = gridGeometry.getResolution(true);
            } catch (IncompleteGridGeometryException ex) {
            }
            final Envelope2D covEnv = GridGeometry2D.castOrCopy(gridGeometry).getEnvelope2D();

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

        final GridExtent extent = new GridExtent(outWidth, outHeight);
        return new GridGeometry2D(extent, PixelOrientation.UPPER_LEFT, gridToGeo, crs);
    }

    @FunctionalInterface
    private static interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }
}
