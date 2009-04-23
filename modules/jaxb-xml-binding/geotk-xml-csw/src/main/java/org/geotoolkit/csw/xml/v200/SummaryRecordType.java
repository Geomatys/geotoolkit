/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.csw.xml.v200;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.dublincore.xml.v1.elements.SimpleLiteral;


/**
 * This type defines a summary representation of the common record format. 
 * It extends AbstractRecordType to include the core properties.
 *       
 * 
 * <p>Java class for SummaryRecordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SummaryRecordType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw}AbstractRecordType">
 *       &lt;sequence>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element ref="{http://www.purl.org/dc/elements/1.1/}identifier"/>
 *           &lt;element ref="{http://www.purl.org/dc/elements/1.1/}type"/>
 *           &lt;element ref="{http://www.purl.org/dc/elements/1.1/}title"/>
 *           &lt;element ref="{http://www.purl.org/dc/elements/1.1/}subject"/>
 *           &lt;element ref="{http://www.purl.org/dc/elements/1.1/}format"/>
 *           &lt;element ref="{http://www.purl.org/dc/elements/1.1/}relation"/>
 *           &lt;element ref="{http://www.purl.org/dc/terms/}modified"/>
 *           &lt;element ref="{http://www.purl.org/dc/terms/}abstract"/>
 *           &lt;element ref="{http://www.purl.org/dc/terms/}spatial"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SummaryRecordType", propOrder = {
    "identifierOrTypeOrTitle"
})
public class SummaryRecordType extends AbstractRecordType {

    @XmlElementRefs({
        @XmlElementRef(name = "type"      , namespace = "http://www.purl.org/dc/elements/1.1/", type = JAXBElement.class),
        @XmlElementRef(name = "modified"  , namespace = "http://www.purl.org/dc/terms/"       , type = JAXBElement.class),
        @XmlElementRef(name = "subject"   , namespace = "http://www.purl.org/dc/elements/1.1/", type = JAXBElement.class),
        @XmlElementRef(name = "spatial"   , namespace = "http://www.purl.org/dc/terms/"       , type = JAXBElement.class),
        @XmlElementRef(name = "title"     , namespace = "http://www.purl.org/dc/elements/1.1/", type = JAXBElement.class),
        @XmlElementRef(name = "abstract"  , namespace = "http://www.purl.org/dc/terms/"       , type = JAXBElement.class),
        @XmlElementRef(name = "format"    , namespace = "http://www.purl.org/dc/elements/1.1/", type = JAXBElement.class),
        @XmlElementRef(name = "identifier", namespace = "http://www.purl.org/dc/elements/1.1/", type = JAXBElement.class),
        @XmlElementRef(name = "relation"  , namespace = "http://www.purl.org/dc/elements/1.1/", type = JAXBElement.class)
    })
    private List<JAXBElement<SimpleLiteral>> identifierOrTypeOrTitle;

    /**
     * Gets the value of the identifierOrTypeOrTitle property.
     * 
     */
    public List<JAXBElement<SimpleLiteral>> getIdentifierOrTypeOrTitle() {
        if (identifierOrTypeOrTitle == null) {
            identifierOrTypeOrTitle = new ArrayList<JAXBElement<SimpleLiteral>>();
        }
        return this.identifierOrTypeOrTitle;
    }

}
