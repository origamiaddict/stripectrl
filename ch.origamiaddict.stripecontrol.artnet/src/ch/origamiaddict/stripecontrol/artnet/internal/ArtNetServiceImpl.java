package ch.origamiaddict.stripecontrol.artnet.internal;

import java.util.ArrayList;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

import ch.origamiaddict.stripecontrol.artnet.service.IArtNetService;
import ch.origamiaddict.stripecontrol.artnet.utils.DmxChannel;

public class ArtNetServiceImpl implements IArtNetService, EventHandler {
	private ArtNetSocket artnet = null;
	@SuppressWarnings("unused")
	private ComponentContext context;
	
	private LogService logger;
	
	private int[] dmxData = null;
	private int[] dmxDataBlackout = null;
	private boolean blackout = false;
    
    public boolean isBlackout() {
		return blackout;
	}

	public void setBlackout(boolean blackout) {
		log(LogService.LOG_DEBUG, " ArtNet blackout set to " + blackout);
		this.blackout = blackout;
		try {
			sendDmxPacket();
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}

	/**
	 * This constructor will instantiate a new USBDMX.com device. It will open
	 * the connection to the device, and enable DMX transmition.
	 * 
	 * @throws ADMXDeviceException
	 *             This exception will be thrown if there is an error trying to
	 *             connect to the USBDMX.com device.
	 */
	public ArtNetServiceImpl() throws Exception {
		super();
		dmxData = new int[512];
		try {
			artnet = new ArtNetSocket();
		} catch (Exception e) {
			throw new Exception(
					"Couldn't create an ArtNet object... Most likely a network error." + e.getMessage());
		}
	}
	
	private void log(int level, String message) {
		if(logger!= null) {
			logger.log(level, message);
		}
	}

    protected void activate(ComponentContext context){
        this.context = context;
        log(LogService.LOG_DEBUG, "Artnet driver activated");
    }



	/**
	 * This method will start a fade from the startValues to the endValues. The
	 * two Channel object arrays must be the same length and contain the same
	 * channel addresses, in the same order. IUpdateChannel and
	 * IUpdateFadeProgress are used to update the view during the fade.
	 * 
	 * @param startValues
	 *            The Channel object array of the starting values for the fade.
	 *            These values will be faded down in fadeDownMillis
	 *            milliseconds. This array must be the same length as endValues,
	 *            and contain the same addresses in the same order.
	 * @param endValues
	 *            The Channel object array of the ending values for the fade.
	 *            These values will be faded up in fadeUpMillis milliseconds.
	 *            This array must be the same length as startValues, and contain
	 *            the same addresses in the same order.
	 * @param fadeUpMillis
	 *            The time in milliseconds that it will take to fade in the
	 *            endValues.
	 * @param fadeDownMillis
	 *            The time in milliseconds that it will take to fade out the
	 *            startValues.
	 * @param channelUpdater
	 *            This interface contains two methods that will be called every
	 *            time a new value is written to the device. These methods tell
	 *            the model the current values of the channels during the fade.
	 *            Using these values, the view can be updated as the channels
	 *            fade.
	 * @param fadeUpdater
	 *            This class contains two methods that are used to update the
	 *            fade progress bars during the fade. They should be called
	 *            every time a value is written to the device.
	 * @return An array of Channel objects with the final values of the faded
	 *         channels.
	 * @throws ADMXDeviceException
	 *             This exception will be thrown if there is an error while
	 *             writting to the device.
	 */
	/*
	 * public Channel[] fadeValues(final Channel[] startValues, final Channel[]
	 * endValues, long fadeUpMillis, long fadeDownMillis, final IUpdateChannel
	 * channelUpdater, final IUpdateFadeProgress fadeUpdater) throws
	 * ADMXDeviceException { long startTime = System.currentTimeMillis();
	 * 
	 * final int fadeUpSends = (int)(fadeUpMillis / 22.0 + 0.5) + 1; final int
	 * fadeDownSends = (int)(fadeDownMillis / 22.0 + 0.5) + 1; final int
	 * totalSends = Math.max(fadeUpSends, fadeDownSends);
	 * 
	 * final boolean[] threadTimerDone = new boolean[1]; final boolean[]
	 * writeError = new boolean[1]; writeError[0] = false; threadTimerDone[0] =
	 * false; stopTransition[0] = false;
	 * 
	 * final boolean[] updateValues = new boolean[1]; updateValues[0] = false;
	 * final long[] errorResult = new long[1];
	 * 
	 * Timer threadTimer = new Timer(); threadTimer.scheduleAtFixedRate(new
	 * TimerTask() { Channel[] currentChannels = new
	 * Channel[startValues.length]; int send=1;
	 * 
	 * public void run() { for(int i = 0; i<startValues.length; i++) { myDmx[i] =
	 * (byte)(clip((float)(1.0 * (fadeDownSends - send) / fadeDownSends)) *
	 * startValues[i].value + clip(((float)(1.0 * send)) / fadeUpSends) *
	 * endValues[i].value); currentChannels[i] = new
	 * Channel(startValues[i].address, (short)((int)myDmx[i] & 0xFF)); } try {
	 * sendDmxPacket(); } catch(Exception e) { threadTimerDone[0] = true;
	 * writeError[0] = true; cancel(); return; }
	 * 
	 * channelUpdater.updateChannels(currentChannels);
	 * fadeUpdater.updateFadeUpProgress((int)(100.0 * clip((float)(1.0 * send /
	 * fadeUpSends)))); fadeUpdater.updateFadeDownProgress(100 - (int)(100.0 *
	 * clip((float)(1.0 * send / fadeDownSends))));
	 * 
	 * if(stopTransition[0] == true) { threadTimerDone[0] = true; cancel();
	 * return; }
	 * 
	 * send++; if(send > totalSends) { threadTimerDone[0] = true; cancel(); } } },
	 * 0, 22);
	 * 
	 * while(threadTimerDone[0] == false) Thread.yield();
	 * 
	 * if(writeError[0] == true) throw new ADMXDeviceException("Error writing to
	 * ArtNet device! Error code: " + Long.toString(errorResult[0]));
	 * 
	 * //long endTime = System.currentTimeMillis();
	 * 
	 * //System.out.println("fade up time=" + fadeUpMillis + " fade down time=" +
	 * fadeDownMillis + " run time=" + (endTime-startTime) + " to end time=" +
	 * endTime);
	 * 
	 * return endValues; }
	 */
	/**
	 * This method will clip values to a range of 0 to 1.
	 * 
	 * @param value
	 *            The value to be clipped.
	 * @return The clipped value.
	 */
	@SuppressWarnings("unused")
	private float clip(float value) {
		if (value > 1)
			return 1;
		else if (value < 0)
			return 0;
		else
			return value;
	}

	public void closeSocket() throws Exception {
		artnet.kill();
	}

	protected void deactivate(ComponentContext context){
		try {
			this.closeSocket();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log(LogService.LOG_ERROR, "Error closing ArtNet socket");
			e.printStackTrace();
		}
        this.context = null;
        log(LogService.LOG_DEBUG, "ArtNet driver deactivated");
    }

	@Override
	public boolean isRunning() {
		//Todo: more mock than anything else
		if(artnet != null) {
			return true;
		} else {
			return false;
		}
	}

	private void sendDmxPacket() throws Exception {
		ArtNetDmxPacket dmxPkt = new ArtNetDmxPacket();
		
		if(blackout) {
			if(dmxDataBlackout == null) {
				dmxDataBlackout = new int[dmxData.length];
			}
			dmxPkt.setData(dmxDataBlackout);
		} else {
			dmxPkt.setData(dmxData);			
		}
		dmxPkt.setLength(dmxData.length);
		dmxPkt.setPhysical(0);
		dmxPkt.setSequence(0);
		dmxPkt.setUniverse(0);
		artnet.sendPacket(dmxPkt);
	}

	/**
	 * This method sets the given channel, value pair on the device.
	 * 
	 * @param channel
	 *            The Channel object with the address, value pair to set on the
	 *            device.
	 * @return A Channel object containing the new value of the given address.
	 * @throws ADMXDeviceException
	 *             This exception will be thrown if there is an error while
	 *             writting to the device.
	 */
	public DmxChannel setValue(DmxChannel channel) throws Exception {		
		dmxData[channel.address - 1] = channel.value;
		try {
			sendDmxPacket();
		} catch (Exception e) {
			throw new Exception("Couldn't send ArtNet Packet: "
					+ e.getMessage());
		}
		return channel;
	}

	/**
	 * This method sets a series of values on the device.
	 * 
	 * @param channels
	 *            The array of address, value pairs to set on the device.
	 * @return The Channel objects containing the new values of the given
	 *         addresses.
	 * @throws ADMXDeviceException
	 *             This exception will be thrown if there is an error while
	 *             writting to the device.
	 */
	public DmxChannel[] setValues(DmxChannel[] channels) throws Exception {
		for (int x = 0; x < channels.length; x++) {
			dmxData[channels[x].address - 1] = channels[x].value;
		}
		try {
			sendDmxPacket();
		} catch (Exception e) {
			throw new Exception("Couldn't send via ArtNet");
		}
		return channels;
	}
	
	public Integer[] getValues(Integer[] channels) {
		
		Integer[] ret = new Integer[channels.length]; 
		
		for(int i= 0; i < channels.length; i++) {
			ret[i] = this.dmxData[channels[i] - 1];
		}
		return ret;
	}

	@Override
	public void handleEvent(Event event) {
		//"ch/origamiaddict/stripecontrol/events/dmxdriver/*"
		log(LogService.LOG_DEBUG, "ArtNet event received");
	
		
		//TODO: test for performance
		ArrayList<DmxChannel> channels = new ArrayList<DmxChannel>();
		
		for(String strCh : event.getPropertyNames()) {
			
			if (strCh.substring(0, 3).equals("ch_")) {
				try {
					

					Short channel = Short.parseShort(strCh.substring(3));
					Short value   = Short.parseShort((String) event.getProperty(strCh));
					
					channels.add(new DmxChannel(channel, value));
					
					try {
						setValues(channels.toArray(new DmxChannel[0]));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} catch (Exception e) {
					//TODO: handle
					
				}	
			}
		}
		
	}

	/*
	 * public void stopTransition() { stopTransition[0] = true; }
	 */

	/*
	 * public void fadeValues(final CueValueSet cue, final short[] changedAddrs)
	 * throws Exception { final int sends = (int)(cue.getFadeUpMillis() / 22.0 +
	 * 0.5) + 1;
	 * 
	 * final boolean[] threadTimerDone = new boolean[1]; final boolean[]
	 * writeError = new boolean[1]; writeError[0] = false; threadTimerDone[0] =
	 * false; stopTransition[0] = false;
	 * 
	 * final String[] errorResult = new String[1];
	 * 
	 * Timer threadTimer = new Timer(); threadTimer.scheduleAtFixedRate(new
	 * TimerTask() { int send=1;
	 * 
	 * public void run() { cue.setFadeLevel(clip(((float)(1.0 * send)) /
	 * sends));
	 * 
	 * for(short i=0; i<512; i++) { dmxData[i]
	 * =(byte)(valueGetter.getChannelValue((short)(i+1))); }
	 * 
	 * try { sendDmxPacket(); } catch(Exception e) { threadTimerDone[0] = true;
	 * writeError[0] = true; errorResult[0] = e.getMessage(); cancel(); return; }
	 * 
	 * if(stopTransition[0] == true) { threadTimerDone[0] = true; cancel();
	 * return; }
	 * 
	 * send++; if(send > sends) { threadTimerDone[0] = true; cancel(); } } }, 0,
	 * 22);
	 * 
	 * while(threadTimerDone[0] == false) Thread.yield();
	 * 
	 * if(writeError[0] == true) throw new Exception("Error sending Art-Net
	 * packet: " + errorResult[0]); }
	 */

	/*
	 * public void fadeValues(final CueValueSet startCue, final CueValueSet
	 * endCue) throws Exception { final int fadeUpSends =
	 * (int)(endCue.getFadeUpMillis() / 22.0 + 0.5) + 1; final int fadeDownSends =
	 * (int)(endCue.getFadeDownMillis() / 22.0 + 0.5) + 1; final int totalSends =
	 * Math.max(fadeUpSends, fadeDownSends);
	 * 
	 * final boolean[] threadTimerDone = new boolean[1]; final boolean[]
	 * writeError = new boolean[1]; writeError[0] = false; threadTimerDone[0] =
	 * false; stopTransition[0] = false;
	 * 
	 * final String[] errorResult = new String[1];
	 * 
	 * Timer threadTimer = new Timer(); threadTimer.scheduleAtFixedRate(new
	 * TimerTask() { int send=1;
	 * 
	 * public void run() { startCue.setFadeLevel(clip((float)(1.0 *
	 * (fadeDownSends - send) / fadeDownSends)));
	 * endCue.setFadeLevel(clip(((float)(1.0 * send)) / fadeUpSends));
	 * 
	 * for(short i=0; i < 512; i++) { dmxData[i]
	 * =(byte)(valueGetter.getChannelValue((short)(i+1))); }
	 * 
	 * try { sendDmxPacket(); } catch(Exception e) { threadTimerDone[0] = true;
	 * writeError[0] = true; errorResult[0] = e.getMessage(); cancel(); return; }
	 * 
	 * if(stopTransition[0] == true) { threadTimerDone[0] = true; cancel();
	 * return; }
	 * 
	 * send++; if(send > totalSends) { threadTimerDone[0] = true; cancel(); } } },
	 * 0, 22);
	 * 
	 * while(threadTimerDone[0] == false) Thread.yield();
	 * 
	 * if(writeError[0] == true) throw new Exception("Error sending Art-Net
	 * Packet: " + errorResult[0]); }
	 */
}
