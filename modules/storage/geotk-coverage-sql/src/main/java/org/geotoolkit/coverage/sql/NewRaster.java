/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.measure.Range;
import org.apache.sis.metadata.iso.extent.Extents;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.image.io.WarningProducer;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.identification.Identification;


/**
 * Information about a new raster to be added.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
final class NewRaster {
    /**
     * The format name as given by the data store provider. Example: "NetCDF".
     */
    final String driver;

    /**
     * Path to the new file to add.
     */
    final Path path;

    /**
     * Identifier of the resource in the file.
     */
    final String dataset;

    /**
     * Time period declared in metadata. If a temporal component of {@link #geometry} is specified, that temporal
     * component have precedence. The {@code startTime} and {@code endTime} fields are used as a fallback.
     */
    private Instant startTime, endTime;

    /**
     * Grid geometry of the file to add.
     */
    GridGeometry geometry;

    /**
     * Description of bands.
     */
    List<SampleDimension> bands;

    /**
     * Creates information about a new raster to be added to the catalog.
     */
    private NewRaster(final String driver, final String dataset, final Path file) {
        this.driver  = driver;
        this.path    = file;
        this.dataset = dataset;
    }

    /**
     * Returns a string representation of this raster entry for debugging purpose.
     *
     * @return string representation (may change in any future version).
     */
    @Override
    public String toString() {
        return path.getFileName().toString() + " @ " + dataset;
    }

    /**
     * Opens the given file using the given provider, or by auto-detection if {@code provider} is null.
     */
    static DataStore open(final DataStoreProvider provider, final Path file) throws DataStoreException {
        if (provider == null) {
            return DataStores.open(file);
        }
        final StorageConnector connector = new StorageConnector(file);
        try {
            return provider.open(connector);
        } finally {
            try {
                connector.closeAllExcept(null);
            } catch (Exception e) {
                WarningProducer.LOGGER.log(Level.WARNING, "Cannot properly close storage connector: "+e.getMessage());
            }
        }
    }

    /**
     * Returns information about rasters to add. Keys in the returned map are resource identifiers.
     * There is often only one entry, but we may have more entries if the storage contains many images
     * or many netCDF variables for example. In the netCDF case, each variable may be a different
     * "image" with a different {@linkplain #imageIndex}.
     *
     * @param  files  paths to the files to add.
     * @return information about rasters, separated by resource identifier.
     */
    static Map<String,List<NewRaster>> list(final String product, final AddOption option,
            final DataStoreProvider provider, final Path... files) throws DataStoreException
    {
        final Map<String,List<NewRaster>> rasters = new LinkedHashMap<>();
        for (final Path file : files) {
            try (final DataStore ds = open(provider, file)) {
                final String driver = ds.getProvider().getShortName();
                final Collection<GridCoverageResource> candidates = org.geotoolkit.storage.DataStores.flatten(ds, true, GridCoverageResource.class);
                /*
                 * If there is only one resource, do not specify the dataset. This increase the chance
                 * of being able to reuse the same SeriesEntry for many coverage, especially when using
                 * DataStores that put filename in their resource name.
                 */
                final boolean isMultiResources = (option == AddOption.CREATE_AS_CHILD_PRODUCT) || candidates.size() > 1;
                for (final GridCoverageResource gr : candidates) {
                    final String dataset;
                    if (isMultiResources) {
                        dataset = gr.getIdentifier().get().tip().toString();
                    } else {
                        dataset = null;
                    }
                    final NewRaster r = new NewRaster(driver, dataset, file);
                    r.geometry = gr.getGridGeometry();
                    r.bands = gr.getSampleDimensions();
                    if (!r.setTimeRange(gr.getMetadata())) {
                        r.setTimeRange(ds.getMetadata());
                    }
                    rasters.computeIfAbsent((dataset != null) ? dataset : product, (k) -> new ArrayList<>()).add(r);
                }
            }
        }
        return rasters;
    }

    /**
     * Sets {@link #startTime} and {@link #endTime} from the given metadata.
     *
     * @return whether the time range has been set.
     */
    private boolean setTimeRange(final Metadata metadata) {
        if (metadata != null) {
            for (final Identification id : metadata.getIdentificationInfo()) {
                for (final Extent extent : id.getExtents()) {
                    final Range<Date> tr = Extents.getTimeRange(extent);
                    if (tr != null) {
                        Date d;
                        if ((d = tr.getMinValue()) != null) startTime = d.toInstant();
                        if ((d = tr.getMaxValue()) != null) endTime   = d.toInstant();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * If period is still unspecified, uses the time range from metadata.
     */
    final void completeTimeRange(final Instant[] period) {
        Instant t = period[0];
        if (t != null || period[1] != null) {               // Modify 'period' only if not specified at all.
            if (startTime == null || endTime == null) {
                return;                                     // Do not modify if we don't have complete time range.
            }
            if (t == null) {
                t = period[1];
            } else if (!t.equals(period[1])) {              // Do not modify if 'period' is already a non-empty range.
                return;
            }
            if (t.isBefore(startTime) || t.isAfter(endTime)) {
                return;                                     // Do not modify if our timerange does not contain 't'.
            }
        }
        period[0] = startTime;
        period[1] = endTime;
    }

    /**
     * Returns a suggested identifier for format entries.
     * This method tries to return something shorter than the product name if possible.
     *
     * @param  product  the product name, to be used as a fallback if we have no better identifier.
     */
    final String suggestedID(final String product) {
        if (bands != null && bands.size() == 1) {
            final String c = bands.get(0).getName().tip().toString();
            if (c.length() < product.length()) return c;
        }
        return product;
    }
}
