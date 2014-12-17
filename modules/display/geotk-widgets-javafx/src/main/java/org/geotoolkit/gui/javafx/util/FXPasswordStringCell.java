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

import java.security.MessageDigest;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.PasswordField;

/**
 *
 * @author Samuel Andrés (Geomatys)
 */
public class FXPasswordStringCell<S> extends FXStringCell<S>{
    
    private final PasswordField field = new PasswordField();
    private final MessageDigest messageDigest;

    public FXPasswordStringCell() {
        this(null);
    }

    public FXPasswordStringCell(final MessageDigest messageDigest) {
        setGraphic(field);
        setAlignment(Pos.CENTER);
        setContentDisplay(ContentDisplay.CENTER);
        this.messageDigest=messageDigest;
    }
    
    private String digest(final String toEncrypt){
        if(messageDigest==null) return toEncrypt;
        else {
            return new String(messageDigest.digest((toEncrypt).getBytes()));
        }
    }

    @Override
    public void terminateEdit() {
        commitEdit(field.getText());
    }

    @Override
    public void startEdit() {
        String value = (String) getItem();
        field.setText(value);
        super.startEdit();
        setText(null);
        setGraphic(field);
        field.requestFocus();
    }

    @Override
    public void commitEdit(Object newValue) {
        super.commitEdit(digest((String) newValue));
    }
}
