/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.sml.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.util.Utilities;

/**
 *
 * @author guilhem
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class LinkRef {

    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String ref;

    public LinkRef() {
    }

    public LinkRef(String ref) {
        this.ref = ref;
    }

    /**
     * Gets the value of the ref property.
     */
    public String getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     */
    public void setRef(String value) {
        this.ref = value;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof LinkRef) {
            final LinkRef that = (LinkRef) object;
            return Utilities.equals(this.ref, that.ref);

        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (this.ref != null ? this.ref.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[LinkRef]").append("\n");
        if (ref != null) {
            sb.append("ref: ").append(ref).append('\n');
        }
        return sb.toString();
    }
}
