package ch.origamiaddict.stripecontrol.stripe;

import java.awt.Color;
import java.util.List;

import ch.origamiaddict.stripecontrol.stripe.channel.ChannelValue;

public interface IStripe {
	
	public String getName();
	public void setName(String name);

	public void setChannels(int... channels);
	
	public Integer[] getChannels();

	public Integer[] getValues();

	public boolean setValues(int... values);

	public List<ChannelValue> getChannelValues();

	public void setChannelValues(ChannelValue... channels);
	
	public boolean isChannelsLocked();
	public void lockChannels(boolean lock);
	public void setChannels(Integer[] array);
	
	public Color getColor();

	public void setColor(Color c);
	
	public Stripe.TYPE getType();
}