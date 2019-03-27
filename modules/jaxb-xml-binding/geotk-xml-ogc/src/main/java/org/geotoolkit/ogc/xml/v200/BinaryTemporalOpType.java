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
import java.util.Objects;
import java.util.logging.Level;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v321.AbstractTimeObjectType;
import org.opengis.filter.FilterVisitor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.temporal.BinaryTemporalOperator;


/**
 * <p>Java class for BinaryTemporalOpType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BinaryTemporalOpType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/fes/2.0}TemporalOpsType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/fes/2.0}ValueReference"/>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/fes/2.0}expression"/>
 *           &lt;any namespace='##other'/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BinaryTemporalOpType", propOrder = {
    "valueReference",
    "expression",
    "any"
})
public class BinaryTemporalOpType extends TemporalOpsType  implements BinaryTemporalOperator {

    @XmlElement(name = "ValueReference", required = true)
    private String valueReference;
    @XmlElementRef(name = "expression", namespace = "http://www.opengis.net/fes/2.0", type = JAXBElement.class)
    private JAXBElement<?> expression;

    @XmlMixed
    @XmlAnyElement(lax = true)
    private List<Object> any;

     /**
     * An empty constructor used by JAXB
     */
    public BinaryTemporalOpType() {

    }

    /**
     * Build a new Binary temporal operator
     */
    public BinaryTemporalOpType(final String propertyName, final Object temporal) {
        this.valueReference   = propertyName;
        if (temporal != null) {
            this.any = Arrays.asList(temporal);
        }

    }

    public BinaryTemporalOpType(final BinaryTemporalOpType that) {
        if (that != null) {
            final ObjectFactory factory = new ObjectFactory();
            if (that.expression != null) {
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
                    throw new IllegalArgumentException("Unexpected type for expression in BinaryTemporalOpType:" + expression.getClass().getName());
                }
            }

            if (that.any != null) {
                this.any = new ArrayList<>();
                for (Object obj : that.any) {
                    if (obj instanceof AbstractTimeObjectType) {
                        this.any.add(((AbstractTimeObjectType)obj).getClone());
                    } else if (obj != null){
                        this.any.add(obj);
                        LOGGER.log(Level.INFO, "Unable to clone:{0}", obj.getClass().getName());
                    }
                }
            }

            this.valueReference = that.valueReference;
        }
    }

    /**
     * Gets the value of the valueReference property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValueReference() {
        return valueReference;
    }

    /**
     * Sets the value of the valueReference property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValueReference(String value) {
        this.valueReference = value;
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

    /**
     * Sets the value of the any property.
     *
     * @param value
     *     allowed object is
     *     {@link Object }
     *
     */
    public void setAny(final Object value) {
        this.any = Arrays.asList(value);
    }

    public void cleanAny() {
        if (this.any != null) {
            final List<Object> toRemove = new ArrayList<>();
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
        if (valueReference != null) {
            return new InternalPropertyName(valueReference);
        }
        return null;
    }

    @Override
    public Expression getExpression2() {
        if (expression != null && expression.getValue() instanceof Expression) {
            return (Expression) expression.getValue();
        }
        this.cleanAny();
        if (any != null && !any.isEmpty()) {
            Object obj = any.get(0);
            if (obj instanceof JAXBElement) {
                obj = ((JAXBElement)obj).getValue();
            }
            if (obj instanceof Expression) {
                return (Expression)obj;
            }
        }
        return null;
    }

    @Override
    public TemporalOpsType getClone() {
        throw new UnsupportedOperationException("Must be overriden in sub-class.");
    }

    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof BinaryTemporalOpType) {
            final BinaryTemporalOpType that = (BinaryTemporalOpType) object;


            boolean env = false;
            if (this.expression != null && that.expression != null) {
                env = Objects.equals(this.expression.getValue(), that.expression.getValue());
            } else if (this.expression == null && that.expression == null) {
                env = true;
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
                            anyEq = Objects.equals(((JAXBElement)thisany).getValue(), ((JAXBElement)thatany).getValue());
                        } else {
                            anyEq = Objects.equals(thisany, thatany);
                        }
                    }
                }
            }

            return  Objects.equals(this.valueReference, that.valueReference) && env && anyEq;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.valueReference != null ? this.valueReference.hashCode() : 0);
        hash = 67 * hash + (this.expression != null ? this.expression.hashCode() : 0);
        hash = 67 * hash + (this.any != null ? this.any.hashCode() : 0);
        return hash;
    }


    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[").append(this.getClass().getSimpleName()).append("]");
        if (valueReference != null) {
            s.append("valueReference: ").append(valueReference).append('\n');
        }
        if (expression != null && expression.getValue() != null) {
            s.append("expression: ").append(expression.getValue().toString()).append('\n');
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
}
