package template;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 常用的一些小代码
 */
public class Utils {

    public int  gcd2(int a,int b){
        return (a==0?b:gcd2(b%a,a));
    }


    /**
     * 线性欧拉筛
     */
    public void findPrime(int n) {
        boolean[] isPrime = new boolean[n + 1];
        isPrime[0] = isPrime[1] = true;
        int[] Prime = new int[n + 1];
        int t = 0;
        Prime[t++] = 2;
        for (int i = 2; i <= n; i++) {
            if (!isPrime[i])
                Prime[t++] = i;
            for (int j = 0; j < t && Prime[j] * i <= n; j++) {
                isPrime[i * Prime[j]] = true;
                if (i % Prime[j] == 0)
                    break;
            }
        }
    }
    /**
     * 快速幂，计算a的m次方在模mod
     */
    public long pow(int a, int m, int mod) {
        long ans = 1;
        long contribute = a;
        while (m > 0) {
            if (m % 2 == 1) {
                ans = ans * contribute % mod;
                if (ans < 0) {
                    ans += mod;
                }
            }
            contribute = contribute * contribute % mod;
            if (contribute < 0) {
                contribute += mod;
            }
            m /= 2;
        }
        return ans;
    }
    // 计算组合数
    public static BigInteger combination(int m, int n) {
        if (m < 0 || n < 0 || m < n) {
            throw new IllegalArgumentException("Invalid input");
        }

        BigInteger numerator = BigInteger.ONE;
        BigInteger denominator = BigInteger.ONE;

        for (int i = 0; i < n; i++) {
            numerator = numerator.multiply(BigInteger.valueOf(m - i));
            denominator = denominator.multiply(BigInteger.valueOf(i + 1));
        }

        return numerator.divide(denominator);
    }

    // 计算排列数
    public static BigInteger permutation(int m, int n) {
        if (m < 0 || n < 0 || m < n) {
            throw new IllegalArgumentException("Invalid input");
        }

        BigInteger result = BigInteger.ONE;

        for (int i = 0; i < n; i++) {
            result = result.multiply(BigInteger.valueOf(m - i));
        }

        return result;
    }

}
