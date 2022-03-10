package com.wisal.android.paging.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import com.wisal.android.paging.R
import com.wisal.android.paging.ext.setDrawableLeft
import com.wisal.android.paging.models.Status


@BindingAdapter("imageUrl")
fun ImageView.loadImage(url: String?) {
    url?.let {
        Glide.with(this).load(it).into(this)
    }
}

@BindingAdapter("status")
fun MaterialTextView.status(status: Status) {
    text = status.toString()
    when (status) {
        Status.ALIVE -> setDrawableLeft(R.color.green)
        Status.DEAD -> setDrawableLeft(R.color.red)
        Status.UNKNOWN -> setDrawableLeft(R.color.gray)
    }
}

