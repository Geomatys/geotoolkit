/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
import java.util.Optional;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Resource;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.ogcapi.client.routes.RoutesApi;
import org.geotoolkit.ogcapi.dto.LinkRelations;
import org.geotoolkit.ogcapi.dto.common.Link;
import org.geotoolkit.ogcapi.dto.routes.RouteList;
import org.opengis.util.GenericName;

/**
 * Root resource for OGC API - Routes: lists the routes previously created on the
 * server (Manage Routes conformance class).
 *
 * @author Quentin BIALOTA (Geomatys)
 * @see <a href="https://docs.ogc.org/DRAFTS/21-000.html">OGC API - Routes - Part 1: Core (draft)</a>
 */
public final class RoutesResource extends AbstractResource implements Aggregate, AutoCloseable {

    final RoutesApi api;
    private List<Resource> components;

    RoutesResource(OpenApiConfiguration config) {
        super(null, false);
        this.api = new RoutesApi(config);
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(Names.createLocalName(null, null, "Routes"));
    }

    @Override
    public synchronized Collection<? extends Resource> components() throws DataStoreException {
        if (components == null) {
            components = new ArrayList<>();
            try {
                final RouteList list = api.getRoutes().send().body().get();
                for (Link link : list.getLinks()) {
                    if (LinkRelations.ITEM.equals(link.getRel()) || LinkRelations.ITEM.equals(link.getRel().replace("http", "https"))) {
                        components.add(new RouteResource(this, link));
                    }
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
