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
package org.geotoolkit.internal.wizard;

import java.awt.Color;
import java.util.Map;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;

import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.gui.swing.LoggingPanel;
import org.geotoolkit.gui.swing.image.MosaicChooser;
import org.geotoolkit.gui.swing.image.MosaicBuilderEditor;
import org.geotoolkit.gui.swing.image.MultiColorChooser;
import org.geotoolkit.image.io.mosaic.MosaicBuilder;
import org.geotoolkit.image.io.mosaic.TileWritingPolicy;


/**
 * The object that create a mosaic once {@link MosaicWizard} finished to collect all information.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
final class MosaicCreator extends DeferredWizardResult {
    /**
     * Creates a new {@code MosaicCreator}.
     */
    MosaicCreator() {
    }

    /**
     * Performs the creation of the mosaic.
     */
    @Override
    public void start(final Map settings, final ResultProgressHandle progress) {
        progress.setBusy("Creating the mosaic"); // TODO: use setProgress(...) instead.
        final TileManager tiles;
        try {
            final TileManager[] inputs  = ((MosaicChooser)       settings.get(MosaicWizard.SELECT)).getSelectedTiles();
            final MosaicBuilder builder = ((MosaicBuilderEditor) settings.get(MosaicWizard.LAYOUT)).getMosaicBuilder();
            final Color[]       colors  = ((MultiColorChooser)   settings.get(MosaicWizard.COLORS)).getSelectedColors();
            // TODO: needs an API for giving the colors to the MosaicBuilder.
            tiles = builder.createTileManager(inputs, TileWritingPolicy.WRITE_NEWS_NONEMPTY);
        } catch (Throwable exception) {
            progress.failed(exception.getLocalizedMessage(), false);
            return;
        } finally {
            ((LoggingPanel) settings.get(MosaicWizard.CONFIRM)).dispose();
        }
        progress.finished(tiles);
    }
}
