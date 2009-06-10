/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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

import java.io.IOException;
import java.util.Map;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.util.logging.Level;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.BorderFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdesktop.swingx.JXLabel;

import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.WizardController;

import org.geotoolkit.image.jai.Registry;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.gui.swing.LoggingPanel;
import org.geotoolkit.gui.swing.image.MosaicChooser;
import org.geotoolkit.gui.swing.image.MosaicBuilderEditor;
import org.geotoolkit.gui.swing.image.MultiColorChooser;
import org.geotoolkit.internal.SwingUtilities;


/**
 * Guides the user through the steps of creating a set of {@linkplain Tile tiles}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 3.00
 * @module
 *
 * @todo Needs localization.
 */
public final class MosaicWizard extends AbstractWizard {
    /**
     * The default size for content panes.
     */
    private static final Dimension SIZE = new Dimension(800, 400);

    /**
     * The ID of the chooser for the input mosaic, which is the first step in the wizard.
     */
    static final String SELECT = "Select";

    /**
     * The ID for the editor of the layout of the mosaic to create.
     */
    static final String LAYOUT = "Layout";

    /**
     * The ID for the panel selecting the colors to make transparent.
     */
    static final String COLORS = "Colors";

    /**
     * The ID for the panel asking confirmation.
     */
    static final String CONFIRM = "Confirm";

    /**
     * {@code true} if the input mosaic changed.  This is set to {@code true} if the
     * first panel is revisited, in order to tells the second panel that it needs to
     * configure itself for a new input mosaic.
     * <p>
     * If the user is navigating back from the third panel, then the second panel is
     * not changed.
     */
    private boolean inputChanged;

    /**
     * Creates a new wizard.
     */
    public MosaicWizard() {
        super("Geotoolkit Pyramid Builder", new String[] {
            SELECT,
            LAYOUT,
            COLORS,
            CONFIRM
        }, new String[] {
            "Select source tiles",
            "Define pyramid tiling",
            "Remove opaque border",
            "Confirm"
        });
    }

