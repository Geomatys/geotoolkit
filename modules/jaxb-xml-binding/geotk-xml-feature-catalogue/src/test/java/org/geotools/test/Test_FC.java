/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geotools.test;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import net.seagis.cat.csw.v202.Capabilities;
import net.seagis.cat.csw.v202.DescribeRecordResponseType;
import net.seagis.cat.csw.v202.DescribeRecordType;
import net.seagis.cat.csw.v202.DistributedSearchType;
import net.seagis.cat.csw.v202.ElementSetNameType;
import net.seagis.cat.csw.v202.ElementSetType;
import net.seagis.cat.csw.v202.GetCapabilities;
import net.seagis.cat.csw.v202.GetDomainResponseType;
import net.seagis.cat.csw.v202.GetDomainType;
import net.seagis.cat.csw.v202.GetRecordByIdResponseType;
import net.seagis.cat.csw.v202.GetRecordByIdType;
import net.seagis.cat.csw.v202.GetRecordsResponseType;
import net.seagis.cat.csw.v202.GetRecordsType;
import net.seagis.cat.csw.v202.HarvestResponseType;
import net.seagis.cat.csw.v202.HarvestType;
import net.seagis.cat.csw.v202.QueryConstraintType;
import net.seagis.cat.csw.v202.QueryType;
import net.seagis.cat.csw.v202.ResultType;
import net.seagis.cat.csw.v202.TransactionResponseType;
import net.seagis.cat.csw.v202.TransactionType;
import net.seagis.ws.rs.NamespacePrefixMapperImpl;
import net.seagis.ows.v100.ExceptionReport;
import org.geotools.feature.catalog.AssociationRoleImpl;
import org.geotools.feature.catalog.BindingImpl;
import org.geotools.feature.catalog.BoundFeatureAttributeImpl;
import org.geotools.feature.catalog.ConstraintImpl;
import org.geotools.feature.catalog.DefinitionReferenceImpl;
import org.geotools.feature.catalog.DefinitionSourceImpl;
import org.geotools.feature.catalog.FeatureAssociationImpl;
import org.geotools.feature.catalog.FeatureAttributeImpl;
import org.geotools.feature.catalog.FeatureCatalogueImpl;
import org.geotools.feature.catalog.FeatureOperationImpl;
import org.geotools.feature.catalog.FeatureTypeImpl;
import org.geotools.feature.catalog.InheritanceRelationImpl;
import org.geotools.feature.catalog.ListedValueImpl;
import org.geotools.feature.catalog.PropertyTypeImpl;
import org.geotools.metadata.iso.MetaDataImpl;
import org.geotools.metadata.iso.citation.AddressImpl;
import org.geotools.metadata.iso.citation.CitationDateImpl;
import org.geotools.metadata.iso.citation.CitationImpl;
import org.geotools.metadata.iso.citation.ContactImpl;
import org.geotools.metadata.iso.citation.ResponsiblePartyImpl;
import org.geotools.metadata.iso.citation.TelephoneImpl;
import org.geotools.service.ServiceIdentificationImpl;
import org.geotools.util.LocalName;
import org.geotools.util.SimpleInternationalString;
import org.opengis.feature.catalog.DefinitionSource;
import org.opengis.feature.catalog.FeatureType;
import org.opengis.metadata.citation.CitationDate;
import org.opengis.metadata.citation.DateType;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.Role;
import org.geotools.util.Multiplicity;
import org.geotools.util.MultiplicityRange;
import org.geotools.util.TypeName;
import org.opengis.feature.catalog.AssociationRole;
import org.opengis.feature.catalog.Constraint;
import org.opengis.feature.catalog.DefinitionReference;
import org.opengis.feature.catalog.ListedValue;
import org.opengis.feature.catalog.PropertyType;
import org.opengis.feature.catalog.RoleType;
import org.opengis.util.UnlimitedInteger;

/**
 *
 * @author guilhem
 */
public class Test_FC {
    
     /**
     * The main method.
     * @param args the path to file to read.
     * @throws Exception*/
    public static Logger logger = Logger.getLogger("main");
     
