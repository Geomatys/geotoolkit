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
package org.geotoolkit.storage.geojson;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.sis.util.Static;

/**
 * @author Quentin Boileau (Geomatys)
 * @author Johann Sorel (Geomatys)
 * @version 2.0
 * @since   2.0
 * @module
 */
public final class GeoJSONConstants extends Static {

    public static final String FEATURE_COLLECTION = "FeatureCollection";
    public static final String FEATURE = "Feature";

    public static final String POINT = "Point";
    public static final String LINESTRING = "LineString";
    public static final String POLYGON = "Polygon";
    public static final String MULTI_POINT = "MultiPoint";
    public static final String MULTI_LINESTRING = "MultiLineString";
    public static final String MULTI_POLYGON = "MultiPolygon";
    public static final String GEOMETRY_COLLECTION = "GeometryCollection";

    public static final List<String> GEOMETRY_TYPES = Arrays.asList(POINT, LINESTRING, POLYGON,
            MULTI_POINT, MULTI_LINESTRING, MULTI_POLYGON, GEOMETRY_COLLECTION);

    public static final String CRS_NAME = "name";
    public static final String CRS_LINK = "link";

    public static final String CRS_TYPE_PROJ4 = "proj4";
    public static final String CRS_TYPE_OGCWKT = "ogcwkt";
    public static final String CRS_TYPE_ESRIWKT = "esriwkt";

    public static final String TYPE = "type";
    public static final String FEATURES = "features";
    public static final String GEOMETRY = "geometry";
    public static final String GEOMETRIES = "geometries";
    public static final String COORDINATES = "coordinates";
    public static final String PROPERTIES = "properties";
    public static final String CRS = "crs";
    public static final String NAME = "name";
    public static final String HREF = "href";
    public static final String BBOX = "bbox";
    public static final String ID = "id";
}
