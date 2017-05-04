/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.gui.javafx.util;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.stage.DirectoryChooser;

/**
 * Allow an user to choose a directory using a text field or a system file browser.
 * @author Alexis Manin (Geomatys)
 */
public class FXDirectoryTextField extends AbstractPathTextField {

    @Override
    protected String chooseInputContent() {
        final DirectoryChooser chooser = new DirectoryChooser();
        String strPath = getText();
        if (strPath != null && !strPath.isEmpty()) {
            final Path tmp = Paths.get(strPath);
            if (Files.isDirectory(tmp)) {
                chooser.setInitialDirectory(tmp.toFile());
            } else if (Files.isDirectory(tmp.getParent())) {
                chooser.setInitialDirectory(tmp.getParent().toFile());
            }
        }
        File returned = chooser.showDialog(null);
        if (returned == null) {
            return null;
        } else {
            return returned.getAbsolutePath();
        }
    }

    @Override
    protected URI getURIForText(String inputText) throws Exception {
        return new URI(inputText);
    }

}
