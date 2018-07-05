package jjj.reactor;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;

/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  16:17 03/04/2018.
 */
public class SampleSubscriber<T> extends BaseSubscriber<T> {
    
    public void hookOnSubscribe(Subscription subscription) {
        subscription.request(1);
    }
    
    public void hookOnNext(T value) {
        super.hookOnNext(value);
        String v = (String) value;
        if (v.equalsIgnoreCase("red")) {
            request(2);
        }
        
    }
    
    
}
