/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2011, Geomatys
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


package org.geotoolkit.ogc.xml.v200;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.DirectPosition;
import org.geotoolkit.gml.xml.Envelope;
import org.geotoolkit.gml.xml.v321.DirectPositionType;
import org.geotoolkit.gml.xml.v321.EnvelopeType;
import org.geotoolkit.ogc.xml.v110.PropertyNameType;
import org.geotoolkit.util.Utilities;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.spatial.BBOX;


/**
 * <p>Java class for BBOXType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BBOXType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/fes/2.0}SpatialOpsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}expression" minOccurs="0"/>
 *         &lt;any namespace='##other'/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BBOXType", propOrder = {
    "expression",
    "any"
})
public class BBOXType extends SpatialOpsType implements BBOX {

    private static final String DEFAULT_SRS = "EPSG:4326";
    
    @XmlElementRef(name = "expression", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private JAXBElement<?> expression;

    @XmlMixed
    @XmlAnyElement(lax = true)
    private List<Object> any;

    /**
     * An empty constructor used by JAXB
     */
    public BBOXType() {

    }

    /**
     * build a new BBox with an envelope.
     */
    public BBOXType(final String propertyName, final double minx, final double miny, final double maxx, final double maxy, final String srs) {
        if (propertyName != null) {
            final ObjectFactory factory = new ObjectFactory();
            this.expression = factory.createValueReference(propertyName);
        }
        DirectPositionType lower = new DirectPositionType(minx, miny);
        DirectPositionType upper = new DirectPositionType(maxx, maxy);
        this.any = new ArrayList<Object>();
        this.any.add(new EnvelopeType(lower, upper, srs));

    }

    /**
     * build a new BBox with an envelope.
     */
    public BBOXType(final String propertyName, final Object any) {
        if (propertyName != null) {
            final ObjectFactory factory = new ObjectFactory();
            this.expression = factory.createValueReference(propertyName);
        }
        this.any = Arrays.asList(any);
    }

    public BBOXType(final BBOXType that) {
        if (that != null) {
            if (that.expression != null) {
                final ObjectFactory factory = new ObjectFactory();
                final Object exp = that.expression.getValue();
                if (exp instanceof String) {
                    this.expression = factory.createValueReference((String)exp);
                } else if (exp instanceof LiteralType) {
                    final LiteralType lit = new LiteralType((LiteralType)exp);
                    this.expression = factory.createLiteral(lit);
                } else if (exp instanceof FunctionType) {
                    final FunctionType func = new FunctionType((FunctionType)exp);
                    this.expression = factory.createFunction(func);
                } else {
                    throw new IllegalArgumentException("Unexpected type for expression in PropertyIsBetweenType:" + expression.getClass().getName());
                }
            }
            
            if (that.any != null) {
                this.any = new ArrayList<Object>();
                for (Object obj : that.any) {
                    if (obj instanceof EnvelopeType) {
                        this.any.add(new EnvelopeType((EnvelopeType)obj));
                    } else {
                        this.any.add(obj);
                        LOGGER.log(Level.INFO, "Unable to clone:{0}", obj.getClass().getName());
                    }
                }
            }
        }
    }
    
    /**
     * Gets the value of the expression property.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link LiteralType }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     {@link JAXBElement }{@code <}{@link FunctionType }{@code >}
     *
     */
    public JAXBElement<?> getExpression() {
        return expression;
    }

    @Override
    public String getPropertyName() {
        if (expression != null && expression.getValue() instanceof String) {
            return (String)expression.getValue();
        }
        return null;
    }

    /**
     * Sets the value of the expression property.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link LiteralType }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link String }{@code >}
     *     {@link JAXBElement }{@code <}{@link FunctionType }{@code >}
     *
     */
    public void setExpression(JAXBElement<?> value) {
        this.expression = ((JAXBElement<?> ) value);
    }

    /**
     * Gets the value of the any property.
     *
     * @return
     *     possible object is
     *     {@link Object }
     *
     */
    public Object getAny() {
        cleanAny();
        if (any != null && !any.isEmpty()) {
            return any.get(0);
        }
        return null;
    }

    public void cleanAny() {
        if (this.any != null) {
            final List<Object> toRemove = new ArrayList<Object>();
            int i = 0;
            for (Object element : any) {
                if (element instanceof String) {
                    String s = (String) element;
                    s = s.replace("\n", "");
                    s = s.replace("\t", "");
                    s = s.trim();
                    if (s.isEmpty()) {
                        toRemove.add(element);
                    } else {
                        any.set(i, s);
                    }
                }
                i++;
            }
            this.any.removeAll(toRemove);
        }
    }

    /**
     * Sets the value of the any property.
     *
     * @param value
     *     allowed object is
     *     {@link Object }
     *
     */
    public void setAny(Object value) {
        this.any = Arrays.asList(value);
    }

    @Override
    public boolean evaluate(final Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object accept(final FilterVisitor visitor, final Object extraData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Expression getExpression1() {
        return new PropertyNameType(getPropertyName());
    }

    @Override
    public Expression getExpression2() {
        if (this.any != null && !this.any.isEmpty()) {
            return new LiteralType(this.any.get(0));
        }
        return null;
    }
    
    @Override
    public SpatialOpsType getClone() {
        return new BBOXType(this);
    }
    
    @Override
    public String getSRS() {
        if (any != null && any.get(0) instanceof Envelope) {
            final Envelope env = (Envelope) any.get(0);
            return (env.getSrsName() != null) ? env.getSrsName() : DEFAULT_SRS;
        }
        return null;
    }

    @Override
    public double getMinX() {
        DirectPosition pos = null;
        if (any != null && any.get(0) instanceof Envelope) {
            final Envelope env = (Envelope) any.get(0);
            pos = env.getLowerCorner();
        }
        if (pos != null && pos.getValue() != null && pos.getValue().size() > 1) {
            return pos.getValue().get(0);
        }
        return -1;
    }

    @Override
    public double getMinY() {
       DirectPosition pos = null;
        if (any != null && any.get(0) instanceof Envelope) {
            final Envelope env = (Envelope) any.get(0);
            pos = env.getLowerCorner();
        }
        if (pos != null && pos.getValue() != null && pos.getValue().size() > 1) {
            return pos.getValue().get(1);
        }
        return -1;
    }

    @Override
    public double getMaxX() {
        DirectPosition pos = null;
        if (any != null && any.get(0) instanceof Envelope) {
            final Envelope env = (Envelope) any.get(0);
            pos = env.getUpperCorner();
        }
        if (pos != null && pos.getValue() != null && pos.getValue().size() > 1) {
            return pos.getValue().get(0);
        }
        return -1;
    }

    @Override
    public double getMaxY() {
        DirectPosition pos = null;
        if (any != null && any.get(0) instanceof Envelope) {
            final Envelope env = (Envelope) any.get(0);
            pos = env.getUpperCorner();
        }
        if (pos != null && pos.getValue() != null && pos.getValue().size() > 1) {
            return pos.getValue().get(1);
        }
        return -1;
    }
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder("[BBOXType]");
        if (expression != null) {
            s.append("PropertyName=").append(expression.getValue()).append('\n');
        }
        cleanAny();
        if (any != null) {
            for (Object obj : any) {
                if (obj instanceof JAXBElement) {
                    s.append("any [JAXBElement]= ").append(((JAXBElement)obj).getValue()).append('\n');
                } else {
                    s.append("any= ").append(obj).append('\n');
                }
            }
        }
        return s.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BBOXType) {
            final BBOXType that = (BBOXType) obj;
            boolean prop = false;
            if (this.expression == null && that.expression == null) {
                prop = true;
            } else if (this.expression != null && that.expression != null) {
                prop = Utilities.equals(this.expression.getValue(), that.expression.getValue());
            }
            boolean anyEq = false;
            if (this.any == null && that.any == null) {
                anyEq = true;
            } else if (this.any != null && that.any != null) {
                this.cleanAny();
                that.cleanAny();
                if (this.any.size() == that.any.size()) {
                    for (int i = 0; i < this.any.size(); i++) {
                        final Object thisany = this.any.get(i);
                        final Object thatany = that.any.get(i);
                        if (thisany instanceof JAXBElement && thatany instanceof JAXBElement) {
                            anyEq = Utilities.equals(((JAXBElement)thisany).getValue(), ((JAXBElement)thatany).getValue());
                        } else {
                            anyEq = Utilities.equals(thisany, thatany);
                        }
                    }
                }
            }
            return prop && anyEq;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.expression != null ? this.expression.hashCode() : 0);
        hash = 97 * hash + (this.any != null ? this.any.hashCode() : 0);
        return hash;
    }
}
