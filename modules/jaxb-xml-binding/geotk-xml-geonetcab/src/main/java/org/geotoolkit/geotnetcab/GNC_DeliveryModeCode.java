/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */


package org.geotoolkit.geotnetcab;

import java.util.ArrayList;
import java.util.List;
import org.opengis.util.CodeList;

/**
 *
 * @author guilhem
 */
public class GNC_DeliveryModeCode  extends CodeList<GNC_DeliveryModeCode> {

     /**
     * List of all enumerations of this type.
     * Must be declared before any enum declaration.
     */
    private static final List<GNC_DeliveryModeCode> VALUES = new ArrayList<GNC_DeliveryModeCode>(3);

    public static final GNC_DeliveryModeCode PRESENTIAL = new GNC_DeliveryModeCode("Presential");
    public static final GNC_DeliveryModeCode DISTANT    = new GNC_DeliveryModeCode("Distant");
    public static final GNC_DeliveryModeCode BLENDED    = new GNC_DeliveryModeCode("Blended");

    /**
     * Constructs an enum with the given name. The new enum is
     * automatically added to the list returned by {@link #values}.
     *
     * @param name The enum name. This name must not be in use by an other enum of this type.
     */
    private GNC_DeliveryModeCode(final String name) {
        super(name, VALUES);
    }

    /**
     * Returns the list of {@code GNC_DeliveryModeCode}s.
     */
    public static GNC_DeliveryModeCode[] values() {
        synchronized (VALUES) {
            return VALUES.toArray(new GNC_DeliveryModeCode[VALUES.size()]);
        }
    }

    /**
     * Returns the list of enumerations of the same kind than this enum.
     */
    public GNC_DeliveryModeCode[] family() {
        return values();
    }

    /**
     * Returns the GNC_DeliveryModeCode that matches the given string, or returns a
     * new one if none match it.
     */
    public static GNC_DeliveryModeCode valueOf(final String code) {
        return valueOf(GNC_DeliveryModeCode.class, code);
    }
}
