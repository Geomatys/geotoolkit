/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2011, Geomatys
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
package org.geotoolkit.internal.jaxb.text;

import java.net.URI;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import org.geotoolkit.resources.Errors;


/**
 * A {@link CharSequenceAdapter} which can substitute text by anchors. At the difference of most
 * adapters provided in {@code org.geotoolkit.internal.jaxb} packages, this adapter is
 * <em>configurable</em>. It must be created explicitly with a map of bindings between
 * labels and URNs, and the configured adapter must be given to the mashaller as below:
 *
 * {@preformat java
 *     AnchoredCharSequenceAdapter adapter = new AnchoredCharSequenceAdapter();
 *     adapter.addLinkage(...);
 *     marshaller.setAdapter(adapter);
 *     marshaller.setAdapter(adapter.string);
 *     marshaller.setAdapter(adapter.international);
 * }
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.14
 *
 * @since 3.00
 * @module
 */
public final class AnchoredCharSequenceAdapter extends CharSequenceAdapter {
    /**
     * For default constructor of {@code AnchoredFooAdapter} only.
     */
    static final AnchoredCharSequenceAdapter INSTANCE =
            new AnchoredCharSequenceAdapter(Collections.<String,Object>emptyMap());

    /**
     * Binds string labels with URNs or anchors. Values can be
     * either {@link URI} or {@link AnchorType} instances.
     */
    private final Map<String,Object> anchors;

    /**
     * An adapter for {@link String} using the same anchors than this adapter.
     */
    public final AnchoredStringAdapter string;

    /**
     * An adapter for {@link org.opengis.util.InternationalString}
     * using the same anchors than this adapter.
     */
    public final AnchoredInternationalStringAdapter international;

    /**
     * Creates a uninitialized adapter.
     */
    public AnchoredCharSequenceAdapter() {
        anchors       = new HashMap<String,Object>();
        string        = new AnchoredStringAdapter(this);
        international = new AnchoredInternationalStringAdapter(this);
    }

    /**
     * Constructor for {@link #INSTANCE} only.
     */
    private AnchoredCharSequenceAdapter(final Map<String,Object> anchors) {
        this.anchors       = anchors;
        this.string        = null;
        this.international = null;
    }

    /**
     * Adds a label associated to the given URN.
     *
     * @param  label The label associated to the URN.
     * @param  linkage The URN.
     * @throws IllegalStateException If a URN is already associated to the given label.
     */
    public void addLinkage(final String label, final URI linkage) throws IllegalStateException {
        add(label, linkage);
    }

    /**
     * Adds an anchor (label associated to an URN).
     *
     * @param  anchor The anchor to add.
     * @throws IllegalStateException If a URN is already associated to the anchor value.
     *
     * @since 3.14
     */
    public void addLinkage(final AnchorType anchor) throws IllegalStateException {
        add(anchor.toString(), anchor);
    }

    /**
     * Implementation of {@code addLinkage} methods.
     */
    private void add(final String label, final Object linkage) throws IllegalStateException {
        synchronized (anchors) {
            final Object old = anchors.put(label, linkage);
            if (old != null) {
                anchors.put(label, old);
                throw new IllegalStateException(Errors.format(Errors.Keys.VALUE_ALREADY_DEFINED_$1, label));
            }
        }
    }

    /**
     * Converts a {@linkplain String string} to the object to be marshalled in a
     * XML file or stream. JAXB calls automatically this method at marshalling time.
     *
     * @param value The string value.
     * @return The adapter for this string.
     */
    @Override
    public CharacterString marshal(CharSequence value) {
        if (value != null) {
            final Object linkage;
            String key = value.toString();
            if (key != null) {
                key = key.trim();
                if (key.length() != 0) {
                    synchronized (anchors) {
                        linkage = anchors.get(key);
                    }
                    if (linkage != null) {
                        if (linkage instanceof URI) {
                            value = new AnchorType((URI) linkage, key);
                        } else {
                            value = (AnchorType) linkage;
                        }
                    }
                    return new CharacterString(value);
                }
            }
        }
        return null;
    }
}
