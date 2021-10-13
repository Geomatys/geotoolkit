/*
 *    GeotoolKit - An Open Source Java GIS Toolkit
 *    http://geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.feature.catalog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.Objects;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.util.ComparisonMode;
import org.opengis.feature.catalog.BoundFeatureAttribute;
import org.opengis.feature.catalog.FeatureOperation;


/**
 * Operation that every instance of an associated feature type must implement. - [ocl] - triggeredByValuesOf realizes GF_Operation::triggeredByValuesOf; - observesValuesOf realizes GF_Operation::observesValuesOf; - affectsValuesOf realizes GF_Operation::affectsValuesOf - [/ocl]
 *
 * <p>Java class for FC_FeatureOperation_Type complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="FC_FeatureOperation_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.isotc211.org/2005/gfc}AbstractFC_PropertyType_Type">
 *       &lt;sequence>
 *         &lt;element name="signature" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType"/>
 *         &lt;element name="formalDefinition" type="{http://www.isotc211.org/2005/gco}CharacterString_PropertyType" minOccurs="0"/>
 *         &lt;element name="triggeredByValuesOf" type="{http://www.isotc211.org/2005/gfc}FC_BoundFeatureAttribute_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="observesValuesOf" type="{http://www.isotc211.org/2005/gfc}FC_BoundFeatureAttribute_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="affectsValuesOf" type="{http://www.isotc211.org/2005/gfc}FC_BoundFeatureAttribute_PropertyType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "signature",
    "formalDefinition",
    "triggeredByValuesOf",
    "observesValuesOf",
    "affectsValuesOf"
})
@XmlRootElement(name="FC_FeatureOperation")
public class FeatureOperationImpl extends PropertyTypeImpl implements FeatureOperation {

    @XmlElement(required = true)
    private String signature;
    private String formalDefinition;
    private List<BoundFeatureAttribute> triggeredByValuesOf;
    private List<BoundFeatureAttribute> observesValuesOf;
    private List<BoundFeatureAttribute> affectsValuesOf;

    public FeatureOperationImpl() {

    }

    public FeatureOperationImpl(final FeatureOperation operation) {
        super(operation);
        if (operation != null) {
            signature           = operation.getSignature();
            formalDefinition    = operation.getFormalDefinition();
            triggeredByValuesOf = operation.getTriggeredByValuesOf();
            observesValuesOf    = operation.getObservesValuesOf();
            affectsValuesOf     = operation.getAffectsValuesOf();
         }

    }
    /**
     * Gets the value of the signature property.
     */
    @Override
    public String getSignature() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     *
     */
    public void setSignature(final String value) {
        this.signature = value;
    }

    /**
     * Gets the value of the formalDefinition property.
    */
    @Override
    public String getFormalDefinition() {
        return formalDefinition;
    }

    /**
     * Sets the value of the formalDefinition property.
     */
    public void setFormalDefinition(final String value) {
        this.formalDefinition = value;
    }

    /**
     * Gets the value of the triggeredByValuesOf property.
     *
     *
     */
    @Override
    public List<BoundFeatureAttribute> getTriggeredByValuesOf() {
        if (triggeredByValuesOf == null) {
            triggeredByValuesOf = new ArrayList<>();
        }
        return this.triggeredByValuesOf;
    }

    public void setTriggeredByValuesOf(final BoundFeatureAttribute triggeredByValuesOf) {
        if (this.triggeredByValuesOf == null) {
            this.triggeredByValuesOf = new ArrayList<>();
        }
        this.triggeredByValuesOf.add(triggeredByValuesOf);
    }

    public void setTriggeredByValuesOf(final List<BoundFeatureAttribute> triggeredByValuesOf) {
        this.triggeredByValuesOf = triggeredByValuesOf;
    }

    /**
     * Gets the value of the observesValuesOf property.
     *
     */
    @Override
    public List<BoundFeatureAttribute> getObservesValuesOf() {
        if (observesValuesOf == null) {
            observesValuesOf = new ArrayList<>();
        }
        return this.observesValuesOf;
    }

    public void setObservesValuesOf(final BoundFeatureAttribute observesValuesOf) {
        if (this.observesValuesOf == null) {
            this.observesValuesOf = new ArrayList<>();
        }
        this.observesValuesOf.add(observesValuesOf);
    }

    public void setObservesValuesOf(final List<BoundFeatureAttribute> observesValuesOf) {
        this.observesValuesOf = observesValuesOf;
    }

    /**
     * Gets the value of the affectsValuesOf property.
     *
     */
    public List<BoundFeatureAttribute> getAffectsValuesOf() {
        if (affectsValuesOf == null) {
            affectsValuesOf = new ArrayList<>();
        }
        return this.affectsValuesOf;
    }

    public void setAffectsValuesOf(final BoundFeatureAttribute affectsValuesOf) {
        if (this.affectsValuesOf == null) {
            this.affectsValuesOf = new ArrayList<>();
        }
        this.affectsValuesOf.add(affectsValuesOf);
    }

    public void setAffectsValuesOf(final List<BoundFeatureAttribute> affectsValuesOf) {
        this.affectsValuesOf = affectsValuesOf;
    }

    @Override
    public FeatureOperationImpl getReferenceableObject() {
        FeatureOperationImpl result = new FeatureOperationImpl(this);
        result.setReference(true);
        return result;
    }

    private void beforeMarshal(final Marshaller marshaller) {
        if (rootElement) {
            beforeMarshal(new HashMap<>());
        }
    }


    @Override
    public Map<String, Referenceable> beforeMarshal(Map<String, Referenceable> alreadySee) {
        alreadySee = super.beforeMarshal(alreadySee);

        List<BoundFeatureAttribute> triggReplacement = new ArrayList<>();
        for (BoundFeatureAttribute bfa: getTriggeredByValuesOf()) {
            BoundFeatureAttributeImpl bfai = (BoundFeatureAttributeImpl) bfa;

            if (alreadySee.get(bfai.getId()) != null) {
                triggReplacement.add(bfai.getReferenceableObject());
            } else {
                alreadySee = bfai.beforeMarshal(alreadySee);
                triggReplacement.add(bfai);
            }
        }
        triggeredByValuesOf = triggReplacement;

        List<BoundFeatureAttribute> affReplacement = new ArrayList<>();
        for (BoundFeatureAttribute bfa: getAffectsValuesOf()) {
            BoundFeatureAttributeImpl bfai = (BoundFeatureAttributeImpl) bfa;

            if (alreadySee.get(bfai.getId()) != null) {
                affReplacement.add(bfai.getReferenceableObject());
            } else {
                alreadySee = bfai.beforeMarshal(alreadySee);
                affReplacement.add(bfai);
            }
        }
        affectsValuesOf = affReplacement;

        List<BoundFeatureAttribute> obsReplacement = new ArrayList<>();
        for (BoundFeatureAttribute bfa: getObservesValuesOf()) {
            BoundFeatureAttributeImpl bfai = (BoundFeatureAttributeImpl) bfa;

            if (alreadySee.get(bfai.getId()) != null) {
                obsReplacement.add(bfai.getReferenceableObject());
            } else {
                alreadySee = bfai.beforeMarshal(alreadySee);
                obsReplacement.add(bfai);
            }
        }
        observesValuesOf = obsReplacement;
        return alreadySee;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (signature !=null) {
            s.append("signature:").append(signature).append('\n');
        }
        if (formalDefinition != null) {
            s.append("formal defintion:").append(formalDefinition).append('\n');
        }
        if (affectsValuesOf != null) {
            s.append("affects value of:").append('\n');
            for (BoundFeatureAttribute b: affectsValuesOf) {
                s.append(b).append('\n');
            }
        }
        if (observesValuesOf != null) {
            s.append("observes value of:").append('\n');
            for (BoundFeatureAttribute b: observesValuesOf) {
                s.append(b).append('\n');
            }
        }
        if (triggeredByValuesOf != null) {
            s.append("triggered by value of:").append('\n');
            for (BoundFeatureAttribute b: triggeredByValuesOf) {
                s.append(b).append('\n');
            }
        }
        return s.toString();
    }

    /**
     * Verify if this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }
        if (super.equals(object) && object instanceof FeatureOperationImpl) {
            final FeatureOperationImpl that = (FeatureOperationImpl) object;

            return Objects.equals(this.affectsValuesOf,     that.affectsValuesOf)   &&
                   Objects.equals(this.formalDefinition,   that.formalDefinition) &&
                   Objects.equals(this.observesValuesOf,    that.observesValuesOf)  &&
                   Objects.equals(this.signature,          that.signature)        &&
                   Objects.equals(this.triggeredByValuesOf, that.triggeredByValuesOf);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + super.hashCode();
        hash = 97 * hash + (this.signature != null ? this.signature.hashCode() : 0);
        return hash;
    }

}
