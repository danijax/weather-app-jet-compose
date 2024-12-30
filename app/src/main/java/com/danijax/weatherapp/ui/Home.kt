package com.danijax.weatherapp.ui


import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.danijax.weatherapp.R
import com.danijax.weatherapp.core.Dimens
import com.danijax.weatherapp.core.util.Graph
import com.danijax.weatherapp.domain.model.WeatherInfo
import com.danijax.weatherapp.ui.ui.theme.BGRAY
import com.danijax.weatherapp.ui.ui.theme.WeatherAppTheme
import com.danijax.weatherapp.ui.ui.theme.popinsFamily
import timber.log.Timber


@Composable
fun MainHomeSearchBar(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            HomeSearchBar(
                query = searchQuery,
                onQueryChange = {
                    searchQuery = it
                    viewModel.onSearchQueryChanged(it)
                },
                onSearchClicked = {
                    Timber.tag("Query").d(searchQuery)
                    navController.navigate(Graph.SEARCH)
                },
                content = {
                    MainHomeScreen()
                }
            )

        }
    }
}

@Composable
fun SearchResultScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            HomeSearchBar(
                query = searchQuery,
                enabled = true,
                onQueryChange = {
                    searchQuery = it
                    viewModel.onSearchQueryChanged(it)
                },
                onSearchClicked = {
                    Timber.tag("Query").d(searchQuery)
                    //navController.navigate("SEARCH")
                },
                content = {
                    WeatherSearchScreen(navController = navController)
                }
            )

        }
    }
}

@Composable
fun MainHomeScreen(
    modifier: Modifier = Modifier, navController: NavHostController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        when (uiState) {
            WeatherUiState.Empty -> {
                EmptyCity()
            }

            is WeatherUiState.Error -> {
                Toast.makeText(LocalContext.current, uiState.message, Toast.LENGTH_SHORT)
                    .show()
                EmptyCity()
            }

            is WeatherUiState.Success -> {
                WeatherCard(
                    temperature = uiState.weatherInfo.temperature,
                    city = uiState.weatherInfo.cityName,
                    humidity = uiState.weatherInfo.humidity,
                    uv = uiState.weatherInfo.uv,
                    icon = uiState.weatherInfo.iconUrl
                )

            }

            WeatherUiState.Loading -> {
                LoadingScreen(message = "Loading Weather Data", modifier = Modifier)
            }
        }
    }
}

@Composable
fun WeatherSearchScreen(
    modifier: Modifier = Modifier, navController: NavHostController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel()
) {
    val searchUiState = viewModel.searchUiState.collectAsStateWithLifecycle().value
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        when (searchUiState) {
            WeatherSearchUiState.Empty -> {
                EmptySearchResultScreen()
            }

            is WeatherSearchUiState.Error -> {
                EmptySearchResultScreen()
            }

            WeatherSearchUiState.Loading -> {
                LoadingScreen(message = "Loading Weather Data", modifier = Modifier)
            }

            is WeatherSearchUiState.Success -> {
                DisplaySearchResult(searchUiState.weatherInfo, onResultClicked = {
                    viewModel.updateSavedCity(searchUiState.weatherInfo.cityName)
                    navController.popBackStack()
                })
            }
        }
    }
}


@Composable
fun SearchBar(
    text: String,
    hint: String,
    modifier: Modifier = Modifier,
    isEnabled: (Boolean) = false,
    height: Dp = Dimens.dp46,
    elevation: Dp = Dimens.dp327,
    cornerShape: Shape = RoundedCornerShape(Dimens.dp16),
    backgroundColor: Color = Color(0xFFF2F2F2),
    onSearchClicked: () -> Unit = {},
    onTextChange: (String) -> Unit = {},
) {
    val focusRequester = remember { FocusRequester() }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = elevation, shape = cornerShape)
            .background(color = backgroundColor, shape = cornerShape),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            modifier = modifier
                .weight(5f)
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .padding(horizontal = Dimens.dp24)
                .clickable {
                    Timber
                        .tag("Focus Handler")
                        .d("Clicked")
                    onSearchClicked()
                },
            textStyle = TextStyle(
                fontFamily = popinsFamily, fontSize = Dimens.sp15, lineHeight = Dimens.sp22,
                fontWeight = FontWeight.W400, textAlign = TextAlign.Left
            ),
            value = text,
            onValueChange = {
                onTextChange(it)
            },
            enabled = isEnabled,
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    Text(
                        text = hint,
                        color = Color(0xFFC4C4C4),
                        fontSize = Dimens.sp16,
                        fontWeight = FontWeight.W400,
                    )
                }
                innerTextField()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(onSearch = { onSearchClicked() }),
            singleLine = true
        )
        Box(
            modifier = modifier
                .weight(1f)
                .size(Dimens.dp40)
                .background(color = Color.Transparent, shape = CircleShape)
                .clickable {
                    if (text.isNotEmpty()) {
                        Timber
                            .tag("Box Handler")
                            .d("Clicked")
                        onTextChange("")
                    }
                },
        ) {
            if (text.isNotEmpty()) {
                Icon(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(Dimens.dp10),
                    painter = painterResource(id = R.drawable.search_24px),
                    contentDescription = stringResource(R.string.search),

                    )
            } else {
                Icon(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(Dimens.dp10),
                    painter = painterResource(id = R.drawable.search_24px),
                    contentDescription = stringResource(R.string.search),
                )
            }
        }
    }
}

