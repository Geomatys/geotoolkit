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
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.factory.FactoryFinder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.feature.type.DefaultName;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.util.GenericName;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;
import org.opengis.util.NameFactory;
import org.opengis.util.NameSpace;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSCoverageReader extends GridCoverageReader{

    private static final Logger LOGGER = Logging.getLogger(WMSCoverageReader.class);

    public WMSCoverageReader(final WMSCoverageReference reference) {
        try {
            setInput(reference);
        } catch (CoverageStoreException ex) {
            //won't happen
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    @Override
    public void setInput(Object input) throws CoverageStoreException {
        if(!(input instanceof WMSCoverageReference)){
            throw new CoverageStoreException("Unsupported input type, can only be WMSCoverageReference.");
        }
        super.setInput(input);
    }

    @Override
    public WMSCoverageReference getInput() throws CoverageStoreException {
        return (WMSCoverageReference) super.getInput();
    }

    @Override
    public List<? extends GenericName> getCoverageNames() throws CoverageStoreException, CancellationException {
        final NameFactory dnf = FactoryFinder.getNameFactory(null);
        final GenericName name = getInput().getName();
        NameSpace ns = null;
        if (DefaultName.getNamespace(name) != null) {
            ns = dnf.createNameSpace(dnf.createGenericName(null, DefaultName.getNamespace(name)), null);
        }
        final GenericName gn = dnf.createLocalName(ns, name.tip().toString());
        return Collections.singletonList(gn);
    }

    @Override
    public GeneralGridGeometry getGridGeometry(final int index) throws CoverageStoreException, CancellationException {
        final WMSCoverageReference ref = getInput();
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

        final WMSCoverageReference ref = getInput();
        final WebMapClient server = (WebMapClient)ref.getStore();

        CoordinateReferenceSystem crs = param.getCoordinateReferenceSystem();
        Envelope wantedEnv = param.getEnvelope();
        double[] resolution = param.getResolution();

        //verify envelope and crs
        if(crs == null && wantedEnv == null){
            //use the max extent
            wantedEnv = ref.getBounds();
            crs = wantedEnv.getCoordinateReferenceSystem();
        }else if(crs != null && wantedEnv != null){
            //check the envelope crs matches given crs
            if(!CRS.equalsIgnoreMetadata(wantedEnv.getCoordinateReferenceSystem(),crs)){
                throw new CoverageStoreException("Invalid parameters : envelope crs do not match given crs.");
            }
        }else if(wantedEnv != null){
            //use the envelope crs
            crs = wantedEnv.getCoordinateReferenceSystem();
        }else if(crs != null){
            //use the given crs
            wantedEnv = ref.getBounds();
            try {
                wantedEnv = CRS.transform(wantedEnv, crs);
            } catch (TransformException ex) {
                throw new CoverageStoreException("Could not transform coverage envelope to given crs.");
            }
        }

        //estimate resolution if not given
        if(resolution == null){
            //we arbitrarly choose 1000 pixel on first axis, wms layer have infinite resolution.
            resolution = new double[2];
            resolution[0] = wantedEnv.getSpan(0)/1000;
            resolution[1] = resolution[0] * (wantedEnv.getSpan(1)/wantedEnv.getSpan(0));
        }


        final GeneralEnvelope env = new GeneralEnvelope(wantedEnv);
        final GetMapRequest request = server.createGetMap();

        //Filling the request header map from the map of the layer's server
        final Map<String, String> headerMap = server.getRequestHeaderMap();
        if (headerMap != null) {
            request.getHeaderMap().putAll(headerMap);
        }

        //calculate image dimension
        final Dimension dim = new Dimension(
                (int)(env.getSpan(0) / resolution[0]),
                (int)(env.getSpan(1) / resolution[1]));

        try {
            ref.prepareQuery(request, env, dim, null);
            System.out.println(request.getURL());
        } catch (TransformException ex) {
            throw new CoverageStoreException(ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new CoverageStoreException(ex.getMessage(), ex);
        }

        //read image
        BufferedImage image = null;
        InputStream stream = null;
        try {
            stream = request.getResponseStream();
            image = ImageIO.read(stream);

            final CoordinateReferenceSystem crs2d = CRSUtilities.getCRS2D(env.getCoordinateReferenceSystem());
            final Envelope env2D = CRS.transform(env, crs2d);
            final AffineTransform gridToCRS = ReferencingUtilities.toAffine(dim, env2D);

            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setName(ref.getCombinedLayerNames());
            gcb.setRenderedImage(image);
            gcb.setPixelAnchor(PixelInCell.CELL_CORNER);
            gcb.setGridToCRS(gridToCRS);
            gcb.setCoordinateReferenceSystem(crs2d);
            return gcb.build();

        } catch (IOException ex) {
            throw new CoverageStoreException(ex.getMessage(), ex);
        } catch (TransformException ex) {
            throw new CoverageStoreException(ex.getMessage(), ex);
        } finally {
            if(stream != null){
                try {
                    stream.close();
                } catch (IOException ex) {
                    throw new CoverageStoreException(ex.getMessage(), ex);
                }
            }
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
