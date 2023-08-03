package com.example.test.ui.help


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.test.MessageRecAdapter
import com.example.test.R
import com.example.test.models.Message
import org.json.JSONException
import org.json.JSONObject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HelpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HelpFragment : Fragment() {
    // creating variables for our
    // widgets in xml file.
    private var chatsRV: RecyclerView? = null
    private var sendMsgIB: ImageButton? = null
    private var userMsgEdt: EditText? = null
    private val USER_KEY = "user"
    private val BOT_KEY = "bot"
    // creating a variable for
    // our volley request queue.
    private var mRequestQueue: RequestQueue? = null

    // creating a variable for array list and adapter class.
    private var messageArrayList: ArrayList<Message>? = null
    private var messageRVAdapter: MessageRecAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_help, container, false)
        // on below line we are initializing all our views.
        // on below line we are initializing all our views.
        chatsRV = view.findViewById(R.id.idRVChats)
        sendMsgIB = view.findViewById(R.id.idIBSend)
        userMsgEdt = view.findViewById(R.id.idEdtMessage)

        // below line is to initialize our request queue.

        // below line is to initialize our request queue.
        mRequestQueue = Volley.newRequestQueue(activity)
        mRequestQueue!!.getCache().clear()

        // creating a new array list

        // creating a new array list
        messageArrayList = ArrayList()

        // adding on click listener for send message button.

        // adding on click listener for send message button.
        sendMsgIB!!.setOnClickListener(View.OnClickListener { // checking if the message entered
            // by user is empty or not.
            if (userMsgEdt!!.text.toString().isEmpty()) {
                // if the edit text is empty display a toast message.
                Toast.makeText(activity, "Please enter your message..", Toast.LENGTH_SHORT)
                    .show()
                return@OnClickListener
            }

            // calling a method to send message
            // to our bot to get response.
            sendMessage(userMsgEdt!!.text.toString())

            // below line we are setting text in our edit text as empty
            userMsgEdt!!.setText("")
        })

        // on below line we are initializing our adapter class and passing our array list to it.

        // on below line we are initializing our adapter class and passing our array list to it.
        messageRVAdapter = activity?.let { MessageRecAdapter(messageArrayList!!, it.baseContext) }

        // below line we are creating a variable for our linear layout manager.

        // below line we are creating a variable for our linear layout manager.
        val linearLayoutManager =
            LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        // below line is to set layout
        // manager to our recycler view.

        // below line is to set layout
        // manager to our recycler view.
        chatsRV!!.layoutManager = linearLayoutManager

        // below line we are setting
        // adapter to our recycler view.

        // below line we are setting
        // adapter to our recycler view.
        chatsRV!!.adapter = messageRVAdapter

        return view

    }

    private fun sendMessage(userMsg: String) {
        // below line is to pass message to our
        // array list which is entered by the user.
        messageArrayList?.add(Message(userMsg, USER_KEY))
        messageRVAdapter!!.notifyDataSetChanged()

        // url for our brain
        // make sure to add mshape for uid.
        // make sure to add your url.
        val url = "Enter you API URL here$userMsg"

        // creating a variable for our request queue.
        val queue = Volley.newRequestQueue(activity)

        // on below line we are making a json object request for a get request and passing our url .
        val jsonObjectRequest =
            JsonObjectRequest(Request.Method.GET, url, null, object : Response.Listener<JSONObject?>{
                override fun onResponse(response: JSONObject?) {
                    try {
                        // in on response method we are extracting data
                        // from json response and adding this response to our array list.
                        val botResponse = response?.getString("cnt")
                        messageArrayList?.add(Message(botResponse!!, BOT_KEY))

                        // notifying our adapter as data changed.
                        messageRVAdapter!!.notifyDataSetChanged()
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        // handling error response from bot.
                        messageArrayList?.add(Message("No response", BOT_KEY))
                        messageRVAdapter!!.notifyDataSetChanged()
                    }
                }
            }, object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    // error handling.
                    messageArrayList?.add(Message("Sorry no response found", BOT_KEY))
                    Toast.makeText(
                        activity,
                        "No response from the bot..",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        // at last adding json object
        // request to our queue.
        queue.add(jsonObjectRequest)
    }


}