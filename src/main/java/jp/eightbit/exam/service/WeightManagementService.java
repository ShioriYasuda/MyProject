package jp.eightbit.exam.service;

import jp.eightbit.exam.entity.WeightRecord;
import java.util.ArrayList;
import java.util.List;

public class WeightManagementService {
    private List<WeightRecord> weightRecords;

    public WeightManagementService() {
        weightRecords = new ArrayList<>();
    }

    public void addWeightRecord(WeightRecord record) {
        weightRecords.add(record);
    }

    public List<WeightRecord> getWeightRecords() {
        return weightRecords;
    }
}
