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
import java.awt.Component;
import java.awt.FlowLayout;
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
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.util.RandomStyleFactory;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;


/**
 * Property panel
 * 
 * @author Johann Sorel
 * @module pending
 */
public class JPropertyDialog extends JDialog{
        
    private JButton apply = new JButton(MessageBundle.getString("property_apply"));
    private JButton revert = new JButton(MessageBundle.getString("property_revert"));
    private JButton close = new JButton(MessageBundle.getString("property_close"));
    
    private JTabbedPane tabs = new JTabbedPane();    
    private PropertyPane activePanel = null;    
    private ArrayList<PropertyPane> panels = new ArrayList<PropertyPane>();
    
    /** Creates a new instance of ASDialog */
    private JPropertyDialog(boolean modal,boolean app, boolean rev, boolean clo) {
        super();
        setModal(modal);
        setTitle(MessageBundle.getString("property_properties"));
        
        JToolBar bas = new JToolBar();
        bas.setFloatable(false);
        bas.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        if(app)bas.add(apply);
        if(rev)bas.add(revert);
        if(clo)bas.add(close);
        
        apply.setIcon(IconBundle.getInstance().getIcon("16_apply"));
        revert.setIcon(IconBundle.getInstance().getIcon("16_reload"));
        close.setIcon(IconBundle.getInstance().getIcon("16_close"));
        
        
        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                activePanel = (PropertyPane)tabs.getSelectedComponent();
            }
        });
        
        apply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(PropertyPane edit : panels){
                    edit.apply();
                }
            }
        });
        
        revert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(activePanel != null)
                    activePanel.reset();
            }
        });
        
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(PropertyPane edit : panels){
                    edit.apply();
                }
                dispose();
            }
        });
        
        setLayout( new BorderLayout());
        add(BorderLayout.SOUTH,bas);
        
    }
    
    public void addEditPanel(PropertyPane pan){
        panels.add(pan);        
        tabs.addTab(pan.getTitle(),pan.getIcon(),pan.getComponent(),pan.getToolTip());        
    }
    

    @Override
    public void setVisible(boolean b) {
        if(b){
            if(panels.size()>1){
                add(BorderLayout.CENTER,tabs);
            }else if(panels.size() == 1){
                add(BorderLayout.CENTER,(JComponent)panels.get(0));
            }
        }      
        super.setVisible(b);
    }
    
    public static void showDialog(List<PropertyPane> lst, Object target){
        showDialog(lst,target, true);
    }

    public static void showDialog(List<PropertyPane> lst, Object target, boolean modal){
        JPropertyDialog dia = new JPropertyDialog(modal,true,true,true);

        for(PropertyPane pro : lst){
            pro.setTarget(target);
            dia.addEditPanel(pro);
        }

        dia.setSize(700,500);
        dia.setLocationRelativeTo(null);
        dia.setVisible(true);
    }


    public static Symbolizer showSymbolizerDialog(Symbolizer symbol, Object target){
        return showSymbolizerDialog(symbol, false, target);
    }

    public static Symbolizer showSymbolizerDialog(Symbolizer symbol, boolean allowTypeChange, Object target){

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
                        pane.setSymbolizer(RandomStyleFactory.createPointSymbolizer());
                    }else if(o == LineSymbolizer.class){
                        pane.setSymbolizer(RandomStyleFactory.createLineSymbolizer());
                    }else if(o == PolygonSymbolizer.class){
                        pane.setSymbolizer(RandomStyleFactory.createPolygonSymbolizer());
                    }
                }
            });

            container.add(BorderLayout.NORTH,box);
        }

        final JPropertyDialog dia = new JPropertyDialog(true,false,false,true);
        dia.setContentPane(container);
        dia.setSize(700,500);
        dia.setLocationRelativeTo(null);
        dia.setVisible(true);

        return pane.getSymbolizer();
    }

    
}
