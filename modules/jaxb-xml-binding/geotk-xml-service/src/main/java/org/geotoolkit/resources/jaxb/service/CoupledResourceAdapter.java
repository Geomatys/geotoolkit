/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2008, Geotools Project Managment Committee (PMC)
 *    (C) 2008, Geomatys
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
package org.geotoolkit.resources.jaxb.service;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.geotoolkit.service.CoupledResourceImpl;
import org.opengis.service.CoupledResource;

/**
 * JAXB adapter in order to map implementing class with the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @since 2.5
 * @source $URL: http://svn.geotools.org/trunk/modules/library/metadata/src/main/java/org/geotools/resources/jaxb/metadata/CoupledResourceAdapter.java $
 * @author Guilhem Legal
 */
public class CoupledResourceAdapter extends XmlAdapter<CoupledResourceAdapter, CoupledResource> {
    
    private CoupledResource coupledResource;
    
    /**
     * Empty constructor for JAXB only.
     */
    private CoupledResourceAdapter() {
    }

    /**
     * Wraps an coupledResource value with a {@code SV_CoupledResource} tags at marshalling-time.
     *
     * @param coupledResource The CoupledResource value to marshall.
     */
    protected CoupledResourceAdapter(final CoupledResource coupledResource) {
        this.coupledResource = coupledResource;
    }

    /**
     * Returns the CoupledResource value covered by a {@code SV_CoupledResource} tags.
     *
     * @param value The value to marshall.
     * @return The adapter which covers the CoupledResource value.
     */
    protected CoupledResourceAdapter wrap(final CoupledResource value) {
        return new CoupledResourceAdapter(value);
    }

    /**
     * Returns the {@link CoupledResourceImpl} generated from the metadata value.
     * This method is systematically called at marshalling-time by JAXB.
     */
    @XmlElement(name = "SV_CoupledResource")
    public CoupledResourceImpl getCoupledResource() {
        return (coupledResource instanceof CoupledResourceImpl) ?
            (CoupledResourceImpl)coupledResource : new CoupledResourceImpl(coupledResource);
    }

    /**
     * Sets the value for the {@link CoupledResourceImpl}. This method is systematically
     * called at unmarshalling-time by JAXB.
     */
    public void setCoupledResource(final CoupledResourceImpl CoupledResource) {
        this.coupledResource = CoupledResource;
    }

    /**
     * Does the link between metadata red from an XML stream and the object which will
     * contains this value. JAXB calls automatically this method at unmarshalling-time.
     *
     * @param value The adapter for this metadata value.
     * @return A java object which represents the metadata value.
     */
    @Override
    public CoupledResource unmarshal(CoupledResourceAdapter value) throws Exception {
        if (value == null) {
            return null;
        }
        return value.coupledResource;
    }

    /**
     * Does the link between java object and the way they will be marshalled into
     * an XML file or stream. JAXB calls automatically this method at marshalling-time.
     *
     * @param value The bound type value, here the CoupledResource.
     * @return The adapter for this CoupledResource.
     */
    @Override
    public CoupledResourceAdapter marshal(CoupledResource value) throws Exception {
        return new CoupledResourceAdapter(value);
    }

    
    

}
