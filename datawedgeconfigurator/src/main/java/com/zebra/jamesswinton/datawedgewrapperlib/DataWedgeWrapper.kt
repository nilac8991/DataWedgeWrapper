package com.zebra.jamesswinton.datawedgewrapperlib

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import com.zebra.jamesswinton.datawedgewrapperlib.interfaces.OnCompleteResultIntentListener
import com.zebra.jamesswinton.datawedgewrapperlib.interfaces.OnLastResultIntentListener
import com.zebra.jamesswinton.datawedgewrapperlib.models.CommandIdentifier
import com.zebra.jamesswinton.datawedgewrapperlib.models.ResultType
import com.zebra.jamesswinton.datawedgewrapperlib.utilities.Constants.IntentType

object DataWedgeWrapper {

    // Debugging
    private const val TAG = "DataWedgeWrapper"

    // Intent Placeholders
    private const val DW_ACTION_STRING = "com.symbol.datawedge.api.ACTION"
    private const val INTENT_TYPE_KEY_PREFIX = "com.symbol.datawedge.api."
    private const val SEND_RESULT_KEY = "SEND_RESULT"
    private const val COMMAND_IDENTIFIER_KEY = "COMMAND_IDENTIFIER"

    // Result Placeholders
    private const val INTENT_ACTION = "com.symbol.datawedge.api.RESULT_ACTION"
    private const val INTENT_CATEGORY = "android.intent.category.DEFAULT"

    // Interface
    private var mOnLastResultIntentListener: OnLastResultIntentListener? = null
    private var mOnCompleteResultIntentListener: OnCompleteResultIntentListener? = null

    // Intent Methods
    fun sendIntent(context: Context, intentType: IntentType, data: Bundle?) {
        // Build Intent
        val i = Intent()
        i.action = DW_ACTION_STRING
        i.putExtra(INTENT_TYPE_KEY_PREFIX + intentType.name, data)
        i.putExtra(SEND_RESULT_KEY, ResultType.NONE)

        // Send Intent
        context.sendBroadcast(i)
    }

    fun sendIntent(context: Context, intentType: IntentType, data: String?) {
        // Build Intent
        val i = Intent()
        i.action = DW_ACTION_STRING
        i.putExtra(INTENT_TYPE_KEY_PREFIX + intentType.name, data)

        // Send Intent
        context.sendBroadcast(i)
    }

    fun sendIntentWithLastResult(
        context: Context, intentType: IntentType, data: Bundle?,
        identifier: CommandIdentifier?,
        onLastResultIntentListener: OnLastResultIntentListener?
    ) {
        // Store Interface
        mOnLastResultIntentListener = onLastResultIntentListener

        // Register Result Receiver
        registerReceiver(context)

        // Build Intent
        val i = Intent()
        i.action = DW_ACTION_STRING
        i.putExtra(INTENT_TYPE_KEY_PREFIX + intentType.name, data)
        i.putExtra(SEND_RESULT_KEY, ResultType.LAST_RESULT)
        i.putExtra(COMMAND_IDENTIFIER_KEY, identifier)

        // Send Intent
        context.sendBroadcast(i)
    }

    fun sendIntentWithLastResult(
        context: Context, intentType: IntentType, data: String?,
        identifier: CommandIdentifier?,
        onLastResultIntentListener: OnLastResultIntentListener?
    ) {
        // Store Interface
        mOnLastResultIntentListener = onLastResultIntentListener

        // Register Result Receiver
        registerReceiver(context)

        // Build Intent
        val i = Intent()
        i.action = DW_ACTION_STRING
        i.putExtra(INTENT_TYPE_KEY_PREFIX + intentType.name, data)
        i.putExtra(SEND_RESULT_KEY, ResultType.LAST_RESULT)
        i.putExtra(COMMAND_IDENTIFIER_KEY, identifier)

        // Send Intent
        context.sendBroadcast(i)
    }

    fun sendIntentWithCompleteResult(
        context: Context, intentType: IntentType, data: Bundle?,
        identifier: CommandIdentifier?,
        onCompleteResultIntentListener: OnCompleteResultIntentListener?
    ) {
        // Store Interface
        mOnCompleteResultIntentListener = onCompleteResultIntentListener

        // Register Result Receiver
        registerReceiver(context)

        // Build Intent
        val i = Intent()
        i.action = DW_ACTION_STRING
        i.putExtra(INTENT_TYPE_KEY_PREFIX + intentType.name, data)
        i.putExtra(SEND_RESULT_KEY, ResultType.COMPLETE_RESULT)
        i.putExtra(COMMAND_IDENTIFIER_KEY, identifier)

        // Send Intent
        context.sendBroadcast(i)
    }

    fun sendIntentWithCompleteResult(
        context: Context, intentType: IntentType, data: String?,
        identifier: CommandIdentifier?,
        onCompleteResultIntentListener: OnCompleteResultIntentListener?
    ) {
        // Store Interface
        mOnCompleteResultIntentListener = onCompleteResultIntentListener

        // Register Result Receiver
        registerReceiver(context)

        // Build Intent
        val i = Intent()
        i.action = DW_ACTION_STRING
        i.putExtra(INTENT_TYPE_KEY_PREFIX + intentType.name, data)
        i.putExtra(SEND_RESULT_KEY, ResultType.COMPLETE_RESULT)
        i.putExtra(COMMAND_IDENTIFIER_KEY, identifier)

        // Send Intent
        context.sendBroadcast(i)
    }

    private val intentReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            unregisterReceiver(context)

            val action = intent.action
            val command = intent.getStringExtra("COMMAND")
            val profileName = intent.getStringExtra("PROFILE_NAME")

            if (action == INTENT_ACTION) {
                if (intent.hasExtra("RESULT_LIST")) {
                    val resultList = intent.getSerializableExtra("RESULT_LIST") as ArrayList<Bundle>

                    val resultInfo = StringBuilder()

                    for (bundleResult in resultList) {
                        resultInfo.append("\n\n")
                        bundleResult.keySet().forEach { key ->
                            val valString = bundleResult.getString(key) ?: bundleResult.getStringArray(key)?.joinToString("\n") ?: ""
                            resultInfo.append("$key: $valString\n")
                        }
                    }

                    mOnCompleteResultIntentListener?.onResult(resultList, resultInfo.toString(), command, profileName)

                } else if (intent.hasExtra("RESULT_INFO")) {
                    val result = intent.getStringExtra("RESULT")
                    val successful = result == "SUCCESS"
                    val resultInfo = intent.getBundleExtra("RESULT_INFO")

                    val resultInfoString = StringBuilder("Result: ${resultInfo?.getString("RESULT")}\n")

                    resultInfo?.keySet()?.forEach { key ->
                        val valString = resultInfo.getString(key) ?: resultInfo.getStringArray(key)?.joinToString("\n") ?: ""
                        resultInfoString.append("$key: $valString\n")
                    }

                    mOnLastResultIntentListener?.onResult(successful, resultInfo, resultInfoString.toString(), command, profileName)
                }
            } else {
                Log.w(TAG, "Ignoring received Intent that didn't have action: $INTENT_ACTION")
            }
        }
    }

    private fun registerReceiver(context: Context) {
        val filter = IntentFilter()
        filter.addAction(INTENT_ACTION)
        filter.addCategory(INTENT_CATEGORY)
        context.registerReceiver(intentReceiver, filter)
    }

    private fun unregisterReceiver(context: Context) {
        context.unregisterReceiver(intentReceiver)
    }
}