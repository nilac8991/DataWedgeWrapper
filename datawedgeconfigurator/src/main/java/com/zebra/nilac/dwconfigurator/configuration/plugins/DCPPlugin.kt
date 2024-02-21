package com.zebra.nilac.dwconfigurator.configuration.plugins

import android.os.Bundle
import com.zebra.nilac.dwconfigurator.models.dcp.DCPButtonAnchorPosition
import com.zebra.nilac.dwconfigurator.models.dcp.DCPLaunchMode

class DCPPlugin private constructor(builder: Builder) {

    private val plugin = Bundle()

    init {
        val paramList = Bundle().apply {
            putString(DCP_ENABLED_KEY, if (builder.pluginEnabled) "true" else "false")
            putString("RESET_CONFIG", if (builder.resetConfig) "true" else "false")

            putString(DCP_BUTTON_ANCHOR_POSITION, builder.buttonAnchorPosition.name)
            putString(DCP_LAUNCH_MODE, builder.launchMode.name)
            putString(DCP_HIGHEST_POSITION, builder.highestPositionValue.toString())
            putString(DCP_LOWEST_POSITION, builder.lowestPositionValue.toString())
            putString(DCP_TOUCH_WAIT_TIME, builder.touchWaitTime.toString())
        }
        plugin.putBundle("PARAM_LIST", paramList)
    }

    class Builder {

        // Config
        var resetConfig = true

        //Params
        internal var pluginEnabled: Boolean = false
        internal var buttonAnchorPosition: DCPButtonAnchorPosition = DCPButtonAnchorPosition.BOTH
        internal var launchMode: DCPLaunchMode = DCPLaunchMode.BUTTON
        internal var highestPositionValue = 100 //Of screen height
        internal var lowestPositionValue = 100 //Of screen height
        internal var touchWaitTime = 100 //In ms prior to scanner activation

        fun resetConfig(resetConfig: Boolean): Builder {
            this.resetConfig = resetConfig
            return this
        }

        fun setEnabled(enabled: Boolean): Builder {
            this.pluginEnabled = enabled
            return this
        }

        fun setButtonAnchorPosition(buttonAnchorPosition: DCPButtonAnchorPosition): Builder {
            this.buttonAnchorPosition = buttonAnchorPosition
            return this
        }

        fun setLaunchMode(launchMode: DCPLaunchMode): Builder {
            this.launchMode = launchMode
            return this
        }

        fun setHighestPositionValue(value: Int): Builder {
            this.highestPositionValue = if (value > 100) {
                100
            } else {
                value
            }
            return this
        }

        fun setLowestPositionValue(value: Int): Builder {
            this.lowestPositionValue = if (value > 100) {
                100
            } else {
                value
            }
            return this
        }

        fun setTouchWaitTime(value: Int): Builder {
            this.touchWaitTime = value
            return this
        }

        fun create(): Bundle {
            return DCPPlugin(this).plugin
        }
    }

    companion object {
        const val TAG = "DCPPlugin"

        private const val DCP_ENABLED_KEY = "dcp_input_enabled"
        private const val DCP_BUTTON_ANCHOR_POSITION = "dcp_dock_button_on"
        private const val DCP_LAUNCH_MODE = "dcp_start_in"
        private const val DCP_HIGHEST_POSITION = "dcp_highest_pos"
        private const val DCP_LOWEST_POSITION = "dcp_lowest_pos"
        private const val DCP_TOUCH_WAIT_TIME = "dcp_drag_detect_time"
    }
}