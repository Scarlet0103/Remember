package scarlet.believe.remember.home

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import scarlet.believe.remember.R
import scarlet.believe.remember.db.Label
import scarlet.believe.remember.home.archieve.ArchieveFragment
import scarlet.believe.remember.home.labels.LabelFragment
import scarlet.believe.remember.home.secured.SecuredFragment
import scarlet.believe.remember.splash.SplashActivity
import scarlet.believe.remember.utils.Constants.Companion.USER
import scarlet.believe.remember.utils.NavigationDrawerInterface
import scarlet.believe.remember.utils.SelectMenuItemNav
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.util.*
import javax.crypto.KeyGenerator


class HomeActivity : AppCompatActivity(),FirebaseAuth.AuthStateListener,NavigationView.OnNavigationItemSelectedListener,NavigationDrawerInterface,SelectMenuItemNav{

    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var menu: Menu
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var labelNames : MutableList<String>
    private lateinit var labelList: MutableList<Label>
    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navigationView : NavigationView
    private lateinit var handler : Handler

    private lateinit var settingsMenu : MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val user = getUserFromIntent()
        initGoogleSignInClient()
        initViewModel()
        initView()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container_frag,HomeFragment())
            .commitNow()

    }

    private fun getUserFromIntent() = intent.getSerializableExtra(USER)

    private fun initGoogleSignInClient(){
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)
    }

    private fun initView(){
        labelList = mutableListOf()
        labelNames = mutableListOf()
        drawerLayout = findViewById(R.id.main_drawerlayout)
        navigationView = findViewById(R.id.main_navigationview)
        navigationView.setCheckedItem(R.id.notes_menu)
        handler = Handler()
        setUpNavDrawer()
    }

    private fun initViewModel(){
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
    }


    private fun setUpNavDrawer(){
        addItemsAtRunTime()
        navigationView.bringToFront()
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun lockDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun unlockDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun opencloseDrawer() {
        if(drawerLayout.isDrawerVisible(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START)
        else drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun selectMenuItem(id: Int) {
        if(id==95)
            navigationView.setCheckedItem(settingsMenu)
        else
            navigationView.setCheckedItem(id)
    }

    private fun addItemsAtRunTime(){

        menu = navigationView.menu
        homeViewModel.getAllLabels()?.observe(this, Observer {
            labelList.clear()
            menu.removeGroup(R.id.menu_labels)
            menu.removeGroup(95)
            labelList = it.toMutableList()
            labelList.add(0,Label(labelName = "personal"))
            labelList.add(1,Label(labelName = "Work"))
            var size = labelList.size
            labelNames.clear()
            if(size!=0){
                for(i in 0 until size){
                    labelNames.add(labelList[i].labelName!!)
                    menu.add(R.id.menu_labels,i,i,labelNames[i])
                        .setIcon(R.drawable.ic_label_24)
                        .isCheckable = true
                }
                settingsMenu = menu.add(95,size,size,"Settings")
                settingsMenu.setIcon(R.drawable.ic_settings_24)
                settingsMenu.isCheckable = true
            }
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val name = item.title
        navigationView.setCheckedItem(item)
        when(val id = item.itemId){

            R.id.create_label -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                handler.postDelayed({
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_frag,LabelFragment())
                        .commitNow()
                },500)
            }

            R.id.notes_menu -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                handler.postDelayed({
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_frag,HomeFragment())
                        .commit()
                },500)

            }

            R.id.sec_notes_menu -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                handler.postDelayed({
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_frag,SecuredFragment())
                        .commit()
                },500)
            }

            R.id.archieve_menu -> {
                drawerLayout.closeDrawer(GravityCompat.START)
                handler.postDelayed({
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container_frag,ArchieveFragment())
                        .commitNow()
                },500)
            }
//            R.id.personal_menu ->{
//                drawerLayout.closeDrawer(GravityCompat.START)
//                handler.postDelayed({
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.container_frag,LabelNotesFragment("personal"))
//                        .commitNow()
//                },500)
//            }
//            R.id.work_menu ->{
//                drawerLayout.closeDrawer(GravityCompat.START)
//                handler.postDelayed({
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.container_frag,LabelNotesFragment("work"))
//                        .commitNow()
//                },500)
//            }

        }

        if(labelNames.contains(name)){
            drawerLayout.closeDrawer(GravityCompat.START)
            handler.postDelayed({
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container_frag,LabelNotesFragment(name.toString()))
                    .commitNow()
            },500)
        }

        if(name=="Settings"){
            drawerLayout.closeDrawer(GravityCompat.START)
            handler.postDelayed({
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container_frag,SettingsFragment())
                    .commitNow()
            },500)
        }

        return true
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        val firebaseuser = firebaseAuth.currentUser
        if(firebaseuser==null){
            val intent = Intent(this,SplashActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(this)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(this)
    }


}
