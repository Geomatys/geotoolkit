/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.gui.swing.style;

import org.geotoolkit.gui.swing.style.symbolizer.JLineSymbolizerPane;
import com.sun.java.swing.plaf.gtk.GTKLookAndFeel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Path2D;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.geotoolkit.gui.swing.misc.JOptionDialog;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.MapLayer;
import org.openide.awt.DropDownButtonFactory;

/**
 * A Two state editor.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class JTwoStateEditor<T> extends StyleElementEditor<T> {

    private static final ImageIcon ICON_SIMPLE = IconBundle.getIcon("16_simple_style");
    private static final ImageIcon ICON_ADVANCED = IconBundle.getIcon("16_statefull_disable");
    
    private final StyleElementEditor<T> simple;
    private final StyleElementEditor<T> advanced;
    private StyleElementEditor<T> current;
    
    private final JLayeredPane layeredpane = new JLayeredPane();
    private final JButton typeselect;
    private final JPanel typeSelectPane = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0)){

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            final Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.WHITE);
            
            final int width = getWidth();
            final int theight = typeselect.getHeight() +4;
            final int twidth = typeselect.getWidth() +4;
            final Path2D path = new Path2D.Double();
            path.moveTo(width, 0);
            path.lineTo(width, theight);
            path.lineTo(width-twidth, theight);
            path.lineTo(width-twidth-theight, 0);
            path.lineTo(width, 0);
            path.closePath();
            
            g2d.fill(path);
        }
        
    };
    
    public JTwoStateEditor(final StyleElementEditor<T> simple, final StyleElementEditor<T> advanced) {
        super(simple.getEditedClass());
        setLayout(new BorderLayout());
        this.simple = simple;
        this.advanced = advanced;
        this.current = simple;
        
        final JPopupMenu menu = new JPopupMenu();
        menu.add(new AbstractAction(MessageBundle.getString("style.twostate.simple"), ICON_SIMPLE) {
            @Override
            public void actionPerformed(ActionEvent e) {
                layeredpane.remove(simple);
                layeredpane.remove(advanced);
                layeredpane.add(simple,new Integer(0));
                typeselect.setIcon(ICON_SIMPLE);
                current = simple;
            }
        });
        menu.add(new AbstractAction(MessageBundle.getString("style.twostate.advanced"), ICON_ADVANCED) {
            @Override
            public void actionPerformed(ActionEvent e) {
                layeredpane.remove(simple);
                layeredpane.remove(advanced);
                layeredpane.add(advanced,new Integer(0));
                typeselect.setIcon(ICON_ADVANCED);
                current = advanced;
            }
        });
        
        
        typeselect = DropDownButtonFactory.createDropDownButton(ICON_SIMPLE, menu);
        typeselect.setBorderPainted(false);
        typeselect.setContentAreaFilled(false);
        typeselect.setMargin(new Insets(0, 0, 0, 0));
        typeSelectPane.add(typeselect);
        typeSelectPane.setOpaque(false);
        layeredpane.add(simple,new Integer(0));
        layeredpane.add(typeSelectPane,new Integer(1));
        
        add(layeredpane,BorderLayout.CENTER);
        
        addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                simple.setSize(getSize());
                advanced.setSize(getSize());
                typeSelectPane.setSize(getSize());
                layeredpane.revalidate();
                layeredpane.repaint();
            }

            @Override
            public void componentMoved(ComponentEvent e) {}
            @Override
            public void componentShown(ComponentEvent e) {}
            @Override
            public void componentHidden(ComponentEvent e) {}
        });
    }

    @Override
    public Dimension getPreferredSize() {
        final Dimension dim1 = simple.getPreferredSize();
        final Dimension dim2 = advanced.getPreferredSize();
        return new Dimension(Math.max(dim1.width, dim2.width), Math.max(dim1.height, dim2.height));
    }

    @Override
    public void setLayer(MapLayer layer) {
        super.setLayer(layer);
        simple.setLayer(layer);
        advanced.setLayer(layer);
    }
    
    @Override
    public void parse(T target) {
        simple.parse(target);
        advanced.parse(target);
    }

    @Override
    public T create() {
        return current.create();
    }
    
    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new GTKLookAndFeel());
        
        final org.geotoolkit.gui.swing.propertyedit.styleproperty.simple.JLineSymbolizerSimple simple = 
            new org.geotoolkit.gui.swing.propertyedit.styleproperty.simple.JLineSymbolizerSimple();
        final JLineSymbolizerPane advanced = new JLineSymbolizerPane();
        
        JOptionDialog.show(null, new JTwoStateEditor(simple,advanced), JOptionPane.OK_CANCEL_OPTION);
    }
    
}
