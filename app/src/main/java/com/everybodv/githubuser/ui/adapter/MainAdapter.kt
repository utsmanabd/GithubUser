package com.everybodv.githubuser.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everybodv.githubuser.data.local.entity.UsersEntity
import com.everybodv.githubuser.data.remote.response.ItemsItem
import com.everybodv.githubuser.databinding.ItemRowUserBinding
import com.everybodv.githubuser.ui.detail.DetailUserActivity

class MainAdapter(private val listUser: List<ItemsItem>) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemRowUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UsersEntity) {
            binding.tvName.text = user.login
            Glide.with(itemView.context)
                .load(user.avatarUrl)
                .circleCrop()
                .into(binding.detailImage)
            itemView.setOnClickListener {
                val toDetailUser = Intent(itemView.context, DetailUserActivity::class.java)
                toDetailUser.putExtra(DetailUserActivity.EXTRA_USER, user)
                toDetailUser.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                itemView.context.startActivity(toDetailUser)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listUser.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.ivFavorite.visibility = View.GONE
        val user = listUser[position]
        val usersEntity = UsersEntity(user.login, user.avatarUrl, user.isFavorite)
        holder.bind(usersEntity)
    }
}