package com.gropse.serviceme.activities.user

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.util.Base64
import android.widget.ArrayAdapter
import com.google.gson.Gson
import com.gropse.serviceme.R
import com.gropse.serviceme.activities.both.BaseActivity
import com.gropse.serviceme.network.NetworkClient
import com.gropse.serviceme.network.ServiceGenerator
import com.gropse.serviceme.pojo.BaseResponse
import com.gropse.serviceme.pojo.FileResult
import com.gropse.serviceme.utils.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_video_image.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File


class VideoImageActivity : BaseActivity() {

    var mActivity: Activity? = null
    var iconNumber: String = "1"
    private var imageList = ArrayList<ImageUtility.ImageBean>()
    private var files = ""
    private val REQUEST_TAKE_GALLERY_VIDEO = 987

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_image)
        mActivity = this
        setUpToolbar(R.string.video_images)

        btnDone.circularDrawable()

        ivImage1.setOnClickListener {
            iconNumber = "1"
            showImageChooser()
        }

        ivImage2.setOnClickListener {
            iconNumber = "2"
            showImageChooser()
        }

        ivImage3.setOnClickListener {
            iconNumber = "3"
            showImageChooser()
        }

        ivVideo1.setOnClickListener {
            iconNumber = "5"
            showVideoPicker()
        }

        ivVideo2.setOnClickListener {
            iconNumber = "6"
            showVideoPicker()
        }

        ivVideo3.setOnClickListener {
            iconNumber = "7"
            showVideoPicker()
        }

        btnDone.setOnClickListener {
            val intent = Intent()
            intent.putExtra("files", files)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        if(Prefs(this).image1.isNotBlank()) ivImage1.setImageBitmap(StringToBitMap(Prefs(this).image1))
        if(Prefs(this).image2.isNotBlank()) ivImage2.setImageBitmap(StringToBitMap(Prefs(this).image2))
        if(Prefs(this).image3.isNotBlank()) ivImage3.setImageBitmap(StringToBitMap(Prefs(this).image3))

        if(Prefs(this).video1.isNotBlank()) ivVideo1.loadUrl(Prefs(this).video1)
        if(Prefs(this).video2.isNotBlank()) ivVideo1.loadUrl(Prefs(this).video2)
        if(Prefs(this).video3.isNotBlank()) ivVideo1.loadUrl(Prefs(this).video3)
    }

    private fun showImageChooser() {
        val items = arrayOf("Camera", "Gallery")
        val adapter = ArrayAdapter(mActivity, android.R.layout.select_dialog_item, items)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Image")
        builder.setAdapter(adapter) { dialog, item ->
            if (item == 0) {
                ImageUtility.getImageBean(mActivity, ImageUtility.pickSingle(mActivity, ImageUtility.CAMERA))
                        .subscribe { imageBean: ImageUtility.ImageBean -> setImage(imageBean) }
                dialog.dismiss()
            } else {
                ImageUtility.getImageBean(mActivity, ImageUtility.pickSingle(mActivity, ImageUtility.GALLERY))
                        .subscribe { imageBean: ImageUtility.ImageBean -> setImage(imageBean) }
                dialog.dismiss()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun setImage(bean: ImageUtility.ImageBean) {
        when (iconNumber) {
            "1" -> {
                ivImage1.setImageBitmap(bean.bitmap)
                Prefs(this).image1 = BitMapToString(bean.bitmap)
            }
            "2" -> {
                ivImage2.setImageBitmap(bean.bitmap)
                Prefs(this).image2 = BitMapToString(bean.bitmap)
            }
            "3" -> {
                ivImage3.setImageBitmap(bean.bitmap)
                Prefs(this).image3 = BitMapToString(bean.bitmap)
            }
        }

        prepareMediaToUpload("0", bean.imagePath)

        val type = RequestBody.create(MediaType.parse("text/plain"), "0")

        val file = File(bean.imagePath)
        val requestFile = RequestBody.create(MediaType.parse("image/*"), file)
        val imageFile = MultipartBody.Part.createFormData("file", file.name, requestFile)
        addFile(type, imageFile)
    }

    private fun showVideoPicker() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_GALLERY_VIDEO && resultCode == Activity.RESULT_OK && data != null) {

            val path = getPath(data.data)
            prepareMediaToUpload("1", path ?: "")
            when (iconNumber) {
                "5" -> {
                    ivVideo1.loadUrl(path)
                    Prefs(this).video1 = path ?: ""
                }
                "6" -> {
                    ivVideo2.loadUrl(path)
                    Prefs(this).video1 = path ?: ""
                }
                "7" -> {
                    ivVideo3.loadUrl(path)
                    Prefs(this).video1 = path ?: ""
                }
            }
        }
    }

    private fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Video.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        return if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            val path = cursor.getString(columnIndex)
            cursor.close()
            path
        } else
            null
    }

    private fun prepareMediaToUpload(t: String, p: String) {
        val type = RequestBody.create(MediaType.parse("text/plain"), t)

        val file = File(p)
        val requestFile = RequestBody.create(MediaType.parse(if (t == "0") "image/*" else "video/*"), file)
//        val requestFile = RequestBody.create(MediaType.parse(contentResolver.getType(Uri.fromFile(file))), file)
        val mediaFile = MultipartBody.Part.createFormData("file", file.name, requestFile)
        addFile(type, mediaFile)
    }

    private fun addFile(type: RequestBody, file: MultipartBody.Part) {
        if (isNetworkAvailable()) {
            progressBar.visible()
            val client = ServiceGenerator.createService(NetworkClient::class.java)
            val disposable = client.addFile(type, file)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        onResponse(response)
                    }, { throwable ->
                        throwable.printStackTrace()
                        onError(throwable)
                    })
            compositeDisposable?.add(disposable)
        }
    }

    private fun onResponse(response: Any) {
        try {
            if (response is BaseResponse) {
                when (response.errorCode) {
                    1 -> logout()
                    3 -> toast(response.message)
                    200 -> {
                        if (response.obj.isJsonObject) {
                            val bean = Gson().fromJson(response.obj.asJsonObject.toString(), FileResult::class.java)
                            bean?.id.let { files += bean?.id + ", " }
                        }
                    }
                }
            }
            progressBar.gone()
//            frameLayout.gone()
        } catch (t: Throwable) {
            onError(t)
        }
    }

    private fun onError(t: Throwable) {
        t.printStackTrace()
        progressBar.gone()
//        frameLayout.gone()
        toast(R.string.message_error_connection)
    }

    fun BitMapToString(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    fun StringToBitMap(encodedString: String): Bitmap? {
        return try {
            val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
        } catch (e: Exception) {
            e.message
            null
        }

    }
}
