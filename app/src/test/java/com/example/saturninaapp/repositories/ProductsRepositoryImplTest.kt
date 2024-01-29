package com.example.saturninaapp.repositories

import com.example.saturninaapp.models.LoginCredentials
import com.example.saturninaapp.models.RecoverPassword
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

    private var userToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidXNlcl9zYXR1cm5pbmE6bnh6ODloOGI0OHZrcnhuZGR5N2ciLCJyb2wiOiJyb2w6dnVxbjdrNHZ3MG0xYTN3dDdma2IiLCJleHBpcmVzIjoxNzA2NDQ1MzI2LjY2MDMyMzF9.aHiPR6TkJDUP0josHqHioU6b_VFBO_266Lc0iqYL9hE"
    private var credentials = LoginCredentials("Reverb1@outlook.es","123456789A*")

    private var wrongEmail = RecoverPassword("hola@gmail.com")
    @Before
    fun setUp(){
        testApis = apiHelper.testApiInstance(URL)
        repository = ProductsRepositoryImpl(testApis)
    }


    @Test
    fun `bring all products from API`() = runTest {
        val expectedResponse = repository.getItemsProducts()
        println("Message: ")
        println("P1: ${expectedResponse.body?.detail?.get(0)?.name} ${expectedResponse.body?.detail?.get(0)?.precio}")
        println("P2: ${expectedResponse.body?.detail?.get(1)?.name} ${expectedResponse.body?.detail?.get(2)?.precio}")
        assert(expectedResponse.body?.detail?.size!! >= 1)
    }


    @Test
    fun `bring all categories from API`()= runTest {
        val expectedResponse = repository.getClothesCategories(userToken)
        println("Message: category " + expectedResponse.body?.detail?.get(0)?.name)
        assert(expectedResponse.body?.detail?.size!! >= 1)
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

    @Test
    fun `give error when the email entered doesn't exist`()= runTest {
        val expectedResponse = repository.recoverUserPassword(wrongEmail)
        println(expectedResponse.errorMessage.toString())
        assert(expectedResponse.errorMessage.toString() == "Error al recuperar la contraseña")
    }
}