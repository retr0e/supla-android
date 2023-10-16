@file:OptIn(ExperimentalMaterialApi::class)

package org.supla.android.features.thermostatdetail.history.ui
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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.supla.android.R
import org.supla.android.core.ui.BaseViewProxy
import org.supla.android.core.ui.theme.SuplaTheme
import org.supla.android.extensions.toTimestamp
import org.supla.android.features.thermostatdetail.history.HistoryDataSet
import org.supla.android.features.thermostatdetail.history.HistoryDetailViewState
import org.supla.android.features.thermostatdetail.history.data.ChartDataAggregation
import org.supla.android.features.thermostatdetail.history.data.ChartRange
import org.supla.android.ui.views.TextSpinner
import org.supla.android.ui.views.ThermostatChart

interface HistoryDetailProxy : BaseViewProxy<HistoryDetailViewState> {
  fun refresh() {}
  fun changeSetActive(setId: HistoryDataSet.Id) {}
  fun changeRange(range: ChartRange) {}
  fun changeAggregation(aggregation: ChartDataAggregation) {}
  fun moveRangeLeft() {}
  fun moveRangeRight() {}
}

@Composable
fun HistoryDetail(viewModel: HistoryDetailProxy) {
  val viewState by viewModel.getViewState().collectAsState()

  Column(
    modifier = Modifier.fillMaxSize()
  ) {
    DataSetsAndFilters(viewState = viewState, viewModel = viewModel)

    ThermostatChart(
      data = viewState.combinedData(LocalContext.current.resources),
      rangeStart = viewState.range?.start?.toTimestamp()?.toFloat(),
      rangeEnd = viewState.range?.end?.toTimestamp()?.toFloat(),
      emptyChartMessage = viewState.emptyChartMessage(LocalContext.current),
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = dimensionResource(id = R.dimen.distance_default))
    )

    BottomPagination(viewState = viewState, viewModel = viewModel)
  }
}

@Composable
private fun DataSetsAndFilters(viewState: HistoryDetailViewState, viewModel: HistoryDetailProxy) {
  val pullRefreshState = rememberPullRefreshState(
    refreshing = viewState.loading,
    onRefresh = { viewModel.refresh() },
    refreshThreshold = 58.dp
  )

  Box(
    modifier = Modifier
      .pullRefresh(pullRefreshState)
      .fillMaxWidth()
  ) {
    // Vertical scroll is needed to make pull refresh working
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
      DataSetsRow {
        viewState.sets.forEach { data ->
          DataSetItem(dataSet = data, onClick = { viewModel.changeSetActive(data.setId) })
        }
      }
      FiltersRow(viewState, viewModel)
    }

    PullRefreshIndicator(
      refreshing = viewState.loading,
      state = pullRefreshState,
      modifier = Modifier.align(Alignment.TopCenter),
      contentColor = MaterialTheme.colors.primary
    )
  }
}

@Composable
private fun FiltersRow(viewState: HistoryDetailViewState, viewModel: HistoryDetailProxy) =
  Row {
    TextSpinner(
      label = stringResource(id = R.string.history_range_label),
      options = viewState.rangesMap(LocalContext.current.resources),
      onOptionSelected = { viewModel.changeRange(it) },
      selectedOption = viewState.ranges?.selected,
      modifier = Modifier
        .padding(start = dimensionResource(id = R.dimen.distance_default))
    )
    Spacer(
      modifier = Modifier
        .weight(1f)
        .defaultMinSize(minWidth = dimensionResource(id = R.dimen.distance_small))
    )
    TextSpinner(
      label = stringResource(id = R.string.history_aggregation_label),
      options = viewState.aggregationsMap(LocalContext.current.resources),
      onOptionSelected = { viewModel.changeAggregation(it) },
      selectedOption = viewState.aggregations?.selected,
      modifier = Modifier
        .padding(end = dimensionResource(id = R.dimen.distance_default))
    )
  }

@Composable
private fun DataSetsRow(content: @Composable RowScope.() -> Unit) =
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState())
      .padding(
        horizontal = dimensionResource(id = R.dimen.distance_default),
        vertical = dimensionResource(id = R.dimen.distance_default)
      )
      .height(32.dp),
    horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.distance_small)),
    content = content
  )

@Composable
private fun DataSetItem(
  dataSet: HistoryDataSet,
  onClick: () -> Unit = { }
) {
  Row(
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    DataSetIcon(bitmap = dataSet.iconProvider(LocalContext.current).asImageBitmap())

    DataSetButton(
      text = dataSet.valueProvider(LocalContext.current),
      borderColor = colorResource(id = dataSet.color),
      onClick = onClick,
      colors = if (dataSet.active) {
        ButtonDefaults.buttonColors(
          backgroundColor = colorResource(id = dataSet.color),
          contentColor = colorResource(id = R.color.on_primary)
        )
      } else {
        ButtonDefaults.buttonColors(
          backgroundColor = colorResource(id = R.color.background),
          contentColor = colorResource(id = dataSet.color)
        )
      }
    )
  }
}

@Composable
private fun DataSetIcon(bitmap: ImageBitmap) =
  Image(
    bitmap = bitmap,
    contentDescription = null,
    alignment = Alignment.Center,
    modifier = Modifier.size(dimensionResource(id = R.dimen.button_small_height))
  )

@Composable
private fun DataSetButton(text: String, colors: ButtonColors, borderColor: Color, onClick: () -> Unit) =
  Button(
    onClick = onClick,
    modifier = Modifier
      .height(dimensionResource(id = R.dimen.button_small_height)),
    contentPadding = PaddingValues(horizontal = dimensionResource(id = R.dimen.distance_small)),
    colors = colors,
    shape = RoundedCornerShape(dimensionResource(id = R.dimen.button_small_height).div(2)),
    border = BorderStroke(2.dp, borderColor)
  ) {
    Text(
      text = text,
      fontSize = 14.sp,
      fontFamily = FontFamily(Font(R.font.open_sans_bold))
    )
  }

@Composable
private fun BottomPagination(viewState: HistoryDetailViewState, viewModel: HistoryDetailProxy) =
  viewState.rangeText(LocalContext.current)?.let {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.distance_small))
    ) {
      Spacer(modifier = Modifier.weight(1f))
      PaginationIcon(onClick = { viewModel.moveRangeLeft() }, modifier = Modifier.rotate(180f))
      Text(text = it, style = MaterialTheme.typography.subtitle2)
      PaginationIcon(onClick = { viewModel.moveRangeRight() }, enabled = viewState.shiftRightEnabled)
      Spacer(modifier = Modifier.weight(1f))
    }
  }

@Composable
private fun PaginationIcon(onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) =
  IconButton(onClick = onClick, enabled = enabled) {
    Icon(
      painter = painterResource(id = R.drawable.chevron_right),
      contentDescription = null,
      modifier = modifier,
      tint = if (enabled) MaterialTheme.colors.primary else colorResource(id = R.color.disabled)
    )
  }

@Preview
@Composable
private fun Preview() {
  SuplaTheme {
    Box(modifier = Modifier.background(MaterialTheme.colors.background)) {
      HistoryDetail(PreviewProxy())
    }
  }
}

private class PreviewProxy : HistoryDetailProxy {
  override fun getViewState(): StateFlow<HistoryDetailViewState> =
    MutableStateFlow(
      value = HistoryDetailViewState()
    )
}
