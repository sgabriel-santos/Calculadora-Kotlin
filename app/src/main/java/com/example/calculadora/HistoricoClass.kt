package com.example.calculadora

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
//class HistoricoClass(var expressao: List<String>, var resultado: Array<String>) : Parcelable
class HistoricoClass(var expressao: String, var resultado: String) : Parcelable