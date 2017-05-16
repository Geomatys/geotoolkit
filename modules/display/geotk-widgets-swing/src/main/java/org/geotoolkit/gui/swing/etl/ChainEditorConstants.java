/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.gui.swing.etl;

import java.awt.Color;
import java.awt.Font;

/**
 *
 * @author Quentin Boileau (Geomatys).
 */
public final class ChainEditorConstants {

    private ChainEditorConstants() {}

    public static final Font CHAIN_ELEMENT_EXECUTION_TITLE_FONT = new Font("monospaced", Font.BOLD, 12);

    public static final Color DEFAULT_CHAIN_ELEMENT_COLOR = Color.WHITE;

    public static final Color CHAIN_CHAIN_ELEMENT_COLOR = new Color(220, 200, 150); //gold

    public static final Color CHAIN_CONDITIONAL_ELEMENT_COLOR = new Color(104, 151, 202);//blue

    public static final Color CHAIN_TITLE_COLOR = Color.BLACK;

    public static final Color CHAIN_TITLE_MISSING_COLOR = Color.RED;

    public static final Color CHAIN_TITLE_UNDERLINE_COLOR = Color.DARK_GRAY;

    public static final Color MANUAL_INTERVENTION_COLOR = Color.GRAY;

    public static final Color MANUAL_INTERVENTION_BORDER_COLOR = Color.DARK_GRAY;

    /**
     * Links color by default in idle state.
     */
    public static final Color DEFAULT_LINE_COLOR = new Color(110,110,110);

    /**
     * Links color in selected state.
     */
    public static final Color SELECT_LINE_COLOR = Color.RED;

    /**
     * Widget border color in selected state.
     */
    public static final Color SELECT_BORDER_COLOR = Color.RED;

    /**
     * Links color in not selected state.
     */
    public static final Color UNSELECT_LINE_COLOR = Color.GRAY;
}
