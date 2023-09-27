/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.data.mapinfo.mif;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.storage.AbstractFeatureSet;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.WritableFeatureSet;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.feature.ReprojectMapper;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;

/**
 *
 * @author Alexis Manin (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
final class MIFFeatureSet extends AbstractFeatureSet implements WritableFeatureSet {

    private final MIFStore store;
    private final GenericName name;

    public MIFFeatureSet(MIFStore store, GenericName name) {
        super(null, false);
        this.store = store;
        this.name = name;
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        return store.manager.getType(name.toString());
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        final FeatureType ft = getType();
        final MIFReader reader = new MIFReader(store.manager, ft);
        final Spliterator<Feature> spliterator = Spliterators.spliteratorUnknownSize((Iterator) reader, Spliterator.ORDERED);
        final Stream<Feature> stream = StreamSupport.stream(spliterator, false);
        return stream.onClose(reader::close);
    }

    @Override
    public void add(Iterator<? extends Feature> newFeatures) throws DataStoreException {
        final FeatureType ft = getType();
        final MIFReader reader = new MIFReader(store.manager, ft);

        try (final MIFWriter writer = new MIFWriter(store.manager, reader)) {

            // We remove the features as we get them. We don't need to write them as the default writing behaviour is append mode.
            while (writer.hasNext()) {
                writer.next();
                writer.remove();
            }

            if (store.manager.getWrittenCRS() != null) {
                ReprojectMapper mapper = null;
                while (newFeatures.hasNext()) {
                    Feature f = newFeatures.next();

                    if (mapper == null) {
                        final FeatureType type = f.getType();
                        mapper = new ReprojectMapper(type, store.manager.getWrittenCRS());
                    }
                    f = mapper.apply(f);

                    final Feature candidate = writer.next();
                    FeatureExt.copy(f, candidate, false);
                    writer.write();
                }
            } else {
                while (newFeatures.hasNext()) {
                    final Feature f = newFeatures.next();
                    final Feature candidate = writer.next();
                    FeatureExt.copy(f, candidate, false);
                    writer.write();
                }
            }
        }
    }

    @Override
    public void updateType(FeatureType newType) throws DataStoreException {
        throw new DataStoreException("Can not update MIF schema.");
    }

    @Override
    public void removeIf(Predicate<? super Feature> filter) throws DataStoreException {
        throw new UnsupportedOperationException("Remove operation is not supported yet.");
    }

    @Override
    public void replaceIf(Predicate<? super Feature> filter, UnaryOperator<Feature> updater) throws DataStoreException {
        throw new DataStoreException("Update operation is not supported yet.");
    }
}
