package org.geotoolkit.data.kml.model;

import java.util.List;
import org.geotoolkit.data.atom.model.AtomPersonConstruct;
import org.geotoolkit.data.atom.model.AtomLink;
import org.geotoolkit.data.xal.model.AddressDetails;
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPlacemark extends DefaultAbstractFeature implements Placemark {

    private AbstractGeometry abstractGeometry;
    private List<SimpleType> placemarkSimpleExtensions;
    private List<AbstractObject> placemarkObjectExtensions;

    /**
     * 
     */
    public DefaultPlacemark(){
        this.placemarkSimpleExtensions = EMPTY_LIST;
        this.placemarkObjectExtensions = EMPTY_LIST;
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
     * @param abstractGeometry
     * @param placemarkSimpleExtensions
     * @param placemarkObjectExtension
     */
    public DefaultPlacemark(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            String name,
            boolean visibility,
            boolean open,
            AtomPersonConstruct author,
            AtomLink link,
            String address,
            AddressDetails addressDetails,
            String phoneNumber, String snippet,
            String description, AbstractView view,
            AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            AbstractGeometry abstractGeometry,
            List<SimpleType> placemarkSimpleExtensions,
            List<AbstractObject> placemarkObjectExtension){

        super(objectSimpleExtensions, idAttributes,
            name, visibility, open, author, link, address, addressDetails,
            phoneNumber, snippet, description, view, timePrimitive,
            styleUrl, styleSelector, region, extendedData,
            abstractFeatureSimpleExtensions,
            abstractFeatureObjectExtensions);

        this.abstractGeometry = abstractGeometry;
        this.placemarkSimpleExtensions = (placemarkSimpleExtensions == null) ? EMPTY_LIST : placemarkSimpleExtensions;
        this.placemarkObjectExtensions = (placemarkObjectExtension == null) ? EMPTY_LIST : placemarkObjectExtension;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AbstractGeometry getAbstractGeometry() {
        return this.abstractGeometry;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleType> getPlacemarkSimpleExtensions() {
        return this.placemarkSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getPlacemarkObjectExtensions() {
        return this.placemarkObjectExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractGeometry(AbstractGeometry abstractGeometry) {
        this.abstractGeometry = abstractGeometry;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPlacemarkSimpleExtensions(List<SimpleType> placemarkSimpleExtensions) {
        this.placemarkSimpleExtensions = placemarkSimpleExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setPlacemarkObjectExtensions(List<AbstractObject> placemarkObjectExtensions) {
        this.placemarkObjectExtensions = placemarkObjectExtensions;
    }

    @Override
    public String toString(){
        String resultat = super.toString();
        resultat += "Placemark : ";
        //resultat += "\n\t"+abstractGeometry;
        return resultat;
    }
}
