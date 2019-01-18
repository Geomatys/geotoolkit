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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.GridCoverageResource;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;


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
     * Returns information about rasters to add. Keys in the returned map are resource identifiers.
     * There is often only one entry, but we may have more entries if the storage contains many images
     * or many netCDF variables for example. In the netCDF case, each variable may be a different
     * "image" with a different {@linkplain #imageIndex}.
     *
     * @param  files  paths to the files to add.
     * @return information about rasters, separated by resource identifier.
     */
    static Map<String,List<NewRaster>> list(final Path... files) throws DataStoreException {
        final Map<String,List<NewRaster>> rasters = new LinkedHashMap<>();
        for (final Path file : files) {
            try (final DataStore ds = DataStores.open(file)) {
                String driver = ds.getProvider().getShortName();
                collect(driver, file, ds, rasters);
            } catch (TransformException e) {
                throw new CatalogException(e);
            }
        }
        return rasters;
    }

    /**
     * Adds to the given {@code rasters} list information about all resources found in the given node.
     * This method add recursively all children resources.
     *
     * @param  driver    the format name as given by the data store provider. Example: "NetCDF".
     * @param  file      path to the file, including parent directories and file extension.
     * @param  resource  the resource to add to the given {@code rasters} list.
     * @param  rasters   where to add the information about rasters.
     * @param  index     zero-based index of the image to read.
     */
    private static void collect(final String driver, final Path file, final DataStore resource,
            final Map<String,List<NewRaster>> rasters) throws DataStoreException, TransformException
    {

        final Collection<GridCoverageResource> candidates = org.geotoolkit.storage.
                DataStores.flatten(resource, true, GridCoverageResource.class);
        /*
         * If there is only one resource, do not specify the dataset. This increase the chance
         * of being able to reuse the same SeriesEntry for many coverage, especially when using
         * DataStores that put filename in their resource name.
         */
        final boolean isMultiResources = candidates.size() > 1;
        for (final GridCoverageResource gr : candidates) {
            final NewRaster r = new NewRaster(driver, isMultiResources ? gr.getIdentifier().toString() : null, file);
            r.geometry = gr.getGridGeometry();
            r.bands = gr.getSampleDimensions();
            final GenericName identifier = gr.getIdentifier();
            rasters.computeIfAbsent(identifier.tip().toString(), (k) -> new ArrayList<>()).add(r);
        }
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
