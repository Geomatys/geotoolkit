/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012 Geomatys
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import org.geotoolkit.gui.swing.util.WrapLayout;
import org.geotoolkit.map.MapLayer;
import org.opengis.style.Symbolizer;

/**
 * Component ables to display/select symbol *
 * @author Fabien Rétif (Geomatys)
 */
public class JBankView <T> extends StyleElementEditor<T> {

    private MapLayer layer = null;
    /**
     * Selected Mark object
     */
    private T selectedObject = null;
    /**
     * HashMap which stores all components of the form
     */
    private final Map<T, JPreview> componentMap = new HashMap<T, JPreview>();

    /**
     * Creates new form JBankView
     */
    public JBankView(Class<T> clazz) {
        super(clazz);
        initComponents();
        guiCandidates.setLayout(new WrapLayout());

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void setLayer(final MapLayer layer) {
        this.layer = layer;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MapLayer getLayer() {
        return layer;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void parse(final T mark) {

        if (mark != null) {

            selectedObject = mark;

            JComponent myJComponent = componentMap.get(mark);

            if (myJComponent != null) {

                myJComponent.setBorder(new LineBorder(Color.DARK_GRAY));
            }
        }
    }

    /**
     * Creates mark with default stroke, default fill and the selected form
     * {@inheritDoc }
     */
    @Override
    public T create() {

        return (T) selectedObject;
    }
    
    public void clearSelectedTarget() {
        for (JComponent c : componentMap.values()) {
            c.setBorder(null);
        }

        selectedObject = null;
    }

    public void setCandidates(List<T> candidates) {
            
        componentMap.clear();
        guiCandidates.removeAll();
        
         MouseListener mouseListener = new MouseListener() {
            public void mousePressed(MouseEvent me) {
            }

            public void mouseReleased(MouseEvent me) {
            }

            public void mouseEntered(MouseEvent evt) {

                 JPreview label = (JPreview) evt.getSource();   
                 
                 if(!label.getTarget().equals(selectedObject)) {
                    label.setBorder(new LineBorder(Color.LIGHT_GRAY));
                }
            }

            public void mouseExited(MouseEvent evt) {

                 JPreview label = (JPreview) evt.getSource();  

                 if(!label.getTarget().equals(selectedObject)) {
                    label.setBorder(null);
                }
            }

            public void mouseClicked(MouseEvent evt) {
                
                JPreview label = (JPreview) evt.getSource(); 
                
                for(JComponent c : componentMap.values()) {
                    c.setBorder(null);
                }
                
                selectedObject  = (T) label.getTarget();  
                label.setBorder(new LineBorder(Color.DARK_GRAY));               

            }
        };
                

        for (T m : candidates) {

            // Creates preview label
            JPreview previewLabel = new JPreview();
            previewLabel.setPreferredSize(new Dimension(40, 40));
            previewLabel.parse(m);

            //Add action listener to button
            previewLabel.addMouseListener(mouseListener);

            JPanel pane = new JPanel(new BorderLayout());
            pane.add(BorderLayout.CENTER, previewLabel);
            JLabel label = new JLabel();
            if (m instanceof Symbolizer) {
                Symbolizer symbol = (Symbolizer) m;

                if (symbol.getName() != null && !symbol.getName().isEmpty()) {
                    label.setText(symbol.getName());
                } else if (symbol.getDescription() != null && symbol.getDescription().getTitle() != null && !symbol.getDescription().getTitle().toString().isEmpty()) {
                    label.setText(symbol.getDescription().getTitle().toString());
                } else {
                    label.setText("Unnamed");
                }
            } else {
                label.setText(m.getClass().getSimpleName());
            }
            pane.add(BorderLayout.SOUTH, label);
            pane.setOpaque(false);

            guiCandidates.add(pane);

            componentMap.put(m, previewLabel);
        }
        
        guiCandidates.updateUI();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        guiCandidates = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setViewportView(guiCandidates);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel guiCandidates;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
  
}
