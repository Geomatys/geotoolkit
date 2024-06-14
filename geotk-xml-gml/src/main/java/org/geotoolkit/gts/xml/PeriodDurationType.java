/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

package org.geotoolkit.gts.xml;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import org.geotoolkit.temporal.object.TemporalUtilities;


/**
 * <p>Java class for TM_PeriodDuration_PropertyType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="TM_PeriodDuration_PropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.isotc211.org/2005/gts}TM_PeriodDuration"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.isotc211.org/2005/gco}nilReason"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TM_PeriodDuration_PropertyType", propOrder = {
    "tmPeriodDuration"
})
public class PeriodDurationType implements TemporalAmount {

    @XmlElement(name = "TM_PeriodDuration")
    protected Duration tmPeriodDuration;
    @XmlAttribute(namespace = "http://www.isotc211.org/2005/gco")
    protected List<String> nilReason;

    public PeriodDurationType() {
    }

    public PeriodDurationType(final String s) {
        try {
            final DatatypeFactory factory = DatatypeFactory.newInstance();
            this.tmPeriodDuration = factory.newDuration(s);
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger("org.geotoolkit.gts.xml").log(Level.WARNING, "Error while initializing the TM_PeriodDuration", ex);
        }
    }

    public PeriodDurationType(final boolean isPositive, final BigInteger years, final BigInteger months, final BigInteger days, final BigInteger hours, final BigInteger minutes,
        final BigDecimal seconds) {
        try {
            final DatatypeFactory factory = DatatypeFactory.newInstance();
            this.tmPeriodDuration = factory.newDuration(isPositive, years, months, days, hours, minutes, seconds);
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger("org.geotoolkit.gts.xml").log(Level.WARNING, "Error while initializing the TM_PeriodDuration", ex);
        }
    }
    @Override
    public List<TemporalUnit> getUnits() {
        return List.of(ChronoUnit.YEARS, ChronoUnit.MONTHS, ChronoUnit.DAYS, ChronoUnit.HOURS, ChronoUnit.MINUTES, ChronoUnit.SECONDS);
    }

    @Override
    public long get(TemporalUnit unit) {
        if (unit instanceof ChronoUnit c) {
            switch (c) {
                case YEARS:   return tmPeriodDuration.getYears();
                case MONTHS:  return tmPeriodDuration.getMonths();
                case DAYS:    return tmPeriodDuration.getDays();
                case HOURS:   return tmPeriodDuration.getHours();
                case MINUTES: return tmPeriodDuration.getMinutes();
                case SECONDS: return tmPeriodDuration.getSeconds();
            }
        }
        throw new UnsupportedTemporalTypeException("Unsupported temporal unit: " + unit);
    }

    /**
     * Gets the value of the tmPeriodDuration property.
     *
     * @return
     *     possible object is
     *     {@link Duration }
     */
    public Duration getTMPeriodDuration() {
        return tmPeriodDuration;
    }

    /**
     * Sets the value of the tmPeriodDuration property.
     *
     * @param value
     *     allowed object is
     *     {@link Duration }
     */
    public void setTMPeriodDuration(Duration value) {
        this.tmPeriodDuration = value;
    }

    /**
     * Gets the value of the nilReason property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     */
    public List<String> getNilReason() {
        if (nilReason == null) {
            nilReason = new ArrayList<String>();
        }
        return this.nilReason;
    }

    private long getTimeInMillis() {
        return tmPeriodDuration.getTimeInMillis(new Date(0));
    }

    @Override
    public Temporal addTo(Temporal temporal) {
        return TemporalUtilities.toInstant(temporal).plusMillis(getTimeInMillis());
    }

    @Override
    public Temporal subtractFrom(Temporal temporal) {
        return TemporalUtilities.toInstant(temporal).minusMillis(getTimeInMillis());
    }

    @Override
    public String toString() {
        return String.valueOf(tmPeriodDuration);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof PeriodDurationType that) {
            return Objects.equals(this.nilReason,        that.nilReason) &&
                   Objects.equals(this.tmPeriodDuration, that.tmPeriodDuration);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.tmPeriodDuration != null ? this.tmPeriodDuration.hashCode() : 0);
        hash = 83 * hash + (this.nilReason != null ? this.nilReason.hashCode() : 0);
        return hash;
    }
}
