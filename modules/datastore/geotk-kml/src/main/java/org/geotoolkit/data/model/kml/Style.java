package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Style element.</p>
 *
 * <br />&lt;element name="Style" type="kml:StyleType" substitutionGroup="kml:AbstractStyleSelectorGroup"/>
 * <br />&lt;complexType name="StyleType" final="#all">
 * <br />&lt;complexContent>
 * <br />&lt;extension base="kml:AbstractStyleSelectorType">
 * <br />&lt;sequence>
 * <br />&lt;element ref="kml:IconStyle" minOccurs="0"/>
 * <br />&lt;element ref="kml:LabelStyle" minOccurs="0"/>
 * <br />&lt;element ref="kml:LineStyle" minOccurs="0"/>
 * <br />&lt;element ref="kml:PolyStyle" minOccurs="0"/>
 * <br />&lt;element ref="kml:BalloonStyle" minOccurs="0"/>
 * <br />&lt;element ref="kml:ListStyle" minOccurs="0"/>
 * <br />&lt;element ref="kml:StyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;element ref="kml:StyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 * <br />&lt;/sequence>
 * <br />&lt;/extension>
 * <br />&lt;/complexContent>
 * <br />&lt;/complexType>
 * <br />&lt;element name="StyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * <br />&lt;element name="StyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 *
 * @author Samuel Andrés
 */
public interface Style extends AbstractStyleSelector {

    /**
     *
     * @return
     */
    public IconStyle getIconStyle();

    /**
     *
     * @return
     */
    public LabelStyle getLabelStyle();

    /**
     *
     * @return
     */
    public LineStyle getLineStyle();

    /**
     *
     * @return
     */
    public PolyStyle getPolyStyle();

    /**
     *
     * @return
     */
    public BalloonStyle getBalloonStyle();

    /**
     *
     * @return
     */
    public ListStyle getListStyle();

    /**
     *
     * @return the list of Style simple extensions.
     */
    public List<SimpleType> getStyleSimpleExtensions();

    /**
     *
     * @return th elist of Style object extensions.
     */
    public List<AbstractObject> getStyleObjectExtensions();
}
