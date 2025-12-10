package be.he2b.scoutpocket.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import be.he2b.scoutpocket.database.entity.Event
import be.he2b.scoutpocket.model.Section
import be.he2b.scoutpocket.model.backgroundColor
import be.he2b.scoutpocket.model.formattedDateShort
import be.he2b.scoutpocket.model.formattedTimeRange
import be.he2b.scoutpocket.model.textColor
import be.he2b.scoutpocket.navigation.BottomNavItem
import be.he2b.scoutpocket.ui.theme.ScoutPocketTheme
import be.he2b.scoutpocket.viewmodel.AgendaViewModel
import be.he2b.scoutpocket.viewmodel.AgendaViewModelFactory
import com.composables.icons.lucide.Clock
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import java.time.LocalDate
import java.time.LocalTime

private val bottomNavItems = listOf(
    BottomNavItem.Agenda,
    BottomNavItem.Members,
    BottomNavItem.About,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
) {
    val navBarController = rememberNavController()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        // contentWindowInsets = WindowInsets(0,0,0,0),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val navBackStackEntry by navBarController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    val currentItem = bottomNavItems.find { it.route == currentRoute }
                    Text(
                        text = currentItem?.label ?: "Titre",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        },
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            NavHost(
                navController = navBarController,
                startDestination = BottomNavItem.Agenda.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(BottomNavItem.Agenda.route) {
                    AgendaScreen(modifier = Modifier.fillMaxSize())
                }
                composable(BottomNavItem.Members.route) {
                    MembersScreen(modifier = Modifier.fillMaxSize())
                }
                composable(BottomNavItem.About.route) {
                    AboutScreen(modifier = Modifier.fillMaxSize())
                }
            }

            BottomBar(
                navController = navBarController,
                items = bottomNavItems,
                modifier = Modifier
                    .align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
fun AgendaScreen(
    modifier: Modifier = Modifier,
    viewModel: AgendaViewModel = viewModel(
        factory = AgendaViewModelFactory(LocalContext.current.applicationContext)
    ),
) {
    val events = viewModel.events.value
    val isLoading = viewModel.isLoading.value
    val errorMessage = viewModel.errorMessage.value

    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            Text("Ça charge...")
        } else if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        } else if (events.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp, 0.dp),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(events) { event ->
                    EventCard(event = event)
                }
            }
        } else {
            Box(
                modifier = modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Aucun évènement trouvé",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp),
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(25.dp),
            ),
        shape = RoundedCornerShape(25.dp),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium,
                )
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                // Section
                Box(
                    modifier = modifier
                        .background(
                            color = event.section.backgroundColor(),
                            shape = RoundedCornerShape(24.dp),
                        )
                        .padding(12.dp, 4.dp),
                ) {
                    Text(
                        text = event.section.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = event.section.textColor(),
                    )
                }

                // Date
                Box(
                    modifier = modifier
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(24.dp),
                        )
                        .padding(12.dp, 4.dp),
                ) {
                    Text(
                        text = event.formattedDateShort(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }

            // Title
            Text(
                text = event.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )

            // Infos
            Row(
                modifier = modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                // Time
                Column(
                    modifier = modifier
                        .weight(1f)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.large,
                        )
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    ) {
                        Icon(
                            imageVector = Lucide.Clock,
                            contentDescription = null,
                            modifier = modifier.height(20.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                        Text(
                            text = "Heures",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Text(
                        text = event.formattedTimeRange(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                    )
                }

                // Location
                Column(
                    modifier = modifier
                        .weight(1f)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = MaterialTheme.shapes.large,
                        )
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        modifier = modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    ) {
                        Icon(
                            imageVector = Lucide.MapPin,
                            contentDescription = null,
                            modifier = modifier.height(20.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                        Text(
                            text = "Lieu",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Text(
                        text = event.location,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventCardPreview() {
    val sampleEvent = Event(
        id = 1,
        name = "Réunion Classique",
        section = Section.LOUVETEAUX,
        date = LocalDate.now().plusDays(10),
        startTime = LocalTime.of(14, 0),
        endTime = LocalTime.of(17, 30),
        location = "Au local de l'unité"
    )


    ScoutPocketTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            EventCard(event = sampleEvent)
        }
    }
}


@Composable
fun BottomBar(
    navController: NavController,
    items: List<BottomNavItem>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp, 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        NavigationBar(
            navController = navController,
            items = items,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun NavigationBar(
    navController: NavController,
    items: List<BottomNavItem>,
    modifier: Modifier,
) {
    // Large Pill
    Box(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(
                shape = CircleShape,
                shadow = Shadow(
                    radius = 15.dp,
                    spread = 0.dp,
                    color = Color(0xFF000000).copy(alpha = 0.10f),
                    offset = DpOffset(x = 0.dp, y = 2.dp)
                )
            )
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
            .padding(4.dp),
        contentAlignment = Alignment.Center,
    ) {
        // Items
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            val navBarStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBarStackEntry?.destination

            items.forEach { item ->
                NavigationBarItem(
                    item = item,
                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun RowScope.NavigationBarItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        Color.Transparent
    }

    Column(
        modifier = modifier
            .weight(1f)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

// @Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    BottomBar(
        navController = rememberNavController(),
        items = bottomNavItems,
        modifier = Modifier,
    )
}
