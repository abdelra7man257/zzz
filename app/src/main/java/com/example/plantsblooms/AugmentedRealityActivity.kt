package com.example.plantsblooms

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.plantsblooms.databinding.ActivityAgumentedRealityBinding
import com.google.ar.core.Anchor
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.assets.RenderableSource
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode


class AugmentedRealityActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityAgumentedRealityBinding
    private lateinit var arFragment: ArFragment // initial setup of AR fragment
    var url: String =
        "https://firebasestorage.googleapis.com/v0/b/chatty-app-96e4a.appspot.com/o/snake_plant_in_pot.glb?alt=media&token=c602c4e9-34b0-4310-b690-d65d4a5f8a49"
    var pos: Int = 0
    val adapter = ArPlantsRecyclerAdapter(PlantModel.plantsList())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAgumentedRealityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initViews()
    }

    private fun initViews() {

        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment
//        val arData = intent.getStringExtra("agmunted").toString()
//        val pieceName = intent.getStringExtra("pieceName")
//        Log.i("pieceAr", arData)
//        Log.i("pieceData", pieceName.toString())
        viewBinding.recyclerView2.adapter = adapter
        adapter.onItemClick = ArPlantsRecyclerAdapter.OnItemClick { item, position ->
            url = item.arObject
            pos = position
        }

        //click listener of AR scene to create the node and download obj and rendering
        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            spawnObject(hitResult.createAnchor(), Uri.parse(url))
        }

    }

    // place 3D model to the AR scene (Real-time)
    private fun spawnObject(anchor: Anchor, modelUri: Uri) {

        //download 3d object from remote source -> and sets it's properties
        val renderableSource = RenderableSource.builder() //every renederable must have builder
            .setSource(
                this,
                modelUri,
                RenderableSource.SourceType.GLB
            ) //URL - 3d object type (OBJ - GLB - GLTF) -> Glb
            .setRecenterMode(RenderableSource.RecenterMode.ROOT) // 3D OBJ Mode -> center
            .setScale(1f) // scale of 3D model object -> float 0.0 ->1 float
            .build()

        ModelRenderable.builder() // renders the downloaded 3d object and sets it to the node we created
            .setSource(this, renderableSource)
            .setRegistryId(modelUri) //unique id
            .build()
            .thenAccept {modelRenderable->
                //it -> equals here ModelRenderable
                addNodeToScene(anchor, modelRenderable) // calling addNodeToScene FUNCTION
            }
            .exceptionally {// if something went wrong
                Toast.makeText(this, "An Error Occuered", Toast.LENGTH_SHORT).show()
                null
            }
    }
    // function create an node at the AR scene on real-time
    private fun addNodeToScene(anchor: Anchor, modelRenderable: ModelRenderable) {
        val anchorNode = AnchorNode(anchor) // created anchor node from parameter
        TransformableNode(arFragment.transformationSystem).apply {
            renderable = modelRenderable //3D model
            setParent(anchorNode) //every model has parent -> it's node
        }
        arFragment.arSceneView.scene.addChild(anchorNode)//adding anchor created to the scene
    }

}