/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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


/**
 * A {@link StringAdapter} which can substitute text by anchors. At the difference of most
 * adapters provided in {@code org.geotoolkit.internal.jaxb} packages, this adapter is
 * <em>configurable</em>. It must be created explicitly with a map of bindings between
 * labels and URNs, and the configured adapter must be given to the mashaller as below:
 *
 * {@preformat java
 *     marshaller.setAdapter(charSequenceAdapter.string);
 * }
 *
 * @author Guilhem Legal (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @see AnchoredCharSequenceAdapter
 *
 * @since 3.00
 * @module
 */
public final class AnchoredStringAdapter extends StringAdapter {
    /**
     * Binds string labels with URNs.
     */
    private final AnchoredCharSequenceAdapter anchors;

    /**
     * For JAXB compliance.
     */
    private AnchoredStringAdapter() {
        anchors = AnchoredCharSequenceAdapter.INSTANCE;
    }

    /**
     * Creates an adapter.
     */
    AnchoredStringAdapter(final AnchoredCharSequenceAdapter anchors) {
        this.anchors = anchors;
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
        return anchors.marshal(value);
    }
}
