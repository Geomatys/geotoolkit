/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Johann Sorel
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

package org.geotoolkit.gui.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.border.Border;

/**
 * Eye candys util class to remove a border from a component.
 * Nice effect when placing several buttons next to each other without borders.
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class BorderCutter {

    private BorderCutter(){}

    public static JComponent cut(JComponent sub, boolean top, boolean bottom, boolean left, boolean right){
        return new JCutter(sub,top,bottom,left,right);
    }

    /**
     * I tryed several approches to remove a border from a component.
     * - Cutting border : didn't work on all L&F. some L&F do not use the paintBorder method
     * to render the border, for exemple the linux L&F. other like Metal or Nimbus were working.
     * - Layout : only moves the component
     * - Override : must overide every possible component class, to much work.
     *
     * The choosen approach is to make him think there is a component (and so preserver all events).
     * but render it cutted by it's parent. We must override the Paint method and not PaintComponent otherwise
     * some L&F bypass the current component to direction paint the childrens (but we don't want that).
     * The sub component must not be opaque, otherwise if it overlaps completely the parent then the parent paint
     * method will be ignored.
     */
    private static class JCutter extends JComponent{

        private final JComponent sub;
        private final boolean top;
        private final boolean bottom;
        private final boolean left;
        private final boolean right;

        private JCutter(JComponent sub, boolean top, boolean bottom, boolean left, boolean right){
            this.top = top;
            this.bottom = bottom;
            this.left = left;
            this.right = right;
            this.sub = sub;
            sub.setOpaque(false);
            add(sub);
        }

        @Override
        public Dimension getPreferredSize() {
            final Dimension dim = sub.getPreferredSize();
            final Border border = sub.getBorder();
            if(border != null){
                final Insets insets = border.getBorderInsets(sub);

                if(bottom){
                    dim.height -= insets.bottom;
                }
                if(top){
                    dim.height -= insets.top;
                }
                if(left){
                    dim.width -= insets.left;
                }
                if(right){
                    dim.width -= insets.right;
                }
            }

            return dim;
        }


        @Override
        public void paint(Graphics g) {
            final Dimension dim = getSize();
            final Border border = sub.getBorder();

            if(border != null){
                final Insets insets = border.getBorderInsets(sub);

                if(bottom){
                    dim.height += insets.bottom;
                }
                if(top){
                    dim.height += insets.top;
                    g.translate(0, -insets.top);
                }
                if(left){
                    dim.width += insets.left;
                    g.translate(-insets.left, 0);
                }
                if(right){
                    dim.width += insets.right;
                }
            }
            sub.setSize(dim);
            sub.paint(g);
        }

    }

}
