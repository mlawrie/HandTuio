import java.net.SocketException;
import java.net.UnknownHostException;

import org.OpenNI.ActiveHandEventArgs;
import org.OpenNI.Context;
import org.OpenNI.GeneralException;
import org.OpenNI.GestureGenerator;
import org.OpenNI.GestureRecognizedEventArgs;
import org.OpenNI.HandsGenerator;
import org.OpenNI.IObservable;
import org.OpenNI.IObserver;
import org.OpenNI.InactiveHandEventArgs;
import org.OpenNI.Point3D;
import org.OpenNI.StatusException;


public class HandTracker {
	
    private HandsGenerator handsGen;
    private GestureGenerator gestureGen;
    private TUIOServer tuioServer;

    float lx = Float.NaN, ly = Float.NaN, lz = Float.NaN, lspeed = Float.NaN;
    float ltime = System.currentTimeMillis();
    
    public static void main(String args[]) {
    	NativeLoader.loadNatives();
    	NIContext.init(args.length > 1 ? args[1] : null);
    	
    	int port = 3333;
    	if (args.length > 0) {
    		try {
    		port = Integer.parseInt(args[0]);
    		} catch (NumberFormatException e) {
    			System.err.println("Cannot set port to " + args[0]);
    			System.out.println("Usage: java -jar handtuio.jar [port] [config file]");
    			System.exit(1);
    		}
    	}
    
    	HandTracker tracker = new HandTracker(port);
    	
    	while (true) {
    		try {
				NIContext.get().waitAnyUpdateAll();
			} catch (StatusException e) {
				e.printStackTrace();
			}
    	}
    }
    
    private void sendPoint(Point3D pt) {
    	
    	long time = System.currentTimeMillis();
    	float x = (pt.getX()+640)/1280f;
    	float y = (pt.getY()+480)/960f;
    	float z = pt.getZ()/4000f;
    	
    	
    	float dt = (time-ltime)/1000f;
    	float vx = 0, vy = 0, vz = 0;
    	if (lx != Float.NaN) {
    		vx = (x - lx)/dt;
    		vy = (y - ly)/dt;
    		vz = (z - lz)/dt;
    		
    	}
    	
    	float speed = vx + vy + vz;
    	float a = speed/dt;
    	if (lspeed != Float.NaN) {
    		a = (speed - lspeed)/dt;
    	}
    	
    	tuioServer.sendFrame(x,y,z, vx, vy, vz, a);
    	
    	lx = x;
    	ly = y;
    	lz = z;
    	lspeed = speed;
    	ltime = time;
    }
    
	public HandTracker(int port) {
		Context context = NIContext.get();
		
		try {
			tuioServer = new TUIOServer(port);
		} catch (SocketException e1) {
			e1.printStackTrace();
			System.exit(1);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		
		try {
			handsGen = HandsGenerator.create(context);
			gestureGen = GestureGenerator.create(context);
	        gestureGen.addGesture("Wave");
			
		} catch (GeneralException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
        try {
        	gestureGen.getGestureRecognizedEvent().addObserver(new IObserver<GestureRecognizedEventArgs>() {

				@Override
				public void update(
						IObservable<GestureRecognizedEventArgs> arg0,
						GestureRecognizedEventArgs arg1) {
						
						System.out.println("Wave gesture recognized.");
						System.out.println("Tracking new hand begun.");
						lspeed = lx = ly = lz = Float.NaN;
						ltime = System.currentTimeMillis();
						try {
							handsGen.StartTracking(arg1.getEndPosition());
						} catch (StatusException e) {
							e.printStackTrace();
						}
				}});
        	
			handsGen.getHandCreateEvent().addObserver(new IObserver<ActiveHandEventArgs>() {

				@Override
				public void update(IObservable<ActiveHandEventArgs> arg0,
						ActiveHandEventArgs arg1) {
					System.out.println("handCreateEvent");
					sendPoint(arg1.getPosition());
				}});
		
			handsGen.getHandUpdateEvent().addObserver(new IObserver<ActiveHandEventArgs>() {

				@Override
				public void update(IObservable<ActiveHandEventArgs> arg0,
						ActiveHandEventArgs arg1) {
					sendPoint(arg1.getPosition());
				}});
			handsGen.getHandDestroyEvent().addObserver(new IObserver<InactiveHandEventArgs>() {

				@Override
				public void update(IObservable<InactiveHandEventArgs> arg0,
						InactiveHandEventArgs arg1) {
					System.out.println("handDestroyEvent");
					tuioServer.sendFrame();
				}

			});
		} catch (StatusException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		
		
	}
}
