package com.zyp.androidaudiovideostudy.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zyp.androidaudiovideostudy.AbstractAdapter
import com.zyp.androidaudiovideostudy.base.BaseActivity
import com.zyp.androidaudiovideostudy.camera.adapter.StyleAdapter
import com.zyp.androidaudiovideostudy.camera.adapter.StyleItem
import com.zyp.androidaudiovideostudy.databinding.ActivityCameraOpenglLookupActivityBinding
import com.zyp.androidaudiovideostudy.gles.OpenGLLookupHelper
import com.zyp.androidaudiovideostudy.util.Const
import com.zyp.androidaudiovideostudy.util.FileUtils
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * 风格滤镜切换
 */
public class CameraOpenGLLookupActivity : BaseActivity() {
    private val TAG = "CameraOpenGLStyleActivity"
    private var _binding: ActivityCameraOpenglLookupActivityBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var mRenderer: LookupRenderer
    private var mAdapter: StyleAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCameraOpenglLookupActivityBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initRecyclerView()
        initData {
            mAdapter?.addAll(it)
            val rotation = windowManager.getDefaultDisplay().getRotation();
            val lookupBitmap = BitmapFactory.decodeFile(it[0].stylePath)
            mRenderer = LookupRenderer(mBinding.glSurfaceView, lookupBitmap, rotation)
            mBinding.glSurfaceView.setEGLContextClientVersion(2)
            mBinding.glSurfaceView.setRenderer(mRenderer)
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
        mRenderer.setLookup(bitmap)
    }

    private fun initListener() {

    }
}

class LookupRenderer(
    private val glSurfaceView: GLSurfaceView,
    private val lookup: Bitmap,
    private val rotation: Int
) : GLSurfaceView.Renderer {
    private val helper = OpenGLLookupHelper(lookup)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        helper.surfaceCreate(glSurfaceView, rotation)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        helper.surfaceChanged(width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        helper.drawFrame()
    }

    fun setLookup(bitmap: Bitmap) {
        helper.setLookup(bitmap)
    }
}