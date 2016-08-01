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
package org.geotoolkit.display2d.service;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.coverage.grid.GridCoverage;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;
import org.opengis.util.NameFactory;
import org.opengis.util.NameSpace;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.util.Utilities;

/**
 * Manipulate a SceneDef as a CoverageReader.
 * This allow to manipulate an aggregation of several different layers as if
 * it was a single coverage layer.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class PortrayalCoverageReader extends GridCoverageReader {

    private final SceneDef scene;
    private final GenericName name;
    private String contextName;

    public PortrayalCoverageReader(final SceneDef scene) {
        this.scene = scene;

        contextName = scene.getContext().getName();
        if(contextName == null){
            contextName = "portrayal";
        }

        final NameFactory dnf = FactoryFinder.getNameFactory(null);
        final NameSpace ns = dnf.createNameSpace(dnf.createGenericName(null, contextName), null);
        name = dnf.createLocalName(ns, contextName);
    }

    @Override
    public List<? extends GenericName> getCoverageNames() throws CoverageStoreException, CancellationException {
        return Collections.singletonList(name);
    }

    @Override
    public GeneralGridGeometry getGridGeometry(int index) throws CoverageStoreException, CancellationException {
        //we only know the envelope
        final GeneralGridGeometry gridGeom;
        try {
            gridGeom = new GeneralGridGeometry(null, null, scene.getContext().getBounds());
        } catch (IOException ex) {
            throw new CoverageStoreException(ex.getMessage(),ex);
        }
        return gridGeom;
    }

    @Override
    public List<GridSampleDimension> getSampleDimensions(int index) throws CoverageStoreException, CancellationException {
        return null;
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
            throw new CoverageStoreException("Source or destination bands can not be used on portrayal images.");
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
            if(!Utilities.equalsIgnoreMetadata(paramEnv.getCoordinateReferenceSystem(),crs)){
                throw new CoverageStoreException("Invalid parameters : envelope crs do not match given crs.");
            }
        }else if(paramEnv != null){
            //use the envelope crs
            crs = paramEnv.getCoordinateReferenceSystem();
        }else if(crs != null){
            //use the given crs
            paramEnv = getGridGeometry(0).getEnvelope();
            try {
                paramEnv = Envelopes.transform(paramEnv, crs);
            } catch (TransformException ex) {
                throw new CoverageStoreException("Could not transform coverage envelope to given crs.");
            }
        }

        //estimate resolution if not given
        if(resolution == null){
            //we arbitrarly choose 1000 pixel on first axis, layers can have an infinite resolution.
            resolution = new double[2];
            resolution[0] = paramEnv.getSpan(0)/1000;
            resolution[1] = resolution[0] * (paramEnv.getSpan(1)/paramEnv.getSpan(0));
        }

        //calculate final image dimension
        final Dimension dim = new Dimension(
                (int)(paramEnv.getSpan(0) / resolution[0]),
                (int)(paramEnv.getSpan(1) / resolution[1]));
        //calculate final grid to crs transform
        final AffineTransform gridToCRS = ReferencingUtilities.toAffine(dim, paramEnv);


        final CanvasDef canvas = new CanvasDef(dim, null);
        final ViewDef view = new ViewDef(paramEnv);

        final RenderedImage image;
        try {
            image = DefaultPortrayalService.portray(canvas, scene, view);
        } catch (PortrayalException ex) {
            throw new CoverageStoreException(ex.getMessage(),ex);
        }

        //build the coverage ---------------------------------------------------
        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName(contextName);
        gcb.setRenderedImage(image);
        gcb.setPixelAnchor(PixelInCell.CELL_CORNER);
        gcb.setGridToCRS(gridToCRS);
        gcb.setCoordinateReferenceSystem(crs);
        return gcb.build();
    }

}
