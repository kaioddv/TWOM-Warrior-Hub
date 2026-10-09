package com.example.twomhub

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView

class OverlayService : Service() {
 private lateinit var wm: WindowManager
 private var overlay: View? = null
 private var running = false
 private var expanded = true
 private lateinit var params: WindowManager.LayoutParams
 override fun onBind(intent: Intent?): IBinder? = null
 override fun onCreate() { super.onCreate(); wm = getSystemService(WINDOW_SERVICE) as WindowManager; render() }
 private fun render() {
  overlay?.let { wm.removeView(it) }; overlay = null
  val root = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(18,14,18,14)
   background=GradientDrawable().apply { setColor(Color.rgb(19,29,47)); cornerRadius=22f } }
  fun button(label:String, action:()->Unit) = Button(this).apply { text=label; setOnClickListener { action() } }
  if (!expanded) root.addView(button("⚔ Hub") { expanded=true; render() })
  else {
   root.addView(TextView(this).apply { text="TWOM Warrior Hub"; textSize=18f; setTextColor(Color.WHITE) })
   val status=TextView(this).apply { text=if(running) "Estado: ATIVO (simulação)" else "Estado: PARADO"; setTextColor(Color.WHITE) }
   root.addView(status)
   root.addView(button(if(running) "Parar" else "Iniciar") { running=!running; render() })
   val prefs=getSharedPreferences("hub", MODE_PRIVATE)
   listOf("Auto Attack", "Auto Loot", "Auto Move").forEach { key ->
    root.addView(CheckBox(this).apply { text=key; setTextColor(Color.WHITE); isChecked=prefs.getBoolean(key,true)
     setOnCheckedChangeListener { _, checked -> prefs.edit().putBoolean(key,checked).apply() } })
   }
   root.addView(button("MINIMIZAR") { expanded=false; render() })
   root.addView(button("PARAGEM DE EMERGÊNCIA") { running=false; render() })
   root.addView(button("Fechar janela") { stopSelf() })
  }
  params=WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,
   WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT).apply {
   gravity=Gravity.TOP or Gravity.START; x=35; y=160
  }
  root.setOnTouchListener(object:View.OnTouchListener { var sx=0; var sy=0; var tx=0f; var ty=0f
   override fun onTouch(v:View, event:MotionEvent):Boolean { when(event.actionMasked) {
    MotionEvent.ACTION_DOWN -> { sx=params.x; sy=params.y; tx=event.rawX; ty=event.rawY; return true }
    MotionEvent.ACTION_MOVE -> { params.x=sx+(event.rawX-tx).toInt(); params.y=sy+(event.rawY-ty).toInt(); wm.updateViewLayout(root,params); return true }
   }; return false }
  })
  overlay=root; wm.addView(root,params)
 }
 override fun onDestroy() { overlay?.let { wm.removeView(it) }; overlay=null; super.onDestroy() }
}
