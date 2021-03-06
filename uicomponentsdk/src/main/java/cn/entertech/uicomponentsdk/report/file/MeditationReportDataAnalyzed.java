package cn.entertech.uicomponentsdk.report.file;

import cn.entertech.uicomponentsdk.report.file.entity.Interruption;
import cn.entertech.uicomponentsdk.report.file.entity.ReportData;
import cn.entertech.uicomponentsdk.utils.HexDump;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static cn.entertech.uicomponentsdk.utils.HexDump.floatArrayToByteArray;
import static cn.entertech.uicomponentsdk.utils.HexDump.hexSringToBytes;
import static cn.entertech.uicomponentsdk.utils.HexDump.mergeByteArrays;

public class MeditationReportDataAnalyzed implements BrainDataUnit {
    //数据实时间隔 单位毫秒
    public static final int INTERRUPT_TIME_OF_BIODATA = 400;
    public static final int INTERRUPT_TIME_OF_AFFECTIVE = 800;
    //标量数据容量，单位字节
    public static final int CONTAINER_OF_SCALAR_DATA = 3;
    //数组长度容量，单位字节
    public static final int CONTAINER_OF_ARRAY_LENGTH = 4;
    private ArrayList<Interruption> interruptTimeStamps;
    private long startTime;

    private float attentionAvg;
    private List<Double> attentionRec;
    private float relaxationAvg;
    private List<Double> relaxationRec;
    private float pressureAvg;
    private List<Double> pressureRec;
    private float pleasureAvg;
    private List<Double> pleasureRec;

    private List<Double> alphaCurve;
    private List<Double> betaCurve;
    private List<Double> thetaCurve;
    private List<Double> deltaCurve;
    private List<Double> gammaCurve;
    private float hrAvg;
    private float hrMax;
    private float hrMin;
    private float hrvAvg;
    private List<Double> hrRec;
    private List<Double> hrvRec;

    @NotNull
    @Override
    public byte[] toFileBytes() {
        return mergeByteArrays(getScalarDataBytes()
                , getListByteArray("f1", alphaCurve),
                getListByteArray("f8", attentionRec), getListByteArray("f9", relaxationRec)
                , getListByteArray("fa", pressureRec), getListByteArray("fb", pleasureRec)
                , getListByteArray("f6", hrRec), getListByteArray("f7", hrvRec)
                , getListByteArray("f2", betaCurve)
                , getListByteArray("f3", thetaCurve), getListByteArray("f4", deltaCurve)
                , getListByteArray("f5", gammaCurve)
        );
    }

    public MeditationReportDataAnalyzed() {
    }

    public MeditationReportDataAnalyzed(ReportData reportMeditationDataEntity, ArrayList<Interruption> interruptTimeStamp) {
        this.startTime = reportMeditationDataEntity.getStartTime();
        this.interruptTimeStamps = interruptTimeStamp;
        this.attentionAvg = reportMeditationDataEntity.getReportAttentionEnitty().getAttentionAvg().floatValue();
        this.relaxationAvg = reportMeditationDataEntity.getReportRelaxationEnitty().getRelaxationAvg().floatValue();
        this.pressureAvg = reportMeditationDataEntity.getReportPressureEnitty().getPressureAvg().floatValue();
        this.pleasureAvg = reportMeditationDataEntity.getReportPleasureEnitty().getPleasureAvg().floatValue();
        this.hrAvg = reportMeditationDataEntity.getReportHRDataEntity().getHrAvg().floatValue();
        this.hrMax = reportMeditationDataEntity.getReportHRDataEntity().getHrMax().floatValue();
        this.hrMin = reportMeditationDataEntity.getReportHRDataEntity().getHrMin().floatValue();
        this.hrvAvg = reportMeditationDataEntity.getReportHRDataEntity().getHrvAvg().floatValue();

        this.attentionRec = addInterruptData(reportMeditationDataEntity.getReportAttentionEnitty().getAttentionRec(), INTERRUPT_TIME_OF_AFFECTIVE);
        this.relaxationRec = addInterruptData(reportMeditationDataEntity.getReportRelaxationEnitty().getRelaxationRec(), INTERRUPT_TIME_OF_AFFECTIVE);
        this.pressureRec = addInterruptData(reportMeditationDataEntity.getReportPressureEnitty().getPressureRec(), INTERRUPT_TIME_OF_AFFECTIVE);
        this.pleasureRec = addInterruptData(reportMeditationDataEntity.getReportPleasureEnitty().getPleasureRec(), INTERRUPT_TIME_OF_AFFECTIVE);
        this.hrRec = addInterruptData(reportMeditationDataEntity.getReportHRDataEntity().getHrRec(), INTERRUPT_TIME_OF_BIODATA);
        this.hrvRec = addInterruptData(reportMeditationDataEntity.getReportHRDataEntity().getHrvRec(), INTERRUPT_TIME_OF_BIODATA);
        this.alphaCurve = addInterruptData(reportMeditationDataEntity.getReportEEGDataEntity().getAlphaCurve(), INTERRUPT_TIME_OF_BIODATA);
        this.betaCurve = addInterruptData(reportMeditationDataEntity.getReportEEGDataEntity().getBetaCurve(), INTERRUPT_TIME_OF_BIODATA);
        this.thetaCurve = addInterruptData(reportMeditationDataEntity.getReportEEGDataEntity().getThetaCurve(), INTERRUPT_TIME_OF_BIODATA);
        this.deltaCurve = addInterruptData(reportMeditationDataEntity.getReportEEGDataEntity().getDeltaCurve(), INTERRUPT_TIME_OF_BIODATA);
        this.gammaCurve = addInterruptData(reportMeditationDataEntity.getReportEEGDataEntity().getGammaCurve(), INTERRUPT_TIME_OF_BIODATA);
    }

