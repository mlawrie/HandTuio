
import org.OpenNI.GeneralException;
import org.OpenNI.OutArg;
import org.OpenNI.ScriptNode;
import org.OpenNI.Context;

public class NIContext {

	private static Context context;
	private static OutArg<ScriptNode> scriptNode;
	
	public static void init(String path) {
		scriptNode = new OutArg<ScriptNode>();
	    try {
			context = Context.createFromXmlFile(path == null ? "OpenNIConfig.xml" : path, scriptNode);
		} catch (GeneralException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static Context get() {
		return context;
	}
	
}
