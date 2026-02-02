package be.he2b.scoutpocket

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import be.he2b.scoutpocket.database.entity.Event
import be.he2b.scoutpocket.database.repository.RoomEventRepository
import be.he2b.scoutpocket.model.Section
import be.he2b.scoutpocket.ui.screens.AgendaScreen
import be.he2b.scoutpocket.viewmodel.AgendaViewModel
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class AgendaScreenIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockRepository: RoomEventRepository
    private lateinit var viewModel: AgendaViewModel
    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    private val todayEvent = Event(
        id = 1,
        name = "Today Event",
        section = Section.LOUVETEAUX,
        date = LocalDate.now(),
        startTime = LocalTime.of(14, 0),
        endTime = LocalTime.of(16, 0),
        location = "Local"
    )

    private val futureEvent = Event(
        id = 2,
        name = "Future Event",
        section = Section.ECLAIREURS,
        date = LocalDate.now().plusDays(5),
        startTime = LocalTime.of(10, 0),
        endTime = LocalTime.of(12, 0),
        location = "Camp"
    )

    private val futureEvent2 = Event(
        id = 3,
        name = "Another Future Event",
        section = Section.PIONNIERS,
        date = LocalDate.now().plusDays(10),
        startTime = LocalTime.of(9, 0),
        endTime = LocalTime.of(11, 0),
        location = "Forêt"
    )

    private val pastEvent = Event(
        id = 4,
        name = "Past Event",
        section = Section.BALADINS,
        date = LocalDate.now().minusDays(3),
        startTime = LocalTime.of(15, 0),
        endTime = LocalTime.of(17, 0),
        location = "Parc"
    )

    @Before
    fun setup() {
        mockRepository = mockk(relaxed = true)
    }

    private fun setupViewModel(events: List<Event> = emptyList()) {
        coEvery { mockRepository.getAllEvents() } returns flowOf(events)
        viewModel = AgendaViewModel(mockRepository, context)
    }

    @Test
    fun agendaScreenDisplaysLoadingState() {
        setupViewModel()
        composeTestRule.setContent {
            AgendaScreen(navController = rememberNavController(), agendaViewModel = viewModel)
        }
        composeTestRule.runOnUiThread {
            viewModel.isLoading.value = true
            viewModel.allEvents.value = emptyList()
        }
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithText(context.getString(R.string.agenda_loading_events))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun agendaScreenDisplaysEmptyStateWhenNoEvents() {
        setupViewModel(emptyList())
        viewModel.isLoading.value = false
        viewModel.allEvents.value = emptyList()
        composeTestRule.setContent {
            AgendaScreen(navController = rememberNavController(), agendaViewModel = viewModel)
        }
        composeTestRule.onNodeWithText(context.getString(R.string.agenda_no_events_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.agenda_no_events_subtitle)).assertIsDisplayed()
    }

    @Test
    fun agendaScreenDisplaysNextEvent() {
        setupViewModel(listOf(futureEvent))
        viewModel.isLoading.value = false
        viewModel.upcomingEvents.value = listOf(futureEvent)
        viewModel.allEvents.value = listOf(futureEvent)
        composeTestRule.setContent {
            AgendaScreen(navController = rememberNavController(), agendaViewModel = viewModel)
        }
        composeTestRule.onNodeWithText(context.getString(R.string.agenda_next_event)).assertIsDisplayed()
        composeTestRule.onNodeWithText("Future Event").assertIsDisplayed()
    }

    @Test
    fun agendaScreenDisplaysUpcomingCountZero() {
        setupViewModel(listOf(pastEvent))
        viewModel.isLoading.value = false
        viewModel.upcomingEvents.value = emptyList()
        viewModel.pastEvents.value = listOf(pastEvent)
        viewModel.allEvents.value = listOf(pastEvent)
        composeTestRule.setContent {
            AgendaScreen(navController = rememberNavController(), agendaViewModel = viewModel)
        }
        composeTestRule.onAllNodesWithText(context.getString(R.string.agenda_upcoming_count_zero))[0].assertIsDisplayed()
    }

    @Test
    fun agendaScreenDisplaysUpcomingCountOne() {
        setupViewModel(listOf(futureEvent))
        viewModel.isLoading.value = false
        viewModel.upcomingEvents.value = listOf(futureEvent)
        viewModel.allEvents.value = listOf(futureEvent)
        composeTestRule.setContent {
            AgendaScreen(navController = rememberNavController(), agendaViewModel = viewModel)
        }
        composeTestRule.onNodeWithText(context.getString(R.string.agenda_upcoming_count_one)).assertIsDisplayed()
    }

    @Test
    fun agendaScreenDisplaysUpcomingCountMultiple() {
        val events = listOf(futureEvent, futureEvent2, todayEvent)
        setupViewModel(events)
        viewModel.isLoading.value = false
        viewModel.upcomingEvents.value = events
        viewModel.allEvents.value = events
        composeTestRule.setContent {
            AgendaScreen(navController = rememberNavController(), agendaViewModel = viewModel)
        }
        composeTestRule.onNodeWithText(context.getString(R.string.agenda_upcoming_count_other, 3)).assertIsDisplayed()
    }

    @Test
    fun agendaScreenDisplaysUpcomingAndPastTabs() {
        val events = listOf(futureEvent, pastEvent)
        setupViewModel(events)
        viewModel.isLoading.value = false
        viewModel.upcomingEvents.value = listOf(futureEvent)
        viewModel.pastEvents.value = listOf(pastEvent)
        viewModel.allEvents.value = events
        composeTestRule.setContent {
            AgendaScreen(navController = rememberNavController(), agendaViewModel = viewModel)
        }
        composeTestRule.onAllNodesWithText(context.getString(R.string.agenda_upcoming_tab))[0].assertIsDisplayed()
        composeTestRule.onAllNodesWithText(context.getString(R.string.agenda_past_tab))[0].assertIsDisplayed()
    }

    @Test
    fun clickingPastTabShowsPastEvents() {
        val events = listOf(futureEvent, pastEvent)
        setupViewModel(events)
        viewModel.isLoading.value = false
        viewModel.upcomingEvents.value = listOf(futureEvent)
        viewModel.pastEvents.value = listOf(pastEvent)
        viewModel.allEvents.value = events
        composeTestRule.setContent {
            AgendaScreen(navController = rememberNavController(), agendaViewModel = viewModel)
        }
        composeTestRule.onAllNodesWithText(context.getString(R.string.agenda_past_tab))[0].performClick()
        composeTestRule.onNodeWithText("Past Event").assertIsDisplayed()
    }

    @Test
    fun clickingUpcomingTabShowsUpcomingEvents() {
        val events = listOf(futureEvent, futureEvent2, pastEvent)
        setupViewModel(events)
        viewModel.isLoading.value = false
        viewModel.upcomingEvents.value = listOf(futureEvent, futureEvent2)
        viewModel.pastEvents.value = listOf(pastEvent)
        viewModel.allEvents.value = events
        composeTestRule.setContent {
            AgendaScreen(navController = rememberNavController(), agendaViewModel = viewModel)
        }
        composeTestRule.onAllNodesWithText(context.getString(R.string.agenda_past_tab))[0].performClick()
        composeTestRule.onAllNodesWithText(context.getString(R.string.agenda_upcoming_tab))[0].performClick()
        composeTestRule.onNodeWithText("Another Future Event").assertIsDisplayed()
    }

    @Test
    fun agendaScreenDisplaysErrorSnackbar() {
        setupViewModel()
        viewModel.isLoading.value = false
        viewModel.allEvents.value = emptyList()
        composeTestRule.setContent {
            AgendaScreen(navController = rememberNavController(), agendaViewModel = viewModel)
        }
        composeTestRule.runOnUiThread {
            viewModel.errorMessage.value = context.getString(R.string.events_loading_error)
        }
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText(context.getString(R.string.events_loading_error))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(context.getString(R.string.error_retry)).assertIsDisplayed()
    }

    @Test
    fun snackbarRetryButtonCallsLoadEvents() {
        setupViewModel()
        viewModel.isLoading.value = false
        viewModel.allEvents.value = emptyList()
        composeTestRule.setContent {
            AgendaScreen(navController = rememberNavController(), agendaViewModel = viewModel)
        }
        composeTestRule.runOnUiThread {
            viewModel.errorMessage.value = context.getString(R.string.events_loading_error)
        }
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText(context.getString(R.string.error_retry))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(context.getString(R.string.error_retry)).performClick()
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            viewModel.errorMessage.value == null
        }
    }

    @Test
    fun agendaScreenShowsEmptyPastStateWhenNoPastEvents() {
        val events = listOf(futureEvent, futureEvent2)
        setupViewModel(events)
        viewModel.isLoading.value = false
        viewModel.upcomingEvents.value = events
        viewModel.pastEvents.value = emptyList()
        viewModel.allEvents.value = events
        composeTestRule.setContent {
            AgendaScreen(navController = rememberNavController(), agendaViewModel = viewModel)
        }
        composeTestRule.onAllNodesWithText(context.getString(R.string.agenda_past_tab))[0].performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.agenda_no_past_title)).assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.agenda_no_past_subtitle)).assertIsDisplayed()
    }

    @Test
    fun agendaScreenDisplaysMultipleUpcomingEvents() {
        val events = listOf(futureEvent, futureEvent2, todayEvent)
        setupViewModel(events)
        viewModel.isLoading.value = false
        viewModel.upcomingEvents.value = events
        viewModel.allEvents.value = events
        composeTestRule.setContent {
            AgendaScreen(navController = rememberNavController(), agendaViewModel = viewModel)
        }
        composeTestRule.onNodeWithText("Future Event").assertIsDisplayed()
        composeTestRule.onNodeWithText("Another Future Event").assertExists()
        composeTestRule.onNodeWithText("Today Event").assertExists()
    }

    @Test
    fun agendaScreenHandlesEventClick() {
        setupViewModel(listOf(futureEvent))
        viewModel.isLoading.value = false
        viewModel.upcomingEvents.value = listOf(futureEvent)
        viewModel.allEvents.value = listOf(futureEvent)
        composeTestRule.setContent {
            AgendaScreen(navController = rememberNavController(), agendaViewModel = viewModel)
        }
        composeTestRule.onNodeWithText("Future Event").assertHasClickAction()
    }

    @Test
    fun agendaScreenTitleIsDisplayed() {
        setupViewModel(listOf(futureEvent))
        viewModel.isLoading.value = false
        viewModel.upcomingEvents.value = listOf(futureEvent)
        viewModel.allEvents.value = listOf(futureEvent)
        composeTestRule.setContent {
            AgendaScreen(navController = rememberNavController(), agendaViewModel = viewModel)
        }
        composeTestRule.onNodeWithText(context.getString(R.string.agenda_screen_title)).assertIsDisplayed()
    }

}
