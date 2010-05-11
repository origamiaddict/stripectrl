package ch.origamiaddict.stripecontrol.fader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;


import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;

import ch.origamiaddict.stripecontrol.queue.IQueueItem;
import ch.origamiaddict.stripecontrol.queue.StripeQueue;
import ch.origamiaddict.stripecontrol.stripe.IStripe;
import ch.origamiaddict.stripecontrol.stripe.channel.ChannelValue;

public class StripeFader<S extends IStripe> implements ITickObserver {
			
	// fade from to in x steps
	private class FadeObject {
		List<ChannelValue> from;
		List<ChannelValue> to;
		List<ChannelValue> current;
		long fadeSteps;
		long fadeStepCnt;

		List<Float> fadeDelta = new ArrayList<Float>();
		List<Float> calcValues = new ArrayList<Float>();

		public void setCalcValues(List<ChannelValue> l) {
			calcValues.clear();
			for (ChannelValue c : l) {
				calcValues.add(new Float(c.getValue()));
			}
		}

	}

	private enum EItemPhase {
		IN, ON, OUT, TRANS;
	}

	private enum ETransType {
		OUT_IN, DIRECT_FADE, DIRECT_CHANGE;
	}
	
	private EventAdmin eventAdmin;
	private LogService logger;

	private Timer stripeTimer;

	private long tickInterval;

	private StripeQueue<S> queue;
	private long inTickCount;

	private long transTickCount;
	private long outTickCount;
	private long onTickCount;
	private long inTicks;

	private long transTicks;

	private long outTicks;

	private long onTicks;

	private long prevOutTickCount = 0;

	private long startInAtTransCount = 0;

	private boolean fadingInProgress = false;

	// start at transition phase
	private EItemPhase phase = EItemPhase.TRANS;
	private ETransType transType = ETransType.OUT_IN;


	FadeObject fo = new FadeObject();


	public StripeFader() {
		//stripeTimer = new Timer();
	}
	
	private void log(int level, String message) {
		if(logger != null) {
			logger.log(level, message);
		}
	}

	private void fade(FadeObject fo) {
		if (fo == null)
			return;

		// must be fading
		if (fo.fadeStepCnt == fo.fadeSteps && fo.fadeSteps > 0) {
			setFadingInProgress(true);
		} else if (fo.fadeStepCnt <= 0) {
			setFadingInProgress(false);
		}

		if (isFadingInProgress()) {
			 
			log(LogService.LOG_DEBUG,"from	: " + fo.from);
			log(LogService.LOG_DEBUG,"to	: " + fo.to);
			log(LogService.LOG_DEBUG,"current " + fo.current);
			log(LogService.LOG_DEBUG,"steps	: " + fo.fadeSteps);
			log(LogService.LOG_DEBUG,"stepcnt " + fo.fadeStepCnt);
			log(LogService.LOG_DEBUG,"delta	: " + fo.fadeDelta);

			float newValue;
			float delta;
			for (int i = 0; i < fo.current.size(); i++) {
				delta = fo.fadeDelta.get(i);

				// crop delta values
				// mabye this isnt the best way...
				if (delta < 1.0 && delta > 0.0f) {
					delta = 1.0f;
				} else if (delta < 0.0 && delta > -1.0f) {
					delta = -1.0f;
				}

				newValue = (float) fo.current.get(i).getValue() + delta;

				if (newValue < 1.0) {
					newValue = 0;
				} else if (newValue > 255) {
					newValue = 255;
				}
				// logger.debug("new Value " + newValue);
				fo.current.get(i).setValue(Math.round(newValue));
			}
			fo.fadeStepCnt--;
			// set channel value here
			log(LogService.LOG_DEBUG,"setting channel values:");
			log(LogService.LOG_DEBUG,"current color-> R:" + fo.current.get(0).getValue()
					+ " G:" + fo.current.get(1).getValue() + " B:"
					+ fo.current.get(2).getValue());
			
			
			//notify all observers of fading..
			//here the values are written to the outside
			
			faderOutput(fo.current);
						
		}
	}
	
	
	private void faderOutput(List<ChannelValue> channelValues) {
		Map<String, String> eventMap = new HashMap<String,String>();			
		for(ChannelValue c : channelValues) {
			eventMap.put("ch_" + new Integer(c.getChannel()).toString(), new Integer(c.getValue()).toString());
		}
		Event event = new Event("ch/origamiaddict/stripecontrol/events/dmxdriver/FADER", eventMap);
		eventAdmin.postEvent(event);
	}
	

