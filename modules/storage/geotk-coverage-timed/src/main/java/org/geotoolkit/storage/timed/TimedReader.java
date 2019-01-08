/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.storage.timed;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.coverage.SampleDimension;
import org.geotoolkit.coverage.grid.GridCoverage;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReadParam;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.index.tree.StoreIndexException;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 * A reader which taking grid coverages from image files indexed in the related
 * {@link TimedResource}. The selection is based on input {@link GridCoverageReadParam#getEnvelope()
 * } parameter.
 *
 * If no envelope is provided, the most up to date data is returned. If an
 * envelope is given but no data from the index match it, we will throw an
 * exception.
 *
 * @author Alexis Manin (Geomatys)
 */
public class TimedReader extends GridCoverageReader {

    final TimedResource parent;

    TimedReader(final TimedResource parent) {
        this.parent = parent;
    }

    @Override
    public GenericName getCoverageName() throws CoverageStoreException, CancellationException {
        return parent.getIdentifier();
    }

    @Override
    public GridGeometry getGridGeometry() throws CoverageStoreException, CancellationException {
        return parent.getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws CoverageStoreException, CancellationException {
        return Collections.EMPTY_LIST;
    }

    @Override
    public GridCoverage read(GridCoverageReadParam param) throws CoverageStoreException, CancellationException {
        if (param == null) {
            param = new GridCoverageReadParam();
        }

        Envelope envelope = param.getEnvelope();
        if (envelope == null) {
            // We'll try to read the most recent data.
            final GeneralEnvelope totalEnv = new GeneralEnvelope(parent.getGridGeometry().getEnvelope());
            final double lastTime = totalEnv.getMaximum(parent.index.timeIndex);
            totalEnv.setRange(parent.index.timeIndex, lastTime, lastTime);
            envelope = totalEnv;
        }

        final Path imageFile;
        try {
            imageFile = parent.index.findMostRecent(envelope)
                    .orElseThrow(() -> new CoverageStoreException("No data available for given envelope."));

        } catch (TransformException | StoreIndexException | IOException ex) {
            throw new CoverageStoreException("Cannot work with given envelope : " + envelope, ex);
        }

        try (TimedUtils.CloseableCoverageReader reader = new TimedUtils.CloseableCoverageReader()) {
            reader.setInput(imageFile.toFile());
            return reader.read(param);
        }
    }
}