    public static void main(String[] args) throws Exception {

        
        //String fileName = "generated19110-InheritanceRelation-.xml";
        //String fileName = "generated19110-featureCatalogue-.xml";
        //String fileName = "generated19110-featureOperation-.xml"; 
        String fileName = "generated19110-featureAssociation-.xml";
        // Unmarshalles the given XML file to objects
        JAXBContext context;
        
        context = JAXBContext.newInstance(Capabilities.class, DescribeRecordType.class
                        ,DistributedSearchType.class, ElementSetNameType.class, ElementSetType.class
                        ,GetCapabilities.class, GetDomainType.class, GetRecordByIdType.class
                        ,GetRecordsType.class, HarvestType.class, QueryConstraintType.class
                        ,QueryType.class, ResultType.class, TransactionType.class
                        ,GetRecordsResponseType.class, GetRecordByIdResponseType.class
                        ,DescribeRecordResponseType.class, GetDomainResponseType.class
                        ,TransactionResponseType.class, HarvestResponseType.class
                        ,ExceptionReport.class, net.seagis.ows.v110.ExceptionReport.class
                        ,net.seagis.dublincore.v2.terms.ObjectFactory.class,
                        
                         ServiceIdentificationImpl.class, 
                         MetaDataImpl.class,
                         AssociationRoleImpl.class, BindingImpl.class, BoundFeatureAttributeImpl.class,
                         ConstraintImpl.class, DefinitionReferenceImpl.class, DefinitionSourceImpl.class,
                         FeatureAssociationImpl.class, FeatureAttributeImpl.class, FeatureCatalogueImpl.class,
                         FeatureOperationImpl.class, FeatureTypeImpl.class, InheritanceRelationImpl.class,
                         ListedValueImpl.class, PropertyTypeImpl.class, Multiplicity.class);   
        
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Marshaller marshaller     = context.createMarshaller();
         try {
            //unmarshaller.setProperty("com.sun.xml.bind.IDResolver", new DocumentIDResolver()); 
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapperImpl("http://www.isotc211.org/2005/srv"));
        } catch (PropertyException e) {
            System.out.println("prefix non trouv");
        }
        
