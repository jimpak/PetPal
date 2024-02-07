//package org.bitc.petpalapp.ui.petstargram
//
//import androidx.lifecycle.ViewModelProvider
//import android.os.Bundle
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.firestoreSettings
//import org.bitc.petpalapp.R
//import org.bitc.petpalapp.databinding.FragmentPetDetailBinding
//import org.bitc.petpalapp.recyclerviewAdapter.setMyViewHolder
//
//
//class DetailFragment : Fragment() {
//    var friestore: FirebaseFirestore? = null
//    lateinit var binding: FragmentPetDetailBinding
//
//
//    companion object {
//        fun newInstance() = DetailFragment()
//    }
//
//    private lateinit var viewModel: DetailViewModel
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        friestore = FirebaseFirestore.getInstance()
//        return inflater.inflate(R.layout.fragment_detail, container, false)
//    }
//
//    inner class DetailView : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//        var itemList: ArrayList<itemPetstar> = arrayListOf()
//
//        init {
//            friestore?.collection("images")
//                ?.orderBy("data")
//                ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//                    if(firebaseFirestoreException != null){
//                        return@addSnapshotListener
//                    }
//                    itemList.clear()
//                    for (document in querySnapshot!!) {
////                        var item = querySnapshot.toObjects(itemList::class.java)
//                        val item = document.toObject(itemList::class.java)
//                        itemList.add(item)
//                    }
//                    notifyDataSetChanged()
//                }
//        }
//        //firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { querySnapshot, firebaseFirestoreException->
//
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//            val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.itempetstar_detail, parent, false)
//            return CustomViewHolder(view)
//        }
//
//
//
//        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)
//
//        override fun getItemCount(): Int {
//            return itemList.size
//        }
//        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//            val viewHolder=(holder as CustomViewHolder).itemView
//
//                //ID
//            viewHolder.detail_profile.text=itemList[position].docId
//
////            이미지
//            Glide
//                .with(holder.itemView.context)
//                .load(itemList!![position].filename)
//                .into(viewHolder.)
//
//
//
//
//        }
//
//    }
//}
//
