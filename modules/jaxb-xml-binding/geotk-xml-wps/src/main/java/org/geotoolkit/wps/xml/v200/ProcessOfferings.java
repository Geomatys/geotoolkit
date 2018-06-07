/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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

package org.geotoolkit.wps.xml.v200;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wps.xml.WPSResponse;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wps/2.0}ProcessOffering" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlType(name = "", propOrder = {
    "offeringToMarshall",
    "legacyDescriptions"
})
@XmlRootElement(name = "ProcessOfferings")
public class ProcessOfferings extends DocumentBase implements WPSResponse{

    protected List<ProcessOffering> processOffering;

    public ProcessOfferings() {

    }

    public ProcessOfferings(List<ProcessOffering> processOffering) {
        this.processOffering = processOffering;
    }

    /**
     *
     * Ordered list of one or more full Process
     * descriptions, listed in the order in which they were requested
     * in the DescribeProcess operation request.
     * Gets the value of the processOffering property.
     *
     */
    public List<ProcessOffering> getProcessOffering() {
        if (processOffering == null) {
            processOffering = new ArrayList<>();
        }
        return this.processOffering;
    }

    @XmlElement(name = "ProcessOffering", required = true)
    private List<ProcessOffering> getOfferingToMarshall() {
        if (FilterByVersion.isV2()) {
            return getProcessOffering();
        }

        return null;
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // Following section is boilerplate code for WPS v1 retro-compatibility.
    //
    ////////////////////////////////////////////////////////////////////////////

    private List<ProcessDescription> offeringAdapter;

    private static final Function<ProcessOffering, ProcessDescription> TO_DESCRIPTION = o ->  o == null ? null : o.getProcess();
    private static final Function<ProcessDescription, ProcessOffering> TO_OFFERING = d -> d == null ? null : new ProcessOffering(d);

    @XmlElement(name="ProcessDescription", namespace = "")
    private List<ProcessDescription> getLegacyDescriptions() {
        final List<ProcessOffering> po = getProcessOffering();
        if (po.isEmpty() && offeringAdapter == null) {
            offeringAdapter = new ListAdapter<>(po, TO_DESCRIPTION, TO_OFFERING);
        }

        return offeringAdapter;
    }

    private static class ListAdapter<U, V> extends AbstractList<V> {

        final List<U> source;
        final Function<U, V> forward;
        final Function<V, U> backward;

        public ListAdapter(List<U> source, Function<U, V> forward, Function<V, U> backward) {
            this.source = source;
            this.forward = forward;
            this.backward = backward;
        }

        @Override
        public V get(int index) {
            return forward.apply(source.get(index));
        }

        @Override
        public int size() {
            return source.size();
        }

        @Override
        public void add(int index, V element) {
            source.add(index, backward.apply(element));
        }

        @Override
        public V remove(int index) {
            return forward.apply(source.remove(index));
        }

        @Override
        public V set(int index, V element) {
            return forward.apply(
                    source.set(index, backward.apply(element))
            );
        }
    }
}
