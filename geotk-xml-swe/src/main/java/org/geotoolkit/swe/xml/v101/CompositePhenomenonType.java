/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.swe.xml.v101;

import java.util.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.util.ComparisonMode;
import org.geotoolkit.swe.xml.CompositePhenomenon;

// geotoolkit dependencies

/**
  * Une propriété complexe composée de plusieur {@linkPlain Phenomenon phenomenon}
  *
  * @version $Id:
  * @author Guilhem Legal
 * @module
  */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CompositePhenomenon", propOrder = {"base", "component"})
public class CompositePhenomenonType extends CompoundPhenomenonType implements CompositePhenomenon {

    /**
     * The base phenomenon.
     */
    private PhenomenonType base;

    /**
     * The components.
     */
    @XmlElement(name="component")
    private List<PhenomenonPropertyType> component;

    /**
     * Empty constructor used by JAXB.
     */
    CompositePhenomenonType(){}

    /**
     * Build a new composite phenomenon.
     */
    public CompositePhenomenonType(final String id, final String name, final String definition, final String description,
            final PhenomenonType base, final Collection<PhenomenonType> component) {
        super(id, name, definition, description, component.size());
        this.base = base;
        this.component = new ArrayList<>();
        for (PhenomenonType pheno: component) {
            this.component.add(new PhenomenonPropertyType(pheno));
        }
    }

    public CompositePhenomenonType(final CompositePhenomenonType compo) {
        super(compo);
        this.base = new PhenomenonType(base);
        this.component = new ArrayList<>();
        for (PhenomenonType pheno: compo.getComponent()) {
            this.component.add(new PhenomenonPropertyType(new PhenomenonType(pheno)));
        }
    }

    /**
     * Return the base phenomenone.
     */
    public PhenomenonType getBase(){
        return base;
    }

    /**
     * Add a component to the list
     */
    public void addComponent(final PhenomenonType phenomenon) {
        component.add(new PhenomenonPropertyType(phenomenon));
    }

    /**
     * Return the components.
     */
    public List<PhenomenonType> getComponent() {
        List<PhenomenonType> result = new ArrayList<>();
        for (PhenomenonPropertyType phen: component) {
            result.add(phen.getPhenomenon());
        }
        return result;
    }

    /**
     * Return true if the composite phenomenon contains the specified phenomenon.
     *
     * @param phenomenonId
     * @return
     */
    public boolean containsPhenomenon(final String phenomenonId) {
        if (component != null) {
            for (PhenomenonPropertyType pheno : component) {
                if (pheno.getPhenomenon().getId().equals(phenomenonId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Return the components.
     */
    public List<PhenomenonPropertyType> getRealComponent() {
        if (component == null) {
            component = new ArrayList<>();
        }
        return component;
    }

    /**
     * Return a code representing this composite phenomenon.
     */
    @Override
    public final int hashCode() {
        return getId().hashCode();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof CompositePhenomenonType && super.equals(object, mode)) {
            final CompositePhenomenonType that = (CompositePhenomenonType) object;
           if ((this.component !=null && that.component == null)||(this.component ==null && that.component != null))
                return false;

            if (this.component !=null && that.component != null) {
                if (this.component.size() == that.component.size()) {
                    Iterator<PhenomenonPropertyType> i = this.component.iterator();
                    while (i.hasNext()) {
                        if (!that.component.contains(i.next()))
                            return false;
                    }
                } else return false;
            }
            return Objects.equals(this.getId(),             that.getId()) &&
                   Objects.equals(this.getDescription(),    that.getDescription()) &&
                   Objects.equals(this.getName(),           that.getName()) &&
                   Objects.equals(this.base,                that.base) &&
                   Objects.equals(this.component,           that.component);
       }
       return false;
    }

    /**
     * Retourne une chaine de charactere representant la station.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString() + '\n');
        if( base != null) {
            s.append("base: ").append(base.toString()).append('\n');
        } else {
            s.append("base is null (relatively normal)");
        }

        if (component != null) {
            Iterator i =  component.iterator();
            s.append("components :").append('\n');
            int j = 0;
            while (i.hasNext()) {
                s.append("component[").append(j).append("]:").append(i.next().toString()).append('\n');
                j++;
            }
        } else {
             s.append("COMPONENT IS NULL");
        }
        return s.toString();
    }

}
