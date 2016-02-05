/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.coverage.landsat;

import java.awt.Image;
import java.io.IOException;
import java.nio.file.Path;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.storage.coverage.AbstractCoverageReference;
import org.geotoolkit.storage.coverage.CoverageStore;
import org.opengis.util.GenericName;
import static org.geotoolkit.coverage.landsat.LandsatConstants.*;
import org.geotoolkit.storage.coverage.CoverageReference;

/**
 * Reader adapted to read and aggregate directly needed bands to build appropriate
 * REFLECTIVE, THERMIC, or PANCHROMATIC Landsat part.
 *
 * @author Remi Marechal (Geomatys)
 * @version 1.0
 * @since   1.0
 */
public class LandsatCoverageReference extends AbstractCoverageReference {

    /**
     * {@link Path} of the parent directory which contain all
     * Landsat 8 images.
     */
    private final Path imagePath;

    /**
     * {@link Path} to the metadata landsat 8 file.
     */
    private final Path metadata;

    /**
     * Index which define what part of the landsat 8 data will be read.<br><br>
     *
     * 0 : means REFLECTIVE<br>
     * 1 : means PANCHROMATIC<br>
     * 2 : means THERMAL<br>
     *
     */
    private final int imageIndex;

    /**
     * Build an appripriate {@link CoverageReference} to read Landsat 8 datas.<br><br>
     *
     * Note : a Landsat 8 product may contains 3 kind of coverages.<br>
     * To make difference between them we use the {@linkplain GenericName name} given in parameter.<br>
     *
     * the expected names are : REFLECTIVE, THERMIC, or PANCHROMATIC.
     *
     * @param store normally Landsat store.
     * @param name REFLECTIVE, THERMIC, or PANCHROMATIC.
     * @param imagePath path metadata file.
     * @param metadata Landsat 8 parent directory.
     */
    public LandsatCoverageReference(final CoverageStore store, final GenericName name,
                                    final Path imagePath, final Path metadata) {
        super(store, name);
        this.imagePath = imagePath;
        this.metadata = metadata;

        String head = name.tip().toString();
        switch (head) {
            case REFLECTIVE_LABEL   : imageIndex = 0; break;
            case PANCHROMATIC_LABEL : imageIndex = 1; break;
            case THERMAL_LABEL      : imageIndex = 2; break;
            default : throw new IllegalStateException("Coverage name : "+name.toString()+" is not appropriate for Landsat 8 behavior.");
        }
    }

    /**
     * {@inheritDoc }
     *
     * 0 : for REFLECTIVE Landsat 8 Coverage part.<br>
     * 1 : for PANCHROMATIC Landsat 8 Coverage part.<br>
     * 2 : for THERMAL Landsat 8 Coverage part.<br>
     *
     * @return 0, 1 or 2.
     */
    @Override
    public int getImageIndex() {
        return imageIndex;
    }

    /**
     * {@inheritDoc }
     *
     * @return always return false, no Landsat 8 writer.
     * @throws DataStoreException
     */
    @Override
    public boolean isWritable() throws DataStoreException {
        return false;
    }

    @Override
    public GridCoverageReader acquireReader() throws CoverageStoreException {
        try {
            return new LandsatReader(imagePath, metadata);
        } catch (IOException ex) {
            throw new CoverageStoreException(ex);
        }
    }

    /**
     * {@inheritDoc }
     *
     * Throw an exception.<br>
     * Landsat 8 writer does not supported.
     *
     * @return
     * @throws CoverageStoreException
     */
    @Override
    public GridCoverageWriter acquireWriter() throws CoverageStoreException {
        throw new CoverageStoreException("Not supported.");
    }

    /**
     * {@inheritDoc }
     * @return
     * @throws org.apache.sis.storage.DataStoreException
     */
    @Override
    public Image getLegend() throws DataStoreException {
        return null;
    }
}
