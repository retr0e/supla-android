package org.supla.android.core.networking.suplaclient

import org.supla.android.data.source.remote.ChannelConfigType
import org.supla.android.data.source.remote.Field
import org.supla.android.data.source.remote.FieldType
import org.supla.android.data.source.remote.SuplaChannelConfig
import org.supla.android.lib.actions.ActionParameters
import java.util.EnumSet

interface SuplaClientProvider {
  fun provide(): SuplaClientApi?
}

interface SuplaClientApi {
  fun cancel()

  @Throws(InterruptedException::class)
  fun join()

  fun reconnect()

  fun open(ID: Int, Group: Boolean, Open: Int): Boolean

  fun setRGBW(
    ID: Int,
    Group: Boolean,
    Color: Int,
    ColorBrightness: Int,
    Brightness: Int,
    TurnOnOff: Boolean
  ): Boolean

  fun executeAction(parameters: ActionParameters): Boolean

  fun registerPushNotificationClientToken(appId: Int, token: String): Boolean

  fun registered(): Boolean

  fun timerArm(remoteId: Int, setOn: Boolean, durationInMs: Int): Boolean

  fun getChannelConfig(remoteId: Int, type: ChannelConfigType): Boolean

  fun setChannelConfig(config: SuplaChannelConfig): Boolean

  fun getDeviceConfig(deviceId: Int, type: EnumSet<FieldType>): Boolean

  fun oAuthTokenRequest()
}
