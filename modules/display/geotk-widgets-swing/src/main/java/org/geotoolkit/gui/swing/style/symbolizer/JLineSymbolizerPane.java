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
package org.geotoolkit.gui.swing.style.symbolizer;

import org.geotoolkit.gui.swing.style.JTwoStateEditor;
import org.opengis.style.LineSymbolizer;

/**
 * Simple and complet line symbolizer editor.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class JLineSymbolizerPane extends JTwoStateEditor<LineSymbolizer>{

    public JLineSymbolizerPane() {
        super(new JLineSymbolizerSimple(), new JLineSymbolizerAdvanced());
        
        //configure panel with a default symbolizer
        parse(getStyleFactory().lineSymbolizer());
    }
    
}
