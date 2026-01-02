package be.he2b.scoutpocket.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import be.he2b.scoutpocket.ui.component.ExpressiveFABMenu
import be.he2b.scoutpocket.ui.component.FABMenuItem
import be.he2b.scoutpocket.viewmodel.AgendaViewModel
import be.he2b.scoutpocket.viewmodel.AgendaViewModelFactory
import be.he2b.scoutpocket.viewmodel.EventViewModel
import be.he2b.scoutpocket.viewmodel.EventViewModelFactory
import be.he2b.scoutpocket.viewmodel.LoginViewModel
import be.he2b.scoutpocket.viewmodel.LoginViewModelFactory
import be.he2b.scoutpocket.viewmodel.MemberViewModel
import be.he2b.scoutpocket.viewmodel.MemberViewModelFactory
import com.composables.icons.lucide.CalendarPlus
import com.composables.icons.lucide.FileUp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.UserPlus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {},
) {
    val context = LocalContext.current

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val agendaViewModel: AgendaViewModel = viewModel(
        factory = AgendaViewModelFactory(LocalContext.current.applicationContext)
    )
    val eventViewModel: EventViewModel = viewModel(
        factory = EventViewModelFactory(LocalContext.current.applicationContext)
    )
    val memberViewModel: MemberViewModel = viewModel(
        factory = MemberViewModelFactory(LocalContext.current.applicationContext)
    )
    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(LocalContext.current.applicationContext)
    )

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                memberViewModel.importMembers(context, uri)
            }
        }
    }

    val showBottomBar = when {
        currentRoute == null -> false
        currentRoute.startsWith(AppScreen.EventDetails.route) -> false
        currentRoute == AppScreen.AddEvent.route -> false
        currentRoute == AppScreen.AddMember.route -> false
        currentRoute == AppScreen.About.route -> false
        else -> true
    }

//    val pageTitle = when {
//        currentRoute == AppScreen.Agenda.route -> "Agenda"
//        currentRoute == AppScreen.Members.route -> "Membres"
//        currentRoute == AppScreen.Profile.route -> "Profile"
//        currentRoute == AppScreen.About.route -> "À propos"
//        currentRoute == AppScreen.AddEvent.route -> "Nouvel évènement"
//        currentRoute == AppScreen.AddMember.route -> "Nouveau(x) membre(s)"
//        currentRoute?.startsWith("eventDetails/") == true -> "Détails"
//        else -> "ScoutPocket"
//    }

    var isFABMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0,0,0,0),
