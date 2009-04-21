/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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
import org.opengis.util.InternationalString;
import org.geotoolkit.util.DefaultInternationalString;
import org.geotoolkit.internal.jaxb.metadata.FreeText;
import org.geotoolkit.internal.jaxb.metadata.InternationalStringAdapter;


/**
 * An {@link InternationalStringAdapter} which can substitute text by anchors. At the difference
 * of most adapters provided in {@code org.geotoolkit.internal.jaxb} packages, this adapter is
 * <em>configurable</em>. It must be created explicitly with a map of bindings between
 * labels and URNs, and the configured adapter must be given to the mashaller as below:
 *
 * {@preformat java
 *     marshaller.setAdapter(new AnchoredInternationalStringAdapter(bindings));
 * }
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
public final class AnchoredInternationalStringAdapter extends InternationalStringAdapter {
    /**
     * Binds string labels with URNs.
     */
    private final Map<String,URI> anchors;

    /**
     * For JAXB compliance.
     */
    private AnchoredInternationalStringAdapter() {
        anchors = null;
    }

    /**
     * Creates a unitialized adapter.
     */
    AnchoredInternationalStringAdapter(final Map<String,URI> anchors) {
        this.anchors = anchors;
    }

    /**
     * Converts an {@link InternationalString} to an object to formatted into a
     * XML stream. JAXB invokes automatically this method at marshalling time.
     *
     * @param  value The string value.
     * @return The adapter for the string.
     */
    @Override
    public final CharacterString marshal(final InternationalString value) {
        if (value != null) {
            if (value instanceof DefaultInternationalString) {
                return new FreeText((DefaultInternationalString) value);
            }
            final String text = value.toString();
            if (text != null) {
                final URI href;
                synchronized (anchors) {
                    href = anchors.get(text);
                }
                if (href != null) {
                    return new CharacterString(new AnchorType(href, text));
                }
                return new CharacterString(text);
            }
        }
        return null;
    }
}
