package jjj.work.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  13:15 19/04/2018.
 */
public class ParseCountry {
    
    public static void main(String[] args) {
        String applicationNamesAso = "timeto,";
        boolean isASO = Arrays.stream(applicationNamesAso.split(",", -1))
                .filter(a -> a != null)
                .anyMatch(a -> a.equalsIgnoreCase("timeto"));
        
        List<Long> eventsList = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        Long discountId = 12L;
        String discountType = "aaa";
        
        String x = eventsList.stream()
                .map(p -> String.format("(%d,'%s',%d)", discountId, discountType, p))
                .collect(Collectors.joining(","));
        
        System.out.println(x);
    }
}
