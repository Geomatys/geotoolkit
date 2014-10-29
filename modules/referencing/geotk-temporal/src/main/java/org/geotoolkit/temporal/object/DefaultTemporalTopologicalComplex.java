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
package org.geotoolkit.temporal.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.internal.referencing.NilReferencingObject;
import org.apache.sis.referencing.AbstractIdentifiedObject;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.NullArgumentException;
import org.opengis.temporal.TemporalEdge;
import org.opengis.temporal.TemporalNode;
import org.opengis.temporal.TemporalTopologicalComplex;
import org.opengis.temporal.TemporalTopologicalPrimitive;

/**
 * <p>An aggregation of connected {@linkplain TemporalTopologicalPrimitive temporal topological
 * primitives}.<br/>
 * In our case {@linkplain TemporalTopologicalPrimitive temporal topological
 * primitives} are {@linkplain DefaultTemporalTopologicalPrimitive default temporal topological
 * primitives} type.</p>
 *
 * @author Remi Marechal (Geomatys).
 */
@XmlType(name = "TimeTopologyComplex_Type", propOrder = {
    "composition"
})
@XmlRootElement(name = "TimeTopologyComplex")
public class DefaultTemporalTopologicalComplex extends AbstractIdentifiedObject implements TemporalTopologicalComplex {

    /**
     * Link the {@link DefaultTemporalTopologicalComplex} to the set of {@link TemporalTopologicalPrimitive} that its includes.
     */
    final Collection<TemporalTopologicalPrimitive> composition;
    
    /**
     * Creates a default {@link TemporalTopologicalComplex} implementation from the given properties and {@link Instant}.
     * The properties given in argument follow the same rules than for the
     * {@linkplain DefaultTemporalGeometricPrimitive#DefaultTemporalGeometricPrimitive(java.util.Map) )  super-class constructor}.
     * 
     * 
     * <table class="referencingTemporal">
     *   <caption>Recognized properties (non exhaustive list)</caption>
     *   <tr>
     *     <th>Property name</th>
     *     <th>Value type</th>
     *     <th>Returned by</th>
     *   </tr>
     *   <tr>
     *     <th colspan="3" class="hsep">Defined in parent class (reminder)</th>
     *   </tr>
     *   <tr>
     *     <td>{@value org.opengis.referencing.IdentifiedObject#NAME_KEY}</td>
     *     <td>{@link Identifier} or {@link String}</td>
     *     <td>{@link #getName()}</td>
     *   </tr>
     *   <tr>
     *     <td>{@value org.opengis.referencing.IdentifiedObject#IDENTIFIERS_KEY}</td>
     *     <td>{@link Identifier} (optionally as array)</td>
     *     <td>{@link #getIdentifiers()}</td>
     *   </tr>
     *   <tr>
     *     <td>{@value org.opengis.referencing.IdentifiedObject#REMARKS_KEY}</td>
     *     <td>{@link InternationalString} (optionally as array)</td>
     *     <td>{@link #getRemarks() }</td>
     *   </tr>
     * </table>
     * 
     * @param properties The properties to be given to this object.
     * @param primitives the set of {@link TemporalTopologicalPrimitive} that its includes
     * @throws NullArgumentException if primitives is {@code null}. 
     */
    public DefaultTemporalTopologicalComplex(final Map<String, ?> properties, final Collection<TemporalTopologicalPrimitive> primitives) throws IllegalArgumentException {
        super(properties);
        ArgumentChecks.ensureNonNull("primitives", primitives);
        this.composition = primitives;
    }

    /**
     * Private constructor adapted for XML binding.
     */
    private DefaultTemporalTopologicalComplex() {
        super(NilReferencingObject.INSTANCE);
        this.composition = null;
    }
    
    /**
     * Constructs a new instance initialized with the values from the specified metadata object.
     * This is a <cite>shallow</cite> copy constructor, since the other metadata contained in the
     * given object are not recursively copied.
     *
     * @param object The Calendar to copy values from, or {@code null} if none.
     *
     * @see #castOrCopy(TemporalTopologicalComplex)
     */
    private DefaultTemporalTopologicalComplex (final TemporalTopologicalComplex object) {
        super(object);
        if (object != null) {
            this.composition = object.getTemporalTopologicalPrimitives();
        } else {
            this.composition = null;
        }
    }

    /**
     * Returns a Geotk implementation with the values of the given arbitrary implementation.
     * This method performs the first applicable action in the following choices:
     *
     * <ul>
     *   <li>If the given object is {@code null}, then this method returns {@code null}.</li>
     *   <li>Otherwise if the given object is already an instance of
     *       {@code DefaultTemporalTopologicalComplex}, then it is returned unchanged.</li>
     *   <li>Otherwise a new {@code DefaultTemporalTopologicalComplex} instance is created using the
     *       {@linkplain #DefaultTemporalTopologicalComplex(TemporalTopologicalComplex) copy constructor}
     *       and returned. Note that this is a <cite>shallow</cite> copy operation, since the other
     *       metadata contained in the given object are not recursively copied.</li>
     * </ul>
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     */
    public static DefaultTemporalTopologicalComplex  castOrCopy(final TemporalTopologicalComplex object) {
        if (object == null || object instanceof DefaultTemporalTopologicalComplex) {
            return (DefaultTemporalTopologicalComplex) object;
        }
        return new DefaultTemporalTopologicalComplex(object);
    }
    
    
    /**
     * Returns aggregation of connected {@linkplain TemporalTopologicalPrimitive temporal topological primitives}
     * 
     * @return aggregation of connected {@linkplain TemporalTopologicalPrimitive temporal topological primitives}
     */
    @Override
    public Collection<TemporalTopologicalPrimitive> getTemporalTopologicalPrimitives() {
        return composition;
    }
    
    /**
     * Returns aggregation of connected {@linkplain TemporalTopologicalPrimitive temporal topological primitives}
     * 
     * @return aggregation of connected {@linkplain TemporalTopologicalPrimitive temporal topological primitives}
     */
    @XmlElement(name = "primitive", required = true)
    private Collection<TemporalTopologicalPrimitive> getComposition() {
        if (composition.isEmpty()) return null; // should never happend
        final Object[] prim = composition.toArray();
        final List<TemporalTopologicalPrimitive> lst = new ArrayList<TemporalTopologicalPrimitive>();
        for (int i = 0, s = prim.length; i < s; i++) {
            if (prim[i] instanceof TemporalEdge) {
                lst.add(DefaultTemporalEdge.castOrCopy((TemporalEdge) prim[i]));
            } else if (prim[i] instanceof TemporalNode) {
                lst.add(DefaultTemporalNode.castOrCopy((TemporalNode) prim[i]));
            } else {
                return null;
            }
        }
        return lst;
    }
}
