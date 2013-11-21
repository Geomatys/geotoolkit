/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.propertyedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.geotoolkit.gui.swing.propertyedit.styleproperty.JSymbolizerStylePanel;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.style.RandomStyleBuilder;
import org.geotoolkit.util.SwingUtilities;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.Symbolizer;


/**
 * Property panel
 *
 * @author Johann Sorel
 * @module pending
 */
public class JPropertyPane extends JPanel{

    private final JButton apply = new JButton(MessageBundle.getString("property_apply"));
    private final JButton revert = new JButton(MessageBundle.getString("property_revert"));
    private final JButton close = new JButton(MessageBundle.getString("property_close"));

    private final JTabbedPane tabs = new JTabbedPane();
    private final ArrayList<PropertyPane> panels = new ArrayList<>();
    private final JToolBar bas = new JToolBar();
    private PropertyPane activePanel = null;

    public JPropertyPane(final boolean app, final boolean rev) {
        this(app,rev,false,null);
    }

    public JPropertyPane(final boolean app, final boolean rev, final boolean clo, final JDialog dialog) {
        super(new BorderLayout());

        bas.setFloatable(false);
        bas.setLayout(new FlowLayout(FlowLayout.RIGHT));

        if(app)bas.add(apply);
        if(rev)bas.add(revert);
        if(clo)bas.add(close);

        apply.setIcon(IconBuilder.createIcon(FontAwesomeIcons.ICON_CHECK_SIGN, 16, new Color(0, 100, 0)));
        revert.setIcon(IconBuilder.createIcon(FontAwesomeIcons.ICON_UNDO, 16, new Color(255, 150, 50)));
        close.setIcon(IconBuilder.createIcon(FontAwesomeIcons.ICON_CHEVRON_SIGN_RIGHT, 16, new Color(0, 100, 0)));


        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                activePanel = (PropertyPane)tabs.getSelectedComponent();
            }
        });

        apply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(activePanel != null){
                    activePanel.apply();
                }
            }
        });

        revert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(activePanel != null){
                    activePanel.reset();
                }
            }
        });

        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        add(BorderLayout.SOUTH,bas);
    }

    public void addEditPanel(final PropertyPane pan){
        panels.add(pan);
        tabs.addTab(pan.getTitle(),pan.getIcon(),pan.getComponent(),pan.getToolTip());

        removeAll();
        if(panels.size()>1){
            add(BorderLayout.CENTER,tabs);
        }else if(panels.size() == 1){
            final JComponent comp = (JComponent)panels.get(0);
            add(BorderLayout.CENTER,comp);
            activePanel = (PropertyPane) comp;
        }
        add(BorderLayout.SOUTH,bas);
        revalidate();
        repaint();
    }

    public void addApplyListener(ActionListener listener){
        apply.addActionListener(listener);
    }

    public static void showDialog(Component parent, final List<PropertyPane> lst, final Object target){
        showDialog(parent,lst,target, true);
    }

    public static void showDialog(Component parent, final List<PropertyPane> lst, final Object target, final boolean modal){

        final Window window = SwingUtilities.windowForComponent(parent);
        final JDialog dia = new JDialog(window);
        dia.setModal(modal);
        dia.setTitle(MessageBundle.getString("property_properties"));
        //dia.setAlwaysOnTop(true);

        final JPropertyPane pane = new JPropertyPane(true,true,true,dia);
        dia.setContentPane(pane);

        for(PropertyPane pro : lst){
            pro.setTarget(target);
            pane.addEditPanel(pro);
        }

        dia.setSize(800,600);
        dia.setLocationRelativeTo(null);
        dia.setVisible(true);
    }


    public static Symbolizer showSymbolizerDialog(Component parent, final Symbolizer symbol, final Object target){
        return showSymbolizerDialog(parent, symbol, false, target);
    }

    public static Symbolizer showSymbolizerDialog(Component parent, final Symbolizer symbol, final boolean allowTypeChange, final Object target){

        final JPanel container = new JPanel(new BorderLayout());
        final JSymbolizerStylePanel pane = new JSymbolizerStylePanel();
        pane.setTarget(target);
        pane.setSymbolizer(symbol);
        container.add(BorderLayout.CENTER,pane);

        if(allowTypeChange){
            final JComboBox box = new JComboBox(
                    new Object[]{
                        PointSymbolizer.class,
                        LineSymbolizer.class,
                        PolygonSymbolizer.class
                    });

            if(symbol instanceof PointSymbolizer){
                box.setSelectedItem(PointSymbolizer.class);
            }else if(symbol instanceof LineSymbolizer){
                box.setSelectedItem(LineSymbolizer.class);
            }else if(symbol instanceof PolygonSymbolizer){
                box.setSelectedItem(PolygonSymbolizer.class);
            }

            box.setRenderer(new DefaultListCellRenderer(){
                @Override
                public Component getListCellRendererComponent(JList jlist, Object o, int i, boolean bln, boolean bln1) {
                    final JLabel lbl = (JLabel) super.getListCellRendererComponent(jlist, o, i, bln, bln1);
                    if(o == PointSymbolizer.class){
                        lbl.setText(MessageBundle.getString("symbol_point"));
                    }else if(o == LineSymbolizer.class){
                        lbl.setText(MessageBundle.getString("symbol_line"));
                    }else if(o == PolygonSymbolizer.class){
                        lbl.setText(MessageBundle.getString("symbol_polygon"));
                    }
                    return lbl;
                }
            });

            box.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent ie) {
                    Object o = box.getSelectedItem();
                    if(o == PointSymbolizer.class){
                        pane.setSymbolizer(RandomStyleBuilder.createRandomPointSymbolizer());
                    }else if(o == LineSymbolizer.class){
                        pane.setSymbolizer(RandomStyleBuilder.createRandomLineSymbolizer());
                    }else if(o == PolygonSymbolizer.class){
                        pane.setSymbolizer(RandomStyleBuilder.createRandomPolygonSymbolizer());
                    }
                }
            });

            container.add(BorderLayout.NORTH,box);
        }

        final Window window = SwingUtilities.windowForComponent(parent);
        final JDialog dia = new JDialog(window);
        dia.setModal(true);
        dia.setTitle(MessageBundle.getString("property_properties"));
        //dia.setAlwaysOnTop(true);
        dia.setSize(800,600);
        dia.setLocationRelativeTo(null);
        dia.setContentPane(container);
        dia.setVisible(true);

        return pane.getSymbolizer();
    }

}
