package com.wisal.android.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView
import com.wisal.android.data.paging.R
import com.wisal.android.ext.setDrawableLeft
import com.wisal.android.models.Status


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

