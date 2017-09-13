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
package org.geotoolkit.storage.coverage;

import java.awt.Image;
import java.util.stream.Stream;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.coverage.io.GridCoverageWriter;
import org.geotoolkit.internal.feature.CoverageFeature;
import org.geotoolkit.internal.feature.TypeConventions;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;
import org.opengis.metadata.content.CoverageDescription;
import org.geotoolkit.data.FeatureSet;

/**
 * Resource to a coverage in the coverage store.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface CoverageResource extends FeatureSet {

    /**
     * Name of the coverage. act as an identifier in the coverage store
     *
     * @return Name
     */
    GenericName getName();

    /**
     * @return int image index in reader/writer.
     */
    int getImageIndex();

    /**
     * Get the coverage description and statistics.
     *
     * @return CoverageDescripion, can be null
     */
    CoverageDescription getCoverageDescription();

    /**
     * @return true if coverage is writable
     * @throws org.apache.sis.storage.DataStoreException
     */
    boolean isWritable() throws DataStoreException;

    /**
     * Get the coverage store this coverage comes from.
     *
     * @return CoverageStore, can be null if coverage has a different kind of source.
     */
    CoverageStore getStore();

    /**
     * Get a reader for this coverage.
     * When you have finished using it, return it using the recycle method.
     *
     * @return GridCoverageReader
     * @throws CoverageStoreException
     */
    GridCoverageReader acquireReader() throws CoverageStoreException;

    /**
     * Get a writer for this coverage.
     * When you have finished using it, return it using the recycle method.
     *
     * @return GridCoverageWriter
     * @throws CoverageStoreException
     */
    GridCoverageWriter acquireWriter() throws CoverageStoreException;

    /**
     * Return the used reader, they can be reused later.
     *
     * @param reader
     */
    void recycle(CoverageReader reader);

    /**
     * Return the used writer, they can be reused later.
     *
     * @param writer
     */
    void recycle(GridCoverageWriter writer);

    /**
     * Return the legend of this coverage
     * @return
     * @throws DataStoreException
     */
    Image getLegend() throws DataStoreException;

    @Override
    public default FeatureType getType() throws DataStoreException {
        final GridCoverageReader reader = acquireReader();
        final FeatureType type = CoverageFeature.createCoverageType(reader);
        recycle(reader);
        return type;
    }

    @Override
    public default Stream<Feature> features(boolean parallal) throws DataStoreException {
        final FeatureType type = getType();
        final FeatureAssociationRole role = (FeatureAssociationRole) type.getProperty(TypeConventions.RANGE_ELEMENTS_PROPERTY.toString());
        final Feature feature = type.newInstance();
        feature.setProperty(CoverageFeature.coverageRecords(this,role));
        return Stream.of(feature);
    }

}
