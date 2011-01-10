/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.inspire.xml.vs;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;
import org.geotoolkit.internal.jaxb.code.ScopeCodeAdapter;
import org.opengis.metadata.maintenance.ScopeCode;

/**
 *
 * @author guilhem
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _ExtendedCapabilities_QNAME = new QName("http://inspira.europa.eu/networkservice/view/1.0", "ExtendedCapabilities");

    /**
     * Create an instance of {@link GetCapabilitiesType }
     *
     */
    public ExtendedCapabilitiesType createExtendedCapabilitieType() {
        return new ExtendedCapabilitiesType();
    }

    public LanguagesType createLanguagesType() {
        return new LanguagesType();
    }

    public LanguageType createLanguageType() {
        return new LanguageType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElementSetNameType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://inspira.europa.eu/networkservice/view/1.0", name = "ExtendedCapabilities" , substitutionHeadNamespace = "http://www.opengis.net/wms", substitutionHeadName = "_ExtendedCapabilities")
    public JAXBElement<ExtendedCapabilitiesType> createExtendedCapabilities(final ExtendedCapabilitiesType value) {
        return new JAXBElement<ExtendedCapabilitiesType>(_ExtendedCapabilities_QNAME, ExtendedCapabilitiesType.class, null, value);
    }

}
