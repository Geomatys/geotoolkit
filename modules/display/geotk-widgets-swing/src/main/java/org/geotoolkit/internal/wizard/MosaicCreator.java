/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.wizard;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;
import java.util.logging.Level;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOWriteProgressListener;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.gui.swing.LoggingPanel;
import org.geotoolkit.gui.swing.image.MosaicChooser;
import org.geotoolkit.gui.swing.image.MosaicBuilderEditor;
import org.geotoolkit.gui.swing.image.MultiColorChooser;
import org.geotoolkit.internal.swing.ExceptionMonitor;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.image.io.mosaic.MosaicBuilder;
import org.geotoolkit.image.io.mosaic.MosaicImageWriteParam;
import org.geotoolkit.image.io.mosaic.TileWritingPolicy;
import org.geotoolkit.resources.Wizards;


/**
 * The object that create a mosaic once {@link MosaicWizard} finished to collect all information.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.03
 *
 * @since 3.00
 * @module
 */
final class MosaicCreator extends DeferredWizardResult implements IIOWriteProgressListener {
    /**
     * Where to report progress on the wizard component.
     */
    private ResultProgressHandle progress;

    /**
     * Creates a new {@code MosaicCreator}.
     */
    MosaicCreator() {
    }

    /**
     * Performs the creation of the mosaic.
     */
    @Override
    @SuppressWarnings("rawtypes")
    public void start(final Map settings, final ResultProgressHandle progress) {
        this.progress = progress;
        progress.setBusy(Wizards.format(Wizards.Keys.CreatingMosaic));
        final TileManager tiles;
        try {
            final TileManager[] inputs  = ((MosaicChooser)       settings.get(MosaicWizard.SELECT)).getSelectedTiles();
            final MosaicBuilder builder = ((MosaicBuilderEditor) settings.get(MosaicWizard.LAYOUT)).getMosaicBuilder();
            final Color[]       colors  = ((MultiColorChooser)   settings.get(MosaicWizard.COLORS)).getSelectedColors();
            final MosaicImageWriteParam param = new MosaicImageWriteParam();
            if (colors.length != 0) {
                param.setOpaqueBorderFilter(colors);
            }
            param.setTileWritingPolicy(TileWritingPolicy.WRITE_NEWS_NONEMPTY);
            Logging.getLogger(MosaicBuilder.class).setLevel(Level.FINE);
            builder.setLogLevel(Level.INFO);
            builder.listeners().addIIOWriteProgressListener(this);
            try {
                tiles = builder.writeFromInput(inputs, param);
            } finally {
                builder.listeners().removeIIOWriteProgressListener(this);
            }
            /*
             * At this point, we finished creating the mosaic. Serializes
             * the TileManager object which describe the new mosaic.
             */
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                   new File(builder.getTileDirectory(), TileManager.SERIALIZED_FILENAME)))) {
                out.writeObject(tiles);
            }
        } catch (Throwable exception) { // NOSONAR: We also want to catch OutOfMemoryError.
            progress.failed(exception.getLocalizedMessage(), false);
            ExceptionMonitor.show((Component) settings.get(MosaicWizard.CONFIRM), exception);
            return;
        } finally {
            ((LoggingPanel) settings.get(MosaicWizard.CONFIRM)).dispose();
        }
        progress.finished(tiles);
    }

    /**
     * Invoked when the mosaic is about to be written.
     */
    @Override
    public void imageStarted(ImageWriter source, int imageIndex) {
        progress.setProgress(Wizards.format(Wizards.Keys.CreatingMosaic), 0, 100);
    }

    /**
     * Invoked after a set of output tiles has been written.
     */
    @Override
    public void imageProgress(ImageWriter source, float percentageDone) {
        progress.setProgress((int) percentageDone, 100);
    }

    /**
     * Ignored. The call to {@link ResultProgressHandle#finished} will be done at
     * the end of the {@link #start} method.
     */
    @Override public void imageComplete(ImageWriter source) {
    }

    /** Ignored. */
    @Override public void thumbnailStarted(ImageWriter source, int imageIndex, int thumbnailIndex) {
    }

    /** Ignored. */
    @Override public void thumbnailProgress(ImageWriter source, float percentageDone) {
    }

    /** Ignored. */
    @Override public void thumbnailComplete(ImageWriter source) {
    }

    /** Ignored. */
    @Override public void writeAborted(ImageWriter source) {
    }
}
