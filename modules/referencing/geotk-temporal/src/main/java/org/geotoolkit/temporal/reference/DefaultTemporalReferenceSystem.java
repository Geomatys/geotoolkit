/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.temporal.reference;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.Identifier;
import org.opengis.temporal.TemporalReferenceSystem;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;

/**
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 */
public class DefaultTemporalReferenceSystem implements TemporalReferenceSystem {

    /**
     * This is a name that uniquely identifies the temporal reference system.
     */
    private Identifier name;
    private Extent domainOfValidity;
    private Extent validArea;
    private InternationalString scope;
    private Collection<GenericName> alias;
    private Set<Identifier> identifiers;
    private InternationalString remarks;

    /**
     * Creates a new instance of TemporalReferenceSystem by passing a Identifier name and a domain of validity.
     * @param name
     * @param domainOfValidity
     */
    public DefaultTemporalReferenceSystem(final Identifier name, final Extent domainOfValidity) {
        this.name = name;
        this.domainOfValidity = domainOfValidity;
    }

    public Identifier getName() {
        return name;
    }

    public Extent getDomainOfValidity() {
        return domainOfValidity;
    }

    /**
     * This method is deprecated, please use getDomainOfValidity() method.
     * @return
     */
    @Deprecated
    public Extent getValidArea() {
        return validArea;
    }

    public InternationalString getScope() {
        return scope;
    }

    public Collection<GenericName> getAlias() {
        return alias;
    }

    public Set<Identifier> getIdentifiers() {
        return identifiers;
    }

    public InternationalString getRemarks() {
        return remarks;
    }

    public String toWKT() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * This is a name that uniquely identifies the temporal reference system.
     */
    public void setName(final Identifier name) {
        this.name = name;
    }

    public void setDomainOfValidity(final Extent domainOfValidity) {
        this.domainOfValidity = domainOfValidity;
    }

    public void setValidArea(final Extent validArea) {
        this.validArea = validArea;
    }

    public void setScope(final InternationalString scope) {
        this.scope = scope;
    }

    @Override
    public boolean equals(final Object object) {
        if (object instanceof DefaultTemporalReferenceSystem) {
            final DefaultTemporalReferenceSystem that = (DefaultTemporalReferenceSystem) object;

            return Objects.equals(this.alias, that.alias) &&
                    Objects.equals(this.domainOfValidity, that.domainOfValidity) &&
                    Objects.equals(this.identifiers, that.identifiers) &&
                    Objects.equals(this.name, that.name) &&
                    Objects.equals(this.scope, that.scope) &&
                    Objects.equals(this.validArea, that.validArea) &&
                    Objects.equals(this.remarks, that.remarks);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.alias != null ? this.alias.hashCode() : 0);
        hash = 37 * hash + (this.domainOfValidity != null ? this.domainOfValidity.hashCode() : 0);
        hash = 37 * hash + (this.identifiers != null ? this.identifiers.hashCode() : 0);
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 37 * hash + (this.remarks != null ? this.remarks.hashCode() : 0);
        hash = 37 * hash + (this.scope != null ? this.scope.hashCode() : 0);
        hash = 37 * hash + (this.validArea != null ? this.validArea.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("TemporalReferenceSystem:").append('\n');
        if (name != null) {
            s.append("name:").append(name).append('\n');
        }
        if (domainOfValidity != null) {
            s.append("domainOfValidity:").append(domainOfValidity).append('\n');
        }
        return s.toString();
    }
}
