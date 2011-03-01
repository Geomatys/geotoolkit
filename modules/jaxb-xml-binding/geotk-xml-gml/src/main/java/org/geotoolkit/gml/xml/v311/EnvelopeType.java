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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.internal.sql.table.Entry;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;


/**
 * Envelope defines an extent using a pair of positions defining opposite corners in arbitrary dimensions. 
 * The first direct position is the "lower corner" (a coordinate position consisting of all the minimal ordinates for each dimension for all points within the envelope),
 * the second one the "upper corner" (a coordinate position consisting of all the maximal ordinates for each dimension for all points within the envelope).
 * 
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
 *           &lt;element name="lowerCorner" type="{http://www.opengis.net/gml}DirectPositionType"/>
 *           &lt;element name="upperCorner" type="{http://www.opengis.net/gml}DirectPositionType"/>
 *         &lt;/sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}coord" maxOccurs="2" minOccurs="2"/>
 *         &lt;element ref="{http://www.opengis.net/gml}pos" maxOccurs="2" minOccurs="2"/>
 *         &lt;element ref="{http://www.opengis.net/gml}coordinates"/>
 *       &lt;/choice>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}SRSReferenceGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
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
public class EnvelopeType implements Entry, Envelope {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.gml.xml.v311");

    @XmlAttribute(namespace="http://www.opengis.net/gml")
    private String id;
    private DirectPositionType lowerCorner;
    private DirectPositionType upperCorner;
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
    @Deprecated
    private List<DirectPositionType> pos;
    @Deprecated
    private CoordinatesType coordinates;

    /**
     * An empty constructor used by JAXB.
     */
    protected EnvelopeType(){}

    /**
     * build a new envelope.
     */
    public EnvelopeType(final String id, final DirectPositionType lowerCorner, final DirectPositionType upperCorner, final String srsName) {
        this.lowerCorner = lowerCorner;
        this.upperCorner = upperCorner;
        this.id          = id;
        this.srsName     = srsName;
    }

    /**
     * build a new envelope.
     */
    public EnvelopeType(final List<DirectPositionType> pos, final String srsName) {
        this.srsName      = srsName;
        this.pos          = pos;
        this.srsDimension = null;
    }

    public EnvelopeType(final Envelope env) {
        this.pos = new ArrayList<DirectPositionType>();
        if (env != null) {
            this.pos.add(new DirectPositionType(env.getLowerCorner(), false));
            this.pos.add(new DirectPositionType(env.getUpperCorner(), false));
            final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
            if (crs != null) {
                try {
                    srsName = "EPSG:" + CRS.lookupEpsgCode(crs, true);
                } catch (FactoryException ex) {
                    LOGGER.log(Level.SEVERE, "Factory exception xhile creating GML envelope from opengis one", ex);
                }
            }
        }
    }

    /**
     * Return the gml identifier of the envelope
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Ihnerited from Entry
     *
     * @return id
     */
    @Override
    public Comparable<?> getIdentifier() {
        return id;
    }

    /**
     * used for Entry
     *
     * @return id
     */
    public String getName() {
        return id;
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
    public void setLowerCorner(final DirectPositionType value) {
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
    public void setUpperCorner(final DirectPositionType value) {
        this.upperCorner = value;
    }

    /**
     * Gets the value of the srsName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
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
    public void setSrsName(final String value) {
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
    public void setSrsDimension(final Integer value) {
        this.srsDimension = value;
    }

    /**
     * Gets the value of the axisLabels property.
     */
    public List<String> getAxisLabels() {
        if (axisLabels == null) {
            axisLabels = new ArrayList<String>();
        }
        return this.axisLabels;
    }

    public void setAxisLabels(final List<String> axisLabels) {
        this.axisLabels = axisLabels;
    }

    public void setAxisLabels(final String axisLabel) {
        if (axisLabel != null) {
            if (axisLabels == null) {
                axisLabels = new ArrayList<String>();
            }
            this.axisLabels.add(axisLabel);
        }
    }


    /**
     * Gets the value of the uomLabels property.
     */
    public List<String> getUomLabels() {
        if (uomLabels == null) {
            uomLabels = new ArrayList<String>();
        }
        return this.uomLabels;
    }


    /**
     * return true if the envelope is fill with x and y coordinates in upper and lower corner.
     * @return
     */
    public boolean isCompleteEnvelope2D() {
        return getLowerCorner() != null && getUpperCorner() != null &&
               getLowerCorner().getValue().size() == 2 && getUpperCorner().getValue().size() == 2;
    }

    /**
     * Deprecated with GML version 3.1. Use the explicit properties "lowerCorner" and "upperCorner" instead.
     * Gets the value of the pos property.
     */
    @Deprecated
    public List<DirectPositionType> getPos() {
        if (pos == null) {
            pos = new ArrayList<DirectPositionType>();
        }
        return this.pos;
    }

    /**
     * Deprecated with GML version 3.1.0.
     * Use the explicit properties "lowerCorner" and "upperCorner" instead.
     * 
     * @return
     *     possible object is
     *     {@link CoordinatesType }
     *     
     */
    @Deprecated
    public CoordinatesType getCoordinates() {
        return coordinates;
    }

    /**
     * Deprecated with GML version 3.1.0.
     * Use the explicit properties "lowerCorner" and "upperCorner" instead.
     * 
     * @param value
     *     allowed object is
     *     {@link CoordinatesType }
     *     
     */
    @Deprecated
    public void setCoordinates(final CoordinatesType value) {
        this.coordinates = value;
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

            return Utilities.equals(this.getAxisLabels(), that.getAxisLabels()) &&
                   Utilities.equals(this.coordinates,     that.coordinates)     &&
                   Utilities.equals(this.id,              that.id)              &&
                   Utilities.equals(this.lowerCorner,     that.lowerCorner)     &&
                   Utilities.equals(this.getPos(),        that.getPos())        &&
                   Utilities.equals(this.srsDimension,    that.srsDimension)    &&
                   Utilities.equals(this.getUomLabels(),  that.getUomLabels())  &&
                   Utilities.equals(this.srsName,         that.srsName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        StringBuilder s = new StringBuilder();
        if (id != null) {
            s.append("id:").append(id).append(" ");
        }
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

    public int getDimension() {
        return srsDimension;
    }

    public double getMinimum(final int i) throws IndexOutOfBoundsException {
        return lowerCorner.getOrdinate(i);
    }

    public double getMaximum(final int i) throws IndexOutOfBoundsException {
        return upperCorner.getOrdinate(i);
    }

    public double getMedian(final int i) throws IndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getSpan(final int i) throws IndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
