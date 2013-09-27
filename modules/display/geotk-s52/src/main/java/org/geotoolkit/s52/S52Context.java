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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.s52.dai.AttributeCombination;
import org.geotoolkit.s52.dai.ColorDefinitionCIE;
import org.geotoolkit.s52.dai.ColorTableIdentifier;
import org.geotoolkit.s52.dai.DAIField;
import org.geotoolkit.s52.dai.DAILookupRecord;
import org.geotoolkit.s52.dai.DAIModuleRecord;
import org.geotoolkit.s52.dai.DAIReader;
import org.geotoolkit.s52.dai.DisplayCategory;
import org.geotoolkit.s52.dai.Instruction;
import org.geotoolkit.s52.dai.LibraryIdentification;
import org.geotoolkit.s52.dai.LinestyleColorReference;
import org.geotoolkit.s52.dai.LinestyleDefinition;
import org.geotoolkit.s52.dai.LinestyleExposition;
import org.geotoolkit.s52.dai.LinestyleIdentifier;
import org.geotoolkit.s52.dai.LinestyleVector;
import org.geotoolkit.s52.dai.LookupComment;
import org.geotoolkit.s52.dai.LookupTableEntryIdentifier;
import org.geotoolkit.s52.dai.PatternBitmap;
import org.geotoolkit.s52.dai.PatternColorReference;
import org.geotoolkit.s52.dai.PatternDefinition;
import org.geotoolkit.s52.dai.PatternExposition;
import org.geotoolkit.s52.dai.PatternIdentifier;
import org.geotoolkit.s52.dai.PatternVector;
import org.geotoolkit.s52.dai.SymbolBitmap;
import org.geotoolkit.s52.dai.SymbolColorReference;
import org.geotoolkit.s52.dai.SymbolDefinition;
import org.geotoolkit.s52.dai.SymbolExposition;
import org.geotoolkit.s52.dai.SymbolIdentifier;
import org.geotoolkit.s52.dai.SymbolVector;
import org.geotoolkit.s52.lookuptable.LookupTable;
import org.geotoolkit.s52.procedure.CLRLIN01;
import org.geotoolkit.s52.procedure.DATCVR02;
import org.geotoolkit.s52.procedure.DEPARE02;
import org.geotoolkit.s52.procedure.DEPCNT03;
import org.geotoolkit.s52.procedure.DEPVAL02;
import org.geotoolkit.s52.procedure.LEGLIN03;
import org.geotoolkit.s52.procedure.LIGHTS05;
import org.geotoolkit.s52.procedure.LITDSN01;
import org.geotoolkit.s52.procedure.OBSTRN06;
import org.geotoolkit.s52.procedure.OWNSHP02;
import org.geotoolkit.s52.procedure.PASTRK01;
import org.geotoolkit.s52.procedure.Procedure;
import org.geotoolkit.s52.procedure.QUALIN01;
import org.geotoolkit.s52.procedure.QUAPNT02;
import org.geotoolkit.s52.procedure.QUAPOS01;
import org.geotoolkit.s52.procedure.RESARE03;
import org.geotoolkit.s52.procedure.RESCSP02;
import org.geotoolkit.s52.procedure.RESTRN01;
import org.geotoolkit.s52.procedure.SAFCON01;
import org.geotoolkit.s52.procedure.SEABED01;
import org.geotoolkit.s52.procedure.SLCONS03;
import org.geotoolkit.s52.procedure.SNDFRM03;
import org.geotoolkit.s52.procedure.SOUNDG02;
import org.geotoolkit.s52.procedure.SYMINS01;
import org.geotoolkit.s52.procedure.TOPMAR01;
import org.geotoolkit.s52.procedure.UDWHAZ04;
import org.geotoolkit.s52.procedure.VESSEL02;
import org.geotoolkit.s52.procedure.VRMEBL02;
import org.geotoolkit.s52.procedure.WRECKS04;
import org.geotoolkit.s52.render.LineSymbolStyle;
import org.geotoolkit.s52.render.PatternSymbolStyle;
import org.geotoolkit.s52.render.PointSymbolStyle;
import org.geotoolkit.s52.render.SymbolStyle;

/**
 * General S-52 rendering context informations.
 *
 * @author Johann Sorel (Geomatys)
 */
public class S52Context {

