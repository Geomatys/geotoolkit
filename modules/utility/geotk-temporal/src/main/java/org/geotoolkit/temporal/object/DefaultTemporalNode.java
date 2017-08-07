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
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.NullArgumentException;
import org.opengis.temporal.Instant;
import org.opengis.temporal.TemporalEdge;
import org.opengis.temporal.TemporalNode;
import org.opengis.util.InternationalString;

/**
 * Zero dimensional topological primitive in time.
 * Its geometric realization is a {@link Instant}.
 *
 * @author Remi Marechal (Geomatys).
 */
@XmlType(name = "TimeNode_Type", propOrder = {
    "previousEdge",
    "nextEdge",
    "realization"
})
@XmlRootElement(name = "TimeNode")
public class DefaultTemporalNode extends DefaultTemporalTopologicalPrimitive implements TemporalNode {

    /**
     * Association that may link this {@link TemporalNode} to its corresponding {@link Instant}.
     */
    private Instant realization;

    /**
     * {@link TemporalEdge} for which it is the {@link TemporalNode} start.
     */
    private TemporalEdge previousEdge;

    /**
     * {@link TemporalEdge} for which it is the {@link TemporalNode} end.
     */
    private TemporalEdge nextEdge;

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
     * @param realization association that may link this {@link TemporalNode} to its corresponding {@link Instant}, should be {@code null}.
     * @param previousEdge {@link TemporalEdge} for which it is the {@link TemporalNode} start, should be {@code null}.
     * @param nextEdge {@link TemporalEdge} for which it is the {@link TemporalNode} end, should be {@code null}.
     * @throws NullArgumentException if properties, start or end are {@code null}.
     */
    public DefaultTemporalNode(final Map<String, ?> properties, final Instant realization, final TemporalEdge previousEdge, final TemporalEdge nextEdge) throws NullArgumentException {
        super(properties);
        this.realization  = realization;
        this.previousEdge = previousEdge;
        this.nextEdge     = nextEdge;
    }

    /**
     * Constructs a new instance initialized with the values from the specified metadata object.
     * This is a <cite>shallow</cite> copy constructor, since the other metadata contained in the
     * given object are not recursively copied.
     *
     * @param object The Instant to copy values from, or {@code null} if none.
     *
     * @see #castOrCopy(TemporalNode)
     */
    private DefaultTemporalNode(final TemporalNode object) {
        super(object);
        if (object != null) {
            realization  = object.getRealization();
            previousEdge = object.getPreviousEdge();
            nextEdge     = object.getNextEdge();
        }
    }

    /**
     * Returns a Geotk implementation with the values of the given arbitrary implementation.
     * This method performs the first applicable action in the following choices:
     *
     * <ul>
     *   <li>If the given object is {@code null}, then this method returns {@code null}.</li>
     *   <li>Otherwise if the given object is already an instance of
     *       {@code DefaultTemporalNode}, then it is returned unchanged.</li>
     *   <li>Otherwise a new {@code DefaultTemporalNode} instance is created using the
     *       {@linkplain #DefaultTemporalNode(Period) copy constructor}
     *       and returned. Note that this is a <cite>shallow</cite> copy operation, since the other
     *       metadata contained in the given object are not recursively copied.</li>
     * </ul>
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     */
    public static DefaultTemporalNode castOrCopy(final TemporalNode object) {
        if (object == null || object instanceof DefaultTemporalNode) {
            return (DefaultTemporalNode) object;
        }
        return new DefaultTemporalNode(object);
    }

    /**
     * Empty constructor only use for XML binding.
     */
    private DefaultTemporalNode() {
        super();
        realization  = null;
        previousEdge = null;
        nextEdge     = null;
    }

    /**
     * Returns optional association that may link this {@link TemporalNode} to its corresponding {@link Instant}, or {@code null} if none.
     * Only one {@link TemporalNode} may be associated with a {@link Instant},
     * and only one {@link Instant} may be associated with this object.
     *
     * @return association that may link this {@link TemporalNode} to its corresponding {@link Instant}, or {@code null} if none.
     */
    @Override
    @XmlElement(name = "position")
    public Instant getRealization() {
        return realization;
    }

    /**
     * Returns {@link TemporalEdge} for which it is the {@link TemporalNode} start.
     *
     * @return {@link TemporalEdge} for which it is the {@link TemporalNode} start.
     */
    @Override
    @XmlElement(name = "previousEdge")
    public TemporalEdge getPreviousEdge() {
        return previousEdge;
    }

    /**
     * Returns {@link TemporalEdge} for which it is the {@link TemporalNode} end.
     *
     * @return {@link TemporalEdge} for which it is the {@link TemporalNode} end .
     */
    @Override
    @XmlElement(name = "nextEdge")
    public TemporalEdge getNextEdge() {
        return nextEdge;
    }
}
