package com.example.life_gamification

import com.example.life_gamification.data.local.entity.UserEntity
import com.example.life_gamification.data.local.entity.UserStatEntity
import com.example.life_gamification.domain.usecase.AddCustomStatUseCase
import com.example.life_gamification.domain.usecase.DeleteCustomStatUseCase
import com.example.life_gamification.domain.usecase.GetCustomStatUseCase
import com.example.life_gamification.data.repository.UserRepository
import com.example.life_gamification.presentation.status.StatusViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals


@OptIn(ExperimentalCoroutinesApi::class)
class StatusViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: StatusViewModel
    private val mockGetStats = mockk<GetCustomStatUseCase>()
    private val mockAddStat = mockk<AddCustomStatUseCase>()
    private val mockDeleteStat = mockk<DeleteCustomStatUseCase>()
    private val mockUserRepo = mockk<UserRepository>()

    private val userId = "test_user"

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        val fakeUser = UserEntity(
            id = userId,
            name = "Test User",
            email = "test@example.com",
            level = 2,
            experience = 100,
            money = 50
        )

        val fakeStats = listOf(
            UserStatEntity(id = 1, userId = userId, name = "Сила", value = 10),
            UserStatEntity(id = 2, userId = userId, name = "Интеллект", value = 20)
        )

        coEvery { mockUserRepo.getUserById(userId) } returns fakeUser
        coEvery { mockGetStats.invoke(userId) } returns flowOf(fakeStats)

        viewModel = StatusViewModel(
            userId = userId,
            getCustomStatUseCase = mockGetStats,
            addCustomStatUseCase = mockAddStat,
            deleteCustomStatUseCase = mockDeleteStat,
            userRepository = mockUserRepo
        )

        // запускаем корутины вручную
        runTest {
            advanceUntilIdle()
        }

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
    fun `addStat calls use case with correct data`() = runTest {
        val statName = "Ловкость"
        val defaultValue = 0

        coEvery { mockAddStat.invoke(userId, statName, defaultValue) } returns Unit

        viewModel.addStat(statName)

        advanceUntilIdle()

        coVerify { mockAddStat.invoke(userId, statName, defaultValue) }
    }
}
