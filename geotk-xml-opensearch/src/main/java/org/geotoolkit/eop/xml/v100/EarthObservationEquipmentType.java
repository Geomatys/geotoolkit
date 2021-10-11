/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019
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

package org.geotoolkit.eop.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractFeatureType;


/**
 * <p>Classe Java pour EarthObservationEquipmentType complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType name="EarthObservationEquipmentType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractFeatureType">
 *       &lt;sequence>
 *         &lt;element name="platform" type="{http://earth.esa.int/eop}PlatformPropertyType" minOccurs="0"/>
 *         &lt;element name="instrument" type="{http://earth.esa.int/eop}InstrumentPropertyType" minOccurs="0"/>
 *         &lt;element name="sensor" type="{http://earth.esa.int/eop}SensorPropertyType" minOccurs="0"/>
 *         &lt;element name="acquisitionParameters" type="{http://earth.esa.int/eop}AcquisitionPropertyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EarthObservationEquipmentType", propOrder = {
    "platform",
    "instrument",
    "sensor",
    "acquisitionParameters"
})
public class EarthObservationEquipmentType
    extends AbstractFeatureType
{

    protected PlatformPropertyType platform;
    protected InstrumentPropertyType instrument;
    protected SensorPropertyType sensor;
    protected AcquisitionPropertyType acquisitionParameters;

    /**
     * Obtient la valeur de la propriété platform.
     *
     * @return
     *     possible object is
     *     {@link PlatformPropertyType }
     *
     */
    public PlatformPropertyType getPlatform() {
        return platform;
    }

    /**
     * Définit la valeur de la propriété platform.
     *
     * @param value
     *     allowed object is
     *     {@link PlatformPropertyType }
     *
     */
    public void setPlatform(PlatformPropertyType value) {
        this.platform = value;
    }

    /**
     * Obtient la valeur de la propriété instrument.
     *
     * @return
     *     possible object is
     *     {@link InstrumentPropertyType }
     *
     */
    public InstrumentPropertyType getInstrument() {
        return instrument;
    }

    /**
     * Définit la valeur de la propriété instrument.
     *
     * @param value
     *     allowed object is
     *     {@link InstrumentPropertyType }
     *
     */
    public void setInstrument(InstrumentPropertyType value) {
        this.instrument = value;
    }

    /**
     * Obtient la valeur de la propriété sensor.
     *
     * @return
     *     possible object is
     *     {@link SensorPropertyType }
     *
     */
    public SensorPropertyType getSensor() {
        return sensor;
    }

    /**
     * Définit la valeur de la propriété sensor.
     *
     * @param value
     *     allowed object is
     *     {@link SensorPropertyType }
     *
     */
    public void setSensor(SensorPropertyType value) {
        this.sensor = value;
    }

    /**
     * Obtient la valeur de la propriété acquisitionParameters.
     *
     * @return
     *     possible object is
     *     {@link AcquisitionPropertyType }
     *
     */
    public AcquisitionPropertyType getAcquisitionParameters() {
        return acquisitionParameters;
    }

    /**
     * Définit la valeur de la propriété acquisitionParameters.
     *
     * @param value
     *     allowed object is
     *     {@link AcquisitionPropertyType }
     *
     */
    public void setAcquisitionParameters(AcquisitionPropertyType value) {
        this.acquisitionParameters = value;
    }

}
