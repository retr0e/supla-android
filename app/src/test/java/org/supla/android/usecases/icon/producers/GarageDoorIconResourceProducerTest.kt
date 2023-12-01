package org.supla.android.usecases.icon.producers
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

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnitRunner
import org.supla.android.R
import org.supla.android.data.model.general.ChannelState
import org.supla.android.lib.SuplaConst

@RunWith(MockitoJUnitRunner::class)
class GarageDoorIconResourceProducerTest : BaseIconResourceProducerTest() {

  @InjectMocks
  override lateinit var producer: GarageDoorIconResourceProducer

  @Test
  fun `should produce open icon`() {
    test(
      state = ChannelState.Value.OPEN,
      function = SuplaConst.SUPLA_CHANNELFNC_OPENSENSOR_GARAGEDOOR,
      expectedIcon = R.drawable.garagedooropen
    )
  }

  @Test
  fun `should produce closed icon`() {
    test(
      state = ChannelState.Value.CLOSED,
      function = SuplaConst.SUPLA_CHANNELFNC_OPENSENSOR_GARAGEDOOR,
      expectedIcon = R.drawable.garagedoorclosed
    )
  }

  @Test
  fun `should produce partially opened icon`() {
    test(
      state = ChannelState.Value.PARTIALLY_OPENED,
      function = SuplaConst.SUPLA_CHANNELFNC_CONTROLLINGTHEGARAGEDOOR,
      expectedIcon = R.drawable.garagedoorclosed50percent
    )
  }

  @Test
  fun `should produce open icon (night mode)`() {
    test(
      state = ChannelState.Value.OPEN,
      function = SuplaConst.SUPLA_CHANNELFNC_CONTROLLINGTHEGARAGEDOOR,
      expectedIcon = R.drawable.garagedooropen_nightmode,
      nightMode = true
    )
  }

  @Test
  fun `should produce closed icon (night mode)`() {
    test(
      state = ChannelState.Value.CLOSED,
      function = SuplaConst.SUPLA_CHANNELFNC_CONTROLLINGTHEGARAGEDOOR,
      expectedIcon = R.drawable.garagedoorclosed_nightmode,
      nightMode = true
    )
  }

  @Test
  fun `should produce partially opened icon (night mode)`() {
    test(
      state = ChannelState.Value.PARTIALLY_OPENED,
      function = SuplaConst.SUPLA_CHANNELFNC_OPENSENSOR_GARAGEDOOR,
      expectedIcon = R.drawable.garagedoorclosed50percent_nightmode,
      nightMode = true
    )
  }
}
