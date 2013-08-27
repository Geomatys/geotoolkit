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
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.sis.util.logging.Logging;
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

    public static final Logger LOGGER = Logging.getLogger(S52Context.class);

    /**
     * S-52 divides geometries in POINT,LINE,AREA types.
     */
    public static enum GeoType{
        POINT,LINE,AREA
    };

    /** Default lookup table for areas with plain boundaries */
    public static final URL LK_AREA_PLAIN        = S52Context.class.getResource("/org/geotoolkit/s52/lookuptable/AREAS WITH PLAIN BOUNDARIES.txt");
    public static final URL LK_AREA_BOUNDARY     = S52Context.class.getResource("/org/geotoolkit/s52/lookuptable/AREAS WITH SYMBOLIZED BOUNDARIES.txt");
    public static final URL LK_LINE              = S52Context.class.getResource("/org/geotoolkit/s52/lookuptable/LINES.txt");
    public static final URL LK_POINT_PAPER_CHART = S52Context.class.getResource("/org/geotoolkit/s52/lookuptable/PAPER CHART POINTS.txt");
    public static final URL LK_POINT_SIMPLIFIED  = S52Context.class.getResource("/org/geotoolkit/s52/lookuptable/SIMPLIFIED POINTS.txt");

    public static final URL ICONS  = S52Context.class.getResource("/org/geotoolkit/s52/icons");


    public static final String TIME_DAY             = "DAY";
    public static final String TIME_DAY_BRIGHT      = "DAY_BRIGHT";
    public static final String TIME_DAY_WHITEBACK   = "DAY_WHITEBACK";
    public static final String TIME_DAY_BLACKBACK   = "DAY_BLACKBACK";
    public static final String TIME_DUSK            = "DUSK";
    public static final String TIME_NIGHT           = "NIGHT";

    private final Map<String,S52Palette> palettes = new HashMap<>();
    private final Map<String,S52SVGIcon> icons = new HashMap<>();
    private S52Palette palette = null;
    private URL iconPath;
    private LookupTable areaLookupTable = null;
    private LookupTable lineLookupTable = null;
    private LookupTable pointLookupTable = null;

    // Mariner context configuration
    // S-52 Annex A Part I p.23
    private String paletteName = "DAY";
    // See also : 7.1.3.1 Text Groupings
    private boolean noText = false;

    /**
     * Change current palette. refer to a palette by name.
     * @param paletteName
     */
    public synchronized void setActivePaletteName(String paletteName) {
        this.paletteName = paletteName;
        //clear the palette
        palette = null;
    }

    /**
     * Get current active palette name.
     * @return String
     */
    public synchronized String getActivePaletteName() {
        return paletteName;
    }

    /**
     * Get current palette.
     * @return S52Palette
     */
    public synchronized S52Palette getPalette() {
        if(palette==null){
            palette = palettes.get(paletteName);
        }
        return palette;
    }

    public LookupTable getLookupTable(GeoType type) {
        switch(type){
            case AREA : return getAreaLookupTable();
            case LINE : return getLineLookupTable();
            case POINT: return getPointLookupTable();
        }
        return null;
    }

    public LookupTable getAreaLookupTable() {
        return areaLookupTable;
    }

    public LookupTable getLineLookupTable() {
        return lineLookupTable;
    }

    public LookupTable getPointLookupTable() {
        return pointLookupTable;
    }

    /**
     * Background color. palette NODTA color.
     * @return Color
     */
    public Color getBackgroundColor(){
        return getPalette().getColor("NODTA");
    }

    //TODO add radar overlay color convinient methods
    // S-52 Annex A Part I p.33 (4.2.3.1 Radar overlay AND 4.2.3.2 Transparant radar)

    /**
     *
     * @param daiPath DAI file contains color palettes
     * @param iconPath Folder containing S-52 icons
     * @param lookupFiles lookup files for rendering instructions
     * @throws IOException
     */
    public synchronized void load(URL daiPath, URL iconPath, URL areaLookupTablePath,
            URL lineLookupTablePath, URL pointLookupTablePath) throws IOException{
        //clear caches
        palettes.clear();
        icons.clear();
        areaLookupTable = null;
        lineLookupTable = null;
        pointLookupTable = null;
        palette = null;
        this.iconPath = iconPath;

        //read DAI file
        final DAIReader daiReader = new DAIReader();
        daiReader.setInput(daiPath);
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
                        palette.addColor((ColorDefinitionCIE)field);
                    }
                }
            }

        }
        daiReader.dispose();

        //read lookup tables for instructions
        areaLookupTable = readTable(areaLookupTablePath);
        lineLookupTable = readTable(lineLookupTablePath);
        pointLookupTable = readTable(pointLookupTablePath);
    }

    private static LookupTable readTable(URL lkFile) throws IOException{
        final LookupTableReader lkReader = new LookupTableReader();
        lkReader.reset();
        lkReader.setInput(lkFile);
        final LookupTable table = lkReader.read();
        lkReader.dispose();
        return table;
    }

}
