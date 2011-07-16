/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotoolkit.util;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author guilhem
 * @module pending
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "value"
})
@XmlRootElement(name = "UnlimitedInteger")
public class XmlUnlimitedInteger {

    @XmlValue
    @XmlSchemaType(name = "nonNegativeInteger")
    private Integer value;

    @XmlAttribute
    private Boolean isInfinite;

    @XmlAttribute(name = "nil", namespace="http://www.w3.org/2001/XMLSchema-instance")
    protected Boolean nil;

    public XmlUnlimitedInteger() {
    }

    public XmlUnlimitedInteger(final int value) {
        this.value = value;
    }

    public XmlUnlimitedInteger(final UnlimitedInteger multiplicity) {
        this.isInfinite = false;
        if (multiplicity != null) {
            this.isInfinite =  multiplicity.isInfinite();
        }
        if (!isInfinite && multiplicity != null) {
            this.value  = multiplicity.intValue();
            this.nil    = null;
        } else {
            this.value  = null;
            this.nil    = true;

        }
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(final Integer value) {
        this.value = value;
    }

    public Boolean isInfinite() {
        return isInfinite;
    }

    @Override
    public String toString() {
        return "[XmlUnlimitedInteger] is infinite: " + isInfinite + " nil: " + nil + " value: " + value;
    }

}
