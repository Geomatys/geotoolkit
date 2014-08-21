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
package org.geotoolkit.gui.swing.render2d.control.information;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.gui.swing.render2d.control.information.presenter.InformationPresenter;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.util.SwingUtilities;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JInformationDialog extends JDialog implements PropertyChangeListener {

    private final AbstractAction nextAction;
    private final AbstractAction previousAction;
    private final JLabel label = new JLabel("0/0");
    private final JPanel contentPane = new JPanel(new BorderLayout());

    private List<? extends Object> selecteds = new ArrayList<Object>();
    private InformationPresenter presenter = null;
    private JComponent currentComponent = null;
    private JToolBar toolbar = null;
    private RenderingContext2D context = null;
    private SearchAreaJ2D area = null;
    private int selected = 0;

    public JInformationDialog(Component parent) {
        super(SwingUtilities.windowForComponent(parent));
        setContentPane(contentPane);
        this.setIconImage(IconBundle.EMPTY_ICON_16.getImage());

        //configure buttons ----------------------------------------------------

        nextAction = new AbstractAction(" > ") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectedInfo(selected+1);
            }
        };

        previousAction = new AbstractAction(" < ") {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSelectedInfo(selected-1);
            }
        };

        final GridBagConstraints cst = new GridBagConstraints();
        toolbar = new JToolBar();
        toolbar.setLayout(new GridBagLayout());
        toolbar.setFloatable(false);
        cst.gridx = 0;
        toolbar.add(new JButton(previousAction),cst);
        cst.gridx = 1;
        toolbar.add(new JButton(nextAction),cst);
        cst.gridx = 2;
        cst.weightx = 1;
        toolbar.add(label,cst);

        contentPane.add(BorderLayout.SOUTH,toolbar);

    }

    private void setSelectedInfo(final int index){

        if(currentComponent != null){
            contentPane.remove(currentComponent);
        }

        selected = index;

        final Object candidate = selecteds.get(index);
        currentComponent = presenter.createComponent(candidate,context,area);
        currentComponent.addPropertyChangeListener(this);
        if(currentComponent!=null){
            contentPane.add(BorderLayout.CENTER,currentComponent);
        }

        previousAction.setEnabled(index != 0);
        nextAction.setEnabled(index < (selecteds.size()-1));
        label.setText("  "+(selected+1) +"/"+selecteds.size()+"  ");
        setTitle(label.getText());

        contentPane.revalidate();
        contentPane.repaint();
    }

    public void display(final List<? extends Object> selecteds, final InformationPresenter presenter,
            final Point2D where, final RenderingContext2D context, final SearchAreaJ2D area){

        this.presenter = presenter;
        this.selecteds = selecteds;
        this.context = context;
        this.area = area;

        if(selecteds == null || where == null){
            return;
        }
        toolbar.setVisible(selecteds.size() > 1);

        setSelectedInfo(0);
        this.pack();
        final Dimension size = this.getSize();
        if(size.width > 600) { size.width = 600; }
        if(size.height > 600) { size.height = 600; }
        this.setSize(size);
        this.setAlwaysOnTop(true);
        this.setLocation((int)where.getX(),(int)where.getY());
        this.setVisible(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("update".equals(evt.getPropertyName())) {
            this.pack();
        }
    }
}
