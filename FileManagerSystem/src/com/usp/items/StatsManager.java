package com.usp.items;

import java.util.*;

public class StatsManager {
    private static final StatsManager INSTANCE = new StatsManager();
    private final List<StatisticRecord> records = new ArrayList<>();

    private StatsManager() {}

    public static StatsManager getInstance() {
        return INSTANCE;
    }

    public void addRecord(StatisticRecord rec) {
        records.add(rec);
    }

    public List<StatisticRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }
}

