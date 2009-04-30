/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2009, Open Source Geospatial Foundation (OSGeo)
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

package org.geotoolkit.internal.setup;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import org.geotoolkit.internal.io.Installation;

import org.geotoolkit.resources.Descriptions;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Vocabulary;
import static java.awt.GridBagConstraints.*;


/**
 * The panel displaying a configuration form for the current epsg database.
 *
 * @author Johann Sorel (Geomatys)
 * @version 3.0
 *
 * @since 3.0
 * @module
 */
public class EPSGPanel extends JPanel{

    private final List<String> urlSamples = new ArrayList<String>();
    private final JLabel guiWarning = new JLabel();
    private final JComboBox guiURL = new JComboBox();
    private final JTextField guiUser = new JTextField();
    private final JTextField guiSchema = new JTextField();
    private final JPasswordField guiPassword = new JPasswordField();
    private final JButton guiApply = new JButton(Vocabulary.format(Vocabulary.Keys.APPLY));

    /**
     * Creates the panel.
     */
    EPSGPanel(final Vocabulary resources){
        super(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        urlSamples.add("");
        urlSamples.add("jdbc:postgresql://localhost:5432/Referencing");
        urlSamples.add("jdbc:derby:"+System.getProperty("user.home").replace(File.separatorChar, '/') +"/Referencing");

        guiURL.setEditable(true);

        GridBagConstraints c = new GridBagConstraints();
        c.insets.left=3;
        c.insets.right=3;
        c.gridx=0; c.gridy=0;
        c.gridwidth=REMAINDER;
        add(guiWarning,c);

        c.fill=BOTH;
        c.gridwidth=1;
        c.gridx=1; c.gridy=1; c.weightx=0;
        add(new JLabel(Vocabulary.format(Vocabulary.Keys.URL)+":"),c);
        c.gridx=2; c.weightx=1;
        add(guiURL,c);

        c.gridx=1; c.gridy=2; c.weightx=0;
        add(new JLabel(Vocabulary.format(Vocabulary.Keys.SCHEMA)+":"),c);
        c.gridx=2; c.weightx=1;
        add(guiSchema,c);

        c.gridx=1; c.gridy=3; c.weightx=0;
        add(new JLabel(Vocabulary.format(Vocabulary.Keys.IDENTIFIER)+":"),c);
        c.gridx=2; c.weightx=1;
        add(guiUser,c);

        c.gridx=1; c.gridy=4; c.weightx=0;
        add(new JLabel(Vocabulary.format(Vocabulary.Keys.PASSWORD)+":"),c);
        c.gridx=2; c.weightx=1;
        add(guiPassword,c);

        c.gridx=2; c.gridy=5;
        c.fill=NONE; c.anchor=EAST;
        add(guiApply,c);

        guiApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });

        refresh();
    }

    private void refresh(){
        final File directory = Installation.EPSG.directory(true);
        final File configFile = new File(directory, "DataSource.properties");
        if (configFile.isFile()){
            guiWarning.setVisible(false);

            final Properties props = new Properties();
            try {
                props.load(new FileInputStream(configFile));
            } catch (IOException ex) {
                String message = Errors.format(Errors.Keys.CANT_READ_$1, "DataSource.properties");
                JOptionPane.showMessageDialog(this, message, "", JOptionPane.ERROR_MESSAGE);
            }

            urlSamples.set(0, props.getProperty("URL"));

            guiURL.setModel(new DefaultComboBoxModel(urlSamples.toArray()));
            guiPassword.setText(props.getProperty("password"));
            guiUser.setText(props.getProperty("user"));
            guiSchema.setText(props.getProperty("schema"));
        }else{
            guiWarning.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
            guiWarning.setText(Descriptions.format(Descriptions.Keys.NO_EPSG_USE_JAVADB));
            guiWarning.setVisible(true);
            guiURL.setModel(new DefaultComboBoxModel(urlSamples.toArray()));
            guiPassword.setText("password");
            guiUser.setText("user");
            guiSchema.setText("epsg");
        }
    }

    private void save(){
        final File directory = Installation.EPSG.directory(true);
        final File configFile = new File(directory, "DataSource.properties");

        if (!configFile.exists() || configFile.canWrite()){
            final String url = guiURL.getSelectedItem().toString().trim();
            final String user = guiUser.getText().trim();
            final String password = String.valueOf(guiPassword.getPassword()).trim();
            final String schema = guiSchema.getText().trim();

            final Properties props = new Properties();
            props.setProperty("URL", url);
            props.setProperty("user", user);
            props.setProperty("password", password);
            props.setProperty("schema", schema);
            try {
                props.store(new FileOutputStream(configFile), null);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, Errors.format(Errors.Keys.CANT_WRITE_$1, "DataSource.properties"),"", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(this, Errors.format(Errors.Keys.CANT_WRITE_$1, "DataSource.properties"), "", JOptionPane.ERROR_MESSAGE);
        }

        refresh();
    }

}
