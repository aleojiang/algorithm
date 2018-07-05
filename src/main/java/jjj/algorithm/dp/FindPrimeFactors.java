package jjj.algorithm.dp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  15:18 26/03/2018.
 */
public class FindPrimeFactors {
    
    public static List<Double> primeFactors(int n)
    {
        List<Double> list = new ArrayList<>();
        
        // Print the number of 2s that divide n
        while (n%2==0)
        {
            list.add(2.0);
            n /= 2;
        }
        
        // n must be odd at this point.  So we can
        // skip one element (Note i = i +2)
        for (int i = 3; i <= Math.sqrt(n); i+= 2)
        {
            // While i divides n, print i and divide n
            while (n%i == 0)
            {
                list.add((double) i);
                n /= i;
            }
        }
        
        // This condition is to handle the case when
        // n is a prime number greater than 2
        if (n > 2)
            list.add((double) n);
        
        return list;
    }
    
    
    
    public static void main(String[] args) {
    
        List<Double> list = primeFactors(123);
        
        list.forEach(System.out::println);
        
        Integer init = nextPrime(0);
        Integer n = 1;
        for (int i=0; i<list.size(); i++) {
            n = Double.valueOf(Math.pow(init,list.get(i))).intValue();
            init = nextPrime(init);
        }
        
        System.out.println(n);
    
    }
    
    private static Integer nextPrime(Integer current) {
        while(!isPrime(current+1)) {
            current += 1;
        }
        return current;
    }
    
    // It returns false if n is composite and returns true if n is probably prime.
    // k is an input parameter that determines accuracy level. Higher value of k indicates more accuracy.
    static boolean isPrime(int n) {
        // Corner cases
        if (n <= 1 || n == 4)
            return false;
        if (n <= 3)
            return true;
    
        // Find r such that n = 2^d * r + 1
        // for some r >= 1
        int d = n - 1;
    
        while (d % 2 == 0)
            d /= 2;
    
        // Iterate given nber of 'k' times
        for (int i = 0; i < 4; i++)
            if (miillerTest(d, n) == false)
                return false;
    
        return true;
    }
    
    // This function is called for all k trials.
    // It returns false if n is composite and
    // returns false if n is probably prime.
    // d is an odd number such that d*2<sup>r</sup>
    // = n-1 for some r >= 1
    static boolean miillerTest(int d, int n) {
        
        // Pick a random number in [2..n-2]
        // Corner cases make sure that n > 4
        int a = 2 + (int)(Math.random() % (n - 4));
        
        // Compute a^d % n
        int x = power(a, d, n);
        
        if (x == 1 || x == n - 1)
            return true;
        
        // Keep squaring x while one of the
        // following doesn't happen
        // (i) d does not reach n-1
        // (ii) (x^2) % n is not 1
        // (iii) (x^2) % n is not n-1
        while (d != n - 1) {
            x = (x * x) % n;
            d *= 2;
            
            if (x == 1)
                return false;
            if (x == n - 1)
                return true;
        }
        
        // Return composite
        return false;
    }
    
    // Utility function to do modular
    // exponentiation. It returns (x^y) % p
    static int power(int x, int y, int p) {
        
        int res = 1; // Initialize result
        
        //Update x if it is more than or
        // equal to p
        x = x % p;
        
        while (y > 0) {
            
            // If y is odd, multiply x with result
            if ((y & 1) == 1)
                res = (res * x) % p;
            
            // y must be even now
            y = y >> 1; // y = y/2
            x = (x * x) % p;
        }
        
        return res;
    }
    
}
