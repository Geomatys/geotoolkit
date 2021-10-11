/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.temporal.reference.xmlAdapter;

import javax.xml.bind.annotation.XmlElement;
import org.apache.sis.internal.jaxb.gco.PropertyType;
import org.apache.sis.xml.Namespaces;
import org.geotoolkit.temporal.object.DefaultTemporalEdge;
import org.geotoolkit.temporal.object.DefaultTemporalNode;
import org.geotoolkit.temporal.object.DefaultTemporalTopologicalPrimitive;
import org.opengis.temporal.TemporalEdge;
import org.opengis.temporal.TemporalNode;
import org.opengis.temporal.TemporalTopologicalPrimitive;

/**
 * JAXB adapter mapping implementing class to the GeoAPI interface. See
 * package documentation for more information about JAXB and interface.
 *
 * @author Remi Marechal (Geomatys).
 * @version 4.0
 * @since   4.0
 */
public class TemporalTopologicalPrimitiveAdapter extends PropertyType<TemporalTopologicalPrimitiveAdapter, TemporalTopologicalPrimitive> {

    /**
     * Empty constructor for JAXB only.
     */
    public TemporalTopologicalPrimitiveAdapter() {
    }

    /**
     * Constructor for the {@link #wrap} method only.
     */
    private TemporalTopologicalPrimitiveAdapter(final TemporalTopologicalPrimitive ttp) {
        super(ttp);
    }

    /**
     * Invoked by JAXB at marshalling time for getting the actual element to write
     * inside the {@code <gml:OrdinalEra>} XML element.
     * This is the value or a copy of the value given in argument to the {@code wrap} method.
     *
     * @return The element to be marshalled.
     */
    @XmlElement(name = "TimeEdge", namespace = Namespaces.GML)
    public DefaultTemporalEdge getElement() {
        if (metadata instanceof DefaultTemporalEdge) return DefaultTemporalEdge.castOrCopy((TemporalEdge) metadata);
        return null;
    }

    @XmlElement(name = "TimeNode", namespace = Namespaces.GML)
    public DefaultTemporalNode getElement2() {
        if (metadata instanceof DefaultTemporalNode) return DefaultTemporalNode.castOrCopy((TemporalNode) metadata);
        return null;
    }

    /**
     * Invoked by JAXB at marshalling time for getting the actual element to write
     * inside the {@code <gml:TemporalTopologicalPrimitive>} XML element.
     * This is the value or a copy of the value given in argument to the {@code wrap} method.
     *
     * @return The element to be marshalled.
     */
    @Override
    protected Class<TemporalTopologicalPrimitive> getBoundType() {
        return TemporalTopologicalPrimitive.class;
    }

    /**
     * Invoked by {@link PropertyType} at marshalling time for wrapping the given value
     * in a {@code <gml:TemporalTopologicalPrimitive>} XML element.
     *
     * @param  instant The element to marshall.
     * @return A {@code PropertyType} wrapping the given the element.
     */
    @Override
    protected TemporalTopologicalPrimitiveAdapter wrap(TemporalTopologicalPrimitive ttp) {
        return new TemporalTopologicalPrimitiveAdapter(ttp);
    }

    /**
     * Invoked by JAXB at unmarshalling time for storing the result temporarily.
     *
     * @param primitive The unmarshalled element.
     */
    public void setElement(final DefaultTemporalEdge primitive) {
        metadata = primitive;
    }

    public void setElement2(final DefaultTemporalNode primitive) {
        metadata = primitive;
    }
}
