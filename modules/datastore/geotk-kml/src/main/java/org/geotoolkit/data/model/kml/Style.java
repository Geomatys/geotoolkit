package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 * <p>This interface maps Style element.</p>
 *
 * <pre>
 * &lt;element name="Style" type="kml:StyleType" substitutionGroup="kml:AbstractStyleSelectorGroup"/>
 *
 * &lt;complexType name="StyleType" final="#all">
 *  &lt;complexContent>
 *      &lt;extension base="kml:AbstractStyleSelectorType">
 *          &lt;sequence>
 *              &lt;element ref="kml:IconStyle" minOccurs="0"/>
 *              &lt;element ref="kml:LabelStyle" minOccurs="0"/>
 *              &lt;element ref="kml:LineStyle" minOccurs="0"/>
 *              &lt;element ref="kml:PolyStyle" minOccurs="0"/>
 *              &lt;element ref="kml:BalloonStyle" minOccurs="0"/>
 *              &lt;element ref="kml:ListStyle" minOccurs="0"/>
 *              &lt;element ref="kml:StyleSimpleExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *              &lt;element ref="kml:StyleObjectExtensionGroup" minOccurs="0" maxOccurs="unbounded"/>
 *          &lt;/sequence>
 *      &lt;/extension>
 *  &lt;/complexContent>
 * &lt;/complexType>
 *
 * &lt;element name="StyleSimpleExtensionGroup" abstract="true" type="anySimpleType"/>
 * &lt;element name="StyleObjectExtensionGroup" abstract="true" substitutionGroup="kml:AbstractObjectGroup"/>
 * </pre>
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

    /**
     *
     * @param iconStyle
     */
    public void setIconStyle(IconStyle iconStyle);

    /**
     *
     * @param labelStyle
     */
    public void setLabelStyle(LabelStyle labelStyle);

    /**
     *
     * @param lineStyle
     */
    public void setLineStyle(LineStyle lineStyle);

    /**
     *
     * @param polyStyle
     */
    public void setPolyStyle(PolyStyle polyStyle);

    /**
     *
     * @param baloonStyle
     */
    public void setBalloonStyle(BalloonStyle baloonStyle);

    /**
     *
     * @param listStyle
     */
    public void setListStyle(ListStyle listStyle);

    /**
     *
     * @param styleSimpleExtensions
     */
    public void setStyleSimpleExtensions(List<SimpleType> styleSimpleExtensions);

    /**
     * 
     * @param styleObjectExtensions
     */
    public void setStyleObjectExtensions(List<AbstractObject> styleObjectExtensions);
}
