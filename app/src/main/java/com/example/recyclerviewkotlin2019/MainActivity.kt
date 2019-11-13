package com.example.recyclerviewkotlin2019

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import org.jetbrains.anko.doAsync

import java.net.URL
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {



    companion object{
        var respuestaAPI = ""
    }


    var listaCoches: ArrayList<Coche> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getData().execute()


    }


    fun listar(v: View){

        val json = JSONObject(respuestaAPI)

        val jsonArray = json.getJSONArray("coches")

        for (i in 0 until jsonArray.length()) {
            val coche = jsonArray.getJSONObject(i)

            //obtenemos los datos de los coches
            val matricula = coche.getString("MATRICULA")
            val marca = coche.getString("MARCA")
            val modelo = coche.getString("MODELO")
            val precio = coche.getDouble("PRECIO")
            val comb = coche.getString("COMBUSTIBLE")
            val img = coche.getString("IMAGEN")


            val c = Coche(matricula,marca,modelo,precio,comb,img)

            listaCoches.add(c)

        }

        val recyclerView:RecyclerView=findViewById(R.id.recycler)
        recyclerView.layoutManager=LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        val adaptador=AdaptadorCoches(listaCoches)
        recyclerView.adapter=adaptador

        val bt = findViewById<Button>(R.id.bt_ver)
        bt.isVisible = false

        val btins = findViewById<Button>(R.id.bt_insertar)
        btins.isVisible = false

    }

    fun muestraInsertar(v: View){
        //mostramos el fragment insertar coche

        val intent = Intent(this, InsertaCoche::class.java)
        startActivity(intent)
    }

    /*

    fun insertarCoche(v: View){
        //esta funcion convierte un objeto coche en un objeto json y se lo manda a la bd
        //subimos la imagen seleccionada al ftp con simpleftp

        //obtener la ruta en la subida al ftp
        val ruta = ""

        //creamos el objeto coche
        val coche = Coche("5555ZZZ", "NISSAN", "QASHQAI",
            24000.00, "GASOLINA", ruta)

        //trnasformamos el coche en json
        val gson = Gson()
        val stringJson = "["+gson.toJson(coche)+"]"

        println(stringJson)

        doAsync {
            val url = "http://iesayala.ddns.net/christian/php/insertarCoches.php"

            post(url, stringJson)

        }

    }
        */


}



class getData() : AsyncTask<Void, Void, String>() {
    override fun doInBackground(vararg params: Void?): String? {
        MainActivity.respuestaAPI = URL("http://iesayala.ddns.net/christian/php/listarCoches.php").readText()
        return "succes"
    }

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
    }
}