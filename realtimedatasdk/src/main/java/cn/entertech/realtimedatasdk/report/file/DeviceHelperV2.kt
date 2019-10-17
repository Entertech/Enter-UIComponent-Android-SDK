package cn.entertech.realtimedatasdk.report.file

import cn.entertech.naptime.file.DeviceHelper

/**
 * Created by EnterTech on 2017/11/13.
 */
class DeviceHelperV2 : DeviceHelper {
    override var maxValue: Int
        get() = 0xffff //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}
}