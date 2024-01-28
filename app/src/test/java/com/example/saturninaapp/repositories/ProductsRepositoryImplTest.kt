package com.example.saturninaapp.repositories

import com.example.saturninaapp.models.LoginCredentials
import com.example.saturninaapp.util.RetrofitHelper
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.math.exp


class ProductsRepositoryImplTest{

    private val URL: String = "https://api-saturnina.1.us-1.fl0.io/api/v1/"

    private lateinit var repository: ProductsCommentsProfileRepository
    private lateinit var testApis: TestApis

    private var credentials = LoginCredentials("Reverb1@outlook.es","123456789A*")

    @Before
    fun setUp(){
        testApis = apiHelper.testApiInstance(URL)
        repository = ProductsRepositoryImpl(testApis)
    }


    @Test
    fun `bring all products from API`() = runTest {
        val expectedResponse = repository.getItemsProducts()
        assert(expectedResponse.body != null)
    }


    @Test
    fun `user get Loged in by inserting credentials and it'll return user's data`() = runTest {
        val expectedResponse = repository.loginUser(credentials)
        print("Message: " + expectedResponse.body?.detail)
        assert(expectedResponse.body != null)
    }


    @Test
    fun `get general comments from all users`() = runTest {
        val expectedResponse = repository.getAllComments()
        print("Message: " + expectedResponse.body?.detail)
        assert(expectedResponse.body != null)
    }
}