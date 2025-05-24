package com.bcaf.finapay.utils;

import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class CurrencyUtil {

    public static String toRupiah(double amount) {
        Locale indoLocale = new Locale("id", "ID");
        NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(indoLocale);
        rupiahFormat.setMaximumFractionDigits(0); // Hilangkan ,00
        return rupiahFormat.format(amount);
    }
}
