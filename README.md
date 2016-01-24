Note4 IR Remote
===============


The Galaxy Note 4 comes with a built-in IR blaster that's capable of controlling a wide range of devices, including TVs, projectors, game consoles, and set-top boxes. This repo contains an Android app that uses the OS's [ConsumerIrManager API](http://developer.android.com/reference/android/hardware/ConsumerIrManager.html) to control the IR blaster and communicate with the user's device.

The app should in theory work on any Android device with an IR blaster, but it's been tested only on a Note 4.

The boilerplate code for accessing ConsumerIrManager comes from [here](https://android.googlesource.com/platform/development/+/438ea813f1846f88205ff98436568aaa34f06845/samples/ApiDemos/src/com/example/android/apis/hardware/ConsumerIr.java).

The app is a work in progress.

## Future goals ##
* **UI**: Add an intuitive UI that allows the user to select their current device and see the list of signals that can be sent to it.
* **Web scraping**: Allow a user to input a URL from [remotecentral.com](http://www.remotecentral.com), e.g. [this URL](https://www.remotecentral.com/cgi-bin/codes/samsung/tv_functions/), and have the page's IR control codes be imported into the app, allowing the user to leverage Remote Central's community-built database of IR control codes.
