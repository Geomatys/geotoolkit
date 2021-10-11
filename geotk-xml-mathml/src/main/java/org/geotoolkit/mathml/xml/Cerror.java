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
package org.geotoolkit.mathml.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * <p>Classe Java pour anonymous complex type.
 *
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.w3.org/1998/Math/MathML}csymbol"/>
 *         &lt;group ref="{http://www.w3.org/1998/Math/MathML}ContExpWithoutCSymbol" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.w3.org/1998/Math/MathML}cerror.attributes"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "csymbol",
    "contExpWithoutCSymbol"
})
@XmlRootElement(name = "cerror")
public class Cerror {

    @XmlElement(required = true)
    protected Csymbol csymbol;
    @XmlElementRefs({
        @XmlElementRef(name = "nary-stats.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "sum.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "share", namespace = "http://www.w3.org/1998/Math/MathML", type = Share.class),
        @XmlElementRef(name = "binary-logical.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "bind", namespace = "http://www.w3.org/1998/Math/MathML", type = Bind.class),
        @XmlElementRef(name = "Differential-Operator.class", namespace = "http://www.w3.org/1998/Math/MathML", type = DifferentialOperatorClass.class),
        @XmlElementRef(name = "binary-reln.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "nary-setlist-constructor.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "piecewise", namespace = "http://www.w3.org/1998/Math/MathML", type = Piecewise.class),
        @XmlElementRef(name = "unary-logical.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "partialdiff.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "cs", namespace = "http://www.w3.org/1998/Math/MathML", type = Cs.class),
        @XmlElementRef(name = "cn", namespace = "http://www.w3.org/1998/Math/MathML", type = Cn.class),
        @XmlElementRef(name = "nary-linalg.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "unary-set.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "factorial", namespace = "http://www.w3.org/1998/Math/MathML", type = Factorial.class),
        @XmlElementRef(name = "nary-constructor.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "abs", namespace = "http://www.w3.org/1998/Math/MathML", type = Abs.class),
        @XmlElementRef(name = "interval.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "limit.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "nary-arith.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "rem", namespace = "http://www.w3.org/1998/Math/MathML", type = Rem.class),
        @XmlElementRef(name = "divide", namespace = "http://www.w3.org/1998/Math/MathML", type = Divide.class),
        @XmlElementRef(name = "imaginary", namespace = "http://www.w3.org/1998/Math/MathML", type = Imaginary.class),
        @XmlElementRef(name = "minus", namespace = "http://www.w3.org/1998/Math/MathML", type = Minus.class),
        @XmlElementRef(name = "nary-logical.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "constant-arith.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "unary-elementary.class", namespace = "http://www.w3.org/1998/Math/MathML", type = UnaryElementaryClass.class),
        @XmlElementRef(name = "apply", namespace = "http://www.w3.org/1998/Math/MathML", type = Apply.class),
        @XmlElementRef(name = "conjugate", namespace = "http://www.w3.org/1998/Math/MathML", type = Conjugate.class),
        @XmlElementRef(name = "binary-set.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "product.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "ci", namespace = "http://www.w3.org/1998/Math/MathML", type = Ci.class),
        @XmlElementRef(name = "constant-set.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "cerror", namespace = "http://www.w3.org/1998/Math/MathML", type = Cerror.class),
        @XmlElementRef(name = "nary-functional.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "cbytes", namespace = "http://www.w3.org/1998/Math/MathML", type = Cbytes.class),
        @XmlElementRef(name = "binary-linalg.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "unary-linalg.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "DeprecatedContExp", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "ceiling", namespace = "http://www.w3.org/1998/Math/MathML", type = Ceiling.class),
        @XmlElementRef(name = "quantifier.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "arg", namespace = "http://www.w3.org/1998/Math/MathML", type = Arg.class),
        @XmlElementRef(name = "nary-minmax.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "real", namespace = "http://www.w3.org/1998/Math/MathML", type = Real.class),
        @XmlElementRef(name = "root", namespace = "http://www.w3.org/1998/Math/MathML", type = Root.class),
        @XmlElementRef(name = "lambda.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "unary-veccalc.class", namespace = "http://www.w3.org/1998/Math/MathML", type = UnaryVeccalcClass.class),
        @XmlElementRef(name = "power", namespace = "http://www.w3.org/1998/Math/MathML", type = Power.class),
        @XmlElementRef(name = "quotient", namespace = "http://www.w3.org/1998/Math/MathML", type = Quotient.class),
        @XmlElementRef(name = "nary-reln.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "floor", namespace = "http://www.w3.org/1998/Math/MathML", type = Floor.class),
        @XmlElementRef(name = "exp", namespace = "http://www.w3.org/1998/Math/MathML", type = Exp.class),
        @XmlElementRef(name = "nary-set.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "int.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "nary-set-reln.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class),
        @XmlElementRef(name = "unary-functional.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    })
    protected java.util.List<Object> contExpWithoutCSymbol;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "xref")
    @XmlSchemaType(name = "anySimpleType")
    protected String xref;
    @XmlAttribute(name = "class")
    @XmlSchemaType(name = "NMTOKENS")
    protected java.util.List<String> clazz;
    @XmlAttribute(name = "style")
    protected String style;
    @XmlAttribute(name = "href")
    @XmlSchemaType(name = "anyURI")
    protected String href;
    @XmlAttribute(name = "other")
    @XmlSchemaType(name = "anySimpleType")
    protected String other;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Obtient la valeur de la propriété csymbol.
     *
     * @return
     *     possible object is
     *     {@link Csymbol }
     *
     */
    public Csymbol getCsymbol() {
        return csymbol;
    }

    /**
     * Définit la valeur de la propriété csymbol.
     *
     * @param value
     *     allowed object is
     *     {@link Csymbol }
     *
     */
    public void setCsymbol(Csymbol value) {
        this.csymbol = value;
    }

    /**
     * Gets the value of the contExpWithoutCSymbol property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contExpWithoutCSymbol property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContExpWithoutCSymbol().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link Neq }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryRelnClass }{@code >}
     * {@link Share }
     * {@link DifferentialOperatorClass }
     * {@link Bind }
     * {@link JAXBElement }{@code <}{@link NaryConstructorClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryMinmaxClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryConstructorClass }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link UnaryLogicalClass }{@code >}
     * {@link Piecewise }
     * {@link JAXBElement }{@code <}{@link NaryMinmaxClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryArithClass }{@code >}
     * {@link Cn }
     * {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NarySetRelnClass }{@code >}
     * {@link JAXBElement }{@code <}{@link UnarySetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryLogicalClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NarySetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryArithClass }{@code >}
     * {@link JAXBElement }{@code <}{@link IntervalClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryConstructorClass }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryLogicalClass }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryFunctionalClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryLinalgClass }{@code >}
     * {@link Divide }
     * {@link JAXBElement }{@code <}{@link NaryLogicalClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryStatsClass }{@code >}
     * {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     * {@link Apply }
     * {@link Conjugate }
     * {@link JAXBElement }{@code <}{@link BinarySetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryLinalgClass }{@code >}
     * {@link Ci }
     * {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     * {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     * {@link Cerror }
     * {@link JAXBElement }{@code <}{@link NaryRelnClass }{@code >}
     * {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryRelnClass }{@code >}
     * {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     * {@link JAXBElement }{@code <}{@link Tendsto }{@code >}
     * {@link JAXBElement }{@code <}{@link QuantifierClass }{@code >}
     * {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link ProductClass }{@code >}
     * {@link JAXBElement }{@code <}{@link Factorof }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     * {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryLinalgClass }{@code >}
     * {@link UnaryVeccalcClass }
     * {@link JAXBElement }{@code <}{@link LambdaClass }{@code >}
     * {@link JAXBElement }{@code <}{@link UnaryLinalgClass }{@code >}
     * {@link Quotient }
     * {@link JAXBElement }{@code <}{@link NaryRelnClass }{@code >}
     * {@link Exp }
     * {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     * {@link JAXBElement }{@code <}{@link LambdaClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NarySetRelnClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryStatsClass }{@code >}
     * {@link JAXBElement }{@code <}{@link SumClass }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryLogicalClass }{@code >}
     * {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     * {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryStatsClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NarySetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link PartialdiffClass }{@code >}
     * {@link JAXBElement }{@code <}{@link SumClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryRelnClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryArithClass }{@code >}
     * {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     * {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link PartialdiffClass }{@code >}
     * {@link Cs }
     * {@link JAXBElement }{@code <}{@link NaryStatsClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryStatsClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryLinalgClass }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryLinalgClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NarySetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link IntervalClass }{@code >}
     * {@link Factorial }
     * {@link Abs }
     * {@link JAXBElement }{@code <}{@link BinaryLogicalClass }{@code >}
     * {@link JAXBElement }{@code <}{@link LimitClass }{@code >}
     * {@link JAXBElement }{@code <}{@link org.w3._1998.math.mathml.List }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryArithClass }{@code >}
     * {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     * {@link JAXBElement }{@code <}{@link LimitClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryRelnClass }{@code >}
     * {@link Rem }
     * {@link Imaginary }
     * {@link Minus }
     * {@link JAXBElement }{@code <}{@link IntClass }{@code >}
     * {@link UnaryElementaryClass }
     * {@link JAXBElement }{@code <}{@link NaryLogicalClass }{@code >}
     * {@link JAXBElement }{@code <}{@link BinarySetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link UnarySetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link ProductClass }{@code >}
     * {@link JAXBElement }{@code <}{@link UnaryLinalgClass }{@code >}
     * {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryConstructorClass }{@code >}
     * {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     * {@link JAXBElement }{@code <}{@link Set }{@code >}
     * {@link JAXBElement }{@code <}{@link Approx }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryFunctionalClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryArithClass }{@code >}
     * {@link Cbytes }
     * {@link JAXBElement }{@code <}{@link NaryStatsClass }{@code >}
     * {@link JAXBElement }{@code <}{@link BinaryLinalgClass }{@code >}
     * {@link JAXBElement }{@code <}{@link UnaryLinalgClass }{@code >}
     * {@link JAXBElement }{@code <}{@link Object }{@code >}
     * {@link JAXBElement }{@code <}{@link Reln }{@code >}
     * {@link JAXBElement }{@code <}{@link QuantifierClass }{@code >}
     * {@link Ceiling }
     * {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     * {@link Arg }
     * {@link JAXBElement }{@code <}{@link NaryLogicalClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NaryMinmaxClass }{@code >}
     * {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     * {@link JAXBElement }{@code <}{@link Declare }{@code >}
     * {@link Real }
     * {@link Root }
     * {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link UnaryLogicalClass }{@code >}
     * {@link Power }
     * {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     * {@link Floor }
     * {@link JAXBElement }{@code <}{@link Fn }{@code >}
     * {@link JAXBElement }{@code <}{@link NarySetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link NarySetRelnClass }{@code >}
     * {@link JAXBElement }{@code <}{@link IntClass }{@code >}
     * {@link JAXBElement }{@code <}{@link QuantifierClass }{@code >}
     * {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     * {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     * {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *
     *
     */
    public java.util.List<Object> getContExpWithoutCSymbol() {
        if (contExpWithoutCSymbol == null) {
            contExpWithoutCSymbol = new ArrayList<Object>();
        }
        return this.contExpWithoutCSymbol;
    }

    /**
     * Obtient la valeur de la propriété id.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * Définit la valeur de la propriété id.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Obtient la valeur de la propriété xref.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getXref() {
        return xref;
    }

    /**
     * Définit la valeur de la propriété xref.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setXref(String value) {
        this.xref = value;
    }

    /**
     * Gets the value of the clazz property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the clazz property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClazz().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public java.util.List<String> getClazz() {
        if (clazz == null) {
            clazz = new ArrayList<String>();
        }
        return this.clazz;
    }

    /**
     * Obtient la valeur de la propriété style.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getStyle() {
        return style;
    }

    /**
     * Définit la valeur de la propriété style.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setStyle(String value) {
        this.style = value;
    }

    /**
     * Obtient la valeur de la propriété href.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getHref() {
        return href;
    }

    /**
     * Définit la valeur de la propriété href.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * Obtient la valeur de la propriété other.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOther() {
        return other;
    }

    /**
     * Définit la valeur de la propriété other.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOther(String value) {
        this.other = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     *
     * <p>
     * the map is keyed by the name of the attribute and
     * the value is the string value of the attribute.
     *
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     *
     *
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
