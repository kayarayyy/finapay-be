package com.bcaf.bcapay.utils;

import org.springframework.stereotype.Component;

@Component
public class LoanUtil {


    public static double calculateTotalInterest(double amount, double annualRate, int tenorInMonths) {
        double monthlyRate = annualRate / 12;
        return amount * monthlyRate * tenorInMonths;
    }


    public static double calculateMonthlyInstallment(double amount, double annualRate, int tenorInMonths) {
        double totalInterest = calculateTotalInterest(amount, annualRate, tenorInMonths);
        return (amount + totalInterest) / tenorInMonths;
    }
}