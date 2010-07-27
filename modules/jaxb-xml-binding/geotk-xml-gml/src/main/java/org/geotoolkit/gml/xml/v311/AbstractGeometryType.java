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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.Utilities;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.Precision;
import org.opengis.geometry.TransfiniteSet;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.primitive.PrimitiveBoundary;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;


/**
 * All geometry elements are derived directly or indirectly from this abstract supertype. A geometry element may 
 * 			have an identifying attribute ("gml:id"), a name (attribute "name") and a description (attribute "description"). It may be associated 
 * 			with a spatial reference system (attribute "srsName"). The following rules shall be adhered: - Every geometry type shall derive 
 * 			from this abstract type. - Every geometry element (i.e. an element of a geometry type) shall be directly or indirectly in the 
 * 			substitution group of _Geometry.
 * 
 * <p>Java class for AbstractGeometryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractGeometryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGMLType">
 *       &lt;attGroup ref="{http://www.opengis.net/gml}SRSReferenceGroup"/>
 *       &lt;attribute name="gid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractGeometryType")
@XmlSeeAlso({
    AbstractRingType.class,
    AbstractGeometricPrimitiveType.class,
    AbstractGeometricAggregateType.class
})
public abstract class AbstractGeometryType extends AbstractGMLEntry implements Geometry, Expression {

    @XmlAttribute
    private String gid;
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
     * empty constructor used by JAXB
     */
    AbstractGeometryType(){}

    public AbstractGeometryType(Integer srsDimension, String srsName, List<String> axisLabels, List<String> uomLabels){
        this.axisLabels   = axisLabels;
        this.srsDimension = srsDimension;
        this.srsName      = srsName;
        this.uomLabels    = uomLabels;
    }

    public AbstractGeometryType(String srsName) {
        this.srsName      = srsName;
    }

    public AbstractGeometryType(String id, String srsName) {
        super(id);
        this.srsName      = srsName;
    }

    /**
     * Gets the value of the gid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGid() {
        return gid;
    }

    /**
     * Sets the value of the gid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGid(String value) {
        this.gid = value;
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
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the axisLabels property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAxisLabels().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAxisLabels() {
        if (axisLabels == null) {
            axisLabels = new ArrayList<String>();
        }
        return this.axisLabels;
    }

    /**
     * Gets the value of the uomLabels property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the uomLabels property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUomLabels().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getUomLabels() {
        if (uomLabels == null) {
            uomLabels = new ArrayList<String>();
        }
        return this.uomLabels;
    }

    @Override
    public PrimitiveBoundary getBoundary() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        String srsName = getSrsName();

        if(srsName != null){
            try {
                return CRS.decode(getSrsName());
            } catch (FactoryException ex) {
                Logger.getLogger(AbstractGeometryType.class.getName()).log(Level.WARNING, "Could not decode CRS which name is : " + srsName, ex);
            }
        }

        return null;
    }

    @Override
    public Precision getPrecision() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry getMbRegion() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DirectPosition getRepresentativePoint() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Complex getClosure() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSimple() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isCycle() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double distance(Geometry geometry) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getDimension(DirectPosition point) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getCoordinateDimension() {
        Integer bi = getSrsDimension();
        if(bi == null){
            return 2;
        }else{
            return bi.intValue();
        }
    }

    @Override
    public Set<? extends Complex> getMaximalComplex() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry transform(CoordinateReferenceSystem newCRS) throws TransformException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry transform(CoordinateReferenceSystem newCRS, MathTransform transform) throws TransformException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Envelope getEnvelope() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public DirectPosition getCentroid() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry getConvexHull() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry getBuffer(double distance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isMutable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Geometry toImmutable() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean contains(DirectPosition point) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean intersects(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean equals(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TransfiniteSet union(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TransfiniteSet intersection(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TransfiniteSet difference(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TransfiniteSet symmetricDifference(TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object != null && getClass().equals(object.getClass())) {
            final AbstractGeometryType that = (AbstractGeometryType) object;

            return Utilities.equals(this.axisLabels,   that.axisLabels)   &&
                   Utilities.equals(this.srsDimension, that.srsDimension) &&
                   Utilities.equals(this.srsName,      that.srsName)      &&
                   Utilities.equals(this.uomLabels,    that.uomLabels);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.srsDimension != null ? this.srsDimension.hashCode() : 0);
        hash = 37 * hash + (this.srsName != null ? this.srsName.hashCode() : 0);
        hash = 37 * hash + (this.axisLabels != null ? this.axisLabels.hashCode() : 0);
        hash = 37 * hash + (this.uomLabels != null ? this.uomLabels.hashCode() : 0);
        return hash;
    }

    @Override
    public AbstractGeometryType clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        if (gid != null) {
            sb.append("gid:").append(gid).append('\n');
        }
        if (srsDimension != null) {
            sb.append("srsDimension:").append(srsDimension).append('\n');
        }
        if (srsName != null) {
            sb.append("srsName:").append(srsName).append('\n');
        }
        if (uomLabels != null) {
            sb.append("uomLabels:").append('\n');
            for (String uomlabel : uomLabels) {
                sb.append(uomlabel).append('\n');
            }
        }
        if (axisLabels != null) {
            sb.append("axisLabels:").append('\n');
            for (String axislabel : axisLabels) {
                sb.append(axislabel).append('\n');
            }
        }
        return sb.toString();
     }
}
