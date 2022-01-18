package com.chancehee.donuttimer

import android.graphics.Color
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {

    private val constraintLayout : ConstraintLayout by lazy {
        findViewById(R.id.constraintLayout)
    }

    private val donut_imageView : ImageView by lazy {
        findViewById(R.id.donut_imageView)
    }

    private val remainMinutesTextView : TextView by lazy{
        findViewById(R.id.remainMinutesTextView)
    }

    private val remainSecondsTextView : TextView by lazy{
        findViewById(R.id.remainSecondsTextView)
    }

    private val seekBar : SeekBar by lazy{
        findViewById(R.id.seekBar)
    }

    private var currentCountDownTimer : CountDownTimer? = null

    private var tickingSoundId : Int? = null
    private var finishSoundId : Int? = null

    private val soundPool = SoundPool.Builder().build()

    private var isCountDown : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSeekBar()
        initSounds()
        clickDonut()
    }

    private fun clickDonut() {
        donut_imageView.setOnClickListener {
            if (isCountDown) {
                isCountDown = false
                currentCountDownTimer?.let {
                    Toast.makeText(this,"일시정지",Toast.LENGTH_SHORT).show()
                    stopCountDown()
                }
            } else{
                isCountDown = true
                Toast.makeText(this,"재개",Toast.LENGTH_SHORT).show()
                DonutStart()
            }
        }
    }

    private fun DonutStart() {
        // 12분 50초 일 때 = 770000초
        var a = remainMinutesTextView.text.toString().toLong()
        var b = remainSecondsTextView.text.toString().toLong()
        var remainSeconds =  (a * 1000 * 60) + (b * 1000)
        currentCountDownTimer = createCountDownTimer(remainSeconds)
        currentCountDownTimer?.start()
        tickingSoundId?.let { soundPool.play(it,1F,1F,0,-1,1F) }
        // 사운드 시작
    }

    private fun initSeekBar() {
        seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener{
                override fun onProgressChanged(s: SeekBar?, p: Int, u: Boolean) {
                    if (u){
                        updateRemainTime(p * 1000 * 60L)
                    }
                }

                override fun onStartTrackingTouch(s: SeekBar?) {
                    stopCountDown()
                }

                override fun onStopTrackingTouch(s: SeekBar?) {
                    s ?: return

                    if (s.progress == 0){
                        stopCountDown()
                    }else{
                        startCountDown()
                    }
                }
            }
        )
    }

    // 현재 프로그레스에 따라서 텍스트뷰 업데이트 해주는 함수.
    private fun updateRemainTime(remainMillis : Long) {
        val remainSeconds = remainMillis / 1000
        //분
        remainMinutesTextView.text = "%02d".format(remainSeconds / 60)
        //초
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun startCountDown() {
        currentCountDownTimer = createCountDownTimer(seekBar.progress * 1000 *60L)
        currentCountDownTimer?.start()
        tickingSoundId?.let { soundPool.play(it,1F,1F,0,-1,1F) }

    }

    private fun stopCountDown() {
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
    }


    private fun createCountDownTimer(initMillis : Long) : CountDownTimer{
        return object : CountDownTimer(initMillis, 1000L){
            override fun onTick(time: Long) {
                updateRemainTime(time)
                updateSeekBar(time)
            }
            override fun onFinish() {
                completeCountDown()
            }
        }
    }


    private fun updateSeekBar(remainMillis: Long) {
        seekBar.progress = (remainMillis / 1000 / 60L).toInt()
    }

    private fun completeCountDown() {
        updateRemainTime(0)
        updateSeekBar(0)
        soundPool.autoPause()
        finishSoundId?.let { soundPool.play(it,1F,1F,0,0,1F) }
    }

    // id를 반환 해준다.
    private fun initSounds() {
        tickingSoundId = soundPool.load(this, R.raw.ticktick, 1)
        finishSoundId = soundPool.load(this, R.raw.finish,1)
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

}