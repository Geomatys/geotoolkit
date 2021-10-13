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
package org.geotoolkit.sml.xml.v101;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.metadata.MetadataStandard;
import org.geotoolkit.gml.xml.v311.AbstractFeatureType;
import org.geotoolkit.sml.xml.AbstractSML;


/**
 * Main Abstract SensorML Object
 *
 * <p>Java class for AbstractSMLType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AbstractSMLType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.opengis.net/gml}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;sequence>
 *           &lt;sequence>
 *             &lt;element ref="{http://www.opengis.net/gml}description" minOccurs="0"/>
 *             &lt;element ref="{http://www.opengis.net/gml}name" minOccurs="0"/>
 *             &lt;element ref="{http://www.opengis.net/gml}boundedBy" minOccurs="0"/>
 *           &lt;/sequence>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractSMLType")
@XmlSeeAlso({
    AbstractProcessType.class
})
public abstract class AbstractSMLType extends AbstractFeatureType implements AbstractSML {

    public AbstractSMLType() {

    }

    public AbstractSMLType(final AbstractSML sm) {
        super(sm);
    }

    @Override
    public MetadataStandard getStandard() {
        return SensorMLStandard.COMPONENT;
    }
}
