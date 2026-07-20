/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.ogcapi.client.coverages.CoverageApi;
import org.geotoolkit.ogcapi.dto.common.CollectionDescription;
import org.geotoolkit.ogcapi.dto.common.Extent;
import org.geotoolkit.ogcapi.dto.coverage.Axis;
import org.geotoolkit.ogcapi.dto.coverage.DataRecord;
import org.geotoolkit.ogcapi.dto.coverage.DataRecordField;
import org.geotoolkit.ogcapi.dto.coverage.DomainSet;
import org.geotoolkit.ogcapi.dto.coverage.GeneralGrid;
import org.geotoolkit.ogcapi.dto.coverage.IndexAxis;
import org.geotoolkit.ogcapi.dto.coverage.IrregularAxis;
import org.geotoolkit.ogcapi.request.coverage.GetCoverage;
import org.geotoolkit.ogcapi.request.coverage.GetCoverageDomainSet;
import org.geotoolkit.ogcapi.request.coverage.GetCoverageRangeType;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 * Coverage view of a collection, exposed through the OGC API - Coverages
 * domainset/rangetype/coverage endpoints.
 *
 * @see <a href="https://docs.ogc.org/DRAFTS/19-087.html">OGC API - Coverages</a>
 * @author Quentin Bialota (Geomatys)
 */
public final class CoverageResource extends AbstractGridCoverageResource {

    private final CollectionResource parent;
    private final CollectionDescription description;
    private final CoverageApi api;

    private GridGeometry gridGeometry;
    private List<SampleDimension> sampleDimensions;

    /**
     * @param parent collection this coverage belongs to
     * @param description full description of the parent collection, used to reach the coverage id and links
     */
    CoverageResource(CollectionResource parent, CollectionDescription description) {
        super(parent);
        this.parent = parent;
        this.description = description;
        final OpenApiConfiguration config = parent.api.getConfiguration();
        this.api = new CoverageApi(config);
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(Names.createLocalName(null, null, description.getId()));
    }

    /**
     * Fetches the domainSet (CIS 1.1 GeneralGrid) from the server and maps it to a
     * {@link GridGeometry}, caching the result.
     *
     * @return the grid geometry of this coverage
     * @throws DataStoreException if the domainSet request fails or the general grid uses an
     * unsupported (irregular) axis
     */
    @Override
    public synchronized GridGeometry getGridGeometry() throws DataStoreException {
        if (gridGeometry == null) {
            try {
                final DomainSet domainSet = api.getCoverageDomainSet(
                        new GetCoverageDomainSet().collectionId(description.getId()).format("json"))
                        .send().body().get();
                gridGeometry = toGridGeometry(domainSet.getGeneralGrid());
            } catch (ServiceException ex) {
                throw new DataStoreException(ex);
            }
        }
        return gridGeometry;
    }

    /**
     * Fetches the rangeType (SWE Common DataRecord) from the server and maps each field to a
     * {@link SampleDimension}, caching the result.
     *
     * @return the sample dimensions of this coverage
     * @throws DataStoreException if the rangeType request fails
     */
    @Override
    public synchronized List<SampleDimension> getSampleDimensions() throws DataStoreException {
        if (sampleDimensions == null) {
            try {
                final DataRecord rangeType = api.getCoverageRangeType(
                        new GetCoverageRangeType().collectionId(description.getId()).format("json"))
                        .send().body().get();
                final List<SampleDimension> dims = new ArrayList<>();
                for (DataRecordField field : rangeType.getField()) {
                    dims.add(new SampleDimension.Builder().setName(field.getName()).build());
                }
                sampleDimensions = dims;
            } catch (ServiceException ex) {
                throw new DataStoreException(ex);
            }
        }
        return sampleDimensions;
    }

    /**
     * Downloads the coverage for the given spatial domain, auto-detecting its format
     * (GeoTIFF or NetCDF for the moment) and delegating the actual decoding to SIS.
     *
     * @param domain spatial subset requested, or {@code null} for the full extent
     * @param range band selection; unsupported, must be empty
     * @throws DataStoreException if a band selection is requested, the download fails, or the
     * downloaded data contains no grid coverage
     */
    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        if (range != null && range.length != 0) {
            throw new DataStoreException("Band selection is not supported on OGC-API coverages.");
        }

        final GetCoverage parameters = new GetCoverage().collectionId(description.getId());
        if (domain != null && domain.isDefined(GridGeometry.ENVELOPE)) {
            final Envelope env = domain.getEnvelope();
            parameters.bbox(toBbox(env)).bboxCrs(toCrs(env));
        }

        // Decode via SIS auto-detection (GeoTIFF/NetCDF) rather than a dedicated
        // codec; the downloaded temp file is left for the JVM to clean up on exit rather than
        // deleted right after read(), since the returned GridCoverage may lazily reference it.
        final Path tmp;
        try {
            final HttpResponse<InputStream> response = api.getCoverage(parameters).send();
            tmp = Files.createTempFile("ogcapi-coverage-", ".bin");
            tmp.toFile().deleteOnExit();
            try (InputStream in = response.body()) {
                Files.copy(in, tmp, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (ServiceException | IOException ex) {
            throw new DataStoreException(ex);
        }

        final DataStore store = DataStores.open(tmp);
        final GridCoverageResource resource = firstGridCoverageResource(store);
        if (resource == null) {
            throw new DataStoreException("Downloaded coverage does not contain any grid coverage data.");
        }
        return resource.read(domain, range);
    }

    private static GridCoverageResource firstGridCoverageResource(Resource resource) throws DataStoreException {
        if (resource instanceof GridCoverageResource gcr) {
            return gcr;
        }
        if (resource instanceof Aggregate agg) {
            for (Resource component : agg.components()) {
                final GridCoverageResource found = firstGridCoverageResource(component);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    private static GridGeometry toGridGeometry(GeneralGrid grid) throws DataStoreException {
        final CoordinateReferenceSystem crs = Extent.getCrsFromName(grid.getSrsName());
        final List<Axis> axis = grid.getAxis();
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        for (int i = 0; i < axis.size(); i++) {
            final Axis a = axis.get(i);
            if (a instanceof IrregularAxis) {
                // TODO: non-uniform (irregular) grid axes are out of scope for the moment
                throw new DataStoreException("Irregular grid axes are not supported yet.");
            }
            env.setRange(i, toNumber(a.getLowerBound()), toNumber(a.getUpperBound()));
        }

        final List<IndexAxis> limits = grid.getGridLimits().getAxis();
        final long[] low = new long[limits.size()];
        final long[] high = new long[limits.size()];
        for (int i = 0; i < limits.size(); i++) {
            low[i] = (long) toNumber(limits.get(i).getLowerBound());
            high[i] = (long) toNumber(limits.get(i).getUpperBound());
        }
        final GridExtent extent = new GridExtent(null, low, high, true);

        return new GridGeometry(extent, env, GridOrientation.HOMOTHETY);
    }

    private static double toNumber(Object value) {
        if (value instanceof Number n) {
            return n.doubleValue();
        }
        return Double.parseDouble(value.toString());
    }

    private static List<Double> toBbox(Envelope env) {
        final List<Double> bbox = new ArrayList<>();
        for (int i = 0; i < env.getDimension(); i++) {
            bbox.add(env.getLowerCorner().getCoordinate(i));
        }
        for (int i = 0; i < env.getDimension(); i++) {
            bbox.add(env.getUpperCorner().getCoordinate(i));
        }
        return bbox;
    }

    private static String toCrs(Envelope env) {
        final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
        return crs == null ? null : IdentifiedObjects.getIdentifierOrName(crs);
    }
}
