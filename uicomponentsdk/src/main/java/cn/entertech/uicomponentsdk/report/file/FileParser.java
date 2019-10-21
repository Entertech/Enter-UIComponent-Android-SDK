package cn.entertech.uicomponentsdk.report.file;

import cn.entertech.uicomponentsdk.utils.HexDump;

import java.util.ArrayList;
import java.util.List;

import static cn.entertech.uicomponentsdk.utils.HexDump.getFloat;

public class FileParser {
    public static MeditationReportDataAnalyzed parseMeditationReport(String source) {
        MeditationReportDataAnalyzed meditationReportDataAnalyzed = new MeditationReportDataAnalyzed();
        int keyStartIndex = 96;
        int keyEndIndex = keyStartIndex + 2;
        int valueStartIndex = keyEndIndex;
        int valueEndIndex = valueStartIndex + 8;
        String key = StringUtil.substring(source, keyStartIndex, keyEndIndex);
        String value = "";
        while (!"F1".equals(key)) {
            value = StringUtil.substring(source, valueStartIndex, valueEndIndex);
            Float floatValue;
            if ("FFFFFFFF".equals(value)) {
                floatValue = -1f;
            } else {
                byte[] bytes = HexDump.hexSringToBytes(value);
                if (bytes.length == 0) {
                    return null;
                }
                floatValue = getFloat(bytes);
            }
            switch (key) {
                case "01":
                    meditationReportDataAnalyzed.setHrAvg(floatValue);
                    break;
                case "02":
                    meditationReportDataAnalyzed.setHrMax(floatValue);
                    break;
                case "03":
                    meditationReportDataAnalyzed.setHrMin(floatValue);
                    break;
                case "04":
                    meditationReportDataAnalyzed.setHrvAvg(floatValue);
                    break;
                case "07":
                    meditationReportDataAnalyzed.setAttentionAvg(floatValue);
                    break;
                case "0A":
                    meditationReportDataAnalyzed.setRelaxationAvg(floatValue);
                    break;
                case "0D":
                    meditationReportDataAnalyzed.setPressureAvg(floatValue);
                    break;
                case "10":
                    meditationReportDataAnalyzed.setPleasureAvg(floatValue);
                    break;
                case "16":
                    meditationReportDataAnalyzed.setStartTime(Long.parseLong(value, 16));
                    break;
                default:
                    break;
            }
            keyStartIndex = keyStartIndex + 10;
            keyEndIndex = keyStartIndex + 2;
            valueStartIndex = keyEndIndex;
            valueEndIndex = valueStartIndex + 8;
            key = StringUtil.substring(source, keyStartIndex, keyEndIndex);
        }

        List<Double> attentionRec = new ArrayList<>();
        List<Double> relaxationRec = new ArrayList<>();
        List<Double> pressureRec = new ArrayList<>();
        List<Double> pleasureRec = new ArrayList<>();
        List<Double> alphaCurve = new ArrayList<>();
        List<Double> betaCurve = new ArrayList<>();
        List<Double> thetaCurve = new ArrayList<>();
        List<Double> deltaCurve = new ArrayList<>();
        List<Double> gammaCurve = new ArrayList<>();
        List<Double> hrRec = new ArrayList<>();
        List<Double> hrvRec = new ArrayList<>();

        int recKeyStartIndex = keyStartIndex;
        int dataEndIndex = recKeyStartIndex;
        while (dataEndIndex < source.length()) {
            int recKeyEndIndex = recKeyStartIndex + 2;
            int lengthStartIndex = recKeyEndIndex;
            int lengthEndIndex = lengthStartIndex + 8;
            int dataStartIndex = lengthEndIndex;
            String dataKey = StringUtil.substring(source, recKeyStartIndex, recKeyEndIndex);
            String hexLength = StringUtil.substring(source,
                    lengthStartIndex, lengthEndIndex);
            byte[] bytesLength = HexDump.hexSringToBytes(hexLength);
            int length = (int) getFloat(bytesLength);
            dataEndIndex = dataStartIndex + length * 8;
            int dataTotalCount = 10 + length * 2 * 4;
            switch (dataKey) {
                case "F1":
                    for (int j = 0; j < length; j++) {
                        float cruveValue = getFloat(HexDump.hexSringToBytes(StringUtil.substring(source,
                                dataStartIndex + j * 8, dataStartIndex + 8 + j * 8)));
                        alphaCurve.add((double) cruveValue);
                        meditationReportDataAnalyzed.setAlphaCurve(alphaCurve);
                    }
                    break;
                case "F2":
                    for (int j = 0; j < length; j++) {
                        float cruveValue = getFloat(HexDump.hexSringToBytes(StringUtil.substring(source,
                                dataStartIndex + j * 8, dataStartIndex + 8 + j * 8)));
                        betaCurve.add((double) cruveValue);
                        meditationReportDataAnalyzed.setBetaCurve(betaCurve);
                    }
                    break;
                case "F3":
                    for (int j = 0; j < length; j++) {
                        float cruveValue = getFloat(HexDump.hexSringToBytes(StringUtil.substring(source,
                                dataStartIndex + j * 8, dataStartIndex + 8 + j * 8)));
                        thetaCurve.add((double) cruveValue);
                        meditationReportDataAnalyzed.setThetaCurve(thetaCurve);
                    }
                    break;
                case "F4":
                    for (int j = 0; j < length; j++) {
                        float cruveValue = getFloat(HexDump.hexSringToBytes(StringUtil.substring(source,
                                dataStartIndex + j * 8, dataStartIndex + 8 + j * 8)));
                        deltaCurve.add((double) cruveValue);
                        meditationReportDataAnalyzed.setDeltaCurve(deltaCurve);
                    }
                    break;
                case "F5":
                    for (int j = 0; j < length; j++) {
                        float cruveValue = getFloat(HexDump.hexSringToBytes(StringUtil.substring(source,
                                dataStartIndex + j * 8, dataStartIndex + 8 + j * 8)));
                        gammaCurve.add((double) cruveValue);
                        meditationReportDataAnalyzed.setGammaCurve(gammaCurve);
                    }
                    break;
                case "F6":
                    for (int j = 0; j < length; j++) {
                        float cruveValue = getFloat(HexDump.hexSringToBytes(StringUtil.substring(source,
                                dataStartIndex + j * 8, dataStartIndex + 8 + j * 8)));
                        hrRec.add((double) cruveValue);
                        meditationReportDataAnalyzed.setHrRec(hrRec);
                    }
                    break;
                case "F7":
                    for (int j = 0; j < length; j++) {
                        float cruveValue = getFloat(HexDump.hexSringToBytes(StringUtil.substring(source,
                                dataStartIndex + j * 8, dataStartIndex + 8 + j * 8)));
                        hrvRec.add((double) cruveValue);
                        meditationReportDataAnalyzed.setHrvRec(hrvRec);
                    }
                    break;
                case "F8":
                    for (int j = 0; j < length; j++) {
                        float cruveValue = getFloat(HexDump.hexSringToBytes(StringUtil.substring(source,
                                dataStartIndex + j * 8, dataStartIndex + 8 + j * 8)));
                        attentionRec.add((double) cruveValue);
                        meditationReportDataAnalyzed.setAttentionRec(attentionRec);
                    }
                    break;
                case "F9":
                    for (int j = 0; j < length; j++) {
                        float cruveValue = getFloat(HexDump.hexSringToBytes(StringUtil.substring(source,
                                dataStartIndex + j * 8, dataStartIndex + 8 + j * 8)));
                        relaxationRec.add((double) cruveValue);
                        meditationReportDataAnalyzed.setRelaxationRec(relaxationRec);
                    }
                    break;
                case "FA":
                    for (int j = 0; j < length; j++) {
                        float cruveValue = getFloat(HexDump.hexSringToBytes(StringUtil.substring(source,
                                dataStartIndex + j * 8, dataStartIndex + 8 + j * 8)));
                        pressureRec.add((double) cruveValue);
                        meditationReportDataAnalyzed.setPressureRec(pressureRec);
                    }
                    break;
                case "FB":
                    for (int j = 0; j < length; j++) {
                        float cruveValue = getFloat(HexDump.hexSringToBytes(StringUtil.substring(source,
                                dataStartIndex + j * 8, dataStartIndex + 8 + j * 8)));
                        pleasureRec.add((double) cruveValue);
                        meditationReportDataAnalyzed.setPleasureRec(pleasureRec);
                    }
                    break;
                default:
                    break;
            }
            recKeyStartIndex = recKeyStartIndex + dataTotalCount;
        }
        return meditationReportDataAnalyzed;
    }
}
