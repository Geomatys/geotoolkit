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
package org.geotoolkit.se.xml.v110;

import java.math.BigInteger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import org.geotoolkit.se.xml.vext.JenksType;
import org.geotoolkit.se.xml.vext.RangeType;
import org.geotoolkit.se.xml.vext.RecolorType;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the net.opengis.se package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 *
 * @module
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _WellKnownName_QNAME = new QName("http://www.opengis.net/se", "WellKnownName");
    private static final QName _Format_QNAME = new QName("http://www.opengis.net/se", "Format");
    private static final QName _Geometry_QNAME = new QName("http://www.opengis.net/se", "Geometry");
    private static final QName _LookupString_QNAME = new QName("http://www.opengis.net/se", "LookupString");
    private static final QName _SourceChannelName_QNAME = new QName("http://www.opengis.net/se", "SourceChannelName");
    private static final QName _PointSymbolizer_QNAME = new QName("http://www.opengis.net/se", "PointSymbolizer");
    private static final QName _DisplacementY_QNAME = new QName("http://www.opengis.net/se", "DisplacementY");
    private static final QName _DisplacementX_QNAME = new QName("http://www.opengis.net/se", "DisplacementX");
    private static final QName _GammaValue_QNAME = new QName("http://www.opengis.net/se", "GammaValue");
    private static final QName _Rotation_QNAME = new QName("http://www.opengis.net/se", "Rotation");
    private static final QName _TValue_QNAME = new QName("http://www.opengis.net/se", "TValue");
    private static final QName _NegativePattern_QNAME = new QName("http://www.opengis.net/se", "NegativePattern");
    private static final QName _FeatureTypeStyle_QNAME = new QName("http://www.opengis.net/se", "FeatureTypeStyle");
    private static final QName _OverlapBehavior_QNAME = new QName("http://www.opengis.net/se", "OverlapBehavior");
    private static final QName _Interpolate_QNAME = new QName("http://www.opengis.net/se", "Interpolate");
    private static final QName _ColorMap_QNAME = new QName("http://www.opengis.net/se", "ColorMap");
    private static final QName _Stroke_QNAME = new QName("http://www.opengis.net/se", "Stroke");
    private static final QName _Function_QNAME = new QName("http://www.opengis.net/se", "Function");
    private static final QName _Label_QNAME = new QName("http://www.opengis.net/se", "Label");
    private static final QName _Radius_QNAME = new QName("http://www.opengis.net/se", "Radius");
    private static final QName _ExternalGraphic_QNAME = new QName("http://www.opengis.net/se", "ExternalGraphic");
    private static final QName _SemanticTypeIdentifier_QNAME = new QName("http://www.opengis.net/se", "SemanticTypeIdentifier");
    private static final QName _CoverageStyle_QNAME = new QName("http://www.opengis.net/se", "CoverageStyle");
    private static final QName _GrayChannel_QNAME = new QName("http://www.opengis.net/se", "GrayChannel");
    private static final QName _LegendGraphic_QNAME = new QName("http://www.opengis.net/se", "LegendGraphic");
    private static final QName _Value_QNAME = new QName("http://www.opengis.net/se", "Value");
    private static final QName _Pattern_QNAME = new QName("http://www.opengis.net/se", "Pattern");
    private static final QName _TextSymbolizer_QNAME = new QName("http://www.opengis.net/se", "TextSymbolizer");
    private static final QName _IsAligned_QNAME = new QName("http://www.opengis.net/se", "IsAligned");
    private static final QName _InterpolationPoint_QNAME = new QName("http://www.opengis.net/se", "InterpolationPoint");
    private static final QName _RedChannel_QNAME = new QName("http://www.opengis.net/se", "RedChannel");
    private static final QName _InlineContent_QNAME = new QName("http://www.opengis.net/se", "InlineContent");
    private static final QName _Normalize_QNAME = new QName("http://www.opengis.net/se", "Normalize");
    private static final QName _MaxScaleDenominator_QNAME = new QName("http://www.opengis.net/se", "MaxScaleDenominator");
    private static final QName _StringPosition_QNAME = new QName("http://www.opengis.net/se", "StringPosition");
    private static final QName _Graphic_QNAME = new QName("http://www.opengis.net/se", "Graphic");
    private static final QName _SvgParameter_QNAME = new QName("http://www.opengis.net/se", "SvgParameter");
    private static final QName _DateValue_QNAME = new QName("http://www.opengis.net/se", "DateValue");
    private static final QName _Substring_QNAME = new QName("http://www.opengis.net/se", "Substring");
    private static final QName _Threshold_QNAME = new QName("http://www.opengis.net/se", "Threshold");
    private static final QName _GeneralizeLine_QNAME = new QName("http://www.opengis.net/se", "GeneralizeLine");
    private static final QName _MapItem_QNAME = new QName("http://www.opengis.net/se", "MapItem");
    private static final QName _Description_QNAME = new QName("http://www.opengis.net/se", "Description");
    private static final QName _Position_QNAME = new QName("http://www.opengis.net/se", "Position");
    private static final QName _GraphicStroke_QNAME = new QName("http://www.opengis.net/se", "GraphicStroke");
    private static final QName _FormatDate_QNAME = new QName("http://www.opengis.net/se", "FormatDate");
    private static final QName _BlueChannel_QNAME = new QName("http://www.opengis.net/se", "BlueChannel");
    private static final QName _LineSymbolizer_QNAME = new QName("http://www.opengis.net/se", "LineSymbolizer");
    private static final QName _ChannelSelection_QNAME = new QName("http://www.opengis.net/se", "ChannelSelection");
    private static final QName _Data_QNAME = new QName("http://www.opengis.net/se", "Data");
    private static final QName _LinePlacement_QNAME = new QName("http://www.opengis.net/se", "LinePlacement");
    private static final QName _Opacity_QNAME = new QName("http://www.opengis.net/se", "Opacity");
    private static final QName _Categorize_QNAME = new QName("http://www.opengis.net/se", "Categorize");
    private static final QName _InitialGap_QNAME = new QName("http://www.opengis.net/se", "InitialGap");
    private static final QName _LabelPlacement_QNAME = new QName("http://www.opengis.net/se", "LabelPlacement");
    private static final QName _AnchorPointY_QNAME = new QName("http://www.opengis.net/se", "AnchorPointY");
    private static final QName _AnchorPointX_QNAME = new QName("http://www.opengis.net/se", "AnchorPointX");
    private static final QName _Trim_QNAME = new QName("http://www.opengis.net/se", "Trim");
    private static final QName _ShadedRelief_QNAME = new QName("http://www.opengis.net/se", "ShadedRelief");
    private static final QName _ColorReplacement_QNAME = new QName("http://www.opengis.net/se", "ColorReplacement");
    private static final QName _ReliefFactor_QNAME = new QName("http://www.opengis.net/se", "ReliefFactor");
    private static final QName _GraphicFill_QNAME = new QName("http://www.opengis.net/se", "GraphicFill");
    private static final QName _AnchorPoint_QNAME = new QName("http://www.opengis.net/se", "AnchorPoint");
    private static final QName _PerpendicularOffset_QNAME = new QName("http://www.opengis.net/se", "PerpendicularOffset");
    private static final QName _OnlineResource_QNAME = new QName("http://www.opengis.net/se", "OnlineResource");
    private static final QName _Font_QNAME = new QName("http://www.opengis.net/se", "Font");
    private static final QName _ContrastEnhancement_QNAME = new QName("http://www.opengis.net/se", "ContrastEnhancement");
    private static final QName _StringValue_QNAME = new QName("http://www.opengis.net/se", "StringValue");
    private static final QName _BrightnessOnly_QNAME = new QName("http://www.opengis.net/se", "BrightnessOnly");
    private static final QName _GreenChannel_QNAME = new QName("http://www.opengis.net/se", "GreenChannel");
    private static final QName _Length_QNAME = new QName("http://www.opengis.net/se", "Length");
    private static final QName _Halo_QNAME = new QName("http://www.opengis.net/se", "Halo");
    private static final QName _MinScaleDenominator_QNAME = new QName("http://www.opengis.net/se", "MinScaleDenominator");
    private static final QName _NumericValue_QNAME = new QName("http://www.opengis.net/se", "NumericValue");
    private static final QName _BaseSymbolizer_QNAME = new QName("http://www.opengis.net/se", "BaseSymbolizer");
    private static final QName _MarkIndex_QNAME = new QName("http://www.opengis.net/se", "MarkIndex");
    private static final QName _Displacement_QNAME = new QName("http://www.opengis.net/se", "Displacement");
    private static final QName _IsRepeated_QNAME = new QName("http://www.opengis.net/se", "IsRepeated");
    private static final QName _Symbolizer_QNAME = new QName("http://www.opengis.net/se", "Symbolizer");
    private static final QName _Size_QNAME = new QName("http://www.opengis.net/se", "Size");
    private static final QName _FeatureTypeName_QNAME = new QName("http://www.opengis.net/se", "FeatureTypeName");
    private static final QName _PolygonSymbolizer_QNAME = new QName("http://www.opengis.net/se", "PolygonSymbolizer");
    private static final QName _ImageOutline_QNAME = new QName("http://www.opengis.net/se", "ImageOutline");
    private static final QName _CoverageName_QNAME = new QName("http://www.opengis.net/se", "CoverageName");
    private static final QName _Recode_QNAME = new QName("http://www.opengis.net/se", "Recode");
    private static final QName _LookupValue_QNAME = new QName("http://www.opengis.net/se", "LookupValue");
    private static final QName _Fill_QNAME = new QName("http://www.opengis.net/se", "Fill");
    private static final QName _PointPlacement_QNAME = new QName("http://www.opengis.net/se", "PointPlacement");
    private static final QName _Histogram_QNAME = new QName("http://www.opengis.net/se", "Histogram");
    private static final QName _ElseFilter_QNAME = new QName("http://www.opengis.net/se", "ElseFilter");
    private static final QName _Mark_QNAME = new QName("http://www.opengis.net/se", "Mark");
    private static final QName _StringLength_QNAME = new QName("http://www.opengis.net/se", "StringLength");
    private static final QName _Concatenate_QNAME = new QName("http://www.opengis.net/se", "Concatenate");
    private static final QName _Rule_QNAME = new QName("http://www.opengis.net/se", "Rule");
    private static final QName _Gap_QNAME = new QName("http://www.opengis.net/se", "Gap");
    private static final QName _RasterSymbolizer_QNAME = new QName("http://www.opengis.net/se", "RasterSymbolizer");
    private static final QName _FormatNumber_QNAME = new QName("http://www.opengis.net/se", "FormatNumber");
    private static final QName _Name_QNAME = new QName("http://www.opengis.net/se", "Name");
    private static final QName _ChangeCase_QNAME = new QName("http://www.opengis.net/se", "ChangeCase");

    //extension ----------------------------------------------------------------
    private static final QName _Range_QNAME = new QName("http://www.opengis.net/se", "Range");
    private static final QName _Recolor_QNAME = new QName("http://www.opengis.net/se", "Recolor");
    private static final QName _Jenks_QNAME = new QName("http://www.opengis.net/se", "Jenks");
    //extension ----------------------------------------------------------------

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.se
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LabelPlacementType }
     *
     */
    public LabelPlacementType createLabelPlacementType() {
        return new LabelPlacementType();
    }

    /**
     * Create an instance of {@link ShadedReliefType }
     *
     */
    public ShadedReliefType createShadedReliefType() {
        return new ShadedReliefType();
    }

    /**
     * Create an instance of {@link ChannelSelectionType }
     *
     */
    public ChannelSelectionType createChannelSelectionType() {
        return new ChannelSelectionType();
    }

    /**
     * Create an instance of {@link RecodeType }
     *
     */
    public RecodeType createRecodeType() {
        return new RecodeType();
    }

    /**
     * Create an instance of {@link RasterSymbolizerType }
     *
     */
    public RasterSymbolizerType createRasterSymbolizerType() {
        return new RasterSymbolizerType();
    }

    /**
     * Create an instance of {@link GraphicType }
     *
     */
    public GraphicType createGraphicType() {
        return new GraphicType();
    }

    /**
     * Create an instance of {@link FillType }
     *
     */
    public FillType createFillType() {
        return new FillType();
    }

    /**
     * Create an instance of {@link NormalizeType }
     *
     */
    public NormalizeType createNormalizeType() {
        return new NormalizeType();
    }

    /**
     * Create an instance of {@link StringPositionType }
     *
     */
    public StringPositionType createStringPositionType() {
        return new StringPositionType();
    }

    /**
     * Create an instance of {@link GraphicStrokeType }
     *
     */
    public GraphicStrokeType createGraphicStrokeType() {
        return new GraphicStrokeType();
    }

    /**
     * Create an instance of {@link OnlineResourceType }
     *
     */
    public OnlineResourceType createOnlineResourceType() {
        return new OnlineResourceType();
    }

    /**
     * Create an instance of {@link LineSymbolizerType }
     *
     */
    public LineSymbolizerType createLineSymbolizerType() {
        return new LineSymbolizerType();
    }

    /**
     * Create an instance of {@link StringLengthType }
     *
     */
    public StringLengthType createStringLengthType() {
        return new StringLengthType();
    }

    /**
     * Create an instance of {@link GeometryType }
     *
     */
    public GeometryType createGeometryType() {
        return new GeometryType();
    }

    /**
     * Create an instance of {@link StrokeType }
     *
     */
    public StrokeType createStrokeType() {
        return new StrokeType();
    }

    /**
     * Create an instance of {@link ImageOutlineType }
     *
     */
    public ImageOutlineType createImageOutlineType() {
        return new ImageOutlineType();
    }

    /**
     * Create an instance of {@link InterpolationPointType }
     *
     */
    public InterpolationPointType createInterpolationPointType() {
        return new InterpolationPointType();
    }

    /**
     * Create an instance of {@link ConcatenateType }
     *
     */
    public ConcatenateType createConcatenateType() {
        return new ConcatenateType();
    }

    /**
     * Create an instance of {@link PointSymbolizerType }
     *
     */
    public PointSymbolizerType createPointSymbolizerType() {
        return new PointSymbolizerType();
    }

    /**
     * Create an instance of {@link InlineContentType }
     *
     */
    public InlineContentType createInlineContentType() {
        return new InlineContentType();
    }

    /**
     * Create an instance of {@link ExternalGraphicType }
     *
     */
    public ExternalGraphicType createExternalGraphicType() {
        return new ExternalGraphicType();
    }

    /**
     * Create an instance of {@link DisplacementType }
     *
     */
    public DisplacementType createDisplacementType() {
        return new DisplacementType();
    }

    /**
     * Create an instance of {@link ColorMapType }
     *
     */
    public ColorMapType createColorMapType() {
        return new ColorMapType();
    }

    /**
     * Create an instance of {@link SubstringType }
     *
     */
    public SubstringType createSubstringType() {
        return new SubstringType();
    }

    /**
     * Create an instance of {@link PolygonSymbolizerType }
     *
     */
    public PolygonSymbolizerType createPolygonSymbolizerType() {
        return new PolygonSymbolizerType();
    }

    /**
     * Create an instance of {@link FormatDateType }
     *
     */
    public FormatDateType createFormatDateType() {
        return new FormatDateType();
    }

    /**
     * Create an instance of {@link InterpolateType }
     *
     */
    public InterpolateType createInterpolateType() {
        return new InterpolateType();
    }

    /**
     * Create an instance of {@link GraphicFillType }
     *
     */
    public GraphicFillType createGraphicFillType() {
        return new GraphicFillType();
    }

    /**
     * Create an instance of {@link FeatureTypeStyleType }
     *
     */
    public FeatureTypeStyleType createFeatureTypeStyleType() {
        return new FeatureTypeStyleType();
    }

    /**
     * Create an instance of {@link PointPlacementType }
     *
     */
    public PointPlacementType createPointPlacementType() {
        return new PointPlacementType();
    }

    /**
     * Create an instance of {@link ElseFilterType }
     *
     */
    public ElseFilterType createElseFilterType() {
        return new ElseFilterType();
    }

    /**
     * Create an instance of {@link SvgParameterType }
     *
     */
    public SvgParameterType createSvgParameterType() {
        return new SvgParameterType();
    }

    /**
     * Create an instance of {@link ChangeCaseType }
     *
     */
    public ChangeCaseType createChangeCaseType() {
        return new ChangeCaseType();
    }

    /**
     * Create an instance of {@link CategorizeType }
     *
     */
    public CategorizeType createCategorizeType() {
        return new CategorizeType();
    }

    /**
     * Create an instance of {@link LinePlacementType }
     *
     */
    public LinePlacementType createLinePlacementType() {
        return new LinePlacementType();
    }

    /**
     * Create an instance of {@link TextSymbolizerType }
     *
     */
    public TextSymbolizerType createTextSymbolizerType() {
        return new TextSymbolizerType();
    }

    /**
     * Create an instance of {@link BaseSymbolizerType }
     *
     */
    public BaseSymbolizerType createBaseSymbolizerType() {
        return new BaseSymbolizerType();
    }

    /**
     * Create an instance of {@link HistogramType }
     *
     */
    public HistogramType createHistogramType() {
        return new HistogramType();
    }

    /**
     * Create an instance of {@link FontType }
     *
     */
    public FontType createFontType() {
        return new FontType();
    }

    /**
     * Create an instance of {@link AnchorPointType }
     *
     */
    public AnchorPointType createAnchorPointType() {
        return new AnchorPointType();
    }

    /**
     * Create an instance of {@link FormatNumberType }
     *
     */
    public FormatNumberType createFormatNumberType() {
        return new FormatNumberType();
    }

    /**
     * Create an instance of {@link ParameterValueType }
     *
     */
    public ParameterValueType createParameterValueType() {
        return new ParameterValueType();
    }

    /**
     * Create an instance of {@link HaloType }
     *
     */
    public HaloType createHaloType() {
        return new HaloType();
    }

    /**
     * Create an instance of {@link CoverageStyleType }
     *
     */
    public CoverageStyleType createCoverageStyleType() {
        return new CoverageStyleType();
    }

    /**
     * Create an instance of {@link SelectedChannelType }
     *
     */
    public SelectedChannelType createSelectedChannelType() {
        return new SelectedChannelType();
    }

    /**
     * Create an instance of {@link LegendGraphicType }
     *
     */
    public LegendGraphicType createLegendGraphicType() {
        return new LegendGraphicType();
    }

    /**
     * Create an instance of {@link RuleType }
     *
     */
    public RuleType createRuleType() {
        return new RuleType();
    }

    /**
     * Create an instance of {@link DescriptionType }
     *
     */
    public DescriptionType createDescriptionType() {
        return new DescriptionType();
    }

    /**
     * Create an instance of {@link ContrastEnhancementType }
     *
     */
    public ContrastEnhancementType createContrastEnhancementType() {
        return new ContrastEnhancementType();
    }

    /**
     * Create an instance of {@link TrimType }
     *
     */
    public TrimType createTrimType() {
        return new TrimType();
    }

    /**
     * Create an instance of {@link MarkType }
     *
     */
    public MarkType createMarkType() {
        return new MarkType();
    }

    /**
     * Create an instance of {@link MapItemType }
     *
     */
    public MapItemType createMapItemType() {
        return new MapItemType();
    }

    /**
     * Create an instance of {@link ColorReplacementType }
     *
     */
    public ColorReplacementType createColorReplacementType() {
        return new ColorReplacementType();
    }





    //extension ----------------------------------------------------------------

    /**
     * Create an instance of {@link RangeType }
     *
     */
    public RangeType createRangeType() {
        return new RangeType();
    }

    /**
     * Create an instance of {@link RecolorType }
     *
     */
    public RecolorType createRecolorType() {
        return new RecolorType();
    }

    /**
     * Create an instance of {@link JenksType }
     *
     */
    public JenksType createJenksType() {
        return new JenksType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RangeType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Range")
    public JAXBElement<RangeType> createRange(final RangeType value) {
        return new JAXBElement<RangeType>(_Range_QNAME, RangeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecolorType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Recolor", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Function")
    public JAXBElement<RecolorType> createRecolor(final RecolorType value) {
        return new JAXBElement<RecolorType>(_Recode_QNAME, RecolorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link JenksType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Jenks", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Function")
    public JAXBElement<JenksType> createJenksType(final JenksType value) {
        return new JAXBElement<JenksType>(_Jenks_QNAME, JenksType.class, null, value);
    }


    //extension ----------------------------------------------------------------




    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "WellKnownName")
    public JAXBElement<String> createWellKnownName(final String value) {
        return new JAXBElement<String>(_WellKnownName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Format")
    public JAXBElement<String> createFormat(final String value) {
        return new JAXBElement<String>(_Format_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeometryType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Geometry")
    public JAXBElement<GeometryType> createGeometry(final GeometryType value) {
        return new JAXBElement<GeometryType>(_Geometry_QNAME, GeometryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "LookupString")
    public JAXBElement<ParameterValueType> createLookupString(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_LookupString_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "SourceChannelName")
    public JAXBElement<String> createSourceChannelName(final String value) {
        return new JAXBElement<String>(_SourceChannelName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PointSymbolizerType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "PointSymbolizer", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Symbolizer")
    public JAXBElement<PointSymbolizerType> createPointSymbolizer(final PointSymbolizerType value) {
        return new JAXBElement<PointSymbolizerType>(_PointSymbolizer_QNAME, PointSymbolizerType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "DisplacementY")
    public JAXBElement<ParameterValueType> createDisplacementY(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_DisplacementY_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "DisplacementX")
    public JAXBElement<ParameterValueType> createDisplacementX(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_DisplacementX_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "GammaValue")
    public JAXBElement<Double> createGammaValue(final Double value) {
        return new JAXBElement<Double>(_GammaValue_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Rotation")
    public JAXBElement<ParameterValueType> createRotation(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_Rotation_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "TValue")
    public JAXBElement<ParameterValueType> createTValue(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_TValue_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "NegativePattern")
    public JAXBElement<String> createNegativePattern(final String value) {
        return new JAXBElement<String>(_NegativePattern_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FeatureTypeStyleType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "FeatureTypeStyle")
    public JAXBElement<FeatureTypeStyleType> createFeatureTypeStyle(final FeatureTypeStyleType value) {
        return new JAXBElement<FeatureTypeStyleType>(_FeatureTypeStyle_QNAME, FeatureTypeStyleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "OverlapBehavior")
    public JAXBElement<String> createOverlapBehavior(final String value) {
        return new JAXBElement<String>(_OverlapBehavior_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InterpolateType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Interpolate", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Function")
    public JAXBElement<InterpolateType> createInterpolate(final InterpolateType value) {
        return new JAXBElement<InterpolateType>(_Interpolate_QNAME, InterpolateType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ColorMapType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "ColorMap")
    public JAXBElement<ColorMapType> createColorMap(final ColorMapType value) {
        return new JAXBElement<ColorMapType>(_ColorMap_QNAME, ColorMapType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StrokeType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Stroke")
    public JAXBElement<StrokeType> createStroke(final StrokeType value) {
        return new JAXBElement<StrokeType>(_Stroke_QNAME, StrokeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FunctionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Function", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "expression")
    public JAXBElement<FunctionType> createFunction(final FunctionType value) {
        return new JAXBElement<FunctionType>(_Function_QNAME, FunctionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Label")
    public JAXBElement<ParameterValueType> createLabel(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_Label_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Radius")
    public JAXBElement<ParameterValueType> createRadius(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_Radius_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExternalGraphicType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "ExternalGraphic")
    public JAXBElement<ExternalGraphicType> createExternalGraphic(final ExternalGraphicType value) {
        return new JAXBElement<ExternalGraphicType>(_ExternalGraphic_QNAME, ExternalGraphicType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "SemanticTypeIdentifier")
    public JAXBElement<String> createSemanticTypeIdentifier(final String value) {
        return new JAXBElement<String>(_SemanticTypeIdentifier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CoverageStyleType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "CoverageStyle")
    public JAXBElement<CoverageStyleType> createCoverageStyle(final CoverageStyleType value) {
        return new JAXBElement<CoverageStyleType>(_CoverageStyle_QNAME, CoverageStyleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectedChannelType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "GrayChannel")
    public JAXBElement<SelectedChannelType> createGrayChannel(final SelectedChannelType value) {
        return new JAXBElement<SelectedChannelType>(_GrayChannel_QNAME, SelectedChannelType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LegendGraphicType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "LegendGraphic")
    public JAXBElement<LegendGraphicType> createLegendGraphic(final LegendGraphicType value) {
        return new JAXBElement<LegendGraphicType>(_LegendGraphic_QNAME, LegendGraphicType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Value")
    public JAXBElement<ParameterValueType> createValue(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_Value_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Pattern")
    public JAXBElement<String> createPattern(final String value) {
        return new JAXBElement<String>(_Pattern_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TextSymbolizerType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "TextSymbolizer", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Symbolizer")
    public JAXBElement<TextSymbolizerType> createTextSymbolizer(final TextSymbolizerType value) {
        return new JAXBElement<TextSymbolizerType>(_TextSymbolizer_QNAME, TextSymbolizerType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "IsAligned")
    public JAXBElement<Boolean> createIsAligned(final Boolean value) {
        return new JAXBElement<Boolean>(_IsAligned_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InterpolationPointType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "InterpolationPoint", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "expression")
    public JAXBElement<InterpolationPointType> createInterpolationPoint(final InterpolationPointType value) {
        return new JAXBElement<InterpolationPointType>(_InterpolationPoint_QNAME, InterpolationPointType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectedChannelType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "RedChannel")
    public JAXBElement<SelectedChannelType> createRedChannel(final SelectedChannelType value) {
        return new JAXBElement<SelectedChannelType>(_RedChannel_QNAME, SelectedChannelType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InlineContentType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "InlineContent")
    public JAXBElement<InlineContentType> createInlineContent(final InlineContentType value) {
        return new JAXBElement<InlineContentType>(_InlineContent_QNAME, InlineContentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NormalizeType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Normalize")
    public JAXBElement<NormalizeType> createNormalize(final NormalizeType value) {
        return new JAXBElement<NormalizeType>(_Normalize_QNAME, NormalizeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "MaxScaleDenominator")
    public JAXBElement<Double> createMaxScaleDenominator(final Double value) {
        return new JAXBElement<Double>(_MaxScaleDenominator_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringPositionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "StringPosition", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Function")
    public JAXBElement<StringPositionType> createStringPosition(final StringPositionType value) {
        return new JAXBElement<StringPositionType>(_StringPosition_QNAME, StringPositionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GraphicType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Graphic")
    public JAXBElement<GraphicType> createGraphic(final GraphicType value) {
        return new JAXBElement<GraphicType>(_Graphic_QNAME, GraphicType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SvgParameterType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "SvgParameter")
    public JAXBElement<SvgParameterType> createSvgParameter(final SvgParameterType value) {
        return new JAXBElement<SvgParameterType>(_SvgParameter_QNAME, SvgParameterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "DateValue")
    public JAXBElement<ParameterValueType> createDateValue(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_DateValue_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SubstringType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Substring", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Function")
    public JAXBElement<SubstringType> createSubstring(final SubstringType value) {
        return new JAXBElement<SubstringType>(_Substring_QNAME, SubstringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Threshold")
    public JAXBElement<ParameterValueType> createThreshold(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_Threshold_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "GeneralizeLine")
    public JAXBElement<Boolean> createGeneralizeLine(final Boolean value) {
        return new JAXBElement<Boolean>(_GeneralizeLine_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MapItemType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "MapItem", substitutionHeadNamespace = "http://www.opengis.net/ogc", substitutionHeadName = "expression")
    public JAXBElement<MapItemType> createMapItem(final MapItemType value) {
        return new JAXBElement<MapItemType>(_MapItem_QNAME, MapItemType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DescriptionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Description")
    public JAXBElement<DescriptionType> createDescription(final DescriptionType value) {
        return new JAXBElement<DescriptionType>(_Description_QNAME, DescriptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Position")
    public JAXBElement<ParameterValueType> createPosition(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_Position_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GraphicStrokeType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "GraphicStroke")
    public JAXBElement<GraphicStrokeType> createGraphicStroke(final GraphicStrokeType value) {
        return new JAXBElement<GraphicStrokeType>(_GraphicStroke_QNAME, GraphicStrokeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FormatDateType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "FormatDate", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Function")
    public JAXBElement<FormatDateType> createFormatDate(final FormatDateType value) {
        return new JAXBElement<FormatDateType>(_FormatDate_QNAME, FormatDateType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectedChannelType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "BlueChannel")
    public JAXBElement<SelectedChannelType> createBlueChannel(final SelectedChannelType value) {
        return new JAXBElement<SelectedChannelType>(_BlueChannel_QNAME, SelectedChannelType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LineSymbolizerType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "LineSymbolizer", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Symbolizer")
    public JAXBElement<LineSymbolizerType> createLineSymbolizer(final LineSymbolizerType value) {
        return new JAXBElement<LineSymbolizerType>(_LineSymbolizer_QNAME, LineSymbolizerType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChannelSelectionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "ChannelSelection")
    public JAXBElement<ChannelSelectionType> createChannelSelection(final ChannelSelectionType value) {
        return new JAXBElement<ChannelSelectionType>(_ChannelSelection_QNAME, ChannelSelectionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Data")
    public JAXBElement<Double> createData(final Double value) {
        return new JAXBElement<Double>(_Data_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LinePlacementType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "LinePlacement")
    public JAXBElement<LinePlacementType> createLinePlacement(final LinePlacementType value) {
        return new JAXBElement<LinePlacementType>(_LinePlacement_QNAME, LinePlacementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Opacity")
    public JAXBElement<ParameterValueType> createOpacity(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_Opacity_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CategorizeType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Categorize", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Function")
    public JAXBElement<CategorizeType> createCategorize(final CategorizeType value) {
        return new JAXBElement<CategorizeType>(_Categorize_QNAME, CategorizeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "InitialGap")
    public JAXBElement<ParameterValueType> createInitialGap(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_InitialGap_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LabelPlacementType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "LabelPlacement")
    public JAXBElement<LabelPlacementType> createLabelPlacement(final LabelPlacementType value) {
        return new JAXBElement<LabelPlacementType>(_LabelPlacement_QNAME, LabelPlacementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "AnchorPointY")
    public JAXBElement<ParameterValueType> createAnchorPointY(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_AnchorPointY_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "AnchorPointX")
    public JAXBElement<ParameterValueType> createAnchorPointX(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_AnchorPointX_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TrimType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Trim", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Function")
    public JAXBElement<TrimType> createTrim(final TrimType value) {
        return new JAXBElement<TrimType>(_Trim_QNAME, TrimType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ShadedReliefType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "ShadedRelief")
    public JAXBElement<ShadedReliefType> createShadedRelief(final ShadedReliefType value) {
        return new JAXBElement<ShadedReliefType>(_ShadedRelief_QNAME, ShadedReliefType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ColorReplacementType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "ColorReplacement")
    public JAXBElement<ColorReplacementType> createColorReplacement(final ColorReplacementType value) {
        return new JAXBElement<ColorReplacementType>(_ColorReplacement_QNAME, ColorReplacementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "ReliefFactor")
    public JAXBElement<Double> createReliefFactor(final Double value) {
        return new JAXBElement<Double>(_ReliefFactor_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GraphicFillType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "GraphicFill")
    public JAXBElement<GraphicFillType> createGraphicFill(final GraphicFillType value) {
        return new JAXBElement<GraphicFillType>(_GraphicFill_QNAME, GraphicFillType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AnchorPointType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "AnchorPoint")
    public JAXBElement<AnchorPointType> createAnchorPoint(final AnchorPointType value) {
        return new JAXBElement<AnchorPointType>(_AnchorPoint_QNAME, AnchorPointType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "PerpendicularOffset")
    public JAXBElement<ParameterValueType> createPerpendicularOffset(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_PerpendicularOffset_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OnlineResourceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "OnlineResource")
    public JAXBElement<OnlineResourceType> createOnlineResource(final OnlineResourceType value) {
        return new JAXBElement<OnlineResourceType>(_OnlineResource_QNAME, OnlineResourceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FontType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Font")
    public JAXBElement<FontType> createFont(final FontType value) {
        return new JAXBElement<FontType>(_Font_QNAME, FontType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContrastEnhancementType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "ContrastEnhancement")
    public JAXBElement<ContrastEnhancementType> createContrastEnhancement(final ContrastEnhancementType value) {
        return new JAXBElement<ContrastEnhancementType>(_ContrastEnhancement_QNAME, ContrastEnhancementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "StringValue")
    public JAXBElement<ParameterValueType> createStringValue(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_StringValue_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "BrightnessOnly")
    public JAXBElement<Boolean> createBrightnessOnly(final Boolean value) {
        return new JAXBElement<Boolean>(_BrightnessOnly_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectedChannelType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "GreenChannel")
    public JAXBElement<SelectedChannelType> createGreenChannel(final SelectedChannelType value) {
        return new JAXBElement<SelectedChannelType>(_GreenChannel_QNAME, SelectedChannelType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Length")
    public JAXBElement<ParameterValueType> createLength(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_Length_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HaloType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Halo")
    public JAXBElement<HaloType> createHalo(final HaloType value) {
        return new JAXBElement<HaloType>(_Halo_QNAME, HaloType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "MinScaleDenominator")
    public JAXBElement<Double> createMinScaleDenominator(final Double value) {
        return new JAXBElement<Double>(_MinScaleDenominator_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "NumericValue")
    public JAXBElement<ParameterValueType> createNumericValue(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_NumericValue_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BaseSymbolizerType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "BaseSymbolizer")
    public JAXBElement<BaseSymbolizerType> createBaseSymbolizer(final BaseSymbolizerType value) {
        return new JAXBElement<BaseSymbolizerType>(_BaseSymbolizer_QNAME, BaseSymbolizerType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BigInteger }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "MarkIndex")
    public JAXBElement<BigInteger> createMarkIndex(final BigInteger value) {
        return new JAXBElement<BigInteger>(_MarkIndex_QNAME, BigInteger.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DisplacementType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Displacement")
    public JAXBElement<DisplacementType> createDisplacement(final DisplacementType value) {
        return new JAXBElement<DisplacementType>(_Displacement_QNAME, DisplacementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "IsRepeated")
    public JAXBElement<Boolean> createIsRepeated(final Boolean value) {
        return new JAXBElement<Boolean>(_IsRepeated_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SymbolizerType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Symbolizer")
    public JAXBElement<SymbolizerType> createSymbolizer(final SymbolizerType value) {
        return new JAXBElement<SymbolizerType>(_Symbolizer_QNAME, SymbolizerType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Size")
    public JAXBElement<ParameterValueType> createSize(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_Size_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link QName }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "FeatureTypeName")
    public JAXBElement<QName> createFeatureTypeName(final QName value) {
        return new JAXBElement<QName>(_FeatureTypeName_QNAME, QName.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PolygonSymbolizerType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "PolygonSymbolizer", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Symbolizer")
    public JAXBElement<PolygonSymbolizerType> createPolygonSymbolizer(final PolygonSymbolizerType value) {
        return new JAXBElement<PolygonSymbolizerType>(_PolygonSymbolizer_QNAME, PolygonSymbolizerType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImageOutlineType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "ImageOutline")
    public JAXBElement<ImageOutlineType> createImageOutline(final ImageOutlineType value) {
        return new JAXBElement<ImageOutlineType>(_ImageOutline_QNAME, ImageOutlineType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "CoverageName")
    public JAXBElement<String> createCoverageName(final String value) {
        return new JAXBElement<String>(_CoverageName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecodeType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Recode", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Function")
    public JAXBElement<RecodeType> createRecode(final RecodeType value) {
        return new JAXBElement<RecodeType>(_Recode_QNAME, RecodeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "LookupValue")
    public JAXBElement<ParameterValueType> createLookupValue(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_LookupValue_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FillType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Fill")
    public JAXBElement<FillType> createFill(final FillType value) {
        return new JAXBElement<FillType>(_Fill_QNAME, FillType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PointPlacementType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "PointPlacement")
    public JAXBElement<PointPlacementType> createPointPlacement(final PointPlacementType value) {
        return new JAXBElement<PointPlacementType>(_PointPlacement_QNAME, PointPlacementType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link HistogramType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Histogram")
    public JAXBElement<HistogramType> createHistogram(final HistogramType value) {
        return new JAXBElement<HistogramType>(_Histogram_QNAME, HistogramType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ElseFilterType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "ElseFilter")
    public JAXBElement<ElseFilterType> createElseFilter(final ElseFilterType value) {
        return new JAXBElement<ElseFilterType>(_ElseFilter_QNAME, ElseFilterType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MarkType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Mark")
    public JAXBElement<MarkType> createMark(final MarkType value) {
        return new JAXBElement<MarkType>(_Mark_QNAME, MarkType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StringLengthType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "StringLength", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Function")
    public JAXBElement<StringLengthType> createStringLength(final StringLengthType value) {
        return new JAXBElement<StringLengthType>(_StringLength_QNAME, StringLengthType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConcatenateType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Concatenate", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Function")
    public JAXBElement<ConcatenateType> createConcatenate(final ConcatenateType value) {
        return new JAXBElement<ConcatenateType>(_Concatenate_QNAME, ConcatenateType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RuleType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Rule")
    public JAXBElement<RuleType> createRule(final RuleType value) {
        return new JAXBElement<RuleType>(_Rule_QNAME, RuleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Gap")
    public JAXBElement<ParameterValueType> createGap(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_Gap_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RasterSymbolizerType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "RasterSymbolizer", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Symbolizer")
    public JAXBElement<RasterSymbolizerType> createRasterSymbolizer(final RasterSymbolizerType value) {
        return new JAXBElement<RasterSymbolizerType>(_RasterSymbolizer_QNAME, RasterSymbolizerType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FormatNumberType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "FormatNumber", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Function")
    public JAXBElement<FormatNumberType> createFormatNumber(final FormatNumberType value) {
        return new JAXBElement<FormatNumberType>(_FormatNumber_QNAME, FormatNumberType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "Name")
    public JAXBElement<String> createName(final String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChangeCaseType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/se", name = "ChangeCase", substitutionHeadNamespace = "http://www.opengis.net/se", substitutionHeadName = "Function")
    public JAXBElement<ChangeCaseType> createChangeCase(final ChangeCaseType value) {
        return new JAXBElement<ChangeCaseType>(_ChangeCase_QNAME, ChangeCaseType.class, null, value);
    }

}
