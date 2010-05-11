package ch.origamiaddict.stripecontrol.queue;

public class TransitionItem implements ITransitionItem {
	
	private long fadeInTime		= 0;
	private long fadeOutTime	= 0;
	private long transitionTime	= 0;
	
	public TransitionItem() {
		super();
	}
	
	public TransitionItem(long fadeIn, long transition, long fadeOut) {
		super();
		fadeInTime 		= fadeIn;
		fadeOutTime 	= fadeOut;
		transitionTime 	= transition;
	}
	
	
	public long getFadeInTime() {
		return fadeInTime;
	}
	
	public void setFadeInTime(long milliseconds) {
		this.fadeInTime = milliseconds;
	}
	
	public long getFadeOutTime() {
		return fadeOutTime;
	}
	
	public void setFadeOutTime(long milliseconds) {
		this.fadeOutTime = milliseconds;
	}
	
	public long getTransitionTime() {
		return transitionTime;
	}
	
	public void setTransitionTime(long milliseconds) {
		this.transitionTime = milliseconds;
	}
	
	@Override
	public String toString() {
		return "::TRANSITION:: IN: "+ fadeInTime +" TRANS: " + transitionTime + " OUT: " + fadeOutTime;
	}
}