    public static final Logger LOGGER = Logging.getLogger(S52Context.class);
    private static URL DEFAULT_DAI = null;

    /**
     * S-52 divides geometries in POINT,LINE,AREA types.
     */
    public static enum GeoType{
        POINT,LINE,AREA
    };

    public static final String LKN_AREA_PLAIN       = "PLAIN_BOUNDARIES";
    public static final String LKN_AREA_SYMBOLIZED  = "SYMBOLIZED_BOUNDARIES";
    public static final String LKN_LINE             = "LINES";
    public static final String LKN_POINT_PAPER      = "PAPER_CHART";
    public static final String LKN_POINT_SIMPLIFIED = "SIMPLIFIED";

    public static final String TIME_DAY             = "DAY";
    public static final String TIME_DAY_BRIGHT      = "DAY_BRIGHT";
    public static final String TIME_DAY_WHITEBACK   = "DAY_WHITEBACK";
    public static final String TIME_DAY_BLACKBACK   = "DAY_BLACKBACK";
    public static final String TIME_DUSK            = "DUSK";
    public static final String TIME_NIGHT           = "NIGHT";

    private static final Map<String,Procedure> PROCEDURES = new HashMap<>();
    static {
        final Procedure[] array = new Procedure[]{
            new CLRLIN01(),
            new DATCVR02(),
            new DEPARE02(),
            new DEPCNT03(),
            new DEPVAL02(),
            new LEGLIN03(),
            new LIGHTS05(),
            new LITDSN01(),
            new OBSTRN06(),
            new OWNSHP02(),
            new PASTRK01(),
            new QUALIN01(),
            new QUAPNT02(),
            new QUAPOS01(),
            new RESARE03(),
            new RESCSP02(),
            new RESTRN01(),
            new SAFCON01(),
            new SEABED01(),
            new SLCONS03(),
            new SNDFRM03(),
            new SOUNDG02(),
            new SYMINS01(),
            new TOPMAR01(),
            new UDWHAZ04(),
            new VESSEL02(),
            new VRMEBL02(),
            new WRECKS04()
        };
        for(Procedure p : array){
            PROCEDURES.put(p.getName(), p);
        }
    }

    private final Map<String,S52Palette> palettes = new HashMap<>();
    private final Map<String,S52SVGIcon> icons = new HashMap<>();
    private final Map<String,LookupTable> lookups = new HashMap<>();
    private S52Palette palette = null;
    private URL iconPath;

    private final Map<String,SymbolStyle> styles = new HashMap<>();

    // Mariner context configuration ///////////////////////////////////////////
    // S-52 Annex A Part I p.23
    private String paletteName = TIME_DAY;
    //selected lookups
    private String arealk = LKN_AREA_SYMBOLIZED;
    private String linelk = LKN_LINE;
    private String pointlk = LKN_POINT_SIMPLIFIED;
    // See also : 7.1.3.1 Text Groupings
    private boolean noText = false;
    // S-52 Annex A Part I p.137
    private float safetyDepth = 1f; //meters
    private float shallowContour = 5f; //meters
    private float safetyContour = 1f; //meters
    private float deepContour = 10f; // meters
    private boolean lowAccuracySymbols = false;
    private boolean twoShades = true;
    private boolean shallowPattern = false;
    private boolean shipsOutline = false;
    private boolean contourLabels = true; //viewing group 33022
    private float distanceTags = 0f; //nm
    private float timeTags = 0f; //min
    private boolean fullSectors = true;
    private boolean lightDescription = false;
    private boolean isolatedDangerInShallowWater = true;
    //used by procedure SOUNDG02
    private float analyzedDepth = 0f;

    // DAI informations ////////////////////////////////////////////////////////

    public List<String> getAvailablePalettes(){
        return new ArrayList<>(palettes.keySet());
    }

    public List<String> getAvailablePointTables(){
        final List<String> values = new ArrayList<>();
        values.add(LKN_POINT_PAPER);
        values.add(LKN_POINT_SIMPLIFIED);
        return values;
    }

    public List<String> getAvailableLineTables(){
        final List<String> values = new ArrayList<>();
        values.add(LKN_LINE);
        return values;
    }

    public List<String> getAvailableAreaTables(){
        final List<String> values = new ArrayList<>();
        values.add(LKN_AREA_PLAIN);
        values.add(LKN_AREA_SYMBOLIZED);
        return values;
    }

