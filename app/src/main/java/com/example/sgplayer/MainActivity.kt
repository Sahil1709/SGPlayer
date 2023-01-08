package com.example.sgplayer

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.MediaController
import android.widget.VideoView

class MainActivity : AppCompatActivity() {

    // Declare the views and the gesture detectors
    lateinit var videoView: VideoView
    lateinit var mediaController: MediaController
    lateinit var gestureDetector: GestureDetector
    lateinit var scaleGestureDetector: ScaleGestureDetector
    lateinit var audioManager: AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the views and audio manager
        videoView = findViewById(R.id.video_view)
        mediaController = MediaController(this)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // Set the media controller for the video view
        videoView.setMediaController(mediaController)

        // Set the video URI
        val videoUri = Uri.parse("https://www.sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4")
        videoView.setVideoURI(videoUri)

        // Initialize the gesture detectors
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                // Handle double tap on the center of the screen
                if (e.x < videoView.width / 3) {
                    // Double tap on the left side of the screen - seek backward
                    videoView.seekTo(videoView.currentPosition - 5000)
                } else if (e.x > videoView.width * 2 / 3) {
                    // Double tap on the right side of the screen - seek forward
                    videoView.seekTo(videoView.currentPosition + 5000)
                } else {
                    // Double tap on the center of the screen - play/pause
                    if (videoView.isPlaying) {
                        videoView.pause()
                    } else {
                        videoView.start()
                    }
                }
                return true
            }

            override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                // Handle scroll gestures to adjust brightness and volume
                if (e1.x < videoView.width / 3) {
                    // Scroll on the left side of the screen - adjust brightness
                    val brightness = (distanceY / videoView.height) * 255
                    val layoutParams = window.attributes
                    layoutParams.screenBrightness += brightness
                    window.attributes = layoutParams
                } else if (e1.x > videoView.width * 2 / 3) {
                    // Scroll on the right side of the screen - adjust volume
                    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                    val volume = (distanceY / videoView.height) * maxVolume
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume.toInt(), 0)
                }
                return true
            }
        })
        scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                // Handle pinch-to-zoom gestures
                val scaleFactor = detector.scaleFactor
                videoView.scaleX *= scaleFactor
                videoView.scaleY *= scaleFactor
                return true
            }
        })

        // Set the touch listener for the video view
        videoView.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            scaleGestureDetector.onTouchEvent(event)
            true
        }
    }
}
