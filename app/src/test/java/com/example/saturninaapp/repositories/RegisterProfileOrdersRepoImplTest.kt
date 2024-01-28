package com.example.saturninaapp.repositories

import com.example.saturninaapp.models.UpdateUserProfilePut
import com.example.saturninaapp.models.User
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RegisterProfileOrdersRepoImplTest{

    private val URL: String = "https://api-saturnina.1.us-1.fl0.io/api/v1/"

    private lateinit var repository: RegisterUpdateProfileOrdersRepository
    private lateinit var testApis: TestApisCreate

    private var user = User("Jhon","Doe","0","Johnfoe.com","12345678898496840FGHSFHSGTHGFH*")
    private var userToken = ""
    private var userId = ""
    private var newProfileData = UpdateUserProfilePut("JOSE","josloco665@gmail.com","PINOS","1112223336")


    @Before
    fun setUp(){
        testApis = apiHelperCreate.testApiInstance(URL)
        repository = RegisterProfileOrdersRepoImpl(testApis)
    }



    @Test
    fun `shouldn't be able to create account with the wrong data`()= runTest {

    }


    @Test
    fun `update user profile date by sending UpdateUserProfilePut object`()= runTest {

    }


    @Test
    fun `get all orders made by the user with id`()= runTest {

    }


    @Test
    fun `give error when getting orders from a non existing userId, aka user doesn't exist`()= runTest {

    }

}