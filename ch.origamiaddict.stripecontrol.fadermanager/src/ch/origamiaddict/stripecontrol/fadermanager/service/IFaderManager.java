package ch.origamiaddict.stripecontrol.fadermanager.service;

import java.util.List;

import org.osgi.service.event.EventAdmin;

import ch.origamiaddict.stripecontrol.fader.StripeFader;
import ch.origamiaddict.stripecontrol.queue.StripeQueue;

public interface IFaderManager {

	public void bindEventAdmin(EventAdmin eventAdmin);

	public void unbindEventAdmin(EventAdmin eventAdmin);

	public StripeFader addFader(String name);

	public StripeFader addFader(String name, StripeQueue queue);

	public StripeFader getFader(String name);

	public List<StripeFader> getFaders();

	public void setFaderQueue(String faderName, StripeQueue queue);

	public StripeFader removeFader(String name);

	public void removeFaders();

	public String[] getFaderNames();

	public void startFading(String faderName);

	public void stopFading(String faderName);

	public void startFadingAt(String faderName, int index);

	public void setFadingPuls(String faderName, long millisec);

	public long getFadingPuls(String faderName);

	public StripeQueue getFaderQueue(String faderName);

	public StripeFader createRgbColorWheelFader(String name);

	public StripeFader createRandomRgbFader(String name, int size, boolean loop);

	public void setFaderLoop(String faderName, boolean loop);

	public boolean isFaderLoop(String faderName);

}