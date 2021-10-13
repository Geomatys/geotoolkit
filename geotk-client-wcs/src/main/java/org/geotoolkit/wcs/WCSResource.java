/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.wcs;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverage2D;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.internal.storage.AbstractGridResource;
import org.apache.sis.internal.storage.StoreResource;
import org.apache.sis.referencing.CRS;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.Utilities;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.wcs.xml.v100.CoverageOfferingBriefType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class WCSResource extends AbstractGridResource implements StoreResource {

    static final Dimension DEFAULT_SIZE = new Dimension(256, 256);

    private final WebCoverageClient server;
    private final CoverageOfferingBriefType brief;
    private final String format = "image/png";

    public WCSResource(WebCoverageClient server, CoverageOfferingBriefType brief) {
        super(null);
        this.server = server;
        this.brief = brief;
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return new GridGeometry(null, brief.getLonLatEnvelope(), GridOrientation.HOMOTHETY);
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        if (domain == null) {
            domain = getGridGeometry();
        }

        if (range != null && range.length != 0) {
            throw new DataStoreException("Source or destination bands can not be used on WMS coverages.");
        }

        GeneralEnvelope env;
        if (domain.isDefined(GridGeometry.ENVELOPE)) {
            env = new GeneralEnvelope(domain.getEnvelope());
        } else {
            env = new GeneralEnvelope(getGridGeometry().getEnvelope());
        }

        CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();

        final CoordinateReferenceSystem crs2d;
        try {
            crs2d = CRSUtilities.getCRS2D(crs);
        } catch (TransformException ex) {
            throw new DataStoreException("WMS reading expect a CRS whose first component is 2D.", ex);
        }

        final CoordinateReferenceSystem candidateCRS = env.getDimension() > 2? crs : crs2d;
        if (env.getCoordinateReferenceSystem() == null) {
            env.setCoordinateReferenceSystem(candidateCRS);
        } else if (!Utilities.equalsIgnoreMetadata(env.getCoordinateReferenceSystem(), candidateCRS)) {
            try {
                env = GeneralEnvelope.castOrCopy(Envelopes.transform(env, candidateCRS));
            } catch (TransformException ex) {
                throw new DataStoreException("Could not transform coverage envelope to given crs.", ex);
            }
        }

        final Dimension dim;
        if (domain.isDefined(GridGeometry.EXTENT)) {
            GridExtent extent = domain.getExtent();
            dim = new Dimension(
                    (int) extent.getSize(0),
                    (int) extent.getSize(1));
        } else {
            dim = DEFAULT_SIZE;
        }

        final GetCoverageRequest request = server.createGetCoverage();
        request.setEnvelope(env);
        request.setDimension(dim);
        request.setCoverage(brief.getName());
        request.setResponseCRS(env.getCoordinateReferenceSystem());
        request.setFormat(format);

        //read image
        try (final InputStream stream = request.getResponseStream()) {
            BufferedImage image = ImageIO.read(stream);

            //the envelope CRS may have been changed by prepareQuery method
            final CoordinateReferenceSystem resultCrs = CRS.getHorizontalComponent(env.getCoordinateReferenceSystem());
            final Envelope env2D = Envelopes.transform(env, resultCrs);
            //grid to crs returned is in corner
            final AffineTransform gridToCRS = ReferencingUtilities.toAffine(dim, env2D);

            //we must honor the number of sample dimensions we declared
            //some WMS services returned mixed sample and color model for better compression
            final List<SampleDimension> sampleDimensions = getSampleDimensions();
            if (sampleDimensions.size() != image.getSampleModel().getNumBands()) {
                BufferedImage cp;
                if (sampleDimensions.size() == 3) {
                    cp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
                } else if (sampleDimensions.size() == 4) {
                    cp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                } else {
                    throw new DataStoreException();
                }
                cp.createGraphics().drawRenderedImage(image, new AffineTransform());
                image = cp;
            }

            final GridExtent extent = new GridExtent(image.getWidth(), image.getHeight());
            final GridGeometry grid = new GridGeometry(extent, PixelInCell.CELL_CORNER, new AffineTransform2D(gridToCRS), resultCrs);
            return new GridCoverage2D(grid, sampleDimensions, image);

        } catch (IOException | TransformException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }

    }

    @Override
    public DataStore getOriginator() {
        return server;
    }

}
