package ch.origamiaddict.stripecontrol.factories;

import java.awt.Color;
import java.util.Random;

import ch.origamiaddict.stripecontrol.queue.IQueueItem;
import ch.origamiaddict.stripecontrol.queue.QueueItem;
import ch.origamiaddict.stripecontrol.stripe.IStripe;
import ch.origamiaddict.stripecontrol.stripe.Stripe;

public class QueueItemFactory<S extends IStripe> {

	public enum EItemConfigValue {
		TRANS, FADE_IN, FADE_OUT, STAY_ON, CHANNELS, VALUES;
	}

	private static class TransitionDefaults {
		static final long TRANSITION_TIME = 3000;
		static final long FADE_IN_TIME = 1500;
		static final long FADE_OUT_TIME = 1500;
	}

	private static class QueueItemDefaults {
		static final long STAY_ON_TIME = 2000;
	}

	private static class RgbStripeDefaults {
		static final int[] RGB_CHANNELS = { 1, 2, 3 };
	}

	/*@SuppressWarnings("unchecked")
	public S createStripe() {
		Type actualTypeArgument = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Class<?> actualRawTypeArgument = null;
		if (actualTypeArgument instanceof ParameterizedType) {
			actualRawTypeArgument = (Class<?>) ((ParameterizedType) actualTypeArgument).getRawType();
		} else {
			actualRawTypeArgument = (Class<?>) actualTypeArgument;
		}
		try {
			return (S) actualRawTypeArgument.newInstance();
		} catch (Exception e) {
			return null;
		}
	}*/

	// TODO: generic problem to solve new Stripe() isn't generic enough
	@SuppressWarnings("unchecked")
	public IQueueItem<S> createEmptyQueueItem() {
		IQueueItem<S> q = new QueueItem<S>();
		q.setStripe((S) new Stripe());
		return q;
	}
	
	@SuppressWarnings("unchecked")
	public IQueueItem<S> createDefaultItem() {
		IQueueItem<S> q = new QueueItem<S>();
		q.setOnTime(QueueItemDefaults.STAY_ON_TIME);
		q.getTransition().setFadeInTime(TransitionDefaults.FADE_IN_TIME);
		q.getTransition().setFadeOutTime(TransitionDefaults.FADE_OUT_TIME);
		q.getTransition().setTransitionTime(TransitionDefaults.TRANSITION_TIME);
		q.setStripe((S) new Stripe());
		q.getStripe().setChannels(RgbStripeDefaults.RGB_CHANNELS);
		return q;
	}

	public IQueueItem<S> createDefaultItem(Color c) {
		IQueueItem<S> q = createDefaultItem();
		((IStripe) q.getStripe()).setColor(c);
		return q;
	}

	public IQueueItem<S> createCustomRgbItem(Color c, long in, long on,
			long out, long trans) {
		IQueueItem<S> q = createDefaultItem(c);
		q.setOnTime(on);
		q.getTransition().setFadeInTime(in);
		q.getTransition().setFadeOutTime(out);
		q.getTransition().setTransitionTime(trans);
		return q;
	}

	public IQueueItem<S> createRandomColorRgbItem() {
		Random rand = new Random();
		return createDefaultItem(new Color(rand.nextInt(256), rand
				.nextInt(256), rand.nextInt(256)));
	}

	public IQueueItem<S> modifyItem(IQueueItem<S> item,
			EItemConfigValue config, int... newValue) {
		IQueueItem<S> i = item;
		switch (config) {
		case TRANS:
			i.getTransition().setTransitionTime(newValue[0]);
			break;
		case FADE_IN:
			i.getTransition().setFadeInTime(newValue[0]);
			break;
		case FADE_OUT:
			i.getTransition().setFadeOutTime(newValue[0]);
			break;
		case STAY_ON:
			i.setOnTime(newValue[0]);
			break;
		case CHANNELS:
			i.getStripe().setChannels(newValue);
			break;
		case VALUES:
			i.getStripe().setValues(newValue);
			break;
		}
		return i;
	}

}
