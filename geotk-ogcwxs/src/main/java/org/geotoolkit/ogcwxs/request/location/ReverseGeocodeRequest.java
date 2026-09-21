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
import org.geotoolkit.ols.xml.v121.PositionType;
import org.geotoolkit.ols.xml.v121.ReverseGeocodePreferenceType;

/**
 * Parameters for the OLS Location Utility Service {@code ReverseGeocodeRequest} operation.
 *
 * <blockquote><q>
 * The service also performs as a reverse Geocode by determining a complete,
 * normalized place name/street address/postal code, given a geographic
 * position.
 * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=29">; OGC 07-074 OpenLS Core, Part 3: Location Utility Service</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class ReverseGeocodeRequest extends AbstractLocationRequest<ReverseGeocodeRequest> {

    private PositionType position;
    private final List<ReverseGeocodePreferenceType> reverseGeocodePreference = new ArrayList<>();

    /**
     * Position to reverse-geocode. Required.
     *
     * @see #position(PositionType)
     */
    public PositionType getPosition() {
        return position;
    }

    /**
     * <blockquote><q>
     * The starting position (lat,long) for Reverse Geocoder.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=32">; OGC 07-074 OpenLS Core, ReverseGeocodeRequest Parameters/Position</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *   <li>Required.</li>
     * </ul>
     */
    public ReverseGeocodeRequest position(PositionType position) {
        this.position = position;
        return this;
    }

    /**
     * Preferences constraining the kind of location returned (e.g. street, intersection). Optional.
     *
     * @see #reverseGeocodePreference(ReverseGeocodePreferenceType)
     */
    public List<ReverseGeocodePreferenceType> getReverseGeocodePreference() {
        return reverseGeocodePreference;
    }

    /**
     * <blockquote><q>
     * Describes the preference for the response from the Reverse Geocoder
     * Service: StreetAddress, IntersectionAddress, or PositionOfInterest (Place
     * and/or PostalCode).
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=32">; OGC 07-074 OpenLS Core, ReverseGeocodeRequest Parameters/ReverseGeocodePreference</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *   <li>Optional; may be repeated.</li>
     *   <li>If not specified, the service returns the nearest StreetAddress.</li>
     * </ul>
     */
    public ReverseGeocodeRequest reverseGeocodePreference(ReverseGeocodePreferenceType preference) {
        this.reverseGeocodePreference.add(preference);
        return this;
    }
}
