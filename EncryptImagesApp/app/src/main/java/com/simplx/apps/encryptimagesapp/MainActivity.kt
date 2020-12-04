package com.simplx.apps.encryptimagesapp

import android.Manifest
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.simplx.apps.encryptimagesapp.utils.MyEncryptionMaker
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {

    lateinit var myDir: File

    companion object {
        private val FILE_NAME_ENC = "person_1_enc"
        private val FILE_NAME_DEC = "person_1_dec.png"

        private val key = "PDY80oOtPHNYz1FG7"
        private val specString = "yoe6Nd84MOZCzbbO"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Dexter.withActivity(this).withPermissions(
            *arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
            .withListener(
                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        encryptBtn.isEnabled = true
                        decryptBtn.isEnabled = true
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        Toast.makeText(
                            applicationContext,
                            "You Must accept the permission", Toast.LENGTH_LONG
                        ).show()
                    }

                }
            ).check()

        val root = Environment.getExternalStorageDirectory().toString()
        myDir = File("$root/saved_images")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }

        encryptBtn.setOnClickListener {
            // CONVERT DRAWABLE TO BITMAP
            val drawable = ContextCompat.getDrawable(this, R.drawable.person_1)
            val bitmapDrawable = drawable as BitmapDrawable
            val bitmap = bitmapDrawable.bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val input = ByteArrayInputStream(stream.toByteArray())

            val outputFileEnc = File(myDir, FILE_NAME_ENC)

            try {
                MyEncryptionMaker.encryptToFile(
                    key,
                    specString,
                    input,
                    FileOutputStream(outputFileEnc)
                )

                Toast.makeText(this, "Encrypted.", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        decryptBtn.setOnClickListener {
            val outputFileDec = File(myDir, FILE_NAME_DEC)
            val encFile = File(myDir, FILE_NAME_ENC)

            try {
                MyEncryptionMaker.decryptToFile(
                    key,
                    specString,
                    FileInputStream(encFile),
                    FileOutputStream(outputFileDec)
                )

                image_view.setImageURI(Uri.fromFile(outputFileDec))
                outputFileDec.delete()

                Toast.makeText(this, "Decrypted.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
}

