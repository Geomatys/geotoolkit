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
import javax.xml.bind.annotation.XmlID;
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
 *         &lt;group ref="{http://www.w3.org/1998/Math/MathML}MathExpression"/>
 *         &lt;sequence minOccurs="0">
 *           &lt;element ref="{http://www.w3.org/1998/Math/MathML}mprescripts"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.w3.org/1998/Math/MathML}mmultiscripts.attributes"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "apply",
    "bind",
    "ci",
    "cn",
    "csymbol",
    "cbytes",
    "cerror",
    "cs",
    "share",
    "piecewise",
    "deprecatedContExp",
    "intervalClass",
    "unaryFunctionalClass",
    "lambdaClass",
    "naryFunctionalClass",
    "quotient",
    "divide",
    "minus",
    "power",
    "rem",
    "root",
    "factorial",
    "abs",
    "conjugate",
    "arg",
    "real",
    "imaginary",
    "floor",
    "ceiling",
    "exp",
    "naryMinmaxClass",
    "naryArithClass",
    "naryLogicalClass",
    "unaryLogicalClass",
    "binaryLogicalClass",
    "quantifierClass",
    "naryRelnClass",
    "binaryRelnClass",
    "intClass",
    "differentialOperatorClass",
    "partialdiffClass",
    "unaryVeccalcClass",
    "narySetlistConstructorClass",
    "narySetClass",
    "binarySetClass",
    "narySetRelnClass",
    "unarySetClass",
    "sumClass",
    "productClass",
    "limitClass",
    "unaryElementaryClass",
    "naryStatsClass",
    "naryConstructorClass",
    "unaryLinalgClass",
    "naryLinalgClass",
    "binaryLinalgClass",
    "constantSetClass",
    "constantArithClass",
    "presentationExpression",
    "semantics",
    "mprescripts"
})
public class Mmultiscripts {

