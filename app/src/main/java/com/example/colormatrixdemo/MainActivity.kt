package com.example.colormatrixdemo

import android.graphics.*
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

const val MID_VALUE = 128

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    //新圖片
    private lateinit var newImage: ImageView

    //舊圖片
    private lateinit var oldImage: ImageView

    //色調
    private val rotateMatrix = ColorMatrix()

    //飽和度
    private val saturationMatrix = ColorMatrix()

    //亮度
    private val scaleMatrix = ColorMatrix()

    //三合一
    private val colorMatrix = ColorMatrix()

    //原圖
    private lateinit var bitmap: Bitmap

    private lateinit var seekBarRotate: SeekBar
    private lateinit var seekBarSaturation: SeekBar
    private lateinit var seekBarScale: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //獲取舊圖片顯示在左邊
        oldImage = findViewById(R.id.oldImage)
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.girl)
        oldImage.setImageBitmap(bitmap)

        //設定最高255 預設128
        initSeekBar()
    }

    private fun initSeekBar() {
        seekBarRotate = findViewById(R.id.seekBar_rotate)
        seekBarRotate.max = 255
        seekBarRotate.progress = MID_VALUE

        seekBarSaturation = findViewById(R.id.seekBar_saturation)
        seekBarSaturation.max = 255
        seekBarSaturation.progress = MID_VALUE

        seekBarScale = findViewById(R.id.seekBar_scale)
        seekBarScale.max = 255
        seekBarScale.progress = MID_VALUE

        seekBarRotate.setOnSeekBarChangeListener(this)
        seekBarSaturation.setOnSeekBarChangeListener(this)
        seekBarScale.setOnSeekBarChangeListener(this)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when (seekBar.id) {
            R.id.seekBar_rotate -> {
                val mHue = (progress - MID_VALUE).toFloat() / MID_VALUE * 180
                rotateMatrix.reset()
                rotateMatrix.setRotate(0, mHue)
                rotateMatrix.setRotate(1, mHue)
                rotateMatrix.setRotate(2, mHue)
            }
            R.id.seekBar_saturation -> {
                val mSaturation = progress / MID_VALUE.toFloat()
                saturationMatrix.reset()
                saturationMatrix.setSaturation(mSaturation)
            }
            R.id.seekBar_scale -> {
                val mBrightness = progress / MID_VALUE.toFloat()
                scaleMatrix.reset()
                scaleMatrix.setScale(mBrightness, mBrightness, mBrightness, 1f)
            }
        }

        colorMatrix.reset()
        colorMatrix.postConcat(rotateMatrix)
        colorMatrix.postConcat(saturationMatrix)
        colorMatrix.postConcat(scaleMatrix)
        oldImage.colorFilter = ColorMatrixColorFilter(colorMatrix)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    //儲存新圖
    fun saveBitmap(view: View) {
        //新圖放到畫布上
        val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)

        //設定畫筆參數
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)

        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        val file = File(externalCacheDir, "matrixGirl.jpg")
        val ops = FileOutputStream(file)
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ops)

        newImage = findViewById(R.id.newImage)
        newImage.setImageBitmap(newBitmap)
    }

    //還原預設值
    fun returnBitmap(view: View) {
        seekBarRotate.progress = MID_VALUE
        seekBarSaturation.progress = MID_VALUE
        seekBarScale.progress = MID_VALUE
    }
}


