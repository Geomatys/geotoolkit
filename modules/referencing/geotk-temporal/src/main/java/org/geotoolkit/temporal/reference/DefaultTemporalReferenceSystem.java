/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 * 
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2014, Geomatys
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

import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.referencing.AbstractReferenceSystem;
import org.apache.sis.referencing.crs.DefaultTemporalCRS;
import org.apache.sis.xml.Namespaces;
import org.opengis.metadata.extent.Extent;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.cs.TimeCS;
import org.opengis.referencing.datum.TemporalDatum;
import org.opengis.temporal.TemporalReferenceSystem;
import org.opengis.util.InternationalString;

/**
 * Information about a temporal reference system.
 *
 * @author Mehdi Sidhoum (Geomatys)
 * @module pending
 * @version 4.0
 * @since   4.0
 */
@XmlType(name = "TimeReferenceSystem_Type", propOrder = {
    "scope",
    "domaineOfValidity"
})
@XmlRootElement(name = "TimeReferenceSystem", namespace = Namespaces.GML)
public class DefaultTemporalReferenceSystem extends AbstractReferenceSystem implements TemporalReferenceSystem {
    
    /**
     * Creates a default {@link TemporalReferenceSystem} implementation from the given properties, datum and coordinate system.
     * The properties given in argument follow the same rules than for the
     * {@linkplain DefaultTemporalCRS#DefaultTemporalCRS(java.util.Map, org.opengis.referencing.datum.TemporalDatum, org.opengis.referencing.cs.TimeCS)  super-class constructor}.
     * The following table is a reminder of current main (not all) properties:
     *
     * <table class="ISO 19108">
     *   <caption>Recognized properties (non exhaustive list)</caption>
     *   <tr>
     *     <th>Property name</th>
     *     <th>Value type</th>
     *     <th>Returned by</th>
     *   </tr>
     *   <tr>
     *     <td>{@value org.opengis.referencing.IdentifiedObject#NAME_KEY}</td>
     *     <td>{@link org.opengis.referencing.Identifier} or {@link String}</td>
     *     <td>{@link #getName()}</td>
     *   </tr>
     *   <tr>
     *     <td>{@value org.opengis.referencing.datum.Datum#DOMAIN_OF_VALIDITY_KEY}</td>
     *     <td>{@link org.opengis.metadata.extent.Extent}</td>
     *     <td>{@link #getDomainOfValidity()}</td>
     *   </tr>
     * </table>
     *
     * @param properties The properties to be given to the coordinate reference system.
     */
    public DefaultTemporalReferenceSystem(Map<String, ?> properties) {
        super(properties);
    }
    
    /**
     * Empty constructor only use for XML marshalling.
     */
    protected DefaultTemporalReferenceSystem() {
        super(org.apache.sis.internal.referencing.NilReferencingObject.INSTANCE); 
    }
    
    /**
     * Constructs a new instance initialized with the values from the specified metadata object.
     * This is a <cite>shallow</cite> copy constructor, since the other metadata contained in the
     * given object are not recursively copied.
     *
     * @param object The TemporalReferenceSystem to copy values from, or {@code null} if none.
     *
     * @see #castOrCopy(TemporalReferenceSystem)
     */
    public DefaultTemporalReferenceSystem(final TemporalReferenceSystem object) {
        super(object);
    }

    /**
     * Returns a Geotk implementation with the values of the given arbitrary implementation.
     * This method performs the first applicable action in the following choices:
     *
     * <ul>
     *   <li>If the given object is {@code null}, then this method returns {@code null}.</li>
     *   <li>Otherwise if the given object is already an instance of
     *       {@code DefaultTemporalReferenceSystem}, then it is returned unchanged.</li>
     *   <li>Otherwise a new {@code DefaultTemporalReferenceSystem} instance is created using the
     *       {@linkplain #DefaultTemporalReferenceSystem(TemporalReferenceSystem) copy constructor}
     *       and returned. Note that this is a <cite>shallow</cite> copy operation, since the other
     *       metadata contained in the given object are not recursively copied.</li>
     * </ul>
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     */
    public static DefaultTemporalReferenceSystem castOrCopy(final TemporalReferenceSystem object) {
        if (object == null || object instanceof DefaultTemporalReferenceSystem) {
            return (DefaultTemporalReferenceSystem) object;
        }
        return new DefaultTemporalReferenceSystem(object);
    }
    
    /**
     * Method use for xml.
     * 
     * @return {@linkplain Extent#getDescription() extend description} from
     * {@linkplain #getDomainOfValidity() super class domaine of validity}
     */
    @XmlElement(name = "domainOfValidity", required = true)
    protected InternationalString getdomaineOfValidity() {
        return super.getDomainOfValidity().getDescription();
    }
}
