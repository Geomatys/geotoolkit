package org.geotoolkit.data.kml.model;

import java.awt.Color;
import java.util.List;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.xal.model.AddressDetails;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractOverlay extends DefaultAbstractFeature implements AbstractOverlay {

    protected Color color;
    protected int drawOrder = DEF_DRAW_ORDER;
    protected Icon icon;
    protected List<SimpleType> abstractOveraySimpleExtensions;
    protected List<AbstractObject> abstractOverlayObjectExtensions;

    /**
     * 
     */
    protected DefaultAbstractOverlay(){
        super();
        this.abstractOveraySimpleExtensions = EMPTY_LIST;
        this.abstractOverlayObjectExtensions = EMPTY_LIST;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param visibility
     * @param open
     * @param author
     * @param link
     * @param address
     * @param addressDetails
     * @param phoneNumber
     * @param snippet
     * @param description
     * @param view
     * @param timePrimitive
     * @param styleUrl
     * @param styleSelector
     * @param region
     * @param extendedData
     * @param abstractFeatureSimpleExtensions
     * @param abstractFeatureObjectExtensions
     * @param color
     * @param drawOrder
     * @param icon
     * @param abstractOveraySimpleExtensions
     * @param abstractOverlayObjectExtensions
     */
    protected DefaultAbstractOverlay(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink link,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            Color color, int drawOrder, Icon icon,
            List<SimpleType> abstractOveraySimpleExtensions, List<AbstractObject> abstractOverlayObjectExtensions){
       super(objectSimpleExtensions, idAttributes,
            name, visibility, open, author, link,
            address, addressDetails, phoneNumber, snippet,
            description, view, timePrimitive,
            styleUrl, styleSelector,
            region, extendedData,
            abstractFeatureSimpleExtensions,
            abstractFeatureObjectExtensions);
       this.color = color;
       this.drawOrder = drawOrder;
       this.icon = icon;
       this.abstractOveraySimpleExtensions = (abstractOveraySimpleExtensions == null) ? EMPTY_LIST : abstractOveraySimpleExtensions;
       this.abstractOverlayObjectExtensions = (abstractOverlayObjectExtensions == null) ? EMPTY_LIST : abstractOverlayObjectExtensions;
        
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Color getColor() {return this.color;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public int getDrawOrder() {return this.drawOrder;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Icon getIcon() {return this.icon;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getAbstractOverlaySimpleExtensions() {return this.abstractOveraySimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getAbstractOverlayObjectExtensions() {return this.abstractOverlayObjectExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setColor(Color color){this.color = color;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDrawOrder(int drawOrder){this.drawOrder = drawOrder;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setIcon(Icon icon){this.icon = icon;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractOverlaySimpleExtensions(List<SimpleType> abstractOverlaySimpleExtensions){
        this.abstractOveraySimpleExtensions = abstractOverlaySimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractOverlayObjectExtensions(List<AbstractObject> abstractOverlayObjectExtensions){
        this.abstractOverlayObjectExtensions = abstractOverlayObjectExtensions;
    }

}
