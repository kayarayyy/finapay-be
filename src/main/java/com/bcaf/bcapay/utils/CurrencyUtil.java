package com.bcaf.bcapay.utils;

import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component
public class CurrencyUtil {

    public static String toRupiah(double amount) {
        Locale indoLocale = Locale.forLanguageTag("id-ID");
        NumberFormat rupiahFormat = NumberFormat.getCurrencyInstance(indoLocale);
        return rupiahFormat.format(amount);
    }
}