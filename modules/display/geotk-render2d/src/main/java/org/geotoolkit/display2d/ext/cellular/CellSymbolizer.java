/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013-2014, Geomatys
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
package org.geotoolkit.display2d.ext.cellular;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.measure.Units;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.coverage.grid.GridCoverage2D;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.map.CoverageMapLayer;
import org.geotoolkit.se.xml.v110.RuleType;
import org.geotoolkit.se.xml.v110.SymbolizerType;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.opengis.feature.AttributeType;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.expression.Expression;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.style.ExtensionSymbolizer;
import org.opengis.style.Rule;
import org.opengis.style.StyleVisitor;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CellSymbolizerType")
@XmlRootElement(name="CellSymbolizer",namespace="http://geotoolkit.org")
public class CellSymbolizer extends SymbolizerType implements ExtensionSymbolizer{

    public static final String PROPERY_GEOM_CENTER = "geom_center";
    public static final String PROPERY_GEOM_CONTOUR = "geom_contour";
    public static final String PROPERY_SUFFIX_COUNT = "_count";
    public static final String PROPERY_SUFFIX_MIN = "_min";
    public static final String PROPERY_SUFFIX_MEAN = "_mean";
    public static final String PROPERY_SUFFIX_MAX = "_max";
    public static final String PROPERY_SUFFIX_RANGE = "_range";
    public static final String PROPERY_SUFFIX_RMS = "_rms";
    public static final String PROPERY_SUFFIX_SUM = "_sum";

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.display2d.ext.cellular");
    public static final String NAME = "Cell";

    @XmlElement(name = "CellSize",namespace="http://geotoolkit.org")
    private int cellSize;

    @XmlElement(name = "Rule", type = RuleType.class)
    private RuleType ruleType;

    @XmlTransient
    private Rule rule;


    public CellSymbolizer() {
    }

    public CellSymbolizer(int cellSize, Rule rule){
        this.cellSize = cellSize;
        this.rule = rule;

        final StyleXmlIO util = new StyleXmlIO();
        if(rule!=null){
            this.ruleType = (RuleType) util.getTransformerXMLv110().visit(rule,null);
        }

    }

    @Override
    public Unit<Length> getUnitOfMeasure() {
        return Units.POINT;
    }

    @Override
    public String getGeometryPropertyName() {
        return null;
    }

    @Override
    public Expression getGeometry() {
        return null;
    }

