package com.example.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class try_onFragment extends Fragment {

    private ArFrontFacingFragment arFragment;
    private ArSceneView arSceneView;
    private final Set<CompletableFuture<?>> loaders = new HashSet<>();
    private ModelRenderable necklaceModel;
    private final HashMap<AugmentedFace, Node> faceNecklaceNodes = new HashMap<>();

    private final Map<String, List<Integer>> jewelryMap = new HashMap<>();
    private String currentCategory = "Necklace";
    private int currentModelIndex = 0;

    public try_onFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_try_on, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize model categories
        jewelryMap.put("Ring", Arrays.asList(R.raw.f_ring1, R.raw.f_ring2));
        jewelryMap.put("Nose Ring", Arrays.asList(R.raw.nose_ring1, R.raw.n_ring2));
        jewelryMap.put("Ear Ring", Arrays.asList(R.raw.ear_ring_grt, R.raw.golden_ear_ring));
        jewelryMap.put("Necklace", Arrays.asList(R.raw.necklace_b, R.raw.necklace_c));

        // Setup category buttons
        Button btnRing = view.findViewById(R.id.btnRing);
        Button btnNoseRing = view.findViewById(R.id.btnNoseRing);
        Button btnEarRing = view.findViewById(R.id.btnEarRing);
        Button btnSwap = view.findViewById(R.id.btnSwap);
        Button btnNecklace = view.findViewById(R.id.btnNecklace);

        btnNecklace.setOnClickListener(v -> selectCategory("Necklace"));
        btnRing.setOnClickListener(v -> selectCategory("Ring"));
        btnNoseRing.setOnClickListener(v -> selectCategory("Nose Ring"));
        btnEarRing.setOnClickListener(v -> selectCategory("Ear Ring"));
        btnSwap.setOnClickListener(v -> swapModel());

        requireActivity().getSupportFragmentManager().addFragmentOnAttachListener(this::onAttachFragment);

        if (Sceneform.isSupported(requireContext())) {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.arFragmentContainer, ArFrontFacingFragment.class, null)
                    .commit();
        }

        loadNecklaceModel();
    }

    private void selectCategory(String category) {
        currentCategory = category;
        currentModelIndex = 0;
        loadNecklaceModel();
    }

    private void swapModel() {
        List<Integer> models = jewelryMap.get(currentCategory);
        if (models == null || models.isEmpty()) return;

        currentModelIndex = (currentModelIndex + 1) % models.size();
        loadNecklaceModel();
    }

    private void onAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
        if (fragment.getId() == R.id.arFragmentContainer) {
            arFragment = (ArFrontFacingFragment) fragment;
            arFragment.setOnViewCreatedListener(this::onArViewCreated);
        }
    }

    private void onArViewCreated(ArSceneView arSceneView) {
        this.arSceneView = arSceneView;
        arSceneView.setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);
        arFragment.setOnAugmentedFaceUpdateListener(this::onAugmentedFaceTrackingUpdate);
    }

    private void loadNecklaceModel() {
        List<Integer> models = jewelryMap.get(currentCategory);
        if (models == null || models.isEmpty()) return;

        int modelRes = models.get(currentModelIndex);

        loaders.add(ModelRenderable.builder()
                .setSource(requireContext(), modelRes)
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
                    Toast.makeText(requireContext(), "Unable to load model", Toast.LENGTH_LONG).show();
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
        Vector3 scale;
        Vector3 offset = new Vector3(0, 0, 0);

        switch (currentCategory) {
            case "Necklace":
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
            case "Nose Ring":
                pose = face.getRegionPose(AugmentedFace.RegionType.NOSE_TIP);
                offset = new Vector3(0.015f, 0f, 0f);
                scale = new Vector3(0.003f, 0.003f, 0.003f);
                Vector3 position2 = new Vector3(
                        pose.tx() + offset.x,
                        pose.ty() + offset.y,
                        pose.tz() + offset.z
                );

                node.setWorldPosition(position2);
                node.setWorldRotation(Quaternion.axisAngle(new Vector3(1, 0, 0), 180));
                node.setWorldScale(scale);
                break;
            case "Ear Ring":
                if (currentModelIndex==0){
                    pose = face.getRegionPose(AugmentedFace.RegionType.FOREHEAD_LEFT);
                    offset = new Vector3(0.05f, -0.29f, -0.02f); // Approximate position for left ear
                    scale = new Vector3(0.09f, 0.09f, 0.09f);
                    Vector3 position3 = new Vector3(
                            pose.tx() + offset.x,
                            pose.ty() + offset.y,
                            pose.tz() + offset.z
                    );
                    node.setWorldPosition(position3);
                    node.setWorldRotation(Quaternion.axisAngle(new Vector3(1, 0, 0), 360));
                    node.setWorldScale(scale);
                } else if (currentModelIndex==1) {
                    pose = face.getRegionPose(AugmentedFace.RegionType.FOREHEAD_LEFT);
                    offset = new Vector3(0.07f, -0.09f, -0.02f); // Approximate position for left ear
                    scale = new Vector3(0.7f, 0.7f, 0.7f);
                    Vector3 position3 = new Vector3(
                            pose.tx() + offset.x,
                            pose.ty() + offset.y,
                            pose.tz() + offset.z
                    );
                    node.setWorldPosition(position3);
                    node.setWorldRotation(Quaternion.axisAngle(new Vector3(1, 0, 0), 90));
                    node.setWorldScale(scale);
                }

                break;
            case "Ring":
                pose = face.getRegionPose(AugmentedFace.RegionType.FOREHEAD_LEFT);
                offset = new Vector3(0.1f, -0.2f, -0.05f);
                scale = new Vector3(0.02f, 0.02f, 0.02f);
                Vector3 position4 = new Vector3(
                        pose.tx() + offset.x,
                        pose.ty() + offset.y,
                        pose.tz() + offset.z
                );

                node.setWorldPosition(position4);
                node.setWorldRotation(Quaternion.axisAngle(new Vector3(1, 0, 0), 360));
                node.setWorldScale(scale);
                break;
            default:
                pose = face.getCenterPose();
                scale = new Vector3(0.02f, 0.02f, 0.02f);

        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for (CompletableFuture<?> loader : loaders) {
            if (!loader.isDone()) {
                loader.cancel(true);
            }
        }
    }
}
