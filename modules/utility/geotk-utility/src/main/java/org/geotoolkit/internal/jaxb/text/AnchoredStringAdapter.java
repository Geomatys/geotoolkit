/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
import org.geotoolkit.resources.Errors;


/**
 * A {@link StringAdapter} which can substitute text by anchors. At the difference of most
 * adapters provided in {@code org.geotoolkit.internal.jaxb} packages, this adapter is
 * <em>configurable</em>. It must be created explicitly with a map of bindings between
 * labels and URNs, and the configured adapter must be given to the mashaller as below:
 *
 * {@preformat java
 *     AnchoredStringAdapter adapter = new AnchoredStringAdapter();
 *     adapter.addLinkage(...);
 *     marshaller.setAdapter(adapter);
 * }
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
public final class AnchoredStringAdapter extends StringAdapter {
    /**
     * Binds string labels with URNs.
     */
    private final Map<String,URI> anchors = new HashMap<String,URI>();

    /**
     * An adapter for {@link InternationalString} using the same anchors than this adapter.
     */
    public final AnchoredInternationalStringAdapter international =
            new AnchoredInternationalStringAdapter(anchors);

    /**
     * Creates a unitialized adapter.
     */
    public AnchoredStringAdapter() {
    }

    /**
     * Adds a label associated to the given URN.
     *
     * @param  label The label associated to the URN.
     * @param  linkage The URN.
     * @throws IllegalStateException If a URN is already associated to the given linkage.
     */
    public synchronized void addLinkage(final String label, final URI linkage)
            throws IllegalStateException
    {
        final URI old;
        synchronized (anchors) {
            old = anchors.put(label, linkage);
        }
        if (old != null) {
            throw new IllegalStateException(Errors.format(Errors.Keys.VALUE_ALREADY_DEFINED_$1, label));
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
    public CharacterString marshal(final String value) {
        if (value == null) {
            return null;
        }
        final URI href;
        synchronized (anchors) {
            href = anchors.get(value);
        }
        if (href != null) {
            return new CharacterString(new AnchorType(href, value));
        }
        return new CharacterString(value);
    }
}
