package ch.origamiaddict.stripecontrol.queue;

import java.util.ArrayList;
import java.util.Collection;

import ch.origamiaddict.stripecontrol.stripe.IStripe;

public class StripeQueue<S extends IStripe> {

	private ArrayList<IQueueItem<S>> queue;

	private IQueueItem<S> currItem;
	private IQueueItem<S> nextItem;
	private IQueueItem<S> prevItem;

	private boolean loopQueue;

	private int currIdx;

	public StripeQueue() {
		super();
		reset();
	}
	
	public void forceFadeInTimeForAll(long milliseconds) {
		for(IQueueItem<S> i : queue) {
			i.getTransition().setFadeInTime(milliseconds);
		}
	} 
	public void forceFadeOutTimeForAll(long milliseconds) {
		for(IQueueItem<S> i : queue) {
			i.getTransition().setFadeOutTime(milliseconds);
		}
		
	}
	public void forceTransitionTimeForAll(long milliseconds) {
		for(IQueueItem<S> i : queue) {
			i.getTransition().setTransitionTime(milliseconds);
		}
	}
	public void forceOnTimeForAll(long milliseconds) {
		for(IQueueItem<S> i : queue) {
			i.setOnTime(milliseconds);
		}
	}

	public void addItem(IQueueItem<S> i) {
		queue.add(i);
	}

	@Deprecated
	public void addItem() {
		try{
			throw new Exception("No longer supported!");
		} catch (Exception e) {
			e.getMessage();
		}		
		//queue.add(getDefaultCueItem());
	}
	
	public void addItems(Collection<IQueueItem<S>> items) {
		queue.addAll(items);
	}

	public IQueueItem<S> getItem(int index) {
		return queue.get(index);
	}

	public boolean isLastItemInCue() {
		if (!isLoop()) {
			if (hasNext()) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	private boolean hasNext() {
		if (currIdx + 1 < queue.size()) {
			return true;
		} else {
			return false;
		}
	}

	private void setPreviousByCurrentIndex(int currentIndex) {
		if (currentIndex == 0) {
			if (isLoop()) {
				// get last
				prevItem = queue.get(queue.size() - 1);
			} else {
				prevItem = null;
			}
		} else {
			prevItem = queue.get(currentIndex - 1);
		}
	}

	private void setNextByCurrentIndex(int currentIndex) {
		if (hasNext()) {
			nextItem = queue.get(currentIndex + 1);
		} else {
			if (isLoop()) {
				nextItem = queue.get(0);
			} else {
				nextItem = null;
			}
		}
	}

	public IQueueItem<S> next() {

		if (queue.isEmpty())
			return null;

		// index management
		// reset index if loop, else just return null
		if (hasNext()) {
			currIdx++;
		} else if (isLoop()) {
			//no next item and loop queue 
			currIdx = 0;
		} else {
			//no next item and no loop queue
			currIdx = 0;
			nextItem = null;
			currItem = null;
			return currItem;
		}

		currItem = queue.get(currIdx);
		
		// set previous item
		setPreviousByCurrentIndex(currIdx);

		// set next item
		setNextByCurrentIndex(currIdx);
		
		return currItem;
	}

	public int getSize() {
		return queue.size();
	}

	public IQueueItem<S> getCurrent() {
		return currItem;
	}

	public IQueueItem<S> getNext() {
		return nextItem;
	}

	public IQueueItem<S> getPrevious() {
		return prevItem;
	}

	public boolean isLoop() {
		return loopQueue;
	}

	public void setLoop(boolean loop) {
		this.loopQueue = loop;
	}

	public void setCurrentTo(int index) {
		if (index > queue.size() - 1 || index < 0)
			//wrong index value
			return;

		currIdx = index;
		currItem = queue.get(index);

		setPreviousByCurrentIndex(index);
		setNextByCurrentIndex(index);
	}

	public void reset() {
		queue = new ArrayList<IQueueItem<S>>();
		currIdx = -1;
		currItem = null;
		nextItem = null;
		prevItem = null;
		loopQueue = false;
	}

}