@Preview()
@Composable
fun Home() {
    WeatherAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            var searchQuery by remember { mutableStateOf("") }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HomeSearchBar(
                    modifier = Modifier.padding(innerPadding),
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                    },
                    onSearchClicked = {},
                    content = {}

                )
                Spacer(modifier = Modifier.height(16.dp))
                //WeatherCard(40,)
                Spacer(modifier = Modifier.height(16.dp))
                //ConditionsCard()
            }
        }
    }
}

@Composable
fun HomeSearchBar(
    modifier: Modifier = Modifier,
    query: String,
    enabled: Boolean = false,
    onQueryChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(
            hint = stringResource(R.string.search),
            isEnabled = enabled,
            cornerShape = RoundedCornerShape(Dimens.dp20),
            onTextChange = onQueryChange,
            text = query,
            onSearchClicked = {
                Timber.tag("Home Search").d("Clicked")
                onSearchClicked()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        content()

    }
}

@Composable
fun EmptyCity() {
    Column(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No City Selected", style = TextStyle(
                fontFamily = popinsFamily, fontSize = Dimens.sp30, lineHeight = Dimens.sp45,
                fontWeight = FontWeight.W600, textAlign = TextAlign.Center
            )
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Please Search For A City", style = TextStyle(
                fontFamily = popinsFamily, fontSize = Dimens.sp15, lineHeight = Dimens.sp22,
                fontWeight = FontWeight.W600, textAlign = TextAlign.Center
            )
        )

    }
}

@Composable
fun WeatherCard(icon: String, temperature: Double, city: String, uv: Double, humidity: Int) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        WeatherIcon(
            iconUrl = icon,
            contentDescription = "",
            modifier = Modifier.size(width = 123.dp, height = 113.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = city, style = TextStyle(
                    fontFamily = popinsFamily, fontSize = Dimens.sp30, lineHeight = Dimens.sp45,
                    fontWeight = FontWeight.W600, textAlign = TextAlign.Center
                )
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                modifier = Modifier,
                painter = painterResource(id = R.drawable.ic_city),
                contentDescription = stringResource(R.string.search),
            )


        }

        Spacer(Modifier.width(8.dp))
        DegreeSymbolView(
            modifier = Modifier,
            temperature = temperature.toString(),
            temperatureTextSize = Dimens.sp500,
            symbolTextSize = 24.sp
        )
        Spacer(Modifier.height(24.dp))
        ConditionsCard(feelsLike = temperature, uv = uv, humidity = humidity)


    }
}


