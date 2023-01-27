/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.observation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sis.internal.util.UnmodifiableArrayList;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class ObservationStoreCapabilities {

    /**
     * The list of supported response formats for each version.
     */
    public final Map<String, List<String>> responseFormats;

    /**
     * The list of supported response modes.
     */
    public final List<String> responseModes ;

    /**
     * the list of properties that can be applied on the result.
     */
    public final List<String> queryableResultProperties;

    /**
     * Return true if each observation has a position.
     */
    public final boolean isBoundedObservation;

    /**
     * true if the filter reader take in charge the calculation of the collection bounding shape.
     */
    public final boolean computeCollectionBound;

    /**
     * True if templates are filled with a default period when there is no eventTime suplied.
     */
    public final boolean isDefaultTemplateTime;

    /**
     * True if tha store has a filter reader implementation.
     */
    public final boolean hasFilter;

    public ObservationStoreCapabilities(boolean isDefaultTemplateTime, boolean isBoundedObservation,  boolean computeCollectionBound,
            List<String> queryableResultProperties, Map<String, List<String>> responseFormats, List<String> responseModes, boolean hasFilter) {
        this.isDefaultTemplateTime = isDefaultTemplateTime;
        this.isBoundedObservation = isBoundedObservation;
        this.computeCollectionBound = computeCollectionBound;
        this.queryableResultProperties = queryableResultProperties != null ? Collections.unmodifiableList(queryableResultProperties) : Collections.EMPTY_LIST;
        this.responseFormats = responseFormats != null ? Collections.unmodifiableMap(responseFormats) : Collections.EMPTY_MAP;
        this.responseModes = responseModes != null ? Collections.unmodifiableList(responseModes) : Collections.EMPTY_LIST;
        this.hasFilter = hasFilter;
    }
}