//        topBar = {
//            if (currentRoute == AppScreen.AddEvent.route
//                || currentRoute == AppScreen.AddMember.route
//                || currentRoute?.startsWith("eventDetails/") == true
//
//            ) {
//                TopAppBar(
//                    title = {
//                        Box(
//                            modifier = Modifier.fillMaxWidth(),
//                            contentAlignment = Alignment.Center,
//                        ) {
//                            Text(
//                                text = pageTitle,
//                                style = MaterialTheme.typography.titleLarge,
//                            )
//                        }
//                    },
//                    navigationIcon = {
//                        IconButton(
//                            onClick = { navController.navigateUp() },
//                            modifier = modifier
//                                .padding(start = 12.dp)
//                                .background(
//                                    color = MaterialTheme.colorScheme.secondaryContainer,
//                                    shape = CircleShape,
//                                ),
//                        ) {
//                            Icon(
//                                imageVector = Lucide.X,
//                                contentDescription = "Retour",
//                                tint = MaterialTheme.colorScheme.onSurface,
//                            )
//                        }
//                    },
//                    actions = {
//                        if (currentRoute == AppScreen.AddEvent.route
//                            || currentRoute == AppScreen.AddMember.route
//                        ) {
//                            IconButton(
//                                onClick = {
//                                    if (currentRoute == AppScreen.AddEvent.route) {
//                                        eventViewModel.createEvent()
//                                    } else if (currentRoute == AppScreen.AddMember.route) {
//                                        memberViewModel.createMember()
//                                    }
//                                },
//                                modifier = modifier
//                                    .padding(end = 12.dp)
//                                    .background(
//                                        color = MaterialTheme.colorScheme.secondaryContainer,
//                                        shape = CircleShape,
//                                    ),
//                            ) {
//                                Icon(
//                                    imageVector = Lucide.Check,
//                                    contentDescription = "Valider",
//                                    tint = MaterialTheme.colorScheme.onSurface,
//                                )
//                            }
//                        } else {
//                            Spacer(modifier = Modifier.size(56.dp))
//                        }
//                    },
//                    colors = TopAppBarDefaults.topAppBarColors(
//                        containerColor = Color.Transparent,
//                        titleContentColor = MaterialTheme.colorScheme.onBackground,
//                    )
//                )
//            } else {
//                CenterAlignedTopAppBar(
//                    title = {
//                        Text(
//                            text = pageTitle,
//                            style = MaterialTheme.typography.titleLarge,
//                        )
//                    },
//                    colors = TopAppBarDefaults.topAppBarColors(
//                        containerColor = Color.Transparent,
//                        titleContentColor = MaterialTheme.colorScheme.onBackground,
//                    )
//                )
//            }
//        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {
            NavHost(
                navController = navController,
                startDestination = AppScreen.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(AppScreen.Home.route) {
                    HomeScreen(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController,
                    )
                }
                composable(AppScreen.Agenda.route) {
                    AgendaScreen(
                        modifier = Modifier.fillMaxSize(),
                        agendaViewModel = agendaViewModel,
                        navController = navController,
                    )
                }
                composable(AppScreen.Members.route) {
                    MembersScreen(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController,
                        viewModel = memberViewModel,
                    )
                }
                composable(AppScreen.Profile.route) {
                    ProfileScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = loginViewModel,
                        navController = navController,
                        onLogout = onLogout,
                    )
                }
                composable(AppScreen.About.route) {
                    AboutScreen(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController,
                    )
                }
                composable(AppScreen.AddEvent.route) {
                    AddEventScreen(
                        navController = navController,
                        viewModel = eventViewModel,
                    )
                }
                composable(
                    AppScreen.AddMember.route,
                    arguments = listOf(
                        navArgument("mode") {
                            type = NavType.StringType
                            defaultValue = "manual"
                        }
                    )
                ) { backStackEntry ->
                    val mode = backStackEntry.arguments?.getString("mode") ?: "manual"

                    AddMemberScreen(
                        navController = navController,
                        viewModel = memberViewModel,
                        initialMode = mode,
                        onImportCSV = {
                            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                addCategory(Intent.CATEGORY_OPENABLE)
                                type = "*/*"
                                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                                    "text/csv",
                                    "text/comma-separated-values",
                                    "text/plain"
                                ))
                            }
                            filePickerLauncher.launch(intent)
                            navController.navigate(BottomNavItem.Members.route)
                        }
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

            if (isFABMenuExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            isFABMenuExpanded = false
                        }
                )
            }

            if (showBottomBar) {
                BottomBar(
                    navController = navController,
                    items = listOf(
                        BottomNavItem.Home,
                        BottomNavItem.Agenda,
                        BottomNavItem.Members,
                        BottomNavItem.Profile,
                    ),
                    fabMenuItems = listOf(
                        FABMenuItem(
                            icon = Lucide.CalendarPlus,
                            label = "Nouvel événement",
                            onClick = {
                                navController.navigate(AppScreen.AddEvent.route)
                            }
                        ),
                        FABMenuItem(
                            icon = Lucide.UserPlus,
                            label = "Nouveau membre",
                            onClick = {
                                navController.navigate(AppScreen.addMemberRoute("manual"))
                            }
                        ),
                        FABMenuItem(
                            icon = Lucide.FileUp,
                            label = "Importer CSV",
                            onClick = {
                                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                                    addCategory(Intent.CATEGORY_OPENABLE)
                                    type = "*/*"
                                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf(
                                        "text/csv",
                                        "text/comma-separated-values",
                                        "text/plain"
                                    ))
                                }
                                filePickerLauncher.launch(intent)
                            }
                        )
                    ),
                    isFABMenuExpanded = isFABMenuExpanded,
                    onFABMenuExpandedChange = { isFABMenuExpanded = it },
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                )
            }
        }
    }
}

@Composable
fun BottomBar(
    navController: NavController,
    items: List<BottomNavItem>,
    fabMenuItems: List<FABMenuItem>,
    isFABMenuExpanded: Boolean,
    onFABMenuExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ExpressiveFABMenu(
            items = fabMenuItems,
            isExpanded = isFABMenuExpanded,
            onExpandedChange = onFABMenuExpandedChange,
        )

        NavigationBar(
            navController = navController,
            items = items,
            modifier = Modifier.fillMaxWidth(),
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
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            )
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
    Column(
        modifier = modifier
            .weight(1f)
            .clip(CircleShape)
            .background(
                color = if (selected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    Color.Transparent
                }
            )
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
            tint = if (selected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
        )
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontWeight = FontWeight.SemiBold,
        )
    }
}
