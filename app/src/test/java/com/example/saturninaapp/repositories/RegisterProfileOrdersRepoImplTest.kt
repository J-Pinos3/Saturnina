package com.example.saturninaapp.repositories

import com.example.saturninaapp.models.OrderStatusData
import com.example.saturninaapp.models.UpdateUserProfilePut
import com.example.saturninaapp.models.User
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RegisterProfileOrdersRepoImplTest{

    private val URL: String = "https://api-saturnina.1.us-1.fl0.io/api/v1/"

    private lateinit var repository: RegisterUpdateProfileOrdersRepository
    private lateinit var testApis: TestApisCreate

    private var user = User("Jhon","De","0","Johnfoe.com","12345678898496840FGHSFHSGTHGFH*")
    private var userToken = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidXNlcl9zYXR1cm5pbmE6OWQ5ajMwNnN1MmdpMHV1YXVodW8iLCJyb2wiOiJyb2w6NzRydnE3amF0em82YWMxOW1jNzkiLCJleHBpcmVzIjoxNzA2NTMwMDkyLjEzMTg5MjJ9.XozyKOBpSj2jdTDXfSGi1duxljYdHKcbaBBpYIoKK4c"
    private var userId = "user_saturnina:9d9j306su2gi0uuauhuo"
    private var newProfileData = UpdateUserProfilePut("JOSE","josloco665@gmail.com","PINOS","1112223336")
    private var orderStatus = OrderStatusData("Finalizado","Felicidades tu compra fue aprobada")
    private var id_order_detail: String ="order:jsbrtewpyz7xow8978mo"

    @Before
    fun setUp(){
        testApis = apiHelperCreate.testApiInstance(URL)
        repository = RegisterProfileOrdersRepoImpl(testApis)
    }



    @Test
    fun `shouldn't be able to create account with the wrong data`()= runTest {
        val expectedResponse = repository.createUser(user)
        println(expectedResponse.errorMessage.toString())
        assert(expectedResponse.errorMessage.toString() == "Error al crear usuario")
    }


    @Test
    fun `update user profile date by sending UpdateUserProfilePut object`()= runTest {
        val expectedResponse = repository.updateUserProfile(userToken, userId, newProfileData)
        println("Message: " + expectedResponse.body)
        assert(expectedResponse.body != null)
    }


    @Test
    fun `get all orders made by the user with id`()= runTest {
        val ordersResponse = repository.getOrdersForUser(userToken, userId)
        println("Message: ")
        println("o1: ${ordersResponse.body?.detail?.get(0)?.result?.get(0)?.cantidad} ${ordersResponse.body?.detail?.get(0)?.result?.get(0)?.descripcion}" +
                " ${ordersResponse.body?.detail?.get(0)?.result?.get(0)?.status} ${ordersResponse.body?.detail?.get(0)?.result?.get(0)?.id_orden?.price_order}")
        assert(ordersResponse.body?.detail?.get(0)?.result?.size != null)
    }


    @Test
    fun `get user's profile data`()= runTest {
        val expectedResponse = repository.getUserProfile(userToken)
        println("Profile: " + expectedResponse.body?.detail?.nombre + " ${expectedResponse.body?.detail?.apellido}")
        //assert(expectedResponse.body != null)
    }


    @Test
    fun `update status for an existing order`()= runTest {
        val expectedResponse = repository.uptdateOrderStatus(userToken, orderStatus, id_order_detail)
        assert(expectedResponse.body != null)
    }

}