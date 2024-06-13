package com.example.demo.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.demo.R
import com.example.demo.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    private val mapaParesOrigenDestino: MutableMap<Int, Int> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.resolver = applicationContext.contentResolver

        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        drawerLayout = binding.drawerLayout

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostHome) as NavHostFragment
        val navController = navHostFragment.navController

        NavigationUI.setupWithNavController(binding.navView, navController)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        setContentView(view)

        agregarParOrigenDestino(R.id.dia_list_fragment, R.id.home_fragment)
        agregarParOrigenDestino(R.id.recorr_list_fragment, R.id.dia_list_fragment)
        agregarParOrigenDestino(R.id.unsoc_list_fragment, R.id.recorr_detail_fragment)
    }

    override fun onSupportNavigateUp(): Boolean {

        val navController = this.findNavController(R.id.navHostHome)
        var fragActual = navController.currentDestination?.id

        /*
         Verifica si el fragmento actual tiene un destino particular
         si no lo tiene, sale por else y ejecuta comportamiento default
         */
        val fragDestino = mapaParesOrigenDestino[fragActual]

        return if (fragDestino != null) {
            // Desapila hasta que alcances el fragmento de destino
            while (fragActual != fragDestino) {
                navController.popBackStack()

                val nuevoFragActual = navController.currentDestination?.id
                if (nuevoFragActual == fragActual) break // Salir si no se ha movido a un nuevo fragmento

                fragActual = nuevoFragActual
            }
            true
        } else {
            NavigationUI.navigateUp(navController, drawerLayout)
        }
    }

    private fun agregarParOrigenDestino(idOrigen: Int, idDestino: Int) {
        mapaParesOrigenDestino[idOrigen] = idDestino
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.navHostHome)
        val currentFragment = navController.currentDestination?.id

        // Verifica si estás en el fragmento inicial de activity_main.xml
        if (currentFragment == R.id.home_fragment) {
            super.onBackPressed()
            return
        } else {
            onSupportNavigateUp()
        }
    }
}