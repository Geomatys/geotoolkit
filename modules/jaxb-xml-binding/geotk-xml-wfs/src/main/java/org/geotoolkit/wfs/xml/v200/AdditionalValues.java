/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.wfs.xml.v200;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element ref="{http://www.opengis.net/wfs/2.0}ValueCollection"/>
 *         &lt;element ref="{http://www.opengis.net/wfs/2.0}SimpleFeatureCollection"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "valueCollection",
    "simpleFeatureCollection"
})
@XmlRootElement(name = "additionalValues")
public class AdditionalValues {

    @XmlElement(name = "ValueCollection")
    private ValueCollectionType valueCollection;
    @XmlElementRef(name = "SimpleFeatureCollection", namespace = "http://www.opengis.net/wfs/2.0", type = JAXBElement.class)
    private JAXBElement<? extends SimpleFeatureCollectionType> simpleFeatureCollection;

    /**
     * Gets the value of the valueCollection property.
     *
     * @return
     *     possible object is
     *     {@link ValueCollectionType }
     *
     */
    public ValueCollectionType getValueCollection() {
        return valueCollection;
    }

    /**
     * Sets the value of the valueCollection property.
     *
     * @param value
     *     allowed object is
     *     {@link ValueCollectionType }
     *
     */
    public void setValueCollection(ValueCollectionType value) {
        this.valueCollection = value;
    }

    /**
     * Gets the value of the simpleFeatureCollection property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link FeatureCollectionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SimpleFeatureCollectionType }{@code >}
     *
     */
    public JAXBElement<? extends SimpleFeatureCollectionType> getSimpleFeatureCollection() {
        return simpleFeatureCollection;
    }

    /**
     * Sets the value of the simpleFeatureCollection property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link FeatureCollectionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link SimpleFeatureCollectionType }{@code >}
     *
     */
    public void setSimpleFeatureCollection(JAXBElement<? extends SimpleFeatureCollectionType> value) {
        this.simpleFeatureCollection = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]\n");
        if (valueCollection != null) {
            sb.append("valueCollection:").append(valueCollection).append('\n');
        }
        if (simpleFeatureCollection != null) {
            sb.append("simpleFeatureCollection:").append(simpleFeatureCollection.getValue()).append('\n');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AdditionalValues) {
            final AdditionalValues that = (AdditionalValues) obj;
            boolean spa = false;
            if (this.simpleFeatureCollection != null && that.simpleFeatureCollection != null) {
                spa = Utilities.equals(this.simpleFeatureCollection.getValue(), that.simpleFeatureCollection.getValue());
            } else if (this.simpleFeatureCollection == null && that.simpleFeatureCollection == null) {
                spa = true;
            }
            return spa && Utilities.equals(this.valueCollection, that.valueCollection);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.valueCollection != null ? this.valueCollection.hashCode() : 0);
        hash = 71 * hash + (this.simpleFeatureCollection != null ? this.simpleFeatureCollection.hashCode() : 0);
        return hash;
    }
}
