/**
 * This module uses:
 * <ul>
 *     <li><a href="https://github.com/ktuukkan/marine-api">Marine API</a> for NMEA-0183 message decoding</li>
 *     <li><a href="https://projectreactor.io/">Spring Reactor</a> to provide both asynchronous and non-blocking management of NMEA message flow</li>
 * </ul>
 *
 * For now, only file and serial port data sources are accepted. However, an experimental {@link org.geotoolkit.data.nmea.Discovery discvoery API}
 * has been created to allow for support of other datasource (USB, TCP, etc.) in th future.
 * For example, an efficient TCP support could be built by using <a href="https://projectreactor.io/docs/netty/release/api/index.html?reactor/netty/tcp/TcpClient.html">Reactor Netty TCP client</a>.
 *
 * Main components of this module are {@link org.geotoolkit.data.nmea.FeatureProcessor} and {@link org.geotoolkit.data.nmea.SerialPortFeatureSet}.
 * They allow for long-running updates of GPS data.
 *
 */
package org.geotoolkit.data.nmea;
