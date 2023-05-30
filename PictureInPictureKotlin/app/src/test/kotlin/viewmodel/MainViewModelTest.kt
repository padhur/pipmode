package viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.android.pictureinpicture.interfaces.TimeProvider
import com.example.android.pictureinpicture.viewmodel.MainViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var startedObserver: Observer<Boolean>

    @Mock
    private lateinit var timeObserver: Observer<String>

    @Mock
    private lateinit var mockTimeProvider: TimeProvider

    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = MainViewModel(mockTimeProvider)
    }

    @After
    fun tearDown() {
        viewModel.started.removeObserver(startedObserver)
        viewModel.time.removeObserver(timeObserver)
    }

    @Test
    fun `test startOrPause() starts the stopwatch when not started`() =
        testCoroutineRule.runBlockingTest {
            `when`(mockTimeProvider.uptimeMillis()).thenReturn(0L)
            // Create observers for LiveData
            val startedObserver = Observer<Boolean> {}
            val timeObserver = Observer<String> {}

            // Initially, the stopwatch is not started
            assert(viewModel.started.value == false)

            // Observe LiveData
            viewModel.started.observeForever(startedObserver)
            viewModel.time.observeForever(timeObserver)

            // Trigger startOrPause()
            viewModel.startOrPause()

            // The stopwatch should be started
            assert(viewModel.started.value == true)

            // Advance time by 100 milliseconds
            `when`(mockTimeProvider.uptimeMillis()).thenReturn(100L)

            // The time should be updated accordingly
            assert(viewModel.time.value == "00:00:01")

            // Clean up observers
            viewModel.started.removeObserver(startedObserver)
            viewModel.time.removeObserver(timeObserver)
        }

    @Test
    fun `test startOrPause() pauses the stopwatch when already started`() =
        testCoroutineRule.runBlockingTest {
            `when`(mockTimeProvider.uptimeMillis()).thenReturn(0L)

            val startedObserver = Observer<Boolean> {}
            val timeObserver = Observer<String> {}

            assertEquals(false, viewModel.started.value)

            viewModel.started.observeForever(startedObserver)
            viewModel.time.observeForever(timeObserver)

            viewModel.startOrPause()

            assertEquals(true, viewModel.started.value)

            delay(100L)

            assertEquals("00:00:01", viewModel.time.value)

            viewModel.startOrPause()

            assertEquals(false, viewModel.started.value)

            delay(100L)

            assertEquals("00:00:01", viewModel.time.value)

            viewModel.started.removeObserver(startedObserver)
            viewModel.time.removeObserver(timeObserver)
        }

    @Test
    fun `test clear() resets the stopwatch to zero`() = testCoroutineRule.runBlockingTest {
        `when`(mockTimeProvider.uptimeMillis()).thenReturn(0L)

        val startedObserver = Observer<Boolean> {}
        val timeObserver = Observer<String> {}

        assertEquals(false, viewModel.started.value)

        viewModel.started.observeForever(startedObserver)
        viewModel.time.observeForever(timeObserver)

        viewModel.startOrPause()

        assertEquals(true, viewModel.started.value)

        delay(100L)

        assertEquals("00:00:01", viewModel.time.value)

        viewModel.clear()

        assertEquals("00:00:00", viewModel.time.value)

        viewModel.started.removeObserver(startedObserver)
        viewModel.time.removeObserver(timeObserver)
    }
}