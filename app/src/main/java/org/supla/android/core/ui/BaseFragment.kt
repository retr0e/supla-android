package org.supla.android.core.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import org.supla.android.lib.SuplaClientMessageHandler
import org.supla.android.lib.SuplaClientMessageHandler.OnSuplaClientMessageListener
import org.supla.android.lib.SuplaClientMsg

abstract class BaseFragment<S : ViewState, E : ViewEvent>(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

  private val suplaMessageListener: OnSuplaClientMessageListener = OnSuplaClientMessageListener { onSuplaMessage(it) }

  protected abstract fun getViewModel(): BaseViewModel<S, E>

  @CallSuper
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    lifecycleScope.launchWhenStarted {
      getViewModel().isLoadingEvent().collect { isLoading ->
        (requireActivity() as? BaseActivity)?.showLoading(isLoading)
      }
    }

    lifecycleScope.launchWhenStarted { getViewModel().getViewEvents().collect { event -> handleEvents(event) } }
    lifecycleScope.launchWhenStarted { getViewModel().getViewState().collect { state -> handleViewState(state) } }
  }

  @CallSuper
  override fun onStart() {
    super.onStart()
    SuplaClientMessageHandler.getGlobalInstance().registerMessageListener(suplaMessageListener)
  }

  @CallSuper
  override fun onStop() {
    SuplaClientMessageHandler.getGlobalInstance().unregisterMessageListener(suplaMessageListener)
    super.onStop()
  }

  protected abstract fun handleViewState(state: S)

  protected abstract fun handleEvents(event: E)

  protected open fun onSuplaMessage(message: SuplaClientMsg) {

  }
}