        Object request;
        if (true){

            request = unmarshaller.unmarshal(new FileReader(fileName));
           // System.out.println("unmarshalled:" + request.getClass().getSimpleName() +" \n " +  request.toString());
       //} else {
        
            String name = "Digital Geographic information Exchange Standard (DIGEST) Feature and Attribute Coding Catalogue (FACC)";
            List<String> scopes = new ArrayList<String>();
            scopes.add("Hydrography");scopes.add("Ports and Harbours");scopes.add("Transportation Networks");
            String versionNumber = "2.1";
            Date versionDate = new Date(2000, 9, 30);
            
            // producer
            ResponsiblePartyImpl producer = new ResponsiblePartyImpl();
            producer.setIndividualName("John Q.Public");
            producer.setOrganisationName(new SimpleInternationalString("US National Geospatial-Intelligence Agency (NGA)"));
            ContactImpl contact = new ContactImpl();
            TelephoneImpl phone = new TelephoneImpl();
            List<String> facsmiles = new ArrayList<String>();
            facsmiles.add("1 703 XXX XXX");
            phone.setFacsimiles(facsmiles);
            phone.setVoices(facsmiles);
            contact.setPhone(phone);
            AddressImpl address = new AddressImpl();
            List<String> dps = new ArrayList<String>();
            dps.add("12310 Sunrise Valley Drive");
            address.setDeliveryPoints(dps);
            address.setCity(new SimpleInternationalString("Reston"));
            address.setAdministrativeArea(new SimpleInternationalString("Virginia"));
            address.setPostalCode("2091-3449");
            List<String> ema = new ArrayList<String>();
            ema.add("PublicJQ@nga.mil");
            address.setElectronicMailAddresses(ema);
            contact.setAddress(address);
            producer.setContactInfo(contact);
            producer.setRole(Role.POINT_OF_CONTACT);
            
            //definition source
            List<DefinitionSource> defSources = new ArrayList<DefinitionSource>();
            CitationImpl source = new CitationImpl();
            source.setTitle(new SimpleInternationalString("International Hydrographic Organization (IHO) Hydrographic Dictionnary, Part I, Volume I English"));
            List<CitationDate> dates = new ArrayList<CitationDate>();
            CitationDateImpl date = new CitationDateImpl();
            date.setDate(new Date(1994,1,1));
            date.setDateType(DateType.PUBLICATION);
            dates.add(date);
            source.setDates(dates);
            source.setEdition(new SimpleInternationalString("Fifth"));
            List<ResponsibleParty> rps = new ArrayList<ResponsibleParty>();
            ResponsiblePartyImpl rp = new ResponsiblePartyImpl();
            rp.setOrganisationName(new SimpleInternationalString("International Hydrographic Bureau"));
            rp.setRole(Role.PUBLISHER);
            rps.add(rp);
            source.setCitedResponsibleParties(rps);
            source.setOtherCitationDetails(new SimpleInternationalString("Special publication n°32"));
            DefinitionSource defSource = new DefinitionSourceImpl("ds-IHO", source);
            defSources.add(defSource);
            
            //featureTypes 1
            
            
            List<FeatureType> ftypes = new ArrayList<FeatureType>();
            String def = "An excavation made in the earth for the purpose of extracting natural deposit. (see also AQ090)";
            String code = "DEP";
            List<org.opengis.util.LocalName> aliases = new ArrayList<org.opengis.util.LocalName>();
            aliases.add(new LocalName("Extraction Mine"));
            List<PropertyType> cof = new ArrayList<PropertyType>();
            
            
            //attribute constraint
            Constraint constraint   = new ConstraintImpl("Positive values represent distance below the refernce point from wixh the measurement is made");
            List<Constraint> consts = new ArrayList<Constraint>();
            consts.add(constraint);
            
            // feature attribute 1 with constraint
            FeatureAttributeImpl attr = new FeatureAttributeImpl("attribute-1",
                                                                 new LocalName("Depth"),
                                                                 "Distance measured from the highest",
                                                                 new Multiplicity(new MultiplicityRange(1, new UnlimitedInteger(1))),
                                                                 null,
                                                                 consts,
                                                                 code,
                                                                 null,
                                                                 new TypeName("Real"));
            cof.add(attr);
            
            // listed values
            List<ListedValue> values = new ArrayList<ListedValue>();
            
            ListedValue v1 = new ListedValueImpl("0", "Unknown", "The attribute value is missing", null);
            values.add(v1);
            
            //definition reference
            DefinitionReference defRef      = new DefinitionReferenceImpl("3833, pier", defSource);
            
            ListedValue v2 = new ListedValueImpl("1", "Pier", null, defRef);
            values.add(v2);
            
            DefinitionReference defRef2 = new DefinitionReferenceImpl("5985, wharf",defSource);
            
            ListedValue v3 = new ListedValueImpl("2", "Wharf", null, defRef2);
            values.add(v3);
            
            DefinitionReference defRef3 = new DefinitionReferenceImpl("4125, quay", defSource);
            
            ListedValue v4 = new ListedValueImpl("3", "Quay", null, defRef3);
            values.add(v4);
            
            ListedValue v5 = new ListedValueImpl("997", "Unpopulated", "The attribute value exist, but due to the policy considerations it cannot be given", null);
            values.add(v5);
            
            ListedValue v6 = new ListedValueImpl("998", "Not applicable", "No attribute value in the range of possible attribute values is applicable", null);
            values.add(v6);
            
            ListedValue v7 = new ListedValueImpl("999", "Other", "The attribute value cannot be given for some reason other than it is 'Multiple', 'Not applicable', 'Unknown', or 'Unpopulated'", null);
            values.add(v7);
            
            
            // feature attribute 2 with listed value
            FeatureAttributeImpl attr2 = new FeatureAttributeImpl("attribute-2",
                                                                 new LocalName("Pier/Wharf/Quay classification"),
                                                                 "Classification of decked berthing structure, based on configuration and structure",
                                                                 new Multiplicity(new MultiplicityRange(1, new UnlimitedInteger(1))),
                                                                 null,
                                                                 null,
                                                                 code,
                                                                 values,
                                                                 new TypeName("Real"));
            cof.add(attr2);
            
            code = "AA010";
            FeatureTypeImpl ft1 = new FeatureTypeImpl(new LocalName("Mine"), 
                                                      def, 
                                                      code, 
                                                      false, 
                                                      aliases, 
                                                      null, 
                                                      cof);
            
            attr.setFeatureType(ft1);
            
            
            ftypes.add(ft1);
            
            
            
            
            
            //field of application
            List<String> foa = new ArrayList<String>();
            foa.add("Military Engineering");
            foa.add("Marine Navigation");
            FeatureCatalogueImpl catalogue = new FeatureCatalogueImpl("cat-1",
                                                                  name, 
                                                                  scopes,
                                                                  producer,
                                                                  versionDate,
                                                                  versionNumber,
                                                                  ftypes,
                                                                  defSources,
                                                                  foa,
                                                                  "Gofer");
            
            ft1.setFeatureCatalogue(catalogue);
            
            
            
            
            
            
            //feature type 2
            List<PropertyType> cof2 = new ArrayList<PropertyType>();
            
            FeatureTypeImpl ft2 = new FeatureTypeImpl(new LocalName("Road"), 
                                                      "An open way maintained for vehicular use.", 
                                                      "AP030", 
                                                      false, 
                                                      null, 
                                                      catalogue, 
                                                      cof2);
            
            //feature type 3
            List<PropertyType> cof3 = new ArrayList<PropertyType>();
            
            FeatureTypeImpl ft3 = new FeatureTypeImpl(new LocalName("Bidge"), 
                                                      "A man made structure spanning and providing passage over a body of water", 
                                                      "AQ040", 
                                                      false, 
                                                      null, 
                                                      catalogue, 
                                                      cof3);
            
            // Assoication role
            AssociationRoleImpl role1 = new AssociationRoleImpl("role-1",
                                                            new LocalName("Over"),
                                                            "Bridge whitch the road crosses over ...",
                                                            new Multiplicity(new MultiplicityRange(1, new UnlimitedInteger(Integer.MAX_VALUE))),
                                                            ft2,
                                                            null,
                                                            null,
                                                            RoleType.ORDINARY,
                                                            true,
                                                            true,
                                                            null,
                                                            ft3);
            cof2.add(role1);
            
             // Assoication role
            AssociationRoleImpl role2 = new AssociationRoleImpl("role-2",
                                                            new LocalName("Under"),
                                                            "Roads which cross this bridge.",
                                                           new Multiplicity(new MultiplicityRange(0, new UnlimitedInteger(1))),
                                                            ft3,
                                                            null,
                                                            null,
                                                            RoleType.ORDINARY,
                                                            false,
                                                            true,
                                                            null,
                                                            ft2);
            cof3.add(role2);
            
            // Feature association
            List<AssociationRole> roles = new ArrayList<AssociationRole>();
            
            FeatureAssociationImpl fassoc = new FeatureAssociationImpl(new LocalName("Stacked On"),
                                                                    "An object is over another object",
                                                                    "101",
                                                                    false,
                                                                    null,
                                                                    catalogue,
                                                                    null,
                                                                    roles);
            roles.add(role1);
            roles.add(role2);
            
            role1.setRelation(fassoc);
            role2.setRelation(fassoc);
            
            
            //// inheritance relation
            
            
            //feature type 4
            
            FeatureTypeImpl ft4 = new FeatureTypeImpl(new LocalName("Building"), 
                                                      "A relatively permanent structure, roofed and usually walled and designed for some particular use.", 
                                                      "AL015", 
                                                      false, 
                                                      null, 
                                                      catalogue, 
                                                      null);
             //feature type 5
            
            FeatureTypeImpl ft5 = new FeatureTypeImpl(new LocalName("Lighthouse"), 
                                                      "A distinctive structure exhibiting light(s) designed to serve as an aid to navigation.", 
                                                      "BC050", 
                                                      false, 
                                                      null, 
                                                      catalogue, 
                                                      null);
            
            // inheritanceRelation
            
            InheritanceRelationImpl inherit = new InheritanceRelationImpl("is a",
                                                                         "An object is classified as a specialization of another object",
                                                                         false,
                                                                         ft5,
                                                                         ft4);
            inherit.setId("inherits-1");
            ft4.getInheritsTo().add(inherit);
            ft5.getInheritsFrom().add(inherit);
            
            
            // feature operation
            FeatureOperationImpl operation = new FeatureOperationImpl();
            operation.setId("operation-1");
            operation.setMemberName(new LocalName("Raise dam"));
            operation.setDefinition("The action of raising the dam causes changes in the discharge from the dam....");
            operation.setCardinality(new Multiplicity(new MultiplicityRange(1, new UnlimitedInteger(1))));
            operation.setFeatureType(ft5);
            operation.setSignature("damRaise((Dam) dam, (Real) newHeight) : Dam");
            operation.setFormalDefinition(" damRaise(ConstructDam(d), h) = error " +
                                          "  'cannot raise height of a damunder construction'" +
                                          " damRaise(Operate (d,i,j), h)" +
                                          "   | (h >i)    && (h < maxHeight(d)) = Operate(d,h,j)" +
                                          "   | otherwise = error 'illegal new height for dam'  ");
            
            BoundFeatureAttributeImpl bf1 = new BoundFeatureAttributeImpl();
            bf1.setId("boundatt-1");
            bf1.setAttribute(attr2);
            bf1.setFeatureType(ft5);
            
            operation.getObservesValuesOf().add(bf1);
            
            if (false) {
                request = catalogue;
            } else if (false) {
                request = fassoc;
            } else if (false){
                request = inherit;
            } else if (false) {
                request = operation;
            }
            
            System.out.println("beforeEquals");
            System.out.println("equals?" + (request.equals(catalogue)));
            System.out.println("after equals");
            
            System.out.println(request);
            /*FeatureAssociationImpl requtt = (FeatureAssociationImpl) request;
            
            
            
            
            */
            //logger.info("construit:   " + operation.toString(""));
            //logger.info("unmarshallé: " + requtt.toString(""));
        }
        
        /*FeatureCatalogueImpl cata = new FeatureCatalogueImpl();
        FeatureTypeImpl ft = new FeatureTypeImpl();
        ft.setId("ft-1");
        
        cata.setId("cat-1");
        cata.setFeatureType(ft);
        ft.setFeatureCatalogue(cata);*/
       
        String fileOutput = "output.xml";
        marshaller.marshal(request, new File(fileOutput));

       
    }

}
