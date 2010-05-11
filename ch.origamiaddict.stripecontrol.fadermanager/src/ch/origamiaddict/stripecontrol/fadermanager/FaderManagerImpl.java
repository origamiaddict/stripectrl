package ch.origamiaddict.stripecontrol.fadermanager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;

import ch.origamiaddict.stripecontrol.factories.QueueFactory;
import ch.origamiaddict.stripecontrol.fader.StripeFader;
import ch.origamiaddict.stripecontrol.fadermanager.service.IFaderManager;
import ch.origamiaddict.stripecontrol.queue.StripeQueue;

public class FaderManagerImpl implements IFaderManager {
	
	private LogService logger;
	
	@SuppressWarnings("unused")
	private ComponentContext context;
	private EventAdmin eventAdmin;
	private Map<String, StripeFader> faderMap;
	
	protected void activate(ComponentContext context) {
		this.context = context;	
		faderMap = new HashMap<String, StripeFader>();
		//logger.log(LogService.LOG_DEBUG, arg1)
	}

	public StripeFader addFader(String name) {
		StripeFader fader = new StripeFader();
		fader.setEventAdmin(eventAdmin);
		fader.setLogService(logger);
		fader.setQueueManager(new StripeQueue());
		faderMap.put(name, fader);
		return fader;
	}
	
	public StripeFader addFader(String name, StripeQueue queue) {
		StripeFader f = addFader(name);
		f.setQueueManager(queue);
		return f;
	}
	
	public void bindEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	public StripeFader createRandomRgbFader(String name, int size, boolean loop) {
		QueueFactory qf = new QueueFactory();
		this.addFader(name);
		this.getFader(name).setQueueManager(qf.createRandomRgbQueue(size, loop));
		return this.getFader(name);

	}

	public StripeFader createRgbColorWheelFader(String name) {
		QueueFactory qf = new QueueFactory();

		this.addFader(name);
		this.getFader(name).setQueueManager(qf.createColorWheelRgbQueue());
		this.getFader(name).setTickInterval(50);
		return this.getFader(name);
	}

	protected void deactivate(ComponentContext context) {
		this.context = null;
	}

	public StripeFader getFader(String name) {
		return faderMap.get(name);
	}

	public String[] getFaderNames() {
		return faderMap.keySet().toArray(new String[0]);
	}

	public StripeQueue getFaderQueue(String faderName) {
		if (faderMap.containsKey(faderName)) {
			return faderMap.get(faderName).getQueueManager();
		} else {
			return null;
		}
	}

	public List<StripeFader> getFaders() {
		return new ArrayList<StripeFader>(faderMap.values());
	}

	public long getFadingPuls(String faderName) {
		if (faderMap.containsKey(faderName)) {
			return faderMap.get(faderName).getTickInterval();
		}
		return -1L;
	}

	public boolean isFaderLoop(String faderName) {
		if (faderMap.containsKey(faderName)) {
			return faderMap.get(faderName).getQueueManager().isLoop();
		} else {
			return false;
		}		
	}

	public StripeFader removeFader(String name) {
		if (faderMap.containsKey(name)) {
			return faderMap.remove(name);
		} else {
			return null;
		}
	}

	public void removeFaders() {
		faderMap.clear();
	}

	public void setFaderLoop(String faderName, boolean loop) {
		if (faderMap.containsKey(faderName)) {
			faderMap.get(faderName).getQueueManager().setLoop(loop);
		}
	}

	public void setFaderQueue(String faderName, StripeQueue queue) {
		if (faderMap.containsKey(faderName)) {
			faderMap.get(faderName).setQueueManager(queue);
		}
	}

	public void setFadingPuls(String faderName, long millisec) {
		if (faderMap.containsKey(faderName)) {
			faderMap.get(faderName).setTickInterval(millisec);
		}
	}

	public void startFading(String faderName) {
		if (faderMap.containsKey(faderName)) {
			faderMap.get(faderName).startFading();
		}
	}

	public void startFadingAt(String faderName, int index) {
		if (faderMap.containsKey(faderName)) {
			faderMap.get(faderName).startFadingAt(index);
		}
	}
	
	public void stopFading(String faderName) {
		if (faderMap.containsKey(faderName)) {
			faderMap.get(faderName).stopFading();
		}
	}
	
	public void unbindEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = null;
	}

}
