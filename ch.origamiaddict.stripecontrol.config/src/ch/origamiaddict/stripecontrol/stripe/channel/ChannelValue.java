package ch.origamiaddict.stripecontrol.stripe.channel;

public class ChannelValue implements IChannelValue {

	private int channel;
	private int value;

	public ChannelValue() {
		super();
		channel = -1;
		value = -1;
	}

	public ChannelValue(int c, int v) {
		super();
		channel = c;
		value = v;
	}

	public int getChannel() {
		return channel;
	}

	public int getValue() {
		return value;
	}

	public void setChannel(int c) {
		channel = c;
	}

	public void setValue(int v) {
		value = v;
	}

	@Override
	public String toString() {
		return "CH: " + channel + " VAL: " + value;
	}
}
