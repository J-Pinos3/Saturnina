package com.example.saturninaapp.activities

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.saturninaapp.R
import com.example.saturninaapp.adapters.ItemClothesAdapter
import com.example.saturninaapp.models.CategoryClothes
import com.example.saturninaapp.models.ItemClothes
import com.google.android.material.navigation.NavigationView

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var drawer: DrawerLayout
    lateinit var toggle: ActionBarDrawerToggle

    //for the recycler view that'll show items in the dashboard
    private lateinit var rvProductsDash: RecyclerView
    private lateinit var itemClothesAdapter: ItemClothesAdapter

    private val itemsClothes = mutableListOf<ItemClothes>(
        ItemClothes("Camiseta bordada",10, 20.35, 'X', CategoryClothes.bordado),
        ItemClothes("Zapatos pintados",10, 10.35, 'X', CategoryClothes.bordado),
        ItemClothes("Chompa con estampado",10, 39.56, 'X', CategoryClothes.estampado),
        ItemClothes("Camiseta con estampado",10, 20.11, 'X', CategoryClothes.estampado),

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close)

        drawer.addDrawerListener(toggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)


        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        //recycler
        rvProductsDash = findViewById(R.id.rvProductsDash)
        itemClothesAdapter = ItemClothesAdapter(itemsClothes)
        rvProductsDash.layoutManager = LinearLayoutManager(this)
        rvProductsDash.adapter = itemClothesAdapter

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when( item.itemId ){
            R.id.nav_item_one -> Toast.makeText(this, "Item 1", Toast.LENGTH_SHORT).show()
            R.id.nav_item_two -> Toast.makeText(this, "Item 2", Toast.LENGTH_SHORT).show()
            R.id.nav_item_three -> Toast.makeText(this, "Item 3", Toast.LENGTH_SHORT).show()
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if( toggle.onOptionsItemSelected(item) ){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}