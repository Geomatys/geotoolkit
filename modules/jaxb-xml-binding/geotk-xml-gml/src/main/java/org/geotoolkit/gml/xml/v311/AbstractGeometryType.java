/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gml.xml.v311;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.opengis.filter.expression.Expression;


/**
 * All geometry elements are derived directly or indirectly from this abstract supertype. A geometry element may have an identifying attribute (gml:id), one or more names (elements identifier and name) and a description (elements description and descriptionReference) . It may be associated with a spatial reference system (attribute group gml:SRSReferenceGroup).
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
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGMLType">
 *       &lt;attGroup ref="{http://www.opengis.net/gml}SRSReferenceGroup"/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractGeometryType")
@XmlSeeAlso({
    GeometricComplexType.class,
    GridType.class,
    AbstractRingType.class,
    AbstractGeometricPrimitiveType.class,
    AbstractGeometricAggregateType.class
})
public abstract class AbstractGeometryType extends AbstractGMLEntry implements Expression {

    @XmlAttribute
    private Integer srsDimension;
    @XmlAttribute
    private String srsName;
    @XmlAttribute
    private List<String> axisLabels;
    @XmlAttribute
    private List<String> uomLabels;

    /**
     * empty constructor used by JAXB
     */
    AbstractGeometryType(){
    }
    
    public AbstractGeometryType(Integer srsDimension, String srsName, List<String> axisLabels, List<String> uomLabels){
        this.axisLabels   = axisLabels;
        this.srsDimension = srsDimension;
        this.srsName      = srsName;
        this.uomLabels    = uomLabels;
    }
    /**
     * Gets the value of the srsDimension property.
     */
    public Integer getSrsDimension() {
        return srsDimension;
    }

    /**
     * Gets the value of the srsName property.
     */
    public String getSrsName() {
        return srsName;
    }

    public void setSrsName(String srsName) {
        this.srsName = srsName;
    }
    /**
     * Gets the value of the axisLabels property (unmodifiable).
     */
    public List<String> getAxisLabels() {
        if (axisLabels == null){
            axisLabels = new ArrayList<String>();
        }
        return Collections.unmodifiableList(axisLabels);
    }

    /**
     * Gets the value of the uomLabels property (unmodifiable).
     * 
     */
    public List<String> getUomLabels() {
        if (uomLabels == null){
            uomLabels = new ArrayList<String>();
        }
        return Collections.unmodifiableList(uomLabels);
    }
    
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
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

}
