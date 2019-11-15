package com.example.recyclerviewkotlin2019

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import java.net.HttpURLConnection
import java.net.URL
import android.content.Intent

import android.app.Activity
import android.database.Cursor

import android.net.Uri
import it.sauronsoftware.ftp4j.FTPClient
import java.io.*
import android.provider.MediaStore

import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_inserta_coche.*


class InsertaCoche : AppCompatActivity() {

    var imageUri:Uri? = null

    var ruta:String = ""

    var t = Thread()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inserta_coche)
        checkPermissions()
    }

    fun insertarCoche(v: View){
        //esta funcion convierte un objeto coche en un objeto json y se lo manda a la bd

        //creamos objeto coche
        val coche = Coche(et_matricula.text.toString(), et_marca.text.toString(), et_modelo.text.toString(),
            et_precio.text.toString().toDouble(), et_comb.text.toString(), "")


        subeImagen()



        val tdos = Thread(Runnable{

            //obtener la ruta de la imagen
            val res = "http://iesayala.ddns.net/christian/imagenes/"+ruta
            coche.ruta = res

            //trnasformamos el coche en json
            val gson = Gson()
            val stringJson = "["+gson.toJson(coche)+"]"

            println(stringJson)



            val url = "http://iesayala.ddns.net/christian/php/insertarCoches.php"

            post(url, stringJson)


        })

        t.start()
        t.join()
        tdos.start()


        finish()


    }



    fun fotoGaleria(v: View){
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, 1)


    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
        }
        else if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data

        }
    }


    fun subeImagen(){

        t = Thread(Runnable{
            try {
                val mFtpClient = FTPClient()
                mFtpClient.connect("80.26.235.16", 21)
                mFtpClient.login("christian", "password")
                mFtpClient.type = FTPClient.TYPE_BINARY
                mFtpClient.setPassive(true)
                mFtpClient.noop()
                mFtpClient.changeDirectory("/imagenes/")

                //obtener la ruta de la imagen

                val rut = obtenerRuta(imageUri!!)

                val index = rut!!.lastIndexOf('/')

                println(index)

                ruta = rut!!.substring(index+1,rut.length)

                println(ruta)



                mFtpClient.upload(File(rut))
                mFtpClient.sendSiteCommand("chmod " + "777 " + "/imagenes/"+ruta)
                mFtpClient.disconnect(true)



            } catch (e: Exception) {
                e.printStackTrace()
            }

        })




    }

    private fun checkPermissions() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1052
            )

        }

    }

    fun obtenerRuta(uri:Uri): String? {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = applicationContext.getContentResolver().query(uri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor!!.moveToFirst()
            return cursor!!.getString(column_index)
        } finally {
            if (cursor != null) {
                cursor!!.close()
            }
        }
    }

    fun post(url: String, body: String): String {
        return URL(url)
            .openConnection()
            .let {
                it as HttpURLConnection
            }.apply {
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                requestMethod = "POST"

                doOutput = true
                val outputWriter = OutputStreamWriter(outputStream)
                outputWriter.write(body)
                outputWriter.flush()
            }.let {
                if (it.responseCode == 200) it.inputStream else it.errorStream
            }.let { streamToRead ->
                BufferedReader(InputStreamReader(streamToRead)).use {
                    val response = StringBuffer()

                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    it.close()
                    response.toString()
                }
            }
    }
}
