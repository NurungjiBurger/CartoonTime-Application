package com.alltimes.cartoontime.ui.screen.moneytransaction.charge

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.alltimes.cartoontime.R
import com.alltimes.cartoontime.data.model.ui.ScreenType
import com.alltimes.cartoontime.ui.screen.composable.Loading
import com.alltimes.cartoontime.ui.screen.composable.Numpad
import com.alltimes.cartoontime.ui.viewmodel.ChargeViewModel

@Composable
fun ChargePasswordInputScreen(viewModel: ChargeViewModel) {

    // viewmodel variable
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val networkStatus by viewModel.networkStatus.collectAsState()

    // screen variable
    val imgSize = 40.dp
    val imgSpace = 10.dp

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFF4F2EE))
    ) {
        val (backButton, title, description0, description1, passwordRow, description2, numPad) = createRefs()

        // 뒤로가기 버튼
        Box(
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
                .background(Color.Transparent)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        viewModel.goScreen(ScreenType.CHARGEPOINTINPUT)
                        viewModel.initializePassword()
                    }
                )
                .constrainAs(backButton) {
                    top.linkTo(parent.top, margin = 10.dp)
                    start.linkTo(parent.start, margin = 10.dp)
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back Icon",
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
            )
        }

        // 상단 타이틀
        Text(
            text = "포인트 충전하기",
            fontSize = 30.sp,
            color = Color.Black,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(backButton.top)
                bottom.linkTo(backButton.bottom)
                start.linkTo(parent.start, margin = 10.dp)
                end.linkTo(parent.end, margin = 10.dp)
                width = Dimension.wrapContent
            }
        )

        Text(
            text = "간편 비밀번호",
            fontSize = 36.sp,
            color = Color.Black,
            modifier = Modifier
                .constrainAs(description0) {
                    top.linkTo(title.bottom, margin = 50.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Text(
            text = "비밀번호를 입력해주세요.",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier
                .constrainAs(description1) {
                    top.linkTo(description0.bottom, margin = 50.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )


        // 비밀번호 6자리 표시
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 50.dp, end = 50.dp)
                .constrainAs(passwordRow) {
                    top.linkTo(description1.bottom, margin = 50.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(6) { i ->
                Image(
                    painter = if (password.length > i)
                        painterResource(id = R.drawable.ic_filled_circle)
                    else painterResource(id = R.drawable.ic_empty_circle),
                    contentDescription = "password${i + 1}",
                    modifier = Modifier
                        .size(imgSize)
                        .padding(horizontal = imgSpace)
                )
            }
        }

        // 비밀번호를 잊으셨나요 ?
        Box(
            modifier = Modifier
                .constrainAs(description2) {
                    bottom.linkTo(numPad.top, 10.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Text(
                text = "비밀번호를 잊어버리셨나요?",
                fontSize = 16.sp,
                color = Color.Gray,
            )
        }


        // 숫자패드
        // 0 ~ 9, 삭제 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(numPad) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Numpad(viewModel)
        }
    }

    // 로딩 다이얼로그
    isLoading?.let { Loading("충전 중...", isLoading = it, onDismiss = { /* Dismiss Logic */ }) }

    // 인터넷 로딩 다이얼로그 표시
    networkStatus?.let { Loading("인터넷 연결 시도중 ... ", isLoading = it, onDismiss = { /* Dismiss Logic */ }) }

}