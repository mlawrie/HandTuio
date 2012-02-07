## HandTUIO

This is a basic implementation of single hand tracking on a PrimeSense/Kinect camera using OpenNI written in Java, which outputs to TUIO.

The app runs as a TUIO server, enabling you to do some PrimeSense fanciness in Flash, Processing or whatever. It sends both 2D and 3D cursor information, so if your
TUIO implementation supports it, try using the 3D data.

To run, simply call `java -jar handtuio.jar` from the `build/` folder. You can also specify an alternate port or OpenNI config file with `java -jar handtuio.jar -- 3333 my_config.xml`.
 I have included the required libs for Mac OSX. To run on Windows, you should just have to copy the .dll files from your OpenNI lib folder into the `build/lib/` folder.

Cheers!
