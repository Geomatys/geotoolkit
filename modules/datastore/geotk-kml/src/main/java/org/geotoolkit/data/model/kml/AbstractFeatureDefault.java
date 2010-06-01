package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public abstract class AbstractFeatureDefault extends AbstractObjectDefault implements AbstractFeature {

    protected String name;
    protected boolean visibility;
    protected boolean open;
    protected AtomPersonConstruct author;
    protected AtomLink atomLink;
    protected String address;
    protected AddressDetails addressDetails;
    protected String phoneNumber;
    protected String snippet;
    protected String description;
    protected AbstractView view;
    protected AbstractTimePrimitive timePrimitive;
    protected String styleUrl;
    protected List<AbstractStyleSelector> styleSelector;
    protected Region region;
    protected ExtendedData extendedData;
    protected List<SimpleType> featureSimpleExtensions;
    protected List<AbstractObject> featureObjectExtensions;

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
     */
    protected AbstractFeatureDefault(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
            String name, boolean visibility, boolean open, AtomPersonConstruct author, AtomLink atomLink,
            String address, AddressDetails addressDetails, String phoneNumber, String snippet,
            String description, AbstractView view, AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions){

        super(objectSimpleExtensions, idAttributes);
        this.name = name;
        this.visibility = visibility;
        this.open = open;
        this.author = author;
        this.atomLink = atomLink;
        this.address = address;
        this.addressDetails = addressDetails;
        this.phoneNumber = phoneNumber;
        this.snippet = snippet;
        this.description = description;
        this.view = view;
        this.timePrimitive = timePrimitive;
        this.styleUrl = styleUrl;
        this.styleSelector = styleSelector;
        this.region = region;
        this.extendedData = extendedData;
        this.featureSimpleExtensions = abstractFeatureSimpleExtensions;
        this.featureObjectExtensions = abstractFeatureObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getName() {return this.name;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getVisibility() {return this.visibility;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public boolean getOpen() {return this.open;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AtomPersonConstruct getAuthor() {return this.author;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AtomLink getAtomLink() {return this.atomLink;}

    @Override
    public String getAddress() {return this.address;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AddressDetails getAddressDetails() {return this.addressDetails;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getPhoneNumber() {return this.phoneNumber;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getSnippet() {return this.snippet;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getDescription() {return this.description;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AbstractView getView() {return this.view;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AbstractTimePrimitive getTimePrimitive() {return this.timePrimitive;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getStyleUrl() {return this.styleUrl;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractStyleSelector> getStyleSelectors() {return this.styleSelector;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Region getRegion(){return this.region;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public ExtendedData getExtendedData(){return this.extendedData;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getAbstractFeatureSimpleExtensions() {return this.featureSimpleExtensions;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getAbstractFeatureObjectExtensions() {return this.featureObjectExtensions;}

    @Override
    public String toString(){
        String resultat = "AbstractFeatureDefault : "+
                "\n\tname : "+this.name+
                "\n\tvisibility : "+this.visibility+
                "\n\topen : "+this.open+
                "\n\tauthor : "+this.author+
                "\n\tlink : "+this.atomLink+
                "\n\taddress : "+this.address+
                "\n\taddressDetails : "+this.addressDetails+
                "\n\tphoneNumber : "+this.phoneNumber+
                "\n\tsnippet : "+this.snippet+
                "\n\tdescription : "+this.description+
                "\n\tview : "+this.view+
                "\n\ttimePrimitive : "+this.timePrimitive+
                "\n\tstyleUrl : "+this.styleUrl+
                "\n\tstyleSelectors : "+this.styleSelector.size();
                for (AbstractStyleSelector s : this.styleSelector){
                        resultat += "\n\tstyleSelector : "+s;
                }
                resultat += "\n\tregion : "+this.region+
                "\n\textendedData : "+this.extendedData;
        return resultat;
    }

}
