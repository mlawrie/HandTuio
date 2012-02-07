import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPacket;
import com.illposed.osc.OSCPortOut;
import com.illposed.osc.OSCBundle;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class TUIOServer
{    
    
    private final OSCPortOut sender;
    private long fseqFrame = 0;
    private int id = 1;
    
    public TUIOServer(int port) throws SocketException, UnknownHostException
    {
    	sender = new OSCPortOut(InetAddress.getLocalHost(), port);
    	System.out.println("TUIO running on port " + port);
    }
    
    private OSCPacket send2Dcur(Object[] args) {
    	return new OSCMessage("/tuio/2Dcur", args);
    	
    }
    
    private OSCPacket send3Dcur(Object[] args) {
    	return new OSCMessage("/tuio/3Dcur", args);
    }
    
    public void sendFrame() {
    	//Send blank frame when hand is removed
		OSCPacket[] packets = new OSCPacket[4];
    	
    	packets[0] = send2Dcur(new Object[] {"alive"});
    	packets[1] = send2Dcur(new Object[] {"fseq", (fseqFrame % Integer.MAX_VALUE) });
    	
    	packets[2] = send3Dcur(new Object[] {"alive"});
    	packets[3] = send3Dcur(new Object[] {"fseq", (fseqFrame % Integer.MAX_VALUE) });
    	
    	try {
			sender.send(new OSCBundle(packets));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		id++;
		id %= Integer.MAX_VALUE;
    	fseqFrame++;
    }
    
    public void sendFrame(float x, float y, float z, float vx, float vy, float vz, float a) {
    	
    	OSCPacket[] packets = new OSCPacket[6];
    	
    	packets[0] = send2Dcur(new Object[] {"alive", id});
    	packets[1] = send2Dcur(new Object[] {"set", id, x, y, vx, vy, a});
    	packets[2] = send2Dcur(new Object[] {"fseq", (fseqFrame % Integer.MAX_VALUE) });
    	
    	packets[3] = send3Dcur(new Object[] {"alive", id});
    	packets[4] = send3Dcur(new Object[] {"set", id+1, x, y, z, vx, vy, vz, a});
    	packets[5] = send3Dcur(new Object[] {"fseq", (fseqFrame % Integer.MAX_VALUE) });
    	
    	try {
			sender.send(new OSCBundle(packets));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	fseqFrame++;
    }
    

}