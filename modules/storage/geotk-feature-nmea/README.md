# NMEA

## Useful links

Online NMEA file generator: https://nmeagen.org/

## USB

### Troubleshooting

First, you can check this page for common problems: 
http://usb4java.org/faq.html

#### Linux

Most common problem is permissions lack. You can try to add your user into the following groups:
 * dialout
 * uucp
 * tty

If it does not  solve your problem, try to define a custom UDEV rule to allow connection on USB drive.
See the following *askubuntu* post for details:
https://askubuntu.com/a/680328
