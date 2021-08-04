package com.example.bestrecipes.view.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.bestrecipes.R
import com.example.bestrecipes.databinding.ActivityAddUpdateDishBinding
import com.example.bestrecipes.databinding.DialogCustomImageSelectionBinding
import com.karumi.dexter.Dexter
import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding: ActivityAddUpdateDishBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setupActionBar()
        mBinding.ivAddDishImage.setOnClickListener(this)
    }

    private fun setupActionBar() {
        setSupportActionBar(mBinding.toolbarAddDishActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mBinding.toolbarAddDishActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_add_dish_image -> {
                    customImageSelectionDialog()
                    return
                }
            }
        }
    }

    private fun customImageSelectionDialog() {
        val dialog = Dialog(this)
        val binding: DialogCustomImageSelectionBinding =
            DialogCustomImageSelectionBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        binding.tvCamera.setOnClickListener {
            Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                //this is not required for newer devices
                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        //if report not empty, clean way
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(
                                this@AddUpdateDishActivity, "You have camera permission now",
                                Toast.LENGTH_SHORT
                            ).show()

//                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                            startActivityForResult(intent, CAMERA)
                        }
                    }

//                    else {
//                        Log.e("<><><>","You have camera permission now - else")
//                        showRationalDialogForPermissions()
//                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
//                    token?.continuePermissionRequest()
                    //to display default dialog again
                }
            }
            ).onSameThread().check()

            dialog.dismiss()
        }
        binding.tvGallery.setOnClickListener {
            Dexter.withContext(this).withPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    Toast.makeText(
                        this@AddUpdateDishActivity, "You have gallery permission now",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    Toast.makeText(
                        this@AddUpdateDishActivity, "You have denied the gallery permission",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }

            }).onSameThread().check()

            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage(
                "Permissions required for this feature." +
                        "It can be enable under Application Settings"
            )
            .setPositiveButton("GO TO SETTINGS")
            { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("CANCEL")
            { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    companion object {
        private const val CAMERA = 1
    }
}