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
package org.geotoolkit.process.coverage.copy;

import java.awt.Dimension;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.geotoolkit.coverage.CoverageReference;
import org.geotoolkit.coverage.CoverageStore;
import org.geotoolkit.coverage.GridMosaic;
import org.geotoolkit.coverage.Pyramid;
import org.geotoolkit.coverage.PyramidalModel;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.geometry.GeneralEnvelope;
import static org.geotoolkit.parameter.Parameters.*;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessException;
import static org.geotoolkit.process.coverage.copy.CopyCoverageStoreDescriptor.*;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.cs.DiscreteCoordinateSystemAxis;
import org.geotoolkit.storage.DataStoreException;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.coverage.grid.GridGeometry;
import org.opengis.feature.type.Name;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Copy a {@linkplain CoverageStore coverage store} into another one, that supports
 * a {@linkplain PyramidalModel pyramid model}.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public class CopyCoverageStoreProcess extends AbstractProcess {

    /**
     * Default constructor
     */
    public CopyCoverageStoreProcess(final ParameterValueGroup input) {
        super(INSTANCE,input);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute() throws ProcessException {
        final CoverageStore inStore = value(STORE_IN, inputParameters);
        final CoverageStore outStore = value(STORE_IN, inputParameters);

        try {
            for(Name n : inStore.getNames()){
                final CoverageReference ref = inStore.getCoverageReference(n);
                saveCoverage(outStore, ref);
            }
        } catch (DataStoreException ex) {
            throw new ProcessException(ex.getLocalizedMessage(), this, ex);
        } catch (TransformException ex) {
            throw new ProcessException(ex.getLocalizedMessage(), this, ex);
        }
    }

    /**
     * Save a {@linkplain CoverageReference coverage} into a {@linkplain CoverageStore coverage store}.
     *
     * @param outStore Coverage store in which to copy values.
     * @param inRef Coverage to store.
     * @throws DataStoreException
     * @throws TransformException
     */
    private void saveCoverage(final CoverageStore outStore, final CoverageReference inRef)
            throws DataStoreException, TransformException
    {
        final CoverageReference outRef = outStore.create(inRef.getName());
        if (!(outRef instanceof PyramidalModel)) {
            throw new DataStoreException("The given coverage reference is not a pyramidal model, "
                    + "this process only work with this kind of model.");
        }
        final PyramidalModel pm = (PyramidalModel) outRef;

        final GridCoverageReader reader = inRef.createReader();
        final GeneralGridGeometry globalGeom = reader.getGridGeometry(inRef.getImageIndex());
        final CoordinateReferenceSystem crs = globalGeom.getCoordinateReferenceSystem();
        final CoordinateSystem cs = crs.getCoordinateSystem();

        final Pyramid pyramid = pm.createPyramid(crs);

        // Stores additional coordinate system axes, to know how many pyramids should be created
        final List<List<Comparable>> possibilities = new ArrayList<List<Comparable>>();

        final int nbdim = cs.getDimension();
        for (int i = 2; i < nbdim; i++) {
            final CoordinateSystemAxis axis = cs.getAxis(i);
            if (axis instanceof DiscreteCoordinateSystemAxis) {
                final DiscreteCoordinateSystemAxis daxis = (DiscreteCoordinateSystemAxis) axis;
                final List<Comparable> values = new ArrayList<Comparable>();
                possibilities.add(values);
                final int nbval = daxis.length();
                for (int k = 0; k < nbval; k++) {
                    final Comparable c = daxis.getOrdinateAt(k);
                    values.add(c);
                }
            }
        }

        if (possibilities.isEmpty()) {
            //only a single image to insert
            saveMosaic(pm, pyramid, reader, inRef.getImageIndex(), null);

        } else {
            //multiple dimensions to insert
            final GeneralEnvelope env = new GeneralEnvelope(globalGeom.getEnvelope());
            final CopyCoverageStoreProcess.CombineIterator ite = new CopyCoverageStoreProcess.CombineIterator(possibilities, env);

            Envelope ce = ite.next();
            do {
                saveMosaic(pm, pyramid, reader, inRef.getImageIndex(), ce);
                ce = ite.next();
            } while (ce != null);
        }

    }

    /**
     * Save a {@linkplain Pyramid mosaic} via its {@linkplain PyramidalModel model}.
     *
     * @param pm The {@linkplain PyramidalModel model} of this pyramid. Must not be {@code null}.
     * @param pyramid {@link Pyramid} to store. Must not be {@code null}.
     * @param reader {@linplain GridCoverageReader reader} of the input coverage. Must not be {@code null}.
     * @param imageIndex Index of the image to read in the reader.
     * @param env {@link Envelope} of the pyramid.
     * @throws DataStoreException
     * @throws TransformException
     */
    private static void saveMosaic(final PyramidalModel pm, final Pyramid pyramid, final GridCoverageReader reader,
            final int imageIndex, final Envelope env) throws DataStoreException, TransformException {
        final GridCoverageReadParam params = new GridCoverageReadParam();
        if (env != null) {
            params.setEnvelope(env);
        }

        final GridCoverage coverage = reader.read(imageIndex, params);
        final GridGeometry gridgeom = coverage.getGridGeometry();
        final MathTransform gridToCRS = gridgeom.getGridToCRS();

        final double[] segment = new double[gridToCRS.getSourceDimensions() * 2];
        segment[gridToCRS.getSourceDimensions()] = 1;
        gridToCRS.transform(segment, 0, segment, 0, 2);
        final double scale = Math.abs(segment[0] - segment[gridToCRS.getTargetDimensions()]);

        final RenderedImage img = ((GridCoverage2D) coverage).getRenderedImage();
        final Dimension gridSize = new Dimension(1, 1);
        final Dimension TileSize = new Dimension(img.getWidth(), img.getHeight());
        final DirectPosition upperleft = coverage.getEnvelope().getUpperCorner();
        //envelope seems to lost its additional 2D+ values
        for (int i = 0, n = env.getDimension(); i < n; i++) {
            upperleft.setOrdinate(i, env.getMedian(i));
        }
        final GridMosaic mosaic = pm.createMosaic(pyramid.getId(), gridSize, TileSize, upperleft, scale);
        pm.writeTile(pyramid.getId(), mosaic.getId(), 0, 0, img);
    }


    /**
     * Iterator on pyramid envelopes.
     */
    private class CombineIterator implements Iterator<Envelope> {
        private final List<List<Comparable>> values;
        private final int[] positions;
        private final GeneralEnvelope baseEnvelope;
        private boolean finish = false;

        /**
         * Defines an iterator on given values with the given base envelope.
         *
         * @param values Values to iterate. Must not be {@code null}.
         * @param baseEnvelope Base envelope. Must not be {@code null}.
         */
        public CombineIterator(final List<List<Comparable>> values, final GeneralEnvelope baseEnvelope) {
            this.values = values;
            this.positions = new int[values.size()];
            this.baseEnvelope = baseEnvelope;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Envelope next() {
            if (finish) {
                return null;
            }

            for (int i = 0; i < positions.length; i++) {
                final Comparable c = values.get(i).get(positions[i]);
                Number n;
                if(c instanceof Number){
                    n = (Number) c;
                }else if(c instanceof Date){
                    n = ((Date)c).getTime();
                    //transform correctly value, unit type might have changed.
                    final CoordinateReferenceSystem baseCRS = DefaultTemporalCRS.JAVA;
                    final CoordinateReferenceSystem targetCRS = ((CompoundCRS)baseEnvelope.getCoordinateReferenceSystem()).getComponents().get(1+i);

                    //try to convert from one axis to the other
                    try{
                        final MathTransform trs = CRS.findMathTransform(baseCRS, targetCRS);
                        final double[] bv = new double[]{n.doubleValue()};
                        trs.transform(bv, 0, bv, 0, 1);
                        n = bv[0];
                    }catch(Exception ex){
                        fireWarningOccurred(ex.getMessage(), 0, ex);
                    }

                }else{
                    fireProcessFailed("Comparable type not supported : "+ c, null);
                    return null;
                }

                baseEnvelope.setRange(2+i, n.doubleValue(), n.doubleValue());

                //prepare next iteration
                if (i == positions.length - 1) {
                    positions[i] = positions[i] + 1;
                }
            }

            //prepare next iteration
            for (int i = positions.length - 1; i >= 0; i--) {
                if (positions[i] >= values.get(i).size()) {
                    if (i == 0) {
                        finish = true;
                        break;
                    }
                    //increment previous, restart this level at zero
                    positions[i] = 0;
                    positions[i - 1]++;
                }
            }

            return baseEnvelope;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {
            return !finish;
        }

        /**
         * Not implemented in this implementation.
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
