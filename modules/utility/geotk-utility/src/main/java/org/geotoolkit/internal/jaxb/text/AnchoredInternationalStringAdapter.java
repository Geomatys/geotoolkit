/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2011, Open Source Geospatial Foundation (OSGeo)
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

import org.opengis.util.InternationalString;
import org.geotoolkit.internal.jaxb.metadata.FreeText;
import org.geotoolkit.internal.jaxb.metadata.InternationalStringAdapter;


/**
 * An {@link InternationalStringAdapter} which can substitute text by anchors. At the difference
 * of most adapters provided in {@code org.geotoolkit.internal.jaxb} packages, this adapter is
 * <em>configurable</em>. It must be created explicitly with a map of bindings between
 * labels and URNs, and the configured adapter must be given to the mashaller as below:
 *
 * {@preformat java
 *     AnchoredCharSequenceAdapter charSequenceAdapter = ...
 *     marshaller.setAdapter(charSequenceAdapter.international);
 * }
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.13
 *
 * @see AnchoredCharSequenceAdapter
 *
 * @since 3.00
 * @module
 */
public final class AnchoredInternationalStringAdapter extends InternationalStringAdapter {
    /**
     * Binds string labels with URNs.
     */
    private final AnchoredCharSequenceAdapter anchors;

    /**
     * For JAXB compliance.
     */
    private AnchoredInternationalStringAdapter() {
        anchors = AnchoredCharSequenceAdapter.INSTANCE;
    }

    /**
     * Creates an adapter.
     */
    AnchoredInternationalStringAdapter(final AnchoredCharSequenceAdapter anchors) {
        this.anchors = anchors;
    }

    /**
     * Converts an {@link InternationalString} to an object to be formatted into
     * a XML stream. JAXB invokes automatically this method at marshalling time.
     *
     * @param  value The string value.
     * @return The adapter for the string.
     */
    @Override
    public CharacterString marshal(final InternationalString value) {
        if (value instanceof AnchorType) {
            return new CharacterString((AnchorType) value);
        }
        final FreeText ft = FreeText.create(value);
        if (ft != null) {
            return ft;
        }
        return anchors.marshal(value);
    }
}
