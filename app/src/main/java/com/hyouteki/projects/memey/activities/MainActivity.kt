package com.hyouteki.projects.memey.activities

import android.content.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.hyouteki.projects.memey.R
import com.hyouteki.projects.memey.classes.MemeyFragment
import com.hyouteki.projects.memey.comms.BottomSheetComms
import com.hyouteki.projects.memey.comms.DialogMainComms
import com.hyouteki.projects.memey.comms.FragMainComms
import com.hyouteki.projects.memey.daos.MemeDao
import com.hyouteki.projects.memey.databinding.ActivityMainBinding
import com.hyouteki.projects.memey.dialogs.FullTitleBottomSheet
import com.hyouteki.projects.memey.dialogs.MemeOptionsBottomSheet
import com.hyouteki.projects.memey.dialogs.SwitchBottomSheet
import com.hyouteki.projects.memey.fragments.*
import com.hyouteki.projects.memey.interfaces.*
import com.hyouteki.projects.memey.models.Meme
import com.hyouteki.projects.memey.viewmodels.MemeyViewModel


class MainActivity : AppCompatActivity(), Communicator, FragMainComms, DialogMainComms,
    BottomSheetComms {
    private val user = FirebaseAuth.getInstance().currentUser
    private lateinit var sharedPreferences: SharedPreferences
    private val storageReference = FirebaseStorage.getInstance().reference
    private var fragmentInstance: MemeyFragment? = null
    private var currentLocalMeme: Meme? = null
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = getColor(R.color.secBackground)

        initialize()
        handleTouches()
        handleCallbacks()
    }

    private fun initialize() {
        sharedPreferences = Saver.getPreferences(this)
        when (sharedPreferences.getString(Saver.DEFAULT_TAB, Tags.MEME_TAB)) {
            Tags.MEME_TAB -> {
                fragmentInstance =
                    when (sharedPreferences.getString(
                        Saver.MEME_TAB_LAYOUT,
                        Tags.MULTIPLE_MEME_LAYOUT
                    )) {
                        Tags.MULTIPLE_MEME_LAYOUT -> MemeFragment()
                        else -> SingleMemeFragment()
                    }
            }
            Tags.GIF_TAB -> {
                fragmentInstance = GifFragment()
            }
            Tags.MEMEY_MEME_TAB -> {
                fragmentInstance = LocalMemeFragment()
            }
        }
        binding.title.text = fragmentInstance?.getTitle()
        launchFragment()
    }

    private fun handleCallbacks() {
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.package.ACTION_LOGOUT")
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("onReceive", "Logout in progress")
                startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                finish()
            }
        }, intentFilter)
    }

    override fun shareMeme(meme: Meme) {
        val get = sharedPreferences.getString("shareLink", "Image link")
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        if (get == "Image link") {
            shareIntent.putExtra(Intent.EXTRA_TEXT, meme.photoUrl)
        } else {
            shareIntent.putExtra(Intent.EXTRA_TEXT, meme.postUrl)
        }
        startActivity(Intent.createChooser(shareIntent, "Send this amazing meme using..."))
    }

    private fun launchFragment() {
        fragmentInstance?.let {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.frame, it)
                commit()
            }
        }
    }

    private fun handleTouches() {
        binding.refresh.setOnClickListener {
            when (fragmentInstance) {
                is MemeFragment, is SingleMemeFragment -> {
                    switchToMeme()
                }
            }
        }
        binding.change.setOnClickListener {
            SwitchBottomSheet().show(supportFragmentManager, "Switch#Dialog@Memey")
        }
        binding.menu.setOnClickListener {
            startActivity(Intent(this, MoreActivity::class.java))
        }
        binding.search.setOnClickListener {
            when (fragmentInstance) {
                is MemeFragment -> {
                    startActivity(Intent(this, SearchMemeActivity::class.java))
                }
                is GifFragment -> {
                    startActivity(Intent(this, SearchGifActivity::class.java))
                }
            }
        }
    }

    override fun localMemeFavoriteClickMethod(meme: Meme) {
        MemeDao().addMeme(meme)
    }

    override fun openMemeOptions(meme: Meme) {
        this.currentLocalMeme = meme
        MemeOptionsBottomSheet().show(supportFragmentManager, "MemeOptions#BottomSheet@Memey")
    }

    override fun deleteCurrentLocalMeme() {
        currentLocalMeme?.let {
            MemeyViewModel.localMemeCollection.document(it.mid).delete().addOnSuccessListener {
                Helper.makeToast(this, "Deleted successfully")
//                fragmentInstance = ProfileFragment()
                launchFragment()
            }.addOnFailureListener {
                Helper.makeToast(this, "Could not delete. Try again")
            }
        }
        currentLocalMeme?.let {
            val photoReference = storageReference.child("Meme/${it.mid}.png")
            photoReference.delete()
                .addOnSuccessListener(OnSuccessListener<Void?> { // File deleted successfully
                    Logger.debugger("onSuccess: deleted file")
                }).addOnFailureListener(OnFailureListener { // Uh-oh, an error occurred!
                    Logger.debugger("onFailure: did not delete file")
                })

        }
    }

    override fun showFullTitle(title: String) {
        Helper.makeToast(this, "hello")
        val dialog = FullTitleBottomSheet()
        val bundle = Bundle()
        bundle.putString("title", title)
        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "FullTitle#BottomSheet@Memey")
    }

    override fun showProgressBar() {
        binding.progress.visibility = View.VISIBLE
    }

    override fun dismissProgressBar() {
        binding.progress.visibility = View.GONE
    }

    override fun switchToMeme() {
        fragmentInstance =
            when (sharedPreferences.getString(Saver.MEME_TAB_LAYOUT, Tags.MULTIPLE_MEME_LAYOUT)) {
                Tags.MULTIPLE_MEME_LAYOUT -> MemeFragment()
                else -> SingleMemeFragment()
            }
        binding.title.text = "Memes"
        launchFragment()
    }

    override fun switchToGif() {
        fragmentInstance = GifFragment()
        binding.title.text = "Gifs"
        launchFragment()
    }

    override fun switchToLocalMeme() {
        fragmentInstance = LocalMemeFragment()
        binding.title.text = "MEMEY memes"
        launchFragment()
    }

}