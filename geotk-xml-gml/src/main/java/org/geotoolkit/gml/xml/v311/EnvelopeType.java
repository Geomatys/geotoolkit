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
package org.geotoolkit.gml.xml.v311;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.util.logging.Logging;
import org.opengis.filter.Expression;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;
import static org.geotoolkit.util.Utilities.listNullEquals;
import org.opengis.util.ScopedName;


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
@XmlRootElement(name="Envelope")
public class EnvelopeType implements org.opengis.geometry.Envelope, org.geotoolkit.gml.xml.Envelope, Expression {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.gml.xml.v311");

    /**
     * this attribute do not exist. Must be removed
     * @deprecated
     */
    @XmlAttribute(namespace="http://www.opengis.net/gml")
    @Deprecated
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
     *
     * @param id This parameter is deprecated. it will no ne used.
     * @param srsName CRS identifier name.
     *
     * @deprecated use the constrcutor without id.
     */
    @Deprecated
    public EnvelopeType(final String id, final DirectPositionType lowerCorner, final DirectPositionType upperCorner, final String srsName) {
        this(lowerCorner, upperCorner, srsName);
    }

    /**
     * build a new GML envelope.
     *
     * @param srsName CRS identifier name.
     */
    public EnvelopeType(final DirectPositionType lowerCorner, final DirectPositionType upperCorner, final String srsName) {
        this.lowerCorner = lowerCorner;
        this.upperCorner = upperCorner;
        this.srsName     = srsName;
    }

    /**
     * build a new envelope.
     */
    @Deprecated
    public EnvelopeType(final List<DirectPositionType> pos, final String srsName) {
        this.srsName      = srsName;
        this.pos          = pos;
        this.srsDimension = null;
    }

    public EnvelopeType(final org.opengis.geometry.Envelope env) {
        this.pos = new ArrayList<>();
        if (env != null) {
            this.pos.add(new DirectPositionType(env.getLowerCorner(), false));
            this.pos.add(new DirectPositionType(env.getUpperCorner(), false));
            final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
            if (crs != null) {
                try {
                     if (crs instanceof CompoundCRS) {
                        final StringBuilder sb = new StringBuilder();
                        final CompoundCRS compCrs = (CompoundCRS) crs;
                        // see OGC 07-092r3 7.5.2
                        sb.append("urn:ogc:def:crs,");
                        for (CoordinateReferenceSystem child : compCrs.getComponents()) {
                            String childSrs = IdentifiedObjects.lookupURN(child, null);
                            if (childSrs != null) {
                                if (childSrs.startsWith("urn:ogc:def:")) {
                                    childSrs = childSrs.substring(12);
                                }
                                sb.append(childSrs).append(',');
                            } else {
                                sb.append("crs:EPSG::unknow,");
                            }
                        }
                        sb.deleteCharAt(sb.length() - 1);
                        srsName = sb.toString();
                    } else {
                        srsName = IdentifiedObjects.lookupURN(crs, null);
                        if (srsName == null) {
                           srsName = "urn:ogc:def:crs:EPSG::unknow";
                        }
                    }
                    srsDimension = crs.getCoordinateSystem().getDimension();
                } catch (FactoryException ex) {
                    LOGGER.log(Level.SEVERE, "Factory exception xhile creating GML envelope from opengis one", ex);
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
     */
    @Override
    public void setSrsName(final String value) {
        this.srsName = value;
    }

    /**
     * Gets the value of the srsDimension property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
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
     */
    @Override
    public void setSrsDimension(final Integer value) {
        this.srsDimension = value;
    }

    /**
     * Gets the value of the axisLabels property.
     */
    @Override
    public List<String> getAxisLabels() {
        if (axisLabels == null) {
            axisLabels = new ArrayList<>();
        }
        return this.axisLabels;
    }

    @Override
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
     */
    @Override
    public List<String> getUomLabels() {
        if (uomLabels == null) {
            uomLabels = new ArrayList<>();
        }
        return this.uomLabels;
    }

    /**
     * return true if the envelope is fill with x and y coordinates in upper and lower corner.
     */
    @Override
    public boolean isCompleteEnvelope2D() {
        return getLowerCorner() != null && getUpperCorner() != null &&
               getLowerCorner().getValue().size() == 2 && getUpperCorner().getValue().size() == 2;
    }

    /**
     * Deprecated with GML version 3.1. Use the explicit properties "lowerCorner" and "upperCorner" instead.
     * Gets the value of the pos property.
     */
    @Deprecated
    @Override
    public List<DirectPositionType> getPos() {
        if (pos == null) {
            pos = new ArrayList<>();
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
     */
    @Deprecated
    @Override
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

            return listNullEquals(this.axisLabels,      that.axisLabels)      &&
                   Objects.equals(this.coordinates,     that.coordinates)     &&
                   Objects.equals(this.id,              that.id)              &&
                   Objects.equals(this.lowerCorner,     that.lowerCorner)     &&
                   listNullEquals(this.pos,             that.pos)             &&
                   Objects.equals(this.srsDimension,    that.srsDimension)    &&
                   listNullEquals(this.uomLabels,       that.uomLabels)       &&
                   Objects.equals(this.srsName,         that.srsName);
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
            s.append("srsDimension:").append(srsDimension).append(" ");
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

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        if (srsName != null) {
            try {
                return CRS.forCode(srsName);
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
        if (srsDimension == null && srsName != null) {
            // try to compute the dimension
            CoordinateReferenceSystem crs = getCoordinateReferenceSystem();
            if (crs != null) {
                srsDimension = crs.getCoordinateSystem().getDimension();
            }
        }
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
    public double getMaximum(final int i) throws IndexOutOfBoundsException {
        if (upperCorner != null) {
            return upperCorner.getOrdinate(i);
        }
        return -1;
    }

    @Override
    public double getMedian(final int i) throws IndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public double getSpan(final int i) throws IndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public ScopedName getFunctionName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<Expression> getParameters() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object apply(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Expression toValueType(Class type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
