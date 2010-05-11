package ch.origamiaddict.stripecontrol.queue;

import ch.origamiaddict.stripecontrol.stripe.IStripe;

public class QueueItem<S extends IStripe> implements IQueueItem<S> {
	
	private long onTime		= 0;	
	private S stripe;
	
	private ITransitionItem transition;
	
	public QueueItem() {
		super();
		transition = new TransitionItem();
	}
	
	public QueueItem(long onTime) {
		super();
		this.onTime = onTime;
	}

	@Override
	public long getOnTime() {
		return onTime;
	}

	@Override
	public S getStripe() {
		return stripe;
	}

	@Override
	public void setOnTime(long milliseconds) {
		 onTime = milliseconds;
	}

	@Override
	public void setStripe(S stripe) {
		this.stripe = stripe;
	}

	
	public ITransitionItem getTransition() {
		return transition;
	}

	public void setTransition(ITransitionItem transition) {
		this.transition = transition;
	}
	
	@Override
	public String toString() {		
		return "::QUEUE ITEM:: ON: " + onTime + "\n" + "-->" + transition + "\n" + "-->" + stripe;
	}
	
	/*public ITransitionItem getInTransition() {
		return transition;
	}*/
		
}
