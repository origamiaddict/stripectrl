package ch.origamiaddict.stripecontrol.stripe;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ch.origamiaddict.stripecontrol.stripe.channel.ChannelValue;

public class Stripe implements IStripe {

	protected Map<Integer, Integer> channelValues;
	protected boolean channelsLocked = false;
	private String name;
	
	public static enum TYPE{ 
		RGB,
		UNI,
		OTHER, 
		NONE
	};

	public Stripe() {
		super();
		channelValues = new LinkedHashMap<Integer, Integer>();
	}

	public Stripe(int... channels) {
		super();
		channelValues = new LinkedHashMap<Integer, Integer>();
		setChannels(channels);
	}

	public void setChannelValues(ChannelValue... channels) {
		if (channelsLocked)
			return;
		channelValues.clear();
		for (ChannelValue c : channels) {
			channelValues.put(c.getChannel(), c.getValue());
		}
	}

	public void setChannels(int... channels) {
		if (channelsLocked)
			return;
		channelValues.clear();
		for (int c : channels) {
			channelValues.put(c, -1);
		}
	}

	public Integer[] getValues() {
		return channelValues.values().toArray(new Integer[0]);
	}

	public boolean setValues(int... values) {
		if (values.length > channelValues.size()) {
			return false;
		} else {
			Iterator<Integer> it = channelValues.keySet().iterator();
			for (int v : values) {
				channelValues.put(it.next(), v);
			}
			return true;
		}
	}

	public List<ChannelValue> getChannelValues() {
		List<ChannelValue> channelValueList = new ArrayList<ChannelValue>();

		for (Integer channel : channelValues.keySet()) {
			channelValueList.add(new ChannelValue(channel, channelValues
					.get(channel)));
		}

		return channelValueList;
	}

	@Override
	public Integer[] getChannels() {
		return channelValues.keySet().toArray(new Integer[0]);
	}

	@Override
	public boolean isChannelsLocked() {
		return channelsLocked;
	}

	@Override
	public void lockChannels(boolean lock) {
		channelsLocked = lock;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		List<ChannelValue> arr = getChannelValues();
		for(ChannelValue c : arr) {
			sb.append("\n  --> " + c);
		}
		
		return "::STRIPE::" + sb;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setChannels(Integer[] channels) {
		if (channelsLocked)
			return;
		channelValues.clear();
		for (int c : channels) {
			channelValues.put(c, -1);
		}
		
	}
	

	public Color getColor() {
		switch(getType()) {
			case RGB:
				Iterator<Integer> it = channelValues.keySet().iterator();
				return new Color(channelValues.get(it.next()), channelValues.get(it.next()), channelValues.get(it.next()));
			case UNI:
				return new Color(channelValues.get(0), channelValues.get(0), channelValues.get(0));
			case NONE:
			default:
				return null;
		}
		
	}

	public void setColor(Color c) {
		switch(getType()) {
			case RGB:
				Iterator<Integer> it = channelValues.keySet().iterator();
				channelValues.put(it.next(), c.getRed());
				channelValues.put(it.next(), c.getGreen());
				channelValues.put(it.next(), c.getBlue());
				break;
			case UNI:
				channelValues.put(channelValues.keySet().iterator().next(), c.getRed());
				break;
		}
	}

	@Override
	public TYPE getType() {
		switch(channelValues.size()) {
			case 0:
				return TYPE.NONE;
			case 1:
				return TYPE.UNI;
			case 3:
				return TYPE.RGB;
			default:
				return TYPE.OTHER;	
		}
	}
}
