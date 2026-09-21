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

import jakarta.xml.bind.JAXBElement;
import org.geotoolkit.ols.xml.v121.AbstractPOISelectionCriteriaType;
import org.geotoolkit.ols.xml.v121.DistanceUnitType;
import org.geotoolkit.ols.xml.v121.POILocationType;
import org.geotoolkit.ols.xml.v121.SortDirectionType;

/**
 * Parameters for the OLS Directory Service {@code DirectoryRequest} operation.
 *
 * <blockquote><q>
 * This service returns a list of POI's or a single detailed POI record
 * given a set of selection criteria.
 * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=17">; OGC 07-074 OpenLS Core, Part 4: Directory Service</a></small></i>
 * </blockquote>
 * <br>
 * <pre>
 * &lt;DirectoryRequest&gt;
 *     &lt;POIProperties directoryType="White Pages"&gt;
 *         &lt;POIProperty name="POIName" value="Red Dragon Chinese Restaurant"/&gt;
 *     &lt;/POIProperties&gt;
 * &lt;/DirectoryRequest&gt;
 * </pre>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class DirectoryRequest extends AbstractLocationRequest<DirectoryRequest> {

    private POILocationType poiLocation;
    private JAXBElement<? extends AbstractPOISelectionCriteriaType> poiSelectionCriteria;
    private String sortCriteria;
    private SortDirectionType sortDirection;
    private DistanceUnitType distanceUnit;

    /**
     * Location around which points of interest are searched. Optional.
     *
     * @see #poiLocation(POILocationType)
     */
    public POILocationType getPoiLocation() {
        return poiLocation;
    }

    /**
     * <blockquote><q>
     * Defines the type of location constraints to perform search.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=105">; OGC 07-074 OpenLS Core, DirectoryRequest Parameters/POILocation</a></small></i>
     * </blockquote>
     */
    public DirectoryRequest poiLocation(POILocationType poiLocation) {
        this.poiLocation = poiLocation;
        return this;
    }

    /**
     * Selection criteria (e.g. POI properties, category), wrapped through the matching
     * {@link org.geotoolkit.ols.xml.v121.ObjectFactory#createPOISelectionCriteria(org.geotoolkit.ols.xml.v121.AbstractPOISelectionCriteriaType)}
     * (or equivalent) factory method. Required.
     *
     * @see #poiSelectionCriteria(JAXBElement)
     */
    public JAXBElement<? extends AbstractPOISelectionCriteriaType> getPoiSelectionCriteria() {
        return poiSelectionCriteria;
    }

    /**
     * <blockquote><q>
     * Base element that specifies POI selection criteria
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=105">; OGC 07-074 OpenLS Core, DirectoryRequest Parameters/AbstractPOISelectionCriteria</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *   <li>Required.</li>
     * </ul>
     */
    public DirectoryRequest poiSelectionCriteria(JAXBElement<? extends AbstractPOISelectionCriteriaType> poiSelectionCriteria) {
        this.poiSelectionCriteria = poiSelectionCriteria;
        return this;
    }

    /**
     * Name of the property results should be sorted by. Optional.
     *
     * @see #sortCriteria(String)
     */
    public String getSortCriteria() {
        return sortCriteria;
    }

    /**
     * <blockquote><q>
     * POI property used to sort the result. It may take on the well-known
     * values of "Name" and "Distance" as well as any other POI property
     * names.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=105">; OGC 07-074 OpenLS Core, DirectoryRequest Parameters/SortCriteria</a></small></i>
     * </blockquote>
     */
    public DirectoryRequest sortCriteria(String sortCriteria) {
        this.sortCriteria = sortCriteria;
        return this;
    }

    /**
     * Sort direction, defaults to ascending. Optional.
     *
     * @see #sortDirection(SortDirectionType)
     */
    public SortDirectionType getSortDirection() {
        return sortDirection;
    }

    /**
     * <blockquote><q>
     * Defines the sort direction (Ascending/Descending)
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=105">; OGC 07-074 OpenLS Core, DirectoryRequest Parameters/SortDirection</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *   <li>Allowed values: Ascending, Descending.</li>
     *   <li>Default: Ascending.</li>
     * </ul>
     */
    public DirectoryRequest sortDirection(SortDirectionType sortDirection) {
        this.sortDirection = sortDirection;
        return this;
    }

    /**
     * Unit used for reported distances, defaults to meters. Optional.
     *
     * @see #distanceUnit(DistanceUnitType)
     */
    public DistanceUnitType getDistanceUnit() {
        return distanceUnit;
    }

    /**
     * <blockquote><q>
     * Specifies the unit for measuring distance.
     * </q><i><small><a href="https://docs.ogc.org/is/07-074/07-074.pdf#page=105">; OGC 07-074 OpenLS Core, DirectoryRequest Parameters/DistanceUnit</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *   <li>Default: M.</li>
     * </ul>
     */
    public DirectoryRequest distanceUnit(DistanceUnitType distanceUnit) {
        this.distanceUnit = distanceUnit;
        return this;
    }
}
