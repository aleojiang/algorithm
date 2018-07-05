package demo.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  09:17 2018/6/26.
 */
public class JdkDynamicProxy {
    
    interface IA {
        void m1();
    }
    
    interface IB {
        void m1();
    }
    
    
    static class Original implements IA, IB {
    
        @Override
        public void m1() {
        
        }
    }
    
    static class MyHandler implements InvocationHandler {
    
        private Object invocationTarget;
    
        public MyHandler(Object invocationTarget) {
            this.invocationTarget = invocationTarget;
        }
    
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // Start time
            long startTime = System.nanoTime();
    
            // Invoke the method on the target instance
            Object result = method.invoke(invocationTarget, args);
    
            // Print the execution time
            System.out.println("Executed method " + method.getName() + " in "
                    + (System.nanoTime() - startTime) + " nanoseconds");
    
            // Return the result to the caller
            return result;
        }
    }
    
    public static void main(String[] args) {
        
        Original original = new Original();
        MyHandler handler = new MyHandler(original);
        IA f = (IA) Proxy.newProxyInstance(IA.class.getClassLoader(),
                new Class[] { IA.class, IB.class },
                handler);
        f.m1();
        
    }
    
    

}
