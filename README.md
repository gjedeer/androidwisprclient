androidwisprclient
==================

An Android client for automatic authentication in FON network using WISPr

Supports routers managed by FON and partners (BT, Neuf, Zon, Comstar, etc)

This is a fork of https://github.com/jfisbein/androidwisprclient blessed by the original author, Joan Fisbein.

I intend to:

✓ Fix internet connectivity detection

✓ Add support for Netia routers (FON_NETIA_FREE_INTERNET) [this is done, no special treatment was needed, it was a general bug that's fixed]

✓ Try to get this code on F-Droid [done https://f-droid.org/repository/browse/?fdid=com.oakley.fon ]

4) Accept pull requests from you (DIY style, your pull requests are welcome but your issues are not)

Get your signed APK file here:

https://github.com/gjedeer/androidwisprclient/releases

## Tips

* If you're using a Netia-provided fon router (as opposed to a regular fonera purchased separately), you need to append the #netia suffix to your username (ex: peter@gmail.com -> peter@gmail.com#netia)
* Don't add FON_* networks in Android Wi-Fi connection manager. This program will find such networks and connect to them automatically. Adding them may interfere with its operation.
