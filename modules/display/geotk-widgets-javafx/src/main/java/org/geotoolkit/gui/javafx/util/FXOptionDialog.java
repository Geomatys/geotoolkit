/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014-2015, Geomatys
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

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import org.controlsfx.control.action.Action;
import org.geotoolkit.internal.GeotkFX;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXOptionDialog {

    public static boolean showOkCancel(Object owner, Node content, String title, boolean modal){

        final Dialog dia = new Dialog();
        final DialogPane pane = new DialogPane();
        pane.getButtonTypes().add(ButtonType.OK);
        pane.getButtonTypes().add(ButtonType.CANCEL);
        pane.setContent(content);
        dia.setTitle(title);
        if(owner instanceof Node){
            final Window window = ((Node)owner).getScene().getWindow();
            dia.initOwner(window);
        }
        dia.setDialogPane(pane);
        final Optional<ButtonType> result = dia.showAndWait();

        return result.isPresent() && result.get() == ButtonType.OK;

//        if(result.isPresent() && result.get() == ButtonType.OK){
//            if(layerVisible){
//                return chooser.getSelectedLayers();
//            }else{
//                final Client store = chooser.getStore();
//                if(store == null){
//                    return Collections.EMPTY_LIST;
//                }else{
//                    return Collections.singletonList(store);
//                }
//            }
//        }else{
//            return Collections.EMPTY_LIST;
//        }
//
//        final Dialog dialog = new Dialog();
//        dialog.setContent(content);
//        dialog.setIconifiable(false);
//        final AtomicBoolean state = new AtomicBoolean(false);
//        dialog.getActions().addAll(new OkAction(dialog, state), new CancelAction(dialog, state));
//
//        dialog.show();
//        return state.get();
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
