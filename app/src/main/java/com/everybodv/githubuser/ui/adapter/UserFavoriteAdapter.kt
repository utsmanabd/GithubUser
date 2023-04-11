package com.everybodv.githubuser.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everybodv.githubuser.R
import com.everybodv.githubuser.data.local.entity.UsersEntity
import com.everybodv.githubuser.databinding.ItemRowUserBinding
import com.everybodv.githubuser.ui.detail.DetailUserActivity

class UserFavoriteAdapter(private val onFavoriteClick: (UsersEntity) -> Unit) : ListAdapter<UsersEntity, UserFavoriteAdapter.MyViewHolder>(
    DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemRowUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)

        val ivFavorite = holder.binding.ivFavorite
        if (user.isFavorite) {
            ivFavorite.setImageResource(R.drawable.ic_favorite_filled_pink)
        } else {
            ivFavorite.setImageResource(R.drawable.ic_favorite_pink)
        }
        ivFavorite.setOnClickListener {
            if (user.isFavorite) {
                ivFavorite.setImageResource(R.drawable.ic_favorite_pink)
            } else {
                ivFavorite.setImageResource(R.drawable.ic_favorite_filled_pink)
            }
            onFavoriteClick(user)
        }
    }

    class MyViewHolder(val binding: ItemRowUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UsersEntity) {
            binding.tvName.text = user.login
            Glide.with(itemView.context)
                .load(user.avatarUrl)
                .circleCrop()
                .into(binding.detailImage)
            itemView.setOnClickListener {
                val toDetailUser = Intent(itemView.context, DetailUserActivity::class.java)
                toDetailUser.putExtra(DetailUserActivity.EXTRA_USER, user)
                itemView.context.startActivity(toDetailUser)
            }
        }
    }
    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<UsersEntity> =
            object : DiffUtil.ItemCallback<UsersEntity>() {
                override fun areItemsTheSame(oldUser: UsersEntity, newUser: UsersEntity): Boolean {
                    return oldUser.login == newUser.login
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(oldUser: UsersEntity, newUser: UsersEntity): Boolean {
                    return oldUser == newUser
                }
            }
    }

}