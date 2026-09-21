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
package org.geotoolkit.ogcwxs.request.location;

/**
 * Parameters for the OLS Presentation Service {@code GetPortrayMapCapabilitiesRequest} operation.
 * This operation carries no payload of its own beyond the common request/header attributes.
 *
 * <blockquote><q>
 * Request the capabilities that the implementation provides (srs, formats,
 * layers, styles)
 * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=120">; OGC 07-074 OpenLS Core, GetPortrayMapCapabilitiesRequestType</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetPortrayMapCapabilitiesRequest extends AbstractLocationRequest<GetPortrayMapCapabilitiesRequest> {
}
