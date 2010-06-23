package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.xal.model.AddressDetails;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;
import static org.geotoolkit.data.kml.xml.KmlModelConstants.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultNetworkLink extends DefaultAbstractFeature implements NetworkLink {

    private boolean refreshVisibility;
    private boolean flyToView;
    private Link link;
    private List<SimpleType> networkLinkSimpleExtensions;
    private List<AbstractObject> networkLinkObjectExtensions;

    /**
     * 
     */
    public DefaultNetworkLink(){
        this.refreshVisibility = DEF_REFRESH_VISIBILITY;
        this.flyToView = DEF_FLY_TO_VIEW;
        this.networkLinkSimpleExtensions = EMPTY_LIST;
        this.networkLinkObjectExtensions = EMPTY_LIST;
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param name
     * @param visibility
     * @param open
     * @param author
     * @param atomLink
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
     * @param refreshVisibility
     * @param flyToView
     * @param link
     * @param networkLinkSimpleExtensions
     * @param networkLinkObjectExtensions
     */
    public DefaultNetworkLink(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink atomLink,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            boolean refreshVisibility, boolean flyToView, Link link,
            List<SimpleType> networkLinkSimpleExtensions, List<AbstractObject> networkLinkObjectExtensions){
        super(objectSimpleExtensions, idAttributes, name, visibility, open, author, atomLink,
                address, addressDetails, phoneNumber, snippet, description, view,
                timePrimitive, styleUrl, styleSelector, region, extendedData,
                abstractFeatureSimpleExtensions, abstractFeatureObjectExtensions);
        this.refreshVisibility = refreshVisibility;
        this.flyToView = flyToView;
        this.link = link;
        this.networkLinkSimpleExtensions = (networkLinkSimpleExtensions == null) ? EMPTY_LIST : networkLinkSimpleExtensions;
        this.networkLinkObjectExtensions = (networkLinkObjectExtensions == null) ? EMPTY_LIST : networkLinkObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getRefreshVisibility() {return this.refreshVisibility;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getFlyToView() {return this.flyToView;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Link getLink() {return this.link;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getNetworkLinkSimpleExtensions() {return this.networkLinkSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getNetworkLinkObjectExtensions() {return this.networkLinkObjectExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRefreshVisibility(boolean refreshVisibility) {
        this.refreshVisibility = refreshVisibility;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setFlyToView(boolean flyToView) {
        this.flyToView = flyToView;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setLink(Link link) {
        this.link = link;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setNetworkLinkSimpleExtensions(List<SimpleType> networkLinkSimpleExtensions) {
        this.networkLinkSimpleExtensions = networkLinkSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setNetworkLinkObjectExtensions(List<AbstractObject> networkLinkObjectExtensions) {
        this.networkLinkObjectExtensions = networkLinkObjectExtensions;
    }
}
