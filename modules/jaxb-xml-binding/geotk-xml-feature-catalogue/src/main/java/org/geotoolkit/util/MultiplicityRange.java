

package org.geotoolkit.util;

import org.opengis.util.*;
import org.geotoolkit.resources.jaxb.feature.catalog.UnlimitedIntegerAdapter;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.internal.jaxb.primitive.IntegerAdapter;
import org.geotoolkit.util.Utilities;


/**
 * A component of a multiplicity, consisting of an non-negative lower bound, and a potentially infinite upper bound.
 * 
 * <p>Java class for MultiplicityRange_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MultiplicityRange_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="lower" type="{http://www.isotc211.org/2005/gco}Integer_PropertyType"/>
 *         &lt;element name="upper" type="{http://www.isotc211.org/2005/gco}UnlimitedInteger_PropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MultiplicityRange_Type", propOrder = {
    "lower",
    "upper"
})
public class MultiplicityRange {

    @XmlJavaTypeAdapter(IntegerAdapter.class)
    @XmlElement(required = true)
    private Integer lower;
    
    @XmlJavaTypeAdapter(UnlimitedIntegerAdapter.class)
    @XmlElement(required = true)
    private UnlimitedInteger upper;

    /**
     * An empty constructor used by JAXB
     */
    public MultiplicityRange() {
        
    }
    
    /**
     * An empty constructor used by JAXB
     */
    public MultiplicityRange(final int lower, final UnlimitedInteger upper) {
        this.lower = lower;
        this.upper = upper;
    }
    
    /**
     * Gets the value of the lower property.
     */
    public int getLower() {
        return lower;
    }

    /**
     * Sets the value of the lower property.
     * 
    */
    public void setLower(final Integer value) {
        this.lower = value;
    }

    /**
     * Gets the value of the upper property.
     */
    public UnlimitedInteger getUpper() {
        return upper;
    }

    /**
     * Sets the value of the upper property.
     * 
     */
    public void setUpper(final UnlimitedInteger value) {
        this.upper = value;
    }
    
    @Override
    public String toString() {
        return "[MultiplicityRange]: lower=" + lower + " upper=" + upper; 
    }
    public String toString(final String margin) {
        return margin + "[MultiplicityRange]: lower=" + lower + " upper=" + upper; 
    }
    
    
    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof MultiplicityRange) {
            final MultiplicityRange that = (MultiplicityRange) object;
            return Utilities.equals(this.lower, that.lower);
                //&& Utilities.equals(this.upper, that.upper);
            // temporary patch TODO fix it  && Utilities.equals(this.upper, that.upper);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.lower;
        hash = 97 * hash + (this.upper != null ? this.upper.hashCode() : 0);
        return hash;
    }

}
