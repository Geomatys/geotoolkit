/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
 * <p>Java class for GNC_ThematicTypeCode_PropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GNC_ThematicTypeCode_PropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.mdweb-project.org/files/xsd}GNC_ThematicTypeCode" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.isotc211.org/2005/gco}nilReason"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
public class GNC_ThematicTypeCode extends CodeList<GNC_ThematicTypeCode>  {

    /**
     * List of all enumerations of this type.
     * Must be declared before any enum declaration.
     */
    private static final List<GNC_ThematicTypeCode> VALUES = new ArrayList<GNC_ThematicTypeCode>(9);

    public static final GNC_ThematicTypeCode DISASTER     = new GNC_ThematicTypeCode("Disaster");
    public static final GNC_ThematicTypeCode HEALTH       = new GNC_ThematicTypeCode("Health");
    public static final GNC_ThematicTypeCode ENERGY       = new GNC_ThematicTypeCode("Energy");
    public static final GNC_ThematicTypeCode CLIMATE      = new GNC_ThematicTypeCode("Climate");
    public static final GNC_ThematicTypeCode WATER        = new GNC_ThematicTypeCode("Water");
    public static final GNC_ThematicTypeCode ECOSYSTEMS   = new GNC_ThematicTypeCode("Ecosystems");
    public static final GNC_ThematicTypeCode AGRICULTURE  = new GNC_ThematicTypeCode("Agriculture");
    public static final GNC_ThematicTypeCode BIODIVERSITY = new GNC_ThematicTypeCode("Biodiversity");
    public static final GNC_ThematicTypeCode OTHERS       = new GNC_ThematicTypeCode("Others");

    /**
     * Constructs an enum with the given name. The new enum is
     * automatically added to the list returned by {@link #values}.
     *
     * @param name The enum name. This name must not be in use by an other enum of this type.
     */
    private GNC_ThematicTypeCode(final String name) {
        super(name, VALUES);
    }

    /**
     * Returns the list of {@code GNC_ThematicTypeCode}s.
     */
    public static GNC_ThematicTypeCode[] values() {
        synchronized (VALUES) {
            return VALUES.toArray(new GNC_ThematicTypeCode[VALUES.size()]);
        }
    }

    /**
     * Returns the list of enumerations of the same kind than this enum.
     */
    public GNC_ThematicTypeCode[] family() {
        return values();
    }

    /**
     * Returns the GNC_ThematicTypeCode that matches the given string, or returns a
     * new one if none match it.
     */
    public static GNC_ThematicTypeCode valueOf(final String code) {
        return valueOf(GNC_ThematicTypeCode.class, code);
    }
}
