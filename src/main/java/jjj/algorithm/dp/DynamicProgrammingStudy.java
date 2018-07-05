package jjj.algorithm.dp;

/**
 * Overlapping Subproblems and Optimal Substructure properties
 * <p>
 * <p>
 * <p>
 * Created by: patrick.jiang@activenetwork.com
 * Created on:  09:15 29/03/2018.
 */
public class DynamicProgrammingStudy {
    
    private static void log(String msg) {
        System.out.println(msg);
    }
    
    private static void log(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + ",");
        }
        System.out.println("");
    }
    
    private static void log(int[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j] + ",");
            }
            System.out.println("");
        }
        System.out.println("");
    }
    
    public static void main(String[] args) {
        //        String Y = "AGGTAB";
        //        String X = "GXTXAYB";
        //        int n = lcs_dp_space_optimized(X, Y);
        //        log("Length of LCS is " + n);
        
//        int cost[][] = { { 1, 2, 3 },
//                { 4, 8, 2 },
//                { 1, 5, 3 }
//        };
//        mcp_dp(cost, 2, 2);
    
        int[] s = { 70, 55, 13, 2, 99, 2, 80, 80, 80, 80, 100, 19, 7, 5, 5, 5, 1000, 32, 32 };
    
        longestZigZag(s);
    }
    
    /**
     * Optimal substructure
     * <p>
     * The Longest Increasing Subsequence (LIS) problem is to find the length of
     * the longest subsequence of a given sequence such that all elements of the
     * subsequence are sorted in increasing order.
     * <p>
     * Solution:
     * Let arr[0..n-1] be the input array and L(i) be the length of the LIS ending at index i such that arr[i] is the
     * last element of the LIS.
     * Then, L(i) can be recursively written as:
     * L(i) = 1 + max( L(j) ) where 0 < j < i and arr[j] < arr[i]; or
     * L(i) = 1, if no such j exists.
     * To find the LIS for a given array, we need to return max(L(i)) where 0 < i < n.
     * Thus, we see the LIS problem satisfies the optimal substructure property as the main problem can be solved
     * using solutions to subproblems.
     * <p>
     * Following is a simple recursive implementation of the LIS problem. It follows the recursive structure
     * discussed above.
     * <p>
     * Time complexity: O(n^2)
     */
    static int max_ref = 0;
    
    public static int _lis(int[] arr, int length) {
        if (length == 1)
            return length;
        
        // 'max_ending_here' is length of LIS ending with arr[n-1]
        int res, max_ending_here = 1;
        
        /**
         *  Recursively get all LIS ending with arr[0], arr[1] ... arr[n-2].
         *  If arr[i-1] is smaller than arr[n-1], and max ending with arr[n-1] needs to be updated, then update it
         */
        for (int i = 1; i < length; i++) {
            res = _lis(arr, i);
            if (arr[i - 1] < arr[length - 1] && res + 1 > max_ending_here)
                max_ending_here = res + 1;
        }
        
        // Compare max_ending_here with the overall max. And
        // update the overall max if needed
        if (max_ref < max_ending_here)
            max_ref = max_ending_here;
        
        // Return length of LIS ending with arr[n-1]
        return max_ending_here;
    }
    
    public static int lis2(int arr[], int n) {
        int lis[] = new int[n];
        int i, j, max = 0;
        
        /* Initialize LIS values for all indexes */
        for (i = 0; i < n; i++)
            lis[i] = 1;
        
        /* Compute optimized LIS values in bottom up manner */
        for (i = 1; i < n; i++)
            for (j = 0; j < i; j++)
                if (arr[i] > arr[j] && lis[i] < lis[j] + 1)
                    lis[i] = lis[j] + 1;
        
        /* Pick maximum of all LIS values */
        for (i = 0; i < n; i++)
            if (max < lis[i])
                max = lis[i];
        
        return max;
    }
    
    /**
     * LCS Problem Statement: Given two sequences, find the length of longest subsequence present in both of them.
     * A subsequence is a sequence that appears in the same relative order, but not necessarily contiguous.
     * For example, “abc”, “abg”, “bdf”, “aeg”, ‘”acefg”, .. etc are subsequences of “abcdefg”.
     * So a string of length n has 2^n different possible subsequences.
     * *
     */
    // naive recursion with time complexity O(n^2)
    private static int lcs_naive(char[] a, char[] b, int la, int lb) {
        // from last char
        if (la == 0 || lb == 0)
            return 0;
        if (a[la - 1] == b[lb - 1]) {
            return 1 + lcs_naive(a, b, la - 1, lb - 1);
        } else {
            return max(lcs_naive(a, b, la, lb - 1),
                    lcs_naive(a, b, la - 1, lb));
        }
    }
    
    // Dynamic Programming Java implementation of LCS problem with time complexity O(m*n)
    private static int lcs_dp(char[] X, char[] Y, int m, int n) {
        int L[][] = new int[m + 1][n + 1];
        //Following steps build L[m+1][n+1] in bottom up fashion. Note
        // that L[i][j] contains length of LCS of X[0..i-1] and Y[0..j-1] */
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 || j == 0)
                    L[i][j] = 0;
                else if (X[i - 1] == Y[j - 1])
                    L[i][j] = L[i - 1][j - 1] + 1;
                else
                    L[i][j] = max(L[i - 1][j], L[i][j - 1]);
            }
        }
        return L[m][n];
    }
    
    // A Space Optimized Solution of LCS
    private static int lcs_dp_space_optimized(String a, String b) {
        log("(" + a + ")-(" + b + ")");
        StringBuffer sb = new StringBuffer();
        int L[][] = new int[2][b.length() + 1];
        // Binary index, used to index current row and previous row.
        int bi = 0;
        for (int i = 0; i <= a.length(); i++) {
            bi = i & 1;
            log("---" + i + "---" + (i > 0 ? a.charAt(i - 1) : ""));
            for (int j = 0; j <= b.length(); j++) {
                if (i == 0 || j == 0) {
                    L[bi][j] = 0;
                } else if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    L[bi][j] = L[1 - bi][j - 1] + 1;
                } else {
                    L[bi][j] = Math.max(L[1 - bi][j], L[bi][j - 1]);
                }
            }
            log(L[bi]);
            log(L[1 - bi]);
        }
        
        int i = a.length();
        int j = b.length();
        while (i > 0 && j > 0) {
            bi = i & 1;
            if (a.charAt(i - 1) == b.charAt(j - 1)) {
                sb.append(a.charAt(i - 1));
                i--;
                j--;
            } else if (L[bi][j] > L[1 - bi][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        log(sb.reverse().toString());
        return Math.max(L[bi][b.length()], L[1 - bi][b.length()]);
    }
    
    private static int max(int a, int b) {
        return Math.max(a, b);
    }
    
    /**
     * Given two strings str1 and str2 and below operations that can performed on str1.
     * Find minimum number of edits (operations) required to convert ‘str1’ into ‘str2’.
     * - Insert
     * - Remove
     * - Replace
     * All of the above operations are of equal cost.
     * Time complexity: O(3m)
     */
    private static int editDist(String s1, String s2, int m, int n) {
        // If first string is empty, the only option is to
        // insert all characters of second string into first
        if (m == 0)
            return n;
        
        // If second string is empty, the only option is to
        // remove all characters of first string
        if (n == 0)
            return m;
        
        if (s1.charAt(m - 1) == s2.charAt(n - 1)) {
            return editDist(s1, s2, m - 1, n - 1);
        }
        
        return 1 + min(
                editDist(s1, s2, m - 1, n - 1), // Replace
                editDist(s1, s2, m, n - 1),  // Insert
                editDist(s1, s2, m - 1, n)   // Remove
                      );
    }
    
    private static int editDistDP(String str1, String str2, int m, int n) {
        // Create a table to store results of subproblems
        int dp[][] = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                // If first string is empty, only option is to insert all characters of second string
                if (i == 0) {
                    dp[i][j] = j;  // Min. operations = j
                }
                // If second string is empty, only option is to remove all characters of second string
                else if (j == 0) {
                    dp[i][j] = i; // Min. operations = i
                }
                // If last characters are same, ignore last char
                // and recur for remaining string
                else if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
                // If last character are different, consider all possibilities and find minimum
                else {
                    dp[i][j] = 1 + min(dp[i][j - 1],  // Insert
                            dp[i - 1][j],  // Remove
                            dp[i - 1][j - 1]); // Replace
                }
            }
        }
        return dp[m][n];
    }
    
    private static int min(int x, int y, int z) {
        if (x <= y && x <= z)
            return x;
        if (y <= x && y <= z)
            return y;
        else
            return z;
    }
    
    /**
     * Given a cost matrix cost[][] and a position (m, n) in cost[][],
     * write a function that returns cost of minimum cost path to reach (m, n) from (0, 0).
     * Each cell of the matrix represents a cost to traverse through that cell.
     * Total cost of a path to reach (m, n) is sum of all the costs on that path (including both source and
     * destination).
     * You can only traverse down, right and diagonally lower cells from a given cell,
     * i.e., from a given cell (i, j), cells (i+1, j), (i, j+1) and (i+1, j+1) can be traversed.
     * You may assume that all costs are positive integers.
     */
    
    private static int mcp_naive(int[][] costs, int m, int n) {
        if (m == 0 && n == 0) {
            return costs[m][n];
        }
        return costs[m][n] + min(
                mcp_naive(costs, m, n - 1),
                mcp_naive(costs, m - 1, n),
                mcp_naive(costs, m - 1, n - 1));
    }
    
    // overlapping
    // optimal substructure
    private static int mcp_dp(int[][] cost, int m, int n) {
        log(cost);
        // create a table to store
        int i, j;
        int[][] tc = new int[m + 1][n + 1];
        tc[0][0] = cost[0][0];
        
        /* Initialize first column of total cost(tc) array */
        for (i = 1; i <= m; i++) {
            tc[i][0] = tc[i - 1][0] + cost[i][0];
        }
        /* Initialize first row of tc array */
        for (j = 1; j <= n; j++) {
            tc[0][j] = tc[0][j - 1] + cost[0][j];
        }
        
        log(tc);
        
        /* Construct rest of the tc array */
        for (i = 1; i <= m; i++)
            for (j = 1; j <= n; j++)
                tc[i][j] = min(tc[i - 1][j - 1],
                        tc[i - 1][j],
                        tc[i][j - 1]) + cost[i][j];
        
        log(tc);
        
        return tc[m][n];
    }
    
    /**
     * Optimal Substructure
     * To count total number solutions, we can divide all set solutions in two sets.
     * 1) Solutions that do not contain mth coin (or Sm).
     * 2) Solutions that contain at least one Sm.
     * Let count(S[], m, n) be the function to count the number of solutions, then it can be written as sum of count
     * (S[], m-1, n) and count(S[], m, n-Sm).
     * Therefore, the problem has optimal substructure property as the problem can be solved using solutions to
     * subproblems.
     */
    
    /**
     * @param S
     * @param m: number of available changes
     * @param n: total bill
     */
    private static int count_coinchange_naive(int[] S, int m, int n) {
        // If n is 0 then there is 1 solution (do not include any coin)
        if (n == 0)
            return 1;
        
        // If n is less than 0 then no solution exists
        if (n < 0)
            return 0;
        
        // If there are no coins and n is greater than 0, then no solution exist
        if (m <= 0)
            return 0;
        
        return count_coinchange_naive(S, m - 1, n) + count_coinchange_naive(S, m, n - S[m - 1]);
    }
    
    private static int count_coinchange_dp(int[] S, int m, int n) {
        int[][] table = new int[n + 1][m];
        for (int i = 0; i < n + 1; i++) {
            for (int j = 0; j < m; j++) {
                if (i == 0)
                    table[i][j] = 1;
                else if (j == 0) {
                    if (i % S[j] == 0)
                        table[i][j] = 1;
                    else
                        table[i][j] = 0;
                } else if (S[j] > i)
                    table[i][j] = table[i][j - 1];
                else
                    table[i][j] = table[i - S[j]][j] + table[i][j - 1];
            }
        }
        return table[n][m - 1];
    }
    
    /**
     * Knapsack problem
     * <p>
     * Given weights and values of n items, put these items in a knapsack of capacity W to get the maximum total
     * value in the knapsack.
     * In other words, given two integer arrays val[0..n-1] and wt[0..n-1] which represent values and weights
     * associated with n items respectively.
     * Also given an integer W which represents knapsack capacity, find out the maximum value subset of val[] such
     * that sum of the weights of this
     * subset is smaller than or equal to W. You cannot break an item, either pick the complete item, or don’t pick
     * it (0-1 property).
     *
     * @param volume
     * @param cargoes
     * @return
     */
    public static int backPack(int volume, int[] cargoes) {
        if (cargoes == null || cargoes.length == 0 || volume == 0)
            return 0;
        
        int len = cargoes.length;
        // state for each step
        int[][] state = new int[len][volume + 1];
        for (int i = 0; i < len; i++) {
            state[i][0] = 0;
        }
        //
        for (int j = 0; j < volume + 1; j++) {
            if (j >= cargoes[0]) {
                state[0][j] = cargoes[0];
            }
        }
        //
        for (int i = 1; i < len; i++) {
            for (int j = 1; j < volume + 1; j++) {
                if (j >= cargoes[i]) {
                    state[i][j] = Math.max(state[i - 1][j], state[i - 1][j - cargoes[i]] + cargoes[i]);
                } else {
                    state[i][j] = state[i - 1][j];
                }
            }
        }
        return state[len - 1][volume];
        
    }
    
    public static int minimumTotal(int[][] triangle) {
        if (triangle == null || triangle.length == 0)
            return 0;
        int len = triangle.length;
        // state to keep each step
        int[][] cost = new int[len][len];
        cost[0][0] = triangle[0][0];
        for (int i = 1; i < len; i++) {
            for (int j = 0; j < triangle[i].length; j++) {
                // in case of out of boundary
                int lower = Math.max(0, j - 1);
                int upper = Math.min(j, triangle[i - 1].length - 1);
                // state transform function
                cost[i][j] = Math.min(cost[i - 1][lower], cost[i - 1][upper]) + triangle[i][j];
            }
        }
        int minCost = Integer.MAX_VALUE;
        for (int k = 0; k < triangle[len - 1].length; k++) {
            minCost = Math.min(minCost, cost[len - 1][k]);
        }
        return minCost;
    }
    
    /**
     * zigzag sequence is that the differences of sibling element are alternately positive and negative
     *
     * @param arr
     * @return
     */
    private static int longestZigZag(int[] arr) {
        int m = arr.length;
        int[][] las = new int[m][2];
        
        for(int i=0; i<m; i++) {
            for(int j=0; i<i; j++) {
                las[i][0] = Math.max(las[i][0], las[j][1] + 1);
            }
        }
        return 0;
    }
}
