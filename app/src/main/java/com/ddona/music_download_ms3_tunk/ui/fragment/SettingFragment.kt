package com.ddona.music_download_ms3_tunk.ui.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ddona.music_download_ms3_tunk.BuildConfig
import com.ddona.music_download_ms3_tunk.databinding.FragmentSettingBinding
import com.ddona.music_download_ms3_tunk.ui.activity.PlayerActivity


class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater)

        binding.btnUpgrade.setOnClickListener {
            Toast.makeText(context, "Coming soon!!", Toast.LENGTH_SHORT).show()
        }

        binding.lnRatting.setOnClickListener {
            rateApp()
        }

        binding.lnFeedback.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("message/rfc822")
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("tunk@proxglobal.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback: MUSIC DOWNLOADER")
            startActivity(intent)
        }

        binding.lnPrrivacy.setOnClickListener {
            Toast.makeText(context, "Coming soon!!", Toast.LENGTH_SHORT).show()
        }

        binding.lnShare.setOnClickListener {

            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "plain/text"
            shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                "https://play.google.com/store/apps/details? =" + BuildConfig.APPLICATION_ID
            )
            startActivity(Intent.createChooser(shareIntent, "Sharing App Link!!"))


        }

        return binding.root
    }

    fun rateApp() {
        try {
            val rateIntent = rateIntentForUrl("market://details")
            startActivity(rateIntent)
        } catch (e: ActivityNotFoundException) {
            val rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details")
            startActivity(rateIntent)
        }
        BuildConfig.APPLICATION_ID
    }

    private fun rateIntentForUrl(url: String): Intent {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(java.lang.String.format("%s?id=%s", url, BuildConfig.APPLICATION_ID))
        )
        var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        flags = if (Build.VERSION.SDK_INT >= 21) {
            flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        } else {
            flags or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
        }
        intent.addFlags(flags)
        return intent
    }


}