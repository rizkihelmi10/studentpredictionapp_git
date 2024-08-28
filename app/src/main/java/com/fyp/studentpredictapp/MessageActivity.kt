package com.fyp.studentpredictapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MessageActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.inflateMenu(R.menu.menu_message)
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    showResolveButton()
                    true
                }

                else -> false
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        messagesAdapter = MessagesAdapter()
        recyclerView.adapter = messagesAdapter

        val dividerItemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)
        fetchMessages()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_message, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchMessages() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val messageCollection = firestore.collection("messages")
            messageCollection.whereEqualTo("userId", userId).get()
                .addOnSuccessListener { result: QuerySnapshot ->
                    val messages = result.map { document ->
                        document.toObject(Message::class.java)
                    }
                    messagesAdapter.submitList(messages)
                }
                .addOnFailureListener { exception ->
                    exception.printStackTrace()
                }
        }
    }

    private fun showResolveButton() {
        val resolveButton = Button(this).apply {
            text = "Resolve"
            setOnClickListener {
                resolveSelectedMessages()
            }
        }

        toolbar.addView(resolveButton)
    }

    private fun resolveSelectedMessages() {
        val selectedMessages = messagesAdapter.getSelectedMessages()
        if (selectedMessages.isNotEmpty()) {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val batch = firestore.batch()
                for (message in selectedMessages) {
                    val messageRef = firestore.collection("messages")
                        .whereEqualTo("userId", userId)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            for (document in querySnapshot.documents) {
                                batch.delete(document.reference)
                            }
                        }
                        .addOnFailureListener { exception ->
                            exception.printStackTrace()
                        }
                }

                batch.commit()
                    .addOnSuccessListener {
                        messagesAdapter.removeMessages(selectedMessages)
                    }
                    .addOnFailureListener { exception ->
                        exception.printStackTrace()
                    }
            }
        }
    }

    data class Message(
        val courseCode: String = "",
        val message: String = "",
        val id: String = ""
    ) {
        constructor() : this("", "", "")
    }

    class MessagesAdapter : RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {
        private val messages = mutableListOf<Message>()
        private val selectedMessages = mutableListOf<Message>()

        fun submitList(newMessages: List<Message>) {
            messages.clear()
            messages.addAll(newMessages)
            notifyDataSetChanged()
        }

        fun getSelectedMessages(): List<Message> {
            return selectedMessages
        }

        fun removeMessages(messagesToRemove: List<Message>) {
            messages.removeAll(messagesToRemove)
            selectedMessages.removeAll(messagesToRemove)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
            return MessageViewHolder(view)
        }

        override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
            holder.bind(messages[position])
        }

        override fun getItemCount() = messages.size

        inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val courseCodeTextView: TextView =
                itemView.findViewById(R.id.courseCodeTextView)
            private val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)

            init {
                itemView.setOnClickListener {
                    val message = messages[adapterPosition]
                    if (selectedMessages.contains(message)) {
                        selectedMessages.remove(message)
                    } else {
                        selectedMessages.add(message)
                    }
                    notifyItemChanged(adapterPosition)
                }
            }

            fun bind(message: Message) {
                courseCodeTextView.text = "Message from ${message.courseCode} lecturer"
                messageTextView.text = message.message
                courseCodeTextView.setTextColor(Color.GREEN)
                if (message.message.contains("Need Attention", true)) {
                    messageTextView.setTextColor(Color.RED)
                }

                itemView.setBackgroundColor(
                    if (selectedMessages.contains(message)) {
                        Color.LTGRAY
                    } else {
                        Color.TRANSPARENT
                    }
                )
            }
        }

    }
}
