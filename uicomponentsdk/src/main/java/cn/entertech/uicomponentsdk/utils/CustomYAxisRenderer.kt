package cn.entertech.uicomponentsdk.utils

import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.YAxisRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import java.lang.Math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10

class CustomYAxisRenderer(
    viewPortHandler: ViewPortHandler?,
    yAxis: YAxis?,
    trans: Transformer?
) : YAxisRenderer(viewPortHandler, yAxis, trans) {
    override fun computeAxisValues(min: Float, max: Float) {
        val labelCount = mAxis.labelCount
        val range = Math.abs(max - min).toDouble()
        if (labelCount == 0 || range <= 0 || java.lang.Double.isInfinite(range)) {
            mAxis.mEntries = floatArrayOf()
            mAxis.mCenteredEntries = floatArrayOf()
            mAxis.mEntryCount = 0
            return
        }

        // Find out how much spacing (in y value space) between axis values
        val rawInterval = range / labelCount
        var interval =
            Utils.roundToNextSignificant(rawInterval).toDouble()

        // If granularity is enabled, then do not allow the interval to go below specified granularity.
        // This is used to avoid repeated values when rounding values for display.
        if (mAxis.isGranularityEnabled) interval =
            (if (interval < mAxis.granularity) mAxis.granularity else interval) as Double

        // Normalize interval
        val intervalMagnitude =
            ceil(Utils.roundToNextSignificant(
                Math.pow(
                    10.0,
                    Math.log10(interval).toInt().toDouble()
                )
            ).toDouble())
        val intervalSigDigit = (interval / intervalMagnitude)
        if (intervalSigDigit > 5) {
            // Use one order of magnitude higher, to avoid intervals like 0.9 or
            // 90
            interval = floor(10 * intervalMagnitude)
        }

        val largeInterval = intervalSigDigit.niceCeil() * intervalMagnitude.toInt()
        val smallInterval = intervalSigDigit.niceFloor() * intervalMagnitude.toInt()
        val largeCount = getNiceCount(min.toDouble(), max.toDouble(), largeInterval)
        val smallCount = getNiceCount(min.toDouble(), max.toDouble(), smallInterval)
        interval =  if (kotlin.math.abs(largeCount - NICE_TICK_COUNT) < kotlin.math.abs(smallCount - NICE_TICK_COUNT)) {
            largeInterval.toDouble()
        } else {
            smallInterval.toDouble()
        }
        var n = if (mAxis.isCenterAxisLabelsEnabled) 1 else 0

        // force label count
        if (mAxis.isForceLabelsEnabled) {
            interval = range.toFloat() / (labelCount - 1).toFloat().toDouble()
            mAxis.mEntryCount = labelCount
            if (mAxis.mEntries.size < labelCount) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = FloatArray(labelCount)
            }
            var v = min
            for (i in 0 until labelCount) {
                mAxis.mEntries[i] = v
                v += interval.toFloat()
            }
            n = labelCount

            // no forced count
        } else {
            var first =
                if (interval == 0.0) 0.0 else Math.floor(min / interval) * interval
            if (mAxis.isCenterAxisLabelsEnabled) {
                first -= interval
            }
            var last =
                if (interval == 0.0) 0.0 else
                    Math.ceil(max / interval) * interval

            if (last > max){
                last -= interval
            }
            var f: Double
            if (interval != 0.0) {
                f = first
                while (f <= last) {
                    ++n
                    f += interval
                }
            }
            mAxis.mEntryCount = n
            if (mAxis.mEntries.size < n) {
                // Ensure stops contains at least numStops elements.
                mAxis.mEntries = FloatArray(n)
            }
            f = first
            var i: Int = 0
            while (i < n) {
                if (f == 0.0) // Fix for negative zero case (Where value == -0.0, and 0.0 == -0.0)
                    f = 0.0
                mAxis.mEntries[i] = f.toFloat()
                f += interval
                ++i
            }
        }

        // set decimals
        if (interval < 1) {
            mAxis.mDecimals = ceil(-log10(interval)).toInt()
        } else {
            mAxis.mDecimals = 0
        }
        if (mAxis.isCenterAxisLabelsEnabled) {
            if (mAxis.mCenteredEntries.size < n) {
                mAxis.mCenteredEntries = FloatArray(n)
            }
            val offset = interval.toFloat() / 2f
            for (i in 0 until n) {
                mAxis.mCenteredEntries[i] = mAxis.mEntries[i] + offset
            }
        }
    }
}