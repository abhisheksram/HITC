package com.example.hotc.util

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.example.hotc.BuildConfig
import com.example.hotc.R
import java.io.File


fun Context.showToast(msg : String){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun View.gone(){
    this.visibility = View.GONE
}

fun View.hide(){
    this.visibility = View.INVISIBLE
}

fun View.show(){
    this.visibility = View.VISIBLE
}

fun ImageView.loadImage(file: File) {
    Glide.with(this)
        .load(file)
        .transition(withCrossFade())
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}

fun ImageView.loadImageWithPlaceHolder(file: File) {
    Glide.with(this)
        .load(file)
        .placeholder(R.drawable.place_holder)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .into(this)
}

fun handleError(throwable: Throwable?) {
    throwable?.let { log(throwable) }
}

fun log(throwable: Throwable) {
    log("OG", throwable)
}
fun log(tag: String, throwable: Throwable) {
    if (BuildConfig.DEBUG) {
        Log.e(tag, throwable.localizedMessage!!)
    }
}


