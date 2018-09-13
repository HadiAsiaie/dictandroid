package com.vivinte.dictandroid

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.vivinte.dictandroid.BuildConfig.DEBUG
import android.content.ContentResolver
import android.net.Uri
import android.content.res.AssetFileDescriptor
import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import android.os.Looper
import java.util.*

object MediaPlayerHolder{
    private var mediaPlayer:MediaPlayer?=null;
    fun getAppMediaPlayer():MediaPlayer{
        if (mediaPlayer==null){
            mediaPlayer=MediaPlayer()
            return mediaPlayer!!
        }
        else{
            return mediaPlayer!!
        }
    }
}
fun myAssert(value:Boolean,message:String=""){
    if (DEBUG){
        if (!value){
            Log.d("Assertion Error",message)
            throw AssertionError()
        }
    }
}
fun getFileList() : MutableList<String> {
    val list:MutableList<String> = mutableListOf()
    for( i in 40..64) {
        list.add("av$i.png")
    }
    return list
}
fun getProfilePicturesUrlList() : MutableList<String> {
    val list=getFileList()
    val urls:MutableList<String> = mutableListOf()
    for (av in list) {
        val url="/profile_pictures/base/"+av
        urls.add(url)
    }
    return urls
}
fun copy2DList(ar:MutableList<MutableList<Int>>):MutableList<MutableList<Int>>{
    return MutableList(ar.size, { index ->
        ar[index].toMutableList()
    })
}

fun resourceToUri(context: Context, resID: Int): Uri {
    return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
            context.resources.getResourcePackageName(resID) + '/'.toString() +
            context.resources.getResourceTypeName(resID) + '/'.toString() +
            context.resources.getResourceEntryName(resID))
}
fun playSound(context: Context,file:Int,completionHandler:(()->Unit)?=null){
    val mainHandler = Handler(Looper.getMainLooper())
    AsyncTask.execute {
        myAssert(Looper.myLooper() != Looper.getMainLooper())
        val dontPlaySound=false
        if (dontPlaySound) {
            if (completionHandler != null) {
                mainHandler.post {
                    completionHandler()
                }

            }
            return@execute
        }

        //val uri= resourceToUri(context,file)
        //var mediaPlayer: MediaPlayer? = MediaPlayer.create(context, file)
        //val mediaPlayer: MediaPlayer? = MediaPlayerHolder.getAppMediaPlayer()
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setOnErrorListener { mp, what, extra ->
            /*
            if(completionHandler != null) {
                completionHandler();
            }
            */
            Log.d("","error happened in media player")

            return@setOnErrorListener false

        }
        mediaPlayer.setDataSource(context, resourceToUri(context,file))


        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener(MediaPlayer.OnPreparedListener {
            player -> player.start() }
        )

        mediaPlayer.setOnCompletionListener {
            if(completionHandler != null) {
                Log.d("","calling completion handler")
                mainHandler.post {
                    myAssert(Looper.myLooper() == Looper.getMainLooper())
                    completionHandler()
                }
            }
            //mediaPlayer.reset()
            mediaPlayer.release()
            //mediaPlayer=null
        }
        //mediaPlayer!!.start() // no need to call prepare(); create() does that for you
    }




}
fun haveSound():Boolean{
    return true
}
