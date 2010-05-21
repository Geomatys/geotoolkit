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

package org.geotoolkit.naturesdi;

import java.util.ArrayList;
import java.util.List;
import org.opengis.util.CodeList;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class NATSDI_RankNameCode extends CodeList<NATSDI_RankNameCode> {

    /**
     * List of all enumerations of this type.
     * Must be declared before any enum declaration.
     */
    private static final List<NATSDI_RankNameCode> VALUES = new ArrayList<NATSDI_RankNameCode>(7);

    public static final NATSDI_RankNameCode KINGDOM = new NATSDI_RankNameCode("kingdom");

    public static final NATSDI_RankNameCode PHYLUM = new NATSDI_RankNameCode("phylum");

    public static final NATSDI_RankNameCode CLASS = new NATSDI_RankNameCode("class");

    public static final NATSDI_RankNameCode ORDER = new NATSDI_RankNameCode("order");

    public static final NATSDI_RankNameCode FAMILLY = new NATSDI_RankNameCode("familly");

    public static final NATSDI_RankNameCode GENUS = new NATSDI_RankNameCode("genus");

    public static final NATSDI_RankNameCode SPECIES = new NATSDI_RankNameCode("species");

    /**
     * Constructs an enum with the given name. The new enum is
     * automatically added to the list returned by {@link #values}.
     *
     * @param name The enum name. This name must not be in use by an other enum of this type.
     */
    private NATSDI_RankNameCode(final String name) {
        super(name, VALUES);
    }

    /**
     * Returns the list of {@code NATSDI_RankNameCode}s.
     */
    public static NATSDI_RankNameCode[] values() {
        synchronized (VALUES) {
            return VALUES.toArray(new NATSDI_RankNameCode[VALUES.size()]);
        }
    }

    /**
     * Returns the list of enumerations of the same kind than this enum.
     */
    public NATSDI_RankNameCode[] family() {
        return values();
    }

    /**
     * Returns the NATSDI_RankNameCode that matches the given string, or returns a
     * new one if none match it.
     */
    public static NATSDI_RankNameCode valueOf(String code) {
        return valueOf(NATSDI_RankNameCode.class, code);
    }
}
