package jjj.lock.lock;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  10:54 08/04/2018.
 */
public class SpinLocks {
    private AtomicReference<Thread> owner = new AtomicReference<>();
    
    public void simpleSpinLock() {
        Thread currentThread = Thread.currentThread();
        // if the owner has no thread  then set current thread to it.
        while(owner.compareAndSet(null, currentThread)) {}
    }
    
    public void simpleSpinUnlock() {
        Thread currentThread = Thread.currentThread();
        // set the owner to initial null thread only when current thread has own the simpleSpinLock
        owner.compareAndSet(currentThread, null);
    }
    
    private AtomicInteger serviceNumber = new AtomicInteger();
    private AtomicInteger tickNumber = new AtomicInteger();
    
    //
    public int tickSpinLock() {
        // get the tick number
        int myTicketNum = tickNumber.getAndIncrement();
        // wait until serviceNumber is the same as
        while (serviceNumber.get() != myTicketNum) {
        }
        return myTicketNum;
    }
    
    // only the locker owner can release the locker
    public void tickSpinUnLock(int myTicket) {
        int next = myTicket + 1;
        serviceNumber.compareAndSet(myTicket, next);
    }
    
    //
    
    public static class MCSNode {
        volatile MCSNode next;
        volatile boolean isBlock = true; // 默认是在等待锁
    }
    
    volatile MCSNode queue;// 指向最后一个申请锁的MCSNode
    private static final AtomicReferenceFieldUpdater<SpinLocks, MCSNode> mcsUpdater = AtomicReferenceFieldUpdater
            .newUpdater(SpinLocks.class, MCSNode.class, "queue");
    
    public void lock(MCSNode currentThread) {
        MCSNode predecessor = mcsUpdater.getAndSet(this, currentThread); // step 1
        if (predecessor != null) {
            predecessor.next = currentThread;// step 2
            
            while (currentThread.isBlock) {// step 3
            }
        }
    }
    
    public void unlock(MCSNode currentThread) {
        if (currentThread.isBlock) {// 锁拥有者进行释放锁才有意义
            return;
        }
        
        if (currentThread.next == null) {// 检查是否有人排在自己后面
            if (mcsUpdater.compareAndSet(this, currentThread, null)) {// step 4
                // compareAndSet返回true表示确实没有人排在自己后面
                return;
            } else {
                // 突然有人排在自己后面了，可能还不知道是谁，下面是等待后续者
                // 这里之所以要忙等是因为：step 1执行完后，step 2可能还没执行完
                while (currentThread.next == null) { // step 5
                }
            }
        }
        
        currentThread.next.isBlock = false;
        currentThread.next = null;// for GC
    }
    
    public static class CLHNode {
        private boolean isLocked = true; // 默认是在等待锁
    }
    
    @SuppressWarnings("unused" )
    private volatile CLHNode tail ;
    private static final AtomicReferenceFieldUpdater<SpinLocks, CLHNode> clhUpdater = AtomicReferenceFieldUpdater
            . newUpdater(SpinLocks.class, CLHNode .class , "tail" );
    
    public void lock(CLHNode currentThread) {
        CLHNode preNode = clhUpdater.getAndSet( this, currentThread);
        if(preNode != null) {//已有线程占用了锁，进入自旋
            while(preNode.isLocked ) {
            }
        }
    }
    
    public void unlock(CLHNode currentThread) {
        // 如果队列里只有当前线程，则释放对当前线程的引用（for GC）。
        if (!clhUpdater.compareAndSet(this, currentThread, null)) {
            // 还有后续线程
            currentThread.isLocked = false ;// 改变状态，让后续线程结束自旋
        }
    }
    
}
