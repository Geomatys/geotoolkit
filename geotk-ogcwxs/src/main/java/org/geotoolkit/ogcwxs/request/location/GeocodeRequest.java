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

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.ols.xml.v121.AddressType;

/**
 * Parameters for the OLS Location Utility Service {@code GeocodeRequest} operation.
 *
 * <blockquote><q>
 * This service performs as a Geocode by determining a geographic position,
 * given a place name, street address or postal code. It also returns a
 * complete, normalized description of the place.
 * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=29">; OGC 07-074 OpenLS Core, Part 3: Location Utility Service</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GeocodeRequest extends AbstractLocationRequest<GeocodeRequest> {

    private final List<AddressType> address = new ArrayList<>();
    private Boolean returnFreeForm;

    /**
     * Addresses to geocode. At least one is required.
     *
     * @see #address(AddressType)
     */
    public List<AddressType> getAddress() {
        return address;
    }

    /**
     * <blockquote><q>
     * List of 1 Addresses. May be unstructured free form address, partial or
     * complete addresses.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=31">; OGC 07-074 OpenLS Core, GeocodeRequestType/Address</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *   <li>At least one Address is required.</li>
     * </ul>
     */
    public GeocodeRequest address(AddressType address) {
        this.address.add(address);
        return this;
    }

    /**
     * Whether the geocoder should also return a free-form representation of matched addresses. Optional.
     *
     * @see #returnFreeForm(Boolean)
     */
    public Boolean getReturnFreeForm() {
        return returnFreeForm;
    }

    /**
     * <blockquote><q>
     * Used to request freeform addresses in the response, as opposed to
     * structured adddresses
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=116">; OGC 07-074 OpenLS Core, GeocodeRequestType/returnFreeForm</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *   <li>Default value: false.</li>
     * </ul>
     */
    public GeocodeRequest returnFreeForm(Boolean returnFreeForm) {
        this.returnFreeForm = returnFreeForm;
        return this;
    }
}
