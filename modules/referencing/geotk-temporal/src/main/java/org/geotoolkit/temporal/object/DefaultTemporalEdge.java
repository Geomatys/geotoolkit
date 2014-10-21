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

import java.util.Map;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalEdge;
import org.opengis.temporal.TemporalNode;

/**
 * One dimensional topological primitive in time.
 * In other words it coresponds to a {@link Period}.
 * 
 * @author remi Marechal (Geomatys).
 */
@XmlType(name = "TimeEdge_Type", propOrder = {
    "realization",
    "start", 
    "end"
})
@XmlRootElement(name = "TimeEdge")
public class DefaultTemporalEdge extends DefaultTemporalTopologicalPrimitive implements TemporalEdge {

    /**
     * Association that may link this {@link TemporalEdge} to its corresponding {@link Period}.
     */
    private Period realization;
    
    /**
     * {@link TemporalNode} for which it is the {@link TemporalEdge} start.
     * A {@link TemporalEdge} may have one and only one start node.
     */
    private TemporalNode start;
    
    /**
     * {@link TemporalNode} for which it is the {@link TemporalEdge} end.
     * A {@link TemporalEdge} may have one and only one end node.
     */
    private TemporalNode end;
    
    /**
     * Creates a default {@link TemporalNode} implementation from the given properties and {@link Instant}.
     * The properties given in argument follow the same rules than for the
     * {@linkplain DefaultTemporalGeometricPrimitive#DefaultTemporalGeometricPrimitive(java.util.Map) )  super-class constructor}.
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
     * @param realization Association that may link this {@link TemporalEdge} to its corresponding {@link Period}, should be {@code null}.
     * @param start {@link TemporalNode} for which it is the {@link TemporalEdge} start.
     * @param end {@link TemporalNode} for which it is the {@link TemporalEdge} end.
     * @throws NullArgumentException if properties, start or end are {@code null}. 
     */
    public DefaultTemporalEdge(final Map<String, ?> properties, final Period realization, final TemporalNode start, final TemporalNode end) {
        super(properties);
        this.realization = realization;
        this.start       = start;
        this.end         = end;
    }
    
    /**
     * Constructs a new instance initialized with the values from the specified metadata object.
     * This is a <cite>shallow</cite> copy constructor, since the other metadata contained in the
     * given object are not recursively copied.
     *
     * @param object The Instant to copy values from, or {@code null} if none.
     *
     * @see #castOrCopy(TemporalEdge)
     */
    private DefaultTemporalEdge(final TemporalEdge object) {
        super(object);
        if (object != null) {
            realization = object.getRealization();
            start       = object.getStart();
            end         = object.getEnd();
        }
    }

    /**
     * Returns a Geotk implementation with the values of the given arbitrary implementation.
     * This method performs the first applicable action in the following choices:
     *
     * <ul>
     *   <li>If the given object is {@code null}, then this method returns {@code null}.</li>
     *   <li>Otherwise if the given object is already an instance of
     *       {@code DefaultTemporalEdge}, then it is returned unchanged.</li>
     *   <li>Otherwise a new {@code DefaultTemporalEdge} instance is created using the
     *       {@linkplain #DefaultTemporalEdge(TemporalEdge) copy constructor}
     *       and returned. Note that this is a <cite>shallow</cite> copy operation, since the other
     *       metadata contained in the given object are not recursively copied.</li>
     * </ul>
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     */
    public static DefaultTemporalEdge castOrCopy(final TemporalEdge object) {
        if (object == null || object instanceof DefaultTemporalEdge) {
            return (DefaultTemporalEdge) object;
        }
        return new DefaultTemporalEdge(object);
    }
    
    /**
     * Empty constructor only use for XML binding.
     */
    private DefaultTemporalEdge() {
        super();
    }
    
    /**
     * Returns association that may link this {@link TemporalEdge} to its corresponding {@link Period}.
     * 
     * @return association that may link this {@link TemporalEdge} to its corresponding {@link Period}.
     */
    @Override
    @XmlElement(name = "extent")
    public Period getRealization() {
        return realization;
    }

    /**
     * Returns {@link TemporalNode} for which it is the {@link TemporalEdge} start.
     * 
     * @return {@link TemporalNode} for which it is the {@link TemporalEdge} start.
     */
    @Override
    @XmlElement(name = "start", required = true)
    public TemporalNode getStart() {
        return start;
    }

    /**
     * Returns {@link TemporalNode} for which it is the {@link TemporalEdge} end.
     * 
     * @return {@link TemporalNode} for which it is the {@link TemporalEdge} start.
     */
    @Override
    @XmlElement(name = "end", required = true)
    public TemporalNode getEnd() {
        return end;
    }
}
