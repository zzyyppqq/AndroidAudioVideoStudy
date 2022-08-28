package com.zyp.av.camera

import android.content.pm.ActivityInfo
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.zyp.av.base.BaseActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zyp.av.AbstractAdapter
import com.zyp.av.camera.adapter.StyleAdapter
import com.zyp.av.camera.adapter.StyleItem
import com.zyp.av.databinding.ActivityCameraOpenglFilterActivityBinding
import com.zyp.av.util.Const
import com.zyp.av.util.FileUtils
import com.zyp.av.util.ToastUtil
import java.io.File
import java.util.*
import java.util.concurrent.Executors

/**
 * OpenGL显示相机数据, 并进行滤波、保存相机数据
 * github： OpenglRecoder、GLCameraDemo
 */
class CameraOpenGLFilterActivity : BaseActivity() {
    private val TAG = "CameraOpenGLFilterActivity"
    private var _binding: ActivityCameraOpenglFilterActivityBinding? = null
    private val mBinding get() = _binding!!
    private var mAdapter: StyleAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCameraOpenglFilterActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED

        initRecyclerView()
        initData {
            mAdapter?.addAll(it)
            val lookupBitmap = BitmapFactory.decodeFile(it[0].stylePath)
            val rotation = windowManager.getDefaultDisplay().getRotation();
            mBinding.cameraSurfaceView.init(Camera.CameraInfo.CAMERA_FACING_BACK, rotation, lookupBitmap)
        }
        initListener()
    }


    private fun initRecyclerView() {
        mBinding.recyclerView.let {
            mAdapter = StyleAdapter()
            it.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            it.adapter = mAdapter
            mAdapter?.setOnRecyclerViewItemClickListener(object :
                AbstractAdapter.OnRecyclerItemClickListener<StyleItem> {
                override fun onItemClick(itemView: View, position: Int, data: StyleItem) {
                    itemClick(position, data)
                }
            })
        }
    }

    private fun initData(block: (list: List<StyleItem>) -> Unit) {
        Executors.newSingleThreadExecutor().execute {
            val dstPath = Const.sdPath + File.separator + "style"
            if (!File(dstPath).exists()) {
                FileUtils.deleteFile(File(dstPath))
                FileUtils.copyFilesAssets(applicationContext, "style", dstPath)
            }
            val styleDataRootPath =
                Const.sdPath + File.separator + "style" + File.separator + "data"
            val styleIconRootPath =
                Const.sdPath + File.separator + "style" + File.separator + "icon"

            // 滤镜数据
            val styleNameList: MutableList<String> = ArrayList()
            val styleRes = File(styleDataRootPath).listFiles { pathname: File ->
                !pathname.name.startsWith(".")
            }
            for (file in styleRes) {
                styleNameList.add(file.name)
            }
            Collections.sort(styleNameList)

            // 获取图标, 名称, 滤镜路径
            val styleDataList = ArrayList<Array<String>>()
            for (styleName in styleNameList) {
                val iconPath = styleIconRootPath + File.separator + styleName
                val name = styleName.substring("00".length, styleName.length - ".png".length)
                val stylePath = styleDataRootPath + File.separator + styleName
                styleDataList.add(arrayOf(iconPath, name, stylePath))
            }
            val styleItems = ArrayList<StyleItem>()
            styleDataList.forEach {
                val styleItem = StyleItem(it[0], it[1], it[2])
                styleItems.add(styleItem)
                Log.i(TAG, "StyleItem: $styleItem")
            }

            runOnUiThread {
                block(styleItems)
            }
        }

    }

    private fun itemClick(position: Int, data: StyleItem) {
        val bitmap = BitmapFactory.decodeFile(data.stylePath)
        mBinding.cameraSurfaceView.setLookup(bitmap)
    }


    private fun initListener() {
        mBinding.btCameraFront.setOnClickListener {
            mBinding.cameraSurfaceView.switchCamera(Camera.CameraInfo.CAMERA_FACING_FRONT)
        }

        mBinding.btCameraBack.setOnClickListener {
            mBinding.cameraSurfaceView.switchCamera(Camera.CameraInfo.CAMERA_FACING_BACK)
        }

        mBinding.btStartFilterRecord.setOnClickListener {
            mBinding.cameraSurfaceView.startRecord("${Const.sdPath}/${System.currentTimeMillis()}.mp4", 1.0f)
            ToastUtil.show("start record")
        }

        mBinding.btStopFilterRecord.setOnClickListener {
            mBinding.cameraSurfaceView.stopRecord()
            ToastUtil.show("stop record")
        }
    }

}