package com.application.hooksPlus.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.hooksPlus.R
import com.application.hooksPlus.board.*
import com.application.hooksPlus.keyboard.KeyboardAdapter
import com.application.hooksPlus.keyboard.KeyboardColorProvider
import com.application.hooksPlus.keyboard.KeyboardHandler
import com.application.hooksPlus.rooms.RoomsActivity
import com.application.hooksPlus.roots.RootsAdapter
import com.application.hooksPlus.roots.RootsHandler
import com.application.hooksPlus.support.*
import com.application.hooksPlus.support.IntentArguments.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_board.*


private const val RC_SIGN_IN = 9001

class BoardActivity : AppCompatActivity() {
    private var auth: FirebaseAuth? = null
    private var googleSignInClient: GoogleSignInClient? = null
    private var rootSize: Int = 0
    private var played: Boolean = false
    private val providersBridge = ProvidersBridge()
    private val handlersBridge = HandlersBridge()
    private val additionalHandlersBridge = AdditionalHandlersBridge()
    private val adaptersBridge = AdaptersBridge()
    private val letterConfigurator = LetterConfigurator(adaptersBridge)
    private lateinit var mDetector: GestureDetectorCompat
    private val masterArray: MutableList<CurrentLists> = ArrayList()
    private lateinit var rootsRecycler: RecyclerView
    private lateinit var boardRecycler: RecyclerView
    private lateinit var keyboardRecycler: RecyclerView
    private lateinit var dotsRecycler: RecyclerView
    private lateinit var boardConfigurator: BoardConfigurator
    private lateinit var boardBinder: BoardBinder
    private lateinit var hashTables: HashTables
    private var maxWordCounter: Int = MAX_WORD_COUNTER_ES
    private lateinit var user: String
    private var root: String? = null
    private var roomId: String? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)
        mDetector = GestureDetectorCompat(this, GestureHandler(this))
        setUpActivity()
        checkListeners()
    }

    private fun checkListeners() {
        sign_in_button.setOnClickListener {
            Log.i("test", "add button clicked")
//            signIn()
            loadGameMode()
        }
        playButton.setOnClickListener {
            additionalHandlersBridge.playHandler?.handlePlayButton()
        }
        modeButton.setOnClickListener {
            additionalHandlersBridge.modeHandler?.switchMode(button = modeButton)
            if (additionalHandlersBridge.modeHandler?.mode == Modes.NINJA) {
                resetActivity()
                additionalHandlersBridge.scoreHandler?.resetScore()
            }
        }
        commandButton.setOnClickListener {
            executeBattleCommand(null, true)
        }
        languageChangeButton.setOnClickListener {
            additionalHandlersBridge.languageHandler?.toggleLanguage()
        }
        menuButton.setOnClickListener {
            openRooms(this, masterArray)
        }
        shiftButton.setOnClickListener {
            additionalHandlersBridge.shitftHandler?.toggleButton(button = it as ImageView)
        }
    }

    private fun executeBattleCommand(root: String?, showLeftLetter: Boolean) {
        resetActivity()
        additionalHandlersBridge.scoreHandler?.resetScore()
        if (root == null) {
            additionalHandlersBridge.commandsHandler?.executeFindRoot("AL", true)
        } else {
            additionalHandlersBridge.commandsHandler?.executeFindRoot(root, showLeftLetter)
        }
        additionalHandlersBridge.modeHandler?.setBattleMode(button = modeButton)
    }

//    private fun setUpSignIn() {
//        auth = FirebaseAuth.getInstance()
//        createSignInRequest()
//    }

