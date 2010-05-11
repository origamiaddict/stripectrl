package ch.origamiaddict.stripecontrol.queue;

import ch.origamiaddict.stripecontrol.stripe.IStripe;

public interface IQueueItem<S extends IStripe> {
	public void setStripe(S stripe);

	public void setOnTime(long milliseconds);

	public S getStripe();

	public long getOnTime();

	public ITransitionItem getTransition();

	public void setTransition(ITransitionItem transition);
}