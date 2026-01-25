package com.example.tbokhle.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tbokhle.R;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FragmentThree extends Fragment {

    // URL of your PHP file (change IP if using real phone)
    private static final String ADD_PRODUCT_URL =
            "http://10.0.2.2/tbokhle_api/add_product.php";

    // Launchers
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    private Bitmap capturedImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Camera permission launcher
        cameraPermissionLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        granted -> {
                            if (granted) {
                                openCamera();
                            } else {
                                Toast.makeText(requireContext(),
                                        "Camera permission required",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                );

        // Camera result launcher
        cameraLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == getActivity().RESULT_OK
                                    && result.getData() != null) {

                                Bundle extras = result.getData().getExtras();
                                capturedImage = (Bitmap) extras.get("data");

                                if (capturedImage != null) {
                                    runTextRecognition(capturedImage);
                                }
                            }
                        }
                );
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_three, container, false);

        SwitchCompat swManual = view.findViewById(R.id.swManualEntry);

        view.findViewById(R.id.btnStartScanning).setOnClickListener(v -> {
            if (swManual.isChecked()) {
                openManualFragment();
            } else {
                checkCameraPermission();
            }
        });

        return view;
    }

    private void openManualFragment() {
        FragmentTransaction ft = requireActivity()
                .getSupportFragmentManager()
                .beginTransaction();

        ft.replace(R.id.nav_host_fragment_activity_main, new FragmentManualAdd());
        ft.addToBackStack(null);
        ft.commit();
    }

    // Check camera permission
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    // Open phone camera
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    // Run ML Kit text recognition
    private void runTextRecognition(Bitmap bitmap) {

        InputImage image = InputImage.fromBitmap(bitmap, 0);

        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                .process(image)
                .addOnSuccessListener(result -> {

                    String detectedText = result.getText();
                    String productName = detectedText.isEmpty()
                            ? ""
                            : detectedText.split("\n")[0];

                    showProductDialog(productName);

                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(),
                                "Text recognition failed",
                                Toast.LENGTH_SHORT).show());
    }

    // Show dialog to complete product info
    private void showProductDialog(String autoProductName) {

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_products, null);

        EditText etName = dialogView.findViewById(R.id.etProductName);
        EditText etQty = dialogView.findViewById(R.id.etQuantity);
        EditText etExpiry = dialogView.findViewById(R.id.etExpiry);
        Spinner spCategory = dialogView.findViewById(R.id.spCategory);

        etName.setText(autoProductName);

        // Spinner setup
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.categories,
                        android.R.layout.simple_spinner_item
                );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);

        // Date picker
        etExpiry.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(requireContext(),
                    (view, y, m, d) ->
                            etExpiry.setText(y + "-" + (m + 1) + "-" + d),
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Add Product")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {

                    String name = etName.getText().toString().trim();
                    String qty = etQty.getText().toString().trim();
                    String date = etExpiry.getText().toString().trim();
                    String category = spCategory.getSelectedItem().toString();

                    if (name.isEmpty() || qty.isEmpty() || date.isEmpty()) {
                        Toast.makeText(requireContext(),
                                "Please fill all fields",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    saveProductToServer(name, qty, date, category);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Send product data to PHP using Volley
    private void saveProductToServer(
            String name,
            String quantity,
            String expiryDate,
            String category
    ) {

        StringRequest request = new StringRequest(
                Request.Method.POST,
                ADD_PRODUCT_URL,
                response -> {
                    if (response.trim().equals("success")) {
                        Toast.makeText(requireContext(),
                                "Product saved successfully",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(),
                                "Server error",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(requireContext(),
                        "Network error",
                        Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("quantity", quantity);
                params.put("expiry_date", expiryDate);
                params.put("category", category);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(request);
    }
}
