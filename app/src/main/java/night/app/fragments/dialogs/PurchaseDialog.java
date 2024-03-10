package night.app.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import night.app.R;
import night.app.activities.MainActivity;
import night.app.adapters.RingtoneViewHolder;
import night.app.adapters.ThemeViewHolder;
import night.app.databinding.DialogPurchaseBinding;
import night.app.fragments.GardenPageFragment;
import night.app.networks.ServiceRequest;

public class  PurchaseDialog <ViewHolder extends RecyclerView.ViewHolder> extends DialogFragment {
    DialogPurchaseBinding binding;

    ViewHolder holder;

    private void refreshGardenPage() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.fr_app_page, GardenPageFragment.class, null)
                .commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        requireDialog().getWindow()
            .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_purchase, container, false);
        binding.setTheme(MainActivity.getAppliedTheme());

        Integer price = Integer.parseInt(requireArguments().getString("price", "No value"));
        Integer coins = MainActivity.getDataStore().getPrefs().get(PreferencesKeys.intKey("coins"));

        binding.tvPurchaseItemPrice.setText(String.valueOf(price));
        binding.tvPurchaseCoins.setText(String.valueOf(coins == null ? coins = 0 : coins));

        binding.tvPurchaseRemain.setText(String.valueOf(coins - price));
        binding.tvPurchaseName.setText(getArguments().getString("name", ""));

        // button operation
        if (Integer.parseInt(binding.tvPurchaseRemain.getText().toString()) < 0) {
            // disable positive button if user has no enough coins
            binding.btnPositive.setEnabled(false);
        }
        else {
            binding.btnPositive.setOnClickListener(v -> {
                MainActivity activity = (MainActivity) requireActivity();
                Preferences prefs = MainActivity.getDataStore().getPrefs();

                try {
                    JSONObject request = new JSONObject();
                    String sid = prefs.get(PreferencesKeys.stringKey("sessionId"));
                    String uid = prefs.get(PreferencesKeys.stringKey("username"));

                    request.put("sid", sid);
                    request.put("uid", uid);

                    ConfirmDialog dialog = new ConfirmDialog("Purchase", "", null);
                    dialog.showNow(getParentFragmentManager(), null);

                    if (sid == null || uid == null) {
                        dialog.replaceContent("Purchase failed", "You need to login to use this service.", null);
                        dismiss();
                        return;
                    }

                    request.put("prodId", requireArguments().get("prodId"));
                    dialog.showLoading();

                    new ServiceRequest().purchase(request.toString(), res -> {
                        int status = res.optInt("responseCode");

                        if (status == 200) {
                            MainActivity.getDatabase().dao().updateProductStatus(requireArguments().getInt("prodId"));

                            activity.runOnUiThread(() -> {
                                dialog.replaceContent("Purchase Success", "You can apply the item now.", null);
                                dialog.showMessage();

                                try {
                                    MainActivity.getDataStore().update(PreferencesKeys.intKey("coins"), res.getJSONObject("response").getInt("coins"));
                                    refreshGardenPage();
                                    if (holder instanceof RingtoneViewHolder) {
                                        ((RingtoneViewHolder) holder).setOwned();
                                    }
                                    else if (holder instanceof ThemeViewHolder) {
                                        ((ThemeViewHolder) holder).setThemeApplied();
                                    }
                                }
                                catch (JSONException e) { }

                                dismiss();
                            });
                            return;
                        }
                        activity.runOnUiThread(() -> {
                            String errorMsg = switch (res.optInt("responseCode")) {
                                case 400 -> "Invalid packet structures.";
                                case 401 -> "Unauthorized user.";
                                case 404 -> "API path not found.";
                                case 406 -> {
                                    try {
                                        MainActivity.getDataStore().update(PreferencesKeys.intKey("coins"), res.getJSONObject("response").getInt("coins"));
                                        refreshGardenPage();
                                    }
                                    catch (JSONException e) { }

                                    yield "You have not enough money.";
                                }
                                case 500 -> "Server error.";
                                default  -> "Unexpected status code: " + res.optInt("responseCode");
                            };

                            dialog.replaceContent("Purchase failed", errorMsg, null);
                            dialog.showMessage();
                            dismiss();
                        });
                    });
                }
                catch (JSONException e) {

                }
            });
        }

        binding.btnNegative.setOnClickListener(v -> dismiss());

        return binding.getRoot();
    }

    public PurchaseDialog(ViewHolder holder) {
        this.holder = holder;
    }
}

