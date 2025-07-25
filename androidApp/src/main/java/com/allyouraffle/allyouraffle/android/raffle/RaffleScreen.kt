package com.allyouraffle.allyouraffle.android.raffle

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.allyouraffle.allyouraffle.android.R
import com.allyouraffle.allyouraffle.android.util.LoadingScreen
import com.allyouraffle.allyouraffle.android.util.SharedPreference
import com.allyouraffle.allyouraffle.android.util.errorToast
import com.allyouraffle.allyouraffle.model.RaffleResponse
import com.allyouraffle.allyouraffle.viewModel.RaffleViewModel
import com.allyouraffle.allyouraffle.viewModel.TicketViewModel
import kotlinx.coroutines.runBlocking


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RaffleListScreen(
    navController: NavHostController,
    isFree: Boolean,
    viewModel: RaffleViewModel
) {
    println("RAFFLE SCREEN$isFree")
    val raffleList by viewModel.raffleList.collectAsState()
    val error = viewModel.error.collectAsState()
    val context = LocalContext.current
    val sharedPreference = SharedPreference(context)
    val jwt = sharedPreference.getJwt()
    LaunchedEffect(Unit) {
        viewModel.initRaffle(isFree)
    }
    if (error.value != null) {
        errorToast(context, error.value!!, viewModel)
//        Toast.makeText(context,error.value,Toast.LENGTH_SHORT).show()
//        viewModel.setNullError()
    }

    RaffleScreenBody(viewModel, isFree, jwt, raffleList, navController)


}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun RaffleScreenBody(
    viewModel: RaffleViewModel,
    isFree: Boolean,
    jwt: String,
    raffleList: List<RaffleResponse>,
    navController: NavHostController
) {
    var refreshing by remember { mutableStateOf(false) }
    val ticketViewModel = remember {
        TicketViewModel()
    }
    val tickets = ticketViewModel.ticketCount.collectAsState()
    val pullRefreshState = rememberPullRefreshState(refreshing, onRefresh = {
        refreshing = true
        runBlocking {
            viewModel.loadRaffles(isFree)
            ticketViewModel.loadTickets(jwt)
        }
        refreshing = false
    })
    LaunchedEffect(Unit) {
        ticketViewModel.loadTickets(jwt)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
//        Logo(55.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Banner(message = if (isFree) "광고 래플" else "천원 래플", tickets)
        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxHeight()) {
            PullRefreshIndicator(
                refreshing = refreshing,
                state = pullRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .zIndex(1f)
            )
            // 상품 리스트

            LazyColumn(
                modifier = Modifier
                    .pullRefresh(pullRefreshState)
                    .padding(horizontal = 10.dp)
                    .fillMaxHeight()
            ) {
                items(raffleList) { raffle ->
                    ProductCard(raffle, navController, isFree)
                }
            }
            if(viewModel.loading.value){
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun ProductCard(
    raffle: RaffleResponse,
    navController: NavController,
    isFree: Boolean
) {
    val rowHeight = 120.dp
    Card(
        modifier = Modifier
            .padding(horizontal = 2.dp, vertical = 5.dp)
            .clickable {
                navController.navigate("raffle/" + raffle.id + "/" + isFree)
            },
        elevation = 4.dp,
        shape = RoundedCornerShape(10.dp),
        backgroundColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight)
                .background(MaterialTheme.colorScheme.onPrimary),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(modifier = Modifier.aspectRatio(1f)) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(raffle.item.imageUrl)
                            .build(),
                        error = painterResource(
                            R.drawable.baseline_error_outline_24
                        )
                    ),
                    contentDescription = raffle.item.name,
                    modifier = Modifier
                        .fillMaxHeight(),

                    contentScale = ContentScale.Crop
                )
            }


            RaffleRightColumn(raffle, rowHeight, isFree)

        }

    }
}

@Composable
fun RaffleRightColumn(
    raffle: RaffleResponse,
    rowHeight: Dp,
    isFree: Boolean
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onPrimary)
            .height(rowHeight)
            .padding(start = 3.dp),
        verticalArrangement = Arrangement.Center,
    ) {

        Text(
            text = raffle.item.name,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 5.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(15.dp))
        LinearProgressIndicator(
            progress = { raffle.currentCount.toFloat() / raffle.totalCount },
            modifier = Modifier
                .fillMaxWidth()
                .height(15.dp)
                .padding(3.dp)
                .clip(RoundedCornerShape(12.dp)),
            color = if (isFree) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.tertiary,
            trackColor = MaterialTheme.colorScheme.onTertiary,
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = ((raffle.currentCount.toFloat() / raffle.totalCount.toFloat()) * 100).toInt()
                .toString() + "%",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp,
            textAlign = TextAlign.Right,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 10.dp)
        )
    }
}