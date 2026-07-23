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
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.sis.storage.AbstractResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.ogcapi.dto.common.Link;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSON;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONFeature;
import org.geotoolkit.ogcapi.dto.geojson.GeoJSONFeatureCollection;
import org.geotoolkit.ogcapi.request.routes.GetRoute;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.util.GenericName;

/**
 * A single route, exposed as its Route Exchange Model feature collection
 * ({@code GET /routes/{routeId}}: overview / start / segment / end features).
 *
 * @author Quentin BIALOTA (Geomatys)
 * @see <a href="https://docs.ogc.org/DRAFTS/21-000.html">OGC API - Routes - Part 1: Core (draft)</a>
 */
public final class RouteResource extends AbstractResource implements FeatureSet {

    private final RoutesResource parent;
    private final String routeId;
    private GeoJSONFeatureCollection route;

    RouteResource(RoutesResource parent, Link routeLink) {
        super(parent);
        this.parent = parent;
        final String href = routeLink.getHref();
        this.routeId = href.substring(href.lastIndexOf('/') + 1);
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(Names.createLocalName(null, null, routeId));
    }

    private synchronized GeoJSONFeatureCollection getRoute() throws DataStoreException {
        if (route == null) {
            try {
                route = parent.api.getRoute(new GetRoute().routeId(routeId)).send().body().get();
            } catch (ServiceException ex) {
                throw new DataStoreException(ex);
            }
        }
        return route;
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        final List<GeoJSONFeature> features = getRoute().getFeatures();
        if (features.isEmpty()) {
            throw new DataStoreException("Route " + routeId + " has no features");
        }
        // ponytail: REM features are heterogeneous (overview/start/segment/end, each with its
        // own geometry and properties), the spec defines no single feature type. Using the
        // first feature's inferred type as the nominal FeatureSet type is a simplification;
        // split into one FeatureSet per featureType if a caller needs strict typing.
        return GeoJSON.fromGeoJSON(features.get(0), null).getType();
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        final List<GeoJSONFeature> jsonFeatures = getRoute().getFeatures();
        final List<Feature> features = new ArrayList<>(jsonFeatures.size());
        for (GeoJSONFeature f : jsonFeatures) {
            features.add(GeoJSON.fromGeoJSON(f, null));
        }
        return features.stream();
    }

}
