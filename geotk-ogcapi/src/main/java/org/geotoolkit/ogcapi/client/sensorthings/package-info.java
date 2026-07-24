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


/**
 * OGC SensorThings API bindings: Part 1 (Sensing), Part 2 (Tasking Core), and the STAplus
 * extension, via a single generic entity-set-driven CRUD client ({@link
 * org.geotoolkit.ogcapi.client.sensorthings.SensorThingsApi}).
 *
 * <p>Out of scope: the MQTT pub/sub extension, the {@code $ref} association endpoints, and the
 * {@code $resultFormat=dataArray} compact Observation-array encoding.</p>
 *
 * @see https://docs.ogc.org/is/18-088/18-088.html
 * @see https://docs.ogc.org/is/17-079r1/17-079r1.html
 * @see https://docs.ogc.org/is/22-022r1/22-022r1.html
 */
package org.geotoolkit.ogcapi.client.sensorthings;
