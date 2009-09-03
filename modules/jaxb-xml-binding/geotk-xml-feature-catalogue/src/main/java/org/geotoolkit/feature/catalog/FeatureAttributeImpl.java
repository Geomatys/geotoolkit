


package org.geotoolkit.feature.catalog;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.util.Utilities;
import org.opengis.feature.catalog.Constraint;
import org.opengis.feature.catalog.FeatureAttribute;
import org.opengis.feature.catalog.FeatureType;
import org.opengis.feature.catalog.ListedValue;
import org.opengis.util.LocalName;
import org.geotoolkit.util.Multiplicity;
import org.opengis.util.TypeName;


/**
 * Characteristic of a feature type.
 * 
 * <p>Java class for FC_FeatureAttribute_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FC_FeatureAttribute_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gfc}AbstractFC_PropertyType_Type">
 *       &lt;sequence>
 *         &lt;element name="code" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType" minOccurs="0"/>
 *         &lt;element name="valueMeasurementUnit" type="{http://www.isotc211.org/2005/gco}UnitOfMeasure_PropertyType" minOccurs="0"/>
 *         &lt;element name="listedValue" type="{http://www.isotc211.org/2005/gfc}FC_ListedValue_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="valueType" type="{http://www.isotc211.org/2005/gco}TypeName_PropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "code",
    //"valueMeasurementUnit",
    "listedValue",
    "valueType"
})
@XmlRootElement(name="FC_FeatureAttribute")
public class FeatureAttributeImpl extends PropertyTypeImpl implements FeatureAttribute {

    private String code;
   //TODO private UnitOfMeasurePropertyType valueMeasurementUnit;
    private List<ListedValue> listedValue;
    @XmlElement(required = true)
    private TypeName valueType;

     /**
     * An empty constructor used by JAXB
     */
    public FeatureAttributeImpl() {
        
    }
    
    /**
     * Clone a FeatureAttribute
     */
    public FeatureAttributeImpl(FeatureAttribute feature) {
        super(feature);
        if (feature != null) {
            this.code        = feature.getCode();
            this.listedValue = feature.getListedValue();
            this.valueType   = feature.getValueType();
        }
    }
    
    /**
     * Build a new Feature Attribute
     */
    public FeatureAttributeImpl(String id, LocalName memberName, String definition, Multiplicity cardinality, FeatureType featureType, 
            List<Constraint> constrainedBy, String code, List<ListedValue> listedValue, TypeName valueType) {
        super(id, memberName, definition, cardinality, featureType, constrainedBy, null);
        this.code        = code;
        this.listedValue = listedValue;
        this.valueType   = valueType;
    }
    /**
     * Gets the value of the code property.
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the valueMeasurementUnit property.
     * 
   
    public UnitOfMeasurePropertyType getValueMeasurementUnit() {
        return valueMeasurementUnit;
    }

    /**
     * Sets the value of the valueMeasurementUnit property.
     
    public void setValueMeasurementUnit(UnitOfMeasurePropertyType value) {
        this.valueMeasurementUnit = value;
    }

    /**
     * Gets the value of the listedValue property.
     */
    public List<ListedValue> getListedValue() {
        if (listedValue == null) {
            listedValue = new ArrayList<ListedValue>();
        }
        return this.listedValue;
    }
    
     /**
     * Gets the value of the listedValue property.
     */
    public void setListedValue(List<ListedValue> listedValue) {
        this.listedValue = listedValue;
    }
    
    /**
     * Gets the value of the listedValue property.
     */
    public void setListedValue(ListedValue listedValue) {
        if (this.listedValue == null) {
            this.listedValue = new ArrayList<ListedValue>();
        }
        this.listedValue.add(listedValue);
    }

    /**
     * Gets the value of the valueType property.
     * 
     */
    public TypeName getValueType() {
        return valueType;
    }

    /**
     * Sets the value of the valueType property.
     */
    public void setValueType(TypeName value) {
        this.valueType = value;
    }
    
    public FeatureAttributeImpl getReference() {
        FeatureAttributeImpl result = new FeatureAttributeImpl(this);
        result.setReference(true);
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        s.append("code: ").append(code).append('\n');
        if (listedValue != null) {
            s.append("listed values: ").append('\n');
            for (ListedValue l: listedValue){
                s.append(l).append('\n');
            }
        }
        if (valueType != null) {
            s.append("valueType: ").append(valueType).append('\n');
        }
        return s.toString();
    }
    
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object) && object instanceof FeatureAttributeImpl) {
            final FeatureAttributeImpl that = (FeatureAttributeImpl) object;
            
            return Utilities.equals(this.code,        that.code)        &&
                   Utilities.equals(this.listedValue, that.listedValue) &&
                   Utilities.equals(this.valueType,   that.valueType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + super.hashCode();
        hash = 29 * hash + (this.code != null ? this.code.hashCode() : 0);
        return hash;
    }

}
