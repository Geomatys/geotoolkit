/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.storage.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.feature.Features;
import org.apache.sis.feature.internal.shared.AttributeConvention;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataSet;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.GridCoverageResource;
import static org.apache.sis.util.ArgumentChecks.*;
import org.apache.sis.util.collection.BackingStoreException;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.util.collection.CloseableIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.filter.ResourceId;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.content.ContentInformation;
import org.opengis.metadata.content.FeatureCatalogueDescription;
import org.opengis.metadata.content.FeatureTypeInfo;
import org.opengis.util.GenericName;

/**
 * Convenient methods to manipulate FeatureStore and FeatureCollection.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class FeatureStoreUtilities {

    static final Logger LOGGER = Logger.getLogger("org.geotoolkit.data");

    private FeatureStoreUtilities() {
    }

    /**
     * Write the features from the given collection and return the list of generated FeatureID
     * send by the writer.
     *
     * @param writer, writer will not be closed
     * @return List of generated FeatureId. Can be empty if output data type has
     * no identifier property.
     * @throws FeatureStoreRuntimeException
     */
    public static List<ResourceId> write(final FeatureWriter writer, final Collection<? extends Feature> collection)
            throws FeatureStoreRuntimeException {
        final List<ResourceId> ids = new ArrayList<>();
        boolean withId = false;
        // Check if there's identifiers to report.
        try {
            writer.getFeatureType().getProperty(AttributeConvention.IDENTIFIER);
            withId = true;
        } catch (PropertyNotFoundException e) {
            LOGGER.log(Level.FINE, "No identifier available at copy", e);
        }

        RuntimeException error = null;
        final Iterator<? extends Feature> ite = collection.iterator();
        try {
            while (ite.hasNext()) {
                final Feature f = ite.next();
                final Feature candidate = writer.next();
                FeatureExt.copy(f, candidate, false);
                writer.write();
                if (withId) {
                    ids.add(FeatureExt.getId(candidate));
                }
            }
        } catch (RuntimeException e) {
            error = e;
            throw error;
        } finally {
            if (ite instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) ite).close();
                } catch (Exception ex) {
                    if (error == null)
                        throw new FeatureStoreRuntimeException(ex);
                    else
                        error.addSuppressed(ex);
                }
            }
        }

        return ids;
    }

    /**
     * Iterate on the given iterator and calculate count.
     * @throws FeatureStoreRuntimeException
     */
    public static long calculateCount(final CloseableIterator reader) throws FeatureStoreRuntimeException {
        long count = 0;

        try {
            while (reader.hasNext()) {
                reader.next();
                count++;
            }
        } finally {
            reader.close();
        }

        return count;
    }

    /**
     * Iterate on the given iterator and calculate the envelope.
     * @throws FeatureStoreRuntimeException
     */
    public static Envelope calculateEnvelope(final FeatureIterator iterator) throws FeatureStoreRuntimeException {
        ensureNonNull("iterator", iterator);

        GeneralEnvelope env = null;

        try {
            while (iterator.hasNext()) {
                final Feature f = iterator.next();
                final Envelope bbox = FeatureExt.getEnvelope(f);
                if (bbox != null) {
                    if (env != null) {
                        env.add(bbox);
                    } else {
                        env = new GeneralEnvelope(bbox);
                    }
                }
            }
        } finally {
            iterator.close();
        }

        return env;
    }

    /**
     * Get or count features in FeatureSet.
     *
     * <p>
     * This operation can be expensive in time and resources if the count
     * must be computed. It is recommended to use {@link DataSet#getMetadata() }
     * informations instead if a resulting null count is acceptable.
     * </p>
     *
     * @param dataset Data set to extract or compute from, must not be null
     * @return features count
     * @throws org.apache.sis.storage.DataStoreException
     */
    public static Long getCount(FeatureSet dataset) throws DataStoreException {
        return getCount(dataset, false);
    }

    /**
     * Get or count features in FeatureSet.
     *
     * <p>
     * This operation can be expensive in time and resources if the count
     * must be computed. It is recommended to use {@link DataSet#getMetadata() }
     * informations instead if a resulting null count is acceptable.
     * </p>
     *
     * @param dataset Data set to extract or compute from, must not be null
     * @param forceCompute ignore dataset declared envelope and always compute count
     * @return features count
     * @throws org.apache.sis.storage.DataStoreException
     */
    public static Long getCount(FeatureSet dataset, boolean forceCompute) throws DataStoreException {

        Long count = null;
        if (!forceCompute) {
            //extract information from metadata
            Metadata metadata = dataset.getMetadata();
            if (metadata != null) {
                search:
                for (ContentInformation ci : metadata.getContentInfo()) {
                    if (ci instanceof FeatureCatalogueDescription) {
                        FeatureCatalogueDescription fcd = (FeatureCatalogueDescription) ci;
                        for (FeatureTypeInfo fti : fcd.getFeatureTypeInfo()) {
                            Integer nb = fti.getFeatureInstanceCount();
                            if (nb != null) {
                                count = nb.longValue();
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (count == null) {
            try (Stream<Feature> stream = dataset.features(true)) {
                count = stream.count();
            } catch (BackingStoreException ex) {
                throw ex.unwrapOrRethrow(DataStoreException.class);
            }
        }

        return count;
    }

    /**
     * Get or compute DataSet envelope.
     *
     * <p>
     * This operation can be expensive in time and resources if the envelope
     * must be computed. It is recommended to use {@link DataSet#getEnvelope() }
     * instead if a resulting null envelope is acceptable.
     * </p>
     *
     * @param dataset Data set to extract or compute from, must not be null
     * @return dataset envelope or null if the envelope could not be computed or is not geospatial.
     * @throws org.apache.sis.storage.DataStoreException
     */
    public static Envelope getEnvelope(DataSet dataset) throws DataStoreException {
        return getEnvelope(dataset, false);
    }

    /**
     * Get or compute DataSet envelope.
     *
     * <p>
     * This operation can be expensive in time and resources if the envelope
     * must be computed. It is recommended to use {@link DataSet#getEnvelope() }
     * instead if a resulting null envelope is acceptable.
     * </p>
     *
     * @param dataset Data set to extract or compute from, must not be null
     * @param forceCompute ignore dataset declared envelope and always compute envelope
     * @return dataset envelope or null if the envelope could not be computed or is not geospatial.
     * @throws org.apache.sis.storage.DataStoreException
     */
    public static Envelope getEnvelope(DataSet dataset, boolean forceCompute) throws DataStoreException {
        Envelope envelope = null;
        if (!forceCompute) {
            try {
                envelope = dataset.getEnvelope().orElse(null);
            } catch (DataStoreException ex) {
                //do nothing, iterate on features to compute it
            }
        }

        if (envelope == null) {
            GeneralEnvelope env = null;
            if (dataset instanceof org.apache.sis.storage.FeatureSet) {
                try (Stream<Feature> features = ((org.apache.sis.storage.FeatureSet) dataset).features(false)) {
                    final Iterator<Feature> iterator = features.iterator();
                    while (iterator.hasNext()) {
                        final Feature f = iterator.next();
                        final Envelope bbox = FeatureExt.getEnvelope(f);
                        if (bbox != null) {
                            if (env != null) {
                                env.add(bbox);
                            } else {
                                env = new GeneralEnvelope(bbox);
                            }
                        }
                    }
                } catch (BackingStoreException ex) {
                    throw ex.unwrapOrRethrow(DataStoreException.class);
                }
            } else if (dataset instanceof GridCoverageResource) {
                final GridCoverageResource gcr = (GridCoverageResource) dataset;
                final GridGeometry gridGeometry = gcr.getGridGeometry();
                if (gridGeometry.isDefined(GridGeometry.ENVELOPE)) {
                    return gridGeometry.getEnvelope();
                }
            }
            envelope = env;
        }

        return envelope;
    }

    /**
     * Return a Xpath form for the specified property.
     *
     * @param defaultGeometry a feature property.
     * @return The name of the property in XPath form.
     */
    public static String defaultGeometryPropertyNameToXpathForm(final PropertyType defaultGeometry) {
        GenericName geomName;
        // LinkOperation is private so we have no other option to do this until a correction is applied on SIS.
        Optional<String> tmp = Features.getLinkTarget(defaultGeometry);
        if (tmp.isPresent()) {
            Operation op = (Operation) defaultGeometry;
            geomName = op.getResult().getName();
        } else {
            geomName = defaultGeometry.getName();
        }
        return NamesExt.toXpathForm(geomName);
    }
}
