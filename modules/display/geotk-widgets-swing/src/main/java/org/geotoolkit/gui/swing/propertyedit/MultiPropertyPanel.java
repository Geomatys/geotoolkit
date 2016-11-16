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
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import org.geotoolkit.gui.swing.util.JImagePane;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 * Multiproperty panel.
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @module
 */
public abstract class MultiPropertyPanel extends JPanel implements PropertyPane, PropertyChangeListener {

    private final Map<String,List<Object>> panels = new LinkedHashMap<>();
    private final JXTaskPaneContainer guiMenus = new JXTaskPaneContainer();
    private final Map<String,JXTaskPane> guiGroups = new HashMap<>();
    private final JImagePane guiPreview = new JImagePane();
    private final JXTaskPane preview = new JXTaskPane();

    private PropertyPane active = null;
    private Object currentTarget = null;

    /**
     * Creates new form MultiPropertyPanel
     */
    public MultiPropertyPanel() {
        super();
        initComponents();
        preview.getContentPane().setLayout(new BorderLayout());
        preview.add(BorderLayout.CENTER,guiPreview);

        guiMenus.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        guiTypesPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        guiTypesPane.add(guiMenus, BorderLayout.CENTER);
        typeScroll.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

    }

    public void addPropertyPanel(final String groupName, final PropertyPane panel) {
        List<Object> lst = panels.get(groupName);
        if(lst==null){
            lst = new ArrayList<>();
            panels.put(groupName, lst);
        }
        lst.add(panel);
    }
        
    public void addAction(final String groupName, final Action action) {
        List<Object> lst = panels.get(groupName);
        if(lst==null){
            lst = new ArrayList<>();
            panels.put(groupName, lst);
        }
        lst.add(action);
    }

    public boolean setSelectedPropertyPanel(final PropertyPane panel) {

        if(active!=null){
            if (active instanceof Component) {
                ((Component)active).removePropertyChangeListener(this);
            }
            remove(active.getComponent());
            active = null;
            revalidate();
            repaint();
        }

        if (panel != null) {
            if (active instanceof Component) {
                ((Component)active).addPropertyChangeListener(this);
            }
            active = panel;
            active.setTarget(currentTarget);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                        public void run() {
                            final JComponent panComp = (JComponent) panel.getComponent();
                            add(BorderLayout.CENTER,panComp);
                            revalidate();
                            repaint();
                        }
                    });

            final Image img = panel.getPreview();
            guiPreview.setVisible(img!=null);
            guiPreview.setImage(img);
            if(img != null){
                guiPreview.setPreferredSize(new Dimension(100, 140));
                guiPreview.revalidate();
                guiPreview.repaint();
                preview.setEnabled(true);
                preview.setCollapsed(false);
            }else{
                guiPreview.setPreferredSize(new Dimension(1, 1));
                preview.setEnabled(false);
                preview.setCollapsed(true);
            }

            return true;
        }

        return false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        typeScroll = new JScrollPane();
        guiTypesPane = new JPanel();

        setLayout(new BorderLayout());

        typeScroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        typeScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        typeScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        guiTypesPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        guiTypesPane.setLayout(new BorderLayout());
        typeScroll.setViewportView(guiTypesPane);

        add(typeScroll, BorderLayout.WEST);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public void setTarget(final Object target) {

        this.currentTarget = target;
        //select only panels which handle this target
        for(JXTaskPane tp : guiGroups.values()){
            guiMenus.remove(tp);
        }
        guiMenus.remove(preview);
        guiGroups.clear();

        PropertyPane selected = null;
        for (Entry<String,List<Object>> entry : panels.entrySet()) {
            final String groupName = entry.getKey();
            final List<Object> panels = entry.getValue();
            for(final Object candidate : panels){

                JXTaskPane task = guiGroups.get(groupName);
                if(task==null){
                    task = new JXTaskPane();
                    task.setTitle(groupName);
                    task.setIcon(null);
                    task.setCollapsed(true);
                    task.setSpecial(true);
                    task.setEnabled(false);
                    guiGroups.put(groupName, task);
                    guiMenus.add(task);
                }

                final Action act;
                if(candidate instanceof Action){
                    act = (Action) candidate;
                    task.setSpecial(false);
                    task.setCollapsed(false);
                    task.setEnabled(true);
                }else {
                    final PropertyPane panel = (PropertyPane) candidate;
                    if(selected==null){
                        selected = panel;
                    }

                    act = new AbstractAction() {
                        {
                            putValue(Action.NAME, panel.getTitle());
                            putValue(Action.SHORT_DESCRIPTION, panel.getToolTip());
                            putValue(Action.SMALL_ICON, panel.getIcon());
                        }
                        public void actionPerformed(ActionEvent e) {
                          setSelectedPropertyPanel(panel);
                        }
                      };

                    if(panel.canHandle(target)){
                        act.setEnabled(true);
                        panel.setTarget(target);
                        //activate the group task pane
                        task.setCollapsed(false);
                        task.setSpecial(false);
                        task.setEnabled(true);
                    }else{
                        act.setEnabled(false);
                        panel.setTarget(null);
                    }
                }

                task.add(act);
            }
        }

        guiMenus.add(preview);

        setSelectedPropertyPanel(selected);
    }

    @Override
    public void apply() {
        for (List<Object> lst : panels.values()) {
            for(Object candidate : lst){
                if(!(candidate instanceof PropertyPane)) continue;
                final PropertyPane pan = (PropertyPane) candidate;

                if (pan.equals(active)) {
                    pan.apply();
                } else {
                    pan.reset();
                }
            }
        }
    }

    @Override
    public void reset() {
        for (List<Object> lst : panels.values()) {

            for(Object candidate : lst){
                if(!(candidate instanceof PropertyPane)) continue;
                final PropertyPane pan = (PropertyPane) candidate;
                pan.reset();
            }
        }
    }

    @Override
    public abstract String getTitle();

    @Override
    public abstract ImageIcon getIcon();

    @Override
    public abstract String getToolTip();

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PropertyPane.RELOAD.equals(evt.getPropertyName())) {
            reset();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JPanel guiTypesPane;
    private JScrollPane typeScroll;
    // End of variables declaration//GEN-END:variables

}
