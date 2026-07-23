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

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.ogcapi.client.common.CollectionsApi;
import org.geotoolkit.ogcapi.dto.LinkRelations;
import org.geotoolkit.ogcapi.dto.common.CollectionDescription;
import org.geotoolkit.ogcapi.dto.common.Collections;
import org.geotoolkit.ogcapi.dto.common.Link;
import org.geotoolkit.ogcapi.request.common.GetCollection;
import org.geotoolkit.ogcapi.request.common.GetCollectionList;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Bialota (Geomatys)
 */
public final class CollectionResource extends AbstractResource implements Aggregate, AutoCloseable {

    final CollectionsApi api;
    private List<Resource> components;

    CollectionResource(OpenApiConfiguration config){
        super(null, false);
        this.api = new CollectionsApi(config);
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(Names.createLocalName(null, null, "Collections"));
    }

    @Override
    public synchronized Collection<? extends Resource> components() throws DataStoreException {
        if (components == null) {
            components = new ArrayList<>();

            try {
                final HttpResponse<Supplier<Collections>> response = api.getCollections(
                        new GetCollectionList().limit(Integer.MAX_VALUE).format("json")).send();
                final Collections collection = response.body().get();
                final List<CollectionDescription> collections = collection.getCollections();

                loop:
                for (CollectionDescription cd : collections) {
                    final HttpResponse<Supplier<CollectionDescription>> fres = api.getCollection(
                            new GetCollection().collectionId(cd.getId()).format("json")).send();
                    final CollectionDescription fullDescription = fres.body().get();

                    if ("record".equals(fullDescription.getItemType())) {
                        //catalog record data
                        components.add(new RecordResource(this, fullDescription));
                        continue loop;
                    }

                    final List<Link> links = fullDescription.getLinks();
                    for (Link link : links) {
                        if (LinkRelations.OGC_DGGRS_LIST.equals(link.getRel()) || LinkRelations.OGC_DGGRS_LIST.equals(link.getRel().replace("http", "https"))) {
                            //dggrs data
                            components.add(new DggrsResource(this, fullDescription));
                            continue loop;
                        }
                        if (LinkRelations.OGC_COVERAGE.equals(link.getRel()) || LinkRelations.OGC_COVERAGE.equals(link.getRel().replace("http", "https"))) {
                            //coverage data
                            components.add(new CoverageResource(this, fullDescription));
                            continue loop;
                        }
                        if (LinkRelations.OGC_ITEMS.equals(link.getRel()) || LinkRelations.OGC_ITEMS.equals(link.getRel().replace("http", "https"))) {
                            //feature data
                            components.add(new FeatureResource(this, fullDescription));
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
