package jjj.atomic;

import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  14:06 17/04/2018.
 */
public class AtomicTest {
    private static final Logger LOG = Loggers.getLogger(AtomicTest.class);
    
    final static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference(0, 0);
    
    public static void main(String[] args) throws Exception {
        test_AtomicReferenceFieldUpdater();
        test_ABA_problem();
    }
    
    private static void test_ABA_problem() throws InterruptedException {
        
        final Integer value = atomicStampedReference.getReference();
        final Integer stamp = atomicStampedReference.getStamp();
        
        Thread t1 = new Thread(() -> {
            waitForSeconds(1);
            LOG.info("expected={}, new={}, op={}", atomicStampedReference.getReference(), 100,
                    atomicStampedReference.compareAndSet(0, 100, stamp, stamp ));
            atomicStampedReference.compareAndSet(atomicStampedReference.getReference(), 100, stamp, stamp+1);
            LOG.info("expected={}, new={}, op={}", atomicStampedReference.getReference(), 100,
                    atomicStampedReference.compareAndSet(atomicStampedReference.getReference(), 100, stamp, stamp+1 ));
        });
        
        Thread t2 = new Thread(() -> {
            waitForSeconds(2);
            atomicStampedReference.compareAndSet(value, value + 100, stamp, stamp + 1);
            LOG.info("expected={}, new={}, op={}", atomicStampedReference.getReference(), 100,
                    atomicStampedReference.compareAndSet(atomicStampedReference.getReference(), 100, stamp, stamp+1 ));
            atomicStampedReference.compareAndSet(value, value + 100, stamp, stamp + 1);
            LOG.info("expected={}, new={}, op={}", atomicStampedReference.getReference(), 100,
                    atomicStampedReference.compareAndSet(atomicStampedReference.getReference(), 100, stamp, stamp+1 ));
        });
        
        t1.start();
        t1.join();
        t2.start();
        t2.join();
        LOG.info("value={}", atomicStampedReference.getReference());
        
    }
    
    private static void waitForSeconds(long i) {
        try {
            TimeUnit.SECONDS.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private static void test_AtomicReferenceFieldUpdater() {
        AtomicReferenceFieldUpdater updater = AtomicReferenceFieldUpdater.newUpdater(Dog.class, String.class, "name");
        Dog dog1 = new Dog();
        updater.compareAndSet(dog1, dog1.name, "dog2");
        System.out.println(dog1.name);
    }
    
    static class Dog {
        volatile String name = "dog1";
    }
    
}