    protected Apply apply;
    protected Bind bind;
    protected Ci ci;
    protected Cn cn;
    protected Csymbol csymbol;
    protected Cbytes cbytes;
    protected Cerror cerror;
    protected Cs cs;
    protected Share share;
    protected Piecewise piecewise;
    @XmlElementRef(name = "DeprecatedContExp", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<?> deprecatedContExp;
    @XmlElementRef(name = "interval.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<IntervalClass> intervalClass;
    @XmlElementRef(name = "unary-functional.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<UnaryFunctionalClass> unaryFunctionalClass;
    @XmlElementRef(name = "lambda.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<LambdaClass> lambdaClass;
    @XmlElementRef(name = "nary-functional.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<NaryFunctionalClass> naryFunctionalClass;
    protected Quotient quotient;
    protected Divide divide;
    protected Minus minus;
    protected Power power;
    protected Rem rem;
    protected Root root;
    protected Factorial factorial;
    protected Abs abs;
    protected Conjugate conjugate;
    protected Arg arg;
    protected Real real;
    protected Imaginary imaginary;
    protected Floor floor;
    protected Ceiling ceiling;
    protected Exp exp;
    @XmlElementRef(name = "nary-minmax.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<NaryMinmaxClass> naryMinmaxClass;
    @XmlElementRef(name = "nary-arith.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<NaryArithClass> naryArithClass;
    @XmlElementRef(name = "nary-logical.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<NaryLogicalClass> naryLogicalClass;
    @XmlElementRef(name = "unary-logical.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<UnaryLogicalClass> unaryLogicalClass;
    @XmlElementRef(name = "binary-logical.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<BinaryLogicalClass> binaryLogicalClass;
    @XmlElementRef(name = "quantifier.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<QuantifierClass> quantifierClass;
    @XmlElementRef(name = "nary-reln.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<NaryRelnClass> naryRelnClass;
    @XmlElementRef(name = "binary-reln.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<?> binaryRelnClass;
    @XmlElementRef(name = "int.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<IntClass> intClass;
    @XmlElement(name = "Differential-Operator.class")
    protected DifferentialOperatorClass differentialOperatorClass;
    @XmlElementRef(name = "partialdiff.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<PartialdiffClass> partialdiffClass;
    @XmlElement(name = "unary-veccalc.class")
    protected UnaryVeccalcClass unaryVeccalcClass;
    @XmlElementRef(name = "nary-setlist-constructor.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<?> narySetlistConstructorClass;
    @XmlElementRef(name = "nary-set.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<NarySetClass> narySetClass;
    @XmlElementRef(name = "binary-set.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<BinarySetClass> binarySetClass;
    @XmlElementRef(name = "nary-set-reln.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<NarySetRelnClass> narySetRelnClass;
    @XmlElementRef(name = "unary-set.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<UnarySetClass> unarySetClass;
    @XmlElementRef(name = "sum.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<SumClass> sumClass;
    @XmlElementRef(name = "product.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<ProductClass> productClass;
    @XmlElementRef(name = "limit.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<LimitClass> limitClass;
    @XmlElement(name = "unary-elementary.class")
    protected UnaryElementaryClass unaryElementaryClass;
    @XmlElementRef(name = "nary-stats.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<NaryStatsClass> naryStatsClass;
    @XmlElementRef(name = "nary-constructor.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<NaryConstructorClass> naryConstructorClass;
    @XmlElementRef(name = "unary-linalg.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<UnaryLinalgClass> unaryLinalgClass;
    @XmlElementRef(name = "nary-linalg.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<NaryLinalgClass> naryLinalgClass;
    @XmlElementRef(name = "binary-linalg.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<BinaryLinalgClass> binaryLinalgClass;
    @XmlElementRef(name = "constant-set.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<ConstantSetClass> constantSetClass;
    @XmlElementRef(name = "constant-arith.class", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<ConstantArithClass> constantArithClass;
    @XmlElementRef(name = "PresentationExpression", namespace = "http://www.w3.org/1998/Math/MathML", type = JAXBElement.class)
    protected JAXBElement<?> presentationExpression;
    protected org.geotoolkit.mathml.xml.Mscarries.Semantics semantics;
    protected Mprescripts mprescripts;
    @XmlAttribute(name = "subscriptshift")
    protected String subscriptshift;
    @XmlAttribute(name = "superscriptshift")
    protected String superscriptshift;
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
    @XmlAttribute(name = "mathcolor")
    protected String mathcolor;
    @XmlAttribute(name = "mathbackground")
    protected String mathbackground;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Obtient la valeur de la propriété apply.
     *
     * @return
     *     possible object is
     *     {@link Apply }
     *
     */
    public Apply getApply() {
        return apply;
    }

    /**
     * Définit la valeur de la propriété apply.
     *
     * @param value
     *     allowed object is
     *     {@link Apply }
     *
     */
    public void setApply(Apply value) {
        this.apply = value;
    }

    /**
     * Obtient la valeur de la propriété bind.
     *
     * @return
     *     possible object is
     *     {@link Bind }
     *
     */
    public Bind getBind() {
        return bind;
    }

    /**
     * Définit la valeur de la propriété bind.
     *
     * @param value
     *     allowed object is
     *     {@link Bind }
     *
     */
    public void setBind(Bind value) {
        this.bind = value;
    }

    /**
     * Obtient la valeur de la propriété ci.
     *
     * @return
     *     possible object is
     *     {@link Ci }
     *
     */
    public Ci getCi() {
        return ci;
    }

    /**
     * Définit la valeur de la propriété ci.
     *
     * @param value
     *     allowed object is
     *     {@link Ci }
     *
     */
    public void setCi(Ci value) {
        this.ci = value;
    }

    /**
     * Obtient la valeur de la propriété cn.
     *
     * @return
     *     possible object is
     *     {@link Cn }
     *
     */
    public Cn getCn() {
        return cn;
    }

    /**
     * Définit la valeur de la propriété cn.
     *
     * @param value
     *     allowed object is
     *     {@link Cn }
     *
     */
    public void setCn(Cn value) {
        this.cn = value;
    }

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
     * Obtient la valeur de la propriété cbytes.
     *
     * @return
     *     possible object is
     *     {@link Cbytes }
     *
     */
    public Cbytes getCbytes() {
        return cbytes;
    }

    /**
     * Définit la valeur de la propriété cbytes.
     *
     * @param value
     *     allowed object is
     *     {@link Cbytes }
     *
     */
    public void setCbytes(Cbytes value) {
        this.cbytes = value;
    }

    /**
     * Obtient la valeur de la propriété cerror.
     *
     * @return
     *     possible object is
     *     {@link Cerror }
     *
     */
    public Cerror getCerror() {
        return cerror;
    }

    /**
     * Définit la valeur de la propriété cerror.
     *
     * @param value
     *     allowed object is
     *     {@link Cerror }
     *
     */
    public void setCerror(Cerror value) {
        this.cerror = value;
    }

    /**
     * Obtient la valeur de la propriété cs.
     *
     * @return
     *     possible object is
     *     {@link Cs }
     *
     */
    public Cs getCs() {
        return cs;
    }

    /**
     * Définit la valeur de la propriété cs.
     *
     * @param value
     *     allowed object is
     *     {@link Cs }
     *
     */
    public void setCs(Cs value) {
        this.cs = value;
    }

    /**
     * Obtient la valeur de la propriété share.
     *
     * @return
     *     possible object is
     *     {@link Share }
     *
     */
    public Share getShare() {
        return share;
    }

    /**
     * Définit la valeur de la propriété share.
     *
     * @param value
     *     allowed object is
     *     {@link Share }
     *
     */
    public void setShare(Share value) {
        this.share = value;
    }

    /**
     * Obtient la valeur de la propriété piecewise.
     *
     * @return
     *     possible object is
     *     {@link Piecewise }
     *
     */
    public Piecewise getPiecewise() {
        return piecewise;
    }

    /**
     * Définit la valeur de la propriété piecewise.
     *
     * @param value
     *     allowed object is
     *     {@link Piecewise }
     *
     */
    public void setPiecewise(Piecewise value) {
        this.piecewise = value;
    }

    /**
     * Obtient la valeur de la propriété deprecatedContExp.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Declare }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link Reln }{@code >}
     *     {@link JAXBElement }{@code <}{@link Fn }{@code >}
     *
     */
    public JAXBElement<?> getDeprecatedContExp() {
        return deprecatedContExp;
    }

    /**
     * Définit la valeur de la propriété deprecatedContExp.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Declare }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link Reln }{@code >}
     *     {@link JAXBElement }{@code <}{@link Fn }{@code >}
     *
     */
    public void setDeprecatedContExp(JAXBElement<?> value) {
        this.deprecatedContExp = value;
    }

    /**
     * Obtient la valeur de la propriété intervalClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link IntervalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link IntervalClass }{@code >}
     *
     */
    public JAXBElement<IntervalClass> getIntervalClass() {
        return intervalClass;
    }

    /**
     * Définit la valeur de la propriété intervalClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link IntervalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link IntervalClass }{@code >}
     *
     */
    public void setIntervalClass(JAXBElement<IntervalClass> value) {
        this.intervalClass = value;
    }

    /**
     * Obtient la valeur de la propriété unaryFunctionalClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *
     */
    public JAXBElement<UnaryFunctionalClass> getUnaryFunctionalClass() {
        return unaryFunctionalClass;
    }

    /**
     * Définit la valeur de la propriété unaryFunctionalClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryFunctionalClass }{@code >}
     *
     */
    public void setUnaryFunctionalClass(JAXBElement<UnaryFunctionalClass> value) {
        this.unaryFunctionalClass = value;
    }

    /**
     * Obtient la valeur de la propriété lambdaClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link LambdaClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link LambdaClass }{@code >}
     *
     */
    public JAXBElement<LambdaClass> getLambdaClass() {
        return lambdaClass;
    }

    /**
     * Définit la valeur de la propriété lambdaClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link LambdaClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link LambdaClass }{@code >}
     *
     */
    public void setLambdaClass(JAXBElement<LambdaClass> value) {
        this.lambdaClass = value;
    }

    /**
     * Obtient la valeur de la propriété naryFunctionalClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link NaryFunctionalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryFunctionalClass }{@code >}
     *
     */
    public JAXBElement<NaryFunctionalClass> getNaryFunctionalClass() {
        return naryFunctionalClass;
    }

    /**
     * Définit la valeur de la propriété naryFunctionalClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link NaryFunctionalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryFunctionalClass }{@code >}
     *
     */
    public void setNaryFunctionalClass(JAXBElement<NaryFunctionalClass> value) {
        this.naryFunctionalClass = value;
    }

    /**
     * Obtient la valeur de la propriété quotient.
     *
     * @return
     *     possible object is
     *     {@link Quotient }
     *
     */
    public Quotient getQuotient() {
        return quotient;
    }

    /**
     * Définit la valeur de la propriété quotient.
     *
     * @param value
     *     allowed object is
     *     {@link Quotient }
     *
     */
    public void setQuotient(Quotient value) {
        this.quotient = value;
    }

    /**
     * Obtient la valeur de la propriété divide.
     *
     * @return
     *     possible object is
     *     {@link Divide }
     *
     */
    public Divide getDivide() {
        return divide;
    }

    /**
     * Définit la valeur de la propriété divide.
     *
     * @param value
     *     allowed object is
     *     {@link Divide }
     *
     */
    public void setDivide(Divide value) {
        this.divide = value;
    }

    /**
     * Obtient la valeur de la propriété minus.
     *
     * @return
     *     possible object is
     *     {@link Minus }
     *
     */
    public Minus getMinus() {
        return minus;
    }

    /**
     * Définit la valeur de la propriété minus.
     *
     * @param value
     *     allowed object is
     *     {@link Minus }
     *
     */
    public void setMinus(Minus value) {
        this.minus = value;
    }

    /**
     * Obtient la valeur de la propriété power.
     *
     * @return
     *     possible object is
     *     {@link Power }
     *
     */
    public Power getPower() {
        return power;
    }

    /**
     * Définit la valeur de la propriété power.
     *
     * @param value
     *     allowed object is
     *     {@link Power }
     *
     */
    public void setPower(Power value) {
        this.power = value;
    }

    /**
     * Obtient la valeur de la propriété rem.
     *
     * @return
     *     possible object is
     *     {@link Rem }
     *
     */
    public Rem getRem() {
        return rem;
    }

    /**
     * Définit la valeur de la propriété rem.
     *
     * @param value
     *     allowed object is
     *     {@link Rem }
     *
     */
    public void setRem(Rem value) {
        this.rem = value;
    }

    /**
     * Obtient la valeur de la propriété root.
     *
     * @return
     *     possible object is
     *     {@link Root }
     *
     */
    public Root getRoot() {
        return root;
    }

    /**
     * Définit la valeur de la propriété root.
     *
     * @param value
     *     allowed object is
     *     {@link Root }
     *
     */
    public void setRoot(Root value) {
        this.root = value;
    }

    /**
     * Obtient la valeur de la propriété factorial.
     *
     * @return
     *     possible object is
     *     {@link Factorial }
     *
     */
    public Factorial getFactorial() {
        return factorial;
    }

    /**
     * Définit la valeur de la propriété factorial.
     *
     * @param value
     *     allowed object is
     *     {@link Factorial }
     *
     */
    public void setFactorial(Factorial value) {
        this.factorial = value;
    }

    /**
     * Obtient la valeur de la propriété abs.
     *
     * @return
     *     possible object is
     *     {@link Abs }
     *
     */
    public Abs getAbs() {
        return abs;
    }

    /**
     * Définit la valeur de la propriété abs.
     *
     * @param value
     *     allowed object is
     *     {@link Abs }
     *
     */
    public void setAbs(Abs value) {
        this.abs = value;
    }

    /**
     * Obtient la valeur de la propriété conjugate.
     *
     * @return
     *     possible object is
     *     {@link Conjugate }
     *
     */
    public Conjugate getConjugate() {
        return conjugate;
    }

    /**
     * Définit la valeur de la propriété conjugate.
     *
     * @param value
     *     allowed object is
     *     {@link Conjugate }
     *
     */
    public void setConjugate(Conjugate value) {
        this.conjugate = value;
    }

    /**
     * Obtient la valeur de la propriété arg.
     *
     * @return
     *     possible object is
     *     {@link Arg }
     *
     */
    public Arg getArg() {
        return arg;
    }

    /**
     * Définit la valeur de la propriété arg.
     *
     * @param value
     *     allowed object is
     *     {@link Arg }
     *
     */
    public void setArg(Arg value) {
        this.arg = value;
    }

    /**
     * Obtient la valeur de la propriété real.
     *
     * @return
     *     possible object is
     *     {@link Real }
     *
     */
    public Real getReal() {
        return real;
    }

    /**
     * Définit la valeur de la propriété real.
     *
     * @param value
     *     allowed object is
     *     {@link Real }
     *
     */
    public void setReal(Real value) {
        this.real = value;
    }

    /**
     * Obtient la valeur de la propriété imaginary.
     *
     * @return
     *     possible object is
     *     {@link Imaginary }
     *
     */
    public Imaginary getImaginary() {
        return imaginary;
    }

    /**
     * Définit la valeur de la propriété imaginary.
     *
     * @param value
     *     allowed object is
     *     {@link Imaginary }
     *
     */
    public void setImaginary(Imaginary value) {
        this.imaginary = value;
    }

    /**
     * Obtient la valeur de la propriété floor.
     *
     * @return
     *     possible object is
     *     {@link Floor }
     *
     */
    public Floor getFloor() {
        return floor;
    }

    /**
     * Définit la valeur de la propriété floor.
     *
     * @param value
     *     allowed object is
     *     {@link Floor }
     *
     */
    public void setFloor(Floor value) {
        this.floor = value;
    }

    /**
     * Obtient la valeur de la propriété ceiling.
     *
     * @return
     *     possible object is
     *     {@link Ceiling }
     *
     */
    public Ceiling getCeiling() {
        return ceiling;
    }

    /**
     * Définit la valeur de la propriété ceiling.
     *
     * @param value
     *     allowed object is
     *     {@link Ceiling }
     *
     */
    public void setCeiling(Ceiling value) {
        this.ceiling = value;
    }

    /**
     * Obtient la valeur de la propriété exp.
     *
     * @return
     *     possible object is
     *     {@link Exp }
     *
     */
    public Exp getExp() {
        return exp;
    }

    /**
     * Définit la valeur de la propriété exp.
     *
     * @param value
     *     allowed object is
     *     {@link Exp }
     *
     */
    public void setExp(Exp value) {
        this.exp = value;
    }

    /**
     * Obtient la valeur de la propriété naryMinmaxClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link NaryMinmaxClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryMinmaxClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryMinmaxClass }{@code >}
     *
     */
    public JAXBElement<NaryMinmaxClass> getNaryMinmaxClass() {
        return naryMinmaxClass;
    }

    /**
     * Définit la valeur de la propriété naryMinmaxClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link NaryMinmaxClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryMinmaxClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryMinmaxClass }{@code >}
     *
     */
    public void setNaryMinmaxClass(JAXBElement<NaryMinmaxClass> value) {
        this.naryMinmaxClass = value;
    }

    /**
     * Obtient la valeur de la propriété naryArithClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link NaryArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryArithClass }{@code >}
     *
     */
    public JAXBElement<NaryArithClass> getNaryArithClass() {
        return naryArithClass;
    }

    /**
     * Définit la valeur de la propriété naryArithClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link NaryArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryArithClass }{@code >}
     *
     */
    public void setNaryArithClass(JAXBElement<NaryArithClass> value) {
        this.naryArithClass = value;
    }

    /**
     * Obtient la valeur de la propriété naryLogicalClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link NaryLogicalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryLogicalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryLogicalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryLogicalClass }{@code >}
     *
     */
    public JAXBElement<NaryLogicalClass> getNaryLogicalClass() {
        return naryLogicalClass;
    }

    /**
     * Définit la valeur de la propriété naryLogicalClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link NaryLogicalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryLogicalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryLogicalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryLogicalClass }{@code >}
     *
     */
    public void setNaryLogicalClass(JAXBElement<NaryLogicalClass> value) {
        this.naryLogicalClass = value;
    }

    /**
     * Obtient la valeur de la propriété unaryLogicalClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link UnaryLogicalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryLogicalClass }{@code >}
     *
     */
    public JAXBElement<UnaryLogicalClass> getUnaryLogicalClass() {
        return unaryLogicalClass;
    }

    /**
     * Définit la valeur de la propriété unaryLogicalClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link UnaryLogicalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryLogicalClass }{@code >}
     *
     */
    public void setUnaryLogicalClass(JAXBElement<UnaryLogicalClass> value) {
        this.unaryLogicalClass = value;
    }

    /**
     * Obtient la valeur de la propriété binaryLogicalClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BinaryLogicalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryLogicalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryLogicalClass }{@code >}
     *
     */
    public JAXBElement<BinaryLogicalClass> getBinaryLogicalClass() {
        return binaryLogicalClass;
    }

    /**
     * Définit la valeur de la propriété binaryLogicalClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BinaryLogicalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryLogicalClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryLogicalClass }{@code >}
     *
     */
    public void setBinaryLogicalClass(JAXBElement<BinaryLogicalClass> value) {
        this.binaryLogicalClass = value;
    }

    /**
     * Obtient la valeur de la propriété quantifierClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link QuantifierClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link QuantifierClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link QuantifierClass }{@code >}
     *
     */
    public JAXBElement<QuantifierClass> getQuantifierClass() {
        return quantifierClass;
    }

    /**
     * Définit la valeur de la propriété quantifierClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link QuantifierClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link QuantifierClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link QuantifierClass }{@code >}
     *
     */
    public void setQuantifierClass(JAXBElement<QuantifierClass> value) {
        this.quantifierClass = value;
    }

    /**
     * Obtient la valeur de la propriété naryRelnClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link NaryRelnClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryRelnClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryRelnClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryRelnClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryRelnClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryRelnClass }{@code >}
     *
     */
    public JAXBElement<NaryRelnClass> getNaryRelnClass() {
        return naryRelnClass;
    }

    /**
     * Définit la valeur de la propriété naryRelnClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link NaryRelnClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryRelnClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryRelnClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryRelnClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryRelnClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryRelnClass }{@code >}
     *
     */
    public void setNaryRelnClass(JAXBElement<NaryRelnClass> value) {
        this.naryRelnClass = value;
    }

    /**
     * Obtient la valeur de la propriété binaryRelnClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Neq }{@code >}
     *     {@link JAXBElement }{@code <}{@link Approx }{@code >}
     *     {@link JAXBElement }{@code <}{@link Tendsto }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link Factorof }{@code >}
     *
     */
    public JAXBElement<?> getBinaryRelnClass() {
        return binaryRelnClass;
    }

    /**
     * Définit la valeur de la propriété binaryRelnClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Neq }{@code >}
     *     {@link JAXBElement }{@code <}{@link Approx }{@code >}
     *     {@link JAXBElement }{@code <}{@link Tendsto }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link Factorof }{@code >}
     *
     */
    public void setBinaryRelnClass(JAXBElement<?> value) {
        this.binaryRelnClass = value;
    }

    /**
     * Obtient la valeur de la propriété intClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link IntClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link IntClass }{@code >}
     *
     */
    public JAXBElement<IntClass> getIntClass() {
        return intClass;
    }

    /**
     * Définit la valeur de la propriété intClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link IntClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link IntClass }{@code >}
     *
     */
    public void setIntClass(JAXBElement<IntClass> value) {
        this.intClass = value;
    }

    /**
     * Obtient la valeur de la propriété differentialOperatorClass.
     *
     * @return
     *     possible object is
     *     {@link DifferentialOperatorClass }
     *
     */
    public DifferentialOperatorClass getDifferentialOperatorClass() {
        return differentialOperatorClass;
    }

    /**
     * Définit la valeur de la propriété differentialOperatorClass.
     *
     * @param value
     *     allowed object is
     *     {@link DifferentialOperatorClass }
     *
     */
    public void setDifferentialOperatorClass(DifferentialOperatorClass value) {
        this.differentialOperatorClass = value;
    }

    /**
     * Obtient la valeur de la propriété partialdiffClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link PartialdiffClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link PartialdiffClass }{@code >}
     *
     */
    public JAXBElement<PartialdiffClass> getPartialdiffClass() {
        return partialdiffClass;
    }

    /**
     * Définit la valeur de la propriété partialdiffClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link PartialdiffClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link PartialdiffClass }{@code >}
     *
     */
    public void setPartialdiffClass(JAXBElement<PartialdiffClass> value) {
        this.partialdiffClass = value;
    }

    /**
     * Obtient la valeur de la propriété unaryVeccalcClass.
     *
     * @return
     *     possible object is
     *     {@link UnaryVeccalcClass }
     *
     */
    public UnaryVeccalcClass getUnaryVeccalcClass() {
        return unaryVeccalcClass;
    }

    /**
     * Définit la valeur de la propriété unaryVeccalcClass.
     *
     * @param value
     *     allowed object is
     *     {@link UnaryVeccalcClass }
     *
     */
    public void setUnaryVeccalcClass(UnaryVeccalcClass value) {
        this.unaryVeccalcClass = value;
    }

    /**
     * Obtient la valeur de la propriété narySetlistConstructorClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link Set }{@code >}
     *     {@link JAXBElement }{@code <}{@link org.geotoolkit.mathml.xml.List }{@code >}
     *
     */
    public JAXBElement<?> getNarySetlistConstructorClass() {
        return narySetlistConstructorClass;
    }

    /**
     * Définit la valeur de la propriété narySetlistConstructorClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link Set }{@code >}
     *     {@link JAXBElement }{@code <}{@link org.geotoolkit.mathml.xml.List }{@code >}
     *
     */
    public void setNarySetlistConstructorClass(JAXBElement<?> value) {
        this.narySetlistConstructorClass = value;
    }

    /**
     * Obtient la valeur de la propriété narySetClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link NarySetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NarySetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NarySetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NarySetClass }{@code >}
     *
     */
    public JAXBElement<NarySetClass> getNarySetClass() {
        return narySetClass;
    }

    /**
     * Définit la valeur de la propriété narySetClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link NarySetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NarySetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NarySetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NarySetClass }{@code >}
     *
     */
    public void setNarySetClass(JAXBElement<NarySetClass> value) {
        this.narySetClass = value;
    }

    /**
     * Obtient la valeur de la propriété binarySetClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BinarySetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySetClass }{@code >}
     *
     */
    public JAXBElement<BinarySetClass> getBinarySetClass() {
        return binarySetClass;
    }

    /**
     * Définit la valeur de la propriété binarySetClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BinarySetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinarySetClass }{@code >}
     *
     */
    public void setBinarySetClass(JAXBElement<BinarySetClass> value) {
        this.binarySetClass = value;
    }

    /**
     * Obtient la valeur de la propriété narySetRelnClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link NarySetRelnClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NarySetRelnClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NarySetRelnClass }{@code >}
     *
     */
    public JAXBElement<NarySetRelnClass> getNarySetRelnClass() {
        return narySetRelnClass;
    }

    /**
     * Définit la valeur de la propriété narySetRelnClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link NarySetRelnClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NarySetRelnClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NarySetRelnClass }{@code >}
     *
     */
    public void setNarySetRelnClass(JAXBElement<NarySetRelnClass> value) {
        this.narySetRelnClass = value;
    }

    /**
     * Obtient la valeur de la propriété unarySetClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link UnarySetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnarySetClass }{@code >}
     *
     */
    public JAXBElement<UnarySetClass> getUnarySetClass() {
        return unarySetClass;
    }

    /**
     * Définit la valeur de la propriété unarySetClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link UnarySetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnarySetClass }{@code >}
     *
     */
    public void setUnarySetClass(JAXBElement<UnarySetClass> value) {
        this.unarySetClass = value;
    }

    /**
     * Obtient la valeur de la propriété sumClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SumClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link SumClass }{@code >}
     *
     */
    public JAXBElement<SumClass> getSumClass() {
        return sumClass;
    }

    /**
     * Définit la valeur de la propriété sumClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SumClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link SumClass }{@code >}
     *
     */
    public void setSumClass(JAXBElement<SumClass> value) {
        this.sumClass = value;
    }

    /**
     * Obtient la valeur de la propriété productClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ProductClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProductClass }{@code >}
     *
     */
    public JAXBElement<ProductClass> getProductClass() {
        return productClass;
    }

    /**
     * Définit la valeur de la propriété productClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ProductClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ProductClass }{@code >}
     *
     */
    public void setProductClass(JAXBElement<ProductClass> value) {
        this.productClass = value;
    }

    /**
     * Obtient la valeur de la propriété limitClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link LimitClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link LimitClass }{@code >}
     *
     */
    public JAXBElement<LimitClass> getLimitClass() {
        return limitClass;
    }

    /**
     * Définit la valeur de la propriété limitClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link LimitClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link LimitClass }{@code >}
     *
     */
    public void setLimitClass(JAXBElement<LimitClass> value) {
        this.limitClass = value;
    }

    /**
     * Obtient la valeur de la propriété unaryElementaryClass.
     *
     * @return
     *     possible object is
     *     {@link UnaryElementaryClass }
     *
     */
    public UnaryElementaryClass getUnaryElementaryClass() {
        return unaryElementaryClass;
    }

    /**
     * Définit la valeur de la propriété unaryElementaryClass.
     *
     * @param value
     *     allowed object is
     *     {@link UnaryElementaryClass }
     *
     */
    public void setUnaryElementaryClass(UnaryElementaryClass value) {
        this.unaryElementaryClass = value;
    }

    /**
     * Obtient la valeur de la propriété naryStatsClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link NaryStatsClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryStatsClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryStatsClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryStatsClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryStatsClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryStatsClass }{@code >}
     *
     */
    public JAXBElement<NaryStatsClass> getNaryStatsClass() {
        return naryStatsClass;
    }

    /**
     * Définit la valeur de la propriété naryStatsClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link NaryStatsClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryStatsClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryStatsClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryStatsClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryStatsClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryStatsClass }{@code >}
     *
     */
    public void setNaryStatsClass(JAXBElement<NaryStatsClass> value) {
        this.naryStatsClass = value;
    }

    /**
     * Obtient la valeur de la propriété naryConstructorClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link NaryConstructorClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryConstructorClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryConstructorClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryConstructorClass }{@code >}
     *
     */
    public JAXBElement<NaryConstructorClass> getNaryConstructorClass() {
        return naryConstructorClass;
    }

    /**
     * Définit la valeur de la propriété naryConstructorClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link NaryConstructorClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryConstructorClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryConstructorClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryConstructorClass }{@code >}
     *
     */
    public void setNaryConstructorClass(JAXBElement<NaryConstructorClass> value) {
        this.naryConstructorClass = value;
    }

    /**
     * Obtient la valeur de la propriété unaryLinalgClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link UnaryLinalgClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryLinalgClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryLinalgClass }{@code >}
     *
     */
    public JAXBElement<UnaryLinalgClass> getUnaryLinalgClass() {
        return unaryLinalgClass;
    }

    /**
     * Définit la valeur de la propriété unaryLinalgClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link UnaryLinalgClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryLinalgClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link UnaryLinalgClass }{@code >}
     *
     */
    public void setUnaryLinalgClass(JAXBElement<UnaryLinalgClass> value) {
        this.unaryLinalgClass = value;
    }

    /**
     * Obtient la valeur de la propriété naryLinalgClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link NaryLinalgClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryLinalgClass }{@code >}
     *
     */
    public JAXBElement<NaryLinalgClass> getNaryLinalgClass() {
        return naryLinalgClass;
    }

    /**
     * Définit la valeur de la propriété naryLinalgClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link NaryLinalgClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link NaryLinalgClass }{@code >}
     *
     */
    public void setNaryLinalgClass(JAXBElement<NaryLinalgClass> value) {
        this.naryLinalgClass = value;
    }

    /**
     * Obtient la valeur de la propriété binaryLinalgClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link BinaryLinalgClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryLinalgClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryLinalgClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryLinalgClass }{@code >}
     *
     */
    public JAXBElement<BinaryLinalgClass> getBinaryLinalgClass() {
        return binaryLinalgClass;
    }

    /**
     * Définit la valeur de la propriété binaryLinalgClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link BinaryLinalgClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryLinalgClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryLinalgClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link BinaryLinalgClass }{@code >}
     *
     */
    public void setBinaryLinalgClass(JAXBElement<BinaryLinalgClass> value) {
        this.binaryLinalgClass = value;
    }

    /**
     * Obtient la valeur de la propriété constantSetClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     *
     */
    public JAXBElement<ConstantSetClass> getConstantSetClass() {
        return constantSetClass;
    }

    /**
     * Définit la valeur de la propriété constantSetClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantSetClass }{@code >}
     *
     */
    public void setConstantSetClass(JAXBElement<ConstantSetClass> value) {
        this.constantSetClass = value;
    }

    /**
     * Obtient la valeur de la propriété constantArithClass.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     *
     */
    public JAXBElement<ConstantArithClass> getConstantArithClass() {
        return constantArithClass;
    }

    /**
     * Définit la valeur de la propriété constantArithClass.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     *     {@link JAXBElement }{@code <}{@link ConstantArithClass }{@code >}
     *
     */
    public void setConstantArithClass(JAXBElement<ConstantArithClass> value) {
        this.constantArithClass = value;
    }

    /**
     * Obtient la valeur de la propriété presentationExpression.
     *
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Mstack }{@code >}
     *     {@link JAXBElement }{@code <}{@link Msub }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mn }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mmultiscripts }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mi }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mstyle }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mroot }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mfrac }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mlongdiv }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mtable }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mfenced }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mrow }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mtext }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mover }{@code >}
     *     {@link JAXBElement }{@code <}{@link Munderover }{@code >}
     *     {@link JAXBElement }{@code <}{@link Maligngroup }{@code >}
     *     {@link JAXBElement }{@code <}{@link Malignmark }{@code >}
     *     {@link JAXBElement }{@code <}{@link Maction }{@code >}
     *     {@link JAXBElement }{@code <}{@link Msubsup }{@code >}
     *     {@link JAXBElement }{@code <}{@link Merror }{@code >}
     *     {@link JAXBElement }{@code <}{@link Munder }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mpadded }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mspace }{@code >}
     *     {@link JAXBElement }{@code <}{@link Msup }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mphantom }{@code >}
     *     {@link JAXBElement }{@code <}{@link Ms }{@code >}
     *     {@link JAXBElement }{@code <}{@link Menclose }{@code >}
     *     {@link JAXBElement }{@code <}{@link Msqrt }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mo }{@code >}
     *
     */
    public JAXBElement<?> getPresentationExpression() {
        return presentationExpression;
    }

    /**
     * Définit la valeur de la propriété presentationExpression.
     *
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Mstack }{@code >}
     *     {@link JAXBElement }{@code <}{@link Msub }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mn }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mmultiscripts }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mi }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mstyle }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mroot }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mfrac }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mlongdiv }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mtable }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mfenced }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mrow }{@code >}
     *     {@link JAXBElement }{@code <}{@link Object }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mtext }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mover }{@code >}
     *     {@link JAXBElement }{@code <}{@link Munderover }{@code >}
     *     {@link JAXBElement }{@code <}{@link Maligngroup }{@code >}
     *     {@link JAXBElement }{@code <}{@link Malignmark }{@code >}
     *     {@link JAXBElement }{@code <}{@link Maction }{@code >}
     *     {@link JAXBElement }{@code <}{@link Msubsup }{@code >}
     *     {@link JAXBElement }{@code <}{@link Merror }{@code >}
     *     {@link JAXBElement }{@code <}{@link Munder }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mpadded }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mspace }{@code >}
     *     {@link JAXBElement }{@code <}{@link Msup }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mphantom }{@code >}
     *     {@link JAXBElement }{@code <}{@link Ms }{@code >}
     *     {@link JAXBElement }{@code <}{@link Menclose }{@code >}
     *     {@link JAXBElement }{@code <}{@link Msqrt }{@code >}
     *     {@link JAXBElement }{@code <}{@link Mo }{@code >}
     *
     */
    public void setPresentationExpression(JAXBElement<?> value) {
        this.presentationExpression = value;
    }

    /**
     * Obtient la valeur de la propriété semantics.
     *
     * @return
     *     possible object is
     *     {@link org.geotoolkit.mathml.xml.Mscarries.Semantics }
     *
     */
    public org.geotoolkit.mathml.xml.Mscarries.Semantics getSemantics() {
        return semantics;
    }

    /**
     * Définit la valeur de la propriété semantics.
     *
     * @param value
     *     allowed object is
     *     {@link org.geotoolkit.mathml.xml.Mscarries.Semantics }
     *
     */
    public void setSemantics(org.geotoolkit.mathml.xml.Mscarries.Semantics value) {
        this.semantics = value;
    }

    /**
     * Obtient la valeur de la propriété mprescripts.
     *
     * @return
     *     possible object is
     *     {@link Mprescripts }
     *
     */
    public Mprescripts getMprescripts() {
        return mprescripts;
    }

    /**
     * Définit la valeur de la propriété mprescripts.
     *
     * @param value
     *     allowed object is
     *     {@link Mprescripts }
     *
     */
    public void setMprescripts(Mprescripts value) {
        this.mprescripts = value;
    }

    /**
     * Obtient la valeur de la propriété subscriptshift.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSubscriptshift() {
        return subscriptshift;
    }

    /**
     * Définit la valeur de la propriété subscriptshift.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSubscriptshift(String value) {
        this.subscriptshift = value;
    }

    /**
     * Obtient la valeur de la propriété superscriptshift.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSuperscriptshift() {
        return superscriptshift;
    }

    /**
     * Définit la valeur de la propriété superscriptshift.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSuperscriptshift(String value) {
        this.superscriptshift = value;
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
     * Obtient la valeur de la propriété mathcolor.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMathcolor() {
        return mathcolor;
    }

    /**
     * Définit la valeur de la propriété mathcolor.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMathcolor(String value) {
        this.mathcolor = value;
    }

    /**
     * Obtient la valeur de la propriété mathbackground.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMathbackground() {
        return mathbackground;
    }

    /**
     * Définit la valeur de la propriété mathbackground.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMathbackground(String value) {
        this.mathbackground = value;
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