    /**
     * addInterruptData
     * 补齐体验中断数据
     *
     * @param source
     * @param interrupt
     * @return
     */
    public List<Double> addInterruptData(List<Double> source, int interrupt) {
        for (int i = 0; i < interruptTimeStamps.size(); i++) {
            long interruptStartTime = interruptTimeStamps.get(i).getInterruptStartTime();
            long interruptEndTime = interruptTimeStamps.get(i).getInterruptEndTime();
            if (interruptStartTime < startTime) {
                continue;
            }
            int insertIndex = (int) (interruptStartTime - startTime) / interrupt;
            int insertCount = (int) (interruptEndTime - interruptStartTime) / interrupt;
            for (int j = 0; j < insertCount; j++) {
                source.add(insertIndex, 0.0);
            }
        }
        return source;
    }


    public byte[] getScalarDataBytes() {
        return mergeByteArrays(
                HexDump.hexSringToBytes("01"), HexDump.float2byte(hrAvg),
                HexDump.hexSringToBytes("02"), HexDump.float2byte(hrMax),
                HexDump.hexSringToBytes("03"), HexDump.float2byte(hrMin),
                HexDump.hexSringToBytes("04"), HexDump.float2byte(hrvAvg),
                HexDump.hexSringToBytes("07"), HexDump.float2byte(attentionAvg),
                HexDump.hexSringToBytes("0a"), HexDump.float2byte(relaxationAvg),
                HexDump.hexSringToBytes("0d"), HexDump.float2byte(pressureAvg),
                HexDump.hexSringToBytes("10"), HexDump.float2byte(pleasureAvg),
                HexDump.hexSringToBytes("16"), hexSringToBytes(Long.toHexString(startTime))
        );

    }

    public byte[] getByteArray(List<Double> data) {
        if (data == null || data.size() == 0) {
            return new byte[0];
        }
        float[] floatArray = new float[data.size()];
        for (int i = 0; i < data.size(); i++) {
            floatArray[i] = data.get(i).floatValue();
        }
        return floatArrayToByteArray(floatArray);
    }


    private String formatString(int val, int byteCount) {
//        Log.d("####", "val is " + val);
        if (val == -1.0 || val == -1) {
            return "ffffff";
        }
        return String.format("%0" + byteCount * 2 + "x", val);
    }

    public byte[] getListByteArray(String key, List<Double> list) {
        if (list == null) {
            return mergeByteArrays(HexDump.hexSringToBytes(key), HexDump.float2byte(0));
        }
        return mergeByteArrays(HexDump.hexSringToBytes(key), HexDump.float2byte(list.size()), getByteArray(list));
    }

    public float getAttentionAvg() {
        return attentionAvg;
    }

    public void setAttentionAvg(float attentionAvg) {
        this.attentionAvg = attentionAvg;
    }

