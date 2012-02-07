import java.lang.reflect.Field;


public class NativeLoader {

	public static void loadNatives() {
		try {
			  Class clazz = ClassLoader.class;
				Field field = clazz.getDeclaredField("sys_paths");
				boolean accessible = field.isAccessible();
				if (!accessible)
				field.setAccessible(true);
				Object original = field.get(clazz);
				
				// Reset it to null so that whenever "System.loadLibrary" is called, it will be reconstructed with the changed value.
				field.set(clazz, null);
				
				try {
					// Change the value and load the library.
					System.setProperty("java.library.path", "lib/");
					System.loadLibrary("OpenNI.jni");
				} finally {
					//Revert back the changes.
					field.set(clazz, original);
					field.setAccessible(accessible);
				}
			  } catch (Exception e) {
				  System.err.println("Unable to load require libraries:");
				  e.printStackTrace();
			  }
	}
}
