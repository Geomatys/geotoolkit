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
public class PlacemarkDefault extends AbstractFeatureDefault implements Placemark {

    private final AbstractGeometry abstractGeometry;
    private final List<SimpleType> placemarkSimpleExtension;
    private final List<AbstractObject> placemarkObjectExtension;

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
    public PlacemarkDefault(List<SimpleType> objectSimpleExtensions,
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
        this.placemarkSimpleExtension = (placemarkSimpleExtensions == null) ? EMPTY_LIST : placemarkSimpleExtensions;
        this.placemarkObjectExtension = (placemarkObjectExtension == null) ? EMPTY_LIST : placemarkObjectExtension;
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
        return this.placemarkSimpleExtension;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<AbstractObject> getPlacemarkObjectExtensions() {
        return this.placemarkObjectExtension;
    }

    @Override
    public String toString(){
        String resultat = super.toString();
        resultat += "Placemark : ";
        //resultat += "\n\t"+abstractGeometry;
        return resultat;
    }
}
