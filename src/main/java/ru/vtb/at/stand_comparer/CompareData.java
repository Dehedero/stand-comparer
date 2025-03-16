package ru.vtb.at.stand_comparer;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class CompareData {

    public static String NA = "N/A";

    public String service, serviceAVersion, serviceBVersion;
    public String comparison;

    public CompareData(String service, String serviceAVersion, String serviceBVersion, String comparison) {
        this.service = service;
        this.serviceAVersion = serviceAVersion;
        this.serviceBVersion = serviceBVersion;
        this.comparison = comparison;
    }

    public String getFormatted(int leftWordPaddingMaxLen) {
        String leftWord = service.trim() + "_" + serviceAVersion.trim();
        if (leftWord.length() < leftWordPaddingMaxLen)
            leftWord = StringUtils.rightPad(leftWord, leftWordPaddingMaxLen);
        return leftWord + "   " + comparison + "   " + service + "_" + serviceBVersion + System.lineSeparator();
    }

    public String getBVersion() {
        return service + "_" + serviceBVersion + System.lineSeparator();
    }

    public String getAVersion() {
        return service + "_" + serviceAVersion + System.lineSeparator();
    }

    public String getMajor() {
        if ((serviceAVersion.contains(NA) || comparison.equals("<")) && !serviceBVersion.contains(NA))
            return getBVersion();
        else
            return getAVersion();
    }

}
