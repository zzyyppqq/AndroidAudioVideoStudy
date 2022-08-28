package com.zyp.av.base

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.apply {
            setHomeButtonEnabled(true);
            // 隐藏/显示返回箭头
            setDisplayHomeAsUpEnabled(true)
            // 隐藏/显示Tittle
            setDisplayShowTitleEnabled(true)
            // 隐藏/显示Custom
            setDisplayShowCustomEnabled(true)
            // setSubtitle("Subtitle")
            // 隐藏/显示功能图片
            setDisplayShowHomeEnabled(true);
            // 设置使用activity的logo还是activity的icon
            setDisplayUseLogoEnabled(true)
            setTitle(this@BaseActivity.javaClass.simpleName);
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //android.R.id.home对应应用程序图标的id
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}