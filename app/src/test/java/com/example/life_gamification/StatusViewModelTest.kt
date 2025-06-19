package com.example.life_gamification
import com.example.life_gamification.data.local.dao.UserDao
import com.example.life_gamification.data.local.entity.UserDailyQuestsEntity
import com.example.life_gamification.data.local.entity.UserEntity
import com.example.life_gamification.data.local.entity.UserStatEntity
import com.example.life_gamification.data.repository.UserRepository
import com.example.life_gamification.domain.repository.UserTasksRepository.UserTaskRepository
import com.example.life_gamification.domain.usecase.StatusUseCases
import com.example.life_gamification.presentation.Status.StatusViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class StatusViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var viewModel: StatusViewModel
    private val mockUserRepo = mockk<UserRepository>()
    private val mockUserTaskRepo = mockk<UserTaskRepository>()
    private val mockUserDao = mockk<UserDao>()
    private val mockStatusUseCases = mockk<StatusUseCases>()

    private val userId = "test_user"
    private val fakeStats = listOf(
        UserStatEntity(id = 1, userId = userId, name = "Сила", value = 10),
        UserStatEntity(id = 2, userId = userId, name = "Интеллект", value = 20)
    )
    private val fakeDailies = listOf(
        UserDailyQuestsEntity(id = 1, userId = userId, name = "Утренняя зарядка", addXp = 5),
        UserDailyQuestsEntity(id = 2, userId = userId, name = "Чтение книги", addXp = 3)
    )
    private val fakeUser = UserEntity(
        id = userId,
        name = "Test User",
        email = "test@example.com",
        level = 2,
        experience = 100,
        money = 50
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        coEvery { mockUserRepo.getUserById(userId) } returns fakeUser
        coEvery { mockStatusUseCases.getCustomStat(userId) } returns flowOf(fakeStats)
        coEvery { mockStatusUseCases.getCustomDaily(userId) } returns flowOf(fakeDailies)
        coEvery { mockStatusUseCases.getCustomStatList(userId) } returns fakeStats
        coEvery { mockStatusUseCases.getCustomDailyList(userId) } returns fakeDailies
        coEvery { mockStatusUseCases.addCustomStat(any(), any(), any()) } returns Unit
        coEvery { mockStatusUseCases.deleteCustomStat(any()) } returns Unit
        coEvery { mockStatusUseCases.addCustomDaily(any(), any(), any()) } returns Unit
        coEvery { mockStatusUseCases.deleteCustomDaily(any()) } returns Unit
        coEvery { mockStatusUseCases.updateDaily(any()) } returns Unit
        coEvery { mockStatusUseCases.updateCustomStat(any()) } returns Unit

        viewModel = StatusViewModel(
            userId = userId,
            useCases = mockStatusUseCases,
            userRepository = mockUserRepo,
            userTaskRepository = mockUserTaskRepo
        )

        testScope.advanceUntilIdle()
    }
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `user is loaded correctly`() {
        assertEquals("Test User", viewModel.user.value?.name)
        assertEquals(100, viewModel.user.value?.experience)
    }

    @Test
    fun `addStat calls use case with correct parameters`() = testScope.runTest {
        viewModel.addStat("Ловкость", 5)
        advanceUntilIdle()

        coVerify { mockStatusUseCases.addCustomStat(userId, "Ловкость", 5) }
    }
}
