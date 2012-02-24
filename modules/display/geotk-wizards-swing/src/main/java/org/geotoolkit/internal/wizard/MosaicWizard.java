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

import org.netbeans.spi.wizard.WizardController;

import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.gui.swing.LoggingPanel;
import org.geotoolkit.gui.swing.image.MosaicChooser;
import org.geotoolkit.gui.swing.image.MosaicBuilderEditor;
import org.geotoolkit.gui.swing.image.MultiColorChooser;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.Wizards;


/**
 * Guides the user through the steps of creating a set of
 * {@linkplain org.geotoolkit.image.io.mosaic.Tile tiles}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @version 3.11
 *
 * @since 3.00
 * @module
 */
public final class MosaicWizard extends AbstractWizard {
    /**
     * The default size for content panes.
     */
    private static final Dimension SIZE = new Dimension(800, 400);

    /**
     * The ID of the chooser for the input mosaic, which is the first step in the wizard.
     */
    static final String SELECT = "SELECT";

    /**
     * The ID for the editor of the layout of the mosaic to create.
     */
    static final String LAYOUT = "LAYOUT";

    /**
     * The ID for the panel selecting the colors to make transparent.
     */
    static final String COLORS = "COLORS";

    /**
     * The ID for the panel asking confirmation.
     */
    static final String CONFIRM = "CONFIRM";

    /**
     * The last line displayed in the last panel.
     */
    private static final String CONFIRM_LABEL = "CONFIRM_LABEL";

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
        super(Wizards.format(Wizards.Keys.MOSAIC_TITLE), new String[] {
            SELECT,
            LAYOUT,
            COLORS,
            CONFIRM
        }, new String[] {
            Wizards.format(Wizards.Keys.SELECT_SOURCE_TILES),
            Wizards.format(Wizards.Keys.DEFINE_PYRAMID_TILING),
            Wizards.format(Wizards.Keys.REMOVE_OPAQUE_BORDER),
            Vocabulary.format(Vocabulary.Keys.CONFIRM)
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
    @SuppressWarnings("rawtypes")
    protected JComponent createPanel(final WizardController controller, final String id, final Map settings) {
        JComponent component;
        switch (id) {
            // -------------------------------------------------------------------
            //     Panel 1:  Select source tiles
            // -------------------------------------------------------------------
            case SELECT: {
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
                            case 0:  problem = Wizards.format(Wizards.Keys.NO_SELECTED_TILES); break;
                            case 1:  problem = null; break; // We can process
                            default: problem = Wizards.format(Wizards.Keys.INVALID_MOSAIC_LAYOUT); break;
                        }
                        controller.setProblem(problem);
                    }
                }
                component = new Chooser();
                addSetting(settings, SELECT, component);
                break;
            }
            // -------------------------------------------------------------------
            //     Panel 2:  Define pyramid tiling
            // -------------------------------------------------------------------
            case LAYOUT: {
                @SuppressWarnings("serial")
                final class Editor extends MosaicBuilderEditor {
                    Editor() {
                    }

                    Editor(final TileManager... input) throws IOException {
                        super(input);
                    }

                    /** Invoked when the values in the form changed. */
                    @Override protected void plotEfficiency(final long delay) {
                        controller.setProblem(Wizards.format(Wizards.Keys.CALCULATION_PROGESSING));
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
                break;
            }
            // -------------------------------------------------------------------
            //     Panel 3:  Remove opaque border
            // -------------------------------------------------------------------
            case COLORS: {
                component = new MultiColorChooser();
                addSetting(settings, COLORS, component);
                break;
            }
            // -------------------------------------------------------------------
            //     Panel 4:  Confirm
            // -------------------------------------------------------------------
            case CONFIRM: {
                final LoggingPanel logging = new LoggingPanel("org.geotoolkit.image.io.mosaic");
                logging.setColumnVisible(LoggingPanel.Column.LOGGER, false);
                logging.setColumnVisible(LoggingPanel.Column.CLASS,  false);
                logging.setColumnVisible(LoggingPanel.Column.METHOD, false);
                logging.setColumnVisible(LoggingPanel.Column.LEVEL,  false);
                logging.getHandler().setLevel(Level.FINE); // The level used by MosaicImageWriter.
                final JXLabel label = new JXLabel(Wizards.format(Wizards.Keys.ENOUGH_INFORMATION));
                label.setLineWrap(true);
                final JPanel panel = new JPanel(new BorderLayout());
                panel.add(logging, BorderLayout.CENTER);
                panel.add(label, BorderLayout.SOUTH);
                component = panel;
                addSetting(settings, CONFIRM, logging);
                addSetting(settings, CONFIRM_LABEL, label);
                break;
            }
            default: {
                throw new IllegalArgumentException(id); // Should never happen.
            }
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
    @SuppressWarnings("rawtypes")
    protected void recycleExistingPanel(final String id, final WizardController controller,
            final Map settings, final JComponent panel)
    {
        switch (id) {
            case SELECT: {
                inputChanged = true;
                break;
            }
            case LAYOUT: {
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
                break;
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
    @SuppressWarnings("rawtypes")
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
    @SuppressWarnings("rawtypes")
    protected Object finish(final Map settings) {
        final Wizards resources = Wizards.getResources(null);
        ((JXLabel) settings.get(CONFIRM_LABEL)).setText(resources.getMenuLabel(Wizards.Keys.CREATING_MOSAIC));
        return new MosaicCreator();
    }
}
