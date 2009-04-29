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

import java.io.IOException;
import java.util.Map;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.BorderFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.api.wizard.WizardDisplayer;
import org.netbeans.spi.wizard.WizardController;
import org.netbeans.spi.wizard.WizardPanelProvider;

import org.geotoolkit.image.jai.Registry;
import org.geotoolkit.image.io.mosaic.TileManager;
import org.geotoolkit.gui.swing.image.MosaicChooser;
import org.geotoolkit.gui.swing.image.MosaicBuilderEditor;


/**
 * Guides the user through the steps of creating a set of {@linkplain Tile tiles}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 *
 * @todo Needs localization.
 */
public final class MosaicWizard extends WizardPanelProvider {
    /**
     * The default size for content panes.
     */
    private static final Dimension SIZE = new Dimension(800, 400);

    /**
     * The mosaic chooser, which is the first step in the wizard.
     */
    private MosaicChooser chooser;

    /**
     * Creates a new wizard.
     */
    public MosaicWizard() {
        super("Geotoolkit Pyramid Builder", new String[] {
            "Select",
            "Layout"
        }, new String[] {
            "Select source tiles",
            "Define pyramid tiling"
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
        final JComponent component;
        if (id.equals("Select")) {
            final class Chooser extends MosaicChooser implements ChangeListener {
                private static final long serialVersionUID = -6696539336904269650L;
                @Override public void stateChanged(final ChangeEvent event) {
                    final TileManager[] tiles = chooser.getSelectedTiles();
                    final String problem;
                    switch (tiles.length) {
                        case 0:  problem = "At least one tile must be selected."; break;
                        case 1:  problem = null; break; // We can process
                        default: problem = "The selected tiles can not make a single mosaic."; break;
                    }
                    controller.setProblem(problem);
                }
            }
            final Chooser c;
            component = chooser = c = new Chooser();
            chooser.addChangeListener(c);
            c.stateChanged(null); // Force the call to controller.setProblem("..."),
        } else {
            final MosaicBuilderEditor editor;
            component = editor = new MosaicBuilderEditor();
            try {
                editor.initializeForTiles(chooser.getSelectedTiles());
            } catch (IOException exception) {
                controller.setProblem(exception.toString());
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
    protected void recycleExistingPanel(final String id, final WizardController controller,
            final Map settings, final JComponent panel)
    {
        if (id.equals("Layout")) {
            final MosaicBuilderEditor editor = (MosaicBuilderEditor) panel;
            try {
                editor.initializeForTiles(chooser.getSelectedTiles());
                controller.setProblem(null); // In case IOException was previously set.
            } catch (IOException exception) {
                controller.setProblem(exception.toString());
            }
        }
    }

    /**
     * Displays this wizard.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        Registry.setDefaultCodecPreferences();
        final MosaicWizard wizard = new MosaicWizard();
        WizardDisplayer.showWizard(wizard.createWizard());
        System.exit(0);
    }
}
