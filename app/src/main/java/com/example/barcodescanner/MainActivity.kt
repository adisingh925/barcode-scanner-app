package com.example.barcodescanner

import android.R.attr
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.IOException


class MainActivity : AppCompatActivity() {

    lateinit var img:ImageView

    lateinit var textview:TextView

    lateinit var image2:InputImage

    lateinit var image1:InputImage

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC,
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_CODE_93,
            Barcode.FORMAT_CODABAR,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_ITF,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
            Barcode.FORMAT_PDF417,
            Barcode.FORMAT_DATA_MATRIX)
        .build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val capture = findViewById<Button>(R.id.button)

        val upload = findViewById<Button>(R.id.button2)

        img = findViewById(R.id.imageView)

        capture.setOnClickListener()
        {
            Dexter.withContext(this).withPermission(android.Manifest.permission.CAMERA)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent,111)
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        TODO("Not yet implemented")
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest?,
                        token: PermissionToken?
                    ) {
                        token?.continuePermissionRequest()
                    }

                }).check()
        }

        upload.setOnClickListener()
        {
            Dexter.withContext(this).withPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE).withListener(object :PermissionListener
            {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val intent = Intent()
                    intent.setType("image/*")
                    intent.setAction(Intent.ACTION_PICK)
                    startActivityForResult(Intent.createChooser(intent,"select image"),222)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    TODO("Not yet implemented")
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

            }).check()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == RESULT_OK && requestCode == 111)
        {
            var bitmap = data?.extras?.get("data") as Bitmap
            img.setImageBitmap(bitmap)
            image1 = InputImage.fromBitmap(bitmap, 0)
            var scanner = BarcodeScanning.getClient(options)
            var result = scanner.process(image1)
                .addOnSuccessListener { barcodes ->
                    Toast.makeText(this,"Success", Toast.LENGTH_SHORT).show()
                    for (barcode in barcodes) {
                        var bounds = barcode.boundingBox
                        var corners = barcode.cornerPoints

                        var rawValue = barcode.rawValue

                        var builder = AlertDialog.Builder(this)
                        builder.setTitle("code data")
                        builder.setMessage("$rawValue")
                        builder.setNegativeButton("cancel", DialogInterface.OnClickListener
                        { dialog,which ->
                            dialog.cancel()
                        })

                        builder.show()
                    }

                }
                .addOnFailureListener {
                    Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
                }
        }
        if(requestCode == 222 && resultCode == RESULT_OK)
        {
            if (data != null) {
                img.setImageURI(data.data)
            }

            try {
                if (data != null) {
                    image2 = InputImage.fromFilePath(this,data.data)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            var scanner = BarcodeScanning.getClient(options)
            var result = scanner.process(image2)
                .addOnSuccessListener { barcodes ->
                    Toast.makeText(this,"Success", Toast.LENGTH_SHORT).show()
                    for (barcode in barcodes) {
                        var bounds = barcode.boundingBox
                        var corners = barcode.cornerPoints

                        var rawValue = barcode.rawValue

                        var builder = AlertDialog.Builder(this)
                        builder.setTitle("code data")
                        builder.setMessage("$rawValue")
                        builder.setNegativeButton("cancel", DialogInterface.OnClickListener
                        { dialog,which ->
                            dialog.cancel()
                        })

                        builder.show()
                    }

                }
                .addOnFailureListener {
                    Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
                }
        }
    }
}