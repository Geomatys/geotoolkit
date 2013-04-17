/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.data.s57;

import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.data.iso8211.FieldValueType;
import org.geotoolkit.data.iso8211.SubFieldDescription;

/**
 * 7.3.11 Agency , see S-62(TODO add all the list)
 * 
 * LAST UPDATE 11/04/2013 from http://registry.iho.int/s100_gi_registry/home.php
 * 
 * @author  Johann Sorel (Geomatys)
 */
public final class S62Agency extends S57Constants.S57CodeList<S62Agency> {
    public static final SubFieldDescription ASCIIFORMAT = new SubFieldDescription(FieldValueType.TEXT, 2);
    public static final SubFieldDescription BINARYFORMAT = new SubFieldDescription(FieldValueType.LE_INTEGER_UNSIGNED, 2);
    static final List<S62Agency> VALUES = new ArrayList<S62Agency>();
    
    ////////////////////////////////////////////////////////////////////////
    // MAIN PRODUCERS : IHO MS /////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    /** Algeria, Service Hydrographique des Forces Navales (2008-10-16) */
    public static final S62Agency ALGERIA_SERVICE_HYDROGRAPHIQUE_DES_FORCES_NAVALES = new S62Agency("DZ", 610);
    /** Argentina, Servicio de Hidrografía Naval (SHN) (2008-10-16) */
    public static final S62Agency ARGENTINA_SHN = new S62Agency("AR", 1);
    /** Australia, Australian Hydrographic Service (AHS) (2008-10-16) */
    public static final S62Agency AUSTRALIA_AHS = new S62Agency("AU", 10);
    /** Bahrain, Hydrographic Survey Office (2008-10-16) */
    public static final S62Agency BAHRAIN_HYDROGRAPHIC_SURVEY_OFFICE = new S62Agency("BH", 20);
    /** Bangladesh, Hydrographic Department (2008-10-16) */
    public static final S62Agency BANGLADESH_HYDROGRAPHIC_DEPARTMENT = new S62Agency("BD", 660);
    /** Belgium, MDK – Afdeling Kust – Division Coast (2008-10-16) */
    public static final S62Agency BELGIUM_MDK_AFDELING_KUST_DIVISION_COAST = new S62Agency("BE", 30);
    /** Brazil, Directorate of Hydrography and Navigation (DHN) (2008-10-16) */
    public static final S62Agency BRAZIL_DHN = new S62Agency("BR", 40);
    /** Cameroon, Port Autonome de Douala (PAD) (2008-10-16) */
    public static final S62Agency CAMEROON_PAD = new S62Agency("CM", 740);
    /** Canada, Canadian Hydrographic Service (CHS) (2008-10-16) */
    public static final S62Agency CANADA_CHS = new S62Agency("CA", 50);
    /** Canada, Canadian Forces (2008-10-16) */
    public static final S62Agency CANADA_CANADIAN_FORCES = new S62Agency("C4", 51);
    /** Chile, Servicio Hidrográfico y Oceanográfico de la Armada (SHOA) (2008-10-16) */
    public static final S62Agency CHILE_SHOA = new S62Agency("CL", 60);
    /** China, Maritime Safety Administration (MSA) (2008-10-16) */
    public static final S62Agency CHINA_MSA = new S62Agency("CN", 70);
    /** China, The Navigation Guarantee Department of The Chinese Navy Headquarters (2008-10-16) */
    public static final S62Agency CHINA_THE_NAVIGATION_GUARANTEE_DEPARTMENT_OF_THE_CHINESE_NAVY_HEADQUARTERS = new S62Agency("C1", 71);
    /** China, Hong Kong Special Administrative Region (2008-10-16) */
    public static final S62Agency CHINA_HONG_KONG_SPECIAL_ADMINISTRATIVE_REGION = new S62Agency("C2", 72);
    /** China, Macau Special Administrative Region (2008-10-16) */
    public static final S62Agency CHINA_MACAU_SPECIAL_ADMINISTRATIVE_REGION = new S62Agency("C3", 73);
    /** Colombia, Ministerio de Defensa Nacional (2008-10-16) */
    public static final S62Agency COLOMBIA_MINISTERIO_DE_DEFENSA_NACIONAL = new S62Agency("CO", 760);
    /** Congo (Dem. Rep. of), Ministère des Transports et Communications (2008-10-16) */
    public static final S62Agency CONGO_MINISTÈRE_DES_TRANSPORTS_ET_COMMUNICATIONS = new S62Agency("CD", 590);
    /** Croatia, Hrvatski Hidrografski Institut (2008-10-16) */
    public static final S62Agency CROATIA_HRVATSKI_HIDROGRAFSKI_INSTITUT = new S62Agency("HR", 80);
    /** Cuba, Oficina Nacional de Hidrografia y Geodesia (2008-10-16) */
    public static final S62Agency CUBA_OFICINA_NACIONAL_DE_HIDROGRAFIA_Y_GEODESIA = new S62Agency("CU", 90);
    /** Cyprus, Hydrographic Unit of the Department of Lands and Surveys (2008-10-16) */
    public static final S62Agency CYPRUS_HYDROGRAPHIC_UNIT_OF_THE_DEPARTMENT_OF_LANDS_AND_SURVEYS = new S62Agency("CY", 100);
    /** Denmark, Kort-Og Matrikelstyrelsen (KMS) (2008-10-16) */
    public static final S62Agency DENMARK_KMS = new S62Agency("DK", 110);
    /** Dominican Rep., Instituto Cartografico Militar (2008-10-16) */
    public static final S62Agency DOMINICAN_REP_INSTITUTO_CARTOGRAFICO_MILITAR = new S62Agency("DO", 120);
    /** Ecuador, Instituto Oceanográfico de la Armada (INOCAR) (2008-10-16) */
    public static final S62Agency ECUADOR_INOCAR = new S62Agency("EC", 130);
    /** Egypt, Shobat al Misaha al Baharia (2008-10-16) */
    public static final S62Agency EGYPT_SHOBAT_AL_MISAHA_AL_BAHARIA = new S62Agency("EG", 140);
    /** Estonia, Estonian Maritime Administration (EMA) (2008-10-16) */
    public static final S62Agency ESTONIA_EMA = new S62Agency("EE", 870);
    /** Fiji, Fiji Islands Maritime Safety Administration (FIMSA) (2008-10-16) */
    public static final S62Agency FIJI_FIMSA = new S62Agency("FJ", 150);
    /** Finland, Finnish Maritime Administration (FMA) (2008-10-16) */
    public static final S62Agency FINLAND_FMA = new S62Agency("FI", 160);
    /** France, Service Hydrographique et Océanographique de la Marine (SHOM) (2008-10-16) */
    public static final S62Agency FRANCE_SHOM = new S62Agency("FR", 170);
    /** Germany, Bundesamt für Seeschiffahrt und Hydrographie (BSH) (2008-10-16) */
    public static final S62Agency GERMANY_BSH = new S62Agency("DE", 180);
    /** Greece, Hellenic Navy Hydrographic Service (HNHS) (2008-10-16) */
    public static final S62Agency GREECE_HNHS = new S62Agency("GR", 190);
    /** Guatemala, Ministerio de la Defensa Nacional (2008-10-16) */
    public static final S62Agency GUATEMALA_MINISTERIO_DE_LA_DEFENSA_NACIONAL = new S62Agency("GT", 200);
    /** Guatemala, Comisión Portuaria Nacional (2008-10-16) */
    public static final S62Agency GUATEMALA_COMISIÓN_PORTUARIA_NACIONAL = new S62Agency("G1", 201);
    /** Iceland, Icelandic Coast Guard (2008-10-16) */
    public static final S62Agency ICELAND_ICELANDIC_COAST_GUARD = new S62Agency("IS", 210);
    /** India, National Hydrographic Office (NHO) (2008-10-16) */
    public static final S62Agency INDIA_NHO = new S62Agency("IN", 220);
    /** Indonesia, Jawatan Hidro-Oseanografi (JANHIDROS) (2008-10-16) */
    public static final S62Agency INDONESIA_JANHIDROS = new S62Agency("ID", 230);
    /** Ireland, Maritime Safety Directorate (2008-10-16) */
    public static final S62Agency IRELAND_MARITIME_SAFETY_DIRECTORATE = new S62Agency("IE", 990);
    /** Islamic Rep. of Iran, Ports and Shipping Organization (PSO) (2008-10-16) */
    public static final S62Agency ISLAMIC_REP_OF_IRAN_PSO = new S62Agency("IR", 240);
    /** Italy, Istituto Idrografico della Marina (IIM) (2008-10-16) */
    public static final S62Agency ITALY_IIM = new S62Agency("IT", 250);
    /** Jamaica, Surveys and Mapping Division (2008-10-16) */
    public static final S62Agency JAMAICA_SURVEYS_AND_MAPPING_DIVISION = new S62Agency("JM", 1010);
    /** Japan, Japan Hydrographic and Oceanographic Department (JHOD) (2008-10-16) */
    public static final S62Agency JAPAN_JHOD = new S62Agency("JP", 260);
    /** Korea (DPR of), Hydrographic Department (2008-10-16) */
    public static final S62Agency KOREA_HYDROGRAPHIC_DEPARTMENT = new S62Agency("KP", 270);
    /** Korea (Rep. of), National Oceanographic Research Institute (NORI) (2008-10-16) */
    public static final S62Agency KOREA_NORI = new S62Agency("KR", 280);
    /** Kuwait, Ministry of Communications (2008-10-16) */
    public static final S62Agency KUWAIT_MINISTRY_OF_COMMUNICATIONS = new S62Agency("KW", 1050);
    /** Latvia, Maritime Administration of Latvia (2008-10-16) */
    public static final S62Agency LATVIA_MARITIME_ADMINISTRATION_OF_LATVIA = new S62Agency("LV", 1060);
    /** Malaysia, National Hydrographic Centre (2008-10-16) */
    public static final S62Agency MALAYSIA_NATIONAL_HYDROGRAPHIC_CENTRE = new S62Agency("MY", 290);
    /** Mauritius , Ministry of Housing and Land, Hydrographic Unit (2008-10-16) */
    public static final S62Agency MAURITIUS_MINISTRY_OF_HOUSING_AND_LAND_HYDROGRAPHIC_UNIT = new S62Agency("MU", 1170);
    /** Mexico, Secretaria de Marina – Armada de Mexico, Direccion General Adjunta de Oceanografia, Hidrografia y Meteorologia (2008-10-16) */
    public static final S62Agency MEXICO_SECRETARIA_DE_MARINA_ARMADA_DE_MEXICO_DIRECCION_GENERAL_ADJUNTA_DE_OCEANOGRAFIA_HIDROGRAFIA_Y_METEOROLOGIA = new S62Agency("MX", 1180);
    /** Monaco, Direction des Affaires Maritimes (2008-10-16) */
    public static final S62Agency MONACO_DIRECTION_DES_AFFAIRES_MARITIMES = new S62Agency("MC", 300);
    /** Morocco , Division Hydrographie et Cartographie (DHC) de la Marine Royale  (2008-10-16) */
    public static final S62Agency MOROCCO_DHC = new S62Agency("MA", 1200);
    /** Mozambique, Instituto Nacional de Hidrografia e Navegação (INAHINA) (2008-10-16) */
    public static final S62Agency MOZAMBIQUE_INAHINA = new S62Agency("MZ", 1210);
    /** Myanmar , Central Naval Hydrographic Depot (CNHD) (2008-10-16) */
    public static final S62Agency MYANMAR_CNHD = new S62Agency("MM", 1220);
    /** Netherlands, Koninklijke Marine Dienst der Hydrografie / CZSK (2008-10-16) */
    public static final S62Agency NETHERLANDS_KONINKLIJKE_MARINE_DIENST_DER_HYDROGRAFIE = new S62Agency("NL", 310);
    /** New Zealand, Land Information New Zealand (LINZ) (2008-10-16) */
    public static final S62Agency NEW_ZEALAND_LINZ = new S62Agency("NZ", 320);
    /** New Zealand, New Zealand Defence force (NZDF) Geospatial Intelligence Organisation (GIO) (2010-08-12) */
    public static final S62Agency NEW_ZEALAND_NZDF = new S62Agency("N3", 321);
    /** Nigeria, Nigerian Navy Hydrographic Office (2008-10-16) */
    public static final S62Agency NIGERIA_NIGERIAN_NAVY_HYDROGRAPHIC_OFFICE = new S62Agency("NG", 330);
    /** Norway, Norwegian Hydrographic Service (2008-10-16) */
    public static final S62Agency NORWAY_NORWEGIAN_HYDROGRAPHIC_SERVICE = new S62Agency("NO", 340);
    /** Norway, Electronic Chart Centre (2008-10-16) */
    public static final S62Agency NORWAY_ELECTRONIC_CHART_CENTRE = new S62Agency("N1", 341);
    /** Norway, Norwegian Defence (2008-10-16) */
    public static final S62Agency NORWAY_NORWEGIAN_DEFENCE = new S62Agency("N2", 342);
    /** Oman, National Hydrographic Office (2008-10-16) */
    public static final S62Agency OMAN_NATIONAL_HYDROGRAPHIC_OFFICE = new S62Agency("OM", 350);
    /** Pakistan, Pakistan Hydrographic Department (2008-10-16) */
    public static final S62Agency PAKISTAN_PAKISTAN_HYDROGRAPHIC_DEPARTMENT = new S62Agency("PK", 360);
    /** Papua New Guinea, Hydrographic Division, National Maritime Safety Division (NMSA) (2008-10-16) */
    public static final S62Agency PAPUA_NEW_GUINEA_NMSA = new S62Agency("PG", 370);
    /** Peru, Dirección de Hidrografía y Navegación (DHN) (2008-10-16) */
    public static final S62Agency PERU_DHN = new S62Agency("PE", 380);
    /** Philippines, National Mapping and Resource Information Authority, Coast & Geodetic Survey Department (2008-10-16) */
    public static final S62Agency PHILIPPINES_NATIONAL_MAPPING_AND_RESOURCE_INFORMATION_AUTHORITY_COAST_AND_GEODETIC_SURVEY_DEPARTMENT = new S62Agency("PH", 390);
    /** Poland, Biuro Hydrograficzne  (2008-10-16) */
    public static final S62Agency POLAND_BIURO_HYDROGRAFICZNE_ = new S62Agency("PL", 400);
    /** Portugal, Instituto Hidrografico, Portugal (IHP) (2008-10-16) */
    public static final S62Agency PORTUGAL_IHP = new S62Agency("PT", 410);
    /** Qatar, Urban Planning & Development Authority, Hydrographic Section (2008-10-16) */
    public static final S62Agency QATAR_URBAN_PLANNING_AND_DEVELOPMENT_AUTHORITY_HYDROGRAPHIC_SECTION = new S62Agency("QA", 1290);
    /** Romania , Directia Hidrografica Maritima (2008-10-16) */
    public static final S62Agency ROMANIA_DIRECTIA_HIDROGRAFICA_MARITIMA = new S62Agency("RO", 1300);
    /** Russian Federation, Head Department of Navigation & Oceanography (DNO) (2008-10-16) */
    public static final S62Agency RUSSIAN_FEDERATION_DNO = new S62Agency("RU", 420);
    /** Russian Federation, Federal State Unitary Hydrographc Department (2011-11-16) */
    public static final S62Agency RUSSIAN_FEDERATION_FEDERAL_STATE_UNITARY_HYDROGRAPHC_DEPARTMENT = new S62Agency("R1", 425);
    /** Saudi Arabia, General Directorate of Military Survey (GDMS) (2008-10-16) */
    public static final S62Agency SAUDI_ARABIA_GDMS = new S62Agency("SA", 1360);
    /** Saudi Arabia, General Commission for Survey (GCS) (2011-11-23) */
    public static final S62Agency SAUDI_ARABIA_GCS = new S62Agency("S1", 1365);
    /** Serbia , Direkcija Za Unutrašnje Plovne Puteve (2008-10-16) */
    public static final S62Agency SERBIA_DIREKCIJA_ZA_UNUTRAŠNJE_PLOVNE_PUTEVE = new S62Agency("RS", 580);
    /** Singapore, Hydrographic Department, Maritime and Port Authority (MPA) (2008-10-16) */
    public static final S62Agency SINGAPORE_MPA = new S62Agency("SG", 430);
    /** Slovenia , Ministry of Transport Maritime Office (2008-10-16) */
    public static final S62Agency SLOVENIA_MINISTRY_OF_TRANSPORT_MARITIME_OFFICE = new S62Agency("SI", 1400);
    /** South Africa (Rep. of), South African Navy Hydrographic Office (SANHO) (2008-10-16) */
    public static final S62Agency REP_SOUTH_AFRICA_SANHO = new S62Agency("ZA", 440);
    /** Spain, Instituto Hidrográfico de la Marina (IHM) (2008-10-16) */
    public static final S62Agency SPAIN_IHM = new S62Agency("ES", 450);
    /** Sri Lanka, National Hydrographic Office, National Aquatic Resources Research and Development Agency (NARA) (2008-10-16) */
    public static final S62Agency SRI_LANKA_NARA = new S62Agency("LK", 460);
    /** Suriname, Maritieme Autoriteit Suriname (MAS) (2008-10-16) */
    public static final S62Agency SURINAME_MAS = new S62Agency("SR", 470);
    /** Sweden, Sjöfartsverket, Swedish Maritime Administration (2008-10-16) */
    public static final S62Agency SWEDEN_SJÖFARTSVERKET_SWEDISH_MARITIME_ADMINISTRATION = new S62Agency("SE", 480);
    /** Syrian Arab Republic, General Directorate of Ports (2008-10-16) */
    public static final S62Agency SYRIAN_ARAB_REPUBLIC_GENERAL_DIRECTORATE_OF_PORTS = new S62Agency("SY", 490);
    /** Thailand, Hydrographic Department, Royal Thai Navy (2008-10-16) */
    public static final S62Agency THAILAND_HYDROGRAPHIC_DEPARTMENT_ROYAL_THAI_NAVY = new S62Agency("TH", 500);
    /** Tonga, Tonga Defence Services (2008-10-16) */
    public static final S62Agency TONGA_TONGA_DEFENCE_SERVICES = new S62Agency("TO", 505);
    /** Trinidad & Tobago, Trinidad & Tobago Hydrographic Unit (2008-10-16) */
    public static final S62Agency TRINIDAD_AND_TOBAGO_TRINIDAD_AND_TOBAGO_HYDROGRAPHIC_UNIT = new S62Agency("TT", 510);
    /** Tunisia, Service Hydrographique et Océanographique (SHO), Armée de Mer  (2008-10-16) */
    public static final S62Agency TUNISIA_SHO = new S62Agency("TN", 1470);
    /** Turkey, Seyir, Hidrografi ve Osinografi Dairesi Baskanligi, Office of Navigation, Hydrography and Oceanography (2008-10-16) */
    public static final S62Agency TURKEY_SEYIR_HIDROGRAFI_VE_OSINOGRAFI_DAIRESI_BASKANLIGI_OFFICE_OF_NAVIGATION_HYDROGRAPHY_AND_OCEANOGRAPHY = new S62Agency("TR", 520);
    /** UK, United Kingdom Hydrographic Office (2008-10-16) */
    public static final S62Agency UK_UNITED_KINGDOM_HYDROGRAPHIC_OFFICE = new S62Agency("GB", 540);
    /** Ukraine, State Hydrographic Service of Ukraine (2008-10-16) */
    public static final S62Agency UKRAINE_STATE_HYDROGRAPHIC_SERVICE_OF_UKRAINE = new S62Agency("UA", 1490);
    /** United Arab Emirates, Ministry of Communications (2008-10-16) */
    public static final S62Agency UNITED_ARAB_EMIRATES_MINISTRY_OF_COMMUNICATIONS = new S62Agency("AE", 530);
    /** Uruguay, Servicio de Oceanografía, Hidrografía y Meteorología de la Armada (SOHMA) (2008-10-16) */
    public static final S62Agency URUGUAY_SOHMA = new S62Agency("UY", 560);
    /** USA , Office of Coast Survey, National Ocean Service, National Oceanic and Atmospheric Administration (NOS) (2008-10-16) */
    public static final S62Agency USA_NOS = new S62Agency("US", 550);
    /** USA ,  National Geospatial-Intelligence Agency Department of Defense (NGA) (2008-10-16) */
    public static final S62Agency USA_NGA = new S62Agency("U1", 551);
    /** USA ,  Commander, Naval Meteorology and Oceanography Command (CNMOC) (2008-10-16) */
    public static final S62Agency USA_CNMOC = new S62Agency("U2", 552);
    /** USA , U.S. Army Corps of Engineers (USACE) (2008-10-16) */
    public static final S62Agency USA_USACE = new S62Agency("U3", 553);
    /** Venezuela, Commandancia General de la Armada, Dirección de Hidrografía y Navegación (DHN) (2008-10-16) */
    public static final S62Agency VENEZUELA_DHN = new S62Agency("VE", 570);
    ////////////////////////////////////////////////////////////////////////
    // MAIN PRODUCERS : OTHER STATES ///////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    /** , International Hydrographic Organization (IHO) (2008-10-16) */
    public static final S62Agency _IHO = new S62Agency("AA", 1810);
    /** , Co-operating Hydrographic Offices in the Malacca and Singapore Straits (Indonesia, Japan, Malaysia and Singapore) (2008-10-16) */
    public static final S62Agency _INDONESIA_JAPAN_MALAYSIA_AND_SINGAPORE = new S62Agency("MS", 2010);
    /** , East Asia Hydrographic Commission (EAHC) (2008-10-16) */
    public static final S62Agency _EAHC = new S62Agency("EA", 2040);
    /** Albania, Albanian Hydrographic Service (2008-10-16) */
    public static final S62Agency ALBANIA_ALBANIAN_HYDROGRAPHIC_SERVICE = new S62Agency("AL", 600);
    /** Angola, Not known (2008-10-16) */
    public static final S62Agency ANGOLA_NOT_KNOWN = new S62Agency("AO", 620);
    /** Anguilla, Ministry of Infrastructure, Communications & Utilities (2008-10-16) */
    public static final S62Agency ANGUILLA_MINISTRY_OF_INFRASTRUCTURE_COMMUNICATIONS_AND_UTILITIES = new S62Agency("AI", 625);
    /** Antigua and Barbuda, Antigua and Barbuda Port Authority (2008-10-16) */
    public static final S62Agency ANTIGUA_AND_BARBUDA_ANTIGUA_AND_BARBUDA_PORT_AUTHORITY = new S62Agency("AG", 630);
    /** Aruba, Netherlands ENC charting responsibility (2008-10-16) */
    public static final S62Agency ARUBA_NETHERLANDS_ENC_CHARTING_RESPONSIBILITY = new S62Agency("AW", 640);
    /** Azerbaijan, Azerbaijan Navy (2008-10-16) */
    public static final S62Agency AZERBAIJAN_AZERBAIJAN_NAVY = new S62Agency("AZ", 645);
    /** Bahamas,  Port Department, Ministry of Transport and Aviation (2008-10-16) */
    public static final S62Agency BAHAMAS_PORT_DEPARTMENT_MINISTRY_OF_TRANSPORT_AND_AVIATION = new S62Agency("BS", 650);
    /** Barbados, Barbados Port Inc (2008-10-16) */
    public static final S62Agency BARBADOS_BARBADOS_PORT_INC = new S62Agency("BB", 670);
    /** Belize, Belize Port Authority (2008-10-16) */
    public static final S62Agency BELIZE_BELIZE_PORT_AUTHORITY = new S62Agency("BZ", 680);
    /** Benin, Direction Générale du Port Autonome de Cotonou (2008-10-16) */
    public static final S62Agency BENIN_DIRECTION_GÉNÉRALE_DU_PORT_AUTONOME_DE_COTONOU = new S62Agency("BJ", 690);
    /** Bermuda, Ministry of Works, Engineering and Housing (2008-10-16) */
    public static final S62Agency BERMUDA_MINISTRY_OF_WORKS_ENGINEERING_AND_HOUSING = new S62Agency("BM", 695);
    /** Bolivia, Servicio Nacional de Hidrografia Naval de Bolivia (2008-10-16) */
    public static final S62Agency BOLIVIA_SERVICIO_NACIONAL_DE_HIDROGRAFIA_NAVAL_DE_BOLIVIA = new S62Agency("BO", 700);
    /** British Virgin Islands, Chief Minister’s Office (2008-10-16) */
    public static final S62Agency BRITISH_VIRGIN_ISLANDS_CHIEF_MINISTERS_OFFICE = new S62Agency("VG", 705);
    /** Brunei Darussalam, Department of Marine (2008-10-16) */
    public static final S62Agency BRUNEI_DARUSSALAM_DEPARTMENT_OF_MARINE = new S62Agency("BN", 710);
    /** Brunei Darussalam, Survey Department (2008-10-16) */
    public static final S62Agency BRUNEI_DARUSSALAM_SURVEY_DEPARTMENT = new S62Agency("B2", 715);
    /** Bulgaria , Hidrografska Sluzhba Pri Ministerstvo Na Otbranata (2008-10-16) */
    public static final S62Agency BULGARIA_HIDROGRAFSKA_SLUZHBA_PRI_MINISTERSTVO_NA_OTBRANATA = new S62Agency("BG", 720);
    /** Cambodia, Service de l’Hydrologie et des grands barrages (2008-10-16) */
    public static final S62Agency CAMBODIA_SERVICE_DE_LHYDROLOGIE_ET_DES_GRANDS_BARRAGES = new S62Agency("KH", 730);
    /** Cape Verde, Instituto Marityimo e Portuário (IMP) (2008-10-16) */
    public static final S62Agency CAPE_VERDE_IMP = new S62Agency("CV", 750);
    /** Comoros, Not Known (2008-10-16) */
    public static final S62Agency COMOROS_NOT_KNOWN = new S62Agency("KM", 770);
    /** Congo (Rep. of), Port Autonome de Pointe Noire (2008-10-16) */
    public static final S62Agency CONGO_PORT_AUTONOME_DE_POINTE_NOIRE = new S62Agency("CG", 780);
    /** Cook Islands, Maritime Division, Ministry of Tourism and Transport (2008-10-16) */
    public static final S62Agency COOK_ISLANDS_MARITIME_DIVISION_MINISTRY_OF_TOURISM_AND_TRANSPORT = new S62Agency("CK", 790);
    /** Costa-Rica, Instituto Geografico Nacional (IGN) (2008-10-16) */
    public static final S62Agency COSTA_RICA_IGN = new S62Agency("CR", 800);
    /** Côte d’Ivoire, Direction Générale du Port Autonome d’Abidjan (2008-10-16) */
    public static final S62Agency CÔTE_DIVOIRE_DIRECTION_GÉNÉRALE_DU_PORT_AUTONOME_DABIDJAN = new S62Agency("CI", 810);
    /** Djibouti, Ministère de l’Equipement et des Transports, Direction des Affaires Maritimes  (2008-10-16) */
    public static final S62Agency DJIBOUTI_MINISTÈRE_DE_LEQUIPEMENT_ET_DES_TRANSPORTS_DIRECTION_DES_AFFAIRES_MARITIMES_ = new S62Agency("DJ", 820);
    /** Dominica, Not known (2008-10-16) */
    public static final S62Agency DOMINICA_NOT_KNOWN = new S62Agency("DM", 830);
    /** El Salvador, Gerente de Geodesia, Centro Nacional de Registros, Instituto Geografico y del Catastro Nacional (2008-10-16) */
    public static final S62Agency EL_SALVADOR_GERENTE_DE_GEODESIA_CENTRO_NACIONAL_DE_REGISTROS_INSTITUTO_GEOGRAFICO_Y_DEL_CATASTRO_NACIONAL = new S62Agency("SV", 840);
    /** Equatorial Guinea, Ministry of Transportation and Civil Aviation (2008-10-16) */
    public static final S62Agency EQUATORIAL_GUINEA_MINISTRY_OF_TRANSPORTATION_AND_CIVIL_AVIATION = new S62Agency("GQ", 850);
    /** Eritrea, Department of Marine Transport (2008-10-16) */
    public static final S62Agency ERITREA_DEPARTMENT_OF_MARINE_TRANSPORT = new S62Agency("ER", 860);
    /** Ethiopia, Ministry of Transport and Communications Marine Transport Authority (2008-10-16) */
    public static final S62Agency ETHIOPIA_MINISTRY_OF_TRANSPORT_AND_COMMUNICATIONS_MARINE_TRANSPORT_AUTHORITY = new S62Agency("ET", 880);
    /** Gabon, Direction Générale de la Marine Marchande (2008-10-16) */
    public static final S62Agency GABON_DIRECTION_GÉNÉRALE_DE_LA_MARINE_MARCHANDE = new S62Agency("GA", 890);
    /** Gambia, Gambia Ports Authority (2008-10-16) */
    public static final S62Agency GAMBIA_GAMBIA_PORTS_AUTHORITY = new S62Agency("GM", 900);
    /** Georgia, State Hydrographic Service of Georgia (2008-10-16) */
    public static final S62Agency GEORGIA_STATE_HYDROGRAPHIC_SERVICE_OF_GEORGIA = new S62Agency("GE", 905);
    /** Ghana, Ghana Ports and Harbours Authority (2008-10-16) */
    public static final S62Agency GHANA_GHANA_PORTS_AND_HARBOURS_AUTHORITY = new S62Agency("GH", 910);
    /** Grenada, Grenada Ports Authority (2008-10-16) */
    public static final S62Agency GRENADA_GRENADA_PORTS_AUTHORITY = new S62Agency("GD", 920);
    /** Guinea, Port Autonome de Conakry (2008-10-16) */
    public static final S62Agency GUINEA_PORT_AUTONOME_DE_CONAKRY = new S62Agency("GN", 930);
    /** Guinea-Bissau, Administração dos Portos da Guiné-Bissau (2008-10-16) */
    public static final S62Agency GUINEA_BISSAU_ADMINISTRAÇÃO_DOS_PORTOS_DA_GUINÉ_BISSAU = new S62Agency("GW", 940);
    /** Guyana, Maritime Administration Department Hydrographic Office (2008-10-16) */
    public static final S62Agency GUYANA_MARITIME_ADMINISTRATION_DEPARTMENT_HYDROGRAPHIC_OFFICE = new S62Agency("GY", 950);
    /** Haiti, Service Maritime et de Navigation d’Haïti (2008-10-16) */
    public static final S62Agency HAITI_SERVICE_MARITIME_ET_DE_NAVIGATION_DHAÏTI = new S62Agency("HT", 960);
    /** Honduras, Empresa Nacional Portuaria (2008-10-16) */
    public static final S62Agency HONDURAS_EMPRESA_NACIONAL_PORTUARIA = new S62Agency("HN", 970);
    /** Iraq, Marine Department, General Company for Iraki Ports (2008-10-16) */
    public static final S62Agency IRAQ_MARINE_DEPARTMENT_GENERAL_COMPANY_FOR_IRAKI_PORTS = new S62Agency("IQ", 980);
    /** Israel, Administration of Shipping and Ports (2008-10-16) */
    public static final S62Agency ISRAEL_ADMINISTRATION_OF_SHIPPING_AND_PORTS = new S62Agency("IL", 1000);
    /** Israel, Survey of Israel (2008-10-16) */
    public static final S62Agency ISRAEL_SURVEY_OF_ISRAEL = new S62Agency("I1", 1001);
    /** Israel, Israel Navy (2008-10-16) */
    public static final S62Agency ISRAEL_ISRAEL_NAVY = new S62Agency("I2", 1002);
    /** Jordan, The Ports Corporation, Jordan (2008-10-16) */
    public static final S62Agency JORDAN_THE_PORTS_CORPORATION_JORDAN = new S62Agency("JO", 1020);
    /** Kenya, Survey of Kenya, Kenya Ports Authority (2008-10-16) */
    public static final S62Agency KENYA_SURVEY_OF_KENYA_KENYA_PORTS_AUTHORITY = new S62Agency("KE", 1030);
    /** Kiribati, Ministry of Transport and Communications (2008-10-16) */
    public static final S62Agency KIRIBATI_MINISTRY_OF_TRANSPORT_AND_COMMUNICATIONS = new S62Agency("KI", 1040);
    /** Lebanon, Ministry of Public Works & Transport (2008-10-16) */
    public static final S62Agency LEBANON_MINISTRY_OF_PUBLIC_WORKS_AND_TRANSPORT = new S62Agency("LB", 1070);
    /** Liberia, Ministry of Lands, Mines and Energy (2008-10-16) */
    public static final S62Agency LIBERIA_MINISTRY_OF_LANDS_MINES_AND_ENERGY = new S62Agency("LR", 1080);
    /** Libyan Arab Jamahiriya, Not known (2008-10-16) */
    public static final S62Agency LIBYAN_ARAB_JAMAHIRIYA_NOT_KNOWN = new S62Agency("LY", 1090);
    /** Lithuania, Lithuanian Maritime Safety Administration (2008-10-16) */
    public static final S62Agency LITHUANIA_LITHUANIAN_MARITIME_SAFETY_ADMINISTRATION = new S62Agency("LT", 1100);
    /** Madagascar, Institut Géographique et Hydrographique National (2008-10-16) */
    public static final S62Agency MADAGASCAR_INSTITUT_GÉOGRAPHIQUE_ET_HYDROGRAPHIQUE_NATIONAL = new S62Agency("MG", 1110);
    /** Malawi, Hydrographic Survey Unit (2008-10-16) */
    public static final S62Agency MALAWI_HYDROGRAPHIC_SURVEY_UNIT = new S62Agency("MW", 1120);
    /** Malawi, Marine Department (2008-10-16) */
    public static final S62Agency MALAWI_MARINE_DEPARTMENT = new S62Agency("M2", 1121);
    /** Maldives, Department of Information and Broadcasting (2008-10-16) */
    public static final S62Agency MALDIVES_DEPARTMENT_OF_INFORMATION_AND_BROADCASTING = new S62Agency("MV", 1130);
    /** Malta, Malta Maritime Authority Ports Directorate, Hydrographic Unit (2008-10-16) */
    public static final S62Agency MALTA_MALTA_MARITIME_AUTHORITY_PORTS_DIRECTORATE_HYDROGRAPHIC_UNIT = new S62Agency("MT", 1140);
    /** Marshall Islands, Ministry of Resources and Development (2008-10-16) */
    public static final S62Agency MARSHALL_ISLANDS_MINISTRY_OF_RESOURCES_AND_DEVELOPMENT = new S62Agency("MH", 1150);
    /** Mauritania , Ministère de la Défense Nationale (2008-10-16) */
    public static final S62Agency MAURITANIA_MINISTÈRE_DE_LA_DÉFENSE_NATIONALE = new S62Agency("MR", 1160);
    /** Micronesia (Federated States of), Not known (2008-10-16) */
    public static final S62Agency MICRONESIA_NOT_KNOWN = new S62Agency("FM", 1190);
    /** Montenegro, Ministry of Defence, Navy Headquarters (2008-10-16) */
    public static final S62Agency MONTENEGRO_MINISTRY_OF_DEFENCE_NAVY_HEADQUARTERS = new S62Agency("ME", 1225);
    /** Montserrat, Montserrat Port Authority (2008-10-16) */
    public static final S62Agency MONTSERRAT_MONTSERRAT_PORT_AUTHORITY = new S62Agency("M3", 1197);
    /** Namibia, Ministry of Works, Transports and Communications (2008-10-16) */
    public static final S62Agency NAMIBIA_MINISTRY_OF_WORKS_TRANSPORTS_AND_COMMUNICATIONS = new S62Agency("NA", 1230);
    /** Nauru, Nauru Phosphate Corporation (2008-10-16) */
    public static final S62Agency NAURU_NAURU_PHOSPHATE_CORPORATION = new S62Agency("NR", 1240);
    /** Nicaragua, Ministero de la Presidencia, Instituto Nicaragüense de Estudios Territoriales, Dirección de Recursos Hídricos, Departamento de Hidrografía (2008-10-16) */
    public static final S62Agency NICARAGUA_MINISTERO_DE_LA_PRESIDENCIA_INSTITUTO_NICARAGÜENSE_DE_ESTUDIOS_TERRITORIALES_DIRECCIÓN_DE_RECURSOS_HÍDRICOS_DEPARTAMENTO_DE_HIDROGRAFÍA = new S62Agency("NI", 1250);
    /** Niue , Lands and Survey Division (2008-10-16) */
    public static final S62Agency NIUE_LANDS_AND_SURVEY_DIVISION = new S62Agency("NU", 1255);
    /** Palau, Bureau of Domestic Affairs (2008-10-16) */
    public static final S62Agency PALAU_BUREAU_OF_DOMESTIC_AFFAIRS = new S62Agency("PW", 1260);
    /** Panama, Autoridad Maritima de Panama (2008-10-16) */
    public static final S62Agency PANAMA_AUTORIDAD_MARITIMA_DE_PANAMA = new S62Agency("PA", 1270);
    /** Paraguay, Fuerzas Armadas de la Nacion, Armada Paraguaya, Comando de apoyo de combate (2008-10-16) */
    public static final S62Agency PARAGUAY_FUERZAS_ARMADAS_DE_LA_NACION_ARMADA_PARAGUAYA_COMANDO_DE_APOYO_DE_COMBATE = new S62Agency("PY", 1280);
    /** Saint Kitts and Nevis, St. Christopher Air and Sea Ports Authority, Maritime Division (2008-10-16) */
    public static final S62Agency SAINT_KITTS_AND_NEVIS_AIR_AND_SEA_PORTS_AUTHORITY_MARITIME_DIVISION = new S62Agency("KN", 1310);
    /** Saint Lucia, Saint Lucia Air and Sea Ports Authority, Division of Maritime Affairs (2008-10-16) */
    public static final S62Agency SAINT_LUCIA_SAINT_LUCIA_AIR_AND_SEA_PORTS_AUTHORITY_DIVISION_OF_MARITIME_AFFAIRS = new S62Agency("LC", 1320);
    /** Saint Vincent and the Grenadines, Ministry of Communications and Works (2008-10-16) */
    public static final S62Agency SAINT_VINCENT_AND_THE_GRENADINES_MINISTRY_OF_COMMUNICATIONS_AND_WORKS = new S62Agency("VC", 1330);
    /** Samoa, Ministry of Transport, Marine and Shipping Division (2008-10-16) */
    public static final S62Agency SAMOA_MINISTRY_OF_TRANSPORT_MARINE_AND_SHIPPING_DIVISION = new S62Agency("WS", 1340);
    /** Sao Tome and Principe, Not known (2008-10-16) */
    public static final S62Agency SAO_TOME_AND_PRINCIPE_NOT_KNOWN = new S62Agency("ST", 1350);
    /** Senegal, Service de sécurité maritime du Sénégal, Port autonome de Dakar (2008-10-16) */
    public static final S62Agency SENEGAL_SERVICE_DE_SÉCURITÉ_MARITIME_DU_SÉNÉGAL_PORT_AUTONOME_DE_DAKAR = new S62Agency("SN", 1370);
    /** Seychelles, Hydrographic and Topographic Brigade of the Seychelles (2008-10-16) */
    public static final S62Agency SEYCHELLES_HYDROGRAPHIC_AND_TOPOGRAPHIC_BRIGADE_OF_THE_SEYCHELLES = new S62Agency("SC", 1380);
    /** Sierra Leone, Sierra Leone Maritime Administration, Sierra Leone Ports Authority (2008-10-16) */
    public static final S62Agency SIERRA_LEONE_SIERRA_LEONE_MARITIME_ADMINISTRATION_SIERRA_LEONE_PORTS_AUTHORITY = new S62Agency("SL", 1390);
    /** Solomon Islands, Solomon Islands Hydrographic Office (SIHO) (2008-10-16) */
    public static final S62Agency SOLOMON_ISLANDS_SIHO = new S62Agency("SB", 1410);
    /** Somalia, Somali Hydrographic Office (2008-10-16) */
    public static final S62Agency SOMALIA_SOMALI_HYDROGRAPHIC_OFFICE = new S62Agency("SO", 1420);
    /** Sudan, Survey Department (2008-10-16) */
    public static final S62Agency SUDAN_SURVEY_DEPARTMENT = new S62Agency("SD", 1430);
    /** Tanzania, Hydrographic Surveys Section, Surveys and Mapping Division, Ministry of Lands, Housing and Human Settlements Development  (2008-10-16) */
    public static final S62Agency TANZANIA_HYDROGRAPHIC_SURVEYS_SECTION_SURVEYS_AND_MAPPING_DIVISION_MINISTRY_OF_LANDS_HOUSING_AND_HUMAN_SETTLEMENTS_DEVELOPMENT_ = new S62Agency("TZ", 1440);
    /** Tanzania, Tanzania Ports Authority (TPA) (2008-10-16) */
    public static final S62Agency TANZANIA_TPA = new S62Agency("T1", 1441);
    /** The Cayman Islands, Governor’s Office (2008-10-16) */
    public static final S62Agency THE_CAYMAN_ISLANDS_GOVERNORS_OFFICE = new S62Agency("KY", 755);
    /** Togo, University of Benin, Research Department (2008-10-16) */
    public static final S62Agency TOGO_UNIVERSITY_OF_BENIN_RESEARCH_DEPARTMENT = new S62Agency("TG", 1450);
    /** Tokelau, Not known (2008-10-16) */
    public static final S62Agency TOKELAU_NOT_KNOWN = new S62Agency("TK", 1460);
    /** Turks & Caicos Islands, Governor’s Office (2008-10-16) */
    public static final S62Agency TURKS_AND_CAICOS_ISLANDS_GOVERNORS_OFFICE = new S62Agency("TC", 1475);
    /** Tuvalu, Ministry of Labour, Works and Communications (2008-10-16) */
    public static final S62Agency TUVALU_MINISTRY_OF_LABOUR_WORKS_AND_COMMUNICATIONS = new S62Agency("TV", 1480);
    /** Uganda, Commissioner for Transport Regulation (2008-10-16) */
    public static final S62Agency UGANDA_COMMISSIONER_FOR_TRANSPORT_REGULATION = new S62Agency("UG", 1485);
    /** Vanuatu, Vanuatu Hydrographic Unit (2008-10-16) */
    public static final S62Agency VANUATU_VANUATU_HYDROGRAPHIC_UNIT = new S62Agency("VU", 1500);
    /** Viet Nam, Viet Nam Maritime Safety Agency (VMSA-1) (2008-10-16) */
    public static final S62Agency VIET_NAM_VMSA_1 = new S62Agency("VN", 1510);
    /** Viet Nam, Viet Nam Maritime Safety Agency (VMSA-2) (2008-10-16) */
    public static final S62Agency VIET_NAM_VMSA_2 = new S62Agency("V1", 1511);
    /** Yemen, Ministry of Transport, Yemen Ports Authority, Maritime Affairs Authority (2008-10-16) */
    public static final S62Agency YEMEN_MINISTRY_OF_TRANSPORT_YEMEN_PORTS_AUTHORITY_MARITIME_AFFAIRS_AUTHORITY = new S62Agency("YE", 1520);
    ////////////////////////////////////////////////////////////////////////
    // SUPPLEMENTARY PRODUCERS /////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    /** A.F.D.J. R.A. Galati (2008-10-16) */
    public static final S62Agency AFDJ_RA_GALATI = new S62Agency("3R", 16203);
    /** Abris, Llc (2011-10-20) */
    public static final S62Agency ABRIS_LLC = new S62Agency("8T", 36363);
    /** ADVETO Advanced Technology AB (2008-10-16) */
    public static final S62Agency ADVETO_ADVANCED_TECHNOLOGY_AB = new S62Agency("4S", 20316);
    /** AEMDR, Rousse, Bulgaria (2008-10-16) */
    public static final S62Agency AEMDR_ROUSSE_BULGARIA = new S62Agency("3B", 15163);
    /** Aero Karta Complex Ltd (2011-10-18) */
    public static final S62Agency AERO_KARTA_COMPLEX_LTD = new S62Agency("3Z", 16038);
    /** AMEC (2008-10-16) */
    public static final S62Agency AMEC = new S62Agency("6A", 27242);
    /** American Commercial Lines (ACL), Inc. (2008-10-16) */
    public static final S62Agency ACL = new S62Agency("5A", 23130);
    /** Amt fuer Geoinformationswesen der Bundeswehr (2008-10-16) */
    public static final S62Agency AMT_FUER_GEOINFORMATIONSWESEN_DER_BUNDESWEHR = new S62Agency("1D", 7453);
    /** Antarctic Treaty Consultative Committee (2008-10-16) */
    public static final S62Agency ANTARCTIC_TREATY_CONSULTATIVE_COMMITTEE = new S62Agency("QM", 1600);
    /** ARAMCO (2008-10-16) */
    public static final S62Agency ARAMCO = new S62Agency("1A", 6682);
    /** Arctic and Antarctic Research Institute (AARI) of the Russian Federal Service for Hydrometeorology and Environmental Monitoring (Roshydromet) (2008-10-16) */
    public static final S62Agency AARI = new S62Agency("4A", 19018);
    /** Austrian Supreme Shipping Authority (2008-10-16) */
    public static final S62Agency AUSTRIAN_SUPREME_SHIPPING_AUTHORITY = new S62Agency("1S", 7980);
    /** Azerbaijan Navy, Service of Navigation and Oceanography  (2008-10-16) */
    public static final S62Agency AZERBAIJAN_NAVY_SERVICE_OF_NAVIGATION_AND_OCEANOGRAPHY_ = new S62Agency("7A", 31354);
    /** Azienda Regionale Navigazione Interna (ARNI) (2008-10-16) */
    public static final S62Agency ARNI = new S62Agency("2A", 10794);
    /** Azovo-Donskoe State Basin Waterway and Shipping Authority (2008-10-16) */
    public static final S62Agency AZOVO_DONSKOE_STATE_BASIN_WATERWAY_AND_SHIPPING_AUTHORITY = new S62Agency("3A", 14906);
    /** BaikalChart, Russia (2008-10-16) */
    public static final S62Agency BAIKALCHART_RUSSIA = new S62Agency("4B", 19275);
    /** BMT ARGOSS Ltd (2011-03-21) */
    public static final S62Agency BMT_ARGOSS_LTD = new S62Agency("0M", 3817);
    /** Bundesanstalt für Wasserbau, Karlsruhe (2008-10-16) */
    public static final S62Agency BUNDESANSTALT_FÜR_WASSERBAU_KARLSRUHE = new S62Agency("2B", 11051);
    /** C-Map Russia (2008-10-16) */
    public static final S62Agency C_MAP_RUSSIA = new S62Agency("4Z", 20323);
    /** C-Tech SRL, Romania (2008-10-16) */
    public static final S62Agency C_TECH_SRL_ROMANIA = new S62Agency("6B", 27499);
    /** Canadian Coast Guard (2008-10-16) */
    public static final S62Agency CANADIAN_COAST_GUARD = new S62Agency("1G", 7968);
    /** Canadian Ice Service (2008-10-16) */
    public static final S62Agency CANADIAN_ICE_SERVICE = new S62Agency("4I", 20306);
    /** CARIS (2008-10-16) */
    public static final S62Agency CARIS = new S62Agency("1C", 7196);
    /** Center for Coastal & Ocean Mapping/Joint Hydrographic Center, University of New Hampshire (2008-10-16) */
    public static final S62Agency CENTER_FOR_COASTAL_AND_OCEAN_MAPPING_JOINT_HYDROGRAPHIC_CENTER_UNIVERSITY_OF_NEW_HAMPSHIRE = new S62Agency("4U", 20318);
    /** Centre Sevzapgeoinform (SZGI) (2008-10-16) */
    public static final S62Agency SZGI = new S62Agency("7S", 32652);
    /** Channel of Moscow (2008-10-16) */
    public static final S62Agency CHANNEL_OF_MOSCOW = new S62Agency("1M", 7974);
    /** Chart Pilot Ltd. (2011-10-17) */
    public static final S62Agency CHART_PILOT_LTD = new S62Agency("2G", 11973);
    /** ChartCo Limited (2011-10-05) */
    public static final S62Agency CHARTCO_LIMITED = new S62Agency("7J", 32298);
    /** Chartworld Gmbh (2008-10-16) */
    public static final S62Agency CHARTWORLD_GMBH = new S62Agency("9C", 40092);
    /** CherSoft Ltd (2008-10-16) */
    public static final S62Agency CHERSOFT_LTD = new S62Agency("9A", 39578);
    /** Comite International Radio-Maritime (2008-10-16) */
    public static final S62Agency COMITE_INTERNATIONAL_RADIO_MARITIME = new S62Agency("QO", 1620);
    /** Command & Control Technologies GmbH (2008-10-16) */
    public static final S62Agency COMMAND_AND_CONTROL_TECHNOLOGIES_GMBH = new S62Agency("3C", 15420);
    /** CRUP d.o.o., Croatia (2008-10-16) */
    public static final S62Agency CRUP_DOO_CROATIA = new S62Agency("5C", 23644);
    /** Development Centre for Ship Technology and Transport Systems, Germany (2008-10-16) */
    public static final S62Agency DEVELOPMENT_CENTRE_FOR_SHIP_TECHNOLOGY_AND_TRANSPORT_SYSTEMS_GERMANY = new S62Agency("3E", 15934);
    /** Digital Geographic Information Working Group (2008-10-16) */
    public static final S62Agency DIGITAL_GEOGRAPHIC_INFORMATION_WORKING_GROUP = new S62Agency("QQ", 1640);
    /** DMER, Zagreb (2008-10-16) */
    public static final S62Agency DMER_ZAGREB = new S62Agency("2H", 12055);
    /** drait (2013-01-16) */
    public static final S62Agency DRAIT = new S62Agency("0A", 3860);
    /** e-MLX, Korea (2008-10-16) */
    public static final S62Agency E_MLX_KOREA = new S62Agency("2X", 12097);
    /** ENC Center, National Taiwan Ocean University (2008-10-16) */
    public static final S62Agency ENC_CENTER_NATIONAL_TAIWAN_OCEAN_UNIVERSITY = new S62Agency("1U", 7982);
    /** Environmental Systems Research Institute (ESRI) (2008-10-16) */
    public static final S62Agency ESRI = new S62Agency("4E", 20046);
    /** Euronav Ltd UK (2008-10-16) */
    public static final S62Agency EURONAV_LTD_UK = new S62Agency("2E", 11822);
    /** European Communities Commission (2008-10-16) */
    public static final S62Agency EUROPEAN_COMMUNITIES_COMMISSION = new S62Agency("QR", 1650);
    /** European Harbour Masters Association (2008-10-16) */
    public static final S62Agency EUROPEAN_HARBOUR_MASTERS_ASSOCIATION = new S62Agency("QS", 1660);
    /** European Inland ECDIS Expert Group (2011-04-07) */
    public static final S62Agency EUROPEAN_INLAND_ECDIS_EXPERT_GROUP = new S62Agency("5I", 24418);
    /** Fachstelle fuer Geoinformationen Sued beim WSA Regensburg (2008-10-16) */
    public static final S62Agency FACHSTELLE_FUER_GEOINFORMATIONEN_SUED_BEIM_WSA_REGENSBURG = new S62Agency("4W", 20320);
    /** Federation Internationale des Geometres (2008-10-16) */
    public static final S62Agency FEDERATION_INTERNATIONALE_DES_GEOMETRES = new S62Agency("QU", 1680);
    /** Finnish Navy (2008-10-16) */
    public static final S62Agency FINNISH_NAVY = new S62Agency("3F", 16191);
    /** Food and Agriculture Organization (2008-10-16) */
    public static final S62Agency FOOD_AND_AGRICULTURE_ORGANIZATION = new S62Agency("QT", 1670);
    /** Force Technology, Danish Maritime Institute (2008-10-16) */
    public static final S62Agency FORCE_TECHNOLOGY_DANISH_MARITIME_INSTITUTE = new S62Agency("1F", 7967);
    /** FPAEMDR DRIIREST (2011-07-18) */
    public static final S62Agency FPAEMDR_DRIIREST = new S62Agency("3D", 16038);
    /** Guoy Consultancy Sdn Bhd (2008-10-16) */
    public static final S62Agency GUOY_CONSULTANCY_SDN_BHD = new S62Agency("6C", 27756);
    /** Hamburg Port Authority (2008-10-16) */
    public static final S62Agency HAMBURG_PORT_AUTHORITY = new S62Agency("9H", 40865);
    /** Hochschule Bremen (Nautik) (2008-10-16) */
    public static final S62Agency NAUTIK = new S62Agency("2N", 12061);
    /** HSA Systems Pty Ltd (2008-10-16) */
    public static final S62Agency HSA_SYSTEMS_PTY_LTD = new S62Agency("8A", 35466);
    /** Hydrographic Office of the Sarawak Marine Department (2008-10-16) */
    public static final S62Agency HYDROGRAPHIC_OFFICE_OF_THE_SARAWAK_MARINE_DEPARTMENT = new S62Agency("5M", 24422);
    /** HYPACK, Inc. (2008-10-16) */
    public static final S62Agency HYPACK_INC = new S62Agency("3H", 16193);
    /** ICAN (2008-10-16) */
    public static final S62Agency ICAN = new S62Agency("3I", 16194);
    /** IHO Data Centre for Digital Bathymetry (2008-10-16) */
    public static final S62Agency IHO_DATA_CENTRE_FOR_DIGITAL_BATHYMETRY = new S62Agency("QP", 1630);
    /** IIC Technologies (2008-10-16) */
    public static final S62Agency IIC_TECHNOLOGIES = new S62Agency("2C", 11308);
    /** Innovative Navigation GmbH (2008-10-16) */
    public static final S62Agency INNOVATIVE_NAVIGATION_GMBH = new S62Agency("2I", 12056);
    /** Intergovernmental Oceanographic Commission (2008-10-16) */
    public static final S62Agency INTERGOVERNMENTAL_OCEANOGRAPHIC_COMMISSION = new S62Agency("XK", 1850);
    /** International Association of Geodesy (2008-10-16) */
    public static final S62Agency INTERNATIONAL_ASSOCIATION_OF_GEODESY = new S62Agency("QW", 1700);
    /** International Association of Institutes of Navigation (2008-10-16) */
    public static final S62Agency INTERNATIONAL_ASSOCIATION_OF_INSTITUTES_OF_NAVIGATION = new S62Agency("QX", 1710);
    /** International Association of Lighthouse Authorities (2008-10-16) */
    public static final S62Agency INTERNATIONAL_ASSOCIATION_OF_LIGHTHOUSE_AUTHORITIES = new S62Agency("QY", 1720);
    /** International Association of Ports and Harbours (2008-10-16) */
    public static final S62Agency INTERNATIONAL_ASSOCIATION_OF_PORTS_AND_HARBOURS = new S62Agency("QZ", 1730);
    /** International Atomic Energy Agency (2008-10-16) */
    public static final S62Agency INTERNATIONAL_ATOMIC_ENERGY_AGENCY = new S62Agency("QV", 1690);
    /** International Cable Protection Committee (2008-10-16) */
    public static final S62Agency INTERNATIONAL_CABLE_PROTECTION_COMMITTEE = new S62Agency("XB", 1750);
    /** International Cartographic Association (2008-10-16) */
    public static final S62Agency INTERNATIONAL_CARTOGRAPHIC_ASSOCIATION = new S62Agency("XA", 1740);
    /** International Centre for ENC (IC-ENC) (2008-10-16) */
    public static final S62Agency IC_ENC = new S62Agency("IC", 2030);
    /** International Chamber of Shipping (2008-10-16) */
    public static final S62Agency INTERNATIONAL_CHAMBER_OF_SHIPPING = new S62Agency("XC", 1760);
    /** International Commission for the Scientific Exploration of the Mediterranean (2008-10-16) */
    public static final S62Agency INTERNATIONAL_COMMISSION_FOR_THE_SCIENTIFIC_EXPLORATION_OF_THE_MEDITERRANEAN = new S62Agency("XD", 1770);
    /** International Council of Scientific Unions (2008-10-16) */
    public static final S62Agency INTERNATIONAL_COUNCIL_OF_SCIENTIFIC_UNIONS = new S62Agency("XE", 1780);
    /** International Electrotechnical Commission (2008-10-16) */
    public static final S62Agency INTERNATIONAL_ELECTROTECHNICAL_COMMISSION = new S62Agency("XF", 1790);
    /** International Geographical Union (2008-10-16) */
    public static final S62Agency INTERNATIONAL_GEOGRAPHICAL_UNION = new S62Agency("XG", 1800);
    /** International Maritime Academy (2008-10-16) */
    public static final S62Agency INTERNATIONAL_MARITIME_ACADEMY = new S62Agency("XH", 1820);
    /** International Maritime Organization (2008-10-16) */
    public static final S62Agency INTERNATIONAL_MARITIME_ORGANIZATION = new S62Agency("XI", 1830);
    /** International Maritime Satellite Organization (2008-10-16) */
    public static final S62Agency INTERNATIONAL_MARITIME_SATELLITE_ORGANIZATION = new S62Agency("XJ", 1840);
    /** International Organization for Standardization (2008-10-16) */
    public static final S62Agency INTERNATIONAL_ORGANIZATION_FOR_STANDARDIZATION = new S62Agency("XL", 1860);
    /** International Radio Consultative Committee (2008-10-16) */
    public static final S62Agency INTERNATIONAL_RADIO_CONSULTATIVE_COMMITTEE = new S62Agency("QN", 1610);
    /** International Society for Photogrammetry and Remote Sensing (2008-10-16) */
    public static final S62Agency INTERNATIONAL_SOCIETY_FOR_PHOTOGRAMMETRY_AND_REMOTE_SENSING = new S62Agency("XM", 1870);
    /** International Telecommunication Union (2008-10-16) */
    public static final S62Agency INTERNATIONAL_TELECOMMUNICATION_UNION = new S62Agency("XN", 1880);
    /** International Union of Geodesy and Geophysics (2008-10-16) */
    public static final S62Agency INTERNATIONAL_UNION_OF_GEODESY_AND_GEOPHYSICS = new S62Agency("XO", 1890);
    /** International Union of Surveying and Mapping (2008-10-16) */
    public static final S62Agency INTERNATIONAL_UNION_OF_SURVEYING_AND_MAPPING = new S62Agency("XP", 1900);
    /** Jeppesen Marine (2008-10-16) */
    public static final S62Agency JEPPESEN_MARINE = new S62Agency("2J", 12083);
    /** JS Co Geocentre-Consulting, Moscow (2008-10-16) */
    public static final S62Agency JS_CO_GEOCENTRE_CONSULTING_MOSCOW = new S62Agency("1Z", 7987);
    /** Kamvodput (2008-10-16) */
    public static final S62Agency KAMVODPUT = new S62Agency("2K", 12084);
    /** Kingway Technology Co (2008-10-16) */
    public static final S62Agency KINGWAY_TECHNOLOGY_CO = new S62Agency("1K", 7972);
    /** Land Information New Zealand Hydrographic Services (2008-10-16) */
    public static final S62Agency LAND_INFORMATION_NEW_ZEALAND_HYDROGRAPHIC_SERVICES = new S62Agency("2Z", 12099);
    /** Laser-Scan Ltd (2008-10-16) */
    public static final S62Agency LASER_SCAN_LTD = new S62Agency("1L", 7973);
    /** Latincomp (2011-03-31) */
    public static final S62Agency LATINCOMP = new S62Agency("7L", 32264);
    /** MARIN (Maritime Research Institute Netherlands) (2008-10-16) */
    public static final S62Agency MARITIME_RESEARCH_INSTITUTE_NETHERLANDS = new S62Agency("2M", 12060);
    /** MD Atlantic Technologies (2008-10-16) */
    public static final S62Agency MD_ATLANTIC_TECHNOLOGIES = new S62Agency("4R", 20315);
    /** MeteoConsult (2008-10-16) */
    public static final S62Agency METEOCONSULT = new S62Agency("4N", 20311);
    /** National Navigation Authority of the Czech Republic (2011-04-06) */
    public static final S62Agency NATIONAL_NAVIGATION_AUTHORITY_OF_THE_CZECH_REPUBLIC = new S62Agency("9D", 39624);
    /** Nautical Data International, Inc. (2008-10-16) */
    public static final S62Agency NAUTICAL_DATA_INTERNATIONAL_INC = new S62Agency("1N", 7975);
    /** Navionics S.p.A. (2008-10-16) */
    public static final S62Agency NAVIONICS_SPA = new S62Agency("1I", 7970);
    /** Navionics test and sample datasets (2008-10-16) */
    public static final S62Agency NAVIONICS_TEST_AND_SAMPLE_DATASETS = new S62Agency("1J", 7971);
    /** Navtor AS (2012-02-11) */
    public static final S62Agency NAVTOR_AS = new S62Agency("6N", 28233);
    /** NAVTRON SRL (2008-10-16) */
    public static final S62Agency NAVTRON_SRL = new S62Agency("5N", 24423);
    /** Nobeltec, Inc (2008-10-16) */
    public static final S62Agency NOBELTEC_INC = new S62Agency("9Z", 40883);
    /** Noorderzon Software (2008-10-16) */
    public static final S62Agency NOORDERZON_SOFTWARE = new S62Agency("1X", 7985);
    /** nv De Scheepvaart (2008-10-16) */
    public static final S62Agency NV_DE_SCHEEPVAART = new S62Agency("7V", 32655);
    /** Ocean Surveys Inc. (2008-10-16) */
    public static final S62Agency OCEAN_SURVEYS_INC = new S62Agency("3O", 16200);
    /** Offshore Systems Ltd. (2008-10-16) */
    public static final S62Agency OFFSHORE_SYSTEMS_LTD = new S62Agency("1O", 7976);
    /** Oil Companies International Marine Forum (2008-10-16) */
    public static final S62Agency OIL_COMPANIES_INTERNATIONAL_MARINE_FORUM = new S62Agency("XQ", 1910);
    /** OOO Tekhpromcomplect (2008-10-16) */
    public static final S62Agency OOO_TEKHPROMCOMPLECT = new S62Agency("4T", 20317);
    /** Pan American Institute of Geography and History (2008-10-16) */
    public static final S62Agency PAN_AMERICAN_INSTITUTE_OF_GEOGRAPHY_AND_HISTORY = new S62Agency("XR", 1920);
    /** Panama Canal Authority (2012-07-31) */
    public static final S62Agency PANAMA_CANAL_AUTHORITY = new S62Agency("6P", 28243);
    /** Pechora Waterways and Navigation Board (2008-10-16) */
    public static final S62Agency PECHORA_WATERWAYS_AND_NAVIGATION_BOARD = new S62Agency("3P", 16201);
    /** Petroslav Hydroservice, Russia (2008-10-16) */
    public static final S62Agency PETROSLAV_HYDROSERVICE_RUSSIA = new S62Agency("7H", 32641);
    /** PLOVPUT Beograd (2008-10-16) */
    public static final S62Agency PLOVPUT_BEOGRAD = new S62Agency("2P", 12063);
    /** Port Of London Authority (2008-10-16) */
    public static final S62Agency PORT_OF_LONDON_AUTHORITY = new S62Agency("1P", 7977);
    /** Port of Rotterdam (2008-10-16) */
    public static final S62Agency PORT_OF_ROTTERDAM = new S62Agency("2R", 12065);
    /** PRIMAR - European ENC Coordinating Centre (2008-10-16) */
    public static final S62Agency PRIMAR_EUROPEAN_ENC_COORDINATING_CENTRE = new S62Agency("PM", 2020);
    /** Public Works and Government Services Canada - Pacific Region (2011-03-30) */
    public static final S62Agency PUBLIC_WORKS_AND_GOVERNMENT_SERVICES_CANADA_PACIFIC_REGION = new S62Agency("4P", 16012);
    /** Quality Positioning Services (2008-10-16) */
    public static final S62Agency QUALITY_POSITIONING_SERVICES = new S62Agency("1Q", 7978);
    /** Radio Technical Commission for Maritime Services (2008-10-16) */
    public static final S62Agency RADIO_TECHNICAL_COMMISSION_FOR_MARITIME_SERVICES = new S62Agency("XS", 1930);
    /** Rheinschifffahrtsdirektion (RSD) Basel (2008-10-16) */
    public static final S62Agency RSD = new S62Agency("4C", 19532);
    /** Rijkswaterstaat (2008-10-16) */
    public static final S62Agency RIJKSWATERSTAAT = new S62Agency("1R", 7979);
    /** River Transport Authority (RTA), Egypt (2008-10-16) */
    public static final S62Agency RTA = new S62Agency("5E", 24158);
    /** Safe Trip SA, Argentina (2008-10-16) */
    public static final S62Agency SAFE_TRIP_SA_ARGENTINA = new S62Agency("5P", 24425);
    /** Science Applications International Corp (2008-10-16) */
    public static final S62Agency SCIENCE_APPLICATIONS_INTERNATIONAL_CORP = new S62Agency("3S", 16204);
    /** Scientific Commission on Antarctic Research (2008-10-16) */
    public static final S62Agency SCIENTIFIC_COMMISSION_ON_ANTARCTIC_RESEARCH = new S62Agency("XT", 1940);
    /** SeaZone Solutions (2012-03-23) */
    public static final S62Agency SEAZONE_SOLUTIONS = new S62Agency("5Z", 24168);
    /** Seebyte Ltd. (2008-10-16) */
    public static final S62Agency SEEBYTE_LTD = new S62Agency("8C", 35980);
    /** SevenCs AG & Co KG (2008-10-16) */
    public static final S62Agency SEVENCS_AG_AND_CO_KG = new S62Agency("7C", 31868);
    /** SHOM test data (2013-03-11) */
    public static final S62Agency SHOM_TEST_DATA = new S62Agency("4L", 20127);
    /** Solutions from Silicon, Sydney (2008-10-16) */
    public static final S62Agency SOLUTIONS_FROM_SILICON_SYDNEY = new S62Agency("9S", 40876);
    /** Ssangyong Information & Communications Corp. (2008-10-16) */
    public static final S62Agency SSANGYONG_INFORMATION_AND_COMMUNICATIONS_CORP = new S62Agency("2S", 12079);
    /** State Federal Unitary Enterprise NW Regional Production Centre of Geoinformation and Mine Surveying Centre, "Sevzapgeoinform" (Russia) (2008-10-16) */
    public static final S62Agency RUSSIA = new S62Agency("5R", 24427);
    /** SVP, s.p., OZ Bratislava (2008-10-16) */
    public static final S62Agency SVP_SP_OZ_BRATISLAVA = new S62Agency("2D", 11565);
    /** TEC Asociados (2008-10-16) */
    public static final S62Agency TEC_ASOCIADOS = new S62Agency("5T", 24455);
    /** Tér-Team Ltd., Budapest (2008-10-16) */
    public static final S62Agency TÉR_TEAM_LTD_BUDAPEST = new S62Agency("1Y", 7986);
    /** Terra Corp (2008-10-16) */
    public static final S62Agency TERRA_CORP = new S62Agency("7T", 32653);
    /** TerraNautical Data (2008-10-16) */
    public static final S62Agency TERRANAUTICAL_DATA = new S62Agency("1E", 7710);
    /** The Federal Service of Geodesy and Cartography of Russia (2008-10-16) */
    public static final S62Agency THE_FEDERAL_SERVICE_OF_GEODESY_AND_CARTOGRAPHY_OF_RUSSIA = new S62Agency("7R", 32651);
    /** The Hydrographic Society (2008-10-16) */
    public static final S62Agency THE_HYDROGRAPHIC_SOCIETY = new S62Agency("XU", 1950);
    /** The Volga State Territorial Department for Waterways (2008-10-16) */
    public static final S62Agency THE_VOLGA_STATE_TERRITORIAL_DEPARTMENT_FOR_WATERWAYS = new S62Agency("2V", 12095);
    /** The Volga-Baltic State Territorial Department for Waterways (2008-10-16) */
    public static final S62Agency THE_VOLGA_BALTIC_STATE_TERRITORIAL_DEPARTMENT_FOR_WATERWAYS = new S62Agency("1V", 7983);
    /** The Volga-Don Waterways And Navigation Board (2008-10-16) */
    public static final S62Agency THE_VOLGA_DON_WATERWAYS_AND_NAVIGATION_BOARD = new S62Agency("3V", 16207);
    /** Transas Marine (2008-10-16) */
    public static final S62Agency TRANSAS_MARINE = new S62Agency("2T", 12093);
    /** Tresco Engineering bvba (2008-10-16) */
    public static final S62Agency TRESCO_ENGINEERING_BVBA = new S62Agency("9T", 40877);
    /** Tresco Navigation Systems (2008-10-16) */
    public static final S62Agency TRESCO_NAVIGATION_SYSTEMS = new S62Agency("3T", 16205);
    /** Tridentnav Systems (2008-10-16) */
    public static final S62Agency TRIDENTNAV_SYSTEMS = new S62Agency("3N", 16199);
    /** U.S. Geological Survey (USGS) - Coastal and Marine Geology (2008-10-16) */
    public static final S62Agency USGS = new S62Agency("6U", 28542);
    /** UKHO - private production (2008-10-16) */
    public static final S62Agency UKHO_PRIVATE_PRODUCTION = new S62Agency("1T", 7981);
    /** UKHO test and sample datasets (2008-10-16) */
    public static final S62Agency UKHO_TEST_AND_SAMPLE_DATASETS = new S62Agency("1B", 0);
    /** ULTRANS TM srl (2008-10-16) */
    public static final S62Agency ULTRANS_TM_SRL = new S62Agency("2U", 12094);
    /** United Kingdom Royal Navy (2008-10-16) */
    public static final S62Agency UNITED_KINGDOM_ROYAL_NAVY = new S62Agency("5U", 24430);
    /** United Nations, Office for Ocean Affairs and Law of the Sea (2008-10-16) */
    public static final S62Agency UNITED_NATIONS_OFFICE_FOR_OCEAN_AFFAIRS_AND_LAW_OF_THE_SEA = new S62Agency("XW", 1970);
    /** US Army Corps of Engineers - Channel Condition Data (2008-10-16) */
    public static final S62Agency US_ARMY_CORPS_OF_ENGINEERS_CHANNEL_CONDITION_DATA = new S62Agency("3U", 16206);
    /** via donau - ?sterreichische Wasserstrassen-Gesellschaft mbH (2008-10-16) */
    public static final S62Agency VIA_DONAU_STERREICHISCHE_WASSERSTRASSEN_GESELLSCHAFT_MBH = new S62Agency("2W", 12096);
    /** Vituki Water Resources Research Centre Hungary (2008-10-16) */
    public static final S62Agency VITUKI_WATER_RESOURCES_RESEARCH_CENTRE_HUNGARY = new S62Agency("1H", 7969);
    /** Voies Navigables de France (VNF) (2008-10-16) */
    public static final S62Agency VNF = new S62Agency("4V", 20319);
    /** Wasser- und Schiffahrtsdirektion Nord (2008-10-16) */
    public static final S62Agency WASSER_UND_SCHIFFAHRTSDIREKTION_NORD = new S62Agency("5W", 24432);
    /** Wasser- und Schiffahrtsverwaltung des Bundes - Direktion SW (2008-10-16) */
    public static final S62Agency WASSER_UND_SCHIFFAHRTSVERWALTUNG_DES_BUNDES_DIREKTION_SW = new S62Agency("1W", 7984);
    /** Wasserschutzpolizei-Schule (2008-10-16) */
    public static final S62Agency WASSERSCHUTZPOLIZEI_SCHULE = new S62Agency("3W", 16208);
    /** Waterwegen en Zeekanaal (2008-10-16) */
    public static final S62Agency WATERWEGEN_EN_ZEEKANAAL = new S62Agency("7W", 32656);
    /** World Meteorological Organization (2008-10-16) */
    public static final S62Agency WORLD_METEOROLOGICAL_ORGANIZATION = new S62Agency("XV", 1960);

    S62Agency(final String ascii, final int binary) {
        super(S62Agency.class, ascii, binary, VALUES);
    }

    public static S62Agency[] values() {
        synchronized (VALUES) {
            return VALUES.toArray(new S62Agency[VALUES.size()]);
        }
    }

    @Override
    public S62Agency[] family() {
        return values();
    }

    public static S62Agency valueOf(Object code) {
        return (S62Agency) valueOf(VALUES, code);
    }
    
}