	// TODO: heres the big fading bug
	public void fadeStartHandler() {
		// which phase are we currently in?
		switch (phase) {
		case TRANS:
			switch (transType) {
			case OUT_IN:

				// when to start the in fading
				if (transTickCount <= startInAtTransCount) {
					// start in fade
					log(LogService.LOG_DEBUG,"IN start " + startInAtTransCount);
					// phase transition
					phase = EItemPhase.IN;
					setFade(null, queue.getCurrent(), inTicks);
					// setFade(cueManager.getPrevious(),
					// cueManager.getCurrent(), inTicks);
				} else {
					transTickCount--;
				}

				if (prevOutTickCount > 0) {
					// fade out
					prevOutTickCount--;
				} else {
					// logger.info("OUT finished");
				}
				break;
			case DIRECT_FADE:
				if (transTickCount == 0) {
					phase = EItemPhase.ON;
					log(LogService.LOG_DEBUG,"ON start");
				} else {
					transTickCount--;
				}
				break;
			case DIRECT_CHANGE:
				// todo: test
				// TODO: doesn't work!!
				setFade(queue.getCurrent(), queue.getNext(), 1);
				try {
					setupCurrentFadeCounters();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// just for readability
				phase = EItemPhase.ON;
				break;
			}
			break;
		case IN:
			if (inTickCount == 0) {
				// start on phase
				log(LogService.LOG_DEBUG,"ON start");
				phase = EItemPhase.ON;
				// setFade("on", null, null, onTicks);
			} else {
				// continue in fade
				inTickCount--;
			}
			break;
		case ON:
			if (onTickCount == 0) {
				log(LogService.LOG_DEBUG,"ON end");
				switch (transType) {
				case OUT_IN:
					// start out phase
					log(LogService.LOG_DEBUG,"OUT start");
					phase = EItemPhase.OUT;
					setFade(queue.getCurrent(), null, outTicks);
					break;
				case DIRECT_FADE:
					log(LogService.LOG_DEBUG,"Direct Fade start");
					setFade(queue.getCurrent(), queue.getNext(), transTicks);
					try {
						setupCurrentFadeCounters();
					} catch (Exception e) {
						e.printStackTrace();
					}
					phase = EItemPhase.TRANS;
					break;

				// TODO: does this only belong into case TRANS?
				case DIRECT_CHANGE:
					// todo: test
					// TODO: doesn't work!!
					setFade(queue.getCurrent(), queue.getNext(), 1);
					try {
						setupCurrentFadeCounters();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// just for readability
					phase = EItemPhase.ON;
					break;
				}
			} else {
				// continue on fade
				onTickCount--;
			}
			break;
		case OUT:
			// setup next fade
			if (outTickCount == outTicks) {
				prevOutTickCount = outTickCount;
				// setup next fade once
				try {
					setupCurrentFadeCounters();
					phase = EItemPhase.TRANS;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		}
	}

	public StripeQueue<S> getQueueManager() {
		return queue;
	}

	public long getTickInterval() {
		return tickInterval;
	}

	public boolean isFadingInProgress() {
		return fadingInProgress;
	}

	public void setQueueManager(StripeQueue<S> queue) {
		this.queue = queue;
	}

	private void setFade(IQueueItem<S> fromItem, IQueueItem<S> toItem, long fadeSteps) {

		List<ChannelValue> from;
		List<ChannelValue> to;

		if (fromItem == null & toItem == null) {
			// shouldn't happen
			log(LogService.LOG_DEBUG,"servere setFade()");
			return;
		}

		log(LogService.LOG_DEBUG,"add fading from " + fromItem + " to " + toItem + " in "
				+ fadeSteps + " steps");
		// logger.info(toItem.getStripe());

		if (fromItem == null) {
			from = toItem.getStripe().getChannelValues();
			for (ChannelValue c : from) {
				c.setValue(0);
			}
		} else {
			from = fromItem.getStripe().getChannelValues();
		}

		if (toItem == null) {
			to = fromItem.getStripe().getChannelValues();
			for (ChannelValue c : to) {
				c.setValue(0);
			}
		} else {
			to = toItem.getStripe().getChannelValues();
		}

		if (isFadingInProgress()) {
			// setup new fade with current color value = new from value
			fo.from = fo.current;
			fo.to = to;
			fo.fadeSteps = fadeSteps;
			fo.fadeStepCnt = fadeSteps;
			fo.setCalcValues(fo.current);

		} else {
			// setup new fading process
			fo.setCalcValues(from);
			fo.from = from;
			fo.current = from;
			fo.to = to;
			fo.fadeSteps = fadeSteps;
			fo.fadeStepCnt = fadeSteps;
		}

		// calculating fade deltas
		fo.fadeDelta.clear();
		for (int i = 0; i < fo.to.size(); i++) {
			fo.fadeDelta.add(((float) fo.to.get(i).getValue() - (float) fo.from
					.get(i).getValue())
					/ (float) fadeSteps);
		}

	}

	public void setFadingInProgress(boolean fadingInProgress) {
		this.fadingInProgress = fadingInProgress;
	}

	public void setTickInterval(long tickInterval) {
		this.tickInterval = tickInterval;
	}

	// maybe private later
	public void setupCurrentFadeCounters() throws Exception {

		if (getTickInterval() == 0)
			throw new ArithmeticException("tickcount can't be 0 (zero)");

		IQueueItem<S> current = queue.next();
		IQueueItem<S> previous = queue.getPrevious();
		// ICueItem next = cueManager.getNext();

		log(LogService.LOG_DEBUG,"Calculating fading ticks...");

		// nulling first...
		inTicks = 0;
		onTicks = 0;
		outTicks = 0;
		transTicks = 0;

		long outPrevCnt = 0;
		long inOutTransDelta;

		if (current != null) {
			this.inTicks = Math.round((float) current.getTransition()
					.getFadeInTime()
					/ (float) getTickInterval());
			// this.inTicks = current.getTransition().getFadeInTime() /
			// getTickInterval();
			this.onTicks = Math.round((float) current.getOnTime()
					/ (float) getTickInterval());
			this.outTicks = Math.round((float) current.getTransition()
					.getFadeOutTime()
					/ (float) getTickInterval());
		}

		if (previous != null) {
			this.transTicks = Math.round((float) previous.getTransition()
					.getTransitionTime()
					/ (float) getTickInterval());
			outPrevCnt = Math.round((float) previous.getTransition()
					.getFadeOutTime()
					/ (float) getTickInterval());
		} else {
			transTicks = 0;
		}

		// calculate transition delta
		// bsp 2000 - 1000 - 1500 = -500
		inOutTransDelta = transTicks - outPrevCnt - inTicks;
		// TODO: fix here -> fading bug
		if (inOutTransDelta == 0) {
			startInAtTransCount = 0;
		} else if (inOutTransDelta > 0) {
			startInAtTransCount = transTicks - inTicks;
		} else if (inOutTransDelta < 0) {
			// TODO:verifyverify this is correct:
			if (transTicks == 0)
				startInAtTransCount = 0;
			else {
				startInAtTransCount = transTicks
						- (inTicks + inOutTransDelta / 2);
				this.outTicks -= inOutTransDelta / 2;
			}
		}

		log(LogService.LOG_DEBUG,"trans: " + transTicks);
		log(LogService.LOG_DEBUG,"in:    " + inTicks);
		log(LogService.LOG_DEBUG,"on:    " + onTicks);
		log(LogService.LOG_DEBUG,"out:   " + outTicks);
		log(LogService.LOG_DEBUG,"start in at transition count: " + startInAtTransCount);

		// setting up reduction counters:
		inTickCount = inTicks;
		onTickCount = onTicks;
		outTickCount = outTicks;
		transTickCount = transTicks;

		// setting up transition type
		if (inTicks == 0 && outPrevCnt == 0) {
			// direct fade between colors
			transType = ETransType.DIRECT_FADE;
		} else if (transTicks == 0) {
			// no fade, direct color change
			transType = ETransType.DIRECT_CHANGE;
		} else {
			// normal fading with intensity reduction
			transType = ETransType.OUT_IN;
		}

	}

	private void startTicker() {
		log(LogService.LOG_DEBUG,"Ticker started");
		StripeTimerTask tt = new StripeTimerTask();
		tt.addObserver(this);
		stripeTimer = new Timer();
		stripeTimer.scheduleAtFixedRate(tt, 0, tickInterval);
	}

	private void stopTicker() {
		log(LogService.LOG_DEBUG,"Ticker stopped");
		stripeTimer.cancel();
	}

	@Override
	public void tick(ITickObservable o) {
		// logger.info("Tick");
		this.fadeStartHandler();
		this.fade(fo);
	}

	public void startFading() {
		startTicker();
	}
	
	public void startFadingAt(int index) {
		queue.setCurrentTo(index);
		startFading();
	}

	public void stopFading() {
		stopTicker();
	}

	public void setEventAdmin(EventAdmin eventAdmin) {
		this.eventAdmin = eventAdmin;
	}

	public void setLogService(LogService logger) {
		this.logger = logger;
		
	}

}
