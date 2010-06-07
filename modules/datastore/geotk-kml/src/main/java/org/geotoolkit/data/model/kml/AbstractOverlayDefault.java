package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public abstract class AbstractOverlayDefault extends AbstractFeatureDefault implements AbstractOverlay {

    protected final Color color;
    protected final int drawOrder;
    protected final Icon icon;
    protected final List<SimpleType> abstractOveraySimpleExtensions;
    protected final List<AbstractObject> abstractOverlayObjectExtensions;

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
    protected AbstractOverlayDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
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

}
