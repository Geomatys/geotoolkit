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
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.coverage.grid.GeneralGridGeometry;
import org.geotoolkit.coverage.io.CoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.coverage.io.CoverageWriter;
import org.geotoolkit.coverage.io.GridCoverageReader;
import org.geotoolkit.internal.feature.CoverageFeature;
import org.geotoolkit.internal.feature.TypeConventions;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureAssociationRole;
import org.opengis.feature.FeatureType;
import org.opengis.metadata.content.CoverageDescription;
import org.geotoolkit.data.FeatureSet;
import org.geotoolkit.geometry.GeometricUtilities;
import org.geotoolkit.geometry.jts.JTS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.geometry.Envelope;

/**
 * Resource to a coverage in the coverage store.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public interface CoverageResource extends FeatureSet {
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
     */
    boolean isWritable() throws DataStoreException;

    /**
     * Get the data store this coverage comes from.
     *
     * @return DataStore, can be null if coverage has a different kind of source.
     */
    DataStore getStore();

    /**
     * Get a reader for this coverage.
     * When you have finished using it, return it using the recycle method.
     */
    CoverageReader acquireReader() throws CoverageStoreException;

    /**
     * Get a writer for this coverage.
     * When you have finished using it, return it using the recycle method.
     */
    CoverageWriter acquireWriter() throws CoverageStoreException;

    /**
     * Return the used reader, they can be reused later.
     */
    void recycle(CoverageReader reader);

    /**
     * Return the used writer, they can be reused later.
     */
    void recycle(CoverageWriter writer);

    /**
     * Return the legend of this coverage
     */
    Image getLegend() throws DataStoreException;

    @Override
    public default FeatureType getType() throws DataStoreException {
        final CoverageReader reader = acquireReader();

        if (reader instanceof GridCoverageReader) {
            final GridCoverageReader gcr = (GridCoverageReader) reader;
            try {
                final FeatureType type = CoverageFeature.createCoverageType(gcr);
                recycle(reader);
                return type;
            } catch (CoverageStoreException ex) {
                try {
                    reader.dispose();
                } catch (CoverageStoreException ex2) {
                    ex.addSuppressed(ex2);
                }
                throw ex;
            }
        } else {
            throw new DataStoreException("Unsupported readed, only GridCoverageReader instances are supported for now.");
        }
    }

    @Override
    public default Stream<Feature> features(boolean parallal) throws DataStoreException {
        final FeatureType type = getType();
        final FeatureAssociationRole role = (FeatureAssociationRole) type.getProperty(TypeConventions.RANGE_ELEMENTS_PROPERTY.toString());
        final Feature feature = type.newInstance();

        final CoverageReader reader = acquireReader();
        if (reader instanceof GridCoverageReader) {
            final GridCoverageReader gcr = (GridCoverageReader) reader;
            try {
                final GeneralGridGeometry gridGeom = gcr.getGridGeometry(getImageIndex());
                Envelope envelope = gridGeom.getEnvelope();
                if (envelope != null) {
                    Geometry geom = GeometricUtilities.toJTSGeometry(envelope, GeometricUtilities.WrapResolution.SPLIT);
                    if (geom != null) {
                        JTS.setCRS(geom, gridGeom.getCoordinateReferenceSystem());
                        feature.setPropertyValue(AttributeConvention.GEOMETRY_PROPERTY.toString(), geom);
                    }
                }
                recycle(reader);
            } catch (CoverageStoreException ex) {
                try {
                    reader.dispose();
                } catch (CoverageStoreException ex2) {
                    ex.addSuppressed(ex2);
                }
                throw ex;
            }

            feature.setProperty(CoverageFeature.coverageRecords((GridCoverageResource)this,role));
            return Stream.of(feature);
        } else {
            throw new DataStoreException("Unsupported readed, only GridCoverageReader instances are supported for now.");
        }
    }
}
