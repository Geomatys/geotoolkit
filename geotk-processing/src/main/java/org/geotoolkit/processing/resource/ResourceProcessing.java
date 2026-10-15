/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.processing.resource;

import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.IllegalNameException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.tiling.WritableTileMatrix;
import org.apache.sis.storage.tiling.WritableTileMatrixSet;
import org.apache.sis.storage.tiling.WritableTiledResource;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.storage.ResourceProcessor;
import org.opengis.parameter.ParameterDescriptor;

/**
 * Parameters and utilities shared by the resource process descriptors.
 *
 * @author Johann Sorel (Geomatys)
 */
final class ResourceProcessing {

    /**
     * Optional identifier of the created resource.
     */
    static final ParameterDescriptor<String> IDENTIFIER = new ParameterBuilder()
            .addName("identifier")
            .setRemarks("Identifier of the created resource")
            .setRequired(false)
            .create(String.class, null);

    /**
     * Name of the output parameter holding the created resource.
     */
    static final String RESULT_NAME = "result";

    /**
     * Resource on which tile matrix operations are applied, must be a {@link WritableTiledResource}.
     */
    static final ParameterDescriptor<Resource> TILED_RESOURCE = new ParameterBuilder()
            .addName("resource")
            .setRemarks("Writable tiled resource")
            .setRequired(true)
            .create(Resource.class, null);

    /**
     * Identifier of the tile matrix set in the tiled resource.
     */
    static final ParameterDescriptor<String> TILE_MATRIX_SET = new ParameterBuilder()
            .addName("tileMatrixSet")
            .setRemarks("Tile matrix set identifier")
            .setRequired(true)
            .create(String.class, null);

    /**
     * Identifier of the tile matrix in the tile matrix set.
     */
    static final ParameterDescriptor<String> TILE_MATRIX = new ParameterBuilder()
            .addName("tileMatrix")
            .setRemarks("Tile matrix identifier")
            .setRequired(true)
            .create(String.class, null);

    /**
     * Number of deleted tiles.
     */
    static final ParameterDescriptor<Long> DELETED = new ParameterBuilder()
            .addName("deleted")
            .setRemarks("Number of deleted tiles")
            .setRequired(true)
            .create(Long.class, null);

    private ResourceProcessing() {
    }

    /**
     * Searches the tile matrix designated by the {@link #TILE_MATRIX_SET} and {@link #TILE_MATRIX} input values
     * in the given resource.
     *
     * @param resource tiled resource, from the {@link #TILED_RESOURCE} input value
     * @param input process input parameters
     * @return the writable tile matrix
     * @throws IllegalNameException if the tile matrix set or tile matrix does not exist
     * @throws DataStoreException if an error occurred while reading the tile matrix sets
     */
    static WritableTileMatrix getTileMatrix(final WritableTiledResource resource, final Parameters input) throws DataStoreException {
        final String tmsId = input.getValue(TILE_MATRIX_SET);
        final String tmId = input.getValue(TILE_MATRIX);
        for (WritableTileMatrixSet tms : resource.getTileMatrixSets()) {
            if (tms.getIdentifier().toString().equals(tmsId)) {
                for (WritableTileMatrix tm : tms.getTileMatrices().values()) {
                    if (tm.getIdentifier().toString().equals(tmId)) {
                        return tm;
                    }
                }
            }
        }
        throw new IllegalNameException("TileMatrix " + tmId + " not found in TileMatrixSet " + tmsId);
    }

    /**
     * Creates a resource processor configured with the {@link #IDENTIFIER} input value, if defined.
     *
     * @param input process input parameters
     * @return configured resource processor
     */
    static ResourceProcessor createProcessor(final Parameters input) {
        final ResourceProcessor processor = new ResourceProcessor();
        final String identifier = input.getValue(IDENTIFIER);
        if (identifier != null && !identifier.isBlank()) {
            processor.setIdentifier(Names.createLocalName(null, null, identifier));
        }
        return processor;
    }
}
