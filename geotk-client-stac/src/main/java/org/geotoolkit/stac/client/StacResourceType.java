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
package org.geotoolkit.stac.client;

/**
 * Identifies the type of resource returned by a STAC API endpoint URL.
 *
 * @author Quentin Bialota (Geomatys)
 */
public enum StacResourceType {

    /**
     * The URL points to a STAC Item (GeoJSON Feature with {@code "type": "Feature"}).
     */
    ITEM,

    /**
     * The URL points to a STAC Collection ({@code "type": "Collection"}).
     */
    COLLECTION,

    /**
     * The URL returned a valid JSON response but its {@code "type"} field did not match
     * any recognized STAC resource type (e.g. a Catalog, ItemCollection, or API root).
     */
    UNKNOWN
}