    /**
     * Creates a panel that represents a named step in the wizard.
     *
     * @param controller The object which controls whether the Next/Finish buttons in the wizard are enabled.
     * @param id         The name of the step, one of the array of steps passed in the constructor.
     * @param settings   A Map containing settings from earlier steps in the wizard.
     * @return The component that should be displayed in the center of the wizard.
     */
    @Override
    protected JComponent createPanel(final WizardController controller, final String id, final Map settings) {
        JComponent component;
        if (id.equals(SELECT)) {
            // -------------------------------------------------------------------
            //     Panel 1:  Select source tiles
            // -------------------------------------------------------------------
            final class Chooser extends MosaicChooser implements ChangeListener {
                Chooser() {
                    addChangeListener(this);
                    stateChanged(null); // Force the call to controller.setProblem("..."),
                }

                private static final long serialVersionUID = -6696539336904269650L;
                @Override public void stateChanged(final ChangeEvent event) {
                    final TileManager[] tiles = getSelectedTiles();
                    final String problem;
                    switch (tiles.length) {
                        case 0:  problem = "At least one tile must be selected."; break;
                        case 1:  problem = null; break; // We can process
                        default: problem = "The selected tiles can not make a single mosaic."; break;
                    }
                    controller.setProblem(problem);
                }
            }
            component = new Chooser();
            addSetting(settings, SELECT, component);
        } else if (id.equals(LAYOUT)) {
            // -------------------------------------------------------------------
            //     Panel 2:  Define pyramid tiling
            // -------------------------------------------------------------------
            @SuppressWarnings("serial")
            final class Editor extends MosaicBuilderEditor {
                Editor() {
                }

                Editor(final TileManager... input) throws IOException {
                    super(input);
                }

                /** Invoked when the values in the form changed. */
                @Override protected void plotEfficiency(final long delay) {
                    controller.setProblem("Calculation in progress...");
                    super.plotEfficiency(delay);
                }

                /** Invoked on success. */
                @Override public void done(final TileManager output) {
                    super.done(output);
                    controller.setProblem(null);
                }

                /** Invoked on failure - can't move to the next step. */
                @Override public void failed(final Throwable exception) {
                    super.failed(exception);
                    controller.setProblem(exception.getLocalizedMessage());
                }
            }
            final MosaicChooser chooser = (MosaicChooser) settings.get(SELECT);
            try {
                component = new Editor(chooser.getSelectedTiles());
            } catch (IOException exception) {
                component = new Editor();
                controller.setProblem(exception.toString());
            }
            addSetting(settings, LAYOUT, component);
        } else if (id.equals(COLORS)) {
            // -------------------------------------------------------------------
            //     Panel 3:  Remove opaque border
            // -------------------------------------------------------------------
            component = new MultiColorChooser();
            addSetting(settings, COLORS, component);
        } else {
            // -------------------------------------------------------------------
            //     Panel 4:  Confirm
            // -------------------------------------------------------------------
            final LoggingPanel logging = new LoggingPanel("org.geotoolkit.image.io.mosaic");
            logging.setColumnVisible(LoggingPanel.Column.LOGGER, false);
            logging.setColumnVisible(LoggingPanel.Column.CLASS,  false);
            logging.setColumnVisible(LoggingPanel.Column.METHOD, false);
            logging.setColumnVisible(LoggingPanel.Column.LEVEL,  false);
            logging.getHandler().setLevel(Level.FINE); // The level used by MosaicImageWriter.
            final JXLabel label = new JXLabel("The wizard has now enough informations for " +
                    "creating the mosaic. Press \"Finish\" to confirm.");
            label.setLineWrap(true);
            final JPanel panel = new JPanel(new BorderLayout());
            panel.add(logging, BorderLayout.CENTER);
            panel.add(label, BorderLayout.SOUTH);
            component = panel;
            addSetting(settings, CONFIRM, logging);
        }
        component.setPreferredSize(SIZE);
        component.setBorder(BorderFactory.createEmptyBorder(6, 15, 9, 15));
        return component;
    }

    /**
     * Invoked when a panel is being revisited one more time.
     *
     * @param id         The name of the step, one of the array of steps passed in the constructor.
     * @param controller The object which controls whether the Next/Finish buttons in the wizard are enabled.
     * @param settings   A Map containing settings from earlier steps in the wizard.
     * @param panel      The panel being recycled.
     */
    @Override
    protected void recycleExistingPanel(final String id, final WizardController controller,
            final Map settings, final JComponent panel)
    {
        if (id.equals(SELECT)) {
            inputChanged = true;
        } else if (id.equals(LAYOUT)) {
            if (inputChanged) {
                inputChanged = false;
                final MosaicChooser chooser = (MosaicChooser) settings.get(SELECT);
                final MosaicBuilderEditor editor = (MosaicBuilderEditor) panel;
                try {
                    editor.initializeForTiles(chooser.getSelectedTiles());
                } catch (IOException exception) {
                    controller.setProblem(exception.toString());
                    return;
                }
            }
        }
        controller.setProblem(null);
    }

    /**
     * Invoked when the user cancel the wizard.
     *
     * @param  settings The settings provided by the user.
     * @return {@code true} in all cases, for allowing cancelation.
     */
    @Override
    public boolean cancel(final Map settings) {
        final LoggingPanel logging = (LoggingPanel) settings.get(CONFIRM);
        if (logging != null) {
            logging.dispose();
        }
        return super.cancel(settings);
    }

    /**
     * Invoked when the user finished to go through wizard steps.
     *
     * @param  settings The settings provided by the user.
     * @return The object which will create the mosaic.
     */
    @Override
    protected Object finish(final Map settings) {
        return new MosaicCreator();
    }

    /**
     * Displays this wizard.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.setLookAndFeel(MosaicWizard.class, "main");
        Registry.setDefaultCodecPreferences();
        final MosaicWizard wizard = new MosaicWizard();
        WizardDisplayer.showWizard(wizard.createWizard());
        System.exit(0);
    }
}
