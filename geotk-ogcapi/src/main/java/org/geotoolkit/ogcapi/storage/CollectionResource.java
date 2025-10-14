/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.client.service.ServiceResponse;
import org.geotoolkit.ogcapi.client.common.CollectionsApi;
import org.geotoolkit.ogcapi.model.LinkRelations;
import org.geotoolkit.ogcapi.model.common.CollectionDescription;
import org.geotoolkit.ogcapi.model.common.Collections;
import org.geotoolkit.ogcapi.model.common.Link;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CollectionResource extends AbstractResource implements Aggregate, AutoCloseable {

    final CollectionsApi api;
    private List<Resource> components;

    CollectionResource(OpenApiConfiguration config){
        super(null, false);
        this.api = new CollectionsApi(config);
    }

    @Override
    public synchronized Collection<? extends Resource> components() throws DataStoreException {
        if (components == null) {
            components = new ArrayList<>();

            try {
                final ServiceResponse<Collections> response = api.getCollections(null, null, Integer.MAX_VALUE, "json");
                final Collections collection = response.getData();
                final List<CollectionDescription> collections = collection.getCollections();

                loop:
                for (CollectionDescription cd : collections) {
                    final ServiceResponse<CollectionDescription> fres = api.getCollection(cd.getId(), "json");
                    final CollectionDescription fullDescription = fres.getData();

                    final List<Link> links = fullDescription.getLinks();
                    for (Link link : links) {
                        if (LinkRelations.OGC_DGGRS_LIST.equals(link.getRel()) || LinkRelations.OGC_DGGRS_LIST.equals(link.getRel().replace("http", "https"))) {
                            //dggrs data
                            components.add(new DggrsResource(this, fullDescription));
                            continue loop;
                        }
                    }

                    //undefined resource
                    components.add(new CollectionItemResource(this, fullDescription));
                }

            } catch (ServiceException ex) {
                throw new DataStoreException(ex);
            }
        }
        return java.util.Collections.unmodifiableList(components);
    }

    @Override
    public void close() throws Exception {
        api.close();
    }

}
