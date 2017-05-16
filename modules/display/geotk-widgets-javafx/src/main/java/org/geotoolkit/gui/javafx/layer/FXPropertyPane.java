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

package org.geotoolkit.gui.javafx.layer;

import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import org.geotoolkit.gui.javafx.style.FXStyleElementController;
import org.geotoolkit.gui.javafx.style.FXStyleElementEditor;
import org.geotoolkit.gui.javafx.util.FXOptionDialog;
import org.geotoolkit.map.MapLayer;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXPropertyPane extends BorderPane{

    public FXPropertyPane() {
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    }

    public String getTitle(){
        return "";
    }

    public Image getIcon(){
        return null;
    }

    public boolean init(Object candidate){
        return true;
    }


    public static Symbolizer showSymbolizerDialog(Object parent, final Symbolizer symbol, final Object target){
        return showSymbolizerDialog(parent, symbol, false, target);
    }

    public static Symbolizer showSymbolizerDialog(Object parent, final Symbolizer symbol, final boolean allowTypeChange, final Object target){

        final BorderPane container = new BorderPane();

        final FXStyleElementController editor = FXStyleElementEditor.findEditor(symbol);
        editor.valueProperty().set(symbol);
        if(target instanceof MapLayer){
            editor.setLayer((MapLayer)target);
        }
        container.setCenter(editor);

//        if(allowTypeChange){
//            final JComboBox box = new JComboBox(
//                    new Object[]{
//                        PointSymbolizer.class,
//                        LineSymbolizer.class,
//                        PolygonSymbolizer.class
//                    });
//
//            if(symbol instanceof PointSymbolizer){
//                box.setSelectedItem(PointSymbolizer.class);
//            }else if(symbol instanceof LineSymbolizer){
//                box.setSelectedItem(LineSymbolizer.class);
//            }else if(symbol instanceof PolygonSymbolizer){
//                box.setSelectedItem(PolygonSymbolizer.class);
//            }
//
//            box.setRenderer(new DefaultListCellRenderer(){
//                @Override
//                public Component getListCellRendererComponent(JList jlist, Object o, int i, boolean bln, boolean bln1) {
//                    final JLabel lbl = (JLabel) super.getListCellRendererComponent(jlist, o, i, bln, bln1);
//                    if(o == PointSymbolizer.class){
//                        lbl.setText(MessageBundle.getString("symbol_point"));
//                    }else if(o == LineSymbolizer.class){
//                        lbl.setText(MessageBundle.getString("symbol_line"));
//                    }else if(o == PolygonSymbolizer.class){
//                        lbl.setText(MessageBundle.getString("symbol_polygon"));
//                    }
//                    return lbl;
//                }
//            });
//
//            box.addItemListener(new ItemListener() {
//                @Override
//                public void itemStateChanged(ItemEvent ie) {
//                    Object o = box.getSelectedItem();
//                    if(o == PointSymbolizer.class){
//                        pane.setSymbolizer(RandomStyleBuilder.createRandomPointSymbolizer());
//                    }else if(o == LineSymbolizer.class){
//                        pane.setSymbolizer(RandomStyleBuilder.createRandomLineSymbolizer());
//                    }else if(o == PolygonSymbolizer.class){
//                        pane.setSymbolizer(RandomStyleBuilder.createRandomPolygonSymbolizer());
//                    }
//                }
//            });
//
//            container.add(BorderLayout.NORTH,box);
//        }

        FXOptionDialog.showOkCancel(parent, editor, "Editor", true);

        return (Symbolizer) editor.valueProperty().get();
    }


}
