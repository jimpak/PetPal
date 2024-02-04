package org.bitc.petpalapp.ui.mypet.util

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import org.bitc.petpalapp.R

class ImageAdapter(private val context: Context) :  PagerAdapter() {

    private var imagesList: List<ImageModel> = emptyList()

    fun setImageList(images: List<ImageModel>) {
        this.imagesList = images
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return imagesList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image, container, false)
        val imageView: ImageView = view.findViewById(R.id.regiimgResult)

        val imageUrl = imagesList[position].imageUrl
        Glide.with(context)
            .load(imageUrl)
            .into(imageView)

        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}