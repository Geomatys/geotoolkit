/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 3 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

package org.geotoolkit.gui.javafx.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.geotoolkit.internal.GeotkFX;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXOptionDialog {
    
    public static boolean showOkCancel(Object owner, Node content, String title, boolean modal){
        
        final Dialog dialog = new Dialog(owner, title);
        dialog.setContent(content);
        dialog.setIconifiable(false);        
        final AtomicBoolean state = new AtomicBoolean(false);        
        dialog.getActions().addAll(new OkAction(dialog, state), new CancelAction(dialog, state));
        
        dialog.show();
        return state.get();
    }
    
    private static class OkAction extends Action implements Consumer<ActionEvent>{
        private final Dialog dialog;
        private final AtomicBoolean state;

        public OkAction(Dialog dialog, AtomicBoolean state) {
            super(GeotkFX.getString(FXOptionDialog.class,"ok"));
            setEventHandler(this);
            this.dialog = dialog;
            this.state = state;
        }
        
        @Override
        public void accept(ActionEvent event) {
            state.set(true);
            dialog.hide();
        }
    }
    
    private static class CancelAction extends Action implements Consumer<ActionEvent>{
        private final Dialog dialog;
        private final AtomicBoolean state;

        public CancelAction(Dialog dialog, AtomicBoolean state) {
            super(GeotkFX.getString(FXOptionDialog.class,"cancel"));
            setEventHandler(this);
            this.dialog = dialog;
            this.state = state;
        }
        
        @Override
        public void accept(ActionEvent event) {
            state.set(false);
            dialog.hide();
        }
    }
    
    
}
