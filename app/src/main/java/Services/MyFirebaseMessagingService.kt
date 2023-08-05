package Services

import com.example.spy.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import utils.UserUtils
import kotlin.random.Random

class MyFirebaseMessagingService: FirebaseMessagingService() {


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if(FirebaseAuth.getInstance().currentUser !=  null){
            UserUtils.updateToken(this, token)
        }

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data= message.data
        if(data !=null){
            Common.showNotification(this, Random.nextInt(),
                data[Common.NOTI_TITLE]!!,
                data[Common.NOTI_BODY]!!,
                null
            )
        }


    }












}