package com.lugares.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase

import com.lugares.model.Lugar


class LugarDao {

    private val coleccion1 = "lugaresApp"
    private val usuario= Firebase.auth.currentUser?.email.toString()
    private val coleccion2 = "misLugares"

    //Obtener la instancia de la base de datos de FireStore
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        firestore.firestoreSettings= FirebaseFirestoreSettings.Builder().build()
    }


    fun getAllData() : MutableLiveData<List<Lugar>>{
        val listalugares = MutableLiveData<List<Lugar>>()

        //Recuperar todos los "documentos" / "lugares" de nuestra coleccion "misLugares"
        firestore.collection(coleccion1).document(usuario).collection(coleccion2)
            .addSnapshotListener{ instantanea , e ->
                 if (e != null) {
                     return@addSnapshotListener
                 }
                if (instantanea != null) { //Si hay info se recuperan los datos
                    val lista = ArrayList<Lugar>()
                    //Se recorre la instantanea...
                    instantanea.documents.forEach() {
                        val lugar = it.toObject(Lugar :: class.java)
                        if (lugar!=null){
                            lista.add(lugar)
                        }
                    }
                    listalugares.value = lista
            }
            }

        return listalugares
    }


    fun saveLugar (lugar : Lugar) {
        val documento : DocumentReference
        if (lugar.id.isEmpty()) { //Si no hay id, entonces es un lugar nuevo
            documento = firestore.collection(coleccion1).document(usuario)
                .collection(coleccion2).document()
            lugar.id = documento.id
        } else { //significa que el lugar existe entonces lo voy a modificar..
            documento = firestore.collection(coleccion1).document(usuario)
                .collection(coleccion2).document(lugar.id)
        }
        documento.set(lugar)
            .addOnSuccessListener {
                Log.d("saveLugar", "Lugar agregado/modificado")
            }
            .addOnCanceledListener {
                Log.e("saveLugar", "ERROR Lugar no agregado/modificado")
            }
    }




    fun deleteLugar (lugar : Lugar){
        if (lugar.id.isNotEmpty()){ //El lugar existe
            firestore.collection(coleccion1).document(usuario)
                .collection(coleccion2).document(lugar.id).delete()
                .addOnSuccessListener {
                    Log.d("deleteLugar", "Lugar eliminado")
                }
                .addOnCanceledListener {
                    Log.e("deleteLugar", "ERROR Lugar no eliminado")
                }
        }
    }


}