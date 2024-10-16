package com.alltimes.cartoontime.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alltimes.cartoontime.data.model.ui.ScreenType
import com.alltimes.cartoontime.ui.screen.main.BookDetailScreen
import com.alltimes.cartoontime.ui.screen.main.BookNavScreen
import com.alltimes.cartoontime.ui.screen.main.BookRecommendScreen
import com.alltimes.cartoontime.ui.screen.main.ConfirmScreen
import com.alltimes.cartoontime.ui.screen.main.MainScreen
import com.alltimes.cartoontime.ui.viewmodel.BootViewModel
import com.alltimes.cartoontime.ui.viewmodel.MainViewModel
import com.alltimes.cartoontime.ui.viewmodel.SignUpViewModel
import com.alltimes.cartoontime.utils.NavigationHelper
import com.alltimes.cartoontime.utils.PermissionsHelper

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavController
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewModelProvider를 사용하여 ViewModel 초기화
        viewModel = MainViewModel(application, this)
        viewModel.accelerometerStart(lifecycleOwner = this)
        viewModel.UpdateUserInfo()

        // 권한 요청 부분을 PermissionsHelper로 처리
        if (!PermissionsHelper.hasAllPermissions(this)) {
            PermissionsHelper.requestPermissions(this)
        } else {
            initializeApp() // 권한이 이미 있을 경우 초기화
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.onResumeAll()
    }

    override fun onPause() {
        super.onPause()

        viewModel.onPuaseAll()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // 모든 권한이 허가되었는지 확인
        if (PermissionsHelper.allPermissionsGranted(grantResults)) {
            initializeApp() // 권한이 허가되면 앱 초기화
        } else {
            // 권한이 허가되지 않았을 경우 앱 종료
            ActivityCompat.finishAffinity(this) // 앱을 완전히 종료
        }
    }

    private fun initializeApp() {
        viewModel.accelerometerStart(lifecycleOwner = this)

        setContent {
            navController = rememberNavController()

            NavHost(navController as NavHostController, startDestination = "mainscreen") {
                composable("mainscreen") { MainScreen(viewModel = viewModel) }
                composable("bookRecommendScreen") { BookRecommendScreen(viewModel = viewModel) }
                composable("bookDetailScreen") { BookDetailScreen(viewModel = viewModel) }
                composable("bookNavScreen") { BookNavScreen(viewModel = viewModel) }
                composable("confirmScreen") { ConfirmScreen(viewModel = viewModel) }
            }
        }

        // ViewModel에서 Activity 전환 요청 처리
        viewModel.activityNavigationTo.observe(this) { navigationTo ->
            navigationTo?.activityType?.let { activityType ->
                NavigationHelper.navigate(this, activityType)
            }
        }

        // ViewModel에서 Screen 전환 요청 처리
        viewModel.screenNavigationTo.observe(this) { navigationTo ->
            navigationTo?.screenType?.let { screenType ->
                navigateToScreen(screenType)
            }
        }
    }

    // 스크린 전환을 처리하는 함수로 분리하여 처리
    private fun navigateToScreen(screenType: ScreenType) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        val route = when (screenType) {
            ScreenType.MAIN -> "mainscreen"
            ScreenType.BOOKRECOMMEND -> "bookRecommendScreen"
            ScreenType.BOOKDETAIL -> "bookDetailScreen"
            ScreenType.BOOKNAV -> "bookNavScreen"
            ScreenType.CONFIRM -> "confirmScreen"
            else -> return
        }

        // 현재 화면이 이동하려는 화면과 다를 경우에만 화면 전환
        if (currentRoute != route) {
            navController.navigate(route) {
                // 메인 -> 책 추천, 책 추천 -> 책 상세 일 경우 스택 초기화 X
                if ((currentRoute == "bookRecommendScreen" && route == "bookDetailScreen")
                    || (currentRoute == "mainscreen" && route == "bookRecommendScreen")
                ) {
                    // 아무 행동 하지 않음
                } else {
                    // 그 외에는 현재 화면 스택에서 제거 && 화면 이동
                    popUpTo(currentRoute.toString()) { inclusive = true } // 현재 화면 스택에서 제거
                }
                // 동일 화면 중복 쌓임 방지
                launchSingleTop = true
            }

            // 스크린 전환 시 각속도 측정 멈추기
            viewModel.accelerometerStop()

            // 메인 스크린으로 이동할 경우에만 각속도 측정 시작
            if (screenType == ScreenType.MAIN) {
                viewModel.accelerometerStart(lifecycleOwner = this)
            }
        }
    }
}