    // MARINER CONFIGURATION ///////////////////////////////////////////////////

    public void setNoText(boolean noText) {
        this.noText = noText;
    }

    public boolean isNoText() {
        return noText;
    }

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

    /**
     * Get palette by name.
     * @return S52Palette
     */
    public synchronized S52Palette getPalette(String paletteName) {
        return palettes.get(paletteName);
    }

    public void setActivePointTable(String pointlk) {
        this.pointlk = pointlk;
    }

    public String getActivePointTable() {
        return pointlk;
    }

    public void setActiveLineTable(String linelk) {
        this.linelk = linelk;
    }

    public String getActiveLineTable() {
        return linelk;
    }

    public void setActiveAreaTable(String arealk) {
        this.arealk = arealk;
    }

    public String getActiveAreaTable() {
        return arealk;
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
        return lookups.get(arealk);
    }

    public LookupTable getLineLookupTable() {
        return lookups.get(linelk);
    }

    public LookupTable getPointLookupTable() {
        return lookups.get(pointlk);
    }

    public float getSafetyDepth() {
        return safetyDepth;
    }

    public void setSafetyDepth(float safetyDepth) {
        this.safetyDepth = safetyDepth;
    }

    public float getShallowContour() {
        return shallowContour;
    }

    public void setShallowContour(float shallowContour) {
        this.shallowContour = shallowContour;
    }

    public float getSafetyContour() {
        return safetyContour;
    }

    public void setSafetyContour(float safetyContour) {
        this.safetyContour = safetyContour;
    }

    public float getDeepContour() {
        return deepContour;
    }

    public void setDeepContour(float deepContour) {
        this.deepContour = deepContour;
    }

    public boolean isTwoShades() {
        return twoShades;
    }

    public void setTwoShades(boolean twoShades) {
        this.twoShades = twoShades;
    }

    public boolean isShallowPattern() {
        return shallowPattern;
    }

    public void setShallowPattern(boolean shallowPattern) {
        this.shallowPattern = shallowPattern;
    }

    public boolean isShipsOutline() {
        return shipsOutline;
    }

    public void setShipsOutline(boolean shipsOutline) {
        this.shipsOutline = shipsOutline;
    }

    public float getDistanceTags() {
        return distanceTags;
    }

    public void setDistanceTags(float distanceTags) {
        this.distanceTags = distanceTags;
    }

    public float getTimeTags() {
        return timeTags;
    }

    public void setTimeTags(float timeTags) {
        this.timeTags = timeTags;
    }

    public boolean isFullSectors() {
        return fullSectors;
    }

    public void setFullSectors(boolean fullSectors) {
        this.fullSectors = fullSectors;
    }

    public boolean isLightDescription() {
        return lightDescription;
    }

    public void setLightDescription(boolean lightDescription) {
        this.lightDescription = lightDescription;
    }

    public boolean isLowAccuracySymbols() {
        return lowAccuracySymbols;
    }

    public void setLowAccuracySymbols(boolean lowAccuracySymbols) {
        this.lowAccuracySymbols = lowAccuracySymbols;
    }

    public boolean isContourLabels() {
        return contourLabels;
    }

    public void setContourLabels(boolean contourLabels) {
        this.contourLabels = contourLabels;
    }

    public float getAnalyzedDepth() {
        return analyzedDepth;
    }

    public void setAnalyzedDepth(float analyzedDepth) {
        this.analyzedDepth = analyzedDepth;
    }

    public void setIsolatedDangerInShallowWater(boolean isolatedDangerInShallowWater) {
        this.isolatedDangerInShallowWater = isolatedDangerInShallowWater;
    }

    public boolean isIsolatedDangerInShallowWater() {
        return isolatedDangerInShallowWater;
    }

    ////////////////////////////////////////////////////////////////////////////

    public S52SVGIcon getIcon(String name) throws IOException{
        //TODO waiting for all symbols
        final S52SVGIcon icon = new S52SVGIcon(iconPath.toString()+"BCNCAR01.svg");
        return icon;
    }

    public SymbolStyle getSyle(String name){
        return styles.get(name);
    }

