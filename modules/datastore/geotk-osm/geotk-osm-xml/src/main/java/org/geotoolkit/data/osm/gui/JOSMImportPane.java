/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.data.osm.gui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JOSMImportPane extends JPanel{

    private final JOSMDBCreationPane guiCreatePane = new JOSMDBCreationPane();
    private final JOSMDBConfigPane guiConfigPane = new JOSMDBConfigPane();
    private final JOSMExtractTypePane guiExtractPane = new JOSMExtractTypePane();

    public JOSMImportPane(){
        super(new BorderLayout());
        final JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Configuration", guiConfigPane);
        tabs.addTab("Import", guiCreatePane);
        tabs.addTab("Compose", guiExtractPane);

        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                guiCreatePane.setDBParameters(guiConfigPane.getDBConnectionParameters());
                guiCreatePane.setOsmFile(guiConfigPane.getOSMFile());
                guiExtractPane.setDBParameters(guiConfigPane.getDBConnectionParameters());
            }
        });

        add(BorderLayout.CENTER,tabs);
    }


    public static void main(String[] args) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        JFrame frm = new JFrame("OSM Postgres import.");
        JOSMImportPane pane = new JOSMImportPane();
        frm.setContentPane(pane);
        frm.pack();
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);
    }

}
