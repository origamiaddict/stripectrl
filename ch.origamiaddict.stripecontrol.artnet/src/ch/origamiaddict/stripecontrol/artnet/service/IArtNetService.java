	package ch.origamiaddict.stripecontrol.artnet.service;

import ch.origamiaddict.stripecontrol.artnet.utils.DmxChannel;

public interface IArtNetService {

	public DmxChannel setValue(DmxChannel channel) throws Exception;
	public DmxChannel[] setValues(DmxChannel[] channels) throws Exception;
	public Integer[] getValues(Integer[] channels);
	
    public boolean isBlackout();
	public void setBlackout(boolean blackout);
	public boolean isRunning();
}
