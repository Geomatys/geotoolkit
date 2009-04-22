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
package org.geotoolkit.wcs.xml.v111;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.AnyValue;
import org.geotoolkit.ows.xml.v110.CodeType;
import org.geotoolkit.ows.xml.v110.DescriptionType;
import org.geotoolkit.ows.xml.v110.KeywordsType;
import org.geotoolkit.ows.xml.v110.LanguageStringType;
import org.geotoolkit.ows.xml.v110.UnNamedDomainType;


/**
 * Description of an individual field in a coverage range record. 
 * 
 * <p>Java class for FieldType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FieldType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}DescriptionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wcs}Identifier"/>
 *         &lt;element name="Definition" type="{http://www.opengis.net/ows/1.1}UnNamedDomainType"/>
 *         &lt;element name="NullValue" type="{http://www.opengis.net/ows/1.1}CodeType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/wcs}InterpolationMethods"/>
 *         &lt;element name="Axis" type="{http://www.opengis.net/wcs}AxisType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FieldType", propOrder = {
    "identifier",
    "definition",
    "nullValue",
    "interpolationMethods",
    "axis"
})
public class FieldType extends DescriptionType {

    @XmlElement(name = "Identifier", required = true)
    private String identifier;
    @XmlElement(name = "Definition", required = true)
    private UnNamedDomainType definition;
    @XmlElement(name = "NullValue")
    private List<CodeType> nullValue = new ArrayList<CodeType>();
    @XmlElement(name = "InterpolationMethods", required = true)
    private InterpolationMethods interpolationMethods;
    @XmlElement(name = "Axis")
    private List<AxisType> axis = new ArrayList<AxisType>();

    /**
     * Empty constrcutor used by JAXB.
     */
    FieldType() {
        super();
    }
    
    /**
     * Build a full Field.
     * 
     * @param title
     * @param _abstract
     * @param keywords
     * @param identifier
     * @param definition
     * @param nullValue
     * @param interpolationMethods
     * @param axis
     */
    public FieldType(List<LanguageStringType> title,  List<LanguageStringType> _abstract,
            List<KeywordsType> keywords, String identifier, UnNamedDomainType definition,
            List<CodeType> nullValue, InterpolationMethods interpolationMethods, List<AxisType> axis) {
        super(title, _abstract, keywords);
        this.identifier           = identifier;
        this.interpolationMethods = interpolationMethods;
        this.axis                 = axis;
        this.nullValue            = nullValue;
        this.definition           = definition;
    }
    
     /**
     * Build a Light Field.
     * 
     * @param identifier
     * @param definition
     * @param nullValue
     * @param interpolationMethods
     * @param axis
     */
    public FieldType(String identifier, UnNamedDomainType definition,
            CodeType nullValue, InterpolationMethods interpolationMethods) {
        super();
        this.identifier           = identifier;
        this.interpolationMethods = interpolationMethods;
        this.nullValue            = new ArrayList<CodeType>();
        this.nullValue.add(nullValue);
        if (definition != null) {
            this.definition       = definition;
        } else {
            this. definition = new UnNamedDomainType(new AnyValue());
        }
    }
    
    /**
     * Identifier of this Field. These field identifiers shall be unique in one CoverageDescription. 
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Gets the value of the definition property.
     */
    public UnNamedDomainType getDefinition() {
        return definition;
    }

    /**
     * Gets the value of the nullValue property.
     * 
     */
    public List<CodeType> getNullValue() {
        return Collections.unmodifiableList(nullValue);
    }

    /**
     * Spatial interpolation method(s) that server can apply to this field. One of these interpolation methods shall be used when a GetCoverage operation request requires resampling. When the only interpolation method listed is �none�, clients may only retrieve coverages from this coverage in its native CRS at its native resolution. 
     * 
     */
    public InterpolationMethods getInterpolationMethods() {
        return interpolationMethods;
    }

    /**
     * Gets the value of the axis property.
     */
    public List<AxisType> getAxis() {
        return Collections.unmodifiableList(axis);
    }

}
