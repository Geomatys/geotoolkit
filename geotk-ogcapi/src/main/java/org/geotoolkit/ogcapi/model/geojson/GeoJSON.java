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
package org.geotoolkit.ogcapi.model.geojson;

import java.util.List;
import java.util.Map.Entry;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.privy.AttributeConvention;
import org.apache.sis.storage.DataStoreException;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateXY;
import org.locationtech.jts.geom.CoordinateXYZM;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GeoJSON {

    private static final GeometryFactory GF = new GeometryFactory();

    private GeoJSON() {

    }

    public static Feature fromGeoJSON(GeoJSONFeature json, FeatureType type) throws DataStoreException {

        final GeoJSONGeometry jsongeom = json.getGeometry();
        Geometry geom = null;
        if (jsongeom != null) {
            geom = fromGeoJSON(jsongeom);
        }

        if (type == null) {
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName("geojson");

            if (json.getId() != null) {
                ftb.addAttribute(json.getId().getClass()).setName(AttributeConvention.IDENTIFIER_PROPERTY).addRole(AttributeRole.IDENTIFIER_COMPONENT);
            }
            if (geom != null) {
                ftb.addAttribute(geom.getClass()).setName(AttributeConvention.GEOMETRY_PROPERTY).addRole(AttributeRole.DEFAULT_GEOMETRY);
            }
            for (Entry<String,Object> entry : json.getProperties().entrySet()) {
                ftb.addAttribute(entry.getValue().getClass()).setName(entry.getKey());
            }
            type = ftb.build();
        }

        final Feature feature = type.newInstance();

        if (json.getId() != null) {
            feature.setPropertyValue(AttributeConvention.IDENTIFIER, json.getId());
        }

        if (geom != null) {
            feature.setPropertyValue(AttributeConvention.GEOMETRY, geom);
        }

        for (Entry<String,Object> entry : json.getProperties().entrySet()) {
            feature.setPropertyValue(entry.getKey(), entry.getValue());
        }
        return feature;
    }

    public static Geometry fromGeoJSON(GeoJSONGeometry geom) throws DataStoreException {
        if (geom instanceof GeoJSONPoint json) {
            final List<Double> coordinates = json.getCoordinates();
            final int size = coordinates.size();
            final Coordinate coord;
            switch (size) {
                case 2 : coord = new CoordinateXY(coordinates.get(0), coordinates.get(1)); break;
                case 3 : coord = new Coordinate(coordinates.get(0), coordinates.get(1), coordinates.get(2)); break;
                case 4 : coord = new CoordinateXYZM(coordinates.get(0), coordinates.get(1), coordinates.get(2), coordinates.get(3)); break;
                default : throw new DataStoreException("Geometry " + size + "not supported");
            }
            return GF.createPoint(coord);
        } else {
            throw new DataStoreException("Geometry not supported " + geom);
        }
    }

}
