package com.example.recyclerviewkotlin2019

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class AdaptadorCoches(var lista: ArrayList<Coche>): RecyclerView.Adapter<AdaptadorCoches.MiViewHolder>(){


    class MiViewHolder(view: View):RecyclerView.ViewHolder(view){
        fun enlazaItems(datos:Coche){
            val titulo:TextView=itemView.findViewById(R.id.titulo)
            val imagen:ImageView=itemView.findViewById(R.id.imagen)

            titulo.text= datos.Marca + " " + datos.Modelo + " " + datos.Precio

            Glide.with(itemView.context).load(datos.ruta).into(imagen)

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiViewHolder {
       val v=LayoutInflater.from(parent.context).inflate(R.layout.contenido_item,parent,false)
       return MiViewHolder(v)
    }

    override fun getItemCount(): Int {
      return lista.size
    }
////////////MiViewHolder.ViewHolder
    override fun onBindViewHolder(holder: MiViewHolder, position: Int) {
        holder.enlazaItems(lista[position])
    }
}