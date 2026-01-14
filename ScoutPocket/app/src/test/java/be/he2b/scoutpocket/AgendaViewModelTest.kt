package be.he2b.scoutpocket

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import be.he2b.scoutpocket.database.entity.Event
import be.he2b.scoutpocket.database.repository.EventRepository
import be.he2b.scoutpocket.model.Section
import be.he2b.scoutpocket.viewmodel.AgendaViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AgendaViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockRepository: EventRepository
    private lateinit var mockContext: Context
    private lateinit var viewModel: AgendaViewModel

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

    private val pastEvent = Event(
        id = 3,
        name = "Past Event",
        section = Section.PIONNIERS,
        date = LocalDate.now().minusDays(3),
        startTime = LocalTime.of(9, 0),
        endTime = LocalTime.of(11, 0),
        location = "Forêt"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk(relaxed = true)
        mockContext = mockk(relaxed = true)

        every { mockContext.getString(R.string.events_loading_error) } returns
                "Erreur lors du chargement des événements"
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks()
    }

    @Test
    fun initShouldStartLoadingEvents() = runTest {
        coEvery { mockRepository.getAllEvents() } returns flowOf(emptyList())

        viewModel = AgendaViewModel(mockRepository, mockContext)
        advanceUntilIdle()

        coVerify { mockRepository.getAllEvents() }
    }

    @Test
    fun loadEventsShouldSetLoadingState() = runTest {
        coEvery { mockRepository.getAllEvents() } returns flowOf(emptyList())
        viewModel = AgendaViewModel(mockRepository, mockContext)
        advanceUntilIdle()

        viewModel.loadEvents()

        assertTrue(viewModel.isLoading.value)
    }

    @Test
    fun loadEventsShouldClearErrorMessage() = runTest {
        coEvery { mockRepository.getAllEvents() } returns flowOf(emptyList())
        viewModel = AgendaViewModel(mockRepository, mockContext)
        viewModel.errorMessage.value = "Previous error"
        advanceUntilIdle()

        viewModel.loadEvents()

        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun loadEventsShouldPartitionEventsByDate() = runTest {
        val events = listOf(todayEvent, futureEvent, pastEvent)
        coEvery { mockRepository.getAllEvents() } returns flowOf(events)

        viewModel = AgendaViewModel(mockRepository, mockContext)
        advanceUntilIdle()

        assertEquals(2, viewModel.upcomingEvents.value.size)
        assertEquals(1, viewModel.pastEvents.value.size)
        assertEquals(3, viewModel.allEvents.value.size)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun loadEventsShouldIncludeTodayInUpcoming() = runTest {
        val events = listOf(todayEvent)
        coEvery { mockRepository.getAllEvents() } returns flowOf(events)

        viewModel = AgendaViewModel(mockRepository, mockContext)
        advanceUntilIdle()

        assertEquals(1, viewModel.upcomingEvents.value.size)
        assertEquals(todayEvent, viewModel.upcomingEvents.value.first())
        assertEquals(0, viewModel.pastEvents.value.size)
    }

    @Test
    fun loadEventsShouldReversePastEvents() = runTest {
        val pastEvent1 = pastEvent.copy(
            id = 1,
            section = Section.BALADINS,
            date = LocalDate.now().minusDays(5)
        )
        val pastEvent2 = pastEvent.copy(
            id = 2,
            section = Section.LOUVETEAUX,
            date = LocalDate.now().minusDays(3)
        )
        val pastEvent3 = pastEvent.copy(
            id = 3,
            section = Section.ECLAIREURS,
            date = LocalDate.now().minusDays(1)
        )

        val events = listOf(pastEvent1, pastEvent2, pastEvent3)
        coEvery { mockRepository.getAllEvents() } returns flowOf(events)

        viewModel = AgendaViewModel(mockRepository, mockContext)
        advanceUntilIdle()

        assertEquals(3, viewModel.pastEvents.value.size)
        assertEquals(pastEvent3.id, viewModel.pastEvents.value[0].id)
        assertEquals(pastEvent2.id, viewModel.pastEvents.value[1].id)
        assertEquals(pastEvent1.id, viewModel.pastEvents.value[2].id)
    }

    @Test
    fun loadEventsShouldHandleEmptyList() = runTest {
        coEvery { mockRepository.getAllEvents() } returns flowOf(emptyList())

        viewModel = AgendaViewModel(mockRepository, mockContext)
        advanceUntilIdle()

        assertEquals(0, viewModel.upcomingEvents.value.size)
        assertEquals(0, viewModel.pastEvents.value.size)
        assertEquals(0, viewModel.allEvents.value.size)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun loadEventsShouldHandleOnlyFutureEvents() = runTest {
        val events = listOf(futureEvent)
        coEvery { mockRepository.getAllEvents() } returns flowOf(events)

        viewModel = AgendaViewModel(mockRepository, mockContext)
        advanceUntilIdle()

        assertEquals(1, viewModel.upcomingEvents.value.size)
        assertEquals(0, viewModel.pastEvents.value.size)
        assertEquals(1, viewModel.allEvents.value.size)
    }

    @Test
    fun loadEventsShouldHandleOnlyPastEvents() = runTest {
        val events = listOf(pastEvent)
        coEvery { mockRepository.getAllEvents() } returns flowOf(events)

        viewModel = AgendaViewModel(mockRepository, mockContext)
        advanceUntilIdle()

        assertEquals(0, viewModel.upcomingEvents.value.size)
        assertEquals(1, viewModel.pastEvents.value.size)
        assertEquals(1, viewModel.allEvents.value.size)
    }

    @Test
    fun loadEventsShouldStopLoadingAfterSuccess() = runTest {
        val events = listOf(futureEvent)
        coEvery { mockRepository.getAllEvents() } returns flowOf(events)

        viewModel = AgendaViewModel(mockRepository, mockContext)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun loadEventsShouldHandleRepositoryException() = runTest {
        coEvery { mockRepository.getAllEvents() } throws Exception("Database error")

        viewModel = AgendaViewModel(mockRepository, mockContext)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertEquals("Erreur lors du chargement des événements",
            viewModel.errorMessage.value)
        assertEquals(0, viewModel.allEvents.value.size)
    }

    @Test
    fun loadEventsShouldHandleNetworkException() = runTest {
        coEvery { mockRepository.getAllEvents() } throws
                java.net.UnknownHostException("Network error")

        viewModel = AgendaViewModel(mockRepository, mockContext)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertEquals("Erreur lors du chargement des événements",
            viewModel.errorMessage.value)
    }

    @Test
    fun loadEventsShouldHandleNullPointerException() = runTest {
        coEvery { mockRepository.getAllEvents() } throws NullPointerException()

        viewModel = AgendaViewModel(mockRepository, mockContext)
        advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
        assertEquals("Erreur lors du chargement des événements",
            viewModel.errorMessage.value)
    }

    @Test
    fun clearErrorShouldResetErrorMessage() = runTest {
        coEvery { mockRepository.getAllEvents() } returns flowOf(emptyList())
        viewModel = AgendaViewModel(mockRepository, mockContext)
        viewModel.errorMessage.value = "Some error"
        advanceUntilIdle()

        viewModel.clearError()

        assertNull(viewModel.errorMessage.value)
    }

    @Test
    fun retryLoadingShouldCallLoadEventsAgain() = runTest {
        var callCount = 0
        coEvery { mockRepository.getAllEvents() } answers {
            callCount++
            if (callCount == 1) {
                throw Exception("First attempt failed")
            } else {
                flowOf(listOf(futureEvent))
            }
        }

        viewModel = AgendaViewModel(mockRepository, mockContext)
        advanceUntilIdle()

        assertEquals("Erreur lors du chargement des événements",
            viewModel.errorMessage.value)

        viewModel.loadEvents()
        advanceUntilIdle()

        assertNull(viewModel.errorMessage.value)
        assertEquals(1, viewModel.upcomingEvents.value.size)
        assertFalse(viewModel.isLoading.value)
    }

    @Test
    fun loadEventsShouldHandleAllSectionTypes() = runTest {
        val events = listOf(
            Event(
                id = 1, name = "Event Unite", section = Section.UNITE,
                date = LocalDate.now().plusDays(1), startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(12, 0), location = "Local"
            ),
            Event(
                id = 2, name = "Event Baladins", section = Section.BALADINS,
                date = LocalDate.now().plusDays(2), startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(12, 0), location = "Local"
            ),
            Event(
                id = 3, name = "Event Louveteaux", section = Section.LOUVETEAUX,
                date = LocalDate.now().plusDays(3), startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(12, 0), location = "Local"
            ),
            Event(
                id = 4, name = "Event Eclaireurs", section = Section.ECLAIREURS,
                date = LocalDate.now().plusDays(4), startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(12, 0), location = "Local"
            ),
            Event(
                id = 5, name = "Event Pionniers", section = Section.PIONNIERS,
                date = LocalDate.now().plusDays(5), startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(12, 0), location = "Local"
            )
        )
        coEvery { mockRepository.getAllEvents() } returns flowOf(events)

        viewModel = AgendaViewModel(mockRepository, mockContext)
        advanceUntilIdle()

        assertEquals(5, viewModel.upcomingEvents.value.size)
        assertEquals(5, viewModel.allEvents.value.size)

        val sections = viewModel.upcomingEvents.value.map { it.section }
        assertTrue(sections.contains(Section.UNITE))
        assertTrue(sections.contains(Section.BALADINS))
        assertTrue(sections.contains(Section.LOUVETEAUX))
        assertTrue(sections.contains(Section.ECLAIREURS))
        assertTrue(sections.contains(Section.PIONNIERS))
    }

    @Test
    fun loadEventsShouldHandleMultipleEvents() = runTest {
        val sections = Section.entries.toTypedArray()
        val events = (1..10).map { index ->
            Event(
                id = index,
                name = "Event $index",
                section = sections[index % sections.size],
                date = if (index % 2 == 0)
                    LocalDate.now().plusDays(index.toLong())
                else
                    LocalDate.now().minusDays(index.toLong()),
                startTime = LocalTime.of(10, 0),
                endTime = LocalTime.of(12, 0),
                location = "Location $index"
            )
        }
        coEvery { mockRepository.getAllEvents() } returns flowOf(events)

        viewModel = AgendaViewModel(mockRepository, mockContext)
        advanceUntilIdle()

        assertEquals(10, viewModel.allEvents.value.size)
        assertEquals(5, viewModel.upcomingEvents.value.size)
        assertEquals(5, viewModel.pastEvents.value.size)
    }

    @Test
    fun flowUpdatesShouldTriggerRecomposition() = runTest {
        val initialEvents = listOf(futureEvent)
        val updatedEvents = listOf(futureEvent, pastEvent)

        val flow = kotlinx.coroutines.flow.MutableStateFlow(initialEvents)
        coEvery { mockRepository.getAllEvents() } returns flow

        viewModel = AgendaViewModel(mockRepository, mockContext)
        advanceUntilIdle()

        assertEquals(1, viewModel.allEvents.value.size)

        flow.value = updatedEvents
        advanceUntilIdle()

        assertEquals(2, viewModel.allEvents.value.size)
    }

    @Test
    fun errorMessageShouldPersistUntilCleared() = runTest {
        coEvery { mockRepository.getAllEvents() } throws Exception("Error")
        viewModel = AgendaViewModel(mockRepository, mockContext)
        advanceUntilIdle()

        val errorMessage = viewModel.errorMessage.value

        repeat(3) {
            assertEquals(errorMessage, viewModel.errorMessage.value)
        }

        assertEquals("Erreur lors du chargement des événements",
            viewModel.errorMessage.value)
    }
}
