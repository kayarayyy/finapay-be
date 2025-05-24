package com.bcaf.finapay.utils;

import org.springframework.stereotype.Component;

@Component
public class LoanUtil {


    public static double calculateTotalInterest(double amount, double annualRate, int tenorInMonths) {
        double monthlyRate = annualRate / 12;
        return amount * monthlyRate * tenorInMonths;
    }

    public static double calculateTotalAdminFee(double amount, double adminRate) {
        
        return amount * adminRate;
    }


    public static double calculateMonthlyInstallment(double amount, double annualRate, int tenorInMonths) {
        double totalInterest = calculateTotalInterest(amount, annualRate, tenorInMonths);
        return (amount + totalInterest) / tenorInMonths;
    }
}