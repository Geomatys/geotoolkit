/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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

import java.util.Collection;
import java.util.stream.Stream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.geotoolkit.storage.multires.GeneralProgressiveResource;
import org.geotoolkit.storage.multires.MultiResolutionResource;
import org.geotoolkit.storage.multires.Pyramid;
import org.geotoolkit.storage.multires.TileGenerator;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ProgressiveFeatureSetResource<T extends FeatureSet & MultiResolutionResource>
        extends GeneralProgressiveResource implements FeatureSet, MultiResolutionResource {

    private T base = null;

    public ProgressiveFeatureSetResource(T resource, TileGenerator generator) throws DataStoreException {
        super(resource, generator);
        this.base = resource;
    }

    @Override
    public Collection<Pyramid> getModels() throws DataStoreException {
        return (Collection<Pyramid>) super.getModels();
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        return base.getType();
    }

    @Override
    public FeatureSet subset(Query query) throws UnsupportedQueryException, DataStoreException {
        return FeatureSet.super.subset(query);
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        return new PyramidFeatureSetReader(this, getType()).features(null, parallel);
    }

}
