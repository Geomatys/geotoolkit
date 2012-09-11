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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.*;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.util.Utilities;
import org.opengis.filter.expression.Expression;
import org.opengis.geometry.*;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.primitive.PrimitiveBoundary;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;


/**
 * All geometry elements are derived directly or indirectly from this abstract supertype. 
 * A geometry element may have an identifying attribute (gml:id), 
 * one or more names (elements identifier and name) and a description (elements description and descriptionReference) .
 * It may be associated with a spatial reference system (attribute group gml:SRSReferenceGroup).
 * The following rules shall be adhered to:
 * -	Every geometry type shall derive from this abstract type.
 * -	Every geometry element (i.e. an element of a geometry type) shall be directly or indirectly in the substitution group of AbstractGeometry.
 * 
 * <p>Java class for AbstractGeometryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractGeometryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractGMLType">
 *       &lt;attGroup ref="{http://www.opengis.net/gml/3.2}SRSReferenceGroup"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractGeometryType")
@XmlSeeAlso({
    GeometricComplexType.class,
    GridType.class,
    AbstractGeometricPrimitiveType.class,
    AbstractGeometricAggregateType.class
})
public abstract class AbstractGeometryType extends AbstractGMLType implements Geometry, Expression, AbstractGeometry {

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

    // Opengis attribute
    @XmlTransient
    private Precision precision;
    
    
    /**
     * empty constructor used by JAXB
     */
    AbstractGeometryType(){}

    public AbstractGeometryType(final Integer srsDimension, final String srsName, final List<String> axisLabels, final List<String> uomLabels){
        this.axisLabels   = axisLabels;
        this.srsDimension = srsDimension;
        this.srsName      = srsName;
        this.uomLabels    = uomLabels;
    }

    public AbstractGeometryType(final String srsName) {
        this.srsName      = srsName;
    }

    public AbstractGeometryType(final String id, final String srsName) {
        super(id);
        this.srsName      = srsName;
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
    public List<String> getAxisLabels() {
        if (axisLabels == null) {
            axisLabels = new ArrayList<String>();
        }
        return this.axisLabels;
    }

    /**
     * Gets the value of the uomLabels property.
     * 
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
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        if(srsName != null){
            try {
                return CRS.decode(srsName);
            } catch (FactoryException ex) {
                Logger.getLogger(org.geotoolkit.gml.xml.v311.AbstractGeometryType.class.getName()).log(Level.WARNING, "Could not decode CRS which name is : " + srsName, ex);
            }
        }

        return null;
    }

    @Override
    public Precision getPrecision() {
        return precision;
    }
    
    /**
     * @param precision the precision to set
     */
    public void setPrecision(Precision precision) {
        this.precision = precision;
    }

    @Override
    public Geometry getMbRegion() {
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public DirectPosition getRepresentativePoint() {
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public Complex getClosure() {
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public boolean isSimple() {
        // TODO calculate this for each subtype of geometry
        return false;
    }

    @Override
    public boolean isCycle() {
        // TODO calculate this for each subtype of geometry
        return false;
    }

    @Override
    public double distance(final Geometry geometry) {
        // TODO calculate this for each subtype of geometry
        return -1;
    }

    @Override
    public int getDimension(final DirectPosition point) {
       // TODO calculate this for each subtype of geometry
        return -1;
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
       // TODO calculate this for each subtype of geometry
        return new HashSet<Complex>();
    }

    @Override
    public Geometry transform(final CoordinateReferenceSystem newCRS) throws TransformException {
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public Geometry transform(final CoordinateReferenceSystem newCRS, final MathTransform transform) throws TransformException {
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public Envelope getEnvelope() {
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public DirectPosition getCentroid() {
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public Geometry getConvexHull() {
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public Geometry getBuffer(final double distance) {
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    /**
     * cant return immutable version of the object fir this module. 
     * @return 
     */
    @Override
    public Geometry toImmutable() {
        return null;
    }

    @Override
    public boolean contains(final TransfiniteSet pointSet) {
        // TODO calculate this for each subtype of geometry
        return false;
    }

    @Override
    public boolean contains(final DirectPosition point) {
        // TODO calculate this for each subtype of geometry
        return false;
    }

    @Override
    public boolean intersects(final TransfiniteSet pointSet) {
        // TODO calculate this for each subtype of geometry
        return false;
    }

    @Override
    public boolean equals(final TransfiniteSet pointSet) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public TransfiniteSet union(final TransfiniteSet pointSet) {
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public TransfiniteSet intersection(final TransfiniteSet pointSet) {
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public TransfiniteSet difference(final TransfiniteSet pointSet) {
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public TransfiniteSet symmetricDifference(final TransfiniteSet pointSet) {
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
    
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object != null && getClass().equals(object.getClass())) {
            final AbstractGeometryType that = (AbstractGeometryType) object;

            return Objects.equals(this.axisLabels,   that.axisLabels)   &&
                   Objects.equals(this.srsDimension, that.srsDimension) &&
                   Objects.equals(this.srsName,      that.srsName)      &&
                   Objects.equals(this.uomLabels,    that.uomLabels);
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
}
