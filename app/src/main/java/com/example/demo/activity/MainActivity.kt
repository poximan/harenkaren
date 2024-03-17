package com.example.demo.activity

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.demo.R
import com.example.demo.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp

private lateinit var binding: ActivityMainBinding
private lateinit var appBarConfiguration: AppBarConfiguration
private lateinit var drawerLayout: DrawerLayout

class MainActivity : AppCompatActivity() {

    private val mapaParesOrigenDestino: MutableMap<Int, Int> = mutableMapOf()

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        val latitudTextView = findViewById<TextView>(R.id.latitud)
        try {
            savedInstanceState.putString("latAnterior", latitudTextView.text.toString())
        }
        catch (nulo : NullPointerException){
            savedInstanceState.putString("latAnterior", "0.0")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            val latitud: TextView? = findViewById(R.id.latitud)
            latitud?.text = savedInstanceState.getString("latAnterior")
        }

        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        drawerLayout = binding.drawerLayout

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.theNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController

        val extras = intent.getStringExtra("KEY")
        if (extras != null && extras == "NOTIFICATION") {
            val notificationBody = intent.getStringExtra("body1")
            val notificationTitle = intent.getStringExtra("title")

            val bundle = Bundle().apply {
                putString("notificationTitle", notificationTitle)
                putString("notificationBody", notificationBody)
            }

            val destinationId = R.id.show_message_fragment
            navController.navigate(destinationId, bundle)
        }

        NavigationUI.setupWithNavController(binding.navView, navController)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        setContentView(view)

        agregarParOrigenDestino(R.id.dia_list_fragment,R.id.home_fragment)
        agregarParOrigenDestino(R.id.recorr_list_fragment,R.id.dia_detail_fragment)
        agregarParOrigenDestino(R.id.unsoc_list_fragment,R.id.recorr_detail_fragment)
    }

    override fun onSupportNavigateUp(): Boolean {

        val navController = this.findNavController(R.id.theNavHostFragment)
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
    fun agregarParOrigenDestino(idOrigen: Int, idDestino: Int) {
        mapaParesOrigenDestino[idOrigen] = idDestino
    }

    fun onSupportNavigateUp2(): Boolean {

        val navController = this.findNavController(R.id.theNavHostFragment)

        var fragActual = navController.currentDestination?.id
        val fragObservado = R.id.unsoc_list_fragment
        val fragObjetivo = R.id.recorr_detail_fragment

        return if(fragActual == fragObservado) {
            while (fragActual != fragObjetivo) {
                navController.popBackStack()    // Desapilar una vez
                fragActual = navController.currentDestination?.id   // ID del nuevo destino
            }
            true
        } else
            NavigationUI.navigateUp(navController, drawerLayout)
    }
}