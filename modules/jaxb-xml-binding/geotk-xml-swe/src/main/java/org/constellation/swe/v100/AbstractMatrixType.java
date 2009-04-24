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

package org.constellation.swe.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for AbstractMatrixType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractMatrixType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0}AbstractDataArrayType">
 *       &lt;attribute name="referenceFrame" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="localFrame" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractMatrixType")
@XmlSeeAlso({
    SquareMatrixType.class
})
public class AbstractMatrixType extends AbstractDataArrayType {

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String referenceFrame;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String localFrame;

    /**
     * Gets the value of the referenceFrame property.
     */
    public String getReferenceFrame() {
        return referenceFrame;
    }

    /**
     * Sets the value of the referenceFrame property.
     */
    public void setReferenceFrame(String value) {
        this.referenceFrame = value;
    }

    /**
     * Gets the value of the localFrame property.
     */
    public String getLocalFrame() {
        return localFrame;
    }

    /**
     * Sets the value of the localFrame property.
     */
    public void setLocalFrame(String value) {
        this.localFrame = value;
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof AbstractMatrixType && super.equals(object)) {
            final AbstractMatrixType  that = (AbstractMatrixType) object;
            return Utilities.equals(this.localFrame,     that.localFrame)&&
                   Utilities.equals(this.referenceFrame, that.referenceFrame);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (this.referenceFrame != null ? this.referenceFrame.hashCode() : 0);
        hash = 79 * hash + (this.localFrame != null ? this.localFrame.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (localFrame != null) {
            s.append("localFrame:").append(localFrame).append('\n');
        }
        if (referenceFrame != null) {
            s.append("referenceFrame:").append(referenceFrame).append('\n');
        }
        return s.toString();
    }
}
