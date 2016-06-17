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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder;
import javafx.beans.property.adapter.JavaBeanFloatPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanLongPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.JavaBeanProperty;
import javafx.beans.property.adapter.JavaBeanStringPropertyBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.geotoolkit.image.palette.PaletteFactory;
import org.geotoolkit.internal.Loggers;
import org.geotoolkit.style.interval.DefaultIntervalPalette;
import org.geotoolkit.style.interval.DefaultRandomPalette;
import org.geotoolkit.style.interval.Palette;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class FXUtilities {

    
    public static final PaletteFactory PF = PaletteFactory.getDefault();
    public static final List<Object> PALETTES = new ArrayList<>();
    public static final List<Object> PALETTES_NAMED = new ArrayList<>();

    static{
        PALETTES.add(new DefaultRandomPalette());
        final Set<String> paletteNames = PF.getAvailableNames();

        for (String palName : paletteNames) {
            PALETTES.add(palName);
            PALETTES_NAMED.add(palName);
        }

        double[] fractions = new double[]{
                -3000,
                -1500,
                -0.1,
                +0,
                556,
                1100,
                1600,
                2200,
                3000};
        java.awt.Color[] colors = new java.awt.Color[]{
                new java.awt.Color(9, 9, 145, 255),
                new java.awt.Color(31, 131, 224, 255),
                new java.awt.Color(182, 240, 240, 255),
                new java.awt.Color(5, 90, 5, 255),
                new java.awt.Color(150, 200, 150, 255),
                new java.awt.Color(190, 150, 20, 255),
                new java.awt.Color(100, 100, 50, 255),
                new java.awt.Color(200, 210, 220, 255),
                new java.awt.Color(255, 255, 255, 255),
        };
        PALETTES.add(new DefaultIntervalPalette(fractions,colors));
    }
    
    public static final Font FONTAWESOME;
    
    static {
        FONTAWESOME = Font.loadFont(FXUtilities.class.getResource("/org/geotoolkit/font/fontawesome-webfont.ttf").toExternalForm(), 16);
    }
    
    private FXUtilities() {}
    
    public static <T> JavaBeanProperty<T> beanProperty(Object candidate, String propertyName, Class<T> dataType){
        try {
            if(Boolean.class.equals(dataType)){
                return (JavaBeanProperty<T>)JavaBeanBooleanPropertyBuilder.create().bean(candidate).name(propertyName).build();
            }else if(Integer.class.equals(dataType)){
                return (JavaBeanProperty<T>)JavaBeanIntegerPropertyBuilder.create().bean(candidate).name(propertyName).build();
            }else if(Long.class.equals(dataType)){
                return (JavaBeanProperty<T>)JavaBeanLongPropertyBuilder.create().bean(candidate).name(propertyName).build();
            }else if(Float.class.equals(dataType)){
                return (JavaBeanProperty<T>)JavaBeanFloatPropertyBuilder.create().bean(candidate).name(propertyName).build();
            }else if(Double.class.equals(dataType)){
                return (JavaBeanProperty<T>)JavaBeanDoublePropertyBuilder.create().bean(candidate).name(propertyName).build();
            }else if(String.class.equals(dataType)){
                return (JavaBeanProperty<T>)JavaBeanStringPropertyBuilder.create().bean(candidate).name(propertyName).build();
            }else{
                return JavaBeanObjectPropertyBuilder.create().bean(candidate).name(propertyName).build();
            }
        } catch (NoSuchMethodException ex) {
            throw new IllegalArgumentException(ex.getMessage(),ex);
        }
    }
    
    /**
     * Recursive loop on node and it's children, calling the consumer for each node.
     * 
     * @param node visited node
     * @param consumer called for each scene node
     */
    public static void visit(Node node, Consumer<Node> consumer){
        consumer.accept(node);
        if(node instanceof Parent){
            final Parent parent = (Parent) node;
            for(Node child : parent.getChildrenUnmodifiable()){
                visit(child, consumer);
            }
        }
    }
    
    public static java.awt.Color toSwingColor(Color fxColor){
        final float r = (float) fxColor.getRed();
        final float g = (float) fxColor.getGreen();
        final float b = (float) fxColor.getBlue();
        final float a = (float) fxColor.getOpacity();
        return new java.awt.Color(r,g,b,a);
    }
    
    public static Color toFxColor(java.awt.Color swingColor){
        final double r = (double)swingColor.getRed() / 255.0;
        final double g = (double)swingColor.getGreen() / 255.0;
        final double b = (double)swingColor.getBlue() / 255.0;
        final double a = (double)swingColor.getAlpha() / 255.0;
        return new Color(r, g, b, a);
    }
        
    /**
     * Expand all nodes from root to given node
     * @param candidate node to expand including all it's parents
     */
    public static void expandRootToItem(TreeItem candidate) {
        if (candidate != null) {
            expandRootToItem(candidate.getParent());
            if (!candidate.isLeaf()) {
                candidate.setExpanded(true);
            }
        }
    }
    
    /**
     * Expand all nodes child node recursively
     * @param candidate candidate node to expand including all it's children
     */
    public static void expandAll(TreeItem candidate) {
        if (candidate != null) {
            candidate.setExpanded(true);
            for(Object ti : candidate.getChildren()){
                expandAll((TreeItem)ti);
            }
        }
    }
    
    /**
     * Test if a TreeItem is a parent of another TreeItem.
     * 
     * @param parent candidate parent
     * @param child candidate child
     * @return true if parent is a parent of child
     */
    public static boolean isParent(TreeItem parent, TreeItem child){
        TreeItem candidate = child;
        while(candidate!=null){
            if(candidate==parent){
                return true;
            }
            candidate = candidate.getParent();
        }
        return false;
    }
    
    public static Image createPalettePreview(Object paletteValue, Dimension size){
        return createPalettePreview(paletteValue, size, true);
    }

    public static Image createPalettePreview(Object paletteValue, Dimension size, boolean interpolate){
        final BufferedImage image = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = image.createGraphics();

        if (paletteValue != null) {
            final Rectangle rect = new Rectangle(size);
            rect.grow(-2, -2);

            if (paletteValue instanceof String) {
                try {
                    final String paletteName = String.valueOf(paletteValue);
                    final org.geotoolkit.image.palette.Palette palette = PF.getPalette(paletteName, 10);
                    final RenderedImage img = interpolate ? palette.getLegend(rect.getSize()) : palette.getImage(rect.getSize());
                    ((Graphics2D) g).drawRenderedImage(img, new AffineTransform(1,0,0,1,2,2));
                } catch (IOException ex) {
                    Loggers.JAVAFX.log(Level.WARNING, ex.getMessage(), ex);
                }
            } else if (paletteValue instanceof Palette) {
                final Palette palette = (Palette) paletteValue;
                palette.render((Graphics2D) g, rect, interpolate);
            }
        }

        return SwingFXUtils.toFXImage(image, null);
    }
    
    /**
     * Hide table or treetable header.
     * 
     * @param view Table or TreeTable view
     */
    public static void hideTableHeader(final Control view){
        if( !(view instanceof TreeTableView || view instanceof TableView)){
            throw new IllegalArgumentException("Control must be a TreeTableView or TableView but was : "+view.getClass());
        }
        
        //hide the header
        view.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                Pane header = (Pane)view.lookup("TableHeaderRow");
                if(header!=null && header.isVisible()) {
                  header.setMaxHeight(0);
                  header.setMinHeight(0);
                  header.setPrefHeight(0);
                  header.setVisible(false);
                  header.setManaged(false);
                }
            }
        });
    }
    
    /**
     * Bug in javafx ?
     *
     * Something wrong in javafx treetableview selection.
     * When removing a treetiem which was selection, the previous node gets
     * selected but is not highlighted, we can't unselected it.
     *
     * @param tree treetable view
     * @return List of TreeItem
     */
    public static List<TreeItem> getSelectionItems(TreeTableView tree){
        final List<TreeItem> selection = new ArrayList<>();
        for(Object i : tree.getSelectionModel().getSelectedCells()){
            final TreeTablePosition ttp = (TreeTablePosition) i;
            final TreeItem ti = tree.getTreeItem(ttp.getRow());
            if(ti!=null && !selection.contains(ti)) selection.add(ti);
        }
        return selection;
    }

    /**
     * Create a view of given collections.
     * view content are concatenated.
     * Unlike FXCollections.concat this list is not editable but has events.
     *
     * @param listes lists to sequence
     * @return sequenced list of all given lists
     */
    public static ObservableList view(ObservableList ... listes){
        return new ViewList(listes);
    }

    private static final class ViewList extends ObservableListBase implements ListChangeListener{

        private final ObservableList[] listes;

        public ViewList(ObservableList ... listes) {
            this.listes = listes;

            for(ObservableList lst : listes){
                lst.addListener(this);
            }
        }

        @Override
        public Object get(int index) {
            for(int i=0;i<listes.length;i++){
                int size = listes[i].size();
                if(size<=index){
                    index -= size;
                }else{
                    return listes[i].get(index);
                }
            }
            throw new ArrayIndexOutOfBoundsException(index);
        }

        @Override
        public int size() {
            int size = 0;
            for (ObservableList liste : listes) {
                size += liste.size();
            }
            return size;
        }

        private int getOffset(ObservableList lst){
            int size = 0;
            for (ObservableList liste : listes) {
                if(lst==liste) break;
                size += liste.size();
            }
            return size;
        }

        @Override
        public void onChanged(Change c) {
            final int offset = getOffset(c.getList());

            beginChange();
            while (c.next()) {
                if (c.wasPermutated()) {
                    //permutate
                    beginChange();
                    final int[] perms = new int[c.getTo()-c.getFrom()];
                    for (int i = c.getFrom(),k=0; i < c.getTo(); ++i,k++) {
                        perms[k] = c.getPermutation(i);
                    }
                    nextPermutation(offset+c.getFrom(), offset+c.getTo(), perms);
                    endChange();
                } else if (c.wasUpdated()) {
                    //update item
                    beginChange();
                    nextUpdate(offset+c.getFrom());
                    endChange();
                } else {
                    beginChange();
                    if(c.wasUpdated()){
                        throw new UnsupportedOperationException("Update events not supported.");
                    }else if(c.wasAdded()){
                        nextAdd(offset+c.getFrom(), offset+c.getTo());
                    }else if(c.wasRemoved()){
                        nextReplace(offset+c.getFrom(), offset+c.getTo(), c.getRemoved());
                    }
                    endChange();
                }
            }
            endChange();
        }

    }

}
