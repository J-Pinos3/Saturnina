package com.example.saturninaapp.repositories

import com.example.saturninaapp.models.CommentaryData
import com.example.saturninaapp.models.DetailProduct
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

    private var userToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidXNlcl9zYXR1cm5pbmE6OWQ5ajMwNnN1MmdpMHV1YXVodW8iLCJyb2wiOiJyb2w6NzRydnE3amF0em82YWMxOW1jNzkiLCJleHBpcmVzIjoxNzA2NTMyNzQzLjg2MjI4MTh9.qjVvl6wbg4Z6ElPraqnLnKTsmdT1VTGqWeCAcdajbDg"
    private var credentials = LoginCredentials("Reverb1@outlook.es","123456789A*")

    private var wrongEmail = RecoverPassword("hola@gmail.com")

    private var product: DetailProduct = DetailProduct("category:1w6ehoocf4ar2yfyin9h",null, "Reparación de gorra"
    ,"product:8bym7i5dj1nili4m92pq", emptyList(), "Reparación de gorras", 30.0, null)

    private var userId = "user_saturnina:9d9j306su2gi0uuauhuo"
    private var comment: CommentaryData = CommentaryData("me gustaría que el bordado sea rojo con blanco",
    userId, "product:hg7wo0alfquvr38ktas1", 5)

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


    @Test
    fun `add product item to cart`()= runTest {
        val expectedResponse = repository.addProductToCar(product)
        println("Message: $expectedResponse")
    }


    @Test
    fun `give error when the user wants to comment over a given product and haven't get it yet` ()= runTest {
        val expectedResponse = repository.createCommentary(userToken, comment)
        println(expectedResponse.errorMessage.toString())
        assert(expectedResponse.errorMessage.toString() == "Debes realizar una compra primero")
    }
}