

package org.geotoolkit.service;

import org.opengis.service.ParameterDirection;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.service.Parameter;
import org.opengis.util.MemberName;
import org.opengis.util.TypeName;


/**
 * <p>Java class for SV_Parameter_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SV_Parameter_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.isotc211.org/2005/gco}MemberName_Type"/>
 *         &lt;element name="direction" type="{http://www.isotc211.org/2005/srv}SV_ParameterDirection_PropertyType" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType" minOccurs="0"/>
 *         &lt;element name="optionality" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType"/>
 *         &lt;element name="repeatability" type="{http://www.isotc211.org/2005/gco}Boolean_PropertyType"/>
 *         &lt;element name="valueType" type="{http://www.isotc211.org/2005/gco}TypeName_PropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlType(name = "SV_Parameter_Type", propOrder = {
    "name",
    "direction",
    "description",
    "optionality",
    "repeatability",
    "valueType"
})
@XmlRootElement(name="SV_Parameter")
public class ParameterImpl implements Parameter {

    
    private MemberName name;
    private ParameterDirection direction;
    private String description;
    private String optionality;
    private Boolean repeatability;
    private TypeName valueType;

    /**
     * An empty constructor used by JAXB
     */
    public ParameterImpl() {
        
    }
    
    /**
     * Clone a parameter. 
     */
    public ParameterImpl(final Parameter parameter) {
        this.description   = parameter.getDescription();
        this.direction     = parameter.getDirection();
        this.name          = parameter.getName();
        this.optionality   = parameter.getOptionality();
        this.repeatability = parameter.getRepeatability();
        this.valueType     = parameter.getValueType();
        
    }
    
    /**
     * Gets the value of the name property.
     * 
     */
    @XmlElement(required = true)
    public MemberName getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     */
    public void setName(final MemberName value) {
        this.name = value;
    }

    /**
     * Gets the value of the direction property.
     * 
     */
    @XmlElement
    public ParameterDirection getDirection() {
        return direction;
    }

    /**
     * Sets the value of the direction property.
     * 
     */
    public void setDirection(final ParameterDirection value) {
        this.direction = value;
    }

    /**
     * Gets the value of the description property.
     * 
    */
    @XmlElement
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     */
    public void setDescription(final String value) {
        this.description = value;
    }

    /**
     * Gets the value of the optionality property.
     * 
     */
    @XmlElement(required = true)
    public String getOptionality() {
        return optionality;
    }

    /**
     * Sets the value of the optionality property.
     * 
     */
    public void setOptionality(final String value) {
        this.optionality = value;
    }

    /**
     * Gets the value of the repeatability property.
     * 
     */
    @XmlElement(required = true)
    public Boolean getRepeatability() {
        return repeatability;
    }

    /**
     * Sets the value of the repeatability property.
     * 
     */
    public void setRepeatability(final Boolean value) {
        this.repeatability = value;
    }

    /**
     * Gets the value of the valueType property.
     * 
     */
    @XmlElement
    public TypeName getValueType() {
        return valueType;
    }

    /**
     * Sets the value of the valueType property.
     * 
     */
    public void setValueType(final TypeName value) {
        this.valueType = value;
    }

}
