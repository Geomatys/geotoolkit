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
package org.geotoolkit.gml.xml.v311;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * Basic encoding for reference system objects, simplifying and restricting the DefinitionType as needed.
 * 
 * <p>Java class for AbstractReferenceSystemBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractReferenceSystemBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.opengis.net/gml}DefinitionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}metaDataProperty" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}srsName"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.opengis.net/gml}id use="required""/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractReferenceSystemBaseType")
@XmlSeeAlso({
    AbstractReferenceSystemType.class
})
public abstract class AbstractReferenceSystemBaseType extends DefinitionType {

    @XmlElement(name = "srsName",  namespace = "http://www.opengis.net/gml")
    private String srsName;

    public AbstractReferenceSystemBaseType() {

    }

    /**
     * super constructor to access to Entry constructor
     */
    public AbstractReferenceSystemBaseType(final String id, final String name, final String description, String srsName)  {
        super(id, name, description);
        this.srsName = srsName;
    }

    /**
     * @return the srsName
     */
    public String getSrsName() {
        return srsName;
    }

    /**
     * @param srsName the srsName to set
     */
    public void setSrsName(String srsName) {
        this.srsName = srsName;
    }

     /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof AbstractReferenceSystemBaseType && super.equals(object)) {
            final AbstractReferenceSystemBaseType that = (AbstractReferenceSystemBaseType) object;
            return Utilities.equals(this.srsName, that.srsName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.srsName != null ? this.srsName.hashCode() : 0);
        return hash;
    }

     @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append("\n");
        if (srsName != null) {
            sb.append("srsName: ").append(srsName).append('\n');
        }
        return sb.toString();
    }
}
