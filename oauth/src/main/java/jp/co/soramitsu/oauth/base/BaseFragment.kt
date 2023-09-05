package jp.co.soramitsu.oauth.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import jp.co.soramitsu.oauth.base.navigation.MainRouter
import jp.co.soramitsu.oauth.theme.AuthSdkTheme


@AndroidEntryPoint
internal abstract class BaseFragment : Fragment() {

    @Inject
    lateinit var mainRouter: MainRouter

    private lateinit var navHostController: NavHostController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val navController = rememberNavController()
                navHostController = navController
                mainRouter.attachNavController(requireActivity(), navController)
                AuthSdkTheme {
                    NavGraph(navHostController = navController)
                }
            }
        }
    }

    @Composable
    open fun NavGraph(navHostController: NavHostController) = Unit

    override fun onDestroyView() {
        super.onDestroyView()
        if (::navHostController.isInitialized) {
            mainRouter.detachNavController(requireActivity(), navHostController)
        }
    }
}