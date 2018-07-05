package jjj.reactor;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  09:13 04/04/2018.
 */
public class NoOnTextRequestSubscriber<T> extends BaseSubscriber<T> {
    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        subscription.request(10);
    }
    
    @Override
    protected void hookOnNext(T value) {
    }
}
