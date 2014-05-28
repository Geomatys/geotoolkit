/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.data.geojson.utils;

import org.apache.sis.util.Static;

/**
 * @author Quentin Boileau (Geomatys)
 */
public final class GeoJSONTypes extends Static {

    public static final String FEATURE_COLLECTION = "FeatureCollection";
    public static final String FEATURE = "Feature";

    public static final String POINT = "Point";
    public static final String LINESTRING = "LineString";
    public static final String POLYGON = "Polygon";
    public static final String MULTI_POINT = "MultiPoint";
    public static final String MULTI_LINESTRING = "MultiLineString";
    public static final String MULTI_POLYGON = "MultiPolygon";
    public static final String GEOMETRY_COLLECTION = "GeometryCollection";

    public static final String CRS_NAME = "name";
    public static final String CRS_LINK = "link";

    public static final String CRS_TYPE_PROJ4 = "proj4";
    public static final String CRS_TYPE_OGCWKT = "ogcwkt";
    public static final String CRS_TYPE_ESRIWKT = "esriwkt";

}