    public Procedure getProcedure(String name){
        return PROCEDURES.get(name);
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
     * @param daiPath DAI file contains color palettes, symbols and lookup tables
     * @throws IOException
     */
    public synchronized void load(URL daiPath) throws IOException{
        //clear caches
        palettes.clear();
        icons.clear();
        lookups.clear();
        palette = null;
        this.iconPath = null;

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
            }else if(idField instanceof LookupTableEntryIdentifier){
                final LookupTableEntryIdentifier lei = (LookupTableEntryIdentifier) idField;
                final DAILookupRecord rec = new DAILookupRecord();
                rec.identifier = lei;

                for(int i=1;i<size;i++){
                    final DAIField field = record.getFields().get(i);
                    if(field instanceof Instruction){
                        rec.instruction = (Instruction) field;
                    }else if(field instanceof AttributeCombination){
                        rec.attributes = (AttributeCombination) field;
                    }else if(field instanceof LookupComment){
                        rec.comment = (LookupComment) field;
                    }else if(field instanceof DisplayCategory){
                        rec.category = (DisplayCategory) field;
                    }else{
                        throw new IOException("Unexpected field "+field);
                    }
                }

                LookupTable table = lookups.get(rec.identifier.TNAM);
                if(table == null){
                    table = new LookupTable();
                    lookups.put(rec.identifier.TNAM, table);
                }
                table.getRecords().add(rec);

            }else if(idField instanceof LibraryIdentification){
                //we don't need this one for rendering.
                //contains metadatas only

            }else if(idField instanceof LinestyleIdentifier){
                final LineSymbolStyle style = new LineSymbolStyle();
                style.ident = (LinestyleIdentifier) idField;

                for(int i=1;i<size;i++){
                    final DAIField field = record.getFields().get(i);
                    if(field instanceof LinestyleColorReference){
                        style.colors = (LinestyleColorReference) field;
                    }else if(field instanceof LinestyleDefinition){
                        style.definition = (LinestyleDefinition) field;
                    }else if(field instanceof LinestyleExposition){
                        style.explication = (LinestyleExposition) field;
                    }else if(field instanceof LinestyleVector){
                        style.vectors.add((LinestyleVector) field);
                    }else{
                        throw new IOException("Unexpected field "+field);
                    }
                }
                styles.put(style.definition.getName(), style);

            }else if(idField instanceof PatternIdentifier){
                final PatternSymbolStyle style = new PatternSymbolStyle();
                style.ident = (PatternIdentifier) idField;

                for(int i=1;i<size;i++){
                    final DAIField field = record.getFields().get(i);
                    if(field instanceof PatternBitmap){
                        style.bitmap = (PatternBitmap) field;
                    }else if(field instanceof PatternColorReference){
                        style.colors = (PatternColorReference) field;
                    }else if(field instanceof PatternDefinition){
                        style.definition = (PatternDefinition) field;
                    }else if(field instanceof PatternExposition){
                        style.explication = (PatternExposition) field;
                    }else if(field instanceof PatternVector){
                        style.vectors.add((PatternVector) field);
                    }else{
                        throw new IOException("Unexpected field "+field);
                    }
                }
                styles.put(style.definition.getName(), style);

            }else if(idField instanceof SymbolIdentifier){
                final PointSymbolStyle style = new PointSymbolStyle();
                style.ident = (SymbolIdentifier) idField;

                for(int i=1;i<size;i++){
                    final DAIField field = record.getFields().get(i);
                    if(field instanceof SymbolBitmap){
                        style.bitmap = (SymbolBitmap) field;
                    }else if(field instanceof SymbolColorReference){
                        style.colors = (SymbolColorReference) field;
                    }else if(field instanceof SymbolDefinition){
                        style.definition = (SymbolDefinition) field;
                    }else if(field instanceof SymbolExposition){
                        style.explication = (SymbolExposition) field;
                    }else if(field instanceof SymbolVector){
                        style.vectors.add((SymbolVector) field);
                    }else{
                        throw new IOException("Unexpected field "+field);
                    }
                }
                styles.put(style.definition.getName(), style);
            }else{
                throw new IOException("Unexpected record \n"+record);
            }

        }
        daiReader.dispose();
    }

    public synchronized static void setDefaultDAI(URL url){
        DEFAULT_DAI = url;
    }

    public synchronized static URL getDefaultDAI() {
        return DEFAULT_DAI;
    }

}
