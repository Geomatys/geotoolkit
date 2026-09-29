/**
 * HDF-5 file format reader.
 *
 * <p>Specification: <a href="https://support.hdfgroup.org/documentation/hdf5/latest/_f_m_t3.html">HDF5 File Format Specification Version 3.0</a></p>
 *
 * <p>Note-1 : The specification contains bugs(differences between what it says and what the hdf5 lib does) and unsaid informations.
 *        As a result we have no other choice but to check the code from HDF5 library
 *        and sometimes to transpose it's logic without understand it to be able to read HDF-5 files.
 *        This is the unfortunate result of a specification build above a specific library.
 *        Which is not uncommon (like the Draco compression), but leads to lower quality specifications.</p>
 * <p>Note-2 : As a result of note 1, HDF-5 should not be used as a reliable exchange format because what dictates
 *        a valid HDF-5 file is what produces HDFLib, not what the specification dictates. Such behavior may lead
 *        to unreadable files in the futur.</p>
 */
package org.geotoolkit.hdf;
