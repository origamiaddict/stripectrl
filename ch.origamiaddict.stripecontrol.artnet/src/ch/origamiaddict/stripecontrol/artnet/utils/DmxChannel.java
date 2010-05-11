package ch.origamiaddict.stripecontrol.artnet.utils;

public class DmxChannel implements Comparable<DmxChannel> {

	public short address;

	public short value;

	public DmxChannel() {
		address = -1;
		value = -1;
	}

	public DmxChannel(short _address, short _value) {
		address = _address;
		value = _value;
	}

	public int compareTo(DmxChannel c) {
		return ((Short) address).compareTo(c.address);
	}

	@Override
	public String toString() {
		return "(" + address + ", " + value + ")";
	}

	public static short[] getAddresses(DmxChannel[] channels) {
		short[] addresses = new short[channels.length];
		for (int i = 0; i < addresses.length; i++)
			addresses[i] = channels[i].address;

		return addresses;
	}

	public static DmxChannel[] replaceValues(DmxChannel[] oldValues,
			short[] newValues) {
		for (int i = 0; i < oldValues.length; i++)
			oldValues[i].value = newValues[i];

		return oldValues;
	}

	public static DmxChannel[] makeChannels(short[] addresses, short[] values) {
		DmxChannel[] channels = new DmxChannel[addresses.length];
		for (int i = 0; i < channels.length; i++)
			channels[i] = new DmxChannel(addresses[i], values[i]);

		return channels;
	}

}