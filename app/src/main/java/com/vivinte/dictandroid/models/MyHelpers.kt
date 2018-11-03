package com.vivinte.dictandroid.models

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.vivinte.dictandroid.BuildConfig.DEBUG
import android.content.ContentResolver
import android.net.Uri
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import android.R.attr.data
import android.view.textclassifier.TextLinks
import com.android.volley.NetworkResponse
import java.io.File
import android.net.Proxy.getHost
import java.net.URI
import java.net.URL


internal class InputStreamVolleyRequest(method: Int, mUrl: String, private val mListener: Response.Listener<ByteArray>,
                                        errorListener: Response.ErrorListener) : Request<ByteArray>(method, mUrl, errorListener){
    override fun deliverResponse(response: ByteArray?) {

        mListener.onResponse(response);
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<ByteArray> {


        //Initialise local responseHeaders map with response headers received
        //responseHeaders = response.headers

        //Pass the response data here
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response))
    }

    override fun getHeaders(): MutableMap<String, String> {
        val res=HashMap<String,String>()
        res["referer"]="https://translate.google.com"
        res["user-agent"]="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.36"
        res["accept-encoding"]="gzip, deflate, sdch"
        res["accept"]="*/*"
        return res
        //return super.getHeaders()
    }
}
object MediaPlayerHolder{
    private var mediaPlayer:MediaPlayer?=null;
    fun getAppMediaPlayer():MediaPlayer{
        if (mediaPlayer ==null){
            mediaPlayer =MediaPlayer()
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
    val list= getFileList()
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
fun URLForGoogleSound():String{
    return "https://translate.google.com/translate_tts?ie=UTF-8&q=%@&tl=%@&textlen=%d&client=tw-ob"
}
fun getSoundFileName(text:String,accent:String):String{
    val s=accent+"_"+text;
    var hash=0L;
    for (ch in s){
        hash = (hash * 3217 + ch.toInt()) % (1.shl(20))
    }
    return hash.toString()
}
fun playText(context: Context,text:String,from:String,completionHandler:(()->Unit)?=null){
    val urlFormat= URLForGoogleSound().replace("%@","%s")
    val accent=from+"-"+"us"//todo get accent here
    val soundUrl=urlFormat.format(text,accent,text.length)
    val url = URL(soundUrl)
    val uri = URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef())
    val scapeUrl= uri.toURL()



    Log.d("MyHelpers","url: $scapeUrl")
    val request=InputStreamVolleyRequest(Request.Method.GET,soundUrl,Response.Listener {
        Log.d("MyHelpers","Got response");
        val fileName= getSoundFileName(text,accent)
        val file=context.getFileStreamPath(fileName)
        Log.d("MyHelpers",file.absolutePath)
        val realFile=File(file.absolutePath)
        if (realFile.exists()){
            Log.d("MyHelpers","file exists")
        }
        else{
            Log.d("MyHelpers","file does not exist")
            val outputStream=context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(it)
            outputStream.close()
            Log.d("MyHelpers","wrote file to disk")
        }



        playSound(context,fileName,completionHandler)
    }, Response.ErrorListener {
        Log.d("MyHelpers","Got errors");
        if (completionHandler != null){
            completionHandler();
        }

    });

    MyApplication.getQueue().add(request)


}
fun playSound(context: Context,fileName:String,completionHandler:(()->Unit)?=null){
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
        mediaPlayer.setDataSource(context.getFileStreamPath(fileName).path)
       // mediaPlayer.setDataSource(context, resourceToUri(context, file))


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
