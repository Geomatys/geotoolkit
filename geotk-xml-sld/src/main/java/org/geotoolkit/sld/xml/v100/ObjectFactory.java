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
package org.geotoolkit.sld.xml.v100;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the net.opengis.sld package.
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

    private static final QName _PerpendicularOffset_QNAME = new QName("http://www.opengis.net/sld", "PerpendicularOffset");
    private static final QName _MaxScaleDenominator_QNAME = new QName("http://www.opengis.net/sld", "MaxScaleDenominator");
    private static final QName _ReliefFactor_QNAME = new QName("http://www.opengis.net/sld", "ReliefFactor");
    private static final QName _Label_QNAME = new QName("http://www.opengis.net/sld", "Label");
    private static final QName _Abstract_QNAME = new QName("http://www.opengis.net/sld", "Abstract");
    private static final QName _Name_QNAME = new QName("http://www.opengis.net/sld", "Name");
    private static final QName _GreenChannel_QNAME = new QName("http://www.opengis.net/sld", "GreenChannel");
    private static final QName _Radius_QNAME = new QName("http://www.opengis.net/sld", "Radius");
    private static final QName _MinScaleDenominator_QNAME = new QName("http://www.opengis.net/sld", "MinScaleDenominator");
    private static final QName _Title_QNAME = new QName("http://www.opengis.net/sld", "Title");
    private static final QName _RasterSymbolizer_QNAME = new QName("http://www.opengis.net/sld", "RasterSymbolizer");
    private static final QName _BrightnessOnly_QNAME = new QName("http://www.opengis.net/sld", "BrightnessOnly");
    private static final QName _Symbolizer_QNAME = new QName("http://www.opengis.net/sld", "Symbolizer");
    private static final QName _Size_QNAME = new QName("http://www.opengis.net/sld", "Size");
    private static final QName _FeatureTypeName_QNAME = new QName("http://www.opengis.net/sld", "FeatureTypeName");
    private static final QName _SemanticTypeIdentifier_QNAME = new QName("http://www.opengis.net/sld", "SemanticTypeIdentifier");
    private static final QName _SourceChannelName_QNAME = new QName("http://www.opengis.net/sld", "SourceChannelName");
    private static final QName _PointSymbolizer_QNAME = new QName("http://www.opengis.net/sld", "PointSymbolizer");
    private static final QName _DisplacementY_QNAME = new QName("http://www.opengis.net/sld", "DisplacementY");
    private static final QName _DisplacementX_QNAME = new QName("http://www.opengis.net/sld", "DisplacementX");
    private static final QName _PolygonSymbolizer_QNAME = new QName("http://www.opengis.net/sld", "PolygonSymbolizer");
    private static final QName _GammaValue_QNAME = new QName("http://www.opengis.net/sld", "GammaValue");
    private static final QName _WellKnownName_QNAME = new QName("http://www.opengis.net/sld", "WellKnownName");
    private static final QName _BlueChannel_QNAME = new QName("http://www.opengis.net/sld", "BlueChannel");
    private static final QName _LineSymbolizer_QNAME = new QName("http://www.opengis.net/sld", "LineSymbolizer");
    private static final QName _Service_QNAME = new QName("http://www.opengis.net/sld", "Service");
    private static final QName _Format_QNAME = new QName("http://www.opengis.net/sld", "Format");
    private static final QName _IsDefault_QNAME = new QName("http://www.opengis.net/sld", "IsDefault");
    private static final QName _TextSymbolizer_QNAME = new QName("http://www.opengis.net/sld", "TextSymbolizer");
    private static final QName _AnchorPointY_QNAME = new QName("http://www.opengis.net/sld", "AnchorPointY");
    private static final QName _Rotation_QNAME = new QName("http://www.opengis.net/sld", "Rotation");
    private static final QName _AnchorPointX_QNAME = new QName("http://www.opengis.net/sld", "AnchorPointX");
    private static final QName _RedChannel_QNAME = new QName("http://www.opengis.net/sld", "RedChannel");
    private static final QName _Opacity_QNAME = new QName("http://www.opengis.net/sld", "Opacity");
    private static final QName _GrayChannel_QNAME = new QName("http://www.opengis.net/sld", "GrayChannel");
    private static final QName _Value_QNAME = new QName("http://www.opengis.net/sld", "Value");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.sld
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ExternalGraphic }
     *
     */
    public ExternalGraphic createExternalGraphic() {
        return new ExternalGraphic();
    }

    /**
     * Create an instance of {@link UserLayer }
     *
     */
    public UserLayer createUserLayer() {
        return new UserLayer();
    }

    /**
     * Create an instance of {@link Displacement }
     *
     */
    public Displacement createDisplacement() {
        return new Displacement();
    }

    /**
     * Create an instance of {@link LayerFeatureConstraints }
     *
     */
    public LayerFeatureConstraints createLayerFeatureConstraints() {
        return new LayerFeatureConstraints();
    }

    /**
     * Create an instance of {@link GraphicStroke }
     *
     */
    public GraphicStroke createGraphicStroke() {
        return new GraphicStroke();
    }

    /**
     * Create an instance of {@link LATESTONTOP }
     *
     */
    public LATESTONTOP createLATESTONTOP() {
        return new LATESTONTOP();
    }

    /**
     * Create an instance of {@link TextSymbolizer }
     *
     */
    public TextSymbolizer createTextSymbolizer() {
        return new TextSymbolizer();
    }

    /**
     * Create an instance of {@link ContrastEnhancement }
     *
     */
    public ContrastEnhancement createContrastEnhancement() {
        return new ContrastEnhancement();
    }

    /**
     * Create an instance of {@link CssParameter }
     *
     */
    public CssParameter createCssParameter() {
        return new CssParameter();
    }

    /**
     * Create an instance of {@link RasterSymbolizer }
     *
     */
    public RasterSymbolizer createRasterSymbolizer() {
        return new RasterSymbolizer();
    }

    /**
     * Create an instance of {@link ColorMap }
     *
     */
    public ColorMap createColorMap() {
        return new ColorMap();
    }

    /**
     * Create an instance of {@link Histogram }
     *
     */
    public Histogram createHistogram() {
        return new Histogram();
    }

    /**
     * Create an instance of {@link Font }
     *
     */
    public Font createFont() {
        return new Font();
    }

    /**
     * Create an instance of {@link ParameterValueType }
     *
     */
    public ParameterValueType createParameterValueType() {
        return new ParameterValueType();
    }

    /**
     * Create an instance of {@link StyledLayerDescriptor }
     *
     */
    public StyledLayerDescriptor createStyledLayerDescriptor() {
        return new StyledLayerDescriptor();
    }

    /**
     * Create an instance of {@link Rule }
     *
     */
    public Rule createRule() {
        return new Rule();
    }

    /**
     * Create an instance of {@link AVERAGE }
     *
     */
    public AVERAGE createAVERAGE() {
        return new AVERAGE();
    }

    /**
     * Create an instance of {@link NamedLayer }
     *
     */
    public NamedLayer createNamedLayer() {
        return new NamedLayer();
    }

    /**
     * Create an instance of {@link OnlineResource }
     *
     */
    public OnlineResource createOnlineResource() {
        return new OnlineResource();
    }

    /**
     * Create an instance of {@link Normalize }
     *
     */
    public Normalize createNormalize() {
        return new Normalize();
    }

    /**
     * Create an instance of {@link LinePlacement }
     *
     */
    public LinePlacement createLinePlacement() {
        return new LinePlacement();
    }

    /**
     * Create an instance of {@link EARLIESTONTOP }
     *
     */
    public EARLIESTONTOP createEARLIESTONTOP() {
        return new EARLIESTONTOP();
    }

    /**
     * Create an instance of {@link Fill }
     *
     */
    public Fill createFill() {
        return new Fill();
    }

    /**
     * Create an instance of {@link PolygonSymbolizer }
     *
     */
    public PolygonSymbolizer createPolygonSymbolizer() {
        return new PolygonSymbolizer();
    }

    /**
     * Create an instance of {@link Graphic }
     *
     */
    public Graphic createGraphic() {
        return new Graphic();
    }

    /**
     * Create an instance of {@link Extent }
     *
     */
    public Extent createExtent() {
        return new Extent();
    }

    /**
     * Create an instance of {@link LineSymbolizer }
     *
     */
    public LineSymbolizer createLineSymbolizer() {
        return new LineSymbolizer();
    }

    /**
     * Create an instance of {@link ElseFilter }
     *
     */
    public ElseFilter createElseFilter() {
        return new ElseFilter();
    }

    /**
     * Create an instance of {@link PointPlacement }
     *
     */
    public PointPlacement createPointPlacement() {
        return new PointPlacement();
    }

    /**
     * Create an instance of {@link OverlapBehavior }
     *
     */
    public OverlapBehavior createOverlapBehavior() {
        return new OverlapBehavior();
    }

    /**
     * Create an instance of {@link RANDOM }
     *
     */
    public RANDOM createRANDOM() {
        return new RANDOM();
    }

    /**
     * Create an instance of {@link LabelPlacement }
     *
     */
    public LabelPlacement createLabelPlacement() {
        return new LabelPlacement();
    }

    /**
     * Create an instance of {@link AnchorPoint }
     *
     */
    public AnchorPoint createAnchorPoint() {
        return new AnchorPoint();
    }

    /**
     * Create an instance of {@link RemoteOWS }
     *
     */
    public RemoteOWS createRemoteOWS() {
        return new RemoteOWS();
    }

    /**
     * Create an instance of {@link Mark }
     *
     */
    public Mark createMark() {
        return new Mark();
    }

    /**
     * Create an instance of {@link NamedStyle }
     *
     */
    public NamedStyle createNamedStyle() {
        return new NamedStyle();
    }

    /**
     * Create an instance of {@link PointSymbolizer }
     *
     */
    public PointSymbolizer createPointSymbolizer() {
        return new PointSymbolizer();
    }

    /**
     * Create an instance of {@link ColorMapEntry }
     *
     */
    public ColorMapEntry createColorMapEntry() {
        return new ColorMapEntry();
    }

    /**
     * Create an instance of {@link ImageOutline }
     *
     */
    public ImageOutline createImageOutline() {
        return new ImageOutline();
    }

    /**
     * Create an instance of {@link ChannelSelection }
     *
     */
    public ChannelSelection createChannelSelection() {
        return new ChannelSelection();
    }

    /**
     * Create an instance of {@link LegendGraphic }
     *
     */
    public LegendGraphic createLegendGraphic() {
        return new LegendGraphic();
    }

    /**
     * Create an instance of {@link Halo }
     *
     */
    public Halo createHalo() {
        return new Halo();
    }

    /**
     * Create an instance of {@link FeatureTypeStyle }
     *
     */
    public FeatureTypeStyle createFeatureTypeStyle() {
        return new FeatureTypeStyle();
    }

    /**
     * Create an instance of {@link Geometry }
     *
     */
    public Geometry createGeometry() {
        return new Geometry();
    }

    /**
     * Create an instance of {@link FeatureTypeConstraint }
     *
     */
    public FeatureTypeConstraint createFeatureTypeConstraint() {
        return new FeatureTypeConstraint();
    }

    /**
     * Create an instance of {@link SelectedChannelType }
     *
     */
    public SelectedChannelType createSelectedChannelType() {
        return new SelectedChannelType();
    }

    /**
     * Create an instance of {@link Stroke }
     *
     */
    public Stroke createStroke() {
        return new Stroke();
    }

    /**
     * Create an instance of {@link GraphicFill }
     *
     */
    public GraphicFill createGraphicFill() {
        return new GraphicFill();
    }

    /**
     * Create an instance of {@link ShadedRelief }
     *
     */
    public ShadedRelief createShadedRelief() {
        return new ShadedRelief();
    }

    /**
     * Create an instance of {@link UserStyle }
     *
     */
    public UserStyle createUserStyle() {
        return new UserStyle();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "PerpendicularOffset")
    public JAXBElement<ParameterValueType> createPerpendicularOffset(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_PerpendicularOffset_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "MaxScaleDenominator")
    public JAXBElement<Double> createMaxScaleDenominator(final Double value) {
        return new JAXBElement<Double>(_MaxScaleDenominator_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "ReliefFactor")
    public JAXBElement<Double> createReliefFactor(final Double value) {
        return new JAXBElement<Double>(_ReliefFactor_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "Label")
    public JAXBElement<ParameterValueType> createLabel(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_Label_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "Abstract")
    public JAXBElement<String> createAbstract(final String value) {
        return new JAXBElement<String>(_Abstract_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "Name")
    public JAXBElement<String> createName(final String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectedChannelType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "GreenChannel")
    public JAXBElement<SelectedChannelType> createGreenChannel(final SelectedChannelType value) {
        return new JAXBElement<SelectedChannelType>(_GreenChannel_QNAME, SelectedChannelType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "Radius")
    public JAXBElement<ParameterValueType> createRadius(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_Radius_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "MinScaleDenominator")
    public JAXBElement<Double> createMinScaleDenominator(final Double value) {
        return new JAXBElement<Double>(_MinScaleDenominator_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "Title")
    public JAXBElement<String> createTitle(final String value) {
        return new JAXBElement<String>(_Title_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RasterSymbolizer }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "RasterSymbolizer", substitutionHeadNamespace = "http://www.opengis.net/sld", substitutionHeadName = "Symbolizer")
    public JAXBElement<RasterSymbolizer> createRasterSymbolizer(final RasterSymbolizer value) {
        return new JAXBElement<RasterSymbolizer>(_RasterSymbolizer_QNAME, RasterSymbolizer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "BrightnessOnly")
    public JAXBElement<Boolean> createBrightnessOnly(final Boolean value) {
        return new JAXBElement<Boolean>(_BrightnessOnly_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SymbolizerType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "Symbolizer")
    public JAXBElement<SymbolizerType> createSymbolizer(final SymbolizerType value) {
        return new JAXBElement<SymbolizerType>(_Symbolizer_QNAME, SymbolizerType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "Size")
    public JAXBElement<ParameterValueType> createSize(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_Size_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "FeatureTypeName")
    public JAXBElement<String> createFeatureTypeName(final String value) {
        return new JAXBElement<String>(_FeatureTypeName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "SemanticTypeIdentifier")
    public JAXBElement<String> createSemanticTypeIdentifier(final String value) {
        return new JAXBElement<String>(_SemanticTypeIdentifier_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "SourceChannelName")
    public JAXBElement<String> createSourceChannelName(final String value) {
        return new JAXBElement<String>(_SourceChannelName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PointSymbolizer }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "PointSymbolizer", substitutionHeadNamespace = "http://www.opengis.net/sld", substitutionHeadName = "Symbolizer")
    public JAXBElement<PointSymbolizer> createPointSymbolizer(final PointSymbolizer value) {
        return new JAXBElement<PointSymbolizer>(_PointSymbolizer_QNAME, PointSymbolizer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "DisplacementY")
    public JAXBElement<ParameterValueType> createDisplacementY(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_DisplacementY_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "DisplacementX")
    public JAXBElement<ParameterValueType> createDisplacementX(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_DisplacementX_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PolygonSymbolizer }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "PolygonSymbolizer", substitutionHeadNamespace = "http://www.opengis.net/sld", substitutionHeadName = "Symbolizer")
    public JAXBElement<PolygonSymbolizer> createPolygonSymbolizer(final PolygonSymbolizer value) {
        return new JAXBElement<PolygonSymbolizer>(_PolygonSymbolizer_QNAME, PolygonSymbolizer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "GammaValue")
    public JAXBElement<Double> createGammaValue(final Double value) {
        return new JAXBElement<Double>(_GammaValue_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "WellKnownName")
    public JAXBElement<String> createWellKnownName(final String value) {
        return new JAXBElement<String>(_WellKnownName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectedChannelType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "BlueChannel")
    public JAXBElement<SelectedChannelType> createBlueChannel(final SelectedChannelType value) {
        return new JAXBElement<SelectedChannelType>(_BlueChannel_QNAME, SelectedChannelType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LineSymbolizer }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "LineSymbolizer", substitutionHeadNamespace = "http://www.opengis.net/sld", substitutionHeadName = "Symbolizer")
    public JAXBElement<LineSymbolizer> createLineSymbolizer(final LineSymbolizer value) {
        return new JAXBElement<LineSymbolizer>(_LineSymbolizer_QNAME, LineSymbolizer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "Service")
    public JAXBElement<String> createService(final String value) {
        return new JAXBElement<String>(_Service_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "Format")
    public JAXBElement<String> createFormat(final String value) {
        return new JAXBElement<String>(_Format_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "IsDefault")
    public JAXBElement<Boolean> createIsDefault(final Boolean value) {
        return new JAXBElement<Boolean>(_IsDefault_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TextSymbolizer }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "TextSymbolizer", substitutionHeadNamespace = "http://www.opengis.net/sld", substitutionHeadName = "Symbolizer")
    public JAXBElement<TextSymbolizer> createTextSymbolizer(final TextSymbolizer value) {
        return new JAXBElement<TextSymbolizer>(_TextSymbolizer_QNAME, TextSymbolizer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "AnchorPointY")
    public JAXBElement<ParameterValueType> createAnchorPointY(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_AnchorPointY_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "Rotation")
    public JAXBElement<ParameterValueType> createRotation(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_Rotation_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "AnchorPointX")
    public JAXBElement<ParameterValueType> createAnchorPointX(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_AnchorPointX_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectedChannelType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "RedChannel")
    public JAXBElement<SelectedChannelType> createRedChannel(final SelectedChannelType value) {
        return new JAXBElement<SelectedChannelType>(_RedChannel_QNAME, SelectedChannelType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ParameterValueType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "Opacity")
    public JAXBElement<ParameterValueType> createOpacity(final ParameterValueType value) {
        return new JAXBElement<ParameterValueType>(_Opacity_QNAME, ParameterValueType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SelectedChannelType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "GrayChannel")
    public JAXBElement<SelectedChannelType> createGrayChannel(final SelectedChannelType value) {
        return new JAXBElement<SelectedChannelType>(_GrayChannel_QNAME, SelectedChannelType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sld", name = "Value")
    public JAXBElement<String> createValue(final String value) {
        return new JAXBElement<String>(_Value_QNAME, String.class, null, value);
    }

}
