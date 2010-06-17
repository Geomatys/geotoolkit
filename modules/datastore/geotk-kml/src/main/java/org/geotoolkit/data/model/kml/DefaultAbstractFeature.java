package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public abstract class DefaultAbstractFeature extends DefaultAbstractObject implements AbstractFeature {

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
     */
    protected DefaultAbstractFeature(){
        super();
        this.styleSelector = EMPTY_LIST;
        this.featureSimpleExtensions = EMPTY_LIST;
        this.featureObjectExtensions = EMPTY_LIST;
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
     */
    protected DefaultAbstractFeature(List<SimpleType> objectSimpleExtensions, IdAttributes idAttributes,
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
        this.styleSelector = (styleSelector == null) ? EMPTY_LIST : styleSelector;
        this.region = region;
        this.extendedData = extendedData;
        this.featureSimpleExtensions = (abstractFeatureSimpleExtensions == null) ? EMPTY_LIST : abstractFeatureSimpleExtensions;
        this.featureObjectExtensions = (abstractFeatureObjectExtensions == null) ? EMPTY_LIST : abstractFeatureObjectExtensions;
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

    /**
     *
     * @{@inheritDoc }
     */
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

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setName(String name){this.name = name;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setVisibility(boolean visibility){this.visibility = visibility;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setOpen(boolean open){this.open = open;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAuthor(AtomPersonConstruct author){this.author = author;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAtomLink(AtomLink atomLink){this.atomLink = atomLink;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAddress(String address){this.address = address;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAddressDetails(AddressDetails addressDetails){this.addressDetails = addressDetails;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSnippet(String snippet){this.snippet = snippet;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDescription(String description){this.description = description;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setView(AbstractView view){this.view = view;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setTimePrimitive(AbstractTimePrimitive timePrimitive){this.timePrimitive = timePrimitive;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setStyleUrl(String styleUrl){this.styleUrl = styleUrl;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setStyleSelectors(List<AbstractStyleSelector> styleSelectors){this.styleSelector = styleSelectors;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setRegion(Region region){this.region = region;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setExtendedData(ExtendedData extendedData){this.extendedData = extendedData;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractFeatureSimpleExtensions(List<SimpleType> abstractFeatureSimpleExtensions){
        this.featureSimpleExtensions = abstractFeatureSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractFeatureObjectExtensions(List<AbstractObject> abstractFeatureObjectExtensions){
        this.featureObjectExtensions = abstractFeatureObjectExtensions;
    }

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
