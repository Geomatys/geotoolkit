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
package org.geotoolkit.s52;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.s52.dai.ColorDefinitionCIE;
import org.geotoolkit.s52.dai.ColorTableIdentifier;
import org.geotoolkit.s52.dai.DAIField;
import org.geotoolkit.s52.dai.DAIModuleRecord;
import org.geotoolkit.s52.dai.DAIReader;
import org.geotoolkit.s52.lookuptable.LookupTable;
import org.geotoolkit.s52.lookuptable.LookupTableReader;

/**
 * General S-52 rendering context informations.
 *
 * @author Johann Sorel (Geomatys)
 */
public class S52Context {

    public static final ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_CIEXYZ);

    public static final String TIME_DAY             = "DAY";
    public static final String TIME_DAY_BRIGHT      = "DAY_BRIGHT";
    public static final String TIME_DAY_WHITEBACK   = "DAY_WHITEBACK";
    public static final String TIME_DAY_BLACKBACK   = "DAY_BLACKBACK";
    public static final String TIME_DUSK            = "DUSK";
    public static final String TIME_NIGHT           = "NIGHT";

    private final Map<String,S52Palette> palettes = new HashMap<>();
    private final Map<String,S52SVGIcon> icons = new HashMap<>();
    private final List<LookupTable> lookupTables = new ArrayList<>();
    private String time = "DAY";
    private S52Palette palette = null;
    private URL iconPath;

    public synchronized void setTime(String time) {
        this.time = time;
        //clear the palette
        palette = null;
    }

    public synchronized String getTime() {
        return time;
    }

    public synchronized S52Palette getPalette() {
        if(palette==null){
            palette = palettes.get(time);
        }
        return palette;
    }

    /**
     *
     * @param daiFile DAI file contains color palettes
     * @param iconPath Folder containing S-52 icons
     * @param lookupFiles lookup files for rendering instructions
     * @throws IOException
     */
    public synchronized void load(URL daiFile, URL iconPath, URL ... lookupFiles) throws IOException{
        //clear caches
        palettes.clear();
        icons.clear();
        lookupTables.clear();
        palette = null;
        this.iconPath = iconPath;

        //read DAI file
        final DAIReader daiReader = new DAIReader();
        daiReader.setInput(daiFile);
        while(daiReader.hasNext()){
            final DAIModuleRecord record = daiReader.next();
            //rebuild color palette
            final int size = record.getFields().size();
            final DAIField idField = record.getFields().get(0);
            if(idField instanceof ColorTableIdentifier){
                final ColorTableIdentifier cti = (ColorTableIdentifier) idField;
                final S52Palette palette = new S52Palette(cti.CTUS);
                palettes.put(palette.getName(), palette);

                for(int i=1;i<size;i++){
                    final DAIField field = record.getFields().get(i);
                    if(field instanceof ColorDefinitionCIE){
                        final ColorDefinitionCIE ccie = (ColorDefinitionCIE) field;
                        float[] colorValues = new float[]{
                            (float)ccie.CHRX,
                            (float)ccie.CHRY,
                            (float)ccie.CLUM
                        };
                        colorValues = cs.toRGB(colorValues);
                        final String encoded = (String)GO2Utilities.STYLE_FACTORY.literal(
                                new Color(colorValues[0], colorValues[1], colorValues[2])).getValue();
                        palette.addColor(ccie.CTOK, encoded);
                    }
                }
            }

        }
        daiReader.dispose();

        //read lookup tables for instructions
        final LookupTableReader lkReader = new LookupTableReader();
        for(URL lkFile : lookupFiles){
            lkReader.reset();
            lkReader.setInput(lkFile);
            final LookupTable table = lkReader.read();
            lookupTables.add(table);
        }
        lkReader.dispose();

    }

}
