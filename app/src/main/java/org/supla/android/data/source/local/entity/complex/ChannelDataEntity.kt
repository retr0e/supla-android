package org.supla.android.data.source.local.entity.complex
/*
 Copyright (C) AC SOFTWARE SP. Z O.O.

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import androidx.room.Embedded
import org.supla.android.data.source.local.entity.ChannelConfigEntity
import org.supla.android.data.source.local.entity.ChannelEntity
import org.supla.android.data.source.local.entity.ChannelExtendedValueEntity
import org.supla.android.data.source.local.entity.ChannelValueEntity
import org.supla.android.data.source.local.entity.LocationEntity
import org.supla.android.db.Channel
import org.supla.android.db.ChannelExtendedValue
import org.supla.android.db.ChannelValue
import org.supla.android.lib.SuplaChannelExtendedValue

data class ChannelDataEntity(
  @Embedded(prefix = "channel_") val channelEntity: ChannelEntity,
  @Embedded(prefix = "value_") val channelValueEntity: ChannelValueEntity,
  @Embedded(prefix = "location_") val locationEntity: LocationEntity,
  @Embedded(prefix = "extended_value_") val channelExtendedValueEntity: ChannelExtendedValueEntity?,
  @Embedded(prefix = "config_") val configEntity: ChannelConfigEntity?
) {

  val remoteId: Int
    get() = channelEntity.remoteId

  val function: Int
    get() = channelEntity.function

  @Deprecated("Please use channelDataEntity if possible")
  fun getLegacyChannel(): Channel = Channel().also { channel ->
    channel.id = channelEntity.id
    channel.deviceID = channelEntity.deviceId ?: 0
    channel.remoteId = channelEntity.remoteId
    channel.type = channelEntity.type
    channel.func = channelEntity.function
    channel.setCaption(channelEntity.caption)
    channel.visible = channelEntity.visible
    channel.locationId = channelEntity.locationId
    channel.altIcon = channelEntity.altIcon
    channel.userIconId = channelEntity.userIcon
    channel.manufacturerID = channelEntity.manufacturerId
    channel.productID = channelEntity.productId
    channel.flags = channelEntity.flags
    channel.protocolVersion = channelEntity.protocolVersion
    channel.position = channelEntity.position
    channel.profileId = channelEntity.profileId

    channel.value = ChannelValue().also { value ->
      value.id = channelValueEntity.id
      value.channelId = channelValueEntity.channelRemoteId
      value.onLine = channelValueEntity.online
      value.setChannelStringValue(channelValueEntity.value)
      value.setChannelStringSubValue(channelValueEntity.subValue)
      value.subValueType = channelValueEntity.subValueType
      value.profileId = channelValueEntity.profileId
    }

    channelExtendedValueEntity?.let { extendedValue ->
      channel.extendedValue = ChannelExtendedValue().also { legacyExtendedValue ->
        legacyExtendedValue.id = extendedValue.id
        legacyExtendedValue.channelId = extendedValue.channelId
        legacyExtendedValue.profileId = extendedValue.profileId
        legacyExtendedValue.timerStartTimestamp = extendedValue.timerStartTime?.time
        legacyExtendedValue.extendedValue = extendedValue.getSuplaValue() ?: SuplaChannelExtendedValue()
      }
    }
  }
}