//    private fun createSignInRequest() {
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
//    }

    private fun signIn() {
//        val signInIntent = googleSignInClient?.signInIntent
//        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                val account = task.getResult(ApiException::class.java)!!
//                Log.d("test", "firebaseAuthWithGoogle:" + account.id)
//                firebaseAuthWithGoogle(account.idToken!!)
//            } catch (e: ApiException) {
//                Log.w("test", "Google sign in failed", e)
//            }
//        }
//    }

    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        auth?.signInWithCredential(credential)
//            ?.addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//        registerUser()
//        loadGameMode()
//                } else {
//                    Log.w("test", "signInWithCredential:failure", task.exception)
//                }
//            }
    }

    private fun registerUser() {
        user= getAndroidId(this)
        Log.d("test", "User Name: $user")
    }

    fun getAndroidId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }
    private fun loadGameMode() {
        Log.d("test", "signInWithCredential:success")
        registerUser()
        sign_in_button.visibility = View.GONE
        additionalHandlersBridge.menuHandler?.setVisibility(menuButton)
        additionalHandlersBridge.modeHandler?.mode = Modes.BATTLE
        openRooms(this, masterArray)
    }

    private fun initiate() {
        additionalHandlersBridge.scoreHandler?.resetScore()
        additionalHandlersBridge.modeHandler?.startGaming(modeButton)
        resetActivity()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    fun loadNextRoot() {
        if (!additionalHandlersBridge.playHandler?.play!!) {//|| additionalHandlersBridge.modeHandler?.mode != Modes.BATTLE) {
            rootSize.let { rootSize++ }
            if (rootSize == maxWordCounter + 1) rootSize =
                MIN_WORD_COUNTER
            configureBoard()
        }
    }

    private fun configureBoard() {
        initialiseBoard()
        initialiseAdapters()
        adaptersBridge.dotsAdapter =
            DotsAdapter(rootSize = rootSize, numberOfItems = maxWordCounter)
        initialiseHandlers()
        providersBridge.keyboardColorProvider = KeyboardColorProvider(
            rootSize = rootSize,
            masterArray = masterArray,
            adaptersBridge = adaptersBridge,
            additionalHandlersBridge = additionalHandlersBridge,
            hashTables = hashTables
        )
        runAdapters()
        handlersBridge.hooksHandler?.initialiseHook()
    }

    private fun resetActivity() {
        setUpMasterArray()
        initialiseRecyclers()
        initialiseBoard()
        initialiseAdapters()
        adaptersBridge.dotsAdapter =
            DotsAdapter(rootSize = rootSize, numberOfItems = maxWordCounter)
        initialiseHandlers()
        providersBridge.keyboardColorProvider = KeyboardColorProvider(
            rootSize = rootSize,
            masterArray = masterArray,
            adaptersBridge = adaptersBridge,
            additionalHandlersBridge = additionalHandlersBridge,
            hashTables = hashTables
        )
        runAdapters()
        handlersBridge.hooksHandler?.initialiseHook()

    }

    @SuppressLint("SetTextI18n")
    private fun initialiseNavigation() {
        rootSize = MIN_WORD_COUNTER
    }

    private fun setUpActivity() {
        initialiseRecyclers()
        initialiseNavigation()
        loadGame()
    }

    private fun loadGame() {
        setUpVariables()
        initialiseBoard()
        configureBoard()
        initiate()
        //setUpSignIn()
        handlersBridge.hooksHandler?.initialiseHook()
    }

    private fun setUpVariables() {
        hashTables = HashTables(additionalHandlersBridge)
        providersBridge.currentListProvider =
            CurrentListProvider(additionalHandlersBridge, hashTables)
        setUpMasterArray()
        setUpAdditionalHandlers()
        maxWordCounter = resolveMaxWordCounter()
    }

    private fun initialiseBoard() {
        boardConfigurator = BoardConfigurator(
            rootSize = rootSize,
            masterArray = masterArray,
            adaptersBridge = adaptersBridge
        )
        boardBinder = BoardBinder(
            handlersBridge = handlersBridge,
            additionalHandlersBridge = additionalHandlersBridge,
            masterArray = masterArray, rootSize = rootSize,
            hashTables = hashTables
        )
    }

    private fun initialiseAdapters() {
        adaptersBridge.rootsAdapter = RootsAdapter(
            rootSize = rootSize,
            masterArray = masterArray,
            handlersBridge = handlersBridge,
            additionalHandlersBridge = additionalHandlersBridge,
            hashTables = hashTables
        )
        adaptersBridge.keyboardAdapter = KeyboardAdapter(
            letterHashMap = hashTables.viewMap,
            handlersBridge = handlersBridge,
            adaptersBridge = adaptersBridge,
            additionalHandlersBridge = additionalHandlersBridge,
            letterConfigurator = letterConfigurator,
            providersBridge = providersBridge
        )

        adaptersBridge.boardAdapter = BoardAdapter(
            boardBinder = boardBinder,
            masterArray = masterArray,
            rootSize = rootSize,
            additionalHandlersBridge = additionalHandlersBridge
        )
    }

    private fun initialiseHandlers() {
        handlersBridge.boardEventHandler =
            BoardEventHandler(
                rootSize = rootSize,
                masterArray = masterArray,
                handlersBridge = handlersBridge
            )
        handlersBridge.keyboardHandler = KeyboardHandler(
            providersBridge = providersBridge,
            rootSize = rootSize,
            masterArray = masterArray,
            handlersBridge = handlersBridge,
            additionalHandlersBridge = additionalHandlersBridge
        )
        handlersBridge.hooksHandler = HooksHandler(
            boardConfigurator = boardConfigurator,
            boardRecycler = boardRecycler,
            rootSize = rootSize,
            masterArray = masterArray,
            providersBridge = providersBridge,
            handlersBridge = handlersBridge,
            adaptersBridge = adaptersBridge,
            additionalHandlersBridge = additionalHandlersBridge
        )
        handlersBridge.rootsHandler = RootsHandler(
            handlersBridge = handlersBridge,
            rootsRecycler = rootsRecycler,
            rootSize = rootSize,
            masterArray = masterArray,
            providersBridge = providersBridge,
            additionalHandlersBridge = additionalHandlersBridge
        )
    }

    private fun setUpAdditionalHandlers() {
        setUpPlayHandler()
        setUpCommandsHandler()
        setUpModelHandler()
        setUpScoreHandler()
        setUpTimerHandler()
        setUpLanguageHandler()
        setUpMenuHandler()
        setUpShiftHandler()
    }

    private fun setUpShiftHandler() {
        additionalHandlersBridge.shitftHandler = ShiftHandler()
        additionalHandlersBridge.shitftHandler?.initialiseButton(findViewById(R.id.shiftButton))
    }

    private fun setUpPlayHandler() {
        additionalHandlersBridge.playHandler = PlayHandler(
            adaptersBridge = adaptersBridge,
            rootSize = rootSize,
            masterArray = masterArray,
            additionalHandlersBridge = additionalHandlersBridge,
            boardRecycler = boardRecycler,
            handlersBridge = handlersBridge
        )
        additionalHandlersBridge.playHandler?.initialiseButton(findViewById(R.id.playButton))
    }

    private fun setUpCommandsHandler() {
        additionalHandlersBridge.commandsHandler =
            CommandsHandler(handlersBridge, masterArray, rootSize, this)
        additionalHandlersBridge.commandsHandler?.initialiseButton(findViewById(R.id.commandButton))
    }

    private fun setUpModelHandler() {
        additionalHandlersBridge.modeHandler = ModeHandler(additionalHandlersBridge)
        additionalHandlersBridge.modeHandler?.initialiseButton(findViewById(R.id.modeButton))
    }

    private fun setUpScoreHandler() {
        additionalHandlersBridge.scoreHandler = ScoreHandler(additionalHandlersBridge)
        additionalHandlersBridge.scoreHandler?.initialiseView(
            findViewById(R.id.scoreText),
            findViewById(R.id.score_layout)
        )
    }

    private fun setUpMenuHandler() {
        additionalHandlersBridge.menuHandler = MenuHandler()
        additionalHandlersBridge.menuHandler?.initialiseButton(findViewById(R.id.menuButton))
    }

    private fun setUpLanguageHandler() {
        additionalHandlersBridge.languageHandler =
            LanguageHandler(masterArray, rootSize, additionalHandlersBridge, this)
        additionalHandlersBridge.languageHandler?.initialiseView(findViewById(R.id.languageChangeButton))
    }

    private fun setUpTimerHandler() {
        additionalHandlersBridge.timerHandler =
            TimerHandler(additionalHandlersBridge, handlersBridge)
        additionalHandlersBridge.timerHandler?.initialiseView(
            findViewById(R.id.timerText),
            findViewById(R.id.timer_layout)
        )
    }

    private fun runAdapters() {
        runRootsRecycler()
        runBoardRecycler()
        runDotsRecycler()
        runKeyboardRecycler()
        updateBoardRecycler()
    }

    private fun updateBoardRecycler() {
        boardConfigurator.setBoardList()
        adaptersBridge.boardAdapter?.notifyDataSetChanged()
        boardRecycler.smoothScrollToPosition(masterArray[rootSize - 1].pointersList.rootCount)
    }

    private fun runKeyboardRecycler() {
        keyboardRecycler.apply {
            layoutManager = GridLayoutManager(
                this@BoardActivity, 7
            )
            adapter = adaptersBridge.keyboardAdapter!!
        }
    }

    private fun runDotsRecycler() {
        dotsRecycler.apply {
            layoutManager = LinearLayoutManager(
                this@BoardActivity,
                RecyclerView.HORIZONTAL,
                false
            )
            adapter = adaptersBridge.dotsAdapter!!
        }
    }

    private fun runBoardRecycler() {
        boardRecycler.apply {
            layoutManager = LinearLayoutManager(
                this@BoardActivity,
                RecyclerView.VERTICAL,
                true
            ).apply {
                stackFromEnd = true
            }
            adapter = adaptersBridge.boardAdapter!!
        }
    }

    private fun runRootsRecycler() {
        rootsRecycler.apply {
            layoutManager = LinearLayoutManager(
                this@BoardActivity,
                RecyclerView.VERTICAL,
                false
            )
            adapter = adaptersBridge.rootsAdapter!!
        }
    }

    private fun initialiseRecyclers() {
        rootsRecycler = rootsRecyclerView
        boardRecycler = boardRecyclerView
        keyboardRecycler = keyboardRecyclerView
        dotsRecycler = dotsdRecyclerView
    }

    fun moveToRootList(root: String) {
        rootSize = root.length
        configureBoard()
        additionalHandlersBridge.commandsHandler?.executeUpdateRootCommand(root)
    }

    fun loadTables() {
        maxWordCounter = resolveMaxWordCounter()
        hashTables = HashTables(additionalHandlersBridge)
        providersBridge.currentListProvider =
            CurrentListProvider(additionalHandlersBridge, hashTables)
        setUpMasterArray()
        initialiseRecyclers()
        initialiseBoard()
        initialiseAdapters()
        adaptersBridge.dotsAdapter =
            DotsAdapter(rootSize = rootSize, numberOfItems = maxWordCounter)
        initialiseHandlers()
        providersBridge.keyboardColorProvider = KeyboardColorProvider(
            rootSize = rootSize,
            masterArray = masterArray,
            adaptersBridge = adaptersBridge,
            additionalHandlersBridge = additionalHandlersBridge,
            hashTables = hashTables
        )
        runAdapters()
        handlersBridge.hooksHandler?.initialiseHook()
        resetActivity()
    }

    private fun setUpMasterArray() {
        masterArray.clear()
        masterArray.addAll(providersBridge.currentListProvider!!.getBlankArrayCurrentList())
    }

    private fun resolveMaxWordCounter() =
        if (additionalHandlersBridge.languageHandler?.language == Languages.ES) MAX_WORD_COUNTER_ES
        else MAX_WORD_COUNTER

    private fun openRooms(context: Context, masterArray: MutableList<CurrentLists>) {
        if (!(additionalHandlersBridge.modeHandler?.mode == Modes.BATTLE && additionalHandlersBridge.playHandler?.play == true)) {
            val intent = additionalHandlersBridge.scoreHandler?.let {
                Intent(context, RoomsActivity::class.java)
                    .putExtra("roomId", roomId)
                    .putExtra("userID", user)
                    .putParcelableArrayListExtra("rootList", getParcelableRootList(masterArray))
                    .putExtra("playedTime", additionalHandlersBridge.timerHandler?.playingTime)
                    .putExtra("score", it.score)
                    .putExtra(
                        "language",
                        additionalHandlersBridge.languageHandler?.language?.languageID
                    )
                    .putExtra("played", played)
            }
            if (played) {
                intent?.putExtra(
                    "keyboardColorListWrapper",
                    KeyboardColorListWrapper(
                        masterArray[root?.length!! - 1].rootColorTableLeft,
                        masterArray[root?.length!! - 1].rootColorTableRight
                    )
                )
            }
            startForResult.launch(intent)
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                roomId = result.data?.getStringExtra("roomId")
                root = result.data?.getStringExtra("root")
                val showLeftLetter = result.data?.getBooleanExtra("showLeftLetter", true)
                additionalHandlersBridge.playHandler?.playable = true
                invokeBattle(root, showLeftLetter)
                played = true
            }
        }

    private fun invokeBattle(root: String?, showLeftLetter: Boolean?) {
        if (root != null && showLeftLetter != null) {
            additionalHandlersBridge.playHandler?.setSolidStop()
            rootsRecycler.visibility = View.GONE
            executeBattleCommand(root, showLeftLetter)
            additionalHandlersBridge.scoreHandler?.clearVisibility()
            additionalHandlersBridge.timerHandler?.setUp()
            additionalHandlersBridge.timerHandler?.timer?.start()
            dotsRecycler.visibility = View.GONE
            additionalHandlersBridge.shitftHandler?.clearVisibility()
        }
    }

    private fun getParcelableRootList(masterArray: MutableList<CurrentLists>) =
        ArrayList(masterArray.map { currentLists -> ParcelableObject(currentLists.rootList) })

    companion object {
        const val MIN_WORD_COUNTER = 1
        const val MAX_WORD_COUNTER_ES = 4
        const val MAX_WORD_COUNTER = 4
    }
}

