

package org.geotoolkit.util;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.resources.jaxb.feature.catalog.MultiplicityRangeAdapter;
import org.geotoolkit.util.Utilities;


/**
 * Use to represent the possible cardinality of a relation. Represented by a set of simple multiplicity ranges.
 * 
 * <p>Java class for Multiplicity_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Multiplicity_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gco}AbstractObject_Type">
 *       &lt;sequence>
 *         &lt;element name="range" type="{http://www.isotc211.org/2005/gco}MultiplicityRange_PropertyType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType( propOrder = {
    "range"
})
@XmlRootElement(name = "Multiplicity")
public class Multiplicity {

    @XmlJavaTypeAdapter(MultiplicityRangeAdapter.class)
    @XmlElement(required = true)
    private List<MultiplicityRange> range;

    /**
     * An empty constructor used by JAXB
     */
    public Multiplicity() {
        
    }
    
    /**
     * Build a simple Mulitiplicity 
     */
    public Multiplicity(MultiplicityRange range) {
        this.range = new ArrayList<MultiplicityRange>();
        this.range.add(range);
    }
    
    /**
     * Build a complex Mulitiplicity 
     */
    public Multiplicity(List<MultiplicityRange> range) {
        this.range = range;
    }
    
    /**
     * Gets the value of the range property.
     */
    public List<MultiplicityRange> getRange() {
        if (range == null) {
            range = new ArrayList<MultiplicityRange>();
        }
        return this.range;
    }
    
    /**
     * sets the value of the range property.
     */
    public void setRange(List<MultiplicityRange> range) {
        this.range = range;
    }
    
    
    /**
     * sets the value of the range property.
     */
    public void setRange(MultiplicityRange range) {
        if (this.range == null) {
            this.range = new ArrayList<MultiplicityRange>();
        }
        this.range.add(range);
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[Multiplicity]:").append('\n');
        for (MultiplicityRange m: getRange()) {
            s.append(m).append('\n');
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
        if (object instanceof Multiplicity) {
            final Multiplicity that = (Multiplicity) object;
            return Utilities.equals(this.range, that.range);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.range != null ? this.range.hashCode() : 0);
        return hash;
    }

}