    public List<Double> getAttentionRec() {
        return attentionRec;
    }

    public void setAttentionRec(List<Double> attentionRec) {
        this.attentionRec = attentionRec;
    }

    public float getRelaxationAvg() {
        return relaxationAvg;
    }

    public void setRelaxationAvg(float relaxationAvg) {
        this.relaxationAvg = relaxationAvg;
    }

    public List<Double> getRelaxationRec() {
        return relaxationRec;
    }

    public void setRelaxationRec(List<Double> relaxationRec) {
        this.relaxationRec = relaxationRec;
    }

    public float getPressureAvg() {
        return pressureAvg;
    }

    public void setPressureAvg(float pressureAvg) {
        this.pressureAvg = pressureAvg;
    }

    public List<Double> getPressureRec() {
        return pressureRec;
    }

    public void setPressureRec(List<Double> pressureRec) {
        this.pressureRec = pressureRec;
    }

    public float getPleasureAvg() {
        return pleasureAvg;
    }

    public void setPleasureAvg(float pleasureAvg) {
        this.pleasureAvg = pleasureAvg;
    }

    public List<Double> getPleasureRec() {
        return pleasureRec;
    }

    public void setPleasureRec(List<Double> pleasureRec) {
        this.pleasureRec = pleasureRec;
    }

    public List<Double> getAlphaCurve() {
        return alphaCurve;
    }

    public void setAlphaCurve(List<Double> alphaCurve) {
        this.alphaCurve = alphaCurve;
    }

    public List<Double> getBetaCurve() {
        return betaCurve;
    }

    public void setBetaCurve(List<Double> betaCurve) {
        this.betaCurve = betaCurve;
    }

    public List<Double> getThetaCurve() {
        return thetaCurve;
    }

    public void setThetaCurve(List<Double> thetaCurve) {
        this.thetaCurve = thetaCurve;
    }

    public List<Double> getDeltaCurve() {
        return deltaCurve;
    }

    public void setDeltaCurve(List<Double> deltaCurve) {
        this.deltaCurve = deltaCurve;
    }

    public List<Double> getGammaCurve() {
        return gammaCurve;
    }

    public void setGammaCurve(List<Double> gammaCurve) {
        this.gammaCurve = gammaCurve;
    }

    public float getHrAvg() {
        return hrAvg;
    }

    public void setHrAvg(float hrAvg) {
        this.hrAvg = hrAvg;
    }

    public float getHrMax() {
        return hrMax;
    }

    public void setHrMax(float hrMax) {
        this.hrMax = hrMax;
    }

    public float getHrMin() {
        return hrMin;
    }

    public void setHrMin(float hrMin) {
        this.hrMin = hrMin;
    }

    public float getHrvAvg() {
        return hrvAvg;
    }

    public void setHrvAvg(float hrvAvg) {
        this.hrvAvg = hrvAvg;
    }

    public List<Double> getHrRec() {
        return hrRec;
    }

    public void setHrRec(List<Double> hrRec) {
        this.hrRec = hrRec;
    }

    public List<Double> getHrvRec() {
        return hrvRec;
    }

    public void setHrvRec(List<Double> hrvRec) {
        this.hrvRec = hrvRec;
    }

    @Override
    public String toString() {
        return "MeditationReportDataAnalyzed{" +
                "attentionAvg=" + attentionAvg +
                ", attentionRec=" + attentionRec +
                ", relaxationAvg=" + relaxationAvg +
                ", relaxationRec=" + relaxationRec +
                ", pressureAvg=" + pressureAvg +
                ", pressureRec=" + pressureRec +
                ", pleasureAvg=" + pleasureAvg +
                ", pleasureRec=" + pleasureRec +
                ", alphaCurve=" + alphaCurve +
                ", betaCurve=" + betaCurve +
                ", thetaCurve=" + thetaCurve +
                ", deltaCurve=" + deltaCurve +
                ", gammaCurve=" + gammaCurve +
                ", hrAvg=" + hrAvg +
                ", hrMax=" + hrMax +
                ", hrMin=" + hrMin +
                ", hrRec=" + hrRec +
                ", hrvAvg=" + hrvAvg +
                ", hrvRec=" + hrvRec +
                '}';
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
    public long getStartTime() {
        return startTime;
    }
}
