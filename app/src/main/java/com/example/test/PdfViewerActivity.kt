package com.example.test

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import com.github.barteksc.pdfviewer.PDFView
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class PdfViewerActivity : AppCompatActivity(), View.OnClickListener{
    private lateinit var backBtnImg: ImageView

    // a variable for our pdf view.
    lateinit var pdfView: PDFView
    lateinit var file:File

    // on below line we are creating a variable for our pdf view url.
    var pdfUrl = "https://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        val actionBar: ActionBar?
        actionBar = supportActionBar
        // with color hash code as its parameter
        val colorDrawable = ColorDrawable(Color.parseColor("#FFFFFF"))
        // Set BackgroundDrawable
        actionBar?.setElevation(0F);
        actionBar?.setBackgroundDrawable(colorDrawable)

        setContentView(R.layout.activity_pdf_viewer)
        // our pdf view with its id.
        pdfView = findViewById(R.id.pdfView)
        backBtnImg = findViewById(R.id.back_button_img)
        file = intent.getSerializableExtra("file") as File
        // it along with pdf view url.
       // RetrievePDFFromURL(pdfView).execute(pdfUrl)
        pdfView.fromFile(file).load()
        Log.d("file", file.path)
    }

    fun animate() {
        overridePendingTransition(
            R.anim.slide_out_right,
            R.anim.slide_in_left
        );
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.back_button_img -> {
                    val i = Intent(this@PdfViewerActivity, StartActivity::class.java)
                    intent.putExtra("fromPdf", true)
                    startActivity(i)
                    animate()
                }
                else -> {
                    print("Error")
                }
            }

        }
    }













    // to it as a parameter.
    class RetrievePDFFromURL(pdfView: PDFView) :
        AsyncTask<String, Void, InputStream>() {
        // on below line we are creating a variable for our pdf view.
        val mypdfView: PDFView = pdfView
        // on below line we are calling our do in background method.
        override fun doInBackground(vararg params: String?): InputStream? {
            // on below line we are creating a variable for our input stream.
            var inputStream: InputStream? = null
            try {
                // on below line we are creating an url
                // for our url which we are passing as a string.
                val url = URL(params.get(0))
                // on below line we are creating our http url connection.
                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection

                // 200 response code means response is successful
                if (urlConnection.responseCode == 200) {
                    // on below line we are initializing our input stream
                    // if the response is successful.
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            }
            // on below line we are adding catch block to handle exception
            catch (e: Exception) {
                // our exception and returning null
                e.printStackTrace()
                return null;
            }
            // on below line we are returning input stream.
            return inputStream;
        }


        // on below line we are calling on post execute
        // method to load the url in our pdf view.
        override fun onPostExecute(result: InputStream?) {
            // on below line we are loading url within our
            // pdf view on below line using input stream.
            mypdfView.fromStream(result).load()

        }
    }









}
