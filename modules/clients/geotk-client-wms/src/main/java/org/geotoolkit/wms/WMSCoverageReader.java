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
package org.geotoolkit.wms;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.factory.FactoryFinder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.util.NamesExt;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;
import org.opengis.util.NameFactory;
import org.opengis.util.NameSpace;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.referencing.CRS;
import org.apache.sis.util.Utilities;
import org.geotoolkit.internal.referencing.CRSUtilities;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class WMSCoverageReader extends GridCoverageReader{

    static final Dimension DEFAULT_SIZE = new Dimension(256, 256);
    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.wms");

    public WMSCoverageReader(final WMSCoverageResource reference) {
        try {
            setInput(reference);
        } catch (CoverageStoreException ex) {
            //won't happen
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    @Override
    public void setInput(Object input) throws CoverageStoreException {
        if(!(input instanceof WMSCoverageResource)){
            throw new CoverageStoreException("Unsupported input type, can only be WMSCoverageReference.");
        }
        super.setInput(input);
    }

    @Override
    public WMSCoverageResource getInput() throws CoverageStoreException {
        return (WMSCoverageResource) super.getInput();
    }

    @Override
    public List<? extends GenericName> getCoverageNames() throws CoverageStoreException, CancellationException {
        final NameFactory dnf = FactoryFinder.getNameFactory(null);
        final GenericName name = getInput().getName();
        NameSpace ns = null;
        if (NamesExt.getNamespace(name) != null) {
            ns = dnf.createNameSpace(dnf.createGenericName(null, NamesExt.getNamespace(name)), null);
        }
        final GenericName gn = dnf.createLocalName(ns, name.tip().toString());
        return Collections.singletonList(gn);
    }

    @Override
    public GeneralGridGeometry getGridGeometry(final int index) throws CoverageStoreException, CancellationException {
        final WMSCoverageResource ref = getInput();
        //we only know the envelope,
        final GeneralGridGeometry gridGeom = new GeneralGridGeometry(null, null, ref.getBounds());
        return gridGeom;
    }

    @Override
    public List<GridSampleDimension> getSampleDimensions(final int index) throws CoverageStoreException, CancellationException {
        //unknowned
        return null;
    }

    @Override
    public GridCoverage read(final int index, GridCoverageReadParam param) throws CoverageStoreException, CancellationException {
        if(index != 0){
            throw new CoverageStoreException("Invalid Image index.");
        }

        if(param == null){
            param = new GridCoverageReadParam();
        }

        final int[] desBands = param.getDestinationBands();
        final int[] sourceBands = param.getSourceBands();
        if(desBands != null || sourceBands != null){
            throw new CoverageStoreException("Source or destination bands can not be used on WMS coverages.");
        }

        final WMSCoverageResource ref = getInput();
        final WebMapClient server = (WebMapClient)ref.getStore();

        CoordinateReferenceSystem crs = param.getCoordinateReferenceSystem();
        if (crs == null) {
            crs = ref.getBounds().getCoordinateReferenceSystem();
        }
        try {
            crs = CRSUtilities.getCRS2D(crs);
        } catch (TransformException ex) {
            throw new CoverageStoreException("WMS reading expect a CRS whose first component is 2D.", ex);
        }

        GeneralEnvelope env;
        if (param.getEnvelope() == null) {
            env = new GeneralEnvelope(ref.getBounds());
        } else {
            env = new GeneralEnvelope(param.getEnvelope());
        }

        if (env.getCoordinateReferenceSystem() != null && !Utilities.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(), crs)) {
            try {
                env = GeneralEnvelope.castOrCopy(Envelopes.transform(env, crs));
            } catch (TransformException ex) {
                throw new CoverageStoreException("Could not transform coverage envelope to given crs.", ex);
            }
        }

        final Dimension dim;
        if (param.getResolution() != null) {
            final double[] resolution = param.getResolution();
            // WARNING : we use 0 and 1 indices because we determined input reference system starts
            // with 2D component above (see CRSUtilities.getCRS2D())
            dim = new Dimension((int)(env.getSpan(0)/resolution[0]), (int)(env.getSpan(1)/resolution[1]));
        } else {
            dim = DEFAULT_SIZE;
        }

        final GetMapRequest request = server.createGetMap();

        //Filling the request header map from the map of the layer's server
        final Map<String, String> headerMap = server.getRequestHeaderMap();
        if (headerMap != null) {
            request.getHeaderMap().putAll(headerMap);
        }

        try {
            ref.prepareQuery(request, env, dim, null);
            LOGGER.fine(request.getURL().toExternalForm());
        } catch (Exception ex) {
            throw new CoverageStoreException(ex.getMessage(), ex);
        }

        //read image
        try (final InputStream stream = request.getResponseStream()) {
            final BufferedImage image = ImageIO.read(stream);

            final CoordinateReferenceSystem crs2d = CRS.getHorizontalComponent(env.getCoordinateReferenceSystem());
            final Envelope env2D = Envelopes.transform(env, crs2d);
            final AffineTransform gridToCRS = ReferencingUtilities.toAffine(dim, env2D);

            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setName(ref.getCombinedLayerNames());
            gcb.setRenderedImage(image);
            gcb.setPixelAnchor(PixelInCell.CELL_CORNER);
            gcb.setGridToCRS(gridToCRS);
            gcb.setCoordinateReferenceSystem(crs2d);
            return gcb.build();

        } catch (IOException|TransformException ex) {
            throw new CoverageStoreException(ex.getMessage(), ex);
        }
    }

    @Override
    public void dispose() throws CoverageStoreException {
        //nothing to dispose, we must preserve the input
    }

    @Override
    public void reset() throws CoverageStoreException {
        //nothing to reset, we must preserve the input
    }

}
