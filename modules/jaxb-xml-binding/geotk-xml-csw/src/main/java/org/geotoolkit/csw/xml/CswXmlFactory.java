/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.csw.xml;

import java.util.List;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.v202.DomainValuesType;
import org.geotoolkit.csw.xml.v202.GetDomainResponseType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class CswXmlFactory {

    public static GetDomainResponse getDomainResponse(String version, List<DomainValues> domainValues) {
        if ("2.0.2".equals(version)) {
            return new GetDomainResponseType(domainValues);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v200.GetDomainResponseType(domainValues);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }

    public static DomainValues getDomainValues(String version, String parameterName, String propertyName, List<String> listOfValues, QName type) {
        if ("2.0.2".equals(version)) {
            return new DomainValuesType(parameterName, null, listOfValues, type);
        } else if ("2.0.0".equals(version)) {
            return new org.geotoolkit.csw.xml.v200.DomainValuesType(parameterName, null, listOfValues, type);
        } else {
            throw new IllegalArgumentException("unsupported version:" + version);
        }
    }
}
