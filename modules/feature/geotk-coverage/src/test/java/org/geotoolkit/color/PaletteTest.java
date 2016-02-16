/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.color;

import java.awt.Color;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Palette builder and utility tests.
 *
 * @author Johann Sorel (Geomatys)
 */
public class PaletteTest extends org.geotoolkit.test.TestBase {

    @Test
    public void interpolateSingleTest(){

        assertEquals(Color.RED,                     Palettes.interpolate(Color.RED, Color.GREEN, 0f));
        assertEquals(Color.GREEN,                   Palettes.interpolate(Color.RED, Color.GREEN, 1f));
        assertEquals(new Color(128, 127, 0, 255), Palettes.interpolate(Color.RED, Color.GREEN, 0.5f));
        assertEquals(new Color(170,  85, 0, 255), Palettes.interpolate(Color.RED, Color.GREEN, 1f/3f));

    }

    @Test
    public void interpolateMultipleTest(){

        assertArrayEquals(new Color[]{Color.RED,Color.GREEN},
                Palettes.interpolate(Color.RED, Color.GREEN, 2));

        assertArrayEquals(new Color[]{Color.RED,new Color(128, 127, 0, 255),Color.GREEN},
                Palettes.interpolate(Color.RED, Color.GREEN, 3));

        assertArrayEquals(new Color[]{Color.RED,new Color(170, 85, 0, 255),new Color(85, 170, 0, 255), Color.GREEN},
                Palettes.interpolate(Color.RED, Color.GREEN, 4));

    }

}
