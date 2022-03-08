/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.sld.xml;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.apache.sis.measure.Units;
import org.geotoolkit.ogc.xml.FilterToOGC110Converter;
import org.geotoolkit.ogc.xml.v110.PropertyNameType;
import org.geotoolkit.se.xml.v110.AnchorPointType;
import org.geotoolkit.se.xml.v110.CategorizeType;
import org.geotoolkit.se.xml.v110.ChannelSelectionType;
import org.geotoolkit.se.xml.v110.ColorMapType;
import org.geotoolkit.se.xml.v110.ColorReplacementType;
import org.geotoolkit.se.xml.v110.ContrastEnhancementType;
import org.geotoolkit.se.xml.v110.CoverageStyleType;
import org.geotoolkit.se.xml.v110.DescriptionType;
import org.geotoolkit.se.xml.v110.DisplacementType;
import org.geotoolkit.se.xml.v110.ExternalGraphicType;
import org.geotoolkit.se.xml.v110.FeatureTypeStyleType;
import org.geotoolkit.se.xml.v110.FillType;
import org.geotoolkit.se.xml.v110.FontType;
import org.geotoolkit.se.xml.v110.GeometryType;
import org.geotoolkit.se.xml.v110.GraphicFillType;
import org.geotoolkit.se.xml.v110.GraphicStrokeType;
import org.geotoolkit.se.xml.v110.GraphicType;
import org.geotoolkit.se.xml.v110.HaloType;
import org.geotoolkit.se.xml.v110.ImageOutlineType;
import org.geotoolkit.se.xml.v110.InlineContentType;
import org.geotoolkit.se.xml.v110.InterpolateType;
import org.geotoolkit.se.xml.v110.InterpolationPointType;
import org.geotoolkit.se.xml.v110.LabelPlacementType;
import org.geotoolkit.se.xml.v110.LegendGraphicType;
import org.geotoolkit.se.xml.v110.LinePlacementType;
import org.geotoolkit.se.xml.v110.LineSymbolizerType;
import org.geotoolkit.se.xml.v110.MarkType;
import org.geotoolkit.se.xml.v110.MethodType;
import org.geotoolkit.se.xml.v110.ModeType;
import org.geotoolkit.se.xml.v110.OnlineResourceType;
import org.geotoolkit.se.xml.v110.ParameterValueType;
import org.geotoolkit.se.xml.v110.PointPlacementType;
import org.geotoolkit.se.xml.v110.PointSymbolizerType;
import org.geotoolkit.se.xml.v110.PolygonSymbolizerType;
import org.geotoolkit.se.xml.v110.RasterSymbolizerType;
import org.geotoolkit.se.xml.v110.RuleType;
import org.geotoolkit.se.xml.v110.ShadedReliefType;
import org.geotoolkit.se.xml.v110.StrokeType;
import org.geotoolkit.se.xml.v110.SvgParameterType;
import org.geotoolkit.se.xml.v110.TextSymbolizerType;
import org.geotoolkit.se.xml.v110.ThreshholdsBelongToType;
import org.geotoolkit.se.xml.vext.ColorItemType;
import org.geotoolkit.se.xml.vext.JenksType;
import org.geotoolkit.se.xml.vext.RangeType;
import org.geotoolkit.se.xml.vext.RecolorType;
import org.geotoolkit.style.StyleUtilities;
import org.geotoolkit.style.function.Categorize;
import org.geotoolkit.style.function.ColorItem;
import org.geotoolkit.style.function.Interpolate;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.function.Jenks;
import org.geotoolkit.style.function.Method;
import org.geotoolkit.style.function.Mode;
import org.geotoolkit.style.function.RecolorFunction;
import org.geotoolkit.style.function.ThreshholdsBelongTo;
import org.opengis.filter.Expression;
import org.opengis.filter.Literal;
import org.opengis.metadata.citation.OnlineResource;
import org.opengis.style.AnchorPoint;
import org.opengis.style.ChannelSelection;
import org.opengis.style.ColorMap;
import org.opengis.style.ColorReplacement;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.ContrastMethod;
import org.opengis.style.Description;
import org.opengis.style.Displacement;
import org.opengis.style.ExtensionSymbolizer;
import org.opengis.style.ExternalGraphic;
import org.opengis.style.ExternalMark;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Fill;
import org.opengis.style.Font;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicFill;
import org.opengis.style.GraphicLegend;
import org.opengis.style.GraphicStroke;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.Halo;
import org.opengis.style.LabelPlacement;
import org.opengis.style.LinePlacement;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Mark;
import org.opengis.style.OverlapBehavior;
import org.opengis.style.PointPlacement;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Rule;
import org.opengis.style.SelectedChannelType;
import org.opengis.style.SemanticType;
import org.opengis.style.ShadedRelief;
import org.opengis.style.Stroke;
import org.opengis.style.Style;
import org.opengis.style.StyleVisitor;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class GTtoSE110Transformer extends FilterToOGC110Converter implements StyleVisitor {

    private static final String GENERIC_ANY = "generic:any";
    private static final String GENERIC_POINT = "generic:point";
    private static final String GENERIC_LINE = "generic:line";
    private static final String GENERIC_POLYGON = "generic:polygon";
    private static final String GENERIC_TEXT = "generic:text";
    private static final String GENERIC_RASTER = "generic:raster";
    private static final String VERSION = "1.1.0";
    private final org.geotoolkit.sld.xml.v110.ObjectFactory sld_factory_v110;
    private final org.geotoolkit.se.xml.v110.ObjectFactory se_factory;

    public GTtoSE110Transformer() {
        this.sld_factory_v110 = new org.geotoolkit.sld.xml.v110.ObjectFactory();
        this.se_factory = new org.geotoolkit.se.xml.v110.ObjectFactory();
    }

    static String colorToString(Color color){
        String redCode = Integer.toHexString(color.getRed());
        String greenCode = Integer.toHexString(color.getGreen());
        String blueCode = Integer.toHexString(color.getBlue());
        if (redCode.length() == 1)      redCode = "0" + redCode;
        if (greenCode.length() == 1)    greenCode = "0" + greenCode;
        if (blueCode.length() == 1)     blueCode = "0" + blueCode;

        final String colorCode;
        int alpha = color.getAlpha();
        if(alpha != 255){
            String alphaCode = Integer.toHexString(alpha);
            if (alphaCode.length() == 1) alphaCode = "0" + alphaCode;
            colorCode = "#" + alphaCode + redCode + greenCode + blueCode;
        }else{
            colorCode = "#" + redCode + greenCode + blueCode;
        }
        return colorCode.toUpperCase();
    }

    /**
     * Transform a GT Expression in a jaxb parameter value type.
     */
    public ParameterValueType visitExpression(final Expression exp) {
        if(exp==null) return null;

        final JAXBElement<?> ele = extract(exp);
        if (ele == null) {
            return null;
        } else {
            final ParameterValueType param = se_factory.createParameterValueType();
            param.getContent().add(extract(exp));
            return param;
        }

    }

    /**
     * Transform an expression or float array in a scg parameter.
     */
    public SvgParameterType visitSVG(final Object obj, final String value) {
        SvgParameterType svg = se_factory.createSvgParameterType();
        svg.setName(value);
        // HACK: duplicate code from ColorMap visit method.
        // Note that the following block cause divergence with duplicated code in GTtoSE100Transformer.
        // Removing duplication would require a lot of effort (complete rewrite), so for now, I just make code uglier
        if (obj instanceof Categorize) {
            svg.getContent().add(se_factory.createCategorize(visit((Categorize) obj)));
        } else if (obj instanceof Interpolate) {
            svg.getContent().add(se_factory.createInterpolate(visit((Interpolate) obj)));
        } else if (obj instanceof Jenks) {
            svg.getContent().add(se_factory.createJenksType(visit((Jenks) obj)));
        } else if (obj instanceof Expression) {
            final JAXBElement<?> ele = extract((Expression) obj);
            if (ele == null) {
                svg = null;
            } else {
                svg.getContent().add(ele);
            }
        } else if (obj instanceof float[]) {
            final float[] dashes = (float[]) obj;
            final StringBuilder sb = new StringBuilder();
            for (final float f : dashes) {
                sb.append(f);
                sb.append(' ');
            }
            svg.getContent().add(sb.toString().trim());
        } else {
            throw new IllegalArgumentException("Unknowed CSS parameter jaxb structure :" + obj);
        }

        return svg;
    }

    /**
     * Transform a geometrie name in a geometrytype.
     */
    public GeometryType visitGeometryType(String str) {
        final GeometryType geo = se_factory.createGeometryType();
        final PropertyNameType value = ogc_factory.createPropertyNameType();
        if (str == null) {
            str = "";
        }
        value.setContent(str);
        geo.setPropertyName(value);
        return geo;
    }

    /**
     * Transform a Feature name in a QName.
     */
    public QName visitName(final GenericName name) {
        return new QName(name.scope().isGlobal() ? null : name.scope().name().toString(), name.toString());
    }

    /**
     * Transform a Unit to the corresponding SLD string.
     */
    public String visitUOM(final Unit<?> uom) {
        if (uom == null) {
            return null;
        }

        if (uom.equals(Units.METRE)) {
            return "http://www.opengeospatial.org/se/units/metre";
        } else if (uom.equals(Units.FOOT)) {
            return "http://www.opengeospatial.org/se/units/foot";
        } else if (uom.equals(Units.POINT) || uom.equals(Units.PIXEL)) {
            return "http://www.opengeospatial.org/se/units/pixel";
        } else {
            String textRepresentation = uom.getSymbol();
            if (textRepresentation == null) textRepresentation = uom.getName();
            if (textRepresentation == null) {
                Logger.getLogger("org.geotoolkit.sld.xml")
                        .warning("Unrepresentable unit (no available symbol or name) will be ignored.");
            }
            return textRepresentation;
        }
    }

    /**
     * Transform a GT Style in Jaxb UserStyle
     */
    @Override
    public org.geotoolkit.sld.xml.v110.UserStyle visit(final Style style, final Object data) {
        final org.geotoolkit.sld.xml.v110.UserStyle userStyle = sld_factory_v110.createUserStyle();
        userStyle.setName(style.getName());
        userStyle.setDescription(visit(style.getDescription(), null));
        userStyle.setIsDefault(style.isDefault());

        for (final FeatureTypeStyle fts : style.featureTypeStyles()) {
            userStyle.getFeatureTypeStyleOrCoverageStyleOrOnlineResource().add(visit(fts, null));
        }

        return userStyle;
    }

    /**
     * Transform a GT FTS in Jaxb FeatureTypeStyle or CoveragaStyle or
     * OnlineResource.
     */
    @Override
    public Object visit(final FeatureTypeStyle fts, final Object data) {
        if (fts.getOnlineResource() != null) {
            //we store only the online resource
            return visit(fts.getOnlineResource(), null);
        } else {

            //try to figure out if we have here a coverage FTS or not
            boolean isCoverage = false;
            if (fts.semanticTypeIdentifiers().contains(SemanticType.RASTER)) {
                isCoverage = true;
            } else if (fts.semanticTypeIdentifiers().contains(SemanticType.ANY) || fts.semanticTypeIdentifiers().isEmpty()) {
                if (fts.getFeatureInstanceIDs() == null) {

                    //try to find a coverage style
                    ruleLoop:
                    for (final Rule r : fts.rules()) {
                        for (final Symbolizer s : r.symbolizers()) {
                            if (s instanceof RasterSymbolizer) {
                                isCoverage = true;
                                break ruleLoop;
                            }
                        }
                    }
                } else {
                    isCoverage = false;
                }
            } else {
                isCoverage = false;
            }

            final Object obj;
            //create the sld FTS
            if (isCoverage) {
                //coverage type
                final CoverageStyleType cst = se_factory.createCoverageStyleType();

                if (!fts.featureTypeNames().isEmpty()) {
                    cst.setCoverageName(fts.featureTypeNames().iterator().next().toString());
                }

                cst.setDescription(visit(fts.getDescription(), null));
                cst.setName(fts.getName());

                for (final SemanticType semantic : fts.semanticTypeIdentifiers()) {

                    if (SemanticType.ANY.equals(semantic)) {
                        cst.getSemanticTypeIdentifier().add(GENERIC_ANY);
                    } else if (SemanticType.POINT.equals(semantic)) {
                        cst.getSemanticTypeIdentifier().add(GENERIC_POINT);
                    } else if (SemanticType.LINE.equals(semantic)) {
                        cst.getSemanticTypeIdentifier().add(GENERIC_LINE);
                    } else if (SemanticType.POLYGON.equals(semantic)) {
                        cst.getSemanticTypeIdentifier().add(GENERIC_POLYGON);
                    } else if (SemanticType.TEXT.equals(semantic)) {
                        cst.getSemanticTypeIdentifier().add(GENERIC_TEXT);
                    } else if (SemanticType.RASTER.equals(semantic)) {
                        cst.getSemanticTypeIdentifier().add(GENERIC_RASTER);
                    } else {
                        cst.getSemanticTypeIdentifier().add(semantic.identifier());
                    }

                }

                for (final Rule rule : fts.rules()) {
                    cst.getRuleOrOnlineResource().add(visit(rule, null));
                }

                obj = cst;
            } else {
                //feature type
                final FeatureTypeStyleType ftst = se_factory.createFeatureTypeStyleType();

                if (!fts.featureTypeNames().isEmpty()) {
                    final GenericName name = fts.featureTypeNames().iterator().next();
                    final String pre = name.scope().isGlobal() ? null : name.scope().name().toString();
                    final String local = name.toString();
                    ftst.setFeatureTypeName(new QName(pre + ':', local));
                }

                ftst.setDescription(visit(fts.getDescription(), null));
                ftst.setName(fts.getName());

                for (final SemanticType semantic : fts.semanticTypeIdentifiers()) {

                    if (SemanticType.ANY.equals(semantic)) {
                        ftst.getSemanticTypeIdentifier().add(GENERIC_ANY);
                    } else if (SemanticType.POINT.equals(semantic)) {
                        ftst.getSemanticTypeIdentifier().add(GENERIC_POINT);
                    } else if (SemanticType.LINE.equals(semantic)) {
                        ftst.getSemanticTypeIdentifier().add(GENERIC_LINE);
                    } else if (SemanticType.POLYGON.equals(semantic)) {
                        ftst.getSemanticTypeIdentifier().add(GENERIC_POLYGON);
                    } else if (SemanticType.TEXT.equals(semantic)) {
                        ftst.getSemanticTypeIdentifier().add(GENERIC_TEXT);
                    } else if (SemanticType.RASTER.equals(semantic)) {
                        ftst.getSemanticTypeIdentifier().add(GENERIC_RASTER);
                    } else {
                        ftst.getSemanticTypeIdentifier().add(semantic.identifier());
                    }

                }

                for (final Rule rule : fts.rules()) {
                    ftst.getRuleOrOnlineResource().add(visit(rule, null));
                }

                obj = ftst;
            }

            return obj;
        }
    }

    /**
     * Transform a GT rule in jaxb rule or OnlineResource
     */
    @Override
    public Object visit(final Rule rule, final Object data) {
        if (rule.getOnlineResource() != null) {
            //we store only the online resource
            return visit(rule.getOnlineResource(), null);
        }

        final RuleType rt = se_factory.createRuleType();
        rt.setName(rule.getName());
        rt.setDescription(visit(rule.getDescription(), null));

        if (rule.isElseFilter()) {
            rt.setElseFilter(se_factory.createElseFilterType());
        } else if (rule.getFilter() != null) {
            rt.setFilter(apply(rule.getFilter()));
        }

        if (rule.getLegend() != null) {
            rt.setLegendGraphic(visit(rule.getLegend(), null));
        }

        rt.setMaxScaleDenominator(rule.getMaxScaleDenominator());
        rt.setMinScaleDenominator(rule.getMinScaleDenominator());

        for (final Symbolizer symbol : rule.symbolizers()) {
            if (symbol instanceof LineSymbolizer) {
                rt.getSymbolizer().add(visit((LineSymbolizer) symbol, null));
            } else if (symbol instanceof PolygonSymbolizer) {
                rt.getSymbolizer().add(visit((PolygonSymbolizer) symbol, null));
            } else if (symbol instanceof PointSymbolizer) {
                rt.getSymbolizer().add(visit((PointSymbolizer) symbol, null));
            } else if (symbol instanceof RasterSymbolizer) {
                rt.getSymbolizer().add(visit((RasterSymbolizer) symbol, null));
            } else if (symbol instanceof TextSymbolizer) {
                rt.getSymbolizer().add(visit((TextSymbolizer) symbol, null));
            } else if (symbol instanceof ExtensionSymbolizer) {
                ((List) rt.getSymbolizer()).add(visit((ExtensionSymbolizer) symbol, null));
            }
        }

        return rt;
    }

    /**
     * Transform a GT point symbol in jaxb point symbol.
     */
    @Override
    public JAXBElement<PointSymbolizerType> visit(final PointSymbolizer point, final Object data) {
        final PointSymbolizerType pst = se_factory.createPointSymbolizerType();
        pst.setName(point.getName());
        pst.setDescription(visit(point.getDescription(), null));
        pst.setUom(visitUOM(point.getUnitOfMeasure()));
        pst.setGeometry(visitExpression(point.getGeometry()));

        if (point.getGraphic() != null) {
            pst.setGraphic(visit(point.getGraphic(), null));
        }
        return se_factory.createPointSymbolizer(pst);
    }

    /**
     * Transform a GT line symbol in jaxb line symbol.
     */
    @Override
    public JAXBElement<LineSymbolizerType> visit(final LineSymbolizer line, final Object data) {
        final LineSymbolizerType lst = se_factory.createLineSymbolizerType();
        lst.setName(line.getName());
        lst.setDescription(visit(line.getDescription(), null));
        lst.setUom(visitUOM(line.getUnitOfMeasure()));
        lst.setGeometry(visitExpression(line.getGeometry()));

        if (line.getStroke() != null) {
            lst.setStroke(visit(line.getStroke(), null));
        }
        lst.setPerpendicularOffset(visitExpression(line.getPerpendicularOffset()));
        return se_factory.createLineSymbolizer(lst);
    }

    /**
     * Transform a GT polygon symbol in a jaxb version.
     */
    @Override
    public JAXBElement<PolygonSymbolizerType> visit(final PolygonSymbolizer polygon, final Object data) {
        final PolygonSymbolizerType pst = se_factory.createPolygonSymbolizerType();
        pst.setName(polygon.getName());
        pst.setDescription(visit(polygon.getDescription(), null));
        pst.setUom(visitUOM(polygon.getUnitOfMeasure()));
        pst.setGeometry(visitExpression(polygon.getGeometry()));

        if (polygon.getDisplacement() != null) {
            pst.setDisplacement(visit(polygon.getDisplacement(), null));
        }

        if (polygon.getFill() != null) {
            pst.setFill(visit(polygon.getFill(), null));
        }

        pst.setPerpendicularOffset(visitExpression(polygon.getPerpendicularOffset()));

        if (polygon.getStroke() != null) {
            pst.setStroke(visit(polygon.getStroke(), null));
        }

        return se_factory.createPolygonSymbolizer(pst);
    }

    /**
     * Transform a GT text symbol in jaxb symbol.
     */
    @Override
    public JAXBElement<TextSymbolizerType> visit(final TextSymbolizer text, final Object data) {
        final TextSymbolizerType tst = se_factory.createTextSymbolizerType();
        tst.setName(text.getName());
        tst.setDescription(visit(text.getDescription(), null));
        tst.setUom(visitUOM(text.getUnitOfMeasure()));
        tst.setGeometry(visitExpression(text.getGeometry()));

        if (text.getHalo() != null) {
            tst.setHalo(visit(text.getHalo(), null));
        }

        if (text.getFont() != null) {
            tst.setFont(visit(text.getFont(), null));
        }

        tst.setLabel(visitExpression(text.getLabel()));

        if (text.getLabelPlacement() != null) {
            tst.setLabelPlacement(visit(text.getLabelPlacement(), null));
        }

        if (text.getFill() != null) {
            tst.setFill(visit(text.getFill(), null));
        }

        return se_factory.createTextSymbolizer(tst);
    }

    /**
     * Transform a GT raster symbolizer in jaxb raster symbolizer.
     */
    @Override
    public JAXBElement<RasterSymbolizerType> visit(final RasterSymbolizer raster, final Object data) {
        final RasterSymbolizerType tst = se_factory.createRasterSymbolizerType();
        tst.setName(raster.getName());
        tst.setDescription(visit(raster.getDescription(), null));
        tst.setUom(visitUOM(raster.getUnitOfMeasure()));
        tst.setGeometry(visitExpression(raster.getGeometry()));

        if (raster.getChannelSelection() != null) {
            tst.setChannelSelection(visit(raster.getChannelSelection(), null));
        }

        if (raster.getColorMap() != null) {
            tst.setColorMap(visit(raster.getColorMap(), null));
        }

        if (raster.getContrastEnhancement() != null) {
            tst.setContrastEnhancement(visit(raster.getContrastEnhancement(), null));
        }

        if (raster.getImageOutline() != null) {
            final ImageOutlineType iot = se_factory.createImageOutlineType();
            if (raster.getImageOutline() instanceof LineSymbolizer) {
                final LineSymbolizer ls = (LineSymbolizer) raster.getImageOutline();
                iot.setLineSymbolizer(visit(ls, null).getValue());
                tst.setImageOutline(iot);
            } else if (raster.getImageOutline() instanceof PolygonSymbolizer) {
                final PolygonSymbolizer ps = (PolygonSymbolizer) raster.getImageOutline();
                iot.setPolygonSymbolizer(visit(ps, null).getValue());
                tst.setImageOutline(iot);
            }
        }

        tst.setOpacity(visitExpression(raster.getOpacity()));

        if (raster.getOverlapBehavior() != null) {
            tst.setOverlapBehavior(visit(raster.getOverlapBehavior(), null));
        }

        if (raster.getShadedRelief() != null) {
            tst.setShadedRelief(visit(raster.getShadedRelief(), null));
        }

        return se_factory.createRasterSymbolizer(tst);
    }

    public JAXBElement<RangeType> visitRange(final Expression thredhold, final List<Symbolizer> symbols) {
        final RangeType type = se_factory.createRangeType();

        if (thredhold != null) {
            type.setThreshold(visitExpression(thredhold));
        }

        for (final Symbolizer symbol : symbols) {
            if (symbol instanceof LineSymbolizer) {
                type.getSymbolizer().add(visit((LineSymbolizer) symbol, null));
            } else if (symbol instanceof PolygonSymbolizer) {
                type.getSymbolizer().add(visit((PolygonSymbolizer) symbol, null));
            } else if (symbol instanceof PointSymbolizer) {
                type.getSymbolizer().add(visit((PointSymbolizer) symbol, null));
            } else if (symbol instanceof RasterSymbolizer) {
                type.getSymbolizer().add(visit((RasterSymbolizer) symbol, null));
            } else if (symbol instanceof TextSymbolizer) {
                type.getSymbolizer().add(visit((TextSymbolizer) symbol, null));
            } else if (symbol instanceof ExtensionSymbolizer) {
                ((List) type.getSymbolizer()).add(visit((ExtensionSymbolizer) symbol, null));
            }
        }

        return se_factory.createRange(type);
    }

    @Override
    public Object visit(final ExtensionSymbolizer ext, final Object data) {
        //we expect the extension to be a valid jaxb element
        return ext;
    }

    /**
     * transform a GT description in jaxb description.
     */
    @Override
    public DescriptionType visit(final Description description, final Object data) {
        final DescriptionType dt = se_factory.createDescriptionType();
        if (description != null) {
            if (description.getTitle() != null) {
                dt.setTitle(description.getTitle().toString());
            }
            if (description.getAbstract() != null) {
                dt.setAbstract(description.getAbstract().toString());
            }
        }
        return dt;
    }

    /**
     * Transform a GT displacement in jaxb displacement.
     */
    @Override
    public DisplacementType visit(final Displacement displacement, final Object data) {
        final DisplacementType disp = se_factory.createDisplacementType();
        disp.setDisplacementX(visitExpression(displacement.getDisplacementX()));
        disp.setDisplacementY(visitExpression(displacement.getDisplacementY()));
        return disp;
    }

    /**
     * Transform a GT fill in jaxb fill.
     */
    @Override
    public FillType visit(final Fill fill, final Object data) {
        final FillType ft = se_factory.createFillType();

        if (fill.getGraphicFill() != null) {
            ft.setGraphicFill(visit(fill.getGraphicFill(), null));
        }

        final List<SvgParameterType> svgs = ft.getSvgParameter();
        svgs.add(visitSVG(fill.getColor(), SEJAXBStatics.FILL));
        svgs.add(visitSVG(fill.getOpacity(), SEJAXBStatics.FILL_OPACITY));

        return ft;
    }

    /**
     * Transform a GT Font in jaxb font.
     */
    @Override
    public FontType visit(final Font font, final Object data) {
        final FontType ft = se_factory.createFontType();

        final List<SvgParameterType> svgs = ft.getSvgParameter();
        for (final Expression exp : font.getFamily()) {
            svgs.add(visitSVG(exp, SEJAXBStatics.FONT_FAMILY));
        }

        svgs.add(visitSVG(font.getSize(), SEJAXBStatics.FONT_SIZE));
        svgs.add(visitSVG(font.getStyle(), SEJAXBStatics.FONT_STYLE));
        svgs.add(visitSVG(font.getWeight(), SEJAXBStatics.FONT_WEIGHT));

        return ft;
    }

    /**
     * Transform a GT stroke in jaxb stroke.
     */
    @Override
    public StrokeType visit(final Stroke stroke, final Object data) {
        final StrokeType st = se_factory.createStrokeType();

        if (stroke.getGraphicFill() != null) {
            st.setGraphicFill(visit(stroke.getGraphicFill(), null));
        } else if (stroke.getGraphicStroke() != null) {
            st.setGraphicStroke(visit(stroke.getGraphicStroke(), null));
        }

        final List<SvgParameterType> svgs = st.getSvgParameter();
        svgs.add(visitSVG(stroke.getColor(), SEJAXBStatics.STROKE));
        if (stroke.getDashArray() != null) {
            svgs.add(visitSVG(stroke.getDashArray(), SEJAXBStatics.STROKE_DASHARRAY));
        }
        svgs.add(visitSVG(stroke.getDashOffset(), SEJAXBStatics.STROKE_DASHOFFSET));
        svgs.add(visitSVG(stroke.getLineCap(), SEJAXBStatics.STROKE_LINECAP));
        svgs.add(visitSVG(stroke.getLineJoin(), SEJAXBStatics.STROKE_LINEJOIN));
        svgs.add(visitSVG(stroke.getOpacity(), SEJAXBStatics.STROKE_OPACITY));
        svgs.add(visitSVG(stroke.getWidth(), SEJAXBStatics.STROKE_WIDTH));

        return st;
    }

    /**
     * transform a GT graphic in jaxb graphic
     */
    @Override
    public GraphicType visit(final Graphic graphic, final Object data) {
        final GraphicType gt = se_factory.createGraphicType();
        gt.setAnchorPoint(visit(graphic.getAnchorPoint(), null));
        for (final GraphicalSymbol gs : graphic.graphicalSymbols()) {
            if (gs instanceof Mark) {
                final Mark mark = (Mark) gs;
                gt.getExternalGraphicOrMark().add(visit(mark, null));
            } else if (gs instanceof ExternalMark) {
                final ExternalMark ext = (ExternalMark) gs;
                gt.getExternalGraphicOrMark().add(visit(ext, null));
            } else if (gs instanceof ExternalGraphic) {
                final ExternalGraphic ext = (ExternalGraphic) gs;
                gt.getExternalGraphicOrMark().add(visit(ext, null));
            }
        }

        gt.setDisplacement(visit(graphic.getDisplacement(), null));
        gt.setOpacity(visitExpression(graphic.getOpacity()));
        gt.setRotation(visitExpression(graphic.getRotation()));
        gt.setSize(visitExpression(graphic.getSize()));
        return gt;
    }

    /**
     * Transform a GT graphic fill in jaxb graphic fill.
     */
    @Override
    public GraphicFillType visit(final GraphicFill graphicFill, final Object data) {
        final GraphicFillType gft = se_factory.createGraphicFillType();
        gft.setGraphic(visit((Graphic) graphicFill, null));
        return gft;
    }

    /**
     * Transform a GT graphic stroke in jaxb graphic stroke.
     */
    @Override
    public GraphicStrokeType visit(final GraphicStroke graphicStroke, final Object data) {
        final GraphicStrokeType gst = se_factory.createGraphicStrokeType();
        gst.setGraphic(visit((Graphic) graphicStroke, null));
        gst.setGap(visitExpression(graphicStroke.getGap()));
        gst.setInitialGap(visitExpression(graphicStroke.getInitialGap()));
        return gst;
    }

    @Override
    public MarkType visit(final Mark mark, final Object data) {
        final MarkType mt = se_factory.createMarkType();
        mt.setFill(visit(mark.getFill(), null));
        mt.setStroke(visit(mark.getStroke(), null));

        if (mark.getExternalMark() != null) {
            mt.setOnlineResource(visit(mark.getExternalMark().getOnlineResource(), null));
            mt.setFormat(mark.getExternalMark().getFormat());
            mt.setMarkIndex(new BigInteger(String.valueOf(mark.getExternalMark().getMarkIndex())));

            final ExternalMark em = mark.getExternalMark();
            if(em!=null && em.getInlineContent() != null){
                final Icon icon = em.getInlineContent();

                final InlineContentType ict = new InlineContentType();
                ict.setEncoding(em.getFormat());

                final Image image;
                if(icon instanceof ImageIcon){
                    image = ((ImageIcon)icon).getImage();
                }else{
                    image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                    icon.paintIcon(null, image.getGraphics(), 0, 0);
                }

                try {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ImageIO.write((RenderedImage)image, "PNG", out);
                    final String chars = Base64.getEncoder().encodeToString(out.toByteArray());
                    ict.getContent().add(chars);
                    mt.setInlineContent(ict);
                } catch (IOException ex) {
                    Logger.getLogger("org.geotoolkit.sld.xml").log(Level.WARNING, null, ex);
                }
            }

        } else {
            StyleUtilities
                    .extractWellKnownName(mark)
                    .ifPresent(mt::setWellKnownName);
        }

        return mt;
    }

    /**
     * Not usable for SLD, See visit(Mark) method.
     */
    @Override
    public Object visit(final ExternalMark externalMark, final Object data) {
        return null;
    }

    @Override
    public ExternalGraphicType visit(final ExternalGraphic externalGraphic, final Object data) {
        final ExternalGraphicType egt = se_factory.createExternalGraphicType();
        egt.setFormat(externalGraphic.getFormat());

        if (externalGraphic.getInlineContent() != null) {
            final Icon icon = externalGraphic.getInlineContent();

            final InlineContentType ict = new InlineContentType();
            ict.setEncoding("base64");

            Image image;
            if(icon instanceof ImageIcon){
                image = ((ImageIcon)icon).getImage();
            }else{
                image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                icon.paintIcon(null, image.getGraphics(), 0, 0);
            }

            if(!(image instanceof BufferedImage)){
                final BufferedImage bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                bi.createGraphics().drawImage(image, new AffineTransform(), null);
                image = bi;
            }

            try {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write((RenderedImage)image, "PNG", out);
                final String chars = Base64.getEncoder().encodeToString(out.toByteArray());
                ict.getContent().add(chars);
                egt.setInlineContent(ict);
            } catch (IOException ex) {
                Logger.getLogger("org.geotoolkit.sld.xml").log(Level.WARNING, null, ex);
            }
        }

        if (externalGraphic.getOnlineResource() != null) {
            egt.setOnlineResource(visit(externalGraphic.getOnlineResource(), null));
        }

        for (final ColorReplacement cr : externalGraphic.getColorReplacements()) {
            egt.getColorReplacement().add(visit(cr, data));
        }

        return egt;
    }

    /**
     * transform a GT point placement in jaxb point placement.
     */
    @Override
    public PointPlacementType visit(final PointPlacement pointPlacement, final Object data) {
        final PointPlacementType ppt = se_factory.createPointPlacementType();
        ppt.setAnchorPoint(visit(pointPlacement.getAnchorPoint(), null));
        ppt.setDisplacement(visit(pointPlacement.getDisplacement(), null));
        ppt.setRotation(visitExpression(pointPlacement.getRotation()));
        return ppt;
    }

    /**
     * transform a GT anchor point in jaxb anchor point.
     */
    @Override
    public AnchorPointType visit(final AnchorPoint anchorPoint, final Object data) {
        final AnchorPointType apt = se_factory.createAnchorPointType();
        apt.setAnchorPointX(visitExpression(anchorPoint.getAnchorPointX()));
        apt.setAnchorPointY(visitExpression(anchorPoint.getAnchorPointY()));
        return apt;
    }

    /**
     * transform a GT lineplacement in jaxb line placement.
     */
    @Override
    public LinePlacementType visit(final LinePlacement linePlacement, final Object data) {
        final LinePlacementType lpt = se_factory.createLinePlacementType();
        lpt.setGap(visitExpression(linePlacement.getGap()));
        lpt.setGeneralizeLine(linePlacement.isGeneralizeLine());
        lpt.setInitialGap(visitExpression(linePlacement.getInitialGap()));
        lpt.setIsAligned(linePlacement.IsAligned());
        lpt.setIsRepeated(linePlacement.isRepeated());
        lpt.setPerpendicularOffset(visitExpression(linePlacement.getPerpendicularOffset()));
        return lpt;
    }

    /**
     * Transform a GT label placement in jaxb label placement.
     *
     * @return
     */
    public LabelPlacementType visit(final LabelPlacement labelPlacement, final Object data) {
        final LabelPlacementType lpt = se_factory.createLabelPlacementType();
        if (labelPlacement instanceof LinePlacement) {
            final LinePlacement lp = (LinePlacement) labelPlacement;
            lpt.setLinePlacement(visit(lp, null));
        } else if (labelPlacement instanceof PointPlacement) {
            final PointPlacement pp = (PointPlacement) labelPlacement;
            lpt.setPointPlacement(visit(pp, null));
        }
        return lpt;
    }

    /**
     * Transform a GT graphicLegend in jaxb graphic legend
     */
    @Override
    public LegendGraphicType visit(final GraphicLegend graphicLegend, final Object data) {
        final LegendGraphicType lgt = se_factory.createLegendGraphicType();
        lgt.setGraphic(visit((Graphic) graphicLegend, null));
        return lgt;
    }

    /**
     * Transform a GT onlineResource in jaxb online resource.
     */
    public org.geotoolkit.se.xml.v110.OnlineResourceType visit(final OnlineResource onlineResource, final Object data) {
        final OnlineResourceType ort = se_factory.createOnlineResourceType();
        ort.setHref(onlineResource.getLinkage().toString());
        return ort;
    }

    /**
     * transform a GT halo in a jaxb halo.
     */
    @Override
    public HaloType visit(final Halo halo, final Object data) {
        final HaloType ht = se_factory.createHaloType();
        ht.setFill(visit(halo.getFill(), null));
        ht.setRadius(visitExpression(halo.getRadius()));
        return ht;
    }

    @Override
    public ColorMapType visit(final ColorMap colorMap, final Object data) {
//TODO Fix that when better undestanding raster functions.
        final org.geotoolkit.se.xml.v110.ColorMapType cmt = se_factory.createColorMapType();

        final Expression fct = colorMap.getFunction();
        if (fct instanceof Categorize) {
            cmt.setCategorize(visit((Categorize) fct));
        } else if (fct instanceof Interpolate) {
            cmt.setInterpolate(visit((Interpolate) fct));
        } else if (fct instanceof Jenks) {
            cmt.setJenks(visit((Jenks) fct));
        }

        return cmt;
    }

    public CategorizeType visit(final Categorize categorize) {
        final CategorizeType type = se_factory.createCategorizeType();
        type.setFallbackValue(categorize.getFallbackValue().getValue().toString());
        type.setLookupValue(visitExpression(categorize.getLookupValue()));

        if (ThreshholdsBelongTo.PRECEDING == categorize.getBelongTo()) {
            type.setThreshholdsBelongTo(ThreshholdsBelongToType.PRECEDING);
        } else {
            type.setThreshholdsBelongTo(ThreshholdsBelongToType.SUCCEEDING);
        }

        final Map<Expression, Expression> steps = categorize.getThresholds();

        final List<JAXBElement<ParameterValueType>> elements = type.getThresholdAndTValue();
        elements.clear();
        int i=0;
        for(Entry<Expression,Expression> entry : steps.entrySet()){
            final Expression key = entry.getKey();
            final Expression val = entry.getValue();
            if(i==0){
                //first element is for -infinity
                type.setValue(visitExpression(val));
            }else{
                elements.add(se_factory.createThreshold(visitExpression(key)));
                elements.add(se_factory.createTValue(visitExpression(val)));
            }
            i++;
        }

        return type;
    }

    public InterpolateType visit(final Interpolate interpolate) {
        final InterpolateType type = se_factory.createInterpolateType();
        type.setFallbackValue(interpolate.getFallbackValue().toString());
        type.setLookupValue(visitExpression(interpolate.getLookupValue()));

        if (interpolate.getMethod() == Method.COLOR) {
            type.setMethod(MethodType.COLOR);
        } else {
            type.setMethod(MethodType.NUMERIC);
        }

        final Mode mode = interpolate.getMode();
        if (mode == Mode.COSINE) {
            type.setMode(ModeType.COSINE);
        } else if (mode == Mode.CUBIC) {
            type.setMode(ModeType.CUBIC);
        } else {
            type.setMode(ModeType.LINEAR);
        }

        final List<InterpolationPointType> points = type.getInterpolationPoint();
        points.clear();
        for (final InterpolationPoint ip : interpolate.getInterpolationPoints()) {
            final InterpolationPointType point = se_factory.createInterpolationPointType();
            point.setData(ip.getData().doubleValue());
            point.setValue(visitExpression(ip.getValue()));
            points.add(point);
        }

        return type;
    }

    public JenksType visit(final Jenks jenks) {
        final JenksType type = se_factory.createJenksType();
        type.setClassNumber(Integer.valueOf(jenks.getClassNumber().getValue().toString()));
        type.setPalette(jenks.getPalette().getValue().toString());

        return type;
    }

    @Override
    public ColorReplacementType visit(final ColorReplacement colorReplacement, final Object data) {
        final ColorReplacementType crt = se_factory.createColorReplacementType();
        final Expression fct = colorReplacement.getRecoding();

        if (fct instanceof RecolorFunction) {
            final RecolorFunction rf = (RecolorFunction) fct;
            crt.setRecolor(visit(rf));
        }

//        if(fct instanceof RecodeFunction){
//            final RecodeFunction recode = (RecodeFunction) fct;
//            final RecodeType rt = se_factory.createRecodeType();
//
//            for(final Expression exp : recode.getParameters()){
//                final MapItemType mit = se_factory.createMapItemType();
//                mit.setValue(visitExpression(exp));
//                rt.getMapItem().add(mit);
//            }
//
//            rt.setLookupValue(visitExpression(FactoryFinder.getFilterFactory(null).literal(RecodeFunction.RASTER_DATA)));
//            crt.setRecode(rt);
//        }
        return crt;
    }

    public RecolorType visit(final RecolorFunction fct) {
        RecolorType rt = new RecolorType();

        for (ColorItem item : fct.getColorItems()) {
            final ColorItemType cit = new ColorItemType();
            final Literal data = item.getSourceColor();
            final Literal value = item.getTargetColor();
            cit.setData(visitExpression(data));
            cit.setValue(visitExpression(value));
            rt.getColorItem().add(cit);
        }

        return rt;
    }

    /**
     * Transform a GT constrast enchancement in jaxb constrast enchancement
     */
    @Override
    public ContrastEnhancementType visit(final ContrastEnhancement contrastEnhancement, final Object data) {
        final ContrastEnhancementType cet = se_factory.createContrastEnhancementType();
        final Number gamma = (Number) contrastEnhancement.getGammaValue().apply(null);
        cet.setGammaValue(gamma != null ? gamma.doubleValue() : null);

        final ContrastMethod cm = contrastEnhancement.getMethod();
        if (ContrastMethod.HISTOGRAM.equals(cm)) {
            cet.setHistogram(se_factory.createHistogramType());
        } else if (ContrastMethod.NORMALIZE.equals(cm)) {
            cet.setNormalize(se_factory.createNormalizeType());
        }

        return cet;
    }

    /**
     * Transform a GT channel selection in jaxb channel selection.
     */
    @Override
    public ChannelSelectionType visit(final ChannelSelection channelSelection, final Object data) {
        final ChannelSelectionType cst = se_factory.createChannelSelectionType();

        if (channelSelection.getRGBChannels() != null) {
            SelectedChannelType[] scts = channelSelection.getRGBChannels();
            cst.setRedChannel(visit(scts[0], null));
            cst.setGreenChannel(visit(scts[1], null));
            cst.setBlueChannel(visit(scts[2], null));

        } else if (channelSelection.getGrayChannel() != null) {
            cst.setGrayChannel(visit(channelSelection.getGrayChannel(), null));
        }

        return cst;
    }

    /**
     * transform a GT overlap in xml string representation.
     */
    public String visit(final OverlapBehavior overlapBehavior, final Object data) {
        switch (overlapBehavior) {
            case AVERAGE:
                return SEJAXBStatics.OVERLAP_AVERAGE;
            case EARLIEST_ON_TOP:
                return SEJAXBStatics.OVERLAP_EARLIEST_ON_TOP;
            case LATEST_ON_TOP:
                return SEJAXBStatics.OVERLAP_LATEST_ON_TOP;
            case RANDOM:
                return SEJAXBStatics.OVERLAP_RANDOM;
            default:
                return null;
        }
    }

    /**
     * transform a GT channel type in jaxb channel type.
     */
    @Override
    public org.geotoolkit.se.xml.v110.SelectedChannelType visit(final SelectedChannelType selectChannelType, final Object data) {
        final org.geotoolkit.se.xml.v110.SelectedChannelType sct = se_factory.createSelectedChannelType();
        sct.setContrastEnhancement(visit(selectChannelType.getContrastEnhancement(), null));
        sct.setSourceChannelName(selectChannelType.getChannelName());
        return sct;
    }

    /**
     * Transform a GT shaded relief in jaxb shaded relief.
     */
    @Override
    public ShadedReliefType visit(final ShadedRelief shadedRelief, final Object data) {
        final ShadedReliefType srt = se_factory.createShadedReliefType();
        srt.setBrightnessOnly(shadedRelief.isBrightnessOnly());
        final Number rf = (Number) shadedRelief.getReliefFactor().apply(null);
        srt.setReliefFactor(rf != null ? rf.doubleValue() : null);
        return srt;
    }
}
