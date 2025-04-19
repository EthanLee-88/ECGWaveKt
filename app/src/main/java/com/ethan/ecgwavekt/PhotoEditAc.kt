package com.ethan.ecgwavekt

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.ethan.ecgwavekt.databinding.AcPhotoEditBinding
import ja.burhanrashid52.photoeditor.*
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeType
import kotlinx.coroutines.launch

class PhotoEditAc : AppCompatActivity() {
    companion object {
        private const val TAG = "PhotoEditAc"

        fun startMe(context: Context) {
            context.startActivity(Intent(context, PhotoEditAc::class.java))
        }
    }

    private lateinit var binding: AcPhotoEditBinding
    private lateinit var mPhotoEditor: PhotoEditor
    private lateinit var mShapeBuilder: ShapeBuilder
    private lateinit var mSaveFileHelper: FileSaveHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AcPhotoEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mPhotoEditor = PhotoEditor.Builder(this, binding.photoEditorView)
            .setPinchTextScalable(true) // set flag to make text scalable when pinch
            .build()
        //Set Image Dynamically
        binding.photoEditorView.source.setImageResource(R.drawable.paris_tower)
        mPhotoEditor.setBrushDrawingMode(true)
        mShapeBuilder = ShapeBuilder()
        mPhotoEditor.setShape(mShapeBuilder)
        mPhotoEditor.setShape(mShapeBuilder.withShapeSize(10F))
        mPhotoEditor.setShape(
            mShapeBuilder.withShapeColor(
                ContextCompat.getColor(
                    this,
                    R.color.brown_color_picker
                )
            )
        )
        mSaveFileHelper = FileSaveHelper(this)
        setViewListener()
    }

    private fun setViewListener() {
        binding.apply {
            imgUndo.setOnClickListener {
                mPhotoEditor.undo()
            }
            imgRedo.setOnClickListener {
                mPhotoEditor.redo()
            }
            shapeRec.setOnClickListener {
                mPhotoEditor.setShape(mShapeBuilder.withShapeType(ShapeType.Rectangle))
            }
            shapeArrow.setOnClickListener {
                mPhotoEditor.setShape(mShapeBuilder.withShapeType(ShapeType.Arrow()))
            }
            imgSave.setOnClickListener {
                saveImage()
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    private fun saveImage() {
        val fileName = System.currentTimeMillis().toString() + ".png"
        val hasStoragePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        if (hasStoragePermission || FileSaveHelper.isSdkHigherThan28()) {
            mSaveFileHelper.createFile(fileName, object : FileSaveHelper.OnFileCreateResult {
                @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
                override fun onFileCreateResult(
                    created: Boolean,
                    filePath: String?,
                    error: String?,
                    uri: Uri?,
                ) {
                    lifecycleScope.launch {
                        if (created && filePath != null) {
                            val saveSettings = SaveSettings.Builder()
                                .setClearViewsEnabled(true)
                                .setTransparencyEnabled(true)
                                .build()
                            val result = mPhotoEditor.saveAsFile(filePath, saveSettings)
                            if (result is SaveFileResult.Success) {
                                mSaveFileHelper.notifyThatFileIsNowPubliclyAvailable(contentResolver)
                                binding.photoEditorView.source.setImageURI(uri)
                            } else {

                            }
                        } else {

                        }
                    }
                }
            })
        } else {

        }
    }
}