package org.bitc.petpalapp.util

import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import org.bitc.petpalapp.Manifest
import org.bitc.petpalapp.ui.petstargram.WriteFragment
import java.text.SimpleDateFormat
import java.util.Date

fun myCheckpermission(fragment:AppCompatDialogFragment){
    val requestPermissionLauncher=fragment.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){isGrated ->
        if(isGrated){
            Toast.makeText(fragment.requireContext(),"권한승인",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(fragment.requireContext(),"권한거부",Toast.LENGTH_SHORT).show()
        }
    }
//    if(ContextCompat.checkSelfPermission(
//        fragment.requireContext(),
////        Manifest.permission.READ_EXTERNAL_STORAGE
//    )!=PackageManager.PERMISSION_GRANTED
//        ){
//        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
//    }

}
//메인액티비티에 권한요청

fun dateToString(date:Date):String{
    val format=SimpleDateFormat("yyyy-MM-dd")
    return format.format(date)
}