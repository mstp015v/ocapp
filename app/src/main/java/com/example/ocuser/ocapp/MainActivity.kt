package com.example.ocuser.ocapp

import android.media.AudioAttributes
import android.media.SoundPool
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class Note(val oto : String , val delay : Long )

class MainActivity : AppCompatActivity() {
    private lateinit var soundPool : SoundPool
    val scoreList = mutableListOf<Note>()        //楽譜
    val scaleMap = mutableMapOf<String,Int>()    //音階と音源の対応
    lateinit var btn_do1 : Button                //ドの鍵盤
    lateinit var btn_re1 : Button                //レの鍵盤
    lateinit var btn_mi1 : Button                //ミの鍵盤
    lateinit var btn_fa1 : Button                //ファの鍵盤
    lateinit var btn_so1 : Button                //ソの鍵盤
    lateinit var btn_ra1 : Button                //ラの鍵盤
    lateinit var btn_si1 : Button                //シの鍵盤
    lateinit var btn_do2 : Button               //高いドの鍵盤
    lateinit var btn_play : Button               //再生のボタン
    lateinit var btn_clear : Button             //クリアのボタン
    lateinit var lay_score : ConstraintLayout   //楽譜
    lateinit var const_set : ConstraintSet
    var beforeId : Int = 0                      //ひとつ前のid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //初期化
        init()
    }

    //ドの鍵盤
    fun btnDo1Click(){ sound("ド1") }
    //レの鍵盤
    fun btnRe1Click(){}
    //ミの鍵盤
    fun btnMi1Click(){}
    //ファの鍵盤
    fun btnFa1Click(){}
    //ソの鍵盤
    fun btnSo1Click(){}
    //ラの鍵盤
    fun btnRa1Click(){}
    //シの鍵盤
    fun btnSi1Click(){}
    //ドの鍵盤
    fun btnDo2Click(){}

    //再生ボタンをクリックした時の処理
    fun makeScore(){
        //oto=音階,delay=長さ
        setScore( "ド1" , 800 )
        setScore( "レ1" , 400 )
    }

    //scoreListを再生する非同期プログラム
    fun scorePlay(){
        GlobalScope.launch( Dispatchers.Main ){
            btn_play.isEnabled = false
            async( Dispatchers.Default ){
                scoreList.forEach{
                    if( it.oto != "休") {
                        sound(it.oto)
                    }
                    Thread.sleep( it.delay )
                }
                return@async
            }.await()
            btn_play.isEnabled = true
        }
    }

    //初期化処理
    fun init(){
        //音源の準備
        val audioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        soundPool = SoundPool.Builder().setAudioAttributes(audioAttributes).setMaxStreams(2).build()
        scaleMap.put("ド1",  soundPool.load(this,R.raw.s_do,1))
        scaleMap.put("レ1",  soundPool.load(this,R.raw.s_re,1))
        scaleMap.put("ミ1",  soundPool.load(this,R.raw.s_mi,1))
        scaleMap.put("ファ1",soundPool.load(this,R.raw.s_fa,1))
        scaleMap.put("ソ1",  soundPool.load(this,R.raw.s_so,1))
        scaleMap.put("ラ1",  soundPool.load(this,R.raw.s_ra,1))
        scaleMap.put("シ1",  soundPool.load(this,R.raw.s_si,1))
        scaleMap.put("ド2",  soundPool.load(this,R.raw.s_do2,1))

        //鍵盤にイベントを割り当てる
        btn_do1 = findViewById(R.id.btn_do1)
        btn_do1.setOnClickListener{
            btnDo1Click()
                    }
        btn_re1 = findViewById(R.id.btn_re1)
        btn_re1.setOnClickListener{
            btnRe1Click()
                    }
        btn_mi1=findViewById(R.id.btn_mi1)
        btn_mi1.setOnClickListener{
            btnMi1Click()
                   }
        btn_fa1=findViewById(R.id.btn_fa1)
        btn_fa1.setOnClickListener {
            btnFa1Click()
        }
        btn_so1=findViewById(R.id.btn_so1)
        btn_so1.setOnClickListener{
            btnSo1Click()
                    }
        btn_ra1=findViewById(R.id.btn_ra1)
        btn_ra1.setOnClickListener {
            btnRa1Click()
            addLayScore( createImageView(R.drawable.onpu02), 30)
        }
        btn_si1=findViewById(R.id.btn_si1)
        btn_si1.setOnClickListener{
            btnSi1Click()
        }
        btn_do2= findViewById( R.id.btn_do2)
        btn_do2.setOnClickListener{
            btnDo2Click()
        }
        //楽譜を再生ボタン
        btn_play = findViewById(R.id.btn_play)
        btn_play.setOnClickListener{
            makeScore()
            scorePlay()
        }
        //楽譜のレイアウト
        lay_score = findViewById(R.id.lay_score)
        //コンストレイントセット
        const_set = ConstraintSet()
    }

    //音を設定する
    fun setScore( oto : String , delay : Long){
        scoreList.add( Note( oto , delay ))
    }

    //音を出す
    fun sound( oto :String ){
        soundPool.play( scaleMap[oto]!!,1.0f,1.0f,0,0,1.0f)
    }

    //画像を作る
    fun createImageView( id : Int ): ImageView {
        val img = ImageView( this )
        img.setImageResource( id )
        img.id = View.generateViewId()
        return img
    }

    //音符をlay_scoreに追加する
    fun addLayScore( img : ImageView , margin : Int){
        const_set.clone( lay_score )
        lay_score.addView( img )
        Log.d("image:" , img.id.toString() )
        const_set.constrainHeight( img.id, 80 )
        const_set.constrainWidth( img.id, 50 )
        if( beforeId==0) {
            const_set.connect(img.id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
            const_set.connect(img.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP , margin)
        }else{
            const_set.connect(img.id, ConstraintSet.LEFT, beforeId, ConstraintSet.RIGHT)
            const_set.connect(img.id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP , margin)
        }
        beforeId = img.id
        const_set.applyTo( lay_score )
    }
}