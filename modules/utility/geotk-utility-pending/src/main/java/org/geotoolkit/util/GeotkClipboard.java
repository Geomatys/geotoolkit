/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * A java local clipboard which can be used by widgets.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public final class GeotkClipboard {
    
    public static final Clipboard INSTANCE = new Clipboard("GeotkClipboard");

    private GeotkClipboard() {}

    /**
     * Convinient method to acces the system clipboard string value.
     * 
     * @return String in the clipboard, null is clipboard is empty or content
     * can not be represented as a string.
     */
    public static String getSystemClipboardValue() {
        final Clipboard systemboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        if (systemboard == null) {
            return null;
        }

        try {
            final Transferable trs = systemboard.getContents(null);
            if(trs == null){
                return null;
            }
            
            boolean hasTransferableText = trs.isDataFlavorSupported(DataFlavor.stringFlavor);
            if(hasTransferableText){
                return (String)trs.getTransferData(DataFlavor.stringFlavor);
            }
            
        } catch (IllegalStateException ex) {
            //not important exception
        } catch (UnsupportedFlavorException ex) {
            //not important exception
        } catch (IOException ex) {
            //not important exception
        }

        return null;
    }

    /**
     * Push the given string in the system clipboard.
     * @param value : if null this call will not have any effect.
     */
    public static void setSystemClipboardValue(final String value){
        if(value == null)return;
        
        final Clipboard systemboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        if (systemboard == null) {
            return;
        }
        
        final StringSelection item = new StringSelection(value);        
        systemboard.setContents(item,item);
    }
            
}
