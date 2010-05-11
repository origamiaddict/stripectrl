package ch.origamiaddict.stripecontrol.queue;

public interface ITransitionItem {

	public long getFadeInTime();

	public void setFadeInTime(long milliseconds);

	public long getFadeOutTime();

	public void setFadeOutTime(long milliseconds);

	public long getTransitionTime();

	public void setTransitionTime(long milliseconds);

}