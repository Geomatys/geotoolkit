/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.data;

import java.util.Collections;
import java.util.Map;

import org.geotoolkit.util.SimpleInternationalString;

import org.opengis.util.InternationalString;

/**
 * A Parameter defines information about a valid process parameter.
 *
 * @author gdavis
 */
public class Parameter<T> {

    /**
     * This is the key (ie machine readable text) used to represent
     * this parameter in a java.util.Map.
     *
     * @param key (or machine readable name) for this parameter.
     */
    public final String key;

    /**
     * Human readable title/name of this parameter.
     */
    public final InternationalString title;

    /**
     * Human readable description of this parameter.
     */
    public final InternationalString description;

    /**
     * Class binding for this parameter.
     * <p>
     * When a value is supplied for this key it should be of the provided type.
     */
    public final Class<T> type;

    /** Can the value be missing? Or is null allowed...
     *@return true if a value is required to be both present and non null
     **/
    public final boolean required;

    /** What is the min and max number of this paramter there can be
     * ( a value of -1 for min means 0 or more,
     * a value of -1 for max means any number greater than or equal to the min value )
     * 
     * eg: a geometry union process can have any number of geom parameters,
     * so by setting the max to -1 and the min to 2 we accomplish that.
     */
    public final int minOccurs;
    public final int maxOccurs;

    /**
     * A sample value; often used as a default when prompting the end-user
     * to fill in the details before executing a process.
     */
    public final Object sample;

    /**
     * Hints for the user interface
     */

    /** "featureType" FeatureType to validate a Feature value against */
    public static final String FEATURE_TYPE = "featureType";

    /** Boolean indicating whether the parameter shall be used as a password field,
     * provides a hint for UI's to mask text fields, configuration systems to encrypt content, etc
     */
    public static final String IS_PASSWORD = "isPassword";

    /**
     * "length" Integer used to limit the length of strings or literal geometries.
     */
    public static final String LENGTH = "length";

    /** "crs": CoordinateReferenceSystem used to restrict a Geometry literal */
    public static final String CRS = "crs";

    /** "element": Class to use as the Element type for List<Element>. Please restrict
     * your use of this facility to simple types; for most higher order data structures
     * multiplicity is already accounted for - example MultiGeometry. */
    public static final String ELEMENT = "element";

    /**
     * "min" and "max" may be useful for restrictions for things like int sizes, etc.
     */
    public static final String MIN = "min";
    public static final String MAX = "max";
    
    /**
     * Refinement of type; such as the FeatureType of a FeatureCollection, or component type of a List.
     * <p>
     * This information is supplied (along with type) to allow a process implementor communicate
     * additional restrictions on the allowed value beyond the strict type.
     * <p>
     * The following keys are understood at this time: LENGTH, FEATURE_TYPE, CRS, ELEMENT
     * .. additional keys will be documented as static final fields over time.
     * <p>
     * Any restrictions mentioned here should be mentioned as part of your
     * parameter description. This metadata is only used to help restrict what
     * the user enters; not all client application will understand and respect
     * these keys - please communicate with your end-user.
     */
    public final Map<String, Object> metadata;

    /**
     * Mandatory parameter - quickly constructed with out a properly internationalized
     * title and description.
     * 
     * @param key
     * @param type
     * @param title
     * @param description
     * @deprecated Please translate title and description into an InternationalString  
     */
    public Parameter(String key, Class<T> type, String title,
            String description) {
        this(key, type, new SimpleInternationalString(title), new SimpleInternationalString(description));
    }

    /**
     * Mandatory parameter
     * @param key
     * @param type
     * @param title
     * @param description
     */
    public Parameter(String key, Class<T> type, InternationalString title,
            InternationalString description) {
        this(key, type, title, description, false, 1, 1, null, null);
    }

    /**
     * Mandiatory parameter with metadata.
     * @param key
     * @param type
     * @param title
     * @param description
     * @param metadata
     */
    public Parameter(String key, Class<T> type, InternationalString title,
            InternationalString description, Map<String, Object> metadata) {
        this(key, type, title, description, false, 1, 1, null, metadata);
    }

    /**
     * Addition of optional parameters
     * @param key
     * @param type
     * @param title
     * @param description
     * @param required
     * @param min
     * @param max
     * @param sample
     * @param metadata
     */
    public Parameter(String key, Class<T> type, InternationalString title,
            InternationalString description,
            boolean required, int min, int max, Object sample,
            Map<String, Object> metadata) {
        this.key = key;
        this.title = title;
        this.type = type;
        this.description = description;
        this.required = required;
        this.minOccurs = min;
        this.maxOccurs = max;
        this.sample = sample;
        this.metadata = metadata == null ? null : Collections.unmodifiableMap(metadata);
    }
}
