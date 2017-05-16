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
package org.geotoolkit.gui.swing.style;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.geotoolkit.cql.CQL;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.gui.swing.filter.JCQLEditor;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.map.MapLayer;
import org.opengis.filter.expression.Expression;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module
 */
public class JSpecialExpressionButton extends JPanel{

    public static final String EXPRESSION_PROPERTY = "expression";

    private static final Icon ICON_EXP_NO = IconBuilder.createIcon(FontAwesomeIcons.ICON_PENCIL, 16, FontAwesomeIcons.DISABLE_COLOR);
    private static final Icon ICON_EXP_YES = IconBuilder.createIcon(FontAwesomeIcons.ICON_PENCIL, 16, FontAwesomeIcons.DEFAULT_COLOR);
    private static final Icon ICON_ERASE = IconBuilder.createIcon(FontAwesomeIcons.ICON_ERASER, 16, FontAwesomeIcons.DEFAULT_COLOR);

    private Expression exp = null;
    private MapLayer layer = null;

    private final JButton guiEdit = new JButton();
    private final JButton guiErase = new JButton();

    public JSpecialExpressionButton(){
        super(new FlowLayout());
        add(guiEdit);
        add(guiErase);

        guiEdit.setBorderPainted(false);
        guiEdit.setContentAreaFilled(false);
        guiEdit.setBorder(null);
        guiEdit.setIcon(ICON_EXP_NO);

        guiErase.setBorderPainted(false);
        guiErase.setContentAreaFilled(false);
        guiErase.setBorder(null);
        guiErase.setIcon(ICON_ERASE);

        guiEdit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try{
                    final Expression ne = JCQLEditor.showDialog(JSpecialExpressionButton.this, layer, exp);
                    if(ne != null && ne != exp){
                        final Expression oldExp = exp;
                        parse(ne);
                        JSpecialExpressionButton.this.firePropertyChange(EXPRESSION_PROPERTY, oldExp, ne);
                    }
                }catch(CQLException ex){
                    ex.printStackTrace();
                }
            }
        });

        guiErase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Expression oldExp = exp;
                parse(null);
                JSpecialExpressionButton.this.firePropertyChange(EXPRESSION_PROPERTY, oldExp, null);
            }
        });

    }

    public void setLayer(final MapLayer layer){
        this.layer = layer;
    }

    public MapLayer getLayer(){
        return layer;
    }

    public void parse(final Expression exp){
        this.exp = exp;
        String tooltip = null;
        if(exp==null){
            guiEdit.setIcon(ICON_EXP_NO);
            guiErase.setVisible(false);
        }else{
            guiEdit.setIcon(ICON_EXP_YES);
            guiErase.setVisible(true);
            tooltip = CQL.write(exp);
        }

        setToolTipText(tooltip);
        guiEdit.setToolTipText(tooltip);
        guiErase.setToolTipText(tooltip);
    }

    public Expression get(){
        return exp;
    }

}
