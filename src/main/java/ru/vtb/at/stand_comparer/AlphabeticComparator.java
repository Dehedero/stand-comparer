package ru.vtb.at.stand_comparer;

import java.util.Comparator;

public class AlphabeticComparator implements Comparator<CompareData> {
    @Override
    public int compare(CompareData o1, CompareData o2) {
        return o1.service.compareTo(o2.service);
    }
}
