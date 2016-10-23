package com.apps.golomb.muzix.interfaces;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.apps.golomb.muzix.R;

/**
 * Created by tomer on 21/10/2016.
 * This class responsible to the logic of creating a new playlist
 */

public class CreatePlaylistDialog implements MaterialDialog.SingleButtonCallback {

    private final DialogResultReceiver receiver;
    private final Object payload;

    public CreatePlaylistDialog(DialogResultReceiver receiver) {
        this(receiver, null);
    }
    public CreatePlaylistDialog(DialogResultReceiver receiver, Object payload){
        this.receiver = receiver;
        this.payload = payload;
    }
    public void show(Context context){
        new MaterialDialog.Builder(context)
                .titleColorRes(R.color.colorAccent)
                .title(R.string.create_a_new_playlist)
                .customView(R.layout.add_dialog, false)
                .positiveText(R.string.create)
                .onPositive(this)
                .negativeText(R.string.cancel)
                .onNegative(this)
                .show();
    }

    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        if(which.equals(DialogAction.POSITIVE)){
            View dialogView = dialog.getCustomView();
            if(dialogView!=null) {
                EditText name = (EditText) dialogView.findViewById(R.id.input_name);
                EditText details = (EditText) dialogView.findViewById(R.id.input_details);
                receiver.receiveDialogResult(name.getText().toString(),details.getText().toString(),payload);
            }
        }
        else if(which.equals(DialogAction.NEGATIVE)){
            dialog.dismiss();
        }
    }

    public interface DialogResultReceiver {
        void receiveDialogResult(String name, String details,Object payload);
    }
}
