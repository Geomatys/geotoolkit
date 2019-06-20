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
package org.geotoolkit.data;

import java.util.Optional;
import java.util.stream.Stream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.event.ChangeEvent;
import org.apache.sis.storage.event.ChangeListener;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.feature.FeatureTypeExt;
import org.geotoolkit.feature.ReprojectMapper;
import org.geotoolkit.feature.ViewMapper;
import org.geotoolkit.storage.AbstractResource;
import org.geotoolkit.storage.StorageEvent;
import org.geotoolkit.storage.StorageListener;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Envelope;

/**
 * Default subset feature resource.
 *
 * @author Johann Sorel (Geomatys)
 */
final class SubsetFeatureResource extends AbstractResource implements FeatureSet, ChangeListener<ChangeEvent> {

    private final StorageListener.Weak weakListener = new StorageListener.Weak(this);

    private final FeatureSet parent;
    private final Query query;
    private FeatureType type;

    public SubsetFeatureResource(FeatureSet parent, Query query) throws DataStoreException {
        super(parent.getIdentifier().get());
        this.parent = parent;
        this.query = query;
        weakListener.registerSource(parent);
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return Optional.empty();
    }

    @Override
    public synchronized FeatureType getType() throws DataStoreException {
        if (type==null) {
            type = parent.getType();
            final String[] properties = query.getPropertyNames();
            if (properties!=null && FeatureTypeExt.isAllProperties(type, properties)) {
                type = new ViewMapper(type, properties).getMappedType();
            }
            if(query.getCoordinateSystemReproject()!=null){
                type = new ReprojectMapper(type, query.getCoordinateSystemReproject()).getMappedType();
            }
        }
        return type;
    }

    @Override
    public FeatureSet subset(Query query) throws DataStoreException {
        final Query merge = QueryUtilities.subQuery(this.query, query);
        return new SubsetFeatureResource(parent, merge);
    }

    @Override
    public Stream<Feature> features(boolean parallal) throws DataStoreException {
        return FeatureStreams.subset(parent.features(false), getType(), query);
    }

    @Override
    public void changeOccured(ChangeEvent event) {
        //forward events
        if (event instanceof StorageEvent) {
            event = ((StorageEvent)event).copy(this);
        }
        sendEvent(event);
    }

}
