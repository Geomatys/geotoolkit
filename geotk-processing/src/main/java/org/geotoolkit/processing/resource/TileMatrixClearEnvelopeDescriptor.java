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

import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.tiling.WritableTileMatrix;
import org.apache.sis.storage.tiling.WritableTiledResource;
import org.apache.sis.util.SimpleInternationalString;
import org.geotoolkit.process.Process;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.AbstractProcessDescriptor;
import org.geotoolkit.processing.GeotkProcessingRegistry;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Process removing the tiles of a tile matrix intersecting an envelope.
 *
 * @author Johann Sorel (Geomatys)
 */
public class TileMatrixClearEnvelopeDescriptor extends AbstractProcessDescriptor {

    public static final String NAME = "resource:clear-tile-matrix-envelope";

    /**
     * Input parameters
     */
    public static final ParameterDescriptor<Resource> RESOURCE = ResourceProcessing.TILED_RESOURCE;
    public static final ParameterDescriptor<String> TILE_MATRIX_SET = ResourceProcessing.TILE_MATRIX_SET;
    public static final ParameterDescriptor<String> TILE_MATRIX = ResourceProcessing.TILE_MATRIX;
    public static final ParameterDescriptor<Envelope> ENVELOPE = new ParameterBuilder()
            .addName("envelope")
            .setRemarks("Envelope of the area to clear, tiles intersecting it are removed")
            .setRequired(true)
            .create(Envelope.class, null);
    public static final ParameterDescriptorGroup INPUT_DESC =
            new ParameterBuilder().addName("InputParameters").createGroup(RESOURCE, TILE_MATRIX_SET, TILE_MATRIX, ENVELOPE);

    /**
     * Output parameters
     */
    public static final ParameterDescriptor<Long> DELETED = ResourceProcessing.DELETED;
    public static final ParameterDescriptorGroup OUTPUT_DESC =
            new ParameterBuilder().addName("OutputParameters").createGroup(DELETED);

    /** Instance */
    public static final ProcessDescriptor INSTANCE = new TileMatrixClearEnvelopeDescriptor();

    private TileMatrixClearEnvelopeDescriptor() {
        super(NAME, GeotkProcessingRegistry.IDENTIFICATION,
                new SimpleInternationalString("Remove the tiles of a tile matrix intersecting an envelope."),
                INPUT_DESC, OUTPUT_DESC);
    }

    @Override
    public Process createProcess(final ParameterValueGroup input) {
        return new ChildProcess(input);
    }

    private static class ChildProcess extends AbstractProcess {

        public ChildProcess(final ParameterValueGroup input) {
            super(TileMatrixClearEnvelopeDescriptor.INSTANCE, input);
        }

        @Override
        protected void execute() throws ProcessException {
            final Resource resource = inputParameters.getValue(RESOURCE);
            final Envelope envelope = inputParameters.getValue(ENVELOPE);
            if (!(resource instanceof WritableTiledResource wtr)) {
                throw new ProcessException("Resource is not a writable tiled resource", this);
            }
            try {
                final WritableTileMatrix tm = ResourceProcessing.getTileMatrix(wtr, inputParameters);
                final GridExtent range = tm.getTilingScheme().derive().rounding(GridRoundingMode.ENCLOSING).subgrid(envelope).getIntersection();
                final long deleted = tm.deleteTiles(range);
                outputParameters.getOrCreate(DELETED).setValue(deleted);
            } catch (DataStoreException ex) {
                throw new ProcessException(ex.getMessage(), this, ex);
            }
        }
    }
}
