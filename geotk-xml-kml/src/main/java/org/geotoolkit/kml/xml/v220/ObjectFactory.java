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
package org.geotoolkit.kml.xml.v220;

import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the net.opengis.kml._2 package.
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

    private static final QName _RefreshVisibility_QNAME = new QName("http://www.opengis.net/kml/2.2", "refreshVisibility");
    private static final QName _CameraObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "CameraObjectExtensionGroup");
    private static final QName _AbstractSubStyleSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractSubStyleSimpleExtensionGroup");
    private static final QName _CameraSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "CameraSimpleExtensionGroup");
    private static final QName _LatLonBoxSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LatLonBoxSimpleExtensionGroup");
    private static final QName _AbstractViewSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractViewSimpleExtensionGroup");
    private static final QName _InnerBoundaryIs_QNAME = new QName("http://www.opengis.net/kml/2.2", "innerBoundaryIs");
    private static final QName _LinearRing_QNAME = new QName("http://www.opengis.net/kml/2.2", "LinearRing");
    private static final QName _MultiGeometrySimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "MultiGeometrySimpleExtensionGroup");
    private static final QName _Width_QNAME = new QName("http://www.opengis.net/kml/2.2", "width");
    private static final QName _LodSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LodSimpleExtensionGroup");
    private static final QName _Text_QNAME = new QName("http://www.opengis.net/kml/2.2", "text");
    private static final QName _Size_QNAME = new QName("http://www.opengis.net/kml/2.2", "size");
    private static final QName _LookAtObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LookAtObjectExtensionGroup");
    private static final QName _PhotoOverlaySimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "PhotoOverlaySimpleExtensionGroup");
    private static final QName _BalloonStyle_QNAME = new QName("http://www.opengis.net/kml/2.2", "BalloonStyle");
    private static final QName _MultiGeometry_QNAME = new QName("http://www.opengis.net/kml/2.2", "MultiGeometry");
    private static final QName _AbstractTimePrimitiveGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractTimePrimitiveGroup");
    private static final QName _Update_QNAME = new QName("http://www.opengis.net/kml/2.2", "Update");
    private static final QName _LodObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LodObjectExtensionGroup");
    private static final QName _OrientationObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "OrientationObjectExtensionGroup");
    private static final QName _ScreenXY_QNAME = new QName("http://www.opengis.net/kml/2.2", "screenXY");
    private static final QName _Alias_QNAME = new QName("http://www.opengis.net/kml/2.2", "Alias");
    private static final QName _StyleMapSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "StyleMapSimpleExtensionGroup");
    private static final QName _AbstractTimePrimitiveObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractTimePrimitiveObjectExtensionGroup");
    private static final QName _MaxSnippetLines_QNAME = new QName("http://www.opengis.net/kml/2.2", "maxSnippetLines");
    private static final QName _DisplayName_QNAME = new QName("http://www.opengis.net/kml/2.2", "displayName");
    private static final QName _AbstractViewGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractViewGroup");
    private static final QName _AbstractGeometryGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractGeometryGroup");
    private static final QName _NetworkLinkControl_QNAME = new QName("http://www.opengis.net/kml/2.2", "NetworkLinkControl");
    private static final QName _IconStyleObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "IconStyleObjectExtensionGroup");
    private static final QName _AbstractGeometryObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractGeometryObjectExtensionGroup");
    private static final QName _PhoneNumber_QNAME = new QName("http://www.opengis.net/kml/2.2", "phoneNumber");
    private static final QName _AbstractSubStyleGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractSubStyleGroup");
    private static final QName _Href_QNAME = new QName("http://www.opengis.net/kml/2.2", "href");
    private static final QName _OverlayXY_QNAME = new QName("http://www.opengis.net/kml/2.2", "overlayXY");
    private static final QName _TileSize_QNAME = new QName("http://www.opengis.net/kml/2.2", "tileSize");
    private static final QName _AliasSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AliasSimpleExtensionGroup");
    private static final QName _LabelStyleSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LabelStyleSimpleExtensionGroup");
    private static final QName _Model_QNAME = new QName("http://www.opengis.net/kml/2.2", "Model");
    private static final QName _PolyStyleSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "PolyStyleSimpleExtensionGroup");
    private static final QName _Cookie_QNAME = new QName("http://www.opengis.net/kml/2.2", "cookie");
    private static final QName _UpdateOpExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "UpdateOpExtensionGroup");
    private static final QName _AltitudeMode_QNAME = new QName("http://www.opengis.net/kml/2.2", "altitudeMode");
    private static final QName _Latitude_QNAME = new QName("http://www.opengis.net/kml/2.2", "latitude");
    private static final QName _LinearRingSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LinearRingSimpleExtensionGroup");
    private static final QName _AbstractFeatureGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractFeatureGroup");
    private static final QName _OrientationSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "OrientationSimpleExtensionGroup");
    private static final QName _BalloonStyleSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "BalloonStyleSimpleExtensionGroup");
    private static final QName _PlacemarkSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "PlacemarkSimpleExtensionGroup");
    private static final QName _RegionSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "RegionSimpleExtensionGroup");
    private static final QName _AbstractContainerObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractContainerObjectExtensionGroup");
    private static final QName _ScaleSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "ScaleSimpleExtensionGroup");
    private static final QName _ListStyle_QNAME = new QName("http://www.opengis.net/kml/2.2", "ListStyle");
    private static final QName _Pair_QNAME = new QName("http://www.opengis.net/kml/2.2", "Pair");
    private static final QName _FlyToView_QNAME = new QName("http://www.opengis.net/kml/2.2", "flyToView");
    private static final QName _LatLonAltBoxSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LatLonAltBoxSimpleExtensionGroup");
    private static final QName _ListItemType_QNAME = new QName("http://www.opengis.net/kml/2.2", "listItemType");
    private static final QName _AbstractSubStyleObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractSubStyleObjectExtensionGroup");
    private static final QName _LatLonBox_QNAME = new QName("http://www.opengis.net/kml/2.2", "LatLonBox");
    private static final QName _ScaleObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "ScaleObjectExtensionGroup");
    private static final QName _TimeStamp_QNAME = new QName("http://www.opengis.net/kml/2.2", "TimeStamp");
    private static final QName _RegionObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "RegionObjectExtensionGroup");
    private static final QName _South_QNAME = new QName("http://www.opengis.net/kml/2.2", "south");
    private static final QName _TimeSpan_QNAME = new QName("http://www.opengis.net/kml/2.2", "TimeSpan");
    private static final QName _MaxSessionLength_QNAME = new QName("http://www.opengis.net/kml/2.2", "maxSessionLength");
    private static final QName _Longitude_QNAME = new QName("http://www.opengis.net/kml/2.2", "longitude");
    private static final QName _ViewVolumeObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "ViewVolumeObjectExtensionGroup");
    private static final QName _NetworkLinkSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "NetworkLinkSimpleExtensionGroup");
    private static final QName _Style_QNAME = new QName("http://www.opengis.net/kml/2.2", "Style");
    private static final QName _LinearRingObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LinearRingObjectExtensionGroup");
    private static final QName _West_QNAME = new QName("http://www.opengis.net/kml/2.2", "west");
    private static final QName _GroundOverlay_QNAME = new QName("http://www.opengis.net/kml/2.2", "GroundOverlay");
    private static final QName _FolderObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "FolderObjectExtensionGroup");
    private static final QName _LatLonBoxObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LatLonBoxObjectExtensionGroup");
    private static final QName _AbstractColorStyleSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractColorStyleSimpleExtensionGroup");
    private static final QName _PointSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "PointSimpleExtensionGroup");
    private static final QName _PairObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "PairObjectExtensionGroup");
    private static final QName _AbstractColorStyleGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractColorStyleGroup");
    private static final QName _Shape_QNAME = new QName("http://www.opengis.net/kml/2.2", "shape");
    private static final QName _LookAt_QNAME = new QName("http://www.opengis.net/kml/2.2", "LookAt");
    private static final QName _MultiGeometryObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "MultiGeometryObjectExtensionGroup");
    private static final QName _Lod_QNAME = new QName("http://www.opengis.net/kml/2.2", "Lod");
    private static final QName _LinkName_QNAME = new QName("http://www.opengis.net/kml/2.2", "linkName");
    private static final QName _ImagePyramidSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "ImagePyramidSimpleExtensionGroup");
    private static final QName _AltitudeModeGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "altitudeModeGroup");
    private static final QName _AbstractContainerGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractContainerGroup");
    private static final QName _SimpleField_QNAME = new QName("http://www.opengis.net/kml/2.2", "SimpleField");
    private static final QName _ViewVolume_QNAME = new QName("http://www.opengis.net/kml/2.2", "ViewVolume");
    private static final QName _ListStyleSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "ListStyleSimpleExtensionGroup");
    private static final QName _North_QNAME = new QName("http://www.opengis.net/kml/2.2", "north");
    private static final QName _State_QNAME = new QName("http://www.opengis.net/kml/2.2", "state");
    private static final QName _KmlObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "KmlObjectExtensionGroup");
    private static final QName _DataExtension_QNAME = new QName("http://www.opengis.net/kml/2.2", "DataExtension");
    private static final QName _Address_QNAME = new QName("http://www.opengis.net/kml/2.2", "address");
    private static final QName _Placemark_QNAME = new QName("http://www.opengis.net/kml/2.2", "Placemark");
    private static final QName _Fill_QNAME = new QName("http://www.opengis.net/kml/2.2", "fill");
    private static final QName _AbstractOverlaySimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractOverlaySimpleExtensionGroup");
    private static final QName _Begin_QNAME = new QName("http://www.opengis.net/kml/2.2", "begin");
    private static final QName _Url_QNAME = new QName("http://www.opengis.net/kml/2.2", "Url");
    private static final QName _Expires_QNAME = new QName("http://www.opengis.net/kml/2.2", "expires");
    private static final QName _Open_QNAME = new QName("http://www.opengis.net/kml/2.2", "open");
    private static final QName _Data_QNAME = new QName("http://www.opengis.net/kml/2.2", "Data");
    private static final QName _TopFov_QNAME = new QName("http://www.opengis.net/kml/2.2", "topFov");
    private static final QName _NetworkLinkObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "NetworkLinkObjectExtensionGroup");
    private static final QName _BottomFov_QNAME = new QName("http://www.opengis.net/kml/2.2", "bottomFov");
    private static final QName _Altitude_QNAME = new QName("http://www.opengis.net/kml/2.2", "altitude");
    private static final QName _RightFov_QNAME = new QName("http://www.opengis.net/kml/2.2", "rightFov");
    private static final QName _MaxAltitude_QNAME = new QName("http://www.opengis.net/kml/2.2", "maxAltitude");
    private static final QName _ResourceMapSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "ResourceMapSimpleExtensionGroup");
    private static final QName _AbstractObjectGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractObjectGroup");
    private static final QName _ListStyleObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "ListStyleObjectExtensionGroup");
    private static final QName _Metadata_QNAME = new QName("http://www.opengis.net/kml/2.2", "Metadata");
    private static final QName _NetworkLinkControlObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "NetworkLinkControlObjectExtensionGroup");
    private static final QName _Snippet_QNAME = new QName("http://www.opengis.net/kml/2.2", "Snippet");
    private static final QName _ViewRefreshMode_QNAME = new QName("http://www.opengis.net/kml/2.2", "viewRefreshMode");
    private static final QName _Camera_QNAME = new QName("http://www.opengis.net/kml/2.2", "Camera");
    private static final QName _LinkSnippet_QNAME = new QName("http://www.opengis.net/kml/2.2", "linkSnippet");
    private static final QName _SnippetDenominator_QNAME = new QName("http://www.opengis.net/kml/2.2", "snippetDenominator");
    private static final QName _AbstractStyleSelectorSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractStyleSelectorSimpleExtensionGroup");
    private static final QName _ScreenOverlaySimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "ScreenOverlaySimpleExtensionGroup");
    private static final QName _Extrude_QNAME = new QName("http://www.opengis.net/kml/2.2", "extrude");
    private static final QName _End_QNAME = new QName("http://www.opengis.net/kml/2.2", "end");
    private static final QName _MinLodPixels_QNAME = new QName("http://www.opengis.net/kml/2.2", "minLodPixels");
    private static final QName _AbstractContainerSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractContainerSimpleExtensionGroup");
    private static final QName _TimeStampSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "TimeStampSimpleExtensionGroup");
    private static final QName _Range_QNAME = new QName("http://www.opengis.net/kml/2.2", "range");
    private static final QName _SchemaExtension_QNAME = new QName("http://www.opengis.net/kml/2.2", "SchemaExtension");
    private static final QName _RefreshInterval_QNAME = new QName("http://www.opengis.net/kml/2.2", "refreshInterval");
    private static final QName _SchemaDataExtension_QNAME = new QName("http://www.opengis.net/kml/2.2", "SchemaDataExtension");
    private static final QName _Outline_QNAME = new QName("http://www.opengis.net/kml/2.2", "outline");
    private static final QName _PhotoOverlay_QNAME = new QName("http://www.opengis.net/kml/2.2", "PhotoOverlay");
    private static final QName _HttpQuery_QNAME = new QName("http://www.opengis.net/kml/2.2", "httpQuery");
    private static final QName _ImagePyramid_QNAME = new QName("http://www.opengis.net/kml/2.2", "ImagePyramid");
    private static final QName _AbstractStyleSelectorObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractStyleSelectorObjectExtensionGroup");
    private static final QName _Schema_QNAME = new QName("http://www.opengis.net/kml/2.2", "Schema");
    private static final QName _TextColor_QNAME = new QName("http://www.opengis.net/kml/2.2", "textColor");
    private static final QName _SchemaData_QNAME = new QName("http://www.opengis.net/kml/2.2", "SchemaData");
    private static final QName _LineStringObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LineStringObjectExtensionGroup");
    private static final QName _AbstractOverlayGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractOverlayGroup");
    private static final QName _LinkSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LinkSimpleExtensionGroup");
    private static final QName _LabelStyleObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LabelStyleObjectExtensionGroup");
    private static final QName _LineString_QNAME = new QName("http://www.opengis.net/kml/2.2", "LineString");
    private static final QName _Description_QNAME = new QName("http://www.opengis.net/kml/2.2", "description");
    private static final QName _Key_QNAME = new QName("http://www.opengis.net/kml/2.2", "key");
    private static final QName _Scale_QNAME = new QName("http://www.opengis.net/kml/2.2", "Scale");
    private static final QName _MaxWidth_QNAME = new QName("http://www.opengis.net/kml/2.2", "maxWidth");
    private static final QName _GroundOverlaySimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "GroundOverlaySimpleExtensionGroup");
    private static final QName _AbstractOverlayObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractOverlayObjectExtensionGroup");
    private static final QName _LocationSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LocationSimpleExtensionGroup");
    private static final QName _SimpleData_QNAME = new QName("http://www.opengis.net/kml/2.2", "SimpleData");
    private static final QName _LinkDescription_QNAME = new QName("http://www.opengis.net/kml/2.2", "linkDescription");
    private static final QName _Near_QNAME = new QName("http://www.opengis.net/kml/2.2", "near");
    private static final QName _FolderSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "FolderSimpleExtensionGroup");
    private static final QName _ViewVolumeSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "ViewVolumeSimpleExtensionGroup");
    private static final QName _PolyStyleObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "PolyStyleObjectExtensionGroup");
    private static final QName _AbstractGeometrySimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractGeometrySimpleExtensionGroup");
    private static final QName _UpdateExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "UpdateExtensionGroup");
    private static final QName _ViewRefreshTime_QNAME = new QName("http://www.opengis.net/kml/2.2", "viewRefreshTime");
    private static final QName _Change_QNAME = new QName("http://www.opengis.net/kml/2.2", "Change");
    private static final QName _AbstractLatLonBoxObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractLatLonBoxObjectExtensionGroup");
    private static final QName _LinkObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LinkObjectExtensionGroup");
    private static final QName _OuterBoundaryIs_QNAME = new QName("http://www.opengis.net/kml/2.2", "outerBoundaryIs");
    private static final QName _Name_QNAME = new QName("http://www.opengis.net/kml/2.2", "name");
    private static final QName _AbstractTimePrimitiveSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractTimePrimitiveSimpleExtensionGroup");
    private static final QName _Region_QNAME = new QName("http://www.opengis.net/kml/2.2", "Region");
    private static final QName _AbstractViewObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractViewObjectExtensionGroup");
    private static final QName _ItemIcon_QNAME = new QName("http://www.opengis.net/kml/2.2", "ItemIcon");
    private static final QName _AbstractStyleSelectorGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractStyleSelectorGroup");
    private static final QName _SourceHref_QNAME = new QName("http://www.opengis.net/kml/2.2", "sourceHref");
    private static final QName _LatLonAltBoxObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LatLonAltBoxObjectExtensionGroup");
    private static final QName _ItemIconObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "ItemIconObjectExtensionGroup");
    private static final QName _Color_QNAME = new QName("http://www.opengis.net/kml/2.2", "color");
    private static final QName _Message_QNAME = new QName("http://www.opengis.net/kml/2.2", "message");
    private static final QName _ImagePyramidObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "ImagePyramidObjectExtensionGroup");
    private static final QName _Icon_QNAME = new QName("http://www.opengis.net/kml/2.2", "Icon");
    private static final QName _BoundaryObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "BoundaryObjectExtensionGroup");
    private static final QName _GroundOverlayObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "GroundOverlayObjectExtensionGroup");
    private static final QName _Tessellate_QNAME = new QName("http://www.opengis.net/kml/2.2", "tessellate");
    private static final QName _Roll_QNAME = new QName("http://www.opengis.net/kml/2.2", "roll");
    private static final QName _Point_QNAME = new QName("http://www.opengis.net/kml/2.2", "Point");
    private static final QName _MaxLodPixels_QNAME = new QName("http://www.opengis.net/kml/2.2", "maxLodPixels");
    private static final QName _Visibility_QNAME = new QName("http://www.opengis.net/kml/2.2", "visibility");
    private static final QName _StyleSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "StyleSimpleExtensionGroup");
    private static final QName _NetworkLinkControlSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "NetworkLinkControlSimpleExtensionGroup");
    private static final QName _SimpleFieldExtension_QNAME = new QName("http://www.opengis.net/kml/2.2", "SimpleFieldExtension");
    private static final QName _ViewBoundScale_QNAME = new QName("http://www.opengis.net/kml/2.2", "viewBoundScale");
    private static final QName _KmlSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "KmlSimpleExtensionGroup");
    private static final QName _PolygonSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "PolygonSimpleExtensionGroup");
    private static final QName _GridOrigin_QNAME = new QName("http://www.opengis.net/kml/2.2", "gridOrigin");
    private static final QName _LocationObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LocationObjectExtensionGroup");
    private static final QName _Create_QNAME = new QName("http://www.opengis.net/kml/2.2", "Create");
    private static final QName _RefreshMode_QNAME = new QName("http://www.opengis.net/kml/2.2", "refreshMode");
    private static final QName _ItemIconSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "ItemIconSimpleExtensionGroup");
    private static final QName _IconStyleSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "IconStyleSimpleExtensionGroup");
    private static final QName _LineStringSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LineStringSimpleExtensionGroup");
    private static final QName _Link_QNAME = new QName("http://www.opengis.net/kml/2.2", "Link");
    private static final QName _Polygon_QNAME = new QName("http://www.opengis.net/kml/2.2", "Polygon");
    private static final QName _LookAtSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LookAtSimpleExtensionGroup");
    private static final QName _Y_QNAME = new QName("http://www.opengis.net/kml/2.2", "y");
    private static final QName _X_QNAME = new QName("http://www.opengis.net/kml/2.2", "x");
    private static final QName _AliasObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AliasObjectExtensionGroup");
    private static final QName _Z_QNAME = new QName("http://www.opengis.net/kml/2.2", "z");
    private static final QName _PointObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "PointObjectExtensionGroup");
    private static final QName _ModelObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "ModelObjectExtensionGroup");
    private static final QName _Tilt_QNAME = new QName("http://www.opengis.net/kml/2.2", "tilt");
    private static final QName _BasicLinkSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "BasicLinkSimpleExtensionGroup");
    private static final QName _Orientation_QNAME = new QName("http://www.opengis.net/kml/2.2", "Orientation");
    private static final QName _AbstractFeatureSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractFeatureSimpleExtensionGroup");
    private static final QName _LatLonAltBox_QNAME = new QName("http://www.opengis.net/kml/2.2", "LatLonAltBox");
    private static final QName _ScreenOverlay_QNAME = new QName("http://www.opengis.net/kml/2.2", "ScreenOverlay");
    private static final QName _When_QNAME = new QName("http://www.opengis.net/kml/2.2", "when");
    private static final QName _PolygonObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "PolygonObjectExtensionGroup");
    private static final QName _BgColor_QNAME = new QName("http://www.opengis.net/kml/2.2", "bgColor");
    private static final QName _Delete_QNAME = new QName("http://www.opengis.net/kml/2.2", "Delete");
    private static final QName _Value_QNAME = new QName("http://www.opengis.net/kml/2.2", "value");
    private static final QName _Kml_QNAME = new QName("http://www.opengis.net/kml/2.2", "kml");
    private static final QName _ResourceMapObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "ResourceMapObjectExtensionGroup");
    private static final QName _NetworkLink_QNAME = new QName("http://www.opengis.net/kml/2.2", "NetworkLink");
    private static final QName _LineStyle_QNAME = new QName("http://www.opengis.net/kml/2.2", "LineStyle");
    private static final QName _BasicLinkObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "BasicLinkObjectExtensionGroup");
    private static final QName _ResourceMap_QNAME = new QName("http://www.opengis.net/kml/2.2", "ResourceMap");
    private static final QName _DrawOrder_QNAME = new QName("http://www.opengis.net/kml/2.2", "drawOrder");
    private static final QName _PairSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "PairSimpleExtensionGroup");
    private static final QName _MinAltitude_QNAME = new QName("http://www.opengis.net/kml/2.2", "minAltitude");
    private static final QName _MaxHeight_QNAME = new QName("http://www.opengis.net/kml/2.2", "maxHeight");
    private static final QName _PolyStyle_QNAME = new QName("http://www.opengis.net/kml/2.2", "PolyStyle");
    private static final QName _StyleMap_QNAME = new QName("http://www.opengis.net/kml/2.2", "StyleMap");
    private static final QName _DisplayMode_QNAME = new QName("http://www.opengis.net/kml/2.2", "displayMode");
    private static final QName _StyleObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "StyleObjectExtensionGroup");
    private static final QName _Coordinates_QNAME = new QName("http://www.opengis.net/kml/2.2", "coordinates");
    private static final QName _ScaleDenominator_QNAME = new QName("http://www.opengis.net/kml/2.2", "scaleDenominator");
    private static final QName _TimeSpanSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "TimeSpanSimpleExtensionGroup");
    private static final QName _East_QNAME = new QName("http://www.opengis.net/kml/2.2", "east");
    private static final QName _PhotoOverlayObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "PhotoOverlayObjectExtensionGroup");
    private static final QName _LineStyleObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LineStyleObjectExtensionGroup");
    private static final QName _AbstractFeatureObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractFeatureObjectExtensionGroup");
    private static final QName _MinRefreshPeriod_QNAME = new QName("http://www.opengis.net/kml/2.2", "minRefreshPeriod");
    private static final QName _DocumentObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "DocumentObjectExtensionGroup");
    private static final QName _TimeStampObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "TimeStampObjectExtensionGroup");
    private static final QName _ScreenOverlayObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "ScreenOverlayObjectExtensionGroup");
    private static final QName _LineStyleSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "LineStyleSimpleExtensionGroup");
    private static final QName _LeftFov_QNAME = new QName("http://www.opengis.net/kml/2.2", "leftFov");
    private static final QName _Document_QNAME = new QName("http://www.opengis.net/kml/2.2", "Document");
    private static final QName _Folder_QNAME = new QName("http://www.opengis.net/kml/2.2", "Folder");
    private static final QName _PlacemarkObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "PlacemarkObjectExtensionGroup");
    private static final QName _Location_QNAME = new QName("http://www.opengis.net/kml/2.2", "Location");
    private static final QName _Rotation_QNAME = new QName("http://www.opengis.net/kml/2.2", "rotation");
    private static final QName _AbstractLatLonBoxSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractLatLonBoxSimpleExtensionGroup");
    private static final QName _AbstractColorStyleObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "AbstractColorStyleObjectExtensionGroup");
    private static final QName _StyleUrl_QNAME = new QName("http://www.opengis.net/kml/2.2", "styleUrl");
    private static final QName _MinFadeExtent_QNAME = new QName("http://www.opengis.net/kml/2.2", "minFadeExtent");
    private static final QName _Heading_QNAME = new QName("http://www.opengis.net/kml/2.2", "heading");
    private static final QName _TimeSpanObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "TimeSpanObjectExtensionGroup");
    private static final QName _MaxFadeExtent_QNAME = new QName("http://www.opengis.net/kml/2.2", "maxFadeExtent");
    private static final QName _RotationXY_QNAME = new QName("http://www.opengis.net/kml/2.2", "rotationXY");
    private static final QName _IconStyle_QNAME = new QName("http://www.opengis.net/kml/2.2", "IconStyle");
    private static final QName _DocumentSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "DocumentSimpleExtensionGroup");
    private static final QName _ModelSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "ModelSimpleExtensionGroup");
    private static final QName _HotSpot_QNAME = new QName("http://www.opengis.net/kml/2.2", "hotSpot");
    private static final QName _StyleMapObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "StyleMapObjectExtensionGroup");
    private static final QName _BalloonStyleObjectExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "BalloonStyleObjectExtensionGroup");
    private static final QName _ColorMode_QNAME = new QName("http://www.opengis.net/kml/2.2", "colorMode");
    private static final QName _ViewFormat_QNAME = new QName("http://www.opengis.net/kml/2.2", "viewFormat");
    private static final QName _BoundarySimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "BoundarySimpleExtensionGroup");
    private static final QName _LabelStyle_QNAME = new QName("http://www.opengis.net/kml/2.2", "LabelStyle");
    private static final QName _TargetHref_QNAME = new QName("http://www.opengis.net/kml/2.2", "targetHref");
    private static final QName _ObjectSimpleExtensionGroup_QNAME = new QName("http://www.opengis.net/kml/2.2", "ObjectSimpleExtensionGroup");
    private static final QName _ExtendedData_QNAME = new QName("http://www.opengis.net/kml/2.2", "ExtendedData");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.kml._2
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DeleteType }
     *
     */
    public DeleteType createDeleteType() {
        return new DeleteType();
    }

    /**
     * Create an instance of {@link LinearRingType }
     *
     */
    public LinearRingType createLinearRingType() {
        return new LinearRingType();
    }

    /**
     * Create an instance of {@link Vec2Type }
     *
     */
    public Vec2Type createVec2Type() {
        return new Vec2Type();
    }

    /**
     * Create an instance of {@link BalloonStyleType }
     *
     */
    public BalloonStyleType createBalloonStyleType() {
        return new BalloonStyleType();
    }

    /**
     * Create an instance of {@link SnippetType }
     *
     */
    public SnippetType createSnippetType() {
        return new SnippetType();
    }

    /**
     * Create an instance of {@link BoundaryType }
     *
     */
    public BoundaryType createBoundaryType() {
        return new BoundaryType();
    }

    /**
     * Create an instance of {@link UpdateType }
     *
     */
    public UpdateType createUpdateType() {
        return new UpdateType();
    }

    /**
     * Create an instance of {@link CameraType }
     *
     */
    public CameraType createCameraType() {
        return new CameraType();
    }

    /**
     * Create an instance of {@link SimpleFieldType }
     *
     */
    public SimpleFieldType createSimpleFieldType() {
        return new SimpleFieldType();
    }

    /**
     * Create an instance of {@link PolyStyleType }
     *
     */
    public PolyStyleType createPolyStyleType() {
        return new PolyStyleType();
    }

    /**
     * Create an instance of {@link ViewVolumeType }
     *
     */
    public ViewVolumeType createViewVolumeType() {
        return new ViewVolumeType();
    }

    /**
     * Create an instance of {@link SchemaDataType }
     *
     */
    public SchemaDataType createSchemaDataType() {
        return new SchemaDataType();
    }

    /**
     * Create an instance of {@link SchemaType }
     *
     */
    public SchemaType createSchemaType() {
        return new SchemaType();
    }

    /**
     * Create an instance of {@link NetworkLinkControlType }
     *
     */
    public NetworkLinkControlType createNetworkLinkControlType() {
        return new NetworkLinkControlType();
    }

    /**
     * Create an instance of {@link DocumentType }
     *
     */
    public DocumentType createDocumentType() {
        return new DocumentType();
    }

    /**
     * Create an instance of {@link ExtendedDataType }
     *
     */
    public ExtendedDataType createExtendedDataType() {
        return new ExtendedDataType();
    }

    /**
     * Create an instance of {@link PairType }
     *
     */
    public PairType createPairType() {
        return new PairType();
    }

    /**
     * Create an instance of {@link LineStringType }
     *
     */
    public LineStringType createLineStringType() {
        return new LineStringType();
    }

    /**
     * Create an instance of {@link PointType }
     *
     */
    public PointType createPointType() {
        return new PointType();
    }

    /**
     * Create an instance of {@link ScreenOverlayType }
     *
     */
    public ScreenOverlayType createScreenOverlayType() {
        return new ScreenOverlayType();
    }

    /**
     * Create an instance of {@link StyleType }
     *
     */
    public StyleType createStyleType() {
        return new StyleType();
    }

    /**
     * Create an instance of {@link LatLonAltBoxType }
     *
     */
    public LatLonAltBoxType createLatLonAltBoxType() {
        return new LatLonAltBoxType();
    }

    /**
     * Create an instance of {@link LodType }
     *
     */
    public LodType createLodType() {
        return new LodType();
    }

    /**
     * Create an instance of {@link MetadataType }
     *
     */
    public MetadataType createMetadataType() {
        return new MetadataType();
    }

    /**
     * Create an instance of {@link TimeStampType }
     *
     */
    public TimeStampType createTimeStampType() {
        return new TimeStampType();
    }

    /**
     * Create an instance of {@link ListStyleType }
     *
     */
    public ListStyleType createListStyleType() {
        return new ListStyleType();
    }

    /**
     * Create an instance of {@link DataType }
     *
     */
    public DataType createDataType() {
        return new DataType();
    }

    /**
     * Create an instance of {@link OrientationType }
     *
     */
    public OrientationType createOrientationType() {
        return new OrientationType();
    }

    /**
     * Create an instance of {@link CreateType }
     *
     */
    public CreateType createCreateType() {
        return new CreateType();
    }

    /**
     * Create an instance of {@link BasicLinkType }
     *
     */
    public BasicLinkType createBasicLinkType() {
        return new BasicLinkType();
    }

    /**
     * Create an instance of {@link ChangeType }
     *
     */
    public ChangeType createChangeType() {
        return new ChangeType();
    }

    /**
     * Create an instance of {@link LineStyleType }
     *
     */
    public LineStyleType createLineStyleType() {
        return new LineStyleType();
    }

    /**
     * Create an instance of {@link PlacemarkType }
     *
     */
    public PlacemarkType createPlacemarkType() {
        return new PlacemarkType();
    }

    /**
     * Create an instance of {@link TimeSpanType }
     *
     */
    public TimeSpanType createTimeSpanType() {
        return new TimeSpanType();
    }

    /**
     * Create an instance of {@link FolderType }
     *
     */
    public FolderType createFolderType() {
        return new FolderType();
    }

    /**
     * Create an instance of {@link ImagePyramidType }
     *
     */
    public ImagePyramidType createImagePyramidType() {
        return new ImagePyramidType();
    }

    /**
     * Create an instance of {@link NetworkLinkType }
     *
     */
    public NetworkLinkType createNetworkLinkType() {
        return new NetworkLinkType();
    }

    /**
     * Create an instance of {@link LatLonBoxType }
     *
     */
    public LatLonBoxType createLatLonBoxType() {
        return new LatLonBoxType();
    }

    /**
     * Create an instance of {@link PhotoOverlayType }
     *
     */
    public PhotoOverlayType createPhotoOverlayType() {
        return new PhotoOverlayType();
    }

    /**
     * Create an instance of {@link SimpleDataType }
     *
     */
    public SimpleDataType createSimpleDataType() {
        return new SimpleDataType();
    }

    /**
     * Create an instance of {@link GroundOverlayType }
     *
     */
    public GroundOverlayType createGroundOverlayType() {
        return new GroundOverlayType();
    }

    /**
     * Create an instance of {@link IconStyleType }
     *
     */
    public IconStyleType createIconStyleType() {
        return new IconStyleType();
    }

    /**
     * Create an instance of {@link StyleMapType }
     *
     */
    public StyleMapType createStyleMapType() {
        return new StyleMapType();
    }

    /**
     * Create an instance of {@link ModelType }
     *
     */
    public ModelType createModelType() {
        return new ModelType();
    }

    /**
     * Create an instance of {@link LinkType }
     *
     */
    public LinkType createLinkType() {
        return new LinkType();
    }

    /**
     * Create an instance of {@link LabelStyleType }
     *
     */
    public LabelStyleType createLabelStyleType() {
        return new LabelStyleType();
    }

    /**
     * Create an instance of {@link KmlType }
     *
     */
    public KmlType createKmlType() {
        return new KmlType();
    }

    /**
     * Create an instance of {@link ResourceMapType }
     *
     */
    public ResourceMapType createResourceMapType() {
        return new ResourceMapType();
    }

    /**
     * Create an instance of {@link LookAtType }
     *
     */
    public LookAtType createLookAtType() {
        return new LookAtType();
    }

    /**
     * Create an instance of {@link LocationType }
     *
     */
    public LocationType createLocationType() {
        return new LocationType();
    }

    /**
     * Create an instance of {@link AliasType }
     *
     */
    public AliasType createAliasType() {
        return new AliasType();
    }

    /**
     * Create an instance of {@link MultiGeometryType }
     *
     */
    public MultiGeometryType createMultiGeometryType() {
        return new MultiGeometryType();
    }

    /**
     * Create an instance of {@link ItemIconType }
     *
     */
    public ItemIconType createItemIconType() {
        return new ItemIconType();
    }

    /**
     * Create an instance of {@link PolygonType }
     *
     */
    public PolygonType createPolygonType() {
        return new PolygonType();
    }

    /**
     * Create an instance of {@link RegionType }
     *
     */
    public RegionType createRegionType() {
        return new RegionType();
    }

    /**
     * Create an instance of {@link ScaleType }
     *
     */
    public ScaleType createScaleType() {
        return new ScaleType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "refreshVisibility", defaultValue = "0")
    public JAXBElement<Boolean> createRefreshVisibility(final Boolean value) {
        return new JAXBElement<Boolean>(_RefreshVisibility_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "CameraObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createCameraObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_CameraObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractSubStyleSimpleExtensionGroup")
    public JAXBElement<Object> createAbstractSubStyleSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_AbstractSubStyleSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "CameraSimpleExtensionGroup")
    public JAXBElement<Object> createCameraSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_CameraSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LatLonBoxSimpleExtensionGroup")
    public JAXBElement<Object> createLatLonBoxSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_LatLonBoxSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractViewSimpleExtensionGroup")
    public JAXBElement<Object> createAbstractViewSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_AbstractViewSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BoundaryType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "innerBoundaryIs")
    public JAXBElement<BoundaryType> createInnerBoundaryIs(final BoundaryType value) {
        return new JAXBElement<BoundaryType>(_InnerBoundaryIs_QNAME, BoundaryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LinearRingType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LinearRing", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractGeometryGroup")
    public JAXBElement<LinearRingType> createLinearRing(final LinearRingType value) {
        return new JAXBElement<LinearRingType>(_LinearRing_QNAME, LinearRingType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "MultiGeometrySimpleExtensionGroup")
    public JAXBElement<Object> createMultiGeometrySimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_MultiGeometrySimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "width", defaultValue = "1.0")
    public JAXBElement<Double> createWidth(final Double value) {
        return new JAXBElement<Double>(_Width_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LodSimpleExtensionGroup")
    public JAXBElement<Object> createLodSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_LodSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "text")
    public JAXBElement<String> createText(final String value) {
        return new JAXBElement<String>(_Text_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Vec2Type }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "size")
    public JAXBElement<Vec2Type> createSize(final Vec2Type value) {
        return new JAXBElement<Vec2Type>(_Size_QNAME, Vec2Type.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LookAtObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createLookAtObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_LookAtObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "PhotoOverlaySimpleExtensionGroup")
    public JAXBElement<Object> createPhotoOverlaySimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_PhotoOverlaySimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BalloonStyleType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "BalloonStyle", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractSubStyleGroup")
    public JAXBElement<BalloonStyleType> createBalloonStyle(final BalloonStyleType value) {
        return new JAXBElement<BalloonStyleType>(_BalloonStyle_QNAME, BalloonStyleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MultiGeometryType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "MultiGeometry", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractGeometryGroup")
    public JAXBElement<MultiGeometryType> createMultiGeometry(final MultiGeometryType value) {
        return new JAXBElement<MultiGeometryType>(_MultiGeometry_QNAME, MultiGeometryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractTimePrimitiveType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractTimePrimitiveGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractTimePrimitiveType> createAbstractTimePrimitiveGroup(final AbstractTimePrimitiveType value) {
        return new JAXBElement<AbstractTimePrimitiveType>(_AbstractTimePrimitiveGroup_QNAME, AbstractTimePrimitiveType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Update")
    public JAXBElement<UpdateType> createUpdate(final UpdateType value) {
        return new JAXBElement<UpdateType>(_Update_QNAME, UpdateType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LodObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createLodObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_LodObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "OrientationObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createOrientationObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_OrientationObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Vec2Type }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "screenXY")
    public JAXBElement<Vec2Type> createScreenXY(final Vec2Type value) {
        return new JAXBElement<Vec2Type>(_ScreenXY_QNAME, Vec2Type.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AliasType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Alias", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AliasType> createAlias(final AliasType value) {
        return new JAXBElement<AliasType>(_Alias_QNAME, AliasType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "StyleMapSimpleExtensionGroup")
    public JAXBElement<Object> createStyleMapSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_StyleMapSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractTimePrimitiveObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createAbstractTimePrimitiveObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_AbstractTimePrimitiveObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "maxSnippetLines", defaultValue = "2")
    public JAXBElement<Integer> createMaxSnippetLines(final Integer value) {
        return new JAXBElement<Integer>(_MaxSnippetLines_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "displayName")
    public JAXBElement<String> createDisplayName(final String value) {
        return new JAXBElement<String>(_DisplayName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractViewType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractViewGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractViewType> createAbstractViewGroup(final AbstractViewType value) {
        return new JAXBElement<AbstractViewType>(_AbstractViewGroup_QNAME, AbstractViewType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractGeometryType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractGeometryGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractGeometryType> createAbstractGeometryGroup(final AbstractGeometryType value) {
        return new JAXBElement<AbstractGeometryType>(_AbstractGeometryGroup_QNAME, AbstractGeometryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NetworkLinkControlType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "NetworkLinkControl")
    public JAXBElement<NetworkLinkControlType> createNetworkLinkControl(final NetworkLinkControlType value) {
        return new JAXBElement<NetworkLinkControlType>(_NetworkLinkControl_QNAME, NetworkLinkControlType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "IconStyleObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createIconStyleObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_IconStyleObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractGeometryObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createAbstractGeometryObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_AbstractGeometryObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "phoneNumber")
    public JAXBElement<String> createPhoneNumber(final String value) {
        return new JAXBElement<String>(_PhoneNumber_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractSubStyleType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractSubStyleGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractSubStyleType> createAbstractSubStyleGroup(final AbstractSubStyleType value) {
        return new JAXBElement<AbstractSubStyleType>(_AbstractSubStyleGroup_QNAME, AbstractSubStyleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "href")
    public JAXBElement<String> createHref(final String value) {
        return new JAXBElement<String>(_Href_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Vec2Type }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "overlayXY")
    public JAXBElement<Vec2Type> createOverlayXY(final Vec2Type value) {
        return new JAXBElement<Vec2Type>(_OverlayXY_QNAME, Vec2Type.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "tileSize", defaultValue = "256")
    public JAXBElement<Integer> createTileSize(final Integer value) {
        return new JAXBElement<Integer>(_TileSize_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AliasSimpleExtensionGroup")
    public JAXBElement<Object> createAliasSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_AliasSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LabelStyleSimpleExtensionGroup")
    public JAXBElement<Object> createLabelStyleSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_LabelStyleSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ModelType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Model", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractGeometryGroup")
    public JAXBElement<ModelType> createModel(final ModelType value) {
        return new JAXBElement<ModelType>(_Model_QNAME, ModelType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "PolyStyleSimpleExtensionGroup")
    public JAXBElement<Object> createPolyStyleSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_PolyStyleSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "cookie")
    public JAXBElement<String> createCookie(final String value) {
        return new JAXBElement<String>(_Cookie_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "UpdateOpExtensionGroup")
    public JAXBElement<Object> createUpdateOpExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_UpdateOpExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AltitudeModeEnumType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "altitudeMode", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "altitudeModeGroup", defaultValue = "clampToGround")
    public JAXBElement<AltitudeModeEnumType> createAltitudeMode(final AltitudeModeEnumType value) {
        return new JAXBElement<AltitudeModeEnumType>(_AltitudeMode_QNAME, AltitudeModeEnumType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "latitude", defaultValue = "0.0")
    public JAXBElement<Double> createLatitude(final Double value) {
        return new JAXBElement<Double>(_Latitude_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LinearRingSimpleExtensionGroup")
    public JAXBElement<Object> createLinearRingSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_LinearRingSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractFeatureType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractFeatureGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractFeatureType> createAbstractFeatureGroup(final AbstractFeatureType value) {
        return new JAXBElement<AbstractFeatureType>(_AbstractFeatureGroup_QNAME, AbstractFeatureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "OrientationSimpleExtensionGroup")
    public JAXBElement<Object> createOrientationSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_OrientationSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "BalloonStyleSimpleExtensionGroup")
    public JAXBElement<Object> createBalloonStyleSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_BalloonStyleSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "PlacemarkSimpleExtensionGroup")
    public JAXBElement<Object> createPlacemarkSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_PlacemarkSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "RegionSimpleExtensionGroup")
    public JAXBElement<Object> createRegionSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_RegionSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractContainerObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createAbstractContainerObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_AbstractContainerObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ScaleSimpleExtensionGroup")
    public JAXBElement<Object> createScaleSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_ScaleSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListStyleType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ListStyle", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractSubStyleGroup")
    public JAXBElement<ListStyleType> createListStyle(final ListStyleType value) {
        return new JAXBElement<ListStyleType>(_ListStyle_QNAME, ListStyleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PairType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Pair", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<PairType> createPair(final PairType value) {
        return new JAXBElement<PairType>(_Pair_QNAME, PairType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "flyToView", defaultValue = "0")
    public JAXBElement<Boolean> createFlyToView(final Boolean value) {
        return new JAXBElement<Boolean>(_FlyToView_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LatLonAltBoxSimpleExtensionGroup")
    public JAXBElement<Object> createLatLonAltBoxSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_LatLonAltBoxSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListItemTypeEnumType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "listItemType", defaultValue = "check")
    public JAXBElement<ListItemTypeEnumType> createListItemType(final ListItemTypeEnumType value) {
        return new JAXBElement<ListItemTypeEnumType>(_ListItemType_QNAME, ListItemTypeEnumType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractSubStyleObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createAbstractSubStyleObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_AbstractSubStyleObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LatLonBoxType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LatLonBox", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<LatLonBoxType> createLatLonBox(final LatLonBoxType value) {
        return new JAXBElement<LatLonBoxType>(_LatLonBox_QNAME, LatLonBoxType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ScaleObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createScaleObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_ScaleObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeStampType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "TimeStamp", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractTimePrimitiveGroup")
    public JAXBElement<TimeStampType> createTimeStamp(final TimeStampType value) {
        return new JAXBElement<TimeStampType>(_TimeStamp_QNAME, TimeStampType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "RegionObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createRegionObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_RegionObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "south", defaultValue = "-180.0")
    public JAXBElement<Double> createSouth(final Double value) {
        return new JAXBElement<Double>(_South_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TimeSpanType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "TimeSpan", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractTimePrimitiveGroup")
    public JAXBElement<TimeSpanType> createTimeSpan(final TimeSpanType value) {
        return new JAXBElement<TimeSpanType>(_TimeSpan_QNAME, TimeSpanType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "maxSessionLength", defaultValue = "-1.0")
    public JAXBElement<Double> createMaxSessionLength(final Double value) {
        return new JAXBElement<Double>(_MaxSessionLength_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "longitude", defaultValue = "0.0")
    public JAXBElement<Double> createLongitude(final Double value) {
        return new JAXBElement<Double>(_Longitude_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ViewVolumeObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createViewVolumeObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_ViewVolumeObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "NetworkLinkSimpleExtensionGroup")
    public JAXBElement<Object> createNetworkLinkSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_NetworkLinkSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StyleType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Style", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractStyleSelectorGroup")
    public JAXBElement<StyleType> createStyle(final StyleType value) {
        return new JAXBElement<StyleType>(_Style_QNAME, StyleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LinearRingObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createLinearRingObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_LinearRingObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "west", defaultValue = "-180.0")
    public JAXBElement<Double> createWest(final Double value) {
        return new JAXBElement<Double>(_West_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GroundOverlayType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "GroundOverlay", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractOverlayGroup")
    public JAXBElement<GroundOverlayType> createGroundOverlay(final GroundOverlayType value) {
        return new JAXBElement<GroundOverlayType>(_GroundOverlay_QNAME, GroundOverlayType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "FolderObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createFolderObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_FolderObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LatLonBoxObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createLatLonBoxObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_LatLonBoxObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractColorStyleSimpleExtensionGroup")
    public JAXBElement<Object> createAbstractColorStyleSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_AbstractColorStyleSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "PointSimpleExtensionGroup")
    public JAXBElement<Object> createPointSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_PointSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "PairObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createPairObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_PairObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractColorStyleType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractColorStyleGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractSubStyleGroup")
    public JAXBElement<AbstractColorStyleType> createAbstractColorStyleGroup(final AbstractColorStyleType value) {
        return new JAXBElement<AbstractColorStyleType>(_AbstractColorStyleGroup_QNAME, AbstractColorStyleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ShapeEnumType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "shape", defaultValue = "rectangle")
    public JAXBElement<ShapeEnumType> createShape(final ShapeEnumType value) {
        return new JAXBElement<ShapeEnumType>(_Shape_QNAME, ShapeEnumType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LookAtType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LookAt", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractViewGroup")
    public JAXBElement<LookAtType> createLookAt(final LookAtType value) {
        return new JAXBElement<LookAtType>(_LookAt_QNAME, LookAtType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "MultiGeometryObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createMultiGeometryObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_MultiGeometryObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LodType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Lod", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<LodType> createLod(final LodType value) {
        return new JAXBElement<LodType>(_Lod_QNAME, LodType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "linkName")
    public JAXBElement<String> createLinkName(final String value) {
        return new JAXBElement<String>(_LinkName_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ImagePyramidSimpleExtensionGroup")
    public JAXBElement<Object> createImagePyramidSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_ImagePyramidSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "altitudeModeGroup")
    public JAXBElement<Object> createAltitudeModeGroup(final Object value) {
        return new JAXBElement<Object>(_AltitudeModeGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractContainerType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractContainerGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractFeatureGroup")
    public JAXBElement<AbstractContainerType> createAbstractContainerGroup(final AbstractContainerType value) {
        return new JAXBElement<AbstractContainerType>(_AbstractContainerGroup_QNAME, AbstractContainerType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleFieldType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "SimpleField")
    public JAXBElement<SimpleFieldType> createSimpleField(final SimpleFieldType value) {
        return new JAXBElement<SimpleFieldType>(_SimpleField_QNAME, SimpleFieldType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ViewVolumeType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ViewVolume", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<ViewVolumeType> createViewVolume(final ViewVolumeType value) {
        return new JAXBElement<ViewVolumeType>(_ViewVolume_QNAME, ViewVolumeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ListStyleSimpleExtensionGroup")
    public JAXBElement<Object> createListStyleSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_ListStyleSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "north", defaultValue = "180.0")
    public JAXBElement<Double> createNorth(final Double value) {
        return new JAXBElement<Double>(_North_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link ItemIconStateEnumType }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "state")
    public JAXBElement<List<ItemIconStateEnumType>> createState(final List<ItemIconStateEnumType> value) {
        return new JAXBElement<List<ItemIconStateEnumType>>(_State_QNAME, ((Class) List.class), null, ((List<ItemIconStateEnumType> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "KmlObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createKmlObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_KmlObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "DataExtension")
    public JAXBElement<Object> createDataExtension(final Object value) {
        return new JAXBElement<Object>(_DataExtension_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "address")
    public JAXBElement<String> createAddress(final String value) {
        return new JAXBElement<String>(_Address_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PlacemarkType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Placemark", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractFeatureGroup")
    public JAXBElement<PlacemarkType> createPlacemark(final PlacemarkType value) {
        return new JAXBElement<PlacemarkType>(_Placemark_QNAME, PlacemarkType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "fill", defaultValue = "1")
    public JAXBElement<Boolean> createFill(final Boolean value) {
        return new JAXBElement<Boolean>(_Fill_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractOverlaySimpleExtensionGroup")
    public JAXBElement<Object> createAbstractOverlaySimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_AbstractOverlaySimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "begin")
    public JAXBElement<String> createBegin(final String value) {
        return new JAXBElement<String>(_Begin_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LinkType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Url", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<LinkType> createUrl(final LinkType value) {
        return new JAXBElement<LinkType>(_Url_QNAME, LinkType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "expires")
    public JAXBElement<String> createExpires(final String value) {
        return new JAXBElement<String>(_Expires_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "open", defaultValue = "0")
    public JAXBElement<Boolean> createOpen(final Boolean value) {
        return new JAXBElement<Boolean>(_Open_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DataType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Data", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<DataType> createData(final DataType value) {
        return new JAXBElement<DataType>(_Data_QNAME, DataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "topFov", defaultValue = "0.0")
    public JAXBElement<Double> createTopFov(final Double value) {
        return new JAXBElement<Double>(_TopFov_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "NetworkLinkObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createNetworkLinkObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_NetworkLinkObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "bottomFov", defaultValue = "0.0")
    public JAXBElement<Double> createBottomFov(final Double value) {
        return new JAXBElement<Double>(_BottomFov_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "altitude", defaultValue = "0.0")
    public JAXBElement<Double> createAltitude(final Double value) {
        return new JAXBElement<Double>(_Altitude_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "rightFov", defaultValue = "0.0")
    public JAXBElement<Double> createRightFov(final Double value) {
        return new JAXBElement<Double>(_RightFov_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "maxAltitude", defaultValue = "0.0")
    public JAXBElement<Double> createMaxAltitude(final Double value) {
        return new JAXBElement<Double>(_MaxAltitude_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ResourceMapSimpleExtensionGroup")
    public JAXBElement<Object> createResourceMapSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_ResourceMapSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createAbstractObjectGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_AbstractObjectGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ListStyleObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createListStyleObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_ListStyleObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MetadataType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Metadata")
    public JAXBElement<MetadataType> createMetadata(final MetadataType value) {
        return new JAXBElement<MetadataType>(_Metadata_QNAME, MetadataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "NetworkLinkControlObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createNetworkLinkControlObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_NetworkLinkControlObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SnippetType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Snippet")
    public JAXBElement<SnippetType> createSnippet(final SnippetType value) {
        return new JAXBElement<SnippetType>(_Snippet_QNAME, SnippetType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ViewRefreshModeEnumType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "viewRefreshMode", defaultValue = "never")
    public JAXBElement<ViewRefreshModeEnumType> createViewRefreshMode(final ViewRefreshModeEnumType value) {
        return new JAXBElement<ViewRefreshModeEnumType>(_ViewRefreshMode_QNAME, ViewRefreshModeEnumType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CameraType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Camera", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractViewGroup")
    public JAXBElement<CameraType> createCamera(final CameraType value) {
        return new JAXBElement<CameraType>(_Camera_QNAME, CameraType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SnippetType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "linkSnippet")
    public JAXBElement<SnippetType> createLinkSnippet(final SnippetType value) {
        return new JAXBElement<SnippetType>(_LinkSnippet_QNAME, SnippetType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "snippetDenominator")
    public JAXBElement<String> createSnippetDenominator(final String value) {
        return new JAXBElement<String>(_SnippetDenominator_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractStyleSelectorSimpleExtensionGroup")
    public JAXBElement<Object> createAbstractStyleSelectorSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_AbstractStyleSelectorSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ScreenOverlaySimpleExtensionGroup")
    public JAXBElement<Object> createScreenOverlaySimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_ScreenOverlaySimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "extrude", defaultValue = "0")
    public JAXBElement<Boolean> createExtrude(final Boolean value) {
        return new JAXBElement<Boolean>(_Extrude_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "end")
    public JAXBElement<String> createEnd(final String value) {
        return new JAXBElement<String>(_End_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "minLodPixels", defaultValue = "0.0")
    public JAXBElement<Double> createMinLodPixels(final Double value) {
        return new JAXBElement<Double>(_MinLodPixels_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractContainerSimpleExtensionGroup")
    public JAXBElement<Object> createAbstractContainerSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_AbstractContainerSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "TimeStampSimpleExtensionGroup")
    public JAXBElement<Object> createTimeStampSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_TimeStampSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "range", defaultValue = "0.0")
    public JAXBElement<Double> createRange(final Double value) {
        return new JAXBElement<Double>(_Range_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "SchemaExtension")
    public JAXBElement<Object> createSchemaExtension(final Object value) {
        return new JAXBElement<Object>(_SchemaExtension_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "refreshInterval", defaultValue = "4.0")
    public JAXBElement<Double> createRefreshInterval(final Double value) {
        return new JAXBElement<Double>(_RefreshInterval_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "SchemaDataExtension")
    public JAXBElement<Object> createSchemaDataExtension(final Object value) {
        return new JAXBElement<Object>(_SchemaDataExtension_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "outline", defaultValue = "1")
    public JAXBElement<Boolean> createOutline(final Boolean value) {
        return new JAXBElement<Boolean>(_Outline_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PhotoOverlayType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "PhotoOverlay", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractOverlayGroup")
    public JAXBElement<PhotoOverlayType> createPhotoOverlay(final PhotoOverlayType value) {
        return new JAXBElement<PhotoOverlayType>(_PhotoOverlay_QNAME, PhotoOverlayType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "httpQuery")
    public JAXBElement<String> createHttpQuery(final String value) {
        return new JAXBElement<String>(_HttpQuery_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImagePyramidType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ImagePyramid", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<ImagePyramidType> createImagePyramid(final ImagePyramidType value) {
        return new JAXBElement<ImagePyramidType>(_ImagePyramid_QNAME, ImagePyramidType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractStyleSelectorObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createAbstractStyleSelectorObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_AbstractStyleSelectorObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SchemaType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Schema")
    public JAXBElement<SchemaType> createSchema(final SchemaType value) {
        return new JAXBElement<SchemaType>(_Schema_QNAME, SchemaType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "textColor", defaultValue = "ff000000")
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    public JAXBElement<byte[]> createTextColor(final byte[] value) {
        return new JAXBElement<byte[]>(_TextColor_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SchemaDataType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "SchemaData", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<SchemaDataType> createSchemaData(final SchemaDataType value) {
        return new JAXBElement<SchemaDataType>(_SchemaData_QNAME, SchemaDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LineStringObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createLineStringObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_LineStringObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractOverlayType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractOverlayGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractFeatureGroup")
    public JAXBElement<AbstractOverlayType> createAbstractOverlayGroup(final AbstractOverlayType value) {
        return new JAXBElement<AbstractOverlayType>(_AbstractOverlayGroup_QNAME, AbstractOverlayType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LinkSimpleExtensionGroup")
    public JAXBElement<Object> createLinkSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_LinkSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LabelStyleObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createLabelStyleObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_LabelStyleObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LineStringType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LineString", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractGeometryGroup")
    public JAXBElement<LineStringType> createLineString(final LineStringType value) {
        return new JAXBElement<LineStringType>(_LineString_QNAME, LineStringType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "description")
    public JAXBElement<String> createDescription(final String value) {
        return new JAXBElement<String>(_Description_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StyleStateEnumType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "key", defaultValue = "normal")
    public JAXBElement<StyleStateEnumType> createKey(final StyleStateEnumType value) {
        return new JAXBElement<StyleStateEnumType>(_Key_QNAME, StyleStateEnumType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ScaleType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Scale", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<ScaleType> createScale(final ScaleType value) {
        return new JAXBElement<ScaleType>(_Scale_QNAME, ScaleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "maxWidth", defaultValue = "0")
    public JAXBElement<Integer> createMaxWidth(final Integer value) {
        return new JAXBElement<Integer>(_MaxWidth_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "GroundOverlaySimpleExtensionGroup")
    public JAXBElement<Object> createGroundOverlaySimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_GroundOverlaySimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractOverlayObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createAbstractOverlayObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_AbstractOverlayObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LocationSimpleExtensionGroup")
    public JAXBElement<Object> createLocationSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_LocationSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleDataType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "SimpleData")
    public JAXBElement<SimpleDataType> createSimpleData(final SimpleDataType value) {
        return new JAXBElement<SimpleDataType>(_SimpleData_QNAME, SimpleDataType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "linkDescription")
    public JAXBElement<String> createLinkDescription(final String value) {
        return new JAXBElement<String>(_LinkDescription_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "near", defaultValue = "0.0")
    public JAXBElement<Double> createNear(final Double value) {
        return new JAXBElement<Double>(_Near_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "FolderSimpleExtensionGroup")
    public JAXBElement<Object> createFolderSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_FolderSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ViewVolumeSimpleExtensionGroup")
    public JAXBElement<Object> createViewVolumeSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_ViewVolumeSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "PolyStyleObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createPolyStyleObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_PolyStyleObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractGeometrySimpleExtensionGroup")
    public JAXBElement<Object> createAbstractGeometrySimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_AbstractGeometrySimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "UpdateExtensionGroup")
    public JAXBElement<Object> createUpdateExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_UpdateExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "viewRefreshTime", defaultValue = "4.0")
    public JAXBElement<Double> createViewRefreshTime(final Double value) {
        return new JAXBElement<Double>(_ViewRefreshTime_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ChangeType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Change")
    public JAXBElement<ChangeType> createChange(final ChangeType value) {
        return new JAXBElement<ChangeType>(_Change_QNAME, ChangeType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractLatLonBoxObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createAbstractLatLonBoxObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_AbstractLatLonBoxObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LinkObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createLinkObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_LinkObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BoundaryType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "outerBoundaryIs")
    public JAXBElement<BoundaryType> createOuterBoundaryIs(final BoundaryType value) {
        return new JAXBElement<BoundaryType>(_OuterBoundaryIs_QNAME, BoundaryType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "name")
    public JAXBElement<String> createName(final String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractTimePrimitiveSimpleExtensionGroup")
    public JAXBElement<Object> createAbstractTimePrimitiveSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_AbstractTimePrimitiveSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Region", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<RegionType> createRegion(final RegionType value) {
        return new JAXBElement<RegionType>(_Region_QNAME, RegionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractViewObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createAbstractViewObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_AbstractViewObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ItemIconType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ItemIcon", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<ItemIconType> createItemIcon(final ItemIconType value) {
        return new JAXBElement<ItemIconType>(_ItemIcon_QNAME, ItemIconType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractStyleSelectorType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractStyleSelectorGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractStyleSelectorType> createAbstractStyleSelectorGroup(final AbstractStyleSelectorType value) {
        return new JAXBElement<AbstractStyleSelectorType>(_AbstractStyleSelectorGroup_QNAME, AbstractStyleSelectorType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "sourceHref")
    public JAXBElement<String> createSourceHref(final String value) {
        return new JAXBElement<String>(_SourceHref_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LatLonAltBoxObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createLatLonAltBoxObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_LatLonAltBoxObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ItemIconObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createItemIconObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_ItemIconObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "color", defaultValue = "ffffffff")
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    public JAXBElement<byte[]> createColor(final byte[] value) {
        return new JAXBElement<byte[]>(_Color_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "message")
    public JAXBElement<String> createMessage(final String value) {
        return new JAXBElement<String>(_Message_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ImagePyramidObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createImagePyramidObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_ImagePyramidObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LinkType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Icon", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<LinkType> createIcon(final LinkType value) {
        return new JAXBElement<LinkType>(_Icon_QNAME, LinkType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "BoundaryObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createBoundaryObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_BoundaryObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "GroundOverlayObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createGroundOverlayObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_GroundOverlayObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "tessellate", defaultValue = "0")
    public JAXBElement<Boolean> createTessellate(final Boolean value) {
        return new JAXBElement<Boolean>(_Tessellate_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "roll", defaultValue = "0.0")
    public JAXBElement<Double> createRoll(final Double value) {
        return new JAXBElement<Double>(_Roll_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PointType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Point", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractGeometryGroup")
    public JAXBElement<PointType> createPoint(final PointType value) {
        return new JAXBElement<PointType>(_Point_QNAME, PointType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "maxLodPixels", defaultValue = "-1.0")
    public JAXBElement<Double> createMaxLodPixels(final Double value) {
        return new JAXBElement<Double>(_MaxLodPixels_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "visibility", defaultValue = "1")
    public JAXBElement<Boolean> createVisibility(final Boolean value) {
        return new JAXBElement<Boolean>(_Visibility_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "StyleSimpleExtensionGroup")
    public JAXBElement<Object> createStyleSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_StyleSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "NetworkLinkControlSimpleExtensionGroup")
    public JAXBElement<Object> createNetworkLinkControlSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_NetworkLinkControlSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "SimpleFieldExtension")
    public JAXBElement<Object> createSimpleFieldExtension(final Object value) {
        return new JAXBElement<Object>(_SimpleFieldExtension_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "viewBoundScale", defaultValue = "1.0")
    public JAXBElement<Double> createViewBoundScale(final Double value) {
        return new JAXBElement<Double>(_ViewBoundScale_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "KmlSimpleExtensionGroup")
    public JAXBElement<Object> createKmlSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_KmlSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "PolygonSimpleExtensionGroup")
    public JAXBElement<Object> createPolygonSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_PolygonSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GridOriginEnumType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "gridOrigin", defaultValue = "lowerLeft")
    public JAXBElement<GridOriginEnumType> createGridOrigin(final GridOriginEnumType value) {
        return new JAXBElement<GridOriginEnumType>(_GridOrigin_QNAME, GridOriginEnumType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LocationObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createLocationObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_LocationObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CreateType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Create")
    public JAXBElement<CreateType> createCreate(final CreateType value) {
        return new JAXBElement<CreateType>(_Create_QNAME, CreateType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RefreshModeEnumType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "refreshMode", defaultValue = "onChange")
    public JAXBElement<RefreshModeEnumType> createRefreshMode(final RefreshModeEnumType value) {
        return new JAXBElement<RefreshModeEnumType>(_RefreshMode_QNAME, RefreshModeEnumType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ItemIconSimpleExtensionGroup")
    public JAXBElement<Object> createItemIconSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_ItemIconSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "IconStyleSimpleExtensionGroup")
    public JAXBElement<Object> createIconStyleSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_IconStyleSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LineStringSimpleExtensionGroup")
    public JAXBElement<Object> createLineStringSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_LineStringSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LinkType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Link", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<LinkType> createLink(final LinkType value) {
        return new JAXBElement<LinkType>(_Link_QNAME, LinkType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PolygonType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Polygon", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractGeometryGroup")
    public JAXBElement<PolygonType> createPolygon(final PolygonType value) {
        return new JAXBElement<PolygonType>(_Polygon_QNAME, PolygonType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LookAtSimpleExtensionGroup")
    public JAXBElement<Object> createLookAtSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_LookAtSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "y", defaultValue = "1.0")
    public JAXBElement<Double> createY(final Double value) {
        return new JAXBElement<Double>(_Y_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "x", defaultValue = "1.0")
    public JAXBElement<Double> createX(final Double value) {
        return new JAXBElement<Double>(_X_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AliasObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createAliasObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_AliasObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "z", defaultValue = "1.0")
    public JAXBElement<Double> createZ(final Double value) {
        return new JAXBElement<Double>(_Z_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "PointObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createPointObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_PointObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ModelObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createModelObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_ModelObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "tilt", defaultValue = "0.0")
    public JAXBElement<Double> createTilt(final Double value) {
        return new JAXBElement<Double>(_Tilt_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "BasicLinkSimpleExtensionGroup")
    public JAXBElement<Object> createBasicLinkSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_BasicLinkSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link OrientationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Orientation", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<OrientationType> createOrientation(final OrientationType value) {
        return new JAXBElement<OrientationType>(_Orientation_QNAME, OrientationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractFeatureSimpleExtensionGroup")
    public JAXBElement<Object> createAbstractFeatureSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_AbstractFeatureSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LatLonAltBoxType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LatLonAltBox", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<LatLonAltBoxType> createLatLonAltBox(final LatLonAltBoxType value) {
        return new JAXBElement<LatLonAltBoxType>(_LatLonAltBox_QNAME, LatLonAltBoxType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ScreenOverlayType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ScreenOverlay", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractOverlayGroup")
    public JAXBElement<ScreenOverlayType> createScreenOverlay(final ScreenOverlayType value) {
        return new JAXBElement<ScreenOverlayType>(_ScreenOverlay_QNAME, ScreenOverlayType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "when")
    public JAXBElement<String> createWhen(final String value) {
        return new JAXBElement<String>(_When_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "PolygonObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createPolygonObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_PolygonObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link byte[]}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "bgColor", defaultValue = "ffffffff")
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    public JAXBElement<byte[]> createBgColor(final byte[] value) {
        return new JAXBElement<byte[]>(_BgColor_QNAME, byte[].class, null, ((byte[]) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DeleteType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Delete")
    public JAXBElement<DeleteType> createDelete(final DeleteType value) {
        return new JAXBElement<DeleteType>(_Delete_QNAME, DeleteType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "value")
    public JAXBElement<String> createValue(final String value) {
        return new JAXBElement<String>(_Value_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link KmlType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "kml")
    public JAXBElement<KmlType> createKml(final KmlType value) {
        return new JAXBElement<KmlType>(_Kml_QNAME, KmlType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ResourceMapObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createResourceMapObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_ResourceMapObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link NetworkLinkType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "NetworkLink", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractFeatureGroup")
    public JAXBElement<NetworkLinkType> createNetworkLink(final NetworkLinkType value) {
        return new JAXBElement<NetworkLinkType>(_NetworkLink_QNAME, NetworkLinkType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LineStyleType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LineStyle", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractColorStyleGroup")
    public JAXBElement<LineStyleType> createLineStyle(final LineStyleType value) {
        return new JAXBElement<LineStyleType>(_LineStyle_QNAME, LineStyleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "BasicLinkObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createBasicLinkObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_BasicLinkObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ResourceMapType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ResourceMap", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<ResourceMapType> createResourceMap(final ResourceMapType value) {
        return new JAXBElement<ResourceMapType>(_ResourceMap_QNAME, ResourceMapType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "drawOrder", defaultValue = "0")
    public JAXBElement<Integer> createDrawOrder(final Integer value) {
        return new JAXBElement<Integer>(_DrawOrder_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "PairSimpleExtensionGroup")
    public JAXBElement<Object> createPairSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_PairSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "minAltitude", defaultValue = "0.0")
    public JAXBElement<Double> createMinAltitude(final Double value) {
        return new JAXBElement<Double>(_MinAltitude_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "maxHeight", defaultValue = "0")
    public JAXBElement<Integer> createMaxHeight(final Integer value) {
        return new JAXBElement<Integer>(_MaxHeight_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link PolyStyleType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "PolyStyle", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractColorStyleGroup")
    public JAXBElement<PolyStyleType> createPolyStyle(final PolyStyleType value) {
        return new JAXBElement<PolyStyleType>(_PolyStyle_QNAME, PolyStyleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StyleMapType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "StyleMap", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractStyleSelectorGroup")
    public JAXBElement<StyleMapType> createStyleMap(final StyleMapType value) {
        return new JAXBElement<StyleMapType>(_StyleMap_QNAME, StyleMapType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DisplayModeEnumType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "displayMode", defaultValue = "default")
    public JAXBElement<DisplayModeEnumType> createDisplayMode(final DisplayModeEnumType value) {
        return new JAXBElement<DisplayModeEnumType>(_DisplayMode_QNAME, DisplayModeEnumType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "StyleObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createStyleObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_StyleObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link List }{@code <}{@link String }{@code >}{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "coordinates")
    public JAXBElement<List<String>> createCoordinates(final List<String> value) {
        return new JAXBElement<List<String>>(_Coordinates_QNAME, ((Class) List.class), null, ((List<String> ) value));
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "scaleDenominator", defaultValue = "1.0")
    public JAXBElement<Double> createScaleDenominator(final Double value) {
        return new JAXBElement<Double>(_ScaleDenominator_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "TimeSpanSimpleExtensionGroup")
    public JAXBElement<Object> createTimeSpanSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_TimeSpanSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "east", defaultValue = "180.0")
    public JAXBElement<Double> createEast(final Double value) {
        return new JAXBElement<Double>(_East_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "PhotoOverlayObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createPhotoOverlayObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_PhotoOverlayObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LineStyleObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createLineStyleObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_LineStyleObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractFeatureObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createAbstractFeatureObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_AbstractFeatureObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "minRefreshPeriod", defaultValue = "0.0")
    public JAXBElement<Double> createMinRefreshPeriod(final Double value) {
        return new JAXBElement<Double>(_MinRefreshPeriod_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "DocumentObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createDocumentObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_DocumentObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "TimeStampObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createTimeStampObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_TimeStampObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ScreenOverlayObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createScreenOverlayObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_ScreenOverlayObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LineStyleSimpleExtensionGroup")
    public JAXBElement<Object> createLineStyleSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_LineStyleSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "leftFov", defaultValue = "0.0")
    public JAXBElement<Double> createLeftFov(final Double value) {
        return new JAXBElement<Double>(_LeftFov_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DocumentType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Document", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractContainerGroup")
    public JAXBElement<DocumentType> createDocument(final DocumentType value) {
        return new JAXBElement<DocumentType>(_Document_QNAME, DocumentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FolderType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Folder", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractContainerGroup")
    public JAXBElement<FolderType> createFolder(final FolderType value) {
        return new JAXBElement<FolderType>(_Folder_QNAME, FolderType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "PlacemarkObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createPlacemarkObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_PlacemarkObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LocationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "Location", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<LocationType> createLocation(final LocationType value) {
        return new JAXBElement<LocationType>(_Location_QNAME, LocationType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "rotation", defaultValue = "0.0")
    public JAXBElement<Double> createRotation(final Double value) {
        return new JAXBElement<Double>(_Rotation_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractLatLonBoxSimpleExtensionGroup")
    public JAXBElement<Object> createAbstractLatLonBoxSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_AbstractLatLonBoxSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "AbstractColorStyleObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createAbstractColorStyleObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_AbstractColorStyleObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "styleUrl")
    public JAXBElement<String> createStyleUrl(final String value) {
        return new JAXBElement<String>(_StyleUrl_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "minFadeExtent", defaultValue = "0.0")
    public JAXBElement<Double> createMinFadeExtent(final Double value) {
        return new JAXBElement<Double>(_MinFadeExtent_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "heading", defaultValue = "0.0")
    public JAXBElement<Double> createHeading(final Double value) {
        return new JAXBElement<Double>(_Heading_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "TimeSpanObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createTimeSpanObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_TimeSpanObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "maxFadeExtent", defaultValue = "0.0")
    public JAXBElement<Double> createMaxFadeExtent(final Double value) {
        return new JAXBElement<Double>(_MaxFadeExtent_QNAME, Double.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Vec2Type }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "rotationXY")
    public JAXBElement<Vec2Type> createRotationXY(final Vec2Type value) {
        return new JAXBElement<Vec2Type>(_RotationXY_QNAME, Vec2Type.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IconStyleType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "IconStyle", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractColorStyleGroup")
    public JAXBElement<IconStyleType> createIconStyle(final IconStyleType value) {
        return new JAXBElement<IconStyleType>(_IconStyle_QNAME, IconStyleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "DocumentSimpleExtensionGroup")
    public JAXBElement<Object> createDocumentSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_DocumentSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ModelSimpleExtensionGroup")
    public JAXBElement<Object> createModelSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_ModelSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Vec2Type }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "hotSpot")
    public JAXBElement<Vec2Type> createHotSpot(final Vec2Type value) {
        return new JAXBElement<Vec2Type>(_HotSpot_QNAME, Vec2Type.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "StyleMapObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createStyleMapObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_StyleMapObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AbstractObjectType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "BalloonStyleObjectExtensionGroup", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractObjectGroup")
    public JAXBElement<AbstractObjectType> createBalloonStyleObjectExtensionGroup(final AbstractObjectType value) {
        return new JAXBElement<AbstractObjectType>(_BalloonStyleObjectExtensionGroup_QNAME, AbstractObjectType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ColorModeEnumType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "colorMode", defaultValue = "normal")
    public JAXBElement<ColorModeEnumType> createColorMode(final ColorModeEnumType value) {
        return new JAXBElement<ColorModeEnumType>(_ColorMode_QNAME, ColorModeEnumType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "viewFormat")
    public JAXBElement<String> createViewFormat(final String value) {
        return new JAXBElement<String>(_ViewFormat_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "BoundarySimpleExtensionGroup")
    public JAXBElement<Object> createBoundarySimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_BoundarySimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LabelStyleType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "LabelStyle", substitutionHeadNamespace = "http://www.opengis.net/kml/2.2", substitutionHeadName = "AbstractColorStyleGroup")
    public JAXBElement<LabelStyleType> createLabelStyle(final LabelStyleType value) {
        return new JAXBElement<LabelStyleType>(_LabelStyle_QNAME, LabelStyleType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "targetHref")
    public JAXBElement<String> createTargetHref(final String value) {
        return new JAXBElement<String>(_TargetHref_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ObjectSimpleExtensionGroup")
    public JAXBElement<Object> createObjectSimpleExtensionGroup(final Object value) {
        return new JAXBElement<Object>(_ObjectSimpleExtensionGroup_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ExtendedDataType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/kml/2.2", name = "ExtendedData")
    public JAXBElement<ExtendedDataType> createExtendedData(final ExtendedDataType value) {
        return new JAXBElement<ExtendedDataType>(_ExtendedData_QNAME, ExtendedDataType.class, null, value);
    }

}
