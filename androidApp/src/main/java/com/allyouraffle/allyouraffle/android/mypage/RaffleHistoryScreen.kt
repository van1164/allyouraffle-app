package com.allyouraffle.allyouraffle.android.mypage

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.allyouraffle.allyouraffle.android.R
import com.allyouraffle.allyouraffle.android.util.LoadingScreen
import com.allyouraffle.allyouraffle.android.util.OnBottomReached
import com.allyouraffle.allyouraffle.android.util.SharedPreference
import com.allyouraffle.allyouraffle.android.util.errorToast
import com.allyouraffle.allyouraffle.network.PurchaseHistory
import com.allyouraffle.allyouraffle.viewModel.RaffleHistoryViewModel
import kotlinx.coroutines.launch

@Composable
fun RaffleHistoryScreen(myPageNavController: NavHostController) {
    val scope = rememberCoroutineScope()
    val viewModel = remember { RaffleHistoryViewModel() }
    val error = viewModel.error.collectAsState()
    val context = LocalContext.current
    val sharedPreference = SharedPreference(context)
    val jwt = sharedPreference.getJwt()
    LaunchedEffect(Unit) {
        if (viewModel.purchaseHistory.value.isEmpty()) {
            viewModel.initHistory(jwt)
        }
    }
    if (error.value != null) {
        errorToast(context, error.value!!, viewModel)
    }

    RaffleHistoryBody(viewModel, jwt, myPageNavController)

}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RaffleHistoryBody(
    viewModel: RaffleHistoryViewModel,
    jwt: String,
    myPageNavController: NavHostController,
) {
    val scope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(refreshing, onRefresh = {
        refreshing = true
        scope.launch {
            viewModel.initHistory(jwt)
        }
        refreshing = false
    })
    val loading = viewModel.loading.collectAsState()
    val purchaseHistory = viewModel.purchaseHistory.collectAsState()
    val listState = rememberLazyListState()

    listState.OnBottomReached {
        scope.launch {
            viewModel.loadHistory(jwt)
        }
    }

    Column(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .zIndex(1f)
        )


        Text(
            "래플 이력",
            modifier = Modifier.padding(top = 10.dp, bottom = 20.dp, start = 10.dp),
            color = MaterialTheme.colorScheme.tertiary,
            fontFamily = MaterialTheme.typography.titleLarge.fontFamily,
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 40.sp, shadow = Shadow(
                    color = Color.DarkGray, offset = Offset(2.0f, 2.0f), blurRadius = 3f
                )
            ),
        )
        LazyColumn(
            modifier = Modifier
                .pullRefresh(pullRefreshState)
                .fillMaxHeight(), state = listState
        ) {
            items(purchaseHistory.value.size) { index ->
                ProductCard(purchaseHistory = purchaseHistory.value[index])
            }
        }

        if (loading.value) {
            LoadingScreen()
        }

    }


}

@Composable
fun ProductCard(
    purchaseHistory: PurchaseHistory,
) {
    val rowHeight = 120.dp
    Card(
        modifier = Modifier.padding(horizontal = 2.dp, vertical = 5.dp).background(Color.Transparent),
        elevation = 4.dp,
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(modifier = Modifier.aspectRatio(1f)) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(purchaseHistory.raffle.item.imageUrl).build(),
                        error = painterResource(
                            R.drawable.baseline_error_outline_24
                        )
                    ),
                    contentDescription = purchaseHistory.raffle.item.name,
                    modifier = Modifier.fillMaxHeight(),
                    alpha = 0.3f,

                    contentScale = ContentScale.Crop
                )

                if (purchaseHistory.raffle.winner == null) {
                    androidx.compose.material3.Text(
                        text = "추첨전",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 30.sp, shadow = Shadow(
                                color = Color.DarkGray, offset = Offset(1.0f, 1.0f), blurRadius = 3f
                            )
                        ),
                        modifier = Modifier.align(Alignment.Center),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                } else if (purchaseHistory.isWinner) {
                    androidx.compose.material3.Text(
                        text = "당첨",
                        color = MaterialTheme.colorScheme.secondary,
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 30.sp, shadow = Shadow(
                                color = Color.DarkGray, offset = Offset(1.0f, 1.0f), blurRadius = 3f
                            )
                        ),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                } else {
                    androidx.compose.material3.Text(
                        text = "낙첨",
                        color = Color.Red,
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 30.sp, shadow = Shadow(
                                color = Color.DarkGray, offset = Offset(1.0f, 1.0f), blurRadius = 3f
                            )
                        ),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }


            RaffleRightColumn(purchaseHistory, rowHeight)

        }

    }
}

@Composable
fun RaffleRightColumn(
    purchaseHistory: PurchaseHistory,
    rowHeight: Dp,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onPrimary)
            .height(rowHeight)
            .fillMaxWidth()
            .padding(start = 3.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        androidx.compose.material3.Text(
            text = purchaseHistory.raffle.item.name,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 5.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )




        Spacer(modifier = Modifier.height(5.dp))
        androidx.compose.material3.Text(
            text = "응모 개수 : ${purchaseHistory.count}",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp,
            textAlign = TextAlign.Right,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 10.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))
        androidx.compose.material3.Text(
            text = "래플 고유 ID : ${purchaseHistory.raffle.id}",
            color = Color.Gray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(end = 10.dp)
                .align(Alignment.End),
            maxLines = 1,
            textAlign = TextAlign.Right,
            overflow = TextOverflow.Ellipsis,
        )

        if (purchaseHistory.raffle.winner == null) {
            androidx.compose.material3.Text(
                text = "추첨 전",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 10.sp,
                textAlign = TextAlign.Right,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 10.dp)
            )
        } else {
            androidx.compose.material3.Text(
                text = "당첨자 : " + purchaseHistory.raffle.winner!!.nickname + "(" + purchaseHistory.raffle.winner!!.phoneNumber?.substring(
                    4..7
                ) + ")",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 10.sp,
                textAlign = TextAlign.Right,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 10.dp)
            )
        }

    }
}