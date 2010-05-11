package ch.origamiaddict.stripecontrol.artnet.internal;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ArtNetSocket {

	private DatagramSocket 	socket;
	private String 			broadcastAddr = "";
	private InetAddress 	toAddr = null;

	/** Creates a new instance of ArtNetInterface */
	public ArtNetSocket() throws Exception {
		try {
			//socket = new DatagramSocket();
			
			
			socket = new DatagramSocket(0x1936);
			socket.setReuseAddress(true);
			InetAddress localhost = InetAddress.getLocalHost();
			String myIp = localhost.getHostAddress();
			String classA = myIp.substring(0, myIp.indexOf("."));
			broadcastAddr = classA + ".255.255.255";
			toAddr = InetAddress.getByName(broadcastAddr);
		} catch (Exception e) {
			throw e;
		}
		
	}

	public void kill() {
		socket.close();
	}

	public void sendPacket(Object obj) throws Exception {
		DatagramPacket packet = null;
		byte[] b = encode(obj);
		if (b.length == 0)
			throw new Exception("invalid packet");
		packet = new DatagramPacket(b, b.length, toAddr, 0x1936);
		socket.send(packet);
	}

	public byte[] encode(Object packet) {
		byte[] array = new byte[0];
		if (packet instanceof ArtNetDmxPacket) {
			array = new byte[18 + ((ArtNetDmxPacket) packet).getLength()];

			// set magic cookie
			array[0] = 'A';
			array[1] = 'r';
			array[2] = 't';
			array[3] = '-';
			array[4] = 'N';
			array[5] = 'e';
			array[6] = 't';
			array[7] = 0x00;

			// Op Code (0x5000)
			array[8] = (byte) (0x5000 & 0x0F);
			array[9] = (byte) (0x5000 >> 8); // High byte

			// Proto Ver (14)
			array[10] = 0x00; // High byte
			array[11] = 14;

			// Sequence (disabled)
			array[12] = 0x00;

			// Physical
			array[13] = (byte) ((ArtNetDmxPacket) packet).getPhysical();

			// Universe
			int uni = ((ArtNetDmxPacket) packet).getUniverse();
			array[14] = (byte) (uni & 0x0F);
			array[15] = (byte) (uni >> 8); // High byte

			// Length
			int len = ((ArtNetDmxPacket) packet).getLength();
			array[16] = (byte) (len >> 8); // High byte
			array[17] = (byte) (len & 0x0F);

			// Dmx Data
			int[] tmpData = ((ArtNetDmxPacket) packet).getData();
			for (int x = 18; x < array.length; x++)
				array[x] = (byte) tmpData[x - 18];
		}

		return array;
	}

}
