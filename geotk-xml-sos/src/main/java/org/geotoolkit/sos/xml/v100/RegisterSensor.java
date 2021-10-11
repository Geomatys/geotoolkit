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
package org.geotoolkit.sos.xml.v100;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.swes.xml.InsertSensor;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sos/1.0}RequestBaseType">
 *       &lt;sequence>
 *         &lt;element name="SensorDescription">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://www.opengis.net/sos/1.0}ObservationTemplate"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *  @author Guilhem Legal
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegisterSensor", propOrder = {
    "sensorDescription",
    "observationTemplate"
})
@XmlRootElement(name = "RegisterSensor")
public class RegisterSensor extends RequestBaseType implements InsertSensor {

    @XmlElement(name = "SensorDescription", required = true)
    private RegisterSensor.SensorDescription sensorDescription;
    @XmlElement(name = "ObservationTemplate", required = true)
    private ObservationTemplate observationTemplate;

    /**
     * An empty constructor used by JAXB
     */
    RegisterSensor() {
    }

    /**
     * Build a new registerSensor request.
     */
    public RegisterSensor(final String version, final Object sensorDescription, final ObservationTemplate observationTemplate) {
        super(version);
        this.observationTemplate = observationTemplate;
        this.sensorDescription = new SensorDescription(sensorDescription);
    }

    /**
     * Gets the value of the sensorDescription property.
     */
    public RegisterSensor.SensorDescription getRealSensorDescription() {
        return sensorDescription;
    }

    /**
     * Gets the value of the observationTemplate property.
     */
    @Override
    public ObservationTemplate getObservationTemplate() {
        return observationTemplate;
    }

    @Override
    public Object getSensorDescription() {
        if (sensorDescription != null) {
            return sensorDescription.getAny();
        }
        return null;
    }

    /**
     * compatibility with SOS 1.0.0
     * @return always null
     */
    @Override
    public String getProcedureDescriptionFormat() {
        return null;
    }

    /**
     * compatibility with SOS 1.0.0
     * @return always null
     */
    @Override
    public Object getInsertionMetadata() {
        return null;
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof RegisterSensor && super.equals(object)) {
            final RegisterSensor that = (RegisterSensor) object;
            return Objects.equals(this.observationTemplate, that.observationTemplate) &&
                   Objects.equals(this.sensorDescription,   that.sensorDescription);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.sensorDescription != null ? this.sensorDescription.hashCode() : 0);
        hash = 23 * hash + (this.observationTemplate != null ? this.observationTemplate.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder("Register sensor:");
        s.append('\n').append("sensor description:").append('\n').append(sensorDescription);
        if (observationTemplate != null) {
            s.append('\n').append("observation template:").append('\n').append(observationTemplate.toString());
        }
        return s.toString();
    }

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;any/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "any"
    })
    public static class SensorDescription {

        @XmlAnyElement(lax = true)
        private Object any;

        /**
         * An empty constructor used by JAXB.
         */
        SensorDescription(){

        }

        /**
         * Build a new Sensor description with an object.
         */
        public SensorDescription(final Object description){
            any = description;
        }

        /**
         * Gets the value of the any property.
         */
        public Object getAny() {
            return any;
        }

        /**
         * Verify if this entry is identical to the specified object.
         */
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof SensorDescription && super.equals(object)) {
                final SensorDescription that = (SensorDescription) object;
                return Objects.equals(this.any, that.any);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 31 * hash + (this.any != null ? this.any.hashCode() : 0);
            return hash;
        }
    }

}
