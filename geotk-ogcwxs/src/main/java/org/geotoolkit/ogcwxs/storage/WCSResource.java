/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2026, Geomatys
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
package org.geotoolkit.ogcwxs.storage;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverage2D;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.coverage.grid.PixelInCell;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.internal.shared.AffineTransform2D;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.base.StoreResource;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.ogcwxs.request.coverage.GetCoverage;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.geotoolkit.wcs.xml.v100.CoverageOfferingBriefType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 * Coverage Reference for a WCS coverage offering.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public final class WCSResource extends AbstractGridCoverageResource implements StoreResource {

    static final Dimension DEFAULT_SIZE = new Dimension(256, 256);

    private final WebCoverageClient server;
    private final CoverageOfferingBriefType brief;
    private final String format = "image/png";

    //lazily probed, generic band count for this coverage : ponytail: WCS 1.0.0's DescribeCoverage
    //rangeSet is not parsed here, band names/units are unavailable ; upgrade when real band
    //semantics (name, unit) are needed by a caller.
    private List<SampleDimension> sampleDimensions;

    WCSResource(WebCoverageClient server, CoverageOfferingBriefType brief) {
        super(null, false);
        this.server = server;
        this.brief = brief;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.ofNullable(brief.getName()).map(org.geotoolkit.util.NamesExt::create);
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return new GridGeometry(null, brief.getLonLatEnvelope(), GridOrientation.HOMOTHETY);
    }

    @Override
    public synchronized List<SampleDimension> getSampleDimensions() throws DataStoreException {
        if (sampleDimensions == null) {
            //probe a single pixel to learn the band count returned by the server
            sampleDimensions = toSampleDimensions(fetch(getGridGeometry(), new Dimension(1, 1)).getSampleModel().getNumBands());
        }
        return sampleDimensions;
    }

    private static List<SampleDimension> toSampleDimensions(int numBands) {
        final SampleDimension.Builder builder = new SampleDimension.Builder();
        final List<SampleDimension> dims = new java.util.ArrayList<>(numBands);
        for (int i = 0; i < numBands; i++) {
            dims.add(builder.setName(i).build());
            builder.clear();
        }
        return dims;
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        if (domain == null) {
            domain = getGridGeometry();
        }
        if (range != null && range.length != 0) {
            throw new DataStoreException("Source or destination bands can not be used on WCS coverages.");
        }

        GeneralEnvelope env;
        if (domain.isDefined(GridGeometry.ENVELOPE)) {
            env = new GeneralEnvelope(domain.getEnvelope());
        } else {
            env = new GeneralEnvelope(getGridGeometry().getEnvelope());
        }

        final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
        final CoordinateReferenceSystem crs2d;
        try {
            crs2d = CRSUtilities.getCRS2D(crs);
        } catch (TransformException ex) {
            throw new DataStoreException("WCS reading expect a CRS whose first component is 2D.", ex);
        }

        final CoordinateReferenceSystem candidateCRS = env.getDimension() > 2 ? crs : crs2d;
        if (env.getCoordinateReferenceSystem() == null) {
            env.setCoordinateReferenceSystem(candidateCRS);
        } else if (!CRS.equivalent(env.getCoordinateReferenceSystem(), candidateCRS)) {
            try {
                env = GeneralEnvelope.castOrCopy(Envelopes.transform(env, candidateCRS));
            } catch (TransformException ex) {
                throw new DataStoreException("Could not transform coverage envelope to given crs.", ex);
            }
        }

        final Dimension dim;
        if (domain.isDefined(GridGeometry.EXTENT)) {
            final GridExtent extent = domain.getExtent();
            dim = new Dimension((int) extent.getSize(0), (int) extent.getSize(1));
        } else {
            dim = DEFAULT_SIZE;
        }

        try {
            final java.awt.image.RenderedImage image = fetch(new GridGeometry(null, env, null), dim);

            final CoordinateReferenceSystem resultCrs = CRS.getHorizontalComponent(env.getCoordinateReferenceSystem());
            final Envelope env2D = Envelopes.transform(env, resultCrs);
            //grid to crs returned is in corner
            final AffineTransform gridToCRS = ReferencingUtilities.toAffine(dim, env2D);

            final List<SampleDimension> dims = toSampleDimensions(image.getSampleModel().getNumBands());
            final GridExtent extent = new GridExtent(image.getWidth(), image.getHeight());
            final GridGeometry grid = new GridGeometry(extent, PixelInCell.CELL_CORNER, new AffineTransform2D(gridToCRS), resultCrs);
            return new GridCoverage2D(grid, dims, image);
        } catch (TransformException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    /**
     * Requests {@code GetCoverage} for this coverage over the given domain and decodes the response
     * image.
     */
    private java.awt.image.RenderedImage fetch(GridGeometry domain, Dimension dim) throws DataStoreException {
        final GeneralEnvelope env = new GeneralEnvelope(domain.getEnvelope());

        final GetCoverage request = new GetCoverage();
        request.setCoverage(brief.getName());
        request.setEnvelope(env);
        request.setDimension(dim);
        request.setResponseCRS(env.getCoordinateReferenceSystem());
        request.setFormat(format);

        try {
            final byte[] body = server.getApi().getCoverage(request).send().body();
            final java.awt.image.RenderedImage image = ImageIO.read(new java.io.ByteArrayInputStream(body));
            if (image == null) {
                throw new DataStoreException("Server response for coverage " + brief.getName() + " could not be decoded as an image.");
            }
            return image;
        } catch (IOException | ServiceException ex) {
            throw new DataStoreException(ex.getMessage(), ex);
        }
    }

    @Override
    public DataStore getOriginator() {
        return server;
    }

}
