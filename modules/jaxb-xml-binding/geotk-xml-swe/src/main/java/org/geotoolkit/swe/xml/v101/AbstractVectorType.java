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
package org.geotoolkit.swe.xml.v101;

import java.net.URI;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swe.xml.AbstractVector;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for AbstractVectorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractVectorType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0.1}AbstractDataRecordType">
 *       &lt;attribute name="referenceFrame" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="localFrame" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractVectorType")
@XmlSeeAlso({
    GeoLocationArea.class,
    EnvelopeType.class,
    PositionType.class,
    VectorType.class
})
public abstract class AbstractVectorType extends AbstractDataRecordType implements AbstractVector {

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private URI referenceFrame;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private URI localFrame;

    public AbstractVectorType() {

    }

    public AbstractVectorType(final AbstractVector av) {
        super(av);
        if (av != null) {
            this.localFrame = av.getLocalFrame();
            this.referenceFrame = av.getReferenceFrame();
        }
    }

    public AbstractVectorType(final String definition) {
        super(definition);
    }

    public AbstractVectorType(final URI referenceFrame, final URI localFrame) {
        this.localFrame = localFrame;
        this.referenceFrame = referenceFrame;
    }
    
    /**
     * Gets the value of the referenceFrame property.
     */
    public URI getReferenceFrame() {
        return referenceFrame;
    }

    /**
     * Sets the value of the referenceFrame property.
     */
    public void setReferenceFrame(final URI value) {
        this.referenceFrame = value;
    }

    /**
     * Gets the value of the localFrame property.
     */
    public URI getLocalFrame() {
        return localFrame;
    }

    /**
     * Sets the value of the localFrame property.
     */
    public void setLocalFrame(final URI value) {
        this.localFrame = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof AbstractVectorType && super.equals(object, mode)) {
            final AbstractVectorType that = (AbstractVectorType) object;

            return Utilities.equals(this.localFrame,     that.localFrame) &&
                   Utilities.equals(this.referenceFrame, that.referenceFrame);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.referenceFrame != null ? this.referenceFrame.hashCode() : 0);
        hash = 37 * hash + (this.localFrame != null ? this.localFrame.hashCode() : 0);
        return hash;
    }
}
