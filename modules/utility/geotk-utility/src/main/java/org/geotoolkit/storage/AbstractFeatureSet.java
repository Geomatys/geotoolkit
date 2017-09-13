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
package org.geotoolkit.storage;

import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.metadata.identification.Identification;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractFeatureSet extends AbstractResource implements FeatureSet {

    protected AbstractFeatureSet() {
    }

    public AbstractFeatureSet(GenericName name) {
        super(name);
    }

    public AbstractFeatureSet(Identifier identifier) {
        super(identifier);
    }

    /**
     * Returns the spatio-temporal envelope of this resource.
     * The default implementation computes the union of all {@link GeographicBoundingBox} in the resource metadata,
     * assuming the {@linkplain org.apache.sis.referencing.CommonCRS#defaultGeographic() default geographic CRS}
     * (usually WGS 84).
     *
     * @return the spatio-temporal resource extent.
     * @throws DataStoreException if an error occurred while reading or computing the envelope.
     */
    @Override
    public Envelope getEnvelope() throws DataStoreException {
        final Metadata metadata = getMetadata();
        GeneralEnvelope bounds = null;
        if (metadata != null) {
            for (final Identification identification : metadata.getIdentificationInfo()) {
                if (identification != null) {                                               // Paranoiac check.
                    for (final Extent extent : identification.getExtents()) {
                        if (extent != null) {                                               // Paranoiac check.
                            for (final GeographicExtent ge : extent.getGeographicElements()) {
                                if (ge instanceof GeographicBoundingBox) {
                                    final GeneralEnvelope env = new GeneralEnvelope((GeographicBoundingBox) ge);
                                    if (bounds == null) {
                                        bounds = env;
                                    } else {
                                        bounds.add(env);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return bounds;
    }

}
