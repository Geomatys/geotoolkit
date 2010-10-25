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
package org.geotoolkit.csw.xml.v200;

import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.csw.xml.Record;
import org.geotoolkit.dublincore.xml.v1.elements.SimpleLiteral;
import org.geotoolkit.ows.xml.v100.BoundingBoxType;
import org.geotoolkit.ows.xml.v100.WGS84BoundingBoxType;
import org.geotoolkit.util.Utilities;


/**
 * This type extends DCMIRecordType to add ows:BoundingBox; 
 * it may be used to specify a bounding envelope for the catalogued resource.
 *       
 * 
 * <p>Java class for RecordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RecordType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/cat/csw}DCMIRecordType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows}BoundingBox" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RecordType", propOrder = {
    "boundingBox"
})
@XmlRootElement(name= "Record")
public class RecordType extends DCMIRecordType implements Record {

    @XmlElementRef(name = "BoundingBox", namespace = "http://www.opengis.net/ows", type = JAXBElement.class)
    private JAXBElement<? extends BoundingBoxType> boundingBox;

     /**
     * An empty constructor used by JAXB
     */
    public RecordType() {
        
    }
    
    /**
     * Build a new Record TODO add contributor, source , spatial, right, relation
     */
    public RecordType(SimpleLiteral identifier, SimpleLiteral title, SimpleLiteral type, 
            List<SimpleLiteral> subject, SimpleLiteral format, SimpleLiteral modified, SimpleLiteral _abstract,
            BoundingBoxType bbox, SimpleLiteral creator, SimpleLiteral distributor, SimpleLiteral language, 
            SimpleLiteral spatial,SimpleLiteral references) {
        
        super(identifier, title,type, subject, format, modified, _abstract, creator, distributor, language, spatial, references);
        
        if (bbox instanceof WGS84BoundingBoxType)
            this.boundingBox = owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType)bbox);
        else
            this.boundingBox = owsFactory.createBoundingBox(bbox);
        
        
    }
    
    /**
     * Gets the value of the boundingBox property.
     * 
     */
    public JAXBElement<? extends BoundingBoxType> getBoundingBox() {
        return boundingBox;
    }
    
    /**
     * Sets the value of the boundingBox property.
     * 
     */
    public void setBoundingBox(JAXBElement<? extends BoundingBoxType> value) {
        this.boundingBox = ((JAXBElement<? extends BoundingBoxType> ) value);
    }

    public void setBoundingBox(BoundingBoxType bbox) {
        if (bbox instanceof WGS84BoundingBoxType) {
            this.boundingBox = owsFactory.createWGS84BoundingBox((WGS84BoundingBoxType)bbox);
        } else {
            this.boundingBox = owsFactory.createBoundingBox(bbox);
        }
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (boundingBox != null && boundingBox.getValue() != null) {
            s.append("bounding box:").append('\n');
            s.append(boundingBox.getValue().toString()).append('\n');
        }
        return s.toString();
    }
    
     /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof RecordType && super.equals(object)) {
            final RecordType that = (RecordType) object;
            
            if (this.boundingBox != null && that.boundingBox != null) {
                return Utilities.equals(this.boundingBox.getValue(),   that.boundingBox.getValue());
                
            } else {
                 if (this.boundingBox == null && that.boundingBox == null) {
                     return true;
                 }
                 return false;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
