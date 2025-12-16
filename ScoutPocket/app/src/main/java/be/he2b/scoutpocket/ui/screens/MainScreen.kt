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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import be.he2b.scoutpocket.navigation.AppScreen
import be.he2b.scoutpocket.navigation.BottomNavItem
import be.he2b.scoutpocket.viewmodel.AgendaViewModel
import be.he2b.scoutpocket.viewmodel.AgendaViewModelFactory
import be.he2b.scoutpocket.viewmodel.EventViewModel
import be.he2b.scoutpocket.viewmodel.EventViewModelFactory
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.X

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
    val agendaViewModel: AgendaViewModel = viewModel(
        factory = AgendaViewModelFactory(LocalContext.current.applicationContext)
    )
    val eventViewModel: EventViewModel = viewModel(
        factory = EventViewModelFactory(LocalContext.current.applicationContext)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0,0,0,0),
        topBar = {
            val navBackStackEntry by navBarController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val title = when {
                currentRoute == BottomNavItem.Agenda.route -> "Agenda"
                currentRoute == BottomNavItem.Members.route -> "Membres"
                currentRoute == BottomNavItem.About.route -> "About"
                currentRoute == AppScreen.AddEvent.name -> "Nouvel évènement"
                currentRoute?.startsWith("eventDetails/") == true -> "Détails"
                else -> "ScoutPocket"
            }

            if (currentRoute == AppScreen.AddEvent.name || currentRoute?.startsWith("eventDetails/") == true) {
                TopAppBar(
                    title = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navBarController.navigateUp() },
                            modifier = modifier
                                .padding(start = 12.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = CircleShape,
                                ),
                        ) {
                            Icon(
                                imageVector = Lucide.X,
                                contentDescription = "Retour",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    )
                )
            } else {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = title,
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
            }
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
                    AgendaScreen(
                        modifier = Modifier.fillMaxSize(),
                        agendaViewModel = agendaViewModel,
                        navController = navBarController,
                    )
                }
                composable(BottomNavItem.Members.route) {
                    MembersScreen(modifier = Modifier.fillMaxSize())
                }
                composable(BottomNavItem.About.route) {
                    AboutScreen(modifier = Modifier.fillMaxSize())
                }
                composable(AppScreen.AddEvent.name) {
                    AddEventScreen(
                        navController = navBarController,
                        viewModel = eventViewModel,
                    )
                }
                composable(
                    route = AppScreen.EventDetails.route,
                    arguments = listOf(
                        navArgument("eventId") {
                            type = NavType.IntType
                        }
                    )
                ) { backStackEntry ->
                    val eventId = backStackEntry.arguments?.getInt("eventId")

                    val eventViewModel: EventViewModel = viewModel(
                        factory = EventViewModelFactory(LocalContext.current.applicationContext)
                    )

                    if (eventId != null) {
                        EventDetailsScreen(
                            modifier = Modifier.fillMaxSize(),
                            eventId = eventId,
                            viewModel = eventViewModel,
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "Erreur : ID de l\'évènement manquant.",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }

            BottomBar(
                navController = navBarController,
                items = bottomNavItems,
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                onAddEventClick = { eventViewModel.createEvent() }
            )
        }
    }
}

@Composable
fun BottomBar(
    navController: NavController,
    items: List<BottomNavItem>,
    modifier: Modifier = Modifier,
    onAddEventClick: () -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isActionButtonVisible =
        currentRoute == BottomNavItem.Agenda.route || currentRoute == AppScreen.AddEvent.name

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp, 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        NavigationBar(
            navController = navController,
            items = items,
            modifier = if (isActionButtonVisible)
                Modifier.weight(1f) else Modifier.fillMaxWidth(),
        )

        if (isActionButtonVisible) {
            Button(
                onClick = {
                    if (currentRoute == BottomNavItem.Agenda.route) {
                        navController.navigate(AppScreen.AddEvent.name)
                    } else if (currentRoute == AppScreen.AddEvent.name) {
                        onAddEventClick()
                    }
                },
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        shape = CircleShape,
                    ),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                if (currentRoute == BottomNavItem.Agenda.route) {
                    Icon(
                        imageVector = Lucide.Plus,
                        contentDescription = null,
                        modifier = modifier.height(28.dp),
                        tint = MaterialTheme.colorScheme.secondaryContainer,
                    )
                } else if (currentRoute == AppScreen.AddEvent.name) {
                    Icon(
                        imageVector = Lucide.Check,
                        contentDescription = null,
                        modifier = modifier.height(28.dp),
                        tint = MaterialTheme.colorScheme.secondaryContainer,
                    )
                }
            }
        }
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

@Preview(showBackground = true)
@Composable
fun BottomBarPreview() {
    BottomBar(
        navController = rememberNavController(),
        items = bottomNavItems,
        modifier = Modifier,
        onAddEventClick = {}
    )
}
