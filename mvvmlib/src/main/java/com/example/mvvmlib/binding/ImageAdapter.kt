package com.example.mvvmlib.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter

object ImageAdapter {

    @BindingAdapter(value = ["url", "placeholder"], requireAll = false)
    @JvmStatic
    fun setImageUrl(imageView: ImageView, url: String, placeholder: Int) {
//        Glide.with(imageView.context)
//            .load(url)
//            .apply(RequestOptions().placeholder(placeholder))
//            .into(imageView)

    }

}