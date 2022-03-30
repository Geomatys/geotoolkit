/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2018, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.List;
import org.apache.sis.internal.storage.MetadataBuilder;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.geotoolkit.storage.AbstractResource;


/**
 * Interoperability with legacy API.
 */
final class ProductAggregate extends AbstractResource implements Aggregate {
    private final DatabaseStore store;

    final ProductEntry product;

    private List<Resource> components;

    ProductAggregate(final DatabaseStore store, final ProductEntry product) {
        super(product.getIdentifier());
        this.store   = store;
        this.product = product;
    }

    private static List<Resource> wrap(final DatabaseStore store, final List<ProductEntry> products) throws DataStoreException {
        final Resource[] resources = new ProductResource[products.size()];
        for (int i=0; i<resources.length; i++) {
            final ProductEntry product = products.get(i);
            resources[i] = store.createResource(product);
        }
        return UnmodifiableArrayList.wrap(resources);
    }

    @Override
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    public synchronized List<Resource> components() throws DataStoreException {
        /*
         * Synchronization note: no method from `org.geotoolkit.coverage.sql` package should invoke
         * (directly or indirectly) this `components()` method, in order to avoid risk of deadlock.
         */
        if (components == null) {
            components = wrap(store, product.components());
        }
        return components;
    }

    @Override
    protected DefaultMetadata createMetadata() throws DataStoreException {
        final MetadataBuilder builder = new MetadataBuilder();
        product.createMetadata(builder);
        return builder.build();
    }

    static final class AlsoGrid extends ProductResource implements Aggregate {
        private List<Resource> components;

        AlsoGrid(final DatabaseStore store, final ProductEntry product) {
            super(store, product);
        }

        @Override
        @SuppressWarnings("ReturnOfCollectionOrArrayField")
        public synchronized List<Resource> components() throws DataStoreException {
            /*
             * Synchronization note: no method from `org.geotoolkit.coverage.sql` package should invoke
             * (directly or indirectly) this `components()` method, in order to avoid risk of deadlock.
             */
            if (components == null) {
                components = wrap((DatabaseStore) store, product.components());
            }
            return components;
        }
    }
}
