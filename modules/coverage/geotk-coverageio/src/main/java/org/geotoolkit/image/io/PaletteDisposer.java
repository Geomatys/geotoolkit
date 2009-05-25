/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007-2009, Open Source Geospatial Foundation (OSGeo)
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

import org.geotoolkit.internal.ReferenceQueueConsumer;


/**
 * Allows garbage-collection of {@link Palette} after their index color model has been
 * garbage collected.
 *
 * @author Martin Desruisseaux (IRD)
 * @version 3.00
 *
 * @since 2.1
 * @module
 */
final class PaletteDisposer extends ReferenceQueueConsumer<ColorModel> {
    /**
     * A weak reference to a color model created by a palette.
     */
    static final class Reference extends WeakReference<ColorModel> {
        /**
         * Starts the disposer thread when the {@link Reference} are about to be created.
         */
        private static final PaletteDisposer DISPOSER = new PaletteDisposer();

        /**
         * The palette that created the color model.
         */
        final Palette palette;

        /**
         * Creates the weak reference for the specified color model.
         */
        public Reference(final Palette palette, final ColorModel colors) {
            super(colors, DISPOSER.queue);
            this.palette = palette;
            final Set<Palette> protectedPalettes = palette.factory.protectedPalettes;
            synchronized (protectedPalettes) {
                protectedPalettes.add(palette);
            }
        }
    }

    /**
     * Creates a new disposer thread.
     */
    private PaletteDisposer() {
        super("PaletteDisposer");
        setDaemon(true);
        start();
    }

    /**
     * Removes the palette from the set of protected ones.
     */
    @Override
    protected void process(final java.lang.ref.Reference<? extends ColorModel> reference) {
        final Reference ref = (Reference) reference;
        final Set<Palette> protectedPalettes = ref.palette.factory.protectedPalettes;
        synchronized (protectedPalettes) {
            protectedPalettes.remove(ref.palette);
        }
    }
}