@Composable
fun ConditionsCard(humidity: Int, uv: Double, feelsLike: Double) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(shape = RoundedCornerShape(16.dp), color = Color(0xFFF2F2F2))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Humidity", style = TextStyle(
                        fontFamily = popinsFamily,
                        fontSize = Dimens.sp12,
                        color = Color(0xFFC4C4C4),
                        fontWeight = FontWeight.W600,
                        lineHeight = Dimens.sp18,
                        textAlign = TextAlign.Center
                    )
                )
                Text(
                    "$humidity %", style = TextStyle(
                        fontFamily = popinsFamily,
                        fontSize = Dimens.sp15,
                        color = Color(0xFFC4C4C4),
                        fontWeight = FontWeight.W600,
                        lineHeight = Dimens.sp22,
                        textAlign = TextAlign.Center
                    )
                )

            }
            Spacer(modifier = Modifier.width(Dimens.dp16))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "UV", style = TextStyle(
                        fontFamily = popinsFamily,
                        fontSize = Dimens.sp12,
                        color = Color(0xFFC4C4C4),
                        fontWeight = FontWeight.W600,
                        lineHeight = Dimens.sp18,
                        textAlign = TextAlign.Center
                    )
                )
                Text(
                    "$uv", style = TextStyle(
                        fontFamily = popinsFamily,
                        fontSize = Dimens.sp15,
                        color = Color(0xFFC4C4C4),
                        fontWeight = FontWeight.W600,
                        lineHeight = Dimens.sp22,
                        textAlign = TextAlign.Center
                    )
                )

            }
            Spacer(modifier = Modifier.width(Dimens.dp16))
            Column( modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                Text(
                    "Feels Like", style = TextStyle(
                        fontFamily = popinsFamily,
                        fontSize = Dimens.sp12,
                        color = Color(0xFFC4C4C4),
                        fontWeight = FontWeight.W600,
                        lineHeight = Dimens.sp18,
                        textAlign = TextAlign.Center
                    )
                )
                    Text(
                        "$feelsLike °", style = TextStyle(
                            fontFamily = popinsFamily,
                            fontSize = Dimens.sp15,
                            color = Color(0xFFC4C4C4),
                            fontWeight = FontWeight.W600,
                            lineHeight = Dimens.sp18,
                            textAlign = TextAlign.Center
                        )
                    )
            }
        }
    }

}

@Composable
fun SearchResultCard(weatherInfo: WeatherInfo, onClick: () -> Unit) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(shape = RoundedCornerShape(16.dp), color = Color(0xFFF2F2F2))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    weatherInfo.cityName, style = TextStyle(
                        fontFamily = popinsFamily,
                        fontSize = Dimens.sp20,
                        color = Color(0xFF2C2C2C),
                        fontWeight = FontWeight.W600,
                        lineHeight = Dimens.sp30,
                        textAlign = TextAlign.Left
                    )
                )

                Row(verticalAlignment = Alignment.Top) {
                    Text(
                        "${weatherInfo.temperature}", style = TextStyle(
                            fontFamily = popinsFamily,
                            fontSize = Dimens.sp60,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.W600,
                            lineHeight = Dimens.sp90,
                            textAlign = TextAlign.Left
                        )
                    )
                    Spacer(modifier = Modifier.width(Dimens.dp8))
                    Text(
                        text = "°",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }


            }
            Spacer(modifier = Modifier.width(Dimens.dp16))

            WeatherIcon(
                weatherInfo.iconUrl,
                "Weather Icon",
                modifier = Modifier.size(width = 83.dp, height = 67.dp)
            )
        }
    }

}

@Composable
fun DisplaySearchResult(weatherInfo: WeatherInfo, onResultClicked: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchResultCard(weatherInfo = weatherInfo, onClick = onResultClicked)
    }
}

@Composable
fun WeatherIcon(
    iconUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data("https:$iconUrl")
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = modifier.size(64.dp),
        onError = { error ->
            Timber.e(error.result.throwable, "Error loading weather icon")
        }
    )
}

@Composable
fun LoadingScreen(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading animation")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(alpha)
        )
    }

}

@Composable
fun EmptySearchResultScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {

    }

}

@Composable
fun TemperatureDisplay(
    city: String = "Mumbai",
    temperature: Int = 20,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .wrapContentSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // City name
        Text(
            text = city,
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Temperature with degree symbol
        DegreeSymbolView(
            modifier = Modifier,
            temperature = temperature.toString(),
            temperatureTextSize = 64.sp,
            symbolTextSize = 24.sp
        )
//        Row(
//            verticalAlignment = Alignment.Top
//        ) {
//            Text(
//                text = temperature.toString(),
//                fontSize = 48.sp,
//                fontWeight = FontWeight.Medium,
//                color = MaterialTheme.colorScheme.onSurface
//            )
//            Text(
//                text = "°",
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Medium,
//                color = MaterialTheme.colorScheme.onSurface,
//                modifier = Modifier.padding(top = 4.dp)
//            )
//        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun TemperatureDisplayPreview() {
    MaterialTheme {
        TemperatureDisplay()
    }
}

@Composable
fun ColumnScope.DegreeSymbolView(
    modifier: Modifier,
    temperature: String,
    temperatureTextSize: TextUnit,
    symbolTextSize: TextUnit
){
    Row( modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = temperature,
            fontSize = temperatureTextSize,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = "°",
            fontSize = symbolTextSize,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
