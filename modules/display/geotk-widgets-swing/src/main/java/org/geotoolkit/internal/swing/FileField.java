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
package org.geotoolkit.internal.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.geotoolkit.resources.Vocabulary;


/**
 * A field for a file or a directory, with a button for bringing the file chooser.
 * By default this component is for directories.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.00
 *
 * @since 3.00
 * @module
 */
public class FileField extends JComponent implements ActionListener {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -4365216544457047082L;

    /**
     * The directory as a text field.
     */
    private final JTextField file = new JTextField();

    /**
     * The file chooser, created only when needed.
     */
    private transient JFileChooser chooser;

    /**
     * Builds a field for a directory, with a button for bringing the file chooser.
     *
     * @param locale The locale to use for creating the panel.
     * @param label An optional label to put before the directory field, or {@code null}.
     * @param vertical {@code true} if the button should be on the bottom instead than on the right side.
     */
    public FileField(final Locale locale, final String label, final boolean vertical) {
        setLayout(new BorderLayout());
        if (label != null) {
            add(new JLabel(label), vertical ? BorderLayout.BEFORE_FIRST_LINE : BorderLayout.BEFORE_LINE_BEGINS);
        }
        add(file, BorderLayout.CENTER);
        final JButton open = new JButton(vertical ?
                Vocabulary.getResources(locale).getMenuLabel(Vocabulary.Keys.Choose) : "...");
        add(open, vertical ? BorderLayout.AFTER_LAST_LINE : BorderLayout.AFTER_LINE_ENDS);
        open.addActionListener(this);
    }

    /**
     * Sets the file or directory to be initially proposed.
     *
     * @param file The file or directory to set, or {@code null} if none.
     */
    public void setFile(final File file) {
        this.file.setText(file != null ? file.getPath() : "");
    }

    /**
     * Returns the file given by the user.
     *
     * @return The file or directory given by the user, or the current directory if none.
     */
    public File getFile() {
        String text = file.getText();
        if (text == null || ((text = text.trim()).length()) == 0) {
            text = ".";
        }
        return new File(text);
    }

    /**
     * Invoked when the user click on the button. This method popup the file chooser.
     *
     * @param event The event (ignored).
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        if (chooser == null) {
            chooser = new JFileChooser(file.getText());
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        } else {
            chooser.setCurrentDirectory(new File(file.getText()));
        }
        if (chooser.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION) {
            setFile(chooser.getSelectedFile());
        }
    }
}
