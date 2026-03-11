package com.example.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.ar.core.AugmentedFace;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Sceneform;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.RenderableInstance;
import com.google.ar.sceneform.ux.ArFrontFacingFragment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TryOnActivity extends AppCompatActivity {

    private ArFrontFacingFragment arFragment;
    private ArSceneView arSceneView;
    private final Set<CompletableFuture<?>> loaders = new HashSet<>();
    private ModelRenderable necklaceModel;
    private final HashMap<AugmentedFace, Node> faceNecklaceNodes = new HashMap<>();
    private final Map<String, Integer> productModelMap = new HashMap<>();
    private String productName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_on);

        Intent intent = getIntent();
        productName = intent.getStringExtra("message_key");
        if (productName != null) productName = productName.toLowerCase();

        productModelMap.put("rsjnecklace", R.raw.necklace_b);
        productModelMap.put("necklace_cell", R.raw.louboutin_necklace);
        productModelMap.put("nose ring2", R.raw.nose_ring2);
        productModelMap.put("nose ring", R.raw.n_ring2);
        productModelMap.put("ear ring", R.raw.ear_ring_grt);
        productModelMap.put("golden_ear_ring", R.raw.golden_ear_ring);
        productModelMap.put("f_ring1", R.raw.f_ring1);
        productModelMap.put("f_ring2", R.raw.f_ring2);

        if (Sceneform.isSupported(this)) {
            getSupportFragmentManager().addFragmentOnAttachListener(this::onAttachFragment);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.arFragmentContainer, ArFrontFacingFragment.class, null)
                    .commit();
        }

        loadModelForProduct(productName);
    }

    private void onAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        if (fragment instanceof ArFrontFacingFragment) {
            arFragment = (ArFrontFacingFragment) fragment;
            arFragment.setOnViewCreatedListener(this::onArViewCreated);
        }
    }

    private void onArViewCreated(ArSceneView arSceneView) {
        this.arSceneView = arSceneView;
        arSceneView.setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);
        arFragment.setOnAugmentedFaceUpdateListener(this::onAugmentedFaceTrackingUpdate);
    }

    private void loadModelForProduct(String productName) {
        Integer modelResId = productModelMap.get(productName);
        if (modelResId == null) {
            Toast.makeText(this, "Product model not found", Toast.LENGTH_SHORT).show();
            return;
        }

        loaders.add(ModelRenderable.builder()
                .setSource(this, modelResId)
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(model -> {
                    necklaceModel = model;
                    model.setShadowCaster(false);
                    model.setShadowReceiver(false);
                    clearExistingNodes();

                    if (arSceneView != null && arFragment != null) {
                        for (AugmentedFace face : arFragment.getArSceneView().getSession().getAllTrackables(AugmentedFace.class)) {
                            onAugmentedFaceTrackingUpdate(face);
                        }
                    }
                })
                .exceptionally(throwable -> {
                    Toast.makeText(this, "Unable to load model", Toast.LENGTH_LONG).show();
                    return null;
                }));
    }

    private void clearExistingNodes() {
        for (Node node : faceNecklaceNodes.values()) {
            arSceneView.getScene().removeChild(node);
        }
        faceNecklaceNodes.clear();
    }

    private void onAugmentedFaceTrackingUpdate(AugmentedFace face) {
        if (necklaceModel == null) return;

        Node existingNode = faceNecklaceNodes.get(face);

        if (face.getTrackingState() == TrackingState.TRACKING) {
            if (existingNode == null) {
                Node node = new Node();
                updateJewelryPosition(node, face);
                RenderableInstance renderableInstance = node.setRenderable(necklaceModel);
                renderableInstance.setShadowCaster(false);
                renderableInstance.setShadowReceiver(false);
                arSceneView.getScene().addChild(node);
                faceNecklaceNodes.put(face, node);
            } else {
                updateJewelryPosition(existingNode, face);
            }
        } else if (face.getTrackingState() == TrackingState.STOPPED) {
            if (existingNode != null) {
                arSceneView.getScene().removeChild(existingNode);
                faceNecklaceNodes.remove(face);
            }
        }
    }

    private void updateJewelryPosition(Node node, AugmentedFace face) {
        Pose pose;
        Vector3 offset = new Vector3(0, 0, 0);
        Vector3 scale;

        switch (productName) {
            case "necklace_cell":
                pose = face.getCenterPose();
                offset = new Vector3(0f, -0.28f, -0.02f);
                scale = new Vector3(2f, 2f, 2f);
                Vector3 position6 = new Vector3(
                        pose.tx() + offset.x,
                        pose.ty() + offset.y,
                        pose.tz() + offset.z
                );

                node.setWorldPosition(position6);
                node.setWorldRotation(Quaternion.axisAngle(new Vector3(1, 0, 0), 360));
                node.setWorldScale(scale);
                break;
            case "rsjnecklace":
                pose = face.getCenterPose();
                offset = new Vector3(0f, -0.23f, -0.02f);
                scale = new Vector3(0.23f, 0.23f, 0.23f);
                Vector3 position1 = new Vector3(
                        pose.tx() + offset.x,
                        pose.ty() + offset.y,
                        pose.tz() + offset.z
                );

                node.setWorldPosition(position1);
                node.setWorldRotation(Quaternion.axisAngle(new Vector3(1, 0, 0), 360));
                node.setWorldScale(scale);
                break;
            case "nose ring2":
                pose = face.getRegionPose(AugmentedFace.RegionType.NOSE_TIP);
                offset = new Vector3(0.015f, -0.008f, 0f);
                scale = new Vector3(0.9f, 0.9f, 0.9f);
                Vector3 position5 = new Vector3(
                        pose.tx() + offset.x,
                        pose.ty() + offset.y,
                        pose.tz() + offset.z
                );

                node.setWorldPosition(position5);
                node.setWorldRotation(Quaternion.axisAngle(new Vector3(1, 3, 0), 360));
                node.setWorldScale(scale);
                break;
            case "nose ring":
                pose = face.getRegionPose(AugmentedFace.RegionType.NOSE_TIP);
                offset = new Vector3(0.015f, 0f, 0f);
                scale = new Vector3(0.003f, 0.003f, 0.003f);
                Vector3 position2 = new Vecto r3(
                        pose.tx() + offset.x,
                        pose.ty() + offset.y,
                        pose.tz() + offset.z
                );

                node.setWorldPosition(position2);
                node.setWorldRotation(Quaternion.axisAngle(new Vector3(1, 0, 0), 180));
                node.setWorldScale(scale);
                break;
            case "ear ring":
                pose = face.getRegionPose(AugmentedFace.RegionType.FOREHEAD_LEFT);
                //offset = new Vector3(0.05f, -0.29f, -0.02f); // Approximate position for left ear
                //scale = new Vector3(0.09f, 0.09f, 0.09f);
                offset = new Vector3(0.05f, -0.29f, -0.02f);
                scale = new Vector3(0.09f, 0.09f, 0.09f);
                Vector3 position3 = new Vector3(
                        pose.tx() + offset.x,
                        pose.ty() + offset.y,
                        pose.tz() + offset.z
                );
                node.setWorldPosition(position3);
                node.setWorldRotation(Quaternion.axisAngle(new Vector3(1, 0, 0), 360));
                node.setWorldScale(scale);
                break;
            case "golden_ear_ring":
                pose = face.getRegionPose(AugmentedFace.RegionType.FOREHEAD_LEFT);
                offset = new Vector3(0.07f, -0.09f, -0.02f); // Approximate position for left ear
                scale = new Vector3(0.7f, 0.7f, 0.7f);
                Vector3 position4 = new Vector3(
                        pose.tx() + offset.x,
                        pose.ty() + offset.y,
                        pose.tz() + offset.z
                );
                node.setWorldPosition(position4);
                node.setWorldRotation(Quaternion.axisAngle(new Vector3(1, 0, 0), 90));
                node.setWorldScale(scale);
                break;
            case "f_ring1":
            case "f_ring2":
                pose = face.getRegionPose(AugmentedFace.RegionType.FOREHEAD_LEFT);
                offset = new Vector3(0.1f, -0.2f, -0.05f);
                scale = new Vector3(0.02f, 0.02f, 0.02f);
                break;
            default:
                pose = face.getCenterPose();
                scale = new Vector3(0.02f, 0.02f, 0.02f);
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (CompletableFuture<?> loader : loaders) {
            if (!loader.isDone()) {
                loader.cancel(true);
            }
        }
    }
}
