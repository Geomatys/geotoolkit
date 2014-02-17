/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.gui.swing.util;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.geotoolkit.gui.swing.resource.IconBundle;

/**
 * Step panel, similar to what can be found on web application.
 * Step > subStep > subStep > ...
 * 
 * @author Johann Sorel (Geomatys)
 */
public class JStepPane extends JPanel{

    public static final class Step{
        
        private final String name;
        private final ImageIcon icon;
        private final JComponent component;

        public Step(String name, ImageIcon icon, JComponent component) {
            this.name = name;
            this.icon = icon;
            this.component = component;
        }
        
    }
    
    private final JToolBar guiTop = new JToolBar();
    private final JPanel guiCenter = new JPanel(new BorderLayout());
    private final List<Step> steps = new ArrayList<Step>();
    
    public JStepPane() {
        setLayout(new BorderLayout());        
        guiTop.setFloatable(false);        
        add(BorderLayout.NORTH,guiTop);
        add(BorderLayout.CENTER,guiCenter);
    }
    
    public void addStep(final Step step){
        steps.add(step);
        moveTo(step);
    }
    
    public void moveTo(Step step){
        if(step == null) return;
        
        final int index = steps.indexOf(step);
        for(int i=steps.size()-1; i>index; i--){
            steps.remove(i);
        }
        rebuildTop();
        
        
        guiCenter.removeAll();
        guiCenter.add(BorderLayout.CENTER,step.component);
        guiCenter.revalidate();
        guiCenter.repaint();
    }
    
    private void rebuildTop(){
        guiTop.removeAll();
        
        for(final Step step : steps){
            
            final AbstractAction act = new AbstractAction(step.name, step.icon) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    moveTo(step);
                }
            };
            act.putValue(AbstractAction.SHORT_DESCRIPTION, step.name);
            final JButton button = new JButton(act);
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            
            guiTop.add(button);
            guiTop.add(new JLabel(IconBundle.getIcon("24_step")));
        }
        
        guiTop.revalidate();
        guiTop.repaint();
    }
    
}
