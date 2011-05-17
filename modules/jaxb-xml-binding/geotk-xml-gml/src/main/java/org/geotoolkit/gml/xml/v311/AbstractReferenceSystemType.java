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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.Utilities;


/**
 * Description of a spatial and/or temporal reference system used by a dataset.
 * 
 * <p>Java class for AbstractReferenceSystemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractReferenceSystemType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractReferenceSystemBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}srsID" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}remarks" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}validArea" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/gml}scope" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractReferenceSystemType", propOrder = {
    "srsID",
    "validArea",
    "scope"
})
// GeographicCRSType.class,VerticalCRSType.class,GeocentricCRSType.class,EngineeringCRSType.class, TemporalCRSType.class, AbstractGeneralDerivedCRSType.class, CompoundCRSType.class
@XmlSeeAlso({
    ImageCRSType.class
})
public abstract class AbstractReferenceSystemType extends AbstractReferenceSystemBaseType {

    private List<IdentifierType> srsID = new ArrayList<IdentifierType>();
    private ExtentType validArea;
    private String scope;

    /**
     * Empty constructor used by JAXB
     */
    public AbstractReferenceSystemType() {
    }

    /**
     * build an abstract reference system.
     */
    public AbstractReferenceSystemType(final String id, final String name, final String description, final String srsName)  {
        super(id, name, description, srsName);
    }

    /**
     * build an abstract reference system.
     */
    public AbstractReferenceSystemType(final List<IdentifierType> srsID, final ExtentType validArea, final String scope) {
        
        this.scope     = scope;
        this.validArea = validArea;
        this.srsID     = srsID;
    }
    
    /**
     * Set of alterative identifications of this reference system. The first srsID, if any, is normally the primary identification code, and any others are aliases.Gets the value of the srsID property.
     */
    public List<IdentifierType> getSrsID() {
        return Collections.unmodifiableList(srsID);
    }

    /**
     * Gets the value of the validArea property.
     */
    public ExtentType getValidArea() {
        return validArea;
    }

    /**
     * Gets the value of the scope property.
     */
    public String getScope() {
        return scope;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append("\n");
        if (srsID != null) {
            sb.append("srsID: ").append('\n');
            for (IdentifierType s: srsID) {
                sb.append(s).append('\n');
            }
        }
        if (scope != null) {
            sb.append("scope: ").append(scope).append('\n');
        }
        if (validArea != null) {
            sb.append("valid area: ").append(validArea).append('\n');
        }
        return sb.toString();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof AbstractReferenceSystemType && super.equals(object, mode)) {
            final AbstractReferenceSystemType that = (AbstractReferenceSystemType) object;

            return Utilities.equals(this.scope,     that.scope) &&
                   Utilities.equals(this.srsID,     that.srsID) &&
                   Utilities.equals(this.validArea, that.validArea);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + (this.srsID != null ? this.srsID.hashCode() : 0);
        hash = 11 * hash + (this.validArea != null ? this.validArea.hashCode() : 0);
        hash = 11 * hash + (this.scope != null ? this.scope.hashCode() : 0);
        return hash;
    }
}
