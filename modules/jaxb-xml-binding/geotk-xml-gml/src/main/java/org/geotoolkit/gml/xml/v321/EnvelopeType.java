/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2012, Geomatys
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


package org.geotoolkit.gml.xml.v321;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.DirectPosition;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.util.logging.Logging;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;


/**
 * <p>Java class for EnvelopeType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="EnvelopeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;sequence>
 *           &lt;element name="lowerCorner" type="{http://www.opengis.net/gml/3.2}DirectPositionType"/>
 *           &lt;element name="upperCorner" type="{http://www.opengis.net/gml/3.2}DirectPositionType"/>
 *         &lt;/sequence>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}pos" maxOccurs="2" minOccurs="2"/>
 *         &lt;element ref="{http://www.opengis.net/gml/3.2}coordinates"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.opengis.net/gml/3.2}SRSReferenceGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnvelopeType", propOrder = {
    "lowerCorner",
    "upperCorner",
    "pos",
    "coordinates"
})
@XmlSeeAlso({
    EnvelopeWithTimePeriodType.class
})
public class EnvelopeType implements Envelope, org.geotoolkit.gml.xml.Envelope {

    private static final Logger LOGGER = Logging.getLogger(EnvelopeType.class);

    private DirectPositionType lowerCorner;
    private DirectPositionType upperCorner;
    private List<DirectPositionType> pos;
    private CoordinatesType coordinates;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String srsName;
    @XmlAttribute
    @XmlSchemaType(name = "positiveInteger")
    private Integer srsDimension;
    @XmlAttribute
    private List<String> axisLabels;
    @XmlAttribute
    private List<String> uomLabels;

    /**
     * An empty constructor used by JAXB.
     */
    protected EnvelopeType(){}

    /**
     * build a new envelope.
     */
    public EnvelopeType(final DirectPositionType lowerCorner, final DirectPositionType upperCorner, final String srsName) {
        this.lowerCorner = lowerCorner;
        this.upperCorner = upperCorner;
        this.srsName     = srsName;
    }

    public EnvelopeType(final EnvelopeType that) {
        if (that != null) {
            if (that.axisLabels != null) {
                this.axisLabels = new ArrayList<>(that.axisLabels);
            }
            if (that.uomLabels != null) {
                this.uomLabels = new ArrayList<>(that.uomLabels);
            }
            if (that.coordinates != null) {
                this.coordinates = new CoordinatesType(that.coordinates);
            }
            if (that.lowerCorner != null) {
                this.lowerCorner = new DirectPositionType(that.lowerCorner);
            }
            if (that.upperCorner != null) {
                this.upperCorner = new DirectPositionType(that.upperCorner);
            }
            if (that.pos != null) {
                this.pos = new ArrayList<>();
                for (DirectPositionType dp : that.pos) {
                    this.pos.add(new DirectPositionType(dp));
                }
            }
            this.srsDimension = that.srsDimension;
            this.srsName      = that.srsName;
        }
    }

    public EnvelopeType(final org.geotoolkit.gml.xml.Envelope that) {
        if (that != null) {
            if (that.getLowerCorner() != null) {
                this.lowerCorner = new DirectPositionType(that.getLowerCorner(), false);
            }
            if (that.getUpperCorner() != null) {
                this.upperCorner = new DirectPositionType(that.getUpperCorner(), false);
            }
            this.srsDimension = that.getSrsDimension();
            this.srsName      = that.getSrsName();
            this.axisLabels   = that.getAxisLabels();
            this.uomLabels    = that.getUomLabels();
            if (that.getCoordinates() != null) {
                this.coordinates  = new CoordinatesType(that.getCoordinates());
            }
            if (that.getPos() != null) {
                this.pos = new ArrayList<>();
                for (DirectPosition p : that.getPos()) {
                    this.pos.add(new DirectPositionType(p));
                }
            }
        }
    }

    /**
     * Gets the value of the lowerCorner property.
     *
     * @return
     *     possible object is
     *     {@link DirectPositionType }
     *
     */
    @Override
    public DirectPositionType getLowerCorner() {
        return lowerCorner;
    }

    /**
     * Sets the value of the lowerCorner property.
     *
     * @param value
     *     allowed object is
     *     {@link DirectPositionType }
     *
     */
    public void setLowerCorner(DirectPositionType value) {
        this.lowerCorner = value;
    }

    /**
     * Gets the value of the upperCorner property.
     *
     * @return
     *     possible object is
     *     {@link DirectPositionType }
     *
     */
    @Override
    public DirectPositionType getUpperCorner() {
        return upperCorner;
    }

    /**
     * Sets the value of the upperCorner property.
     *
     * @param value
     *     allowed object is
     *     {@link DirectPositionType }
     *
     */
    public void setUpperCorner(DirectPositionType value) {
        this.upperCorner = value;
    }

    /**
     * Gets the value of the pos property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link DirectPositionType }
     *
     *
     */
    @Override
    public List<DirectPositionType> getPos() {
        if (pos == null) {
            pos = new ArrayList<>();
        }
        return this.pos;
    }

    /**
     * Gets the value of the coordinates property.
     *
     * @return
     *     possible object is
     *     {@link CoordinatesType }
     *
     */
    @Override
    public CoordinatesType getCoordinates() {
        return coordinates;
    }

    /**
     * Sets the value of the coordinates property.
     *
     * @param value
     *     allowed object is
     *     {@link CoordinatesType }
     *
     */
    public void setCoordinates(CoordinatesType value) {
        this.coordinates = value;
    }

    /**
     * Gets the value of the srsName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    @Override
    public String getSrsName() {
        return srsName;
    }

    /**
     * Sets the value of the srsName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSrsName(String value) {
        this.srsName = value;
    }

    /**
     * Gets the value of the srsDimension property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    @Override
    public Integer getSrsDimension() {
        return srsDimension;
    }

    /**
     * Sets the value of the srsDimension property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setSrsDimension(Integer value) {
        this.srsDimension = value;
    }

    /**
     * Gets the value of the axisLabels property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    @Override
    public List<String> getAxisLabels() {
        if (axisLabels == null) {
            axisLabels = new ArrayList<>();
        }
        return this.axisLabels;
    }

    public void setAxisLabels(final List<String> axisLabels) {
        this.axisLabels = axisLabels;
    }

    public void setAxisLabels(final String axisLabel) {
        if (axisLabel != null) {
            if (axisLabels == null) {
                axisLabels = new ArrayList<>();
            }
            this.axisLabels.add(axisLabel);
        }
    }

    /**
     * Gets the value of the uomLabels property.
     *
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    @Override
    public List<String> getUomLabels() {
        if (uomLabels == null) {
            uomLabels = new ArrayList<>();
        }
        return this.uomLabels;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        if (srsName != null) {
            try {
                return CRS.decode(srsName);
            } catch (NoSuchAuthorityCodeException ex) {
                LOGGER.log(Level.SEVERE, "NoSuchAuthorityCodeException while looking for GML envelope crs:" + srsName, ex);
            } catch (FactoryException ex) {
                LOGGER.log(Level.SEVERE, "FactoryException while looking for GML envelope crs:" + srsName, ex);
            }
        }
        return null;
    }

    @Override
    public int getDimension() {
        return srsDimension;
    }

    @Override
    public double getMinimum(final int i) throws IndexOutOfBoundsException {
        if (lowerCorner != null) {
            return lowerCorner.getOrdinate(i);
        }
        return -1;
    }

    @Override
    public double getMaximum(int i) throws IndexOutOfBoundsException {
        if (upperCorner != null) {
            return upperCorner.getOrdinate(i);
        }
        return -1;
    }

    @Override
    public double getMedian(int i) throws IndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public double getSpan(int i) throws IndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * return true if the envelope is fill with x and y coordinates in upper and lower corner.
     * @return
     */
    @Override
    public boolean isCompleteEnvelope2D() {
        return getLowerCorner() != null && getUpperCorner() != null &&
               getLowerCorner().getValue().size() == 2 && getUpperCorner().getValue().size() == 2;
    }

    /**
     * Verify if this entry est identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof EnvelopeType) {
            final EnvelopeType that = (EnvelopeType) object;

            return Objects.equals(this.getAxisLabels(), that.getAxisLabels()) &&
                   Objects.equals(this.coordinates,     that.coordinates)     &&
                   Objects.equals(this.lowerCorner,     that.lowerCorner)     &&
                   Objects.equals(this.getPos(),        that.getPos())        &&
                   Objects.equals(this.srsDimension,    that.srsDimension)    &&
                   Objects.equals(this.getUomLabels(),  that.getUomLabels())  &&
                   Objects.equals(this.srsName,         that.srsName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.lowerCorner != null ? this.lowerCorner.hashCode() : 0);
        hash = 79 * hash + (this.upperCorner != null ? this.upperCorner.hashCode() : 0);
        hash = 79 * hash + (this.pos != null ? this.pos.hashCode() : 0);
        hash = 79 * hash + (this.coordinates != null ? this.coordinates.hashCode() : 0);
        hash = 79 * hash + (this.srsName != null ? this.srsName.hashCode() : 0);
        hash = 79 * hash + (this.srsDimension != null ? this.srsDimension.hashCode() : 0);
        hash = 79 * hash + (this.axisLabels != null ? this.axisLabels.hashCode() : 0);
        hash = 79 * hash + (this.uomLabels != null ? this.uomLabels.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        if (srsDimension != null) {
            s.append("srsDImension:").append(srsDimension).append(" ");
        }
        if (srsName != null) {
            s.append("srsName:").append(srsName).append(" ");
        }
        if (lowerCorner != null) {
            s.append('\n').append("lowerCorner:").append(lowerCorner.toString());
        }
        if (upperCorner != null) {
            s.append('\n').append("upperCorner:").append(upperCorner.toString());
        }
        if (pos != null) {
            int i = 0;
            for (DirectPositionType posi: pos) {
                s.append('\n').append("pos").append(i).append(":").append(posi.toString());
                i++;
            }
            s.append('\n');
        }
        if (coordinates != null) {
            s.append("coordinates:").append(coordinates.toString());
        }
        if (axisLabels != null) {
            int i = 0;
            for (String axis: axisLabels) {
                s.append('\n').append("axis").append(i).append(":").append(axis);
                i++;
            }
            s.append('\n');
        }
        if (uomLabels != null) {
            int i = 0;
            for (String uom: uomLabels) {
                s.append('\n').append("uom").append(i).append(":").append(uom);
                i++;
            }
            s.append('\n');
        }
        return s.toString();
    }
}
