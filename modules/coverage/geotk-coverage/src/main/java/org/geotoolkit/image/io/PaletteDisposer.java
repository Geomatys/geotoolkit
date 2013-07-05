/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.image.io;

import java.util.Set;
import java.awt.image.ColorModel;
import java.lang.ref.WeakReference;

import org.apache.sis.util.Disposable;
import org.geotoolkit.internal.ReferenceQueueConsumer;


/**
 * Allows garbage-collection of {@link Palette} after their index color model has been
 * garbage collected.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.13
 *
 * @since 2.1
 * @module
 */
final class PaletteDisposer extends WeakReference<ColorModel> implements Disposable {
    /**
     * The palette that created the color model.
     */
    private final Palette palette;

    /**
     * Creates the weak reference for the specified color model.
     */
    public PaletteDisposer(final Palette palette, final ColorModel colors) {
        super(colors, ReferenceQueueConsumer.DEFAULT.queue);
        this.palette = palette;
        final Set<Palette> protectedPalettes = palette.factory.protectedPalettes;
        synchronized (protectedPalettes) {
            protectedPalettes.add(palette);
        }
    }

    /**
     * Removes the palette from the set of protected ones.
     */
    @Override
    public void dispose() {
        final Set<Palette> protectedPalettes = palette.factory.protectedPalettes;
        synchronized (protectedPalettes) {
            protectedPalettes.remove(palette);
        }
    }
}
