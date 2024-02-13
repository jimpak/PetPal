package org.bitc.petpalapp.recyclerviewAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.bitc.petpalapp.R
import org.bitc.petpalapp.model.Event

class EventsAdapter : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    private val eventsList = ArrayList<Event>()

    fun updateData(newEventsList: List<Event>) {
        eventsList.clear()
        eventsList.addAll(newEventsList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventsList[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int {
        return eventsList.size
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventNameTextView: TextView = itemView.findViewById(R.id.eventNameTextView)
        private val eventMemoTextView: TextView = itemView.findViewById(R.id.eventMemoTextView)

        fun bind(event: Event) {
            eventNameTextView.text = "신청자 : ${event.eventName}"
            eventMemoTextView.text = "예약상황 : ${event.eventMemo}"
        }
    }
}