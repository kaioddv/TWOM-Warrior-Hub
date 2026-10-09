package com.example.twomhub

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
 override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState)
  val root = LinearLayout(this).apply { orientation=LinearLayout.VERTICAL; setPadding(36,60,36,20) }
  root.addView(TextView(this).apply { text="TWOM Warrior Hub v0.4\nA janela flutuante controla apenas o estado da Hub. Não envia comandos ao jogo."; textSize=19f })
  root.addView(Button(this).apply { text="Abrir Hub flutuante"; setOnClickListener {
   if (!Settings.canDrawOverlays(this@MainActivity)) startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
   else startService(Intent(this@MainActivity, OverlayService::class.java))
  } })
  root.addView(Button(this).apply { text="Fechar Hub"; setOnClickListener { stopService(Intent(this@MainActivity, OverlayService::class.java)) } })
  setContentView(root)
 }
}