    @Override
    public String getExtensionName() {
        return NAME;
    }

    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }

    public int getCellSize() {
        return cellSize;
    }

    public Rule getRule() {
        if(rule!=null){
            return rule;
        }

        if(ruleType!=null){
            final StyleXmlIO util = new StyleXmlIO();
            try {
                rule = util.getTransformer110().visitRule(ruleType);
            } catch (FactoryException ex) {
                LOGGER.log(Level.WARNING, ex.getMessage(), ex);
            }
        }

        return rule;
    }

    public RuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(RuleType jaxrule) {
        this.ruleType = jaxrule;
        this.rule = null;
    }

    @Override
    public Map<String, Expression> getParameters() {
        final Map<String,Expression> config = new HashMap<>();
        final Set<String> props = GO2Utilities.propertiesNames(Collections.singleton(getRule()));
        int i=0;
        for(String s : props){
            s = cellToBasePropertyName(s);
            if(s!=null){
                config.put(""+i, GO2Utilities.FILTER_FACTORY.property(s));
            }
            i++;
        }
        return config;
    }

    @Override
    public Object accept(StyleVisitor sv, Object o) {
        return sv.visit(this, o);
    }

    public static String cellToBasePropertyName(String s){
        if(s.endsWith(CellSymbolizer.PROPERY_SUFFIX_COUNT)){
            return s.substring(0, s.length()-CellSymbolizer.PROPERY_SUFFIX_COUNT.length());
        }else if(s.endsWith(CellSymbolizer.PROPERY_SUFFIX_MAX)){
            return s.substring(0, s.length()-CellSymbolizer.PROPERY_SUFFIX_MAX.length());
        }else if(s.endsWith(CellSymbolizer.PROPERY_SUFFIX_MEAN)){
            return s.substring(0, s.length()-CellSymbolizer.PROPERY_SUFFIX_MEAN.length());
        }else if(s.endsWith(CellSymbolizer.PROPERY_SUFFIX_MIN)){
            return s.substring(0, s.length()-CellSymbolizer.PROPERY_SUFFIX_MIN.length());
        }else if(s.endsWith(CellSymbolizer.PROPERY_SUFFIX_RANGE)){
            return s.substring(0, s.length()-CellSymbolizer.PROPERY_SUFFIX_RANGE.length());
        }else if(s.endsWith(CellSymbolizer.PROPERY_SUFFIX_RMS)){
            return s.substring(0, s.length()-CellSymbolizer.PROPERY_SUFFIX_RMS.length());
        }else if(s.endsWith(CellSymbolizer.PROPERY_SUFFIX_SUM)){
            return s.substring(0, s.length()-CellSymbolizer.PROPERY_SUFFIX_SUM.length());
        }else if(CellSymbolizer.PROPERY_GEOM_CENTER.equals(s) || CellSymbolizer.PROPERY_GEOM_CONTOUR.equals(s)){
            return null;
        }else{
            return s;
        }
    }

    public static FeatureType buildCellType(CoverageMapLayer layer) throws DataStoreException{
        return buildCellType(layer.getResource());
    }

    public static FeatureType buildCellType(GridCoverageResource ref) throws DataStoreException{
        final List<SampleDimension> lst = ref.getSampleDimensions();
        final GridGeometry gg = ref.getGridGeometry();
        final CoordinateReferenceSystem crs = gg.getCoordinateReferenceSystem();
        if(lst!=null){
            final String[] names = new String[lst.size()];
            for(int i=0;i<names.length;i++){
                names[i] = lst.get(i).getName().toString();
            }
            return buildCellType(lst.size(), names, crs);
        }else{
            //we need to find the number of bands by some other way
            GridCoverage coverage = ref.read(null);
            int nbBands = coverage.getSampleDimensions().size();
            return buildCellType(nbBands, null, crs);
        }
    }

    public static FeatureType buildCellType(GridCoverage2D coverage){
        final List<SampleDimension> dims = coverage.getSampleDimensions();
        final String[] names = new String[dims.size()];
        for(int i=0;i<names.length;i++){
            names[i] = dims.get(i).getName().toString();
        }
        return buildCellType(dims.size(), names, coverage.getCoordinateReferenceSystem2D());
    }

    public static FeatureType buildCellType(int nbBand, String[] bandnames, CoordinateReferenceSystem crs){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        ftb.setName("cell");
        ftb.addAttribute(Point.class).setName(PROPERY_GEOM_CENTER).setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Polygon.class).setName(PROPERY_GEOM_CONTOUR).setCRS(crs);

        for(int b=0,n=nbBand;b<n;b++){
            final String name = "band_"+b;
            final String bandName = (bandnames!=null) ? bandnames[b] : "";
            ftb.addAttribute(Double.class).setDescription(bandName).setName(name+PROPERY_SUFFIX_COUNT);
            ftb.addAttribute(Double.class).setDescription(bandName).setName(name+PROPERY_SUFFIX_MIN);
            ftb.addAttribute(Double.class).setDescription(bandName).setName(name+PROPERY_SUFFIX_MEAN);
            ftb.addAttribute(Double.class).setDescription(bandName).setName(name+PROPERY_SUFFIX_MAX);
            ftb.addAttribute(Double.class).setDescription(bandName).setName(name+PROPERY_SUFFIX_RANGE);
            ftb.addAttribute(Double.class).setDescription(bandName).setName(name+PROPERY_SUFFIX_RMS);
            ftb.addAttribute(Double.class).setDescription(bandName).setName(name+PROPERY_SUFFIX_SUM);
        }
        return ftb.build();
    }

    public static FeatureType buildCellType(final FeatureType basetype, CoordinateReferenceSystem crs){
        crs = (crs==null)? FeatureExt.getCRS(basetype) : crs;
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();

        ftb.setName("cell");
        ftb.addAttribute(Point.class).setName(PROPERY_GEOM_CENTER).setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        ftb.addAttribute(Polygon.class).setName(PROPERY_GEOM_CONTOUR).setCRS(crs);

        //loop on all properties, extract numeric fields only
        for(PropertyType desc : basetype.getProperties(true)){
            if(desc instanceof AttributeType){
                final AttributeType att = (AttributeType) desc;
                final Class binding = att.getValueClass();
                if(Number.class.isAssignableFrom(binding) || String.class.isAssignableFrom(binding)){
                    final String name = att.getName().toString();
                    ftb.addAttribute(double.class).setDescription(name).setName(name+PROPERY_SUFFIX_COUNT);
                    ftb.addAttribute(double.class).setDescription(name).setName(name+PROPERY_SUFFIX_MIN);
                    ftb.addAttribute(double.class).setDescription(name).setName(name+PROPERY_SUFFIX_MEAN);
                    ftb.addAttribute(double.class).setDescription(name).setName(name+PROPERY_SUFFIX_MAX);
                    ftb.addAttribute(double.class).setDescription(name).setName(name+PROPERY_SUFFIX_RANGE);
                    ftb.addAttribute(double.class).setDescription(name).setName(name+PROPERY_SUFFIX_RMS);
                    ftb.addAttribute(double.class).setDescription(name).setName(name+PROPERY_SUFFIX_SUM);
                }
            }
        }

        return ftb.build();
    }

    @Override
    public String toString() {
        return "CellSymbolizer";
    }

}
