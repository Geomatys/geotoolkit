/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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
package org.geotoolkit.wfs.xml;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public enum WFSVersion {
    v100("1.0.0"),
    v110("1.1.0"),
    v200("2.0.0"),
    v202("2.0.2");

    private final String code;

    WFSVersion(final String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    /**
     * Returns {@link WFSVersion} that matches from the code.
     *
     * @param v code value to resolve.
     * @return {@link WFSVersion} from code value.
     * @throws IllegalArgumentException if the code does not correspond to any existing code in this enum
     */
    public static WFSVersion fromCode(final String v) throws IllegalArgumentException {
        for (final WFSVersion candidat : WFSVersion.values()) {
            if (candidat.getCode().equals(v)) {
                return candidat;
            }
        }

        try{
            return WFSVersion.valueOf(v);
        }catch(IllegalArgumentException ex){/*we tryed*/}

        throw new IllegalArgumentException(v);
    }

    /**
     *
     * @return Versions supported by the toolkit. Each version is represented as
     * a string as X.x.x.
     */
    public static List<String> codes() {
        return Stream.of(WFSVersion.values())
                    .map(WFSVersion::getCode)
                    .collect(Collectors.toList());
    }
}
